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
  \brief Private and protected interface of class 'SATCompleteTraceExecutor'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_TRACE_EXEC_SATCOMPLETE_TRACE_EXECUTOR_PRIVATE_H__
#define __NUSMV_CORE_TRACE_EXEC_SATCOMPLETE_TRACE_EXECUTOR_PRIVATE_H__


#include "nusmv/core/trace/exec/SATCompleteTraceExecutor.h"
#include "nusmv/core/trace/exec/CompleteTraceExecutor.h" /* fix this */
#include "nusmv/core/trace/exec/CompleteTraceExecutor_private.h" /* fix this */
#include "nusmv/core/utils/utils.h"


/*!
  \brief SATCompleteTraceExecutor class definition derived from
               class CompleteTraceExecutor

  

  \sa Base class CompleteTraceExecutor
*/

typedef struct SATCompleteTraceExecutor_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(CompleteTraceExecutor);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  BeFsm_ptr fsm;
  BeEnc_ptr enc;

  /* needed for booleanization */
  BddEnc_ptr bdd_enc;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} SATCompleteTraceExecutor;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof SATCompleteTraceExecutor
  \brief The SATCompleteTraceExecutor class private initializer

  The SATCompleteTraceExecutor class private initializer

  \sa SATCompleteTraceExecutor_create
*/
void
sat_complete_trace_executor_init(SATCompleteTraceExecutor_ptr self,
                                 const BeFsm_ptr fsm,
                                 const BeEnc_ptr enc,
                                 const BddEnc_ptr bdd_enc);

/*!
  \methodof SATCompleteTraceExecutor
  \brief The SATCompleteTraceExecutor class private deinitializer

  The SATCompleteTraceExecutor class private deinitializer

  \sa SATCompleteTraceExecutor_destroy
*/
void
sat_complete_trace_executor_deinit(SATCompleteTraceExecutor_ptr self);

#endif /* __NUSMV_CORE_TRACE_EXEC_SATCOMPLETE_TRACE_EXECUTOR_PRIVATE_H__ */
