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
  \brief Private and protected interface of class 'NodeWalker'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_NODE_NODE_WALKER_PRIVATE_H__
#define __NUSMV_CORE_NODE_NODE_WALKER_PRIVATE_H__


#include "nusmv/core/node/NodeWalker.h"
#include "nusmv/core/node/MasterNodeWalker.h"

#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/EnvObject_private.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/array.h"


/*!
  \brief NodeWalker class definition derived from
               class Object



  \sa Base class Object
*/

typedef struct NodeWalker_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(EnvObject);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  char* name;
  int low;
  size_t num;
  boolean can_handle_null;

  MasterNodeWalker_ptr master;

  array_t* node_transformations;

} NodeWalker;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only        */
/* ---------------------------------------------------------------------- */

/*!
  \methodof NodeWalker
  \brief Creates and initializes a walker.
  To be usable, the walker will have to be registered to a MasterNodeWalker

  To each walker is associated a partition of
  consecutive indices over the symbols set. The lowest index of the
  partition is given through the parameter low, while num is the
  partition size. Name is used to easily identify walker instances.

  Constructor is private, as this class is a virtual base class.

  can_handle_null must be set to true if the walker can handle the
  null case.  The null case is trasversal to the partitions set, so
  only the first registered walker that can handle null case will be
  called to handle the null node.

  \sa NodeWalker_destroy
*/
NodeWalker_ptr NodeWalker_create(const NuSMVEnv_ptr env,
                                        const char* name,
                                        int low, size_t num,
                                        boolean can_handle_null);

/*!
  \methodof NodeWalker
  \brief The NodeWalker class private initializer

  The NodeWalker class private initializer

  \sa NodeWalker_create
*/
void
node_walker_init(NodeWalker_ptr self, const NuSMVEnv_ptr env,
                 const char* name, int low, size_t num,
                 boolean can_handle_null);

/*!
  \methodof NodeWalker
  \brief The NodeWalker class private deinitializer

  The NodeWalker class private deinitializer

  \sa NodeWalker_destroy
*/
void node_walker_deinit(NodeWalker_ptr self);

/*!
  \methodof NodeWalker
  \brief This method is privately called by master while registering the
  walker

  If already assigned to a master, it unregisters itself
  from the old master before setting the new master
*/
void
node_walker_set_master(NodeWalker_ptr self,
                       MasterNodeWalker_ptr master);

/*!
  \methodof NodeWalker
  \brief Returns true if the walker can handle the null case

  The null case is trasversal to the partitions set, so
  only the first registered walker that can handle null case will be
  called to handle the null node.
*/
boolean
node_walker_can_handle_null_node(const NodeWalker_ptr self);


/*! \methodof NodeWalker

  Protected method to call the transformation chain of nodes iff the
  user has set a transformation chain.

  \sa NodeWalker_add_node_transformation
*/
node_ptr node_walker_run_transformation_chain(const NodeWalker_ptr self,
                                              node_ptr node);

#endif /* __NUSMV_CORE_NODE_NODE_WALKER_PRIVATE_H__ */
