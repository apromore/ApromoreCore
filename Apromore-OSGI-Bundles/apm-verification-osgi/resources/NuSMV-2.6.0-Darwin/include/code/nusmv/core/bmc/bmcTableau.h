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
  \brief Public interface for tableau-related functionalities

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_BMC_TABLEAU_H__
#define __NUSMV_CORE_BMC_BMC_TABLEAU_H__

#include "nusmv/core/fsm/be/BeFsm.h"

#include "cudd/util.h"
#include "nusmv/core/be/be.h"
#include "nusmv/core/node/node.h"


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
  \brief Builds tableau without loop at time zero, taking into
  account of fairness

  Fairness evaluate to true if there are not fairness
  in the model, otherwise them evaluate to false because of no loop
*/
be_ptr
Bmc_Tableau_GetNoLoop(const BeFsm_ptr be_fsm,
                      const node_ptr ltl_wff,
                      const int k);

/*!
  \brief Builds tableau for all possible loops in \[l, k\], in
  the particular case in which depth is 1. This function takes into account
  of fairness

  Builds the tableau in the case depth==1 as suggested
  by R. Sebastiani
*/
be_ptr
Bmc_Tableau_GetSingleLoop(const BeFsm_ptr be_fsm,
                          const node_ptr ltl_wff,
                          const int k, const int l);

/*!
  \brief Builds tableau for all possible loops in \[l, k\[,
  taking into account of fairness]

  Description        [Each tableau takes into account of fairnesses relative
  to its step. All tableau are collected together into a disjunctive form.]

  SideEffects        []

  SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Each tableau takes into account of fairnesses relative
  to its step. All tableau are collected together into a disjunctive form.
*/
be_ptr
Bmc_Tableau_GetAllLoops(const BeFsm_ptr be_fsm,
                        const node_ptr ltl_wff,
                        const int k, const int l);

/*!
  \brief Builds tableau for all possible loops in \[l, k\], in
  the particular case in which depth is 1. This function takes into account
  of fairness

  Builds the tableau in the case depth==1 as suggested
  by R. Sebastiani
*/
be_ptr
Bmc_Tableau_GetAllLoopsDepth1(const BeFsm_ptr be_fsm,
                              const node_ptr ltl_wff, const int k);

/*!
  \brief Builds a tableau for the LTL at length k with loopback l
  (single loop, no loop and all loopbacks are allowed)

  
*/
be_ptr
Bmc_Tableau_GetLtlTableau(const BeFsm_ptr be_fsm,
                const node_ptr ltl_wff,
                const int k, const int l);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_TABLEAU_H__ */
