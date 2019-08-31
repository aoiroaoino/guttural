package monoton.server

import monoton.http.{Request, Response, ResponseBuilders}

import scala.concurrent.{ExecutionContext, Future}

class HandleRequest(request: Request, handler: Handler[Response]) {

  def execute()(implicit ec: ExecutionContext): Future[Response] =
    handler.run(request).recover {
      case e: Throwable =>
        e.printStackTrace() // TODO: use logger
        ResponseBuilders.InternalServerError() // Handler の予期せぬ例外は全て InternalServerError に。
    }
}
