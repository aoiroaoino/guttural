package monoton.http.codec

import monoton.http.RequestBody
import play.api.libs.json.{JsValue, Json}

import scala.util.control.NonFatal

object PlayJsValue extends RequestBody.JsonFactory[JsValue] {

  override def from(bytes: Array[Byte]): Option[JsValue] =
    try Some(Json.parse(bytes))
    catch { case NonFatal(_) => None }
}
