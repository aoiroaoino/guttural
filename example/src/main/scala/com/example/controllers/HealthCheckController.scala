package com.example.controllers

import monoton.http.Response
import monoton.server.{Controller, Handler}

class HealthCheckController extends Controller {

  def isOk: Handler[Response] =
    for {
      name <- Handler.getOptionalQuery("name", "NO NAME")
      msg  <- Handler.pure(s"It's OK, $name")
    } yield Response.Ok(msg)

  def echo: RunnableHandler =
    for {
      msg <- Handler.getBodyAsString
    } yield Response.Ok(msg)
}
