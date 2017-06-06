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
  \brief Public interface of class 'BDDCompleteTraceExecutor'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_TRACE_EXEC_BDDCOMPLETE_TRACE_EXECUTOR_H__
#define __NUSMV_CORE_TRACE_EXEC_BDDCOMPLETE_TRACE_EXECUTOR_H__


#include "nusmv/core/trace/exec/CompleteTraceExecutor.h" /* fix this */
#include "nusmv/core/utils/utils.h"

#include "nusmv/core/fsm/bdd/BddFsm.h"
#include "nusmv/core/enc/bdd/BddEnc.h"

/*!
  \struct BDDCompleteTraceExecutor
  \brief Definition of the public accessor for class BDDCompleteTraceExecutor

  
*/
typedef struct BDDCompleteTraceExecutor_TAG*  BDDCompleteTraceExecutor_ptr;

/*!
  \brief To cast and check instances of class BDDCompleteTraceExecutor

  These macros must be used respectively to cast and to check
  instances of class BDDCompleteTraceExecutor
*/
#define BDD_COMPLETE_TRACE_EXECUTOR(self) \
         ((BDDCompleteTraceExecutor_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_COMPLETE_TRACE_EXECUTOR_CHECK_INSTANCE(self) \
         (nusmv_assert(BDD_COMPLETE_TRACE_EXECUTOR(self) != BDD_COMPLETE_TRACE_EXECUTOR(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BDDCompleteTraceExecutor
  \brief The BDDCompleteTraceExecutor class constructor

  The BDDCompleteTraceExecutor class constructor

  \sa BDDCompleteTraceExecutor_destroy
*/
BDDCompleteTraceExecutor_ptr
BDDCompleteTraceExecutor_create(const BddFsm_ptr fsm,
                                const BddEnc_ptr enc);

/*!
  \methodof BDDCompleteTraceExecutor
  \brief The BDDCompleteTraceExecutor class destructor

  The BDDCompleteTraceExecutor class destructor

  \sa BDDCompleteTraceExecutor_create
*/
void
BDDCompleteTraceExecutor_destroy(BDDCompleteTraceExecutor_ptr self);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_TRACE_EXEC_BDDCOMPLETE_TRACE_EXECUTOR_H__ */
