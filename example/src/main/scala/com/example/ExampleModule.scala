package com.example

import java.util.concurrent.Executors

import com.example.controllers.{HealthCheckResourceManager, UserResourceManager}
import monoton.server.{Server, ServerImpl}

import scala.concurrent.ExecutionContext

class ExampleModule {

  private val healthCheckController = new HealthCheckResourceManager
  private val userController        = new UserResourceManager

  private val router = new ExampleRouter(healthCheckController, userController)

  private val serverPort = 8080

  private val requestExecutor: ExecutionContext = {
    val es = Executors.newFixedThreadPool(32)
    ExecutionContext.fromExecutor(es)
  }

  val server: Server = new ServerImpl(serverPort, router, requestExecutor)
}
