package ocicat.server

import ocicat.server.netty.NettyServer

import scala.concurrent.ExecutionContext

class ServerImpl(port: Int, router: Router, requestExecutor: ExecutionContext) extends Server {
  private[this] val underlying = new NettyServer(port, router, requestExecutor)

  override def start(): Unit = {
    println("Starting server(Netty) ...")
    underlying.start()
  }
  override def stop(): Unit = println("stop")
}
