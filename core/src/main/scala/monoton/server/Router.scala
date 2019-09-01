package monoton.server

import monoton.http.Method

abstract class Router {

  def routings: Seq[Routing]

  def routes: Seq[Route] = routings.map(_.route)

  def findRouting(method: Method, path: String): Option[Routing] = {
    routings.collectFirst { case r if r.route.isMatching(method, path) => r }
  }

  def isDefinedRouteAt(method: Method, path: String): Boolean = findRouting(method, path).isDefined

  def showRoutes: Seq[String] = routings.map(r => "%-7s %s".format(r.method, r.path))
}
