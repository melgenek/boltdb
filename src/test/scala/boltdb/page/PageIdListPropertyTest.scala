package boltdb.page

import boltdb.types.Types.PageId
import org.scalacheck.Arbitrary
import org.scalacheck.Prop.*
import scala.collection.mutable
import boltdb.types.Types.PageIdList

class PageIdListPropertyTest extends munit.ScalaCheckSuite {

  given pageIdArbitrary: Arbitrary[PageId] = Arbitrary(Arbitrary.arbitrary[Long].map(PageId.apply))

  override def scalaCheckTestParameters =
    super.scalaCheckTestParameters
      .withMinSuccessfulTests(20000)
      .withMaxDiscardRatio(1)

  property("should merge page ids in order") {
    forAll { (a: Array[PageId], b: Array[PageId]) =>
      a.sortInPlace()
      b.sortInPlace()
      val expected = a.appendedAll(b)
      expected.sortInPlace()

      val result = PageIdList(a).merge(PageIdList(b))

      assertEquals(result.toList, PageIdList(expected).toList)
    }
  }

}
