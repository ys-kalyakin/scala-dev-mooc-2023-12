package module1

import java.time.LocalDate
import java.time.YearMonth

object adt {

  object tuples {

    /** Tuples ()
     *
     *
      */

    object Foo
    type ProductFooBoolean = (Foo.type, Boolean)


    /** Создать возможные экземпляры с типом ProductUnitBoolean
      */

    lazy val p1: ProductFooBoolean = (Foo, true)
    lazy val p2: ProductFooBoolean = (Foo, false)


    /** Реализовать тип Person который будет содержать имя и возраст
      */

    type Person = (String, Int)


    /**  Реализовать тип `CreditCard` который может содержать номер (String),
      *  дату окончания (java.time.YearMonth), имя (String), код безопастности (Short)
      */

    type CreditCard = (String, java.time.YearMonth, Short)

    lazy val cc: CreditCard = ("4534545546",  YearMonth.now(), 123)


  }

  object case_classes {

    /** Case classes
      */



    //  Реализовать Person с помощью case класса

    case class Person(name: String, age: Int)

    // Создать экземпляр для Tony Stark 42 года

    val p2 = Person("Tony", 42)

    // Создать case class для кредитной карты

  }



  object either {

    /** Sum
      */

    /** Either - это наиболее общий способ хранить один из двух или более кусочков информации в одно время.
      * Также как и кортежи обладает целым рядом полезных методов
      * Иммутабелен
      */


    object Bar

    type IntOrString = Either[Int, String]
    type BooleanOrBar = Either[Boolean, Bar.type]


    /** Реализовать экземпляр типа IntOrString с помощью конструктора Right
      */

    val i1: IntOrString = Left(10)

    val i2: BooleanOrBar = Left(true)
    val i3: BooleanOrBar = Left(false)
    val i4: BooleanOrBar = Right(Bar)


    type CreditCard // 1
    type WireTransfer // 1
    type Cash // 1

    /** \
      * Реализовать тип PaymentMethod который может быть представлен одной из альтернатив
      */
    type PaymentMethod = Either[CreditCard, Either[WireTransfer, Cash]]

    val o1: CreditCard = ???
    val o2: WireTransfer = ???
    val o3: Cash = ???

    val i5: PaymentMethod = Left(o1)
    val i6: PaymentMethod = Right(Left(o2))
    val i7: PaymentMethod = Right(Right(o3))

  }

  object sealed_traits {

    /** Также Sum type можно представить в виде sealed trait с набором альтернатив
      */

    sealed trait PaymentMethod
    case object CreditCard
    case object WireTransfer
    case object Cash

    val pm: PaymentMethod = ???


  }

  object cards {

    sealed trait Suit                 // масть
    case object  Clubs extends Suit                  // крести
    case object  Diamonds extends Suit              // бубны
    case object  Spades extends Suit                 // пики
    case object  Hearts extends Suit                  // червы
    type  Rank                      // номинал
    type  Two                     // двойка
    type  Three                   // тройка
    type  Four                    // четверка
    type  Five                    // пятерка
    type  Six                     // шестерка
    type  Seven                   // семерка
    type  Eight                   // восьмерка
    type  Nine                    // девятка
    type  Ten                     // десятка
    type  Jack                    // валет
    type  Queen                   // дама
    type  King                    // король
    type  Ace                     // туз
    case class Card(suit: Suit, rank: Rank)                     // карта
    type Deck                     // колода
    type Hand                     // рука
    type Player                   // игрок
    type Game                     // игра
    type PickupCard               // взять карту

  }

}
