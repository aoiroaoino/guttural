package com.example

object Main {

  val app    = new ExampleModule
  val server = app.server

  def main(args: Array[String]): Unit = {
    server.start()
  }
}
