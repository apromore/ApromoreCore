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

  For more information of NuSMV see <http://nusmv.fbk.eu>
  or email to <nusmv-users@fbk.eu>.
  Please report bugs to <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to <nusmv@fbk.eu>. 

-----------------------------------------------------------------------------*/

/*!
  \author Timo Latvala, Marco Roveri
  \brief High-level functionalities interface file for SBMC

  High level functionalities to perform Simple Bounded Model
  Checking for LTL properties.

  For further information about this implementation see:
  T. Latvala, A. Biere, K. Heljanko, and T. Junttila. Simple is
  Better: Efficient Bounded Model Checking for Past LTL. In: R. Cousot
  (ed.), Verification, Model Checking, and Abstract Interpretation,
  6th International Conference VMCAI 2005, Paris, France, Volume 3385
  of LNCS, pp. 380-395, Springer, 2005.  Copyright (C)
  Springer-Verlag.
  

*/

#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_BMC_H__
#define __NUSMV_CORE_BMC_SBMC_SBMC_BMC_H__

#include "nusmv/core/bmc/bmcDump.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/prop/Prop.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

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
  Also see the Bmc_GenSolve_Action possible values

  

  \sa Bmc_GenSolve_Action
*/
int
Bmc_SBMCGenSolveLtl(NuSMVEnv_ptr env,
                    Prop_ptr ltlprop,
                    const int k,
                    const int relative_loop,
                    const boolean must_inc_length,
                    const boolean must_solve,
                    const Bmc_DumpType dump_type,
                    const char* dump_fname_template);

#endif /* __NUSMV_CORE_BMC_SBMC_SBMC_BMC_H__ */

