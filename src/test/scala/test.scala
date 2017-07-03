package quid.test

import quid._
import java.time.LocalDateTime
import org.specs2._

class QuidSpec extends Specification {
  def is = s2"""
    quid must generate
      unique IDs                                  $unique
      many unique IDs                             $manyUnique
      k-sortable unique IDs                       $kSortable
      only 44-character unique IDs in string form $string44
      correctly sortable stem IDs                 $sortableStems

    a quid must
      encode into numeric form                    $numericEncode
      encode into string form                     $stringEncode
      decode from numeric form                    $numericDecode
      decode from string form                    $stringDecode
  """

  val unique = {
    val one = quid()
    val two = quid()

    one must not be_== (two)
  }

  val manyUnique = {
    val ids = quids.take(1000).toSet
    ids.size must_== 1000
  }

  val kSortable = {
    val ids = quids.take(10000).map(_.string).toList
    ids.sorted must_== ids
  }

  val string44 = {
    val lengths = quids.take(10000).map(_.string.size).toSet
    lengths must_== Set(44)
  }

  val sortableStems = {
    val id = quid()
    val stems = id.stems.take(1000).toList
    val ids = (id :: stems).map(_.string)
    (ids.sorted must_== ids) and (stems.map(_.time).toSet must_== Set(id.time))
  }

  val numericEncode = {
    QUID(time = LocalDateTime.of(1234, 12, 13, 14, 15, 16, 178000000), nanos = 2, random = 3).id must_== BigInt("1234121314151617800000000000000000000002000000000000000000000000000000000000003")
  }

  val stringEncode = {
    val id = QUID(time = LocalDateTime.of(1234, 12, 13, 14, 15, 16, 178000000), nanos = 2, random = 3)
    id.string must_== "AR0ExszmcTuDU09eMreaNw0cBpebXBCn59BYR2R2QfUh"
  }

  val numericDecode = {
    QUID.unapply(BigInt("1234121314151617800000000000000000000002000000000000000000000000000000000000003")) must beSome[QUID](===(QUID(time = LocalDateTime.of(1234, 12, 13, 14, 15, 16, 178000000), nanos = 2, random = 3))) and {
      val id = quid()
      QUID.unapply(id.id) must_== Some(id)
    }
  }

  val stringDecode = {
    QUID.unapply("AR0ExszmcTuDU09eMreaNw0cBpebXBCn59BYR2R2QfUh") must beSome[QUID](===(QUID(time = LocalDateTime.of(1234, 12, 13, 14, 15, 16, 178000000), nanos = 2, random = 3))) and {
      val id = quid()
      QUID.unapply(id.string) must_== Some(id)
    }
  }
}
