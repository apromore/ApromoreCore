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
  \author Andrei Tchaltsev
  \brief The header file for the SatMinisat class.

  Minisat is an incremental SAT solver.
  SatMinisat inherits the SatIncSolver (interface) class

*/


#ifndef __NUSMV_CORE_SAT_SOLVERS_SAT_MINISAT_H__
#define __NUSMV_CORE_SAT_SOLVERS_SAT_MINISAT_H__

#include "nusmv/core/sat/SatIncSolver.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct SatMinisat
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct SatMinisat_TAG* SatMinisat_ptr;

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SAT_MINISAT(x)                          \
  ((SatMinisat_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SAT_MINISAT_CHECK_INSTANCE(x)                   \
  (nusmv_assert(SAT_MINISAT(x) != SAT_MINISAT(NULL)))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* SatMinisat Constructor/Destructors */

/*!
  \methodof SatMinisat
  \brief Creates a Minisat SAT solver and initializes it.

  The first parameter is the name of the solver.
*/
SatMinisat_ptr SatMinisat_create(const NuSMVEnv_ptr env,
                                        const char* name,
                                        boolean enable_proof_logging);

/*!
  \methodof SatMinisat
  \brief Destroys an instance of a MiniSat SAT solver

  
*/
void SatMinisat_destroy(SatMinisat_ptr self);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_SAT_SOLVERS_SAT_MINISAT_H__ */
