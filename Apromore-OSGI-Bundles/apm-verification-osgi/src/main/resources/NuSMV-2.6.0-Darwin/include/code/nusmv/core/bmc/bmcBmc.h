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
  \brief High-level functionalities interface file

  High level functionalities allow to perform Bounded Model
   Checking for LTL properties and invariants, as well as simulations.

*/


#ifndef __NUSMV_CORE_BMC_BMC_BMC_H__
#define __NUSMV_CORE_BMC_BMC_BMC_H__

#include "nusmv/core/bmc/bmcDump.h"
#include "nusmv/core/bmc/bmc.h"

#include "nusmv/core/fsm/be/BeFsm.h"
#include "nusmv/core/fsm/sexp/SexpFsm.h"
#include "nusmv/core/trace/Trace.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/prop/Prop.h"


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum {BMC_TRUE, BMC_FALSE, BMC_UNKNOWN, BMC_ERROR} Bmc_result;

/*!
  \brief BMC invariant checking closure strategies

  optional

  \sa optional
*/
typedef enum {
  BMC_INVAR_BACKWARD_CLOSURE,
  BMC_INVAR_FORWARD_CLOSURE
} bmc_invar_closure_strategy;


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Given a LTL property generates and solve the problems
   for all Ki (k_min<=i<=k_max). If bIncreaseK is 0 then k_min==k_max==k and
   only one problem is generated. If bIncreaseK is 1 then k_min == 0 and
   k_max == k.
   Each problem Ki takes into account of all possible loops from k_min to Ki
   if loopback is '*' (BMC_ALL_LOOPS). <BR>
   Also see the Bmc_GenSolve_Action possible values. Returns 1 if solver could
   not be created, 0 if everything went smooth

  Returns 1 if solver could not be created, 0 if
   everything went smooth

  \sa Bmc_GenSolve_Action
*/
int Bmc_GenSolveLtl(NuSMVEnv_ptr env, Prop_ptr ltlprop,
                           const int k, const int relative_loop,
                           const boolean must_inc_length,
                           const boolean must_solve,
                           const Bmc_DumpType dump_type,
                           const char* dump_fname_template);

/*!
  \brief Generates DIMACS version and/or solve and INVARSPEC
   problems

  Returns 1 if solver could not be created, 0 if
   everything went smooth

  \sa Bmc_GenSolvePbs
*/
int Bmc_GenSolveInvar(NuSMVEnv_ptr env,
                             Prop_ptr invarprop,
                             const boolean must_solve,
                             const Bmc_DumpType dump_type,
                             const char* dump_fname_template);

/*!
  \brief Apply Induction algorithm on th given FSM to
                       check the given NNFd invarspec

  Returns BMC_TRUE if the property is true, BMC_UNKNOWN
                       if the induction failed, if the induction fails and the
                       counter example option is activated, then a trace is
                       registered in the global trace manager and its index is
                       stored in trace_index parameter.
*/
Bmc_result Bmc_induction_algorithm(const NuSMVEnv_ptr env,
                                          BeFsm_ptr be_fsm,
                                          node_ptr binvarspec,
                                          Trace_ptr* trace_index,
                                          NodeList_ptr symbols);

/*!
  \brief Solve and INVARSPEC problems by using
   Een/Sorensson method non-incrementally

  Returns a Bmc_result according to the result of the
                       checking

  \sa Bmc_GenSolvePbs
*/
Bmc_result
Bmc_een_sorensson_algorithm(const NuSMVEnv_ptr env,
                            BeFsm_ptr be_fsm,
                            BoolSexpFsm_ptr bool_fsm,
                            node_ptr binvarspec,
                            int max_k,
                            const Bmc_DumpType dump_type,
                            const char* dump_fname_template,
                            Prop_ptr pp,
                            Prop_ptr oldprop,
                            boolean print_steps,
                            boolean use_extra_step,
                            Trace_ptr* trace);

/*!
  \brief Solve and INVARSPEC problems by using
   Een/Sorensson method non-incrementally and without dumping the problem

  Returns a Bmc_result according to the result of the
                       checking

  \sa Bmc_GenSolvePbs
*/
Bmc_result
Bmc_een_sorensson_algorithm_without_dump(const NuSMVEnv_ptr env,
                                         BeFsm_ptr be_fsm,
                                         BoolSexpFsm_ptr bool_fsm,
                                         node_ptr binvarspec,
                                         int max_k,
                                         boolean use_extra_step,
                                         Trace_ptr* trace);

/*!
  \brief Solve and INVARSPEC problems by using
   Een/Sorensson method non-incrementally

  Returns 1 if solver could not be created, 0 if
   everything went smooth

  \sa Bmc_GenSolvePbs
*/
int
Bmc_GenSolveInvar_EenSorensson(NuSMVEnv_ptr env,
                               Prop_ptr invarprop,
                               const int max_k,
                               const Bmc_DumpType dump_type,
                               const char* dump_fname_template,
                               boolean use_extra_step);

/* incremental algorithms */

/*!
  \brief Solves LTL problem the same way as the original
  Bmc_GenSolveLtl but just adds BE representing the path incrementaly.

  

  \sa Bmc_GenSolve_Action
*/
int Bmc_GenSolveLtlInc(NuSMVEnv_ptr env, Prop_ptr ltlprop,
                              const int k, const int relative_loop,
                              const boolean must_inc_length);

/*!
  \brief Solve an INVARSPEC problems with algorithm
  ZigZag

  The function will run not more then max_k transitions,
  then if the problem is not proved the function just returns 0
*/
int Bmc_GenSolveInvarZigzag(NuSMVEnv_ptr env,
                                   Prop_ptr invarprop, const int max_k);

/*!
  \brief Solve an INVARSPEC problems wiht algorithm Dual

  The function tries to solve the problem
  with not more then max_k transitions. If the problem is not
  solved after max_k transition then the function returns 0.

  If the no_closure flag is true, only the \"base\" encoding is used
  
*/
int Bmc_GenSolveInvarDual(NuSMVEnv_ptr env,
                                 Prop_ptr invarprop, const int max_k,
                                 bmc_invar_closure_strategy strategy);

/*!
  \brief Solve an INVARSPEC problems wiht algorithm Fasification

  The function tries to solve the problem
  with not more then max_k transitions. If the problem is not
  solved after max_k transition then the function returns 0.

  
*/
int Bmc_GenSolveInvarFalsification(NuSMVEnv_ptr env, Prop_ptr invarprop,
                                   const int max_k, int step_k);

#endif /* __NUSMV_CORE_BMC_BMC_BMC_H__ */

