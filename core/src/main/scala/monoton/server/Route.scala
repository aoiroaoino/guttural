package monoton.server

import monoton.http.{Method, Request, Response}

final class Route(val method: Method, val path: String, val handler: () => Handler[Response])

object Route {

  def apply(method: Method, path: String, handler: => Handler[Response]): Route =
    new Route(method, path, () => handler)

  def apply(method: Method, path: String, response: => Response)(implicit dummyImplicit: DummyImplicit): Route =
    new Route(method, path, () => Handler.later(response))

  def apply(method: Method, path: String, handleFunc: Request => Response): Route =
    new Route(method, path, () => Handler.fromFunction(handleFunc))
}
