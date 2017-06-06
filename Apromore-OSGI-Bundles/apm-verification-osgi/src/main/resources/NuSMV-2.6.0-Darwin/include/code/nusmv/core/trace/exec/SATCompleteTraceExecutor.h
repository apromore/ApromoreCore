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
  \brief Public interface of class 'SATCompleteTraceExecutor'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_TRACE_EXEC_SATCOMPLETE_TRACE_EXECUTOR_H__
#define __NUSMV_CORE_TRACE_EXEC_SATCOMPLETE_TRACE_EXECUTOR_H__


#include "nusmv/core/trace/exec/CompleteTraceExecutor.h" /* fix this */
#include "nusmv/core/utils/utils.h"

#include "nusmv/core/fsm/be/BeFsm.h"
#include "nusmv/core/enc/be/BeEnc.h"
#include "nusmv/core/enc/bdd/BddEnc.h"

/*!
  \struct SATCompleteTraceExecutor
  \brief Definition of the public accessor for class SATCompleteTraceExecutor

  
*/
typedef struct SATCompleteTraceExecutor_TAG*  SATCompleteTraceExecutor_ptr;

/*!
  \brief To cast and check instances of class SATCompleteTraceExecutor

  These macros must be used respectively to cast and to check
  instances of class SATCompleteTraceExecutor
*/
#define SAT_COMPLETE_TRACE_EXECUTOR(self) \
         ((SATCompleteTraceExecutor_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SAT_COMPLETE_TRACE_EXECUTOR_CHECK_INSTANCE(self) \
         (nusmv_assert(SAT_COMPLETE_TRACE_EXECUTOR(self) != SAT_COMPLETE_TRACE_EXECUTOR(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof SATCompleteTraceExecutor
  \brief The SATCompleteTraceExecutor class constructor

  The SATCompleteTraceExecutor class constructor

  \sa SATCompleteTraceExecutor_destroy
*/
SATCompleteTraceExecutor_ptr
SATCompleteTraceExecutor_create(const BeFsm_ptr fsm,
                                const BeEnc_ptr enc,
                                const BddEnc_ptr bdd_enc);

/*!
  \methodof SATCompleteTraceExecutor
  \brief The SATCompleteTraceExecutor class destructor

  The SATCompleteTraceExecutor class destructor

  \sa SATCompleteTraceExecutor_create
*/
void SATCompleteTraceExecutor_destroy(SATCompleteTraceExecutor_ptr self);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_TRACE_EXEC_SATCOMPLETE_TRACE_EXECUTOR_H__ */
