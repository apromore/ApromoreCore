/* ---------------------------------------------------------------------------


  This file is part of the ``sat'' package of NuSMV version 2.
  Copyright (C) 2004 by FBK-irst.

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
  \author Andrei Tchaltsev, Roberto Cavada
  \brief The public interface for the <tt>sat</tt> package

  This package contains the generic interface to access 
  to sat solvers. A set of specific Sat solvers implementation are internally 
  kept, and are not accessible

*/



#ifndef __NUSMV_CORE_SAT_SAT_H__
#define __NUSMV_CORE_SAT_SAT_H__

/* ====================================================================== */
#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/be/be.h"
#include "nusmv/core/node/node.h"

#include "nusmv/core/sat/SatSolver.h"
#include "nusmv/core/sat/SatIncSolver.h"
/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief A flag indicating that there is at least one incremental
  SAT solver

  
*/

#ifdef NUSMV_HAVE_INCREMENTAL_SAT
#error macro NUSMV_HAVE_INCREMENTAL_SAT must not be defined at this point
#endif

#if NUSMV_HAVE_SOLVER_MINISAT || NUSMV_HAVE_SOLVER_ZCHAFF

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NUSMV_HAVE_INCREMENTAL_SAT 1
#else
#define NUSMV_HAVE_INCREMENTAL_SAT 0
#endif


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Creates a SAT solver (non-incremental) of a given name.

  The name of a solver is case-insensitive. Returns NULL
  if requested sat solver is not available.
*/
SatSolver_ptr Sat_CreateNonIncSolver(const NuSMVEnv_ptr env,
                                            const char* satSolver);

/*!
  \brief Creates a SAT solver (non-incremental) of a given
  name. Proof-logging may be enabled if needed.

  The name of a solver is case-insensitive. Returns NULL
  if requested sat solver is not available. Proof logging is currently
  available only with MiniSat.
*/
SatSolver_ptr Sat_CreateNonIncProofSolver(const NuSMVEnv_ptr env,
                                                 const char* satSolver);

/*!
  \brief Creates an incremental SAT solver instance of a given
  name.

  The name of a solver is case-insensitive. Returns NULL
  if requested sat solver is not available.
*/
SatIncSolver_ptr Sat_CreateIncSolver(const NuSMVEnv_ptr env,
                                            const char* satSolver);

/*!
  \brief Creates an incremental proof logging SAT solver instance
  of a given name.

  The name of a solver is case-insensitive. Returns NULL
  if requested sat solver is not available.
*/
SatIncSolver_ptr Sat_CreateIncProofSolver(const NuSMVEnv_ptr env,
                                                 const char* satSolver);

/*!
  \brief Given a string representing the name of a sat solver,
  returns a normalized solver name -- just potential changes in character cases
  

  In case of an error, if an input string does not
  represented any solver, returns (const char*) NULL. Returned string
  must not be freed.
*/
const char* Sat_NormalizeSatSolverName(const char* solverName);

/*!
  \brief Prints out the sat solvers names the system currently
  supplies

  

  \sa Sat_GetAvailableSolversString
*/
void Sat_PrintAvailableSolvers(FILE* file);

/*!
  \brief Retrieves a string with the sat solvers names the
                      system currently supplies

  Returned string must be freed

  \sa Sat_PrintAvailableSolvers
*/
char* Sat_GetAvailableSolversString(void);


/* ====================================================================== */

#endif /* __NUSMV_CORE_SAT_SAT_H__ */
