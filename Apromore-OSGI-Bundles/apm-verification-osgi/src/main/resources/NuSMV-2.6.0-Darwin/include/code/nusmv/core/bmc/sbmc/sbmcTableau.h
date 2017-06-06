/* ---------------------------------------------------------------------------


  This file is part of the ``bmc'' package of NuSMV version 2.
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
  \brief Public interface for SBMC tableau-related functionalities

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_TABLEAU_H__
#define __NUSMV_CORE_BMC_SBMC_SBMC_TABLEAU_H__

#include "nusmv/core/fsm/be/BeFsm.h"

#include "nusmv/core/be/be.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/utils.h"

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
  \brief Builds tableau without loop

  Fairness is ignored
*/
be_ptr
Bmc_SBMCTableau_GetNoLoop(const BeFsm_ptr be_fsm,
                      const node_ptr ltl_wff,
                      const int k);

/*!
  \brief Builds tableau for a single loop. This function takes
                      into account of fairness

  
*/
be_ptr
Bmc_SBMCTableau_GetSingleLoop(const BeFsm_ptr be_fsm,
                          const node_ptr ltl_wff,
                          const int k, const int l);

/*!
  \brief Builds tableau for all possible loops in \[l, k\[,
  taking into account of fairness using Kepa/Timo method]

  Description        [Fairness is taken care of by adding it to the formula.]

  SideEffects        []

  SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Fairness is taken care of by adding it to the formula.
*/
be_ptr
Bmc_SBMCTableau_GetAllLoops(const BeFsm_ptr be_fsm,
                        const node_ptr ltl_wff,
                        const int k, const int l);

/*!
  \brief Builds a tableau that constraints state k to be equal to
                      state l. This is the condition for a path of length (k+1)
                      to represent a (k-l)loop (new semantics).

  State l and state k are forced to represent the same
                      state by conjuncting the if-and-only-if conditions
                      {Vil<->Vik} between Vil (variable i at time l) and Vik
                      (variable i at time k) for each variable Vi.

  \sa Bmc_Tableau_GetAllLoopsDisjunction
*/
be_ptr
Bmc_SBMCTableau_GetLoopCondition(const BeEnc_ptr be_enc,
                                 const int k, const int l);

/**AutomaticEnd***************************************************************/

#endif /* _BMC_TABLEAU__H */
