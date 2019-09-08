package monoton.server

import monoton.server.akka_http.AkkaHttpServer

class ServerImplByAkkaHttp(port: Int, router: Router) extends Server {
  private[this] val routingFlow = new RoutingFlow(router)
  private[this] val server      = new AkkaHttpServer(port, routingFlow)

  val routingTableMessage =
    s"""Routing table:
       |
       |${router.showRoutes.map("  " + _).mkString("\n")}
     """.stripMargin

  override def start(): Unit = {
    println("Starting server ( Akka HTTP ) ...")
    println(routingTableMessage)

    server.start()
  }

  override def stop(): Unit = println("stop")
}
