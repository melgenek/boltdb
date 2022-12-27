package boltdb.page

import boltdb.types.Types.PageId

import scala.collection.AbstractIterator
import scala.collection.Searching.{Found, InsertionPoint}
import scala.reflect.ClassTag
import scala.util.Sorting

final class MutableArray[@specialized(Long, Int) T: Ordering : ClassTag] private(var array: Array[T], var start: Int, var end: Int) {
  def head: T = array(start)

  def capacity: Int = array.length

  def length: Int = end - start

  def addOne(v: T): Unit = {
    ensureCapacity(end + 1)
    array(end) = v
    end += 1
  }

  def addAll(another: MutableArray[T]): Unit = {
    ensureCapacity(end + another.length)
    another.copyTo(this, end)
  }

  private def ensureCapacity(capacity: Int): Unit = {
    if (capacity > array.length) {
      val newArray = new Array[T](math.max(array.length * 2, capacity))
      Array.copy(array, start, newArray, 0, length)
      array = newArray
      end -= start
      start = 0
    }
  }

  def sliceFrom(from: Int): MutableArray[T] = new MutableArray(array, start + from, end)

  def sliceTo(to: Int): MutableArray[T] = new MutableArray(array, start, start + to)

  def splitAtValue(v: T): (MutableArray[T], MutableArray[T]) = {
    array.search(v, start, end) match {
      case Found(point) => (new MutableArray(array, start, point + 1), new MutableArray(array, point + 1, end))
      case InsertionPoint(point) => (new MutableArray(array, start, point), new MutableArray(array, point, end))
    }
  }

  def copyTo(dest: MutableArray[T], destPos: Int): Int = {
    val len = length
    assert(
      dest.array.length >= dest.start + destPos + len,
      s"The size has to be at least '${dest.start + destPos + len}', but was '${dest.array.length}'"
    )
    Array.copy(array, start, dest.array, dest.start + destPos, len)
    dest.end = math.max(destPos + len, dest.end)
    len
  }

  def nonEmpty: Boolean = end - start > 0

  def isEmpty: Boolean = !nonEmpty

  def toList: List[T] = array.slice(start, end).toList

  def iterator: AbstractIterator[(Int, T)] = new AbstractIterator[(Int, T)] {
    private var idx: Int = 0

    def hasNext: Boolean = (idx + start) < end

    def next(): (Int, T) = {
      val result = (idx, array(idx + start))
      idx += 1
      result
    }
  }

  def sort(): Unit = {
    (array: @unchecked) match {
      case a: Array[Int] => java.util.Arrays.sort(a, start, end)
      case a: Array[Long] => java.util.Arrays.sort(a, start, end)
    }
  }

  override def toString: String = s"{${array.take(start).mkString(",")},(${array.slice(start, end).mkString(",")}),${array.drop(end).mkString(",")}}[$start:$end]"
}

object MutableArray {
  def zeroed[@specialized(Long, Int) T: Ordering : ClassTag](size: Int = 16): MutableArray[T] = new MutableArray(new Array[T](size), 0, 0)

  def apply[@specialized(Long, Int) T: Ordering : ClassTag](array: Array[T]): MutableArray[T] = new MutableArray(array, 0, array.length)

  def apply[@specialized(Long, Int) T: Ordering : ClassTag](value: T, values: T*): MutableArray[T] = {
    val arr = (value +: values).toArray
    new MutableArray(arr, 0, arr.length)
  }
}