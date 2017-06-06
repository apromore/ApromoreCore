
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
  \brief Public interface of class 'BoolSexpFsm'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_FSM_SEXP_BOOL_SEXP_FSM_H__
#define __NUSMV_CORE_FSM_SEXP_BOOL_SEXP_FSM_H__


#include "nusmv/core/fsm/sexp/SexpFsm.h" 
#include "nusmv/core/utils/utils.h"

/*!
  \struct BoolSexpFsm
  \brief Definition of the public accessor for class BoolSexpFsm

  
*/
typedef struct BoolSexpFsm_TAG*  BoolSexpFsm_ptr;

/*!
  \brief To cast and check instances of class BoolSexpFsm

  These macros must be used respectively to cast and to check
  instances of class BoolSexpFsm
*/
#define BOOL_SEXP_FSM(self) \
         ((BoolSexpFsm_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BOOL_SEXP_FSM_CHECK_INSTANCE(self) \
         (nusmv_assert(BOOL_SEXP_FSM(self) != BOOL_SEXP_FSM(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BoolSexpFsm
  \brief The BoolSexpFsm class constructor

  The BoolSexpFsm class constructor

  \sa BoolSexpFsm_destroy
*/
BoolSexpFsm_ptr 
BoolSexpFsm_create(const FlatHierarchy_ptr hierarchy, 
                   const Set_t vars_set,
                   BddEnc_ptr benc, SymbLayer_ptr det_layer);

/*!
  \methodof BoolSexpFsm
  \brief The BoolSexpFsm class constructor from existing sexp
  fsm which is typically a scalar FSM

  The BoolSexpFsm class constructor from existing
  fsm. If the given fsm is already boolean, a copy is returned. If it is
  a scalar FSM, its boolean version is created and returned.

  \sa BoolSexpFsm_destroy
*/
BoolSexpFsm_ptr 
BoolSexpFsm_create_from_scalar_fsm(const SexpFsm_ptr scalar_fsm, 
                                   BddEnc_ptr benc, 
                                   SymbLayer_ptr det_layer);

/*!
  \methodof BoolSexpFsm
  \brief The BoolSexpFsm class destructor

  The BoolSexpFsm class destructor

  \sa BoolSexpFsm_create
*/
VIRTUAL void BoolSexpFsm_destroy(BoolSexpFsm_ptr self);

/*!
  \methodof BoolSexpFsm
  \brief Returns the BoolEnc instance connected to self

  This method can be called only when a valid BddEnc was
  passed to the class constructor (not NULL). Returned instance do not
  belongs to the caller and must _not_ be destroyed
*/
BoolEnc_ptr BoolSexpFsm_get_bool_enc(const BoolSexpFsm_ptr self);

/*!
  \methodof BoolSexpFsm
  \brief The BoolSexpFsm copy constructor

  The BoolSexpFsm copy constructor

  \sa BoolSexpFsm_create
*/
BoolSexpFsm_ptr BoolSexpFsm_copy(BoolSexpFsm_ptr self);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_FSM_SEXP_BOOL_SEXP_FSM_H__ */
