package com.example.controllers

import monoton.http.Response
import monoton.server.{Handler, Resource}

class HealthCheckResource extends Resource {

  def ping: Handler[Response] = Handler.pure(Ok("pong"))

  def echo = Handler.getRequest.map(req => Ok(req.body.asText))
}
