package monoton.http.server

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.Materializer
import monoton.http.{Request, Response}

abstract class HttpMessageConvertingPipeline extends Pipeline[HttpRequest, HttpResponse, Request, Response]

final class DefaultAkkaHttpMessageConvertingPipeline()(implicit mat: Materializer)
    extends HttpMessageConvertingPipeline {

  override def upstream(s: HttpRequest): Either[HttpResponse, Request] = ???

  override def downstream(b: Response, s: HttpRequest): HttpResponse = ???
}
