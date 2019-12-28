package monoton.http
package server

import scala.concurrent.Future

abstract class Handler[A] {
  def run(a: A): Future[Response]
}

abstract class RequestHandler extends Handler[Request]
