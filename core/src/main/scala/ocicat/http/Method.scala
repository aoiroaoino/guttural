package ocicat.http

// https://httpwg.org/specs/rfc7231.html#methods

sealed abstract class Method(val upperCase: String) extends Product with Serializable

object Method {
  case object Get     extends Method("GET")
  case object Head    extends Method("HEAD")
  case object Post    extends Method("POST")
  case object Put     extends Method("PUT")
  case object Delete  extends Method("DELETE")
  case object Connect extends Method("CONNECT")
  case object Options extends Method("OPTIONS")
  case object Trace   extends Method("TRACE")

  private val all = Seq(Get, Head, Post, Put, Delete, Connect, Options, Trace)

  // case-insensitive
  def fromString(s: String): Option[Method] = all.find(_.upperCase == s.toUpperCase)
}
