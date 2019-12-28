package monoton.http

sealed abstract case class Status(code: Int, reasonPhrase: String)

object Status {
  private def apply(code: Int, reasonPhrase: String): Status = new Status(code, reasonPhrase) {}

  // Information 1xx
  // https://httpwg.org/specs/rfc7231.html#status.1xx

  val Continue           = Status(100, "Continue")
  val SwitchingProtocols = Status(101, "Switching Protocols")

  private val Informational_1xx = Seq(Continue, SwitchingProtocols)

  // Successful 2xx
  // https://httpwg.org/specs/rfc7231.html#status.2xx

  val Ok                          = Status(200, "OK")
  val Created                     = Status(201, "Created")
  val Accepted                    = Status(202, "Accepted")
  val NonAuthoritativeInformation = Status(203, "Non-Authoritative Information")
  val NoContent                   = Status(204, "No Content")
  val ResetContent                = Status(205, "Reset Content")

  private val Successful_2xx = Seq(Ok, Created, Accepted, NonAuthoritativeInformation, NoContent, ResetContent)

  // Redirection 3xx
  // https://httpwg.org/specs/rfc7231.html#status.3xx

  val MultipleChoices   = Status(300, "Multiple Choices")
  val MovedPermanently  = Status(301, "Moved Permanently")
  val Found             = Status(302, "Found")
  val SeeOther          = Status(303, "See Other")
  val NotModified       = Status(304, "Not Modified")
  val UseProxy          = Status(305, "Use Proxy")
  val Unused            = Status(306, "(Unused)")
  val TemporaryRedirect = Status(307, "Temporary Redirect")

  private val Redirection_3xx =
    Seq(MultipleChoices, MovedPermanently, Found, SeeOther, NotModified, UseProxy, Unused, TemporaryRedirect)

  // Client Error 4xx
  // https://httpwg.org/specs/rfc7231.html#status.4xx

  val BadRequest           = Status(400, "Bad Request")
  val Unauthorized         = Status(401, "Unauthorized")
  val PaymentRequired      = Status(402, "Payment Required")
  val Forbidden            = Status(403, "Forbidden")
  val NotFound             = Status(404, "Not Found")
  val MethodNotAllowed     = Status(405, "Method Not Allowed")
  val NotAcceptable        = Status(406, "Not Acceptable")
  val RequestTimeout       = Status(408, "Request Timeout")
  val Conflict             = Status(409, "Conflict")
  val Gone                 = Status(410, "Gone")
  val LengthRequired       = Status(411, "Length Required")
  val PayloadTooLarge      = Status(413, "Payload Too Large")
  val URITooLong           = Status(414, "URI Too Long")
  val UnsupportedMediaType = Status(415, "Unsupported Media Type")
  val ExpectationFailed    = Status(417, "Expectation Failed")
  val UpgradeRequired      = Status(426, "Upgrade Required")

  private val ClientError_4xx = Seq(
    BadRequest,
    Unauthorized,
    PaymentRequired,
    Forbidden,
    NotFound,
    MethodNotAllowed,
    NotAcceptable,
    RequestTimeout,
    Conflict,
    Gone,
    LengthRequired,
    PayloadTooLarge,
    URITooLong,
    UnsupportedMediaType,
    ExpectationFailed,
    UpgradeRequired
  )

  // Server Error 5xx
  // https://httpwg.org/specs/rfc7231.html#status.5xx

  val InternalServerError     = Status(500, "Internal Server Error")
  val NotImplemented          = Status(501, "Not Implemented")
  val BadGateway              = Status(502, "Bad Gateway")
  val ServiceUnavailable      = Status(503, "Service Unavailable")
  val GatewayTimeout          = Status(504, "Gateway Timeout")
  val HTTPVersionNotSupported = Status(505, "HTTP Version Not Supported")

  private val ServerError_5xx =
    Seq(InternalServerError, NotImplemented, BadGateway, ServiceUnavailable, GatewayTimeout, HTTPVersionNotSupported)

  // ===

  private val all = Informational_1xx ++ Successful_2xx ++ Redirection_3xx ++ ClientError_4xx ++ ServerError_5xx

  private[http] def fromStatusCode(n: Int): Option[Status] = all.find(_.code == n)

  // e.g. "HTTP/1.1 200 OK"
  private[http] def fromStatusLine(s: String): Option[Status] =
    s.split(' ') match {
      case Array(_, code, _) =>
        try fromStatusCode(code.toInt)
        catch { case _: NumberFormatException => None }
      case _ => None
    }
}
