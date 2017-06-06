/* ---------------------------------------------------------------------------


  This file is part of the ``wff'' package.
  %COPYRIGHT%


-----------------------------------------------------------------------------*/

/*!
  \author Michele Dorigatti
  \brief \todo: Missing synopsis

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_WFF_WFF_REWRITE_H__
#define __NUSMV_CORE_WFF_WFF_REWRITE_H__

#include "nusmv/core/utils/defs.h"
#include "nusmv/core/utils/Pair.h"
#include "nusmv/core/compile/FlatHierarchy.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief Enumeration of possible sub-expressions kinds


*/

enum WffRewriteFormulaKind_TAG {
  WFF_REWRITE_FORMULA_KIND_FIRST = 0,
  WFF_REWRITE_FORMULA_KIND_STATE = 1, /* non-temporal and without input vars or
                                      next operator */
  WFF_REWRITE_FORMULA_KIND_INPUT = 1 << 1, /* non-temporal but with input vars */
  WFF_REWRITE_FORMULA_KIND_NEXT = 1 << 2, /* non-temporal but with next operator */
  WFF_REWRITE_FORMULA_KIND_INPUT_NEXT =
  WFF_REWRITE_FORMULA_KIND_INPUT |
  WFF_REWRITE_FORMULA_KIND_NEXT, /* non-temporal but with input vars
                                           and next operator */
  WFF_REWRITE_FORMULA_KIND_TEMP = 1 << 3, /* temporal (it must be without input vars or
                                              next operator) */
  WFF_REWRITE_FORMULA_KIND_LAST = 1 << 4
};
typedef enum WffRewriteFormulaKind_TAG WffRewriteFormulaKind;

/*!
  \brief Rewriting methods


  WFF_REWRITE_STANDARD:

  The LTL formula is rewritten by substituting a fresh boolean state
  variable sv for Phi and adding a new transition relation TRANS sv
  <-> Phi. For example, LTL formula

  G (s < i);

  becomes

  G sv;

  and the model is augmented by

  VAR sv : boolean;
  TRANS sv <-> (s < i);

  Note 1: new deadlocks are introduced after the rewriting (because
  new vars are assigned a value before the value of input vars are
  known).  For example, with "TRANS s <i" the original model does
  not have a deadlock but after rewriting it does.  For BDD LTL this
  is not a problem because all paths with deadlocks are ignored and
  all original paths are kept by the rewriting.


  Note 2: the validity of an old and a new LTL formulas is the same
  on *infinite* paths. On finite paths the semantics of formulas is
  different because of the deadlocks.  For above example, if there
  is additionally "TRANS s < i" then on infinite paths "G sv" and "G
  s < i" are both valid whereas there is finite path which violate
  "G sv" and there is NO such finite path for "G s<i".

  This thing happens with BMC (which looks for finite path violating
  a formula) vs BDD (which checks only infinite paths). See next
  rewrite method for a possible solution.

  WFF_REWRITE_DEADLOCK_FREE method:

  The LTL formula is rewriten by substituting Phi with "X sv", where
  sv is a fresh boolean state variable, and adding a new transition
  relation "TRANS next(sv) <-> Phi" and a new initial condition
  "INIT sv"; For example, LTL formula

  G (s < i)

  becomes

  G (X sv)

  and the model is augmented by

  VAR sv : boolean;
  INIT sv;
  TRANS next(sv) <-> (s < i);
*/
typedef enum {
  WFF_REWRITE_METHOD_STANDARD,
  WFF_REWRITE_METHOD_DEADLOCK_FREE,
} WffRewriteMethod;


/*! \brief The type of expected input property.

  The input property to be converted is a generalized invar
  (WFF_REWRITER_REWRITE_INPUT_NEXT) with at least one next/input
  variable in the invariant, or an LTL property to be converted if
  possible into an invariant to be checked (modulo a proper extension
  of the transition system (e.g. p -> G q, .. G(p) -> G(q)), ....)
*/
typedef enum {
  WFF_REWRITER_REWRITE_INPUT_NEXT,
  WFF_REWRITER_LTL_2_INVAR
} WffRewriterExpectedProperty;

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Return an equivalent specification, without input vars
  and next operators

  The equivalence is kept by making side effects on layer
  and outfh.
  layer must belong to the outfh symbol table.

  \se layer and outfh are possibly modified

  \sa Prop_Rewriter_private.h
*/
Pair_ptr Wff_Rewrite_rewrite_formula(NuSMVEnv_ptr const env,
                                            const WffRewriteMethod method,
                                            const WffRewriterExpectedProperty eproptype,
                                            SymbLayer_ptr layer,
                                            FlatHierarchy_ptr outfh,
                                            node_ptr const spec,
                                            const short int spec_type);

/*!
  \brief Return an equivalent specification, without input vars
  and next operators

  The equivalence is kept by making side effects on layer
  and outfh. layer must belong to the outfh symbol table. When the
  WFF_REWRITE_METHOD_DEADLOCK_FREE is selected, then
  initialize_monitor_to_true control the value the monitor variable
  is initialized to. I.e. if true it is initialized to TRUE, else to FALSE.

  \se layer and outfh are possibly modified

  \sa Prop_Rewriter_private.h
*/
Pair_ptr Wff_Rewrite_rewrite_formula_generic(NuSMVEnv_ptr const env,
                                             const WffRewriteMethod method,
                                             const WffRewriterExpectedProperty eproptype,
                                             SymbLayer_ptr layer,
                                             FlatHierarchy_ptr outfh,
                                             node_ptr const spec,
                                             const short int spec_type,
                                             const boolean initialize_monitor_to_true,
                                             const boolean ltl2invar_negate_property);

/*!
  \brief Checks if "wff" contains input variables or next
  operators, i.e. it is a generalized wff

*/
boolean Wff_Rewrite_is_rewriting_needed(SymbTable_ptr st, node_ptr wff,
                                               node_ptr context);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_WFF_WFF_REWRITE_H__ */
