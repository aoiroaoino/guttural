package ocicat.server

import ocicat.http.Response

trait Controller {
  type RunnableHandler = Handler[Response]
}
