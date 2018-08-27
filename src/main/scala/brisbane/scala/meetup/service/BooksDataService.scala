package brisbane.scala.meetup.service

import brisbane.scala.meetup.dao.BooksDao
import brisbane.scala.meetup.models.{Book, Id}
import cats.data._
import cats.effect.Effect
import cats.implicits._
import org.http4s.Status

trait BooksDataService[F[_]] {
  def create(book: Book): EitherT[F, Status, Id]
  def fetch(): EitherT[F, Status, Vector[Book.WithId]]
  def fetch(id: Id): EitherT[F, Status, Book.WithId]
  def upsert(id: Id, book: Book): EitherT[F, Status, Unit]
  def update(id: Id, bookUpdate: Book.Partial): EitherT[F, Status, Unit]
  def delete(id: Id): EitherT[F,Status, Unit]
}

class BooksDataServiceImpl[F[_] : Effect](
  dao: BooksDao[F]
) extends BooksDataService[F] {

  override def create(book: Book): EitherT[F, Status, Id] =
    EitherT.right[Status](dao.create(book))

  override def fetch(): EitherT[F, Status, Vector[Book.WithId]] =
    EitherT.right[Status](dao.fetchAll())

  override def fetch(id: Id): EitherT[F, Status, Book.WithId] =
    EitherT(dao.fetch(id).map {
      case None => Left(Status.NotFound)
      case Some(book) => Right(book)
    })

  override def upsert(id: Id, book: Book): EitherT[F, Status, Unit] =
    EitherT.right[Status](dao.upsert(id, book))

  override def update(id: Id, bookUpdate: Book.Partial): EitherT[F, Status, Unit] =
    EitherT(dao.update(id, bookUpdate).map {
      case None => Left(Status.NotFound)
      case Some(()) => Right(())
    })

  override def delete(id: Id): EitherT[F, Status, Unit] =
    EitherT(dao.delete(id).map {
      case None => Left(Status.NotFound)
      case Some(()) => Right(())
    })
}
