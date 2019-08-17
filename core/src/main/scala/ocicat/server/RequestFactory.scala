package ocicat.server

abstract class RequestFactory[A] {
  def create(from: A): Option[Request]
}
