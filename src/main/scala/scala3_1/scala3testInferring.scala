package scala3_1

import scala3_1.scala3giveninstances.{Ord, maximum}

object scala3testInferring {
  def descending[T](using asc: Ord[T]): Ord[T] = new Ord[T]:
    def compare(x: T, y: T): Int = asc.compare(y,x)
    
  def minimum[T](xs: List[T])(using Ord[T]) =
    maximum(xs)(using descending)
      
  @main def scala3testInferringEx() ={
    val xs = 10::2::3::(-1)::Nil
    println(minimum(xs))
  } 
  
}
