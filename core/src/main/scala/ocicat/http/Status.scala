package ocicat.http

sealed abstract case class Status(code: Int, reasonPhrase: String)

object Status {
  // Successful 2xx
  // https://httpwg.org/specs/rfc7231.html#status.2xx
  val Ok: Status      = new Status(200, "OK")      {}
  val Created: Status = new Status(201, "Created") {}

  // Redirection 3xx
  // https://httpwg.org/specs/rfc7231.html#status.3xx
  val SeeOther: Status    = new Status(303, "See Other")    {}
  val NotModified: Status = new Status(304, "Not Modified") {}

  // 4xx
  val BadRequest: Status   = new Status(400, "Bad Request")  {}
  val Unauthorized: Status = new Status(401, "Unauthorized") {}
  val NotFound: Status     = new Status(404, "Not Found")    {}

  // 5xx
  val InternalServerError: Status = new Status(500, "Internal Server Error") {}
  val NotImplemented: Status      = new Status(501, "Not Implemented")       {}

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
