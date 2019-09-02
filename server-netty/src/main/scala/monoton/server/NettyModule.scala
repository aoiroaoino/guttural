package monoton.server

import com.google.inject.AbstractModule

class NettyModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Server]).to(classOf[ServerImpl])
  }
}
