package monoton.server

import scala.concurrent.{ExecutionContext, Future}

trait Driver[S, T, A, B] {

  def to(s: S): Either[T, A]

  def from(b: B, s: S): T

  final def run(s: S)(f: A => Future[B])(implicit ec: ExecutionContext): Future[T] =
    to(s).fold(Future.successful, f(_).map(from(_, s)))
}
