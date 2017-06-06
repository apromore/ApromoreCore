/* ---------------------------------------------------------------------------


  This file is part of the ``addons_core'' package of NuSMV version 2. 
  Copyright (C) 2007 Fondazione Bruno Kessler. 

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
  \author Marco Roveri
  \brief required

  optional

*/


#ifndef __NUSMV_ADDONS_CORE_ADDONS_CORE_H__
#define __NUSMV_ADDONS_CORE_ADDONS_CORE_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif 

#include "cudd/util.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
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
  \brief Initialization of the AddonsCore Sub-Packages

  Initialization of the AddonsCore Sub-Packages

  \se Sub-Packages are initialized with possible side
  effects on some global variables (e.g., shell commands)

  \sa optional
*/
void AddonsCore_Init(NuSMVEnv_ptr env);

/*!
  \brief Reset the Addons core Sub-Packages

  Reset the Addons core Sub-Packages

  \se Reset all the structures used by the Addons core
  Sub-Packages

  \sa optional
*/
void AddonsCore_Reset(NuSMVEnv_ptr env);

/*!
  \brief Quit the Addons core Sub-Packages

  Quit the Addons core Sub-Packages

  \se Quits all the structures used by the Addons core
  Sub-Packages

  \sa optional
*/
void AddonsCore_Quit(NuSMVEnv_ptr env);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_ADDONS_CORE_ADDONS_CORE_H__ */
