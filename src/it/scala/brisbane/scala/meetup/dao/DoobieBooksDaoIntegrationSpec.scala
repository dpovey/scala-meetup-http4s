package brisbane.scala.meetup.dao

import java.util.UUID

import brisbane.scala.meetup.models.{Book, Id}
import cats.effect.IO
import doobie.specs2.IOChecker
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import org.specs2.execute.{AsResult, Failure, Result, Success}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class DoobieBooksDaoIntegrationSpec extends Specification with IOChecker {

  implicit val asyncResult = new AsResult[IO[Unit]] {
    override def asResult(t: => IO[Unit]): Result = {
      t.attempt.unsafeRunSync() match {
        case Left(err) => Failure(e = err.getMessage, stackTrace = err.getStackTrace.toList)
        case Right(_) => Success()
      }
    }
  }

  override val transactor: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/meetup", "postgres", ""
  )

  private val queries = DoobieBooksDao.queries

  private val book = Book(
    "Fear and Loathing in Las Vegas",
    "Hunter S. Thompson",
    "917.93/13, B",
    "PN4874.T444 A3 1971",
    206)

  private val id: Id = Id(UUID.randomUUID())

  private val bookWithID = Book.withId(id, book)

  "type check queries" >> {
    check(queries.create(book))
    check(queries.fetch(id))
    check(queries.fetch())
    check(queries.upsert(bookWithID))
    check(queries.update(id, Book.Partial(title = Some("title"))).get)
  }

  class ctx extends Scope {
    val dao = new DoobieBooksDao[IO](transactor)
    val secondEdition = "Fear and Loathing in Las Vegas. 2nd Ed."
    val unpunctuatedAuthor = "Hunter S Thompson"
  }


  "DoobieBooksDao" should {
    "Create, Read, Update and Delete Books" in new ctx {
      for {
        id       <- dao.create(book)
        aBook    <- dao.fetch(id)
        allBooks <- dao.fetchAll()
        _        <- aBook match {
                      case None => IO.raiseError(new AssertionError("Expected value for aBook"))
                      case Some(b) => dao.upsert(b.id, b.book.copy(title = secondEdition))
                    }
        _        <- dao.update(id, Book.Partial(author = Some(unpunctuatedAuthor)))
        again    <- dao.fetch(id)
      } yield {
        assert(aBook.contains(Book.withId(id, book)))
        assert(allBooks == Seq(Book.withId(id, book)))
        assert(again.map(_.book).contains(book.copy(title = secondEdition, author = unpunctuatedAuthor)))
      }
    }
  }
}
