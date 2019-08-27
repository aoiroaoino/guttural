package monoton.server

import java.nio.charset.{Charset, StandardCharsets}

import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.handler.codec.http.{DefaultFullHttpResponse, HttpResponseStatus, HttpVersion}

package object netty {

  private[netty] def NettyBadRequest(
      httpVersion: HttpVersion,
      contentString: String,
      charset: Charset = DefaultCharset
  ): DefaultFullHttpResponse =
    new DefaultFullHttpResponse(httpVersion, HttpResponseStatus.BAD_REQUEST, toByteBuf(contentString, charset))

  private[netty] def NettyInternalServerError(
      httpVersion: HttpVersion,
      contentString: String,
      charset: Charset = DefaultCharset
  ): DefaultFullHttpResponse =
    new DefaultFullHttpResponse(
      httpVersion,
      HttpResponseStatus.INTERNAL_SERVER_ERROR,
      toByteBuf(contentString, charset)
    )

  private val DefaultCharset = StandardCharsets.UTF_8

  private def toByteBuf(s: String, charset: Charset): ByteBuf =
    if (s.isEmpty) Unpooled.EMPTY_BUFFER
    else Unpooled.wrappedBuffer(s.getBytes(charset))
}
