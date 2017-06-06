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
  \brief Basic services for object-oriented design

  Class Object is a simple pure base class, to be used as base
  for a class hierarchy

*/



#ifndef __NUSMV_CORE_UTILS_OBJECT_H__
#define __NUSMV_CORE_UTILS_OBJECT_H__

#include <memory.h>
#include <assert.h>

#include "nusmv/core/utils/utils.h"

/*!
  \struct Object
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct Object_TAG*  Object_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OBJECT(x)  \
        ((Object_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OBJECT_CHECK_INSTANCE(x)  \
        (nusmv_assert(OBJECT(x) != OBJECT(NULL)))


/* ---------------------------------------------------------------------- */
/* OO keywords:                                                           */

/* Marks a method that can be overriden */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define VIRTUAL

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define INHERITS_FROM(x) \
       x  __parent__

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OVERRIDE(Class, virtual_method) \
       ((Class*) self)->virtual_method
/* ---------------------------------------------------------------------- */



/* ---------------------------------------------------------------------- */
/* Public interface                                                       */
/* ---------------------------------------------------------------------- */

/*!
  \methodof Object
  \brief Class virtual destructor

  Class virtual destructor. Call this to destroy any
  instance of any derived class.
*/
VIRTUAL void Object_destroy(Object_ptr self, void* arg);

/*!
  \methodof Object
  \brief Class virtual copy constructor

  Call this by passing any class instance derived from
                      Object. Cast the result to the real class type
                      to assign the returned value.
                      Since Object is a virtual class, it cannot be
                      really instantiated. This means that the copy constructor
                      must be implemented by derived class if the copy is
                      a needed operation. 
*/
VIRTUAL Object_ptr Object_copy(const Object_ptr self);


#endif /* __NUSMV_CORE_UTILS_OBJECT_H__ */
