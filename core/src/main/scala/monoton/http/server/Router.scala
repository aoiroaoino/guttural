package monoton.http
package server

import scala.concurrent.Future

abstract class Router {
  def resolveHandler(req: Request): Option[RequestHandler]
}

object Router {

  def ofDynamic(pf: PartialFunction[Request, Future[Response]]): Router =
    PartialFunction.condOpt(_) {
      case r if pf.isDefinedAt(r) =>
        new RequestHandler {
          override def run(a: Request): Future[Response] = pf(a)
        }
    }

  def ofRoutingTable(routingTable: Routing.Table): Router =
    req => routingTable.findHandler(req.method, req.absolutePath)

  private[server] abstract class Factory {
    def router: Router
  }

  final case class Route(
      method: Method,
      rawPath: String,
      handler: RequestHandler
  ) {
    private val placeholderPattern = """\{([a-zA-Z_][a-zA-Z0-9_]*?)\}""".r

    val segments: Seq[String]        = rawPath.split('/').toSeq
    val patternSegments: Seq[String] = segments.collect { case placeholderPattern(key) => key }

    def getPathParams(requestPath: String): Map[String, String] = {
      if (isMatchingPath(requestPath)) {
        val targetPathSegments = requestPath.split('/').toSeq
        segments.zip(targetPathSegments).collect { case (placeholderPattern(s), t) => (s, t) }.toMap
      } else {
        Map.empty
      }
    }

    def isMatching(requestMethod: Method, requestPath: String): Boolean =
      isMatchingMethod(requestMethod) && isMatchingPath(requestPath)

    // HEAD リクエストの場合は GET リクエストも一致とみなす
    private[monoton] def isMatchingMethod(requestMethod: Method): Boolean =
      if (requestMethod == Method.HEAD) method == Method.HEAD || method == Method.GET
      else method == requestMethod

    private[monoton] def isMatchingPath(requestPath: String): Boolean =
      if (patternSegments.isEmpty) {
        rawPath == requestPath
      } else {
        val targetPathSegments = requestPath.split('/').toSeq
        if (targetPathSegments.nonEmpty && segments.length == targetPathSegments.length) {
          segments.zip(targetPathSegments).forall {
            case (placeholderPattern(_), _) => true
            case (s, t)                     => s == t
          }
        } else {
          false
        }
      }
  }
}

object Routing {
  final case class Table(toSeq: Seq[Router.Route]) {
    def findHandler(method: Method, path: String): Option[RequestHandler] =
      toSeq.collectFirst { case r if r.isMatching(method, path) => r.handler }
  }
}
