package monoton.server

import monoton.http.{Method, Response}

final case class Route(method: Method, path: String, handler: Handler[Response])
