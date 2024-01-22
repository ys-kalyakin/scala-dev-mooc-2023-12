val f: String => Int = {
  case "foo" => 0
  case _ => 1
}

def f2(x: String): Int = {
  case "foo" => 0
  case _ => 1
}