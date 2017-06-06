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
  \brief Private and protected interface of class 'BDDCompleteTraceExecutor'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_TRACE_EXEC_BDDCOMPLETE_TRACE_EXECUTOR_PRIVATE_H__
#define __NUSMV_CORE_TRACE_EXEC_BDDCOMPLETE_TRACE_EXECUTOR_PRIVATE_H__


#include "nusmv/core/trace/exec/BDDCompleteTraceExecutor.h"
#include "nusmv/core/trace/exec/CompleteTraceExecutor.h" /* fix this */
#include "nusmv/core/trace/exec/CompleteTraceExecutor_private.h" /* fix this */
#include "nusmv/core/utils/utils.h"


/*!
  \brief BDDCompleteTraceExecutor class definition derived from
               class CompleteTraceExecutor

  

  \sa Base class CompleteTraceExecutor
*/

typedef struct BDDCompleteTraceExecutor_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(CompleteTraceExecutor);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  BddFsm_ptr fsm;
  BddEnc_ptr enc;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} BDDCompleteTraceExecutor;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof BDDCompleteTraceExecutor
  \brief The BDDCompleteTraceExecutor class private initializer

  The BDDCompleteTraceExecutor class private initializer

  \sa BDDCompleteTraceExecutor_create
*/
void
bdd_complete_trace_executor_init(BDDCompleteTraceExecutor_ptr self,
                           const BddFsm_ptr fsm, const BddEnc_ptr enc);

/*!
  \methodof BDDCompleteTraceExecutor
  \brief The BDDCompleteTraceExecutor class private deinitializer

  The BDDCompleteTraceExecutor class private deinitializer

  \sa BDDCompleteTraceExecutor_destroy
*/
void
bdd_complete_trace_executor_deinit(BDDCompleteTraceExecutor_ptr self);

#endif /* __NUSMV_CORE_TRACE_EXEC_BDDCOMPLETE_TRACE_EXECUTOR_PRIVATE_H__ */
