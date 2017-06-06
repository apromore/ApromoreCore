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
  \brief Public interface of class 'DependencyCore'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_CORE_H__
#define __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_CORE_H__


#include "nusmv/core/compile/dependency/DependencyBase.h"
#include "nusmv/core/utils/defs.h"

/*!
  \struct DependencyCore
  \brief Definition of the public accessor for class DependencyCore

  
*/
typedef struct DependencyCore_TAG*  DependencyCore_ptr;

/*!
  \brief To cast and check instances of class DependencyCore

  These macros must be used respectively to cast and to check
  instances of class DependencyCore
*/
#define DEPENDENCY_CORE(self) \
         ((DependencyCore_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEPENDENCY_CORE_CHECK_INSTANCE(self) \
         (nusmv_assert(DEPENDENCY_CORE(self) != DEPENDENCY_CORE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof DependencyCore
  \brief The DependencyCore class constructor

  The DependencyCore class constructor

  \sa DependencyCore_destroy
*/
DependencyCore_ptr DependencyCore_create(const NuSMVEnv_ptr env,
                                                const char* name);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_CORE_H__ */
