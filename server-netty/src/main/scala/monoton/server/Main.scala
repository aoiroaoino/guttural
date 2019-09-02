package monoton.server

import com.google.inject.{AbstractModule, Guice}

object Main {

  val appModule = Class.forName("AppModule").newInstance().asInstanceOf[AbstractModule]

  val injector = Guice.createInjector(new NettyModule, appModule)
  val server   = injector.getInstance(classOf[Server])

  def main(args: Array[String]): Unit = {
    server.start()
  }
}
