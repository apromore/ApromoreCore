/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV
  version 2.  Copyright (C) 2012 by FBK-irst.

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
  \brief Private interface of the class BigWordNumber

  The private integeface contains the initialisation
  and deinitialisation of the class BigWordNumber, i.e. the memory manager
  of the class.

*/



#ifndef __NUSMV_CORE_UTILS_BIG_WORD_NUMBER_PRIVATE_H__
#define __NUSMV_CORE_UTILS_BIG_WORD_NUMBER_PRIVATE_H__

#if HAVE_CONFIG_H
#include "nusmv-config.h"
#endif

#if !NUSMV_HAVE_BIGNUMBERS
#error "big number cannot be compiled as it was not configured"
#endif


#include "nusmv/core/utils/WordNumber.h"
#include "nusmv/core/utils/BigWordNumber_private.h"

#include "nusmv/core/utils/utils.h"

#include "nusmv/core/utils/bignumbers/bignumbers.h"
#include "nusmv/core/utils/ustring.h"


/*!
  \brief WordNumberValue_intern struct

  
*/

typedef struct wordnumvale_intern {
  int width;
  Number dat;
} WordNumberValue_intern;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef WordNumberValue_intern* WordNumberValue_intern_ptr;


/*!
  \brief WordNumber struct.

  
*/

/* We do not use the parsedString anymore, a lot of space can be saved */
typedef struct WordNumber_TAG
{
  WordNumberValue_intern value; /* Words are unsigned */
  /* string_ptr parsedString; */
} WordNumber;

/*!
  \brief returns a new WordNumberValue_intern struct, which
                     represents a word with value number and width width

  returns a new WordNumberValue_intern struct, which
                     represents a word with value number and width width
*/
WordNumberValue_intern
WordNumber_create_WordNumberValue_intern(Number, int);

/*!
  \brief returns a new WordNumberValue_intern struct, which
                     represents a copy of the word original

  returns a new WordNumberValue_intern struct, which
                     represents a copy of the word original
*/
WordNumberValue_intern
WordNumber_copy_WordNumberValue_intern(WordNumberValue_intern*);

void WordNumber_free_WordNumberValue_intern(WordNumberValue_intern*);


/* ---------------------------------------------------------------------- */
/*     Private methods                                                    */
/* ---------------------------------------------------------------------- */
char*
WordNumber_Internal_value_to_based_string(WordNumberValue_intern value,
                                          int base, boolean
                                          isSigned);

/*!
  \methodof WordNumber
  \todo
*/
WordNumberValue
word_number_to_signed_c_value(const WordNumber_ptr self);

void
WNV_free_WordNumberValue_intern(WordNumberValue_intern_ptr value);

/*!
  \brief returns a WordNumberValue_intern which represents
                      the unsigned extension of v of size numberOfTimes

  returns a WordNumberValue_intern which represents
                      the unsigned extension of v of size numberOfTimes
*/
WordNumberValue_intern
WordNumber_evaluate_unsigned_extend(WordNumberValue_intern v,
                                    int numberOfTimes);

/*!
  \brief returns a WordNumberValue_intern which represents
                      the signed extension of v of size numberOfTimes

  returns a WordNumberValue_intern which represents
                      the signed extension of v of size numberOfTimes
*/
WordNumberValue_intern
WordNumber_evaluate_signed_extend(WordNumberValue_intern v,
                                  int numberOfTimes);

/*!
  \brief returns a WordNumberValue_intern which represents
                      a right rotation of v applied numberOfBits times

  returns a WordNumberValue_intern which represents
                      a right rotation of v applied numberOfBits times
*/
WordNumberValue_intern
WordNumber_evaluate_right_rotate(WordNumberValue_intern v,
                                 int numberOfBits);

/*!
  \brief returns a WordNumberValue_intern which represents
                      a left rotation of v applied numberOfBits times

  returns a WordNumberValue_intern which represents
                      a left rotation of v applied numberOfBits times
*/
WordNumberValue_intern
WordNumber_evaluate_left_rotate(WordNumberValue_intern v,
                                int numberOfBits);

/*!
  \brief returns a WordNumberValue_intern which represents
                      a left shift of v applied numberOfBits times

  returns a WordNumberValue_intern which represents
                      a left shift of v applied numberOfBits times
*/
WordNumberValue_intern
WordNumber_evaluate_left_shift(WordNumberValue_intern v,
                               int numberOfBits);

/*!
  \brief returns a WordNumberValue_intern which represents
                      a signed right shift of v applied numberOfBits times

  returns a WordNumberValue_intern which represents
                      a signed right shift of v applied numberOfBits times
*/
WordNumberValue_intern
WordNumber_evaluate_sright_shift(WordNumberValue_intern v,
                                 int numberOfBits);

/*!
  \brief returns a WordNumberValue_intern which represents
                      a unsigned right shift of v applied numberOfBits times

  returns a WordNumberValue_intern which represents
                      a unsigned right shift of v applied numberOfBits times
*/
WordNumberValue_intern
WordNumber_evaluate_uright_shift(WordNumberValue_intern v,
                                 int numberOfBits);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the result of the word slice [highbit:lowbit]

  Description        [returns a WordNumberValue_intern which represents
                     the result of the word slice [highbit:lowbit]

  SideEffects        []

  SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  returns a WordNumberValue_intern which represents
                     the result of the word slice [highbit:lowbit]

  SideEffects        []

  SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]

*/
WordNumberValue_intern
WordNumber_evaluate_select(WordNumberValue_intern v1,
                           int highBit,
                           int lowBit);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of an addition of 1 to the bitwise NOT of v

  
*/
WordNumberValue_intern
WordNumber_evaluate_unary_minus(WordNumberValue_intern v);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the result of the concatination v1 :: v2

  returns a WordNumberValue_intern which represents
                     the result of the concatination v1 :: v2
*/
WordNumberValue_intern
WordNumber_evaluate_concat(WordNumberValue_intern v1,
                           WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of a bitwise NOT(v1) OR v2

  returns a WordNumberValue_intern which represents
                     the value of a bitwise NOT(v1) OR v2
*/
WordNumberValue_intern
WordNumber_evaluate_implies(WordNumberValue_intern v1,
                            WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     of a bitwise v1 XNOR v2

  returns a WordNumberValue_intern which represents
                     of a bitwise v1 XNOR v2
*/
WordNumberValue_intern
WordNumber_evaluate_xnor(WordNumberValue_intern v1,
                         WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the the value of a bitwise v1 XOR v2

  returns a WordNumberValue_intern which represents
                     the the value of a bitwise v1 XOR v2
*/
WordNumberValue_intern
WordNumber_evaluate_xor(WordNumberValue_intern v1,
                        WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of a bitwise v1 OR v2

  returns a WordNumberValue_intern which represents
                     the value of a bitwise v1 OR v2
*/
WordNumberValue_intern
WordNumber_evaluate_or(WordNumberValue_intern v1,
                       WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of a bitwise v1 AND v2

  returns a WordNumberValue_intern which represents
                     the value of a bitwise v1 AND v2
*/
WordNumberValue_intern
WordNumber_evaluate_and(WordNumberValue_intern v1,
                        WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                       the value of a signed remainder of v1 by v2.
                       Implemented as:
                       (let ((?msb_s ((_ extract |m-1| |m-1|) s))
                       (?msb_t ((_ extract |m-1| |m-1|) t)))
                       (ite (and (= ?msb_s #b0) (= ?msb_t #b0))
                       (bvurem s t)
                       (ite (and (= ?msb_s #b1) (= ?msb_t #b0))
                       (bvneg (bvurem (bvneg s) t))
                       (ite (and (= ?msb_s #b0) (= ?msb_t #b1))
                       (bvurem s (bvneg t)))
                       (bvneg (bvurem (bvneg s) (bvneg t))))))
                     

  
*/
WordNumberValue_intern
WordNumber_evaluate_srem(WordNumberValue_intern v1,
                         WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of a signed remainder of v1 by v2.
                     Implemented as:
                       (let ((?msb_s ((_ extract |m-1| |m-1|) s))
                       (?msb_t ((_ extract |m-1| |m-1|) t)))
                       (ite (and (= ?msb_s #b0) (= ?msb_t #b0))
                       (bvurem s t)
                       (ite (and (= ?msb_s #b1) (= ?msb_t #b0))
                       (bvneg (bvurem (bvneg s) t))
                       (ite (and (= ?msb_s #b0) (= ?msb_t #b1))
                       (bvurem s (bvneg t)))
                       (bvneg (bvurem (bvneg s) (bvneg t))))))
                      

  
*/
WordNumberValue_intern
WordNumber_evaluate_urem(WordNumberValue_intern v1,
                         WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of a signed division of v1 by v2.
                     Implemented as:
                     (bvsdiv s t) abbreviates
                      (let ((?msb_s ((_ extract |m-1| |m-1|) s))
                            (?msb_t ((_ extract |m-1| |m-1|) t)))
                        (ite (and (= ?msb_s #b0) (= ?msb_t #b0))
                             (bvudiv s t)
                        (ite (and (= ?msb_s #b1) (= ?msb_t #b0))
                             (bvneg (bvudiv (bvneg s) t))
                        (ite (and (= ?msb_s #b0) (= ?msb_t #b1))
                             (bvneg (bvudiv s (bvneg t)))
                             (bvudiv (bvneg s) (bvneg t))))))
                      

  
*/
WordNumberValue_intern
WordNumber_evaluate_sdiv(WordNumberValue_intern v1,
                         WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of a bitwise NOT(v)

  
*/
WordNumberValue_intern
WordNumber_evaluate_not(WordNumberValue_intern v1);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of a unsigned division of v1 by v2.

  
*/
WordNumberValue_intern
WordNumber_evaluate_udiv(WordNumberValue_intern v1,
                         WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of a multiplication v1 by v2, modulo size
                     v2

  
*/
WordNumberValue_intern
WordNumber_evaluate_mul(WordNumberValue_intern v1,
                        WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of a subtraction of v1 by v2, modulo size
                     v2

  
*/
WordNumberValue_intern
WordNumber_evaluate_minus(WordNumberValue_intern v1,
                          WordNumberValue_intern v2);

/*!
  \brief returns a WordNumberValue_intern which represents
                     the value of an addition of v1 by v2, modulo size
                     v2

  
*/
WordNumberValue_intern
WordNumber_evaluate_plus(WordNumberValue_intern v1,
                         WordNumberValue_intern v2);



#endif /* __WORD_NUMBER_PRIVATE_H__ */
