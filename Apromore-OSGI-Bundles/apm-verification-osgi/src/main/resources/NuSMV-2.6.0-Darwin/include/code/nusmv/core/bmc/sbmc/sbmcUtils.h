/* ---------------------------------------------------------------------------


  This file is part of the ``bmc.sbmc'' package of NuSMV version 2.
  Copyright (C) 2006 by Tommi Junttila, Timo Latvala.

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
  \author Tommi Junttila, Timo Latvala, Marco Roveri
  \brief Utilities function for SBMC package

  Utilities function for SBMC package

*/


#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_UTILS_H__
#define __NUSMV_CORE_BMC_SBMC_SBMC_UTILS_H__

#include "nusmv/core/prop/propPkg.h"
#include "nusmv/core/bmc/sbmc/sbmcStructs.h"
#include "nusmv/core/enc/enc.h"
#include "nusmv/core/enc/be/BeEnc.h"
#include "nusmv/core/trace/Trace.h"
#include "nusmv/core/trace/TraceMgr.h"
#include "nusmv/core/sat/sat.h" /* for solver and result */
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/assoc.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct sbmc_MetaSolver
  \brief A wrapper to the sat solver


*/
typedef struct sbmc_MetaSolver_TAG sbmc_MetaSolver;

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define sbmc_SNH_text "%s:%d: Should not happen"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define sbmc_SNYI_text "%s:%d: Something not yet implemented\n"


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int sbmc_get_unique_id(const NuSMVEnv_ptr env);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void sbmc_reset_unique_id(const NuSMVEnv_ptr env);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void sbmc_increment_unique_id(const NuSMVEnv_ptr env);

/*!
  \brief Print a node_ptr expression by prefixing and
  suffixing it.

  Prints a node_ptr expression in a file stream by
  prefixing and suffixing it with a string. If the prefix and suffix
  strings are NULL they are not printed out.

  \se None
*/
void sbmc_print_node(const NuSMVEnv_ptr env,
                            FILE * out, const char * prefix, node_ptr node,
                            const char * postfix);

/*!
  \brief Prints a lsList of node_ptr

  Prints a lsList of node_ptr in a file stream.

  \se None
*/
void sbmc_print_node_list(const NuSMVEnv_ptr env,
                                 FILE *out, lsList l);

/*!
  \brief Declare a new boolean state variable in the layer.

  Declare a new boolean state variable in the
  layer. The name is specified as a string. If the variable already
  exists, then an error is generated.

  \se None
*/
node_ptr sbmc_add_new_state_variable(const NuSMVEnv_ptr env,
                                            SymbLayer_ptr layer,
                                            const char *name);

/*!
  \brief Compute the variables that occur in the formula ltlspec.

  Compute the variables that occur in the formula ltlspec.
  The formula ltlspec must  be in NNF.

  \se None
*/
lsList sbmc_find_formula_vars(const NuSMVEnv_ptr env, node_ptr ltlspec);

/*!
  \brief Prints some of the information associated to a
  subformula

  Prints some of the information associated to a
  subformula.

  \se None
*/
void sbmc_print_varmap(const NuSMVEnv_ptr env, FILE *out,
                              node_ptr node, sbmc_node_info *info);

/*!
  \brief Prints some of the information associated to a G
  formula

  Prints some of the information associated to a G
  formula.

  \se None
*/
void sbmc_print_Gvarmap(const NuSMVEnv_ptr env, FILE *out,
                               node_ptr var, node_ptr formula);

/*!
  \brief Prints some of the information associated to a F
  formula

  Prints some of the information associated to a F
  formula.

  \se None
*/
void sbmc_print_Fvarmap(const NuSMVEnv_ptr env, FILE *out,
                               node_ptr var, node_ptr formula);

/*!
  \brief Creates a new fresh state variable.

  Creates a new fresh state variable. The name is
  according to the pattern #LTL_t%u, being %u an unsigned integer. The
  index is incremented by one.

  \se index is incremented by one.
*/
node_ptr sbmc_1_fresh_state_var(const NuSMVEnv_ptr env,
                                       SymbLayer_ptr layer, unsigned int *index);

/*!
  \brief Creates N new fresh state variables.

  Creates N new fresh state variables. The name is
  according to the pattern #LTL_t%u, being %u an unsigned integer. The
  index is incremented by N. The new variables are stroed into an
  array of node_ptr

  \se index is incremented by N.
*/
array_t * sbmc_n_fresh_state_vars(const NuSMVEnv_ptr env,
                                         SymbLayer_ptr layer, const unsigned int n,
                            unsigned int *index);

/*!
  \brief Creates info->pastdepth+1 new state variables
  for the main translation in info->trans_vars.

  Creates info->pastdepth+1 new state variables
  for the main translation in info->trans_vars. state_vars_formula_pd0,
  state_vars_formula_pdx and new_var_index are updated accordingly.

  \se new_var_index is incremented accordingly to the
  number of variables created. state_vars_formula_pd0,
  state_vars_formula_pdx and new_var_index are updated accordingly.
*/
void sbmc_allocate_trans_vars(const NuSMVEnv_ptr env,
                                     sbmc_node_info *info, SymbLayer_ptr layer,
                                     lsList state_vars_formula_pd0,
                                     lsList state_vars_formula_pdx,
                                     unsigned int *new_var_index);

/*!
  \brief Takes a property and return the negation of the
  property conjoined with the big and of fairness conditions.

  Takes a property and return the negation of the
  property conjoined with the big and of fairness conditions.

  \se None
*/
node_ptr sbmc_make_boolean_formula(BddEnc_ptr bdd_enc, Prop_ptr ltlprop);

/*!
  \brief Find state and input variables that occurr in the formula.

  Find state and input variables that occurr in the formula.
  Build the list of system variables for simple path constraints.

  <ul>
  <li> state_vars->formula_state_vars will have the state vars occurring
    in the formula bltlspec</li>
  <li> state_vars->formula_input_vars will have the input vars occurring
    in the formula bltlspec</li>
  <li> state_vars->simple_path_system_vars will be the union of
    state_vars->transition_state_vars,
    state_vars->formula_state_vars, and
    state_vars->formula_input_vars </li>
   </ul>

   Note: frozen variables are not collected since they do no
   paticipate in state equality formulas.

  \se svs is modified to store retrieved information.
*/
void sbmc_find_relevant_vars(state_vars_struct *svs,
                                        BeFsm_ptr be_fsm, node_ptr bltlspec);

/*!
  \brief Extracts a trace from a sat assignment.

  Extracts a trace from a sat assignment.
                      The generated trace is non-volatile

  \se None

  \sa Bmc_Utils_generate_cntexample
                      Sbmc_Utils_fill_cntexample
*/
Trace_ptr
Sbmc_Utils_generate_cntexample(BeEnc_ptr be_enc, sbmc_MetaSolver * solver,
                               node_ptr l_var, const int k,
                               const char * trace_name,
                               NodeList_ptr symbols);

/*!
  \brief Extracts a trace from a sat assignment, and prints it.

  Extracts a trace from a sat assignment, registers it in
                      the TraceMgr and prints it using the default plugin.

  \se None

  \sa Bmc_Utils_generate_and_print_cntexample
                      Sbmc_Utils_generate_cntexample
                      Sbmc_Utils_fill_cntexample
*/
Trace_ptr
Sbmc_Utils_generate_and_print_cntexample(BeEnc_ptr be_enc,
                                         TraceMgr_ptr tm,
                                         sbmc_MetaSolver * solver,
                                         node_ptr l_var,
                                         const int k,
                                         const char * trace_name,
                                         NodeList_ptr symbols);

/*!
  \brief Fills the given trace using the given sat assignment.

  Fills the given trace using the given sat assignment.

  \se The \"res\" trace is filled

  \sa Bmc_Utils_generate_cntexample
*/
Trace_ptr
Sbmc_Utils_fill_cntexample(BeEnc_ptr be_enc, sbmc_MetaSolver * solver,
                           node_ptr l_var, const int k, Trace_ptr trace);

/*!
  \brief Routines for the state indexing scheme

  State 0 is the L state

  \se None

  \sa sbmc_E_state sbmc_real_k sbmc_model_k sbmc_real_k_string
*/
int sbmc_L_state(void);

/*!
  \brief Routines for the state indexing scheme

  State 1 is the E state

  \se None

  \sa sbmc_L_state sbmc_real_k sbmc_model_k sbmc_real_k_string
*/
int sbmc_E_state(void);

/*!
  \brief Routines for the state indexing scheme

  The first real state is 2

  \se None

  \sa sbmc_L_state sbmc_E_state sbmc_model_k sbmc_real_k_string
*/
int sbmc_real_k(int k);

/*!
  \brief Routines for the state indexing scheme

  Given a real k return the corresponding model k (real - 2)

  \se None

  \sa sbmc_L_state sbmc_E_state sbmc_real_k sbmc_real_k_string
*/
unsigned int sbmc_model_k(int k);

/*!
  \brief Routines for the state indexing scheme

  Returns a string correspondingg to the state considered. E, L, Real

  \se The returned value must be freed

  \sa sbmc_L_state sbmc_E_state sbmc_real_k sbmc_model_k
*/
char* sbmc_real_k_string(const unsigned int k_real);

/*!
  \brief Creates a meta solver wrapper

  Creates a meta solver wrapper

  \se None
*/
sbmc_MetaSolver * sbmc_MS_create(BeEnc_ptr be_enc);

/*!
  \brief Destroy a meta solver wrapper

  Destroy a meta solver wrapper

  \se None
*/
void sbmc_MS_destroy(sbmc_MetaSolver *ms);

/*!
  \brief Create the volatile group in the meta solver wrapper

  Create the volatile group in the meta solver wrapper. Use
  of the volatile group is not forced

  \se None
*/
void sbmc_MS_create_volatile_group(sbmc_MetaSolver *ms);

/*!
  \brief Destroy the volatile group of the meta solver wrapper and
  force use of the permanent one

  Destroy the volatile group of the meta solver wrapper and
  force use of the permanent one

  \se None
*/
void sbmc_MS_destroy_volatile_group(sbmc_MetaSolver *ms);

/*!
  \brief Force use of the permanent group of
  the meta solver wrapper

  Force use of the permanent group of
  the meta solver wrapper. Volatile group is left in place, if existing

  \se None
*/
void sbmc_MS_switch_to_permanent_group(sbmc_MetaSolver *ms);

/*!
  \brief Force use of the volatile group of
  the meta solver wrapper

  Force use of the volatile group of
  the meta solver wrapper. The volatile group must have been previously
  created

  \se None
*/
void sbmc_MS_switch_to_volatile_group(sbmc_MetaSolver *ms);

/*!
  \brief Destroy the volatile group of the meta solver wrapper

  Destroy the volatile group of the meta solver
  wrapper, thus only considering the permanent group.

  \se None
*/
void sbmc_MS_goto_permanent_group(sbmc_MetaSolver *ms);

/*!
  \brief Create and force use of the volatile group of
  the meta solver wrapper

  Create and force use of the volatile group of
  the meta solver wrapper.

  \se None
*/
void sbmc_MS_goto_volatile_group(sbmc_MetaSolver *ms);

/*!
  \brief Forces a BE to be true in the solver.

  Forces a BE to be true in the solver. The BE
  converted to CNF, the CNF is then forced in the group in use,
  i.e. in the permanent or in the volatile group.

  \se None
*/
void sbmc_MS_force_true(sbmc_MetaSolver *ms, be_ptr be_constraint,
                        Be_CnfAlgorithm cnf_alg);

/*!
  \brief Forces a list of BEs to be true in the solver.

  Forces a list of BEs to be true in the
  solver. Each is converted to CNF, the CNF is then forced in the
  group in use, i.e. in the permanent or in the volatile group.

  \se None

  \sa sbmc_MS_force_true
*/
void sbmc_MS_force_constraint_list(sbmc_MetaSolver *ms, lsList constraints,
                                   Be_CnfAlgorithm cnf_alg);

/*!
  \brief Solves all groups belonging to the solver and
  returns the flag.

  Solves all groups belonging to the solver and
  returns the flag.

  \se None

  \sa SatSolver_solve_all_groups
*/
SatSolverResult sbmc_MS_solve(sbmc_MetaSolver *ms);

/*!
  \brief Solves all groups belonging to the solver assuming
  the CNF assumptions and returns the flag.

  Solves all groups belonging to the solver assuming
  the CNF assumptions and returns the flag.

  \se None

  \sa SatSolver_solve_all_groups_assume
*/
SatSolverResult sbmc_MS_solve_assume(sbmc_MetaSolver *ms, Slist_ptr assumptions);

/*!
  \brief Returns the underlying solver

  Returns the solver
*/
SatSolver_ptr sbmc_MS_get_solver(sbmc_MetaSolver *ms);

/*!
  \brief Returns the underlying solver

  Returns the solver
*/
Slist_ptr sbmc_MS_get_conflicts(sbmc_MetaSolver *ms);

/*!
  \brief Returns the model (of previous solving)

   The previous solving call should have returned SATISFIABLE.
  The returned list is a list of values in dimac form (positive literal
  is included as the variable index, negative literal as the negative
  variable index, if a literal has not been set its value is not included).

  Returned list must be NOT destroyed.
*/
Slist_ptr sbmc_MS_get_model(sbmc_MetaSolver *ms);

/*!
  \brief Declares a new layer to contain the loop variable.

  Declares a new layer to contain the loop variable.
*/
void sbmc_add_loop_variable(BddEnc_ptr bdd_enc, BeFsm_ptr fsm);

/*!
  \brief Remove the new layer to contain the loop variable.

  Remove the new layer to contain the loop variable.
*/
void sbmc_remove_loop_variable(BddEnc_ptr bdd_enc, BeFsm_ptr fsm);

/*!
  \brief Sets the name of the loop variable.

  Sets the name of the loop variable.
*/
void sbmc_loop_var_name_set(const NuSMVEnv_ptr env, node_ptr n);

/*!
  \brief Gets the name of the loop variable.

  Gets the name of the loop variable.
*/
node_ptr sbmc_loop_var_name_get(const NuSMVEnv_ptr env);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_SBMC_SBMC_UTILS_H__ */
