import module1.threads.{Thread1, getRatesLocation1, getRatesLocation2, getRatesLocation3, getRatesLocation4, getRatesLocation5, getRatesLocation6, getRatesLocation7, getRatesLocation8, printRunningTime}
import module1.{future, hof, list, threads, type_system}
import module2.implicits.implicit_scopes
import module3.functional_effects.functionalProgram
import module3.functional_effects.functionalProgram.executableEncoding
import module3.{multipleErrors, toyModel, zioConcurrency, zioConstructors, zioRecursion}
import zio.{ExitCode, URIO, ZIO}

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


object Main {

  def main(args: Array[String]): Unit = {
      println(s"Hello from ${Thread.currentThread().getName}")
//      val t1 = new Thread1
//      val t2 = new Thread1
//      t1.start()
//      t1.join()
//      t2.start()

//      def rates = {
//        val tf1 = getRatesLocation7
//        val tf2 = getRatesLocation8
//        val tf3: threads.ToyFuture[Int] = for{
//            v1 <- tf1
//            v2 <- tf2
//        } yield v1 + v2
//
//
//        tf1.onComplete{ i1 =>
//          tf2.onComplete{ i2 =>
//            println(i1 + i2)
//          }
//        }
//      }

//      def rates = {
//        future.getRatesLocation1
//        future.getRatesLocation2
//      }

//    future.ratesSum.foreach{ t =>
//      println(t)
//    }(ExecutionContext.global)

//    future.ratesSum.onComplete {
//      case Failure(exception) =>
//        println(exception.getMessage)
//      case Success(value) =>
//        println(value)
//    }(ExecutionContext.global)

//    future.f03

//    val c: executableEncoding.Console[Unit] = functionalProgram.executableEncoding.greet.flatMap(_ =>
//    functionalProgram.executableEncoding.askForAge)
//    c.run()

//    toyModel.echo.run()

     //println(zioRecursion.factorial(10000))
     zio.Runtime.default.unsafeRun(
       zioConcurrency.printEffectRunningTime(zioConcurrency.p3)
     )
  }
}

object ZIOApp extends zio.App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    zioConstructors.z1.exitCode
}