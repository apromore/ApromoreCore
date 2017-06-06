/* ---------------------------------------------------------------------------


  This file is part of the ``enc.utils'' package of NuSMV version 2. 
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
  \brief The header file of AddArray class.

  This class represent an array of ADD. 
  The class is used to internally represent Word exressions during 
  encoding in enc/bdd/BddEnc module.
  Actually, all other the expressions are also represented with AddArray,
  but such array always have only one element.

  NB: some construction and destruction of AddArray can reference or
  de-reference its ADDs. Take care about it.
  

*/


#ifndef __NUSMV_CORE_ENC_UTILS_ADD_ARRAY_H__
#define __NUSMV_CORE_ENC_UTILS_ADD_ARRAY_H__

#include "nusmv/core/dd/DDMgr.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/utils/array.h"
#include "nusmv/core/utils/WordNumber.h"


/*---------------------------------------------------------------------------*/
/* Types                                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \struct AddArray
  \brief AddArray type

  
*/
typedef struct AddArray_TAG* AddArray_ptr;

/*---------------------------------------------------------------------------*/
/* Macros                                                                    */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ADD_ARRAY(x)  \
        ((AddArray_ptr) (x))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ADD_ARRAY_CHECK_INSTANCE(x)  \
        (nusmv_assert(ADD_ARRAY(x) != ADD_ARRAY(NULL)))

/*---------------------------------------------------------------------------*/
/* Definition of external funcions                                           */
/*---------------------------------------------------------------------------*/

/* constructor-converters */

/*!
  \methodof AddArray
  \brief constructor. Create an array of "number" ADDs

  number must be positive. The index of the
  array goes from 0 to (number - 1).
*/
AddArray_ptr AddArray_create(int number);

/*!
  \brief destructor of the class

  The memory will be freed and all ADD will be
  de-referenced
*/
void AddArray_destroy(DDMgr_ptr dd, AddArray_ptr self);

/*!
  \brief Creates a new AddArray from given WordNumber

  Returned add array has the same width as the given word
  number
*/
AddArray_ptr 
AddArray_from_word_number(DDMgr_ptr dd, WordNumber_ptr wn);

/*!
  \brief given an ADD create an AddArray consisting of
  one element

  Given ADD must already be referenced.
*/
AddArray_ptr AddArray_from_add(add_ptr add);

/*!
  \methodof AddArray
  \brief create a new AddArray, a copy of a given one

  During duplication all ADD will be referenced.
*/
AddArray_ptr AddArray_duplicate(AddArray_ptr self);

/* access function */

/*!
  \methodof AddArray
  \brief returns the size (number of elements) of the array

  
*/
int AddArray_get_size(AddArray_ptr self);

/*!
  \methodof AddArray
  \brief Returns the sum of the sizes of the ADDs within self

  
*/
size_t AddArray_get_add_size(const AddArray_ptr self, 
                                    DDMgr_ptr dd);

/*!
  \methodof AddArray
  \brief This function returns the first element of
  the array

  The array should contain exactly one element
*/
add_ptr AddArray_get_add(AddArray_ptr self);

/*!
  \methodof AddArray
  \brief Returns the element number "n" from
  the array.

  "n" can be from 0 to (size-1).
  The returned ADD is NOT referenced.
*/
add_ptr AddArray_get_n(AddArray_ptr self, int number);

/*!
  \methodof AddArray
  \brief Sets the element number "number" to "add".

  The given ADD "add" must already be referenced.
  The previous value should already be de-referenced if it is necessary.
*/
void AddArray_set_n(AddArray_ptr self, int number, 
                           add_ptr add);

/*!
  \methodof AddArray
  \brief Returns the AddArray represented as an array of ADDs.

  Do not change the returned array, which belongs to self
*/
array_t* AddArray_get_array(AddArray_ptr self);

/* --------------------------------------------------------- */
/* auxiliary arithmetic functions */
/* --------------------------------------------------------- */

/* arithemtic Word operations */

/*!
  \brief Applies unary operator to each bit of given argument, and
  returns resulting add array

  Returned AddArray must be destroyed by the caller
*/
AddArray_ptr 
AddArray_word_apply_unary(DDMgr_ptr dd,
                          AddArray_ptr arg1,
                          FP_A_DA op);

/*!
  \brief Applies binary operator to each bits pair of given
  arguments, and returns resulting add array

  Returned AddArray must be destroyed by the caller
*/
AddArray_ptr 
AddArray_word_apply_binary(DDMgr_ptr dd,
                           AddArray_ptr arg1,
                           AddArray_ptr arg2, 
                           FP_A_DAA op);

/*!
  \brief Returns an ADD that is the disjunction of all bits of arg

  Returned ADD is referenced
*/
add_ptr 
AddArray_make_disjunction(DDMgr_ptr dd,
                          AddArray_ptr arg);

/*!
  \brief Returns an ADD that is the conjunction of all bits of arg

  Returned ADD is referenced
*/
add_ptr 
AddArray_make_conjunction(DDMgr_ptr dd,
                          AddArray_ptr arg);

/*!
  \brief Perform the addition operations
  on two Word expressions represented as an array of ADD.
  Every ADD corresponds to a bit of a Word expression

  The size of both arguments should be the same.
*/
AddArray_ptr AddArray_word_plus(DDMgr_ptr dd,
                                       AddArray_ptr arg1,
                                       AddArray_ptr arg2);

/*!
  \brief Perform the subtraction operations
  on two Word expressions represented as an array of ADD.
  Every ADD corresponds to a bit of a Word expression

  The size of both arguments should be the same.
*/
AddArray_ptr AddArray_word_minus(DDMgr_ptr dd,
                                        AddArray_ptr arg1,
                                        AddArray_ptr arg2);

/*!
  \brief Changes the sign of the given word.

  The return expression is equal to (0 - arg) 
*/
AddArray_ptr AddArray_word_unary_minus(DDMgr_ptr dd, 
                                              AddArray_ptr arg);

/*!
  \brief Perform the multiplication operations
  on two Word expressions represented as an array of ADD.
  Every ADD corresponds to a bit of a Word expression

  The size of both arguments should be the same.
*/
AddArray_ptr AddArray_word_times(DDMgr_ptr dd,
                                        AddArray_ptr arg1,
                                        AddArray_ptr arg2);

/*!
  \brief Perform the division operations
  on two unsigned Word expressions represented as an array of ADD.
  Every ADD corresponds to a bit of a Word expression

  The size of both arguments should be the same.
*/
AddArray_ptr AddArray_word_unsigned_divide(DDMgr_ptr dd,
                                         AddArray_ptr arg1,
                                         AddArray_ptr arg2);

/*!
  \brief Perform the remainder operations
  on two unsigned Word expressions represented as an array of ADD.
  Every ADD corresponds to a bit of a Word expression

  The size of both arguments should be the same.
*/
AddArray_ptr AddArray_word_unsigned_mod(DDMgr_ptr dd,
                                      AddArray_ptr arg1,
                                      AddArray_ptr arg2);

/*!
  \brief Perform the division operations
  on two singed Word expressions represented as an array of ADD.
  Every ADD corresponds to a bit of a Word expression

  The size of both arguments should be the same.
*/
AddArray_ptr AddArray_word_signed_divide(DDMgr_ptr dd,
                                                AddArray_ptr arg1,
                                                AddArray_ptr arg2);

/*!
  \brief Perform the remainder operations
  on two signed Word expressions represented as an array of ADD.
  Every ADD corresponds to a bit of a Word expression

  The size of both arguments should be the same.
*/
AddArray_ptr AddArray_word_signed_mod(DDMgr_ptr dd,
                                             AddArray_ptr arg1,
                                             AddArray_ptr arg2);

/*!
  \brief Performs left shift operations
  on a Word expression represented as an array of ADD.
  Every ADD corresponds to a bit of a Word expression

  The "number" argument represent
  the number of bits to shift. "number" can be a usual integer (and
  consist of one ADD) or be an unsigned word (and consist of many ADDs).
  NB: The invoker should destroy the returned array.

  NB for developers:
  Every i-th bit of returned array will be:
       ITE(number=0 , arg[i],
           ITE(number=1, arg[i-1],
            ...
             ITE(number=i, arg[0],
              ITE(number >=0 && number <= width, zero, FAILURE)
   Does anyone know a better encoding?

*/
AddArray_ptr AddArray_word_left_shift(DDMgr_ptr dd,
                                             AddArray_ptr arg,
                                             AddArray_ptr number);

/*!
  \brief Invokes add_array_word_right_shift with isSigned set to
  false

  See add_array_word_right_shift.

  \sa add_array_word_right_shift
*/
AddArray_ptr AddArray_word_unsigned_right_shift(DDMgr_ptr dd,
                                              AddArray_ptr arg,
                                              AddArray_ptr number);

/*!
  \brief Invokes add_array_word_right_shift with isSigned set to
  true

  See add_array_word_right_shift.

  \sa add_array_word_right_shift
*/
AddArray_ptr AddArray_word_signed_right_shift(DDMgr_ptr dd,
                                              AddArray_ptr arg,
                                              AddArray_ptr number);

/*!
  \brief Performs left rotate operations
  on a Word expression represented as an array of ADD.
  Every ADD corresponds to a bit of a Word expression

  The "number" argument represent
  the number of bits to rotate. "number" should have only one ADD.
  NB: The invoker should destroy the returned array.

  NB for developers:
  Every i-th bit  of returned array will be:
       ITE(number=0 , arg[i],
           ITE(number=1, arg[i-1],
            ...
             ITE(number=i, arg[0],
              ITE(number=i+1, arg[width-1],
               ...
               ITE(number=width-1, arg[i+1],
                ITE(number=width, arg[i], FAILURE
*/
AddArray_ptr AddArray_word_left_rotate(DDMgr_ptr dd,
                                              AddArray_ptr arg,
                                              AddArray_ptr number);

/*!
  \brief Performs right rotate operations
  on a Word expression represented as an array of ADD.
  Every ADD corresponds to a bit of a Word expression

  The "number" argument represent
  the number of bits to rotate. "number" should have only one ADD.
  NB: The invoker should destroy the returned array.

  NB for developers:
  Every i-th bit of returned array will be:
       ITE(number=0 , arg[i],
           ITE(number=1, arg[i+1],
            ...
             ITE(number=width-1-i, arg[width-1],
              ITE(number=width-2-i, arg[0],
               ...
               ITE(number=width-1, arg[i-1],
                ITE(number=width, arg[i], FAILURE
*/
AddArray_ptr AddArray_word_right_rotate(DDMgr_ptr dd,
                                               AddArray_ptr arg,
                                               AddArray_ptr number);

/*!
  \brief Performs less-then operation
  on two unsigned Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  the size of arguments should be the same
  The returned array will constain only one (boolean) ADD.
  NB: The invoker should destroy the returned array.

*/
AddArray_ptr AddArray_word_unsigned_less(DDMgr_ptr dd,
                                               AddArray_ptr arg1,
                                               AddArray_ptr arg2);

/*!
  \brief Performs less-or-equal operation
  on two unsigned Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  the size of arguments should be the same
  The returned array will constain only one (boolean) ADD.
  NB: The invoker should destroy the returned array.
  
*/
AddArray_ptr AddArray_word_unsigned_less_equal(DDMgr_ptr dd,
                                                     AddArray_ptr arg1,
                                                     AddArray_ptr arg2);

/*!
  \brief Performs greater-then operation
  on two unsigned Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  the size of arguments should be the same
  The returned array will constain only one (boolean) ADD.
  NB: The invoker should destroy the returned array.

*/
AddArray_ptr AddArray_word_unsigned_greater(DDMgr_ptr dd,
                                                  AddArray_ptr arg1,
                                                  AddArray_ptr arg2);

/*!
  \brief Performs greater-or-equal operation
  on two unsigned Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  the size of arguments should be the same
  The returned array will constain only one (boolean) ADD.
  NB: The invoker should destroy the returned array.

*/
AddArray_ptr AddArray_word_unsigned_greater_equal(DDMgr_ptr dd,
                                                        AddArray_ptr arg1,
                                                        AddArray_ptr arg2);

/*!
  \brief Performs _signed_ less-then operation
  on two Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  the size of arguments should be the same
  The returned array will constain only one (boolean) ADD.
  NB: The invoker should destroy the returned array.
*/
AddArray_ptr AddArray_word_signed_less(DDMgr_ptr dd, 
                                              AddArray_ptr arg1,
                                              AddArray_ptr arg2);

/*!
  \brief Performs _signed_ less-equal-then operation
  on two Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  the size of arguments should be the same
  The returned array will constain only one (boolean) ADD.
  NB: The invoker should destroy the returned array.
*/
AddArray_ptr AddArray_word_signed_less_equal(DDMgr_ptr dd, 
                                                    AddArray_ptr arg1,
                                                    AddArray_ptr arg2);

/*!
  \brief Performs _signed_ greater-then operation
  on two Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  the size of arguments should be the same
  The returned array will constain only one (boolean) ADD.
  NB: The invoker should destroy the returned array.
*/
AddArray_ptr AddArray_word_signed_greater(DDMgr_ptr dd, 
                                                 AddArray_ptr arg1,
                                                 AddArray_ptr arg2);

/*!
  \brief Performs _signed_ greater-equal-then operation
  on two Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  the size of arguments should be the same
  The returned array will constain only one (boolean) ADD.
  NB: The invoker should destroy the returned array.
*/
AddArray_ptr AddArray_word_signed_greater_equal(DDMgr_ptr dd, 
                                                       AddArray_ptr arg1, 
                                                       AddArray_ptr arg2);

/*!
  \brief Extends the width of a signed Word expression keeping
  the value of the expression

  This extension means that the sign (highest) bit
  is added 'arg_repeat' times on the left.
  'arg_repeat' has to be a constant number.
  

  \sa AddArray_word_extend
*/
AddArray_ptr 
AddArray_word_signed_extend(DDMgr_ptr dd,
                          AddArray_ptr arg, AddArray_ptr repeat);

/*!
  \brief Extends the width of an unsigned Word expression keeping
  the value of the expression

  This extension means that the zero bit
  is added 'arg_repeat' times on the left.
  'arg_repeat' has to be a constant number.
  

  \sa AddArray_word_signed_extend
*/
AddArray_ptr 
AddArray_word_unsigned_extend(DDMgr_ptr dd,
                              AddArray_ptr arg, AddArray_ptr repeat);

/*!
  \brief Performs signed resize operation on a Word expression
  represented as arrays of ADD.  Every ADD corresponds to a bit of a
  Word expression

  See note 3136 in issue #1787 for full description of
  signed resize semantics. "new_size" must be ADD leafs with a NUMBER
  node.NB: The invoker should destroy the returned array.

  \sa AddArray_word_unsigned_resize
*/
AddArray_ptr
AddArray_word_signed_resize(DDMgr_ptr dd,
                          AddArray_ptr arg, AddArray_ptr new_size);

/*!
  \brief Performs signed resize operation on a Word expression
  represented as arrays of ADD.  Every ADD corresponds to a bit of a
  Word expression

  See note 3136 in issue #1787 for full description of
  signed resize semantics. "new_size" must be ADD leafs with a NUMBER
  node.NB: The invoker should destroy the returned array.

  \sa AddArray_word_signed_resize
*/
AddArray_ptr
AddArray_word_unsigned_resize(DDMgr_ptr dd,
                              AddArray_ptr arg, AddArray_ptr new_size);

/*!
  \brief Performs equal-operation
  on two Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  the size of arguments should be the same
  The returned array will constain only one (boolean) ADD.
  NB: The invoker should destroy the returned array.
  
*/
AddArray_ptr AddArray_word_equal(DDMgr_ptr dd,
                                        AddArray_ptr arg1,
                                        AddArray_ptr arg2);

/*!
  \brief Performs not-equal-operation
  on two Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  the size of arguments should be the same
  The returned array will constain only one (boolean) ADD.
  NB: The invoker should destroy the returned array.
  
*/
AddArray_ptr AddArray_word_not_equal(DDMgr_ptr dd,
                                            AddArray_ptr arg1,
                                            AddArray_ptr arg2);

/*!
  \brief Creates a ITE word array:
  {ITE(_if, _then[N-1], _else[N-1]),
   ITE(_if, _then[N-2], _else[N-2]),
   ...
   ITE(_if, _then[1], _else[1]),
   ITE(_if, _then[0], _else[0])}

  If _else consist of 1 ADD but _then does not then the same _else[0] is used
  in all ITE. (this is used to pass FAILURE ADD). Otherwise size of _then and _else
  have to be the same.
  _if has to be of 1 bit width.
  Returned array width is as large as _then.

  The invoker should destroy the returned array.
*/
AddArray_ptr AddArray_word_ite(DDMgr_ptr dd,
                                      AddArray_ptr _if,
                                      AddArray_ptr _then, 
                                      AddArray_ptr _else);

/*!
  \brief Performs bit-selection operation
  on a Word expression represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  The high-bit and low-bit of selections
  are specified by "range". "range" must
  be ADD leafs with a RANGE node (holding two integer constants,
  and these constant must be in the range [width-1, 0]).
  NB: The invoker should destroy the returned array.
  
*/
AddArray_ptr AddArray_word_bit_selection(DDMgr_ptr dd,
                                                AddArray_ptr word,
                                                AddArray_ptr range);

/*!
  \brief Performs concatenation operation
  on two Word expressions represented as arrays of ADD.
  Every ADD corresponds to a bit of a Word expression

  
  NB: The invoker should destroy the returned array.
  
*/
AddArray_ptr AddArray_word_concatenation(DDMgr_ptr dd,
                                                AddArray_ptr arg1,
                                                AddArray_ptr arg2);

#endif /* __NUSMV_CORE_ENC_UTILS_ADD_ARRAY_H__ */
