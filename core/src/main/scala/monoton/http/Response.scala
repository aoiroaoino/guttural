package monoton.http

import java.nio.charset.StandardCharsets

final case class Response(status: Status, contentType: ContentType, content: Array[Byte])

object Response {}

trait ContentEncoder[A] {
  def contentType: ContentType
  def encode(a: A): Array[Byte]
}
object ContentEncoder {
  implicit val stringEncoder: ContentEncoder[String] = new ContentEncoder[String] {
    override def contentType: ContentType       = ContentType.`text/plain`
    override def encode(a: String): Array[Byte] = a.getBytes(StandardCharsets.UTF_8)
  }
}
