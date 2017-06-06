/* ---------------------------------------------------------------------------


  This file is part of the ``eval'' package of NuSMV version 2.
  Copyright (C) 2010 by FBK-irst.

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
  \author Marco Pensallorto
  \brief Public interface of class 'BaseEvaluator'

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_EVAL_BASE_EVALUATOR_H__
#define __NUSMV_CORE_TRACE_EVAL_BASE_EVALUATOR_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/utils/object.h"

#include "nusmv/core/wff/ExprMgr.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"

/*!
  \struct BaseEvaluator
  \brief Definition of the public accessor for class BaseEvaluator

  
*/
typedef struct BaseEvaluator_TAG*  BaseEvaluator_ptr;

/*!
  \brief To cast and check instances of class BaseEvaluator

  These macros must be used respectively to cast and to check
  instances of class BaseEvaluator
*/
#define BASE_EVALUATOR(self) \
         ((BaseEvaluator_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BASE_EVALUATOR_CHECK_INSTANCE(self) \
         (nusmv_assert(BASE_EVALUATOR(self) != BASE_EVALUATOR(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BaseEvaluator
  \brief The BaseEvaluator class constructor

  The BaseEvaluator class constructor

  \sa BaseEvaluator_destroy
*/
BaseEvaluator_ptr BaseEvaluator_create(void);

/*!
  \methodof BaseEvaluator
  \brief The BaseEvaluator class destructor

  The BaseEvaluator class destructor

  \sa BaseEvaluator_create
*/
void BaseEvaluator_destroy(BaseEvaluator_ptr self);

/*!
  \methodof BaseEvaluator
  \brief Initializes the evaluator with context information

  Initializes the evaluator with context
  information. This function must be called *before* invoking
  BaseEvaluator_evaluate in order to initialize the context of evaluation

  \se The internal cache of the evaluator is cleared

  \sa BaseEvaluator_evaluate
*/
void BaseEvaluator_set_context(BaseEvaluator_ptr self,
                                      const SymbTable_ptr st,
                                      const hash_ptr env);

/*!
  \methodof BaseEvaluator
  \brief Evaluates given constant expression

  Evaluates a constant expression within context given
  using BaseEvaluator_set_context. Returns a constant which is the
  result of the evaluation of the expression. A FAILURE node is
  returned if result could not be computed (e.g. no assignment for an
  identifier could be found in the environment)

  \sa BaseEvaluator_set_context
*/
Expr_ptr BaseEvaluator_evaluate(BaseEvaluator_ptr self,
                                       Expr_ptr const_expr);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_TRACE_EVAL_BASE_EVALUATOR_H__ */
