package ocicat.server

import scala.util.control.NonFatal

abstract class Request {
  protected def queryString: Map[String, String]

  object query {
    def get[A](key: String)(implicit decoder: QueryStringDecoder[A]): Option[A] =
      queryString.get(key).flatMap(decoder.decode)
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

final case class DefaultRequest(queryString: Map[String, String]) extends Request
