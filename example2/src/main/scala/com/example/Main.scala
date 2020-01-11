package com.example

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import wvlet.airframe._
import guttural.http.server._
import guttural.http.{Request, Response}

import scala.concurrent.Future

object Main {

  val healthCheck = new HealthCheck

  val router = Router.ofDynamic {
    case req @ Request.GET("/health_check") => healthCheck.run.run(req)
    case Request.GET("/ping")               => Future.successful(Response.Ok("pong"))
    case req                                => Future.successful(Response.InternalServerError(req.toString))
  }

  val router2 = Router.builder
    .andThen { case req @ Request.GET("/health_check") => healthCheck.run.run(req) }
    .andThen { case Request.GET("/ping") => Future.successful(Response.Ok("pong")) }
    .build

  "aa".r

  object module {
    val design = newDesign
      .bind[Server].to[ServerImpl]
      .bind[Router].toInstance(router)
      .bind[RequestHandlingPipeline].toProvider { r: Router =>
        new RequestHandlingPipeline(r)
      }
      .bind[HttpMessageConvertingPipeline].to[DefaultAkkaHttpMessageConvertingPipeline]
      .bind[ActorSystem].toInstance(ActorSystem())
      .bind[Materializer].toProvider { system: ActorSystem =>
        ActorMaterializer()(system)
      }
  }

//  implicit val system: ActorSystem = ActorSystem()
//  implicit val mat: Materializer   = ActorMaterializer()

  def main(args: Array[String]): Unit = {

//    val server = new ServerImpl(
//      new RequestHandlingPipeline(router),
//      new DefaultAkkaHttpMessageConvertingPipeline()
//    )
//    Runtime
//      .getRuntime()
//      .addShutdownHook(new Thread {
//        override def run(): Unit = {
//          desi.stop()
//        }
//      })
    module.design.run { s: Server =>
      s.start()
    }
  }
}
