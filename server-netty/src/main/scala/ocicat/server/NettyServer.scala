package ocicat.server

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.{
  Channel,
  ChannelFuture,
  ChannelFutureListener,
  ChannelHandlerContext,
  ChannelInitializer,
  ChannelOption,
  ChannelPipeline,
  SimpleChannelInboundHandler
}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.{
  DefaultFullHttpResponse,
  FullHttpResponse,
  HttpHeaderNames,
  HttpHeaderValues,
  HttpObject,
  HttpRequest,
  HttpResponse,
  HttpResponseStatus,
  HttpServerCodec,
  HttpServerExpectContinueHandler,
  HttpUtil
}
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import javax.net.ssl.SSLContext
import ocicat.http.{Method, Status}

import scala.concurrent.{Await, ExecutionContext, Future}

class HttpHelloWorldServerInitializer(sslCtx: Option[SSLContext], router: Router, requestExecutor: ExecutionContext)
    extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel): Unit = {
    val p: ChannelPipeline = ch.pipeline()
    p.addLast(new HttpServerCodec)
    p.addLast(new HttpServerExpectContinueHandler)
    p.addLast(new HttpHelloWorldServerHandler(router, requestExecutor))
  }
}

class HttpHelloWorldServerHandler(router: Router, executor: ExecutionContext)
    extends SimpleChannelInboundHandler[HttpObject] {
  import HttpHeaderNames._, HttpHeaderValues._

  def createNettyResponse(response: Response)(nettyRequest: HttpRequest): HttpResponse = {
    println("call createNettyResponse")
    val content =
      if (response.content.isEmpty) Unpooled.EMPTY_BUFFER
      else Unpooled.wrappedBuffer(response.content.getBytes(StandardCharsets.UTF_8))
    val status = response.status match { // TODO: fully implement
      case Status.Ok                  => HttpResponseStatus.OK
      case Status.BadRequest          => HttpResponseStatus.BAD_REQUEST
      case Status.NotFound            => HttpResponseStatus.NOT_FOUND
      case Status.InternalServerError => HttpResponseStatus.INTERNAL_SERVER_ERROR
      case _                          => HttpResponseStatus.NOT_IMPLEMENTED
    }

    val res: FullHttpResponse =
      new DefaultFullHttpResponse(nettyRequest.protocolVersion(), status, content)
    res
      .headers()
      .set(CONTENT_TYPE, TEXT_PLAIN)
      .setInt(CONTENT_LENGTH, res.content.readableBytes)
    res
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpObject): Unit = {
    msg match {
      case req: HttpRequest =>
        val resF: Future[HttpResponse] = (for {
          method   <- Handler.someValue(Method.fromString(req.method.name))(Response(Status.BadRequest, "invalid method"))
          path     = Paths.get(req.uri)
          route    <- Handler.someValue(router.findRoute(method, path))(Response(Status.NotFound, s"not found: $path"))
          response <- route.handler
          _        = println(s"method: $method, path: $path, route: $route, response: $response")
        } yield response).run
          .map(createNettyResponse(_)(req))(executor)
          .recover {
            case e: Throwable =>
              e.printStackTrace()
              val res =
                new DefaultFullHttpResponse(
                  req.protocolVersion(),
                  HttpResponseStatus.INTERNAL_SERVER_ERROR,
                  Unpooled.wrappedBuffer("Unexpected error occurred".getBytes(StandardCharsets.UTF_8))
                )
              res
                .headers()
                .set(CONTENT_TYPE, TEXT_PLAIN)
                .setInt(CONTENT_LENGTH, res.content.readableBytes)
              res
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

class NettyServer(
    val port: Int,
    val router: Router,
    val requestExecutor: ExecutionContext
) extends Server {

  override def start(): Unit = {
    val bossGroup   = new NioEventLoopGroup()
    val workerGroup = new NioEventLoopGroup()
    try {
      val b = new ServerBootstrap()
      b.option(ChannelOption.SO_BACKLOG, new java.lang.Integer(1024))
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new HttpHelloWorldServerInitializer(None, router, requestExecutor))

      val ch: Channel = b.bind(port).sync().channel()

      ch.closeFuture().sync()
    } finally {
      workerGroup.shutdownGracefully()
      bossGroup.shutdownGracefully()
    }
  }

  override def stop(): Unit = println("stop")
}
