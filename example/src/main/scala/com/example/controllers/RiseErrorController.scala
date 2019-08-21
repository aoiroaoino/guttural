package com.example.controllers

import monoton.http.Response
import monoton.server.Handler

class RiseErrorController {

  def run: Handler[Response] = Handler.later(throw new Exception("some error occurred"))
}
