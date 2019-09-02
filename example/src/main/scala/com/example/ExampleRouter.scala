package com.example

import com.example.controllers.{HealthCheckResource, UserResource}
import monoton.server.{Handler, RoutingDSL}

class ExampleRouter(
    healthCheckResource: HealthCheckResource,
    userResource: UserResource
) extends RoutingDSL {

  // health check
  POST ~ "/echo" to healthCheckResource.echo
  GET  ~ "/ping" to healthCheckResource.ping

  // users
  resource(userResource) { user =>
    GET  ~ "/users"                       to user.list
    POST ~ "/users"                       to user.create
    PUT  ~ "/users/{userId}"              to user.update _
    PUT  ~ "/users/{userId}/tags/{tagId}" to user.modifyTag _
  }

  // other
  GET  ~ "/long/long/cat/path"   to Handler.TODO
  POST ~ "/foo/bar/baz/qux/path" to Handler.WIP
}
