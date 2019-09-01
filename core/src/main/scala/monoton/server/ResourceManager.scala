package monoton.server

import monoton.http.FormMapping.MappingError
import monoton.http.RequestBody.JsonFactory
import monoton.http.{FormMapping, Request, Response, ResponseBuilders}
import monoton.util.Read

trait ResourceManager extends ResponseBuilders {

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

    object queryString {

      def get[A](key: String)(implicit M: Read[A]): Handler[A] =
        for {
          req <- Handler.getRequest
          a <- Handler.someValue(for {
            vs <- req.queryString.get(key)
            v  <- vs.headOption
            r  <- M.readOption(v)
          } yield r)(BadRequest())
        } yield a

      def getOption[A](key: String)(implicit M: Read[A]): Handler[Option[A]] =
        for {
          req <- Handler.getRequest
          a <- Handler.pure(for {
            vs <- req.queryString.get(key)
            v  <- vs.headOption
            r  <- M.readOption(v)
          } yield r)
        } yield a
    }

    object body {

      // form

      def as[A](mapping: FormMapping[A], ifError: List[MappingError] => Response): Handler[A] =
        Handler.getRequest
          .flatMap(req => Handler.rightValue(req.body.asMultipartFormData.attributes.to(mapping))(ifError))

      def as[A](mapping: FormMapping[A]): Handler[A] = as(mapping, _ => BadRequest())

      // json

      def as[A](factory: JsonFactory[A], ifError: => Response): Handler[A] =
        Handler.getRequest.flatMap(req => Handler.someValue(req.body.asJson.to(factory))(ifError))

      def as[A](factory: JsonFactory[A]): Handler[A] = as(factory, BadRequest())
    }
  }
}
