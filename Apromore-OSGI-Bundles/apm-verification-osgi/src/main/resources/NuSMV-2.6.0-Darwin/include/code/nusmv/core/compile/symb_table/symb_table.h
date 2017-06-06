/* ---------------------------------------------------------------------------


  This file is part of the ``compile.symb_table'' package of NuSMV version 2.
  Copyright (C) 2005 by FBK-irst.

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
  \brief Public interface of compile.symb_table package

  See symb_table.c file for more description

*/

#ifndef __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TABLE_H__
#define __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TABLE_H__

#include "nusmv/core/compile/symb_table/SymbType.h"
#include "nusmv/core/node/node.h"

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* create simplified types with shared memory */

/*!
  \brief returns a no-type

  This type is a type of correct expressions
  which normally do not have any time.
  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_no_type(const NuSMVEnv_ptr env);

/*!
  \brief returns a no-type

  This type is a type of correct expressions which are
  statements, like assignments, or high-level nodes like TRANS, INIT,
  etc. The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_statement_type(const NuSMVEnv_ptr env);

/*!
  \brief returns a boolean enum type

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_boolean_type(const NuSMVEnv_ptr env);

/*!
  \brief returns a pure symbolic enum type.

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
  Do not access the values contained in the type's body.
*/
SymbType_ptr SymbTablePkg_pure_symbolic_enum_type(const NuSMVEnv_ptr env);

/*!
  \brief returns a enum type containing integers AND symbolic
  constants

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
  Do not access the values contained in the type's body.
*/
SymbType_ptr SymbTablePkg_int_symbolic_enum_type(const NuSMVEnv_ptr env);

/*!
  \brief returns a pure integer enum type

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
  Do not access the values contained in the type's body.
  WARNING [MD] Actually this function is never used. Each pure int enum type is
  converted to integer type. See comment in SymbType_make_memory_shared().
*/
SymbType_ptr SymbTablePkg_pure_int_enum_type(const NuSMVEnv_ptr env);

/*!
  \brief returns an Integer type.

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_integer_type(const NuSMVEnv_ptr env);

/*!
  \brief returns a Real type.

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_real_type(const NuSMVEnv_ptr env);

/*!
  \brief returns a Real type.

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_continuous_type(const NuSMVEnv_ptr env);

/*!
  \brief returns an unsigned Word type (with a given width)

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_unsigned_word_type(const NuSMVEnv_ptr env,
                                                    int width);

/*!
  \brief returns a signed Word type (with a given width)

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_signed_word_type(const NuSMVEnv_ptr env,
                                                  int width);

/*!
  \brief Returns a WordArray type (given array width and subtype)

  The memory is shared, so you can compare pointers to
  compare types. The association is done based on the cons of awidth and subtype.
  De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_wordarray_type(const NuSMVEnv_ptr env,
                                         int awidth, 
                                         SymbType_ptr subtype);

/*!
  \brief returns an array type.

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
  PRECONDITION: subtype has to be created with one of SymbTypePkg_.._type
  function.
*/
SymbType_ptr SymbTablePkg_array_type(SymbType_ptr subtype,
                                            int lower_bound,
                                            int upper_bound);

/*!
  \brief returns an unsigned Word type (with a given width)

  The memory is shared, so you can compare pointers to
   compare types. De-initialisation of the package destroys this type.

*/
SymbType_ptr SymbTablePkg_intarray_type(const NuSMVEnv_ptr env,
                                                      SymbType_ptr subtype);

/*!
  \brief returns a boolean-set type.

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_boolean_set_type(const NuSMVEnv_ptr env);

/*!
  \brief returns a integer-set type.

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_integer_set_type(const NuSMVEnv_ptr env);

/*!
  \brief returns a symbolic-set type.

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_symbolic_set_type(const NuSMVEnv_ptr env);

/*!
  \brief returns a integer-symbolic-set type.

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_integer_symbolic_set_type(const NuSMVEnv_ptr env);

/*!
  \brief returns an Error-type.

  The memory is shared, so you can compare pointers to
  compare types. De-initialisation of the package destroys this type.
*/
SymbType_ptr SymbTablePkg_error_type(const NuSMVEnv_ptr env);


#endif /* __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TABLE_H__ */
