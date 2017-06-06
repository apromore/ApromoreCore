/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package.
  Copyright (C) 2012 by FBK.

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
  \author Michele Dorigatti
  \brief Shell interface for the utils package

  \todo: Missing description

*/


#ifndef __NUSMV_SHELL_UTILS_UTILS_CMD_H__
#define __NUSMV_SHELL_UTILS_UTILS_CMD_H__

#include "nusmv/core/utils/defs.h"
#include "nusmv/core/utils/EnvObject.h"
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
  \brief Initializes the commands of the hrc package.

  
*/
void Utils_init_cmd(NuSMVEnv_ptr env);

/*!
  \brief Removes the commands provided by the hrc package.

  
*/
void Utils_quit_cmd(NuSMVEnv_ptr env);

#ifndef NDEBUG

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int CommandUtilsTestSset(NuSMVEnv_ptr env, int argc, char** argv);
#endif

/*!
  \brief Checks if the number of non option arguments is correct

  In the check fails, an error message is printed and the
  usage is called.
  usage can be NULL
*/
int Utils_check_non_option_args(NuSMVEnv_ptr env,
                                       int argc,
                                       unsigned int expected_args,
                                       int (*usage)(void* arg),
                                       void* arg);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_SHELL_UTILS_UTILS_CMD_H__ */
