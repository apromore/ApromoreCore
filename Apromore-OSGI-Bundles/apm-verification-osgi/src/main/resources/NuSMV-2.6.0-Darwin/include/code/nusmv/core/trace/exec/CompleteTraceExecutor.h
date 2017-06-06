/* ---------------------------------------------------------------------------


  This file is part of the ``trace.exec'' package of NuSMV version 2.
  Copyright (C) 2010 by FBK-irst.

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
  \author Marco Pensallorto
  \brief Public interface of class 'CompleteTraceExecutor'

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_EXEC_COMPLETE_TRACE_EXECUTOR_H__
#define __NUSMV_CORE_TRACE_EXEC_COMPLETE_TRACE_EXECUTOR_H__

#include "nusmv/core/trace/exec/BaseTraceExecutor.h"
#include "nusmv/core/utils/utils.h"

#include "nusmv/core/trace/Trace.h"

/*!
  \struct CompleteTraceExecutor
  \brief Definition of the public accessor for class CompleteTraceExecutor

  
*/
typedef struct CompleteTraceExecutor_TAG*  CompleteTraceExecutor_ptr;

/*!
  \brief To cast and check instances of class CompleteTraceExecutor

  These macros must be used respectively to cast and to check
  instances of class CompleteTraceExecutor
*/
#define COMPLETE_TRACE_EXECUTOR(self) \
         ((CompleteTraceExecutor_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define COMPLETE_TRACE_EXECUTOR_CHECK_INSTANCE(self) \
         (nusmv_assert(COMPLETE_TRACE_EXECUTOR(self) != COMPLETE_TRACE_EXECUTOR(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof CompleteTraceExecutor
  \brief The CompleteTraceExecutor class constructor

  The CompleteTraceExecutor class constructor

  \sa CompleteTraceExecutor_destroy
*/
CompleteTraceExecutor_ptr
CompleteTraceExecutor_create(const NuSMVEnv_ptr env);

/*!
  \methodof CompleteTraceExecutor
  \brief The CompleteTraceExecutor class destructor

  The CompleteTraceExecutor class destructor

  \sa CompleteTraceExecutor_create
*/
void CompleteTraceExecutor_destroy(CompleteTraceExecutor_ptr self);

/*!
  \methodof CompleteTraceExecutor
  \brief Executes a complete trace

  Tries to execute a complete on FSM provided at
  construction time and returns true iff the trace is compatible with
  the fsm given at construction time. The number of performed steps is
  stored in *n_steps if a non-NULL pointer is given. This is -1 is if
  the Trace has no feasible initial state.

  \se none

  \sa PartialTraceExecutor_execute
*/
boolean CompleteTraceExecutor_execute(const CompleteTraceExecutor_ptr self, const Trace_ptr trace,
int* n_steps);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_TRACE_EXEC_COMPLETE_TRACE_EXECUTOR_H__ */
