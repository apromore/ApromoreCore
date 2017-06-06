/* ---------------------------------------------------------------------------


  This file is part of the ``mc'' package of NuSMV version 2.
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
  \author Marco Roveri, Roberto Cavada
  \brief Fair CTL model checking algorithms. External header file.

  Fair CTL model checking algorithms. External header file.

*/


#ifndef __NUSMV_CORE_MC_MC_H__
#define __NUSMV_CORE_MC_MC_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/prop/Prop.h"
#include "nusmv/core/fsm/bdd/BddFsm.h"
#include "nusmv/core/trace/Trace.h"
#include "nusmv/core/opt/opt.h"

/*!
  \brief Options for top level function of check_invar command

  

  \sa See the documentation of the single types
*/

typedef struct McCheckInvarOpts_TAG {
  Check_Strategy strategy;
  FB_Heuristic fb_heuristic;
  Bdd2bmc_Heuristic bdd2bmc_heuristic;
  int threshold;
  int bmc_length;
} McCheckInvarOpts;

void McCheckInvarOpts_init(McCheckInvarOpts* options,
                           NuSMVEnv_ptr env);
void McCheckInvarOpts_init_invalid(McCheckInvarOpts* options);
boolean McCheckInvarOpts_is_valid(McCheckInvarOpts* options);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MC_CHECK_INVAR_OPTS_INVALID -1

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Verifies that M,s0 |= alpha 

  Verifies that M,s0 |= alpha using the fair CTL model checking.
*/
void Mc_CheckCTLSpec(NuSMVEnv_ptr env, Prop_ptr prop);

/*!
  \brief This function checks for SPEC of the form AG
  alpha in "context".

  The implicit assumption is that "spec" must be an AG
  formula (i.e. it must contain only conjunctions and AG's).  No attempt
  is done to normalize the formula (e.g. push negations). The AG mode
  relies on the previous computation and storage of the reachable
  state space (<tt>reachable_states_layers</tt>), they are used in
  counterexample computation.

  \sa check_ctlspec
*/
void Mc_CheckAGOnlySpec(NuSMVEnv_ptr env, Prop_ptr prop);

/*!
  \brief Verifies that M,s0 |= AG alpha

  Verifies that M,s0 |= AG alpha, with alpha propositional.
   Uses strategy read from the option variable. 

  \sa check_ctlspec check_ltlspec Mc_CheckInvar_With_Strategy
*/
void Mc_CheckInvar(NuSMVEnv_ptr env, Prop_ptr prop);

/*!
  \brief Verifies that M,s0 |= AG alpha WITHOUT print results or
                counterexamples 

  Verifies that M,s0 |= AG alpha, with alpha propositional.
   Uses strategy read from the option variable.

   If opt_counter_examples is setted and trace is not null, then a
   trace is stored (and must be released by caller) in trace
   parameter location.

   The result of model checking is stored in the given property. 

  \sa check_ctlspec check_ltlspec Mc_CheckInvar_With_Strategy
*/
void Mc_CheckInvarSilently(NuSMVEnv_ptr env,
                                  Prop_ptr prop,
                                  Trace_ptr* trace);

/*!
  \brief Verifies that M,s0 |= AG alpha with the specified strategy

  Verifies that M,s0 |= AG alpha, with alpha propositional.
   Uses strategy given in input

   If opt_counter_examples is setted and trace is not null, then a
   trace is stored (and must be released by caller) in trace
   parameter location.

   The result of model checking is stored in the given property.
   

  \sa check_ctlspec check_ltlspec Mc_CheckInvar
*/
void
Mc_CheckInvar_With_Strategy(NuSMVEnv_ptr env,
                            Prop_ptr prop,
                            Check_Strategy strategy,
                            Trace_ptr* trace,
                            boolean silent);

/*!
  \brief Verifies that M,s0 |= AG alpha with the specified strategy

  Verifies that M,s0 |= AG alpha, with alpha propositional.
   Uses strategy given in input.

   If opt_counter_examples is setted and trace is not null, then a
   trace is stored (and must be released by caller) in trace
   parameter location. A trace is created for variables and defines in 'symbols'.
   If trace is not required 'symbols' can be NULL.

   The result of model checking is stored in the given property.
   

  \sa check_ctlspec check_ltlspec Mc_CheckInvar
*/
void
Mc_CheckInvar_With_Strategy_And_Symbols(NuSMVEnv_ptr env,
                                        Prop_ptr prop,
                                        Check_Strategy strategy,
                                        Trace_ptr* trace,
                                        boolean silent,
                                        NodeList_ptr symbols);

/*!
  \brief Compute quantitative characteristics on the model.

  Compute the given quantitative characteristics on the model.
*/
void Mc_CheckCompute(NuSMVEnv_ptr env, Prop_ptr prop);

/*!
  \brief Top-level function for mc of PSL properties

  The parameters are:
  - prop is the PSL property to be checked
  

  \se None
*/
int Mc_check_psl_property(NuSMVEnv_ptr env, Prop_ptr prop);

/*!
  \brief Checks whether the language is empty

  Checks whether the language is empty. Basically just a
  wrapper function that calls the language emptiness algorithm given
  by the value of the oreg_justice_emptiness_bdd_algorithm option.

  If <tt>allinit</tt> is <tt>true</tt> the check is performed by
  verifying whether all initial states are included in the set of fair
  states. If it is the case from all initial states there exists a
  fair path and thus the language is not empty. On the other hand, if
  <tt>allinit</tt> is false, the check is performed by verifying
  whether there exists at least one initial state that is also a fair
  state. In this case there is an initial state from which it starts a
  fair path and thus the lnaguage is not empty. <tt>allinit</tt> is
  not supported for forward Emerson-Lei.

  Depending on the global option use_reachable_states the set of fair
  states computed can be restricted to reachable states only. In this
  latter case the check can be further simplified. Forward Emerson-Lei
  requires forward_search and use_reachable_states to be enabled.

  If <tt>verbose</tt> is true, then some information on the set of
  initial states is printed out too. <tt> verbose</tt> is ignored for
  forward Emerson-Lei.  

  \se None

  \sa mc_check_language_emptiness_el_bwd,
  mc_check_language_emptiness_el_fwd
*/
void Mc_CheckLanguageEmptiness(NuSMVEnv_ptr env,
                                      const BddFsm_ptr fsm,
                                      boolean allinit,
                                      boolean verbose);
/* mcTrace.c */

/*!
  \brief Creates a trace out of a < S (i, S)* >  bdd list

  Creates a trace out of a < S (i, S)* >  bdd list.
                The built trace is non-volatile. For more control over
                the built trace, please see
                Mc_fill_trace_from_bdd_state_input_list 

  \se none

  \sa Trace_create, Bmc_create_trace_from_cnf_model,
                Mc_fill_trace_from_bdd_state_input_list
*/
Trace_ptr
Mc_create_trace_from_bdd_state_input_list(const BddEnc_ptr bdd_enc,
                                          const NodeList_ptr symbols,
                                          const char* desc,
                                          const TraceType type,
                                          node_ptr path);

/*!
  \brief Fills the given trace out of a < S (i, S)* >  bdd list

  Fills the given trace out of a < S (i, S)* > bdd list.
                The returned trace is the given one, filled with all
                steps. The given trace MUST be empty. Path must be non-Nil

  \se none

  \sa Trace_create, Bmc_fill_trace_from_cnf_model
*/
Trace_ptr
Mc_fill_trace_from_bdd_state_input_list(const BddEnc_ptr bdd_enc,
                                        Trace_ptr trace,
                                        node_ptr path);

/*!
  \brief Populates a trace step with state assignments

  

  \se none
*/
void
Mc_trace_step_put_state_from_bdd(Trace_ptr trace, TraceIter step,
                                 BddEnc_ptr bdd_enc, bdd_ptr bdd);

/*!
  \brief Populates a trace step with input assignments

  

  \se none
*/
void
Mc_trace_step_put_input_from_bdd(Trace_ptr trace, TraceIter step,
                                 BddEnc_ptr bdd_enc, bdd_ptr bdd);

/*!
  \brief Prints out a CTL specification

  Prints out a CTL specification
*/
void print_spec(OStream_ptr file, Prop_ptr prop, Prop_PrintFmt fmt);

/*!
  \brief Print an invariant specification

  Print an invariant specification
*/
void print_invar(OStream_ptr file, Prop_ptr n, Prop_PrintFmt fmt);

/*!
  \brief Prints out a COMPUTE specification

  Prints out a COMPUTE specification
*/
void print_compute(OStream_ptr file, Prop_ptr, Prop_PrintFmt fmt);

/*!
  \brief Set of states satisfying <i>EX(g)</i>.

  Computes the set of states satisfying <i>EX(g)</i>.

  \sa eu ef eg
*/
BddStates ex(BddFsm_ptr, BddStates);

/*!
  \brief Set of states satisfying <i>EF(g)</i>.

  Computes the set of states satisfying <i>EF(g)</i>.

  \sa eu ex
*/
BddStates ef(BddFsm_ptr, BddStates);

/*!
  \brief Set of states satisfying <i>EF(g)</i>.

  Computes the set of states satisfying <i>EG(g)</i>.

  \sa eu ex
*/
BddStates eg(BddFsm_ptr, BddStates);

/*!
  \brief Set of states satisfying <i>E\[ f U g \]</i>.

  Computes the set of states satisfying <i>E\[ f U g \]</i>.

  \sa ebu
*/
BddStates eu(BddFsm_ptr, BddStates, BddStates);

/*!
  \brief Set of states satisfying <i>A\[f U g\]</i>.

  Computes the set of states satisfying <i>A\[f U g\]</i>.

  \sa ax af ex ef
*/
BddStates au(BddFsm_ptr, BddStates, BddStates);

/*!
  \brief Set of states satisfying <i>E\[f U^{inf..sup} g\]</i>.

  Computes the set of states satisfying
                      <i>E\[f U^{inf..sup} g\]</i></i>.

  \sa eu
*/
BddStates ebu(BddFsm_ptr, BddStates, BddStates, int, int);

/*!
  \brief Set of states satisfying <i>EF^{inf..sup}(g)</i>.

  Computes the set of states satisfying
                     <i>EF^{inf..sup}(g)</i>.

  \sa ef
*/
BddStates ebf(BddFsm_ptr, BddStates, int, int);

/*!
  \brief Set of states satisfying <i>EG^{inf..sup}(g)</i>.

  Computes the set of states satisfying
                      <i>EG^{inf..sup}(g)</i>.

  \sa eg
*/
BddStates ebg(BddFsm_ptr, BddStates, int, int);

/*!
  \brief Set of states satisfying <i>A\[f U^{inf..sup} g\]</i>.

  Computes the set of states satisfying
                     <i>A\[f U^{inf..sup} g\]</i>.

  \sa au
*/
BddStates abu(BddFsm_ptr, BddStates, BddStates, int, int);

/*!
  \brief Computes the minimum length of the shortest path
  from <i>f</i> to <i>g</i>.

  This function computes the minimum length of the
  shortest path from <i>f</i> to <i>g</i>.<br>
  Starts from <i>f</i> and proceeds forward until finds a state in <i>g</i>.
  Notice that this function works correctly only if <code>-f</code>
  option is used.

  \sa maxu
*/
int       minu(BddFsm_ptr, bdd_ptr, bdd_ptr);

/*!
  \brief This function computes the maximum length of the
  shortest path from <i>f</i> to <i>g</i>.

  This function computes the maximum length of the
  shortest path from <i>f</i> to <i>g</i>. It starts from !g and
  proceeds backward until no states in <i>f</i> can be found. In other
  words, it looks for the maximum length of <i>f->AG!g</i>.
  Notice that this function works correctly only if <code>-f</code>
  option is used.

  Returns -1 if infinity, -2 if undefined

  \sa minu
*/
int       maxu(BddFsm_ptr, bdd_ptr, bdd_ptr);

/*!
  \brief Counterexamples and witnesses generator.

  This function takes as input a CTL formula and
   returns a witness showing how the given formula does not hold. The
   result consists of a list of states (i.e. an execution trace) that
   leads to a state in which the given formula does not hold.

  \sa explain_recur ex_explain eu_explain eg_explain
   ebg_explain ebu_explain
*/
node_ptr explain(BddFsm_ptr, BddEnc_ptr, node_ptr,
                        node_ptr, node_ptr);

/*!
  \brief Compile a CTL formula into BDD and performs
  Model Checking.

  Compile a CTL formula into BDD and performs
  Model Checking.

  \sa eval_compute
*/
bdd_ptr
eval_ctl_spec(BddFsm_ptr, BddEnc_ptr enc, node_ptr, node_ptr);

/*!
  \brief This function takes a list of formulas, and
  returns the list of their BDDs.

  This function takes as input a list of formulae,
  and return as output the list of the corresponding BDDs, obtained by
  evaluating each formula in the given context.
*/
node_ptr
eval_formula_list(BddFsm_ptr, BddEnc_ptr enc, node_ptr, node_ptr);

/*!
  \brief Computes shortest and longest length of the path
  between two set of states.

  This function performs the invocation of the
  routines to compute the length of the shortest and longest execution
  path between two set of states s_1 and s_2.

  \sa eval_ctl_spec
*/
int
eval_compute(BddFsm_ptr, BddEnc_ptr enc, node_ptr, node_ptr);

/*!
  \brief Frees a list of BDD as generated by eval_formula_list

  Frees a list of BDD as generated by eval_formula_list

  \sa eval_formula_list
*/
void     free_formula_list(DDMgr_ptr , node_ptr);

/* directly called by commands */

/*!
  \brief Performs fair bdd-based PSL model checking.

  Performs fair bdd-based PSL model checking.
*/
int Mc_check_psl_spec(const NuSMVEnv_ptr env, const int prop_no);

/*!
  \brief Performs model checking of invariants

  
*/
int Mc_check_invar(NuSMVEnv_ptr env,
                          Prop_ptr prop,
                          McCheckInvarOpts* options);

/* Called by SA */

/*!
  \brief This function constructs a counterexample
  starting from state target_state

  Compute a counterexample starting from a given state.
  Returned counterexample is a sequence of "state (input, state)*"
*/
node_ptr make_AG_counterexample(BddFsm_ptr, BddStates);

#endif /* __NUSMV_CORE_MC_MC_H__ */
