package com.example.controllers

import monoton.http.{Request, Response}
import monoton.server.Controller

class HealthCheckController extends Controller {

  def ping: ConstHandler = Ok("pong")

  def echo: Request => Response = { req =>
    Ok(req.body.asText)
  }
}
