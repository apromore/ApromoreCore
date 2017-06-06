/* ---------------------------------------------------------------------------


  This file is part of the ``node'' package of NuSMV version 2.
  Copyright (C) 2006 by FBK-irst.

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
  \brief Public interface of class 'NodeWalker'

  A NodeWalker instance is used to traverse a parse tree.
  Depending on the purpose, the class must be specialized. For example
  a node printer, or a type checker would derive from this class.
  A node walker can handle a partition over the set of node's types,
  and one instance can live into a 'master' that is responsible for
  calling the right walker depending on the node it is traversing.

  See for example classes node.printers.PrinterBase and
  compile.type_checking.checkers.CheckerBase

*/



#ifndef __NUSMV_CORE_NODE_NODE_WALKER_H__
#define __NUSMV_CORE_NODE_NODE_WALKER_H__


#include "nusmv/core/node/node.h"

#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct NodeWalker
  \brief Definition of the public accessor for class NodeWalker


*/
typedef struct NodeWalker_TAG*  NodeWalker_ptr;

/*!
  \brief To cast and check instances of class NodeWalker

  These macros must be used respectively to cast and to check
  instances of class NodeWalker
*/
#define NODE_WALKER(self) \
         ((NodeWalker_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_WALKER_CHECK_INSTANCE(self) \
         (nusmv_assert(NODE_WALKER(self) != NODE_WALKER(NULL)))



/*! This structure is used when calling add_node_transformation
  E.g a printer will call all transformation functions which has been
  registered, in a chain, before printing each node.

  This can be used e.g. to handle printing of daggification of
  expressions.
  \sa PrinterBase_print_node
 */
typedef struct NodeTransformation_TAG {
  node_ptr (*func)(const NodeWalker_ptr walker,
                   node_ptr node, void* arg);
  void* arg;  /* the argument to be passed */
} NodeTransformation;


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof NodeWalker
  \brief The NodeWalker class destructor

  The NodeWalker class destructor. If registerd to a
  master, it unregisters itself before finalizing.

  \sa NodeWalker_create
*/
void NodeWalker_destroy(NodeWalker_ptr self);

/*!
  \methodof NodeWalker
  \brief Returns true if the given node belongs to the partition
  associated to this walker

  Returns true if the given node belongs to the partition
  associated to this walker. If n is Nil then the specific walker will be
  asked
*/
boolean
NodeWalker_can_handle(const NodeWalker_ptr self, node_ptr n);

/*!
  \methodof NodeWalker
  \brief Returns the walker name as a string

  The returned string belongs to self, do not deallocate
  or change it.
*/
const char* NodeWalker_get_name(const NodeWalker_ptr self);

/*!
  \methodof NodeWalker
  \brief Checks if self collides with other in terms of their
 respective symbol sets

  Returns true if self and other's symbols set collide
  (i.e. are not partitions). Returns false if they are ok.
*/
boolean NodeWalker_collides(const NodeWalker_ptr self,
                            const NodeWalker_ptr other);


/*!
  \methodof NodeWalker
  \brief Adds a node tranformation function that will be called at each
  step before printing.

  A shallow copy of the given structure will be done and kept internally.
  Returned handle can be used later to remove the transformation.

  \sa NodeWalker_remove_node_transformation
*/
int NodeWalker_add_node_transformation(NodeWalker_ptr self,
                                       const NodeTransformation* nt);


/*!
  \methodof NodeWalker
  Removes a previously registered node transformation.
  \sa NodeWalker_add_node_transformation
*/
void NodeWalker_remove_node_transformation(NodeWalker_ptr self,
                                           int tranf_handle);


#endif /* __NUSMV_CORE_NODE_NODE_WALKER_H__ */
