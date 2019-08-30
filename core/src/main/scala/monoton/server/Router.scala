package monoton.server

import monoton.http.Method

abstract class Router {

  protected def routes: Seq[Route]

  def findRoute(method: Method, path: String): Option[Route] =
    routes.collectFirst { case r if r.method == method && r.path == path => r }

  def isDefinedRouteAt(method: Method, path: String): Boolean = findRoute(method, path).isDefined

  def showRoutes: Seq[String] = routes.map(r => "%-7s %s".format(r.method, r.path))
}

object Router {
  def apply(rs: Route*): Router = new Router {
    override def routes: Seq[Route] = rs.toSeq
  }
}
