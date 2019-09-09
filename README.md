# Monoton

Web Framework for Scala.

## What's Monoton?

- Simple 
- Functional
- 

## Quick Start

### build settings

```sbt
addSbtPlugin("dev.aoiroaoino" % "monoton-plugin" % "0.1.0-SNAPSHOT")
```
and
```sbt
lazy val httpServer = project
  .settings( /* your settings */ )
  .enablePlugins(MonotonPlugin)
```

### Define Resources

```scala
import monoton.http.Response
import monoton.server.{Handler, Resource}
// Write other imports

class StatusResource extends Resource {

  def ping: Handler[Response] = Handler.pure(Ok("pong"))
}
```

### Define Router

```scala
import javax.inject.Inject
import monoton.server.RoutingDSL
import monoton.util.Read
// Write other imports

class MyRouter @Inject()(statusResource: StatusResource) extends RoutingDSL {

  GET ~ "/ping" to statusResource.ping
}
```

### Define AppModule

```scala
import scala.concurrent.ExecutionContext
import com.google.inject.AbstractModule
import monoton.server.Router
// Write other imports

class AppModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Int]).toInstance(8080) // port
    bind(classOf[ExecutionContext]).toInstance(ExecutionContext.global)
    bind(classOf[Router]).to(classOf[MyRouter])
  }
}
```
