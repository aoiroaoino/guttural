package monoton.server.netty

import java.net.URI
import java.nio.charset.StandardCharsets

import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import monoton.http.{ContentType, DefaultRequest, Method, Request, Response}
import monoton.server.{Handler, Router}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.chaining._

// io.netty.channel.ChannelPipelineException: monoton.server.netty.HttpServerHandler is not a @Sharable handler, so can't be added or removed multiple times.
class HttpServerHandlerProvider(router: Router, executor: ExecutionContext) {
  def get: HttpServerHandler = new HttpServerHandler(router, executor)
}

class HttpServerHandler(router: Router, executor: ExecutionContext) extends SimpleChannelInboundHandler[HttpObject] {
  import HttpHeaderNames._, HttpHeaderValues._

  def createNettyResponse(response: Response)(nettyRequest: HttpRequest): HttpResponse = {
    val status = Converter.toHttpResponseStatus(response.status)
    val content =
      if (response.content.isEmpty) Unpooled.EMPTY_BUFFER
      else Unpooled.wrappedBuffer(response.content)

    new DefaultFullHttpResponse(nettyRequest.protocolVersion(), status, content)
      .tap { res =>
        res
          .headers()
          .set(CONTENT_TYPE, TEXT_PLAIN)
          .setInt(CONTENT_LENGTH, res.content.readableBytes)
      }
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpObject): Unit = {
    msg match {
      case req: HttpRequest =>
        val uri = new URI(req.uri)
        val request: Request = {
          val query = Option(uri.getQuery)
            .map(_.split('&').toList)
            .map(_.map(_.split('=').toList))
            .map(_.collect { case k :: v :: Nil => (k, v) }.toMap)
            .getOrElse(Map.empty)
          val body = req match {
            case fullReq: FullHttpRequest =>
              val buf = new Array[Byte](fullReq.content.readableBytes)
              fullReq.content.duplicate.readBytes(buf)
              buf
            case other =>
              Array.emptyByteArray
          }
          val method      = Method.fromString(req.method.name).get
          val contentType = if (method == Method.GET) None else ContentType.fromString(req.headers.get(CONTENT_TYPE))
          DefaultRequest(query, contentType, body, uri, method)
        }
        val resF: Future[HttpResponse] = (for {
          path     <- Handler.catchNonFatal(uri.getPath)(_ => Response.BadRequest("invalid uri path"))
          route    <- Handler.someValue(router.findRoute(request.method, path))(Response.NotFound(s"not found: $path"))
          response <- route.handler
        } yield response)
          .run(request)
          .map(createNettyResponse(_)(req))(executor)
          .recover {
            case e: Throwable =>
              e.printStackTrace()
              new DefaultFullHttpResponse(
                req.protocolVersion(),
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.wrappedBuffer("Unexpected error occurred".getBytes(StandardCharsets.UTF_8))
              ).tap { res =>
                res
                  .headers()
                  .set(CONTENT_TYPE, TEXT_PLAIN)
                  .setInt(CONTENT_LENGTH, res.content.readableBytes)
              }
          }(executor)

        resF.foreach { res =>
          val f: ChannelFuture   = ctx.write(res)
          val keepAlive: Boolean = HttpUtil.isKeepAlive(req)
          if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE)
          }
          if (keepAlive && !req.protocolVersion.isKeepAliveDefault) {
            req.headers.set(CONNECTION, HttpHeaderValues.KEEP_ALIVE)
          } else {
            req.headers.set(CONNECTION, CLOSE)
          }
          ctx.flush()
        }(executor)
      case other => // nop
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }
}
