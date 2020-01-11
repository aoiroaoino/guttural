package guttural.client

import guttural.http.{ContentType, Method, RequestBody, Response, Status}
import scalaj.http.Http

import scala.concurrent.{ExecutionContext, Future}

class HttpClientImplByScalajHttp(implicit ec: ExecutionContext) extends HttpClient[Future] {

  override def get(url: String, queryStrings: Map[String, String]): Future[Response] = Future {
    val res = Http(url).params(queryStrings).asBytes
    (for {
      status      <- Status.fromStatusLine(res.statusLine)
      contentType <- res.contentType.flatMap(ContentType.fromString)
    } yield Response(status, contentType, res.body)).get // oops
  }

  override def post(url: String, body: RequestBody): Future[Response] = Future {
    val res = Http(url).postData(body.asBytes).asBytes
    (for {
      status      <- Status.fromStatusLine(res.statusLine)
      contentType <- res.contentType.flatMap(ContentType.fromString)
    } yield Response(status, contentType, res.body)).get // oops
  }
}
