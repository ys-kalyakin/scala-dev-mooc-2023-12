package module1.futures

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object task_futures_sequence {

    /**
     * В данном задании Вам предлагается реализовать функцию fullSequence,
     * похожую на Future.sequence, но в отличии от нее,
     * возвращающую все успешные и не успешные результаты.
     * Возвращаемое тип функции - кортеж из двух списков,
     * в левом хранятся результаты успешных выполнений,
     * в правово результаты неуспешных выполнений.
     * Не допускается использование методов объекта Await и мутабельных переменных var
     */
    /**
     * @param futures список асинхронных задач
     * @return асинхронную задачу с кортежом из двух списков
     */
    def fullSequence[A](futures: List[Future[A]])
                       (implicit ex: ExecutionContext): Future[(List[A], List[Throwable])] = {
        val r: Future[(List[A], List[Throwable])] = futures.foldLeft(Future.successful((List.empty[A], List.empty[Throwable]))) {
            (fl, fr) => {
                fl.flatMap { res =>
                    fr.transformWith {
                        case Success(value) => Future.successful(res._1 ::: List(value), res._2)
                        case Failure(exception) => Future.successful((res._1, res._2 ::: List(exception)))
                    }
                }
            }
        }
        r
    }

}
