package monoton.http

sealed abstract case class Status(code: Int, reasonPhrase: String)

object Status {

  // Information 1xx
  // https://httpwg.org/specs/rfc7231.html#status.1xx

  val Continue           = status(100, "Continue")
  val SwitchingProtocols = status(101, "Switching Protocols")

  private val Informational_1xx = Seq(Continue, SwitchingProtocols)

  // Successful 2xx
  // https://httpwg.org/specs/rfc7231.html#status.2xx

  val Ok                          = status(200, "OK")
  val Created                     = status(201, "Created")
  val Accepted                    = status(202, "Accepted")
  val NonAuthoritativeInformation = status(203, "Non-Authoritative Information")
  val NoContent                   = status(204, "No Content")
  val ResetContent                = status(205, "Reset Content")

  private val Successful_2xx = Seq(Ok, Created, Accepted, NonAuthoritativeInformation, NoContent, ResetContent)

  // Redirection 3xx
  // https://httpwg.org/specs/rfc7231.html#status.3xx

  val MultipleChoices   = status(300, "Multiple Choices")
  val MovedPermanently  = status(301, "Moved Permanently")
  val Found             = status(302, "Found")
  val SeeOther          = status(303, "See Other")
  val NotModified       = status(304, "Not Modified")
  val UseProxy          = status(305, "Use Proxy")
  val Unused            = status(306, "(Unused)")
  val TemporaryRedirect = status(307, "Temporary Redirect")

  private val Redirection_3xx =
    Seq(MultipleChoices, MovedPermanently, Found, SeeOther, NotModified, UseProxy, Unused, TemporaryRedirect)

  // Client Error 4xx
  // https://httpwg.org/specs/rfc7231.html#status.4xx

  val BadRequest           = status(400, "Bad Request")
  val Unauthorized         = status(401, "Unauthorized")
  val PaymentRequired      = status(402, "Payment Required")
  val Forbidden            = status(403, "Forbidden")
  val NotFound             = status(404, "Not Found")
  val MethodNotAllowed     = status(405, "Method Not Allowed")
  val NotAcceptable        = status(406, "Not Acceptable")
  val RequestTimeout       = status(408, "Request Timeout")
  val Conflict             = status(409, "Conflict")
  val Gone                 = status(410, "Gone")
  val LengthRequired       = status(411, "Length Required")
  val PayloadTooLarge      = status(413, "Payload Too Large")
  val URITooLong           = status(414, "URI Too Long")
  val UnsupportedMediaType = status(415, "Unsupported Media Type")
  val ExpectationFailed    = status(417, "Expectation Failed")
  val UpgradeRequired      = status(426, "Upgrade Required")

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

  val InternalServerError     = status(500, "Internal Server Error")
  val NotImplemented          = status(501, "Not Implemented")
  val BadGateway              = status(502, "Bad Gateway")
  val ServiceUnavailable      = status(503, "Service Unavailable")
  val GatewayTimeout          = status(504, "Gateway Timeout")
  val HTTPVersionNotSupported = status(505, "HTTP Version Not Supported")

  private val ServerError_5xx =
    Seq(InternalServerError, NotImplemented, BadGateway, ServiceUnavailable, GatewayTimeout, HTTPVersionNotSupported)

  // ===

  private val all = Informational_1xx ++ Successful_2xx ++ Redirection_3xx ++ ClientError_4xx ++ ServerError_5xx

  private def status(code: Int, reasonPhrase: String): Status = new Status(code, reasonPhrase) {}

  private[http] def fromStatusCode(n: Int): Option[Status] = all.find(_.code == n)
}
