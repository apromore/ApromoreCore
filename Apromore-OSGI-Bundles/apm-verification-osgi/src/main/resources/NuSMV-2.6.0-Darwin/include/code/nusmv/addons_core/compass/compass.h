/* ---------------------------------------------------------------------------


  This file is part of the ``compass'' package of NuSMV version 2.
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
  \brief The header file of the <tt>compass</tt> addon.

  The <tt>compass</tt> implementation package

*/


#ifndef __NUSMV_ADDONS_CORE_COMPASS_COMPASS_H__
#define __NUSMV_ADDONS_CORE_COMPASS_COMPASS_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/enc/bdd/BddEnc.h"
#include "nusmv/core/fsm/bdd/BddFsm.h"
#include "nusmv/core/dd/dd.h"


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
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Initializes the addon

  
*/
void Compass_init(NuSMVEnv_ptr env);

/*!
  \brief Reinitializes the addon

  
*/
void Compass_reset(NuSMVEnv_ptr env);

/*!
  \brief Deinitializes the addon

  
*/
void Compass_quit(NuSMVEnv_ptr env);

/*!
  \brief 

  
*/
int Compass_write_sigref(NuSMVEnv_ptr env,
                                 BddFsm_ptr fsm,
                                 FILE* sigref_file,
                                 FILE* prob_file, /* can be NULL */
                                 FILE* ap_file, /* can be NULL */
                                 Expr_ptr tau, /* can be NULL */
                                 boolean do_indent /* Beautify the XML output */
                                 );

/*!
  \brief Handles the piece of sigref format regarding the language
(<variables> ... </variables>) 

  Returns 0 if successful, a negative number if an occurs
*/
int
Compass_write_language_sigref(BddEnc_ptr enc, FILE* file);

/*!
  \brief Prints recursively an ADD node.
<dd_node ... </dd_node>

  Returns 0 if successful, a negative number if an occurs
*/
int
Compass_print_add_sigref_format(DDMgr_ptr dd, add_ptr add, FILE* file,
                                boolean do_indent);

#endif /* __NUSMV_ADDONS_CORE_COMPASS_COMPASS_H__ */
