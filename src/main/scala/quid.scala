package quid

import java.math.BigInteger
import java.util.concurrent.atomic.AtomicLong
import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

object `package` {
  val rnd = new scala.util.Random
  val UTC = ZoneId.of("Z")

  implicit class LongPimp(x: Long) {
    val width = math.log10(x).intValue + 1
    def shift_left_10(desired: Int) =
      x * (if (width < desired) math.pow(10, desired - width).longValue else 1)
  }

  implicit class BigIntPimp(x: BigInt) {
    val width = x.toString(10).size
    def shift_left_10(desired: Int) =
      x * (if (width < desired) BigInteger.TEN.pow(desired - width)
           else BigInt(1))
  }

  implicit class BigIntCPimp(bi: BigInt.type) {
    def max_value(bits: Int) = BigInt(2).pow(bits) - 1
    val Max_Width_64 = max_value(64).width
    val Max_Width_128 = max_value(128).width
  }

  implicit class PimpLocalDateTime(ldt: LocalDateTime) {
    import ChronoField._
    def encode =
      (((((ldt.getLong(YEAR_OF_ERA).shift_left_10(6) +
        ldt.getLong(MONTH_OF_YEAR)).shift_left_10(8) +
        ldt.getLong(DAY_OF_MONTH)).shift_left_10(10) +
        ldt.getLong(HOUR_OF_DAY)).shift_left_10(12) +
        ldt.getLong(MINUTE_OF_HOUR)).shift_left_10(14) +
        ldt.getLong(SECOND_OF_MINUTE)).shift_left_10(17) +
        ldt.getLong(MILLI_OF_SECOND)
  }

  def now() = LocalDateTime.now(UTC)

  case class QUID(
      time: LocalDateTime = now(),
      nanos: Long = System.nanoTime,
      random: BigInt = BigInt(128, rnd)
  ) {
    def id = {
      val lhs = BigInt(time.encode)
        .shift_left_10(BigInt.Max_Width_64 * 2) + BigInt(nanos)
      lhs.shift_left_10(BigInt.Max_Width_128 + lhs.width) + random
    }
    def string = B62.encode(id)
    def stem = QUID(time = time)
    object stems extends Iterator[QUID] {
      def hasNext = true
      def next = stem
    }
  }

  object QUID {
    private val Time =
      new DateTimeFormatterBuilder()
        .appendPattern("yyyyMMddHHmmss")
        .appendValue(ChronoField.MILLI_OF_SECOND, 3)
        .toFormatter

    private val Ten62 = BigInteger.TEN.pow(62)
    private val Ten39 = BigInteger.TEN.pow(39)

    def unapply(i: BigInt): Option[QUID] = {
      val time = i / Ten62
      val nanosAndRandom = i - (time * Ten62)
      val nanos = nanosAndRandom / Ten39
      val random = nanosAndRandom - (nanos * Ten39)

      Some(
        QUID(
          time = LocalDateTime.parse(time.toString(10), Time),
          nanos = nanos.longValue,
          random = random
        ))
    }

    def unapply(s: String): Option[QUID] = unapply(B62.decode(s))
  }

  def quid(): QUID = QUID()

  object quids extends Iterator[QUID] {
    def hasNext = true
    def next = quid()
  }
}
