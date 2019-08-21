package monoton.server

import monoton.http.Response

trait Controller {
  type RunnableHandler = Handler[Response]

  object request {}
}
