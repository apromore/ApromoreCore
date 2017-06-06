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
  \brief Public interface of class 'SATPartialTraceExecutor'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_TRACE_EXEC_SATPARTIAL_TRACE_EXECUTOR_H__
#define __NUSMV_CORE_TRACE_EXEC_SATPARTIAL_TRACE_EXECUTOR_H__


#include "nusmv/core/trace/exec/PartialTraceExecutor.h"
#include "nusmv/core/utils/utils.h"

#include "nusmv/core/fsm/be/BeFsm.h"
#include "nusmv/core/enc/be/BeEnc.h"
#include "nusmv/core/enc/bdd/BddEnc.h"

/*!
  \struct SATPartialTraceExecutor
  \brief Definition of the public accessor for class SATPartialTraceExecutor

  
*/
typedef struct SATPartialTraceExecutor_TAG*  SATPartialTraceExecutor_ptr;

/*!
  \brief To cast and check instances of class SATPartialTraceExecutor

  These macros must be used respectively to cast and to check
  instances of class SATPartialTraceExecutor
*/
#define SAT_PARTIAL_TRACE_EXECUTOR(self) \
         ((SATPartialTraceExecutor_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SAT_PARTIAL_TRACE_EXECUTOR_CHECK_INSTANCE(self) \
         (nusmv_assert(SAT_PARTIAL_TRACE_EXECUTOR(self) != SAT_PARTIAL_TRACE_EXECUTOR(NULL)))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof SATPartialTraceExecutor
  \brief The SATPartialTraceExecutor class constructor

  The SATPartialTraceExecutor class constructor

  \sa SATPartialTraceExecutor_destroy
*/
SATPartialTraceExecutor_ptr SATPartialTraceExecutor_create(const BeFsm_ptr fsm, const BeEnc_ptr enc, const BddEnc_ptr bdd_enc,
boolean use_restart);

/*!
  \methodof SATPartialTraceExecutor
  \brief The SATPartialTraceExecutor class destructor

  The SATPartialTraceExecutor class destructor

  \sa SATPartialTraceExecutor_create
*/
void
SATPartialTraceExecutor_destroy(SATPartialTraceExecutor_ptr self);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_TRACE_EXEC_SATPARTIAL_TRACE_EXECUTOR_H__ */
