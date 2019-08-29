package monoton.util

import scala.concurrent.{ExecutionContext, Future}

trait Flow[S, T, A, B] { self =>

  def to(s: S): Either[T, A]

  def from(b: B, s: S): T

  final def run(s: S)(f: A => B): T =
    to(s).map(a => from(f(a), s)).merge

  final def runF(s: S)(f: A => Future[B])(implicit ec: ExecutionContext): Future[T] =
    to(s).fold(Future.successful, f(_).map(from(_, s)))

  final def andThen[C, D](other: Flow[A, B, C, D]): Flow[S, T, C, D] =
    new Flow[S, T, C, D] {
      override def to(s: S): Either[T, C] = self.to(s).flatMap(other.to(_).left.map(self.from(_, s)))
      override def from(b: D, s: S): T    = self.run(s)(other.from(b, _))
    }

  final def |>[C, D](other: Flow[A, B, C, D]): Flow[S, T, C, D] = andThen(other)
}
