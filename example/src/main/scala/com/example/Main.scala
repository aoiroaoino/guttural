package com.example

import guttural.http.server.Router

object Main {

  val app    = new ExampleModule
  val server = app.server

  val router = Router.ofDynamic {
    case
  }

  def main(args: Array[String]): Unit = {
    server.start()
  }
}
