package monoton.http

import java.nio.ByteBuffer

import io.circe.Json
import io.circe.jawn.parseByteBuffer

object CirceJson extends RequestBody.JsonFactory[Json] {

  override def from(bytes: Array[Byte]): Option[Json] =
    parseByteBuffer(ByteBuffer.wrap(bytes)).toOption
}
