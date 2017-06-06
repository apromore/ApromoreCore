/* ---------------------------------------------------------------------------


  This file is part of the ``bmc'' package of NuSMV version 2.
  Copyright (C) 2000-2001 by FBK-irst and University of Trento.

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
  \brief The private interfaces for the <tt>bmc</tt> package

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_BMC_INT_H__
#define __NUSMV_CORE_BMC_BMC_INT_H__


#include <time.h>
#include <limits.h>
#include <stdio.h>


#include "nusmv/core/enc/be/BeEnc.h"

#include "nusmv/core/fsm/FsmBuilder.h"
#include "nusmv/core/compile/compile.h"

#include "nusmv/core/be/be.h"

#include "nusmv/core/trace/Trace.h"
#include "nusmv/core/trace/TraceMgr.h"

#include "nusmv/core/dd/dd.h"
#include "nusmv/core/opt/opt.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/utils.h"

/* Uncomment the following line to print out benchmarking info */
/* #define BENCHMARKING */


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_DUMP_FILENAME_MAXLEN 4096

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_NO_PROPERTY_INDEX -1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_BEXP_OUTPUT_SMV 0

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_BEXP_OUTPUT_LB 1

/* BMC Options  default values */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_DIMACS_FILENAME        "@f_k@k_l@l_n@n"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_INVAR_DIMACS_FILENAME  "@f_invar_n@n"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BMC_PB_LENGTH     10

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BMC_PB_LOOP         Bmc_Utils_GetAllLoopbacksString()

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BMC_INVAR_ALG       "classic"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BMC_INC_INVAR_ALG   "dual"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BMC_OPTIMIZED_TABLEAU 1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BMC_FORCE_PLTL_TABLEAU 0

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ST_BMC_CONV_BEXPR2BE_HASH "bcbh"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ST_BMC_TABLEAU_LTL_HASH "btlh"

extern cmp_struct_ptr cmps;
extern FsmBuilder_ptr global_fsm_builder;
extern TraceMgr_ptr global_trace_manager;



/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/


/**AutomaticStart*************************************************************/


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Creates the key for ltl_tableau_hash

  
*/
node_ptr
bmc_tableau_memoization_get_key(NodeMgr_ptr nodemgr,
                                node_ptr wff, int time, int k, int l);

/*!
  \brief Insertion function for ltl_tableau_hash

  
*/
void bmc_tableau_memoization_insert(hash_ptr, node_ptr key, be_ptr be);

/*!
  \brief Lookup function for ltl_tableau_hash

  
*/
be_ptr bmc_tableau_memoization_lookup(hash_ptr, node_ptr key);

/*!
  \brief Call SymbTable_get_handled_hash_ptr with proper
                       arguments

  
*/
hash_ptr Bmc_Tableau_get_handled_hash(SymbTable_ptr symb_table,
                                             char* hash_str);

/*!
  \command{_bmc_test_tableau} Generates a random formula to logically test the
  equivalent tableau

  \command_args{[-h] | [-n property_index] | [[ -d max_depth] [-c max_conns] [-o operator]]
  }

  Use this hidden command to generate random formulae and
  to test the equivalent tableau. The first time this command is called in the
  current NuSMV session it creates a new smv file with a model and generates a
  random ltl spec to test tableau.
  The following times it is called it appends a new formula to the file.
  The generated model contains the same number of non-deterministic variables
  the currently model loaded into NuSMV contains. <BR>
  You cannot call this command if the bmc_loopback is set to '*' (all loops).

  The test is possibly broken if the model contains frozen variables 
  
*/
int
Bmc_TestTableau(NuSMVEnv_ptr env, int argc, char ** argv);

/*!
  \brief Call this function to reset the test sub-package (into
  the reset command for example)

  
*/
void Bmc_TestReset(void);

/*!
  \brief  Checks wether a formula contains only future operators 

  
*/
boolean
isPureFuture(const node_ptr pltl_wff);

/*!
  \brief 

  
*/
be_ptr
Bmc_GetTestTableau(const BeEnc_ptr be_enc,
                   const node_ptr ltl_wff,
                   const int k, const int l);

/*!
  \brief Given a wff expressed in ltl builds the model-independent
  tableau at 'time' of a path formula bounded by [k, l]

  This function is the entry point of a mutual recursive
  calling stack. All logical connectives are resolved, excepted for NOT, which
  closes the recursive calling stack. Also variables and falsity/truth
  constants close the recursion.

  \sa bmc_tableauGetNextAtTime,
  bmc_tableauGetGloballyAtTime, bmc_tableauGetEventuallyAtTime,
  bmc_tableauGetUntilAtTime, bmc_tableauGetReleasesAtTime
*/
be_ptr
BmcInt_Tableau_GetAtTime(const BeEnc_ptr be_enc,
                         const node_ptr ltl_wff,
                         const int time, const int k, const int l);

/* ================================================== */
/* Tableaux for an LTL formula:                       */

/*!
  \brief Resolves the NEXT operator, building the tableau for
  its argument

  Returns a falsity constants if the next operator leads
  out of [l, k] and there is no loop
*/
be_ptr
bmc_tableauGetNextAtTime(const BeEnc_ptr be_enc,
                         const node_ptr ltl_wff,
                         const int time, const int k, const int l);

/*!
  \brief Resolves the future operator, and builds a conjunctive
  expression of tableaus, by iterating intime up to k in a different manner
  depending on the [l, k] interval form

  ltl_wff is the 'p' part in 'F p'.
  If intime<=k is out of [l, k] or if there is no loop,
  iterates from intime to k, otherwise iterates from l to k
*/
be_ptr
bmc_tableauGetEventuallyAtTime(const BeEnc_ptr be_enc,
                               const node_ptr ltl_wff,
                               const int intime, const int k,
                               const int l);

/*!
  \brief As bmc_tableauGetEventuallyAtTime, but builds a
  conjunctioned expression in order to be able to assure a global constraint

  ltl_wff is the 'p' part in 'G p'

  \sa bmc_tableauGetEventuallyAtTime
*/
be_ptr
bmc_tableauGetGloballyAtTime(const BeEnc_ptr be_enc,
                             const node_ptr ltl_wff,
                             const int intime, const int k,
                             const int l);

/*!
  \brief Builds an expression which evaluates the until operator

  Carries out the steps number to be performed, depending
  on l,k and time, then calls bmc_tableauGetUntilAtTime_aux

  \sa bmc_tableauGetUntilAtTime_aux
*/
be_ptr
bmc_tableauGetUntilAtTime(const BeEnc_ptr be_enc,
                          const node_ptr p, const node_ptr q,
                          const int time, const int k, const int l);

/*!
  \brief Builds an expression which evaluates the release
  operator

  Carries out the steps number to be performed, depending
  on l,k and time, then calls bmc_tableauGetReleasesAtTime_aux

  \sa bmc_tableauGetReleasesAtTime_aux
*/
be_ptr
bmc_tableauGetReleasesAtTime(const BeEnc_ptr be_enc,
                             const node_ptr p, const node_ptr q,
                             const int time, const int k, const int l);
/* ================================================== */


/* ================================================== */
/* Tableaux for a PLTL formula:                       */

/*!
  \brief Builds the tableau for a PLTL formula.

  Builds both the bounded-tableau and the loop-tableau for a PLTL
                formula "pltl_wff" (depending on the value of l). The time
                the tableau refers to is (implicitly) time zero.

  \sa getTableauAtTime
*/
be_ptr
Bmc_TableauPLTL_GetTableau(const BeEnc_ptr be_enc,
                           const node_ptr pltl_wff,
                           const int k, const int l);

/*!
  \brief Builds the conjunction of the tableaux for a PLTL formula
                computed on every time instant along a (k,l)-loop.

  This function is a special case of "evaluateOn", thus it
                computes its answer by calling "evaluateOn" with some specifc
                arguments. The only use of this function is in constructing
                optimized tableaux for those depth-one formulas where
                "RELEASES" is the unique operator.

  \sa evaluateOn
*/
be_ptr
Bmc_TableauPLTL_GetAllTimeTableau(const BeEnc_ptr be_enc,
                                  const node_ptr pltl_wff,
                                  const int k);
/* ================================================== */


/* ================================================== */
/* Utils module:                                      */

/*!
  \brief Creates a list of BE variables that are intended to be
               used by the routine that makes the state unique in
               invariant checking.

  If coi is enabled, than the returned list will contain
               only those boolean state variable the given property
               actually depends on.  Otherwise the full set of state
               boolean vars will occur in the list.  Frozen variables
               are not required, since they do not change from state
               to state, thus, cannot make a state distinguishable
               from other states.

               Returned list must be destroyed by the called.
*/
lsList
Bmc_Utils_get_vars_list_for_uniqueness(BeEnc_ptr be_enc,
                                       Prop_ptr invarprop);

/*!
  \brief Creates a list of BE variables that are intended to be
               used by the routine that makes the state unique in
               invariant checking.

  If coi is enabled, than the returned list will contain
               only those boolean state variable the given property
               actually depends on.  Otherwise the full set of state
               boolean vars will occur in the list.  Frozen variables
               are not required, since they do not change from state
               to state, thus, cannot make a state distinguishable
               from other states.

               Returned list must be destroyed by the called.
*/
lsList
Bmc_Utils_get_vars_list_for_uniqueness_fsm(BeEnc_ptr be_enc,
                                           SexpFsm_ptr bool_fsm);

/* ================================================== */
/* Simulation                                         */
void bmc_simulate_set_curr_sim_trace(const NuSMVEnv_ptr env, Trace_ptr trace, int idx);
Trace_ptr bmc_simulate_get_curr_sim_trace(const NuSMVEnv_ptr env);
int bmc_simulate_get_curr_sim_trace_index(const NuSMVEnv_ptr env);
/* ================================================== */

/*!
  \brief Creates a trace out of a cnf model

  Creates a complete, k steps long trace in the language
                of \"symbols\" out a cnf model from a sat solver.
                The returned trace is non-volatile.

                For more control over the built trace, please see
                Bmc_fill_trace_from_cnf_model

  \se none

  \sa Trace_create, Mc_create_trace_from_bdd_input_list,
               Bmc_fill_trace_from_cnf_model
*/
Trace_ptr
Bmc_create_trace_from_cnf_model(const BeEnc_ptr be_enc,
                                const NodeList_ptr symbols,
                                const char* desc,
                                const TraceType type,
                                const Slist_ptr cnf_model,
                                int k);

/*!
  \brief Fills the given trace out of a cnf model

  Fills the trace. The trace will be a complete, k steps
                long trace in the language of \"symbols\" out a cnf
                model from a sat solver.

  \se none

  \sa Trace_create, Mc_fill_trace_from_bdd_input_list
*/
Trace_ptr
Bmc_fill_trace_from_cnf_model(const BeEnc_ptr be_enc,
                              const Slist_ptr cnf_model,
                              int k, Trace_ptr trace);

/* internal bmc/trace utils */

/*!
  \brief Populates trace with valid defaults assignments

  Populates trace with valid defaults assignments.

               The trace can be safely considered complete when this
               function returns.  Existing assignments will not be
               affected.

  \se Trace is populated with default values
*/
void
bmc_trace_utils_complete_trace(Trace_ptr trace,
                               const BoolEnc_ptr bool_enc);

/*!
  \brief Appends a _complete_ (i,S') pair to existing trace

  This is a private service of BmcStepWise_Simulation
*/
void
bmc_trace_utils_append_input_state(Trace_ptr trace, BeEnc_ptr be_enc,
                                   const Slist_ptr cnf_model);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_INT_H__ */
