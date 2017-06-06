/* ---------------------------------------------------------------------------


  This file is part of the ``fsm.be'' package of NuSMV version 2.
  Copyright (C) 2005 by FBK-irst. 

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
  \brief Public interface of the Finite State Machine class in BE
  format

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_FSM_BE_BE_FSM_H__
#define __NUSMV_CORE_FSM_BE_BE_FSM_H__


#include "nusmv/core/fsm/sexp/BoolSexpFsm.h"
#include "nusmv/core/enc/be/BeEnc.h"

#include "nusmv/core/node/node.h"
#include "nusmv/core/be/be.h"

#include "nusmv/core/utils/utils.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct BeFsm
  \brief This is the BeFsm accessor type

  
*/
typedef struct BeFsm_TAG* BeFsm_ptr;


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
  \brief To cast and check instances of class BeEnc

  These macros must be used respectively to cast and to check
  instances of class BeEnc
*/
#define BE_FSM(self) \
         ((BeFsm_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BE_FSM_CHECK_INSTANCE(self) \
         (nusmv_assert(BE_FSM(self) != BE_FSM(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BeFsm
  \brief Class BeFsm constructor

  It gets init, invar, transition relation and the list
   of fairness in Boolean Expression format.

  \sa BeFsm_destroy
*/
BeFsm_ptr 
BeFsm_create(BeEnc_ptr be_enc, 
              const be_ptr init, 
              const be_ptr invar, 
              const be_ptr trans, 
              const node_ptr list_of_be_fairness); 

/* should not be in FsmBuilder? */

/*!
  \methodof BeFsm
  \brief Class BeFsm constructor

  Creates a new instance of the BeFsm class, getting
   information from an instance of a boolean Fsm_Sexp type.

  \sa BeFsm_create, BeFsm_destroy
*/
BeFsm_ptr 
BeFsm_create_from_sexp_fsm(BeEnc_ptr be_enc, 
                           const BoolSexpFsm_ptr bfsm);

/*!
  \methodof BeFsm
  \brief Class BeFsm destructor

  

  \se self will be invalidated

  \sa BeFsm_create, BeFsm_create_from_sexp_fsm
*/
void BeFsm_destroy(BeFsm_ptr self);

/*!
  \methodof BeFsm
  \brief Copy constructor for class BeFsm

  Creates a new independent copy of the given fsm instance.
   You must destroy the returned class instance by invoking the class
   destructor when you no longer need it.

  \sa BeFsm_destroy
*/
BeFsm_ptr BeFsm_copy(BeFsm_ptr self);

/*!
  \methodof BeFsm
  \brief Returns the be encoding associated with the given fsm
   instance

  
*/
BeEnc_ptr BeFsm_get_be_encoding(const BeFsm_ptr self);

/*!
  \methodof BeFsm
  \brief Returns the initial states stored in BE format into the
   given fsm instance

  
*/
be_ptr BeFsm_get_init(const BeFsm_ptr self);

/*!
  \methodof BeFsm
  \brief Returns the invariants stored in BE format into the
   given fsm instance

  
*/
be_ptr BeFsm_get_invar(const BeFsm_ptr self);

/*!
  \methodof BeFsm
  \brief Returns the transition relation stored in BE format
   into the given fsm instance

  
*/
be_ptr BeFsm_get_trans(const BeFsm_ptr self);

/*!
  \methodof BeFsm
  \brief Returns the list of fairness stored in BE format
   into the given fsm instance

  
*/
node_ptr BeFsm_get_fairness_list(const BeFsm_ptr self);

/*!
  \methodof BeFsm
  \brief Apply the synchronous product between self and other
   modifying self

  
*/
void
BeFsm_apply_synchronous_product(BeFsm_ptr self, const BeFsm_ptr other);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_FSM_BE_BE_FSM_H__ */
