/* ---------------------------------------------------------------------------


  This file is part of the ``enc.bdd'' package of NuSMV version 2.
  Copyright (C) 2004 by FBK-irst.

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
  \brief Private and protected interface of class 'BddEnc'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_ENC_BDD_BDD_ENC_PRIVATE_H__
#define __NUSMV_CORE_ENC_BDD_BDD_ENC_PRIVATE_H__


#include "nusmv/core/enc/bdd/BddEnc.h"
#include "nusmv/core/enc/bdd/BddEncCache.h"

#include "nusmv/core/enc/base/BoolEncClient.h"
#include "nusmv/core/enc/base/BoolEncClient_private.h"

#include "nusmv/core/utils/array.h"
#include "nusmv/core/utils/utils.h"

/*!
  \brief Initial size of dynamic arrays containing variable indices.

  Initial size of dynamic arrays containing variable indices.
*/
#define BDD_ENC_INIT_VAR_NUM 4096


/*!
  \brief BddEnc class definition derived from
               class BoolEncClient



  \sa Base class BoolEncClient
*/

typedef struct BddEnc_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(BoolEncClient);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  TypeChecker_ptr type_checker; /* used to get the type of expression */

  VarsHandler_ptr dd_vars_hndr;
  DDMgr_ptr dd; /* this field is here only for performances */

  BddEncCache_ptr cache;

  /* used for grouping of vars */
  OrdGroups_ptr ord_groups;
  hash_ptr layer2groups;

  /* used to shuffle the variable ordering */
  array_t* level2index; /* array of int */

  /* used to lock/unlock vars ordering: */
  dd_reorderingtype curr_reord_type;
  int reord_status;
  int reord_locked_num;
  int curr_reorderings; /* number of reorderings so far */

  /* number of variables: */
  int input_vars_num;
  int state_vars_num;
  int frozen_vars_num;

  /* The array of symbolic variable names. Each element i contains the
     symbolic name associated to the variable with index i */
  array_t* index2name; /* array of node_ptr */
  hash_ptr name2index;

  /* These arrays are used to maintain correspondence between current
  and next variables. Position i contains the index of the
  corresponding next state variable. They are used to perform forward
  and backward shifting respectively */
  array_t* current2next; /* array of int */
  array_t* next2current; /* array of int */

  /* Arrays used to pick up a minterm from a given BDD. These arrays
     should contain at least all variables in the support of the BDD
     which we want extract a minterm of. When a layer is removed,
     these arrays will be compacted, i.e. no gaps are allowed to
     exist at any time.
     Associtated to each array there is the current frontier. */
  array_t* minterm_input_vars; /* array of bdd_ptr */
  int minterm_input_vars_dim;

  /* array sizes are kept explicit here because due to compaction
     array size and size can be different */

  array_t* minterm_state_vars; /* array of bdd_ptr */
  int minterm_state_vars_dim;

  array_t* minterm_next_state_vars; /* array of bdd_ptr */
  int minterm_next_state_vars_dim;

  array_t* minterm_frozen_vars; /* array of bdd_ptr */
  int minterm_frozen_vars_dim;

  array_t* minterm_state_frozen_vars; /* array of bdd_ptr */
  int minterm_state_frozen_vars_dim;

  array_t* minterm_state_frozen_input_vars; /* array of bdd_ptr */
  int minterm_state_frozen_input_vars_dim;


  /* This list is intended to hold the indices of variables there were
     removed and are then available for reusing. If this list is empty,
     there are no gaps at all, and the next available index will be taken
     from the number of currently allocated variables. This list
     keeps the indices ordered. */
  NodeList_ptr index_gaps;

  /* Contains the maximum index that has been used so far to allocate
     new vars. If there are gaps (i.e. removed vars not yet reused),
     this value is greater than the number of variables currently
     allocated. This index is used to allocate new indices when gaps
     are not available for reuse. Notice that index 0 is never used
     for variables, as it seems to be reserved by cudds.

     DO NOT USE this field directly, call methods
     bdd_enc_get_avail_state_var_index and bdd_enc_get_avail_input_var_index
     instead. */
  int used_indices_frontier;

  /* These are the cubes of input, state current and state next vars.
     When a new var is added, only corresponding ADD cubes will be
     modified, the construction of the BDD is delayed until the BDD
     cube is required: */

  /* 1. The cube of input variables to be used in image forward and
     backward */
  add_ptr input_vars_add;
  bdd_ptr input_vars_bdd;

  /* 2. The cube of state variables to be used in image forward */
  add_ptr state_vars_add;
  bdd_ptr state_vars_bdd;

  /* 3. The cube of state variables to be used in image backward */
  add_ptr next_state_vars_add;
  bdd_ptr next_state_vars_bdd;

  /* 4. The cube of frozen variables */
  add_ptr frozen_vars_add;
  bdd_ptr frozen_vars_bdd;

  /* 5. The cube of current state variables and frozen variables to be
     used in image forward. This is computed out of state and frozen
     vars cubes */
  bdd_ptr state_frozen_vars_bdd;

  /* This is a stack of instances of class BddEncPrintInfo, used
     to print Bdds */
  node_ptr print_stack;

  /* This variable is used to control the behavior of the
     method bdd_enc_eval (specifically the behavior of its subroutine
     get_definition). */
  boolean enforce_constant;


  /* Masks: */
  add_ptr input_vars_mask_add;
  add_ptr state_frozen_vars_mask_add;
  add_ptr state_frozen_input_vars_mask_add;

  bdd_ptr input_vars_mask_bdd;
  bdd_ptr state_frozen_vars_mask_bdd;
  bdd_ptr state_frozen_input_vars_mask_bdd;


  /* To check failure leaves quickly */
  hash_ptr failures_hash;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} BddEnc;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_ENC_EVALUATING (ADD_ARRAY(-1))


/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only        */
/* ---------------------------------------------------------------------- */
/*!
  \methodof BddEnc
  \todo
*/
void bdd_enc_init(BddEnc_ptr self,
                  SymbTable_ptr symb_table,
                  BoolEnc_ptr bool_enc, VarsHandler_ptr dd_vars_hdlr,
                  OrdGroups_ptr ord_groups);

/*!
  \methodof BddEnc
  \todo
*/
void bdd_enc_deinit(BddEnc_ptr self);

void bdd_enc_commit_layer(BaseEnc_ptr enc_base, const char* layer_name);

void bdd_enc_remove_layer(BaseEnc_ptr enc_base, const char* layer_name);


/*
   Later on this method should be moved to public interface.
   At the moment it is used by GAME addon
*/
/*!
  \methodof BddEnc
  \todo
*/
void bdd_enc_shuffle_variables_order(BddEnc_ptr self,
                                     NodeList_ptr vars);
#endif /* __NUSMV_CORE_ENC_BDD_BDD_ENC_PRIVATE_H__ */
