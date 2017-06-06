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
  \brief The header file for the SatZchaff class.

  Zchaff is an incremental SAT solver.
  SatZchaff inherits the SatIncSolver (interface) class

*/


#ifndef __NUSMV_CORE_SAT_SOLVERS_SAT_ZCHAFF_H__
#define __NUSMV_CORE_SAT_SOLVERS_SAT_ZCHAFF_H__

#include "nusmv/core/sat/SatIncSolver.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct SatZchaff
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct SatZchaff_TAG* SatZchaff_ptr;

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
#define SAT_ZCHAFF(x) \
         ((SatZchaff_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SAT_ZCHAFF_CHECK_INSTANCE(x) \
         (nusmv_assert(SAT_ZCHAFF(x) != SAT_ZCHAFF(NULL)))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* SatZchaff Constructor/Destructors */

/*!
  \methodof SatZchaff
  \brief Creates a Zchaff SAT solver and initializes it.

  The first parameter is the name of the solver.
*/
SatZchaff_ptr SatZchaff_create(const NuSMVEnv_ptr env,
                                      const char* name);

/*!
  \methodof SatZchaff
  \brief Destroys a Zchaff SAT solver instence

  The first parameter is the name of the solver.
*/
void SatZchaff_destroy(SatZchaff_ptr self);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_SAT_SOLVERS_SAT_ZCHAFF_H__ */
