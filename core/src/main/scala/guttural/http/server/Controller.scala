package guttural.http
package server

import java.nio.charset.{Charset, StandardCharsets}

import guttural.http.FormMapping.MappingError
import guttural.http.RequestBody.JsonFactory
import guttural.syntax.AllSyntax
import guttural.util.Read

trait Controller extends ResponseBuilders with AllSyntax {

  implicit def autoBuild(builder: HandlerBuilder[Response]): RequestHandler = builder.build

  def run: RequestHandler

  object request {

    object cookies {

      def get(name: String): HandlerBuilder[Option[Cookie]] = HandlerBuilder.getRequest.map(_.cookies.get(name))

      def all: HandlerBuilder[Cookies] = HandlerBuilder.getRequest.map(_.cookies)
    }

    object queryString {

      def get[A](key: String)(implicit M: Read[A]): HandlerBuilder[A] =
        for {
          req <- HandlerBuilder.getRequest
          a <- HandlerBuilder.someValue(for {
            vs <- req.queryString.get(key)
            v  <- vs.headOption
            r  <- M.readOption(v)
          } yield r)(BadRequest)
        } yield a

      def getOption[A](key: String)(implicit M: Read[A]): HandlerBuilder[Option[A]] =
        for {
          req <- HandlerBuilder.getRequest
          a <- HandlerBuilder.pure(for {
            vs <- req.queryString.get(key)
            v  <- vs.headOption
            r  <- M.readOption(v)
          } yield r)
        } yield a
    }

    object body {

      def asBytes: HandlerBuilder[Array[Byte]] = HandlerBuilder.getRequest.map(_.body.asBytes)

      def asText(charset: Charset = StandardCharsets.UTF_8): HandlerBuilder[String] =
        HandlerBuilder.getRequest.map(_.body.asText(charset))

      // application/x-www-form-urlencoded and multipart/form-data

      def as[A](mapping: FormMapping[A], ifError: List[MappingError] => Response): HandlerBuilder[A] =
        HandlerBuilder.getRequest
          .flatMap(req => HandlerBuilder.rightValue(req.body.asMultipartFormData.attributes.to(mapping))(ifError))

      def as[A](mapping: FormMapping[A]): HandlerBuilder[A] = as(mapping, _ => BadRequest)

      // multipart/form-data

      object files {}

      // application/json

      def as[A](factory: JsonFactory[A], ifError: => Response): HandlerBuilder[A] =
        HandlerBuilder.getRequest.flatMap(req => HandlerBuilder.someValue(req.body.asJson.to(factory))(ifError))

      def as[A](factory: JsonFactory[A]): HandlerBuilder[A] = as(factory, BadRequest)
    }
  }
}
