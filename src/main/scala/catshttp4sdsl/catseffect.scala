package catshttp4sdsl

import cats.data.{EitherT, OptionT, ReaderT}
import cats.effect.IO
import cats.effect.unsafe.implicits.global

object catshttp4sdsl {
  // 1.
  def getUserName: IO[Option[String]] = IO.pure(Some("ssdf"))
  def getId(name: String): IO[Option[Int]] = IO.pure(Some(1))
  def getPermission(id: Int): IO[Option[String]] = IO.pure(Some("Permissions"))


  def main(args: Array[String]): Unit = {
    implicit  val ec = scala.concurrent.ExecutionContext.Implicits.global
    //1.
    val res: OptionT[IO, (String, Int, String)] = for {
      username <- OptionT(getUserName)
      id <- OptionT(getId(username))
      permissions <- OptionT(getPermission(id))
    } yield (username, id, permissions)

    //println(res.value.unsafeRunSync())
    //2.
    def getUserName1: IO[Option[String]] = IO.pure(Some("ssdf"))
    def getId1(name: String): IO[Int] = IO.pure(1)
    def getPermission1(id: Int): IO[Option[String]] = IO.pure(Some("Permissions"))
    val res1 = for {
      username <- OptionT(getUserName1)
      id <- OptionT.liftF(getId1(username))
      permissions <- OptionT(getPermission1(id))
    } yield (username, id, permissions)
//    println(res1.value.unsafeRunSync())

    // EitherT
    sealed trait UserServiceError
    case class PermissionDenied(msg: String) extends  UserServiceError
    case class UserNotFound(userId: Int) extends UserServiceError
    def getUserName2(userId: Int): EitherT[IO, UserServiceError, String] = EitherT.pure("test")
    def getUserAddress(userId: Int): EitherT[IO, UserServiceError, String] =
      EitherT.fromEither(Right("bla bla bla"))

    def getProfile(id: Int) = for {
      name <- getUserName2(id)
      address <- getUserAddress(id)
    } yield (name, address)
    println(getProfile(2).value.unsafeRunSync())

    trait ConnectionPool
    case class Environment(cp: ConnectionPool)
    def getUserAlias(id: Int): ReaderT[IO, Environment, String] = ReaderT(cp=>IO.pure("111"))
    def getComment(id:Int): ReaderT[IO, Environment, String] = ReaderT.liftF(IO.pure("222"))
    def updateComment(id: Int, text: String): ReaderT[IO, Environment, Unit] = ReaderT.liftF(IO.println("updated"))

    val result = for {
      a <- getUserAlias(1)
      b <- getComment(1)
      _ <- updateComment(1, "bla bla bla")
    } yield (a,b)
    println(result(Environment(new ConnectionPool {})).unsafeRunSync())


  }
}