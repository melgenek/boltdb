package boltdb.page

import boltdb.types.Types.PageId
import scala.collection.mutable
import boltdb.types.Types.PageIdList

class PageIdListTest extends munit.FunSuite {
  test("should merge page ids when start with b") {
    val a = PageIdList(4L, 5L, 6L, 10L, 11L, 12L, 13L, 27L)
    val b = PageIdList(1L, 3L, 8L, 9L, 25L, 30L)

    val result = a.merge(b)

    assertEquals(result.toList, PageIdList(1L, 3L, 4L, 5L, 6L, 8L, 9L, 10L, 11L, 12L, 13L, 25L, 27L, 30L).toList)
  }

  test("should merge page ids when start with a") {
    val a = PageIdList(4L, 5L, 6L, 10L, 11L, 12L, 13L, 27L, 35L, 36L)
    val b = PageIdList(8L, 9L, 25L, 30L)

    val result = a.merge(b)

    assertEquals(result.toList, PageIdList(4L, 5L, 6L, 8L, 9L, 10L, 11L, 12L, 13L, 25L, 27L, 30L, 35L, 36L).toList)
  }

  test("should merge page ids with equal elements") {
    val a = PageIdList(1L, 3L, 4L, 10L)
    val b = PageIdList(2L, 4L, 5L)

    val result = a.merge(b)

    assertEquals(result.toList, PageIdList(1L, 2L, 3L, 4L, 4L, 5L, 10L).toList)
  }
}
