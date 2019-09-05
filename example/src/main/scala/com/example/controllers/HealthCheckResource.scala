package com.example.controllers

import java.time.ZonedDateTime
import java.util.UUID

import monoton.http.{Cookie, Response}
import monoton.server.{Handler, Resource}

class HealthCheckResource extends Resource {

  def ping: Handler[Response] = Ok("pong")

  def echo = Handler.getRequest.map(req => Ok(req.body.asText))

  val CookieNameUUID = "MONOTON_COOKIE_TEST_UUID"
  val CookieNameTime = "MONOTON_COOKIE_TEST_TIME"
  def setCookie =
    for {
      cookies <- request.cookies.all
      uuid    = UUID.randomUUID().toString
    } yield
      Ok(cookies.to(List).map(_.toString).sorted.mkString("\n"))
        .addCookie(CookieNameUUID, uuid)
        .addCookie(Cookie(CookieNameTime, ZonedDateTime.now().toString))
}
