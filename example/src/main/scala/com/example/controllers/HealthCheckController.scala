package com.example.controllers

import ocicat.server.{Controller, Handler, Response}

class HealthCheckController extends Controller {

  def isOk: Handler[Response] =
    for {
      name <- Handler.getOptionalQuery("name", "NO NAME")
      msg  <- Handler.pure(s"It's OK, $name")
    } yield Response.Ok(msg)
}
