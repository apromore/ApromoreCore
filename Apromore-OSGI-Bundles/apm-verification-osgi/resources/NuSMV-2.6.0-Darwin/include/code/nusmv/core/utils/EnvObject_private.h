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
  \brief Private and protected interface of class 'EnvObject'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_UTILS_ENV_OBJECT_PRIVATE_H__
#define __NUSMV_CORE_UTILS_ENV_OBJECT_PRIVATE_H__

#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/object_private.h"
#include "nusmv/core/utils/utils.h"


/*!
  \brief EnvObject class definition derived from
               class Object



  \sa Base class Object
*/

typedef struct EnvObject_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(Object);

  NuSMVEnv_ptr environment;

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} EnvObject;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof EnvObject
  \brief The EnvObject class private initializer

  The EnvObject class private initializer

  \sa EnvObject_create
*/
void env_object_init(EnvObject_ptr self, NuSMVEnv_ptr env);

/*!
  \methodof EnvObject
  \brief The EnvObject class private deinitializer

  The EnvObject class private deinitializer

  \sa EnvObject_destroy
*/
void env_object_deinit(EnvObject_ptr self);

/*!
  \methodof EnvObject
  \brief The EnvObject class private cloner fun

  The EnvObject class private cloner fun

  \sa EnvObject_create
*/
void env_object_copy_aux(const EnvObject_ptr self, EnvObject_ptr copy);


#endif /* __NUSMV_CORE_UTILS_ENV_OBJECT_PRIVATE_H__ */
