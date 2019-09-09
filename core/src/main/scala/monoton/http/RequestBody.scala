package monoton.http

import java.nio.charset.{Charset, StandardCharsets}

import monoton.http.FormMapping.MappingError

abstract class RequestBody extends Serializable {

  def asBytes: Array[Byte]

  def asText(charset: Charset): String
  def asText: String = asText(RequestBody.DefaultCharset)

  def asJson: RequestBody.AsJson

  def asXWWWFormUrlencoded: RequestBody.AsForm
  def asMultipartFormData: RequestBody.AsForm
}

object RequestBody {

  val DefaultCharset: Charset = StandardCharsets.UTF_8

  // text/plain

  abstract class TextPlain extends RequestBody {
    override def asMultipartFormData: AsForm  = AsForm.NotConvertible
    override def asXWWWFormUrlencoded: AsForm = AsForm.NotConvertible
  }

  final case class DefaultTextPlain(bytes: Array[Byte], charset: Option[Charset]) extends TextPlain {
    override def asBytes: Array[Byte]             = bytes
    override def asText(charset: Charset): String = new String(bytes, charset)
    override def asJson: AsJson                   = AsJson.Just(bytes)
  }

  // application/x-www-form-urlencoded

  abstract class ApplicationXWWWFormUrlencoded extends RequestBody {
    override def asBytes: Array[Byte]             = Array.emptyByteArray
    override def asText(charset: Charset): String = ""
    override def asJson: AsJson                   = AsJson.NotConvertible
  }

  final case class DefaultApplicationXWWWFormUrlencoded(m: Map[String, String]) extends ApplicationXWWWFormUrlencoded {
    override def asMultipartFormData: AsForm  = AsForm.Just(m) // 厳密にすべきか悩む
    override def asXWWWFormUrlencoded: AsForm = AsForm.Just(m)
  }

  // application/json

  abstract class ApplicationJson extends RequestBody {
    override def asXWWWFormUrlencoded: AsForm = AsForm.NotConvertible
    override def asMultipartFormData: AsForm  = AsForm.NotConvertible
  }

  final case class DefaultApplicationJson(bytes: Array[Byte]) extends ApplicationJson {
    override def asBytes: Array[Byte]             = bytes
    override def asText(charset: Charset): String = new String(bytes, charset)
    override def asJson: AsJson                   = AsJson.Just(bytes)
  }

  abstract class ApplicationOctetStream extends RequestBody {
    override def asXWWWFormUrlencoded: AsForm = AsForm.NotConvertible
    override def asMultipartFormData: AsForm  = AsForm.NotConvertible
  }
  final case class DefaultApplicationOctetStream(bytes: Array[Byte]) extends ApplicationOctetStream {
    override def asBytes: Array[Byte]             = bytes
    override def asText(charset: Charset): String = new String(bytes, charset)
    override def asJson: AsJson                   = AsJson.Just(bytes)
  }

  // multipart/form-data

  abstract class MultipartFormData extends RequestBody {
    override def asJson: AsJson                   = AsJson.NotConvertible
    override def asBytes: Array[Byte]             = Array.emptyByteArray
    override def asText(charset: Charset): String = ""
  }
  final case class DefaultMultipartFormData(atters: Map[String, String] /* , files: Seq[Path] */ )
      extends MultipartFormData {
    override def asXWWWFormUrlencoded: AsForm = AsForm.Just(atters) // 厳密にすべきか悩む
    override def asMultipartFormData: AsForm  = AsForm.Just(atters)
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

  // Form Converter

  final case class Attributes(underlying: Map[String, String]) {
    def to[A](implicit M: FormMapping[A]): Either[List[MappingError], A] = M.getValue(underlying)
  }

  trait AsForm {
    def attributes: Attributes
    // def files
  }
  object AsForm {
    final case class Just(m: Map[String, String]) extends AsForm {
      override def attributes: Attributes = Attributes(m)
    }
    case object NotConvertible extends AsForm {
      override def attributes: Attributes = Attributes(Map.empty)
    }
  }

  // empty

  case object Empty extends RequestBody {
    override def asBytes: Array[Byte]             = Array.emptyByteArray
    override def asText(charset: Charset): String = ""
    override def asJson: AsJson                   = AsJson.NotConvertible
    override def asXWWWFormUrlencoded: AsForm     = AsForm.NotConvertible
    override def asMultipartFormData: AsForm      = AsForm.NotConvertible
  }
}
