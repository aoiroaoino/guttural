package monoton.http.codec

import monoton.http.{ContentEncoder, ContentType}
import play.api.libs.json.{JsValue, Json}

package object playjson {

  implicit val jsValueContentEncoder: ContentEncoder[JsValue] =
    new ContentEncoder[JsValue] {
      override val defaultContentType: ContentType = ContentType.`application/json`
      override def encode(a: JsValue): Array[Byte] = Json.toBytes(a)
    }
}
