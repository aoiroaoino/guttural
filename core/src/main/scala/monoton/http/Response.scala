package monoton.http

import java.nio.charset.StandardCharsets

sealed abstract case class Response(
    status: Status,
    contentType: ContentType,
    content: Array[Byte],
    cookies: Cookies = Cookies.empty
) {
  private[http] def clearContent: Response = Response(status, contentType, Array.emptyByteArray, cookies)

  def addCookie(cookie: Cookie): Response = Response(status, contentType, content, cookies.add(cookie))
}

object Response extends ResponseBuilders {

  private[monoton] def apply(
      status: Status,
      contentType: ContentType,
      content: Array[Byte],
      cookies: Cookies = Cookies.empty
  ): Response =
    new Response(status, contentType, content, cookies) {}

  def emptyContent(status: Status): Response = {
    val encoder = ContentEncoder.unitEncoder
    Response(status, encoder.defaultContentType, encoder.encode(()), Cookies.empty)
  }

  abstract class Builder {
    def status: Status
  }

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
      override def defaultContentType: ContentType = ContentType.`application/octet-stream`
      override def encode(a: Unit): Array[Byte]    = Array.emptyByteArray
    }
  }
}

private[http] trait ResponseBuilders {

  final val Ok: Response = Response.emptyContent(Status.Ok)
  final def Ok[A](a: A)(implicit encoder: Response.ContentEncoder[A]): Response =
    Response(Status.Ok, encoder.defaultContentType, encoder.encode(a))

  final val NotFound: Response = Response.emptyContent(Status.NotFound)
  final def NotFound[A](a: A)(implicit encoder: Response.ContentEncoder[A]): Response =
    Response(Status.NotFound, encoder.defaultContentType, encoder.encode(a))

  final val BadRequest: Response = Response.emptyContent(Status.BadRequest)
  final def BadRequest[A](a: A)(implicit encoder: Response.ContentEncoder[A]): Response =
    Response(Status.BadRequest, encoder.defaultContentType, encoder.encode(a))

  final val NotImplemented: Response = Response.emptyContent(Status.NotImplemented)
  final def NotImplemented[A](a: A)(implicit encoder: Response.ContentEncoder[A]): Response =
    Response(Status.NotImplemented, encoder.defaultContentType, encoder.encode(a))

  final val Unauthorized: Response = Response.emptyContent(Status.Unauthorized)
  final def Unauthorized[A](a: A)(implicit encoder: Response.ContentEncoder[A]): Response =
    Response(Status.Unauthorized, encoder.defaultContentType, encoder.encode(a))

  final val InternalServerError: Response = Response.emptyContent(Status.InternalServerError)
  final def InternalServerError[A](a: A)(implicit encoder: Response.ContentEncoder[A]): Response =
    Response(Status.InternalServerError, encoder.defaultContentType, encoder.encode(a))
}
