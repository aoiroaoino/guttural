package monoton.server

import monoton.http.Method

class Route(val method: Method, val path: String) {
  private val placeholderPattern = """\{([a-zA-Z_][a-zA-Z0-9_]*?)\}""".r

  val segments: Seq[String]        = path.split('/').toSeq
  val patternSegments: Seq[String] = segments.collect { case placeholderPattern(key) => key }

  def getPathParams(targetPath: String): Map[String, String] = {
    if (isMatch(targetPath)) {
      val targetPathSegments = targetPath.split('/').toSeq
      segments.zip(targetPathSegments).collect { case (placeholderPattern(s), t) => (s, t) }.toMap
    } else {
      Map.empty
    }
  }

  def isMatch(targetPath: String): Boolean =
    if (patternSegments.isEmpty) {
      path == targetPath
    } else {
      val targetPathSegments = targetPath.split('/').toSeq
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
