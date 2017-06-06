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
  \brief Public interface of class 'SimulateState'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_SIMULATE_SIMULATE_STATE_H__
#define __NUSMV_CORE_SIMULATE_SIMULATE_STATE_H__


#include "nusmv/core/utils/object.h"
#include "nusmv/core/dd/DDMgr.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/trace/TraceLabel.h"

/*!
  \struct SimulateState
  \brief Definition of the public accessor for class SimulateState

  
*/
typedef struct SimulateState_TAG*  SimulateState_ptr;

/*!
  \brief To cast and check instances of class SimulateState

  These macros must be used respectively to cast and to check
  instances of class SimulateState
*/
#define SIMULATE_STATE(self) \
         ((SimulateState_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SIMULATE_STATE_CHECK_INSTANCE(self) \
         (nusmv_assert(SIMULATE_STATE(self) != SIMULATE_STATE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* Constructors ***************************************************************/

/*!
  \methodof SimulateState
  \brief The SimulateState class constructor

  The SimulateState class constructor

  \sa SimulateState_destroy
*/
SimulateState_ptr
SimulateState_create(DDMgr_ptr const dd_mgr,
                     bdd_ptr const bdd,
                     TraceLabel const trace_label);

/* Destructors ****************************************************************/

/*!
  \methodof SimulateState
  \brief The SimulateState class destructor

  The SimulateState class destructor

  \sa SimulateState_create
*/
void SimulateState_destroy(SimulateState_ptr self);

/* Getters ********************************************************************/

/*!
  \brief Getter for the bdd field

  Getter for the bdd field
*/
bdd_ptr SimulateState_get_bdd(SimulateState_ptr const self);

/*!
  \brief Getter for the trace_label field

  Getter for the trace_label field
*/
TraceLabel
SimulateState_get_trace_label(SimulateState_ptr const self);

/* Setters ********************************************************************/

/*!
  \brief Set all fields of the class

  Set all fields of the class
*/
void SimulateState_set_all(SimulateState_ptr const self,
                                  bdd_ptr const state,
                                  TraceLabel const label);

/* Others *********************************************************************/

/*!
  \brief Create an istance and add to environment or set it if
  already present

  Create an istance and add to environment or set it if
  already present
*/
SimulateState_ptr
SimulateState_set_in_env(NuSMVEnv_ptr const env,
                         bdd_ptr const bdd,
                         TraceLabel const trace_label);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_SIMULATE_SIMULATE_STATE_H__ */
