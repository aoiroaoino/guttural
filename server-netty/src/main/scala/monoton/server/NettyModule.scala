package monoton.server

import com.google.inject.AbstractModule
import com.google.inject.multibindings.OptionalBinder

import scala.concurrent.ExecutionContext

class NettyModule extends AbstractModule {

  override def configure(): Unit = {
    OptionalBinder.newOptionalBinder(binder, classOf[Int]).setDefault.toInstance(8080)
    OptionalBinder.newOptionalBinder(binder, classOf[ExecutionContext]).setDefault.toInstance(ExecutionContext.global)
    bind(classOf[Server]).to(classOf[ServerImpl])
  }
}
