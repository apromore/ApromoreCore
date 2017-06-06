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
  \brief Private and protected interface of class 'LogicRecognizerBase'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_WFF_LR_LOGIC_RECOGNIZER_BASE_PRIVATE_H__
#define __NUSMV_CORE_WFF_LR_LOGIC_RECOGNIZER_BASE_PRIVATE_H__


#include "nusmv/core/wff/lr/LogicRecognizerBase.h"
#include "nusmv/core/node/NodeWalker.h"
#include "nusmv/core/node/NodeWalker_private.h"
#include "nusmv/core/utils/defs.h"
#include "nusmv/core/wff/lr/MasterLogicRecognizer_private.h"

/*!
  \brief Shorthand for the recursion over the recognize method of
  the master or self

  
*/
#define LR_THROW(self, exp, ctx)                                        \
  (NodeWalker_can_handle(NODE_WALKER(self), exp) ?                      \
   LOGIC_RECOGNIZER_BASE(self)->recognize(self, exp, ctx) :               \
   master_logic_recognizer_recognize(MASTER_LOGIC_RECOGNIZER(NODE_WALKER(self)->master), \
                                     exp, ctx))

/*!
  \brief LogicRecognizerBase class definition derived from
               class NodeWalker

  Base class for the walkers that detects the logic to which a wff
  belong

  \sa Base class NodeWalker
*/

typedef struct LogicRecognizerBase_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(NodeWalker);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  LogicType (*recognize)(LogicRecognizerBase_ptr self,
                               node_ptr wff,
                               node_ptr context);

} LogicRecognizerBase;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof LogicRecognizerBase
  \brief The LogicRecognizerBase class private initializer

  The LogicRecognizerBase class private initializer

  \sa LogicRecognizerBase_create
*/
void logic_recognizer_base_init(LogicRecognizerBase_ptr self,
                                       const NuSMVEnv_ptr env,
                                       const char* name,
                                       int low,
                                       size_t num);

/*!
  \methodof LogicRecognizerBase
  \brief The LogicRecognizerBase class private deinitializer

  The LogicRecognizerBase class private deinitializer

  \sa LogicRecognizerBase_destroy
*/
void logic_recognizer_base_deinit(LogicRecognizerBase_ptr self);

/*!
  \methodof LogicRecognizerBase
  \brief Returns the logic to which expression belongs

  Pure virtual method, must be implemented
*/
LogicType logic_recognizer_base_recognize(LogicRecognizerBase_ptr self,
                                                       node_ptr wff,
                                                       node_ptr context);



#endif /* __NUSMV_CORE_WFF_LR_LOGIC_RECOGNIZER_BASE_PRIVATE_H__ */
