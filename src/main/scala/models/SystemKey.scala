package models

object SystemKey extends Enumeration {
  type SystemKey = Value
  val Ctrl, Alt, Shift = Value

  object SystemKeyOrdering {
    implicit val ordering: Ordering[SystemKey] = Ordering.by {
      case Ctrl  => 1
      case Alt   => 2
      case Shift => 3
    }
  }
}
