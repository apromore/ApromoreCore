/* ---------------------------------------------------------------------------


  This file is part of the ``rbc'' package of NuSMV version 2.
  Copyright (C) 2007 by FBK-irst.

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
  \brief Public interface of class 'InlineResult'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_RBC_INLINE_RESULT_H__
#define __NUSMV_CORE_RBC_INLINE_RESULT_H__

#include "nusmv/core/rbc/rbc.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct InlineResult
  \brief Definition of the public accessor for class InlineResult


*/
typedef struct InlineResult_TAG*  InlineResult_ptr;

/*!
  \brief To cast and check instances of class InlineResult

  These macros must be used respectively to cast and to check
  instances of class InlineResult
*/
#define INLINE_RESULT(self) \
         ((InlineResult_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define INLINE_RESULT_CHECK_INSTANCE(self) \
         (nusmv_assert(INLINE_RESULT(self) != INLINE_RESULT(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof InlineResult
  \brief The InlineResult class constructor

  The InlineResult class constructor

  \sa InlineResult_destroy
*/
InlineResult_ptr
InlineResult_create(Rbc_Manager_t* mgr, Rbc_t* f);

/*!
  \methodof InlineResult
  \brief The InlineResult class destructor

  The InlineResult class destructor

  \sa InlineResult_create
*/
void InlineResult_destroy(InlineResult_ptr self);

/*!
  \methodof InlineResult
  \brief The InlineResult class copy constructor

  The InlineResult class copy constructor

  \sa InlineResult_destroy
*/
InlineResult_ptr
InlineResult_copy(const InlineResult_ptr self);

/*!
  Use when you need to store self in a cache.

  This sets internal counter to count the number of instances of self,
  to avoid wrong destruction.  Use only when you know what you are
  doing, for example if self must be returned by a function whose
  caller takes the owner, but self gets also stored within a cache by
  the callee function.

  If used improperly, the instance will not be freed propertly,
  resulting in a leak.

  Returns self, so it can be used in expressions (e.g. in return).
*/
InlineResult_ptr InlineResult_ref(InlineResult_ptr self);

/*!
  \methodof InlineResult
  \brief Returns the original formula f that was submitted to
                      the constructor
*/
Rbc_t*
InlineResult_get_original_f(InlineResult_ptr self);

/*!
  \methodof InlineResult
  \brief Returns the inlined formula, _without_ the conjuction
  set

  A lazy approach to SAT can exploit the fact that if
  a model satisfies inlined f, then it satisfies f as well. If a
  counterexample must be shown though, inlined f must be conjuct
  with the conjuction set.
*/
Rbc_t*
InlineResult_get_inlined_f(InlineResult_ptr self);

/*!
  \methodof InlineResult
  \brief Returns a formula representing the conjuction set

  The conjuction set is made into a formula like:

        foreach (v,exp) belonging to the conjuction set,

        (\/ v <-> exp) \/ inlined_f

  \sa ConjSet
*/
Rbc_t*
InlineResult_get_c(InlineResult_ptr self);

/*!
  \methodof InlineResult
  \brief Returns the inlined f conjucted with the conjuction set


*/
Rbc_t*
InlineResult_get_inlined_f_and_c(InlineResult_ptr self);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_RBC_INLINE_RESULT_H__ */
