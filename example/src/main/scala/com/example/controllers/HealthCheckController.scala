package com.example.controllers

import monoton.http.{Request, Response}
import monoton.server.{Controller, Handler}

class HealthCheckController extends Controller {

  def isOk: Handler[Response] =
    for {
      name <- Handler.getOptionalQuery("name", "NO NAME")
      msg  <- Handler.pure(s"It's OK, $name")
    } yield Ok(msg)

  def ping = Ok("pong")

  def echo: Request => Response = { req =>
    Ok(req.bodyAsString)
  }
}
