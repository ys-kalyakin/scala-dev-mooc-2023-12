package module1

import scala.annotation.tailrec
import scala.language.postfixOps



/**
 * referential transparency
 */


 object referential_transparency{

  case class Abiturient(id: String, email: String, fio: String)

  type Html = String

  sealed trait Notification

  object Notification{
    case class Email(email: String, text: Html) extends Notification
    case class Sms(telephone: String, msg: String) extends Notification
  }


  case class AbiturientDTO(email: String, fio: String, password: String)

  trait NotificationService{
    def sendNotification(notification: Notification): Unit
    def createNotification(abiturient: Abiturient): Notification
  }


  trait AbiturientService{

    def registerAbiturient(abiturientDTO: AbiturientDTO): Abiturient
  }

}


 // recursion

object recursion {

  /**
   * Реализовать метод вычисления n!
   * n! = 1 * 2 * ... n
   */

  def fact(n: Int): Int = {
    var _n = 1
    var i = 2
    while (i <= n){
      _n *= i
      i += 1
    }
    _n
  }

  def factRec(n: Int): Int =
    if(n == 0) 1
    else n * factRec(n - 1)




  def tailRec(n: Int): Int = {
    @tailrec
    def loop(i: Int, accum: Int): Int =
      if(i == 0) accum else loop(i - 1, n * accum)
    loop(n, 1)
  }




  /**
   * реализовать вычисление N числа Фибоначчи
   * F0 = 0, F1 = 1, Fn = Fn-1 + Fn - 2
   *
   */


}

object hof{


  // обертки

  def logRunningTime[A, B](f: A => B): A => B = a => {
    val start = System.currentTimeMillis()
    val result: B = f(a)
    val end = System.currentTimeMillis()
    println(s"Running time: ${end - start}")
    result
  }

  def doomy(str: String) = {
    Thread.sleep(1000)
    println(str)
  }

  val r1: String => Unit = logRunningTime(doomy)

  r1("Hello")



  // изменение поведения ф-ции

  def isOdd(i: Int): Boolean = i % 2 > 0

  def not[A](f: A => Boolean): A => Boolean = a => !f(a)

  val isEven: Int => Boolean = not(isOdd)

  println(isOdd(3))
  println(isEven(2))

  var i = 1

  def incr(f: Int => Int): Int => Int =
    a => {
      i = 5
      f(i + 2)
    }

  // изменение самой функции

  def partial[A, B, C](a: A, f: (A, B) => C): B => C =
    b => f(a, b)

  def partial2[A, B, C](a: A, f: (A, B) => C): B => C =
    f.curried(a)

  def sum(x: Int, y: Int): Int = x + y

  val p: Int => Int = partial(2, sum)

  p(2) // 4
  p(3) // 5
  p(4) // 6
















  trait Consumer{
       def subscribe(topic: String): LazyList[Record]
   }

   case class Record(value: String)

   case class Request()

   object Request {
       def parse(str: String): Request = ???
   }

  /**
   *
   * (Опционально) Реализовать ф-цию, которая будет читать записи Request из топика,
   * и сохранять их в базу
   */
   def createRequestSubscription() = ???



}






/**
 *  Реализуем тип Option
 */



 object opt {


  class Animal
  class Dog extends Animal

  /**
   *
   * Реализовать структуру данных Option, который будет указывать на присутствие либо отсутсвие результата
   */

  // 1. invariance
  // 2. covariance  A <- B  Option[A] <- Option[B]
  // 3. contravariance A <- B Option[A] -> Option[B]
  sealed trait Option[+T]{
      def isEmpty: Boolean = this match {
          case Some(v) => false
          case None => true
      }

      def get: T = this match {
          case Some(v) => v
          case None => throw new Exception("get on empty option")
      }


      def map[B](f: T => B): Option[B] = flatMap(t => Option(f(t)))

      def flatMap[B](f: T => Option[B]): Option[B] = this match {
          case Some(v) => f(v)
          case None => None
      }

      def printIfAny(): Unit = this match {
          case Some(v) => println(v)
          case None =>
      }

      def zip[B](other : Option[B]) : Option[(T, B)] = {
          if (!isEmpty && !other.isEmpty) {
              Some(get, other.get)
          } else None
      }

      def filter(predicate : T => Boolean) : Option[T] = this match {
          case Some(v) => if (predicate(v)) Some(v) else None
          case None => None
      }
  }

    case class Some[V](v: V) extends Option[V]
    case object None extends Option[Nothing]   // Any <- Dog

    object Option{
        def apply[T](v: T): Option[T] =
            if(v == null) None
            else Some(v)
    }

    val o1: Option[Int] = Option(1)
    o1.isEmpty // false









    /**
     *
     * Реализовать метод printIfAny, который будет печатать значение, если оно есть
     */
    val o2 = None
    o2.printIfAny()
    val o3 = Option(2)
    o3.printIfAny()

    /**
     *
     * Реализовать метод zip, который будет создавать Option от пары значений из 2-х Option
     */
    val z1: Option[Int] = Option(1)
    val z2: Option[String] = Option("2")
    val z3: Option[(Int, String)] = z1.zip(z2)
    println(z3)

    /**
     *
     * Реализовать метод filter, который будет возвращать не пустой Option
     * в случае если исходный не пуст и предикат от значения = true
     */

    val f1: Option[Int] = Option(1)
    println(f1.filter(v => v > 2))
    val f2: Option[Int] = None
    println(f2.filter(v => v < 3))
 }

object list {
    /**
     *
     * Реализовать односвязанный иммутабельный список List
     * Список имеет два случая:
     * Nil - пустой список
     * Cons - непустой, содержит первый элемент (голову) и хвост (оставшийся список)
     */

    sealed trait List[+T]{
        def ::[TT >: T](elem: TT): List[TT] = List.::(elem, this)

        def mkString[TT >: T](delimiter: String): String = innerMkString(new StringBuilder(), delimiter, this)

        @tailrec
        private def innerMkString[TT >: T](accumulator: StringBuilder, delimiter: String, lst: List[TT]): String = lst match {
            case List.::(head, List.Nil) =>
                accumulator.append(head)
                accumulator.toString()
            case List.::(head, tail) =>
                accumulator.append(head).append(delimiter)
                innerMkString(accumulator, delimiter, tail)
        }

        def reverse() : List[T] = {
            this match {
                case List.Nil => List.Nil
                case List.::(head, tail) =>
                    @tailrec
                    def reverseInner(reversedList: List[T], lst: List[T]): List[T] = lst match {
                        case List.::(head, List.Nil) => head :: reversedList
                        case List.::(head, tail) =>
                            reverseInner(head :: reversedList, tail)
                    }

                    reverseInner(head :: List.Nil, tail)
            }
        }

        def map[T1](f: T => T1): List[T1] = this match {
            case List.Nil => List.Nil
            case List.::(head, tail) => {
                @tailrec
                def mapInner(mappedList: List[T1], lst: List[T]): List[T1] = lst match {
                    case List.::(head, List.Nil) => f(head) :: mappedList
                    case List.::(head, tail) =>
                        mapInner(f(head) :: mappedList, tail)
                }
                mapInner(List(f(head)), tail).reverse()
            }
        }

        def filter(predicate: T => Boolean) : List[T] = this match {
            case List.Nil => List.Nil
            case List.::(head, tail) => {
                def filterElement(filteredList: List[T], element: T) = if (predicate(element)) element :: filteredList else filteredList
                @tailrec
                def filterInner(filteredList: List[T], lst: List[T]): List[T] = lst match {
                    case List.::(head, List.Nil) => filterElement(filteredList, head)
                    case List.::(head, tail) => filterInner(filterElement(filteredList, head), tail)
                }
                filterInner(filterElement(List.Nil, head), tail).reverse()
            }
        }
    }

    object List{
        case class ::[A](head: A, tail: List[A]) extends List[A]
        case object Nil extends List[Nothing]

        def apply[A](v: A*): List[A] = if(v.isEmpty) List.Nil else new ::(v.head, apply(v.tail:_*))
    }

    val l1: List[Int] = List(1, 2, 3)
    val l2: List[Int] = 1 :: 2 :: 3 :: List.Nil



    /**
     * Метод cons, добавляет элемент в голову списка, для этого метода можно воспользоваться названием `::`
     *
     */
    val c1: List[Int] = List(1)
    println(c1.::(2))

    /**
     * Метод mkString возвращает строковое представление списка, с учетом переданного разделителя
     *
     */
    val ms1: List[Int] = 1 :: 2 :: 3 :: List.Nil
    println(ms1.mkString(":"))

    /**
     * Конструктор, позволяющий создать список из N - го числа аргументов
     * Для этого можно воспользоваться *
     *
     * Например вот этот метод принимает некую последовательность аргументов с типом Int и выводит их на печать
     * def printArgs(args: Int*) = args.foreach(println(_))
     */
    val lst: List[Int] = List(1, 2, 3, 4)
    println(lst)

    /**
     *
     * Реализовать метод reverse который позволит заменить порядок элементов в списке на противоположный
     */
    val rev: List[Int] = List(1, 2, 3, 4)
    println(rev.reverse())

    /**
     *
     * Реализовать метод map для списка который будет применять некую ф-цию к элементам данного списка
     */
    val mp: List[Int] = List(1, 2, 3, 4)
    println(mp.map(v => v * 2))

    /**
     *
     * Реализовать метод filter для списка который будет фильтровать список по некому условию
     */
    val fltr = List(1, 2, 3, 4, 5, 6)
    println(fltr.filter(v => v % 2 == 0))

    /**
     *
     * Написать функцию incList котрая будет принимать список Int и возвращать список,
     * где каждый элемент будет увеличен на 1
     */
    private def incList(lst : List[Int]) = lst.map(v => v + 1)
    val inclst: List[Int] = List(1, 2, 3, 4)
    println(incList(inclst))


    /**
     *
     * Написать функцию shoutString котрая будет принимать список String и возвращать список,
     * где к каждому элементу будет добавлен префикс в виде '!'
     */
    private def shoutString(lst: List[String]): List[String] = lst.map(v => "!" + v)
    val slst: List[String] = List("1", "2", "3")
    println(shoutString(slst))
}