/* ---------------------------------------------------------------------------


  This file is part of the ``node'' package of NuSMV version 2.
  Copyright (C) 1998-2001 by CMU and FBK-irst.
  Copyright (C) 2011 by FBK.

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
  \author Marco Roveri
  \brief The header file of the <tt>node</tt> package.

  The <tt>node</tt> package implements a data structure
  which offers constructs with e syntax and semantic lisp like.

*/


#ifndef __NUSMV_CORE_NODE_NODE_H__
#define __NUSMV_CORE_NODE_NODE_H__

#include <stdio.h> /* for FILE* */

#include "cudd/util.h" /* [MD] Is it necessary? */
#include "nusmv/core/node/NodeMgr.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief Casts the given pointer to a node_ptr

  
*/
#define NODE_PTR(x)                             \
  ((node_ptr) (x))

/*!
  \brief standard shortcut for iterating over a cons list

  Expected types:
                node_ptr list
                node_ptr iter
*/
#define NODE_CONS_LIST_FOREACH(list, iter)                              \
  for (iter = list;                                                     \
       /* nusmv_assert(Nil == iter || CONS == node_get_type(iter)), */ Nil != iter; \
       iter = cdr(iter))

/*!
  \brief Get the current element of the node conslist iterator

  optional

  \se required

  \sa optional
*/
#define Node_conslist_get(iter) \
  car(iter)

/*!
  \brief Casts the given node_ptr to an int

  
*/
#define NODE_TO_INT(x)                          \
  (PTR_TO_INT(x))

/*!
  \brief Casts the given int to a node_ptr

  
*/

#if NUSMV_SIZEOF_VOID_P == 8 && NUSMV_SIZEOF_INT == 4
#if NUSMV_SIZEOF_LONG == 8

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_FROM_INT(x)                                \
  (PTR_FROM_INT(node_ptr, (0x00000000FFFFFFFFL & ((nusmv_ptrint)x))))
#else
/* Long is 4 bytes, thus we need to use long long (Windows 64bits) */
#define NODE_FROM_INT(x)                                \
  (PTR_FROM_INT(node_ptr, (0x00000000FFFFFFFFLL & ((nusmv_ptrint)x))))
#endif
#else
#define NODE_FROM_INT(x)                        \
  (PTR_FROM_INT(node_ptr, (x)))
#endif

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define caar(_n_) car(car(_n_))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define cadr(_n_) car(cdr(_n_))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define cdar(_n_) cdr(car(_n_))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define cddr(_n_) cdr(cdr(_n_))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_get_type(_n_) (_n_)->type

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_get_lineno(_n_) (_n_)->lineno

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_get_lstring(_n_) (_n_)->left.strtype

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_get_int(_n_) (_n_)->left.inttype

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_bdd_setcar(_n_,_b_) \
{ /*nusmv_assert(!(_n_)->locked);*/ (_n_)->left.bddtype = (_b_); }

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_bdd_setcdr(_n_,_b_) \
{ /*nusmv_assert(!(_n_)->locked);*/ (_n_)->right.bddtype = (_b_); }

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_node_setcar(_n_,_b_)\
{ /*nusmv_assert(!(_n_)->locked);*/ (_n_)->left.nodetype = (_b_); }

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_node_setcdr(_n_,_b_)\
{ /*nusmv_assert(!(_n_)->locked);*/ (_n_)->right.nodetype = (_b_); }

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_int_setcar(_n_,_b_) \
{ /*nusmv_assert(!(_n_)->locked);*/ (_n_)->left.inttype = (_b_); }

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_int_setcdr(_n_,_b_) \
{ /*nusmv_assert(!(_n_)->locked);*/ (_n_)->right.inttype = (_b_); }

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_str_setcar(_n_,_b_) \
{ /*nusmv_assert(!(_n_)->locked);*/ (_n_)->left.strtype = (_b_);  }

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define node_str_setcdr(_n_,_b_) \
{ /*nusmv_assert(!(_n_)->locked);*/ (_n_)->right.strtype = (_b_); }


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Initializes the node package

  Creates master and printers, and initializes the node
  structures

  \sa node_pkg_quit
*/
void node_pkg_init(NuSMVEnv_ptr env);

/*!
  \brief Deinitializes the packages, finalizing all internal
  structures

  

  \sa node_pkg_init
*/
void node_pkg_quit(NuSMVEnv_ptr env);

/*!
  \brief Swaps two nodes.

  Swaps two nodes.

  \se The two nodes are swapped.
*/
void swap_nodes(node_ptr *, node_ptr *);

/*!
  \brief Returns 0 if given node is not a FAILURE node

  
*/
int node_is_failure(node_ptr node);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int node_is_leaf(node_ptr node);

/*!
  \brief Checks if a node is syntactally a symbol

  Checks if a node is syntactally a symbol
*/
boolean Node_is_symbol(node_ptr const node);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean Node_is_relation(node_ptr const expr);

/*!
  \brief True if node is a temporal operator

  No recursion is performed
  Warning, some case missing. What about PSL, for instance?
*/
boolean Node_is_temporal_op(node_ptr const node);

/*!
  \brief Returns the left branch of a node.

  Returns the left branch of a node.

  \se None

  \sa cdr cons
*/
node_ptr car(node_ptr);

/*!
  \brief Returns the right branch of a node.

  Returns the right branch of a node.

  \se None

  \sa car cons
*/
node_ptr cdr(node_ptr);

/*!
  \brief Print an ARRAY_TYPE structure in smv

  

  \sa print_sexp
*/
void print_array_type(const NuSMVEnv_ptr env, FILE* output_stream, const node_ptr body);

/*!
  \brief Replaces the car of X with Y

  Replaces the car of X with Y

  \se The car of X is replaced by Y.

  \sa car cdr cons setcdr
*/
void setcar(node_ptr, node_ptr);

/*!
  \brief Replaces the cdr of X with Y

  Replaces the cdr of X with Y

  \se The cdr of X is replaced by Y.

  \sa car cdr cons setcar
*/
void setcdr(node_ptr, node_ptr);

/*!
  \brief Replaces the type of the node

  Replaces the type of the node

  \se Replaces the type of the node

  \sa car cdr cons setcar node_get_type
*/
void node_set_type(node_ptr, int);

/* Node Types interface *******************************************************/

/*!
  \brief Builds a boolean type node, suitable for HrcNode,
  FlatHierarchy.

  It is of the same shape the parse uses.
*/
node_ptr Node_find_boolean_type(NodeMgr_ptr nodemgr);

/*!
  \brief Builds a integer type node, suitable for HrcNode,
  FlatHierarchy.

  It is of the same shape the parse uses.
*/
node_ptr Node_find_integer_type(NodeMgr_ptr nodemgr);

/*!
  \brief Builds a real type node, suitable for HrcNode,
  FlatHierarchy.

  It is of the same shape the parse uses.
*/
node_ptr Node_find_real_type(NodeMgr_ptr nodemgr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr Node_find_error_type(NodeMgr_ptr nodemgr);



/* Conslist interface */
/* The following functions are deprecated and should never be used in new code
   (they are the interface to the nodelist type) */
/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \brief DEPRECATED
                      Returns a new empty list

  

  \se None
*/
node_ptr new_list(void);

/*!
  \brief DEPRECATED
                      Returns a copy of a list

  An invoker should free the returned list.

  \se free_list
*/
node_ptr copy_list(NodeMgr_ptr nodemgr, node_ptr l);

/*!
  \brief DEPRECATED
                      Frees all the elements of the list.

  Frees all the elements of the list for further use.

  \se None

  \sa car
*/
void     free_list(NodeMgr_ptr nodemgr, node_ptr l);


/* Getters and Setters ********************************************************/

/*!
  \brief DEPRECATED
                      Returns the length of list r.

  Returns the length of list r.

  \se None
*/
unsigned int      llength(node_ptr);


/* Queries  *******************************************************************/

/*!
  \brief DEPRECATED
                      Returns 1 is the list is empty, 0 otherwise

  

  \se None
*/
int      is_list_empty(node_ptr);

/*!
  \brief DEPRECATED
                      Checks list R to see if it contains the element N.

  Checks list R to see if it contains the element N.

  \se None

  \sa node_subtract
*/
int      in_list(node_ptr, node_ptr);

/*!
  \brief Debug function to check if a node_ptr is actually a cons
  list

  Debug function to check if a node_ptr is actually a cons
  list
*/
boolean Node_is_conslist(node_ptr list);


/* Miscellaneous **************************************************************/

/*!
  \brief DEPRECATED
                      Applies FUN to successive cars of LISTs.

  Applies FUN to successive cars of LISTs.

  \se None

  \sa map
*/
void     walk(VPFN fun, node_ptr);

/*!
  \brief Add an element to the list

  This is a functional interface, so to modify "list" the
  snippet is:

  list = Node_conslist_add(nodemgr, list, element); 
*/
node_ptr Node_conslist_add(NodeMgr_ptr nodemgr,
                                  node_ptr list,
                                  node_ptr element);

/*!
  \brief Remove an element from the list

  Linear time
*/
node_ptr Node_conslist_remove(NodeMgr_ptr nodemgr,
                                     node_ptr list,
                                     node_ptr element);

/*!
  \brief DEPRECATED
                      Appends two lists and returns the result.

  Constructs a new list by concatenating its arguments.

  \se The modified list is returned. Side effects on
  the returned list were performed. It is equivalent to the lisp NCONC
*/
node_ptr append(node_ptr, node_ptr);

/*!
  \brief DEPRECATED
                      Appends two lists and returns the result.

  Constructs a new list by concatenating its arguments.

  \se The modified list is returned. No side effects on
  the returned list were performed.
*/
node_ptr append_ns(NodeMgr_ptr nodemgr, node_ptr, node_ptr);

/*!
  \brief DEPRECATED
                      Reverse a list.

  Returns a new sequence containing the same
  elements as X but in reverse order.

  \se The orignial list is modified

  \sa last car cons append
*/
node_ptr reverse(node_ptr);

/*!
  \brief DEPRECATED
                      reverses the list with no side-effect

  Returns a reversed version of the given list.
  The original list is NOT modified

  \se None
*/
node_ptr reverse_ns(NodeMgr_ptr nodemgr, node_ptr);

/*!
  \brief DEPRECATED
                      Returns the last cons in X.

  Returns the last cons in X.

  \se None

  \sa car
*/
node_ptr last(node_ptr);

/*!
  \brief DEPRECATED
                      Applies FUN to successive cars of LISTs and
                      returns the results as a list.

  Applies FUN to successive cars of LISTs and
  returns the results as a list.

  \se None

  \sa map2 walk
*/
node_ptr map(NodeMgr_ptr nodemgr, NPFN fun, node_ptr);

/*!
  \brief DEPRECATED
                      Applies FUN to successive cars of LISTs and
                      returns the results as a list.

  Applies FUN to successive cars of LISTs and
  returns the results as a list. Supports custom parameter

  \se None

  \sa map2 walk
*/
node_ptr map_param(NodeMgr_ptr nodemgr,
                          node_ptr (*fun)(node_ptr, void*),
                          node_ptr l, void*);

/*!
  \brief DEPRECATED
                      Applies FUN to successive cars of LISTs and
                      returns the results as a list. Lists l1 and l2 are
                      traversed in parallel.

  Applies FUN to successive cars of LISTs and
  returns the results as a list. l1 and l2 must have the same length

  \se None

  \sa map walk
*/
node_ptr map2(NodeMgr_ptr nodemgr,
                     NPFNN fun, node_ptr, node_ptr);

/*!
  \brief DEPRECATED
                      Extracts even elements of list L.

  Extracts even elements of list L.

  \se None

  \sa odd_elements
*/
node_ptr even_elements(NodeMgr_ptr nodemgr, node_ptr);

/*!
  \brief DEPRECATED
                      Extracts odd elements of list L.

  Extracts odd elements of list L.

  \se None

  \sa even_elements
*/
node_ptr odd_elements(NodeMgr_ptr nodemgr, node_ptr);

/*!
  \brief DEPRECATED
                      Deletes from list set2 the elements of list set1.

  Deletes elements of list set1 from list set2
  without doing side effect. The resulting list is returned.
  This is quite inefficient: O(|set1| * |set2|) where |<parameter>| denote the
  length of the list

  \se None
*/
node_ptr node_subtract(NodeMgr_ptr nodemgr, node_ptr, node_ptr);

/******************************************************************************/

#endif /* __NUSMV_CORE_NODE_NODE_H__ */
