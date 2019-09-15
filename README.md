# Monoton

Web Framework for Scala inspired by Play Framework.

Status: PoC

## Setup

### framework

```sbtshell
$ sbt root/publishLocal
$ sbt plugin/publishLocal
```

`project/plugins.sbt`

```scala
addSbtPlugin("dev.aoiroaoino" % "monoton-plugin" % "0.1.0-SNAPSHOT")
```

`build.sbt`

```scala
lazy val root = (project in file("."))
  .enablePlugins(MonotonPlugin)
```

### docs

```sbtshell
$ sbt docs/makeMicrosite
```

```bash
$ cd docs/target/site
$ jekyll serve
```

## Usage

### Minimal App

```bash
$ sbt run
```

```bash
$ curl -i http://localhost:8080
HTTP/1.1 200 OK
content-type: text/html
content-length: 24
connection: close

<h1>Hello, Monoton!</h1>
```

### Ping/Pong App

`[root]/example/controllers/StatusController.scala`:

```scala
package example.controllers

import monoton.http.Response
import monoton.server.{Controller, Handler}

class StatusController extends Controller {
  def ping: Handler[Response] = Handler.pure(Ok("pong"))
}
```

`[root]/example/MyRouter.scala`:

```scala
package example

import javax.inject.{Inject, Singleton}
import monoton.server.RoutingDSL
import example.controllers.StatusController

@Singleton
class MyRouter @Inject()(status: StatusController) extends RoutingDSL {

  GET ~ "/ping" to status.ping
}
```

`[root]/AppModule.scala`:

```scala
import com.google.inject.AbstractModule
import monoton.server.Router

class AppModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Router]).to(classOf[example.MyRouter])
  }
}
```

```bash
$ sbt run
```

```bash
$ curl -i http://localhost:8080/ping
HTTP/1.1 200 OK
content-type: text/plain
content-length: 4
connection: close

pong
```

### Other Example

see: https://github.com/aoiroaoino/monoton-example
