/* ---------------------------------------------------------------------------


  This file is part of the ``compile.flattening'' package of NuSMV version 2.
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
  \brief Private and protected interface of class 'MasterCompileFlattener'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_COMPILE_FLATTENING_MASTER_COMPILE_FLATTENER_PRIVATE_H__
#define __NUSMV_CORE_COMPILE_FLATTENING_MASTER_COMPILE_FLATTENER_PRIVATE_H__


#include "nusmv/core/compile/flattening/MasterCompileFlattener.h"
#include "nusmv/core/node/MasterNodeWalker.h"
#include "nusmv/core/node/MasterNodeWalker_private.h"
#include "nusmv/core/utils/defs.h"
#include "nusmv/core/utils/assoc.h"


/*!
  \brief MasterCompileFlattener class definition derived from
               class MasterNodeWalker

  

  \sa Base class MasterNodeWalker
*/

typedef struct MasterCompileFlattener_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(MasterNodeWalker);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} MasterCompileFlattener;

/*!
  \brief Body of define in evaluation

  Indicates that the body of a define is under the
   flattening, it is usde to discover possible recursive definitions.
*/
#define MASTER_COMPILE_FLATTENER_BUILDING_FLAT_BODY (node_ptr)-11


/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof MasterCompileFlattener
  \brief The MasterCompileFlattener class private initializer

  The MasterCompileFlattener class private initializer

  \sa MasterCompileFlattener_create
*/
void master_compile_flattener_init(MasterCompileFlattener_ptr self,
                                          const NuSMVEnv_ptr env);

/*!
  \methodof MasterCompileFlattener
  \brief The MasterCompileFlattener class private deinitializer

  The MasterCompileFlattener class private deinitializer

  \sa MasterCompileFlattener_destroy
*/
void master_compile_flattener_deinit(MasterCompileFlattener_ptr self);

/*!
  \methodof MasterCompileFlattener
  \brief Recursive function for flattenig a sexp.

  
   The function changes its behavior depending on the value of mode:
     - Flattener_Get_Def_Mode: in this mode, the defines in the
   expression are returned as-is (i.e. they are not expanded!)
     - Flattener_Expand_Def_Mode: in this mode, all the defines found in
   the formula will be expanded.

   DOCUMENTATION ABOUT ARRAY:

      In NuSMV ARRAY has 2 meanings, it can be a part of identifier
   (which we call identifier-with-brackets) or a part of
   expression. For example, VAR v[5] : boolean; declares a new
   identifier-with-brackets v[5] where [5] is a part of
   identifier. Thus v[d], where d is a define equal to 5, is not a
   valid identifier as well as v[4+1] or v, whereas v[5] is valid.

   For "VAR v : array 1..5 of boolean;" v[5] is identifier (array
   elements are declared in symbol table) v[d] is not,
   but both are valid expressions.

   This difference is important for code, e.g.
     DEFINE d := v;
     INVARSPEC d[5];
   If v[5] is declared as individual identifier this code is invalid
   because v is not declared whereas if v is an array the code becomes
   valid.

   Flattener additionally makes every ARRAY-expression normalized.
   For example, d[i+1] is changed to
   case i+1=0 : v[0]; i+1=1 : v[1]; ... FAILURE; esac.
   Such a way every v[N] become a legal identifier wrt symbol table.
   Note that above normalization is done independent if defines are set
   to be expanded or not.

   NOTE: arrays of modules are not supported. Thus ARRAY before DOT
   can be legal only through identifier-with-brackets declaration, e.g.
   for v[3].b to be valid it is necessary to declare v[3] as module
   instance.

   NOTE: currently this function applies find_atom to the constants
   and IDs and new_node to operations nodes. If this approach changes
   then internal function ltl_rewrite_input has to be changed as well.
   Thus, the returned expression may be NOT NORMALIZED!

  \sa Compile_FlattenSexp Compile_FlattenSexpExpandDefine
*/
node_ptr
master_compile_flattener_flatten(MasterCompileFlattener_ptr self,
                                 SymbTable_ptr symb_table,
                                 hash_ptr def_hash,
                                 node_ptr sexp,
                                 node_ptr context,
                                 MasterCompileFlattener_def_mode def_mode);

/*!
  \methodof MasterCompileFlattener
  \brief Gets the flattened version of an atom.

  Gets the flattened version of an atom. If the
   atom is a define then it is expanded. If the definition mode
   is set to "expand", then the expanded flattened version is returned,
   otherwise, the atom is returned.

  \se The flatten_def_hash is modified in
   order to memoize previously computed definition expansion.
*/
node_ptr
master_compile_flattener_get_definition(MasterCompileFlattener_ptr self,
                                        SymbTable_ptr symb_table,
                                        hash_ptr def_hash,
                                        node_ptr name,
                                        MasterCompileFlattener_def_mode def_mode);

/*!
  \methodof MasterCompileFlattener
  \brief Get the hash of the defines

  
*/
hash_ptr
master_compile_flattener_get_def_hash(MasterCompileFlattener_ptr self,
                                      SymbTable_ptr symb_table);

#endif /* __NUSMV_CORE_COMPILE_FLATTENING_MASTER_COMPILE_FLATTENER_PRIVATE_H__ */
