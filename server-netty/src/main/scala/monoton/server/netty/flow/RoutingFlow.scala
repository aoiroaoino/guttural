package monoton.server.netty.flow

import monoton.http.{Request, Response, ResponseBuilders}
import monoton.server.{HandleRequest, Router}
import monoton.util.Flow

class RoutingFlow(router: Router) extends Flow[Request, Response, HandleRequest, Response] {

  override def to(req: Request): Either[Response, HandleRequest] =
    router
      .findRouting(req.method, req.absolutePath)
      .map(routing => new HandleRequest(req, routing.getHandler(req.absolutePath)))
      .toRight(ResponseBuilders.NotFound())

  override def from(b: Response, s: Request): Response = b
}
