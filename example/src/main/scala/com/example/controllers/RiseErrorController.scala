package com.example.controllers

import ocicat.http.Response
import ocicat.server.Handler

class RiseErrorController {

  def run: Handler[Response] = Handler.later(throw new Exception("some error occurred"))
}
