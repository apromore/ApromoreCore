/* ---------------------------------------------------------------------------


  This file is part of the ``enc'' package of NuSMV version 2.
  Copyright (C) 2003 by FBK-irst.

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
  \author Marco Roveri, Roberto Cavada
  \brief Interface for operators are used by dd package

  Functions like add_plus, add_equal, etc., call these operators

*/


#ifndef __NUSMV_CORE_ENC_OPERATORS_H__
#define __NUSMV_CORE_ENC_OPERATORS_H__


#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/WordNumber.h"
#include "nusmv/core/utils/array.h"
#include "nusmv/core/node/node.h"


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief  performs logical AND on two nodes.

   Nodes can be integers with values 0 and 1 (logical AND).
  All other combinations are illegal.
*/
node_ptr node_and(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief  performs logical OR on two nodes.

   Nodes can be integers with values 0 and 1 (logical OR).
  All other combinations are illegal.
*/
node_ptr node_or(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief  performs logical NOT on a node.

   Node can be an integer with values 0 or 1 (logical NOR).
  All other combinations are illegal.

  NOTE: At the momement, CUDD does not have unary 'apply', so
  you have to write a unary operator in the form of a binary one which
  actually applies to the first operand only
*/
node_ptr node_not(node_ptr n, node_ptr this_node_not_used, const NuSMVEnv_ptr env);

/*!
  \brief  performs logical IFF on two nodes.

   Nodes can be integers with values 0 and 1 (logical IFF).
  All other combinations are illegal.
*/
node_ptr node_iff(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief  performs logical XOR on two nodes.

   Nodes can be integers with values 0 and 1 (logical XOR).
  All other combinations are illegal.
*/
node_ptr node_xor(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief  performs logical IMPLIES on two nodes.

  Nodes can be integers with values 0 and 1 (logical IMPLIES).
  All other combinations are illegal.
*/
node_ptr node_implies(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief returns NUMBER with value 1 (symbol ExprMgr_true(exprs)) if
  the nodes are the same, and value 0 (symbol ExprMgr_false(exprs)) otherwise

  
  In NuSMV an constant is equal to another constant then this
  constants are actually the same and representable by the same node.
  

  \sa node_setin
*/
node_ptr node_equal(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief returns NUMBER with value 1 (symbol ExprMgr_true(exprs)) if
  the nodes are of different values, and value 0 (symbol ExprMgr_false(exprs)) otherwise

  
  In NuSMV an constant is equal to another constant then this
  constants are actually the same and representable by the same node.
  

  \sa node_setin
*/
node_ptr node_not_equal(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief returns NUMBER with value 1 if
  the first node is less than the second one, and 0 - otherwise.

  Nodes should be both NUMBER
*/
node_ptr node_lt(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief returns NUMBER with value 1 if
  the first node is greater than the second one, and 0 - otherwise.

  Nodes should be both NUMBER
*/
node_ptr node_gt(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief returns NUMBER with value 1 if
  the first node is less or equal than the second one, and 0 - otherwise.

  Nodes should be both NUMBER
*/
node_ptr node_le(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief returns NUMBER with value 1 if
  the first node is greater or equal than the second one, and 0 - otherwise.

  Nodes should be both NUMBER
*/
node_ptr node_ge(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief Negates the operand (unary minus)

  Left node can be NUMBER, and the right one is Nil.
*/
node_ptr node_unary_minus(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief Adds two nodes

  Nodes can be both NUMBER.
*/
node_ptr node_plus(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief Subtract two nodes

  Nodes can be both NUMBER.
*/
node_ptr node_minus(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief Multiplies two nodes

  Nodes can be both NUMBER.
*/
node_ptr node_times(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief Divides two nodes

  Nodes can be both NUMBER.
*/
node_ptr node_divide(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief Computes the remainder of division of two nodes

  Nodes can be both NUMBER.
*/
node_ptr node_mod(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief creates RANGE node from two NUMBER nodes.

  this range is used in bit-selection only

  \se node_bit_selection
*/
node_ptr node_bit_range(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief Computes the set union of two s_expr.

  This function computes the sexp resulting from
  the union of s_expr "n1" and "n2".
  NB: if any of the operands is a FAILURE node, the FAILURE node is returned.
*/
node_ptr node_union(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/*!
  \brief Set inclusion

  Checks if s_expr "n1" is a subset of s_expr
  "n2", if it is the case them <code>ExprMgr_true(exprs)</code> is returned,
  else <code>ExprMgr_false(exprs)</code> is returned.

  If "n1" is a list of values then <code>ExprMgr_true(exprs)</code> is returned only
  if all elements of "n1" is a subset of "n2".

  NB: if any of the operands is a FAILURE node, the FAILURE node is returned.
*/
node_ptr node_setin(node_ptr n1, node_ptr n2, const NuSMVEnv_ptr env);

/* ---------------------------------------------------------------------- */
/* WORD related                                                           */
/* ---------------------------------------------------------------------- */
/* RC: some function may be moved away */

/*!
  \brief Returns the width of the given word

  
*/
size_t node_word_get_width(node_ptr w);

#define _CHECK_WORD(w)                                      \
  nusmv_assert(((node_get_type(w) == UNSIGNED_WORD ||       \
                 node_get_type(w) == SIGNED_WORD) &&        \
                 node_word_get_width(w) > 0) ||             \
               (node_get_type(w) == NUMBER_UNSIGNED_WORD || \
                node_get_type(w) == NUMBER_SIGNED_WORD))


#define _CHECK_WORDS(w1, w2)                                          \
  _CHECK_WORD(w1); _CHECK_WORD(w2);                                   \
  if ((node_get_type(w1) == UNSIGNED_WORD ||                          \
       node_get_type(w1) == SIGNED_WORD)  &&                          \
      (node_get_type(w2) == UNSIGNED_WORD ||                          \
       node_get_type(w2) == SIGNED_WORD)) /* all words */             \
    nusmv_assert(node_word_get_width(w1) == node_word_get_width(w2)); \
  else if ((node_get_type(w1) == UNSIGNED_WORD ||                       \
            node_get_type(w1) == SIGNED_WORD)  &&                       \
           (node_get_type(w2) == NUMBER_UNSIGNED_WORD ||                \
            node_get_type(w2) == NUMBER_SIGNED_WORD)) /* word and const */ \
    nusmv_assert(node_word_get_width(w1) ==                             \
                 WordNumber_get_width(WORD_NUMBER(car(w2))));           \
  else if ((node_get_type(w2) == UNSIGNED_WORD ||                       \
            node_get_type(w2) == SIGNED_WORD)  &&                       \
           (node_get_type(w1) == NUMBER_UNSIGNED_WORD ||                \
            node_get_type(w1) == NUMBER_SIGNED_WORD)) /* const and word */ \
    nusmv_assert(node_word_get_width(w2) ==                             \
                 WordNumber_get_width(WORD_NUMBER(car(w1))));           \
  else if ((node_get_type(w2) == NUMBER_UNSIGNED_WORD ||                \
            node_get_type(w2) == NUMBER_SIGNED_WORD)  &&                \
           (node_get_type(w1) == NUMBER_UNSIGNED_WORD ||                \
            node_get_type(w1) == NUMBER_SIGNED_WORD)) /* const and const */ \
    nusmv_assert(WordNumber_get_width(WORD_NUMBER(car(w2))) ==          \
                 WordNumber_get_width(WORD_NUMBER(car(w1))));           \
  else error_unreachable_code();

/*!
  \brief Creates a node_ptr that represents the encoding of a
  WORD.

  bitval is the initial value of all bits. w it the
  word width
*/
node_ptr node_word_create(node_ptr bitval, size_t w,
                                 const NuSMVEnv_ptr env);

/*!
  \brief Creates a node_ptr that represents the encoding of a
  WORD, taking the values of bits from the given list

  The list (of CONS nodes) must have length equal to w

  \se node_word_create
*/
node_ptr node_word_create_from_list(node_ptr l, size_t w,
                                           const NuSMVEnv_ptr env);

/*!
  \brief Creates a node_ptr that represents the encoding of a
  WORD, taking the values of bits from the given WordNumber

  Word width is taken from the given WordNumber
*/
node_ptr node_word_create_from_wordnumber(WordNumber_ptr wn,
                                                 const NuSMVEnv_ptr env);

/*!
  \brief Creates a node_ptr that represents the encoding of a
  WORD, taking the values of bits from the given integer value

  
*/
node_ptr
node_word_create_from_integer(unsigned long long value, size_t width,
                              const NuSMVEnv_ptr env);

/*!
  \brief Creates a node_ptr that represents the encoding of a
  WORD, taking the values of bits from the given array of nodes.

  
*/
node_ptr node_word_create_from_array(array_t* arr,
                                            const NuSMVEnv_ptr env);

/*!
  \brief Converts the given word to a dynamic array.

  The array must be freed by the caller.
  Note that the order is reversed,i.e. bits found earlier in the WORD expression
  are but closer to the end in the array (they should be higher bits).
*/
array_t* node_word_to_array(node_ptr w);

/* operations: */

/*!
  \brief Traverses the word bits, and foreach bit creates a node
  whose operator is given. The result is returned as a new word encoding

  
*/
node_ptr node_word_apply_unary(node_ptr wenc, int op,
                                      const NuSMVEnv_ptr env);

/*!
  \brief Traverses the word bits, and foreach bit creates a node
  whose operator is given. The result is returned as a new word encoding

  
*/
node_ptr node_word_apply_attime(node_ptr wenc, int time,
                                       const NuSMVEnv_ptr env);

/*!
  \brief Traverses two given words, and creates a new word
  applying to each pair of bits the given operator

  
*/
node_ptr
node_word_apply_binary(node_ptr wenc1, node_ptr wenc2, int op,
                       const NuSMVEnv_ptr env);

/*!
  \brief Returns an AND node that is the conjuction of all
  bits of the given word

  
*/
node_ptr node_word_make_conjuction(node_ptr w,
                                          const NuSMVEnv_ptr env);

/*!
  \brief Returns an OR node that is the disjuction of all
  bits of the given word

  
*/
node_ptr node_word_make_disjunction(node_ptr w,
                                           const NuSMVEnv_ptr env);

/*!
  \brief Casts the given word to boolean

  The word must have width 1
*/
node_ptr node_word_cast_bool(node_ptr w, const NuSMVEnv_ptr env);

/*!
  \brief Returns a new word that is the negation of the given
  word

  
*/
node_ptr node_word_not(node_ptr w, const NuSMVEnv_ptr env);

/*!
  \brief Returns a new word that is the conjuction of the given
  words

  
*/
node_ptr node_word_and(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Returns a new word that is the disjuction of the given
  words

  
*/
node_ptr node_word_or(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Returns a new word that is the xor of the given
  words

  
*/
node_ptr node_word_xor(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Returns a new word that is the xnor of the given
  words

  
*/
node_ptr node_word_xnor(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Returns a new word that is the logical implication of
  the given words

  
*/
node_ptr node_word_implies(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Returns a new word that is the <-> of the given
  words

  
*/
node_ptr node_word_iff(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief 

  
*/
node_ptr node_word_equal(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Returns a new word that is the xor of the given
  words

  
*/
node_ptr node_word_notequal(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Returns a new word that is the concatenationof the given
  words

  The first given word is the most significant word
  of the result
*/
node_ptr node_word_concat(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Performs bit selections of the given word, that can be
  constant and non-constant

   Range must be compatible with the given word
  width, and must be a node in the form of COLON(NUMBER, NUMBER)
*/
node_ptr node_word_selection(node_ptr word, node_ptr range, const NuSMVEnv_ptr env);

/*!
  \brief Concatenates bit 0 (if isSigned is false) or
  the highest bit of exp (if isSigned is true) 'times' number of times to exp

  exp has to be a UNSIGNED_WORD and 'times' has to be
  a NUMBER
*/
node_ptr node_word_extend(node_ptr a, node_ptr b,
                                 boolean isSigned, const NuSMVEnv_ptr env);

/*!
  \brief Bit-blasts the given words, creating a new word
  encoding that is an added circuit

  
*/
node_ptr
node_word_adder(node_ptr a, node_ptr b, node_ptr carry_in,
                node_ptr* carry_out, const NuSMVEnv_ptr env);

/*!
  \brief Creates a new word encoding that is the circuit that
  adds given words

  
*/
node_ptr node_word_plus(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Creates a new word encoding that is the circuit that
  subtracts given words

  
*/
node_ptr node_word_minus(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Creates a new word encoding that is the circuit that
  performs unsigned subtraction of given words

  
*/
node_ptr node_word_uminus(node_ptr a, const NuSMVEnv_ptr env);

/*!
  \brief Creates a new word encoding that is the circuit that
  performs multiplication of given words

  
*/
node_ptr node_word_times(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Creates a new word encoding that is the circuit that
  divides given unsigned words

  
*/
node_ptr node_word_unsigned_divide(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Creates a new word encoding that is the circuit that
  performs modulo of given unsigned words

  
*/
node_ptr node_word_unsigned_mod(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Creates a new word encoding that is the circuit that
  divides given signed words

  
*/
node_ptr node_word_signed_divide(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Creates a new word encoding that is the circuit that
  performs modulo of given signed words

  
*/
node_ptr node_word_signed_mod(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Predicate for a < b

  
*/
node_ptr node_word_unsigned_less(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Predicate for a <= b

  
*/
node_ptr node_word_unsigned_less_equal(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Predicate for a > b

  
*/
node_ptr node_word_unsigned_greater(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Predicate for a >= b

  
*/
node_ptr node_word_unsigned_greater_equal(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Predicate for a <s b

  Signed operation is performed
*/
node_ptr node_word_signed_less(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Predicate for a <=s b

  Signed operation is performed
*/
node_ptr node_word_signed_less_equal(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Predicate for a >s b

  Signed operation is performed
*/
node_ptr node_word_signed_greater(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief Predicate for a >=s b

  Signed operation is performed
*/
node_ptr node_word_signed_greater_equal(node_ptr a, node_ptr b, const NuSMVEnv_ptr env);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr map2_param(node_ptr (*fun)(node_ptr, node_ptr, int, const NuSMVEnv_ptr),
                           node_ptr l1, node_ptr l2, int op, const NuSMVEnv_ptr env);

#endif /* __NUSMV_CORE_ENC_OPERATORS_H__ */
