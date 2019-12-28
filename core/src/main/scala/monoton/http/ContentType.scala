package monoton.http

sealed abstract case class ContentType(value: String)

object ContentType {
  private def apply(value: String): ContentType = new ContentType(value) {}

  final val `text/plain`: ContentType = ContentType("text/plain")
  final val `text/html`: ContentType  = ContentType("text/html")

  final val `multipart/form-data`: ContentType = ContentType("multipart/form-data")

  final val `application/json`: ContentType                  = ContentType("application/json")
  final val `application/x-www-form-urlencoded`: ContentType = ContentType("application/x-www-form-urlencoded")
  final val `application/octet-stream`: ContentType          = ContentType("application/octet-stream")

  def fromString(s: String): Option[ContentType] = PartialFunction.condOpt(s.toLowerCase) {
    case `text/plain`.value                        => `text/plain`
    case `text/html`.value                         => `text/html`
    case `application/json`.value                  => `application/json`
    case `application/x-www-form-urlencoded`.value => `application/x-www-form-urlencoded`
    case `multipart/form-data`.value               => `multipart/form-data`
    case `application/octet-stream`.value          => `application/octet-stream`
  }
}
