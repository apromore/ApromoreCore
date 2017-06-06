/* ---------------------------------------------------------------------------


  This file is part of the ``compile.type_checking.checkers''
  package of NuSMV version 2. Copyright (C) 2006 by FBK-irst.

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
  \brief Public interface of class 'CheckerBase'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_COMPILE_TYPE_CHECKING_CHECKERS_CHECKER_BASE_H__
#define __NUSMV_CORE_COMPILE_TYPE_CHECKING_CHECKERS_CHECKER_BASE_H__

#include "nusmv/core/node/node.h"
#include "nusmv/core/node/NodeWalker.h"

#include "nusmv/core/compile/symb_table/SymbType.h"
#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct CheckerBase
  \brief Definition of the public accessor for class CheckerBase

  
*/
typedef struct CheckerBase_TAG*  CheckerBase_ptr;

/*!
  \brief To cast and check instances of class CheckerBase

  These macros must be used respectively to cast and to check
  instances of class CheckerBase
*/
#define CHECKER_BASE(self) \
         ((CheckerBase_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CHECKER_BASE_CHECK_INSTANCE(self) \
         (nusmv_assert(CHECKER_BASE(self) != CHECKER_BASE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof CheckerBase
  \brief Checks the given node

  This is virtual method. Before calling, please ensure
  the given node can be handled by self, by calling
  CheckerBase_can_handle.

  Note: This method will be never called by the user

  \sa CheckerBase_can_handle
*/
VIRTUAL SymbType_ptr
CheckerBase_check_expr(CheckerBase_ptr self,
                       node_ptr expr, node_ptr context);



/**AutomaticEnd***************************************************************/


#endif /* __NUSMV_CORE_COMPILE_TYPE_CHECKING_CHECKERS_CHECKER_BASE_H__ */
