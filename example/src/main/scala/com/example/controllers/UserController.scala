package com.example.controllers

import java.util.UUID

import com.example.controllers.auth.AuthenticatedUserRequest
import io.circe.Printer
import monoton.http.{CirceJson, Form, FormMapping, Response}
import monoton.server.{Controller, Handler}

class UserController extends Controller {

  def create: Handler[Response] =
    for {
      userId <- request.to(AuthenticatedUserRequest).map(_.userId)
      json   <- request.body.as(CirceJson)
      _      = println(json.pretty(Printer.spaces2))
    } yield Ok(userId)

  final case class UserForm(id: UUID, name: String, age: Int)

  val form: FormMapping[UserForm] =
    Form.mapping("id", "name", "age")(UserForm.apply)

  def update: Handler[Response] =
    for {
      dto <- request.body.bindToForm(form) { errors =>
        println("form mapping errors: " + errors)
        BadRequest("form mapping error")
      }
      _ = println(dto)
    } yield Ok("ok")

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
