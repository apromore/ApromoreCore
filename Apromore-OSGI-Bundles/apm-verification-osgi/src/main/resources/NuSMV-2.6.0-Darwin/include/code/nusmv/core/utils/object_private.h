/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2. 
  Copyright (C) 2003 by FBK-irst.

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
  \brief Basic (private) services for object-oriented design

  Private interface for class Object. To be used only by 
  Object class implementation, and by any derivate class

*/



#ifndef __NUSMV_CORE_UTILS_OBJECT_PRIVATE_H__
#define __NUSMV_CORE_UTILS_OBJECT_PRIVATE_H__

#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h" 

typedef struct Object_TAG
{
  VIRTUAL void (*finalize)(Object_ptr self, void* arg);
  VIRTUAL Object_ptr (*copy)(const Object_ptr self);
} Object; 



/* ---------------------------------------------------------------------- */
/* Private interface to be used by derivate classes only                  */
/* ---------------------------------------------------------------------- */

/*!
  \methodof Object
  \brief Class private inizializer

  This private method must be called by
  derived class inizializer *before* any other operation

  \sa object_deinit
*/
void object_init(Object_ptr self);

/*!
  \methodof Object
  \brief Class private deinizializer

  Must be called by
  derived class inizializer *after* any other operation. The deinizializer
  in derived class must be called only by the finalizer (which is called
  the destructor). No other operation is allowed on the instance is being
  to be destroyed.

  \sa object_init
*/
void object_deinit(Object_ptr self);

/*!
  \methodof Object
  \brief Copy costructor auxiliary private method

  This must be called by any derived class auxiliary copy
  constructor *before* any other operation.

  \sa object_copy
*/
void object_copy_aux(const Object_ptr self, Object_ptr copy);



#endif /* __NUSMV_CORE_UTILS_OBJECT_PRIVATE_H__ */
