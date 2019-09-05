package monoton.syntax

import monoton.http.ContentEncoder.unitEncoder
import monoton.http.{ContentEncoder, Response, Status}
import monoton.server.Handler

import scala.concurrent.{ExecutionContext, Future}

trait FutureSyntax {
  implicit final def toFutureSyntax[A](fa: Future[A]): FutureOps[A] = new FutureOps(fa)
}

final class FutureOps[A](private val fa: Future[A]) extends AnyVal {

  def successValueOr(ifFailure: Throwable => Response)(implicit ec: ExecutionContext): Handler[A] =
    Handler.successValue(fa)(ifFailure)
  def failureValueOr(ifSuccess: A => Response)(implicit ec: ExecutionContext): Handler[Throwable] =
    Handler.failureValue(fa)(ifSuccess)

  def valueOr(ifFailure: Throwable => Response)(implicit ec: ExecutionContext): Handler[A] = successValueOr(ifFailure)

  def valueOrNotFound(implicit ec: ExecutionContext): Handler[A] =
    valueOr(_ => Response(Status.NotFound, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrBadRequest[C](f: Throwable => C)(implicit F: ContentEncoder[C], ec: ExecutionContext): Handler[A] =
    valueOr(e => Response(Status.BadRequest, F.defaultContentType, F.encode(f(e))))
  def valueOrBadRequest(implicit ec: ExecutionContext): Handler[A] =
    valueOr(_ => Response(Status.BadRequest, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrInternalServerError[C](
      f: Throwable => C
  )(implicit F: ContentEncoder[C], ec: ExecutionContext): Handler[A] =
    valueOr(e => Response(Status.InternalServerError, F.defaultContentType, F.encode(f(e))))
  def valueOrInternalServerError(implicit ec: ExecutionContext): Handler[A] =
    valueOr(_ => Response(Status.InternalServerError, unitEncoder.defaultContentType, unitEncoder.encode(())))
}
