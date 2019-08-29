package com.example

import com.example.controllers.{HealthCheckController, UserController}
import monoton.server.RoutingDSL

class ExampleRouter(
    healthCheckController: HealthCheckController,
    userController: UserController
) extends RoutingDSL {

  // health check
  POST >> "/echo"     to healthCheckController.echo
  GET  >> "/ping"     to healthCheckController.ping

  // users
  GET  >> "/users"    to userController.list
  POST >> "/users"    to userController.create
  POST >> "/users/id" to userController.update // TODO: PUT と request からの値取得
}
