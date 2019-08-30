package monoton.server.netty

import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import io.netty.util.ReferenceCountUtil
import monoton.http.ResponseBuilders
import monoton.server.netty.flow.{HttpMessageConvertFlow, NotImplementedMethodFilter, RoutingFlow}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scala.util.chaining._

class HttpServerHandler(
    httpMessageConvertFlow: HttpMessageConvertFlow,
    routingFlow: RoutingFlow,
    executor: ExecutionContext
) extends SimpleChannelInboundHandler[HttpRequest] {
  import HttpHeaderNames._, HttpHeaderValues._

  private implicit val ec: ExecutionContext = executor

  // TODO: URI の検証と Route の検索を Filter にし、Handler の実行と HttpResponse のみを行うように
  val httpRequestFlow = NotImplementedMethodFilter |> httpMessageConvertFlow |> routingFlow

  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpRequest): Unit = {
    httpRequestFlow
      .runF(msg) {
        case (req, route) =>
          route.handler().run(req).recover {
            case e: Throwable =>
              e.printStackTrace()
              ResponseBuilders.InternalServerError("Unexpected error occurred")
          }
      }
      .andThen {
        case Success(httpRes) =>
          val f: ChannelFuture   = ctx.write(httpRes)
          val keepAlive: Boolean = HttpUtil.isKeepAlive(msg)
          if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE)
          }
          if (keepAlive && !msg.protocolVersion.isKeepAliveDefault) {
            httpRes.headers.set(CONNECTION, HttpHeaderValues.KEEP_ALIVE)
          } else {
            httpRes.headers.set(CONNECTION, CLOSE)
          }
          ctx.flush()
        case Failure(e) =>
          e.printStackTrace()
      }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
