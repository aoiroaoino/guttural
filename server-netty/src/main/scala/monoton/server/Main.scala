package monoton.server

import com.google.inject.{AbstractModule, Guice}

object Main {

  val appModule = try {
    Class.forName("AppModule").newInstance().asInstanceOf[AbstractModule]
  } catch {
    case e: Throwable =>
      println(e.getMessage)
      Class.forName("monoton.server.DefaultModule").newInstance().asInstanceOf[AbstractModule]
  }

  val injector = Guice.createInjector(new NettyModule, appModule)
  val server   = injector.getInstance(classOf[Server])

  def main(args: Array[String]): Unit = {
    server.start()
  }
}
