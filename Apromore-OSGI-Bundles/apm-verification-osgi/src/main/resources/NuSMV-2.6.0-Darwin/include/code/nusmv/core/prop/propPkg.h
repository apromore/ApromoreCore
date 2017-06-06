/* ---------------------------------------------------------------------------


  This file is part of the ``prop'' package of NuSMV version 2. 
  Copyright (C) 2000-2001 by FBK-irst. 

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
  \author Marco Roveri, Roberto Cavada
  \brief Prop package-level declarations

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_PROP_PROP_PKG_H__
#define __NUSMV_CORE_PROP_PROP_PKG_H__


#if HAVE_CONFIG_H
#include "nusmv-config.h"
#endif

#include "nusmv/core/prop/PropDb.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/cinit/NuSMVEnv.h"
 

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
  \brief Initializes the package: master property and 
  property database are allocated

  After you had called this, you must also call 
  PropPkg_init_cmd if you need to use the interactive shell for 
  commands
*/
void PropPkg_init(NuSMVEnv_ptr env);

/*!
  \brief Quits the package

  
*/
void PropPkg_quit(NuSMVEnv_ptr env);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_PROP_PROP_PKG_H__ */
