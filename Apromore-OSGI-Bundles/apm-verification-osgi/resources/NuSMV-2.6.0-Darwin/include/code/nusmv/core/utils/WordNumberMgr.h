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
  \brief Public interface of class 'WordNumberMgr'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_WORD_NUMBER_MGR_H__
#define __NUSMV_CORE_UTILS_WORD_NUMBER_MGR_H__


#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/WordNumber.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

#if defined(_MSC_VER)
# include "nusmv/core/utils/portability.h"  /* for strtoull */
#endif

/*!
  \struct WordNumberMgr
  \brief Definition of the public accessor for class WordNumberMgr


*/
typedef struct WordNumberMgr_TAG*  WordNumberMgr_ptr;

/*!
  \brief To cast and check instances of class WordNumberMgr

  These macros must be used respectively to cast and to check
   instances of class WordNumberMgr
*/
#define WORD_NUMBER_MGR(self)                   \
  ((WordNumberMgr_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define WORD_NUMBER_MGR_CHECK_INSTANCE(self)                            \
  (nusmv_assert(WORD_NUMBER_MGR(self) != WORD_NUMBER_MGR(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof WordNumberMgr
  \brief The WordNumberMgr class constructor

  The WordNumberMgr class constructor

  \sa WordNumberMgr_destroy
*/
WordNumberMgr_ptr WordNumberMgr_create(const NuSMVEnv_ptr env);

/*!
  \methodof WordNumberMgr
  \brief The WordNumberMgr class destructor

  The WordNumberMgr class destructor

  \sa WordNumberMgr_create
*/
void WordNumberMgr_destroy(WordNumberMgr_ptr self);

/*!
  \brief The functions returns the maximal width a Word constant
  can have. This is implemenatation-dependent limit


*/
int WordNumberMgr_max_width(void);

/*!
  \methodof WordNumberMgr
  \brief returns a maximal value of unsigned word of given width
                      if unsigned long longs are used to store words


*/
WordNumber_ptr
WordNumberMgr_max_unsigned_value(WordNumberMgr_ptr self, int width);

/*!
  \methodof WordNumberMgr
  \brief returns a maximal value of signed word of given width.
                      if unsigned long longs are used to store words


*/
WordNumber_ptr
WordNumberMgr_max_signed_value(WordNumberMgr_ptr self, int width);

/*!
  \methodof WordNumberMgr
  \brief returns a minimal value of signed word of given width.
                      if unsigned long longs are used to store words


*/
WordNumber_ptr
WordNumberMgr_min_signed_value(WordNumberMgr_ptr self, int width);

/*!
  \methodof WordNumberMgr
  \brief Constructs a Word number WordNumber_ptr from the string
  representation

  The string and base should be proper for standard
  "strtoull" function.  The base can be 2, 8 or 16.  In the case of
  any problem NULL is returned.

  Note: base 10 is not allowed, because it does not provide enough info
  about the width of the Word number.

  NOTE: Memory sharing is used, i.e. given a string with the same
  value of WordNumber this constructor will return the same pointer
  (this is important for node_ptr hashing)
*/
WordNumber_ptr
WordNumberMgr_string_to_word_number(WordNumberMgr_ptr self,
                                    char* str, int base);

/*!
  \methodof WordNumberMgr
  \brief Constructs a Word number WordNumber_ptr from the string
  representation

  The string and base should be proper for standard
  "he base can be 2, 8, 10 or 16. The number should be in the range supposed
  by the width. The provided width of the constant should be enough to hold
  the obtained number. In the case of any problem NULL is returned.

  NOTE: Memory sharing is used, i.e. given a string with the same
  value of WordNumber this constructor will return the same pointer
  (this is important for node_ptr hashing)
*/
WordNumber_ptr
WordNumberMgr_sized_string_to_word_number(WordNumberMgr_ptr self,
                                          char* str, int base,
                                          int width);

/*!
  \methodof WordNumberMgr
  \brief Constructs a Word number WordNumber_ptr from the string
  representation obtained during parsing

  The string is the string obtained during parsing. The
  string should correspond to the NuSMV lexer token "word constants",
  i.e. "0" character followed by the base, optional signed specifier,
  optional width (decimal number), "_" character and the value
  (binary, octal, decimal or hexadecimal number).  The base and the
  digits should correspond each other.

  The limit for width is implementation dependant.
  In the case of any problem NULL is returned, and if errorString is not NULL,
  it is set to a text string explaining the cause of the error.
  The returned error-string belongs to this function (it may change during next
  function invocation).

  NOTE: Unlike the non Big WordNumberMgr_parsed_string_to_word_number
  this constructor IS memory shared
*/
WordNumber_ptr
WordNumberMgr_parsed_string_to_word_number(WordNumberMgr_ptr self,
                                           char* str,
                                           char** errorString);

/*!
  \methodof WordNumberMgr
  \brief returns a WordNumber

  value and width should be correct, i.e. in a proper
  range. See WordNumber_from_signed_integer if original value is signed.

  NOTE: Memory sharing is used, i.e. given the same parameter this
  constructor will return the same pointer (this is important for
  node_ptr hashing)

  \sa WordNumber_from_signed_integer
*/
WordNumber_ptr
WordNumberMgr_integer_to_word_number(WordNumberMgr_ptr self,
                                     WordNumberValue value,
                                     int width);

/*!
  \methodof WordNumberMgr
  \brief returns a WordNumber

   This constructor is the same as
  WordNumber_from_integer except than value is interpreted as signed
  value casted to WordNumberValue.

  The difference is that signed negative value casted to WordNumberValue
  will have 1s at positions greater than width. These bits are ignored
  but in WordNumber_from_integer they cause assertion violation.

  For originally positive values both constructors behave the same.


  \sa WordNumber_from_integer
*/
WordNumber_ptr
WordNumberMgr_signed_integer_to_word_number(WordNumberMgr_ptr self,
                                            WordNumberValue value,
                                            int width);

/*!
  \methodof WordNumberMgr
  \brief returns a memory shared WordNumber

  \todo Missing description
*/
WordNumber_ptr
WordNumberMgr_normalize_word_number(WordNumberMgr_ptr self,
                                    WordNumber_ptr v);

/*!
  \methodof WordNumberMgr
  \brief perform the negation operation


*/
WordNumber_ptr
WordNumberMgr_unary_minus(WordNumberMgr_ptr self,
                          WordNumber_ptr v);

/*!
  \methodof WordNumberMgr
  \brief perform summation operation

  the width of operands should be equal
*/
WordNumber_ptr
WordNumberMgr_plus(WordNumberMgr_ptr self,
                   WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief perform subtraction operation on Words

  the width of operands should be equal
*/
WordNumber_ptr
WordNumberMgr_minus(WordNumberMgr_ptr self,
                    WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief perform multiplidation operation on Words

  the width of operands should be equal
*/
WordNumber_ptr
WordNumberMgr_times(WordNumberMgr_ptr self,
                    WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief perform unsigned division operation on Words

  the width of operands should be equal. The
  right operand should not be 0.
*/
WordNumber_ptr
WordNumberMgr_unsigned_divide(WordNumberMgr_ptr self,
                              WordNumber_ptr v1,
                              WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief perform signed division operation on Words

  the width of operands should be equal. The
  right operand should not be 0
*/
WordNumber_ptr
WordNumberMgr_signed_divide(WordNumberMgr_ptr self,
                            WordNumber_ptr v1,  WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief perform remainder unsigned operation on Words

  the width of operands should be equal. The right
  operand should not be 0.
  Note: numbers are considered as unsigned.
*/
WordNumber_ptr
WordNumberMgr_unsigned_mod(WordNumberMgr_ptr self,
                           WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief perform remainder signed operation on Words

  the width of operands should be equal. The right
  operand should not be 0
*/
WordNumber_ptr
WordNumberMgr_signed_mod(WordNumberMgr_ptr self,
                         WordNumber_ptr v1,  WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief returns bitwise NOT of a Word number


*/
WordNumber_ptr
WordNumberMgr_not(WordNumberMgr_ptr self,
                  WordNumber_ptr v);

/*!
  \methodof WordNumberMgr
  \brief returns bitwise AND of two Word numbers

  the width of operands should be equal
*/
WordNumber_ptr
WordNumberMgr_and(WordNumberMgr_ptr self,
                  WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief returns bitwise OR of two Word numbers

  the width of operands should be equal
*/
WordNumber_ptr
WordNumberMgr_or(WordNumberMgr_ptr self,
                 WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief returns bitwise XOR of two Word numbers

  the width of operands should be equal
*/
WordNumber_ptr
WordNumberMgr_xor(WordNumberMgr_ptr self,
                  WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief returns bitwise XNOR(or IFF) of two Word numbers

  the width of operands should be equal
*/
WordNumber_ptr
WordNumberMgr_xnor(WordNumberMgr_ptr self,
                   WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief returns bitwise IMPLIES of two Word numbers

  the width of operands should be equal
*/
WordNumber_ptr
WordNumberMgr_implies(WordNumberMgr_ptr self,
                      WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief returns bitwise IFF(or XNOR) of two Word numbers

  the width of operands should be equal
*/
WordNumber_ptr
WordNumberMgr_iff(WordNumberMgr_ptr self,
                  WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief returns a concatenation of two Word numbers


*/
WordNumber_ptr
WordNumberMgr_concatenate(WordNumberMgr_ptr self,
                          WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \methodof WordNumberMgr
  \brief returns a Word number consisting of the
  bits [highBit .. lowBit] from a given Word number

  highBit should be less than the Word width and greater or
  equal to lowBit. lowBit should be greater or equal to 0.
*/
WordNumber_ptr
WordNumberMgr_bit_select(WordNumberMgr_ptr self,
                         WordNumber_ptr v, int highBit, int lowBit);

/*!
  \methodof WordNumberMgr
  \brief perform right shift on a Word numbers

  the number of shifted bits should be in the range
  \[0, width\]. The word is padded with zeros.
*/
WordNumber_ptr
WordNumberMgr_unsigned_right_shift(WordNumberMgr_ptr self,
                                   WordNumber_ptr v, int numberOfBits);

/*!
  \methodof WordNumberMgr
  \brief perform right shift on a Word numbers

  the number of shifted bits should be in the range
  \[0, width\]. The word is padded with zeros.
*/
WordNumber_ptr
WordNumberMgr_signed_right_shift(WordNumberMgr_ptr self,
                                 WordNumber_ptr v, int numberOfBits);

/*!
  \methodof WordNumberMgr
  \brief perform left shift on a Word numbers

  the number of shifted bits should be in the range
  \[0, width\]. The word is padded with zeros.
*/
WordNumber_ptr
WordNumberMgr_left_shift(WordNumberMgr_ptr self,
                         WordNumber_ptr v, int numberOfBits);

/*!
  \methodof WordNumberMgr
  \brief perform right rotate on a Word numbers

  the number of rotated bits should be in the range
  \[0, width\].
*/
WordNumber_ptr
WordNumberMgr_right_rotate(WordNumberMgr_ptr self,
                           WordNumber_ptr v, int numberOfBits);

/*!
  \methodof WordNumberMgr
  \brief perform left rotate on a Word numbers

  the number of rotated bits should be in the range
  \[0, width\].
*/
WordNumber_ptr
WordNumberMgr_left_rotate(WordNumberMgr_ptr self,
                          WordNumber_ptr v, int numberOfBits);

/*!
  \methodof WordNumberMgr
  \brief performs sign extend, i.e. concatenates 'numberOfTimes'
  number of times the highest bit of v with v.


*/
WordNumber_ptr
WordNumberMgr_signed_extend(WordNumberMgr_ptr self,
                            WordNumber_ptr v, int numberOfTimes);

/*!
  \methodof WordNumberMgr
  \brief performs unsign extend


*/
WordNumber_ptr
WordNumberMgr_unsigned_extend(WordNumberMgr_ptr self,
                              WordNumber_ptr v, int numberOfTimes);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_WORD_NUMBER_MGR_H__ */
