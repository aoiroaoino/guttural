package monoton.server

import monoton.http.Method

sealed abstract class Router {
  private[server] def routes: Seq[Route]

  def findRoute(method: Method, path: String): Option[Route] =
    routes.collectFirst { case r if r.method == method && r.path == path => r }

  def showRoutes: String =
    routes.map(r => "%-7s %s".format(r.method, r.path)).mkString("\n")
}

object Router {
  def apply(rs: Route*): Router = new Router {
    override def routes: Seq[Route] = rs.toSeq
  }
}
