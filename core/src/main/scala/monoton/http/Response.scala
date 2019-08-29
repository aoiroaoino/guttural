package monoton.http

import java.nio.charset.StandardCharsets

final case class Response(status: Status, contentType: ContentType, content: Array[Byte])

object Response {}

trait ContentEncoder[A] {
  def defaultContentType: ContentType
  def encode(a: A): Array[Byte]
}
object ContentEncoder {
  implicit val stringEncoder: ContentEncoder[String] = new ContentEncoder[String] {
    override def defaultContentType: ContentType = ContentType.`text/plain`
    override def encode(a: String): Array[Byte]  = a.getBytes(StandardCharsets.UTF_8)
  }
  implicit val unitEncoder: ContentEncoder[Unit] = new ContentEncoder[Unit] {
    override def defaultContentType: ContentType = ContentType.`text/plain`
    override def encode(a: Unit): Array[Byte]    = Array.emptyByteArray
  }
}
