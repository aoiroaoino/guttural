package monoton.http

import java.net.URI

import monoton.http.Request.RequestTarget

import scala.util.control.NonFatal

abstract class Request {
  import Request._

  // start-line ( request-line )
  def method: Method
  def requestTarget: RequestTarget
//  def httpVersion: String

  // * ( header-field CRLF )
//  def headers: Seq[HeaderField]
  def headers: Map[String, String]

  // message-body
  def body: RequestBody
}

object Request {

  sealed abstract class RequestTarget extends Product with Serializable

  object RequestTarget {

    final case class OriginForm(absolutePath: String, query: Map[String, Seq[String]]) extends RequestTarget

    final case class AbsoluteForm(absoluteURI: URI) extends RequestTarget

    final case class AuthorityForm(authority: String) extends RequestTarget

    case object AsteriskForm extends RequestTarget
  }

  // factory
  def apply(
      method: Method,
      uri: URI,
      query: Map[String, Seq[String]],
      headers: Map[String, String],
      body: RequestBody
  ): Request =
    DefaultRequest(
      method,
      RequestTarget.OriginForm(uri.getPath, query), // TODO: support other target types
      headers,
      body
    )

}

final case class DefaultRequest(
    method: Method,
    requestTarget: RequestTarget,
    headers: Map[String, String],
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
