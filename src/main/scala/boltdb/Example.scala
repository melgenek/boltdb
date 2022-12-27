package boltdb

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode
import java.nio.file.{Files, OpenOption, Paths, StandardOpenOption}

object Example extends App {
  val channel = FileChannel.open(Paths.get("file.txt"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)
  val buffer = channel.map(MapMode.READ_WRITE, 4096, 100)

//  buffer.position(100)
//  println(buffer.getInt())
//  buffer.limit(3)
  buffer.putInt(111)
//  buffer.flip()
  println(buffer)
  val array = new Array[Int](10)
  println(buffer.asIntBuffer().get(array))
//  println(buffer.remaining())
//  println(buffer.getInt())
//  println(buffer.remaining())
//  buffer.rewind()
//  println(buffer.remaining())
//  buffer.force()
//  println(buffer.getInt())
//  println(buffer)


}
