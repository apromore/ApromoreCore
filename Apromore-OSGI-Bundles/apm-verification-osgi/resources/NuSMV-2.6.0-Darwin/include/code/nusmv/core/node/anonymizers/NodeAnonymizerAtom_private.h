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
  \brief Private and protected interface of class 'NodeAnonymizerAtom'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_ATOM_PRIVATE_H__
#define __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_ATOM_PRIVATE_H__


#include "nusmv/core/node/anonymizers/NodeAnonymizerAtom.h" 
#include "nusmv/core/node/anonymizers/NodeAnonymizerBase.h" 
#include "nusmv/core/node/anonymizers/NodeAnonymizerBase_private.h" 
#include "nusmv/core/utils/defs.h"


/*!
  \brief NodeAnonymizerAtom class definition derived from
               class NodeAnonymizerBase

  

  \sa Base class NodeAnonymizerBase
*/

typedef struct NodeAnonymizerAtom_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(NodeAnonymizerBase);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} NodeAnonymizerAtom;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof NodeAnonymizerAtom
  \brief The NodeAnonymizerAtom class private initializer

  The NodeAnonymizerAtom class private initializer

  \sa NodeAnonymizerAtom_create
*/
void node_anonymizer_atom_init(NodeAnonymizerAtom_ptr self,
                                      NuSMVEnv_ptr env,
                                      const char* default_prefix,
                                      size_t memoization_threshold);

/*!
  \methodof NodeAnonymizerAtom
  \brief The NodeAnonymizerAtom class private deinitializer

  The NodeAnonymizerAtom class private deinitializer

  \sa NodeAnonymizerAtom_destroy
*/
void node_anonymizer_atom_deinit(NodeAnonymizerAtom_ptr self);

/*!
  \methodof NodeAnonymizerAtom
  \brief True if id is an id

  Here we assume that id is wellformed, we could add debug
  checks
*/
boolean node_anonymizer_atom_is_id(NodeAnonymizerBase_ptr self,
                                          node_ptr id);


#endif /* __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_ATOM_PRIVATE_H__ */
