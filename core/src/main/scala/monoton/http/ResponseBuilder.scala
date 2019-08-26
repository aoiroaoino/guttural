package monoton.http

abstract class ResponseBuilder {
  def status: Status
}

abstract class ResponseBuilderWithBody extends ResponseBuilder {

  final def apply[A](a: A)(implicit encoder: ContentEncoder[A]): Response =
    Response(status, encoder.contentType, encoder.encode(a))
}
