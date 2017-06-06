/* ---------------------------------------------------------------------------


  This file is part of the ``bmc.sbmc'' package of NuSMV version 2.
  Copyright (C) 2004 Timo Latvala <timo.latvala@tkk.fi>

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
  \author Timo Latvala, Marco Roveri
  \brief Public interface of the SBMC Generation module

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_GEN_H__
#define __NUSMV_CORE_BMC_SBMC_SBMC_GEN_H__

#include "nusmv/core/bmc/bmc.h"
#include "nusmv/core/bmc/sbmc/sbmcBmcInc.h"
#include "nusmv/core/bmc/sbmc/sbmcBmc.h"

#include "nusmv/core/be/be.h"
#include "nusmv/core/fsm/be/BeFsm.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Returns the LTL problem at length k with loopback l
  (single loop, no loop and all loopbacks are allowed)

  
*/
be_ptr
Bmc_Gen_SBMCProblem(const BeFsm_ptr be_fsm,
                    const node_ptr ltl_wff,
                    const int k, const int l);

/*!
  \brief Top-level function for bmc of PSL properties

  The parameters are:
  - prop is the PSL property to be checked
  - dump_prob is true if the problem must be dumped as DIMACS file (default filename
  from system corresponding variable)
  - inc_sat is true if incremental sat must be used. If there is no
  support for inc sat, an internal error will occur.
  - is_single_prob is true if k must be not incremented from 0 to k_max
    (single problem)
  - k and rel_loop are the bmc parameters.

  \se None
*/
int Sbmc_Gen_check_psl_property(NuSMVEnv_ptr env,
                                       Prop_ptr prop,
                                       boolean dump_prob,
                                       boolean inc_sat,
                                       boolean do_completeness_check,
                                       boolean do_virtual_unrolling,
                                       boolean single_prob,
                                       int k, int rel_loop);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_SBMC_SBMC_GEN_H__ */
