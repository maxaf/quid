# QUID
## Questionable Unique ID

The below quick example is made to be self-explanatory:

```scala
scala> val id = quid()
id: quid.QUID = QUID(2017-07-03T04:34:40.535,682416776580794,216970222662707036904691001753094337245)

scala> id.id
res0: scala.math.BigInt = 2017070304344053500000000682416776580794216970222662707036904691001753094337245

scala> id.string
res1: String = H3TRb87TuB5Dv4gJdaV6drlbCPmQ34sr9g0NhoTaJpnh
```

### API

```scala
import quid._
```

* Obtain an identifier:

```scala
val id = quid()
```

* The returned object is of class `quid.QUID` and has three fields:
  * `time: LocalDateTime` - current time as of ID creation
  * `nanos: Long` - count of nanoseconds elapsed since an arbitrary time in the past
  * `random: BigInt` - 128-bit random number

* Convert the identifier to a `BigInt`, which can then be treated as a number, or converted to a `Array[Byte]` for efficient storage:

```scala
id.id
```

* Convert the identifier to a 44-character `String`:

```scala
id.string
```

* Decode `BigInt` or `String` representation back to a `QUID` instance:

```scala
scala> QUID.unapply(id.id)
res2: Option[quid.QUID] = Some(QUID(2017-07-03T04:41:12.212,682808452047743,298520094930793769427953864532933077597))

scala> QUID.unapply(id.string)
res3: Option[quid.QUID] = Some(QUID(2017-07-03T04:41:12.212,682808452047743,298520094930793769427953864532933077597))
```

### Properties of the generated ID

* The API and returned IDs are both thread-safe.
* The IDs are time-sortable:
  * Strictly sortable (w/nanosecond precision) within a single JVM.
  * Roughly sortable (w/millisecond precision, modulo clock differences) among many JVMs.
  * `BigInt` representation is numerically sortable.
  * `String` representation is a base-62 encoding of the `BigInt`, so it's lexicographically sortable too.
* Uniqueness is guaranteed by:
  * Slight clock drift between JVMs running on different machines.
  * Individual JVMs (same machine or distributed) will be counting `System.nanoTime` based on different starting values.
  * 128-bit random component is a lot of randomness.
    * Caveat: I hope you have a good entropy source. `¯\_(ツ)_/¯`
