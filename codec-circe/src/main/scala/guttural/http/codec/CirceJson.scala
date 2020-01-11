package guttural.http.codec

import java.nio.ByteBuffer

import io.circe.Json
import io.circe.jawn.parseByteBuffer
import guttural.http.RequestBody

object CirceJson extends RequestBody.JsonFactory[Json] {

  override def from(bytes: Array[Byte]): Option[Json] =
    parseByteBuffer(ByteBuffer.wrap(bytes)).toOption
}
