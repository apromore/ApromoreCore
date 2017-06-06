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
  \brief Public interface of class 'NodeAnonymizerST'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_ST___
#define __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_ST___


#include "nusmv/core/node/anonymizers/NodeAnonymizerDot.h" 
#include "nusmv/core/utils/defs.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"

/*!
  \struct NodeAnonymizerST
  \brief Definition of the public accessor for class NodeAnonymizerST

  
*/
typedef struct NodeAnonymizerST_TAG*  NodeAnonymizerST_ptr;

/*!
  \brief To cast and check instances of class NodeAnonymizerST

  These macros must be used respectively to cast and to check
  instances of class NodeAnonymizerST
*/
#define NODE_ANONYMIZER_ST(self) \
         ((NodeAnonymizerST_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_ANONYMIZER_ST_CHECK_INSTANCE(self) \
         (nusmv_assert(NODE_ANONYMIZER_ST(self) != NODE_ANONYMIZER_ST(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof NodeAnonymizerST
  \brief The NodeAnonymizerST class constructor

  The NodeAnonymizerST class constructor
  default_prefix must be NULL, since it is not used

  \sa NodeAnonymizerST_destroy
*/
NodeAnonymizerST_ptr NodeAnonymizerST_create(NuSMVEnv_ptr env,
                                                    const char* default_prefix,
                                                    size_t memoization_threshold,
                                                    SymbTable_ptr symb_table);

/*!
  \methodof NodeAnonymizerST
  \brief The NodeAnonymizerST class destructor

  The NodeAnonymizerST class destructor

  \sa NodeAnonymizerST_create
*/
void NodeAnonymizerST_destroy(NodeAnonymizerST_ptr self);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_ST___ */
