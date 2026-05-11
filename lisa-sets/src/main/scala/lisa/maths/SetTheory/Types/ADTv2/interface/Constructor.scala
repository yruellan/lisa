package lisa.maths.SetTheory.Types.ADTv2.interface

import lisa.maths.SetTheory.SetTheory.{*, given}
import lisa.maths.SetTheory.Types.ADTv2.encoding.SemanticConstructor
import lisa.maths.SetTheory.Types.ADTv2.support.{**, toSeq}
import lisa.maths.SetTheory.Types.TypingHelpers.::
import lisa.maths.SetTheory.Types.TypingHelpers.{FunctionalClass, TypedConstantFunctional}
import lisa.maths.SetTheory.Types.ADTv2.support.InterfaceHelpers.{instantiatedSemanticSignature, introAppAt as buildIntroAppAt, resolvedTypeArguments, theoremAt, typingTheoremAt}
import lisa.maths.SetTheory.Types.ADTv2.support.Utils.{renderAppliedSymbol, wellTypedSet}
import lisa.utils.prooflib.ProofTacticLib.Arity

final class ConstructorImpl[N <: Arity, S](using
    sort: lisa.utils.fol.FOL.IsSort[S],
    val line: sourcecode.Line,
    val file: sourcecode.File,
    valueOfN: ValueOf[N]
)(
    val semantic: SemanticConstructor[N]
) extends TypedConstantFunctional[S](
      semantic.id,
      FunctionalClass(
        List.fill(semantic.typeVariablesSeq.size)(None),
        semantic.typeVariablesSeq.toList,
        semantic.typ
      ),
      typingTheoremAt(semantic.fullName, semantic.typeVariablesSeq, semantic.intro)
    ) {

  printAs(args => renderAppliedSymbol(semantic.fullName, semantic.typeVariablesSeq.size, args))

  val name: String = semantic.fullName
  val typeVariables: Variable[Ind] ** N = semantic.typeVariables
  val typeVariablesSeq: Seq[Variable[Ind]] = semantic.typeVariablesSeq
  val get_arity: Int = valueOfN.value
  val term: Expr[Ind] = termAt(typeVariablesSeq)

  lazy val intro: THM = theoremAt(
    displayName = name,
    typeVariables = typeVariablesSeq,
    typeArgs = Seq.empty,
    suffix = "introduction",
    baseTheorem = semantic.intro
  )

  lazy val introApp: THM = introAppAt()

  lazy val injectivity: THM = theoremAt(
    displayName = name,
    typeVariables = typeVariablesSeq,
    typeArgs = Seq.empty,
    suffix = "injectivity",
    baseTheorem = semantic.injectivity
  )

  def introAt(typeArgs: Expr[Ind]*): THM =
    theoremAt(name, typeVariablesSeq, typeArgs, "introduction", semantic.intro)

  def introAppAt(typeArgs: Expr[Ind]*): THM = buildIntroAppAt(
    displayName = name,
    typeVariables = typeVariablesSeq,
    typeArgs = typeArgs,
    baseTheorem = semantic.intro,
    headTermAt = termAt,
    headTypeAt = substitutions => semantic.typ.substitute(substitutions*),
    assumptionsAt = substitutions =>
      wellTypedSet(instantiatedSemanticSignature(semantic.semanticSignature, substitutions)),
    typingArgsAt = substitutions =>
      instantiatedSemanticSignature(semantic.semanticSignature, substitutions),
    conclusionAt = substitutions =>
      semantic.appliedTerm.substitute(substitutions*) ::
        semantic.adt.specializedTerm(resolvedTypeArguments(typeVariablesSeq, substitutions))
  )

  def injectivityAt(typeArgs: Expr[Ind]*): THM =
    theoremAt(name, typeVariablesSeq, typeArgs, "injectivity", semantic.injectivity)

  def termAt(args: Seq[Expr[Ind]]): Expr[Ind] = {
    require(
      args.size == typeVariablesSeq.size || args.isEmpty,
      s"Constructor $name expects ${typeVariablesSeq.size} type argument(s), got ${args.size}."
    )
    val effectiveArgs = if args.isEmpty then typeVariablesSeq else args
    (this #@@ effectiveArgs).asInstanceOf[Expr[Ind]]
  }

  def applyUnsafe(args: Expr[Ind] ** N): Expr[Ind] = termAt(args.toSeq)

  def applySeq(args: Seq[Expr[Ind]]): Expr[Ind] = termAt(args)

  def apply(args: Expr[Ind]*): Expr[Ind] = termAt(args)
}

type Constructor[N <: Arity] = ConstructorImpl[N, ?]

object Constructor {
  def apply[N <: Arity](using
      line: sourcecode.Line,
      file: sourcecode.File,
      valueOfN: ValueOf[N]
  )(semantic: SemanticConstructor[N]): Constructor[N] =
    new ConstructorImpl[N, semantic.HeadSort](using semantic.headSort, line, file, valueOfN)(semantic)
}
