package ocicat.server

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.language.higherKinds
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

final case class Handler[A](asFunction: (A => Future[Response]) => Future[Response]) {
  private def runFunc(f: A => Future[Response]): Future[Response] =
    try asFunction(f)
    catch { case NonFatal(e) => Future.failed(e) }

  def run(implicit ev: A =:= Response): Future[Response] = runFunc(a => Future.successful(ev(a)))

  def runWith(f: A => Future[Response]): Future[Response] = runFunc(f)

  def map[B](f: A => B): Handler[B] = Handler(bfr => runFunc(f andThen bfr))

  def flatMap[B](f: A => Handler[B]): Handler[B] = Handler(bfr => runFunc(a => f(a).asFunction(bfr)))
}

object Handler {

  def pure[A](a: A): Handler[A]     = Handler(_(a))
  def later[A](a: => A): Handler[A] = Handler(_(a))

  def unit: Handler[Unit] = pure(())

  def catchNonFatal[A](a: => A)(ifError: Throwable => Response): Handler[A] = successValue(Try(a))(ifError)

  def someValue[A](fa: Option[A])(ifNone: => Response): Handler[A] =
    Handler(fa.fold(Future.successful(ifNone)))

  def noneValue[A](fa: Option[A])(ifSome: A => Response): Handler[Unit] =
    Handler(afr => fa.fold(afr(()))(a => Future.successful(ifSome(a))))

  def rightValue[L, R](fa: Either[L, R])(ifLeft: L => Response): Handler[R] =
    Handler(fa.fold(l => Future.successful(ifLeft(l)), _))

  def leftValue[L, R](fa: Either[L, R])(ifRight: R => Response): Handler[L] =
    Handler(fa.fold(_, r => Future.successful(ifRight(r))))

  def successValue[A](fa: Try[A])(ifFailure: Throwable => Response): Handler[A] =
    Handler(fa.fold(e => Future.successful(ifFailure(e)), _))

  def failureValue[A](fa: Try[A])(ifSuccess: A => Response): Handler[Throwable] =
    Handler(fa.fold(_, a => Future.successful(ifSuccess(a))))

  def successValue[A](fa: => Future[A])(ifFailure: Throwable => Response)(implicit ec: ExecutionContext): Handler[A] =
    Handler { afr =>
      val p = Promise[Response]()
      fa.onComplete {
        case Success(a) => afr(a).onComplete(p.complete)
        case Failure(e) => Future.successful(ifFailure(e))
      }
      p.future
    }

  def failureValue[A](fa: => Future[A])(ifSuccess: A => Response)(implicit ec: ExecutionContext): Handler[Throwable] =
    Handler { afr =>
      val p = Promise[Response]()
      fa.onComplete {
        case Success(a) => Future.successful(ifSuccess(a))
        case Failure(e) => afr(e).onComplete(p.complete)
      }
      p.future
    }
}
