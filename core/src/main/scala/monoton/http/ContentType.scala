package monoton.http

sealed abstract case class ContentType(value: String)

object ContentType {

  val `text/plain`: ContentType                        = new ContentType("text/plain")                        {}
  val `text/html`: ContentType                         = new ContentType("text/html")                         {}
  val `application/json`: ContentType                  = new ContentType("application/json")                  {}
  val `application/octet-stream`: ContentType          = new ContentType("application/octet-stream")          {}
  val `application/x-www-form-urlencoded`: ContentType = new ContentType("application/x-www-form-urlencoded") {}
  val `multipart/form-data`: ContentType               = new ContentType("multipart/form-data")               {}

  def fromString(s: String): Option[ContentType] = PartialFunction.condOpt(s.toLowerCase) {
    case "text/plain"                        => `text/plain`
    case "text/html"                         => `text/html`
    case "application/json"                  => `application/json`
    case "application/x-www-form-urlencoded" => `application/x-www-form-urlencoded`
    case "multipart/form-data"               => `multipart/form-data`
    case _                                   => `application/octet-stream`
  }
}
