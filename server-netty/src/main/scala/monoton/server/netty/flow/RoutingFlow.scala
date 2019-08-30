package monoton.server.netty.flow

import monoton.http.{Request, Response, ResponseBuilders}
import monoton.server.{Route, Router}
import monoton.util.Flow

import scala.util.chaining._

class RoutingFlow(router: Router) extends Flow[Request, Response, (Request, Route), Response] {

  override def to(req: Request): Either[Response, (Request, Route)] =
    router
      .findRoute(req.method, req.absolutePath)
      .map(req -> _)
      .toRight(ResponseBuilders.NotFound())

  override def from(b: Response, s: Request): Response = b
}
