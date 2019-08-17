package ocicat.http

sealed abstract class Status(val code: Int, val reasonPhrase: String) extends Product with Serializable

object Status {
  // Successful 2xx
  // https://httpwg.org/specs/rfc7231.html#status.2xx
  case object Ok      extends Status(200, "OK")
  case object Created extends Status(201, "Created")

  // Redirection 3xx
  // https://httpwg.org/specs/rfc7231.html#status.3xx
  case object SeeOther    extends Status(303, "See Other")
  case object NotModified extends Status(304, "Not Modified")

  // 4xx
  case object BadRequest   extends Status(400, "Bad Request")
  case object Unauthorized extends Status(401, "Unauthorized")
  case object NotFound     extends Status(404, "Not Found")

  // 5xx
  case object InternalServerError extends Status(500, "Internal Server Error")
  case object NotImplemented      extends Status(501, "Not Implemented")

  private val all = Seq(
    // 2xx
    Ok,
    Created,
    // 3xx
    SeeOther,
    NotModified,
    // 4xx
    BadRequest,
    Unauthorized,
    NotFound,
    // 5xx
    InternalServerError,
    NotImplemented
  )

  private[http] def fromCode(code: Int): Option[Status] = all.find(_.code == code)
}
