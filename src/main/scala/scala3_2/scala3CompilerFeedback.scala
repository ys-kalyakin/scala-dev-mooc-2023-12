package scala3_2.scala3CompilerFeedback


trait Order[A]{
  def compare(aq:A, a2:A):Int
}

object Order{
  implicit  def orderList[A](implicit  orderA: Order[A]): Order[List[A]] = ???
}

def sort[A](list: List[A])(implicit  order:Order[A]): List[A] = ???

class Foo

object Foo{
  implicit  def orderBar: Order[Foo] = ???
}



/*object Implicits{
  sort(List(List(new Foo)))
}
*/

class Bar

object Implicits{
  implicit def orderBy: Order[Bar] = ???
}

object  testfeedback1 {
 //import Implicits.orderBy
  sort(List(new Bar))
}