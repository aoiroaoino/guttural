package com.example.controllers

import com.example.model.{User, UserDataAccessor, UserId}
import monoton.http.codec.CirceJson
import monoton.http.Form
import monoton.server.{Handler, Controller}

import scala.util.chaining._

class UserController extends Controller {
  import monoton.http.codec.circe._
  import UserController._

  def list: RequestHandler =
    for {
      users <- Handler.pure(UserDataAccessor.findAll())
      res   = users.mkString("\n")
    } yield Ok(res)

  def create: RequestHandler =
    for {
      form <- request.body.as(createUserForm, errors => BadRequest(errors.mkString("\n")))
      user = User.create(form.name, form.age)
      _    <- UserDataAccessor.upsert(user).valueOr(_ => InternalServerError("unexpected error"))
    } yield Ok(user.id.value.toString)

  def delete(userId: UserId): RequestHandler =
    UserDataAccessor
      .delete(userId)
      .valueOrNotFound(_ => s"$userId is not found")
      .map(_ => Ok(s"delete $userId"))

  def update(userId: UserId): RequestHandler =
    request.body.as(CirceJson).map(Ok(_))

  def modifyTag =
    (userId: UserId, tagId: Int) =>
      for {
        cookies <- request.cookies.all
        _       = println(cookies.to(Vector).tap(println).mkString("\n"))
      } yield Ok(s"userId: $userId, tagId: $tagId")
}

object UserController {

  final case class CreateUserForm(name: String, age: Int)
  val createUserForm = Form.mapping("name", "age")(CreateUserForm.apply)
}
