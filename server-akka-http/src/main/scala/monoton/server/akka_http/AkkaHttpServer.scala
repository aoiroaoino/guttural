package monoton.server.akka_http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import monoton.server.{HEADMethodFilter, RoutingFlow}

class AkkaHttpServer(
    port: Int,
    routingFlow: RoutingFlow
) {
  implicit val system = ActorSystem()
  implicit val mat    = ActorMaterializer()
  implicit val ec     = system.dispatcher

  val convertFlow = new HttpMessageConvertFlow

  val flow = convertFlow |> HEADMethodFilter |> routingFlow

  val source = Http().bind(interface = "localhost", port = port)
  val sink   = Sink.foreach[Http.IncomingConnection](_.handleWithAsyncHandler(flow.runF(_)(_.execute())))

  def start(): Unit = source.to(sink).run()
}
