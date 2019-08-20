package ocicat.server.netty

import io.netty.channel.{ChannelInitializer, ChannelPipeline}
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{
  HttpContentDecompressor,
  HttpObjectAggregator,
  HttpServerCodec,
  HttpServerExpectContinueHandler
}

class HttpServerInitializer(provider: HttpServerHandlerProvider) extends ChannelInitializer[SocketChannel] {

  override def initChannel(ch: SocketChannel): Unit = {
    val p: ChannelPipeline = ch.pipeline()
    p.addLast(new HttpServerCodec)
    p.addLast(new HttpContentDecompressor)
    p.addLast(new HttpObjectAggregator(256 * 1024 * 1024))
    p.addLast(new HttpServerExpectContinueHandler)
    p.addLast(provider.get)
  }
}
