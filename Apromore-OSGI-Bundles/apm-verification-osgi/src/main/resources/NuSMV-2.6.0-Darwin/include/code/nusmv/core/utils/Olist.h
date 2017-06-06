/* ---------------------------------------------------------------------------


  This file is part of the ``compile'' package of NuSMV version 2.
  Copyright (C) 2008 by FBK.

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
  \author Andrei Tchaltsev
  \brief Public interface for a Olist class

  See Olist.c for the description.

*/



#ifndef __NUSMV_CORE_UTILS_OLIST_H__
#define __NUSMV_CORE_UTILS_OLIST_H__

#include "nusmv/core/utils/defs.h"
#include "nusmv/core/node/printers/MasterPrinter.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct Olist
  \brief Implementation of Olist class


*/
typedef struct Olist_TAG* Olist_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OLIST(x) \
         ((Olist_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OLIST_CHECK_INSTANCE(x) \
         ( nusmv_assert(OLIST(x) != OLIST(NULL)) )

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OLIST_FOREACH(list, iter) \
 for (iter = Olist_first(list); !Oiter_is_end(iter); iter = Oiter_next(iter))

/* internal type. it cannot be used outside. */

/*!
  \struct Onode
  \brief A node of the list


*/
typedef struct Onode_TAG* Onode_ptr;
/* here a struct definition is used only to create a new type. Thus
   C type checker will be able to catch incorrect use of iterators */
typedef struct Oiter_TAG {Onode_ptr* node;} Oiter;

/* ---------------------------------------------------------------------- */
/* Public interface                                                       */
/* ---------------------------------------------------------------------- */
/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof Olist
  \brief Creates an instance of a One-directional List


*/
Olist_ptr Olist_create(void);

/*!
  \methodof Olist
  \brief Destroys a list instance

  The memory used by the list will be freed.
  Note: memory occupied by the elements is not freed! It is the user
  responsibility.
*/
void Olist_destroy(Olist_ptr self);

/*!
  \methodof Olist
  \brief Creates a copy of a given list

  Note: input list does not change

  \sa Olist_copy_reversed
*/
Olist_ptr Olist_copy(Olist_ptr self);

/*!
  \methodof Olist
  \brief Creates a copy of a given list with the order of elements
  reversed

  Note: input list does not change

  \sa Olist_copy
*/
Olist_ptr Olist_copy_reversed(Olist_ptr self);

/*!
  \methodof Olist
  \brief Creates a copy of a given list
  with all its elements except the provided one.

  Note: input list does not change

  \sa Olist_copy
*/
Olist_ptr Olist_copy_without_element(Olist_ptr self,
                                            void* element);

/*!
  \methodof Olist
  \brief Reverse the order of elements in the list

  Note: all existing iterators pointing to the
  elements of the list may become invalid.

  \sa Olist_copy_reversed
*/
void Olist_reverse(Olist_ptr self);

/*!
  \methodof Olist
  \brief Moves the content from one list to another

  The content is moved from "self" to "to_list" and placed
  before iterator iter_to.

  Note: all existing iterators pointing to the elements of the lists
  may become invalid.

  Precondition: iter_to has to be an iterator of list 'to_list' or
  Oiter_is_end(iter_to) has to be return true.

  \sa Olist_copy_reversed
*/
void Olist_move(Olist_ptr self, Olist_ptr to_list,
                       Oiter iter_to);

/*!
  \methodof Olist
  \brief Moves the content from one list to another

  This function is similar to Olist_move
  with iter_to set up to point past the last element.
  Note: all existing iterators pointing to the elements of the list
  may become invalid.

  \sa Olist_move
*/
void Olist_move_all(Olist_ptr self, Olist_ptr to_list);

/*!
  \methodof Olist
  \brief Removes all the elements of the list, i.e.  makes the
  list empty

   After this function call, Olist_is_empty(self)
  always returns true.

  Note: all existing iterators pointing to the elements of the list
  becomes invalid.

  \sa Olist_copy_reversed
*/
void Olist_clean(Olist_ptr self);

/*!
  \methodof Olist
  \brief Adds all the elements of 'other' ath the end of 'self'

  Linear in the size of 'other'

  \sa Olist_append
*/
void Olist_concat(Olist_ptr self, Olist_ptr other);

/*!
  \methodof Olist
  \brief Adds at the beginning of a list a new element

  \sa Olist_append
*/
void Olist_prepend(Olist_ptr self, void* element);

/*!
  \methodof Olist
  \brief Adds at the end of a list a new element

  \sa Olist_prepend
*/
void Olist_append(Olist_ptr self, void* element);

/*!
  \methodof Olist
  \brief Removes a first element of a list

  The removed element is returned.
  Precondition: the list must not be empty.

  NOTE: all iterators already pointing to the element next to the
  first one will become invalid.  Any operations on them are
  prohibited.
  ADVICE: do not use several iterators over the same list
  if deletion operation is possible.

  \sa Olist_append, Olist_prepend
*/
void* Olist_delete_first(Olist_ptr self);

/*!
  \methodof Olist
  \brief Returns the size of a list


*/
int Olist_get_size(const Olist_ptr self);

/*!
  \methodof Olist
  \brief Returns true iff the list is empty


*/
boolean Olist_is_empty(Olist_ptr self);

/*!
  \methodof Olist
  \brief Returns an iterator pointing to a first element of a
  list

  If the list is empty the iterator will point past
  the last element of a list (i.e. past the list). This means
  function Oiter_is_end will return true in this case.  NOTE: there
  is no need to free the iterator after using it.  NOTE: it is
  allowed to assign one iterator to another one.  NOTE: deletion
  the elements of the list may make the iterator invalid (see
  corresponding delete functions).

  \sa Oiter_is_end, Oiter_next, Oiter_element, Olist_end
*/
Oiter Olist_first(Olist_ptr self);

/*!
  \methodof Olist
  \brief Returns an iterator pointing the last element
  of the list

  Function Oiter_is_end will always return true on the
  result of this function. NOTE: there is no need to free the iterator
  after using it.  NOTE: it is allowed to assign one iterator to
  another one.  NOTE: deletion the elements of the list may make the
  iterator invalid (see corresponding delete functions).

  \sa Oiter_is_end, Oiter_next, Oiter_element
*/
Oiter Olist_last(Olist_ptr self);

/*!
  \methodof Olist
  \brief Returns an iterator pointing past the last element
  of the list

  Function Oiter_is_end will always return true on the
  result of this function. NOTE: there is no need to free the iterator
  after using it.  NOTE: it is allowed to assign one iterator to
  another one.  NOTE: deletion the elements of the list may make the
  iterator invalid (see corresponding delete functions).

  \sa Oiter_is_end, Oiter_next, Oiter_element
*/
Oiter Olist_end(Olist_ptr self);

/*!
  \methodof Olist
  \brief Returns true if iter corresponds to the first iter.



  \sa Oiter_is_end, Oiter_next, Oiter_element
*/
boolean Olist_iter_is_first(Olist_ptr self, Oiter iter);

/*!
  \brief Returns true iff an iterator points past the last element
  of a list.

  The iterator must have been created with function
  Olist_first, Olist_end or Olist_next

  \sa Olist_first, Olist_end, Oiter_next, Oiter_element
*/
boolean Oiter_is_end(Oiter iter);

/*!
  \methodof Olist
  \brief Returns true if iter corresponds to the last element.



  \sa Oiter_is_end, Oiter_next, Oiter_element
*/
boolean Olist_iter_is_last(Olist_ptr self, Oiter iter);

/*!
  \brief Returns an iterator pointing to the next element
  of a list w.r.t. the element pointed by a provided iterator.

  Precondition: this function can be applied only
  if Oiter_is_end(iter) returns false

  \sa Olist_first, Oiter_is_end, Oiter_element
*/
Oiter Oiter_next(Oiter iter);

/*!
  \brief Returns a value of a list element pointed by
  a provided iterator

  Precondition: this function can be applied only
  if Oiter_is_end(iter) returns false

  \sa Olist_first, Oiter_is_end, Oiter_next
*/
void* Oiter_element(Oiter iter);

/*!
  \brief Sets a new value to the list element pointed by
  a provided iterator

  Precondition: this function can be applied only
  if Oiter_is_end(iter) returns false

  \sa Olist_first, Oiter_is_end, Oiter_next, Oiter_element
*/
void Oiter_set_element(Oiter iter, void* element);

/*!
  \methodof Olist
  \brief Insert a new element into the list "self" directly after
  an element pointed by "iter"

  Precondition: iter must point to elements of list "self"
  and NOT past the last element of the list.
  If iter is not an iterator of list self there will be
  problems with memory which are usually very difficult to debug.

  NOTE: after the function call iterators pointing to the element
  after iter will now point to the newly inserted element. All other
  existing iterators (including iter) will point to the same element
  as before.

  Returns an iterator pointing to the newly inserted element.
*/
Oiter Olist_insert_after(Olist_ptr self, Oiter iter,
                               void* element);

/*!
  \methodof Olist
  \brief Insert a new element into the list "self" directly before
  an element pointed by "iter"

  Precondition: iter must point to elements of list "self"
  or past the last element of the list.

  If the iterator points past the last element of a list then
  this function is equivalent to calling Olist_append(self, element).

  NOTE: All existing iterators equal to "iter" (and iter itself)
  after insertion will point to the newly created element.
  All other iterators remain intact.

  Returns an iterator pointing to the newly inserted element.
*/
Oiter Olist_insert_before(Olist_ptr self, Oiter iter,
                          void* element);

/*!
  \methodof Olist
  \brief Removes an element pointed by an iterator from a list

  Precondition: iter must point to elements of list "self" and
  NOT the past the last element of the list.

  The element being removed is returned in argument *element (only if
  element != NULL).

  Returns an iterator pointing to the element after removed one.

  NOTE: all iterators already pointing to the next element will become invalid.
  Any operations on them are prohibited.
  ADVICE: do not use several iterators over the same list if deletion
  operation is possible.

  NOTE: if the deletion is used inside an OLIST_FOREACH macro cycle it must be
  considered that the next element will not be the one after the current deleted
  element, but the next of it.

*/
Oiter Olist_delete(Olist_ptr self, Oiter iter, void** element);

/*!
  \methodof Olist
  \brief Returns true iff the list contains the given element


*/
boolean Olist_contains(const Olist_ptr self, const void* element);

/*!
  \methodof Olist
  \brief Tries to remove all the occurrences of the specified
                      element from the list, returns true if an element was
                      removed, false otherwise


*/
boolean Olist_remove(Olist_ptr self, const void* element);

/*!
  \methodof Olist
  \brief Sorts the list in place

  mergesort is used to sort the list.
  worst case complexisty O(N log2(N)).

  cmp is comparison function returning value v, v < 0, v == 0 or v > 0,
  extra is a user parameter that is passed to every invocation of cmp
*/
void Olist_sort(Olist_ptr self,
                       int (*cmp)(void* el1, void* el2, void* extra),
                       void* extra);

/*!
  \methodof Olist
  \brief Prints the elements of the list using
  print_node and putting string ", " between elements


  Precondition: all elements of the list have to be node_ptr.
*/
void Olist_print_node(Olist_ptr self,
                             MasterPrinter_ptr printer, FILE* output);

/*!
  \brief This is a test function

  Prototype of this function is not defined anywhere.
  Thus to use it define the prototype where you want and then invoke
  this function.
  MD: I added the prototype to avoid having missing prototypes. A better way to
  hide the function is to use a macro to compile it away.
*/
void olist_testing_function(const NuSMVEnv_ptr env);

/*!
  \methodof Olist
  \brief Functional programming map

  Call func over each element of the list. Very useful for
  example for freeing the elements
*/
void
Olist_map(Olist_ptr self, void (*func)(void* elem));

#endif /* __OLIST_H__ */
