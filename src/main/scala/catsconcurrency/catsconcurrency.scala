package catsconcurrency

import cats.effect._
import cats.implicits.catsSyntaxTuple2Parallel

import scala.util.Try

object RefExample extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      ref <- Ref.of[IO, Int](0)
      _ <- ref.update(_ + 1)
      value <- ref.get
      _ <- IO.delay(println(s"Value: $value"))

    } yield ExitCode.Success
}

object DeferredExample extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      deferred <- Deferred[IO, Int]
      _ <- (IO.delay(Thread.sleep(2000)) *> deferred.complete(42)).start
      value <- deferred.get
      _ <- IO.delay(println(s"Received value $value"))
    } yield ExitCode.Success
}

sealed trait Command extends Product with Serializable

object Command {
  case object Echo extends Command

  case object Exit extends Command

  case class AddNumber(num: Int) extends Command

  case object ReadNumber extends Command

  case class LaunchDog(name: String) extends Command

  case object ReleaseTheDogs extends Command

  def parse(s: String): Either[String, Command] =
    s.toLowerCase match {
      case "echo" => Right(Echo)
      case "exit" => Right(Exit)
      case "dogs" => Right(ReleaseTheDogs)
      case "read-number" => Right(ReadNumber)
      case cmd =>
        cmd.split(" ").toList match {
          case List("l", dogName) =>
            Right(LaunchDog(dogName))
          case List("n", IntString(num)) =>
            Right(AddNumber(num))
          case _ => Left("not valid command")
        }
    }

  private object IntString {
    def unapply(s: String): Option[Int] =
      Try(s.toInt).toOption
  }
}

// without environment pattern
/*
object MainCatsConcurrency extends IOApp.Simple {
  def program(counter: Ref[IO, Int]): IO[Unit] = iteration(counter).iterateWhile(a=>a).void
  def iteration(counter: Ref[IO, Int]): IO[Boolean] = for {
    cmd <- IO.print("> ") *> IO.readLine
    shouldProceed <- Command.parse(cmd) match {
      case Left(err) => IO.raiseError(new Exception(s"Invalid command: $err"))
      case Right(command) => process(counter)(command)
    }

  } yield shouldProceed

/*  def program: IO[Unit] = for {
    cmd <- IO.readLine
    _ <- Command.parse(cmd) match {
      case Left(err) => IO.raiseError(new Exception(s"Invalid command: $err"))
      case Right(command) => process(command).flatMap{
        case true => program
        case false => IO.unit
      }
    }
  } yield ()
*/
  def process(counter: Ref[IO, Int])(command: Command): IO[Boolean] =
    command match {
      case Command.Echo => {
        IO.readLine.flatMap(text => IO.println(text)).as(true)
      }
      case Command.Exit => {
        IO.println("Bye Bye").as(false)
      }
      case Command.AddNumber(num) => counter.updateAndGet(_+num).flatMap(IO.println).as(true)
      case Command.ReadNumber => counter.get.flatMap(IO.println).as(true)
      case Command.LaunchDog(name) => ???
      case Command.ReleaseTheDogs => ???
    }

  def run: IO[Unit] = for {
    counter <- Ref.of[IO, Int](0)
    _ <- program(counter)
  } yield ()

}*/

//2 with environment pattern
object MainCatsConcurrency extends IOApp.Simple {
  final case class Environment(counter: Ref[IO, Int], startGun: Deferred[IO, Unit])
  def buildEnv: Resource[IO, Environment] =  (
    Resource.make(IO.println("Alloc. counter") *> Ref.of[IO, Int](0))(_=>
      IO.println("Dealloc. counter")
    ), Resource.make(IO.println("Alloc. gun") *> Deferred[IO, Unit])(_=>
    IO.println("Dealloc. gun"))
  ).parMapN {case (counter, gun) => Environment(counter, gun)}




  def program(env: Environment): IO[Unit] = iteration(env).iterateWhile(a=>a).void
  def iteration(env: Environment): IO[Boolean] = for {
    cmd <- IO.print("> ") *> IO.readLine
    shouldProceed <- Command.parse(cmd) match {
      case Left(err) => IO.raiseError(new Exception(s"Invalid command: $err"))
      case Right(command) => process(env)(command)
    }

  } yield shouldProceed

  def process(env: Environment)(command: Command): IO[Boolean] =
    command match {
      case Command.Echo => {
        IO.readLine.flatMap(text => IO.println(text)).as(true)
      }
      case Command.Exit => {
        IO.println("Bye Bye").as(false)
      }
      case Command.AddNumber(num) => env.counter.updateAndGet(_+num).flatMap(IO.println).as(true)
      case Command.ReadNumber => env.counter.get.flatMap(IO.println).as(true)
      case Command.LaunchDog(name) =>
        val fiberIO = (IO.println(s"Dog $name is ready") *> env.startGun.get *>
          IO.println(s"Dog $name is starting") *> env.counter.updateAndGet(_+1)
          .flatMap(value => IO.println(s"Dog $name observe value $value")))
        fiberIO.start.as(true)
      case Command.ReleaseTheDogs => env.startGun.complete()
    }

  def run: IO[Unit] =
    buildEnv.use(env => program(env))

}