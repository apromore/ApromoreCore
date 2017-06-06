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
  \brief Public interface of class 'FlattenerBase'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_BASE_H__
#define __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_BASE_H__


#include "nusmv/core/compile/flattening/MasterCompileFlattener.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/node/NodeWalker.h"
#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/utils/defs.h"

/*!
  \struct FlattenerBase
  \brief Definition of the public accessor for class FlattenerBase

  
*/
typedef struct FlattenerBase_TAG*  FlattenerBase_ptr;

/*!
  \brief To cast and check instances of class FlattenerBase

  These macros must be used respectively to cast and to check
  instances of class FlattenerBase
*/
#define FLATTENER_BASE(self) \
         ((FlattenerBase_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FLATTENER_BASE_CHECK_INSTANCE(self) \
         (nusmv_assert(FLATTENER_BASE(self) != FLATTENER_BASE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof FlattenerBase
  \brief Flattens the given expression with the given context.

  Flattens the given expression with the given
  context.

  The caller (the master) has to check if the current flattener may
  handle sexp.
*/
VIRTUAL node_ptr FlattenerBase_flatten(FlattenerBase_ptr self,
                                              SymbTable_ptr symb_table,
                                              hash_ptr def_hash,
                                              node_ptr sexp,
                                              node_ptr context,
                                              MasterCompileFlattener_def_mode mode);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_BASE_H__ */
