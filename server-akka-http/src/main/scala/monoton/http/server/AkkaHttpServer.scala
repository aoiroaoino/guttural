package monoton.http.server

import java.util.concurrent.atomic.AtomicReference

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import akka.stream.scaladsl.Sink

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.FiniteDuration

final class AkkaHttpServer(
    requestHandlingPipeline: RequestHandlingPipeline,
    httpMessageConvertingPipeline: HttpMessageConvertingPipeline
)(implicit system: ActorSystem, mat: Materializer)
    extends Server {

  private implicit val defaultExecutor: ExecutionContext = system.dispatcher

  private[this] val pipeline =
    httpMessageConvertingPipeline
      .andThen(internal.HEADRequestDownstreamFilter)
      .andThen(requestHandlingPipeline)

  private[this] val sink = Sink.foreach[Http.IncomingConnection](
    _.handleWithAsyncHandler(pipeline.runAsync(_) { case (req, h) => h.run(req) })
  )

  private[this] val server: AtomicReference[Option[Http.ServerBinding]] = new AtomicReference(None)

  override def start(host: String, port: Int): Unit = {
    println("Starting...")
    Http().bind(host, port).to(sink).run().foreach(s => server.set(Some(s)))
  }

  override def stop(timeout: FiniteDuration): Unit = {
    println("Stopping...")
    server.get() match {
      case Some(s) =>
        Await.ready(s.terminate(timeout), timeout)
      case None =>
        println("Server is not running or has already terminated.")
    }
  }
}
