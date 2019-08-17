package ocicat.server

trait Server {

  def port: Int

  def router: Router

  def start(): Unit

  def stop(): Unit
}
