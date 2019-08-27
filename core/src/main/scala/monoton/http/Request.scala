package monoton.http

import java.net.URI
import java.nio.charset.{Charset, StandardCharsets}

import monoton.server.BodyParser

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

    final case class OriginForm(absolutePath: String, query: Option[String]) extends RequestTarget
    object OriginForm {
      def apply(uri: URI): OriginForm = OriginForm(uri.getPath, Option(uri.getQuery))
    }

    final case class AbsoluteForm(absoluteURI: URI) extends RequestTarget

    final case class AuthorityForm(authority: String) extends RequestTarget

    case object AsteriskForm extends RequestTarget
  }
}

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
