package guttural.http.server

import guttural.http.{server, Method, Response}

import scala.language.higherKinds

trait RoutingDSLByTypedFinal extends Router.Factory {

  override lazy val router: server.Router = ???

  def routes[A <: RoutingDSLByTypedFinal.RouterSym](sym: A): sym.Repr[Router]
}

object RoutingDSLByTypedFinal {

  trait RouterSym { self =>
    type Repr[A]

    def path(s: String): Repr[String]

    def method(m: Method): Repr[Method]

    def handler(h: => HandlerBuilder[Response]): Repr[() => HandlerBuilder[Response]]

    def route(
        method: Repr[Method],
        path: Repr[String],
        handler: Repr[() => HandlerBuilder[Response]]
    ): Repr[Router.Route]

    def routing(routes: Repr[Router.Route]*): Repr[Router]

    // helpers

    def GET(path: String, handler: => HandlerBuilder[Response]): Repr[Router.Route] =
      route(method(Method.GET), self.path(path), self.handler(handler))

    def POST(path: String, handler: => HandlerBuilder[Response]): Repr[Router.Route] =
      route(method(Method.POST), self.path(path), self.handler(handler))
  }
}

class SomeRouter extends RoutingDSLByTypedFinal {

  override def routes[A <: RoutingDSLByTypedFinal.RouterSym](sym: A): sym.Repr[Router] = {
    import sym._
    routing(
      GET(
        path = "/users",
        handler = HandlerBuilder.TODO
      ),
      POST(
        path = "/users",
        handler = HandlerBuilder.TODO
      ),
    )
  }
}
