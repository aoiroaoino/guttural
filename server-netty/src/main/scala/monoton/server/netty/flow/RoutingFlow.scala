package monoton.server.netty.flow

import monoton.http.{Request, Response, ResponseBuilders}
import monoton.server.netty.HandleRequest
import monoton.server.Router
import monoton.util.Flow

class RoutingFlow(router: Router) extends Flow[Request, Response, HandleRequest, Response] {

  override def to(req: Request): Either[Response, HandleRequest] =
    router
      .findRoute(req.method, req.absolutePath)
      .map(new HandleRequest(req, _))
      .toRight(ResponseBuilders.NotFound())

  override def from(b: Response, s: Request): Response = b
}
