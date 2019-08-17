package ocicat.server.netty

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{Channel, ChannelOption}
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import ocicat.server.Router

import scala.concurrent.ExecutionContext

class NettyServer(val port: Int, val router: Router, requestExecutor: ExecutionContext) {

  def start(): Unit = {
    val bossGroup   = new NioEventLoopGroup()
    val workerGroup = new NioEventLoopGroup()
    try {
      val b = new ServerBootstrap()
      b.option(ChannelOption.SO_BACKLOG, new java.lang.Integer(1024))
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new HttpServerInitializer(None, router, requestExecutor))

      val ch: Channel = b.bind(port).sync().channel()

      ch.closeFuture().sync()
    } finally {
      workerGroup.shutdownGracefully()
      bossGroup.shutdownGracefully()
    }
  }
}
