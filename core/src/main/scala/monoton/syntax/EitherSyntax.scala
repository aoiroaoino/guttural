package monoton.syntax

import monoton.http.server.HandlerBuilder
import monoton.http.{Response, Status}

trait EitherSyntax {
  implicit final def ToEitherOps[L, R](fa: Either[L, R]): EitherOps[L, R] = new EitherOps(fa)
}

class EitherOps[L, R](private val fa: Either[L, R]) extends AnyVal {
  import Response.ContentEncoder, ContentEncoder.unitEncoder

  def rightValueOr(ifLeft: L => Response): HandlerBuilder[R] = HandlerBuilder.rightValue(fa)(ifLeft)
  def leftValueOr(ifRight: R => Response): HandlerBuilder[L] = HandlerBuilder.leftValue(fa)(ifRight)

  def valueOr(ifLeft: L => Response): HandlerBuilder[R] = rightValueOr(ifLeft)

  def valueOrNotFound[C](f: L => C)(implicit F: Response.ContentEncoder[C]): HandlerBuilder[R] =
    valueOr(l => Response(Status.NotFound, F.defaultContentType, F.encode(f(l))))
  def valueOrNotFound: HandlerBuilder[R] =
    valueOr(_ => Response(Status.NotFound, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrBadRequest[C](f: L => C)(implicit F: ContentEncoder[C]): HandlerBuilder[R] =
    valueOr(l => Response(Status.BadRequest, F.defaultContentType, F.encode(f(l))))
  def valueOrBadRequest: HandlerBuilder[R] =
    valueOr(_ => Response(Status.BadRequest, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrInternalServerError[C](f: L => C)(implicit F: ContentEncoder[C]): HandlerBuilder[R] =
    valueOr(l => Response(Status.InternalServerError, F.defaultContentType, F.encode(f(l))))
  def valueOrInternalServerError: HandlerBuilder[R] =
    valueOr(_ => Response(Status.InternalServerError, unitEncoder.defaultContentType, unitEncoder.encode(())))
}
