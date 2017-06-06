/* ---------------------------------------------------------------------------


  This file is part of the ``prop'' package of NuSMV version 2.
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
  \author Michele Dorigatti
  \brief Public interface of class 'Prop_Rewriter'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_PROP_PROP_REWRITER_H__
#define __NUSMV_CORE_PROP_PROP_REWRITER_H__


#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/defs.h"
#include "nusmv/core/prop/Prop.h"
#include "nusmv/core/wff/wffRewrite.h"
#include "nusmv/core/fsm/fsm.h"

/*!
  \struct Prop_Rewriter
  \brief Definition of the public accessor for class Prop_Rewriter


*/
typedef struct Prop_Rewriter_TAG*  Prop_Rewriter_ptr;

/*!
  \brief To cast and check instances of class Prop_Rewriter

  These macros must be used respectively to cast and to check
  instances of class Prop_Rewriter
*/
#define PROP_REWRITER(self) \
         ((Prop_Rewriter_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_REWRITER_CHECK_INSTANCE(self) \
         (nusmv_assert(PROP_REWRITER(self) != PROP_REWRITER(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof Prop_Rewriter
  \brief The Prop_Rewriter class constructor

  All the parameters are just referenced.
  @param fsm_type can be a bitwise disjuction (example:
  FSM_TYPE_BE | FSM_TYPE_BDD)

  \sa Prop_Rewriter_destroy
*/
Prop_Rewriter_ptr Prop_Rewriter_create(NuSMVEnv_ptr env,
                                       Prop_ptr prop,
                                       WffRewriteMethod method,
                                       WffRewriterExpectedProperty expprop,
                                       FsmType fsm_type,
                                       BddEnc_ptr bddenc);

/*!
  \methodof Prop_Rewriter
  \brief The Prop_Rewriter class destructor

  The Prop_Rewriter class destructor

  \sa Prop_Rewriter_create
*/
void Prop_Rewriter_destroy(Prop_Rewriter_ptr self);


/* Getters and Setters ********************************************************/

/*!
  \methodof Prop_Rewriter
  \brief Getter for original property

  Getter for original property
*/
Prop_ptr Prop_Rewriter_get_original_property(Prop_Rewriter_ptr self);


/* Miscellaneous **************************************************************/

/*!
  \methodof Prop_Rewriter
  \brief Rewrites self->original and stores the result in self->rewritten

  This is the most important method of the class. Here the
  property is rewritten.
  The returned property must be freed by the caller.
*/
Prop_ptr Prop_Rewriter_rewrite(Prop_Rewriter_ptr self);

/*!
  \methodof Prop_Rewriter
  \brief Copy the results in self->rewritten to self->original

  Copy the results in self->rewritten to self->original
*/
void Prop_Rewriter_update_original_property(Prop_Rewriter_ptr self);

/*!
  \methodof Prop_Rewriter
  \brief Makes the monitor variables visible in traces.

  Makes the monitor variables visible in traces. Must be set
  right after initialization of the rewriter.
*/
void Prop_Rewriter_make_monitor_vars_visible(Prop_Rewriter_ptr self);

/*!
  \methodof Prop_Rewriter
  \brief Makes the monitor variables invisible in traces.

  Makes the monitor variables invisible in
  traces. By default they are invisible. Must be set right after
  initialization of the rewriter.
*/
void Prop_Rewriter_make_monitor_vars_invisible(Prop_Rewriter_ptr self);

/*!
  \methodof Prop_Rewriter
  \brief Makes the monitor variables initialized to True.

  When the WFF_REWRITE_METHOD_DEADLOCK_FREE is selected, then
  the monitor variable if created is initialized to be TRUE (default).
*/
void Prop_Rewriter_initialize_monitor_vars_to_true(Prop_Rewriter_ptr self);

/*!
  \methodof Prop_Rewriter
  \brief Makes the monitor variables initialized to False.

  When the WFF_REWRITE_METHOD_DEADLOCK_FREE is selected, then
  the monitor variable if created is initialized to be FALSE.
*/
void Prop_Rewriter_initialize_monitor_vars_to_false(Prop_Rewriter_ptr self);

/*!
  \methodof Prop_Rewriter
  \brief Negates the property before building the monitor.
  Only when converting LTL to invar.
*/
void Prop_Rewriter_ltl2invar_negate_property_to_true(Prop_Rewriter_ptr self);

/*!
  \methodof Prop_Rewriter
  \brief Do not negate the property before building the monitor.
  Only when converting LTL to invar.
*/
void Prop_Rewriter_ltl2invar_negate_property_to_false(Prop_Rewriter_ptr self);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_PROP_PROP_REWRITER_H__ */
