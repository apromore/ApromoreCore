/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
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
  \brief Public interface of class 'Pair'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_PAIR_H__
#define __NUSMV_CORE_UTILS_PAIR_H__


#include "nusmv/core/utils/utils.h"

/*!
  \struct Pair
  \brief Definition of the public accessor for class Pair

  
*/
typedef struct Pair_TAG*  Pair_ptr;

/*!
  \brief To cast and check instances of class Pair

  These macros must be used respectively to cast and to check
  instances of class Pair
*/
#define PAIR(self) \
         ((Pair_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PAIR_CHECK_INSTANCE(self) \
         (nusmv_assert(PAIR(self) != PAIR(NULL)))


/*!
  \brief Pair class definition

  
*/

typedef struct Pair_TAG
{
  void* first;
  void* second;
  boolean frozen;
} Pair;


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof Pair
  \brief The Pair class constructor

  The Pair class constructor

  \sa Pair_destroy
*/
Pair_ptr Pair_create(void* first, void* second);

/*!
  \methodof Pair
  \brief The Pair class initializer

  The Pair class initializer.  Use this function if
                      declaring a Pair in the stack 

  \sa Pair_create
*/
void Pair_init(Pair_ptr self, void* first, void* second);

/*!
  \methodof Pair
  \brief Mark the Pair instance as read-only

  Mark the Pair instance as read-only.
                      This is usefull when debugging, and using a Pair
                      instance as key of an hash table, for example
*/
void Pair_freeze(Pair_ptr self);

/*!
  \methodof Pair
  \brief Check if the Pair is freezed

  Check if the Pair is freezed (i.e. it is
                      read-only)
*/
boolean Pair_is_freezed(const Pair_ptr self);

/*!
  \methodof Pair
  \brief Get the first value of the Pair instance

  Get the first value of the Pair instance
*/
void* Pair_get_first(const Pair_ptr self);

/*!
  \methodof Pair
  \brief Get the second value of the Pair instance

  Get the second value of the Pair instance
*/
void* Pair_get_second(const Pair_ptr self);

/*!
  \methodof Pair
  \brief Sets the first value for the Pair instance.

  Sets the first value for the Pair instance.
                      The Pair must not be frozen
*/
void Pair_set_first(Pair_ptr self, void* first);

/*!
  \methodof Pair
  \brief Sets the second value for the Pair instance

  Sets the second value for the Pair instance.
                      The Pair must not be frozen
*/
void Pair_set_second(Pair_ptr self, void* second);

/*!
  \methodof Pair
  \brief Sets both the values for the Pair instance

  Sets both the values for the Pair instance.
                      The Pair must not be frozen
*/
void Pair_set_values(Pair_ptr self,
                            void* first, void* second);

/*!
  \methodof Pair
  \brief The Pair class destructor

  The Pair class destructor

  \sa Pair_create
*/
void Pair_destroy(Pair_ptr self);

/*!
  \brief Pair comparison function

  Pair comparison function.
                      Returns if the two Pair instances are the
                      equal.  No distinction between frozen / unfrozen
                      instances is made.
                      Can be casted to ST_PFICPCP.

                      Casts to char* are added to prevent "warning: pointer of
                      type ‘void *’ used in subtraction".
*/
int Pair_compare(const Pair_ptr a,
                        const Pair_ptr b);

/*!
  \methodof Pair
  \brief Pair hash function

  Pair hash function.
                      No distinction between frozen / unfrozen
                      instances is made.
                      Can be casted to ST_PFICPI
*/
unsigned long Pair_hash(const Pair_ptr self, int size);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_PAIR_H__ */
