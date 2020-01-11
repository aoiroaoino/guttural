package com.example

import guttural.http.server.Controller
import guttural.http.server.RequestHandler

class GetUserList() extends Controller {

  def run: RequestHandler =
    for {
      num <- request.queryString.get[Int]("page")
    } yield {
      Ok(num.toString)
    }
}
