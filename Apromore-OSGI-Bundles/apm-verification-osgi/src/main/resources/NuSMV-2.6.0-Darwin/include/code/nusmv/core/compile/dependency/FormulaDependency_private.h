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
  \brief Private and protected interface of class 'FormulaDependency'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_COMPILE_DEPENDENCY_FORMULA_DEPENDENCY_PRIVATE_H__
#define __NUSMV_CORE_COMPILE_DEPENDENCY_FORMULA_DEPENDENCY_PRIVATE_H__


#include "nusmv/core/compile/dependency/FormulaDependency.h"
#include "nusmv/core/node/MasterNodeWalker.h"
#include "nusmv/core/node/MasterNodeWalker_private.h"
#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/utils/defs.h"
#include "nusmv/core/utils/Tuple5.h"

/*!
  \brief FormulaDependency class definition derived from
               class MasterNodeWalker

  

  \sa Base class MasterNodeWalker
*/

typedef struct FormulaDependency_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(MasterNodeWalker);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} FormulaDependency;

/*!
  \brief Indicates that the dependency computation is ongoing.

  The value used during the building of dependencies of
   defined symbols to keep track that compuation is ongoing to discover
   circular definitions.
*/
#define BUILDING_DEP_SET (Set_t)-10

/*!
  \brief Indicates that the dependency is empty

  
*/
#define EMPTY_DEP_SET (Set_t)-11

/*!
  \brief Indicates that no dependency has been yet computed.

  
*/
#define NO_DEP_SET (Set_t)-12

/*!
  \brief True if the set is not NULL or equal the fake sets used as
                 placeholder

  
*/
#define IS_VALID_SET(set)                          \
  (EMPTY_DEP_SET != set &&                         \
   BUILDING_DEP_SET != set &&                      \
   NO_DEP_SET != set &&                            \
   (Set_t)NULL != set)

/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof FormulaDependency
  \brief The FormulaDependency class private initializer

  The FormulaDependency class private initializer

  \sa FormulaDependency_create
*/
void formula_dependency_init(FormulaDependency_ptr self,
                                    const NuSMVEnv_ptr env);

/*!
  \methodof FormulaDependency
  \brief The FormulaDependency class private deinitializer

  The FormulaDependency class private deinitializer

  \sa FormulaDependency_destroy
*/
void formula_dependency_deinit(FormulaDependency_ptr self);

/*!
  \methodof FormulaDependency
  \brief Get the set of dependencies for formula.

  Get the set of dependencies for formula.

  \sa formula_dependency_get_definition_dependencies
   FormulaDependency_GetDependenciesByType
*/
Set_t
formula_dependency_get_dependencies(FormulaDependency_ptr self,
                                    SymbTable_ptr symb_table,
                                    node_ptr formula, node_ptr context,
                                    SymbFilterType filter,
                                    boolean preserve_time, int time,
                                    hash_ptr dependencies_hash);

/*!
  \methodof FormulaDependency
  \brief Compute the dependencies of an atom

  This function computes the dependencies of an atom. If
   the atom corresponds to a variable then the singleton with the
   variable is returned. If the atom corresponds to a "running"
   condition the singleton with variable PROCESS_SELECTOR_VAR_NAME is
   returned. Otherwise if the atom corresponds to a defined symbol the
   dependency set corresponding to the body of the definition is
   computed and returned. filter specifies what variables we are
   interested to, as in Formula_GetDependenciesByType, and
   is_inside_next is supposed to be true if the atom is inside a Next,
   false otherwise. Returned set must be disposed by the caller

  \se The <tt>dependencies_hash</tt> is modified in
   order to memoize previously computed dependencies of defined symbols.

  \sa FormulaDependency_GetDependencies
*/
Set_t
formula_dependency_get_definition_dependencies(FormulaDependency_ptr self,
                                               SymbTable_ptr symb_table,
                                               node_ptr formula,
                                               SymbFilterType filter,
                                               boolean preserve_time, int time,
                                               hash_ptr dependencies_hash);

/*!
  \methodof FormulaDependency
  \brief Call SymbTable_get_handled_hash_ptr to get the
   hash table used to store the dependencies

  
*/
hash_ptr formula_dependency_get_hash(FormulaDependency_ptr self,
                                            SymbTable_ptr symb_table);

/*!
  \brief Insertion function for dependencies_hash

  Take a Tuple5_ptr set with Tuple5_init() (memorized on
   the stack), allocates it and insert it in the hash table.
   Assumes the key not be already in the table.
*/
void formula_dependency_insert_hash(hash_ptr dep_hash,
                                           node_ptr key,
                                           Set_t value);

/*!
  \brief Insertion function for dependencies_hash

  To be called after the dependencies of the define
   represented by key has been full computed. Replace the placeholder
   BUILDING_DEP_SET with the result.
   Assumes the key to be already in the table. Take a Tuple5_ptr set with
   Tuple5_init() (memorized on the stack) and use it to replace the associated
   valued in the hash.
*/
void formula_dependency_close_define_hash(hash_ptr dep_hash,
                                                 node_ptr key,
                                                 Set_t value);

/*!
  \brief Lookup function for dependencies_hash

  
*/
Set_t formula_dependency_lookup_hash(hash_ptr dep_hash,
                                            node_ptr key);

/*!
  \brief Make the hash key used by dependencies_hash

  The Tuple5 has to freed by the caller
*/
void
formula_dependency_mk_hash_key(node_ptr e, node_ptr c, SymbFilterType filter,
                               boolean preserve_time, int time,
                               Tuple5_ptr key);



#endif /* __NUSMV_CORE_COMPILE_DEPENDENCY_FORMULA_DEPENDENCY_PRIVATE_H__ */
