/* ---------------------------------------------------------------------------


  This file is part of the ``bmc'' package of NuSMV version 2.
  Copyright (C) 2006 by Tommi Juntilla

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
  \author Tommi Juntilla, Marco Roveri
  \brief Public interface for Incremental SBMC tableau-related functionalities

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_TABLEAU_INC_LTLFORMULA_H__
#define __NUSMV_CORE_BMC_SBMC_SBMC_TABLEAU_INC_LTLFORMULA_H__

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
  \brief Creates the BASE constraints.

  Create the BASE constraints.<br>
  Return a list of be_ptr for the created constraints.<br>
  Create the following constraints:<br>
  <ul>
    <li> !LoopExists => ([[f]]_L^d == FALSE) </li>
    <li> LoopExists => ([[Ff]]_E^pd(Gf) => <<Ff>>_E) </li>
    <li> LoopExists => ([[Gf]]_E^pd(Gf) <= <<Gf>>_E) </li>
    <li> LoopExists => ([[fUg]]_E^pd(Gf) => <<Fg>>_E) </li>
    <li> LoopExists => ([[fRg]]_E^pd(Gf) <= <<Gg>>_E) </li>
  </ul>
  If do_optimization is true, then create the following constraints:
  <ul>
    <li> [[p]]_E^d <=> p_E </li>
    <li> [[TRUE]]_E^0 <=> TRUE </li>
    <li> [[FALSE]]_E^0 <=> FALSE </li>
    <li> [[f | g]]_E^d <=> [[f]]_E^d | [[g]]_E^d </li>
    <li> [[f & g]]_E^d <=> [[f]]_E^d & [[g]]_E^d </li>
    <li> [[!f]]_E^d <=> ![[f]]_E^d </li>
    <li> [[Ff]]_E^d <=> [[f]]_E^d | [[Ff]]_L^min(d+1,pd(f)) </li>
    <li> [[Ff]]_E^d+1 => [[Ff]]_E^d </li>
    <li> <<Ff>>_E => [[Ff]]_E^pd(Ff) </li>
    <li> [[Gf]]_E^d <=> [[f]]_E^d & [[Gf]]_L^min(d+1,pd(f)) </li>
    <li> [[Gf]]_E^d => [[Gf]]_E^d+1 </li>
    <li> [[Gf]]_E^pd(Gf) => <<Gf>>_E </li>
    <li> [[fUg]]_E^d <=> [[g]]_E^d | ([[f]]_E^d  & [[fUg]]_L^min(d+1,pd(fUg))) </li>
    <li> [[fRg]]_E^d <=> [[g]]_E^d & ([[f]]_E^d  | [[fRg]]_L^min(d+1,pd(fUg))) </li>
    <li> [[Xf]]_E^d <=> [[f]]_L^min(d+1,pd(f)) </li>
    <li> [[Hf]]_E^d+1 => [[Hf]]_E^d </li>
    <li> [[Of]]_E^d => [[Of]]_E^d+1 </li>
  </ul>
  

  \se None
*/
lsList sbmc_unroll_base(const BeEnc_ptr be_enc,
                               const node_ptr ltlspec,
                               const hash_ptr info_map,
                               const be_ptr be_LoopExists,
                               const int do_optimization);

/*!
  \brief Create the k-invariant constraints for
  propositional operators at time i.

  Create the k-invariant constraints for
  propositional operators at time i. Return a list of be_ptrs for the
  created constraints.

  \se None
*/
lsList sbmc_unroll_invariant_propositional(const BeEnc_ptr be_enc,
                                                  const node_ptr ltlspec,
                                                  const unsigned int i_model,
                                                  const hash_ptr info_map,
                                                  const be_ptr be_InLoop_i,
                                                  const be_ptr be_l_i,
                                                  const int do_optimization);

/*!
  \brief Create the k-invariant constraints for propositional and
  future temporal operators at time i.

  Create the k-invariant constraints for propositional and
  future temporal operators at time i. Return a list of be_ptrs for the
  created constraints.

  \se None
*/
lsList sbmc_unroll_invariant_f(const BeEnc_ptr be_enc,
                                      const node_ptr ltlspec,
                                      const unsigned int i_model,
                                      const hash_ptr info_map,
                                      const be_ptr be_InLoop_i,
                                      const be_ptr be_l_i,
                                      const be_ptr be_LastState_i,
                                      const be_ptr be_LoopExists,
                                      const int do_optimization);

/*!
  \brief Create the k-invariant constraints at time i.

  Create the k-invariant constraints at time
  i. Return a list of be_ptrs for the created constraints.

  \se None
*/
lsList sbmc_unroll_invariant_p(const BeEnc_ptr be_enc,
                                      const node_ptr ltlspec,
                                      const unsigned int i_model,
                                      const hash_ptr info_map,
                                      const be_ptr be_InLoop_i,
                                      const be_ptr be_l_i,
                                      const int do_optimization);

/*!
  \brief Create the formula specific k-dependent constraints.

  Create the formula specific k-dependent constraints.
  Return a list of be_ptrs for the created constraints. 

  \se None
*/
lsList sbmc_formula_dependent(const BeEnc_ptr be_enc,
                                     const node_ptr ltlspec,
                                     const unsigned int k_model,
                                     const hash_ptr info_map);

/*!
  \brief Unroll future and past fragment from
  previous_k+1 upto and including new_k.

  Unroll future and past fragment from previous_k+1
  upto and including new_k. Return a list of constraints.

  \se None
*/
lsList sbmc_unroll_invariant(const BeEnc_ptr be_enc,
                                    const node_ptr bltlspec,
                                    const int previous_k,
                                    const int new_k,
                                    const state_vars_struct *state_vars,
                                    array_t * InLoop_array,
                                    const hash_ptr info_map,
                                    const be_ptr be_LoopExists,
                                    const int opt_do_optimization);

/*!
  \brief required

  Creates several constraints:
  <ul>
  <li>Create the constraint l_{k+1} <=> FALSE</li>
  <li>Create the constraint s_E = s_k</li>
  <li>Create the constraint LoopExists <=> InLoop_k</li>
  <li>Create the formula specific k-dependent constraints</li>
  </ul>

  \se None
*/
lsList sbmc_dependent(const BeEnc_ptr be_enc,
                             const node_ptr bltlspec,
                             const int k,
                             const state_vars_struct *state_vars,
                             array_t *InLoop_array,
                             const be_ptr be_LoopExists,
                             const hash_ptr info_map);

/**AutomaticEnd***************************************************************/

#endif /* _BMC_TABLEAU__H */
