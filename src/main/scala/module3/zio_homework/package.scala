package module3

import zio.{UIO, ZIO}
import zio.clock.{Clock, currentTime}
import zio.console._
import zio.random.Random

import java.io.IOException
import java.util.concurrent.TimeUnit
import scala.language.postfixOps

package object zio_homework {
    /**
     * 1.
     * Используя сервисы Random и Console, напишите консольную ZIO программу которая будет предлагать пользователю угадать число от 1 до 3
     * и печатать в консоль угадал или нет. Подумайте, на какие наиболее простые эффекты ее можно декомпозировать.
     */
    lazy val guessProgram: ZIO[Console with Random, IOException, Unit] = for {
        num <- zio.random.nextIntBetween(1, 4)
        _ <- zio.console.putStrLn("Введите число")
        userInput <- zio.console.getStrLn
        _ <- zio.console.putStrLn(if (userInput.toInt == num) "Угадали" else "Не угадали")
    } yield ()

    /**
     * 2. реализовать функцию doWhile (общего назначения), которая будет выполнять эффект до тех пор, пока его значение в условии не даст true
     *
     */

    def doWhile[R, E, A](effect : ZIO[R, E, A], predicate: A => Boolean) = {
        effect.repeatWhile(predicate)
    }


    /**
     * 3. Реализовать метод, который безопасно прочитает конфиг из файла, а в случае ошибки вернет дефолтный конфиг
     * и выведет его в консоль
     * Используйте эффект "load" из пакета config
     */
    def loadConfigOrDefault: ZIO[Console, Nothing, config.AppConfig] = {
        config.load.orElse {
            for {
                defaultConfig <- ZIO.succeed(config.AppConfig("localhost", "8080"))
                _ <- zio.console.putStrLn("default config: ")
                _ <- zio.console.putStrLn(defaultConfig.toString)
            } yield defaultConfig
        }
    }


    /**
     * 4. Следуйте инструкциям ниже для написания 2-х ZIO программ,
     * обратите внимание на сигнатуры эффектов, которые будут у вас получаться,
     * на изменение этих сигнатур
     */


    /**
     * 4.1 Создайте эффект, который будет возвращать случайеым образом выбранное число от 0 до 10 спустя 1 секунду
     * Используйте сервис zio Random
     */
    lazy val eff: ZIO[Random with Clock, Nothing, Int] = for {
        _ <- zio.clock.sleep(zio.duration.Duration.fromMillis(1000))
        num <- zio.random.nextIntBetween(0, 11)
    } yield num

    /**
     * 4.2 Создайте коллукцию из 10 выше описанных эффектов (eff)
     */
    lazy val effects: Iterable[ZIO[Random with Clock, Nothing, Int]] = ZIO.replicate(10)(eff)


    /**
     * 4.3 Напишите программу которая вычислит сумму элементов коллекции "effects",
     * напечатает ее в консоль и вернет результат, а также залогирует затраченное время на выполнение,
     * можно использовать ф-цию printEffectRunningTime, которую мы разработали на занятиях
     */

    lazy val app = for {
        start <- currentTime(TimeUnit.MILLISECONDS)
        sum <- ZIO.mergeAll(effects)(0)(_ + _)
        _ <- zio.console.putStrLn(s"sum: $sum")
        end <- currentTime(TimeUnit.MILLISECONDS)
        _ <- zio.console.putStrLn(s"time: ${end - start}")
    } yield sum


    /**
     * 4.4 Усовершенствуйте программу 4.3 так, чтобы минимизировать время ее выполнения
     */

    lazy val appSpeedUp = for {
        start <- currentTime(TimeUnit.MILLISECONDS)
        sum <- ZIO.mergeAllPar(effects)(0)(_ + _)
        _ <- zio.console.putStrLn(s"sum: $sum")
        end <- currentTime(TimeUnit.MILLISECONDS)
        _ <- zio.console.putStrLn(s"time: ${end - start}")
    } yield sum


    /**
     * 5. Оформите ф-цию printEffectRunningTime разработанную на занятиях в отдельный сервис, так чтобы ее
     * можно было использовать аналогично zio.console.putStrLn например
     */

    def printEffectRunningTime[R, E, A](effect: ZIO[R, E, A]): ZIO[Console with Clock with R, E, A] = for {
        start <- currentTime(TimeUnit.MILLISECONDS)
        res <- effect
        end <- currentTime(TimeUnit.MILLISECONDS)
        _ <- zio.console.putStrLn(s"time: ${end - start}")
    } yield res

    /**
     * 6.
     * Воспользуйтесь написанным сервисом, чтобы созадть эффект, который будет логировать время выполнения прогаммы из пункта 4.3
     *
     *
     */

    lazy val appWithTimeLogg: ZIO[Console with Clock with Random, E, Int] = printEffectRunningTime(app)

    /**
     *
     * Подготовьте его к запуску и затем запустите воспользовавшись ZioHomeWorkApp
     */

    lazy val runApp: ZIO[Any, Any, Int] = appWithTimeLogg.provideLayer(Console.live ++ Random.live ++ Clock.live)

}
