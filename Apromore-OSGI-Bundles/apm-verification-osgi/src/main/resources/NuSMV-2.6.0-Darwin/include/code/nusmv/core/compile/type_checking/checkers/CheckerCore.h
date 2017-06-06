/* ---------------------------------------------------------------------------


  This file is part of the ``compile.type_checking.checkers'' package of NuSMV version 2.
  Copyright (C) 2004 by FBK-irst.

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
  \brief Public interface of class 'CheckerCore'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_COMPILE_TYPE_CHECKING_CHECKERS_CHECKER_CORE_H__
#define __NUSMV_CORE_COMPILE_TYPE_CHECKING_CHECKERS_CHECKER_CORE_H__


#include "nusmv/core/compile/type_checking/checkers/CheckerBase.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct CheckerCore
  \brief Definition of the public accessor for class CheckerCore

  
*/
typedef struct CheckerCore_TAG*  CheckerCore_ptr;

/*!
  \brief To cast and check instances of class CheckerCore

  These macros must be used respectively to cast and to check
  instances of class CheckerCore
*/
#define CHECKER_CORE(self) \
         ((CheckerCore_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CHECKER_CORE_CHECK_INSTANCE(self) \
         (nusmv_assert(CHECKER_CORE(self) != CHECKER_CORE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof CheckerCore
  \brief The CheckerCore class constructor

  The CheckerCore class constructor

  \sa NodeWalker_destroy
*/
CheckerCore_ptr CheckerCore_create(const NuSMVEnv_ptr env);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_COMPILE_TYPE_CHECKING_CHECKERS_CHECKER_CORE_H__ */
