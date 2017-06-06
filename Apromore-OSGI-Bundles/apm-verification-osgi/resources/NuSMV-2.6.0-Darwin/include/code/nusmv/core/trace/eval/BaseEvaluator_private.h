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
  \brief Private and protected interface of class 'BaseEvaluator'

  This file can be included only by derived and friend classes

*/

#ifndef __NUSMV_CORE_TRACE_EVAL_BASE_EVALUATOR_PRIVATE_H__
#define __NUSMV_CORE_TRACE_EVAL_BASE_EVALUATOR_PRIVATE_H__

#include "nusmv/core/trace/eval/BaseEvaluator.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/object_private.h"


/*!
  \brief BaseEvaluator class definition derived from
               class Object

  

  \sa Base class Object
*/

typedef struct BaseEvaluator_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(Object);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  SymbTable_ptr st;
  hash_ptr env;
  hash_ptr cache;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  Expr_ptr (*resolve)(BaseEvaluator_ptr self, const Expr_ptr const_expr);

} BaseEvaluator;

/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof BaseEvaluator
  \brief The BaseEvaluator class private initializer

  The BaseEvaluator class private initializer

  \sa BaseEvaluator_create
*/
void base_evaluator_init(BaseEvaluator_ptr self);

/*!
  \methodof BaseEvaluator
  \brief The BaseEvaluator class private deinitializer

  The BaseEvaluator class private deinitializer

  \sa BaseEvaluator_destroy
*/
void base_evaluator_deinit(BaseEvaluator_ptr self);

/* virtual expression resolution method */

/*!
  \methodof BaseEvaluator
  \brief Const expr virtual resolution method

  This is intended to be overridden in order to support
  extra types
*/
Expr_ptr base_evaluator_resolve(const BaseEvaluator_ptr self,
                                       const Expr_ptr const_expr);

#endif /* __NUSMV_CORE_TRACE_EVAL_BASE_EVALUATOR_PRIVATE_H__ */
