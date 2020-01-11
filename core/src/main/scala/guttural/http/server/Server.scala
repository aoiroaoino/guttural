package guttural.http.server

import scala.concurrent.duration._

abstract class Server {

  def start(host: String, port: Int): Unit

  def stop(timeout: FiniteDuration): Unit

  final def start(host: String): Unit = start(host, Server.DefaultPort)
  final def start(port: Int): Unit    = start(Server.Localhost, port)
  final def start(): Unit             = start(Server.Localhost, Server.DefaultPort)

  final def stop(): Unit = stop(Server.DefaultTimeout)
}

object Server {
  final val Localhost   = "localhost"
  final val DefaultPort = 12000

  final val DefaultTimeout = 3.minutes
}
