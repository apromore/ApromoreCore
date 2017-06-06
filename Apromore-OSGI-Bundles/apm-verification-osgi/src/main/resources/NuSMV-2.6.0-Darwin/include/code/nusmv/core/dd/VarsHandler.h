/* ---------------------------------------------------------------------------


  This file is part of the ``dd'' package of NuSMV version 2.
  Copyright (C) 2010 by FBK-irst.

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
  \brief Public interface of class 'VarsHandler'

  VarsHandler handles the allocation of new variables,
  and their organization within 'groups' (blocks in dd
  terminology). This is done to allow multiple BddEnc instances to
  share the same dd space.

*/



#ifndef __NUSMV_CORE_DD_VARS_HANDLER_H__
#define __NUSMV_CORE_DD_VARS_HANDLER_H__

#include "nusmv/core/dd/dd.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct VarsHandler
  \brief Definition of the public accessor for class VarsHandler

  
*/
typedef struct VarsHandler_TAG*  VarsHandler_ptr;

/*!
  \struct GroupInfo
  \brief GroupInfo is an opaque structure which contains the
  information about groups of variables.

  When manipulating variable groups, a pointer to a GroupInfo is
  returned and/or accepted by class VarsHandler.
*/
typedef struct GroupInfo_TAG* GroupInfo_ptr;

/*!
  \brief To cast and check instances of class VarsHandler

  These macros must be used respectively to cast and to check
  instances of class VarsHandler
*/
#define VARS_HANDLER(self) \
         ((VarsHandler_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define VARS_HANDLER_CHECK_INSTANCE(self) \
         (nusmv_assert(VARS_HANDLER(self) != VARS_HANDLER(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof VarsHandler
  \brief The VarsHandler class constructor

  The VarsHandler class constructor

  \sa VarsHandler_destroy
*/
VarsHandler_ptr VarsHandler_create(DDMgr_ptr dd);

/*!
  \methodof VarsHandler
  \brief The VarsHandler class destructor

  The VarsHandler class destructor

  \sa VarsHandler_create
*/
void VarsHandler_destroy(VarsHandler_ptr self);

/*!
  \methodof VarsHandler
  \brief Returns the contained dd manager

  
*/
DDMgr_ptr VarsHandler_get_dd_manager(const VarsHandler_ptr self);

/*!
  \methodof VarsHandler
  \brief Constructs a group, with minimal level from_level, size
  and chunk size. Returns a structure which has to be used to
  release the group later.

  The reservation does not necessarily create a group
  at given level, but may allocate it at a greater level (this is
  why the parameter is called from_level). Returns the group ID,
  and the actual minimal level allocated. When done with it, the
  caller has to release the returned groupinfo with
  VarsHandler_release_group or VarsHandler_dissolve_group

  \sa VarsHandler_release_group,
  VarsHandler_dissolve_group
*/
GroupInfo_ptr
VarsHandler_reserve_group(VarsHandler_ptr self,
                          int from_lev, int size, int chunk,
                          boolean can_share, int* lev_low);

/*!
  \methodof VarsHandler
  \brief Returns true if currently it is possible to
  create/reuse the given group of levels

  This method can be used to check if a group can be
  created at given level.
*/
boolean VarsHandler_can_group(const VarsHandler_ptr self,
                                     int from_lev, int size, int chunk);

/*!
  \methodof VarsHandler
  \brief Releases the group (previously created with
  reserve_group)

  The group is not necessarily released, at it (or
  part of it) may be shared with other created groups. After this
  method has been called, gid cannot be used anymore. Returns true
  iff the group is actually removed.

  \sa VarsHandler_reserve_group
*/
boolean
VarsHandler_release_group(VarsHandler_ptr self, GroupInfo_ptr bid);

/*!
  \methodof VarsHandler
  \brief Releases the given block (previously created with
  reserve_group). Differently from release_group, this method
  actually dissolves the group, all its children (contained groups)
  and all groups containing it (i.e. all parents).

  After this method has been called, gid cannot be
  used anymore. Also, all other GroupInfo instances possibly
  pointing to any of the removed groups will be invalidated, so
  later removals will be handled correctly.

  \sa VarsHandler_reserve_group
*/
void
VarsHandler_dissolve_group(VarsHandler_ptr self, GroupInfo_ptr bid);

/*!
  \methodof VarsHandler
  \brief After a reordering, levels in the dd package may do not
  correspond to the levels in the vars handler. This method re-align the
  vars handler wrt the current levels.

  Realigns the whole internal groups structure, and
  all currently existing GroupInfo instances.
*/
void VarsHandler_update_levels(VarsHandler_ptr self);

/*!
  \methodof VarsHandler
  \brief Prints the content of the VarsHandler

  This is used for debugging/verosity purposes
*/
void VarsHandler_print(const VarsHandler_ptr self, FILE* _file);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_DD_VARS_HANDLER_H__ */
