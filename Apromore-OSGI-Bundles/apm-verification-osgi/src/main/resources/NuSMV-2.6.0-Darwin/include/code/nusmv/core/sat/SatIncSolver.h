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
  \brief The header file for the SatIncSolver class.

  An incremental SAT solver interface. 
  SatIncSolver inherits the SatSolver class

*/


#ifndef __NUSMV_CORE_SAT_SAT_INC_SOLVER_H__
#define __NUSMV_CORE_SAT_SAT_INC_SOLVER_H__

#include "nusmv/core/sat/SatSolver.h"
#include "nusmv/core/utils/Olist.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct SatIncSolver
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct SatIncSolver_TAG* SatIncSolver_ptr;

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
#define SAT_INC_SOLVER(x) \
         ((SatIncSolver_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SAT_INC_SOLVER_CHECK_INSTANCE(x) \
         (nusmv_assert(SAT_INC_SOLVER(x) != SAT_INC_SOLVER(NULL)))

/**AutomaticStart*************************************************************/
/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/* SatIncSolver Destructors */

/*!
  \methodof SatIncSolver
  \brief Destroys an instance of a SAT incremental solver

  
*/
void SatIncSolver_destroy(SatIncSolver_ptr self);

/*!
  \methodof SatIncSolver
  \brief Creates a new group and returns its ID. To destroy a group use
  SatIncSolver_destroy_group or
  SatIncSolver_move_to_permanent_and_destroy_group

  

  \sa SatIncSolver_destroy_group,
  SatIncSolver_move_to_permanent_and_destroy_group
*/
VIRTUAL SatSolverGroup
SatIncSolver_create_group(const SatIncSolver_ptr self);

/*!
  \methodof SatIncSolver
  \brief Destroy an existing group (which has been returned by
  SatIncSolver_create_group) and all formulas in it. 

  

  \sa SatIncSolver_create_group
*/
VIRTUAL void
SatIncSolver_destroy_group(const SatIncSolver_ptr self,
                           SatSolverGroup group);

/*!
  \methodof SatIncSolver
  \brief Moves all formulas from a group into the permanent group of
  the solver and then destroy the given group.
  (Permanent group may have more efficient implementation,
  but it can not be destroyed).

  

  \sa SatIncSolver_create_group, SatSolver_get_permanent_group
*/
VIRTUAL void
SatIncSolver_move_to_permanent_and_destroy_group(const SatIncSolver_ptr self,
SatSolverGroup group);

/*!
  \methodof SatIncSolver
  \brief Tries to solve formulas from the groups in the list.

  The permanent group is automatically added to the list.
  Returns a flag whether the solving was successful. If it was successful only
  then SatSolver_get_model may be invoked to obtain the model 

  \sa SatSolverResult,SatSolver_get_permanent_group,
  SatIncSolver_create_group, SatSolver_get_model
*/
VIRTUAL SatSolverResult
SatIncSolver_solve_groups(const SatIncSolver_ptr self,
                          const Olist_ptr groups);

/*!
  \methodof SatIncSolver
  \brief Tries to solve formulas in groups belonging to the solver
  except the groups in the list.

  The permanent group must not be in the list.
  Returns a flag whether the solving was successful. If it was successful only
  then SatSolver_get_model may be invoked to obtain the model 

  \sa SatSolverResult,SatSolver_get_permanent_group,
  SatIncSolver_create_group, SatSolver_get_model
*/
VIRTUAL SatSolverResult
SatIncSolver_solve_without_groups(const SatIncSolver_ptr self,
                                  const Olist_ptr groups);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_SAT_SAT_INC_SOLVER_H__ */
