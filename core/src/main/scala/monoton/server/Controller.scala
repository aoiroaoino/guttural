package monoton.server

import monoton.http.FormMapping.MappingError
import monoton.http.RequestBody.JsonFactory
import monoton.http.{FormMapping, Request, Response, ResponseBuilders}

trait Controller extends ResponseBuilders {

  type RequestHandler = Handler[Response]
  type SimpleHandler  = Request => Response
  type ConstHandler   = Response

  object request {

    def to[A](factory: RequestFactory[A]): Handler[A] =
      for {
        req <- Handler.getRequest
        a   <- Handler.someValue(factory.from(req))(factory.onFailure(req))
      } yield a

//    def toGETRequest(): Handler[GETRequest] = ???

    object body {

      def bindToForm[A](mapping: FormMapping[A])(handleErrors: List[MappingError] => Response): Handler[A] =
        for {
          req  <- Handler.getRequest
          body <- Handler.rightValue(req.body.asMultipartFormData.attributes.to(mapping))(handleErrors)
        } yield body

      def as[A](factory: JsonFactory[A]): Handler[A] =
        Handler.getRequest.flatMap(req => Handler.someValue(req.body.asJson.to(factory))(BadRequest("")))
    }
  }
}
