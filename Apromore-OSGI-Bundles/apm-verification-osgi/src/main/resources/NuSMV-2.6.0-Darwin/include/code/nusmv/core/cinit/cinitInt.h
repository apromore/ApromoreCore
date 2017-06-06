/* ---------------------------------------------------------------------------


  This file is part of the ``cinit'' package of NuSMV version 2.
  Copyright (C) 1998-2001 by CMU and FBK-irst.

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
  \author Adapted to NuSMV by Marco Roveri
  \brief Internal declarations for the main package.

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_CINIT_CINIT_INT_H__
#define __NUSMV_CORE_CINIT_CINIT_INT_H__

#include "nusmv/core/cinit/cinit.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/compile/compile.h"

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Calls the initialization routines of all the packages.

  \todo Missing description

  \se Sets the global variables outstream, errstream,
  nusmv_historyFile.

  \sa SmEnd
*/
void CInit_init(NuSMVEnv_ptr env);

/*!
  \brief Calls the end routines of all the packages.

  \todo Missing description

  \se Closes the output files if not the standard ones.

  \sa CInit_Init
*/
void CInit_end(NuSMVEnv_ptr env);

/*!
  \brief Shuts down and restarts the system, shut down part

  Shuts down and restarts the system, shut down part

  \sa CInit_reset_last
*/
void CInit_reset_first(NuSMVEnv_ptr env);

/*!
  \brief Shuts down and restarts the system, restart part

  Shuts down and restarts the system, restart part

  \sa CInit_reset_first
*/
void CInit_reset_last(NuSMVEnv_ptr env);


/*!
  \brief The batch main.

  The batch main. It first read the input file, than
  flatten the hierarchy. After this preliminar phase it creates the
  boolean variables necessary for the encoding and then starts
  compiling the read model into BDD. Now computes the reachable states
  depending if the flag has been set. Before starting verifying if the
  properties specified in the model hold or not it computes the
  fairness constraints. You can also activate the reordering and
  also choose to avoid the verification of the properties.
*/
void CInit_batch_main(NuSMVEnv_ptr env);

/*---------------------------------------------------------------------------*/
/* Variable declaration                                                      */
/*---------------------------------------------------------------------------*/

extern cmp_struct_ptr cmps;
extern node_ptr parsed_tree;

#endif /* __NUSMV_CORE_CINIT_CINIT_INT_H__ */
