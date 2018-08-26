package brisbane.scala.meetup.models

import io.circe.Encoder
import io.circe.generic.semiauto._

case class Id(id: Int)

object Id {
  implicit val encoder: Encoder[Id] = deriveEncoder
}
