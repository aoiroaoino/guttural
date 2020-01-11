import sbt._

object Dependencies {

  val v = new {
    val airframe = "19.12.4"
    // servers
    val akkaHttp = "10.1.9"
    // adapters
    val circe = "0.12.1"
    // other (common)
    val slf4jApi = "1.7.28"
  }

  val airframe = "org.wvlet.airframe" %% "airframe" % v.airframe
}
