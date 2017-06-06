/* ---------------------------------------------------------------------------


  This file is part of the ``simulate'' package of NuSMV version 2.
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
  \author Andrea Morichetti
  \brief External Header File for MC Simulator

  External Header File for simulation package: simulation
  package provides a set of utilities for traces generation (a trace is a
  possible execution of the model). It performs initial state picking,
  trace inspection, simulation according to different policies (deterministic,
  random, interactive) and with the possibility to specify constraints.

*/


#ifndef __NUSMV_CORE_SIMULATE_SIMULATE_H__
#define __NUSMV_CORE_SIMULATE_SIMULATE_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/fsm/bdd/BddFsm.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/trace/TraceMgr.h"
#include "nusmv/core/trace/TraceLabel.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_SIMULATE_STATE  "esimulatestate"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum {Deterministic, Random, Interactive} Simulation_Mode;

/*--------------------------------------------------------------------------*/
/* Function prototypes                                                      */
/*--------------------------------------------------------------------------*/

/*!
  \brief Chooses one state among future states

  Chooses a state among future states depending on the
  given simulation policy (random, deterministic or interactive). In case of
        interactive simulation, the system stops and allows the user to pick
        a state from a list of possible items. If the number of future states
        is too high, the system requires some further constraints to limit that
        number and will asks for them until the number of states is lower than
        an internal threshold. Entered expressions are accumulated in one big
        constraint used only in the actual step of the simulation. It will be
        discarded after a state will be chosen.

  \se A referenced state (BDD) is returned. NULL if failed.

  \sa Simulate_MultipleStep
*/
bdd_ptr
Simulate_ChooseOneState(NuSMVEnv_ptr, BddFsm_ptr, bdd_ptr,
                        Simulation_Mode, int);

/*!
  \brief 

  
*/
void
Simulate_ChooseOneStateInput(NuSMVEnv_ptr env, BddFsm_ptr,
                             bdd_ptr, bdd_ptr,
                             Simulation_Mode, int,
                             bdd_ptr*,  bdd_ptr*);

/*!
  \brief Multiple step simulation

  Multiple step simulation: loops n times over the choice of
  a state according to the picking policy given at command line. It returns a
  list of at least n+1 referenced states (the first one is always the "current
  state" from which any simulation must start). The obtained list can contain
  a minor number of states if there are no future states at some point.

  \sa Simulate_ChooseOneState
*/
node_ptr
Simulate_MultipleSteps(NuSMVEnv_ptr, BddFsm_ptr, bdd_ptr, boolean,
                       Simulation_Mode, int, int);

/*!
  \brief Picks a state from the set of initial states

  Picks a state from the set of initial states
*/
int
Simulate_pick_state(const NuSMVEnv_ptr env,
                    TraceLabel label,
                    const Simulation_Mode mode,
                    const int display_all, const boolean verbose,
                    bdd_ptr bdd_constraints);

/*!
  \brief Performs a simulation from the current selected state

  Performs a simulation from the current selected state
*/
int
Simulate_simulate(const NuSMVEnv_ptr env,
                  const boolean time_shift, const Simulation_Mode mode,
                  const int steps, const int display_all,
                  const boolean printrace, const boolean only_changes,
                  const bdd_ptr bdd_constr);

/*!
  \brief Goes to a given state of a trace

  "label" representes the new current state
*/
int Simulate_goto_state(NuSMVEnv_ptr env,
                               TraceLabel label);

/*!
  \brief Prints the current state

  If not "Verbosely", it prints just the label
*/
int Simulate_print_current_state(NuSMVEnv_ptr env,
                                        boolean Verbosely);

/*!
  \brief Package init

  Package init
*/
void Simulate_Pkg_init(NuSMVEnv_ptr env);

/*!
  \brief Package deinit

  Package deinit
*/
void Simulate_Pkg_quit(NuSMVEnv_ptr env);

/*!
  \brief Creates a new trace from given label represented as a string,
  and returns created TraceLabel (NULL if an error occurs)
  and the trace number (-1 if an error occurs). There is no need to free
  TraceLabel

  
*/
TraceLabel
Simulate_get_new_trace_no_from_label(NuSMVEnv_ptr env,
                                     TraceMgr_ptr gtm,
                                     const char* str_label,
                                     int* out_tr_number);

/*!
  \brief Converts given constraint expression (as a string) to
                      a bdd

  Input variables are allowed to occur in the passed
                      constraint iff allow_inputs is true.

                      Next operators are allowed to occur in the passed
                      constraint iff allow_nexts is true.

                      If an error occurs, NULL is returned and a
                      message is printed.

                      This function does not raises any
                      exception. Returned BDD must be freed by the
                      caller. In error messages it is assumed that
                      constr_str is read from the command line.
*/
bdd_ptr simulate_get_constraints_from_string(NuSMVEnv_ptr env,
                                             const char* constr_str,
                                             BddEnc_ptr enc,
                                             boolean allow_nexts,
                                             boolean allow_inputs);


#endif /* __NUSMV_CORE_SIMULATE_SIMULATE_H__ */
