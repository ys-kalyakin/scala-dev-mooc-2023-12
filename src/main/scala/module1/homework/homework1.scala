package module1.homework

import scala.util.Random

class BallsExperiment {
    private val basket: IndexedSeq[Int] = Range(0, 6).map(n => if (n % 2 == 0) 1 else 0)

    def isFirstBlackSecondWhite(): Boolean = {
        val firstBall = Random.nextInt(basket.size)
        val fistColor = basket(firstBall)

        if (fistColor == 1)
            return false

        val newBasket = basket.patch(firstBall, Nil, 1)
        val secondBall = Random.nextInt(newBasket.size)
        val secondColor = newBasket(secondBall)

        secondColor == 1
    }
}

object BallsTest {
    def main(args: Array[String]): Unit = {
        val count = 100000
        val listOfExperiments: List[BallsExperiment] = Range(0, count).map(_ => new BallsExperiment()).toList

        val countOfExperiments = listOfExperiments.map(e => e.isFirstBlackSecondWhite())
        val countOfPositiveExperiments: Float = countOfExperiments.count(_ == true)
        println(countOfPositiveExperiments / count)
    }
}