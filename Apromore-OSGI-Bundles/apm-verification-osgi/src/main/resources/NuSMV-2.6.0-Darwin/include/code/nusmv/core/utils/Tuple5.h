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
  \brief Public interface of class 'Tuple5'

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_UTILS_TUPLE5_H__
#define __NUSMV_CORE_UTILS_TUPLE5_H__

#include "nusmv/core/utils/utils.h"

/*!
  \struct Tuple5
  \brief Definition of the public accessor for class Tuple5

  
*/
typedef struct Tuple5_TAG*  Tuple5_ptr;


/*!
  \brief Tuple5 class definition

  
*/

typedef struct Tuple5_TAG
{
  void* first;
  void* second;
  void* third;
  void* forth;
  void* fifth;
  boolean frozen;
} Tuple5;

/*!
  \brief To cast and check instances of class Tuple5

  These macros must be used respectively to cast and to check
  instances of class Tuple5
*/
#define TUPLE_5(self) \
         ((Tuple5_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TUPLE_5_CHECK_INSTANCE(self) \
         (nusmv_assert(TUPLE_5(self) != TUPLE_5(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof Tuple5
  \brief The Tuple5 class constructor

  The Tuple5 class constructor

  \sa Tuple5_destroy
*/
Tuple5_ptr Tuple5_create(void* first, void* second, void* third,
                                void* forth, void* fifth);

/*!
  \methodof Tuple5
  \brief The Tuple5 class initializer

  The Tuple5 class initializer.  Use this function if
                      declaring a Tuple5 in the stack 

  \sa Tuple5_create
*/
void Tuple5_init(Tuple5_ptr self, void* first, void* second,
                        void* third, void* forth, void* fifth);

/*!
  \methodof Tuple5
  \brief Mark the Tuple5 instance as read-only

  Mark the Tuple5 instance as read-only.
                      This is useful when debugging, and using a Tuple5
                      instance as key of an hash table, for example
*/
void Tuple5_freeze(Tuple5_ptr self);

/*!
  \methodof Tuple5
  \brief Check if the Tuple5 is freezed

  Check if the Tuple5 is freezed (i.e. it is
                      read-only)
*/
boolean Tuple5_is_freezed(const Tuple5_ptr self);

/*!
  \methodof Tuple5
  \brief Get the first value of the Tuple5 instance

  Get the first value of the Tuple5 instance
*/
void* Tuple5_get_first(const Tuple5_ptr self);

/*!
  \methodof Tuple5
  \brief Get the second value of the Tuple5 instance

  Get the second value of the Tuple5 instance
*/
void* Tuple5_get_second(const Tuple5_ptr self);

/*!
  \methodof Tuple5
  \brief Get the third value of the Tuple5 instance

  Get the third value of the Tuple5 instance
*/
void* Tuple5_get_third(const Tuple5_ptr self);

/*!
  \methodof Tuple5
  \brief Get the forth value of the Tuple5 instance

  Get the forth value of the Tuple5 instance
*/
void* Tuple5_get_forth(const Tuple5_ptr self);

/*!
  \methodof Tuple5
  \brief Get the fifth value of the Tuple5 instance

  Get the fifth value of the Tuple5 instance
*/
void* Tuple5_get_fifth(const Tuple5_ptr self);

/*!
  \methodof Tuple5
  \brief Sets the first value for the Tuple5 instance.

  Sets the first value for the Tuple5 instance.
                      The Tuple5 must not be frozen
*/
void Tuple5_set_first(Tuple5_ptr self, void* first);

/*!
  \methodof Tuple5
  \brief Sets the second value for the Tuple5 instance

  Sets the second value for the Tuple5 instance.
                      The Tuple5 must not be frozen
*/
void Tuple5_set_second(Tuple5_ptr self, void* second);

/*!
  \methodof Tuple5
  \brief Sets the third value for the Tuple5 instance

  Sets the third value for the Tuple5 instance.
                      The Tuple5 must not be frozen
*/
void Tuple5_set_third(Tuple5_ptr self, void* third);

/*!
  \methodof Tuple5
  \brief Sets the forth value for the Tuple5 instance

  Sets the forth value for the Tuple5 instance.
                      The Tuple5 must not be frozen
*/
void Tuple5_set_forth(Tuple5_ptr self, void* forth);

/*!
  \methodof Tuple5
  \brief Sets the fifth value for the Tuple5 instance

  Sets the fifth value for the Tuple5 instance.
                      The Tuple5 must not be frozen
*/
void Tuple5_set_fifth(Tuple5_ptr self, void* fifth);

/*!
  \methodof Tuple5
  \brief Sets both the values for the Tuple5 instance

  Sets both the values for the Tuple5 instance.
                      The Tuple5 must not be frozen
*/
void Tuple5_set_values(Tuple5_ptr self, void* first, void* second,
                              void* third, void* forth, void* fifth);

/*!
  \methodof Tuple5
  \brief The Tuple5 class destructor

  The Tuple5 class destructor

  \sa Tuple5_create
*/
void Tuple5_destroy(Tuple5_ptr self);

/*!
  \brief Tuple5 comparison function

  Tuple5 comparison function.
                      Returns if the two Tuple5 instances are the
                      equal.  No distinction between frozen / unfrozen
                      instances is made.
                      Can be casted to ST_PFICPCP

                      Casts to char* are added to prevent "warning: pointer of
                      type ‘void *’ used in subtraction".
*/
int Tuple5_compare(const Tuple5_ptr a,
                          const Tuple5_ptr b);

/*!
  \methodof Tuple5
  \brief Tuple5 hash function

  Tuple5 hash function.
                      No distinction between frozen / unfrozen
                      instances is made.
                      Can be casted to ST_PFICPI
*/
unsigned long Tuple5_hash(const Tuple5_ptr self, int size);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_TUPLE5_H__ */
