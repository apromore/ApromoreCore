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
  \brief Public interface of class 'CheckerPsl'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_COMPILE_TYPE_CHECKING_CHECKERS_CHECKER_PSL_H__
#define __NUSMV_CORE_COMPILE_TYPE_CHECKING_CHECKERS_CHECKER_PSL_H__


#include "nusmv/core/compile/type_checking/checkers/CheckerBase.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct CheckerPsl
  \brief Definition of the public accessor for class CheckerPsl

  
*/
typedef struct CheckerPsl_TAG*  CheckerPsl_ptr;

/*!
  \brief To cast and check instances of class CheckerPsl

  These macros must be used respectively to cast and to check
  instances of class CheckerPsl
*/
#define CHECKER_PSL(self) \
         ((CheckerPsl_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CHECKER_PSL_CHECK_INSTANCE(self) \
         (nusmv_assert(CHECKER_PSL(self) != CHECKER_PSL(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof CheckerPsl
  \brief The CheckerPsl class constructor

  The CheckerPsl class constructor

  \sa NodeWalker_destroy
*/
CheckerPsl_ptr CheckerPsl_create(const NuSMVEnv_ptr env);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_COMPILE_TYPE_CHECKING_CHECKERS_CHECKER_PSL_H__ */
