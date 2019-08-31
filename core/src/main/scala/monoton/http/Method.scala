package monoton.http

abstract class Method(val token: String) extends Product with Serializable

object Method {

  // https://httpwg.org/specs/rfc7231.html#methods
  case object GET     extends Method("GET")
  case object HEAD    extends Method("HEAD")
  case object POST    extends Method("POST")
  case object PUT     extends Method("PUT")
  case object DELETE  extends Method("DELETE")
  case object CONNECT extends Method("CONNECT")
  case object OPTIONS extends Method("OPTIONS")
  case object TRACE   extends Method("TRACE")

  private val all = Seq(GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE)
  def fromString(s: String): Option[Method] =
    all.find(_.token == s.toUpperCase) // case-insensitive

  // current supported methods
  private val supportedMethods = Seq(GET, HEAD, POST, PUT)
  def isSupported(method: Method): Boolean =
    supportedMethods.contains(method)
}
