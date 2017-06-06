
/* ---------------------------------------------------------------------------


  This file is part of the ``compass.compile'' package of NuSMV version 2. 
  Copyright (C) 2008 by FBK-irst. 

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
  \brief Public interface of class 'ProbAssign'

  \todo: Missing description

*/



#ifndef __NUSMV_ADDONS_CORE_COMPASS_COMPILE_PROB_ASSIGN_H__
#define __NUSMV_ADDONS_CORE_COMPASS_COMPILE_PROB_ASSIGN_H__


#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct ProbAssign
  \brief Definition of the public accessor for class ProbAssign

  
*/
typedef struct ProbAssign_TAG*  ProbAssign_ptr;

/*!
  \brief To cast and check instances of class ProbAssign

  These macros must be used respectively to cast and to check
  instances of class ProbAssign
*/
#define PROB_ASSIGN(self) \
         ((ProbAssign_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROB_ASSIGN_CHECK_INSTANCE(self) \
         (nusmv_assert(PROB_ASSIGN(self) != PROB_ASSIGN(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof ProbAssign
  \brief The ProbAssign class constructor

  The ProbAssign class constructor

  \sa ProbAssign_destroy
*/
ProbAssign_ptr 
ProbAssign_create(node_ptr assigns, node_ptr value);

/*!
  \methodof ProbAssign
  \brief The ProbAssign class destructor

  The ProbAssign class destructor

  \sa ProbAssign_create
*/
void ProbAssign_destroy(ProbAssign_ptr self);

/*!
  \methodof ProbAssign
  \brief Getters for the vars assignments

  
*/
node_ptr 
ProbAssign_get_assigns_expr(const ProbAssign_ptr self);

/*!
  \methodof ProbAssign
  \brief getters for the probabilistic value

  
*/
node_ptr ProbAssign_get_prob(const ProbAssign_ptr self);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_ADDONS_CORE_COMPASS_COMPILE_PROB_ASSIGN_H__ */
