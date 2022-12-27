import boltdb.types.MemorySegmentAccessors.*
import boltdb.types.MemorySegmentFormat
import boltdb.types.Types.PageId

import java.io.{File, RandomAccessFile}
import java.lang.foreign.MemoryLayout.PathElement
import java.lang.foreign.{MemoryLayout, MemorySegment, SegmentScope, ValueLayout}
import java.nio.channels.FileChannel
import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import scala.collection.IndexedSeqView

case class Leaf(key: Int, value: Int)derives MemorySegmentFormat

case class Example(value1: PageId, value3: Int, value2: Short, value4: Long, value5: Long,
                   leaves: MemorySegment)derives MemorySegmentFormat

@main
def main(): Unit = {
  //  val channel = FileChannel.open(Path.of("test_data/file.txt"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)
  //  val segment: MemorySegment = channel.map(FileChannel.MapMode.READ_WRITE, 0, 4096, SegmentScope.global())

  //  segment.set(ValueLayout.JAVA_INT, 0, 10)
  //  println(segment.get(ValueLayout.JAVA_INT, 0))

  val segment = MemorySegment.allocateNative(100, SegmentScope.auto())

  val example = Example(111L, 222, 333, 444, 555, segment)
  println("example size:" + MemorySegmentFormat[Example].byteSize)
  println("leaf size:" + MemorySegmentFormat[Leaf].byteSize)

  MemorySegmentFormat[Example].write(segment, 0, example)

  val readExample = MemorySegmentFormat[Example].read(segment, 0)

  println(readExample.leaves.get[Leaf](0))
  println(readExample.leaves.get[Leaf](1))
  println(readExample.leaves.get[Leaf](2))

  readExample.leaves.set(0, Leaf(1, 1))
  println(readExample.leaves.get[Leaf](0))
  println(readExample.leaves.get[Leaf](1))
  println(readExample.leaves.get[Leaf](2))

  readExample.leaves.set(1, Leaf(2, 2))

  println(readExample.leaves.get[Leaf](0))
  println(readExample.leaves.get[Leaf](1))
  println(readExample.leaves.get[Leaf](2))
}
