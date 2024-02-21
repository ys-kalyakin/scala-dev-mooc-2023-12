package module2

import scala.language.implicitConversions

object higher_kinded_types{

    def tuple[A, B](a: List[A], b: List[B]): List[(A, B)] =
        a.flatMap{ a => b.map((a, _))}

    def tuple[A, B](a: Option[A], b: Option[B]): Option[(A, B)] =
        a.flatMap{ a => b.map((a, _))}

    def tuple[E, A, B](a: Either[E, A], b: Either[E, B]): Either[E, (A, B)] =
        a.flatMap{ a => b.map((a, _))}


    def tuplef[F[_], A, B](fa: Bindable[F, A], fb: Bindable[F, B]): F[(A, B)] =
        fa.flatMap(a => fb.map((a, _)))

    implicit def OptionToBindable[T](o : Option[T]) : Bindable[Option, T] =
        new Bindable[Option, T]() {

            override def map[B](f: T => B): Option[B] = o.map(f)

            override def flatMap[B](f: T => Option[B]): Option[B] = o.flatMap(f)
        }

    implicit def ListToBindable[T](lst : List[T]) : Bindable[List, T] =
        new Bindable[List, T]() {

            override def map[B](f: T => B): List[B] = lst.map(f)

            override def flatMap[B](f: T => List[B]): List[B] = lst.flatMap(f)
        }

    trait Bindable[F[_], A] {
        def map[B](f: A => B): F[B]
        def flatMap[B](f: A => F[B]): F[B]
    }

    def tupleBindable[F[_], A, B](fa: Bindable[F, A], fb: Bindable[F, B]): F[(A, B)]  =
        fa.flatMap(a => fb.map((a, _)))

    def optBindable[A](opt: Option[A]): Bindable[Option, A] =
        new Bindable[Option, A] {
            override def map[B](f: A => B): Option[B] = opt.map(f)

            override def flatMap[B](f: A => Option[B]): Option[B] = opt.flatMap(f)
        }

    def listBindable[A](opt: List[A]): Bindable[List, A] =
        new Bindable[List, A] {
            override def map[B](f: A => B): List[B] = opt.map(f)

            override def flatMap[B](f: A => List[B]): List[B] = opt.flatMap(f)
        }






    val optA: Option[Int] = Some(1)
    val optB: Option[Int] = Some(2)

    val list1 = List(1, 2, 3)
    val list2 = List(4, 5, 6)

    lazy val r3 = println(tupleBindable(optBindable(optA), optBindable(optB)))
    lazy val r4 = println(tupleBindable(listBindable(list1), listBindable(list2)))

    val r1 = println(tuplef(optA, optB))
    val r2 = println(tuplef(list1, list2))

}