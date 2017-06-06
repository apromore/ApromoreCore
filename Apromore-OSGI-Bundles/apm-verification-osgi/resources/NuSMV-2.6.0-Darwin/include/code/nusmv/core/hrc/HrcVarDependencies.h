/* ---------------------------------------------------------------------------


  This file is part of the ``hrc'' package of NuSMV version 2. 
  Copyright (C) 2009 by FBK-irst. 

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
  \brief Public interface of class 'HrcVarDependencies'

  Public interface of class 'HrcVarDependencies'

*/



#ifndef __NUSMV_CORE_HRC_HRC_VAR_DEPENDENCIES_H__
#define __NUSMV_CORE_HRC_HRC_VAR_DEPENDENCIES_H__


#include "nusmv/core/node/node.h"
#include "nusmv/core/set/set.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct HrcVarDependencies
  \brief Definition of the public accessor for class HrcVarDependencies

  
*/
typedef struct HrcVarDependencies_TAG*  HrcVarDependencies_ptr;

/*!
  \brief To cast and check instances of class HrcVarDependencies

  These macros must be used respectively to cast and to check
  instances of class HrcVarDependencies
*/
#define HRC_VAR_DEPENDENCIES(self) \
         ((HrcVarDependencies_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define HRC_VAR_DEPENDENCIES_CHECK_INSTANCE(self) \
         (nusmv_assert(HRC_VAR_DEPENDENCIES(self) != HRC_VAR_DEPENDENCIES(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof HrcVarDependencies
  \brief The HrcVarDependencies class constructor

  The HrcVarDependencies class constructor

  \sa HrcVarDependencies_destroy
*/
HrcVarDependencies_ptr HrcVarDependencies_create(NodeMgr_ptr nodemgr);

/*!
  \methodof HrcVarDependencies
  \brief The HrcVarDependencies class destructor

  The HrcVarDependencies class destructor

  \sa HrcVarDependencies_create
*/
void HrcVarDependencies_destroy(HrcVarDependencies_ptr self);

/*!
  \methodof HrcVarDependencies
  \brief Getter for the Node Manager

  Getter for the Node Manager
*/
NodeMgr_ptr
HrcVarDependencies_get_node_manager(const HrcVarDependencies_ptr self);

/*!
  \methodof HrcVarDependencies
  \brief Add a variable to the variable set.

  Add a variable to the variable set.
  No checks are performed to ensure that variable is really a variable.
*/
void 
HrcVarDependencies_add_variable(HrcVarDependencies_ptr self,
                                node_ptr variable);

/*!
  \methodof HrcVarDependencies
  \brief Add a define to the define set.

  Add a define to the define set.
  No checks are performed to ensure that define is really a define.
*/
void 
HrcVarDependencies_add_define(HrcVarDependencies_ptr self,
                              node_ptr define);

/*!
  \methodof HrcVarDependencies
  \brief Adds a formal and an actual parameter.

  
*/
void 
HrcVarDependencies_add_parameter(HrcVarDependencies_ptr self,
                                 node_ptr formal,
                                 node_ptr actual);

/*!
  \methodof HrcVarDependencies
  \brief Returns the set that contains all the variables.

  
*/
Set_t 
HrcVarDependencies_get_variables_set(HrcVarDependencies_ptr self);

/*!
  \methodof HrcVarDependencies
  \brief Returns the set that contains all the defines.

  
*/
Set_t 
HrcVarDependencies_get_defines_set(HrcVarDependencies_ptr self);

/*!
  \methodof HrcVarDependencies
  \brief Returns the set that contains all the formal
  parameters.

  
*/
Set_t 
HrcVarDependencies_get_formal_par_set(HrcVarDependencies_ptr self);

/*!
  \methodof HrcVarDependencies
  \brief Returns the set that contains all the actual
  parameters.

  
*/
Set_t 
HrcVarDependencies_get_actual_par_set(HrcVarDependencies_ptr self);

/*!
  \methodof HrcVarDependencies
  \brief Checks if formal parameter is contained in the
  formal_par_set.

  
*/
boolean 
HrcVarDependencies_has_formal_parameter(HrcVarDependencies_ptr self,
                                        node_ptr formal);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_HRC_HRC_VAR_DEPENDENCIES_H__ */
