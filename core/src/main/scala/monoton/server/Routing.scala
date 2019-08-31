package monoton.server

import monoton.http.{Method, Response}

final case class Routing(route: Route, handlerFromRequestPath: String => Handler[Response]) {
  val method: Method = route.method
  val path: String   = route.path

  def getHandler(requestPath: String): Handler[Response] = handlerFromRequestPath(requestPath)
}
