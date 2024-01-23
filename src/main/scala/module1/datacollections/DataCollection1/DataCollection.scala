package module1.datacollections.DataCollection1

object  DataCollection {
  def main(args: Array[String]): Unit = {
    //1.
    val demoCollectionList = "line 1" :: "line 2" :: "line 3" :: Nil
    val demoCollectionList1 = Seq("line 1", "line 2", "line 3")

    val demoCollectionSet = ("line 1" :: "line 2" :: "line 3":: "line 3" :: Nil).toSet
//    demoCollectionSet.foreach(x=> println(x))

    val demoCollectionSet1 = ("line 1" :: "line 2" :: "line 3":: "line 3" :: Nil).groupBy(x=>x).map(x=>x._1)
  //  demoCollectionSet1.foreach(x=> println(x))

    //2. iter
    val iter = demoCollectionList.iterator
    while(iter.hasNext)
      println(iter.next)

    //3.
    println("demo functions")
    val demoCollection = 1 :: 2 :: 3 :: Nil
    println(s"fold result : ${demoCollection.fold(0)((z,i) => z + i)} ")
    println(s"fold left result: ${demoCollection.foldLeft(0)((z,i)=>z+i)}")
    println(s"reduce result: ${demoCollection.reduce((z,i)=> z+i)}")

    // List.mkString(",")

    val test = List(1,2,3,4,5) :: List(1, 50, 3):: List(1, 2) :: Nil
    test.filter(x => x.reduce((y,z) => y+z) > 10).foreach(x=>println(x.mkString(",")))
    // 1,2,3,4,5,1,50,3,1,2
    println(test.flatMap(x=>x).mkString(","))



  }

}