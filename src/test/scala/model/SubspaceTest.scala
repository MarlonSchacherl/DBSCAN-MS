package model

import org.scalatest.funsuite.AnyFunSuite

class SubspaceTest extends AnyFunSuite {

  test("Subspace split should correctly divide points and update bounding box") {
    val points = Array(
      DataPoint(Array(1.0f, 2.0f), id = 1),
      DataPoint(Array(3.0f, 4.0f), id = 2),
      DataPoint(Array(5.0f, 6.0f), id = 3),
      DataPoint(Array(7.0f, 8.0f), id = 4)
    )
    val bbCoords = Array((1.0f, 7.0f), (2.0f, 8.0f))
    val subspace = new Subspace(points, bbCoords)

    val (leftSubspace, rightSubspace) = subspace.split(0)

    assert(leftSubspace.points.length == 2)
    assert(rightSubspace.points.length == 2)

    assert(leftSubspace.bbCoords(0) == (1.0f, 5.0f))
    assert(rightSubspace.bbCoords(0) == (5.0f, 7.0f))

    assert(leftSubspace.bbCoords(1) == (2.0f, 8.0f))
    assert(rightSubspace.bbCoords(1) == (2.0f, 8.0f))
  }

}
