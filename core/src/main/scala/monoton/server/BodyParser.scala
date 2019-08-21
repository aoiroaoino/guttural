package monoton.server

abstract class BodyParser {
  def parse[A](rawBody: Array[Byte]): Either[Throwable, A]
}
