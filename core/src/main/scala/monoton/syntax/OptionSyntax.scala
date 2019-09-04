package monoton.syntax

import monoton.http.{ContentEncoder, Response, Status}
import monoton.http.ContentEncoder.unitEncoder
import monoton.server.Handler

trait OptionSyntax {
  implicit final def toOptionOps[A](fa: Option[A]): OptionOps[A] = new OptionOps(fa)
}

final class OptionOps[A](private val fa: Option[A]) extends AnyVal {

  def someValueOr(ifNone: => Response): Handler[A]      = Handler.someValue(fa)(ifNone)
  def noneValueOr(ifSome: A => Response): Handler[Unit] = Handler.noneValue(fa)(ifSome)

  def valueOr(ifNone: => Response): Handler[A] = someValueOr(ifNone)

  def valueOrNotFound[C](c: C)(implicit F: ContentEncoder[C]): Handler[A] =
    valueOr(Response(Status.NotFound, F.defaultContentType, F.encode(c)))
  def valueOrNotFound: Handler[A] =
    valueOr(Response(Status.NotFound, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrBadRequest[C](c: C)(implicit F: ContentEncoder[C]): Handler[A] =
    valueOr(Response(Status.BadRequest, F.defaultContentType, F.encode(c)))
  def valueOrBadRequest: Handler[A] =
    valueOr(Response(Status.BadRequest, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrInternalServerError[C](c: C)(implicit F: ContentEncoder[C]): Handler[A] =
    valueOr(Response(Status.InternalServerError, F.defaultContentType, F.encode(c)))
  def valueOrInternalServerError: Handler[A] =
    valueOr(Response(Status.InternalServerError, unitEncoder.defaultContentType, unitEncoder.encode(())))
}
