package module3

import scala.io.StdIn

object functional_effects {


  object simpleProgram {

    val greet = {
      println("Как тебя зовут?")
      val name = StdIn.readLine()
      println(s"Привет, $name")
    }

    val askForAge = {
      println("Сколько тебе лет?")
      val age = StdIn.readInt()
      if (age > 18) println("Можешь проходить")
      else println("Ты еще не можешь пройти")
    }


    def greetAndAskForAge = ???


  }


  object functionalProgram {

    /**
     * Executable encoding and Declarative encoding
     */

    object executableEncoding {

      /**
       * 1. Объявить исполняемую модель Console
       */
      case class Console[A](run: () => A) {

        def map[B](f: A => B): Console[B] =
          flatMap(a => Console.succeed(f(a)))

        def flatMap[B](f: A => Console[B]): Console[B] =
          Console.succeed(f(this.run()).run())
      }


      /**
       * 2. Объявить конструкторы
       */
      object Console {
        def succeed[A](a: => A): Console[A] = Console(() => a)

        def printLine(str: String): Console[Unit] = Console(() => println(str))

        def readLine: Console[String] = Console(() => StdIn.readLine())
      }


      /**
       * 3. Описать желаемую программу с помощью нашей модель
       */

      //      val greet = {
      //        println("Как тебя зовут?")
      //        val name = StdIn.readLine()
      //        println(s"Привет, $name")
      //      }

      //        val greet: Console[Unit] = {
      //            Console.printLine("Как тебя зовут?")
      //            val name: Console[String] = Console.readLine
      //            Console.printLine(s"Привет, $name")
      //        }

        val greet: Console[Unit] = for {
          _ <- Console.printLine("Как тебя зовут?")
          name <- Console.readLine
          _ <- Console.printLine(s"Привет, $name")
        } yield ()

      val askForAge: Console[Unit] = for{
        _ <- Console.printLine("Сколько тебе лет?")
        age <- Console.readLine
        _ <- if (age.toInt > 18) Console.printLine("Можешь проходить")
        else Console.printLine("Ты еще не можешь пройти")
      } yield ()
    }





    object declarativeEncoding {

      /**
       * 1. Объявить декларативную модель Console
       */
      sealed trait Console[A]{

//        def flatMap[B](f: A => Console[B]): Console[B] = this match {
//          case PrintLine(str, rest) => PrintLine(str, rest.flatMap(f))
//          case ReadLine(ff) => ReadLine(str => ff(str).flatMap(f))
//          case Succeed(v) => f(v())
//        }

        def flatMap[B](f: A => Console[B]): Console[B] = FlatMap(this, f)
        def map[B](f: A => B): Console[B] = flatMap(a => Succeed(() => f(a)))
      }

      case class PrintLine[A](str: String, rest: Console[A]) extends Console[A]
      case class ReadLine[A](f: String => Console[A]) extends Console[A]
      case class Succeed[A](v: () => A) extends Console[A]
      case class FlatMap[A, B](a: Console[A], f: A => Console[B]) extends Console[B]



      /**
       * 2. Написать конструкторы
       * 
       */

        object Console{
          def succeed[A](v: => A): Console[A] = Succeed(() => v)
          def printLine(str: String): Console[Unit] = PrintLine(str, succeed(()))
          def readLine: Console[String] = ReadLine(str => succeed(str))
        }

      //      val greet = {
      //        println("Как тебя зовут?")
      //        val name = StdIn.readLine()
      //        println(s"Привет, $name")
      //      }

      val greet: Console[Unit] = {
        PrintLine("Как тебя зовут?",
          ReadLine(name =>
            PrintLine(s"Привет, $name", Succeed(() => ()))))
      }

      val greet2: Console[Unit] = for{
        _ <- Console.printLine("Как тебя зовут?")
        name <- Console.readLine
        _ <- Console.printLine(s"Привет, $name")
      } yield ()


      /**
       * 3. Описать желаемую программу с помощью нашей модели
       */




      /**
       * 4. Написать операторы
       *
       */

      /**
       * 5. Написать интерпретатор для нашей функциональной модели
       *
       */

      def interpret[A](console: Console[A]): A = console match {
        case PrintLine(str, rest) =>
          println(str)
          interpret(rest)
        case ReadLine(f) =>
          interpret(f(StdIn.readLine()))
        case FlatMap(a, f) =>
          interpret(f(interpret((a))))
        case Succeed(v) => v()
      }



      /**
       * Реализуем туже программу что и в случае с исполняемым подходом
       */

    }

  }

}