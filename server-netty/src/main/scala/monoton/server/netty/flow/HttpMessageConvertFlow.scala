package monoton.server.netty
package flow

import java.net.URI
import java.util

import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.{
  DefaultFullHttpResponse,
  FullHttpRequest,
  HttpHeaderNames,
  HttpRequest,
  HttpResponse,
  HttpResponseStatus,
  QueryStringDecoder
}
import io.netty.handler.codec.http.cookie.ServerCookieDecoder
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import io.netty.handler.codec.http.multipart.{Attribute, HttpPostMultipartRequestDecoder}
import monoton.http.{QueryStringDecoder => _, _}
import monoton.util.Flow

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._
import scala.util.chaining._

class HttpMessageConvertFlow extends Flow[HttpRequest, HttpResponse, Request, Response] {

  override def to(httpReq: HttpRequest): Either[HttpResponse, Request] = {
    val header = httpReq.headers.entries.asScala.map(e => (e.getKey, e.getValue)).toMap
    val uri    = new URI(httpReq.uri)
    val queryString = {
      val decoder = new QueryStringDecoder(httpReq.uri)
      mutable.Map
        .empty[String, Seq[String]]
        .tap { buf =>
          decoder.parameters.forEach { (t: String, u: util.List[String]) =>
            buf += (t -> u.asScala.toList)
          }
        }
        .toMap
    }
    val cookies = httpReq match {
      case fullReq: FullHttpRequest =>
        Option(fullReq.headers.get(HttpHeaderNames.COOKIE)).fold(Cookies.empty) { cookieStr =>
          val buf: mutable.ArrayBuffer[Cookie] = ArrayBuffer.empty
          ServerCookieDecoder.STRICT.decode(cookieStr).forEach(c => buf.addOne(Cookie(c.name, c.value)))
          new Cookies(buf.toSeq)
        }
      case _ =>
        Cookies.empty
    }
    for {
      reqMethod <- Method
        .fromString(httpReq.method.name)
        .filter(Method.isSupported)
        .toRight(InternalServerError(httpReq.protocolVersion, "Unsupported Method"))
      contentType = Option(httpReq.headers.get(HttpHeaderNames.CONTENT_TYPE)) // nullable
        .map(_.split(';')(0).trim) // TODO
        .flatMap(ContentType.fromString)
        .getOrElse(ContentType.`application/octet-stream`)
    } yield {
      def readRawBody(): Array[Byte] =
        httpReq match {
          case fullReq: FullHttpRequest =>
            val buf = new Array[Byte](fullReq.content.readableBytes())
            fullReq.content().readBytes(buf)
            buf
          case _ =>
            // unsupported streaming request yet...
            Array.emptyByteArray
        }
      val body = contentType match {
        case ContentType.`application/json` =>
          RequestBody.DefaultApplicationJson(readRawBody())
        case ContentType.`text/plain` =>
          RequestBody.DefaultTextPlain(readRawBody(), None)
        case ContentType.`multipart/form-data` =>
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
          RequestBody.DefaultMultipartFormData(data).tap(_ => decoder.destroy()).tap(println)
        case ContentType.`application/octet-stream` =>
          RequestBody.DefaultApplicationOctetStream(readRawBody())
        case _ =>
          RequestBody.Empty // invalid content type
      }
      Request(reqMethod, uri, queryString, header, cookies, body)
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
          .set(HttpHeaderNames.CONTENT_TYPE, res.contentType.value)
          .setInt(HttpHeaderNames.CONTENT_LENGTH, httpRes.content.readableBytes)
      }
  }
}
