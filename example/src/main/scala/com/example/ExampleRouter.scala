package com.example

import java.util.UUID

import com.example.controllers.{HealthCheckController, UserController}
import monoton.server.RoutingDSL

class ExampleRouter(
    healthCheckController: HealthCheckController,
    userController: UserController
) extends RoutingDSL {

  // health check
  POST ~ "/echo" to healthCheckController.echo
  GET  ~ "/ping" to healthCheckController.ping

  // users
  GET  ~ "/users"                       to userController.list
  POST ~ "/users"                       to userController.create
  PUT  ~ "/users/{userId}"              to userController.update _
  PUT  ~ "/users/{userId}/tags/{tagId}" to userController.modifyTag _

  // other
  GET  ~ "/long/long/cat/path"   to TODO
  POST ~ "/foo/bar/baz/qux/path" to WIP
}
