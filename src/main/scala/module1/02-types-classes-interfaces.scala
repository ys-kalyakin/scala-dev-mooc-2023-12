package module1

import java.io.{Closeable, File}
import scala.io.{BufferedSource, Source}
import scala.util.{Try, Using}



object type_system {

  /**
   * Scala type system
   *
   */



  def absurd(v: Nothing) = ???


  // Generics


  val file: File = new File("ints.txt")
  val source: BufferedSource = Source.fromFile(file)
//  val lines: List[String] = try{
//    source.getLines().toList
//  } finally {
//    source.close()
//  }

  def ensureClose[S, R](source: S)(release: S => Any)(f: S => R): R = {
    try{
      f(source)
    } finally {
      release(source)
    }
  }

//  val lines2: List[String] = ensureClose{source}(_.close()){ s =>
//    s.getLines().toList
//    s.getLines().toList
//    s.getLines().toList
//  }











  // ограничения связанные с дженериками


  /**
   *
   * class
   *
   * конструкторы / поля / методы / компаньоны
   */


  class User private (val email: String, val password: String){
    // auxiliary
    def this(email: String) = this(email, "")
  }

  object User{
    def from(email: String, password: String): User =  new User(email, password)
    def apply(email: String, password: String): User =  new User(email, password)
    def from(email: String): User =  new User(email, "password")
  }


  val user: User = User("", "")
  val user3: User = new User("")
  val user2: User = User.from("", "")







  /**
   * Задание 1: Создать класс "Прямоугольник"(Rectangle),
   * мы должны иметь возможность создавать прямоугольник с заданной
   * длиной(length) и шириной(width), а также вычислять его периметр и площадь
   *
   */


  /**
   * object
   *
   * 1. Паттерн одиночка
   * 2. Ленивая инициализация
   * 3. Могут быть компаньоны
   */


  /**
   * case class
   *
   */



    // создать case класс кредитная карта с двумя полями номер и cvc


  case class CreditCard(number: String, cvc: Int)

  val cc = CreditCard("2132314324", 123)

  val cc2 = cc.copy(cvc = 125)


  class Foo(val f: Int = 10)

  class Bar() extends Foo

  val bar: Bar = new Bar()

  bar.f
  /**
   * case object
   *
   * Используются для создания перечислений или же в качестве сообщений для Акторов
   */



  /**
   * trait
   *
   */

  sealed trait UserService{
    def get(id: Int): User
    def insert(u: User): Unit
    def foo: Int
  }

  trait Identifiable{
    def id: Int
  }

  class UserServiceImpl extends UserService with Identifiable {
    def get(id: Int): User = ???

    def insert(u: User): Unit = ???

    def foo: Int = ???

    def id: Int = ???
  }


  class FooBar{ self : UserService =>
  }

  val fooBar = new FooBar with UserService{
    override def get(id: Int): User = ???

    override def insert(u: User): Unit = ???

    override def foo: Int = ???
  }











  class A {
    def foo() = "A"
  }

  trait B extends A {
    override def foo() = "B" + super.foo()
  }

  trait C extends B {
    override def foo() = "C" + super.foo()
  }

  trait D extends A {
    override def foo() = "D" + super.foo()
  }

  trait E extends C {
    override def foo(): String = "E" + super.foo()
  }


  // CBDA
  // A -> D -> B -> C
  val v = new A with D with C with B


  // DECBA
  // A -> B -> C -> E -> D
  val v1 = new A with E with D with C with B


  /**
   * Value classes и Universal traits
   */


}