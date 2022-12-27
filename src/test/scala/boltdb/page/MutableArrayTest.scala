package boltdb.page

import scala.util.Random

class MutableArrayTest extends munit.FunSuite {

  test("should slice") {
    assertEquals(MutableArray(1, 2, 3, 4, 5).sliceFrom(1).toList, MutableArray(2, 3, 4, 5).toList)
    assertEquals(MutableArray(1, 2, 3, 4, 5).sliceTo(4).toList, MutableArray(1, 2, 3, 4).toList)
    assertEquals(MutableArray(1, 2, 3, 4, 5).sliceFrom(1).sliceTo(3).toList, MutableArray(2, 3, 4).toList)
    assertEquals(MutableArray(1, 2, 3, 4, 5).sliceTo(4).sliceFrom(1).toList, MutableArray(2, 3, 4).toList)
    assertEquals(MutableArray(1, 2, 3).sliceFrom(1).sliceFrom(1).sliceFrom(1).toList, MutableArray.zeroed[Int](0).toList)
  }

  test("should iterate") {
    val arr = MutableArray(5, 6, 7).iterator

    val (i1, v1) = arr.next()
    assertEquals(i1, 0)
    assertEquals(v1, 5)

    val (i2, v2) = arr.next()
    assertEquals(i2, 1)
    assertEquals(v2, 6)

    val (i3, v3) = arr.next()
    assertEquals(i3, 2)
    assertEquals(v3, 7)

    assert(!arr.hasNext)
  }

  test("should iterate slice") {
    val arr = MutableArray(4, 5, 6, 7, 8).sliceTo(4).sliceFrom(1).iterator

    val (i1, v1) = arr.next()
    assertEquals(i1, 0)
    assertEquals(v1, 5)

    val (i2, v2) = arr.next()
    assertEquals(i2, 1)
    assertEquals(v2, 6)

    val (i3, v3) = arr.next()
    assertEquals(i3, 2)
    assertEquals(v3, 7)

    assert(!arr.hasNext)
  }


  test("should copy to an array") {
    val arr = MutableArray(1, 2, 3)
    assertEquals(MutableArray(5, 6).copyTo(arr, 1), 2)
    assertEquals(arr.toList, MutableArray(1, 5, 6).toList)
  }

  test("should copy an array to a slice") {
    val arr1 = MutableArray(1, 2, 3).sliceFrom(1)
    assertEquals(MutableArray(5, 6).copyTo(arr1, 0), 2)
    assertEquals(arr1.toList, MutableArray(5, 6).toList)

    val arr2 = MutableArray(1, 2, 3).sliceTo(2)
    assertEquals(MutableArray(5, 6).copyTo(arr2, 0), 2)
    assertEquals(arr2.toList, MutableArray(5, 6).toList)
  }

  test("should copy a slice to a slice") {
    val arr = MutableArray(1, 2, 3, 4, 5).sliceTo(4).sliceFrom(1)
    assertEquals(MutableArray(8, 9).copyTo(arr, 1), 2)
    assertEquals(arr.toList, MutableArray(2, 8, 9).toList)

    val it = arr.iterator
    val (i1, v1) = it.next()
    assertEquals(i1, 0)
    assertEquals(v1, 2)

    val (i2, v2) = it.next()
    assertEquals(i2, 1)
    assertEquals(v2, 8)

    val (i3, v3) = it.next()
    assertEquals(i3, 2)
    assertEquals(v3, 9)
  }

  test("should add one element") {
    val arr = MutableArray(1, 2)

    arr.addOne(3)
    assertEquals(arr.toList, MutableArray(1, 2, 3).toList)

    arr.addOne(4)
    assertEquals(arr.toList, MutableArray(1, 2, 3, 4).toList)

    arr.addOne(5)
    assertEquals(arr.toList, MutableArray(1, 2, 3, 4, 5).toList)

    arr.addOne(6)
    assertEquals(arr.toList, MutableArray(1, 2, 3, 4, 5, 6).toList)

    arr.addOne(7)
    assertEquals(arr.toList, MutableArray(1, 2, 3, 4, 5, 6, 7).toList)

    arr.addOne(8)
    assertEquals(arr.toList, MutableArray(1, 2, 3, 4, 5, 6, 7, 8).toList)

    assertEquals(arr.capacity, 8)
  }

  test("should add to a slice") {
    val arr = MutableArray(1, 2, 3, 4).sliceTo(3).sliceFrom(1)

    arr.addOne(5)
    assertEquals(arr.toList, MutableArray(2, 3, 5).toList)

    arr.addOne(6)
    assertEquals(arr.toList, MutableArray(2, 3, 5, 6).toList)

    assertEquals(arr.capacity, 8)
  }

  test("should add to a slice without resizing") {
    val arr = MutableArray(1, 2, 3, 4).sliceTo(2)

    arr.addOne(5)
    assertEquals(arr.toList, MutableArray(1, 2, 5).toList)

    arr.addOne(6)
    assertEquals(arr.toList, MutableArray(1, 2, 5, 6).toList)

    assertEquals(arr.capacity, 4)
  }

  test("should add to a slice after copying") {
    val arr = MutableArray.zeroed[Int](3)

    MutableArray(5).copyTo(arr, 1)
    assertEquals(arr.toList, MutableArray(0, 5).toList)

    arr.addOne(6)
    assertEquals(arr.toList, MutableArray(0, 5, 6).toList)

    assertEquals(arr.capacity, 3)
  }

  test("should add all") {
    val arr = MutableArray(1, 2, 3)

    arr.addAll(MutableArray(5, 6, 7))
    assertEquals(arr.toList, MutableArray(1, 2, 3, 5, 6, 7).toList)

    assertEquals(arr.capacity, 6)
  }

  test("should add without resizing") {
    val arr = MutableArray.zeroed[Int](5)

    arr.addAll(MutableArray(3, 4, 5, 6, 7))
    assertEquals(arr.toList, MutableArray(3, 4, 5, 6, 7).toList)

    assertEquals(arr.capacity, 5)
  }

  test("should add all to an empty array") {
    val arr = MutableArray.zeroed[Int](20)
    val another = createRandomArray(200)

    arr.addAll(another)

    assertEquals(arr.toList, another.toList)
  }

  test("should sort") {
    val arr = MutableArray.zeroed[Int](10)

    arr.addAll(MutableArray(7, 6, 5, 4, 3))
    arr.sort()
    assertEquals(arr.toList, MutableArray(3, 4, 5, 6, 7).toList)

    assertEquals(arr.capacity, 10)
  }

  private def createRandomArray(size: Int): MutableArray[Int] = {
    val result = MutableArray.zeroed[Int](size)
    val random = new Random(42)
    for (_ <- 0 until size) {
      result.addOne(random.nextInt())
    }
    result.sort()
    result
  }
}

