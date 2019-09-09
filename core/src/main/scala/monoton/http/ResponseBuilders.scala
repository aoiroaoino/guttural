package monoton.http

private[monoton] trait ResponseBuilders {

  final val Ok: Response = Response.emptyContent(Status.Ok)
  final def Ok[A](a: A)(implicit encoder: ContentEncoder[A]): Response =
    Response(Status.Ok, encoder.defaultContentType, encoder.encode(a))

  final val NotFound: Response = Response.emptyContent(Status.NotFound)
  final def NotFound[A](a: A)(implicit encoder: ContentEncoder[A]): Response =
    Response(Status.NotFound, encoder.defaultContentType, encoder.encode(a))

  final val BadRequest: Response = Response.emptyContent(Status.BadRequest)
  final def BadRequest[A](a: A)(implicit encoder: ContentEncoder[A]): Response =
    Response(Status.BadRequest, encoder.defaultContentType, encoder.encode(a))

  final val NotImplemented: Response = Response.emptyContent(Status.NotImplemented)
  final def NotImplemented[A](a: A)(implicit encoder: ContentEncoder[A]): Response =
    Response(Status.NotImplemented, encoder.defaultContentType, encoder.encode(a))

  final val Unauthorized: Response = Response.emptyContent(Status.Unauthorized)
  final def Unauthorized[A](a: A)(implicit encoder: ContentEncoder[A]): Response =
    Response(Status.Unauthorized, encoder.defaultContentType, encoder.encode(a))

  final val InternalServerError: Response = Response.emptyContent(Status.InternalServerError)
  final def InternalServerError[A](a: A)(implicit encoder: ContentEncoder[A]): Response =
    Response(Status.InternalServerError, encoder.defaultContentType, encoder.encode(a))
}

object ResponseBuilders extends ResponseBuilders
