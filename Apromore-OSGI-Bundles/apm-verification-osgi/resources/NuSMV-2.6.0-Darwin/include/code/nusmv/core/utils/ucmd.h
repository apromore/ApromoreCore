/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 1998-2001 by CMU and FBK-irst.

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
  \author Roberto Cavada
  \brief Header part of ucmd.c

  This file contains useful structures, macros and functions to
  be used in command line processing

*/


#ifndef __NUSMV_CORE_UTILS_UCMD_H__
#define __NUSMV_CORE_UTILS_UCMD_H__

#include <stdlib.h> /* for strtol */
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/opt/opt.h" /* for OPT_USER_POV_NULL_STRING */


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum { sv_string, sv_integer, sv_floating, sv_pointer, sv_undef }
  SubstValueType;


/*!
  \brief SubstValue a service structure used by SubstString. Ignore.

  

  \sa SubstString
*/

typedef struct SubstValue_TAG
{
  SubstValueType type;
  union
  {
    const char* string;
    int    integer;
    double floating;
    void*  pointer;
  } assign;
} SubstValue;


/*!
  \brief SubstString is the structure passed to the function
  apply_string_macro_expansion

  For your comfort you can use the SYMBOL_CREATE and
  SYMBOL_ASSIGN macros in order to easily fill all fields of this data
  structure

  \sa SYMBOL_CREATE, SYMBOL_ASSIGN, apply_string_macro_expansion
*/

typedef struct SubstString_TAG
{
  const char* symbol;
  SubstValue value;
  const char* format;
} SubstString;


/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief SYMBOL_CREATE helps the static allocation of a new symbol
  usable with the function apply_string_macro_expansion

  The macro parameter is the symbol string which will be
  searched for substitution in the function apply_string_macro_expansion 

  \sa SYMBOL_ASSIGN, apply_string_macro_expansion
*/
#define SYMBOL_CREATE() \
  { "\0", {sv_undef, {NULL}}, "" }

/*!
  \brief SYMBOL_ASSIGN helps to fill the fields of the SubstString
  structure, previously allocated with the SYMBOL_CREATE macro 

  
  The first parameter is the variable assigned by SYMBOL_CREATE; <BR>
  The third parameter is the type of the value that will substitute
  the symbol. Can be: string, integer, floating or pointer. <BR>
  The fourth parameter is the format string (as the printf format string)
  used to convert the value in a string.
  The fifth parameter is the static value which will substitute all
  occurences of the symbol.

  \se The structure passed as first parameter will change

  \sa SYMBOL_CREATE, apply_string_macro_expansion
*/
#define SYMBOL_ASSIGN(_subst, _symbol, _type, _format, _value) \
_subst.symbol = _symbol; \
_subst.value.type = sv_##_type;    \
_subst.format = _format;  \
_subst.value.assign._type = _value



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Searches for a symbol in the given string, and
  and substitutes all its occurences with the specified element, using the
  given format.

  The first parameter <I>subst</I> contains information
  about the symbol to be checked and about the element which substitutes every
  occurence of the symbol, and the format (as in printf) used to convert the
  element in a string. The element has a type (integer, string, float, etc.)
  and a statically assigned value. <BR>
  The second parameter <I>string</I> contains the string to be searched for,
  and the string finally returned too. So it is *very important* you supply
  a buffer large enought to contain the larger string between source and
  destination strings. Use the third parameter to fix the maximum buffer
  length. <BR><BR>
  The element can be built with a 2-passes procedure.
  The first pass consists in constructing the static instance of the element.
  Use the SYMBOL_CREATE macro to build it, and assign the result to a
  <I>SubstString</I> type variable.
  Then assign the substitution value to the created instance using the macro
  SYMBOL_ASSIGN. <BR>
  <I>Example of usage:</I><BR>
  <PRE>
  {
    char szBuffer[256];
    SubstString sb = SYMBOL_CREATE("$D");

    SYMBOL_ASSIGN(sb, integer, "%d", 10);

    strncpy(szBuffer, "Every symbol $D will be substituted with $D",
            sizeof(szBuffer));

    apply_string_macro_expansion(&sb, szBuffer, sizeof(szBuffer));
  }
  </PRE>
  

  \se The given string will change

  \sa SYMBOL_CREATE, SYMBOL_ASSIGN, SubstString
*/
void
apply_string_macro_expansion(const SubstString* const subst,
                   char* string, size_t buf_len);

/*!
  \brief Converts a given string representing a number (base 10)
  to an integer with the same value

  Returns zero (0) if conversion is carried out
  successfully, otherwise returns 1

  \se 'value' parameter might change
*/
int util_str2int(const char* str, int* value);

/*!
  \brief Checks if given string is NULL, "", or the converted
  string of NULL

  Returns 1 if the string is equal to "", NULL or
  equal to the converted string of NULL (as sprintf does).
  Otherwise returns 0.
  
*/
int util_is_string_null(const char* string);

/*!
  \brief An abstraction over BSD strtol for integers

  Parses an integer value from a string, performing
               error-checking on the parsed value. This function can
               be used to parse incrementally a complex string made of
               numbers and separators.

               Returns 0 iff no error was detected.

               Remarks:

               * Empty strings are allowed as a corner case. They are
                 interpreted as 0.

  \se *endptr points to the next character in string, *out
               contains the integer value corresponding to the parsed
               string.
*/
int util_str2int_incr(const char* str, char **endptr, int* out);
/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_UTILS_UCMD_H__ */
