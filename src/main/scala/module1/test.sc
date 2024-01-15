class Foo{
  var i = 0
  def +:(x: Int) = {
    i = i + x
    i
  }
}

val foo = new Foo

foo.+:(10)
10 +: foo