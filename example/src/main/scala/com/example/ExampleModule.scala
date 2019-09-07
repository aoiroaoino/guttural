package com.example

import java.util.concurrent.Executors

import com.example.controllers.{HealthCheckResource, UserResource}
import monoton.server.{Server, ServerImpl, ServerImplByAkkaHttp}

import scala.concurrent.ExecutionContext

class ExampleModule {

  private val healthCheckController = new HealthCheckResource
  private val userController        = new UserResource

  private val router = new ExampleRouter(healthCheckController, userController)

  private val serverPort = 8080

  private val requestExecutor: ExecutionContext = {
    val es = Executors.newFixedThreadPool(32)
    ExecutionContext.fromExecutor(es)
  }

  val server: Server =
//    new ServerImpl(serverPort, router, requestExecutor)
    new ServerImplByAkkaHttp(serverPort, router)
}
