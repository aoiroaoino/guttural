# monoton

Web Framework for Scala.

## Quick Start

```sbt
libraryDependencies += "dev.aoiroaoino" %% "monoton" % "0.1.0-SNAPSHOT"
```

```scala
import monoton.server.RoutingDSL
import monoton.util.Read
// Write other imports

class MyRouter(statusResource: StatusResource) extends RoutingDSL {

  GET ~ "/ping" to statusResource.ping
}
```

```scala
import monoton.http.Response
import monoton.server.{Handler, Resource}
// Write other imports

class StatusResource extends Resource {

  def ping: Handler[Response] = Handler.pure(Ok("pong"))
}
```

```scala
import com.google.inject.Guice
import monoton.server.{Router, ServerImpl}
// Write other imports

object Main extends App {

  val statusController = new StatusController
  val router = new MyRouter(statusController, userController)

  val server = new ServerImpl(
    port = 8080,
    router = router,
    requestExecutor = ExecutionContext.global
  )

  server.start()
}
```
