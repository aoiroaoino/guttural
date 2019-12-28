package monoton.http
package server
package internal

private[http] object HEADRequestDownstreamFilter extends Pipeline.DownstreamFilter[Request, Response] {

  override def downstream(b: Response, s: Request): Response =
    if (s.method == Method.HEAD) b.clearContent else b
}
