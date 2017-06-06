/* ---------------------------------------------------------------------------


  This file is part of the ``node.normalizers'' package of NuSMV version 2.
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
  \author Mariotti Alessandro
  \brief Private and protected interface of class 'NormalizerBase'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_NODE_NORMALIZERS_NORMALIZER_BASE_PRIVATE_H__
#define __NUSMV_CORE_NODE_NORMALIZERS_NORMALIZER_BASE_PRIVATE_H__


#include "nusmv/core/node/normalizers/NormalizerBase.h"
#include "nusmv/core/node/NodeWalker_private.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/NodeMgr.h"
#include "nusmv/core/utils/WordNumberMgr.h"

/*!
  \brief NormalizerBase class definition derived from
               class NodeWalker

  

  \sa Base class Object
*/

typedef struct NormalizerBase_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(NodeWalker);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  node_ptr (*normalize_node)(NormalizerBase_ptr self, node_ptr n);

} NormalizerBase;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only        */
/* ---------------------------------------------------------------------- */

/*!
  \methodof NormalizerBase
  \brief Creates and initializes a normalizer.
  To be usable, the normalizer will have to be registered to a MasterNormalizer.

  To each normalizer is associated a partition of
  consecutive indices over the symbols set. The lowest index of the
  partition is given through the parameter low, while num is the
  partition size. Name is used to easily identify normalizer instances.

  This constructor is private, as this class is virtual

  \sa NormalizerBase_destroy
*/
NormalizerBase_ptr
NormalizerBase_create(const NuSMVEnv_ptr env, const char* name, int low, size_t num);

/*!
  \methodof NormalizerBase
  \brief The NormalizerBase class private initializer

  The NormalizerBase class private initializer

  \sa NormalizerBase_create
*/
void
normalizer_base_init(NormalizerBase_ptr self, const NuSMVEnv_ptr env,
                     const char* name, int low, size_t num,
                     boolean can_handle_null);

/*!
  \methodof NormalizerBase
  \brief The NormalizerBase class private deinitializer

  The NormalizerBase class private deinitializer

  \sa NormalizerBase_destroy
*/
void normalizer_base_deinit(NormalizerBase_ptr self);

/*!
  \methodof NormalizerBase
  \brief This method must be called by the virtual method
  print_node to recursively print sub nodes

  
*/
node_ptr
normalizer_base_throw_normalize_node(NormalizerBase_ptr self,
                                     node_ptr n);


#endif /* __NUSMV_CORE_NODE_NORMALIZERS_NORMALIZER_BASE_PRIVATE_H__ */
