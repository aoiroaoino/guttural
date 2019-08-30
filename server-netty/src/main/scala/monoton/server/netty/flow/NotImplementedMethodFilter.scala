package monoton.server.netty
package flow

import io.netty.handler.codec.http.{HttpRequest, HttpResponse}
import monoton.http.Method
import monoton.util.Filter

object NotImplementedMethodFilter extends Filter.Upstream[HttpRequest, HttpResponse] {

  override def to(httpReq: HttpRequest): Either[HttpResponse, HttpRequest] =
    Method
      .fromString(httpReq.method.name)
      .filter(Method.isSupported)
      .map(_ => httpReq)
      .toRight(NotImplemented(httpReq.protocolVersion))
}
