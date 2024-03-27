package catstypes

import cats.data.{Chain, Ior, Kleisli, NonEmptyChain, NonEmptyList, OptionT, Validated, ValidatedNec}
import cats.implicits._

import scala.concurrent.Future
import scala.util.Try


object dataStructure {
  //Chain
  val empty: Chain[Int] = Chain[Int]()
  val empty2: Chain[Int] = Chain.empty[Int]

  val ch2 = Chain(1)
  val ch3 = Chain.one(1)
  val ch4 = Chain.fromSeq(1 :: 2 :: 3 :: 4 :: Nil)

  //operators
  val ch5 = ch2 :+ 2 // append, const time
  val ch6 = 3 +: ch2 // prepend, const time
  val r = ch2.headOption
  ch3.map(_+1)
  ch3.flatMap(x=>Chain.one(x+1))

  // nonemptychain
  val nec = NonEmptyChain(1)
  val nec1 = NonEmptyChain.one(1)
  val nec2: Option[NonEmptyChain[Int]] = NonEmptyChain.fromSeq(1::2::3::Nil)
  val r2 = nec.head

  // NonEmptyList
  val nel1 = NonEmptyList(1, List())
  val nel2 = NonEmptyList.one(1)
  val nel3: Option[NonEmptyList[Int]] = NonEmptyList.fromList(1::Nil)
}

object validation {
  type EmailValidationError = String
  type NameValidationError = String
  type AgeValidationError = String

  type Name = String
  type Email = String
  type Age = Int

  case class UserDTO(email: String, name: String, age: Int)
  case class User(email: String, name: String, age: Int)

  def emailValidationE: Either[EmailValidationError, Email] = Left("Not valid email")
  def userNameValidationE: Either[NameValidationError, Name] = Left("Not valid name")
  def ageValidationE: Either[AgeValidationError, Age] = Right(30)

  def validateUserDataE(userDto: UserDTO): Either[String, User] = for {
    email <- emailValidationE
    name <- userNameValidationE
    age <- ageValidationE
  } yield User(email, name, age)

  // cats validation
  val v1 = Validated.invalid[String, String]("sdf")
  val v2 = Validated.valid[String, String]("wsef")
  def emailValidationV: Validated[EmailValidationError, Email] = "email not valid".invalid[String]
  def userNameValidationV: Validated[NameValidationError, Name] = "name not valid".invalid[String]
  def userAgeValidationV: Validated[AgeValidationError, Age] = 30.valid[String]
  /*
  def validateUserDataV(userDto: UserDTO): Validated[String, User] = for {
    email <- emailValidationV
    name <- userNameValidationV
    age <- userAgeValidationV
  } yield User(email, name, age)
  */
  def validateUserDataV(userDto: UserDTO): Validated[String, String] =
    emailValidationV combine userNameValidationV combine userAgeValidationV.map(_.toString)

  //improvement
  def validateUserDataV2(userDto: UserDTO): ValidatedNec[String, User] = (
    emailValidationV.toValidatedNec,
    userNameValidationV.toValidatedNec,
    userAgeValidationV.toValidatedNec
  ).mapN {
    (email, name, age) =>
      User(email, name, age)
  }

  // Ior
  val u: User = User("a", "b", 30)
  lazy val ior: Ior[String, User] = Ior.left("")
  lazy val ior1: Ior[String, User] = Ior.right(u)
  lazy val ior2: Ior[String, User] = Ior.both("warning", u)

  def emailValidationI: Ior[String, String] = Ior.both("email ???", "asd@azcf.de")
  def userNameValidationI: Ior[String, String] = Ior.both("name ???", "mustermann")
  def userAgeValitionI:Ior[String, Int] = 30.rightIor[String]

  def validateUserDataI(userDto: UserDTO): Ior[String, User] = for {
    email <- emailValidationI
    name <- userNameValidationI
    age <- userAgeValitionI
  } yield User(email, name, age)

  def validateUserDataI2(userDto: UserDTO): Ior[NonEmptyChain[String], User] = for {
    email <- emailValidationI.toIorNec
    name <- userNameValidationI.toIorNec
    age <- userAgeValitionI.toIorNec
  } yield User(email, name, age)


  def main(args: Array[String]): Unit = {
    //println(validateUserDataE(UserDTO("","",20)))
    //println(validateUserDataV(UserDTO("","",20)))
    //println(validateUserDataV2(UserDTO("","",20)))
//    println(validateUserDataI(UserDTO("","",20)))
    println(validateUserDataI2(UserDTO("","",20)))
  }
}

// kleisli
object KleisliTest{
  val f1: Int => String = i => i.toString
  val f2: String => String = s => s+"saef"
  val f3: Int => String = f1 andThen f2

  val f4: String => Option[Int] = _.toIntOption
  val f5: Int => Option[Int] = i => Try(10/i).toOption

  val f6: Kleisli[Option, String, Int] = Kleisli(f4) andThen Kleisli(f5)
  val _f6: String => Option[Int] = f6.run
}

//transformers
object transformers{
  val f1: Future[Int] = Future.successful(2)
  def f2(i:Int): Future[Option[Int]] = Future.successful(Try(10/i).toOption)
  def f3(i:Int): Future[Option[Int]] = Future.successful(Try(10/i).toOption)
  import scala.concurrent.ExecutionContext.Implicits.global
/*  val r = for {
    i1 <- f1 //Int
    i2 <- f2(i1)  //option[int]
    i3 <- f3(i1)  //option[int]
  } yield i2+i3
*/
  val r: OptionT[Future, Int] = for {
    i1 <- OptionT.liftF(f1)
    i2 <- OptionT(f2(i1))
    i3 <- OptionT(f3(i1))
  } yield i2 + i3

  val cd: Future[Option[Int]] = r.value

}
