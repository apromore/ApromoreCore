/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 2011 by FBK-irst.

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
  \author Alessandro Mariotti
  \brief Public interface of class 'EnvObject'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_ENV_OBJECT_H__
#define __NUSMV_CORE_UTILS_ENV_OBJECT_H__

#if HAVE_CONFIG_H
#  include "nusmv-config.h"
#endif

#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*!
  \struct EnvObject
  \brief Definition of the public accessor for class EnvObject


*/
typedef struct EnvObject_TAG*  EnvObject_ptr;

/*!
  \brief To cast and check instances of class EnvObject

  These macros must be used respectively to cast and to check
  instances of class EnvObject
*/
#define ENV_OBJECT(self) \
         ((EnvObject_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_OBJECT_CHECK_INSTANCE(self) \
         (nusmv_assert(ENV_OBJECT(self) != ENV_OBJECT(NULL)))

/*!
  \brief Macro that returns the environment from an env object

  \todo Missing description

  \se none
*/
#define ENV_OBJECT_GET_ENV(self) \
        EnvObject_get_environment(ENV_OBJECT(self))

/*!
  \brief Handy shortcut for EnvObject_get_environment
  \sa EnvObject_get_environment
*/
#define EnvObject_env \
  EnvObject_get_environment


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof EnvObject
  \brief The EnvObject class constructor

  The EnvObject class constructor

  \sa EnvObject_destroy
*/
EnvObject_ptr EnvObject_create(const NuSMVEnv_ptr env);

/*!
  \methodof EnvObject
  \brief The EnvObject class destructor

  The EnvObject class destructor

  \sa EnvObject_create
*/
void EnvObject_destroy(EnvObject_ptr self);

/*!
  \methodof EnvObject
  \brief The EnvObject NuSMV environment getter

  The EnvObject NuSMV environment getter

  \sa EnvObject_create
*/
NuSMVEnv_ptr EnvObject_get_environment(const EnvObject_ptr self);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_ENV_OBJECT_H__ */
