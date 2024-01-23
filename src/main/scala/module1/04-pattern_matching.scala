package module1

object pattern_matching{
     // Pattern matching

  /**
   * Матчинг на типы
   */

   val i: Any = ???

   i match {
     case v: Int => println("It's Int")
     case v: Double => println("It's Double")
     case v: String => println("It's String")
   }




  /**
   * Структурный матчинг
   */




  sealed trait Animal{

    def whoIam = this match {
      case Dog(n, _) => println(s"I'm dog $n")
      case Cat(n, _) => println(s"I'm dog $n")
    }

    if(this.isInstanceOf[Dog]){
      val d = this.asInstanceOf[Dog]
      val n = d.name
      val a = d.age
      println(s"I'm dog $n")
    }


  }


  case class Dog(name: String, age: Int) extends Animal
  case class Cat(name: String, age: Int) extends Animal


  /**
   * Матчинг на литерал
   */

  val animal: Animal = ???

  animal match {
    case Dog("Bim", age) => ???
    case Dog(n, a) => ???
    case Cat(name, age) => ???
  }

  val Bim = "Bim"



  /**
   * Матчинг на константу
   */

  animal match {
    case Dog(Bim, age) => ???
    case Cat(name, age) => ???
  }



  /**
   * Матчинг с условием (гарды)
   */

  animal match {
    case Dog(Bim, 10) => ???
    case Dog(name, age)  => ???
    case Cat(name, age) => ???
  }


  /**
   * "as" паттерн
   */

  def treatCat(cat: Cat) = ???
  def treatDog(dog: Dog) = ???



  /**
   * используя паттерн матчинг напечатать имя и возраст
   */

  def treatAnimal(a: Animal) = a match {
    case d @ Dog(n, a) =>
      println(s"$n $a")
      treatDog(d)
    case c : Cat => treatCat(c)
  }



  final case class Employee(name: String, address: Address)
  final case class Address(val street: String, val number: Int)


  val alex = Employee("Alex", Address("XXX", 221))


  class Person(name: String, age: Int)

  object Person{
    def unapply(p: Person): Option[(String, Int)] = ???
    def apply(n: String, a: Int): Person = ???
  }

  val tony = Person("Tony", 42)
  val Person(n, age) = tony

  /**
   * воспользовавшись паттерн матчингом напечатать номер из поля адрес
   */




  /**
   * Паттерн матчинг может содержать литералы.
   * Реализовать паттерн матчинг на alex с двумя кейсами.
   * 1. Имя должно соотвествовать Alex
   * 2. Все остальные
   */




  /**
   * Паттерны могут содержать условия. В этом случае case сработает,
   * если и паттерн совпал и условие true.
   * Условия в паттерн матчинге называются гардами.
   */



  /**
   * Реализовать паттерн матчинг на alex с двумя кейсами.
   * 1. Имя должно начинаться с A
   * 2. Все остальные
   */


  /**
   *
   * Мы можем поместить кусок паттерна в переменную использую `as` паттерн,
   * x @ ..., где x это любая переменная.
   * Это переменная может использоваться, как в условии,
   * так и внутри кейса
   */

    trait PaymentMethod
    case object Card extends PaymentMethod
    case object WireTransfer extends PaymentMethod
    case object Cash extends PaymentMethod

    case class Order(paymentMethod: PaymentMethod)

    lazy val order: Order = ???

    lazy val pm: PaymentMethod = ???


    def checkByCard(o: Order) = ???

    def checkOther(o: Order) = ???



  /**
   * Мы можем использовать вертикальную черту `|` для матчинга на альтернативы
   */

   sealed trait A
   case class B(v: Int) extends A
   case class C(v: Int) extends A
   case class D(v: Int) extends A

   val a: A = ???

  a match {
    case B(_) | C(_) =>
    case D(v) =>
   }


}