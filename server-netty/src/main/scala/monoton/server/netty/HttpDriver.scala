package monoton.server.netty

import java.net.URI
import scala.util.chaining._

import io.netty.buffer.Unpooled
import io.netty.handler.codec.http._
import monoton.http.{ContentType, DefaultRequest, Method, Request, Response}
import monoton.server.Driver

class HttpDriver extends Driver[HttpRequest, HttpResponse, Request, Response] {
  import HttpHeaderNames._, HttpHeaderValues._

  override def to(httpReq: HttpRequest): Either[HttpResponse, Request] = {
    val uri = new URI(httpReq.uri)
    val request: Request = {
      val query = Option(uri.getQuery)
        .map(_.split('&').toList)
        .map(_.map(_.split('=').toList))
        .map(_.collect { case k :: v :: Nil => (k, v) }.toMap)
        .getOrElse(Map.empty)
      val body = httpReq match {
        case fullReq: FullHttpRequest =>
          val buf = new Array[Byte](fullReq.content.readableBytes)
          fullReq.content.duplicate.readBytes(buf)
          buf
        case _ =>
          Array.emptyByteArray
      }
      val method = Method.fromString(httpReq.method.name).get
      val contentType =
        if (method == Method.GET) None
        else
          Option(httpReq.headers.get(CONTENT_TYPE)).flatMap(ContentType.fromString) orElse Some(
            ContentType.`application/x-www-form-urlencoded`
          )
      DefaultRequest(query, contentType, body, uri, method)
    }
    // TODO: 不正だったり対応してなかったり、Request に変換できないリクエストだった場合は Left[HttpResponse]
    Right(request)
  }

  override def from(res: Response, httpReq: HttpRequest): HttpResponse = {
    val status = Converter.toHttpResponseStatus(res.status)
    val content =
      if (res.content.isEmpty) Unpooled.EMPTY_BUFFER
      else Unpooled.wrappedBuffer(res.content)

    new DefaultFullHttpResponse(httpReq.protocolVersion(), status, content)
      .tap { res =>
        res
          .headers()
          .set(CONTENT_TYPE, TEXT_PLAIN)
          .setInt(CONTENT_LENGTH, res.content.readableBytes)
      }
  }
}
