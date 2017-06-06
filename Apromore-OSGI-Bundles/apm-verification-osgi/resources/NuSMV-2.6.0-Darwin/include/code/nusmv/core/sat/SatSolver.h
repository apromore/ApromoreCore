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
  \brief The header file for the SatSolver class.

  A non-incremental SAT solver interface

*/


#ifndef __NUSMV_CORE_SAT_SAT_SOLVER_H__
#define __NUSMV_CORE_SAT_SAT_SOLVER_H__

#include "nusmv/core/be/be.h"
#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Term void *

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TermFactoryCallbacksUserData_ptr void *

/* a flag returned by the 'solve' methods */
typedef enum SatSolverResult_TAG
{ SAT_SOLVER_INTERNAL_ERROR=-1,
  SAT_SOLVER_TIMEOUT,
  SAT_SOLVER_MEMOUT,
  SAT_SOLVER_SATISFIABLE_PROBLEM,
  SAT_SOLVER_UNSATISFIABLE_PROBLEM,
  SAT_SOLVER_UNAVAILABLE
} SatSolverResult;

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct SatSolver
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct SatSolver_TAG* SatSolver_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef nusmv_ptrint SatSolverGroup;
typedef nusmv_ptrint SatSolverItpGroup;

typedef struct TermFactoryCallbacks_TAG {
  Term (*make_false)(TermFactoryCallbacksUserData_ptr user_data);
  Term (*make_true)(TermFactoryCallbacksUserData_ptr user_data);

  Term (*make_and)(Term t1, Term t2, TermFactoryCallbacksUserData_ptr user_data);
  Term (*make_or)(Term t1, Term t2, TermFactoryCallbacksUserData_ptr user_data);
  Term (*make_not)(Term t, TermFactoryCallbacksUserData_ptr user_data);

  Term (*make_var)(int var, TermFactoryCallbacksUserData_ptr user_data);
} TermFactoryCallbacks;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef TermFactoryCallbacks* TermFactoryCallbacks_ptr;

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
#define SAT_SOLVER(x) \
         ((SatSolver_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SAT_SOLVER_CHECK_INSTANCE(x) \
         (nusmv_assert(SAT_SOLVER(x) != SAT_SOLVER(NULL)))

/**AutomaticStart*************************************************************/
/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* SatSolver Destructors */

/*!
  \methodof SatSolver
  \brief Destoys an instance of a  SAT solver

  
*/
void SatSolver_destroy(SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief Returns the permanent group of this class instance.

  Every solver has one permanent group that can not be destroyed.
  This group may has more efficient representation and during invocations
  of any 'solve' functions, the permanent group will always be
  included into the groups to be solved.
*/
SatSolverGroup
SatSolver_get_permanent_group(const SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief Adds a CNF formula to a group 

  
  The function does not specify the polarity of the formula.
  This should be done by SatSolver_set_polarity.
  In general, if polarity is not set any value can be assigned to the formula
  and its variables  (this may potentially slow down the solver because
  there is a number of variables whose value can be any and solver will try to
  assign values to them though it is not necessary). Moreover, some solver
  (such as ZChaff) can deal with non-redundent clauses only, so the input
  clauses must be non-redundent: no variable can be in the same clause twice.
  CNF formular may be a constant.

  \sa SatSolver_set_polarity
*/
VIRTUAL void
SatSolver_add(const SatSolver_ptr self,
              const Be_Cnf_ptr cnfProb,
              SatSolverGroup group);

/*!
  \methodof SatSolver
  \brief Sets the polarity of a CNF formula in a group

  Polarity 1 means the formula will be considered in this group
  as positive. Polarity -1 means the formula will be considered in this group
  as negative. The formula is not added to the group, just the formula's
  polarity. The formula can be added to a group with SatSolver_add.
  The formula and its polarity can be added to different groups.
  CNF formular may be a constant.

  \sa SatSolver_add
*/
VIRTUAL void
SatSolver_set_polarity(const SatSolver_ptr self,
                       const Be_Cnf_ptr cnfProb,
                       int polarity,
                       SatSolverGroup group);

/*!
  \methodof SatSolver
  \brief Sets preferred variables in the solver

  Sets preferred variables in the solver

  \sa SatSolver_clear_preferred_variables
*/
VIRTUAL void
SatSolver_set_preferred_variables(const SatSolver_ptr self,
                                  const Slist_ptr cnfVars);

/*!
  \methodof SatSolver
  \brief Returns the conflicts resulting from a previous call
  to solving under assumptions

  

  \sa SatSolverResult
*/
VIRTUAL Slist_ptr
SatSolver_get_conflicts(const SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief Clear preferred variables in the solver

  Clear preferred variables in the solver

  \sa SatSolver_set_preferred_variables
*/
VIRTUAL void
SatSolver_clear_preferred_variables(const SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief Solves all groups belonging to the solver and returns the flag

  

  \sa SatSolverResult
*/
VIRTUAL SatSolverResult
SatSolver_solve_all_groups(const SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief Solves all groups belonging to the solver assuming the cnf
  assumptions, and returns the flag

  

  \sa SatSolverResult
*/
VIRTUAL SatSolverResult
SatSolver_solve_all_groups_assume(const SatSolver_ptr self, Slist_ptr assumptions);

/*!
  \methodof SatSolver
  \brief Returns the model (of previous solving)

   The previous solving call should have returned SATISFIABLE.
  The returned list is a list of values in dimac form (positive literal
  is included as the variable index, negative literal as the negative
  variable index, if a literal has not been set its value is not included).

  Returned list belongs to self and must be not destroyed or changed.
*/
VIRTUAL Slist_ptr
SatSolver_get_model(const SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief 

  
*/
VIRTUAL int
SatSolver_get_cnf_var(const SatSolver_ptr self, int var);

/*!
  \methodof SatSolver
  \brief Enables or disables random mode for polarity.

  If given seed is != 0, then random polarity mode is enabled
  with given seed, otherwise random mode is disabled
*/
VIRTUAL void
SatSolver_set_random_mode(SatSolver_ptr self, double seed);

/*!
  \methodof SatSolver
  \brief Sets the current polarity mode

  
*/
VIRTUAL void
SatSolver_set_polarity_mode(SatSolver_ptr self, int mode);

/*!
  \methodof SatSolver
  \brief Gets the current polarity mode

  
*/
VIRTUAL int
SatSolver_get_polarity_mode(const SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief Returns the name of the solver

  
*/
const char*
SatSolver_get_name(const SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief Returns the time of last solving

  
*/
long
SatSolver_get_last_solving_time(const SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief Returns current itp group

  
*/
SatSolverItpGroup
SatSolver_curr_itp_group(const SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief Returns the time of last solving

  
*/
SatSolverItpGroup
SatSolver_new_itp_group(const SatSolver_ptr self);

/*!
  \methodof SatSolver
  \brief Returns the time of last solving

  
*/
Term
SatSolver_extract_interpolant(const SatSolver_ptr self, int nof_ga_groups,
                              SatSolverItpGroup* ga_groups,
                              TermFactoryCallbacks_ptr callbacks,
                              TermFactoryCallbacksUserData_ptr user_data);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_SAT_SAT_SOLVER_H__  */
