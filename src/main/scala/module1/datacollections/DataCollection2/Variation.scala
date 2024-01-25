package module1.datacollections.DataCollection2

object Variation {

  abstract class Animal {
    def name: String
  }

  case class Cat(name: String) extends Animal
  case class Dog(name: String) extends Animal
  class Box[A] (var content: A)


  def main(args: Array[String]): Unit ={
    val myCat = Cat("Tigr");
    //инвариантность
//    val myAnimalBox: Box[Animal] = myCat

    //Ковариантность
    class  ImmutableBox[+A](val content: A)
    val catbox: ImmutableBox[Cat] = new ImmutableBox[Cat](Cat("Tigr"))
    val animalBox: ImmutableBox[Animal] = catbox

    // Контрвариантность
    abstract class Serializer[-A] {
      def serializer(a: A): String
    }

    val animalSerializer: Serializer[Animal] = new Serializer[Animal] {
      def serializer(a: Animal): String = ???
    }

    val catSerializer: Serializer[Cat]  = animalSerializer
    catSerializer.serializer(Cat("Tigr"))


  }

}