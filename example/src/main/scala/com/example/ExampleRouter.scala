package com.example

import com.example.controllers.{HealthCheckResourceManager, UserResourceManager}
import monoton.server.{Handler, RoutingDSL}

class ExampleRouter(
    healthCheckController: HealthCheckResourceManager,
    userResourceManager: UserResourceManager
) extends RoutingDSL {

  // health check
  POST ~ "/echo" to healthCheckController.echo
  GET  ~ "/ping" to healthCheckController.ping

  // users
  resource(userResourceManager) { user =>
    GET  ~ "/users"                       to user.list
    POST ~ "/users"                       to user.create
    PUT  ~ "/users/{userId}"              to user.update _
    PUT  ~ "/users/{userId}/tags/{tagId}" to user.modifyTag _
  }

  // other
  GET  ~ "/long/long/cat/path"   to Handler.TODO
  POST ~ "/foo/bar/baz/qux/path" to Handler.WIP
}
