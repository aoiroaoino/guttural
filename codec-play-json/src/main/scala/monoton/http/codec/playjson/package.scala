package monoton.http.codec

import monoton.http.{ContentType, Response}
import play.api.libs.json.{JsValue, Json}

package object playjson {

  implicit val jsValueContentEncoder: Response.ContentEncoder[JsValue] =
    new Response.ContentEncoder[JsValue] {
      override val defaultContentType: ContentType = ContentType.`application/json`
      override def encode(a: JsValue): Array[Byte] = Json.toBytes(a)
    }
}
