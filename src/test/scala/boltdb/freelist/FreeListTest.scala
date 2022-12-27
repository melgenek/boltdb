package boltdb.freelist

import boltdb.page.Page
import boltdb.types.Types.{PageId, PageIdList, TxId, UInt32}

class FreeListTest extends munit.FunSuite {

  test("should allocate") {
    val freelist = FreeList(PageIdList(3L, 4L, 5L, 6L, 7L, 9L, 12L, 13L, 18L))

    assertEquals(freelist.allocate(3), PageId(3))
    assertEquals(freelist.allocate(1), PageId(6))
    assertEquals(freelist.allocate(3), PageId(0))
    assertEquals(freelist.allocate(2), PageId(12))
    assertEquals(freelist.allocate(1), PageId(7))
    assertEquals(freelist.allocate(0), PageId(0))
    assertEquals(freelist.allocate(0), PageId(0))
    assertEquals(freelist.pageIds.toList, PageIdList(9L, 18L).toList)
    assertEquals(freelist.allocate(1), PageId(9))
    assertEquals(freelist.allocate(1), PageId(18))
    assertEquals(freelist.allocate(1), PageId(0))
    assert(freelist.pageIds.isEmpty)
  }

  test("should free a page") {
    val freelist = FreeList()

    freelist.free(TxId(100L), Page(PageId(12)))

    assertEquals(freelist.pending(TxId(100L)).toList, PageIdList(12L).toList)
  }

  test("should free a page with overflow") {
    val freelist = FreeList()

    freelist.free(TxId(100L), Page(PageId(12), overflow = UInt32(3)))

    assertEquals(freelist.pending(TxId(100L)).toList, PageIdList(12L, 13L, 14L, 15L).toList)
  }

  test("should release") {
    val freelist = FreeList()
    freelist.free(TxId(100L), Page(PageId(12), overflow = UInt32(1)))
    freelist.free(TxId(100L), Page(PageId(9)))
    freelist.free(TxId(102L), Page(PageId(39)))

    freelist.release(TxId(100L))
    freelist.release(TxId(101L))
    assertEquals(freelist.pageIds.toList, PageIdList(9L, 12L, 13L).toList)

    freelist.release(TxId(102L))
    assertEquals(freelist.pageIds.toList, PageIdList(9L, 12L, 13L, 39L).toList)
  }
}
