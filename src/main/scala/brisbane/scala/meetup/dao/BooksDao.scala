package brisbane.scala.meetup.dao

import brisbane.scala.meetup.models.{Book, Id}
import cats.implicits._
import cats.effect.Effect
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import doobie.util.update.Update0

trait BooksDao[F[_]] {
  def create(book: Book): F[Id]
  def fetchAll(): F[Vector[Book.WithId]]
  def fetch(id: Id): F[Option[Book.WithId]]
  def upsert(id: Id, book: Book): F[Unit]
  def update(id: Id, bookUpdate: Book.Partial): F[Option[Unit]]
  def delete(id: Id): F[Option[Unit]]
}

class DoobieBooksDao[F[_] : Effect](
  val xa: Transactor[F]
) extends BooksDao[F] {

  val queries = DoobieBooksDao.queries

  override def create(book: Book): F[Id] = queries.create(book).unique.transact(xa)

  override def fetchAll(): F[Vector[Book.WithId]] = queries.fetch().to[Vector].transact(xa)

  override def fetch(id: Id): F[Option[Book.WithId]] = queries.fetch(id).option.transact(xa)

  override def upsert(id: Id, book: Book): F[Unit] =
    queries.upsert(Book.withId(id, book)).run.transact(xa).map(_ => ())

  override def update(id: Id, bookUpdate: Book.Partial): F[Option[Unit]] =
    queries.update(id, bookUpdate) match {
      case None => Effect[F].pure(None)
      case Some(q) => q.run.map {
          case 1 => Option(())
          case _ => None
        }.transact(xa)
      }

  override def delete(id: Id): F[Option[Unit]] =
    queries.delete(id).run.map {
      case 1 => Option(())
      case _ => None
    }.transact(xa)
}

object DoobieBooksDao {
  object queries {

    def create(book: Book): Query0[Id] =
      sql"""
           |INSERT INTO books
           |(title, author, dewey_decimal_class, library_of_congress, pages)
           |VALUES
           |(${book.title}, ${book.author}, ${book.deweyDecimalClass}, ${book.libraryOfCongress}, ${book.pages})
           |RETURNING id
         """.stripMargin.query

    def fetch(): Query0[Book.WithId] =
      sql"SELECT id, title, author, dewey_decimal_class, library_of_congress, pages FROM books".query

    def fetch(id: Id): Query0[Book.WithId] =
      sql"""SELECT id, title, author, dewey_decimal_class, library_of_congress, pages
           |FROM books
           |WHERE id = $id
           |""".stripMargin.query

    def upsert(book: Book.WithId): Update0 = {
      sql"""INSERT INTO books
           |(id,  title, author, dewey_decimal_class, library_of_congress, pages)
           |VALUES
           |(${book.id}, ${book.title}, ${book.author}, ${book.deweyDecimalClass}, ${book.libraryOfCongress}, ${book.pages})
           |ON CONFLICT (id) DO UPDATE SET
           |  title = EXCLUDED.title,
           |  author = EXCLUDED.author,
           |  dewey_decimal_class = EXCLUDED.dewey_decimal_class,
           |  library_of_congress = EXCLUDED.library_of_congress,
           |  pages = EXCLUDED.pages
        """.stripMargin.update
    }

    def update(id: Id, book: Book.Partial): Option[Update0] = {
      val setFragments = List(
        book.title.map(title => fr"title = $title"),
        book.author.map(author => fr"author = $author"),
        book.deweyDecimalClass.map(ddc => fr"dewey_decimal_class = $ddc"),
        book.libraryOfCongress.map(loc => fr"library_of_congress = $loc"),
        book.pages.map(pages => fr"pages = $pages")
      )

      Option(doobie.util.fragments.setOpt(setFragments: _*))
        .filter(_ != Fragment.empty)
        .map { setFragment =>
          val f: Fragment = fr"""
            UPDATE books """ ++ setFragment ++ fr"""
            WHERE id = $id
          """
          f.update
        }
    }

    def delete(id: Id): Update0 = {
      sql"DELETE FROM books WHERE id = $id".update
    }
  }

}