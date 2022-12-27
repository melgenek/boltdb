package boltdb.types

import boltdb.types
import magnolia1.{AutoDerivation, CaseClass, SealedTrait}

import java.lang.foreign.{MemorySegment, ValueLayout}

trait MemorySegmentFormat[A] {
  def read(segment: MemorySegment, offset: Long): A

  def write(segment: MemorySegment, offset: Long, value: A): Unit

  def byteSize: Long
}

object MemorySegmentFormat extends AutoDerivation[MemorySegmentFormat] {

  def apply[A: MemorySegmentFormat]: MemorySegmentFormat[A] = summon[MemorySegmentFormat[A]]

  override def join[A](caseClass: CaseClass[MemorySegmentFormat.Typeclass, A]): MemorySegmentFormat.Typeclass[A] = {
    new MemorySegmentFormat[A] {
      override def read(segment: MemorySegment, offset: Long): A = {
        val params = caseClass.params

        var currentOffset = offset
        val paramValues = new Array[Any](params.length)
        for (param <- params.sortBy(_.index)) {
          val typeclass = param.typeclass
          paramValues(param.index) = typeclass.read(segment, currentOffset)
          currentOffset += typeclass.byteSize
        }

        caseClass.rawConstruct(paramValues)
      }

      override def write(segment: MemorySegment, offset: Long, value: A): Unit = {
        val params = caseClass.params

        var currentOffset = offset
        for (param <- params.sortBy(_.index)) {
          val typeclass = param.typeclass
          typeclass.write(segment, currentOffset, param.deref(value))
          currentOffset += typeclass.byteSize
        }
      }

      override def byteSize: Long = caseClass.params.map(_.typeclass.byteSize).sum
    }
  }

  override def split[A](sealedTrait: SealedTrait[MemorySegmentFormat.Typeclass, A]): MemorySegmentFormat.Typeclass[A] = ???

  given MemorySegmentFormat[Int] with {
    override def read(segment: MemorySegment, offset: Long): Int = segment.get(ValueLayout.JAVA_INT_UNALIGNED, offset)

    override def write(segment: MemorySegment, offset: Long, value: Int): Unit = segment.set(ValueLayout.JAVA_INT_UNALIGNED, offset, value)

    override def byteSize: Long = ValueLayout.JAVA_INT_UNALIGNED.byteSize()
  }

  given MemorySegmentFormat[Short] with {
    override def read(segment: MemorySegment, offset: Long): Short = segment.get(ValueLayout.JAVA_SHORT_UNALIGNED, offset)

    override def write(segment: MemorySegment, offset: Long, value: Short): Unit = segment.set(ValueLayout.JAVA_SHORT_UNALIGNED, offset, value)

    override def byteSize: Long = ValueLayout.JAVA_SHORT_UNALIGNED.byteSize()
  }

  given MemorySegmentFormat[Long] with {
    override def read(segment: MemorySegment, offset: Long): Long = segment.get(ValueLayout.JAVA_LONG_UNALIGNED, offset)

    override def write(segment: MemorySegment, offset: Long, value: Long): Unit = segment.set(ValueLayout.JAVA_LONG_UNALIGNED, offset, value)

    override def byteSize: Long = ValueLayout.JAVA_LONG_UNALIGNED.byteSize()
  }

  given MemorySegmentFormat[MemorySegment] with {
    override def read(segment: MemorySegment, offset: Long): MemorySegment = segment.asSlice(offset)

    override def write(segment: MemorySegment, offset: Long, value: MemorySegment): Unit = {}

    override def byteSize: Long = 0
  }

}
