/* ---------------------------------------------------------------------------


  This file is part of the ``set'' package of NuSMV version 2.
  Copyright (C) 2000-2001 by FBK-irst.

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
  \brief Generic Set Data Structure

  This package provides an implementation of sets.
  It is possible to perform the test of equality among two sets in
  constant time by simply comparing the two sets. Thus it is possible
  to check if a union has increased the cardinality of a set inserting
  elements in one of the two operands by simply comparing the
  result of the union among the operands.

*/


#ifndef __NUSMV_CORE_SET_SET_H__
#define __NUSMV_CORE_SET_SET_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/node/printers/MasterPrinter.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct Set
  \brief Structure for ordered sets

  Sets are containers for elements that cannot occur into
  them more then once. Sets can be travered through iterators, and
  chronological ordering which elements are inserted into them is
  preserved when travered. A set can be frozen at time t, meaning that
  that the set cannot be later changed until the set is destroyed.
  Freezing a set make that set unchangeble in time, and allows for
  more efficient operations like set copy. Copying a frozen set has
  the only effect of incrementing a reference counting for that set,
  without any need for actually copying the set content. Freezing a
  set whose content does not need to be changed later on is therefore
  always a good idea to make set operations and memory usage more
  efficient.

  It is also important to mark that when storing a set in memory
  (e.g. in memoized operations, like in dependencies hashes) it is
  needed to freeze the set, otherwise external code might change the
  set content with side-effect with weird results, as explained below..

  Operations like AddMember, RemoveMember, Union, Difference, etc. do
  not create a new set, instead they modify the set they are applied
  to. For example given two sets S1 and S2, S1 U S2 (set union) can be
  obtained by calling

  Set_Union(S1, S2)

  If S1 is not a frozen set, the result goes to S1 (with side-effect),
  and no copy is performed. If S1 is frozen, S1 is copied to a new set
  S1' and then side effect is performed on S1' to add members in
  S2. All this operation is carried out automatically in a transparent
  manner, but it is required that operations that modify sets all
  returns a set that can be different from the set they are applied
  to. The returned value has to be assigned to a variable. The right
  set protocol then requires an explicit assignment:

  S1 = Set_Union(S1, S2)

  To save memory the empty set is represented with a NULL pointer,
  that is another reason why an explicit assigment is required, and
  that justify the fact that in general S1 and S1' may be different.

  When a set is no longer used, it has to be freed with method
  ReleaseSet. This either frees the set and the memory it uses, or
  decreases the set's reference counting.

  Reference counting is applied only for frozen sets. When a frozen
  set is copied its reference counting is incremented. When a frozen
  set is released, the reference counting is decremented and the set
  is freed only if its reference counting reaches the value of 0,
  meaning that there are no longer users of that set.

  Notice that in previous operation:

  S1 = Set_Union(S1, S2)

  If S1 is a frozen set, this is the sequence of actions that are
  involved:

  1. S1 is copied into a temporary set S1'
  2. S1' is unioned with S2 (with side-effect on S1')
  3. S1 is released (and possibly freed if needed)
  4. S1' is returned as a new non-frozen set and assigned to S1.

  Pass 3 is remarkable here. Suppose that a set is stored into a
  permanent memory area (like a cache, a hash, etc.). When storing the
  set, it has to be frozen and a carefully reference counting has to
  be takein into account. When looking up previously stored set and
  returning that set (e.g. in memoizing) is is important to return a
  copy of the (frozen) set, and explicitly ask the user to release the
  returned set when no longer used. This prevents previous step 3 to
  release sets that are still in usage for example inside the
  cache. For example:

  Set_t s1 = some_memoizing_function();
  Set_t s2 = Set_AddMember(s1, element);
  ...
  Set_ReleaseSet(s2);

  Here s2 is different from s1 (as s1 is frozen and AddMember would
  change it otherwise). Even if function some_memoizing_function
  requires the user to release returned set, there is no need to
  release s1 (and in fact you do not have to, or you have a bug). This
  allows to write second line Set_t s2 = ... as:

  s1 = Set_AddMember(s1, element);

  At the end you will have only to release s1 (as prescribed by
  function some_memoizing_function)


*/
typedef struct Set_TAG* Set_t;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef node_ptr Set_Element_t;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef ListIter_ptr Set_Iterator_t;

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief use this to iterate over a set


*/
#define SET_FOREACH(set, iter)                               \
   for (iter=Set_GetFirstIter(set); !Set_IsEndIter(iter);    \
        iter=Set_GetNextIter(iter))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SET_T(self) \
  ((Set_t)self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SET_ELEMENT_T(self) \
  ((Set_Element_t)self)


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \brief Create a generic empty set

  This function creates an empty set.
*/
Set_t Set_MakeEmpty(void);

/*!
  \brief Given a list, builds a corresponding set

  Given a list, builds a corresponding set

  \sa Set_MakeSingleton
*/
Set_t Set_Make(node_ptr list);

/*!
  \brief Given an union node, builds a corresponding set

  Given an union node, builds a corresponding set
*/
Set_t Set_MakeFromUnion(NodeMgr_ptr nodemgr, node_ptr _union);

/*!
  \brief Creates a Singleton

  Creates a set with a unique element.
*/
Set_t Set_MakeSingleton(Set_Element_t elem);

/*!
  \brief Returns the independent copy of a set

  If the set was frozen, returned set is equal to the set
  given as input, and its reference counting is incremented. See
  description about the structure Set_t for further information

  \sa Set_MakeSingleton
*/
Set_t Set_Copy(const Set_t set);

/*!
  \brief Frees a set

  Releases the memory associated to the given set. If the
  set was frozen, reference counting is taken into account. See
  description about the structure Set_t for further information
*/
void Set_ReleaseSet(Set_t set);

/*!
  \brief Frees a set of sets

  Assuming that an input set consists of elements each of
  which is also a set this function applies Set_ReleaseSet to the input
  set and every set in it.

  \se Set_ReleaseSet
*/
void Set_ReleaseSetOfSet(Set_t set);


/* Getters and Setters ********************************************************/

/*!
  \brief Set Cardinality

  Computes the cardinality of the given set. Constant time
*/
int Set_GiveCardinality(const Set_t set);

/*!
  \brief Adds a new element to the set

  Add in order (at the end) a new element. Constant time
  if not frozen, linear time if frozen. See description about the
  structure Set_t for further information

  \se If set is not frozen, set is changed internally
*/
Set_t Set_AddMember(Set_t set, Set_Element_t el);

/*!
  \brief Removes the given element from the set, if found

  The new set is returned. Linear time. See
  description about the structure Set_t for further information

  \se If set is not frozen, set is changed internally. If
  after removal set is empty, it is also released.
*/
Set_t Set_RemoveMember(Set_t set, Set_Element_t el);


/* Queries ********************************************************************/

/*!
  \brief Set Emptiness

  Checks for Set Emptiness. Constant time
*/
boolean Set_IsEmpty(const Set_t set);

/*!
  \brief Set memberships

  Checks if the given element is a member of the
  set. It returns <tt>True</tt> if it is a member, <tt>False</tt>
  otherwise. Constant time
*/
boolean Set_IsMember(const Set_t set, Set_Element_t elem);

/*!
  \brief Checks if set1 contains set2

  Returns true iff set2 is a subset of set1. Linear in
  the size of set2
*/
boolean Set_Contains(const Set_t set1, const Set_t set2);

/*!
  \brief Checks if set1 = set2

  Returns true iff set1 contains the same elements of
  set2. Linear in the size of set2
*/
boolean Set_Equals(const Set_t set1, const Set_t set2);

/*!
  \brief Checks set1 and set2 has at least one common element

  Returns true iff set1 contains at least one element from
  set2. Linear in the size of set1
*/
boolean Set_Intersects(const Set_t set1, const Set_t set2);

/*!
  \brief Checks if a set is frozen

  Checks if a set is frozen
*/
boolean Set_IsFrozen(Set_t const set);

/*!
  \brief Checks if the set cointains only one element

  Checks if the set cointains only one element
*/
boolean Set_IsSingleton(Set_t const set);


/* Package handling ***********************************************************/

/*!
  \brief Initializes the set package

  Initializes the set package. See also Set_Quit() to
  deinitialize it
*/
void set_pkg_init(void);

/*!
  \brief De-Initializes the set package

  De-Initializes the set package. Use after Set_init()
*/
void set_pkg_quit(void);


/* Converters *****************************************************************/

/*!
  \brief Given a set, returns the corresponding list

  Given a set, returns a corresponding list. Returned
  list belongs to self and must be NOT freed nor changed by the caller.

  \sa Set_MakeSingleton
*/
NodeList_ptr Set_Set2List(const Set_t set);

/*!
  \brief Returns a UNION expression out of all set elements

  Returns a UNION expression out of all set elements.
  Order of entries is reversed in the returned UNION
*/
node_ptr Set_Set2Union(const Set_t set, NodeMgr_ptr nodemgr);

/*!
  \brief Returns a node list expression out of all set elements

  Returns a node list expression out of all set elements.
  Returned list must be disposed by the caller (free_list).
*/
node_ptr Set_Set2Node(const Set_t set, NodeMgr_ptr nodemgr);


/* Printers *******************************************************************/

/*!
  \brief Prints a set

  Prints a set to the specified file stream. Third
  parameter printer is a callback to be used when printing
  elements. If NULL, elements will be assumed to be node_ptr and
  print_node is called. printer_arg is an optional argument to be
  passed to the printer (can be NULL)
*/
void Set_PrintSet(MasterPrinter_ptr mprinter,
                         FILE *, const Set_t set,
                         void (*printer)(FILE* file,
                                         Set_Element_t el, void* arg),
                         void* printer_arg);


/* SubInterface: Set_Iterator_t ***********************************************/

/*!
  \brief Given an iterator of a set, returns the iterator pointing
  to the next chronological element in that set.

  Returns the next iterator.
  Since sets are ordered, iterating through a set means to traverse
  the elements into the set in the same chronological ordering they
  have been previoulsy added to the set. If a set is changed, any
  previous stored iterator on that set might become invalid.
*/
Set_Iterator_t Set_GetNextIter(Set_Iterator_t iter);

/*!
  \brief Returns true if the set iterator is at the end of the
  iteration


*/
boolean Set_IsEndIter(Set_Iterator_t iter);

/*!
  \brief Provides an iterator to the "first" element of the set

  Returns an iterator to the "first" element of the set.
  Since sets are ordered, iterating through a set means to traverse
  the elements into the set in the same chronological ordering they
  have been previoulsy added to the set. If a set is changed, any
  previous stored iterator on that set might become invalid.
*/
Set_Iterator_t Set_GetFirstIter(Set_t set1);


/* Methods using both Set and Set_Iterator_t **********************************/

/*!
  \brief Returns the element at given iterator


*/
Set_Element_t Set_GetMember(const Set_t set, Set_Iterator_t iter);

/*!
  \brief Returns the rest of a set from a starting point

  Given a set and an iterator within that set, returns a
  new set containing all the elements that are found in to the input
  set from the iterator to then end of the set. Returned set must be
  disposed by the caller.

  WARNING!! Deprecated method. This method is provided only for
  backward compatibility and should be no longer used in new code.
*/
Set_t Set_GetRest(const Set_t set, Set_Iterator_t from);


/* Miscellaneous **************************************************************/

/*!
  \brief Freezes a set

  Use when a set has to be memoized
  or stored in memory permanently. When frozen, a set content is
  frozen, meaning that no change can occur later on this set. If the
  set is tried to be changed later, a new copy will be created and
  changes will be applied to that copy. When a frozen set is copied, a
  reference counter is incremented and the same instance is returned
  in constant time. When a frozen set is destroyed, it is destroyed
  only when its ref counter reaches 0 (meaning it is the very last
  instance of that set). Set is also returned for a functional
  flavour. See description about the structure Set_t for
  further information

  \se set is changed internally if not already frozen
*/
Set_t Set_Freeze(Set_t set);

/*!
  \brief Adds all new elements found in list

  Add in order (at the end) all new elements. Linear
  time in the size of list if not frozen, linear time in size of
  set + size of list if frozen. See description about the structure
  Set_t for further information

  \se If set is not frozen, set is changed internally
*/
Set_t Set_AddMembersFromList(Set_t set, const NodeList_ptr list);

/*!
  \brief Set Union

  Computes the Union of two sets. If set1 is not frozen,
  set1 is changed by adding members of set2. If set1 is frozen, it is
  before copied into a new set.

  If set1 is not frozen, complexity is linear in the cardinality od
  set2, otherwise it is linear in the cardinality(set1) +
  cardinality(set2)

  See description about the structure Set_t for further information

  \se If set is not frozen, set is changed internally.
*/
Set_t Set_Union(Set_t set1, const Set_t set2);

/*!
  \brief Set intersection

  Computes the Set intersection. Linear time on the
  cardinality of set1+set2. See description about the structure Set_t for
  further information

  \se If set1 is not frozen, set1 is changed internally. If
  after intersection set1 is empty, it is also released and the empty
  set is returned.
*/
Set_t Set_Intersection(Set_t set1, const Set_t set2);

/*!
  \brief Set Difference

  Computes the Set Difference. Linear time on the
  cardinality of set1. See description about the structure Set_t for
  further information

  \se If set1 is not frozen, set1 is changed internally. If
  after difference set1 is empty, it is also released and the empty
  set is returned.
*/
Set_t Set_Difference(Set_t set1, const Set_t set2);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_SET_SET_H__ */
