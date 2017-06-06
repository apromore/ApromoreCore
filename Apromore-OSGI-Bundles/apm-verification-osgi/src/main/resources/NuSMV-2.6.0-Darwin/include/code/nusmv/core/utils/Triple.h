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
  \brief Public interface of class 'Triple'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_TRIPLE_H__
#define __NUSMV_CORE_UTILS_TRIPLE_H__


#include "nusmv/core/utils/utils.h"

/*!
  \struct Triple
  \brief Definition of the public accessor for class Triple

  
*/
typedef struct Triple_TAG*  Triple_ptr;


/*!
  \brief Triple class definition

  
*/

typedef struct Triple_TAG
{
  void* first;
  void* second;
  void* third;
  boolean frozen;
} Triple;

/*!
  \brief To cast and check instances of class Triple

  These macros must be used respectively to cast and to check
  instances of class Triple
*/
#define TRIPLE(self) \
         ((Triple_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRIPLE_CHECK_INSTANCE(self) \
         (nusmv_assert(TRIPLE(self) != TRIPLE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof Triple
  \brief The Triple class constructor

  The Triple class constructor

  \sa Triple_destroy
*/
Triple_ptr Triple_create(void* first,
                                void* second,
                                void* third);

/*!
  \methodof Triple
  \brief The Triple class initializer

  The Triple class initializer.  Use this function if
                      declaring a Triple in the stack 

  \sa Triple_create
*/
void Triple_init(Triple_ptr self, void* first,
                        void* second, void* third);

/*!
  \methodof Triple
  \brief Mark the Triple instance as read-only

  Mark the Triple instance as read-only.
                      This is usefull when debugging, and using a Triple
                      instance as key of an hash table, for example
*/
void Triple_freeze(Triple_ptr self);

/*!
  \methodof Triple
  \brief Check if the Triple is freezed

  Check if the Triple is freezed (i.e. it is
                      read-only)
*/
boolean Triple_is_freezed(const Triple_ptr self);

/*!
  \methodof Triple
  \brief Get the first value of the Triple instance

  Get the first value of the Triple instance
*/
void* Triple_get_first(const Triple_ptr self);

/*!
  \methodof Triple
  \brief Get the second value of the Triple instance

  Get the second value of the Triple instance
*/
void* Triple_get_second(const Triple_ptr self);

/*!
  \methodof Triple
  \brief Get the third value of the Triple instance

  Get the third value of the Triple instance
*/
void* Triple_get_third(const Triple_ptr self);

/*!
  \methodof Triple
  \brief Sets the first value for the Triple instance.

  Sets the first value for the Triple instance.
                      The Triple must not be frozen
*/
void Triple_set_first(Triple_ptr self, void* first);

/*!
  \methodof Triple
  \brief Sets the second value for the Triple instance

  Sets the second value for the Triple instance.
                      The Triple must not be frozen
*/
void Triple_set_second(Triple_ptr self, void* second);

/*!
  \methodof Triple
  \brief Sets the third value for the Triple instance

  Sets the third value for the Triple instance.
                      The Triple must not be frozen
*/
void Triple_set_third(Triple_ptr self, void* third);

/*!
  \methodof Triple
  \brief Sets both the values for the Triple instance

  Sets both the values for the Triple instance.
                      The Triple must not be frozen
*/
void Triple_set_values(Triple_ptr self, void* first,
                              void* second, void* third);

/*!
  \methodof Triple
  \brief The Triple class destructor

  The Triple class destructor

  \sa Triple_create
*/
void Triple_destroy(Triple_ptr self);

/*!
  \brief Triple comparison function

  Triple comparison function.
                      Returns if the two Triple instances are the
                      equal.  No distinction between frozen / unfrozen
                      instances is made.
                      Can be casted to ST_PFICPCP

                      Casts to char* are added to prevent "warning: pointer of
                      type ‘void *’ used in subtraction".
*/
int Triple_compare(const Triple_ptr a,
                          const Triple_ptr b);

/*!
  \methodof Triple
  \brief Triple hash function

  Triple hash function.
                      No distinction between frozen / unfrozen
                      instances is made.
                      Can be casted to ST_PFICPI
*/
unsigned long Triple_hash(const Triple_ptr self, int size);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_TRIPLE_H__ */
