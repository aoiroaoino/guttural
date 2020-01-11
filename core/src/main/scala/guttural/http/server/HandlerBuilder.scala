package guttural.http.server

import guttural.http.{Request, Response}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.language.higherKinds
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

final case class HandlerBuilder[A](asFunction: Request => (A => Future[Response]) => Future[Response]) {
  private def runFunc(f: A => Future[Response])(req: Request): Future[Response] =
    try asFunction(req)(f)
    catch { case NonFatal(e) => Future.failed(e) }

  def build(implicit ev: A =:= Response): RequestHandler = runFunc(a => Future.successful(ev(a)))

  def build(f: A => Future[Response]): RequestHandler = runFunc(f)

  def map[B](f: A => B): HandlerBuilder[B] = HandlerBuilder(req => bfr => runFunc(f andThen bfr)(req))

  def flatMap[B](f: A => HandlerBuilder[B]): HandlerBuilder[B] =
    HandlerBuilder(req => bfr => runFunc(a => f(a).asFunction(req)(bfr))(req))
}

object HandlerBuilder {

  // ignore request
  private def ignoreRequest[A](f: (A => Future[Response]) => Future[Response]): HandlerBuilder[A] =
    HandlerBuilder(_ => f(_))

  def pure[A](a: A): HandlerBuilder[A]     = HandlerBuilder(_ => _(a))
  def later[A](a: => A): HandlerBuilder[A] = HandlerBuilder(_ => _(a))

  def interrupt(e: Response): HandlerBuilder[Response] = HandlerBuilder(_ => _ => Future.successful(e))

  def TODO: HandlerBuilder[Response] = interrupt(Response.NotImplemented("TODO"))
  def WIP: HandlerBuilder[Response]  = interrupt(Response.NotImplemented("WIP"))

  def unit: HandlerBuilder[Unit] = pure(())

  def catchNonFatal[A](a: => A)(ifError: Throwable => Response): HandlerBuilder[A] = successValue(Try(a))(ifError)

  def fromFunction(func: Request => Response): HandlerBuilder[Response] =
    HandlerBuilder(req => _(func(req)))

  def someValue[A](fa: Option[A])(ifNone: => Response): HandlerBuilder[A] =
    ignoreRequest(fa.fold(Future.successful(ifNone)))

  def noneValue[A](fa: Option[A])(ifSome: A => Response): HandlerBuilder[Unit] =
    ignoreRequest(afr => fa.fold(afr(()))(a => Future.successful(ifSome(a))))

  def rightValue[L, R](fa: Either[L, R])(ifLeft: L => Response): HandlerBuilder[R] =
    ignoreRequest(fa.fold(l => Future.successful(ifLeft(l)), _))

  def leftValue[L, R](fa: Either[L, R])(ifRight: R => Response): HandlerBuilder[L] =
    ignoreRequest(fa.fold(_, r => Future.successful(ifRight(r))))

  def successValue[A](fa: Try[A])(ifFailure: Throwable => Response): HandlerBuilder[A] =
    ignoreRequest(fa.fold(e => Future.successful(ifFailure(e)), _))

  def failureValue[A](fa: Try[A])(ifSuccess: A => Response): HandlerBuilder[Throwable] =
    ignoreRequest(fa.fold(_, a => Future.successful(ifSuccess(a))))

  def successValue[A](
      fa: => Future[A]
  )(ifFailure: Throwable => Response)(implicit ec: ExecutionContext): HandlerBuilder[A] =
    ignoreRequest { afr =>
      val p = Promise[Response]()
      fa.onComplete {
        case Success(a) => afr(a).onComplete(p.complete)
        case Failure(e) => Future.successful(ifFailure(e))
      }
      p.future
    }

  def failureValue[A](
      fa: => Future[A]
  )(ifSuccess: A => Response)(implicit ec: ExecutionContext): HandlerBuilder[Throwable] =
    ignoreRequest { afr =>
      val p = Promise[Response]()
      fa.onComplete {
        case Success(a) => Future.successful(ifSuccess(a))
        case Failure(e) => afr(e).onComplete(p.complete)
      }
      p.future
    }

  // request

  def getRequest: HandlerBuilder[Request] = HandlerBuilder(req => _(req))
}
