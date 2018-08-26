package brisbane.scala.meetup

import brisbane.scala.meetup.service.BooksDataServiceImpl
import cats.effect.{Effect, IO}
import fs2.StreamApp
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

object LibraryServer extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]) = ServerStream.stream[IO]
}

object ServerStream {

  def booksDataService[F[_]: Effect] = new BooksDataServiceImpl[F]

  def libraryService[F[_]: Effect] = new LibraryService[F](booksDataService).service

  def stream[F[_]: Effect](implicit ec: ExecutionContext) =
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(libraryService, "/")
      .serve
}
