package utils


object DistanceMeasures {
  final def euclidean(a: Array[Float], b: Array[Float]): Float = {
    var i = 0
    var acc = 0.0f
    val n = a.length
    while (i < n) {
      val d = a(i) - b(i)
      acc += d * d
      i += 1
    }
    math.sqrt(acc).toFloat
  }

  final def levenshtein(s1: String, s2: String): Int = {
    if (s1 == s2) return 0
    if (s1.isEmpty) return s2.length
    if (s2.isEmpty) return s1.length

    val (longer, shorter) =
      if (s1.length >= s2.length) (s1, s2) else (s2, s1)

    var prev = new Array[Int](shorter.length + 1)
    var curr = new Array[Int](shorter.length + 1)

    var j = 0
    while (j <= shorter.length) { prev(j) = j; j += 1 }

    var i = 1
    while (i <= longer.length) {
      curr(0) = i
      val c1 = longer.charAt(i - 1)

      j = 1
      while (j <= shorter.length) {
        val c2 = shorter.charAt(j - 1)
        val cost = if (c1 == c2) 0 else 1
        val del = prev(j) + 1
        val ins = curr(j - 1) + 1
        val sub = prev(j - 1) + cost
        curr(j) = Math.min(Math.min(del, ins), sub)
        j += 1
      }

      val tmp = prev; prev = curr; curr = tmp
      i += 1
    }

    prev(shorter.length)
  }
}


