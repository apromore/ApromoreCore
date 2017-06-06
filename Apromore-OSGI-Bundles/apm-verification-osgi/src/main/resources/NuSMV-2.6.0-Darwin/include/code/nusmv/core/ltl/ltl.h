/* ---------------------------------------------------------------------------


  This file is part of the ``ltl'' package of NuSMV version 2.
  Copyright (C) 1998-2001 by CMU and FBK-irst.

  NuSMV version 2 is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2 of the License, or (at your option) any later version.

  NuSMV version 2 is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

  For more information on NuSMV see <http://nusmv.fbk.eu>
  or email to <nusmv-users@fbk.eu>.
  Please report bugs to <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to <nusmv@fbk.eu>. 

-----------------------------------------------------------------------------*/

/*!
  \author Marco Roveri
  \brief Routines to handle with LTL model checking.

  Here we perform the reduction of LTL model checking to
  CTL model checking. The technique adopted has been taken from \[1\].
  <ol>
    <li>O. Grumberg E. Clarke and K. Hamaguchi. "Another Look at LTL Model Checking".
       <em>Formal Methods in System Design</em>, 10(1):57--71, February 1997.</li>
  </ol>
  

*/


#ifndef __NUSMV_CORE_LTL_LTL_H__
#define  __NUSMV_CORE_LTL_LTL_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/prop/Prop.h"
#include "nusmv/core/wff/ExprMgr.h"
#include "nusmv/core/trace/Trace.h"
#include "nusmv/core/utils/OStream.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct Ltl_StructCheckLtlSpec
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct Ltl_StructCheckLtlSpec_TAG* Ltl_StructCheckLtlSpec_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef node_ptr (*Ltl_StructCheckLtlSpec_oreg2smv)(NuSMVEnv_ptr,
                                                    unsigned int,
                                                    node_ptr);
typedef node_ptr (*Ltl_StructCheckLtlSpec_ltl2smv)(NuSMVEnv_ptr,
                                                   unsigned int,
                                                   node_ptr);

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LTL_STRUCTCHECKLTLSPEC(self) \
         ((Ltl_StructCheckLtlSpec_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LTL_STRUCTCHECKLTLSPEC_CHECK_INSTANCE(self) \
         (nusmv_assert(LTL_STRUCTCHECKLTLSPEC(self) != \
          LTL_STRUCTCHECKLTLSPEC(NULL)))


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Print the LTL specification.

  Print the LTL specification.
*/
void print_ltlspec(OStream_ptr, Prop_ptr, Prop_PrintFmt);

/*!
  \brief The main routine to perform LTL model checking.

  The main routine to perform LTL model checking. It
  first takes the LTL formula, prints it in a file. It calls the LTL2SMV
  translator on it an reads in the generated tableau. The tableau is
  instantiated, compiled and then conjoined with the original model
  (both the set of fairness conditions and the transition relation are
  affected by this operation, for this reason we save the current
  model, and after the verification of the property we restore the
  original one).

  If already set (The Scalar and the Bdd ones, the FSMs used for
  verification are taken from within the property. Otherwise, global
  FSMs are set within the property and then used for verification.
*/
void Ltl_CheckLtlSpec(NuSMVEnv_ptr env, Prop_ptr prop);

/*!
  \brief Takes a formula (with context) and constructs the flat
  hierarchy from it.

  Description        []

  SideEffects        [layer and outfh are expected to get changed]

  SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  

  \se layer and outfh are expected to get changed
*/
void
Ltl_spec_to_hierarchy(NuSMVEnv_ptr env,
                      Expr_ptr spec, node_ptr context,
                      node_ptr (*what2smv)(NuSMVEnv_ptr env,
                                           unsigned int id,
                                           node_ptr expr),
                      SymbLayer_ptr layer,
                      FlatHierarchy_ptr outfh);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
Expr_ptr
Ltl_apply_input_vars_rewriting(Expr_ptr spec, SymbTable_ptr st,
                               SymbLayer_ptr layer,
                               FlatHierarchy_ptr outfh);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Create an empty Ltl_StructCheckLtlSpec structure.

  Create an empty Ltl_StructCheckLtlSpec structure.
*/
Ltl_StructCheckLtlSpec_ptr Ltl_StructCheckLtlSpec_create(NuSMVEnv_ptr env,
                                                                Prop_ptr prop);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Desrtroy an Ltl_StructCheckLtlSpec structure.

  Desrtroy an Ltl_StructCheckLtlSpec structure.
*/
void Ltl_StructCheckLtlSpec_destroy(Ltl_StructCheckLtlSpec_ptr self);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Set the oreg2smv field of an Ltl_StructCheckLtlSpec structure

  Set the oreg2smv field of an Ltl_StructCheckLtlSpec structure
*/
void Ltl_StructCheckLtlSpec_set_oreg2smv(Ltl_StructCheckLtlSpec_ptr self,
                                                Ltl_StructCheckLtlSpec_oreg2smv oreg2smv);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Set the ltl2smv field of an Ltl_StructCheckLtlSpec structure

  Set the ltl2smv field of an Ltl_StructCheckLtlSpec structure
*/
void Ltl_StructCheckLtlSpec_set_ltl2smv(Ltl_StructCheckLtlSpec_ptr self,
                                               Ltl_StructCheckLtlSpec_ltl2smv ltl2smv);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Set the negate_formula field of an Ltl_StructCheckLtlSpec structure

  Set the negate_formula field of an Ltl_StructCheckLtlSpec structure
*/
void Ltl_StructCheckLtlSpec_set_negate_formula(Ltl_StructCheckLtlSpec_ptr self,
                                                      boolean negate_formula);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Set the do_rewriting field of an Ltl_StructCheckLtlSpec
  structure

  Set the do_rewriting field of an Ltl_StructCheckLtlSpec
  structure
*/
void Ltl_StructCheckLtlSpec_set_do_rewriting(Ltl_StructCheckLtlSpec_ptr self,
                                                   boolean do_rewriting);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Get the s0 field of an Ltl_StructCheckLtlSpec structure

  Get the s0 field of an Ltl_StructCheckLtlSpec structure
  Returned bdd is NOT referenced.
*/
bdd_ptr Ltl_StructCheckLtlSpec_get_s0(Ltl_StructCheckLtlSpec_ptr self);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Get the s0 field purified by tableu variables

  Get the s0 field  of an Ltl_StructCheckLtlSpec structure
  purified by tableu variables
*/
bdd_ptr Ltl_StructCheckLtlSpec_get_clean_s0(Ltl_StructCheckLtlSpec_ptr self);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Initialize the structure by computing the tableau for
  the LTL property

  Initialize the structure by computing the tableau for
  the LTL property and computing the cross-product with the FSM of the model.
*/
void Ltl_StructCheckLtlSpec_build(Ltl_StructCheckLtlSpec_ptr self);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Perform the check to see wether the property holds or not

  Perform the check to see wether the property holds or not.
  Assumes the Ltl_StructcCheckLtlSpec structure being initialized before with
  Ltl_StructCheckLtlSpec_build.

  If compassion is present it calls the check method for compassion,
  otherwise the check method dedicated to the algorithm given by the
  value of the oreg_justice_emptiness_bdd_algorithm option. 

  \sa ltl_stuctcheckltlspec_check_compassion,
  ltl_structcheckltlspec_check_el_bwd, ltl_structcheckltlspec_check_el_fwd
*/
void Ltl_StructCheckLtlSpec_check(Ltl_StructCheckLtlSpec_ptr self);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Prints the result of the Ltl_StructCheckLtlSpec_check fun

  Prints the result of the Ltl_StructCheckLtlSpec_check fun
*/
void Ltl_StructCheckLtlSpec_print_result(Ltl_StructCheckLtlSpec_ptr self);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Perform the computation of a witness for a property

  Perform the computation of a witness for a property.
  Assumes the Ltl_StructcCheckLtlSpec structure being initialized before with
  Ltl_StructCheckLtlSpec_build, and that Ltl_StructCheckLtlSpec_build has been
  invoked.
*/
Trace_ptr
Ltl_StructCheckLtlSpec_build_counter_example(Ltl_StructCheckLtlSpec_ptr self,
                                             NodeList_ptr symbols);

/*!
  \methodof Ltl_StructCheckLtlSpec
  \brief Perform the computation of a witness for a property

  Perform the computation of a witness for a property.
  Assumes the Ltl_StructcCheckLtlSpec structure being initialized before with
  Ltl_StructCheckLtlSpec_build, and that Ltl_StructCheckLtlSpec_build has been
  invoked.
*/
void
Ltl_StructCheckLtlSpec_explain(Ltl_StructCheckLtlSpec_ptr self,
                               NodeList_ptr symbols);

#endif /*  __NUSMV_CORE_LTL_LTL_H__ */
