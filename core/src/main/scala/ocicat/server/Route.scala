package ocicat.server

import java.nio.file.{Path, Paths}

import ocicat.http.Method

final case class Route(method: Method, path: Path, handler: Handler[Response])

object Route {
  def apply(method: Method, path: String, handler: Handler[Response]): Route =
    Route(method, Paths.get(path), handler)
}
