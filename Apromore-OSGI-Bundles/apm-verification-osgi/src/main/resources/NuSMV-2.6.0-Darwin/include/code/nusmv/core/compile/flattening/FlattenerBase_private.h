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
  \brief Private and protected interface of class 'FlattenerBase'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_BASE_PRIVATE_H__
#define __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_BASE_PRIVATE_H__


#include "nusmv/core/compile/flattening/FlattenerBase.h"
#include "nusmv/core/node/NodeWalker.h"
#include "nusmv/core/node/NodeWalker_private.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/defs.h"


/*!
  \brief FlattenerBase class definition derived from
               class NodeWalker

  

  \sa Base class NodeWalker
*/

typedef struct FlattenerBase_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(NodeWalker);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  node_ptr (*flatten) (FlattenerBase_ptr self,
                       SymbTable_ptr symb_table,
                       hash_ptr def_hash,
                       node_ptr sexp,
                       node_ptr context,
                       MasterCompileFlattener_def_mode mode);



} FlattenerBase;

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief Short way of calling flattener_base_throw_flatten_node

  Use this macro to recursively recall the flatten function
*/

#define _THROW(sexp, symb_table, def_hash, context, mode)                    \
  flattener_base_throw_flatten(FLATTENER_BASE(self), symb_table, def_hash,   \
                               sexp, context, mode)

/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof FlattenerBase
  \brief Creates and initializes a flattener.
  To be usable, the flattener will have to be registered to a
  MasterCompileFlattener.

  To each flattener is associated a partition of
  consecutive indices over the symbols set.
  The lowest index of the partition is given through the parameter
  low, while num is the partition size.
  Name is used to easily identify printer instances.

  This constructor is private, as this class is virtual
*/
FlattenerBase_ptr
FlattenerBase_create(const NuSMVEnv_ptr env, const char* name, int low, size_t num);

/*!
  \methodof FlattenerBase
  \brief The FlattenerBase class private initializer

  The FlattenerBase class private initializer

  \sa FlattenerBase_create
*/
void flattener_base_init(FlattenerBase_ptr self, const NuSMVEnv_ptr env,
                                const char* name, int low, size_t num,
                                boolean can_handle_null);

/*!
  \methodof FlattenerBase
  \brief The FlattenerBase class private deinitializer

  The FlattenerBase class private deinitializer
*/
void flattener_base_deinit(FlattenerBase_ptr self);

/*!
  \methodof FlattenerBase
  \brief Flatten the given node

  
*/
node_ptr
flattener_base_flatten(FlattenerBase_ptr self,
                       SymbTable_ptr symb_table,
                       hash_ptr def_hash,
                       node_ptr sexp,
                       node_ptr context,
                       MasterCompileFlattener_def_mode mode);

/*!
  \methodof FlattenerBase
  \brief This method must be called by the virtual method
  throw_flatten to recursively flatten sexp

  
*/
node_ptr
flattener_base_throw_flatten(FlattenerBase_ptr self,
                             SymbTable_ptr symb_table,
                             hash_ptr def_hash,
                             node_ptr sexp,
                             node_ptr context,
                             MasterCompileFlattener_def_mode mode);


#endif /* __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_BASE_PRIVATE_H__ */
