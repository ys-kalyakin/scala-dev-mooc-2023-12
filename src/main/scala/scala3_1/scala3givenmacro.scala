package scala3_1

object  scala3macros {
  trait Show[T]{
    inline def show(x:T): String
  }

  case class Foo(x:Int)

  inline given Show[Foo] with {
    inline def show(x:Foo): String = s"11 ${x.toString} 111"
  }



  @main def scala3macrosEx()={
    println(summon[Show[Foo]].show(Foo(42)))
  }
}