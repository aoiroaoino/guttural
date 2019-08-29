package com.example

import com.example.controllers.{HealthCheckController, UserController}
import monoton.http.Method
import monoton.server.{Route, Router}

class ExampleRouter(
    healthCheckController: HealthCheckController,
    userController: UserController
) {
  val impl = Router(
    Route(Method.POST, "/echo", healthCheckController.echo),
    Route(Method.GET, "/ping", healthCheckController.ping),
    Route(Method.POST, "/users", userController.create),
    Route(Method.POST, "/users/id", userController.update), // TODO: PUT と path からの値取得
    Route(Method.GET, "/users", userController.list)
  )
}
