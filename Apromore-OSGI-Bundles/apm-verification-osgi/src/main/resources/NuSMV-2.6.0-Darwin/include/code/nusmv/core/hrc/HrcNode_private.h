/* ---------------------------------------------------------------------------


  This file is part of the ``hrc'' package of NuSMV version 2.
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
  \brief Private and protected interface of class 'HrcNode'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_HRC_HRC_NODE_PRIVATE_H__
#define __NUSMV_CORE_HRC_HRC_NODE_PRIVATE_H__


#include "nusmv/core/hrc/HrcNode.h"
#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/EnvObject_private.h"
#include "nusmv/core/utils/Olist.h"
#include "nusmv/core/utils/defs.h"

/*!
  \brief HrcNode class definition derived from
               class EnvObject

  

  \sa Base class EnvObject
*/

typedef struct HrcNode_TAG
{
  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  INHERITS_FROM(EnvObject);

  SymbTable_ptr st;           /* The symbol table */
  int lineno;                 /* line number of the module */
  node_ptr name;              /* The name of the module */
  node_ptr instance_name;     /* The instance name */
  HrcNode_ptr parent;         /* The pointer to the parent node */
  Olist_ptr formal_parameters; /* formal parameters */
  Olist_ptr actual_parameters; /* actual parameters */
  Olist_ptr state_variables;   /* state variables */
  Olist_ptr input_variables;   /* input variables */
  Olist_ptr frozen_variables;  /* frozen variables */
  Olist_ptr state_functions;   /* state functions */
  Olist_ptr frozen_functions;  /* frozen functions */
  Olist_ptr defines;           /* DEFINE x := */
  Olist_ptr array_defines;      /* ARRAY DEFINE x := */
  Olist_ptr init_expr;         /* init expression INIT */
  Olist_ptr init_assign;       /* init assignements init(x) :=.. */
  Olist_ptr invar_expr;        /* init expression INVAR */
  Olist_ptr invar_assign;      /* init assignements x :=.. */
  Olist_ptr next_expr;         /* init expression TRANS */
  Olist_ptr next_assign;       /* init assignements next(x) :=.. */
  Olist_ptr justice;           /* JUSTICE/FAIRNESS */
  Olist_ptr compassion;        /* COMPASSION */
  Olist_ptr constants;         /* CONSTANTS */
  Olist_ptr invar_props;       /* INVARSPEC */
  Olist_ptr ctl_props;         /* CTLSPEC */
  Olist_ptr ltl_props;         /* LTLSPEC */
  Olist_ptr psl_props;         /* PSLSPEC */
  Olist_ptr compute_props;     /* COMPUTE */
  Slist_ptr childs;           /* List of sub-childs */
  hash_ptr assigns_table;     /* Assignments hash (left part -> right part) */
  void * undef;               /* For programmers use. Here additional
                                 information can be attached for
                                 several use without having to modify
                                 the structure */
} HrcNode;

/*!
  \brief Free a list and set its pointer to nil

  

  \se List is freed.
*/
#define FREELIST_AND_SET_TO_NIL(list)            \
  Olist_destroy(list);                           \
  list = OLIST(NULL);

/*!
  \brief Free a list and all its elements, settin the list
  pointer to nil.

  Free a list and all its elements, settin the list
  pointer to nil.

  \se Elements in list are freed, list is freed.
*/
#define FREE_LIST_AND_SET_TO_NIL(self, list)                              \
  hrc_node_free_elements_in_list_and_list(                                \
    NODE_MGR(NuSMVEnv_get_value(ENV_OBJECT_GET_ENV(self), ENV_NODE_MGR)), \
    list);                                                                \
  Olist_destroy(list);                                                    \
  list = OLIST(NULL);

/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof HrcNode
  \brief The HrcNode class private initializer

  The HrcNode class private initializer

  \sa HrcNode_create
*/
void hrc_node_init(HrcNode_ptr self, const NuSMVEnv_ptr env);

/*!
  \methodof HrcNode
  \brief The HrcNode class private deinitializer

  The HrcNode class private deinitializer

  \sa HrcNode_destroy
*/
void hrc_node_deinit(HrcNode_ptr self);

#endif /* __NUSMV_CORE_HRC_HRC_NODE_PRIVATE_H__ */
