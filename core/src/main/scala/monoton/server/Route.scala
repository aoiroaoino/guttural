package monoton.server

import monoton.http.Method

class Route(val method: Method, val path: String) {
  private val placeholderPattern = """\{([a-zA-Z_][a-zA-Z0-9_]*?)\}""".r

  val segments: Seq[String]        = path.split('/').toSeq
  val patternSegments: Seq[String] = segments.collect { case placeholderPattern(key) => key }

  def getPathParams(requestPath: String): Map[String, String] = {
    if (isMatchingPath(requestPath)) {
      val targetPathSegments = requestPath.split('/').toSeq
      segments.zip(targetPathSegments).collect { case (placeholderPattern(s), t) => (s, t) }.toMap
    } else {
      Map.empty
    }
  }

  final def isMatching(requestMethod: Method, requestPath: String): Boolean =
    isMatchingMethod(requestMethod) && isMatchingPath(requestPath)

  // HEAD リクエストの場合は GET リクエストも一致とみなす
  private[monoton] def isMatchingMethod(requestMethod: Method): Boolean =
    if (requestMethod == Method.HEAD) method == Method.HEAD || method == Method.GET
    else method == requestMethod

  private[monoton] def isMatchingPath(requestPath: String): Boolean =
    if (patternSegments.isEmpty) {
      path == requestPath
    } else {
      val targetPathSegments = requestPath.split('/').toSeq
      if (targetPathSegments.nonEmpty && segments.length == targetPathSegments.length) {
        segments.zip(targetPathSegments).forall {
          case (placeholderPattern(_), _) => true
          case (s, t)                     => s == t
        }
      } else {
        false
      }
    }
}
