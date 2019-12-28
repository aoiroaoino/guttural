package monoton.http.server

import scala.concurrent.duration._

abstract class Server {

  def start(host: String, port: Int): Unit

  final def start(host: String): Unit = start(host, Server.DefaultPort)
  final def start(): Unit             = start(Server.Localhost, Server.DefaultPort)

  def stop(timeout: FiniteDuration): Unit

  final def stop(): Unit = stop(Server.DefaultTimeout)
}

object Server {
  final val Localhost   = "localhost"
  final val DefaultPort = 12000

  final val DefaultTimeout = 3.minutes
}
