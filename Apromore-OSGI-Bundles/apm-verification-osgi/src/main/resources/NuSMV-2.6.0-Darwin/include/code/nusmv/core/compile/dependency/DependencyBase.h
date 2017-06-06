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
  \brief Public interface of class 'DependencyBase'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_BASE_H__
#define __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_BASE_H__


#include "nusmv/core/compile/dependency/FormulaDependency.h"
#include "nusmv/core/node/NodeWalker.h"
#include "nusmv/core/utils/defs.h"

/*!
  \struct DependencyBase
  \brief Definition of the public accessor for class DependencyBase

  
*/
typedef struct DependencyBase_TAG*  DependencyBase_ptr;

/*!
  \brief To cast and check instances of class DependencyBase

  These macros must be used respectively to cast and to check
  instances of class DependencyBase
*/
#define DEPENDENCY_BASE(self) \
         ((DependencyBase_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEPENDENCY_BASE_CHECK_INSTANCE(self) \
         (nusmv_assert(DEPENDENCY_BASE(self) != DEPENDENCY_BASE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof DependencyBase
  \brief Get the dependencies for expr in the given context.

  Get the dependencies for expr in the given context.

  The caller (the master) has to check if the current dependency may
  handle the expression.
*/
VIRTUAL Set_t
DependencyBase_get_dependencies(DependencyBase_ptr self,
                                SymbTable_ptr symb_table,
                                node_ptr formula, node_ptr context,
                                SymbFilterType filter,
                                boolean preserve_time, int time,
                                hash_ptr dependencies_hash);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_BASE_H__ */
