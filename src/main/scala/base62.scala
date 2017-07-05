package quid

object B62 {
  private val alphabet =
    (('0' to '9') ++ ('A' to 'Z') ++ ('a' to 'z')).toVector
  private val base = BigInt(alphabet.size)

  def decode(s: String): BigInt = {
    val size = s.size
    s.iterator.zipWithIndex.map {
      case (c, i) =>
        val p = size - i - 1
        alphabet.indexOf(c) * base.pow(p)
    }.sum
  }

  def encode(i: BigInt): String = {
    @annotation.tailrec
    def div(i: BigInt, res: List[Int] = Nil): List[Int] = {
      val (q, r) = i /% base
      if (q > 0) div(q, r.toInt :: res)
      else i.toInt :: res
    }
    div(i).map(alphabet).mkString
  }
}
