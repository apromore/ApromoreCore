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
  \brief Public interface of class 'PartialTraceExecutor'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_TRACE_EXEC_PARTIAL_TRACE_EXECUTOR_H__
#define __NUSMV_CORE_TRACE_EXEC_PARTIAL_TRACE_EXECUTOR_H__


#include "nusmv/core/trace/exec/BaseTraceExecutor.h"
#include "nusmv/core/utils/utils.h"

#include "nusmv/core/trace/Trace.h"

/*!
  \struct PartialTraceExecutor
  \brief Definition of the public accessor for class PartialTraceExecutor

  
*/
typedef struct PartialTraceExecutor_TAG*  PartialTraceExecutor_ptr;

/*!
  \brief To cast and check instances of class PartialTraceExecutor

  These macros must be used respectively to cast and to check
  instances of class PartialTraceExecutor
*/
#define PARTIAL_TRACE_EXECUTOR(self) \
         ((PartialTraceExecutor_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PARTIAL_TRACE_EXECUTOR_CHECK_INSTANCE(self) \
         (nusmv_assert(PARTIAL_TRACE_EXECUTOR(self) != PARTIAL_TRACE_EXECUTOR(NULL)))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof PartialTraceExecutor
  \brief Executes a partial trace

  Tries to execute a partial trace on FSM provided at
  construction time. If execution is succesfully completed, a valid
  complete trace is built on language and returned. A NULL Trace is
  retured otherwise.

  The number of performed steps is stored in *n_steps, if n_steps is
  non-NULL. This is -1 if given trace has no feasible initial state.

  \se A complete Trace on language is created upon successful
  completion

  \sa CompleteTraceExecutor_execute
*/
Trace_ptr PartialTraceExecutor_execute(const PartialTraceExecutor_ptr self, const Trace_ptr trace,
const NodeList_ptr language, int* n_steps);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_TRACE_EXEC_PARTIAL_TRACE_EXECUTOR_H__ */
