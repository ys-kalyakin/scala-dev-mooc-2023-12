package scala3_2

object DependentFunctionalTypes {
  def arrayElement(n: Int): (Array[Int], n.type ) => Int =
    (arr, i) => arr(i)

  @main def DependentFunctionalTypesEx() ={
    val arr = Array(1,2,3,4,5)
    val elementAtTwo:Int = arrayElement(2)(arr, 2)
    println(elementAtTwo)
  }

}
