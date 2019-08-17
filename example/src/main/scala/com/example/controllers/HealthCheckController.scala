package com.example.controllers

import ocicat.http.Status
import ocicat.server.{Controller, Handler, Response}

class HealthCheckController extends Controller {

  def isOk: Handler[Response] = Handler.pure(Response(Status.Ok, "It's OK"))
}
