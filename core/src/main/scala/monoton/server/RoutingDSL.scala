package monoton.server

import java.util.concurrent.atomic.AtomicInteger

import monoton.http.{Method, Request, Response}

import scala.collection.mutable.ListBuffer
import scala.util.chaining._

trait RoutingDSL extends Router {

  protected override def routes: Seq[Route] = _routes.map(_.build()).toSeq

  private val _routes = ListBuffer.empty[RouteBuilder]

  def GET: RouteBuilder =
    RouteBuilder(id = RouteBuilder.idGen.incrementAndGet(), method = Some(Method.GET)).tap(upsert)

  def POST: RouteBuilder =
    RouteBuilder(id = RouteBuilder.idGen.incrementAndGet(), method = Some(Method.POST)).tap(upsert)

  def TODO: Handler[Response] = Handler.TODO
  def WIP: Handler[Response]  = Handler.WIP

  private[RoutingDSL] def upsert(routeBuilder: RouteBuilder): Unit = {
    val idx = _routes.indexWhere(_.id == routeBuilder.id)
    if (idx == -1) _routes += routeBuilder else _routes.update(idx, routeBuilder)
  }

  final case class RouteBuilder(
      id: Int,
      method: Option[Method] = None,
      path: Option[String] = None,
      handler: Option[() => Handler[Response]] = None
  ) {

    def ~(s: String): RouteBuilder = copy(path = Some(s)).tap(upsert)

    def to(h: => Handler[Response]): RouteBuilder = copy(handler = Some(() => h)).tap(upsert)
    def to(h: Response): RouteBuilder             = to(Handler.later(h))
    def to(h: Request => Response): RouteBuilder  = to(Handler.fromFunction(h))

    def build(): Route = {
      require(method.isDefined && path.isDefined && handler.isDefined)
      new Route(method.get, path.get, handler.get)
    }
  }

  object RouteBuilder {
    private[RoutingDSL] val idGen: AtomicInteger = new AtomicInteger(0)
  }
}
