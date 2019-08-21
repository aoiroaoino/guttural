package monoton.http

// https://httpwg.org/specs/rfc7231.html#methods

sealed abstract class Method(val upperCase: String) extends Product with Serializable

object Method {
  case object GET     extends Method("GET")
  case object HEAD    extends Method("HEAD")
  case object POST    extends Method("POST")
  case object PUT     extends Method("PUT")
  case object DELETE  extends Method("DELETE")
  case object CONNECT extends Method("CONNECT")
  case object OPTIONS extends Method("OPTIONS")
  case object TRACE   extends Method("TRACE")

  private val all = Seq(GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE)

  // case-insensitive
  def fromString(s: String): Option[Method] = all.find(_.upperCase == s.toUpperCase)
}
