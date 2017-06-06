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
  \brief Public interface of the Generation module

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_BMC_GEN_H__
#define __NUSMV_CORE_BMC_BMC_GEN_H__

#include "nusmv/core/be/be.h"
#include "nusmv/core/fsm/be/BeFsm.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/prop/PropDb.h"


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Builds and returns the invariant problem of the
  given propositional formula

  Builds the negation of
                     (I0 imp P0) and ((P0 and R01) imp P1)
                     that must be unsatisfiable.

  \sa Bmc_Gen_InvarBaseStep, Bmc_Gen_InvarInductStep
*/
be_ptr
Bmc_Gen_InvarProblem(const BeFsm_ptr be_fsm,
                     const node_ptr wff);

/*!
  \brief Returns the LTL problem at length k with loopback l
  (single loop, no loop and all loopbacks are allowed)

  
*/
be_ptr
Bmc_Gen_LtlProblem(const BeFsm_ptr be_fsm,
                   const node_ptr ltl_wff,
                   const int k, const int l);

/*!
  \brief Returns the base step of the invariant construction

  Returns I0 -> P0, where I0 is the init and
  invar at time 0, and P0 is the given formula at time 0

  \sa Bmc_Gen_InvarInductStep
*/
be_ptr
Bmc_Gen_InvarBaseStep(const BeFsm_ptr be_fsm,
                      const node_ptr wff);

/*!
  \brief Returns the induction step of the invariant construction

  Returns (P0 and R01) -> P1, where P0 is the formula
  at time 0, R01 is the transition (without init) from time 0 to 1,
  and P1 is the formula at time 1

  \sa Bmc_Gen_InvarBaseStep
*/
be_ptr
Bmc_Gen_InvarInductStep(const BeFsm_ptr be_fsm,
                        const node_ptr wff);

/*!
  \brief Generates i-th fragment of BMC unrolling

  

  \se None
*/
be_ptr
Bmc_Gen_UnrollingFragment(const BeFsm_ptr self,
                           const int i);

/*!
  \brief Top-level function for bmc of PSL properties

  The parameters are:
  - prop is the PSL property to be checked
  - dump_prob is true if the problem must be dumped as DIMACS file (default filename
  from system corresponding variable)
  - inc_sat is true if incremental sat must be used. If there is no
  support for inc sat, an internal error will occur.
  - single_prob is true if k must be not incremented from 0 to k_max
    (single problem)
  - k and rel_loop are the bmc parameters.

  \se None
*/
int Bmc_Gen_check_psl_property(NuSMVEnv_ptr env,
                                  Prop_ptr prop,
                                  boolean dump_prob,
                                  boolean inc_sat,
                                  boolean single_prob,
                                  int k, int rel_loop);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_GEN_H__ */
