package monoton.http.server

import scala.concurrent.Future

abstract class Pipeline[S, T, A, B] { self =>

  def upstream(s: S): Either[T, A]

  def downstream(b: B, s: S): T

  final def run(s: S)(f: A => B): T = upstream(s).map(a => downstream(f(a), s)).merge

  final def runAsync(s: S)(f: A => Future[B]): Future[T] = ???

  final def andThen[C, D](other: Pipeline[A, B, C, D]): Pipeline[S, T, C, D] =
    new Pipeline[S, T, C, D] {
      override def upstream(s: S): Either[T, C] =
        self.upstream(s).flatMap(other.upstream(_).left.map(self.downstream(_, s)))
      override def downstream(b: D, s: S): T =
        self.run(s)(other.downstream(b, _))
    }

  final def |>[C, D](other: Pipeline[A, B, C, D]): Pipeline[S, T, C, D] = andThen(other)
}

object Pipeline {

  def apply[S, T, A, B](from: S => Either[T, A])(to: B => S => T): Pipeline[S, T, A, B] =
    new Pipeline[S, T, A, B] {
      override def upstream(s: S): Either[T, A] = from(s)
      override def downstream(b: B, s: S): T    = to(b)(s)
    }

  type Filter[S, T] = Pipeline[S, T, S, T]

  object Filter {
    def apply[S, T](from: S => Either[T, S])(to: T => S => T): Filter[S, T] = Pipeline(from)(to)

    def nop[S, T]: Filter[S, T] =
      new Filter[S, T] {
        override def upstream(s: S): Either[T, S] = Right(s)
        override def downstream(b: T, s: S): T    = b
      }
  }

  abstract class UpstreamFilter[S, T] extends Filter[S, T] {
    override final def downstream(b: T, s: S): T = b
  }

  abstract class DownstreamFilter[S, T] extends Filter[S, T] {
    override final def upstream(s: S): Either[T, S] = Right(s)
  }
}
