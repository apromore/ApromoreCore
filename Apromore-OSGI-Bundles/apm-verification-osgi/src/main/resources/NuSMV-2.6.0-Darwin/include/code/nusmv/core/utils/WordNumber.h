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
  \brief The header file of WordNumber class.

  This class represent Word arithmetics (which includes
  bitwise and integer arithmetic). This class is created to hide the
  implementation of Word numbers.

  In the sence of memory this class is similar to node_ptr, i.e. no
  need to warry about memory, but class deinitialiser frees memory
  from all Word numbers.
  The class should be initialized and deinitialised after and before node_ptr,
  repspectively.


*/


#ifndef __NUSMV_CORE_UTILS_WORD_NUMBER_H__
#define __NUSMV_CORE_UTILS_WORD_NUMBER_H__


#include "nusmv/core/utils/utils.h"

/*!
  \struct WordNumber
  \brief WordNumber type


*/
typedef struct WordNumber_TAG* WordNumber_ptr;

/*!
  \brief WordNumber integer value


*/
typedef unsigned long long  WordNumberValue;


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define WORD_NUMBER(x)  \
        ((WordNumber_ptr) (x))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define WORD_NUMBER_CHECK_INSTANCE(x)  \
        (nusmv_assert(WORD_NUMBER(x) != WORD_NUMBER(NULL)))

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* Initialisation and de-initialisation of the WordNumber class
   (the manager) */

/*!
  \brief initialiser of the class


*/
void WordNumber_init(NuSMVEnv_ptr env);

/*!
  \brief deinitialiser of the class


*/
void WordNumber_quit(NuSMVEnv_ptr env);


/* implementation limit */

/*!
  \brief The functions returns the maximal width a Word constant
  can have. This is implemenatation-dependent limit


*/
int WordNumber_max_width(void);

/* access function */

/*!
  \brief returns the value of a WordNumber, as unsigned word


*/
unsigned /*!
  \methodof WordNumber
  \todo
*/
long long WordNumber_get_unsigned_value(WordNumber_ptr self);

/*!
  \brief returns the value of a WordNumber, interpreted as a signed
  word


*/
signed /*!
  \methodof WordNumber
  \todo
*/
long long WordNumber_get_signed_value(WordNumber_ptr self);

/*!
  \methodof WordNumber
  \brief returns the width of a WordNumber


*/
int WordNumber_get_width(WordNumber_ptr self);

/*!
  \methodof WordNumber
  \brief returns the status (true or false) of a particular bit

  the bit number should be in the range \[0, width-1\].
*/
boolean WordNumber_get_bit(WordNumber_ptr self, int n);

/*!
  \methodof WordNumber
  \brief returns the status (true or false) of the sign bit


*/
boolean WordNumber_get_sign(WordNumber_ptr self);

/*!
  \methodof WordNumber
  \brief returns a string which was given
  to WordNumber_from_parsed_string constructor. Always returns NULL, this
  method is kept for backwards compability


*/
const char* WordNumber_get_parsed_string(WordNumber_ptr self);

/* output functions */

/*!
  \brief prints a Word constant in a provided base.

  returns negative value in a case of error.
  Only 2, 8, 10, 16 bits bases are allowed.
  If base is 10 then isSigned is taken into account, i.e. if it is true then
  the number is ouput as signed word, and as unsigned word otherwise.

  \sa WordNumber_print
*/
int WordNumber_based_print(FILE* output_stream,
                                  WordNumber_ptr self,
                                  int base,
                                  boolean isSigned);

/*!
  \methodof WordNumber
  \brief prints a Word constant in a provided base to a string.

  This function is the same as WordNumber_based_print,
  except this function outputs to a string, not a stream.
  Only 2, 8, 10, 16 bits bases are allowed.
  If base is 10 then isSigned is taken into account, i.e. if it is true then
  the number is ouput as signed word, and as unsigned word otherwise.
  In case of any problem, NULL is returned.

  Note: The returned string belongs to the funcion. Do not modify this
  string.
  Note: The next invocation of this function or WordNumber_to_string
  makes the previously returned string unusable

  \sa WordNumber_based_print, WordNumber_to_string
*/
char* WordNumber_to_based_string(WordNumber_ptr self, int base,
                                        boolean isSigned);


/* relational operations */

/*!
  \brief Checks wether the word is the constant word of
  all bit set to zero


*/
boolean WordNumber_is_zero(WordNumber_ptr v);

/*!
  \brief returns TRUE if operands are equal

  the width of operands should be equal
*/
boolean WordNumber_equal(WordNumber_ptr v1, WordNumber_ptr v2);

/*!
  \brief returns TRUE if operands are NOT equal

  the width of operands should be equal
*/
boolean WordNumber_not_equal(WordNumber_ptr v1,
                                    WordNumber_ptr v2);

/*!
  \brief returns TRUE if left operand is less than
  the right one (numbers are considered as unsigned)

  the width of operands should be equal
*/
boolean WordNumber_unsigned_less(WordNumber_ptr v1,
                                        WordNumber_ptr v2);

/*!
  \brief returns TRUE if left operand is less than, or equal to,
  the right one (numbers are considered as unsigned)

  the width of operands should be equal
*/
boolean WordNumber_unsigned_less_or_equal(WordNumber_ptr v1,
                                                 WordNumber_ptr v2);

/*!
  \brief returns TRUE if left operand is greater than
  the right one (numbers are considered as unsigned)

  the width of operands should be equal
*/
boolean WordNumber_unsigned_greater(WordNumber_ptr v1,
                                           WordNumber_ptr v2);

/*!
  \brief returns TRUE if left operand is greate than, or eqaul to,
  the right one (numbers are considered as unsigned)

  the width of operands should be equal
*/
boolean WordNumber_unsigned_greater_or_equal(WordNumber_ptr v1,
                                                    WordNumber_ptr v2);

/*!
  \brief returns TRUE if left operand is signed less than
  the right one (numbers are considered as signed)

  the width of operands should be equal
*/
boolean WordNumber_signed_less(WordNumber_ptr v1,
                                      WordNumber_ptr v2);

/*!
  \brief returns TRUE if left operand is signed less than,
  or equal to, the right one (numbers are considered as signed)

  the width of operands should be equal
*/
boolean WordNumber_signed_less_or_equal(WordNumber_ptr v1,
                                               WordNumber_ptr v2);

/*!
  \brief returns TRUE if left operand is signed greater than
  the right one (numbers are considered as signed)

  the width of operands should be equal
*/
boolean WordNumber_signed_greater(WordNumber_ptr v1,
                                         WordNumber_ptr v2);

/*!
  \brief returns TRUE if left operand is signed greate than,
  or eqaul to, the right one (numbers are considered as signed)

  the width of operands should be equal
*/
boolean WordNumber_signed_greater_or_equal(WordNumber_ptr v1,
                                           WordNumber_ptr v2);

/*!
  \brief


*/
WordNumber_ptr WordNumber_max(WordNumber_ptr v1,
                                     WordNumber_ptr v2,
                                     boolean isSigned);

/*!
  \brief


*/
WordNumber_ptr WordNumber_min(WordNumber_ptr v1,
                                     WordNumber_ptr v2,
                                     boolean isSigned);

#endif /* __NUSMV_CORE_UTILS_WORD_NUMBER_H__ */
