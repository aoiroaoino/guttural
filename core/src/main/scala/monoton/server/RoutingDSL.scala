package monoton.server

import java.util.concurrent.atomic.AtomicInteger

import monoton.http.{Method, Request, Response}
import monoton.util.Read

import scala.collection.mutable.ListBuffer
import scala.util.chaining._

trait RoutingDSL extends Router {

  override lazy val routings: Seq[Routing] = builders.map(_.build()).toSeq

  private val builders = ListBuffer.empty[RoutingBuilder]

  def GET: RoutingBuilder    = createAndRegister(Method.GET)
  def POST: RoutingBuilder   = createAndRegister(Method.POST)
  def PUT: RoutingBuilder    = createAndRegister(Method.PUT)
  def DELETE: RoutingBuilder = createAndRegister(Method.DELETE)
  def PATCH: RoutingBuilder  = createAndRegister(Method.PATCH)

  def resource[A <: Controller](resourceManager: A)(f: A => Unit): Unit = f(resourceManager)

  private def createAndRegister(method: Method): RoutingBuilder =
    RoutingBuilder.create(method).tap(upsert)

  private[RoutingDSL] def upsert(routeBuilder: RoutingBuilder): Unit = {
    val idx = builders.indexWhere(_.id == routeBuilder.id)
    if (idx == -1) builders += routeBuilder else builders.update(idx, routeBuilder)
  }

  final case class RoutingBuilder(
      id: Int,
      method: Option[Method] = None,
      path: Option[String] = None,
      handlerFactory: Option[String => Handler[Response]] = None
  ) {

    def ~(s: String): RoutingBuilder = copy(path = Some(s)).tap(upsert)

    def to(h: => Handler[Response]): RoutingBuilder = copy(handlerFactory = Some(_ => h)).tap(upsert)
    def to(h: Response): RoutingBuilder             = to(Handler.later(h))

    def to[A0: Read](h: A0 => Handler[Response]): RoutingBuilder = {
      val ps = route.patternSegments
      require(ps.length == 1)
      val factory = { path: String =>
        h(Read[A0].read(route.getPathParams(path)(ps(0))))
      }
      copy(handlerFactory = Some(factory)).tap(upsert)
    }
    def to[A0: Read, A1: Read](h: (A0, A1) => Handler[Response]): RoutingBuilder = {
      val ps = route.patternSegments
      require(ps.length == 2)
      val factory = { path: String =>
        h(
          Read[A0].read(route.getPathParams(path)(ps(0))),
          Read[A1].read(route.getPathParams(path)(ps(1)))
        )
      }
      copy(handlerFactory = Some(factory)).tap(upsert)
    }
    def to[A0: Read, A1: Read, A2: Read](h: (A0, A1, A2) => Handler[Response]): RoutingBuilder = {
      val ps = route.patternSegments
      require(ps.length == 3)
      val factory = { path: String =>
        h(
          Read[A0].read(route.getPathParams(path)(ps(0))),
          Read[A1].read(route.getPathParams(path)(ps(1))),
          Read[A2].read(route.getPathParams(path)(ps(2)))
        )
      }
      copy(handlerFactory = Some(factory)).tap(upsert)
    }

    // internal

    private def route: Route = {
      require(method.isDefined && path.isDefined)
      new Route(method.get, path.get)
    }

    def build(): Routing = {
      require(handlerFactory.isDefined)
      Routing(route, handlerFactory.get)
    }
  }

  object RoutingBuilder {
    private[RoutingDSL] val idGen: AtomicInteger = new AtomicInteger(0)

    private[RoutingDSL] def create(method: Method): RoutingBuilder =
      RoutingBuilder(
        id = RoutingBuilder.idGen.incrementAndGet(),
        method = Some(method),
      )
  }
}
