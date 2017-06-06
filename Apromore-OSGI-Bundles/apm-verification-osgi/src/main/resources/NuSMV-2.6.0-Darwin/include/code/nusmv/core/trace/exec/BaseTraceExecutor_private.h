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
  \brief Private and protected interface of class 'BaseTraceExecutor'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_TRACE_EXEC_BASE_TRACE_EXECUTOR_PRIVATE_H__
#define __NUSMV_CORE_TRACE_EXEC_BASE_TRACE_EXECUTOR_PRIVATE_H__


#include "nusmv/core/trace/exec/BaseTraceExecutor.h"
#include "nusmv/core/trace/Trace.h"

#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/EnvObject_private.h"
#include "nusmv/core/utils/utils.h"


/*!
  \brief BaseTraceExecutor class definition derived from
               class Object

  

  \sa Base class Object
*/

typedef struct BaseTraceExecutor_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(EnvObject);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */

  /* verbosity level */
  int verbosity;

  /* the output stream */
  FILE* output_stream;

  /* the error stream */
  FILE* error_stream;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
} BaseTraceExecutor;

/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof BaseTraceExecutor
  \brief The BaseTraceExecutor class private initializer

  The BaseTraceExecutor class private initializer
*/
void trace_executor_init(BaseTraceExecutor_ptr self,
                                const NuSMVEnv_ptr env);

/*!
  \methodof BaseTraceExecutor
  \brief The BaseTraceExecutor class private deinitializer

  The BaseTraceExecutor class private deinitializer

  \sa BaseTraceExecutor_destroy
*/
void trace_executor_deinit(BaseTraceExecutor_ptr self);

/* currently unused */

/*!
  \methodof BaseTraceExecutor
  \brief Private service for defines checking

  Returns true iff values registered in the trace for
  defines actually match evaluated values. If either a value for a
  define is not present in the trace or could not be evaluated (due to
  missing dependencies) it is silently ignored.

  \se none
*/
boolean trace_executor_check_defines(const BaseTraceExecutor_ptr self,
                                            Trace_ptr trace);

#endif /* __NUSMV_CORE_TRACE_EXEC_BASE_TRACE_EXECUTOR_PRIVATE_H__ */
