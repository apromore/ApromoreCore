
/* ---------------------------------------------------------------------------


  This file is part of the ``fsm.sexp'' package of NuSMV version 2.
  Copyright (C) 2009 by FBK-irst.

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
  \brief Private and protected interface of class 'SexpFsm'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_FSM_SEXP_SEXP_FSM_PRIVATE_H__
#define __NUSMV_CORE_FSM_SEXP_SEXP_FSM_PRIVATE_H__


#include "nusmv/core/fsm/sexp/SexpFsm.h"

#include "nusmv/core/compile/compile.h"
#include "nusmv/core/compile/FlatHierarchy.h"
#include "nusmv/core/set/set.h"

#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/utils/object_private.h"
#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h"


/*!
  \brief SexpFsm class definition derived from
               class Object

  

  \sa Base class Object
*/

typedef struct SexpFsm_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(Object);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  SymbTable_ptr st; /* the symbol table */
  FlatHierarchy_ptr hierarchy; /* contains fsm data */
  Set_t vars_set;
  NodeList_ptr symbols;

  hash_ptr hash_var_fsm;
  node_ptr const_var_fsm;

  int* family_counter; /* for reference counting */

  /* flag controlling inlining operations */
  boolean inlining;

  /* flag to recognize boolean fsm from scalar */
  boolean is_boolean;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} SexpFsm;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof SexpFsm
  \brief Initializes the sexp fsm

  hierarchy is copied into an independent FlatHierarchy
  instance. If the new sexp must be based only on a set of variables, the
  hierarchy must be empty
*/
void sexp_fsm_init(SexpFsm_ptr self,
                          const FlatHierarchy_ptr hierarchy,
                          const Set_t vars_set);

/*!
  \methodof SexpFsm
  \brief DeInitializes the vars fsm hash

  
*/
void sexp_fsm_deinit(SexpFsm_ptr self);

/*!
  \methodof SexpFsm
  \brief private service for copying self to other

  
*/
void sexp_fsm_copy_aux(const SexpFsm_ptr self, SexpFsm_ptr copy);


#endif /* __NUSMV_CORE_FSM_SEXP_SEXP_FSM_PRIVATE_H__ */
