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
  \brief An interface to deal with the types of variables (during
  compilation and type checking.

  This class represent the types of the NuSMV type system.

  NOTE: In the following description "integer" is used everywhere to refer to
  the set of natural numbers, EXCEPT for the type specified in c), that has a
  special meaning.

  The types can be:
    a) boolean (TRUE, FALSE)
    b) enum (enumeration), that represents a set of particular values, e.g.
       . a pure symbolic enumeration type: {A, OK, CORRECT, FAIL}
       . a pure integer enumeration type: {1,2,3,4,5, 10, -1, 0}
       . an integer and symbolic enumeration type: {0, 2, 4, OK, FAIL}
       . a range of integer values: 1 .. 10;
    c) integer, that represents the infinite-precision integer arithmetic.
    d) real, that represents the infinite-precision rational arithmetic.
    e) continuous, that represents infinite-precision real variables with
       continuous behavior (they can be derivated);
    f) word of width N (N is an integer positive number), that represents
       bit vectors of N bits.
    g) WordArray of address word of width M and value word of width N
       (with M and N integer positive numbers), that represents a
       memory array of 2^M locations, each of which contains a word of
       M bits.
       WARNING: Traces assumes only array defines to have type array
    h) Array represents array of fixed lower and upper bounds
       and elements of a particular type.
    i) Set types created by "union" expressions and used in "in", "case"
        and ":=" expressions:
       . a set of boolean values :  0 union 1
       . a set of integer values :  0 union -1 union 10
       . a set of symbolic values :  OK union FAIL
       . a set of integer and symbolic values: OK union 1
       Note that only expressions (not declared variables) can
       have these types.
    j) No-type is an artificial type to represent expressions which usually
       do not have any type (for example, assignments).
    k) Error type is an artificial type to represent erroneous
       situations.

  A type can be created with the class' constructor. In the case of a
  enum type, during construction it is necessary to specify explicitly the
  list of values this type consists of.

  The constructor is typically used in a symbol table.

  Another possibility is to obtain the types with
  SymbTablePkg_..._type functions. In this case, the enum types will be
  "abstract", i.e. they will consist of some artificial (not existing)
  values. The important feature is that the memory is shared by these
  functions, i.e. you can compare pointers to types, instead of the
  types' contents.  These functions are mostly used in type checking
  (since the particular values of enum types are of no importance).

*/


#ifndef __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TYPE_H__
#define __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TYPE_H__

#include <stdio.h>

#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/printers/MasterPrinter.h"


/*!
  \struct SymbType
  \brief Generic and symbolic encoding


*/
typedef struct SymbType_TAG* SymbType_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_TYPE(x)  \
        ((SymbType_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_TYPE_CHECK_INSTANCE(x)  \
        (nusmv_assert(SYMB_TYPE(x) != SYMB_TYPE(NULL)))


/*!
  \brief Generic and symbolic encoding


*/


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define nullType   SYMB_TYPE(NULL)


/*!
  \brief Possible kinds of a type


  The tags of types that a variable or an expression can have.
  Note that a variable cannot have a set type.
*/

typedef enum SymbTypeTag_TAG {
  SYMB_TYPE_NONE, /* no-type and no error. */
  SYMB_TYPE_STATEMENT, /* for statements like assignements, INIT, TRANS etc */
  SYMB_TYPE_BOOLEAN,
  SYMB_TYPE_ENUM,  /* an enumeration of values (includes ranges, i.e. -1..4) */
  SYMB_TYPE_INTEGER,  /* (infinite-precision) integer */
  SYMB_TYPE_REAL, /* (infinite-precision) rational */
  SYMB_TYPE_NFUNCTION,
  SYMB_TYPE_CONTINUOUS,
  SYMB_TYPE_SIGNED_WORD, /* word is like an arrary of booleans + signed arithmetic */
  SYMB_TYPE_UNSIGNED_WORD, /* word is like an arrary of booleans + unsigned arithmetic */
  SYMB_TYPE_WORDARRAY, /* an array of words */
  SYMB_TYPE_ARRAY, /* an array */
  SYMB_TYPE_SET_BOOL,  /* a set of boolean values */
  SYMB_TYPE_SET_INT,  /* a set of integer values */
  SYMB_TYPE_SET_SYMB, /* a set of symbolic values */
  SYMB_TYPE_SET_INT_SYMB, /* a set of symbolic and integer values */
  SYMB_TYPE_INTARRAY, /* unbounded array with integer index */
  SYMB_TYPE_ERROR, /* indicates an error */
  /* SYMB_TYPE_NONE must be the first and SYMB_TYPE_ERROR must be
     the last in the list
  */
} SymbTypeTag;


/* a set of constants to identify different enum-types */
enum Enum_types{ ENUM_TYPE_PURE_INT,
                 ENUM_TYPE_PURE_SYMBOLIC,
                 ENUM_TYPE_INT_SYMBOLIC,
};

/* Forward declaration */
#ifdef DEFINED_NFunction_ptr
#else
typedef struct NFunction_TAG*  NFunction_ptr;
#define DEFINED_NFunction_ptr 1
#endif

/* ---------------------------------------------------------------------- */
/*     Public methods                                                     */
/* ---------------------------------------------------------------------- */

/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof SymbType
  \brief Class SymbType constructor

  The tag must be a correct tag. The 'body' is the
   additional info corresponding to a particular kind of the type:
   * for a enum type the body is the list of values;
   * for "BOOL", "INT" or "REAL" the body is unused, and set to Nil;
   * for signed and unsigned "WORD" it is the NUMBER node defining the
      width of the type;
   * for "WORDARRAY", the body is a pair of NUMBER nodes, defining
      the width of the address, and the width of the value.
   * for everything else body is Nil;

   Note that array types have to be created with
   SymbType_create_array, not with this constructor.

   Set-types are used with expressions which represent a set values.
   "NO-TYPE" is used with expressions which normally do not
        have any type such as assignments.
   "ERROR" type indicates an error (not an actual type).

   No-type, error-type and all set-types (boolean-set, integer-set,
   symbolic-set, symbolic-integer-set) should not be created with this
   constructor, but only with memory-shared function
   SymbTablePkg_..._type.  The reason behind this constrain is that
   only expressions (not variables) can have these types, therefore
   only memory-shared versions of these types are required.

   The constructor does not create a copy of the body, but just remember
   the pointer.

   NB: system "reset" command destroys all node_ptr objects, including those
   used in SymbType_ptr. So destroy all symbolic types before the destruction
   of node_ptr objects, i.e. before or during "reset"

  \se allocate memory

  \sa SymbType_create_array, SymbType_destroy
*/
SymbType_ptr SymbType_create(const NuSMVEnv_ptr env,
                             SymbTypeTag tag, node_ptr body);

/*!
  \methodof SymbType
  \brief Class SymbType constructor for array types only

   WARNING: Traces assumes only array defines to have type ARRAY
   This is specialized version of SymbType_create
   which is designed for array types only.
   It is implemented as a special construtor because array types are quite
   different from all the others.

   Parameter subtype is the subtype of the array type. This type has
   to be not-memory-shared and its ownership is passed to created
   type. I.e. subtype will be destroyed when returned type is destroyed.

   lower_bound, upper-bound are the lower and upper bounds,resp, of
   the array.

   All the constrains about memory, lifetype, etc are the same as for
   SymbType_create.


  \se allocate memory

  \sa SymbType_destroy
*/
SymbType_ptr SymbType_create_array(SymbType_ptr subtype,
                                          int lower_bound,
                                          int upper_bound);

/*!
  \methodof SymbType
  \brief Class SymbType constructor for uninterpreted functions only

   This is specialized version of SymbType_create
   which is designed for uninterpreted functions only.
   It is implemented as a special construtor because functions types are quite
   different from all the others.



  \se allocate memory

  \sa SymbType_destroy
*/
SymbType_ptr SymbType_create_nfunction(const NuSMVEnv_ptr env,
                                       NFunction_ptr nfunction);

/* SymbType_create_from_node: use Compile_InstantiateType */

/*!
  \methodof SymbType
  \brief Class SymbType copy-constructor

  This function takes one type and returns its copy.

   Note: the body of the type is not copied, i.e. just pointer is remembered.
   See SymbType_create for more info about body.

   Note: the input type should not be a memory-shared type (since there is no
   meaning in coping a memory sharing type).


  \se allocate memory

  \sa SymbType_destroy
*/
SymbType_ptr SymbType_copy(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Class SymbType destructor

  Deallocate the memory. The destructor
   does not deallocate memory from the type's body (since the
   constructor did not created the body).

   NOTE: If self is a memory sharing type instance, i.e. a type returned by
   SymbTablePkg_..._type functions then the destructor will not delete
   the type.

  \sa SymbType_create
*/
void SymbType_destroy(SymbType_ptr self);


/* Getters and Setters ********************************************************/

/*!
  \methodof SymbType
  \brief Returns the tag (the kind) of the type


*/
SymbTypeTag SymbType_get_tag(const SymbType_ptr self);

/* a enum of useful auxiliary functions */

/*!
  \methodof SymbType
  \brief Returns true if the type is intarray, or else returns false


*/
boolean SymbType_is_intarray(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true if the type is a enum-type, or else returns false


*/
boolean SymbType_is_enum(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is boolean. Otherwise - returns false.

  The kind of enum-type is analysed in the constructor.

  \sa SymbType_create
*/
boolean SymbType_is_boolean(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true if the type is a integer-type, or else returns false


*/
boolean SymbType_is_integer(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true if the type is a real-type, or else returns false


*/
boolean SymbType_is_real(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true if the type is a continuous type, or else returns
   false


*/
boolean SymbType_is_continuous(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is a enum-type and its value
   are integers only. Otherwise - returns false.

  The kind of enum-type is analysed in the constructor.

  \sa SymbType_create
*/
boolean SymbType_is_pure_int_enum(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is a enum-type and its value
   are symbolic constants only. Otherwise - returns false.

  The kind of enum-type is analysed in the constructor.

  \sa SymbType_create
*/
boolean SymbType_is_pure_symbolic_enum(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is a enum-type and its value
   are symbolic AND integer constants. Otherwise - returns false.

  The kind of enum-type is analysed in the constructor.

  \sa SymbType_create
*/
boolean SymbType_is_int_symbolic_enum(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is a Unsigned Word type and the width of
   the word is 1. Otherwise - returns false.



  \sa SymbType_create
*/
boolean SymbType_is_word_1(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is an unsigned Word type



  \sa SymbType_create
*/
boolean SymbType_is_unsigned_word(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is a signed Word type



  \sa SymbType_create
*/
boolean SymbType_is_signed_word(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is a Word type (signed or unsigned)



  \sa SymbType_create
*/
boolean SymbType_is_word(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is one of the set-types, i.e.
   boolean-set, integer-set, symbolic-set, integer-symbolic-set, and
   false otherwise.



  \sa SymbType_create
*/
boolean SymbType_is_set(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is an uninterpreted function type
*/
boolean SymbType_is_function(const SymbType_ptr self);


/*!
  \methodof SymbType
  \brief Returns true, if the type is a error-type, and false otherwise.

  Error type is used to indicate an error

  \sa SymbType_create
*/
boolean SymbType_is_error(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is a statement-type,
   and false otherwise.



  \sa SymbType_create
*/
boolean SymbType_is_statement(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true, if the type is one of infinite-precision types

  Infinite-precision types are such as integer and real.

  \sa SymbType_create
*/
boolean SymbType_is_infinite_precision(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true if the type is an array-type, or else returns false

  WARNING: Traces assumes this function returns true only
   for array defines
*/
boolean SymbType_is_array(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns true if the given type is a wordarray

  .
*/
boolean SymbType_is_wordarray(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief The function calculate how many bits is required to
   store a value of a given type

  This function can be invoked only on finite-precision
   valid type of variables. This means that such types as no-type or error-type
   or real or any memory-shared ones should not be given to this function.


  \sa SymbType_create
*/
int SymbType_calculate_type_size(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Generates and returns a list of all possible values
   of a particular Unsigned Word type


*/
node_ptr
SymbType_generate_all_word_values(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns the width of a Word type

  The given type should be Word and the
   body of the type (given to the constructor) should be NUMBER node.
*/
int SymbType_get_word_width(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns the line number where the type was declared.

  The body of the type, provided during construction, is
   a node NUMBER specifying the width of the Word or a node CONS
   specifying the address-value widths or WordArray.  This node was
   create during parsing and contains the line number of the type
   declaration.
   NB: The type should not be memory-sharing.
   NB: Virtually this function is used only in TypeChecker_is_type_wellformed


  \sa SymbType_create
*/
int SymbType_get_word_line_number(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns the width of the address in a WordArray type

  .
*/
int SymbType_get_wordarray_awidth(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns the subtype of wordarray

  .
*/
SymbType_ptr SymbType_get_wordarray_subtype(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns the list of values of an enum type

  The given type has to be a ENUM type.
   The return list is a list of all possible values of a enum type. This list
   was provided during construction.

   NB: Memory sharing types do not have particular values, since they
   are "simplified".
*/
node_ptr SymbType_get_enum_type_values(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Returns the type of an NFunction

  The given type has to be an NFunction type.
                       The return value contains the function definiction.
*/
NFunction_ptr SymbType_get_nfunction_type(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Get inner type of an array

  The returned pointer belongs to the ginven SymbType_ptr
                        and must not be freed
*/
SymbType_ptr SymbType_get_array_subtype(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Get array lower bound


*/
int SymbType_get_array_lower_bound(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Get array upper bound


*/
int SymbType_get_array_upper_bound(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Get inner type of an array

  The returned pointer belongs to the ginven SymbType_ptr
                        and must not be freed
*/
SymbType_ptr SymbType_get_intarray_subtype(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief Prints the type structure to the output stream.

  This function is made very similar to print_node.
   If a Enum type was created with SymbType_create then all its values will be
   printed, otherwise the type was created with SymbTablePkg_..._type
   and simplified type name (instead of actual type values) is printed.


  \sa  SymbType_sprint
*/
void SymbType_print(const SymbType_ptr self,
                           MasterPrinter_ptr printer,
                           FILE* output_stream);

/*!
  \methodof SymbType
  \brief  Return a string representation of the given type.

   This function is made very similar to sprint_node.
                         If an Enum type was created with
                         SymbType_create then all its values will be
                         printed, otherwise the type was created with
                         SymbTablePkg_..._type and simplified type
                         name (instead of actual type values) is
                         printed.

                         The returned string must be released by the caller.

  \se  The returned string is allocated and has to be released
                        by the caller

  \sa  SymbType_print
*/
char* SymbType_sprint(const SymbType_ptr self,
                             MasterPrinter_ptr printer);

/*!
  \brief returns true if the given type is "backward compatible",
   i.e. a enum or integer type.

  We distinguish "old" types because we may want to turn
   off the type checking on these types for backward
   compatibility. Integer is also considered as "old", because an enum
   of integer values is always casted to Integer.
*/
boolean SymbType_is_back_comp(const SymbType_ptr type);

/*!
  \brief Returns one of the given types, if the other one
   can be implicitly converted to the former one. Otherwise - nullType.

  The implicit conversion is performed
   in accordance to the type order.
   NOTE: only memory-shared types can be given to this function.
*/
SymbType_ptr
SymbType_get_greater(const SymbType_ptr type1, const SymbType_ptr type2);

/*!
  \methodof SymbType
  \brief Returns a minimal set-type which the given type
   can be implicitly converted to, or NULL if this is impossible.


   The implicit conversion is performed in accordance to the type order.
   NOTE: only memory-shared types can be given to this function.

  \sa SymbType_make_type_from_set_type
*/
SymbType_ptr SymbType_make_set_type(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief This function is opposite to
   SymbType_make_set_type, i.e. if the given type is one of
   the set-types, then the type without "set" suffix is
   returned. Otherwise the type is returned without change.


   More precisely the following conversion takes place:
   boolean-set -> boolean
   integer-set ->integer
   symbolic-set -> symbolic-enum
   integer-symbolic-set -> integer-symbolic-set
   another type -> the same type

   The implicit conversion is performed in accordance to the type order.
   NOTE: only memory-shared types can be given to this function.

  \sa SymbType_make_set_type
*/
SymbType_ptr SymbType_make_from_set_type(const SymbType_ptr self);

/*!
  \methodof SymbType
  \brief This function takes a NOT memory shared type
   and returns a memory shared one.

  The input type should have
   a corresponding memory shared type. For example, function type
   and error type do not have memory shared instances.
*/
SymbType_ptr SymbType_make_memory_shared(const SymbType_ptr self);

/* functions: */

/*!
  \brief Returns the left type, if the right
   one can be implicitly converted to the left one. NULL - otherwise

  The implicit conversion is performed
   in accordance to the type order.
   NOTE: only memory-shared types can be given to this function.
*/
SymbType_ptr
SymbType_convert_right_to_left(SymbType_ptr leftType,
                               SymbType_ptr rightType);

/*!
  \brief Returns the minimal type to which the both given types
   can be converted, or Nil if there is none.

  The implicit conversion is performed in accordance to
   the type order.  NOTE: only memory-shared types can be given to this
   function except for SYMB_TYPE_ARRAY which can be non-memory shared
*/
SymbType_ptr
SymbType_get_minimal_common(SymbType_ptr type1, SymbType_ptr type2);

/*!
  \methodof SymbType
  \brief True if and only if the given type is memory shared


*/
boolean
SymbType_is_memory_shared(SymbType_ptr self);

/*!
  \methodof SymbType
  \brief  True if and only if the given types are equal, the given
   types can be memory-sharing or not.


*/
boolean
SymbType_equals(SymbType_ptr self, SymbType_ptr oth);

/*!
\methodof SymbType
\brief True if it is enum and with just one value
*/
boolean SymbType_is_single_value_enum(const SymbType_ptr self);


/* Conversion *****************************************************************/

/*!
  \methodof SymbType
  \brief Returns a node representing a type

  The node returned is what is expected by the HrcNode and
  other data structures. Currently, only types that can be assigned to
  variables are supported. Hence, it is an error to call this function with
  the following SymbTypes:
  * Statement
  * Set
  * String
*/
node_ptr SymbType_to_node(const SymbType_ptr self,
                          NodeMgr_ptr nodemgr);



#endif /* __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TYPE_H__ */
