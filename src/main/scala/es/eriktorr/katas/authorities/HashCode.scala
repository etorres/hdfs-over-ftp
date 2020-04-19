package es.eriktorr.katas.authorities

import scala.annotation.tailrec

object HashCode {
  def hashCode(items: Seq[Int]): Int = {
    val prime = 31

    @tailrec
    def hashCode(items: Seq[Int], accumulated: Int): Int =
      if (items.isEmpty) accumulated
      else hashCode(items.drop(1), prime * accumulated + items.headOption.getOrElse(0))

    hashCode(items, 1)
  }
}
