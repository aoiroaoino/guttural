package com.example

import java.util.UUID

import com.example.controllers.{HealthCheckResource, UserResource}
import com.example.model.UserId
import monoton.server.{Handler, RoutingDSL}
import monoton.util.Read

class ExampleRouter(
    healthCheckResource: HealthCheckResource,
    userResource: UserResource
) extends RoutingDSL {

  implicit def userIdRead(implicit M: Read[UUID]): Read[UserId] = M.map(UserId.apply)

  // health check
  POST ~ "/echo"   to healthCheckResource.echo
  GET  ~ "/ping"   to healthCheckResource.ping
  GET  ~ "/cookie" to healthCheckResource.setCookie

  // users
  resource(userResource) { user =>
    GET    ~ "/users"                       to user.list
    POST   ~ "/users"                       to user.create
    PUT    ~ "/users/{userId}"              to user.update _
    DELETE ~ "/users/{userId}"              to user.delete _
    PUT    ~ "/users/{userId}/tags/{tagId}" to user.modifyTag
  }

  // other
  GET  ~ "/long/long/cat/path"   to Handler.TODO
  POST ~ "/foo/bar/baz/qux/path" to Handler.WIP
}
