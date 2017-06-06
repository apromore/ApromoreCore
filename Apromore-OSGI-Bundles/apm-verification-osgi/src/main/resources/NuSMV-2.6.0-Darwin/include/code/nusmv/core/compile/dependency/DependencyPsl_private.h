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
  \author Sergio Mover , split in Psl Rade Rudic
  \brief Private and protected interface of class 'DependencyPsl'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_PSL_PRIVATE_H__
#define __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_PSL_PRIVATE_H__


#include "nusmv/core/compile/dependency/DependencyPsl.h"
#include "nusmv/core/compile/dependency/DependencyBase.h"
#include "nusmv/core/compile/dependency/DependencyBase_private.h"
#include "nusmv/core/utils/defs.h"


/*!
  \brief DependencyPsl class definition derived from
               class DependencyBase

  

  \sa Base class DependencyBase
*/

typedef struct DependencyPsl_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(DependencyBase);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} DependencyPsl;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof DependencyPsl
  \brief The DependencyPsl class private initializer

  The DependencyPsl class private initializer

  \sa DependencyPsl_create
*/
void dependency_psl_init(DependencyPsl_ptr self,
                                 const NuSMVEnv_ptr env,
                                 const char* name, int low,
                                 size_t num);

/*!
  \methodof DependencyPsl
  \brief The DependencyPsl class private deinitializer

  The DependencyPsl class private deinitializer

  \sa DependencyPsl_destroy
*/
void dependency_psl_deinit(DependencyPsl_ptr self);



#endif /* __NUSMV_CORE_COMPILE_DEPENDENCY_DEPENDENCY_PSL_PRIVATE_H__ */
