package guttural.io

sealed abstract class Anticipate[A] { self =>

  def run(): IO[A]

  def map[B](f: A => B): Anticipate[B] =
}

object Anticipate {

  final case class Pure[A](a: A) extends Anticipate[A] {
    override def unsafeRun(): A = a
  }
  final case class Lazy[A](a: () => A) extends Anticipate[A] {
    override def unsafeRun(): A = a()
  }

  def value[A](value: A): Anticipate[A] = Pure(value)

  def lazyValue[A](value: => A): Anticipate[A] = Lazy(() => value)
}
