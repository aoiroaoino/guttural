package com.example.controllers

import monoton.server.{Controller, Handler}

class HealthCheckController extends Controller {

  def ping: ConstHandler = Ok("pong")

  def echo = Handler.getRequest.map(req => Ok(req.body.asText))
}
