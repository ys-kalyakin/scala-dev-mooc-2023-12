package catsresources

import cats.MonadError
import cats.data.State
import cats.effect.{IO, IOApp}
import cats.implicits._
import cats.effect.unsafe.implicits.global
import cats.effect.kernel._

import scala.concurrent.duration._

object catsresources {
  def main(args: Array[String]): Unit = {
    // 1. monad error
    val optionF = for {
      a <- Some(3)
      b <- Some(3)
      c <- Some(3)
      d <- Some(3)
    } yield a + b + c + d

    val optionF1 = for {
      a <- Right(3)
      b <- Right(3)
      c <- Left("error")
      d <- Right(3)
    } yield a + b + c + d

    type MyMonadError[F[_]] = MonadError[F, String]
    def withErrorHandling[F[_]: MyMonadError] = for {
      a <- MonadError[F, String].pure(10)
      b <- MonadError[F, String].pure(10)
      c <- MonadError[F, String].pure(10)
      d <- MonadError[F, String].pure(10)
    } yield (a+b+c+d)

    type StringError[A] = Either[String, A]
    println(withErrorHandling[StringError])

    //теперь как кинуть ошибку
    def withErrorHandling1[F[_]: MyMonadError]: F[Int] = for {
      a <- MonadError[F, String].pure(10)
      b <- MonadError[F, String].pure(10)
      c <- MonadError[F, String].raiseError[Int]("fail")
      d <- MonadError[F, String].pure(10)
    }yield (a+b+c+d)
    println(withErrorHandling1[StringError])

    // Monad Error функциональный аналог try catch

    println(withErrorHandling1.handleError(error => 42))

    //теперь в общем виде
    def withErrorHandling2[F[_]: MyMonadError] = for {
      a <- MonadError[F, String].pure(10)
      b <- MonadError[F, String].pure(10)
      c <- MonadError[F, String].raiseError[Int]("fail").handleError(error => 42)
      d <- MonadError[F, String].pure(10)
    } yield (a + b + c + d)
    println(withErrorHandling2)

    //2 метод attempt
    def withErrorAttempt[F[_]: MyMonadError]: F[Either[String, Int]] =
      withErrorHandling1[F].attempt
    println(withErrorAttempt)

    // *>
    val failing = IO.raiseError(new Exception("fail"))
    failing *> IO.println("sdf")

    val a = failing.attempt *> IO.println("sdf")

    a.unsafeRunSync()

    //monadcancel
    val justSleep = IO.sleep(1.second) *> IO.println("not cancelled")
    val justSleepAndThrow = IO.sleep(100.millis) *> IO.raiseError(new Error("error"))
    //(justSleep, justSleepAndThrow).parTupled.unsafeRunSync()
    val justSleepAndThrowUncancellable = (IO.sleep(1.second) *> IO.println("not cancelled")).uncancelable
    (justSleepAndThrow, justSleepAndThrowUncancellable).parTupled.unsafeRunSync()
 }
}

object SpawnApp extends IOApp.Simple {
  def longRunningIO(): IO[Unit] =
    (
      IO.sleep(200.millis) *> IO.println(s"hi from thread ${Thread.currentThread()}").iterateWhile( _ => true)
    )


  def longRunningIORef(r: Ref[IO, Int]): IO[Unit] =
    (
      IO.sleep(200.millis) *> IO.println(s"hi from thread ${Thread.currentThread()}").iterateWhile( _ => true)
      )
/*
  def run: IO[Unit] = for {
    r <- Ref.of[IO, Int](10)
    fiber1 <- Spawn[IO].start(longRunningIORef(r))
    fiber2 <- Spawn[IO].start(longRunningIO)
    fiber3 <- Spawn[IO].start(longRunningIO)
    _ <- IO.println("the fibers has been started")
    _ <- IO.sleep(2.second)
    _ <- fiber1.cancel
    _ <- fiber2.cancel
    _ <- IO.sleep(3.second)
  } yield()*/

  def run: IO[Unit] = for {
    fiber <- Spawn[IO].start(longRunningIO)
    _ <- IO.println("the fiber has been started")
    _ <- IO.sleep(1.second)
  } yield ()
}