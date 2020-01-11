package guttural.http

trait FormMapping[A] {
  def getValue(m: Map[String, String]): FormMapping.Result[A]
}

object FormMapping {
  type Result[A] = Either[List[MappingError], A]

  def apply[A](implicit F: FormMapping[A]): FormMapping[A] = F

  trait FieldValueDecoder[A] {}

  sealed abstract case class MappingError(msg: String)

  object MappingError {
    def NotFound(key: String): MappingError           = new MappingError(s"not found: $key")            {}
    def NotConvertibleType(key: String): MappingError = new MappingError(s"not convertible type: $key") {}
  }
}
