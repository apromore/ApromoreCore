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
  \brief Public interface of class 'MasterNodeWalker'

  This class is intended to be a generic container for
  node walkers. To each walker is associated a partition over the set
  of node's types, and the master is responsible for calling the right
  walker depending on the type of the node that is being traversed

*/



#ifndef __NUSMV_CORE_NODE_MASTER_NODE_WALKER_H__
#define __NUSMV_CORE_NODE_MASTER_NODE_WALKER_H__

#include "nusmv/core/node/NodeWalker.h"

#include "nusmv/core/utils/utils.h"

/*!
  \struct MasterNodeWalker
  \brief Definition of the public accessor for class MasterNodeWalker

  
*/
typedef struct MasterNodeWalker_TAG*  MasterNodeWalker_ptr;

/*!
  \brief To cast and check instances of class MasterNodeWalker

  These macros must be used respectively to cast and to check
  instances of class MasterNodeWalker
*/
#define MASTER_NODE_WALKER(self) \
         ((MasterNodeWalker_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MASTER_NODE_WALKER_CHECK_INSTANCE(self) \
         (nusmv_assert(MASTER_NODE_WALKER(self) != MASTER_NODE_WALKER(NULL)))


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof MasterNodeWalker
  \brief The MasterNodeWalker class constructor

  The MasterNodeWalker class constructor

  \sa MasterNodeWalker_destroy
*/
MasterNodeWalker_ptr MasterNodeWalker_create(const NuSMVEnv_ptr env);

/*!
  \methodof MasterNodeWalker
  \brief The MasterNodeWalker class destructor

  The MasterNodeWalker class destructor

  \sa MasterNodeWalker_create
*/
void MasterNodeWalker_destroy(MasterNodeWalker_ptr self);

/*!
  \methodof MasterNodeWalker
  \brief Registers a walker.

  Return true if successfully registered, false if
  already registered, and throws an exception if could not register, due
  to the walker's partition that collides with already registered walkers.

  Warning: If this method succeeds, the walker instance belongs to
  self, and its life cycle will be controlled by self as long as the
  walker is registered within self. The user must not destroy a
  registered walker. 

  \sa unregister_walker
*/
boolean
MasterNodeWalker_register_walker(MasterNodeWalker_ptr self,
                                 NodeWalker_ptr walker);

/*!
  \methodof MasterNodeWalker
  \brief Unregisters a previously registered walker

  If the walker was registered returns the walker instance.
  If not registered (not found among the currently registered walkers),
  returns NULL but no error occurs. After this method is called,
  

  \sa register_walker
*/
NodeWalker_ptr
MasterNodeWalker_unregister_walker(MasterNodeWalker_ptr self,
                                   const char* name);

/*!
  \methodof MasterNodeWalker
  \brief Returns the regostered walker whose name is given

  If the walker is not found among the registered walkers,
  NULL is returned and no error occurs
*/
NodeWalker_ptr
MasterNodeWalker_get_walker(MasterNodeWalker_ptr self, const char* name);

/**AutomaticEnd***************************************************************/


#endif /* __NUSMV_CORE_NODE_MASTER_NODE_WALKER_H__ */
