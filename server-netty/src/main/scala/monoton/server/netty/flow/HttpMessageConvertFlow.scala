package monoton.server.netty.flow

import java.net.URI
import java.util

import io.netty.buffer.Unpooled
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import io.netty.handler.codec.http.multipart.{Attribute, HttpPostMultipartRequestDecoder}
import io.netty.util.ReferenceCountUtil
import monoton.http.{QueryStringDecoder => _, _}
import monoton.server.netty.{BadRequest, InternalServerError}
import monoton.util.Flow

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.chaining._

class HttpMessageConvertFlow extends Flow[HttpRequest, HttpResponse, Request, Response] {
  import HttpHeaderNames._

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
        case Some(ContentType.`application/json`) =>
          RequestBody.DefaultApplicationJson(readRawBody())
        case Some(ContentType.`text/plain`) =>
          RequestBody.DefaultTextPlain(readRawBody(), None)
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
      Request(reqMethod, uri, queryString, header, body)
    }
  }

  override def from(res: Response, httpReq: HttpRequest): HttpResponse = {
    httpReq match {
//      case r: FullHttpRequest => r.release()
      case _ => // nop
    }
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
