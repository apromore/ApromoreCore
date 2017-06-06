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
  \brief Public interface of class 'UStringMgr'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_USTRING_MGR_H__
#define __NUSMV_CORE_UTILS_USTRING_MGR_H__


#include "nusmv/core/utils/utils.h"

/*!
  \struct UStringMgr
  \brief Definition of the public accessor for class UStringMgr


*/
typedef struct UStringMgr_TAG*  UStringMgr_ptr;

/*!
  \brief To cast and check instances of class UStringMgr

  These macros must be used respectively to cast and to check
  instances of class UStringMgr
*/
#define USTRING_MGR(self) \
         ((UStringMgr_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define USTRING_MGR_CHECK_INSTANCE(self) \
         (nusmv_assert(USTRING_MGR(self) != USTRING_MGR(NULL)))

typedef struct string_ {
  struct string_ *link;
  const char* text;
} string_rec;

/*!
  \struct string_
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct string_ *string_ptr;


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof UStringMgr
  \brief The  UStringMgr class constructor

  The  UStringMgr class constructor

  \sa  UStringMgr_destroy
*/
UStringMgr_ptr UStringMgr_create(void);

/*!
  \methodof UStringMgr
  \brief The  UStringMgr class destructor

  The  UStringMgr class destructor

  \sa  UStringMgr_create
*/
void UStringMgr_destroy(UStringMgr_ptr self);

/*!
  \methodof UStringMgr
  \brief Returns the unique representation of the given string

  Returns the unique representation of the given string
*/
string_ptr UStringMgr_find_string(UStringMgr_ptr self, const char* string);

/*!
  \brief Get the char representation of the given unique string

  Get the char representation of the given unique
  string. The returned string belongs to self, not modify or free it.

  \sa  UStringMgr_create
*/
const char* UStringMgr_get_string_text(string_ptr str);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_USTRING_MGR_H__ */
