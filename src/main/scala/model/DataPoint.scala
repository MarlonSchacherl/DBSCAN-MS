package model

import utils.Metric

final case class DataPoint[A](data: A,
                     id: Long,
                     var label: Int = LABEL.NOISE,
                     var visited: Boolean = false,
                     var vectorRep: Array[Float] = null,
                     var mask: Int = -1,
                     var localCluster: Int = -1,
                     var partition: Int = -1,
                     var globalCluster: Int = -1) {
  override def equals(obj: Any): Boolean = obj match {
    case that: DataPoint[_] => this.id == that.id && this.partition == that.partition
    case _ => false
  }

  override def hashCode(): Int = {
    val prime = 31
    prime * (prime + partition) + id.toInt
  }

  override def toString: String = s"DataPoint($data, id=$id, label=$label, visited=$visited, " +
                                  s"vectorRep=${if (vectorRep != null) vectorRep.mkString(", ")}, " +
                                  s"mask=$mask, cluster=$localCluster, partition=$partition, globalCluster=$globalCluster)"

  final def distance(other: DataPoint[A])(implicit m: Metric[A]): Float = {
    m.distance(this.data, other.data)
  }

  /**
   * @return The number of dimensions of vectorRep.
   */
  def dimensions: Int = vectorRep.length

  /**
   * Copies this DataPoint and sets the `vectorRep` to the given value.
   * @param vectorRep The new vector representation.
   * @return A new DataPoint with the given vectorRep.
   *
   * @note '''We're only making shallow copies, therefore assuming data won't be changed after withVectorRep is called'''
   */
  def withVectorRep(vectorRep: Array[Float]): DataPoint[A] = this.copy(vectorRep = vectorRep)

  /**
   * Copies this DataPoint and sets the `margin` mask.
   * @return A new DataPoint with the set mask.
   *
   * @note '''We're only making shallow copies, therefore assuming data & vectorRep won't be changed after withMask is called'''
   */
  def withMask(mask: Int): DataPoint[A] = this.copy(mask = mask)
}

