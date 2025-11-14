package utils

import org.scalatest.funsuite.AnyFunSuite

class TinyArrayBufferTest extends AnyFunSuite {
  test("New TinyArrayBuffer is empty") {
    val buf = new TinyArrayBuffer()
    assert(buf.length === 0)

    val arr = buf.toArray
    assert(arr.isEmpty)
  }

  test("Append a single element") {
    val buf = new TinyArrayBuffer()
    buf += 42

    assert(buf.length === 1)
    assert(buf.toArray === Array(42))
  }

  test("Append multiple elements below initial capacity") {
    val buf = new TinyArrayBuffer(initialCapacity = 8)

    buf += 1
    buf += 2
    buf += 3
    buf += 4
    buf += 5

    assert(buf.length === 5)
    assert(buf.toArray === Array(1, 2, 3, 4, 5))
  }

  test("Grows correctly when exceeding initial capacity") {
    val buf = new TinyArrayBuffer(initialCapacity = 2)

    // force at least one resize
    val expected = Array(0, 1, 2, 3, 4, 5, 6)
    expected.foreach(buf += _)

    assert(buf.length === expected.length)
    assert(buf.toArray === expected)
  }

  test("toArray returns backing array when buffer is exactly full") {
    val buf = new TinyArrayBuffer(initialCapacity = 4)

    buf += 10
    buf += 11
    buf += 12
    buf += 13

    // First call should return the backing array
    val arr1 = buf.toArray
    assert(arr1 === Array(10, 11, 12, 13))

    // Second call should return the same reference, since elemCounter == elems.length
    val arr2 = buf.toArray
    assert(arr1 eq arr2) // reference equality
  }

  test("toArray returns a trimmed copy when buffer is not full") {
    val buf = new TinyArrayBuffer(initialCapacity = 8)

    buf += 100
    buf += 200
    buf += 300
    buf += 400

    val arr1 = buf.toArray
    assert(arr1 === Array(100, 200, 300, 400))
    assert(arr1.length === 4)

    // elemCounter < elems.length, so each call creates a new array
    val arr2 = buf.toArray
    assert(arr2 === Array(100, 200, 300, 400))
    assert(!(arr1 eq arr2)) // should *not* be the same reference
  }

  test("Works with zero-capacity-like usage pattern (grow from very small)") {
    // Start with tiny capacity to stress the resizing logic
    val buf = new TinyArrayBuffer(initialCapacity = 1)

    val expected = (0 until 100).toArray
    expected.foreach(buf += _)

    assert(buf.length === expected.length)
    assert(buf.toArray === expected)
  }

  test("Grows correctly from zero initial capacity") {
    val buf = new TinyArrayBuffer(initialCapacity = 0)

    buf += 1
    buf += 2
    buf += 3

    assert(buf.length === 3)
    assert(buf.toArray === Array(1, 2, 3))
  }

  test("Negative initialCapacity throws NegativeArraySizeException") {
    assertThrows[NegativeArraySizeException] {
      new TinyArrayBuffer(initialCapacity = -1)
    }
  }

  test("toArray copy is not affected by later appends") {
    val buf = new TinyArrayBuffer(initialCapacity = 8)

    buf += 10
    buf += 20

    val arr1 = buf.toArray
    assert(arr1 === Array(10, 20))

    buf += 30
    buf += 40

    // old snapshot must remain unchanged
    assert(arr1 === Array(10, 20))
    // new snapshot sees everything
    assert(buf.toArray === Array(10, 20, 30, 40))
  }


  /* ensureCapacity Tests: */

  // Helper to get the current backing array length via reflection.
  private def capacityOf(buf: TinyArrayBuffer): Int = {
    val field = classOf[TinyArrayBuffer].getDeclaredField("elems")
    field.setAccessible(true)
    val arr = field.get(buf).asInstanceOf[Array[Int]]
    arr.length
  }

  test("ensureCapacity does nothing when minCapacity <= current capacity") {
    val buf = new TinyArrayBuffer(initialCapacity = 8)
    val initialCap = capacityOf(buf)

    // smaller than current capacity
    buf += 1
    buf += 2
    buf += 3
    buf += 4
    val capAfterSmaller = capacityOf(buf)

    // equal to current capacity
    buf += 5
    buf += 6
    buf += 7
    buf += 8
    val capAfterEqual = capacityOf(buf)

    assert(initialCap === 8)
    assert(capAfterSmaller === initialCap)
    assert(capAfterEqual === initialCap)
  }

  test("ensureCapacity doubles capacity when that suffices") {
    val buf = new TinyArrayBuffer(initialCapacity = 4)
    val initialCap = capacityOf(buf)
    assert(initialCap === 4)

    buf += 1
    buf += 2
    buf += 3
    buf += 4
    assert(capacityOf(buf) === 4)

    buf += 5
    val newCap = capacityOf(buf)

    assert(newCap === 8)
  }
}