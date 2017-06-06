/* ---------------------------------------------------------------------------


  This file is part of the ``simulate'' package of NuSMV version 2.
  Copyright (C) 2012 by FBK-irst.

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
  \author Michele Dorigatti
  \brief Private and protected interface of class 'SimulateState'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_SIMULATE_SIMULATE_STATE_PRIVATE_H__
#define __NUSMV_CORE_SIMULATE_SIMULATE_STATE_PRIVATE_H__

/* parent class */
#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/object_private.h"

/* dependent modules */
#include "nusmv/core/dd/DDMgr.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/trace/TraceLabel.h"


/*!
  \brief SimulateState class definition derived from
               class Object

  

  \sa Base class Object
*/

typedef struct SimulateState_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(Object);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  DDMgr_ptr dd_mgr;
  bdd_ptr bdd;
  TraceLabel trace_label;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} SimulateState;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \brief The SimulateState class private initializer

  The SimulateState class private initializer

  \sa SimulateState_create
*/
void simulate_state_init(SimulateState_ptr const self,
                                DDMgr_ptr const dd_mgr,
                                bdd_ptr const bdd,
                                TraceLabel const trace_label);

/*!
  \methodof SimulateState
  \brief The SimulateState class private deinitializer

  The SimulateState class private deinitializer

  \sa SimulateState_destroy
*/
void simulate_state_deinit(SimulateState_ptr self);



#endif /* __NUSMV_CORE_SIMULATE_SIMULATE_STATE_PRIVATE_H__ */
