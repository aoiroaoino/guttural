package monoton.http

import scala.collection.Factory

final case class Cookie(
    name: String,
    value: String
)

final class Cookies(underlying: Seq[Cookie]) {
  def to[C](factory: Factory[Cookie, C]): C = underlying.to(factory)
  def get(name: String): Option[Cookie]     = underlying.find(_.name == name)
}

object Cookies {
  val empty: Cookies = new Cookies(Seq.empty)
}
