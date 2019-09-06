package monoton.server.netty

import monoton.server.RoutingFlow

import scala.concurrent.ExecutionContext

// io.netty.channel.ChannelPipelineException: monoton.server.netty.HttpServerHandler is not a @Sharable handler, so can't be added or removed multiple times.
class HttpServerHandlerProvider(
    httpDriver: HttpMessageConvertFlow,
    routingFlow: RoutingFlow,
    executor: ExecutionContext
) {
  def get: HttpServerHandler = new HttpServerHandler(httpDriver, routingFlow, executor)
}
