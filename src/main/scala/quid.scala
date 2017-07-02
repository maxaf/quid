package quid

import java.util.concurrent.atomic.AtomicLong
import java.time.{ LocalDateTime, ZoneId }
import java.time.temporal.ChronoField._

object `package` {
  val rnd = new scala.util.Random
  val UTC = ZoneId.of("Z")

  implicit class LongPimp(x: Long) {
    val width = math.log10(x).intValue + 1
    def shift_left_10(desired: Int) =
      x * (if (width < desired) math.pow(10, desired - width).longValue else 1)
  }

  implicit class BigIntPimp(x: BigInt) {
    val width = {
      @annotation.tailrec
      def width0(x: BigInt, w: Int): Int = {
        val div = x / 10
        if (div == 0) w
        else width0(div, w + 1)
      }
      width0(x, 1)
    }
    def shift_left_10(desired: Int) =
      x * (if (width < desired) BigInt(10).pow(desired - width) else BigInt(1))
  }

  implicit class BigIntCPimp(bi: BigInt.type) {
    def max_value(bits: Int) = BigInt(2).pow(bits) - 1
    val Max_Width_64 = max_value(64).width
    val Max_Width_128 = max_value(128).width
  }

  def now() = {
    val now = LocalDateTime.now(UTC).atZone(UTC)
    (((((now.getLong(YEAR_OF_ERA).shift_left_10(6) +
      now.getLong(MONTH_OF_YEAR)).shift_left_10(8) +
      now.getLong(DAY_OF_MONTH)).shift_left_10(10) +
      now.getLong(HOUR_OF_DAY)).shift_left_10(12) +
      now.getLong(MINUTE_OF_HOUR)).shift_left_10(14) +
      now.getLong(SECOND_OF_MINUTE)).shift_left_10(17) +
      now.getLong(MILLI_OF_SECOND)
  }

  case class QUID(
      time: Long = now(),
      nanos: Long = System.nanoTime,
      random: BigInt = BigInt(128, rnd)
  ) {
    def id = {
      val lhs = BigInt(time).shift_left_10(BigInt.Max_Width_64 * 2) + BigInt(nanos)
      lhs.shift_left_10(BigInt.Max_Width_128 + lhs.width) + random
    }
    def b36 = id.toString(Character.MAX_RADIX)
    def b62 = B62.encode(id)
  }

  def quid(): QUID = QUID()

  object quids extends Iterator[QUID] {
    def hasNext = true
    def next = quid()
  }
}
