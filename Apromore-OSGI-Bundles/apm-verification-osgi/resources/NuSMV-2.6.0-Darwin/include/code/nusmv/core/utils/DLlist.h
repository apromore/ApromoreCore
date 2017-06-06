/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package.
  %COPYRIGHT%

-----------------------------------------------------------------------------*/

/*!
  \author Andrei Tchaltsev
  \brief Public interface for a DLlist class

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_DLLIST_H__
#define __NUSMV_CORE_UTILS_DLLIST_H__

#include "nusmv/core/utils/StreamMgr.h"
#include "nusmv/core/utils/defs.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct DLlist
  \brief Implementation of DLlist class

  
*/
typedef struct DLlist_TAG* DLlist_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DL_LIST(x) \
         ((DLlist_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DL_LIST_CHECK_INSTANCE(x) \
         ( nusmv_assert(DL_LIST(x) != DL_LIST(NULL)) )

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DL_LIST_FOREACH(list, iter) \
 for (iter = DLlist_first(list); !DLiter_is_end(iter); iter = DLiter_next(iter))

/* internal type. it cannot be used outside. */

/*!
  \struct DLnode
  \brief A node of the list

  
*/
typedef struct DLnode_TAG* DLnode_ptr;
/* here a struct definition is used only to create a new type. Thus
   C type checker will be able to catch incorrect use of iterators */
typedef struct DLiter_TAG {DLnode_ptr node;} DLiter;

/* ---------------------------------------------------------------------- */
/* Public interface                                                       */
/* ---------------------------------------------------------------------- */
/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof DLlist
  \brief Creates an instance of a Two-dimentional List 

  
*/
DLlist_ptr DLlist_create(void);

/*!
  \methodof DLlist
  \brief Destroys a list instance

  The memory used by the list will be freed.
  Note: memory occupied by the elements is not freed! It is the user
  responsibility.
*/
void DLlist_destroy(DLlist_ptr self);

/*!
  \methodof DLlist
  \brief Creates a copy of a given list

  Note: input list does not change

  \sa DLlist_copy_reversed
*/
DLlist_ptr DLlist_copy(DLlist_ptr self);

/*!
  \methodof DLlist
  \brief Creates a copy of a given list with the order of elements
  reversed

  Note: input list does not change

  \sa DLlist_copy
*/
DLlist_ptr DLlist_copy_reversed(DLlist_ptr self);


/* Getters and Setters ********************************************************/

/*!
  \methodof DLlist
  \brief Returns the size of a list

  
*/
int DLlist_get_size(DLlist_ptr self);


/* Checkers *******************************************************************/

/*!
  \methodof DLlist
  \brief Returns true iff the list is empty

  
*/
boolean DLlist_is_empty(DLlist_ptr self);


/* SubInterface: DLiter ********************************************************/

/*!
  \brief Returns true iff an iterator points to the first
  element of a list

  The iterator must have been created with function
  DLlist_first, DLlist_end, DLlist_next or DLlist_prev.
  If a list is empty then for any of its iterators this function
  will return false.

  \sa DLlist_first, DLlist_last, DLiter_next, DLlist_prev,
  DLiter_element
*/
boolean DLiter_is_first(DLiter iter);

/*!
  \brief Returns true iff an iterator points past the last element
  of a list.

  The iterator must have been created with function
  DLlist_first, DLlist_end, DLlist_prev or DLlist_next

  \sa DLlist_first, DLlist_end, DLiter_next, DLiter_prev,
  DLiter_element
*/
boolean DLiter_is_end(DLiter iter);

/*!
  \brief Returns an iterator pointing to the next element
  of a list w.r.t. the element pointed by a provided iterator.

  Precondition: this function can be applied only
  if DLiter_is_end(iter) returns false

  \sa DLiter_prev, ,DLiter_is_end, DLiter_element
*/
DLiter DLiter_next(DLiter iter);

/*!
  \brief Returns an iterator pointing to the previous element
  of a list w.r.t. the element pointed by a provided iterator.

  Precondition: this function can be applied only
  if DLiter_is_first(iter) returns false

  \sa DLiter_next, ,DLiter_is_end, DLiter_element
*/
DLiter DLiter_prev(DLiter iter);

/*!
  \brief Returns a value of a list element pointed by
  a provided iterator

  Precondition: this function can be applied only
  if DLiter_is_end(iter) returns false

  \sa DLlist_first, DLlist_end, DLiter_is_end, DLiter_prev, DLiter_next
*/
void* DLiter_element(DLiter iter);


/* Methods that work with a DLiter *********************************************/

/*!
  \methodof DLlist
  \brief Returns an iterator pointing to the first element
  of a list

  If the list is empty the iterator will point past
  the last element of a list (i.e. past the list). This means function
  DLiter_is_end() will return true in this case and
  DLiter_is_first() will return false.

  NOTE: there is no need to free the iterator after using it.
  NOTE: it is allowed to assign one iterator to another one.
  NOTE: deletion the elements of the list may make the iterator invalid
  (see corresponding delete functions).
  

  \sa DLiter_is_end, DLiter_next, Iiter_prev, DLiter_element,
  
*/
DLiter DLlist_first(DLlist_ptr self);

/*!
  \methodof DLlist
  \brief Returns an iterator pointing past the last element
  of a list

  For returned iterator function DLiter_is_end() will
  always return true and DLiter_is_first() will always return false.

  NOTE: there is no need to free the iterator after using it.
  NOTE: it is allowed to assign one iterator to another one.
  NOTE: deletion the elements of the list may make the iterator invalid
  (see corresponding delete functions).
  

  \sa DLiter_is_end, DLiter_next, Iiter_prev, DLiter_element,
  DLlist_first
*/
DLiter DLlist_end(DLlist_ptr self);

/*!
  \methodof DLlist
  \brief Insert a new element into the list "self" directly after
  an element pointed by "iter"

  Precondition: iter must point to elements of list "self"
  and NOT past the last element of the list.
  If iter is not an iterator of list self there will be
  problems with memory which are usually very difficult to debug.

  NOTE: after the function call all existing iterators (including iter)
  will point to the same element as before.

  Returns an iterator pointing to the newly inserted element.
*/
DLiter DLlist_insert_after(DLlist_ptr self, DLiter iter,
                               void* element);

/*!
  \methodof DLlist
  \brief Insert a new element into the list "self" directly before
  an element pointed by "iter"

  Precondition: iter must point to elements of list "self"
  or past the last element of the list.

  If the iterator points past the last element of a list then
  this function is equivalent to calling DLlist_append(self, element).

  NOTE: after the function call all existing iterators (including iter)
  will point to the same element as before.

  Returns an iterator pointing to the newly inserted element.
*/
DLiter DLlist_insert_before(DLlist_ptr self, DLiter iter,
                               void* element);

/*!
  \methodof DLlist
  \brief Removes an element pointed by an iterator from a list

  
  Precondition: iter must point to elements of list "self" and
  NOT the past the last element of the list.

  The element being removed is returned in argument *element (only if
  element != NULL).

  Returns an iterator pointing to the element after removed one.

  NOTE: all iterators equal to iter will become invalid.
  Any operations on them are prohibited.
  ADVICE: do not use several iterators over the same list if deletion
  operation is possible.
*/
DLiter DLlist_delete(DLlist_ptr self, DLiter iter, void** element);


/* Miscellaneous **************************************************************/

/*!
  \methodof DLlist
  \brief Reverse the order of elements in the list

  Note: existing iterators pointing to the
  elements of the list remains the same and may be used later on

  \sa DLlist_copy_reversed
*/
void DLlist_reverse(DLlist_ptr self);

/*!
  \methodof DLlist
  \brief Adds at the beginning of a list a new element

  

  \sa DLlist_append
*/
void DLlist_prepend(DLlist_ptr self, void* element);

/*!
  \methodof DLlist
  \brief Adds at the end of a list a new element

  

  \sa DLlist_prepend
*/
void DLlist_append(DLlist_ptr self, void* element);

/*!
  \methodof DLlist
  \brief Removes a first element of a list

  The removed element is returned.
  Precondition: the list must not be empty.

  \sa DLlist_append, DLlist_prepend, DLlist_delete_last
*/
void* DLlist_delete_first(DLlist_ptr self);

/*!
  \methodof DLlist
  \brief Removes a last element of a list

  The removed element is returned.
  Precondition: the list must not be empty.

  \sa DLlist_append, DLlist_prepend, DLlist_delete_first
*/
void* DLlist_delete_last(DLlist_ptr self);


/* Self-test/Debug ************************************************************/

/*!
  \brief This is a test function

  Prototype of this function is not defined anywhere.
  Thus to use it define the prototype where you want and then invoke
  this function.
  [MD] I do think this is a good idea. Functions must have a prototype. I
  added the declaration in the header.
*/
void dl_list_testing_function(StreamMgr_ptr streams);


#endif /* __NUSMV_CORE_UTILS_DLLIST_H__ */
