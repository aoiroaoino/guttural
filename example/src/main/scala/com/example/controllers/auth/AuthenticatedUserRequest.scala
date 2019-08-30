package com.example.controllers.auth

import monoton.http.{Request, Response, ResponseBuilders}
import monoton.server.RequestFactory

final case class AuthenticatedUserRequest(request: Request, userId: String)

object AuthenticatedUserRequest extends RequestFactory[AuthenticatedUserRequest] {

  override def from(request: Request): Option[AuthenticatedUserRequest] =
    request.headerFields.get("USER_ID").map(AuthenticatedUserRequest(request, _))

  override def onFailure(request: Request): Response =
    ResponseBuilders.Unauthorized("Invalid User ID")
}
