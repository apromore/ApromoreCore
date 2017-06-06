/* ---------------------------------------------------------------------------


  This file is part of the ``fsm'' package of NuSMV version 2.
  Copyright (C) 2006 by FBK-irst.

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
  \brief Public interface for a high level object that can contruct
  FSMs

  Declares the interface of an high-level object that
  lives at top-level, that is used to help contruction of FSMs.  It
  can control information that are not shared between lower levels, so
  it can handle with objects that have not the full knowledge of the
  whole system

*/




#ifndef __NUSMV_CORE_FSM_FSM_BUILDER_H__
#define __NUSMV_CORE_FSM_FSM_BUILDER_H__

#include "nusmv/core/fsm/sexp/SexpFsm.h"
#include "nusmv/core/fsm/sexp/BoolSexpFsm.h"
#include "nusmv/core/fsm/bdd/BddFsm.h"

#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/trans/bdd/ClusterList.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct FsmBuilder
  \brief FSM builder class constructor

  
*/
typedef struct FsmBuilder_TAG* FsmBuilder_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FSM_BUILDER(x) \
         ((FsmBuilder_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FSM_BUILDER_CHECK_INSTANCE(x) \
         ( nusmv_assert(FSM_BUILDER(x) != FSM_BUILDER(NULL)) )


/* ---------------------------------------------------------------------- */
/* Public interface                                                       */
/* ---------------------------------------------------------------------- */

/*!
  \methodof FsmBuilder
  \brief The constructor creates a BddEnc and self handles it

  Environment requisites: None, but for later uses
                      will need some basic structures (NodeMgr,
                      ErrorMgr, DDMgr, OptsHandler, StreamMgr)
*/
FsmBuilder_ptr
FsmBuilder_create(NuSMVEnv_ptr env);

/*!
  \methodof FsmBuilder
  \brief Class FsmBuilder destructor

  
*/
void  FsmBuilder_destroy(FsmBuilder_ptr self);

/*!
  \methodof FsmBuilder
  \brief Creates a new scalar sexp fsm

  The caller becomes the owner of the returned object
*/
SexpFsm_ptr
FsmBuilder_create_scalar_sexp_fsm(const FsmBuilder_ptr self,
                                  FlatHierarchy_ptr flat_hierarchy,
                                  const Set_t vars_list);

/*!
  \methodof FsmBuilder
  \brief Creates a new boolean sexp fsm, taking into account of
                      the current variables ordering, or the trans
                      ordering file when specified by the
                      user. When used, the latter overrides the
                      former.

  The caller becomes the owner of the returned object.
                      An exception may occur if the trans cluster
                      ordering is specified and an error occurs
                      while parsing it.
*/
BoolSexpFsm_ptr
FsmBuilder_create_boolean_sexp_fsm(const FsmBuilder_ptr self,
                                   FlatHierarchy_ptr flat_hierarchy,
                                   const Set_t vars,
                                   BddEnc_ptr bdd_enc,
                                   SymbLayer_ptr det_layer);

/*!
  \methodof FsmBuilder
  \brief Creates a BddFsm instance from a given SexpFsm

  
  Note: all variables from provided encoding will go to the BDD FSM.
  Use FsmBuilder_create_bdd_fsm_of_vars if only SOME variables should be taken
  into account.
*/
BddFsm_ptr
FsmBuilder_create_bdd_fsm(const FsmBuilder_ptr self,
                          BddEnc_ptr enc,
                          const SexpFsm_ptr sexp_fsm,
                          const TransType trans_type);

/*!
  \methodof FsmBuilder
  \brief Creates a BddFsm instance from a given SexpFsm

  It is the same as FsmBuilder_create_bdd_fsm except that
  the cubes of state, input and next-state variables are given explicitly.

  Note: The functions will take a copy of provided cubes.
*/
BddFsm_ptr
FsmBuilder_create_bdd_fsm_of_vars(const FsmBuilder_ptr self,
                                  const SexpFsm_ptr sexp_fsm,
                                  const TransType trans_type,
                                  BddEnc_ptr enc,
                                  BddVarSet_ptr state_vars_cube,
                                  BddVarSet_ptr input_vars_cube,
                                  BddVarSet_ptr next_state_vars_cube);

/*!
  \methodof FsmBuilder
  \brief Given an expression, returns a bdd ClusterList with
  each conjuction occurring into expr contained in each cluster of
  the list. 

  Each cluster into the list represents a piece of
  transition relation. If the given expression contains
  duplicates, they will not occur into the returned cluster
  list. Returned list should be destroyed by the caller.
*/
ClusterList_ptr
FsmBuilder_clusterize_expr(FsmBuilder_ptr self,
                           BddEnc_ptr enc, Expr_ptr expr);

#endif /* __NUSMV_CORE_FSM_FSM_BUILDER_H__ */
