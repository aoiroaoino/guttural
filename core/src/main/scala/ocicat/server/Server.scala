package monoton.server

trait Server {

  def start(): Unit

  def stop(): Unit
}
