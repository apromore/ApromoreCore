/* ---------------------------------------------------------------------------


  This file is part of the ``utils/bigwordnumbers'' package of NuSMV version 2.
  Copyright (C) 2012 by FBK-irst.

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
  \author \todo: Missing author
  \brief General header to include when using infinite length numbers

  This file contains the methods which NuSMV's infinite length
  API supports

*/


#ifndef __NUSMV_CORE_UTILS_BIGNUMBERS_BIGNUMBERS_H__

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define __NUSMV_CORE_UTILS_BIGNUMBERS_BIGNUMBERS_H__

#include "nusmv/core/utils/utils.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief 

  Represents a infinite lenght / infinite precsion number
                      to be used in NuSMV
*/


typedef struct {
  void *repr;
} Number;

/*---------------------------------------------------------------------------*/
/* Stucture declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief returns the 1's complement of n

  returns the 1's complement of n
*/
Number BigNumber_bit_complement(Number* n);

/*!
  \brief returns the 2's complement of the given number,
                      using the given number of bits

  returns the 2's complement of the given number,
                      using the given number of bits
*/
Number BigNumber_twos_complement(Number* n, int width);

/*!
  \brief returns the bitwise and of left and right

  returns the bitwise and of left and right
*/
Number BigNumber_bit_and(Number* left, Number* right);

/*!
  \brief returns the bitwise or of left and right

  returns the bitwise or of left and right
*/
Number BigNumber_bit_or(Number* left, Number* right);

/*!
  \brief returns the bitwise xor of left and right

  returns the bitwise xor of left and right
*/
Number BigNumber_bit_xor(Number* left, Number* right);

/*!
  \brief returns the result of a leftwise shift of amount

  returns the result of a leftwise shift of amount
*/
Number BigNumber_bit_left_shift(Number* number, int amount);

/*!
  \brief returns the result of a rightwise shift of amount

  returns the result of a rightwise shift of amount
*/
Number BigNumber_bit_right_shift(Number* number, int amount);

/*!
  \brief returns 2^n.

  returns 2^n.
*/
Number BigNumber_pow2(int widht);

/*!
  \brief returns 2^n - 1.

  returns 2^n - 1.
*/
Number BigNumber_max_unsigned_int(int widht);

/*!
  \brief returns 1 if number fits in an unsigned long long.

  returns 1 if number fits in an unsigned long long.

  \se if 1 is returned, target is equal to the value of
                      number.
*/
boolean
BigNumber_to_unsigned_long_long(Number* number,
                                unsigned long long* target);

/*!
  \brief sets the given bit of the binary representation of the
                      number to the given value

  sets the given bit of the binary representation of the
                      number to the given value
*/
void BigNumber_set_bit(Number* number,
                              int location, int value);

/*!
  \brief returns 1 if the given bit of the binary representation
                      of the number is set, 0 otherwise

  returns 1 if the given bit of the binary representation
                      of the number is set, 0 otherwise
*/
boolean BigNumber_test_bit(Number* number, int location);

/*!
  \brief returns a new Number of value n

  returns a new Number of value n
*/
Number
BigNumber_make_number_from_unsigned_long_long(unsigned long long n);

/*!
  \brief returns a number of value n.

  returns a number of value n.
*/
Number BigNumber_make_number_from_long(long n);

/*!
  \brief returns 1 if string represents a number in base base

  returns 1 if string represents a number in base base

  \se if 1 is returned, numb is set to the value of string
*/
int BigNumber_assign_number_from_string(char* string,
                                               char* error_char,
                                               int base,
                                               Number* value);

/*!
  \brief returns a new Number of value left * right

  returns a new Number of value left * right
*/
Number BigNumber_multiplication(Number* left, Number* right);

/*!
  \brief returns a new Number of value left + right

  returns a new Number of value left + right
*/
Number BigNumber_plus(Number* left, Number* right);

/*!
  \brief returns a new Number of value left - right

  returns a new Number of value left - right
*/
Number BigNumber_minus(Number* left, Number* right);

/*!
  \brief returns a new Number of value orig

  returns a new Number of value orig
*/
Number BigNumber_copy(Number* orig);

/*!
  \brief returns 1 if left < right 0, otherwise

  returns 1 if left < right 0, otherwise
*/
boolean BigNumber_less_than(Number* left, Number* right);

/*!
  \brief returns 1 if left == right 0, otherwise

  returns 1 if left == right 0, otherwise
*/
boolean BigNumber_equal(Number* left, Number* right);

/*!
  \brief returns 1 if &left == &right 0, otherwise

  returns 1 if &left == &right 0, otherwise
*/
boolean BigNumber_identity(Number* left, Number* right);

/*!
  \brief returns 1 if value can be expressed in width bits

  returns 1 if value can be expressed in width bits
*/
boolean
BigNumber_does_integer_fit_into_number_of_bits(Number* v, int widht);

/*!
  \brief Performs a division of left by right and stores the
                     results in the locations of q & r

  

  \se q is set to left /(int) right, r is set to left % right
*/
void
BigNumber_divmod(Number* left, Number* right, Number* q, Number* r);

/*!
  \brief Frees the heap space occupied by number

  Frees the heap space occupied by number
*/
void BigNumber_free_number(Number* number);

/*!
  \brief Returns a string repr of number in base base, must be
                     freed by caller

  Returns a string repr of number in base base
*/
char*  BigNumber_print_as_number(Number* number, int base);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_UTILS_BIGNUMBERS_BIGNUMBERS_H__ */
