package com.example.controllers

import ocicat.server.{Handler, Response}

class RiseErrorController {

  def run: Handler[Response] = Handler.later(throw new Exception("some error occurred"))
}
