import java.lang.foreign.{MemorySegment, SegmentScope, ValueLayout}
import java.io.{File, RandomAccessFile}
import java.nio.channels.FileChannel
import java.nio.file.{Files, StandardOpenOption, Path, Paths}

@main
def main(): Unit = {
  val channel = FileChannel.open(Path.of("test_data/file.txt"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)
  val segment: MemorySegment = channel.map(FileChannel.MapMode.READ_WRITE, 0, 4096, SegmentScope.global())

  segment.set(ValueLayout.JAVA_INT, 0, 10)
  println(segment.get(ValueLayout.JAVA_INT, 0))
}
