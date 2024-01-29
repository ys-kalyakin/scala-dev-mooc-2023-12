package object monad {

    /**
     * Реализуйте методы map / flatMap / withFilter чтобы работал код и законы монад соблюдались
     * HINT: для проверки на пустой элемент можно использовать eq
     */

    trait Wrap[+A] {

        def get: A

        def pure[R](x: R): Wrap[R] = {
            if (x == null) {
                EmptyWrap
            } else {
                NonEmptyWrap(x)
            }
        }

        def flatMap[R](f: A => Wrap[R]): Wrap[R] = {
            if (this eq EmptyWrap) {
                EmptyWrap
            } else {
                f(this.get)
            }
        }

        def map[R](f: A => R): Wrap[R] = {
            flatMap(v => pure(f(v)))
        }

        def withFilter(f: A => Boolean): Wrap[A] = {
            if (this eq EmptyWrap) {
                EmptyWrap
            } else {
                if (f(this.get)) this else EmptyWrap
            }
        }

    }

    object Wrap {
        def empty[R]: Wrap[R] = EmptyWrap
    }

    case class NonEmptyWrap[A](result: A) extends Wrap[A] {
        override def get: A = result
    } // pure

    case object EmptyWrap extends Wrap[Nothing] {
        override def get: Nothing = throw new NoSuchElementException("Wrap.get")
    } // bottom, null element

}