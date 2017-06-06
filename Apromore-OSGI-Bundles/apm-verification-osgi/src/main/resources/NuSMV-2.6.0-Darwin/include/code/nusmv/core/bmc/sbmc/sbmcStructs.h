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
  \author Timo Latvala, Tommi Junttila, Marco Roveri
  \brief Structures used within the SBMC package

  Structures used within the SBMC package

*/


#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_STRUCTS_H__
#define __NUSMV_CORE_BMC_SBMC_SBMC_STRUCTS_H__


#include "nusmv/core/node/node.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/list.h"
#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/utils/array.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct state_vars_struct
  \brief SBMC incremental variable information structure

  The structure maintaining variable information needed
  in the SBMC incremental procedure.
*/
typedef struct state_vars_struct_TAG  state_vars_struct;

/*!
  \struct sbmc_node_info_struct
  \brief The data structure holding information for each subformula f

  The data structure holding information for each subformula f
*/
typedef struct sbmc_node_info_struct sbmc_node_info;

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
  \brief Creates an empty state_vars_struct

  Creates an empty state_vars_struct

  \se None
*/
state_vars_struct* sbmc_state_vars_create(const NuSMVEnv_ptr env);

/*!
  \brief state_vars_struct destroyer

  state_vars_struct destroyer

  \se None
*/
void sbmc_state_vars_destroy(state_vars_struct* svs);

/*!
  \brief getter for field \"trans_state_vars\"

  
*/
lsList sbmc_state_vars_get_trans_state_vars(const state_vars_struct * ss);

/*!
  \brief getter for field \"l_var\"

  

  \se None
*/
node_ptr sbmc_state_vars_get_l_var(const state_vars_struct * ss);

/*!
  \brief getter for field \"LoopExists_var\"

  

  \se None
*/
node_ptr sbmc_state_vars_get_LoopExists_var(const state_vars_struct * ss);

/*!
  \brief getter for field \"LastState_var\"

  

  \se None
*/
node_ptr sbmc_state_vars_get_LastState_var(const state_vars_struct * ss);

/*!
  \brief getter for field \"translation_vars_pd0\"

  

  \se None
*/
lsList sbmc_state_vars_get_translation_vars_pd0(const state_vars_struct * ss);

/*!
  \brief getter for field \"translation_vars_pdx\"

  

  \se None
*/
lsList sbmc_state_vars_get_translation_vars_pdx(const state_vars_struct * ss);

/*!
  \brief getter for field \"translation_vars_aux\"

  state_vars_struct destroyer

  \se None
*/
lsList sbmc_state_vars_get_translation_vars_aux(const state_vars_struct * ss);

/*!
  \brief getter for field \"formula_state_vars\"

  

  \se None
*/
lsList sbmc_state_vars_get_formula_state_vars(const state_vars_struct * ss);

/*!
  \brief getter for field \"formula_input_vars\"

  

  \se None
*/
lsList sbmc_state_vars_get_formula_input_vars(const state_vars_struct * ss);

/*!
  \brief getter for field \"simple_path_system_vars\"

  

  \se None
*/
lsList sbmc_state_vars_get_simple_path_system_vars(const state_vars_struct * ss);

/*!
  \brief setter for field \"transition_state_vars\"

  

  \se None
*/
void sbmc_state_vars_set_trans_state_vars(state_vars_struct * ss, lsList f);

/*!
  \brief setter for field \"l_var\"

  

  \se None
*/
void sbmc_state_vars_set_l_var(state_vars_struct * ss, node_ptr f);

/*!
  \brief setter for field \"LoopExists_var\"

  

  \se None
*/
void sbmc_state_vars_set_LoopExists_var(state_vars_struct * ss, node_ptr f);

/*!
  \brief setter for field \"LastState_var\"

  

  \se None
*/
void sbmc_state_vars_set_LastState_var(state_vars_struct * ss, node_ptr f);

/*!
  \brief setter for field \"translation_state_vars_pd0\"

  

  \se None
*/
void sbmc_state_vars_set_translation_vars_pd0(state_vars_struct * ss, lsList f);

/*!
  \brief setter for field \"translation_vars_pdx\"

  

  \se None
*/
void sbmc_state_vars_set_translation_vars_pdx(state_vars_struct * ss, lsList f);

/*!
  \brief setter for field \"translation_vars_aux\"

  

  \se None
*/
void sbmc_state_vars_set_translation_vars_aux(state_vars_struct * ss, lsList f);

/*!
  \brief setter for field \"formula_state_vars\"

  

  \se None
*/
void sbmc_state_vars_set_formula_state_vars(state_vars_struct * ss, lsList f);

/*!
  \brief setter for field \"formula_input_vars\"

  

  \se None
*/
void sbmc_state_vars_set_formula_input_vars(state_vars_struct * ss, lsList f);

/*!
  \brief setter for field \"simple_path_system_vars\"

  

  \se None
*/
void sbmc_state_vars_set_simple_path_system_vars(state_vars_struct * ss, lsList f);

/*!
  \brief Print a state_vars_struct

  Print a state_vars_struct to 'out'

  \se None
*/
void sbmc_state_vars_print(state_vars_struct *svs, FILE* out);

/*!
  \brief Creates an associtative list to avoid duplicates
  of node_ptr

  An associtative list to avoid duplicates of
  node_ptr. If a node is in this set, it has a constant 1 associated
  to it in the associative hash.

  \se None
*/
hash_ptr sbmc_set_create(void);

/*!
  \brief Destroy an associative list used to avoid
  duplicates of node_ptr.

  Destroy an associative list used to avoid
  duplicates of node_ptr.

  \se None
*/
void sbmc_set_destroy(hash_ptr hash);

/*!
  \brief Insert a node in the hash

  Insert a node in the hash associating constant 1

  \se None
*/
void sbmc_set_insert(hash_ptr hash, node_ptr bexp);

/*!
  \brief Checks if a node_ptr was already inserted.

  Checks whether a node_ptr was already
  inserted. In affermative case return 1, else 0.

  \se None
*/
int sbmc_set_is_in(hash_ptr hash, node_ptr bexp);

/*!
  \brief Creates an empty structure to hold information
  associated to each subformula.

  Creates an empty structure to hold information
  associated to each subformula.

  \se None
*/
sbmc_node_info * sbmc_alloc_node_info(void);

/*!
  \brief Frees a structure to hold information
  associated to each subformula.

  Frees a structure to hold information
  associated to each subformula.

  \se None
*/
void sbmc_node_info_free(sbmc_node_info * info);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
unsigned int sbmc_node_info_get_past_depth(sbmc_node_info * h);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
array_t * sbmc_node_info_get_trans_vars(sbmc_node_info * h);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
array_t * sbmc_node_info_get_trans_bes(sbmc_node_info * h);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr sbmc_node_info_get_aux_F_node(sbmc_node_info * h);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
array_t * sbmc_node_info_get_aux_F_trans(sbmc_node_info * h);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr sbmc_node_info_get_aux_G_node(sbmc_node_info * h);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
array_t * sbmc_node_info_get_aux_G_trans(sbmc_node_info * h);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void sbmc_node_info_set_past_depth(sbmc_node_info * h, unsigned int s);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void sbmc_node_info_set_past_trans_vars(sbmc_node_info * h, array_t * s);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void sbmc_node_info_set_trans_bes(sbmc_node_info * h, array_t * s);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void sbmc_node_info_set_aux_F_node(sbmc_node_info * h, node_ptr s);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void sbmc_node_info_set_aux_F_trans(sbmc_node_info * h, array_t * s);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void sbmc_node_info_set_aux_G_node(sbmc_node_info * h, node_ptr s);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void sbmc_node_info_set_aux_G_trans(sbmc_node_info * h, array_t * s);

/*!
  \brief Return the information associated to a
  subformula if any.

  Return the information associated to a
  subformula if any.

  \se None
*/
sbmc_node_info * sbmc_node_info_assoc_find(hash_ptr a, node_ptr n);

/*!
  \brief Insert in the assoc table the infomrnation for
  the subformula.

  Insert in the assoc table the infomrnation for
  the subformula.

  \se None
*/
void sbmc_node_info_assoc_insert(hash_ptr a, node_ptr n, sbmc_node_info * i);

/*!
  \brief Creates an asociative list for pairs node_ptr
  sbmc_node_info *

  Creates an asociative list for pairs node_ptr
  sbmc_node_info *

  \se None
*/
void sbmc_node_info_assoc_free(hash_ptr * a);

/*!
  \brief Creates an asociative list for pairs node_ptr
  sbmc_node_info *

  Creates an asociative list for pairs node_ptr
  sbmc_node_info *

  \se None
*/
hash_ptr sbmc_node_info_assoc_create(void);

/**AutomaticEnd***************************************************************/

#endif /* _SBCM_UTIL */
