package module1

import module1.threads.ToyFuture
import module1.utils.NameableThreads

import java.io.File
import java.util.{Timer, TimerTask}
import java.util.concurrent.{Executor, ExecutorService, Executors}
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, Promise, TimeoutException}
import scala.io.{BufferedSource, Source}
import scala.language.{existentials, postfixOps}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.global

object threads {


  // Thread

  class Thread1 extends Thread {
    override def run(): Unit = {
      Thread.sleep(100)
      println(s"Hello from ${Thread.currentThread().getName}")
    }
  }

  def getRatesLocation1 = {
    Thread.sleep(1000)
    println("GetRatesLocation1")
  }

  def getRatesLocation2 = {
    Thread.sleep(1000)
    println("GetRatesLocation2")
  }

  def printRunningTime(v: => Unit): Unit = {
    val start = System.currentTimeMillis()
    v
    val end = System.currentTimeMillis()
    println(s"Execution time ${end - start}")
  }

  // rates



  // async

  def async(f: => Unit): Thread = new Thread{
    override def run(): Unit = f
  }

  def getRatesLocation3 = async{
    Thread.sleep(2000)
    println("GetRatesLocation1")
  }

  def getRatesLocation4 = async{
    Thread.sleep(1000)
    println("GetRatesLocation2")
  }

  def async2[A](f: => A): A = {
    var v: A = null.asInstanceOf[A]
    val t = new Thread{
      override def run(): Unit = {
        v = f
      }
    }
    t.start()
    t.join()
    v
  }

  def getRatesLocation5: Int = async2{
    Thread.sleep(2000)
    println("GetRatesLocation1")
    10
  }

  def getRatesLocation6: Int = async2{
    Thread.sleep(1000)
    println("GetRatesLocation2")
    20
  }

  class ToyFuture[T]private(v: () => T){

    private var r: T = null.asInstanceOf[T]
    private var isCompleted: Boolean = false
    private val q = mutable.Queue[T => _]()

    def map[B](f: T => B): ToyFuture[B] = ???
    def flatMap[B](f: T => ToyFuture[B]): ToyFuture[B] = ???

    def onComplete[U](f: T => U): Unit =
      if(isCompleted) f(r)
      else q.enqueue(f)

    private def start(executor: Executor): Unit = {
      val t = new Runnable {
        override def run(): Unit = {
          val result = v()
          r = result
          isCompleted = true
          while (q.nonEmpty){
            q.dequeue()(result)
          }
        }
      }
      executor.execute(t)
    }
  }
  object ToyFuture{
    def apply[T](v: => T)(executor: Executor): ToyFuture[T] = {
      val tf = new ToyFuture[T](() => v)
      tf.start(executor)
      tf
    }
  }

  def getRatesLocation7: ToyFuture[Int] = ToyFuture{
    Thread.sleep(2000)
    println("GetRatesLocation1")
    10
  }(executor.pool2)

  def getRatesLocation8: ToyFuture[Int] = ToyFuture{
    Thread.sleep(1000)
    println("GetRatesLocation2")
    20
  }(executor.pool2)









}

object executor {
      val pool1: ExecutorService =
        Executors.newFixedThreadPool(2, NameableThreads("fixed-pool-1"))
      val pool2: ExecutorService =
        Executors.newCachedThreadPool(NameableThreads("cached-pool-2"))
      val pool3: ExecutorService =
        Executors.newWorkStealingPool(4)
      val pool4: ExecutorService =
        Executors.newSingleThreadExecutor(NameableThreads("singleThread-pool-4"))
}

object try_{

  def readFromFile(): List[String] = {
    val s: BufferedSource = Source.fromFile(new File("ints.txt"))
    try {
      val r = s.getLines().toList
      r
    } catch {
      case e: Throwable =>
        println(e.getMessage)
        Nil
    } finally {
      s.close()
    }
  }

  def readFromFile2(): Try[List[String]] = {
    val s: BufferedSource = Source.fromFile(new File("ints.txt"))
    val r = Try(s.getLines().toList)
    s.close()
    r
  }

  def readFromFile3(): Try[List[Int]] = {
    val source: Try[BufferedSource] = Try(Source.fromFile(new File("ints.txt")))
    def lines(s: BufferedSource): Try[List[Int]] = {
      Try(s.getLines().toList.map(_.toInt))
    }
    val r: Try[List[Int]] = for{
      s <- source
      l <- lines(s)
    } yield l
    source.foreach(_.close())
    r
  }


}

object future{
  // constructors
  val f1: Future[Int] = Future.successful(10)
  val f2: Future[Int] = Future.failed[Int](new Throwable("Not found"))
  val f3 = Future(10 + 20)(global)


  def getRatesLocation1: Future[Int] = Future{
    Thread.sleep(2000)
    println("GetRatesLocation1")
    10
  }(global)

  def getRatesLocation2: Future[Int] = Future{
    Thread.sleep(1000)
    println("GetRatesLocation2")
    20
  }(global)

  // combinators
  def longRunningComputation: Int = ???


  def ratesSum: Future[(Int, Int)] =
    getRatesLocation1.zip(getRatesLocation2)

  val r: Future[(Int, Int)] = ratesSum.recover{
    case _ => (0, 0)
  }(global)

//  def printRunningTime(v: => Unit): Unit = {
//    val start = System.currentTimeMillis()
//    v
//    val end = System.currentTimeMillis()
//    println(s"Execution time ${end - start}")
//  }

  implicit val ec = global

  def printRunningTime[T](v: => Future[T]): Future[T] = for{
    start <- Future.successful(System.currentTimeMillis())
    r <- v
    end <- Future.successful(System.currentTimeMillis())
    _ <- Future.successful(println(s"Execution time ${end - start}"))
  } yield r




  def action(v: Int): Int = {
    Thread.sleep(1000)
    println(s"Action $v in ${Thread.currentThread().getName}")
    v
  }


  // Execution contexts
  val ec1 = ExecutionContext.fromExecutor(executor.pool1)
  val ec2 = ExecutionContext.fromExecutor(executor.pool2)
  val ec3 = ExecutionContext.fromExecutor(executor.pool3)
  val ec4 = ExecutionContext.fromExecutor(executor.pool4)


  val f01 = Future(action(1))(ec1)
  val f02 = Future(action(2))(ec2)

  val f03 = f1.flatMap{ v1 =>
    action(5)
    f2.map{ v2 =>
//      action(6)
      action(v1 + v2)
    }(ec4)
  }(ec3)



}

object promise {

  val p: Promise[Int] = Promise[Int]
  val f1: Future[Int] = p.future

  p.isCompleted // false
  f1.isCompleted // false
  p.complete(Try(10)) //
  f1.isCompleted // true



  object FutureSyntax{
     def map[T, B](future: Future[T])(f: T => B): Future[B] = {
       val p = Promise[B]
       future.onComplete {
         case Failure(exception) =>
           p.failure(exception)
         case Success(value) =>
           p.complete(Try(f(value)))
       }(global)
       p.future
     }
  }

}