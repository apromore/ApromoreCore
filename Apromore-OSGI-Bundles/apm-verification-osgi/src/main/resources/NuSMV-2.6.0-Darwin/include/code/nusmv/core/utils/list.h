/*
 * Revision Control Information
 *
 * /projects/hsis/CVS/utilities/list/list.h,v
 * shiple
 * 1.5
 * 1995/08/30 17:37:15
 *
 */
/*
 * List Management Package Header File
 *
 * David Harrison
 * University of California, 1985
 *
 * This file contains public type definitions for the List Managment
 * package implemented in list.c.  This is stand alone package.
 */

#ifndef __NUSMV_CORE_UTILS_LIST_H__
#define __NUSMV_CORE_UTILS_LIST_H__

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LS_DEFINED

#if HAVE_CONFIG_H
#  include "nusmv-config.h"
#endif


/* This can be typedef'ed to void if supported */
typedef struct ls_dummy_defn {
    int dummy;                  /* Not used */
} ls_dummy;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef ls_dummy *lsList;       /* List handle           */
typedef ls_dummy *lsGen;        /* List generator handle */
typedef ls_dummy *lsHandle;     /* Handle to an item     */

/*!
  \brief Used for return codes.
*/
typedef int lsStatus;

/*!
  \brief Arbitrary pointer to data
*/
typedef void *lsGeneric;

/*!
  \brief Nil for lsList
*/
#define LS_NIL          0

/*!
  \brief Bad generator state
*/
#define LS_BADSTATE     -3

/*!
  \brief Bad parameter value
*/
#define LS_BADPARAM     -2

/*!
  \brief No more items
*/
#define LS_NOMORE       -1

/*!
  \brief Succeeded operation
*/
#define LS_OK           0

/*!
  \brief Set spot before object
*/
#define LS_BEFORE       1

/*!
  \brief Set spot after object
*/
#define LS_AFTER        2

/*!
  \brief Stop generating items
*/
#define LS_STOP         3

/*!
  \brief Delete generated item
*/
#define LS_DELETE       4

/*!
  \brief For all those routines that take a handle, this macro can be
  used when no handle is required.
*/
#define LS_NH           (lsHandle *) 0


typedef lsGeneric (*LS_PFLSG)(lsGeneric);

/*!
  \brief Used for LS_PFLSG to copy a list
*/
#define LS_COPY         (LS_PFLSG) (-1)

/*!
  \brief Create a new list

  Creates a new linked list and returns its handle.  The handle is
  used by all other list manipulation routines and should not be
  discarded.
*/
lsList lsCreate(void);

/*!
  \brief Create a new list containing one element

  Create a new linked list containing one element and return its
  handle. If 'itemHandle' is non-zero, it will be filled with a
  handle which can be used to generate a generator positioned at the
  item without generating through the list.
*/
lsList lsSingleton(lsGeneric, lsHandle *);

/*!
  \brief Delete a previously created list

 Frees all resources associated with the specified list.  It frees memory
 associated with all elements of the list and then deletes the list.
 User data is released by calling 'delFunc' with the pointer as the
 argument.  Accessing a list after its destruction is a no-no.
*/
lsStatus lsDestroy(lsList, void (*)(lsGeneric));

/*!
  \brief Delete a previously created list of lists of ground
*/
lsStatus lsDestroyListList(lsList);

/*!
  \brief Copies the contents of a list

  Returns a copy of list `list'.  If `copyFunc' is non-zero,
  it will be called for each item in `list' and the pointer it
  returns will be used in place of the original user data for the
  item in the newly created list.  The form of `copyFunc' should be:
    lsGeneric copyFunc(lsGeneric data)

  This is normally used to make copies of the user data in the new list.
  If no `copyFunc' is provided,  an identity function is used.
*/
lsList lsCopy(lsList, LS_PFLSG);

/*!
  \brief Copies the contents of a list of lists of ground
*/
lsList lsCopyListList(lsList);

/*!
  \brief Append two lists
*/
lsList lsAppend(lsList, lsList, LS_PFLSG);

/*!
  \brief Append second list to first list
*/
void lsJoin(lsList, lsList, LS_PFLSG);

/*!
  \brief Gets the first item of a list

 Returns the first item in the list.  If the list is empty,
 it returns LS_NOMORE.  Otherwise,  it returns LS_OK.
 If 'itemHandle' is non-zero,  it will be filled with a
 handle which may be used to generate a generator.
*/
lsStatus lsFirstItem(lsList, lsGeneric *, lsHandle *);

/*!
  \brief Gets the last item of a list

 Returns the last item of a list.  If the list is empty,
 the routine returns LS_NOMORE.  Otherwise,  'data' will
 be set to the last item and the routine will return LS_OK.
 If 'itemHandle' is non-zero,  it will be filled with a
 handle which can be used to generate a generator postioned
 at this item.
*/
lsStatus lsLastItem(lsList, lsGeneric *, lsHandle *);

/*!
  \brief Add item to start of list

 Adds a new item to the start of a previously created linked list.
 If 'itemHandle' is non-zero,  it will be filled with a handle
 which can be used to generate a generator positioned at the
 item without generating through the list.
*/
lsStatus lsNewBegin(lsList, lsGeneric, lsHandle *);

/*!
  \brief Add item to end of list

 Adds a new item to the end of a previously created linked list.
 This routine appends the item in constant time and
 can be used freely without guilt.
*/
lsStatus lsNewEnd(lsList, lsGeneric, lsHandle *);

/*!
  \brief Delete first item of a list

 This routine deletes the first item of a list.  The user
 data associated with the item is returned so the caller
 may dispose of it.  Returns LS_NOMORE if there is no
 item to delete.
*/
lsStatus lsDelBegin(lsList, lsGeneric *);

/*!
  \brief Delete last item of a list

 This routine deletes the last item of a list.  The user
 data associated with the item is returned so the caller
 may dispose of it.  Returns LS_NOMORE if there is nothing
 to delete.
*/
lsStatus lsDelEnd(lsList, lsGeneric *);

/*!
  \brief Returns the length of the list

 Returns the length of the list.  The list must have been
 already created using lsCreate or lsSingleton.
*/
int lsLength(lsList);

/*!
  \brief Begin generation of items in a list

 This routine defines a generator which is used to step through each
 item of the list. It returns a generator handle which should be used
 when calling lsNext, lsPrev, lsInBefore, lsInAfter, lsDelete, or
 lsFinish.
*/
lsGen lsStart(lsList);

/*!
  \brief Begin generation at end of list

 This routine defines a generator which is used to step through
 each item of a list.  The generator is initialized to the end
 of the list.
*/
lsGen lsEnd(lsList);

/*!
  \brief Produces a generator given a handle

 This routine produces a generator given a handle.  Handles are
 produced whenever an item is added to a list.  The generator produced
 by this routine may be used when calling any of the standard
 generation routines.

 NOTE: the generator should be freed using lsFinish.  The 'option'
 parameter determines whether the generator spot is before or after
 the handle item.
*/
lsGen lsGenHandle(lsHandle, lsGeneric *, int);

/*!
  \brief Generate next item in sequence

 Generates the item after the item previously generated by lsNext or
 lsPrev.  It returns a pointer to the user data structure in 'data'.
 'itemHandle' may be used to get a generation handle without
 generating through the list to find the item.  If there are no more
 elements to generate, the routine returns LS_NOMORE (normally it
 returns LS_OK).

 lsNext DOES NOT automatically clean up after all elements have been
 generated. lsFinish must be called explicitly to do this.

 \sa lsFinish
*/
lsStatus lsNext(lsGen, lsGeneric *, lsHandle *);

/*!
  \brief Generate previous item in sequence

 Generates the item before the item previously generated by lsNext or
 lsPrev. It returns a pointer to the user data structure in 'data'.
 'itemHandle' may be used to get a generation handle without
 generating through the list to find the item.  If there are no more
 elements to generate, the routine returns LS_NOMORE (normally it
 returns LS_OK).

 lsPrev DOES NOT automatically clean up after all elements have been
 generated.  lsFinish must be called explicitly to do this.

 \sa lsFinish
*/
lsStatus lsPrev(lsGen, lsGeneric *, lsHandle *);

/*!
  \brief Insert an item before the most recently generated by lsNext

 Inserts an element BEFORE the current spot.  The item generated by
 lsNext will be unchanged; the inserted item will be generated by
 lsPrev.  This modifies the list.  'itemHandle' may be used at a later
 time to produce a generation handle without generating through the
 list.
*/
lsStatus lsInBefore(lsGen, lsGeneric, lsHandle *);

/*!
  \brief Insert an item after the most recently generated by lsNext

 Inserts an element AFTER the current spot.  The next item generated
 by lsNext will be the new element.  The next item generated by lsPrev
 is unchanged.  This modifies the list.  'itemHandle' may be used at a
 later time to generate a generation handle without searching through
 the list to find the item.
*/
lsStatus lsInAfter(lsGen, lsGeneric, lsHandle *);

/*!
  \brief Delete the item before the current spot

 Removes the item before the current spot.  The next call to lsPrev
 will return the item before the deleted item.  The next call to
 lsNext will be uneffected.  This modifies the list.  The routine
 returns LS_BADSTATE if the user tries to call the routine and there
 is no item before the current spot.  This routine returns the
 userData of the deleted item so it may be freed (if necessary).
*/
lsStatus lsDelBefore(lsGen, lsGeneric *);

/*!
  \brief Delete the item after the current spot

 Removes the item after the current spot.  The next call to lsNext
 will return the item after the deleted item.  The next call to lsPrev
 will be uneffected.  This modifies the list.  The routine returns
 LS_BADSTATE if the user tries to call the routine and there is no
 item after the current spot.  This routine returns the userData of
 the deleted item so it may be freed (if necessary).
*/
lsStatus lsDelAfter(lsGen, lsGeneric *);

/*!
  \brief End generation of items in a list

 Marks the completion of a generation of list items.

 This routine should be called after calls to lsNext to free resources
 used by the generator.  This rule applies even if all items of a list
 are generated by lsNext.
*/
lsStatus lsFinish(lsGen);

/*!
  \brief Returns the list of a handle

 This routine returns the associated list of the specified handle.
 Returns 0 if there were problems.
*/
lsList lsQueryHandle(lsHandle);

/*!
  \brief Returns data associated with handle

 This routine returns the user data of the item associated with
 `itemHandle'.
*/
lsGeneric lsFetchHandle(lsHandle);

/*!
  \brief Removes item associated with handle from list

 This routine removes the item associated with `handle' from its list
 and returns the user data associated with the item for reclaimation
 purposes.  Note this modifies the list that originally contained
 `item'.
*/
lsStatus lsRemoveItem(lsHandle, lsGeneric *);

/*!
  \brief Sorts a list

  This routine sorts `list' using `compare' as the comparison
  function between items in the list.  `compare' has the following form:
    int compare(lsGeneric item1, lsGeneric item2)

  The routine should return -1 if item1 is less than item2, 0 if
  they are equal,  and 1 if item1 is greater than item2.

  The routine uses a generic merge sort written by Rick Rudell.
*/
lsStatus lsSort(lsList, int (*compare)(char*, char*));

/*!
  \brief Removes duplicates from a sorted list

 This routine takes a sorted list and removes all duplicates
 from it.  `compare' has the following form:

   int compare(lsGeneric item1, lsGeneric item2)

 The routine should return -1 if item1 is less than item2, 0 if
 they are equal,  and 1 if item1 is greater than item2. `delFunc'
 will be called with a pointer to a user data item for each
 duplicate destroyed.  `delFunc' can be zero if no clean up
 is required.
*/
lsStatus lsUniq(lsList, int (*compare)(char*, char*), void (*delFunc)(lsGeneric));

/*!
  \brief Calls given function to all element of the list

 This routine generates all items in `list' from the first item
  to the last calling `userFunc' for each item.

  The function should have the following form:
    lsStatus userFunc(lsGeneric data, lsGeneric arg)

  `data' will be the user data associated with the item generated.
  `arg' will be the same pointer provided to lsForeach.  The
  routine should return LS_OK to continue the generation,  LS_STOP
  to stop generating items,  and LS_DELETE to delete the item
  from the list.  If the generation was stopped prematurely,
  the routine will return LS_STOP.  If the user provided function
  does not return an appropriate value,  the routine will return
  LS_BADPARAM.
*/
lsStatus lsForeach(lsList list,
                   lsStatus (*userFunc)(lsGeneric, lsGeneric),
                   lsGeneric arg);

/*!
  \brief Like lsForeach, but in reversed order.

  This routine is just like lsForeach except it generates
  all items in `list' from the last item to the first.

  \sa lsForeach
*/
lsStatus lsBackeach(lsList list,
                    lsStatus (*userFunc)(lsGeneric, lsGeneric),
                    lsGeneric arg);

/*!
  \brief Macro for iteration.

  Macro to iterate the items of a list. Note the following:
  1) in a for loop, the test is evaluate before the first time through the body
  2) the logical OR operator guarantees left to right evaluation, and the second
     operand is not evaluated if first operand evaluates to non-zero
  3) the comma operator returns the value of its second argument.

*/
#define lsForEachItem(                                         \
  list,  /* lsList, list to iterate */                         \
  gen,   /* lsGen, local variable for iterator */              \
  data   /* lsGeneric, variable to return data */              \
)                                                              \
  for(gen = lsStart(list);                                     \
      (lsNext(gen, (lsGeneric *) &data, LS_NH) == LS_OK)       \
      || ((void) lsFinish(gen), 0);                            \
      )


#endif /* __NUSMV_CORE_UTILS_LIST_H__ */
