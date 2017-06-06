/* ---------------------------------------------------------------------------


  This file is part of the ``core.node.anonymizers'' package of NuSMV version 2.
  Copyright (C) 2014 by FBK-irst.

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
  \brief Private and protected interface of class 'NodeAnonymizerDot'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_DOT_PRIVATE_H__
#define __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_DOT_PRIVATE_H__


#include "nusmv/core/node/anonymizers/NodeAnonymizerDot.h"
#include "nusmv/core/node/anonymizers/NodeAnonymizerBase.h"
#include "nusmv/core/node/anonymizers/NodeAnonymizerBase_private.h"
#include "nusmv/core/utils/defs.h"


/*!
  \brief NodeAnonymizerDot class definition derived from
               class NodeAnonymizerBase

  

  \sa Base class NodeAnonymizerBase
*/

typedef struct NodeAnonymizerDot_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(NodeAnonymizerBase);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} NodeAnonymizerDot;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof NodeAnonymizerDot
  \brief The NodeAnonymizerDot class private initializer

  The NodeAnonymizerDot class private initializer

  \sa NodeAnonymizerDot_create
*/
void node_anonymizer_dot_init(NodeAnonymizerDot_ptr self,
                                       NuSMVEnv_ptr env,
                                       const char* default_prefix,
                                       size_t memoization_threshold);

/*!
  \methodof NodeAnonymizerDot
  \brief The NodeAnonymizerDot class private deinitializer

  The NodeAnonymizerDot class private deinitializer

  \sa NodeAnonymizerDot_destroy
*/
void node_anonymizer_dot_deinit(NodeAnonymizerDot_ptr self);

/*!
  \methodof NodeAnonymizerDot
  \brief True if id is an id

  Here we assume that id is wellformed, we could add debug
  checks
*/
boolean node_anonymizer_dot_is_id(NodeAnonymizerBase_ptr self,
                                         node_ptr id);

#endif /* __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_DOT_PRIVATE_H__ */
