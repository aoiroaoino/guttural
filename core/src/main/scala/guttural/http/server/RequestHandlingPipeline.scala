package guttural.http
package server

final class RequestHandlingPipeline(
    router: Router,
    afterProcedure: (Request, Response) => Response
) extends Pipeline[Request, Response, (Request, RequestHandler), Response] {

  def this(router: Router) = this(router, (_, res) => res)

  override def upstream(s: Request): Either[Response, (Request, RequestHandler)] =
    router.resolveHandler(s).map(s -> _).toRight(Response.NotFound)

  override def downstream(b: Response, s: Request): Response =
    try {
      afterProcedure(s, b)
    } catch {
      case _: Throwable => // 可能な限りレスポンスを返す
        // TODO: logging
        Response.InternalServerError("An unexpected error occurred...")
    }
}
