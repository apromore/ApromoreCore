/* ---------------------------------------------------------------------------


  This file is part of the ``fsm.sexp'' package of NuSMV version 2. 
  Copyright (C) 2009 by FBK-irst. 

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
  \brief Private and protected interface of class 'BoolSexpFsm'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_FSM_SEXP_BOOL_SEXP_FSM_PRIVATE_H__
#define __NUSMV_CORE_FSM_SEXP_BOOL_SEXP_FSM_PRIVATE_H__


#include "nusmv/core/fsm/sexp/BoolSexpFsm.h" 

#include "nusmv/core/fsm/sexp/SexpFsm.h"
#include "nusmv/core/fsm/sexp/SexpFsm_private.h"

#include "nusmv/core/utils/utils.h" 


/*!
  \brief BoolSexpFsm class definition derived from
               class SexpFsm

  

  \sa Base class SexpFsm
*/

typedef struct BoolSexpFsm_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(SexpFsm); 

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  BddEnc_ptr enc;     /* bdd encoder for booleanization */
  SymbLayer_ptr det_layer;   /* layer for determinizing */

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} BoolSexpFsm;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof BoolSexpFsm
  \brief The BoolSexpFsm class private initializer

  The BoolSexpFsm class private initializer

  \sa BoolSexpFsm_create
*/
void bool_sexp_fsm_init(BoolSexpFsm_ptr self, 
                               const FlatHierarchy_ptr hierarchy, 
                               const Set_t vars_set, 
                               BddEnc_ptr enc, 
                               SymbLayer_ptr det_layer);

/*!
  \methodof BoolSexpFsm
  \brief The BoolSexpFsm class private deinitializer

  The BoolSexpFsm class private deinitializer

  \sa BoolSexpFsm_destroy
*/
void bool_sexp_fsm_deinit(BoolSexpFsm_ptr self);

/*!
  \methodof BoolSexpFsm
  \brief The BoolSexpFsm class private deinitializer

  The BoolSexpFsm class private deinitializer

  \sa BoolSexpFsm_destroy
*/
void bool_sexp_fsm_copy_aux(const BoolSexpFsm_ptr self, 
                                   BoolSexpFsm_ptr copy);



#endif /* __NUSMV_CORE_FSM_SEXP_BOOL_SEXP_FSM_PRIVATE_H__ */
