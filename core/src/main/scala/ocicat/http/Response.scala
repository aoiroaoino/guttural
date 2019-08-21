package monoton.http

import java.nio.charset.StandardCharsets

final case class Response(status: Status, contentType: ContentType, content: Array[Byte])

object Response {

  sealed abstract class ResponseBuilder(status: Status) {

    def apply[A](a: A)(implicit encoder: ContentEncoder[A]): Response =
      Response(status, encoder.contentType, encoder.encode(a))

    def empty[A](): Response = Response(status, ContentType.`application/octet-stream`, Array.empty)
  }
  object Ok             extends ResponseBuilder(Status.Ok)
  object NotFound       extends ResponseBuilder(Status.NotFound)
  object BadRequest     extends ResponseBuilder(Status.BadRequest)
  object NotImplemented extends ResponseBuilder(Status.NotImplemented)
}

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
