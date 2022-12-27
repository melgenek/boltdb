package boltdb.page

import boltdb.page.Page.*
import boltdb.page.PageType.*
import boltdb.types.Types.*

case class Page(pageId: PageId,
                flags: UInt16 = UInt16(0),
                count: UInt16 = UInt16(0),
                overflow: UInt32 = UInt32(0),
                ptr: UIntPtr = UIntPtr(0L)) {

  def getType: PageType =
    if ((flags & BranchPageFlag) != UInt32.Zero) Branch
    else if ((flags & LeafPageFlag) != UInt32.Zero) Leaf
    else if ((flags & MetaPageFlag) != UInt32.Zero) Meta
    else if ((flags & FreelistPageFlag) != UInt32.Zero) Freelist
    else Unknown
}

object Page {
  final val BranchPageFlag = UInt16(0x01)
  final val LeafPageFlag = UInt16(0x02)
  final val MetaPageFlag = UInt16(0x04)
  final val FreelistPageFlag = UInt16(0x10)
}
