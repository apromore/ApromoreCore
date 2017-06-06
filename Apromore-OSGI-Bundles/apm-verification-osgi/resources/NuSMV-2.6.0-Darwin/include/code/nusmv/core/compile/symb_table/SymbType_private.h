/* ---------------------------------------------------------------------------


  This file is part of the ``compile.symb_table'' package of NuSMV
  version 2.  Copyright (C) 2005 by FBK-irst.

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
  \brief Private interface accessed by class SymbTable

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TYPE_PRIVATE_H__
#define __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TYPE_PRIVATE_H__

#include "nusmv/core/compile/symb_table/SymbType.h"


/* ---------------------------------------------------------------------- */
/*     Private methods                                                    */
/* ---------------------------------------------------------------------- */

/*!
  \methodof SymbType
  \brief Private class SymbType constructor
   for memory sharing type instances

  The difference from the public constructor is that this
   constructor marks the created type as a memory sharing type. As
   result the public constructor will not be able to destroy memory
   sharing instance of a type. Use the private constructor
   SymbType_destroy_memory_sharing_type to destroy such instances.
   

  \se allocate memory

  \sa SymbType_create, SymbType_destroy_memory_sharing_type
*/
SymbType_ptr
SymbType_create_memory_sharing_type(const NuSMVEnv_ptr env,
                                    SymbTypeTag tag, node_ptr body);

/*!
  \methodof SymbType
  \brief Private class SymbType constructor
   for memory sharing array type instances

  The same as SymbType_create_memory_sharing_type
   but can be used to create array types.
   subtype has to be memory shared.

  \se allocate memory

  \sa SymbType_create, SymbType_destroy_memory_sharing_type
*/
SymbType_ptr
SymbType_create_memory_sharing_array_type(SymbType_ptr subtype,
                                          int lower_bound,
                                          int higher_bound);

/*!
  \methodof SymbType
  \brief Private Class SymbType destructor
   for memory sharing instances of types.

  The same as the public destructor SymbType_destroy
   but 'self' has to be created by private constructor
   SymbType_create_memory_sharing_type only.

  \sa SymbType_create_memory_sharing_type, SymbType_create
*/
void SymbType_destroy_memory_sharing_type(SymbType_ptr self);

#endif /* __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TYPE_PRIVATE_H__ */
