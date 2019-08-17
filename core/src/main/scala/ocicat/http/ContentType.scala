package ocicat.http

sealed abstract class ContentType extends Product with Serializable

object ContentType {
  case object TextPlain       extends ContentType
  case object ApplicationJson extends ContentType

  val `text/plain`: ContentType       = TextPlain
  val `application/json`: ContentType = ApplicationJson
}
