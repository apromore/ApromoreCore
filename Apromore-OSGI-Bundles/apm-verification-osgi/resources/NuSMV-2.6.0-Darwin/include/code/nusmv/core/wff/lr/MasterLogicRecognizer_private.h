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
  \brief Private and protected interface of class 'MasterLogicRecognizer'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_WFF_LR_MASTER_LOGIC_RECOGNIZER_PRIVATE_H__
#define __NUSMV_CORE_WFF_LR_MASTER_LOGIC_RECOGNIZER_PRIVATE_H__


#include "nusmv/core/wff/lr/MasterLogicRecognizer.h"
#include "nusmv/core/node/MasterNodeWalker.h"
#include "nusmv/core/node/MasterNodeWalker_private.h"
#include "nusmv/core/utils/defs.h"
#include "nusmv/core/utils/assoc.h"


/*!
  \brief MasterLogicRecognizer class definition derived from
               class MasterNodeWalker

  Handles a list of LogicRecognizers, and memoize the association
  between formulas and type

  \sa Base class MasterNodeWalker
*/

typedef struct MasterLogicRecognizer_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(MasterNodeWalker);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  hash_ptr expr2logic; /* memoization */

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} MasterLogicRecognizer;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof MasterLogicRecognizer
  \brief The MasterLogicRecognizer class private initializer

  The MasterLogicRecognizer class private initializer

  \sa MasterLogicRecognizer_create
*/
void master_logic_recognizer_init(MasterLogicRecognizer_ptr self,
                                         NuSMVEnv_ptr env);

/*!
  \methodof MasterLogicRecognizer
  \brief The MasterLogicRecognizer class private deinitializer

  The MasterLogicRecognizer class private deinitializer

  \sa MasterLogicRecognizer_destroy
*/
void master_logic_recognizer_deinit(MasterLogicRecognizer_ptr self);

/*!
  \methodof MasterLogicRecognizer
  \brief Returns the logic to which expression belongs

  internal method
*/
LogicType master_logic_recognizer_recognize(MasterLogicRecognizer_ptr self,
                                                         node_ptr expression,
                                                         node_ptr context);

/*!
  \methodof MasterLogicRecognizer
  \brief Insert a new association between expression and logic

  
*/
void master_logic_recognizer_insert(MasterLogicRecognizer_ptr self,
                                           node_ptr expression,
                                           node_ptr context,
                                           LogicType logic);

/*!
  \methodof MasterLogicRecognizer
  \brief Lookups the association for expression

  
*/
LogicType master_logic_recognizer_lookup(MasterLogicRecognizer_ptr self,
                                                      node_ptr expression,
                                                      node_ptr context);

/*!
  \brief Given two LogicType returns the more general one

  Non compatible LogicType are not allowed
*/
LogicType master_logic_recognizer_merge(MasterLogicRecognizer_ptr master,
                                                     LogicType left,
                                                     LogicType right);

#endif /* __NUSMV_CORE_WFF_LR_MASTER_LOGIC_RECOGNIZER_PRIVATE_H__ */
