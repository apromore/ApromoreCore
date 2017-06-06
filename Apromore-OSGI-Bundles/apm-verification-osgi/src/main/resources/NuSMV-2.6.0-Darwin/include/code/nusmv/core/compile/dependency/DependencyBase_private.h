/* ---------------------------------------------------------------------------


  This file is part of the ``compile.dependency'' package of NuSMV version 2.
  Copyright (C) 2013 by FBK-irst.

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
  \author Sergio Mover
  \brief Private and protected interface of class 'DependencyBase'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_BASE_PRIVATE_H__
#define __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_BASE_PRIVATE_H__


#include "nusmv/core/compile/dependency/DependencyBase.h"
#include "nusmv/core/node/NodeWalker.h"
#include "nusmv/core/node/NodeWalker_private.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/defs.h"


/*!
  \brief DependencyBase class definition derived from
               class NodeWalker

  

  \sa Base class NodeWalker
*/

typedef struct DependencyBase_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(NodeWalker);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  Set_t (*get_dependencies) (DependencyBase_ptr self,
                             SymbTable_ptr symb_table,
                             node_ptr formula, node_ptr context,
                             SymbFilterType filter,
                             boolean preserve_time, int time,
                             hash_ptr dependencies_hash);
} DependencyBase;


/*!
  \brief Short way of calling dependency_base_throw_get_definition

  Use this macro to recursively recall the
   get_definition function.
*/

#define _THROW(symb_table, formula, context, filter, preserve_time, time, dependency_hash) \
  dependency_base_throw_get_dependencies(DEPENDENCY_BASE(self),                \
                                         symb_table,                           \
                                         formula, context, filter,             \
                                         preserve_time,                        \
                                         time, dependency_hash)

/*!
  \brief Macro used to insert a result in the hash avoiding leaks.

  Macro used to insert a result in the hash avoiding leaks.
*/

#define _INSERT_IN_HASH(dependencies_hash, key, result, is_to_be_inserted)    \
  if (is_to_be_inserted) {                                                    \
    /* this assures absence of leaks */                                       \
    nusmv_assert((Set_t) NULL ==                                              \
                 formula_dependency_lookup_hash(dependencies_hash,            \
                                                NODE_PTR(&key)));             \
    if (Set_IsEmpty(result)) {                                                \
      formula_dependency_insert_hash(dependencies_hash, NODE_PTR(&key),       \
                                     EMPTY_DEP_SET);                          \
    }                                                                         \
    else formula_dependency_insert_hash(dependencies_hash, NODE_PTR(&key),    \
                                        result);                              \
  }                                                                           \

/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof DependencyBase
  \brief Creates and initializes a dependency.
  To be usable, the dependency will have to be registered to a
  FormulaDependency.

  To each instance it is associated a partition of
  consecutive indices over the symbols set.
  The lowest index of the partition is given through the parameter
  low, while num is the partition size.
  Name is used to easily identify the instances.

  This constructor is private, as this class is virtual
*/
DependencyBase_ptr
DependencyBase_create(const NuSMVEnv_ptr env, const char* name, int low, size_t num);

/*!
  \methodof DependencyBase
  \brief The DependencyBase class private initializer

  The DependencyBase class private initializer

  \sa DependencyBase_create
*/
void dependency_base_init(DependencyBase_ptr self,
                                 const NuSMVEnv_ptr env,
                                 const char* name, int low, size_t num,
                                 boolean can_handle_null);

/*!
  \methodof DependencyBase
  \brief The DependencyBase class private deinitializer

  The DependencyBase class private deinitializer
*/
void dependency_base_deinit(DependencyBase_ptr self);

/*!
  \methodof DependencyBase
  \brief This method must be called by the virtual method
  throw_get_dependencies to recursively get the dependencies of an expression

  
*/
Set_t
dependency_base_throw_get_dependencies(DependencyBase_ptr self,
                                       SymbTable_ptr symb_table,
                                       node_ptr formula, node_ptr context,
                                       SymbFilterType filter,
                                       boolean preserve_time, int time,
                                       hash_ptr dependencies_hash);


#endif /* __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_BASE_PRIVATE_H__ */
