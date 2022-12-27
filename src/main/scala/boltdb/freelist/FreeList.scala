package boltdb.freelist

import boltdb.types.Types.{PageId, PageIdList, TxId, UInt64}
import boltdb.page.Page

import java.nio.MappedByteBuffer
import scala.collection.mutable

class FreeList(var pageIds: PageIdList = PageIdList.empty(),
               val pending: mutable.HashMap[TxId, PageIdList] = mutable.HashMap.empty,
               val cache: mutable.HashMap[PageId, Boolean] = mutable.HashMap.empty) {

  /**
   * @return count of pages on the freelist
   */
  def count: Int = freeCount + pendingCount

  /**
   * @return count of free pages
   */
  def freeCount: Int = pageIds.length

  /**
   * @return count of pending pages
   */
  def pendingCount: Int = pending.map(_._2.length).sum

  /**
   * @return the starting page id of a contiguous list of pages of a given size.
   *         If a contiguous block cannot be found then 0 is returned.
   */
  def allocate(n: Int): PageId = {
    if (pageIds.isEmpty) return PageId.Zero

    var initial: PageId = PageId(0)
    var previousId: PageId = PageId.Zero

    val it = pageIds.iterator

    while (it.hasNext) {
      val (idx, pageId) = it.next()
      assert(pageId > PageId.One, s"Invalid page allocation $pageId")

      // Reset initial page if this is not contiguous.
      if (previousId == PageId.Zero || (pageId - previousId) != UInt64.One) {
        initial = pageId
      }

      // If we found a contiguous block then remove it and return it.
      if (((pageId - initial) + PageId.One) == PageId(n)) {
        if (idx + 1 == n) {
          pageIds = pageIds.sliceFrom(idx + 1)
        } else {
          pageIds.sliceFrom(idx + 1).copyTo(pageIds, idx - n + 1)
          pageIds = pageIds.sliceTo(pageIds.length - n)
        }

        for (i <- 0 until n) {
          cache.remove(PageId(i) + initial)
        }

        return initial
      }

      previousId = pageId
    }

    PageId.Zero
  }

  /**
   * releases a page and its overflow for a given transaction id.
   * If the page is already free then an exception is thrown
   *
   */
  def free(txId: TxId, page: Page): Unit = {
    assert(page.pageId > PageId.One, s"Cannot free page 0 or 1: ${page.pageId}")

    val pendingIds = pending.getOrElse(txId, PageIdList.empty())
    for (id <- page.pageId to page.pageId + PageId(page.overflow)) {
      assert(!cache.getOrElse(id, false), s"Page ${page.pageId} has already been freed")

      pendingIds.addOne(id)
      cache(id) = true
    }
    pending(txId) = pendingIds
  }

  //   release moves all page ids for a transaction id (or older) to the freelist.
  def release(txId: TxId): Unit = {
    val removed = PageIdList.empty()
    for ((pendingTxId, pendingPageIds) <- pending) {
      if (pendingTxId <= txId) {
        // Move transaction's pending pages to the available freelist.
        // Don't remove from the cache since the page is still free.
        removed.addAll(pendingPageIds)
        pending.remove(pendingTxId)
      }
    }
    removed.sort()
    pageIds = pageIds.merge(removed)
  }

  //  // release moves all page ids for a transaction id (or older) to the freelist.
  //  func (f *freelist) release(txid txid) {
  //    m := make(pgids, 0)
  //    for tid, ids := range f.pending {
  //      if tid <= txid {
  //        // Move transaction's pending pages to the available freelist.
  //        // Don't remove from the cache since the page is still free.
  //        m = append(m, ids...)
  //        delete(f.pending, tid)
  //      }
  //    }
  //    sort.Sort(m)
  //    f.ids = pgids(f.ids).merge(m)
  //  }

}

object FreeList {
}
