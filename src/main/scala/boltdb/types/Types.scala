package boltdb.types

import scala.collection.immutable.NumericRange

object Types extends PageIdList {
  opaque type PageId = UInt64

  object PageId {
    final val Zero = PageId(0)
    final val One = PageId(1)

    def apply(v: Long): PageId = v

    def apply(v: UInt32): PageId = v

    val format: MemorySegmentFormat[PageId] = MemorySegmentFormat[Long]
  }

  extension (value: PageId) {
    def <(another: PageId): Boolean = java.lang.Long.compareUnsigned(value, another) < 0
    def >(another: PageId): Boolean = java.lang.Long.compareUnsigned(value, another) > 0
    def ==(another: PageId): Boolean = java.lang.Long.compareUnsigned(value, another) == 0
    def -(another: PageId): PageId = value - another
    def +(another: PageId): PageId = value + another
    def to(another: PageId): NumericRange.Inclusive[PageId] = Range.Long.inclusive(value, another, 1)
  }

  given Ordering[PageId] with {
    override def compare(x: PageId, y: PageId): UInt32 = java.lang.Long.compareUnsigned(x, y)
  }

  given Conversion[Long, PageId] with {
    override def apply(v: Long): PageId = v
  }

  opaque type TxId = UInt64

  object TxId {
    def apply(value: UInt64): TxId = value

    extension (txId: TxId) {
      def <=(another: TxId): Boolean = java.lang.Long.compareUnsigned(txId, another) <= 0
    }
  }

  opaque type UIntPtr = UInt64

  object UIntPtr {
    def apply(value: UInt64): UIntPtr = value
  }

  opaque type UInt16 = Short

  object UInt16 {
    def apply(v: Short): UInt16 = v
  }

  extension (value: UInt16) {
    def &(another: UInt16): UInt32 = value & another
  }


  opaque type UInt32 = Int

  object UInt32 {
    final val Zero = UInt32(0)

    def apply(v: Int): UInt32 = v
  }

  extension (v: UInt32) {
    def ==(another: UInt32) = v == another
    def !=(another: UInt32) = v != another
  }

  opaque type UInt64 = Long

  object UInt64 {
    final val One = UInt64(1)

    def apply(v: Long): UInt64 = v
  }

  given Conversion[Long, UInt64] with {
    override def apply(v: Long): UInt64 = v
  }
}
