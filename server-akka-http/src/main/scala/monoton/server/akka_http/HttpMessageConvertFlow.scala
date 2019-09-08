package monoton.server.akka_http.flow

import java.nio.charset.StandardCharsets

import akka.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity, HttpRequest, HttpResponse}
import monoton.http.{Cookie, Cookies, DefaultRequest, Method, Request, RequestBody, Response}
import monoton.util.Flow

class HttpMessageConvertFlow extends Flow[HttpRequest, HttpResponse, Request, Response] {

  override def to(s: HttpRequest): Either[HttpResponse, Request] = {
    val body = s.entity match {
      case e: HttpEntity.Strict =>
        e.contentType match {
          case ContentTypes.`text/plain(UTF-8)` =>
            RequestBody.DefaultTextPlain(e.data.toByteBuffer.array, Some(StandardCharsets.UTF_8))
          case ContentTypes.`application/json` =>
            RequestBody.DefaultApplicationJson(e.data.toByteBuffer.array)
          case other =>
            println("ContentType: " + other)
            RequestBody.DefaultApplicationOctetStream(e.data.toByteBuffer.array)
        }
      case other =>
        println("unsupported yet. HttpEntity: " + other)
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
