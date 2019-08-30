package monoton.server

import monoton.server.netty.flow.{HttpMessageConvertFlow, RoutingFlow}
import monoton.server.netty.{HttpServerHandlerProvider, HttpServerInitializer, NettyServer}

import scala.concurrent.ExecutionContext

class ServerImpl(port: Int, router: Router, requestExecutor: ExecutionContext) extends Server {
  private[this] val routingFlow = new RoutingFlow(router)
  private[this] val httpDriver  = new HttpMessageConvertFlow
  private[this] val provider    = new HttpServerHandlerProvider(httpDriver, routingFlow, requestExecutor)
  private[this] val initializer = new HttpServerInitializer(provider)
  private[this] val server      = new NettyServer(port, initializer)

  val routingTableMessage =
    s"""Routing table:
       |
       |${router.showRoutes.map("  " + _).mkString("\n")}
     """.stripMargin

  override def start(): Unit = {
    println("Starting server ( Netty ) ...")
    println(routingTableMessage)

    server.start()
  }
  override def stop(): Unit = println("stop")
}
