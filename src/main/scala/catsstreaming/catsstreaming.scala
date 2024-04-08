package catsstreaming

import cats.effect.kernel.Async
import cats.effect.std.Queue
import cats.effect.{IO, IOApp, Resource, SyncIO}
import fs2.{Chunk, Pure, Stream}

import java.time.Instant
import scala.concurrent.duration._

object Streams extends IOApp.Simple {
  //1.
  val pureApply: Stream[Pure, Int] = Stream.apply(1,2,3)

  //2
  val ioApply: Stream[IO, Int] = pureApply.covary[IO]

  //3
  val list = List(1,2,3,4)
  val strm1: Stream[Pure, Int] = Stream.emits(list)

  //4
  val a: Seq[Int] = pureApply.toList
  val aa: IO[List[Int]] = ioApply.compile.toList

  //5
  val unfolded: Stream[IO, String] = Stream.unfoldEval(0) { s =>
    val next = s+10
    if (s >=50) IO.none
    else IO.println(next.toString).as(Some((next.toString, next)))
  }

  //6
  val s = Stream.eval(IO.readLine).evalMap(s=>IO.println(s">>$s")).repeatN(3)

  //7, look 8 in run function
  type Descriptor = String
  def openFile: IO[Descriptor] = IO.println("open file").as("file descriptor")
  def closeFile(descriptor: Descriptor): IO[Unit] = IO.println("closing file")
  def readFile(descriptor: Descriptor): Stream[IO, Byte] =
    Stream.emits(s"File content".map(_.toByte).toArray)

  val fileResource: Resource[IO, Descriptor] = Resource.make(openFile)(closeFile)
  val resourceStream: Stream[IO, Int] = Stream.resource(fileResource).flatMap(readFile).map(b=>b.toInt + 100)

  //9
  def writeToSocket[F[_]: Async](chunk: Chunk[String]): F[Unit] =
    Async[F].async_{callback =>
      println(s"[thread: ${Thread.currentThread().getName}] :: Writing $chunk to socket")
      callback(Right())
    }

  //10
  val fixedDelayStream = Stream.fixedDelay[IO](1.second).evalMap(_ => IO.println(Instant.now))
  val fixedRateStream = Stream.fixedRate[IO](1.second).evalMap((_ => IO.println(Instant.now)))

  //11
  val queueIO = cats.effect.std.Queue.bounded[IO, Int](100)
  def putInQueue(queue: Queue[IO, Int], value: Int) =
    queue.offer(value)

  val queueStreamIO: IO[Stream[IO, Int]] = for {
    q <- queueIO
    _ <- (IO.sleep(5.millis) *> putInQueue(q, 5)).replicateA(10).start
  } yield Stream.fromQueueUnterminated(q)

  val queueStream: Stream[IO, Int] = Stream.force(queueStreamIO)

  val queueIO1 = cats.effect.std.Queue.bounded[IO, Int](100)
  def putInQueue1(queue: Queue[IO, Int], value: Int) =
    queue.offer(value)

  val queueStreamIO1: IO[Stream[IO, Int]] = for {
    q <- queueIO1
    _ <- (IO.sleep(5.millis) *> putInQueue(q, 10)).replicateA(10).start
  } yield Stream.fromQueueUnterminated(q)

  val queueStream1: Stream[IO, Int] = Stream.force(queueStreamIO1)



  def increment(s: Stream[IO, Int]): Stream[IO, Int] = s.map(_ + 1)
  def multiplication(s: Stream[IO, Int]): Stream[IO, Int] = s.map(_  * 10)





  def run: IO[Unit] = {

    //for 5
//    unfolded.compile.drain
    //for 6
//    s.compile.drain
    //for 7
//    resourceStream.evalMap(IO.println).compile.drain
    //8 chunks
    //Stream((1 to 100) : _*).chunkN(10).map(println).compile.drain

    //9
   // Stream((1 to 100).map(_.toString): _*)
   //   .chunkN(10)
   //   .covary[IO]
   //   .parEvalMapUnordered(10)(writeToSocket[IO])
   //   .compile
   //   .drain

    //10
//    fixedRateStream.compile.drain
//      2024-04-08T18:01:51.075583100Z
//      2024-04-08T18:01:52.045377100Z
//      2024-04-08T18:01:53.045213900Z
//      2024-04-08T18:01:54.059611300Z
//      2024-04-08T18:01:55.060597800Z
//      2024-04-08T18:01:56.046163500Z
//      2024-04-08T18:01:57.059153Z
//      2024-04-08T18:01:58.059238Z
 //   fixedDelayStream.compile.drain
//    2024-04-08T18:02:39.070075100Z
//      2024-04-08T18:02:40.093933100Z
//      2024-04-08T18:02:41.106016600Z
//      2024-04-08T18:02:42.121492300Z
//      2024-04-08T18:02:43.135865800Z
//      2024-04-08T18:02:44.148744600Z
//      2024-04-08T18:02:45.154124400Z
//      2024-04-08T18:02:46.170971500Z
//      2024-04-08T18:02:47.185826300Z
//      2024-04-08T18:02:48.199371900Z
    //11
//    queueStream.through(increment).through(multiplication).evalMap(IO.println).compile.drain
    (queueStream ++ queueStream1).evalMap(IO.println).compile.drain

  }

}