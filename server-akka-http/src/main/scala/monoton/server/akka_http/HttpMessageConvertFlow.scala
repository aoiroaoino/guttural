package monoton.server.akka_http

import java.nio.charset.StandardCharsets

import akka.http.scaladsl.model._
import akka.stream.Materializer
import monoton.http.{ContentType => _, _}
import monoton.util.Flow

class HttpMessageConvertFlow(implicit mat: Materializer) extends Flow[HttpRequest, HttpResponse, Request, Response] {

  override def to(s: HttpRequest): Either[HttpResponse, Request] = {
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
      case entity: HttpEntity.Default =>
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

  override def from(b: Response, s: HttpRequest): HttpResponse = {
    val contentType = ContentType.parse(b.contentType.value).getOrElse(ContentTypes.`application/octet-stream`)
    HttpResponse(entity = HttpEntity(contentType, b.content))
  }
}
