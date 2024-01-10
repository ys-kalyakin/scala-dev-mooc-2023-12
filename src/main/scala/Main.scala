import module1.type_system

object Main {

  def main(args: Array[String]): Unit = {
    println(s"Hello world ")
    println(type_system.v1.foo())
  }
}