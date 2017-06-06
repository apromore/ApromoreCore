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
  \brief Public interface of class 'FlattenerCore'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_CORE_H__
#define __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_CORE_H__


#include "nusmv/core/compile/flattening/FlattenerBase.h"
#include "nusmv/core/utils/defs.h"

/*!
  \struct FlattenerCore
  \brief Definition of the public accessor for class FlattenerCore

*/
typedef struct FlattenerCore_TAG*  FlattenerCore_ptr;

/*!
  \brief To cast and check instances of class FlattenerCore

  These macros must be used respectively to cast and to check
  instances of class FlattenerCore
*/
#define FLATTENER_CORE(self) \
         ((FlattenerCore_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FLATTENER_CORE_CHECK_INSTANCE(self) \
         (nusmv_assert(FLATTENER_CORE(self) != FLATTENER_CORE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof FlattenerCore
  \brief The FlattenerCore class constructor

  The FlattenerCore class constructor
*/
FlattenerCore_ptr
FlattenerCore_create(const NuSMVEnv_ptr env, const char* name);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_COMPILE_FLATTENING_FLATTENER_CORE_H__ */
