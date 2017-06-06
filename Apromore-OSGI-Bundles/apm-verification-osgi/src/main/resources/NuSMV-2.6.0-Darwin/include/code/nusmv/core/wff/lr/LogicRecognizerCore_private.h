/* ---------------------------------------------------------------------------


  This file is part of the ``wff.lr'' package of NuSMV version 2.
  Copyright (C) 2013 by FBK-irst.

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
  \brief Private and protected interface of class 'LogicRecognizerCore'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_WFF_LR_LOGIC_RECOGNIZER_CORE_PRIVATE_H__
#define __NUSMV_CORE_WFF_LR_LOGIC_RECOGNIZER_CORE_PRIVATE_H__


#include "nusmv/core/wff/lr/LogicRecognizerCore.h"
#include "nusmv/core/wff/lr/LogicRecognizerBase.h"
#include "nusmv/core/wff/lr/LogicRecognizerBase_private.h"
#include "nusmv/core/wff/lr/MasterLogicRecognizer_private.h"
#include "nusmv/core/utils/defs.h"


/*!
  \brief LogicRecognizerCore class definition derived from
               class LogicRecognizerBase

  Handles the core symbols

  \sa Base class LogicRecognizerBase
*/

typedef struct LogicRecognizerCore_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(LogicRecognizerBase);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */


} LogicRecognizerCore;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof LogicRecognizerCore
  \brief The LogicRecognizerCore class private initializer

  The LogicRecognizerCore class private initializer

  \sa LogicRecognizerCore_create
*/
void logic_recognizer_core_init(LogicRecognizerCore_ptr self,
                                       const NuSMVEnv_ptr env,
                                       const char* name,
                                       int low,
                                       size_t num);

/*!
  \methodof LogicRecognizerCore
  \brief The LogicRecognizerCore class private deinitializer

  The LogicRecognizerCore class private deinitializer

  \sa LogicRecognizerCore_destroy
*/
void logic_recognizer_core_deinit(LogicRecognizerCore_ptr self);

/*!
  \methodof LogicRecognizerCore
  \brief Returns the logic to which expression belongs

  Given an expression and its context detects the logic or
  the expression type to which it belongs.
  Each recursion stops as soon as further visiting would not change the result:
  * leaves
  * CTL operators
  * LTL operators
*/
LogicType logic_recognizer_core_recognize(LogicRecognizerBase_ptr self,
                                                       node_ptr wff,
                                                       node_ptr context);


#endif /* __NUSMV_CORE_WFF_LR_LOGIC_RECOGNIZER_CORE_PRIVATE_H__ */
