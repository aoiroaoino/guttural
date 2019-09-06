package monoton.server.netty

import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import io.netty.util.ReferenceCountUtil
import monoton.server.{HEADMethodFilter, RoutingFlow}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class HttpServerHandler(
    httpMessageConvertFlow: HttpMessageConvertFlow,
    routingFlow: RoutingFlow,
    executor: ExecutionContext
) extends SimpleChannelInboundHandler[HttpRequest](false) {
  import HttpHeaderNames._, HttpHeaderValues._

  private implicit val ec: ExecutionContext = executor

  val httpRequestFlow =
  httpMessageConvertFlow |> // up: Netty の HttpRequest を Request へ, down: Response を Netty の HttpResponse へ
  HEADMethodFilter |>       // down: HEAD リクエストは Response の body を空にする
  routingFlow

  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpRequest): Unit = {
    httpRequestFlow
      .runF(msg)(_.execute()(executor)) // run Handler[Response]
      .andThen {
        case Success(httpRes) =>
          val f: ChannelFuture   = ctx.write(httpRes)
          val keepAlive: Boolean = HttpUtil.isKeepAlive(msg)
          if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE)
          }
          if (keepAlive && !msg.protocolVersion.isKeepAliveDefault) {
            httpRes.headers.set(CONNECTION, HttpHeaderValues.KEEP_ALIVE)
          } else {
            httpRes.headers.set(CONNECTION, CLOSE)
          }
          ctx.flush()
        case Failure(e) =>
          e.printStackTrace()
      }
      .onComplete(_ => ReferenceCountUtil.release(msg))
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
