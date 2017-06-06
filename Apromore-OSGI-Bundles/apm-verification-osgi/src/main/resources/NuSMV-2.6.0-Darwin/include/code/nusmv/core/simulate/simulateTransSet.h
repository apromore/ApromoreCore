/* ---------------------------------------------------------------------------


  This file is part of the ``simulate'' package of NuSMV version 2.
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
  \brief Pubic interface for class SimulateTransSet

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_SIMULATE_SIMULATE_TRANS_SET_H__
#define __NUSMV_CORE_SIMULATE_SIMULATE_TRANS_SET_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/fsm/bdd/BddFsm.h"
#include "nusmv/core/enc/bdd/BddEnc.h"
#include "nusmv/core/dd/dd.h"


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct SimulateTransSet
  \brief Holds information about states and associated
  transitions. Used during interactive simulation only.

  During simulation several actions can be taken, with
  associated states. This structure holds a set of future states the
  simulation can go in, and for each future state it holds a set of
  associated inputs. This structure is privately used by the function
  that must take a choice about the next state/input pair during
  simulation
*/
typedef struct SimulateTransSet_TAG* SimulateTransSet_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SIMULATE_TRANS_SET(x) \
   ((SimulateTransSet_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SIMULATE_TRANS_SET_CHECK_INSTANCE(x) \
   (nusmv_assert(SIMULATE_TRANS_SET(x) != SIMULATE_TRANS_SET(NULL)))



/*--------------------------------------------------------------------------*/
/* Methods prototypes                                                       */
/*--------------------------------------------------------------------------*/

/*!
  \methodof SimulateTransSet
  \brief Class constructor

  from_state can be NULL when the set of initial states
  must be queried. next_states_count is checked to be in (1,INT_MAX) 
*/
SimulateTransSet_ptr
SimulateTransSet_create(BddFsm_ptr fsm, BddEnc_ptr enc,
                        bdd_ptr from_state, bdd_ptr next_states_set,
                        double next_states_count);

/*!
  \methodof SimulateTransSet
  \brief Class destructor

  
*/
void
SimulateTransSet_destroy(SimulateTransSet_ptr self);

/*!
  \methodof SimulateTransSet
  \brief Getter for the state the transition set is originating from

  Returned BDD is referenced. NULL can be returned if
  this transition set target states are the initial states set
*/
bdd_ptr
SimulateTransSet_get_from_state(const SimulateTransSet_ptr self);

/*!
  \methodof SimulateTransSet
  \brief Returns the cardinality of the target set of states

  
*/
int
SimulateTransSet_get_next_state_num(const SimulateTransSet_ptr self);

/*!
  \methodof SimulateTransSet
  \brief Returns the Nth element of the target set of states

  Returned BDD is referenced
*/
bdd_ptr
SimulateTransSet_get_next_state(const SimulateTransSet_ptr self,
                                int state_index);

/*!
  \methodof SimulateTransSet
  \brief Returns the cardinality of the inputs set going to
  a given state, represented by its index in the set of target states

  Returned BDD is referenced. NULL can be returned
  if self represent the initial states set
*/
int SimulateTransSet_get_inputs_num_at_state(const SimulateTransSet_ptr self, int state_index);

/*!
  \methodof SimulateTransSet
  \brief Returns the Ith input from the set of inputs
  going to the Nth state in the set of target states

  
*/
bdd_ptr
SimulateTransSet_get_input_at_state(const SimulateTransSet_ptr self,
                                    int state_index, int input_index);

/*!
  \methodof SimulateTransSet
  \brief 

  Returned value is the maximum index that can be chosen by
  user in interactive mode
*/
int
SimulateTransSet_print(const SimulateTransSet_ptr self,
                       boolean show_changes_only, OStream_ptr output);

/*!
  \methodof SimulateTransSet
  \brief 

  Index is the number corresponding to the index the user
  chose after having seen the result of the print method. state and
  input will contain referenced bdds representing the chose
  state-input pair, but input might be NULL for the initial state
*/
void
SimulateTransSet_get_state_input_at(const SimulateTransSet_ptr self,
                                    int index,
                                    bdd_ptr* state, bdd_ptr* input);

/*!
  \methodof SimulateTransSet
  \brief 

  
*/
void
SimulateTransSet_get_state_input_rand(const SimulateTransSet_ptr self,
                                      bdd_ptr* state, bdd_ptr* input);

/*!
  \methodof SimulateTransSet
  \brief 

  
*/
void
SimulateTransSet_get_state_input_det(const SimulateTransSet_ptr self,
                                     bdd_ptr* state, bdd_ptr* input);


#endif /* __NUSMV_CORE_SIMULATE_SIMULATE_TRANS_SET_H__ */
