package monoton.server

import monoton.http.RequestBody.JsonFactory
import monoton.http.{Request, Response, ResponseBuilders}

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

      def as[A](factory: JsonFactory[A]): Handler[A] =
        for {
          req <- Handler.getRequest
          a   <- Handler.someValue(req.body.asJson.to(factory))(BadRequest(""))
        } yield a
    }
  }
}
