/* ---------------------------------------------------------------------------


  This file is part of the ``bmc.sbmc'' package of NuSMV version 2. 
  Copyright (C) 2006 by Tommi Junttila.

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
  \author Tommi Junttila, Marco Roveri
  \brief Tableau function for SBMC package

  Tableau function for SBMC package

*/


#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_TABLEAU_INC_H__
#define __NUSMV_CORE_BMC_SBMC_SBMC_TABLEAU_INC_H__

#include "nusmv/core/enc/enc.h"
#include "nusmv/core/enc/be/BeEnc.h"
#include "nusmv/core/trace/Trace.h"
#include "nusmv/core/bmc/sbmc/sbmcStructs.h"
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
  \brief Makes the BE formula "\land_{v \in vars} s_i = s_j"

  Creates the BE for the formula "\land_{v \in vars} s_i = s_j"

  \se None
*/
be_ptr sbmc_equal_vectors_formula(const BeEnc_ptr be_enc,
                                         lsList vars,
                                         const unsigned int i,
                                         const unsigned int j);

/*!
  \brief Associates each subformula node of ltlspec with
  a sbmc_LTL_info.

  Associates each subformula node of ltlspec with
  a sbmc_LTL_info. Returns a hash from node_ptr to sbmc_LTL_info*.
  New state variables named #LTL_t'i' can be allocate to 'layer'.
  The new state vars are inserted in state_vars_formula_??? appropriately.

  \se None
*/
hash_ptr sbmc_init_LTL_info(const NuSMVEnv_ptr env,
                                   SymbLayer_ptr layer,
                                   node_ptr ltlspec,
                                   lsList state_vars_formula_pd0,
                                   lsList state_vars_formula_pdx,
                                   lsList state_vars_formula_aux,
                                   const int opt_force_state_vars,
                                   const int opt_do_virtual_unrolling);

/*!
  \brief Initialize trans_bes[i][d] for each sub-formula.

   Initialize trans_bes[i][d], 0<=d<=pd, to
  <ul>
    <li> the formula [[f]]_i^d for definitionally translated subformulae</li>
    <li> the [[f]]_i^d be variable for variable translated subformulae</li>
  </ul> 

  \se None
*/
void sbmc_init_state_vector(const BeEnc_ptr be_enc,
                                   const node_ptr ltlspec,
                                   const hash_ptr info_map,
                                   const unsigned int i_real,
                                   const node_ptr LastState_var,
                                   const be_ptr be_LoopExists);

/*!
  \brief Build InLoop_i

  Build InLoop_i stuff
  Define InLoop_i = (InLoop_{i-1} || l_i)<br>
  Returns the BE constraint InLoop_{i-1} => !l_i  (or 0 when i=0)

  \se None
*/
be_ptr sbmc_build_InLoop_i(const BeEnc_ptr be_enc,
                                      const state_vars_struct * state_vars,
                                      array_t *InLoop_array,
                                      const unsigned int i_model);

/*!
  \brief Build SimplePath_{i,k} for each 0<=i<k

  Build SimplePath_{i,k} for each 0<=i<k

  \se None
*/
lsList sbmc_SimplePaths(const BeEnc_ptr be_enc,
                                   const state_vars_struct *state_vars,
                      array_t *InLoop_array,
                                   const unsigned int k);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_SBMC_SBMC_TABLEAU_INC_H__ */
