package monoton.server

import java.nio.charset.StandardCharsets

import com.google.inject.AbstractModule
import javax.inject._
import monoton.http.{ContentType, Response, Status}

@Singleton
class DefaultRouter @Inject() extends RoutingDSL {
  GET ~ "/" to Handler.pure {
    Response(Status.Ok, ContentType.`text/html`, "<h1>Hello, Monoton!</h1>".getBytes(StandardCharsets.UTF_8))
  }
}

class DefaultModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Router]).to(classOf[DefaultRouter])
  }
}
