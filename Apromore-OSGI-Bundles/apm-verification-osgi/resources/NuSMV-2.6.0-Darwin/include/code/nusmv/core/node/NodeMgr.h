/* ---------------------------------------------------------------------------


  This file is part of the ``node'' package of NuSMV version 2.
  Copyright (C) 2011 by FBK-irst.

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
  \author Alessandro Mariotti
  \brief Public interface of class 'NodeMgr'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_NODE_NODE_MGR_H__
#define __NUSMV_CORE_NODE_NODE_MGR_H__

#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Nil ((node_ptr)0)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FAILURE_NODE ((node_ptr)(-1))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLOSED_NODE (node_ptr)(-2)

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/
typedef union value_ node_val;

/*!
  \struct node
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct node node_rec;
typedef struct node * node_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef node_ptr (*NPFN)(node_ptr);
typedef node_ptr (*NPFNN)(node_ptr, node_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void  (*VPFN)(node_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef boolean (*BPFN)(node_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef int (*custom_print_sexp_t)(FILE *, node_ptr);
typedef int (*out_func_t)(void*, char*);
typedef int (*custom_print_node_t)(out_func_t, FILE *, node_ptr,
                                   int *, char **, int *, int *, int *);

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

/*!
  \brief Possible value that a node can assume.

  The value of a node is generic. It may be an integer, a
  pointer to a node, a pointer to a string_ structure or a pointer to
  an ADD or BDD. This in order to have a behavior lisp like, in which
  a variable may be a n integer, an atom or a list.

  \sa string_
*/

union value_ {
  int inttype;
  struct node *nodetype;
  struct string_ * strtype;
  void * bddtype;
};


/*!
  \brief The <tt>node</tt> data structure.

  This data structure allows the implementation of a lisp
  like s-expression.
  <ul>
  <li><b>lineno</b> It is used to store the line number of the input
      file to which the stored data refers to.
  <li><b>type</b> It indicates the kind of node we are dealing
      with. I.e. if the node is <em>CONS</em> like, or a leaf, and in
      this case which kind of leaf (<em>NUMBER</em>, <em>ATOM</em>, ...).
  <li><b>left</b> It's the left branch of the s-expression.
  <li><b>right</b> It's the left branch of the s-expression.
  <li><b>link</b> It's a pointer used in the internal hash.
  
*/

struct node {
  struct node *link;
  /*char locked; */ /* this field should be a bit into the others
                       (e.g. in type) */
  short int type;
  int lineno;
  node_val left;
  node_val right;
  void* extra_data; /* added to handle rbc */
};

/*!
  \struct NodeMgr
  \brief Definition of the public accessor for class NodeMgr

  
*/
typedef struct NodeMgr_TAG*  NodeMgr_ptr;


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief To cast and check instances of class NodeMgr

  These macros must be used respectively to cast and to check
  instances of class NodeMgr
*/
#define NODE_MGR(self) \
         ((NodeMgr_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_MGR_CHECK_INSTANCE(self) \
         (nusmv_assert(NODE_MGR(self) != NODE_MGR(NULL)))


/* shorthands to avoid columns explosions in code when building
   expressions. See relative NodeMgr methods */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define find_node(mgr, t, l, r)                 \
  NodeMgr_find_node(mgr, t, l, r)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define new_node(mgr, t, l, r)                  \
  NodeMgr_new_node(mgr, t, l, r)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define new_lined_node(mgr, t, l, r, lineno)            \
  NodeMgr_new_lined_node(mgr, t, l, r, lineno)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define free_node(mgr, n)                       \
  NodeMgr_free_node(mgr, n)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define find_atom(mgr, n)                       \
  NodeMgr_find_atom(mgr, n)

/* same as old "cons", but with the node manager */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define cons(mgr, x, y)                         \
  NodeMgr_cons(mgr, x, y)

/*!
  \brief required

  optional

  \se required

  \sa optional
*/

/* this is not enough, we need the normalizer */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODEMGR_ASSERT_IS_NODE_NORMALIZED(nodemgr, node) \
  (nusmv_assert(node == find_atom(nodemgr, node)))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof NodeMgr
  \brief The NodeMgr class constructor

  The NodeMgr class constructor.
                      The given environment must contain a valid
                      instance of ErrorMgr registered with the
                      ENV_ERROR_MANAGER key

  \sa NodeMgr_destroy
*/
NodeMgr_ptr NodeMgr_create(const NuSMVEnv_ptr env);

/*!
  \methodof NodeMgr
  \brief The NodeMgr class destructor

  The NodeMgr class destructor

  \sa NodeMgr_create
*/
void NodeMgr_destroy(NodeMgr_ptr self);

/*!
  \methodof NodeMgr
  \brief Prints a summary of <tt>node</tt> resources usage

  For debug and profiling purposes only

  \se none
*/
void NodeMgr_show_profile_stats(NodeMgr_ptr self,
                                           FILE* stream);

/* Fresh nodes */

/*!
  \methodof NodeMgr
  \brief Creates a new node.

  A new <tt>node</tt> of type <tt>type</tt> and
  left and right branch <tt>left<tt> and <tt>right</tt> respectively
  is created. The returned node is not stored in the <tt>node</tt> hash.

  \se None

  \sa find_node
*/
node_ptr NodeMgr_new_node(NodeMgr_ptr self, int type,
                                     node_ptr left, node_ptr right);

/*!
  \methodof NodeMgr
  \brief Creates a new node.

  The same as new_node except the line number
  is explicitly proved. A new <tt>node</tt> of type <tt>type</tt>, with
  left and right branch <tt>left<tt> and <tt>right</tt> respectively
  and on the line number <tt>lineno</tt> is created.
  The returned node is not stored in the <tt>node</tt> hash.

  \se None

  \sa new_node, find_node
*/
node_ptr NodeMgr_new_lined_node(NodeMgr_ptr self,
                                           int type,
                                           node_ptr left,
                                           node_ptr right,
                                           int lineno);

/* Hashed nodes */

/*!
  \methodof NodeMgr
  \brief Free a node of the <tt>node<tt> manager.

  Free a node of the <tt>node<tt> manager. The
  node is available for next node allocation.

  \se None
*/
void NodeMgr_free_node(NodeMgr_ptr self, node_ptr node);

/*!
  \methodof NodeMgr
  \brief Creates a new node.

  A new <tt>node</tt> of type <tt>type</tt> and
  left and right branch <tt>left<tt> and <tt>right</tt> respectively
  is created. The returned node is stored in the <tt>node</tt> hash.

  \se The <tt>node</tt> hash is modified.

  \sa new_node
*/
node_ptr NodeMgr_find_node(NodeMgr_ptr self, int type,
                                     node_ptr x, node_ptr y);

/*!
  \methodof NodeMgr
  \brief Search the <tt>node</tt> hash for a given node.

  Search the <tt>node</tt> hash for a given
  node. If the node is not <tt>Nil</tt>, and the node is not stored in
  the hash, the new node is created, stored in the hash and then returned.

  \se The node <tt>hash</tt> may change.

  \sa find_node
*/
node_ptr NodeMgr_find_atom(NodeMgr_ptr self,
                                  node_ptr node);

/*!
  \methodof NodeMgr
  \brief Conses two nodes.

  Conses two nodes.
  [AMa] I think that cons nodes must be removed from the system, replaced with
  ad-hoc lists or Pair/Triple

  \se None

  \sa car cdr
*/
node_ptr NodeMgr_cons(NodeMgr_ptr self, node_ptr x, node_ptr y);

/* ONLY FOR DEBUGGING. DO NOT USE! */

/*!
  \methodof NodeMgr
  \brief \todo Missing synopsis

  \todo Missing description
*/
void NodeMgr_self_check(NodeMgr_ptr self, boolean check_repeated);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_NODE_NODE_MGR_H__ */
