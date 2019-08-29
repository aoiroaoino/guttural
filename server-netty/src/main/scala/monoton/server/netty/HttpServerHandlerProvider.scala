package monoton.server.netty

import monoton.server.Router

import scala.concurrent.ExecutionContext

// io.netty.channel.ChannelPipelineException: monoton.server.netty.HttpServerHandler is not a @Sharable handler, so can't be added or removed multiple times.
class HttpServerHandlerProvider(router: Router, httpDriver: HttpFlow, executor: ExecutionContext) {
  def get: HttpServerHandler = new HttpServerHandler(router, httpDriver, executor)
}
