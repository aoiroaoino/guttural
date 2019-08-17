package com.example

import com.example.controllers.{HealthCheckController, RiseErrorController, UserController}
import ocicat.http.Method
import ocicat.server.{Route, Router}

class ExampleRouter(
    healthCheckController: HealthCheckController,
    riseErrorController: RiseErrorController,
    userController: UserController
) {
  val impl = Router(
    Route(Method.GET, "/health_check", healthCheckController.isOk),
    Route(Method.POST, "/echo", healthCheckController.echo),
    Route(Method.GET, "/rise_error", riseErrorController.run),
    Route(Method.POST, "/users", userController.create),
    Route(Method.GET, "/users", userController.list)
  )
}
