
/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 2009 by FBK-irst.

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
  \author Roberto Cavada
  \brief Public interface of class 'NodeGraph'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_NODE_GRAPH_H__
#define __NUSMV_CORE_UTILS_NODE_GRAPH_H__


#include "nusmv/core/node/node.h"
#include "nusmv/core/set/set.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/printers/MasterPrinter.h"

/*!
  \struct NodeGraph
  \brief Definition of the public accessor for class NodeGraph


*/
typedef struct NodeGraph_TAG*  NodeGraph_ptr;

/*!
  \brief To cast and check instances of class NodeGraph

  These macros must be used respectively to cast and to check
  instances of class NodeGraph
*/
#define NODE_GRAPH(self) \
         ((NodeGraph_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_GRAPH_CHECK_INSTANCE(self) \
         (nusmv_assert(NODE_GRAPH(self) != NODE_GRAPH(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof NodeGraph
  \brief The NodeGraph class constructor

  The NodeGraph class constructor

  \sa NodeGraph_destroy
*/
NodeGraph_ptr NodeGraph_create(void);

/*!
  \methodof NodeGraph
  \brief The NodeGraph class destructor

  The NodeGraph class destructor

  \sa NodeGraph_create
*/
void NodeGraph_destroy(NodeGraph_ptr self);

/*!
  \methodof NodeGraph
  \brief Creates edges from "var" node to the nodes listed
         in the set "children". If parent node
         (var node)is set on NULL, nodes from children
         set will have parent node. The parent will be
         node that contains NULL pointer. NodeGraph_get_parents
         will not return empty set for those nodes.
*/
void
NodeGraph_add_children(NodeGraph_ptr self, node_ptr var,
                       const Set_t children);

/*!
  \methodof NodeGraph
  \brief
*/
void
NodeGraph_remove_nodes(NodeGraph_ptr self, const Set_t nodes);

/*!
  \methodof NodeGraph
  \brief Clears up all information about removed nodes.
*/
void NodeGraph_clear_removed_nodes(NodeGraph_ptr self);

/*!
  \methodof NodeGraph
  \brief Returns true if the graph is empty, taking into account
         of all removed vertices
*/
boolean NodeGraph_is_empty(const NodeGraph_ptr self);

/*!
  \methodof NodeGraph
  \brief Returns the nodes which have the given number of
         children, but those nodes that have been
         removed. Set must be freed by the caller
*/
Set_t NodeGraph_get_leaves(const NodeGraph_ptr self);

/*!
  \methodof NodeGraph
  \brief Returns all the parents of a given node

  Returns a set of all parents of a give node (add with
  NodeGraph_add_children and with child being among children).
  If a parent node has been marked as removed it is not returned.

  The returned set has to be returned by caller.
*/
Set_t NodeGraph_get_parents(const NodeGraph_ptr self,
                            node_ptr child);

/*!
  \methodof NodeGraph
  \brief Prints out the graph
*/
void NodeGraph_print(const NodeGraph_ptr self,
                            MasterPrinter_ptr printer,
                            FILE* out);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_NODE_GRAPH_H__ */
