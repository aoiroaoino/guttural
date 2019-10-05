package monoton.client

import scala.language.higherKinds
import monoton.http.{RequestBody, Response}

abstract class HttpClient[F[_]] {

  def get(url: String, queryStrings: Map[String, String]): F[Response]

  final def get(url: String): F[Response] = get(url, Map.empty)

  def post(url: String, body: RequestBody): F[Response]
}
