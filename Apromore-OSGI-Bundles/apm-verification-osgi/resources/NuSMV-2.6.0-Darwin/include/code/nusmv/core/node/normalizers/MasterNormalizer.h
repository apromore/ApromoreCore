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
  \author Alessandro Mariotti
  \brief Public interface of class 'MasterNormalizer'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_NODE_NORMALIZERS_MASTER_NORMALIZER_H__
#define __NUSMV_CORE_NODE_NORMALIZERS_MASTER_NORMALIZER_H__

#include "nusmv/core/node/node.h"
#include "nusmv/core/node/MasterNodeWalker.h"

#include "nusmv/core/utils/utils.h"

/*!
  \struct MasterNormalizer
  \brief Definition of the public accessor for class MasterNormalizer

  
*/
typedef struct MasterNormalizer_TAG*  MasterNormalizer_ptr;

/*!
  \brief To cast and check instances of class MasterNormalizer

  These macros must be used respectively to cast and to check
  instances of class MasterNormalizer
*/
#define MASTER_NORMALIZER(self) \
         ((MasterNormalizer_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MASTER_NORMALIZER_CHECK_INSTANCE(self) \
         (nusmv_assert(MASTER_NORMALIZER(self) != MASTER_NORMALIZER(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MASTER_NORMALIZER_ASSERT_IS_NODE_NORMALIZED(self, node)        \
  (nusmv_assert(node == MasterNormalizer_normalize_node(self, node)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MASTER_NORMALIZER_ASSERT_IS_NODE_NOT_NORMALIZED(self, node)        \
  (nusmv_assert(Nil == node || node != MasterNormalizer_normalize_node(self, node)))


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* Class methods */

/*!
  \methodof MasterNormalizer
  \brief The MasterNormalizer class constructor

  The MasterNormalizer class constructor

  \sa Object_destroy
*/
MasterNormalizer_ptr MasterNormalizer_create(const NuSMVEnv_ptr env);

/*!
  \methodof MasterNormalizer
  \brief Traverses the tree, and returns a possibly new tree that
  is a normalized copy of the first. Use for constant-time comparison
  of two trees

  
*/
node_ptr
MasterNormalizer_normalize_node(MasterNormalizer_ptr self, node_ptr n);

/*!
  \methodof MasterNormalizer
  \brief Looks in the internal memoization cache for a
                      match. Returns Nil if no memoized data has been found

  Looks in the internal memoization cache for a
                      match. Returns Nil if no memoized data has been found
*/
node_ptr
MasterNormalizer_lookup_cache(MasterNormalizer_ptr self, node_ptr n);

/*!
  \methodof MasterNormalizer
  \brief Inserts new data in the internal memoization cache.

  Inserts new data in the internal memoization cache.
*/
void
MasterNormalizer_insert_cache(MasterNormalizer_ptr self, node_ptr n,
                              node_ptr find);

/**AutomaticEnd***************************************************************/


#endif /* __NUSMV_CORE_NODE_NORMALIZERS_MASTER_NORMALIZER_H__ */
