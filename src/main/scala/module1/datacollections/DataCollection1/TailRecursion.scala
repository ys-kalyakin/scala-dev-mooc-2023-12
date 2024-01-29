package module1.datacollections.DataCollection1
import scala.annotation.tailrec

object TailRecursion {
  def main(args:Array[String]): Unit = {
    val demoCollection = "line 1" :: "line 2" :: "line 3" :: Nil
    println(s"size is ${tailRec(demoCollection, 0)}")
  }

  @tailrec
  def tailRec(list: List[String], accum: Long): Long = list match {
    case Nil => accum
    case head :: tail => tailRec(tail, accum+1)
  }


}
