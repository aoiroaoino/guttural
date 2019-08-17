package ocicat.http

sealed abstract class ContentType extends Product with Serializable

object ContentType {
  case object TextPlain extends ContentType
  object Application {
    case object Json        extends ContentType
    case object OctetStream extends ContentType
  }

  val `text/plain`: ContentType               = TextPlain
  val `application/json`: ContentType         = Application.Json
  val `application/octet-stream`: ContentType = Application.OctetStream
}
