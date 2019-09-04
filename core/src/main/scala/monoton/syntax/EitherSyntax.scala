package monoton.syntax

import monoton.http.ContentEncoder.unitEncoder
import monoton.http.{ContentEncoder, Response, Status}
import monoton.server.Handler

trait EitherSyntax {
  implicit final def ToEitherOps[L, R](fa: Either[L, R]): EitherOps[L, R] = new EitherOps(fa)
}

class EitherOps[L, R](private val fa: Either[L, R]) extends AnyVal {

  def rightValueOr(ifLeft: L => Response): Handler[R] = Handler.rightValue(fa)(ifLeft)
  def leftValueOr(ifRight: R => Response): Handler[L] = Handler.leftValue(fa)(ifRight)

  def valueOr(ifLeft: L => Response): Handler[R] = rightValueOr(ifLeft)

  def valueOrNotFound[C](f: L => C)(implicit F: ContentEncoder[C]): Handler[R] =
    valueOr(l => Response(Status.NotFound, F.defaultContentType, F.encode(f(l))))
  def valueOrNotFound: Handler[R] =
    valueOr(_ => Response(Status.NotFound, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrBadRequest[C](f: L => C)(implicit F: ContentEncoder[C]): Handler[R] =
    valueOr(l => Response(Status.BadRequest, F.defaultContentType, F.encode(f(l))))
  def valueOrBadRequest: Handler[R] =
    valueOr(_ => Response(Status.BadRequest, unitEncoder.defaultContentType, unitEncoder.encode(())))

  def valueOrInternalServerError[C](f: L => C)(implicit F: ContentEncoder[C]): Handler[R] =
    valueOr(l => Response(Status.InternalServerError, F.defaultContentType, F.encode(f(l))))
  def valueOrInternalServerError: Handler[R] =
    valueOr(_ => Response(Status.InternalServerError, unitEncoder.defaultContentType, unitEncoder.encode(())))
}
