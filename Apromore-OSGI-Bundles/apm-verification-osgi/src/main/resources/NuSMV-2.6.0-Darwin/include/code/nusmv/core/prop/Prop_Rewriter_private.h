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
  \brief Private and protected interface of class 'Prop_Rewriter'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_PROP_PROP_REWRITER_PRIVATE_H__
#define __NUSMV_CORE_PROP_PROP_REWRITER_PRIVATE_H__

#include "nusmv/core/prop/Prop_Rewriter.h"
#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/EnvObject_private.h"
#include "nusmv/core/utils/defs.h"
#include "nusmv/core/prop/Prop.h"

/*!
  \brief Prop_Rewriter class definition derived from
               class Object



  \sa Base class Object
*/


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef Prop_ptr (*Prop_Rewriter_rewrite_method)(Prop_Rewriter_ptr);

typedef struct Prop_Rewriter_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(EnvObject);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  /* references */
  Prop_ptr original;
  WffRewriteMethod method;
  WffRewriterExpectedProperty expprop;
  SymbTable_ptr symb_table;
  boolean is_status_consistent; /* this forces the developers to call
                                   update_original_property before destroy */
  SymbLayer_ptr layer;
  BddEnc_ptr bddenc;
  FsmType fsm_type;

  /* Whether the monitor variables will be visible in the traces or
     not. By default they are not visible. */
  boolean monitor_visible_in_traces;
  /* When the WFF_REWRITE_METHOD_DEADLOCK_FREE is selected, then
     initialize_monitor_to_true control the value the monitor variable
     is initialized to. I.e. if true it is initialized to TRUE, else
     to FALSE. By default it is initialized to TRUE */
  boolean monitor_variable_initialized_to_true;

  /* When true, the invariant for the property is negated before being
     converted */
  boolean ltl2invar_negate_property;
  /* owned */
  Prop_ptr rewritten;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  Prop_Rewriter_rewrite_method rewrite;

} Prop_Rewriter;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof Prop_Rewriter
  \brief The Prop_Rewriter class private initializer

  Assumption: the property must contain an Fsm

  \sa Prop_Rewriter_create
*/
void prop_rewriter_init(Prop_Rewriter_ptr self,
                        NuSMVEnv_ptr env,
                        Prop_ptr original,
                        WffRewriteMethod method,
                        WffRewriterExpectedProperty expprop,
                        FsmType fsm_type,
                        BddEnc_ptr enc,
                        SymbLayer_ptr layer);

/*!
  \methodof Prop_Rewriter
  \brief The Prop_Rewriter class private deinitializer

  The Prop_Rewriter class private deinitializer

  \sa Prop_Rewriter_destroy
*/
void prop_rewriter_deinit(Prop_Rewriter_ptr self);

/*!
  \methodof Prop_Rewriter
  \brief the internal rewrite function

  The returned property will contain an FSM of the type
  self->fsm_type, plus any other FSM needed for building the target one.
*/
Prop_ptr prop_rewriter_rewrite(Prop_Rewriter_ptr self);




#endif /* __NUSMV_CORE_PROP_PROP_REWRITER_PRIVATE_H__ */
