package ocicat.http

sealed abstract class ContentType extends Product with Serializable

object ContentType {
  object Text {
    case object Plain extends ContentType
  }
  object Application {
    case object Json               extends ContentType
    case object OctetStream        extends ContentType
    case object XWWWFORMUrlencoded extends ContentType
  }

  val `text/plain`: ContentType               = Text.Plain
  val `application/json`: ContentType         = Application.Json
  val `application/octet-stream`: ContentType = Application.OctetStream

  def fromString(s: String): Option[ContentType] = PartialFunction.condOpt(s.toLowerCase) {
    case "text/plain"                        => Text.Plain
    case "application/json"                  => Application.Json
    case "application/x-www-form-urlencoded" => Application.XWWWFORMUrlencoded
  }
}
