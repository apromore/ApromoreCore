/* ---------------------------------------------------------------------------


  This file is part of the ``compile.dependency'' package of NuSMV version 2.
  Copyright (C) 2013 by FBK-irst.

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
  \author Sergio Mover
  \brief Public interface of class 'FormulaDependency'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_COMPILE_DEPENDENCY_FORMULA_DEPENDENCY_H__
#define __NUSMV_CORE_COMPILE_DEPENDENCY_FORMULA_DEPENDENCY_H__


#include "nusmv/core/node/MasterNodeWalker.h"
#include "nusmv/core/utils/defs.h"
#include "nusmv/core/set/set.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"

/*!
  \struct FormulaDependency
  \brief Definition of the public accessor for class FormulaDependency

  
*/
typedef struct FormulaDependency_TAG*  FormulaDependency_ptr;

/*!
  \brief To cast and check instances of class FormulaDependency

  These macros must be used respectively to cast and to check
  instances of class FormulaDependency
*/
#define FORMULA_DEPENDENCY(self) \
         ((FormulaDependency_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FORMULA_DEPENDENCY_CHECK_INSTANCE(self) \
         (nusmv_assert(FORMULA_DEPENDENCY(self) != FORMULA_DEPENDENCY(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof FormulaDependency
  \brief The FormulaDependency class constructor

  The FormulaDependency class constructor

  \sa FormulaDependency_destroy
*/
FormulaDependency_ptr
FormulaDependency_create(const NuSMVEnv_ptr env);

/*!
  \methodof FormulaDependency
  \brief The FormulaDependency class destructor

  The FormulaDependency class destructor

  \sa FormulaDependency_create
*/
void FormulaDependency_destroy(FormulaDependency_ptr self);

/*!
  \methodof FormulaDependency
  \brief Computes dependencies of a given expression

  The set of dependencies of a given formula are
   computed. A traversal of the formula is performed. Each time a
   variable is encountered, it is added to the so far computed
   set. When a formula depends on a next variable, then the
   corresponding current variable is added to the set. When an atom is
   found a call to <tt>formula_dependency_get_definition_dependencies</tt> is
   performed to compute the dependencies. Returned set must be
   disposed by the caller. This is the same as calling
   Formula_GetDependenciesByType with filter = VFT_CNIF and
   preserve_time = false

  \sa formula_dependency_get_definition_dependencies
*/
Set_t FormulaDependency_get_dependencies(FormulaDependency_ptr self,
                                                SymbTable_ptr symb_table,
                                                node_ptr formula,
                                                node_ptr context);

/*!
  \methodof FormulaDependency
  \brief Computes the dependencies of an SMV expression by type

  The set of dependencies of a given formula are
   computed, as in Formula_GetDependencies, but the variable type filters the
   dependency collection.

   If flag preserve_time is true, then entries in the returned set
   will preserve the time they occur within the formula. For
   example, formula 'a & next(b) = 2 & attime(c, 2) < 4' returns
   {a,b,c} if preserve_time is false, and {a, next(b), attime(c, 2)}
   if preserve_time is true.

   Returned set must be disposed by the caller

  \sa formulaGetDependenciesByTypeAux
   formula_dependency_get_definition_dependencies
*/
Set_t
FormulaDependency_get_dependencies_by_type(FormulaDependency_ptr self,
                                           SymbTable_ptr symb_table,
                                           node_ptr formula, node_ptr context,
                                           SymbFilterType filter,
                                           boolean preserve_time);


/*!
  \methodof FormulaDependency
  \brief Compute the dependencies of two set of formulae by given type

  Given a formula and a list of fairness constraints, the set of
  symbols of the given type occurring in them is computed. Returned
  Set must be disposed by the caller.
*/
Set_t
FormulaDependency_formulae_get_dependencies_by_type(FormulaDependency_ptr,
                                                    SymbTable_ptr, node_ptr,
                                                    node_ptr, node_ptr,
                                                    SymbFilterType, boolean);

/*!
  \methodof FormulaDependency
  \brief Compute the dependencies of two set of formulae

  Given a formula and a list of fairness constraints, the
   set of variables occurring in them is computed. Returned Set must be
   disposed by the caller
*/
Set_t
FormulaDependency_formulae_get_dependencies(FormulaDependency_ptr self,
                                            SymbTable_ptr symb_table,
                                            node_ptr formula,
                                            node_ptr justice,
                                            node_ptr compassion);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_COMPILE_DEPENDENCY_FORMULA_DEPENDENCY_H__ */
