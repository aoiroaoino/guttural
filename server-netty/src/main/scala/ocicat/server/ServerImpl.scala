package ocicat.server

import ocicat.server.netty.{HttpServerHandler, HttpServerHandlerProvider, HttpServerInitializer, NettyServer}

import scala.concurrent.ExecutionContext

class ServerImpl(port: Int, router: Router, requestExecutor: ExecutionContext) extends Server {
  private[this] val provider    = new HttpServerHandlerProvider(router, requestExecutor)
  private[this] val initializer = new HttpServerInitializer(provider)
  private[this] val server      = new NettyServer(port, initializer)

  override def start(): Unit = {
    println("Starting server(Netty) ...")
    server.start()
  }
  override def stop(): Unit = println("stop")
}
