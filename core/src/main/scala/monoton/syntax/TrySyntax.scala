package monoton.syntax

import monoton.http.ContentEncoder.unitEncoder
import monoton.http.{ContentEncoder, Response, Status}
import monoton.server.Handler

import scala.util.Try

trait TrySyntax {
  implicit final def toTrySyntax[A](fa: Try[A]): TryOps[A] = new TryOps(fa)
}

final class TryOps[A](private val fa: Try[A]) extends AnyVal {

  def successValueOr(ifFailure: Throwable => Response): Handler[A] = Handler.successValue(fa)(ifFailure)
  def failureValueOr(ifSuccess: A => Response): Handler[Throwable] = Handler.failureValue(fa)(ifSuccess)

  def valueOr(ifFailure: Throwable => Response): Handler[A] = successValueOr(ifFailure)

  def valueOrNotFound[C](f: Throwable => C)(implicit F: ContentEncoder[C]): Handler[A] =
    valueOr(e => Response(Status.NotFound, F.defaultContentType, F.encode(f(e))))
  def valueOrNotFound: Handler[A] =
    valueOr(_ => Response(Status.NotFound, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrBadRequest[C](f: Throwable => C)(implicit F: ContentEncoder[C]): Handler[A] =
    valueOr(e => Response(Status.BadRequest, F.defaultContentType, F.encode(f(e))))
  def valueOrBadRequest: Handler[A] =
    valueOr(_ => Response(Status.BadRequest, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrInternalServerError[C](f: Throwable => C)(implicit F: ContentEncoder[C]): Handler[A] =
    valueOr(e => Response(Status.InternalServerError, F.defaultContentType, F.encode(f(e))))
  def valueOrInternalServerError: Handler[A] =
    valueOr(_ => Response(Status.InternalServerError, unitEncoder.defaultContentType, unitEncoder.encode(())))
}
