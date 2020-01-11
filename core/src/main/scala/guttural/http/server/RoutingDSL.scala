package guttural.http
package server

import scala.collection.mutable.ListBuffer
import scala.util.chaining._

trait RoutingDSL extends Router.Factory {

  lazy val router: Router = Router.ofRoutingTable(Routing.Table(builders.map(_.build()).toSeq))

  private val builders = ListBuffer.empty[RoutingBuilder]

  def GET: RoutingBuilder    = createAndRegister(Method.GET)
  def POST: RoutingBuilder   = createAndRegister(Method.POST)
  def PUT: RoutingBuilder    = createAndRegister(Method.PUT)
  def DELETE: RoutingBuilder = createAndRegister(Method.DELETE)
  def PATCH: RoutingBuilder  = createAndRegister(Method.PATCH)

  private def createAndRegister(method: Method): RoutingBuilder =
    RoutingBuilder.create(method).tap(upsert)

  private[RoutingDSL] def upsert(routeBuilder: RoutingBuilder): Unit = {
    val idx = builders.indexWhere(_.id == routeBuilder.id)
    if (idx == -1) builders += routeBuilder else builders.update(idx, routeBuilder)
  }

  protected final case class RoutingBuilder(
      id: Int,
      method: Option[Method] = None,
      path: Option[String] = None,
      handlerFactory: Option[String => HandlerBuilder[Response]] = None
  ) {

    def ~(s: String): RoutingBuilder = copy(path = Some(s)).tap(upsert)

    def to(h: => HandlerBuilder[Response]): RoutingBuilder = copy(handlerFactory = Some(_ => h)).tap(upsert)

    def to(h: Response): RoutingBuilder = to(HandlerBuilder.later(h))

//    def to[A0: Read](h: A0 => HandlerBuilder[Response]): RoutingBuilder = {
//      val ps = route.patternSegments
//      require(ps.length == 1)
//      val factory = { path: String =>
//        h(Read[A0].read(route.getPathParams(path)(ps(0))))
//      }
//      copy(handlerFactory = Some(factory)).tap(upsert)
//    }
//    def to[A0: Read, A1: Read](h: (A0, A1) => HandlerBuilder[Response]): RoutingBuilder = {
//      val ps = route.patternSegments
//      require(ps.length == 2)
//      val factory = { path: String =>
//        h(
//          Read[A0].read(route.getPathParams(path)(ps(0))),
//          Read[A1].read(route.getPathParams(path)(ps(1)))
//        )
//      }
//      copy(handlerFactory = Some(factory)).tap(upsert)
//    }
//    def to[A0: Read, A1: Read, A2: Read](h: (A0, A1, A2) => HandlerBuilder[Response]): RoutingBuilder = {
//      val ps = route.patternSegments
//      require(ps.length == 3)
//      val factory = { path: String =>
//        h(
//          Read[A0].read(route.getPathParams(path)(ps(0))),
//          Read[A1].read(route.getPathParams(path)(ps(1))),
//          Read[A2].read(route.getPathParams(path)(ps(2)))
//        )
//      }
//      copy(handlerFactory = Some(factory)).tap(upsert)
//    }

    // internal

    def build(): Router.Route = {
      require(method.isDefined && path.isDefined)
      require(handlerFactory.isDefined)
      Router.Route(method.get, path.get, handlerFactory.get(path.get).build)
    }
  }

  object RoutingBuilder {
    import java.util.concurrent.atomic.AtomicInteger

    private[RoutingDSL] val idGen: AtomicInteger = new AtomicInteger(0)

    private[RoutingDSL] def create(method: Method): RoutingBuilder =
      RoutingBuilder(
        id = RoutingBuilder.idGen.incrementAndGet(),
        method = Some(method),
      )
  }
}
