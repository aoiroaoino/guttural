package monoton.server

import monoton.http.FormMapping.MappingError
import monoton.http.RequestBody.JsonFactory
import monoton.http.{Cookie, Cookies, FormMapping, Request, Response, ResponseBuilders}
import monoton.syntax.AllSyntax
import monoton.util.Read

trait Resource extends ResponseBuilders with AllSyntax {

  type RequestHandler = Handler[Response]

  // 一貫性がなくなるが、固定値の Response は直接書けるでもいいかな？という実験的な目論見で追加してみる。
  implicit def constResponseToHandlerResponse(res: Response): Handler[Response] = Handler.pure(res)

  object request {

    def to[A](factory: RequestFactory[A]): Handler[A] =
      for {
        req <- Handler.getRequest
        a   <- Handler.someValue(factory.from(req))(factory.onFailure(req))
      } yield a

    object cookies {

      def get(name: String): Handler[Option[Cookie]] = Handler.getRequest.map(_.cookies.get(name))

      def all: Handler[Cookies] = Handler.getRequest.map(_.cookies)
    }

    object queryString {

      def get[A](key: String)(implicit M: Read[A]): Handler[A] =
        for {
          req <- Handler.getRequest
          a <- Handler.someValue(for {
            vs <- req.queryString.get(key)
            v  <- vs.headOption
            r  <- M.readOption(v)
          } yield r)(BadRequest)
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

      def as[A](mapping: FormMapping[A]): Handler[A] = as(mapping, _ => BadRequest)

      // json

      def as[A](factory: JsonFactory[A], ifError: => Response): Handler[A] =
        Handler.getRequest.flatMap(req => Handler.someValue(req.body.asJson.to(factory))(ifError))

      def as[A](factory: JsonFactory[A]): Handler[A] = as(factory, BadRequest)
    }
  }
}
