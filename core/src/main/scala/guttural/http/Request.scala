package guttural.http

import java.net.URI

import scala.util.control.NonFatal
import scala.util.chaining._

abstract class Request {
  import Request._

  // === start-line ( request-line )
  def method: Method

  // request-target ( origin-form ) ※ その他三つは対応予定がないので、まずは origin-form 固定に。
  def absolutePath: String
//  def query: String
  def queryString: Map[String, Seq[String]]

//  def httpVersion: String

  // * ( header-field CRLF )
  def headerFields: Map[String, String]

  def cookies: Cookies

  // message-body
  def body: RequestBody
//  def bodyAs[A]

  def requestLine: Line = ???
}

object Request {

  final case class Line(
      method: Method,
      requestURI: URI,
      httpVersion: String
  ) {}

  final case class Header()

  // factory
  def apply(
      method: Method,
      uri: URI,
      query: Map[String, Seq[String]],
      headers: Map[String, String],
      cookies: Cookies,
      body: RequestBody
  ): Request =
    DefaultRequest(method, uri.getPath, query, headers, cookies, body)

  object GET {
    def unapply(req: Request): Option[String] =
      Option.when(req.method == Method.GET)(req.absolutePath).tap(_ => println(req))
  }
  object POST {
    def unapply(req: Request): Option[(String, Map[String, String], RequestBody)] =
      Option.when(req.method == Method.POST)((req.absolutePath, req.headerFields, req.body))
  }
}

final case class DefaultRequest(
    method: Method,
    absolutePath: String,
    queryString: Map[String, Seq[String]],
    headerFields: Map[String, String],
    cookies: Cookies,
    body: RequestBody
) extends Request

trait QueryStringDecoder[A] {
  def decode(s: String): Option[A]
}
object QueryStringDecoder {

  implicit val stringDecoder: QueryStringDecoder[String] = new QueryStringDecoder[String] {
    override def decode(s: String): Option[String] = Option(s)
  }
  implicit val intDecoder: QueryStringDecoder[Int] = new QueryStringDecoder[Int] {
    override def decode(s: String): Option[Int] =
      try Some(s.toInt)
      catch { case NonFatal(_) => None }
  }
}
