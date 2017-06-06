/* ---------------------------------------------------------------------------


  This file is part of the ``be'' package of NuSMV version 2. 
  Copyright (C) 2000-2001 by FBK-irst and University of Trento. 

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
  \brief The interface file for the RBC-dependant Be_Manager 
  implementation.

  Be_RbcManager is a derived class of Be_Manager

*/


#ifndef __NUSMV_CORE_BE_BE_RBC_MANAGER_H__
#define __NUSMV_CORE_BE_BE_RBC_MANAGER_H__

#include "nusmv/core/be/be.h"
#include "nusmv/core/cinit/NuSMVEnv.h"


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Creates a rbc-specific Be_Manager

  You must call Be_RbcManager_Delete when the created instance
  is no longer used.

  \sa Be_RbcManager_Delete
*/
Be_Manager_ptr Be_RbcManager_Create(const NuSMVEnv_ptr env,
                                           const size_t capacity);

/*!
  \brief Destroys the given Be_MAnager instance you previously
  created by using Be_RbcManager_Create

  

  \sa Be_RbcManager_Create
*/
void           Be_RbcManager_Delete(Be_Manager_ptr self);

/*!
  \brief Changes the maximum number of variables the rbc manager can
  handle

  

  \se The given rbc manager will possibly change
*/
void           Be_RbcManager_Reserve(Be_Manager_ptr self, 
                                            const size_t size);

/*!
  \brief Resets RBC cache

  
*/
void           Be_RbcManager_Reset(const Be_Manager_ptr self);

/*!
  \brief Prints out some statistical data about the underlying
  rbc structure

  
*/
void Be_PrintStats(Be_Manager_ptr manager, int clustSize, FILE* outFile);
/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BE_BE_RBC_MANAGER_H__ */

