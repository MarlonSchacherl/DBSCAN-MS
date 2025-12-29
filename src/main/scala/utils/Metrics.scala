package utils


trait Metric[-A] extends Serializable {
  def distance(a: A, b: A): Float
}

object Metrics {
  implicit object EuclideanArrayFloat extends Metric[Array[Float]] {
    override def distance(a: Array[Float], b: Array[Float]): Float =
      DistanceMeasures.euclidean(a, b)
  }

  implicit object LevenshteinString extends Metric[String] {
    override def distance(a: String, b: String): Float =
      DistanceMeasures.levenshtein(a, b).toFloat
  }
}
