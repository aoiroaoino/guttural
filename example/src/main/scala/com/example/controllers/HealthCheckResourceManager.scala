package com.example.controllers

import monoton.server.{Handler, ResourceManager}

class HealthCheckResourceManager extends ResourceManager {

  def ping: ConstHandler = Ok("pong")

  def echo = Handler.getRequest.map(req => Ok(req.body.asText))
}
