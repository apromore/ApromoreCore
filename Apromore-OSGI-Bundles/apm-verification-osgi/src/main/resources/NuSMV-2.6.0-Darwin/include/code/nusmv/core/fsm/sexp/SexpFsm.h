/* ---------------------------------------------------------------------------


  This file is part of the ``fsm.sexp'' package of NuSMV version 2.
  Copyright (C) 2003 by FBK-irst.

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
  \author Roberto Cavada
  \brief The SexpFsm API

  Class SexpFsm declaration

*/


#ifndef __NUSMV_CORE_FSM_SEXP_SEXP_FSM_H__
#define __NUSMV_CORE_FSM_SEXP_SEXP_FSM_H__

#include "nusmv/core/fsm/sexp/sexp.h"
#include "nusmv/core/wff/ExprMgr.h"

#include "nusmv/core/set/set.h"
#include "nusmv/core/compile/FlatHierarchy.h"
#include "nusmv/core/compile/symb_table/SymbLayer.h"
#include "nusmv/core/compile/PredicateNormaliser.h"
#include "nusmv/core/enc/bdd/BddEnc.h"

/*!
  \struct SexpFsm
  \brief The SexpFsm type 

  The SexpFsm type 
*/
typedef struct SexpFsm_TAG* SexpFsm_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SEXP_FSM(x) \
         ((SexpFsm_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SEXP_FSM_CHECK_INSTANCE(x) \
         (nusmv_assert(SEXP_FSM(x) != SEXP_FSM(NULL)))

/*---------------------------------------------------------------------------*/
/* Public Function Interface                                                 */
/*---------------------------------------------------------------------------*/

/* constructors */

/*!
  \methodof SexpFsm
  \brief Costructor for a scalar sexp fsm

  Given hierarchy will be copied, so the caller is
  responsible for its destruction. Vars set is also copied, so the
  caller is responsible for its destruction (best if frozen)
*/
SexpFsm_ptr
SexpFsm_create(const FlatHierarchy_ptr hierarchy,
               const Set_t vars_set);

/*!
  \methodof SexpFsm
  \brief Copy costructor

  
*/
SexpFsm_ptr SexpFsm_copy(const SexpFsm_ptr self);

/* convertion to predicate-normalised FSM */

/*!
  \methodof SexpFsm
  \methodof SexpFsm
  \brief Copy the Sexp FSM and perform predicate-normalisation
  on all the expressions.

  Predicate-normalisations means that an expression is
  modified in such a way that at the end the subexpressions of a
  not-boolean expression can be only not-boolean. This is performed by
  changing boolean expression "exp" (which is a subexpression of a
  not-boolean expression) to "ITE(exp, 1, 0)", and then pushing all
  ITE up to the root of not-boolean expressions.

  Constrain: the given Sexp FSM has to be NOT boolean. Otherwise,
  it is meaningless to apply normalisation functions, since all the exporessions
  are already boolean.
  

  \se SexpFsm_copy
*/
SexpFsm_ptr SexpFsm_create_predicate_normalised_copy(const SexpFsm_ptr self,
PredicateNormaliser_ptr normaliser);

/* deconstructors */

/*!
  \methodof SexpFsm
  \brief Destructor

  
*/
void SexpFsm_destroy(SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief Returns the symbol table that is connected to the
  BoolEnc instance connected to self

  This method can be called only when a valid BddEnc was
  passed to the class constructor (not NULL). Returned instance do not
  belongs to the caller and must _not_ be destroyed
*/
SymbTable_ptr SexpFsm_get_symb_table(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief Use to check if this FSM is a scalar or boolean fsm

  Since a BoolSexpFsm derives from SexpFsm, a SexpFsm
                      is not necessarily a scalar fsm. Use this
                      method to distinguish scalar from boolean fsm
                      when dealing with generic SexpFsm pointers. 
*/
boolean SexpFsm_is_boolean(const SexpFsm_ptr self);

/* access functions */

/*!
  \methodof SexpFsm
  \brief Returns the internal complete hierarchy

  Returned hierarchy belongs to self and cannot be
  freely changed without indirectly modifying self as well. Copy
  the returned hierarchy before modifying it if you do not want to
  change self.  Also, notice that the SexpFsm constructor copies
  the passed hierarchy.
*/
FlatHierarchy_ptr
SexpFsm_get_hierarchy(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief Returns an Expr that collects init states for all
  variables handled by self

  
*/
Expr_ptr SexpFsm_get_init(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief Returns an Expr that collects invar states for all
  variables handled by self

  
*/
Expr_ptr SexpFsm_get_invar(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief Returns an Expr that collects all next states for all
  variables handled by self

  
*/
Expr_ptr SexpFsm_get_trans(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief Returns an Expr that collects all input states for all
  variables handled by self

  
*/
Expr_ptr SexpFsm_get_input(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief  Gets the list of sexp expressions defining the set of justice
                  constraints for this machine. 

   Gets the list of sexp expressions defining the set of justice
                  constraints for this machine. 
*/
node_ptr SexpFsm_get_justice(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief  Gets the list of sexp expressions defining the set of
                  compassion constraints for this machine. 

   Gets the list of sexp expressions defining the set of
                  compassion constraints for this machine. 
*/
node_ptr SexpFsm_get_compassion(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief Returns the set of variables in the FSM

  Returned instance belongs to self. Do not change not free it.
*/
NodeList_ptr SexpFsm_get_vars_list(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief Returns the set of symbols in the FSM

  Returned instance belongs to self. Do not change not free it.
*/
NodeList_ptr SexpFsm_get_symbols_list(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief Returns the set of variables in the FSM

  Returned instance belongs to self. Do not change not free it.
*/
Set_t SexpFsm_get_vars(const SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief Performs the synchronous product of two FSMs

  The result goes into self, no changes to other.

  \se self will change
*/
void
SexpFsm_apply_synchronous_product(SexpFsm_ptr self,
                                  SexpFsm_ptr other);

/*!
  \methodof SexpFsm
  \brief Checks if the SexpFsm is syntactically universal

  Checks if the SexpFsm is syntactically universal:
                       Checks INIT, INVAR, TRANS, INPUT, JUSTICE,
                       COMPASSION to be empty (ie: True Expr). In this
                       case returns true, false otherwise
*/
boolean
SexpFsm_is_syntactically_universal(SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief  Gets the sexp expression defining the initial state for
                  the variable "v". 

   Gets the sexp expression defining the initial state for
                  the variable "v". 
*/
Expr_ptr
SexpFsm_get_var_init(const SexpFsm_ptr self, node_ptr v);

/*!
  \methodof SexpFsm
  \brief  Gets the sexp expression defining the state constraints
                  for the variable "v". 

   Gets the sexp expression defining the state constraints
                  for the variable "v". 
*/
Expr_ptr
SexpFsm_get_var_invar(const SexpFsm_ptr self, node_ptr v);

/*!
  \methodof SexpFsm
  \brief  Gets the sexp expression defining the transition relation
                  for the variable "v". 

   Gets the sexp expression defining the transition relation
                  for the variable "v". 
*/
Expr_ptr
SexpFsm_get_var_trans(const SexpFsm_ptr self, node_ptr v);

/*!
  \methodof SexpFsm
  \brief  Gets the sexp expression defining the input relation
                  for the variable "v". 

  
*/
Expr_ptr
SexpFsm_get_var_input(const SexpFsm_ptr self, node_ptr v);

/*!
  \methodof SexpFsm
  \brief Self-check for the instance

  
*/
void SexpFsm_self_check(const SexpFsm_ptr self);


#endif /* __NUSMV_CORE_FSM_SEXP_SEXP_FSM_H__ */
