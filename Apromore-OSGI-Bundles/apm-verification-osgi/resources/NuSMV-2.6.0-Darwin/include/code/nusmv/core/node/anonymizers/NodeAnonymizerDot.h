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
  \brief Public interface of class 'NodeAnonymizerDot'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_DOT_H__
#define __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_DOT_H__


#include "nusmv/core/node/anonymizers/NodeAnonymizerBase.h"
#include "nusmv/core/utils/defs.h"

/*!
  \struct NodeAnonymizerDot
  \brief Definition of the public accessor for class NodeAnonymizerDot

  
*/
typedef struct NodeAnonymizerDot_TAG*  NodeAnonymizerDot_ptr;

/*!
  \brief To cast and check instances of class NodeAnonymizerDot

  These macros must be used respectively to cast and to check
  instances of class NodeAnonymizerDot
*/
#define NODE_ANONYMIZER_DOT(self) \
         ((NodeAnonymizerDot_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_ANONYMIZER_DOT_CHECK_INSTANCE(self) \
         (nusmv_assert(NODE_ANONYMIZER_DOT(self) != NODE_ANONYMIZER_DOT(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof NodeAnonymizerDot
  \brief The NodeAnonymizerDot class constructor

  The NodeAnonymizerDot class constructor

  \sa NodeAnonymizerDot_destroy
*/
NodeAnonymizerDot_ptr NodeAnonymizerDot_create(NuSMVEnv_ptr env,
                                                      const char* default_prefix,
                                                      size_t memoization_threshold);

/*!
  \methodof NodeAnonymizerDot
  \brief The NodeAnonymizerDot class destructor

  The NodeAnonymizerDot class destructor

  \sa NodeAnonymizerDot_create
*/
void NodeAnonymizerDot_destroy(NodeAnonymizerDot_ptr self);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_DOT_H__ */
