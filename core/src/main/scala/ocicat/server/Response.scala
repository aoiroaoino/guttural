package ocicat.server

import ocicat.http.Status

final case class Response(status: Status, content: String)
