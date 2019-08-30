package monoton.server.netty

import monoton.http.{Request, Response, ResponseBuilders}
import monoton.server.Route

import scala.concurrent.{ExecutionContext, Future}

class HandleRequest(request: Request, route: Route) {

  def execute()(implicit ec: ExecutionContext): Future[Response] =
    route.handler().run(request).recover {
      case e: Throwable =>
        e.printStackTrace() // TODO: use logger
        ResponseBuilders.InternalServerError() // Handler の予期せぬ例外は全て InternalServerError に。
    }
}
