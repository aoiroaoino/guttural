package ocicat.server

import ocicat.http.{Method, Response}

final case class Route(method: Method, path: String, handler: Handler[Response])
