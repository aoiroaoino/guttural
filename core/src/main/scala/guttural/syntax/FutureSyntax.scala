package guttural.syntax

import guttural.http.server.HandlerBuilder
import guttural.http.{Response, Status}

import scala.concurrent.{ExecutionContext, Future}

trait FutureSyntax {
  implicit final def toFutureSyntax[A](fa: Future[A]): FutureOps[A] = new FutureOps(fa)
}

final class FutureOps[A](private val fa: Future[A]) extends AnyVal {
  import Response.ContentEncoder, ContentEncoder.unitEncoder

  def successValueOr(ifFailure: Throwable => Response)(implicit ec: ExecutionContext): HandlerBuilder[A] =
    HandlerBuilder.successValue(fa)(ifFailure)
  def failureValueOr(ifSuccess: A => Response)(implicit ec: ExecutionContext): HandlerBuilder[Throwable] =
    HandlerBuilder.failureValue(fa)(ifSuccess)

  def valueOr(ifFailure: Throwable => Response)(implicit ec: ExecutionContext): HandlerBuilder[A] =
    successValueOr(ifFailure)

  def valueOrNotFound(implicit ec: ExecutionContext): HandlerBuilder[A] =
    valueOr(_ => Response(Status.NotFound, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrBadRequest[C](f: Throwable => C)(implicit F: ContentEncoder[C], ec: ExecutionContext): HandlerBuilder[A] =
    valueOr(e => Response(Status.BadRequest, F.defaultContentType, F.encode(f(e))))
  def valueOrBadRequest(implicit ec: ExecutionContext): HandlerBuilder[A] =
    valueOr(_ => Response(Status.BadRequest, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrInternalServerError[C](
      f: Throwable => C
  )(implicit F: ContentEncoder[C], ec: ExecutionContext): HandlerBuilder[A] =
    valueOr(e => Response(Status.InternalServerError, F.defaultContentType, F.encode(f(e))))
  def valueOrInternalServerError(implicit ec: ExecutionContext): HandlerBuilder[A] =
    valueOr(_ => Response(Status.InternalServerError, unitEncoder.defaultContentType, unitEncoder.encode(())))
}
