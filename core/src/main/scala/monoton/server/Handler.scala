package monoton.server

import monoton.http.{Request, Response}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.language.higherKinds
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

final case class Handler[A](asFunction: Request => (A => Future[Response]) => Future[Response]) {
  private def runFunc(f: A => Future[Response])(req: Request): Future[Response] =
    try asFunction(req)(f)
    catch { case NonFatal(e) => Future.failed(e) }

  def run(req: Request)(implicit ev: A =:= Response): Future[Response] = runFunc(a => Future.successful(ev(a)))(req)

  def runWith(f: A => Future[Response])(req: Request): Future[Response] = runFunc(f)(req)

  def map[B](f: A => B): Handler[B] = Handler(req => bfr => runFunc(f andThen bfr)(req))

  def flatMap[B](f: A => Handler[B]): Handler[B] = Handler(req => bfr => runFunc(a => f(a).asFunction(req)(bfr))(req))
}

object Handler {

  // ignore request
  private def ignoreRequest[A](f: (A => Future[Response]) => Future[Response]): Handler[A] = Handler(_ => f(_))

  def pure[A](a: A): Handler[A]     = Handler(_ => _(a))
  def later[A](a: => A): Handler[A] = Handler(_ => _(a))

  def interrupt(e: Response): Handler[Response] = Handler(_ => _ => Future.successful(e))

  def TODO: Handler[Response] = interrupt(Response.NotImplemented("TODO"))
  def WIP: Handler[Response]  = interrupt(Response.NotImplemented("WIP"))

  def unit: Handler[Unit] = pure(())

  def catchNonFatal[A](a: => A)(ifError: Throwable => Response): Handler[A] = successValue(Try(a))(ifError)

  def fromFunction(func: Request => Response): Handler[Response] =
    Handler(req => _(func(req)))

  def someValue[A](fa: Option[A])(ifNone: => Response): Handler[A] =
    ignoreRequest(fa.fold(Future.successful(ifNone)))

  def noneValue[A](fa: Option[A])(ifSome: A => Response): Handler[Unit] =
    ignoreRequest(afr => fa.fold(afr(()))(a => Future.successful(ifSome(a))))

  def rightValue[L, R](fa: Either[L, R])(ifLeft: L => Response): Handler[R] =
    ignoreRequest(fa.fold(l => Future.successful(ifLeft(l)), _))

  def leftValue[L, R](fa: Either[L, R])(ifRight: R => Response): Handler[L] =
    ignoreRequest(fa.fold(_, r => Future.successful(ifRight(r))))

  def successValue[A](fa: Try[A])(ifFailure: Throwable => Response): Handler[A] =
    ignoreRequest(fa.fold(e => Future.successful(ifFailure(e)), _))

  def failureValue[A](fa: Try[A])(ifSuccess: A => Response): Handler[Throwable] =
    ignoreRequest(fa.fold(_, a => Future.successful(ifSuccess(a))))

  def successValue[A](fa: => Future[A])(ifFailure: Throwable => Response)(implicit ec: ExecutionContext): Handler[A] =
    ignoreRequest { afr =>
      val p = Promise[Response]()
      fa.onComplete {
        case Success(a) => afr(a).onComplete(p.complete)
        case Failure(e) => Future.successful(ifFailure(e))
      }
      p.future
    }

  def failureValue[A](fa: => Future[A])(ifSuccess: A => Response)(implicit ec: ExecutionContext): Handler[Throwable] =
    ignoreRequest { afr =>
      val p = Promise[Response]()
      fa.onComplete {
        case Success(a) => Future.successful(ifSuccess(a))
        case Failure(e) => afr(e).onComplete(p.complete)
      }
      p.future
    }

  // request

  def getRequest: Handler[Request] = Handler(req => _(req))

  def getBodyAsString: Handler[String] = getRequest.map(_.bodyAsString)

  def getOptionalQuery(key: String): Handler[Option[String]] = Handler(req => _(req.query.get[String](key)))

  def getOptionalQuery(key: String, default: String): Handler[String] =
    Handler(req => _(req.query.get[String](key).getOrElse(default)))

  def getRequiredQuery(key: String)(ifEmpty: => Response): Handler[String] =
    getOptionalQuery(key).flatMap(someValue(_)(ifEmpty))

}
