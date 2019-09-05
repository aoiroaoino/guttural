package com.example.controllers

import java.util.UUID

import com.example.controllers.auth.AuthenticatedUserRequest
import monoton.http.codec.PlayJsValue
import monoton.http.{CirceJson, Form, FormMapping, Response}
import monoton.server.{Handler, Resource}
import scala.util.chaining._

class UserResource extends Resource {
  import UserResource._
  import monoton.http.codec.circe._
  import monoton.http.codec.playjson._

  def create: Handler[Response] =
    for {
      userId <- request.to(AuthenticatedUserRequest).map(_.userId)
      json   <- request.body.as(PlayJsValue)
      _      = println(json)
    } yield Ok(json)

  def update(userId: UUID): Handler[Response] = {
    import io.circe.syntax._
    import io.circe.generic.auto._
    for {
      dto <- request.body.as(form, errors => {
        BadRequest(UpdateResponseJson(success = false, errors.map(_.msg)).asJson)
      })
    } yield Ok(UpdateResponseJson(success = true, dto).asJson)
  }

  def modifyTag(userId: UUID, tagId: Int): Handler[Response] =
    for {
      cookies <- request.cookies.all
      _       = println(cookies.to(Vector).tap(println).mkString("\n"))
    } yield Ok(s"userId: $userId, tagId: $tagId")

  def list: Handler[Response] =
    for {
      _ <- Handler.pure(println("start get users"))
      _ <- request.queryString.getOption[Int]("number").map { i =>
        println(s"query string number: $i")
        i
      }
      _ <- Handler.WIP // ここで打ち切ることができる
      name <- Handler.later[String] {
        println("read user from database")
        throw new Exception("no connection")
      }
    } yield Ok(name)
}

object UserResource {

  final case class UserForm(id: UUID, name: String, age: Int)

  val form: FormMapping[UserForm] =
    Form.mapping("id", "name", "age")(UserForm.apply)

  final case class UpdateResponseJson[A](success: Boolean, content: A)
}
