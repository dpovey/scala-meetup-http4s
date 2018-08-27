package brisbane.scala.meetup.models

import java.util.UUID

import cats.Show
import cats.implicits._
import doobie.postgres.implicits._
import doobie.util.meta.Meta
import io.circe.{Decoder, Encoder}

class Id(val id: UUID) extends AnyVal

object Id {
  def apply(id: UUID): Id = new Id(id)
  implicit val encoder: Encoder[Id] = Encoder[UUID].contramap(_.id)
  implicit val decoder: Decoder[Id] = Decoder[UUID].map(Id(_))
  implicit val meta: Meta[Id] = Meta[UUID].xmap(Id(_), _.id)
  implicit def show: Show[Id] = Show[UUID].contramap(_.id)
}
