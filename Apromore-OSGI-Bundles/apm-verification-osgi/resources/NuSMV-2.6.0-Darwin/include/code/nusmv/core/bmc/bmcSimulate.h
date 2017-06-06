/* ---------------------------------------------------------------------------


  This file is part of the ``bmc'' package of NuSMV version 2.
  Copyright (C) 2010 FBK-irst.

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
  \brief SAT Based incremental simulation

  SAT Bases incremental simulation

*/


#ifndef __NUSMV_CORE_BMC_BMC_SIMULATE_H__
#define __NUSMV_CORE_BMC_BMC_SIMULATE_H__

#include "cudd/util.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/bmc/bmcInt.h"
#include "nusmv/core/bmc/bmcUtils.h"
#include "nusmv/core/bmc/bmcConv.h"
#include "nusmv/core/trace/pkg_trace.h"
#include "nusmv/core/trace/Trace.h"
#include "nusmv/core/simulate/simulate.h"
#include "nusmv/core/compile/compile.h"
#include "nusmv/core/sat/sat.h"
#include "nusmv/core/utils/Olist.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/


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
  \brief Performs simulation

  Generate a problem with no property, and search for a
   solution, appending it to the current simulation trace.
   Returns 1 if solver could not be created, 0 if everything went smooth

  \se None
*/
int
Bmc_Simulate(NuSMVEnv_ptr env,
             const BeFsm_ptr be_fsm,
             BddEnc_ptr bdd_enc,
             be_ptr constraints,
             boolean time_shift,
             const int k,
             const boolean print_trace,
             const boolean changes_only,
             Simulation_Mode mode);

/*!
  \brief SAT Based Incremental simulation

  This function performs incremental sat based
  simulation up to <tt>target_steps</tt>.

  Simulation starts from an initial state internally selected.

  It accepts a constraint to direct the simulation to paths satisfying
  such constraints. The constraints is assumed to be over state, input
  and next state variables. Thus, please carefully consider this
  information while providing constraints to this routine.

  The simulation stops if either the <tt>target_steps</tt> steps of
  simulation have been performed, or the simulation bumped in a
  deadlock (that might be due to the constraints that are too strong).

  Parameters:

  'print_trace'  : shows the generated trace
  'changes_only' : shows only variables that actually change value
  between one step and it's next one

  \se The possibly partial generated simulation trace
  is added to the trace manager for possible reuse.

  \sa optional
*/
int
Bmc_StepWiseSimulation(NuSMVEnv_ptr env,
                       BeFsm_ptr be_fsm,
                       BddEnc_ptr bdd_enc,
                       TraceMgr_ptr trace_manager,
                       int target_steps,
                       be_ptr constraints,
                       boolean time_shift,
                       boolean printtrace,
                       boolean changes_only,
                       Simulation_Mode mode,
                       boolean display_all);

/*!
  \brief Checks the truth value of a list of constraints on the
                      current state, transitions and next states,
                      from given starting state. This can be used
                      in guided interactive simulation to propose
                      the set of transitions which are allowed to
                      occur in the interactive simulation.

  Given a list of constraints (next-expressions as be_ptr),
                      checks which (flattened) constraints are
                      satisfiable from a given state. Iff
                      from_state is NULL (and not TRUE), the
                      initial state of the fsm is
                      considered. Returned list contains values in
                      {0,1}, and has to be freed.

  \se None
*/
Olist_ptr
Bmc_simulate_check_feasible_constraints(NuSMVEnv_ptr env,
                                        BeFsm_ptr be_fsm,
                                        BddEnc_ptr bdd_enc,
                                        Olist_ptr constraints,
                                        be_ptr from_state);

/*!
  \brief Picks a state from the initial state, creates a trace
                     from it.

  The trace is added into the trace manager.
                     Returns the index of the added trace, or -1 if
                     no trace was created.

  \se A new trace possibly created into the trace manager
*/
int
Bmc_pick_state_from_constr(NuSMVEnv_ptr env,
                           BeFsm_ptr fsm, BddEnc_ptr bdd_enc,
                           be_ptr constr, Simulation_Mode mode,
                           boolean display_all);

/*!
  \brief Picks a state from the set of initial states

  Picks a state from the set of initial states
*/
int
Bmc_Simulate_bmc_pick_state(const NuSMVEnv_ptr env,
                            TraceLabel label,
                            be_ptr be_constr,
                            int tr_number,
                            const Simulation_Mode mode,
                            const int display_all,
                            const boolean verbose);

/*!
  \brief Checks feasibility of a list of constraints for the
  simulation

  Checks feasibility of a list of constraints for the
  simulation
*/
int
Bmc_Simulate_bmc_simulate_check_feasible_constraints(const NuSMVEnv_ptr env,
                                                     const Olist_ptr str_constraints,
                                                     const Olist_ptr be_constraints,
                                                     const Olist_ptr expr_constraints,
                                                     const boolean human_readable);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_SIMULATE_H__ */
