/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 2011 by FBK.

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
  \brief Public interface for a Stack class

  See Stack.c for the description.

*/

#ifndef __NUSMV_CORE_UTILS_STACK_H__
#define __NUSMV_CORE_UTILS_STACK_H__

#include "nusmv/core/utils/defs.h"

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/
/*!
  \brief Implementation of Stack class

  
*/

struct Stack_TAG {
  size_t allocated;
  size_t index;

  void** array;
};

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct Stack
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct Stack_TAG Stack;
typedef struct Stack_TAG* Stack_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define STACK(x) \
         ((Stack_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define STACK_CHECK_INSTANCE(x) \
         ( nusmv_assert(STACK(x) != STACK(NULL)) )

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define STACK_TOP(self) *(self->array + self->index - 1)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define STACK_IS_EMPTY(self) (self->index == 0)

/* ---------------------------------------------------------------------- */
/* Public interface                                                       */
/* ---------------------------------------------------------------------- */

/*!
  \methodof Stack
  \brief Creates an instance of a Stack 

  
*/
Stack_ptr Stack_create(void);

/*!
  \methodof Stack
  \brief Creates an instance of a Stack 

  Allow the user to define the initial size
*/
Stack_ptr Stack_create_with_param(int size);

/*!
  \methodof Stack
  \brief Destroys a stack instance

  The memory used by the Stack will be freed.
  Note: memory occupied by the elements is not freed! It is the user
  responsibility.
*/
void Stack_destroy(Stack_ptr self);

/*!
  \methodof Stack
  \brief Creates a copy of a given stack

  
*/
Stack_ptr Stack_copy(Stack_ptr self);

/*!
  \methodof Stack
  \brief Pushes an element at the top of the stack

  

  \sa Stack_pop
*/
void Stack_push(Stack_ptr self, void* element);

/*!
  \methodof Stack
  \brief Removes the element at the top of the stack

  The removed element is returned.

  \sa Stack_push
*/
void* Stack_pop(Stack_ptr self);

/*!
  \methodof Stack
  \brief Returns the element at the top of the stack

  

  \sa Stack_pop
*/
void* Stack_top(Stack_ptr self);

/*!
  \methodof Stack
  \brief Returns true iff the stack is empty

  
*/
boolean Stack_is_empty(Stack_ptr self);

/*!
  \methodof Stack
  \brief Returns the size of the stack

  
*/
size_t Stack_get_size(Stack_ptr self);

#endif /* __S_LIST_H__ */
