package com.example

import com.example.controllers.{HealthCheckController, RiseErrorController}
import ocicat.http.Method
import ocicat.server.{Route, Router}

class ExampleRouter(
    healthCheckController: HealthCheckController,
    riseErrorController: RiseErrorController
) {
  val impl = Router(
    Route(Method.Get, "/health_check", healthCheckController.isOk),
    Route(Method.Get, "/rise_error", riseErrorController.run)
  )
}
