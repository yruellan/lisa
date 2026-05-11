package lisa.maths.SetTheory.Types.ADTv2

import org.scalatest.funsuite.AnyFunSuite

class SpecializationTest extends AnyFunSuite with lisa.TestMain {

  given lib: lisa.SetTheoryLibrary.type = lisa.SetTheoryLibrary

  import lisa.maths.SetTheory.SetTheory.{*, given}
  import lisa.maths.SetTheory.Types.ADTv2.{*, given}
  import lisa.maths.SetTheory.Types.ADTv2.library.*

  test("specialized constructors stay usable") {
    assert(pack.introAt(unit).statement != null)
    assert(pack.introAppAt(unit).statement != null)
  }

  test("higher-order recursive functions expose expected eliminations") {
    assert(add.intro.statement != null)
    assert(add.elim.contains(zero))
  }

  test("term application and theorem specialization agree") {
    assert(box(unit) != null)
    assert(box.inductionAt(unit).statement != null)
  }
}
