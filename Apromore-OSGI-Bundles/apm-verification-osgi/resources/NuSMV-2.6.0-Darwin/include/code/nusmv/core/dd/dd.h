/* ---------------------------------------------------------------------------


  This file is part of the ``dd'' package of NuSMV version 2. 
  Copyright (C) 1998-2001 by CMU and FBK-irst. 

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
  \brief Header file for Decisison Diagram Package.

  External functions and data strucures of the DD
  package. The BDD or ADD returned as a result of an operation are
  always referenced (see the CUDD User Manual for more details about
  this), and need to be dereferenced when the result is no more
  necessary to computation, in order to release the memory associated
  to it when garbage collection occurs.

*/


#ifndef __NUSMV_CORE_DD_DD_H__
#define __NUSMV_CORE_DD_DD_H__

#include "nusmv/core/dd/DDMgr.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/array.h"
#include "nusmv/core/utils/avl.h"
#include "nusmv/core/node/node.h"
#include "cudd/cudd.h"
#include "nusmv/core/opt/OptsHandler.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct DdNode
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct DdNode * add_ptr;
typedef struct DdNode * bdd_ptr;
typedef struct DdNode * dd_ptr; /* represents both add_ptr and bdd_ptr */

/*!
  \struct MtrNode
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct MtrNode dd_block;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef Cudd_ReorderingType dd_reorderingtype;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*VPFDD)(DDMgr_ptr , bdd_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef node_ptr (*NPFDD)(DDMgr_ptr , bdd_ptr);
typedef void (*VPFCVT)(CUDD_VALUE_TYPE);
typedef void (*VPFDDCVT)(DDMgr_ptr, CUDD_VALUE_TYPE);
typedef node_ptr (*NPFCVT)(CUDD_VALUE_TYPE);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef add_ptr (*FP_A_DA)(DDMgr_ptr , add_ptr);
typedef add_ptr (*FP_A_DAA)(DDMgr_ptr , add_ptr, add_ptr);

typedef node_ptr (*NPFNNE)(node_ptr, node_ptr, const NuSMVEnv_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef DdGen dd_gen;

/*!
  \brief The possible actions to control dynamic var reordering

  
*/

typedef enum DdDynVarOrderAction_TAG {
  DD_DYN_VAR_ORDER_ACTION_DISABLE = 1,
  DD_DYN_VAR_ORDER_ACTION_ENABLE,
  DD_DYN_VAR_ORDER_ACTION_FORCE
} DdDynVarOrderAction;

DdDynVarOrderAction Dd_action_str_to_enum(char* action);
char* Dd_action_enum_to_str(DdDynVarOrderAction action);

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/
#ifndef MAX_VAR_INDEX

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MAX_VAR_INDEX            CUDD_MAXINDEX
#endif

/* initial size of the unique tables */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define UNIQUE_SLOTS             CUDD_UNIQUE_SLOTS

/* initial size of the cache */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CACHE_SLOTS              CUDD_CACHE_SLOTS

/* use value currently stored in the manager. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_SAME             CUDD_REORDER_SAME
/* no reardering at all */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_NONE             CUDD_REORDER_NONE

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_RANDOM           CUDD_REORDER_RANDOM

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_RANDOM_PIVOT     CUDD_REORDER_RANDOM_PIVOT

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_SIFT             CUDD_REORDER_SIFT

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_SIFT_CONV        CUDD_REORDER_SIFT_CONVERGE

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_SYMM_SIFT        CUDD_REORDER_SYMM_SIFT

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_SYMM_SIFT_CONV   CUDD_REORDER_SYMM_SIFT_CONV

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_WINDOW2          CUDD_REORDER_WINDOW2

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_WINDOW3          CUDD_REORDER_WINDOW3

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_WINDOW4          CUDD_REORDER_WINDOW4

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_WINDOW2_CONV     CUDD_REORDER_WINDOW2_CONV

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_WINDOW3_CONV     CUDD_REORDER_WINDOW3_CONV

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_WINDOW4_CONV     CUDD_REORDER_WINDOW4_CONV

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_GROUP_SIFT       CUDD_REORDER_GROUP_SIFT

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_GROUP_SIFT_CONV  CUDD_REORDER_GROUP_SIFT_CONV

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_ANNEALING        CUDD_REORDER_ANNEALING

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_GENETIC          CUDD_REORDER_GENETIC

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_LINEAR           CUDD_REORDER_LINEAR

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_LINEAR_CONV      CUDD_REORDER_LINEAR_CONVERGE

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_EXACT            CUDD_REORDER_EXACT

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_REORDER          REORDER_SIFT /* The default value in the CUDD package */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_MINSIZE          10 /* 10 = whatever (Verbatim from file cuddTable.c) */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ADD_FOREACH_NODE(manager, f, gen, node) \
  Cudd_ForeachNode(DDMgr_get_dd_manager(manager), f, gen, node)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_FOREACH_NODE(manager, f, gen, node) \
  ADD_FOREACH_NODE(manager, f, gen, node)

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/* Miscellaneous **************************************************************/

/*!
  \brief Initializes the DD structures within the
                      given environment

  Initializes the DD structures within the
                      given environment
*/
void Dd_init(NuSMVEnv_ptr env);

/*!
  \brief Deinitializes the DD structures within the
                      given environment

  Deinitializes the DD structures within the
                      given environment
*/
void Dd_quit(NuSMVEnv_ptr env);

/*!
  \brief Enable, disable or force to happen the dynamic var
  reordering

  
*/
int Dd_dynamic_var_ordering(NuSMVEnv_ptr env,
                                   DDMgr_ptr dd,
                                   int dynOrderingMethod,
                                   DdDynVarOrderAction action);

/*!
  \brief Prints the user flags of the cudd package so the user can
  set them

  The user can control the cudd package by setting the
  flags printed by this function
*/
int Dd_set_bdd_parameters(NuSMVEnv_ptr env,
                                 DDMgr_ptr dd_manager,
                                 boolean showAfter);

/*!
  \brief Applies function <code>f</code> to the list of BDD/ADD <code>l</code>.

  This function acts like the Lisp <tt>mapcar</tt>. It returns
  the list of the result of the application of function \code>f</code> to each
  element of list <code>l</code>.

  \sa map walk walk_dd
*/
node_ptr map_dd(DDMgr_ptr , NPFDD, node_ptr);

/*!
  \brief Applies function <code>f</code> to the list of BDD/ADD <code>l</code>.

  This function acts like the <tt>map_dd</dd>. This functions
  applies the function <code>f</code> to each element of list
  <code>l</code>. Nothing is returned, performs side-effects on the elements.

  \sa map walk map_dd
*/
void     walk_dd(DDMgr_ptr , VPFDD, node_ptr);

/*!
  \brief Builds a group of variables that should stay adjacent
  during reordering.

  Builds a group of variables that should stay adjacent
  during reordering. The group is made up of n variables. The first
  variable in the group is f. The other variables are the n-1
  variables following f in the order at the time of invocation of this
  function. Returns a handle to the variable group if successful else fail.

  \se Modifies the variable tree.
*/
dd_block* dd_new_var_block(DDMgr_ptr , int, int);

/*!
  \brief Dissolves a group previously created by dd_new_var_block

  Dissolves a group previously created by
  dd_new_var_block.  Returns 0 if the group was actually removed, 1
  otherwise (that may be not due to an error)

  \se Modifies the variable tree.
*/
int      dd_free_var_block(DDMgr_ptr , dd_block*);

/*!
  \brief Returns the index of the variable currently in the i-th
  position of the order.

  Returns the index of the variable currently in the i-th
  position of the order. If the index is MAX_VAR_INDEX, returns
  MAX_VAR_INDEX; otherwise, if the index is out of bounds fails.
*/
int      dd_get_index_at_level(DDMgr_ptr , int);

/*!
  \brief Returns the current position of the i-th variable in the
  order.

  Returns the current position of the i-th variable in the
  order. If the index is CUDD_MAXINDEX, returns CUDD_MAXINDEX; otherwise,
  if the index is out of bounds returns -1.

  \se None

  \sa Cudd_ReadInvPerm Cudd_ReadPermZdd
*/
int      dd_get_level_at_index(DDMgr_ptr , int);

/*!
  \brief Returns the number of BDD variables in existance.

  Returns the number of BDD variables in existance.
*/
int      dd_get_size(DDMgr_ptr );

/*!
  \brief Reorders variables according to given permutation.

  Reorders variables according to given permutation.
  The i-th entry of the permutation array contains the index of the variable
  that should be brought to the i-th level.  The size of the array should be
  equal or greater to the number of variables currently in use.
  Returns 1 in case of success; 0 otherwise.

  \se Changes the variable order for all diagrams and clears
  the cache.
*/
int      dd_set_order(DDMgr_ptr , int *permutation);

/*!
  \brief Enables automatic dynamic reordering of BDDs and ADDs.

  Enables automatic dynamic reordering of BDDs and
  ADDs. Parameter method is used to determine the method used for
  reordering. If REORDER_SAME is passed, the method is
  unchanged.

  \sa dd_autodyn_disable dd_reordering_status
*/
void     dd_autodyn_enable(DDMgr_ptr , dd_reorderingtype);

/*!
  \brief Disables automatic dynamic reordering of BDD and ADD.

  Disables automatic dynamic reordering of BDD and ADD.

  \sa dd_autodyn_enable dd_reordering_status
*/
void     dd_autodyn_disable(DDMgr_ptr );

/*!
  \brief Reports the status of automatic dynamic reordering of BDDs
  and ADDs.

  Reports the status of automatic dynamic reordering of
  BDDs and ADDs. Parameter method is set to the reordering method
  currently selected. Returns 1 if automatic reordering is enabled; 0
  otherwise.

  \se Parameter method is set to the reordering method currently
  selected.

  \sa dd_autodyn_disable dd_autodyn_enable
*/
int      dd_reordering_status(DDMgr_ptr , dd_reorderingtype *);

/*!
  \brief Main dynamic reordering routine.

  Main dynamic reordering routine.
  Calls one of the possible reordering procedures:
  <ul>
  <li>Swapping
  <li>Sifting
  <li>Symmetric Sifting
  <li>Group Sifting
  <li>Window Permutation
  <li>Simulated Annealing
  <li>Genetic Algorithm
  <li>Dynamic Programming (exact)
  </ul>

  For sifting, symmetric sifting, group sifting, and window
  permutation it is possible to request reordering to convergence.<p>

  Returns 1 in case of success; 0 otherwise. In the case of symmetric
  sifting (with and without convergence) returns 1 plus the number of
  symmetric variables, in case of success.<p>

  This functions takes as arguments:
  <ul>
  <li> <tt>dd</tt> the DD manager;
  <li> <tt>heuristics</tt> method used for reordering;
  <li> <tt>minsize</tt> bound below which no reordering occurs;
  </ul>
  

  \se Changes the variable order for all diagrams and clears
  the cache.

  \sa Cudd_ReduceHeap
*/
int      dd_reorder(DDMgr_ptr , int, int);

/*!
  \brief Returns the number of times reordering has occurred.

  Returns the number of times reordering has occurred in
  the manager. The number includes both the calls to Cudd_ReduceHeap
  from the application program and those automatically performed by
  the package. However, calls that do not even initiate reordering are
  not counted. A call may not initiate reordering if there are fewer
  than minsize live nodes in the manager, or if CUDD_REORDER_NONE is
  specified as reordering method. The calls to Cudd_ShuffleHeap are
  not counted.
*/
int      dd_get_reorderings(DDMgr_ptr );

/*!
  \brief Gets the internal reordering method used.

  Returns the internal reordering method used.
*/
dd_reorderingtype dd_get_ordering_method(DDMgr_ptr );

/*!
  \brief Converts a string to a dynamic ordering method type.

  Converts a string to a dynamic ordering method type. If string
  is not "sift" or "window", then returns REORDER_.
*/
int      StringConvertToDynOrderType(char *string);

/*!
  \brief Converts a dynamic ordering method type to a string.

  Converts a dynamic ordering method type to a string.  This
  string must NOT be freed by the caller.
*/
char *   DynOrderTypeConvertToString(int method);

/*!
  \brief Checks the unique table for nodes with non-zero reference
  counts.

  Checks the unique table for nodes with non-zero
  reference counts. It is normally called before dd_quit to make sure
  that there are no memory leaks due to missing add/bdd_free's.
  Takes into account that reference counts may saturate and that the
  basic constants and the projection functions are referenced by the
  manager.  Returns the number of nodes with non-zero reference count.
  (Except for the cases mentioned above.)
*/
int      dd_checkzeroref(DDMgr_ptr );

/*!
  \brief Sets the internal parameters of the package to the given values.

  The CUDD package has a set of parameters that can be assigned
  different values. This function receives a table which maps strings to
  values and sets the parameters represented by the strings to the pertinent
  values. Some basic type checking is done. It returns 1 if everything is
  correct and 0 otherwise.
*/
int      dd_set_parameters(DDMgr_ptr , OptsHandler_ptr, FILE *);

/*!
  \brief Prints out statistic and setting of the DD manager.

  Prints out statistics and settings for a CUDD manager.
*/
int      dd_print_stats(NuSMVEnv_ptr, DDMgr_ptr , FILE *);

/*!
  \brief Prints a disjoint sum of products.

  Prints a disjoint sum of product cover for the function
  rooted at node. Each product corresponds to a path from node a leaf
  node different from the logical zero, and different from the
  background value. Uses the standard output.  Returns 1 if successful;
  0 otherwise.
*/
int      dd_printminterm(DDMgr_ptr , dd_ptr);

/*!
  \brief Writes a dot file representing the argument DDs.

  Writes a file representing the argument DDs in a format
  suitable for the graph drawing program dot.

  It returns 1 in case of success; 0 otherwise (e.g., out-of-memory,
  file system full).

  Cudd_DumpDot does not close the file: This is the caller
  responsibility. Cudd_DumpDot uses a minimal unique subset of the
  hexadecimal address of a node as name for it.

  If the argument inames is non-null, it is assumed to hold the pointers
  to the names of the inputs. Similarly for onames.
  Cudd_DumpDot uses the following convention to draw arcs:
    <ul>
    <li> solid line: THEN arcs;
    <li> dotted line: complement arcs;
    <li> dashed line: regular ELSE arcs.
    </ul>

  The dot options are chosen so that the drawing fits on a letter-size
  sheet.
  

  \sa dd_dump_davinci
*/
int      dd_dump_dot(DDMgr_ptr , int, dd_ptr *, const char **, const char **, FILE *);

/*!
  \brief Writes a daVnci file representing the argument DDs.

  Writes a daVnci file representing the argument
  DDs. For a better description see the \"Cudd_DumpDaVinci\" documentation
  in the CUDD package.

  \sa dd_dump_davinci
*/
int      dd_dump_davinci(DDMgr_ptr , int, dd_ptr *, const char **, const char **, FILE *);

/*!
  \brief Converts an ADD to a BDD.

  Converts an ADD to a BDD. Only TRUE and FALSE leaves
  are admitted. Returns a pointer to the resulting BDD if successful;
  NULL otherwise.

  \sa bdd_to_add bdd_to_01_add
*/
bdd_ptr  add_to_bdd(DDMgr_ptr , add_ptr);

/*!
  \brief Converts an ADD to a BDD according to a strict threshold

  Converts an ADD to a BDD by replacing all discriminants
  greater than value k with TRUE, and all other discriminants with
  FALSE. Returns a pointer to the resulting BDD if successful; a
  failure is generated otherwise.

  \sa add_to_bdd_threshold add_to_bdd bdd_to_01_add
*/
bdd_ptr  add_to_bdd_strict_threshold(DDMgr_ptr , add_ptr, int);

/*!
  \brief Converts a BDD to a FALSE-TRUE ADD.

  Converts a BDD to a FALSE-TRUE ADD. Returns a pointer to the
  resulting ADD if successful; a failure is generated otherwise.

  \sa add_to_bdd bdd_to_01_add
*/
add_ptr  bdd_to_add(DDMgr_ptr , bdd_ptr);

/*!
  \brief Converts a BDD to a 0-1 ADD.

  Converts a BDD to a 0-1 ADD. Returns a pointer to the
  resulting ADD if successful; a failure is generated otherwise.

  \sa bdd_to_add
*/
add_ptr  bdd_to_01_add(DDMgr_ptr , bdd_ptr);

/*!
  \brief Abstracts away variables from an ADD.

  Abstracts away variables from an ADD, summing up the values
                      of the merged branches.
*/
add_ptr  add_exist_abstract(DDMgr_ptr dd, add_ptr a, bdd_ptr b);


/* ADD Interface **************************************************************/

/*!
  \brief Reads the constant TRUE ADD of the manager.

  Reads the constant TRUE ADD of the manager.

  \sa add_false
*/
add_ptr  add_true(DDMgr_ptr );

/*!
  \brief Returns the then child of an internal node.

  Returns the then child of an internal node. If
  <code>f</code> is a constant node, the result is
  unpredictable. Notice that the reference count of the returned node
  is not incremented.

  \se none

  \sa add_else
*/
add_ptr  add_then(DDMgr_ptr , add_ptr);

/*!
  \brief Returns the else child of an internal node.

  Returns the else child of an internal node. If
  <code>f</code> is a constant node, the result is
  unpredictable. Notice that the reference count of the returned node
  is not incremented.

  \se none

  \sa add_else
*/
add_ptr  add_else(DDMgr_ptr , add_ptr);

/*!
  \brief Returns the index of the node.

  Returns the index of the node.

  \se None
*/
int      add_index(DDMgr_ptr , add_ptr);

/*!
  \brief Reads the constant FALSE ADD of the manager.

  Reads the constant FALSE ADD of the manager.

  \sa add_true
*/
add_ptr  add_false(DDMgr_ptr );

/*!
  \brief Check if the ADD is true.

  Check if the ADD is true.

  \sa add_true
*/
int      add_is_true(DDMgr_ptr , add_ptr);

/*!
  \brief Check if the ADD is false.

  Check if the ADD is false.

  \sa add_false
*/
int      add_is_false(DDMgr_ptr , add_ptr);

/*!
  \brief Reads the constant one ADD of the manager.

  Reads the constant one ADD of the manager.

  \sa add_false
*/
add_ptr  add_one(DDMgr_ptr );

/*!
  \brief Reads the constant zero ADD of the manager.

  Reads the constant zero ADD of the manager.

  \sa add_true
*/
add_ptr  add_zero(DDMgr_ptr );

/*!
  \brief Check if the ADD is one.

  Check if the ADD is one.

  \sa add_true
*/
int      add_is_one(DDMgr_ptr , add_ptr);

/*!
  \brief Check if the ADD is zero.

  Check if the ADD is zero.

  \sa add_false
*/
int      add_is_zero(DDMgr_ptr , add_ptr);

/*!
  \brief Reference an ADD node.

  Reference an ADD node.

  \se The reference count of the node is incremented by one.

  \sa add_deref add_free
*/
void     add_ref(add_ptr);

/*!
  \brief Dereference an ADD node.

  Dereference an ADD node.

  \se The reference count of the node is decremented by one.

  \sa add_ref add_free
*/
void     add_deref(add_ptr);

/*!
  \brief Creates a copy of an ADD node.

  Creates a copy of an ADD node.

  \se The reference count is increased by one unit.

  \sa add_ref add_free add_deref
*/
add_ptr  add_dup(add_ptr);

/*!
  \brief Dereference an ADD node. If it dies, recursively decreases
  the reference count of its children.

  Decreases the reference count of node. If the node dies,
  recursively decreases the reference counts of its children. It is used to
  dispose off an ADD that is no longer needed.

  \se The reference count of the node is decremented by one,
  and if the node dies a recursive dereferencing is applied to its children.
*/
void     add_free(DDMgr_ptr , add_ptr);

/*!
  \brief Returns the ADD variable with index <code>index</code>.

  Retrieves the ADD variable with index
  <code>index</code> if it already exists, or creates a new ADD
  variable. Returns a pointer to the variable if successful; a failure
  is generated otherwise.  An ADD variable differs from a BDD variable
  because it points to the arithmetic zero, instead of having a
  complement pointer to 1. The returned value is referenced.

  \sa add_new_var_at_level
*/
add_ptr  add_new_var_with_index(DDMgr_ptr , int);

/*!
  \brief Checks the unique table of the DdManager for the
  existence of an internal node.

  Checks the unique table for the existence of an internal
  node. If it does not exist, it creates a new one. The reference
  count of whatever is returned is increased by one unit. For a newly
  created node, increments the reference counts of what T and E point
  to.  Returns a pointer to the new node if successful; a failure
  occurs if memory is exhausted or if reordering took place.
*/
add_ptr  add_build(DDMgr_ptr , int, add_ptr , add_ptr);

/*!
  \brief Returns a new ADD variable at a specified level.

  Creates a new ADD variable. The new variable has an
  index equal to the largest previous index plus 1 and is positioned at
  the specified level in the order.  Returns a pointer to the new
  variable if successful; a failure is generated otherwise. The
  returned value is referenced.

  \sa add_new_var_with_index
*/
add_ptr  add_new_var_at_level(DDMgr_ptr , int);

/*!
  \brief Returns 1 if the ADD node is a constant node.

  Returns 1 if the ADD node is a constant node (rather than an
  internal node). All constant nodes have the same index (MAX_VAR_INDEX).
*/
int      add_isleaf(add_ptr);

/*!
  \brief Creates an returns an ADD for constant leaf_node.

  Retrieves the ADD for constant leaf_node if it already
  exists, or creates a new ADD.  Returns a pointer to the
  ADD if successful; fails otherwise.

  \se The reference count of the node is incremented by one unit.
*/
add_ptr  add_leaf(DDMgr_ptr , node_ptr);

/*!
  \brief Returns the value of a constant node.

  Returns the value of a constant node. If <code>Leaf</code>
  is an internal node, a failure occurs.
*/
node_ptr add_get_leaf(DDMgr_ptr , add_ptr);

/*!
  \brief Applies AND to the corresponding discriminants of f and g.

  Applies logical AND to the corresponding discriminants
  of f and g. f and g must have only FALSE or TRUE as terminal
  nodes. Returns a pointer to the result if successful; a failure is
  generated otherwise.

  \sa add_or add_xor add_not
*/
add_ptr  add_and(DDMgr_ptr , add_ptr, add_ptr);

/*!
  \brief Applies AND to the corresponding discriminants of f and g.

  Applies logical AND to the corresponding discriminants
  of f and g and stores the result in f. f and g must have only FALSE
  or TRUE as terminal nodes.

  \se The result is stored in the first operand.

  \sa add_and
*/
void     add_and_accumulate(DDMgr_ptr , add_ptr *, add_ptr);

/*!
  \brief Applies OR to the corresponding discriminants of f and g.

  Applies logical OR to the corresponding discriminants
  of f and g. f and g must have only FALSE or TRUE as terminal
  nodes. Returns a pointer to the result if successful; a failure is
  generated otherwise.

  \sa add_and add_xor add_not add_imply
*/
add_ptr  add_or(DDMgr_ptr , add_ptr, add_ptr);

/*!
  \brief Applies OR to the corresponding discriminants of f and g.

  Applies logical OR to the corresponding discriminants
  of f and g and stores the result in f. f and g must have only FALSE
  or TRUE as terminal nodes.

  \se The result is stored in the first operand.

  \sa add_and
*/
void     add_or_accumulate(DDMgr_ptr , add_ptr *, add_ptr);

/*!
  \brief Applies NOT to the corresponding discriminant of f.

  Applies logical NOT to the corresponding discriminant
  of f.  f must have only FALSE or TRUE as terminal nodes. Returns a
  pointer to the result if successful; a failure is generated
  otherwise.

  \sa add_and add_xor add_or add_imply
*/
add_ptr  add_not(DDMgr_ptr , add_ptr);

/*!
  \brief Applies IMPLY to the corresponding discriminants of f and g.

  Applies logical IMPLY to the corresponding
  discriminants of f and g.  f and g must have only FALSE or TRUE as
  terminal nodes. Returns a pointer to the result if successful; a
  failure is generated otherwise.

  \sa add_and add_xor add_or add_not
*/
add_ptr  add_implies(DDMgr_ptr , add_ptr, add_ptr);

/*!
  \brief Applies IFF to the corresponding discriminants of f and g.

  Applies logical IFF to the corresponding discriminants
  of f and g.  f and g must have only FALSE or TRUE as terminal
  nodes. Returns a pointer to the result if successful; a failure is
  generated otherwise.

  \sa add_and add_xor add_or add_not
*/
add_ptr  add_iff(DDMgr_ptr , add_ptr, add_ptr);

/*!
  \brief Applies XOR to the corresponding discriminants of f and g.

  Applies logical XOR to the corresponding discriminants
  of f and g. f and g must have only FALSE or TRUE as terminal nodes. Returns
  a pointer to the result if successful; a failure is generated
  otherwise.

  \sa add_or add_and add_not add_imply
*/
add_ptr  add_xor(DDMgr_ptr , add_ptr, add_ptr);

/*!
  \brief Applies XNOR to the corresponding discriminants of f and g.

  Applies logical XNOR to the corresponding discriminants
  of f and g. f and g must have only FALSE or TRUE as terminal
  nodes. Returns a pointer to the result if successful; a failure is
  generated otherwise.

  \sa add_xor add_or add_and add_not add_imply
*/
add_ptr  add_xnor(DDMgr_ptr , add_ptr, add_ptr);

/*!
  \brief Applies binary op to the corresponding discriminants of f and g.

  Returns a pointer to the result if successful; a failure is
  generated otherwise.
*/
add_ptr  add_apply(DDMgr_ptr , NPFNNE, add_ptr, add_ptr);

/*!
  \brief Applies unary op to the corresponding discriminant of f

  Returns a pointer to the result if successful; a failure is
  generated otherwise.

  NOTE: At the moment CUDD does not have unary 'apply', so you have
  to provide a binary op, which is actually unary and applies to
  the first operand only.
*/
add_ptr  add_monadic_apply(DDMgr_ptr , NPFNNE/*NPFCVT*/, add_ptr);

/*!
  \brief Implements ITE(f,g,h).

  Implements ITE(f,g,h). This procedure assumes that f is
  a FALSE-TRUE ADD.  Returns a pointer to the resulting ADD if
  successful; a failure is generated otherwise.
*/
add_ptr  add_ifthenelse(DDMgr_ptr , add_ptr, add_ptr, add_ptr);

/*!
  \brief Computes the difference between two ADD cubes.

  Computes the difference between two ADD cubes, i.e. the
  cube of ADD variables belonging to cube a and not belonging to cube
  b. Returns a pointer to the resulting cube; a failure is generated
  otherwise.

  \sa bdd_cube_diff
*/
add_ptr  add_cube_diff(DDMgr_ptr , add_ptr , add_ptr);

/*!
  \brief ADD restrict according to Coudert and Madre's algorithm (ICCAD90).

  ADD restrict according to Coudert and Madre's algorithm
  (ICCAD90). Returns the restricted ADD if successful; a failure is
  generated otherwise.
  If application of restrict results in an ADD larger than the input
  ADD, the input ADD is returned.
*/
add_ptr  add_simplify_assuming(DDMgr_ptr , add_ptr, add_ptr);

/*!
  \brief Permutes the variables of an ADD.

  Given a permutation in array permut, creates a new ADD
  with permuted variables. There should be an entry in array permut
  for each variable in the manager. The i-th entry of permut holds the
  index of the variable that is to substitute the i-th variable.
  Returns a pointer to the resulting ADD if successful; a failure is
  generated otherwise. The reuslt is referenced.

  \sa bdd_permute
*/
add_ptr  add_permute(DDMgr_ptr , add_ptr, int *);

/*!
  \brief Finds the variables on which an ADD depends on.

  Finds the variables on which an ADD depends on.
  Returns an ADD consisting of the product of the variables if
  successful; a failure is generated otherwise.

  \sa bdd_support
*/
add_ptr  add_support(DDMgr_ptr , add_ptr);

/*!
  \brief Applies a generic function to constant nodes.

  Applies a generic function <tt>VPFDDCVT op</tt> to the
  constants nodes of <tt>f</tt>.
*/
void     add_walkleaves(DDMgr_ptr , VPFDDCVT, add_ptr);

/*!
  \brief Counts the number of ADD nodes in an ADD.

  Counts the number of ADD nodes in an ADD. Returns the number
  of nodes in the graph rooted at node.

  \sa add_count_minterm
*/
int      add_size(DDMgr_ptr , add_ptr);

/*!
  \brief Counts the number of ADD minterms of an ADD.

  Counts the number of minterms of an ADD. The function is
  assumed to depend on nvars variables. The minterm count is
  represented as a double, to allow for a larger number of variables.
  Returns the number of minterms of the function rooted at node. The
  result is parameterized by the number of \"nvars\" passed as argument.

  \sa bdd_size bdd_count_minterm
*/
double   add_count_minterm(DDMgr_ptr , add_ptr, int);

/*!
  \brief Returns the number of nodes in the unique table.

  Returns the total number of nodes currently in the unique
  table, including the dead nodes.
*/
int      get_dd_nodes_allocated(DDMgr_ptr );

/*!
  \brief Given the result of add_if_then it returns the leaf corresponding.

  Given the result of add_if_then it returns the leaf
  corresponding. The ADD is traversed according to the rules given as
  a result of add_if_then. If it is costant, then the corresponding
  value is returned. The Else branch is recursively traversed, if the
  result of this travesring is an ELSE_CNST, then the result of the
  traversing of the Then branch is returned.

  \sa add_if_then
*/
node_ptr add_value(DDMgr_ptr , add_ptr);

/*!
  \brief Given a minterm, it returns an ADD indicating the rules
  to traverse the ADD.

  Given a minterm, it returns an ADD indicating the rules
  to traverse the ADD.

  \sa add_value
*/
add_ptr  add_if_then(DDMgr_ptr , add_ptr, add_ptr);


/* BDD Interface **************************************************************/

/*!
  \brief Returns 1 if the BDD node is a constant node.

  Returns 1 if the BDD node is a constant node (rather than an
  internal node). All constant nodes have the same index (MAX_VAR_INDEX).
*/
int      bdd_isleaf(bdd_ptr);

/*!
  \brief Reference an BDD node.

  Reference an BDD node.

  \se The reference count of the node is incremented by one.

  \sa bdd_deref bdd_free
*/
void     bdd_ref(bdd_ptr);

/*!
  \brief Dereference an BDD node.

  Dereference an BDD node.

  \se The reference count of the node is decremented by one.

  \sa bdd_ref bdd_free
*/
void     bdd_deref(bdd_ptr);

/*!
  \brief Creates a copy of an BDD node.

  Creates a copy of an BDD node.

  \se The reference count is increased by one unit.

  \sa bdd_ref bdd_free bdd_deref
*/
bdd_ptr  bdd_dup(bdd_ptr);

/*!
  \brief Reads the constant TRUE BDD of the manager.

  Reads the constant TRUE BDD of the manager.

  \sa bdd_false
*/
bdd_ptr  bdd_true(DDMgr_ptr );

/*!
  \brief Reads the constant FALSE BDD of the manager.

  Reads the constant FALSE BDD of the manager.

  \sa bdd_true
*/
bdd_ptr  bdd_false(DDMgr_ptr );

/*!
  \brief Check if the BDD is TRUE.

  Check if the BDD is TRUE.

  \sa bdd_true
*/
int      bdd_is_true(DDMgr_ptr , bdd_ptr);

/*!
  \brief Check if the BDD is false.

  Check if the BDD is false.

  \sa bdd_false
*/
int      bdd_is_false(DDMgr_ptr , bdd_ptr);

/*!
  \brief Check if the BDD is not true.

  Check if the BDD is not true.

  \sa bdd_true
*/
int      bdd_isnot_true(DDMgr_ptr , bdd_ptr);

/*!
  \brief Check if the BDD is not false.

  Check if the BDD is not false.

  \sa bdd_false
*/
int      bdd_isnot_false(DDMgr_ptr , bdd_ptr);

/*!
  \brief Dereference an BDD node. If it dies, recursively decreases
  the reference count of its children.

  Decreases the reference count of node. If the node dies,
  recursively decreases the reference counts of its children. It is used to
  dispose off a BDD that is no longer needed.

  \se The reference count of the node is decremented by one,
  and if the node dies a recursive dereferencing is applied to its children.
*/
void     bdd_free(DDMgr_ptr , bdd_ptr);

/*!
  \brief Applies NOT to the corresponding discriminant of f.

  Applies logical NOT to the corresponding discriminant of f.
  f must be a BDD. Returns a pointer to the result if successful; a
  failure is generated otherwise.

  \sa bdd_and bdd_xor bdd_or bdd_imply
*/
bdd_ptr  bdd_not(DDMgr_ptr , bdd_ptr);

/*!
  \brief Applies AND to the corresponding discriminants of f and g.

  Applies logical AND to the corresponding discriminants
  of f and g. f and g must be BDDs. Returns a pointer to the result if
  successful; a failure is generated otherwise.

  \sa bdd_or bdd_xor bdd_not
*/
bdd_ptr  bdd_and(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Applies AND to the corresponding discriminants of f and g.

  Applies logical AND to the corresponding discriminants
  of f and g and stores the result in f. f and g must be two BDDs. The
  result is referenced.

  \se The result is stored in the first operand and referenced.

  \sa bdd_and
*/
void     bdd_and_accumulate(DDMgr_ptr , bdd_ptr *, bdd_ptr);

/*!
  \brief Applies OR to the corresponding discriminants of f and g.

  Applies logical OR to the corresponding discriminants
  of f and g. f and g must be BDDs. Returns a pointer to the result if
  successful; a failure is generated otherwise.

  \sa bdd_and bdd_xor bdd_not
*/
bdd_ptr  bdd_or(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Applies OR to the corresponding discriminants of f and g.

  Applies logical OR to the corresponding discriminants
  of f and g and stores the result in f. f and g must be two BDDs. The
  result is referenced.

  \se The result is stored in the first operand and referenced.

  \sa bdd_and
*/
void     bdd_or_accumulate(DDMgr_ptr , bdd_ptr *, bdd_ptr);

/*!
  \brief Applies XOR to the corresponding discriminants of f and g.

  Applies logical XOR to the corresponding discriminants
  of f and g. f and g must be BDDs. Returns a pointer to the result if
  successful; a failure is generated otherwise.

  \sa bdd_or bdd_imply bdd_not
*/
bdd_ptr  bdd_xor(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Applies IFF to the corresponding discriminants of f and g.

  Applies logical IFF to the corresponding discriminants
  of f and g. f and g must be BDDs. Returns a pointer to the result if
  successful; a failure is generated otherwise.

  \sa bdd_or bdd_xor bdd_not
*/
bdd_ptr  bdd_iff(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Applies IMPLY to the corresponding discriminants of f and g.

  Applies logical IMPLY to the corresponding discriminants
  of f and g. f and g must be BDDs. Returns a pointer to the result if
  successful; a failure is generated otherwise.

  \sa bdd_or bdd_xor bdd_not
*/
bdd_ptr  bdd_imply(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Existentially abstracts all the variables in cube from fn.

  Existentially abstracts all the variables in cube from fn.
  Returns the abstracted BDD if successful; a failure is generated
  otherwise.

  \sa bdd_forall
*/
bdd_ptr  bdd_forsome(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Universally abstracts all the variables in cube from f.

  Universally abstracts all the variables in cube from f.
  Returns the abstracted BDD if successful; a failure is generated
  otherwise.

  \sa bdd_forsome
*/
bdd_ptr  bdd_forall(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Permutes the variables of a BDD.

  Given a permutation in array permut, creates a new BDD
  with permuted variables. There should be an entry in array permut
  for each variable in the manager. The i-th entry of permut holds the
  index of the variable that is to substitute the i-th variable.
  Returns a pointer to the resulting BDD if successful; a failure is
  generated otherwise. The result is referenced.

  \sa bdd_permute
*/
bdd_ptr  bdd_permute(DDMgr_ptr , bdd_ptr, int *);

/*!
  \brief Takes the AND of two BDDs and simultaneously abstracts the
  variables in cube.

  Takes the AND of two BDDs and simultaneously abstracts
  the variables in cube. The variables are existentially abstracted.
  Returns a pointer to the result if successful; a failure is
  generated otherwise.

  \sa bdd_and bdd_forsome
*/
bdd_ptr  bdd_and_abstract(DDMgr_ptr , bdd_ptr, bdd_ptr, bdd_ptr);

/*!
  \brief BDD restrict according to Coudert and Madre's algorithm
  (ICCAD90).

  BDD restrict according to Coudert and Madre's algorithm
  (ICCAD90). Returns the restricted BDD if successful; a failure is
  generated otherwise.
  If application of restrict results in an BDD larger than the input
  BDD, the input BDD is returned.
*/
bdd_ptr  bdd_simplify_assuming(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Restrict operator as described in Coudert et al. ICCAD90.

  Restrict operator as described in Coudert et
  al. ICCAD90.  Always returns a BDD not larger than the input
  <code>f</code> if successful; a failure is generated otherwise. The
  result is referenced.

  \sa bdd_simplify_assuming
*/
bdd_ptr  bdd_minimize(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Computes f constrain c.

  Computes f constrain c (f @ c).
  Uses a canonical form: (f' @ c) = ( f @ c)'.  (Note: this is not true
  for c.)  List of special cases:
    <ul>
    <li> F @ 0 = 0
    <li> F @ 1 = F
    <li> 0 @ c = 0
    <li> 1 @ c = 1
    <li> F @ F = 1
    <li> F @ F'= 0
    </ul>
  Returns a pointer to the result if successful; a failure is
  generated otherwise.

  \sa bdd_minimize bdd_simplify_assuming
*/
bdd_ptr  bdd_cofactor(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Return a minimum size BDD between bounds.

  \todo Missing description

  \sa bdd_minimize bdd_simplify_assuming bdd_cofactor
*/
bdd_ptr  bdd_between(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Determines whether f is less than or equal to g.

  Returns 1 if f is less than or equal to g; 0 otherwise.
  No new nodes are created.

  \se None
*/
int      bdd_entailed(DDMgr_ptr dd, bdd_ptr f, bdd_ptr g);

/*!
  \brief Determines whether an intersection between
  f and g is not empty

  Returns 1 if an intersection between
  f and g is not empty; 0 otherwise.
  No new nodes are created.

  \se None
*/
int      bdd_intersected(DDMgr_ptr dd, bdd_ptr f, bdd_ptr g);

/*!
  \brief Returns the then child of a bdd node.

  Returns the then child of a bdd node. The node
  must not be a leaf node. Notice that this funxction does not save
  the bdd. Is the responsibility of the user to save it if it is the case.

  \se None
*/
bdd_ptr  bdd_then(DDMgr_ptr , bdd_ptr);

/*!
  \brief Returns the else child of a bdd node.

  Returns the else child of a bdd node. The node
  must not be a leaf node. Notice that this funxction does not save
  the bdd. Is the responsibility of the user to save it if it is the case.

  \se None
*/
bdd_ptr  bdd_else(DDMgr_ptr , bdd_ptr);

/*!
  \brief Implements ITE(i,t,e).

  Implements ITE(i,t,e). Returns a pointer to the
  resulting BDD if successful;  a failure is
  generated otherwise.

  \se None
*/
bdd_ptr  bdd_ite(DDMgr_ptr , bdd_ptr, bdd_ptr, bdd_ptr);

/*!
  \brief Returns 1 if the BDD pointer is complemented.

  Returns 1 if the BDD pointer is complemented.

  \se None
*/
int      bdd_iscomplement(DDMgr_ptr , bdd_ptr);

/*!
  \brief Finds the current position of variable index in the
  order.

  Finds the current position of variable index in the
  order.

  \se None
*/
int      bdd_readperm(DDMgr_ptr , bdd_ptr);

/*!
  \brief Returns the index of the node.

  Returns the index of the node.

  \se None
*/
int      bdd_index(DDMgr_ptr , bdd_ptr);

/*!
  \brief Picks one on-set minterm deterministically from the given BDD.

  Picks one on-set minterm deterministically from the
  given DD. The minterm is in terms of vars. Builds a BDD for the
  minterm and returns a pointer to it if successful; a failure is
  generated otherwise. There are two reasons why the procedure may fail: It may
  run out of memory; or the function fn may be the constant 0. The
  result is referenced.
*/
bdd_ptr  bdd_pick_one_minterm(DDMgr_ptr , bdd_ptr, bdd_ptr *, int);

/*!
  \brief Picks one on-set minterm randomly from the given DD.

  Picks one on-set minterm randomly from the given DD. The
  minterm is in terms of vars. Builds a BDD for the minterm and returns a
  pointer to it if successful; a failure is generated otherwise. There
  are two reasons why the procedure may fail: It may run out of
  memory; or the function f may be the constant 0.
*/
bdd_ptr  bdd_pick_one_minterm_rand(DDMgr_ptr , bdd_ptr, bdd_ptr *, int);

/*!
  \brief Returns the array of All Possible Minterms

  Takes a minterm and returns an array of all its terms,
  according to variables specified in the array vars[].  Notice that the array
  of the result has to be previously allocated, and its size must be greater
  or equal the number of the minterms of the "minterm" function. The array
  contains referenced BDD so it is necessary to dereference them after their
  use. Calls Cudd_PickAllTerms avoiding to pass it a true picking-from set of
        states.

  \sa bdd_pick_one_minterm_rand bdd_pick_one_minterm
*/
int      bdd_pick_all_terms(DDMgr_ptr , bdd_ptr,  bdd_ptr *, int, bdd_ptr *, int);

/*!
  \brief Finds the variables on which an BDD depends on.

  Finds the variables on which an BDD depends on.
  Returns an BDD consisting of the product of the variables if
  successful; a failure is generated otherwise.

  \sa add_support
*/
bdd_ptr  bdd_support(DDMgr_ptr , bdd_ptr);

/*!
  \brief Counts the number of BDD nodes in an BDD.

  Counts the number of BDD nodes in an BDD. Returns the number
  of nodes in the graph rooted at node.

  \sa bdd_count_minterm
*/
int      bdd_size(DDMgr_ptr , bdd_ptr);

/*!
  \brief Counts the number of BDD minterms of an BDD.

  Counts the number of minterms of an BDD. The function is
  assumed to depend on nvars variables. The minterm count is
  represented as a double, to allow for a larger number of variables.
  Returns the number of minterms of the function rooted at node. The
  result is parameterized by the number of \"nvars\" passed as argument.

  \sa bdd_size bdd_count_minterm
*/
double   bdd_count_minterm(DDMgr_ptr , bdd_ptr, int);

/*!
  \brief Returns the BDD variable with index <code>index</code>.

  Retrieves the BDD variable with index <code>index</code>
  if it already exists, or creates a new BDD variable. Returns a
  pointer to the variable if successful; a failure is generated
  otherwise. The returned value is referenced.

  \sa bdd_new_var_at_level add_new_var_at_level
*/
bdd_ptr  bdd_new_var_with_index(DDMgr_ptr , int);

/*!
  \brief Finds a satisfying path in the BDD d.

  Finds a satisfying path in the BDD d. This path should
  not include all variabales. It only need ot include the levels needed to
  satify the BDD.
*/
bdd_ptr bdd_get_one_sparse_sat(DDMgr_ptr , bdd_ptr);

/*!
  \brief Computes the difference between two BDD cubes.

  Computes the difference between two BDD cubes, i.e. the
  cube of BDD variables belonging to cube a and not belonging to cube
  b. Returns a pointer to the resulting cube; a failure is generated
  otherwise.

  \sa add_cube_diff
*/
bdd_ptr  bdd_cube_diff(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Computes the union between two BDD cubes.

  Computes the union between two BDD cubes, i.e. the
  cube of BDD variables belonging to cube a OR to cube b.
  Returns a pointer to the resulting cube; a failure is generated
  otherwise.

  \sa bdd_cube_intersection,bdd_and
*/
bdd_ptr  bdd_cube_union(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Computes the intersection between two BDD cubes.

  Computes the difference between two BDD cubes, i.e. the
  cube of BDD variables belonging to cube a AND belonging to cube
  b. Returns a pointer to the resulting cube; a failure is generated
  otherwise.

  \sa bdd_cube_union,bdd_cube_diff
*/
bdd_ptr  bdd_cube_intersection(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Returns the index of the lowest variable in the BDD a.

  Returns the index of the lowest variable in the
  BDD, i.e. the variable in BDD a with the highest position in the
  ordering. 
*/
int      bdd_get_lowest_index(DDMgr_ptr , bdd_ptr);

/*!
  \brief Finds a largest cube in a BDD.

  Finds a largest cube in a BDD b, i.e. an implicant of BDD b.
  Notice that, it is not guaranteed to be the largest implicant of b.

  \se The number of literals of the cube is returned in length.
*/
bdd_ptr  bdd_largest_cube(DDMgr_ptr , bdd_ptr, int *);

/*!
  \brief Finds a prime implicant for a BDD.

  Finds the prime implicant of a BDD b based on the largest cube
  in low where low implies b.

  \se None
*/
bdd_ptr  bdd_compute_prime_low(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Finds a set of prime implicants for a BDD.

  Finds the set of prime implicants of a BDD b that are
  implied by low where low implies b.

  \se None
*/
array_t * bdd_compute_primes_low(DDMgr_ptr , bdd_ptr, bdd_ptr);

/*!
  \brief Finds a set of prime implicants for a BDD.

  Finds the set of prime implicants of a BDD b.

  \se None
*/
array_t * bdd_compute_primes(DDMgr_ptr dd, bdd_ptr b);

/*!
  \brief Expands cube to a prime implicant of f.

  Expands cube to a prime implicant of f. Returns the prime
  if successful; NULL otherwise.  In particular, NULL is returned if cube
  is not a real cube or is not an implicant of f.

  \se None
*/
bdd_ptr  bdd_make_prime(DDMgr_ptr dd, bdd_ptr cube, bdd_ptr b);

/*!
  \brief Finds the essential variables of a DD.

  Returns the cube of the essential variables. A positive
  literal means that the variable must be set to 1 for the function to be
  1. A negative literal means that the variable must be set to 0 for the
  function to be 1. Returns a pointer to the cube BDD if successful;
  NULL otherwise.

  \se None
*/
bdd_ptr  bdd_compute_essentials(DDMgr_ptr dd, bdd_ptr b);

/*!
  \brief Writes a blif file representing the argument BDDs.

  Writes a blif file representing the argument BDDs as a
  network of multiplexers. One multiplexer is written for each BDD
  node. It returns 1 in case of success; 0 otherwise (e.g.,
  out-of-memory, file system full, or an ADD with constants different
  from 0 and 1).  bdd_DumpBlif does not close the file: This is the
  caller responsibility. bdd_DumpBlif uses a minimal unique subset of
  the hexadecimal address of a node as name for it.  If the argument
  inames is non-null, it is assumed to hold the pointers to the names
  of the inputs. Similarly for onames.

  \se None

  \sa bdd_DumpBlifBody dd_dump_dot
*/
int      bdd_DumpBlif(DDMgr_ptr dd, int n, bdd_ptr *f, char **inames, char **onames, char *mname, FILE *fp);

/*!
  \brief Writes a blif body representing the argument BDDs.

  Writes a blif body representing the argument BDDs as a
  network of multiplexers.  No header (.model, .inputs, and .outputs) and
  footer (.end) are produced by this function.  One multiplexer is written
  for each BDD node. It returns 1 in case of success; 0 otherwise (e.g.,
  out-of-memory, file system full, or an ADD with constants different
  from 0 and 1).  bdd_DumpBlifBody does not close the file: This is the
  caller responsibility. bdd_DumpBlifBody uses a minimal unique subset of
  the hexadecimal address of a node as name for it.  If the argument
  inames is non-null, it is assumed to hold the pointers to the names
  of the inputs. Similarly for onames. This function prints out only
  .names part.

  \se None

  \sa bdd_DumpBlif dd_dump_dot
*/
int      bdd_DumpBlifBody(DDMgr_ptr dd, int n, bdd_ptr *f, char **inames, char **onames, FILE *fp);

/*!
  \brief Determines whether f is less than or equal to g.

  Returns 1 if f is less than or equal to g; 0 otherwise.
  No new nodes are created.

  \se None
*/
int      bdd_leq(DDMgr_ptr dd, bdd_ptr f, bdd_ptr g);

/*!
  \brief Swaps two sets of variables of the same size (x and y) in
  the BDD f.

  Swaps two sets of variables of the same size (x and y)
  in the BDD f. The size is given by n. The two sets of variables are
  assumed to be disjoint.  Returns a pointer to the resulting BDD if
  successful; an error (which either results in a jump to the last CATCH-FAIL
  block, or in a call to exit()) is triggered otherwise.

  \se None
*/
bdd_ptr  bdd_swap_variables(DDMgr_ptr dd, bdd_ptr f, bdd_ptr *x_varlist, bdd_ptr *y_varlist, int n);

/*!
  \brief Substitutes g for x_v in the BDD for f.

  Substitutes g for x_v in the BDD for f. v is the index of the
  variable to be substituted. bdd_compose passes the corresponding
  projection function to the recursive procedure, so that the cache may
  be used.  Returns the composed BDD if successful; an error (which either
  results in a jump to the last CATCH-FAIL  block, or in a call to exit())
  is triggered otherwise.

  \se None
*/
bdd_ptr  bdd_compose(DDMgr_ptr dd, bdd_ptr f, bdd_ptr g, int v);

/*!
  \brief Returns the reference count of a node.

  Returns the reference count of a node. The node pointer can be
  either regular or complemented.

  \se None
*/
int      bdd_ref_count(DDMgr_ptr dd, bdd_ptr n);

/*!
  \brief Computes the value of a function with given variable values.

  Computes the value (0 or 1) of the given function with the given
  values for variables. The parameter "values" must be an array, at least as
  long as the number of indices in the BDD.

  \se None
*/
int      calculate_bdd_value(DDMgr_ptr mgr, bdd_ptr f, int* values);



#endif /* __NUSMV_CORE_DD_DD_H__ */
