package ADTv2Examples.endtoend

import lisa.maths.SetTheory.Types.ADTv2.*
import lisa.maths.SetTheory.Types.ADTv2.library.*
import lisa.maths.SetTheory.Types.Tactics.Typecheck

object NatAndListLibrary extends lisa.Main {

  val natList = list(nat)
  val nilNat = nil(nat)
  val consNat = cons(nat)
  val singletonZero = consNat * zero * nilNat

  println(s"list: $list")
  println(s"natlist: $natList")

  val singletonZeroTyping = Theorem(singletonZero :: natList) {
    have(thesis) by Typecheck.prove
  }

  val singletonLengthTyping = Theorem(length(nat) * nil(nat) :: nat) {
    have(thesis) by Typecheck.prove
  }

  // section("Library theorems")
  // show(double.intro)
  // show(add.elim(zero))
  // println("Skipped: polymorphic self-recursive list recursion is not normalized yet, so length(nat) is not forced in this example.")
}
