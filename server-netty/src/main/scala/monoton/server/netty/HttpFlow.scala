package monoton.server.netty

import java.net.URI

import scala.util.chaining._
import scala.jdk.CollectionConverters._
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import io.netty.handler.codec.http.multipart.{Attribute, HttpPostMultipartRequestDecoder}
import monoton.http.{ContentType, Method, Request, RequestBody, Response}
import monoton.util.Flow

class HttpFlow extends Flow[HttpRequest, HttpResponse, Request, Response] {
  import HttpHeaderNames._, HttpHeaderValues._

  override def to(httpReq: HttpRequest): Either[HttpResponse, Request] = {
    val header = httpReq.headers.entries.asScala.map(e => (e.getKey, e.getValue)).toMap
    val uri    = new URI(httpReq.uri)
    val query = Option(uri.getQuery)
      .map(_.split('&').toList)
      .map(_.map(_.split('=').toList))
      .map(_.collect { case k :: v :: Nil => (k, v) }.toMap)
      .getOrElse(Map.empty)
    val rawBody = httpReq match {
      case fullReq: FullHttpRequest =>
        val buf = new Array[Byte](fullReq.content.readableBytes)
        fullReq.content.duplicate.readBytes(buf)
        buf
      case _ =>
        Array.emptyByteArray
    }
    for {
      reqMethod <- Method
        .fromString(httpReq.method.name)
        .filter(Method.isSupported)
        .toRight(InternalServerError(httpReq.protocolVersion, "Unsupported Method"))
      contentType <- {
        if (reqMethod == Method.GET) Right(None)
        else
          Option(httpReq.headers.get(CONTENT_TYPE))
            .map(_.split(';')(0).trim) // TODO
            .flatMap(ContentType.fromString)
            .toRight(BadRequest(httpReq.protocolVersion, "Missing Content-Type"))
            .map(Some(_))
      }
    } yield {
      new Request {
        override def method: Method                       = reqMethod
        override def requestTarget: Request.RequestTarget = Request.RequestTarget.OriginForm(uri)
        override def headers: Map[String, String]         = header

        override def body: RequestBody = {
          contentType match {
            case Some(ContentType.`application/json`) =>
              RequestBody.DefaultApplicationJson(rawBody)
            case Some(ContentType.`text/plain`) =>
              RequestBody.DefaultTextPlain(rawBody, None)
            case Some(ContentType.`multipart/form-data`) =>
              val decoder = new HttpPostMultipartRequestDecoder(httpReq)
              val data = Iterator
                .continually(decoder)
                .takeWhile(_.hasNext())
                .map { a =>
                  val d = a.next()
                  (d.getName, d.getHttpDataType, d)
                }
                .collect { case (name, HttpDataType.Attribute, d) => (name, d.asInstanceOf[Attribute].getValue) }
                .toMap
              RequestBody.DefaultMultipartFormData(data).tap(_ => decoder.destroy())
            case _ =>
              RequestBody.Empty
          }
        }
      }
    }
  }

  override def from(res: Response, httpReq: HttpRequest): HttpResponse = {
    val status = HttpResponseStatus.valueOf(res.status.code)
    val content =
      if (res.content.isEmpty) Unpooled.EMPTY_BUFFER
      else Unpooled.wrappedBuffer(res.content)

    new DefaultFullHttpResponse(httpReq.protocolVersion(), status, content)
      .tap { httpRes =>
        httpRes
          .headers()
          .set(CONTENT_TYPE, res.contentType.value)
          .setInt(CONTENT_LENGTH, httpRes.content.readableBytes)
      }
  }
}
