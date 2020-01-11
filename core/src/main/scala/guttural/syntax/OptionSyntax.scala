package guttural.syntax

import guttural.http.{Response, Status}
import guttural.http.server.HandlerBuilder

trait OptionSyntax {
  implicit final def toOptionOps[A](fa: Option[A]): OptionOps[A] = new OptionOps(fa)
}

final class OptionOps[A](private val fa: Option[A]) extends AnyVal {
  import Response.ContentEncoder, ContentEncoder.unitEncoder

  def someValueOr(ifNone: => Response): HandlerBuilder[A]      = HandlerBuilder.someValue(fa)(ifNone)
  def noneValueOr(ifSome: A => Response): HandlerBuilder[Unit] = HandlerBuilder.noneValue(fa)(ifSome)

  def valueOr(ifNone: => Response): HandlerBuilder[A] = someValueOr(ifNone)

  def valueOrNotFound[C](c: C)(implicit F: ContentEncoder[C]): HandlerBuilder[A] =
    valueOr(Response(Status.NotFound, F.defaultContentType, F.encode(c)))
  def valueOrNotFound: HandlerBuilder[A] =
    valueOr(Response(Status.NotFound, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrBadRequest[C](c: C)(implicit F: ContentEncoder[C]): HandlerBuilder[A] =
    valueOr(Response(Status.BadRequest, F.defaultContentType, F.encode(c)))
  def valueOrBadRequest: HandlerBuilder[A] =
    valueOr(Response(Status.BadRequest, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrInternalServerError[C](c: C)(implicit F: ContentEncoder[C]): HandlerBuilder[A] =
    valueOr(Response(Status.InternalServerError, F.defaultContentType, F.encode(c)))
  def valueOrInternalServerError: HandlerBuilder[A] =
    valueOr(Response(Status.InternalServerError, unitEncoder.defaultContentType, unitEncoder.encode(())))
}
