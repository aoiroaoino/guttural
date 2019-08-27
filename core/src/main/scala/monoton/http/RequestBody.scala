package monoton.http

import java.nio.charset.{Charset, StandardCharsets}

abstract class RequestBody extends Serializable {

  def asBytes: Array[Byte]

  def asText(charset: Charset): String
  def asText: String = asText(RequestBody.DefaultCharset)

  def asJson: RequestBody.AsJson

  def asXml: RequestBody.AsXml

}

object RequestBody {

  val DefaultCharset: Charset = StandardCharsets.UTF_8

  // text/plain

  abstract class TextPlain extends RequestBody

  final case class DefaultTextPlain(bytes: Array[Byte]) extends TextPlain {
    override def asBytes: Array[Byte]             = bytes
    override def asText(charset: Charset): String = new String(bytes, charset)

    override def asJson: AsJson = AsJson.Just(bytes)
    override def asXml: AsXml   = AsXml.Just(bytes)
  }

  // application/json

  abstract class ApplicationJson extends RequestBody

  final case class DefaultApplicationJson(bytes: Array[Byte]) extends ApplicationJson {
    override def asBytes: Array[Byte]             = bytes
    override def asText(charset: Charset): String = new String(bytes, charset)

    override def asJson: AsJson = AsJson.Just(bytes)
    override def asXml: AsXml   = AsXml.NotConvertible
  }

  // multipart/form-data

  abstract class MultipartFormData extends RequestBody {
    override def asJson: AsJson = AsJson.NotConvertible
    override def asXml: AsXml   = AsXml.NotConvertible
  }

  // JSON Converter

  trait AsJson {
    def to[A](factory: JsonFactory[A]): Option[A]
  }
  object AsJson {
    final case class Just(bytes: Array[Byte]) extends AsJson {
      override def to[A](factory: JsonFactory[A]): Option[A] = factory.from(bytes)
    }
    case object NotConvertible extends AsJson {
      override def to[A](factory: JsonFactory[A]): Option[A] = None
    }
  }

  trait JsonFactory[A] {
    def from(bytes: Array[Byte]): Option[A]
  }

  // XML Converter

  trait AsXml {
    def to[A](factory: XmlFactory[A]): Option[A]
  }
  object AsXml {
    final case class Just(bytes: Array[Byte]) extends AsXml {
      override def to[A](factory: XmlFactory[A]): Option[A] = factory.from(bytes)
    }
    case object NotConvertible extends AsXml {
      override def to[A](factory: XmlFactory[A]): Option[A] = None
    }
  }

  trait XmlFactory[A] {
    def from(bytes: Array[Byte]): Option[A]
  }

  // empty

  case object Empty extends RequestBody {
    override def asBytes: Array[Byte]             = Array.emptyByteArray
    override def asText(charset: Charset): String = ""
    override def asJson: AsJson                   = AsJson.NotConvertible
    override def asXml: AsXml                     = AsXml.NotConvertible
  }
}
