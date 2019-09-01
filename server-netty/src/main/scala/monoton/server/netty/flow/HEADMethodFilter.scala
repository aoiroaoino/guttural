package monoton.server.netty.flow

import monoton.http.{Method, Request, Response}
import monoton.util.Filter

object HEADMethodFilter extends Filter.Downstream[Request, Response] {

  override def from(b: Response, s: Request): Response =
    if (s.method == Method.HEAD) b.clearContent else b
}
