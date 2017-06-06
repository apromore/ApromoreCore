/* ---------------------------------------------------------------------------


  This file is part of the ``enc.utils'' package of NuSMV version 2. 
  Copyright (C) 2005 by FBK-irst. 

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
  \brief Public interface of class OrdGroups. 

  This class represents a set of groups of variables to
  be kept grouped

*/


#ifndef __NUSMV_CORE_ENC_UTILS_ORD_GROUPS_H__
#define __NUSMV_CORE_ENC_UTILS_ORD_GROUPS_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/node/node.h"

/*!
  \struct OrdGroups
  \brief Set of groups of vars.

  Variables can be organized in groups, and the class
  provides methods that allow for searching the group one variable
  belongs to, and the set of variables that a group contains.
*/
typedef struct OrdGroups_TAG*  OrdGroups_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ORD_GROUPS(x)  \
        ((OrdGroups_ptr) (x))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ORD_GROUPS_CHECK_INSTANCE(x)  \
        (nusmv_assert(ORD_GROUPS(x) != ORD_GROUPS(NULL)))

/*!
  \methodof OrdGroups
  \brief Class constructor

  
*/
OrdGroups_ptr OrdGroups_create(void);

/*!
  \methodof OrdGroups
  \brief Class copy constructor

  Returned instance is a copy of self
*/
OrdGroups_ptr OrdGroups_copy(const OrdGroups_ptr self);

/*!
  \methodof OrdGroups
  \brief Class destructor

  
*/
void OrdGroups_destroy(OrdGroups_ptr self);

/*!
  \methodof OrdGroups
  \brief Creates a new group, and returns the group ID for 
  future reference

  
*/
int OrdGroups_create_group(OrdGroups_ptr self);

/*!
  \methodof OrdGroups
  \brief Adds a new variable to the groups set.

  The addition is performed only if the variable has not
  been already added to the same group.  If the variable has been
  already added but to a different group, an error occurs. The group 
  must be already existing.
*/
void 
OrdGroups_add_variable(OrdGroups_ptr self, node_ptr name, 
                       int group);

/*!
  \methodof OrdGroups
  \brief Adds a list of variable to the groups set.

  The addition of each variable is performed only if the
  variable has not been already added to the same group.  If the
  variable has been already added but to a different group, an error
  occurs. The group must be already existing.
*/
void
OrdGroups_add_variables(OrdGroups_ptr self, NodeList_ptr vars, 
                        int group);

/*!
  \methodof OrdGroups
  \brief Returns the set of variables that belong to a given group

  Returned list instance still belongs to self.
*/
NodeList_ptr 
OrdGroups_get_vars_in_group(const OrdGroups_ptr self, int group);

/*!
  \methodof OrdGroups
  \brief Given a var name, it returns the group that variable
  belongs to.

  -1 is returned if the variable does not belong to any
  group.
*/
int
OrdGroups_get_var_group(const OrdGroups_ptr self, node_ptr name);

/*!
  \methodof OrdGroups
  \brief Returns the number of available groups

  
*/
int OrdGroups_get_size(const OrdGroups_ptr self);

#endif /* __NUSMV_CORE_ENC_UTILS_ORD_GROUPS_H__ */
