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
  \brief Public interface for a Sset (Sorted Set) class.

  See Sset.c file for description.

*/



#ifndef __NUSMV_CORE_UTILS_SSET_H__
#define __NUSMV_CORE_UTILS_SSET_H__

#include "nusmv/core/utils/defs.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct Sset
  \brief Implementation of Sset class

  
*/
typedef struct Sset_TAG* Sset_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SSET(x) \
         ((Sset_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SSET_CHECK_INSTANCE(x) \
         ( nusmv_assert(SSET(x) != SSET(NULL)) )

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SSET_FOREACH(sset, iter)  \
   for (iter=SSet_first(sset); SSiter_is_valid(iter); \
        iter=SSiter_next(iter))

/* internal type. it cannot be used outside. */

/*!
  \struct Ssnode
  \brief A node of the tree

   NOTE for developers: the structure is the standard AVL
  tree node. The only difference is that the "balance" field is kept in the 2
  lowest bits of the "parent" field. Note that implementation of
  SSET_BALANCE and a few macros depends on this agreement,
  and there is an assertion that pointer should always have two lowest bits
  set to 0.
*/
typedef struct Ssnode_TAG* Ssnode_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void* Sset_key;

/* Iterator type.
   here a struct definition is used only to create a new type. Thus
   C type checker will be able to catch incorrect use of iterators.
   This does not influence the efficiency */
typedef struct Ssiter_TAG {
    Ssnode_ptr node;
} Ssiter;

/* ---------------------------------------------------------------------- */
/* Public interface                                                       */
/* ---------------------------------------------------------------------- */

/*!
  \methodof Sset
  \brief Creates an instance of a Sorted Set 

  Keys are sorted as integers (< and == operators are used)
*/
Sset_ptr Sset_create(void);

/*!
  \methodof Sset
  \brief Creates an instance of a Sorted Set with a comparing function

  
*/
Sset_ptr Sset_create_with_param(PFIVPVP compare);

/*!
  \methodof Sset
  \brief Destroys a set instance

  The memory used by the set will be freed.
  Note: memory occupied by the elements is not freed! It is the user
  responsibility.
*/
void Sset_destroy(Sset_ptr self);

/*!
  \methodof Sset
  \brief Creates a copy of the given set instance

  
*/
Sset_ptr Sset_copy(const Sset_ptr self);

/*!
  \methodof Sset
  \brief Creates a copy of the given set instance, copying each
               element by calling given function.

  
*/
Sset_ptr Sset_copy_func(const Sset_ptr self,
                               void* (*func)(void*, void*), void* arg);

/*!
  \methodof Sset
  \brief Insert an element "element" under
  the key "key" into the set

  
  Returns true if a new node was created and false if a node with the given
  key has already existed (in which case nothing is changed).
  Note: all the existing iterators remain valid.

  It is user responsibility to free the key, if needed.
  
*/
boolean Sset_insert(Sset_ptr self, Sset_key key,
                           void* element);

/*!
  \methodof Sset
  \brief Looks up for an element with a given key

  Returns an iterator pointing to the found element.
  If there is no such element Ssiter_is_valid() returns false
  on the returned iterator.

  The operation takes O(log2 N) time (N is the size of the set).
*/
Ssiter Sset_find(Sset_ptr self, Sset_key key);

/*!
  \methodof Sset
  \brief Looks up for the closest element whose key is less than
                    or equal a given key.

  Returns an iterator pointing to the found element.
                    If there is no such element Ssiter_is_valid()
                    returns false on the returned iterator.

                    The operation takes O(log2 N) time (N is the
                    size of the set).
*/
Ssiter Sset_find_le(Sset_ptr self, Sset_key key);

/*!
  \methodof Sset
  \brief Looks up for the closest element whose key is greater than
                    or equal a given key.

  Returns an iterator pointing to the found element.
                    If there is no such element Ssiter_is_valid()
                    returns false on the returned iterator.

                    The operation takes O(log2 N) time (N is the
                    size of the set).
*/
Ssiter Sset_find_ge(Sset_ptr self, Sset_key key);

/*!
  \methodof Sset
  \brief Looks up for an element with a given key and if does not
  exist it is created

  Returns an iterator pointing to the found (created) element.
  If is_found != NULL, *is_found is set to true if the element
  was found and false if it was created.

  The operation takes O(log2 N) time (N is the size of the set).

  It is user responsibility to free the key, if needed.
*/
Ssiter Sset_find_insert(Sset_ptr self, Sset_key key,
                               boolean* is_found);

/*!
  \methodof Sset
  \brief Removes an element with key "key" from the set.

  The returned value is the element stored in the deleted node.
  If parameter "is_found" is no NULL, "*is_found" is set to
  true if such an element with the provided key was found, and false otherwise.
  Note: if an element with the key does no exist in the set the
  returned value is NULL.

  The operation takes O(log2 N) time (N is the size of the set).
*/
void* Sset_delete(Sset_ptr self, Sset_key key,
                         boolean* is_found);

/*!
  \methodof Sset
  \brief Removes an element pointed by the iterator.

  
  Precondition: the iterator should be returned one by
  Ssiter_first, Ssiter_last, Ssiter_next, Ssiter_prev.
  Precondition: an element pointed by iterator has to belong to this set.
  Precondition: Ssiter_is_valid(iter) has to be return true.

  WARNING: After this function call the iterator will have undefined value
  and no operation is allowed with it except assignment of a new value.

  The operation takes O(log2 N) time (N is the size of the set).
*/
void Sset_delete_iter(Sset_ptr self, Ssiter iter);

/*!
  \methodof Sset
  \brief Returns the number of elements in a set

  Constant time operation
*/
size_t Sset_get_size(Sset_ptr self);

/*!
  \methodof Sset
  \brief Returns true iff the set is empty

  Constant time operation
*/
boolean Sset_is_empty(Sset_ptr self);

/*!
  \methodof Sset
  \brief Returns an iterator pointing to a first element of a set,
  i.e. element with the smallest key.

  If the set is empty Ssiter_is_valid() will be false
  on the returned iterator.
  NOTE: there is no need to free the iterator after using it.
  NOTE: it is allowed to assign one iterator to another one.
  NOTE: The operation may take up to O(log2 N) time (N is the size of the set).
  

  \sa Ssiter_is_end, Ssiter_next, Ssiter_element
*/
Ssiter Sset_first(Sset_ptr self);

/*!
  \methodof Sset
  \brief Returns an iterator pointing to the last element of a set,
  i.e. element with the greatest key.

  If the set is empty Ssiter_is_valid() will be false
  on the returned iterator.
  NOTE: there is no need to free the iterator after using it.
  NOTE: it is allowed to assign one iterator to another one.
  NOTE: The operation may take up to O(log2 N) time (N is the size of the set).
  

  \sa Ssiter_is_end, Ssiter_next, Ssiter_element
*/
Ssiter Sset_last(Sset_ptr self);

/*!
  \brief Returns an iterator pointing to the next element
  of a set w.r.t. the element pointed by a provided iterator, i.e.
  element with a greater key.

   Precondition: this function can be applied only if
  Ssiter_is_valid(iter) returns true.  

  \sa Sset_first, Ssiter_is_end, Ssiter_element
*/
Ssiter Ssiter_next(Ssiter iter);

/*!
  \brief Returns an iterator pointing to the previous element
  of a set w.r.t. the element pointed by a provided iterator, i.e.
  element with a smaller key.

   Precondition: this function can be applied only if
  Ssiter_is_valid(iter) returns true.

  \sa Sset_first, Ssiter_is_valid, Ssiter_element
*/
Ssiter Ssiter_prev(Ssiter iter);

/*!
  \brief Returns true iff an iterator points a valid node
  of a set, i.e. not past the last element or before the first element
  of a set.

  The iterator must have been created with function
  Sset_first, Sset_last, Sset_next or Sset_prev.
  NOTE: the function is constant time.
  WARNING: if the function returns false no other function
  should be invoked on the given iterator!

  \sa Sset_first, Ssiter_next, Ssiter_element
*/
boolean Ssiter_is_valid(Ssiter iter);

/*!
  \brief Returns a value stored in an element pointed by
  a provided iterator

  Precondition: this function can be applied only
  if Ssiter_is_valid(iter) returns true

  \sa Sset_first, Sset_next, Ssiter_is_valid, Ssiter_next
*/
void* Ssiter_element(Ssiter iter);

/*!
  \brief Returns a key stored in an element pointed by
  a provided iterator (and which was used to order the elements)

  Precondition: this function can be applied only
  if Ssiter_is_valid(iter) returns true

  \sa Sset_first, Sset_next, Ssiter_is_valid, Ssiter_next
*/
Sset_key Ssiter_key(Ssiter iter);

/*!
  \brief Sets up a value stored in an element pointed by
  a provided iterator

  Precondition: this function can be applied only
  if Ssiter_is_valid(iter) returns true

  \sa Sset_first, Sset_next, Ssiter_is_valid, Ssiter_next
*/
void Ssiter_set_element(Ssiter iter, void* element);

#ifndef NDEBUG

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void Sset_test(const NuSMVEnv_ptr env);
#endif

#endif /* __NUSMV_CORE_UTILS_SSET_H__ */
