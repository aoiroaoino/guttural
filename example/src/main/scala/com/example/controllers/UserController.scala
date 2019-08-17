package com.example.controllers

import ocicat.server.{Controller, Handler, Response}

class UserController extends Controller {

  def create: Handler[Response] = Handler.TODO

  def list: Handler[Response] =
    for {
      _ <- Handler.pure(println("start get users"))
      _ <- Handler.interrupt(Response.Ok("=== ここまで実行 ===")) // 後続処理が評価されていなければ、ここで打ち切ることができる
      name <- Handler.later[String] {
        println("read user from database")
        throw new Exception("no connection")
      }
    } yield Response.Ok(name)
}
