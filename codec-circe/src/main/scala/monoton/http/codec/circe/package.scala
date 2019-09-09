package monoton.http.codec

import java.nio.charset.StandardCharsets

import io.circe.{Json, Printer}
import monoton.http.{ContentEncoder, ContentType}

package object circe {

  implicit val jsonContentEncoder: ContentEncoder[Json] = new ContentEncoder[Json] {
    override def defaultContentType: ContentType = ContentType.`application/json`
    override def encode(a: Json): Array[Byte]    = a.pretty(Printer.noSpaces).getBytes(StandardCharsets.UTF_8)
  }
}
