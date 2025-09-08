package algorithm

import model.DataPoint
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random
import utils.Distance.euclidean

class HFITest() extends AnyFunSuite {
  // Test HFI
  test("Basic HFI tests") {
    val seed = 42
    val rng = new Random(seed)
    val dataset: Array[DataPoint] = Array.fill(1000)(DataPoint(Array.fill(5)(rng.nextFloat()), id = 0))

    val numberOfPivots = 10
    val pivots: Array[DataPoint] = HFI(dataset, numberOfPivots, euclidean, seed)

    // Check that the correct number of pivots is returned
    assert(pivots.length == numberOfPivots, s"Expected $numberOfPivots pivots, but got ${pivots.length}")

    // Check that all pivots are from the original dataset
    pivots.foreach { pivot =>
      assert(dataset.contains(pivot), s"Pivot ${pivot.data.mkString(",")} is not in the original dataset")
    }

    // Check that all pivots are unique
    assert(pivots.distinct.length == pivots.length, "Pivots are not unique")


  }

  test("Semantic HFI test") {
    val seed = 42
    val rng = new Random(seed)
    val dataset: Array[DataPoint] = Array.fill(10)(DataPoint(Array.fill(2)(rng.nextFloat()), id = 0))

    val numberOfPivots = 3
    val pivots: Array[DataPoint] = HFI(dataset, numberOfPivots, euclidean, seed)

    // Print the selected pivots for manual inspection
    println("Selected pivots:")
    pivots.foreach(p => println(p.data.mkString("(", ", ", ")")))
  }


  // Test newPivotSetPrecision
  test("Test newPivotSetPrecision") {

  }


  // Test L_infNorm
  test("Test L_infNorm") {

  }


  // Test samplePairs
  test("Test samplePairs") {

  }


}
