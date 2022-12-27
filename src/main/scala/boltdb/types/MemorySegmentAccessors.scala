package boltdb.types

import java.lang.foreign.MemorySegment

object MemorySegmentAccessors {
  extension (segment: MemorySegment) {
    def get[A](idx: Int)(using format: MemorySegmentFormat[A]) = format.read(segment, idx * format.byteSize)

    def set[A](idx: Int, value: A)(using format: MemorySegmentFormat[A]) = {
      format.write(segment, idx * format.byteSize, value)
    }
  }
}