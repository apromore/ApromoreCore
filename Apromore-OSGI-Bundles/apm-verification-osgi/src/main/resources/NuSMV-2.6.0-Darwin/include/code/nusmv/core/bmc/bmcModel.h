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
  \brief Public interface for the model-related functionalities

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_BMC_MODEL_H__
#define __NUSMV_CORE_BMC_BMC_MODEL_H__


#include "nusmv/core/utils/utils.h"
#include "nusmv/core/be/be.h"
#include "nusmv/core/node/node.h"

#include "nusmv/core/fsm/be/BeFsm.h"


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
  \brief Retrieves the init states from the given fsm, and
  compiles them into a BE at time 0

  Use this function instead of explicitly get the init
  from the fsm and shift them at time 0 using the vars manager layer.

  \sa Bmc_Model_GetInvarAtTime
*/
be_ptr Bmc_Model_GetInit0(const BeFsm_ptr be_fsm);

/*!
  \brief Retrieves the init states from the given fsm, and
  compiles them into a BE at time i

  Use this function instead of explicitly get the init
  from the fsm and shift them at time i using the vars manager layer.

  \sa Bmc_Model_GetInvarAtTime
*/
be_ptr Bmc_Model_GetInitI(const BeFsm_ptr be_fsm, const int i);

/*!
  \brief Retrieves the invars from the given fsm, and
  compiles them into a BE at the given time

  Use this function instead of explicitly get the invar
  from the fsm and shift them at the requested time using the vars
  manager layer.

  \sa Bmc_Model_GetInit0
*/
be_ptr Bmc_Model_GetInvarAtTime(const BeFsm_ptr be_fsm,
                                       const int time);

/*!
  \brief Retrieves the trans from the given fsm, and compiles
                      it into a MSatEnc at the given time

  Use this function instead of explicitly get the trans
                      from the fsm and shift it at the requested
                      time using the vars manager layer

  \se None
*/
be_ptr Bmc_Model_GetTransAtTime(const BeFsm_ptr be_fsm,
                                       const int time);

/*!
  \brief Unrolls the transition relation from j to k, taking
  into account of invars

  Using of invars over next variables instead of the
  previuos variables is a specific implementation aspect

  \sa Bmc_Model_GetPathWithInit, Bmc_Model_GetPathNoInit
*/
be_ptr
Bmc_Model_GetUnrolling(const BeFsm_ptr be_fsm,
                       const int j, const int k);

/*!
  \brief Returns the path for the model from 0 to k,
  taking into account the invariants (and no init)

  

  \sa Bmc_Model_GetPathWithInit
*/
be_ptr
Bmc_Model_GetPathNoInit(const BeFsm_ptr be_fsm, const int k);

/*!
  \brief Returns the path for the model from 0 to k,
  taking into account initial conditions and invariants

  

  \sa Bmc_Model_GetPathNoInit
*/
be_ptr
Bmc_Model_GetPathWithInit(const BeFsm_ptr be_fsm, const int k);

/*!
  \brief Generates and returns an expression representing
  all fairnesses in a conjunctioned form

  Uses bmc_model_getFairness_aux which recursively calls
  itself to conjuctive all fairnesses by constructing a top-level 'and'
  operation.
  Moreover bmc_model_getFairness_aux calls the recursive function
  bmc_model_getSingleFairness, which resolves a single fairness as
  a disjunctioned expression in which each ORed element is a shifting of
  the single fairness across \[l, k\] if a loop exists.
  If no loop exists, nothing can be issued, so a falsity value is returned

  \sa bmc_model_getFairness_aux, bmc_model_getSingleFairness
*/
be_ptr
Bmc_Model_GetFairness(const BeFsm_ptr be_fsm,
                      const int k, const int l);

/*!
  \brief Unrolls the transition relation from j to k, taking
                      into account of invars

  Using of invars over previous variables instead of the
                      next variables is a specific implementation aspect
*/
be_ptr 
Bmc_Model_Invar_Dual_forward_unrolling(const BeFsm_ptr be_fsm,
                                       const be_ptr invarspec, 
                                       int i);
/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_MODEL_H__ */
