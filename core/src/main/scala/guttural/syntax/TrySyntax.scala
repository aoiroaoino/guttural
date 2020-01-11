package guttural.syntax

import guttural.http.server.HandlerBuilder
import guttural.http.{Response, Status}

import scala.util.Try

trait TrySyntax {
  implicit final def toTrySyntax[A](fa: Try[A]): TryOps[A] = new TryOps(fa)
}

final class TryOps[A](private val fa: Try[A]) extends AnyVal {
  import Response.ContentEncoder, ContentEncoder.unitEncoder

  def successValueOr(ifFailure: Throwable => Response): HandlerBuilder[A] = HandlerBuilder.successValue(fa)(ifFailure)
  def failureValueOr(ifSuccess: A => Response): HandlerBuilder[Throwable] = HandlerBuilder.failureValue(fa)(ifSuccess)

  def valueOr(ifFailure: Throwable => Response): HandlerBuilder[A] = successValueOr(ifFailure)

  def valueOrNotFound[C](f: Throwable => C)(implicit F: ContentEncoder[C]): HandlerBuilder[A] =
    valueOr(e => Response(Status.NotFound, F.defaultContentType, F.encode(f(e))))
  def valueOrNotFound: HandlerBuilder[A] =
    valueOr(_ => Response(Status.NotFound, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrBadRequest[C](f: Throwable => C)(implicit F: ContentEncoder[C]): HandlerBuilder[A] =
    valueOr(e => Response(Status.BadRequest, F.defaultContentType, F.encode(f(e))))
  def valueOrBadRequest: HandlerBuilder[A] =
    valueOr(_ => Response(Status.BadRequest, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrInternalServerError[C](f: Throwable => C)(implicit F: ContentEncoder[C]): HandlerBuilder[A] =
    valueOr(e => Response(Status.InternalServerError, F.defaultContentType, F.encode(f(e))))
  def valueOrInternalServerError: HandlerBuilder[A] =
    valueOr(_ => Response(Status.InternalServerError, unitEncoder.defaultContentType, unitEncoder.encode(())))
}
