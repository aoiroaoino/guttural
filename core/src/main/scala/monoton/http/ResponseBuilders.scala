package monoton.http

private[monoton] trait ResponseBuilders {

  final val Ok = new ResponseBuilderWithBody {
    override val status = Status.Ok
  }
  final val NotFound = new ResponseBuilderWithBody {
    override val status = Status.NotFound
  }
  final val BadRequest = new ResponseBuilderWithBody {
    override val status = Status.BadRequest
  }
  final val NotImplemented = new ResponseBuilderWithBody {
    override val status = Status.NotImplemented
  }
  final val Unauthorized = new ResponseBuilderWithBody {
    override val status = Status.Unauthorized
  }
  final val InternalServerError = new ResponseBuilderWithBody {
    override val status = Status.InternalServerError
  }
}

object ResponseBuilders extends ResponseBuilders
