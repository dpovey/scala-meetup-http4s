package brisbane.scala.meetup.service

import brisbane.scala.meetup.models.Book
import cats.data.EitherT
import cats.effect.Effect
import org.http4s.Status

trait BooksDataService[F[_]] {
  def create(book: Book): EitherT[F, Status, Int]
  def fetch(): EitherT[F, Status, Vector[Book]]
  def fetch(id: Int): EitherT[F, Status, Book]
  def upsert(id: Int, book: Book): EitherT[F, Status, Unit]
  def update(id: Int, bookUpdate: Book.Partial): EitherT[F, Status, Unit]
  def delete(id: Int): EitherT[F,Status, Unit]
}

class BooksDataServiceImpl[F[_] : Effect] extends BooksDataService[F] {

  override def create(book: Book): EitherT[F, Status, Int] = EitherT.leftT(Status.NotImplemented)

  override def fetch(): EitherT[F, Status, Vector[Book]] = EitherT.leftT(Status.NotImplemented)

  override def fetch(id: Int): EitherT[F, Status, Book] = EitherT.leftT(Status.NotImplemented)

  override def upsert(id: Int, book: Book): EitherT[F, Status, Unit] = EitherT.leftT(Status.NotImplemented)

  override def update(id: Int, bookUpdate: Book.Partial): EitherT[F, Status, Unit] = EitherT.leftT(Status.NotImplemented)

  override def delete(id: Int): EitherT[F, Status, Unit] = EitherT.leftT(Status.NotImplemented)
}
