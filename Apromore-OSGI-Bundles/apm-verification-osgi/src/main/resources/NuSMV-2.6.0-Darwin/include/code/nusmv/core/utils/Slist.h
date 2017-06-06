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
  \brief Public interface for a Slist (Simple List) class

  See Slist.c for the description.

*/



#ifndef __NUSMV_CORE_UTILS_SLIST_H__
#define __NUSMV_CORE_UTILS_SLIST_H__

#include "nusmv/core/utils/defs.h"
#include "nusmv/core/node/printers/MasterPrinter.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct Slist
  \brief Implementation of Slist class

  
*/
typedef struct Slist_TAG* Slist_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SLIST(x) \
         ((Slist_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SLIST_CHECK_INSTANCE(x) \
         ( nusmv_assert(SLIST(x) != SLIST(NULL)) )

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SLIST_FOREACH(list, iter)                               \
   for (iter=Slist_first(list); !Siter_is_end(iter); \
        iter=Siter_next(iter))


/* internal type. it cannot be used outside. */

/*!
  \struct Snode
  \brief A node of the list

  
*/
typedef struct Snode_TAG* Snode_ptr;

/* Iterator type.
   here a struct definition is used only to create a new type. Thus
   C type checker will be able to catch incorrect use of iterators.
   This does not influence on efficiency
*/
typedef struct Siter_TAG {Snode_ptr node;} Siter;

/* Frre function type */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*Slist_free_function)(void*);

/* ---------------------------------------------------------------------- */
/* Public interface                                                       */
/* ---------------------------------------------------------------------- */

/*!
  \methodof Slist
  \brief Creates an instance of a Simple List 

  
*/
Slist_ptr Slist_create(void);

/*!
  \methodof Slist
  \brief Destroys a list instance

  The memory used by the list will be freed.
  Note: memory occupied by the elements is not freed! It is the user
  responsibility.
*/
void Slist_destroy(Slist_ptr self);

/*!
  \methodof Slist
  \brief Destroys the list and every element contained using the
                      specified function

  
*/
void Slist_destroy_and_free_elements(Slist_ptr self,
                                            Slist_free_function f);

/*!
  \methodof Slist
  \brief Creates a copy of a given list

  Note: input list does not change

  \sa Slist_copy_reversed
*/
Slist_ptr Slist_copy(Slist_ptr self);

/*!
  \methodof Slist
  \brief Creates a copy of a given list with the order of elements
  reversed

  Note: input list does not change

  \sa Slist_copy
*/
Slist_ptr Slist_copy_reversed(Slist_ptr self);

/*!
  \methodof Slist
  \brief Reverse the order of elements in the list

  Note: all existing iterators pointing to the
  elements of the list may become invalid.
  Do not use them after this function call.

  \sa Slist_copy_reversed
*/
void Slist_reverse(Slist_ptr self);

/*!
  \methodof Slist
  \brief Adds at the beginning of a list a new element

  

  \sa Slist_append
*/
void Slist_push(Slist_ptr self, void* element);

/*!
  \methodof Slist
  \brief Removes an element at the beginning of a list

  The removed element is returned.
  Existing iterators pointing to the first element become invalid
  after this function call and cannot be used any further.

  \sa Slist_append
*/
void* Slist_pop(Slist_ptr self);

/*!
  \methodof Slist
  \brief Returns the element at the beginning of a list

  

  \sa Slist_append
*/
void* Slist_top(Slist_ptr self);

/*!
  \methodof Slist
  \brief Returns true iff the list is empty

  
*/
boolean Slist_is_empty(Slist_ptr self);

/*!
  \methodof Slist
  \brief Returns an iterator pointing to a first element of a list

  If the list is empty the iterator will point past
  the last element of a list (i.e. past the list). This means function
  Siter_is_end will return true in this case.
  NOTE: there is no need to free the iterator after using it.
  NOTE: it is allowed to assign one iterator to another one.
  

  \sa Siter_is_end, Siter_next, Siter_element
*/
Siter Slist_first(Slist_ptr self);

/*!
  \brief Returns an iterator pointing past the last element
  of the list

  Siter_is_end(Slist_end()) is always true.
  NOTE: there is no need to free the iterator after using it.
  NOTE: it is allowed to assign one iterator to another one.
  

  \sa Siter_is_end, Siter_next, Siter_element
*/
Siter Slist_end(void);

/*!
  \brief Returns true iff an iterator points past the last element
  of a list.

  The iterator must have been created with function
  Slist_first or Slist_next

  \sa Slist_first, Siter_next, Siter_element
*/
boolean Siter_is_end(Siter iter);

/*!
  \brief Returns true iff the iterator points to the last element
                      of a list.

  

  \sa Siter_is_end, Siter_next
*/
boolean Siter_is_last(Siter iter);

/*!
  \brief Returns an iterator pointing to the next element
  of a list w.r.t. the element pointed by a provided iterator.

   Precondition: this function can be applied only if
  Siter_is_end(iter) returns false.  

  \sa Slist_first, Siter_is_end, Siter_element
*/
Siter Siter_next(Siter iter);

/*!
  \brief Returns a value of a list element pointed by
  a provided iterator

  Precondition: this function can be applied only
  if Siter_is_end(iter) returns false

  \sa Slist_first, Siter_is_end, Siter_next
*/
void* Siter_element(Siter iter);

/*!
  \methodof Slist
  \brief Returns an iterator pointing to the first element
  equal to the given one

  
  If there is no such element then on the returned iterator
  Siter_is_end(iter) will be true.

  \sa Slist_first, Siter_is_end, Siter_next
*/
Siter Slist_find(Slist_ptr self, const void* element);

/*!
  \methodof Slist
  \brief Checks whether the specified element is in the list or not

  

  \sa Slist_first, Siter_is_end, Siter_next
*/
boolean Slist_contains(Slist_ptr self, const void* element);

/*!
  \methodof Slist
  \brief Removes all the occurrencies of specified element if
                      present in the list. Returns true if the element was
                      removed, false otherwise

  

  \sa Slist_first, Siter_is_end, Siter_next
*/
boolean Slist_remove(Slist_ptr self, const void* element);

/*!
  \methodof Slist
  \brief Appends two lists modifying self

  

  \se self is extended
*/
void Slist_append(Slist_ptr self, const Slist_ptr other);

/*!
  \methodof Slist
  \brief Returns true iff the two lists are equal
                      (contains the same elements in the same order)

  
*/
boolean Slist_equals(const Slist_ptr self, const Slist_ptr other);

/*!
  \methodof Slist
  \brief Returns the size of a list

  

  \sa Slist_append
*/
unsigned int Slist_get_size(Slist_ptr self);

/*!
  \methodof Slist
  \brief Sorts the list in place

  mergesort is used to sort the list.
  worst case complexisty O(N log2(N)).

  cmp is comparison function returning value v, v < 0, v == 0 or v > 0
  extra is a user parameter that is passed to every invocation of cmp
*/
void Slist_sort(Slist_ptr self,
                       int (*cmp)(void* el1, void* el2, void* extra),
                       void* extra);

/*!
  \methodof Slist
  \brief Pops all the elements of this list

  
*/
void Slist_clear(Slist_ptr self);

/*!
  \brief Prints a list

  Prints a lust to the specified file stream. Third
  parameter printer is a callback to be used when printing
  elements. If NULL, elements will be assumed to be node_ptr and
  print_node is called. printer_arg is an optional argument to be
  passed to the printer (can be NULL)
*/
void Slist_print(Slist_ptr const self,
                        MasterPrinter_ptr wffprint,
                        void (*printer)(FILE* file, void* el, void* arg),
                        FILE* file,
                        void* printer_arg);

#endif /* __NUSMV_CORE_UTILS_SLIST_H__ */
