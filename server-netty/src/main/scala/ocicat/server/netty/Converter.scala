package ocicat.server.netty

import io.netty.handler.codec.http.HttpResponseStatus
import ocicat.http.Status

object Converter {

  // TODO: fully implement
  def toHttpResponseStatus(from: Status): HttpResponseStatus = from match {
    case Status.Ok                  => HttpResponseStatus.OK
    case Status.BadRequest          => HttpResponseStatus.BAD_REQUEST
    case Status.NotFound            => HttpResponseStatus.NOT_FOUND
    case Status.InternalServerError => HttpResponseStatus.INTERNAL_SERVER_ERROR
    case _                          => HttpResponseStatus.NOT_IMPLEMENTED
  }
}
