package com.example.controllers

import com.example.controllers.auth.AuthenticatedUserRequest
import io.circe.Printer
import monoton.http.{CirceJson, Response}
import monoton.server.{Controller, Handler}

class UserController extends Controller {

  def create: Handler[Response] =
    for {
      userId <- request.to(AuthenticatedUserRequest).map(_.userId)
      json   <- request.body.as(CirceJson)
      _      = println(json.pretty(Printer.spaces2))
    } yield Ok(userId)

  def list: Handler[Response] =
    for {
      _ <- Handler.pure(println("start get users"))
      _ <- Handler.WIP // ここで打ち切ることができる
      name <- Handler.later[String] {
        println("read user from database")
        throw new Exception("no connection")
      }
    } yield Ok(name)
}
