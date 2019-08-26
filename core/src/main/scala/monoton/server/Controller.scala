package monoton.server

import monoton.http.{Response, ResponseBuilders}

trait Controller extends ResponseBuilders {
  type RunnableHandler = Handler[Response]

  object request {}
}
