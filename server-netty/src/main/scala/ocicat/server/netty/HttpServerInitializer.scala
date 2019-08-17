package ocicat.server.netty

import io.netty.channel.{ChannelInitializer, ChannelPipeline}
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{HttpServerCodec, HttpServerExpectContinueHandler}
import javax.net.ssl.SSLContext
import ocicat.server.Router

import scala.concurrent.ExecutionContext

private[netty] class HttpServerInitializer(
    sslCtx: Option[SSLContext],
    router: Router,
    requestExecutor: ExecutionContext
) extends ChannelInitializer[SocketChannel] {

  override def initChannel(ch: SocketChannel): Unit = {
    val p: ChannelPipeline = ch.pipeline()
    p.addLast(new HttpServerCodec)
    p.addLast(new HttpServerExpectContinueHandler)
    p.addLast(new HttpServerHandler(router, requestExecutor))
  }
}
