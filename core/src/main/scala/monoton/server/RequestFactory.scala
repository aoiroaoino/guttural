package monoton.server

import monoton.http.{Request, Response}

import scala.language.higherKinds

abstract class RequestFactory[A] {
  def from(request: Request): Option[A]

  def onFailure(request: Request): Response
}
