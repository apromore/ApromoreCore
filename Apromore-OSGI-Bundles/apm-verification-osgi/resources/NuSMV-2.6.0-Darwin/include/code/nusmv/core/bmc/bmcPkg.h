/* ---------------------------------------------------------------------------


  This file is part of the ``bmc'' package of NuSMV version 2.
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
  \brief Public interface for any package-related functionality.

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_BMC_PKG_H__
#define __NUSMV_CORE_BMC_BMC_PKG_H__

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
  \brief Initializes the BMC structure

  It builds the vars manager, initializes the package and
   all sub packages, but only if not previously called.
*/
void Bmc_Init(NuSMVEnv_ptr env);

/*!
  \brief Frees all resources allocated for the BMC model manager

  
*/
void Bmc_Quit(NuSMVEnv_ptr env);

/*!
  \brief De0Initializes the BMC internal structures, but not all
   dependencies. Call Bmc_Quit to deinitialize everything it is is what
   you need instead.

  
*/
void Bmc_QuitData(void);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void Bmc_init_opt(NuSMVEnv_ptr env);

/*!
  \brief 

  Creates the BE fsm from the Sexpr FSM. Currently the be
   enc is a singleton global private variable which is shared between
   all the BE FSMs. If not previoulsy committed (because a boolean
   encoder was not available at the time due to the use of coi) the
   determinization layer will be committed to the be encoder
*/
void Bmc_Pkg_build_master_be_fsm(const NuSMVEnv_ptr env);

/*!
  \brief Initialize the bmc package and optionally builds the be
  fsm and registers the trace executors

  
*/
int Bmc_Pkg_bmc_setup(NuSMVEnv_ptr env,
                             boolean forced);

/*!
  \brief A service for commands, to check if bmc
  has been built

  If coi is not enabled than bmc must be set up,
  otherwise it is only required bmc to have initialized. Returns 1 if
  the execution should be stopped, and prints an error message if it
  is the case (to the given optional file). If everything is fine,
  returns 0 and prints nothing. If 'forced' is true, than the model is
  required to be built even if coi is enabled, and a message is
  printed accordingly (used by the commands that always require that
  the model is built (e.g. bmc_simulate).
*/
int Bmc_check_if_model_was_built(NuSMVEnv_ptr env,
                                        FILE* err, boolean forced);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_PKG_H__ */

