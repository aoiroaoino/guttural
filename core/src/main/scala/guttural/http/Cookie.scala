package guttural.http

import scala.collection.Factory

final case class Cookie(
    name: String,
    value: String
)

final class Cookies(underlying: Set[Cookie]) {
  def to[C](factory: Factory[Cookie, C]): C = underlying.to(factory)

  def get(name: String): Option[Cookie] = underlying.find(_.name == name)

  def add(cookie: Cookie): Cookies = new Cookies(underlying + cookie)

  def nonEmpty: Boolean = underlying.nonEmpty
}

object Cookies {
  val empty: Cookies = new Cookies(Set.empty)
}
