/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 2003 by FBK-irst.

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
  \brief  The header file of NodeList class.

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_UTILS_NODE_LIST_H__
#define __NUSMV_CORE_UTILS_NODE_LIST_H__

#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/printers/MasterPrinter.h"

/*!
  \struct NodeList
  \brief A list based on (and compatible with) node_ptr lists


*/
typedef struct NodeList_TAG* NodeList_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_LIST(x)  \
        ((NodeList_ptr) (x))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_LIST_CHECK_INSTANCE(x)  \
        (nusmv_assert(NODE_LIST(x) != NODE_LIST(NULL)))

/*!
  \struct Link
  \brief Use when iterating on NodeLists


*/
typedef struct Link_TAG* ListIter_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LIST_ITER(x)  \
        ((ListIter_ptr) (x))

/*!
  \brief A type of a predicate function used by NodeList_search

  The function should returns true iff
  a given element "corresponds" to 'arg'. 'arg' can be any datastructure.
*/
typedef boolean (*NodeListPred) (node_ptr element, void* arg);


/*!
  \brief Used when calling method foreach

  Must be a pointer to a user-defined function.
  This function gets:
  - the list which method foreach iterates on
  - the iterator pointing to the current element in the list
  - user data, passed to method foreach

  Must return true to continue iteration, false to interrupt it
*/

typedef boolean (*NODE_LIST_FOREACH_FUN_P)(NodeList_ptr list, ListIter_ptr iter,
                                           void* user_data);


/* NodeList_ptr, ListIter_ptr */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_LIST_FOREACH(list, iter)                               \
   for (iter=NodeList_get_first_iter(list); !ListIter_is_end(iter); \
        iter=ListIter_get_next(iter))

/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof NodeList
  \brief Creates a new list


*/
NodeList_ptr NodeList_create(void);

/*!
  \methodof NodeList
  \brief Constructor that creates a new NodeList that is a wrapper
  of the given list.

  self becomes a user of the given list, meaning that
  when self will be destroyed, it will not free the given list. It is a caller
  responsability of freeing the passed list when possible.
*/
NodeList_ptr NodeList_create_from_list(node_ptr list);

/*!
  \methodof NodeList
  \brief Creates a singleton nodelist



  \se Must be freed by the caller
*/
NodeList_ptr NodeList_create_from_element(node_ptr node);

/*!
  \methodof NodeList
  \brief Copies self and returns a new independent instance

  Linear time
*/
NodeList_ptr NodeList_copy(NodeList_ptr self);

/*!
  \methodof NodeList
  \brief Class destroyer


*/
void NodeList_destroy(NodeList_ptr self);


/* Getters and Setters ********************************************************/

/*!
  \methodof NodeList
  \brief Returns the number of elements in the list

  Constant time
*/
int NodeList_get_length(const NodeList_ptr self);


/* Queries  *******************************************************************/

/*!
  \methodof NodeList
  \brief Returns true iff the size of the list is 0


*/
boolean NodeList_is_empty(const NodeList_ptr self);

/*!
  \methodof NodeList
  \brief Returns true if given element belongs to self

  Constant time (cost may depend on the internal hash
  status)
*/
boolean
NodeList_belongs_to(const NodeList_ptr self, node_ptr elem);


/* Insertion ******************************************************************/

/*!
  \methodof NodeList
  \brief Appends a new node at the end of the list

  Constant time
*/
void NodeList_append(NodeList_ptr self, node_ptr elem);

/*!
  \methodof NodeList
  \brief Prepends a new node at the beginning of the list

  Constant time
*/
void NodeList_prepend(NodeList_ptr self, node_ptr elem);


/* Retrieval********************************************************************/

/*!
  \methodof NodeList
  \brief Returns the element at the position pointed by iter


*/
node_ptr NodeList_get_elem_at(const NodeList_ptr self,
                                     const ListIter_ptr iter);


/* Removal ********************************************************************/

/*!
  \methodof NodeList
  \brief Removes the elements that are found in other list

  Linear time on the size of self. No iteration is done
  if other is empty.  If not NULL, disposer is called on the removed
  element, passing disposer_arg. If the disposer returns true, the
  removal continues, otherwise it aborts and returns with the list as
  it is at that time. Returns the number of removed elements
*/
int NodeList_remove_elems(NodeList_ptr self,
                                 const NodeList_ptr other,
                                 NodeListPred disposer,
                                 void* disposer_arg);


/* ListIter subinterface ******************************************************/

/*!
  \methodof ListIter
  \brief Returns the following iterator


*/
ListIter_ptr ListIter_get_next(const ListIter_ptr self);

/*!
  \methodof ListIter
  \brief Returns true if the iteration is given up


*/
boolean ListIter_is_end(const ListIter_ptr self);

/*!
  \brief Returns the end iterator


*/
ListIter_ptr ListIter_get_end(void);


/* ListIter *******************************************************************/

/*!
  \methodof NodeList
  \brief Returns the iterator pointing to the first element


*/
ListIter_ptr NodeList_get_first_iter(const NodeList_ptr self);

/*!
  \methodof NodeList
  \brief Inserts the given element before the node pointed by the
  given iterator

  Constant time

  \sa insert_after
*/
void NodeList_insert_before(NodeList_ptr self, ListIter_ptr iter,
                                   node_ptr elem);

/*!
  \methodof NodeList
  \brief Inserts the given element after the node pointed by the
  given iterator

  Constant time. iter must be a valid iterator, and
  cannot point at the end of the list

  \sa insert_before
*/
void NodeList_insert_after(NodeList_ptr self, ListIter_ptr iter,
                                  node_ptr elem);

/*!
  \methodof NodeList
  \brief Removes the element pointed by the given iterator

  The removed element is returned. The given iterator
                      won't be usable anymore. Constant time.
*/
node_ptr NodeList_remove_elem_at(NodeList_ptr self,
                                        ListIter_ptr iter);

/*!
  \methodof NodeList
  \brief Searches for an element in a list such that
  'pred'(element, 'arg') returns true.

  Linear time search is used to
  find an element 'elem' such that function pred(elem, arg) returns
  true.
  An iterator pointing to the found element is returned.
  If the element is not found then ListIter_is_end will be true on the
  returned iterator.

  If pred is NULL then a search for an element equal to arg will be
  done (as if pred was a pointer-equality predicate). If pred is
  NULL and the searched element does not occur in the list, the
  function returns in constant time.

  \sa ListIter_is_end, NodeList_belongs_to
*/
ListIter_ptr
NodeList_search(const NodeList_ptr self, NodeListPred pred, void* arg);


/* Printers *******************************************************************/

/*!
  \methodof NodeList
  \brief Prints the nodes in the list, separated by spaces

  The list must be a list of actual node_ptr
*/
void NodeList_print_nodes(const NodeList_ptr self,
                          MasterPrinter_ptr printer,
                          FILE* out);


/* Miscellaneous **************************************************************/

/*!
  \methodof NodeList
  \brief Reverses the list

  Linear time
*/
void NodeList_reverse(NodeList_ptr self);

/*!
  \methodof NodeList
  \brief Append all the elements in src to self

  Cost is linear in the size of src

  \se Content of self will change is src is not empty
*/
void NodeList_concat(NodeList_ptr self, const NodeList_ptr src);

/*!
  \methodof NodeList
  \brief Append all the elements in src to self, but only if
  each element does not occur in self already

  Cost is linear in the size of src

  \se Content of self may change is src is not empty
*/
void NodeList_concat_unique(NodeList_ptr self, const NodeList_ptr src);

/*!
  \methodof NodeList
  \brief Returns the number of occurrences of the given element

  Constant time (cost may depend on the internal hash
  status)
*/
int
NodeList_count_elem(const NodeList_ptr self, node_ptr elem);

/*!
  \methodof NodeList
  \brief Walks through the list, calling given funtion
  for each element

  Returns the number of visited nodes, which can be less
  than the total number of elements since foo can decide to interrupt
  the walking
*/
int
NodeList_foreach(NodeList_ptr self, NODE_LIST_FOREACH_FUN_P foo,
                 void* user_data);

/*!
  \methodof NodeList
  \brief Returns a new list that contains all elements of
  self, after applying function foo to each element

  Elements are not copied. Returned list must be
  freed by the caller
*/
NodeList_ptr NodeList_map(const NodeList_ptr self, NPFN foo);

/*!
  \methodof NodeList
  \brief Returns a new list that contains all elements of
  self for which function foo returned true.

  Elements are not copied. Returned list must be
  freed by the caller
*/
NodeList_ptr NodeList_filter(const NodeList_ptr self, BPFN foo);

/*!
  \methodof NodeList
  \brief Sorts self, using a support array

  qsort is used
*/
void NodeList_sort(NodeList_ptr self, int (*cmp)(const void* el1, const void* el2));

/*!
  \brief Test NodeList_sort


*/
int NodeList_test(NuSMVEnv_ptr env);


#endif /* __NUSMV_CORE_UTILS_NODE_LIST_H__ */
