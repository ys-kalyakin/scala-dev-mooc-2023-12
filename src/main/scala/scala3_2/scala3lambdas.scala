package scala3_2

object  mainLabda {
  type MyTry = [X] =>> Either[Throwable, X]

  @main def mainLabdaEx(): Unit ={
    val myTryInt: MyTry[Int] = Right(10)
    val myTryString: MyTry[String] = Right("sdfg")
    val myTryLeft: MyTry[Int] = Left(Exception("error"))

    println(myTryInt)
    println(myTryString)
    println(myTryLeft)
  }
}