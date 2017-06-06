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
  \brief Private and protected interface of class 'FlattenerCore'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_CORE_PRIVATE_H__
#define __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_CORE_PRIVATE_H__


#include "nusmv/core/compile/flattening/FlattenerCore.h"
#include "nusmv/core/compile/flattening/FlattenerBase.h"
#include "nusmv/core/compile/flattening/FlattenerBase_private.h"
#include "nusmv/core/utils/defs.h"


/*!
  \brief FlattenerCore class definition derived from
               class FlattenerBase

  \sa Base class FlattenerBase
*/

typedef struct FlattenerCore_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(FlattenerBase);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} FlattenerCore;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof FlattenerCore
  \brief The FlattenerCore class private initializer

  The FlattenerCore class private initializer

  \sa FlattenerCore_create
*/
void flattener_core_init(FlattenerCore_ptr self, const NuSMVEnv_ptr env,
                                const char* name, int low, size_t num);

/*!
  \methodof FlattenerCore
  \brief The FlattenerCore class private deinitializer

  The FlattenerCore class private deinitializer
*/
void flattener_core_deinit(FlattenerCore_ptr self);

/*!
  \methodof FlattenerCore
  \brief Do the actual flattening

*/
node_ptr
flattener_core_flatten(FlattenerBase_ptr self,
                       SymbTable_ptr symb_table,
                       hash_ptr def_hash,
                       node_ptr sexp,
                       node_ptr context,
                       MasterCompileFlattener_def_mode mode);


#endif /* __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_CORE_PRIVATE_H__ */
