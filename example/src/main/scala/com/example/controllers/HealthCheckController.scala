package com.example.controllers

import java.time.ZonedDateTime
import java.util.UUID

import monoton.http.server.HandlerBuilder
import monoton.http.{Cookie, Response}
import monoton.server.Controller

class HealthCheckController extends Controller {

  def ping: HandlerBuilder[Response] = Ok("pong")

  def echo = HandlerBuilder.getRequest.map(req => Ok(req.body.asText))

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
