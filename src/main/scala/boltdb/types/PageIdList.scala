package boltdb.types

import boltdb.page.MutableArray
import boltdb.types.Types.PageId

import java.util
import scala.collection.{AbstractIterator, mutable}

trait PageIdList {
  opaque type PageIdList = MutableArray[PageId]

  object PageIdList {
    def empty(capacity: Int = 16): PageIdList = MutableArray.zeroed[PageId](capacity)

    def apply(v: Array[PageId]): PageIdList = MutableArray[PageId](v)

    def apply(value: PageId, values: PageId*): PageIdList = MutableArray[PageId](value, values: _*)
  }

  extension (list: PageIdList) {
    def merge(another: PageIdList): PageIdList = {
      if (list.isEmpty) another
      else if (another.isEmpty) list
      else {
        val result = MutableArray.zeroed[PageId](list.length + another.length)
        mergeToDest(result, list, another)
        result
      }
    }

    def length: Int = list.length

    def isEmpty: Boolean = length == 0

    def addOne(pageId: PageId): Unit = list.addOne(pageId)
    def addAll(another: PageIdList): Unit = list.addAll(another)

    def toList: List[PageId] = list.toList
    def toString: String = list.toList.mkString("[", ",", "]")

    def iterator: AbstractIterator[(Int, PageId)] = list.iterator

    def sliceFrom(from: Int): PageIdList = list.sliceFrom(from)
    def sliceTo(to: Int): PageIdList = list.sliceTo(to)
    def copyTo(dest: PageIdList, destPos: Int): Int = list.copyTo(dest, destPos)

    def sort(): Unit = list.sort()
  }

  /**
   * copies the sorted union of a and b into a destination list.
   */
  def mergeToDest(dest: PageIdList, a: PageIdList, b: PageIdList): Unit = {
    assert(dest.capacity >= a.length + b.length, s"Destination array capacity is too low: ${dest.length} < ${a.length} + ${b.length}")

    if (a.isEmpty) {
      b.copyTo(dest, 0)
    } else if (b.isEmpty) {
      a.copyTo(dest, 0)
    } else {
      var lead = a
      var follow = b
      if (b.head < a.head) {
        lead = b
        follow = a
      }
      var destPosition = 0

      while (follow.nonEmpty) {
        val (beforeValue, afterValue) = lead.splitAtValue(follow.head)
        destPosition += beforeValue.copyTo(dest, destPosition)

        lead = follow
        follow = afterValue
      }

      lead.copyTo(dest, destPosition)
    }
  }

}
