package guttural.http
package server

import java.nio.charset.StandardCharsets

import akka.http.scaladsl.model.{
  ContentType => AkkaHttpContentType,
  ContentTypes,
  HttpEntity,
  HttpRequest,
  HttpResponse
}
import akka.stream.Materializer

abstract class HttpMessageConvertingPipeline extends Pipeline[HttpRequest, HttpResponse, Request, Response]

final class DefaultAkkaHttpMessageConvertingPipeline()(implicit mat: Materializer)
    extends HttpMessageConvertingPipeline {

  override def upstream(s: HttpRequest): Either[HttpResponse, Request] = {
    val body = s.entity match {
      case entity: HttpEntity.Strict =>
        entity.contentType match {
          case ContentTypes.`text/plain(UTF-8)` =>
            RequestBody.DefaultTextPlain(entity.data.toByteBuffer.array, Some(StandardCharsets.UTF_8))
          case ContentTypes.`application/json` =>
            RequestBody.DefaultApplicationJson(entity.data.toByteBuffer.array)
          case other =>
            println("ContentType: " + other)
            RequestBody.DefaultApplicationOctetStream(entity.data.toByteBuffer.array)
        }
      case other =>
        println("unsupported yet. HttpEntity: " + other)
        other.discardBytes()
        RequestBody.Empty
    }
    Right(
      DefaultRequest(
        Method.fromString(s.method.value).get,
        s.uri.path.toString,
        s.uri.query().toMap.view.mapValues(_ :: Nil).toMap,
        s.headers.map(h => h.name -> h.value).toMap,
        new Cookies(s.cookies.map(c => Cookie(c.name, c.value)).toSet),
        body
      )
    )
  }

  override def downstream(b: Response, s: HttpRequest): HttpResponse = {
    val contentType = AkkaHttpContentType.parse(b.contentType.value).getOrElse(ContentTypes.`application/octet-stream`)
    HttpResponse(entity = HttpEntity(contentType, b.content))
  }
}
