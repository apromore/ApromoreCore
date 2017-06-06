/* ---------------------------------------------------------------------------


  This file is part of the ``dd'' package of NuSMV version 2.
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
  \brief Public interface of class 'DDMgr'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_DD_DDMGR_H__
#define __NUSMV_CORE_DD_DDMGR_H__


#include "cudd/cudd.h"
#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct DDMgr
  \brief Definition of the public accessor for class DDMgr

  
*/
typedef struct DDMgr_TAG*  DDMgr_ptr;

/*!
  \brief To cast and check instances of class DDMgr

  These macros must be used respectively to cast and to check
  instances of class DDMgr
*/
#define DD_MGR(self) \
         ((DDMgr_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DD_MGR_CHECK_INSTANCE(self) \
         (nusmv_assert(DD_MGR(self) != DD_MGR(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof DDMgr
  \brief The DDMgr class constructor

  The DDMgr class constructor.
                      Requires a valid instance of an Environment

  \sa DDMgr_destroy
*/
DDMgr_ptr DDMgr_create(const NuSMVEnv_ptr env);

/*!
  \methodof DDMgr
  \brief The DDMgr class destructor

  The DDMgr class destructor

  \sa DDMgr_create
*/
void DDMgr_destroy(DDMgr_ptr self);

/*!
  \methodof DDMgr
  \brief returns the internal CUDD DdManager

  returns the internal CUDD DdManager

  \sa DDMgr_create
*/
DdManager* DDMgr_get_dd_manager(const DDMgr_ptr self);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_DD_DDMGR_H__ */
