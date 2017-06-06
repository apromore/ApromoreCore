/* ---------------------------------------------------------------------------


   This file is part of the ``wff'' package of NuSMV version 2.
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
  \brief Public interface of class 'ExprMgr'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_WFF_EXPR_MGR_H__
#define __NUSMV_CORE_WFF_EXPR_MGR_H__

#if HAVE_CONFIG_H
#  include "nusmv-config.h"
#endif

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/cinit/NuSMVEnv.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"


/* Don't change these values (used to handle NEXT untimed case and FROZEN) */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define EXPR_UNTIMED_CURRENT -2

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define EXPR_UNTIMED_NEXT -1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define EXPR_UNTIMED_DONTCARE -3

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define EXPR_TIME_OFS 10

/*!
  \brief The type of an expression

  this enum is used to distinguish
   different kinds of expressions: SIMPLE, NEXT, CTL and LTL

  \sa EXP_KIND in grammar.y; ExprMgr_is_syntax_correct
*/
typedef enum {
  EXPR_SIMPLE,
  EXPR_NEXT,
  EXPR_LTL,
  EXPR_CTL
} ExprKind;

/*!
  \brief The Expr type

  An Expr is any expression represented as a sexpr object
*/
typedef node_ptr Expr_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define EXPR(x)                                 \
  ((Expr_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define EXPR_CHECK_INSTANCE(x)                  \
  (nusmv_assert(EXPR(x) != EXPR(NULL)))

/*!
  \struct ExprMgr
  \brief Definition of the public accessor for class ExprMgr


*/
typedef struct ExprMgr_TAG*  ExprMgr_ptr;

/*!
  \brief To cast and check instances of class ExprMgr

  These macros must be used respectively to cast and to check
   instances of class ExprMgr
*/
#define EXPR_MGR(self)                          \
  ((ExprMgr_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define EXPR_MGR_CHECK_INSTANCE(self)                   \
  (nusmv_assert(EXPR_MGR(self) != EXPR_MGR(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Expr_get_type(t)                        \
  node_get_type(t)


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof ExprMgr
  \brief The ExprMgr class constructor

  The ExprMgr class constructor

  \sa ExprMgr_destroy
*/
ExprMgr_ptr ExprMgr_create(const NuSMVEnv_ptr env);

/*!
  \methodof ExprMgr
  \brief The ExprMgr class destructor

  The ExprMgr class destructor

  \sa ExprMgr_create
*/
void ExprMgr_destroy(ExprMgr_ptr self);


/*!
  \methodof ExprMgr
  \brief Returns the internal NodeMgr

  Do not destroy the returned object, it belongs to self.
*/
NodeMgr_ptr ExprMgr_get_node_manager(const ExprMgr_ptr self);


/* Top level functions ********************************************************/

/*!
  \methodof ExprMgr
  \brief Top-level simplifier that evaluates constants and
   simplifies syntactically the given expression

  Top-level simplifier that evaluates constants and
   simplifies syntactically the given expression. Simplification is trivial,
   no lemma learning nor sintactic implication is carried out at the moment.

   WARNING:
   the results of simplifications are memoized in a hash stored
   in the symbol table provided. Be very careful not to free/modify the input
   expression or make sure that the input expressions are find_node-ed.
   Otherwise, it is very easy to introduce a bug which will be
   difficult to catch.
   The hash in the symbol table is reset when any layer is removed.

   NOTE FOR DEVELOPERS: if you think that memoization the simplification
   results may cause some bugs you always can try without global
   memoization. See the function body below for info.



  \se None
*/
Expr_ptr ExprMgr_simplify(const ExprMgr_ptr self, SymbTable_ptr st, Expr_ptr expr);

/*!
  \methodof ExprMgr
  \brief This is the top-level function that simplifiers can use to
   simplify expressions. This evaluates constant values in operands
   left and right with respect to the operation required with parameter type.

  Given an expression node E (handled at
   simplifier-level) the simplifier call this function in post order
   after having simplified car(E) and cdr(E). It calls it by passing
   node_get_type(E) as type, and simplified sub expressions for left and right.
   The function Expr_resolve does not traverses further the structures, it simply
   combine given operation encoded in type with given already simplified
   operands left and right.

   For example, suppose E is AND(exp1, exp2). The simplifier:

   1. Simplifies recursively exp1 to exp1' and exp2 to exp2' (lazyness
   might be taken into account if exp1 is found to be a false
   constant).

   2. Calls in postorder ExprMgr_resolve(self, AND, exp1', exp2')

   ExprMgr_resolve will simplify sintactically the conjunction of (self, exp1', exp2')

  \se None

  \sa Expr_simplify
*/
Expr_ptr ExprMgr_resolve(const ExprMgr_ptr self, SymbTable_ptr st,
                                int type, Expr_ptr left, Expr_ptr right);


/* Queries ********************************************************************/

/*!
  \methodof ExprMgr
  \brief Checks if a costant number is zero

  Checks if a costant number is zero

  \se None
*/
boolean ExprMgr_is_equal_to_zero(const ExprMgr_ptr self, const Expr_ptr input);

/*!
  \methodof ExprMgr
  \brief Compares a NUMBER to a constant number

  Compares a NUMBER to a constant number
*/
boolean
ExprMgr_is_ge_to_number(const ExprMgr_ptr self, const Expr_ptr input,
                        const Expr_ptr number);

/*!
  \methodof ExprMgr
  \brief Checks whether given value is the true value



  \se None
*/
boolean ExprMgr_is_true(const ExprMgr_ptr self, const Expr_ptr expr);

/*!
  \methodof ExprMgr
  \brief Checks whether given value is the false value



  \se None
*/
boolean ExprMgr_is_false(const ExprMgr_ptr self, const Expr_ptr expr);

/*!
  \methodof ExprMgr
  \brief Checks whether the given expression is the
   false:true range value



  \se None
*/
boolean ExprMgr_is_boolean_range(const ExprMgr_ptr self, Expr_ptr expr);

/*!
  \methodof ExprMgr
  \brief Checks whether given value is the given scalar number



  \se None
*/
boolean ExprMgr_is_number(const ExprMgr_ptr self, const Expr_ptr expr, const int value);

/*!
  \methodof ExprMgr
  \brief Determines whether a formula has ATTIME nodes in it

  Determines whether a formula has ATTIME nodes in it
   If cache is not null whenever we encounter a formula in
   the cache we simply return the previously computed value,
   otherwise an internal and temporary map is used.

   NOTE: the internal representation of cache is private so
   the user should provide only caches generated by
   this function!

  \se cache can be updated
*/
boolean ExprMgr_is_timed(const ExprMgr_ptr self, Expr_ptr expr, hash_ptr cache);

/*!
  \brief Check if "exp" belongs to the "expectedKind"

  expectedKind can be EXPR_LTL, EXPR_CTL, EXPR_SIMPLE or
  EXPR_NEXT

  \sa parser.grammar.y:static isCorrectExp
*/
boolean ExprMgr_is_syntax_correct(Expr_ptr exp, ExprKind expectedKind);

/* Builders/Simplifiers *******************************************************/

/*!
  \methodof ExprMgr
  \brief Returns the false:true range value



  \se None
*/
Expr_ptr ExprMgr_boolean_range(const ExprMgr_ptr self);

/*!
  \methodof ExprMgr
  \brief Returns the true expression value



  \se None
*/
Expr_ptr ExprMgr_true(const ExprMgr_ptr self);

/*!
  \methodof ExprMgr
  \brief Returns the false expression value



  \se None
*/
Expr_ptr ExprMgr_false(const ExprMgr_ptr self);

/*!
  \methodof ExprMgr
  \brief Returns the scalar number expression with the given value

  Returns the scalar number expression with the given value

  \se None
*/
Expr_ptr ExprMgr_number(const ExprMgr_ptr self, int value);

/*!
  \methodof ExprMgr

  \brief Returns the word number expression corresponding to the max
  value for the given word size.

  Returns the word number expression corresponding to the max value
  for the given word size and word type (signed, unsigned).

  \se None
*/
Expr_ptr ExprMgr_word_max_value(const ExprMgr_ptr self,
                                const int size, const int type);

/*!
  \methodof ExprMgr
  \brief Constructs a NEXT node of given expression



  \se None
*/
Expr_ptr ExprMgr_next(const ExprMgr_ptr self, const Expr_ptr a, const SymbTable_ptr st);

/*!
  \methodof ExprMgr
  \brief Builds the If-Then-Else node with given operators

  Performs local syntactic simplification. 'cond' is the
   case/ite condition, 't' is the THEN expression, 'e' is the ELSE
   expression

  \se None
*/
Expr_ptr ExprMgr_ite(const ExprMgr_ptr self, const Expr_ptr cond,
                            const Expr_ptr t,
                            const Expr_ptr e,
                            const SymbTable_ptr st);

/*!
  \methodof ExprMgr
  \brief Builds the logical EQUAL of given operators

  Works with boolean, scalar and words. Performs local
   syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_equal(const ExprMgr_ptr self, const Expr_ptr a,
                              const Expr_ptr b,
                              const SymbTable_ptr st);

/*!
  \methodof ExprMgr
  \brief Builds the logical NOTEQUAL of given operators

  Works with boolean, scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_notequal(const ExprMgr_ptr self, const Expr_ptr a,
                                 const Expr_ptr b,
                                 const SymbTable_ptr st);

/*!
  \methodof ExprMgr
  \brief Builds the predicate LE (less-then-equal)
   of given operators

  Works with scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_le(const ExprMgr_ptr self, const Expr_ptr a,
                           const Expr_ptr b,
                           const SymbTable_ptr st);

/*!
  \methodof ExprMgr
  \brief Builds the predicate GE (greater-then-equal)
   of given operators

  Works with boolean, scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_ge(const ExprMgr_ptr self, const Expr_ptr a,
                           const Expr_ptr b,
                           const SymbTable_ptr st);

/*!
  \methodof ExprMgr
  \brief Builds the node for extending a word.

  Works with words. Performs local syntactic
   simplification

  \se None
*/
Expr_ptr ExprMgr_simplify_word_extend(const ExprMgr_ptr self, const SymbTable_ptr st,
                                             Expr_ptr w, Expr_ptr i);

/*!
  \methodof ExprMgr
  \brief Creates a ATTIME node



  \se None
*/
Expr_ptr ExprMgr_attime(const ExprMgr_ptr self, Expr_ptr e, int time,
                               const SymbTable_ptr st);

/*!
  \methodof ExprMgr
  \brief Builds the node for UWCONST or SWCONST

  Works with words and scalars. Performs local syntactic
   simplification.

  \se None

  \sa ExprMgr_resolve
*/
Expr_ptr ExprMgr_word_constant(const ExprMgr_ptr self, const SymbTable_ptr st,
                                      int type,
                                      Expr_ptr w,
                                      Expr_ptr i);

/*!
  \methodof ExprMgr
  \brief Builds the logical/bitwise AND of all elements in the
   list

  Performs local syntactic simplification.
   Nil value is considered as true value

   TODO[AT] inefficient implementation: loop is better than recursion


  \se None
*/
Expr_ptr ExprMgr_and_from_list(const ExprMgr_ptr self, node_ptr list,
                                      const SymbTable_ptr st);

/*!
  \methodof ExprMgr
  \brief Builds the logical/bitwise AND of given operators

  Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_and(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the logical/bitwise AND of given operators,
   considering Nil as the true value

  Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_and_nil(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the logical/bitwise NOT of given operator

  Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_not(const ExprMgr_ptr self, const Expr_ptr expr);

/*!
  \methodof ExprMgr
  \brief Builds the logical/bitwise OR of given operators

  Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_or(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the logical/bitwise XOR of given operators

  Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_xor(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the logical/bitwise XNOR of given operators

  Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_xnor(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the logical/bitwise IFF of given operators

  Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_iff(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the logical/bitwise IMPLIES of given operators

  Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_implies(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the predicate LT (less-then) of given operators

  Works with scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_lt(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the predicate LT (less-then) of given operators

  Works with scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_simplify_lt(const ExprMgr_ptr self, const SymbTable_ptr st,
                                    const Expr_ptr a,
                                    const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the predicate GT (greater-then)
   of given operators

  Works with scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_gt(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the predicate GT (greater-then)
   of given operators

  Works with boolean, scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_simplify_gt(const ExprMgr_ptr self, const SymbTable_ptr st,
                                    const Expr_ptr a,
                                    const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the scalar node for PLUS of given operators

  Works with boolean, scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_plus(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the scalar node for MINUS of given operators

  Works with boolean, scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_minus(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the scalar node for TIMES of given operators

  Works with boolean, scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_times(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the scalar node for DIVIDE of given operators

  Works with boolean, scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_divide(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the scalar node for MODule of given operators

  Works with boolean, scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_mod(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the scalar node for UMINUS (unary minus) of given
   operators

  Works with scalar and words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_unary_minus(const ExprMgr_ptr self, const Expr_ptr a);

/*!
  \methodof ExprMgr
  \brief Builds the node for READ (array read) of given
   array a and index i

  Works with wordarray and intarray.

  \se None
*/
Expr_ptr ExprMgr_array_read(const ExprMgr_ptr self,
                            const Expr_ptr a, const Expr_ptr i);

/*!
  \methodof ExprMgr
  \brief Builds the node for WRITE (array write) of given
   array a, index i, and value v

  Works with wordarray and intarray.

  \se None
*/
Expr_ptr ExprMgr_array_write(const ExprMgr_ptr self,
                             const Expr_ptr a, const Expr_ptr i, 
                             const Expr_ptr v);

/*!
  \methodof ExprMgr
  \brief Builds the node for CONSTARRAY (array const) of given
   variable a and  value v

  Works with wordarray and intarray.

  \se None
*/
Expr_ptr ExprMgr_array_const(const ExprMgr_ptr self,
                             const Expr_ptr a, const Expr_ptr v);

/*!
  \methodof ExprMgr
  \brief Builds the node left shifting of words.

   Description        [Works with words.
   Performs local syntactic simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_word_left_shift(const ExprMgr_ptr self, const Expr_ptr a,
                                        const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the node right shifting of words.

   Description        [Works with words.
   Performs local syntactic simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_word_right_shift(const ExprMgr_ptr self, const Expr_ptr a,
                                         const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the node left rotation of words.

   Description        [Works with words.
   Performs local syntactic simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_word_left_rotate(const ExprMgr_ptr self, const Expr_ptr a,
                                         const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the node right rotation of words.

   Description        [Works with words.
   Performs local syntactic simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_word_right_rotate(const ExprMgr_ptr self, const Expr_ptr a,
                                          const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the node for bit selection of words.

   Description        [Works with words. Performs local syntactic
   simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with words. Performs local syntactic
   simplification

  \se None
*/
Expr_ptr ExprMgr_word_bit_select(const ExprMgr_ptr self, const Expr_ptr w,
                                        const Expr_ptr r);

/*!
  \methodof ExprMgr
  \brief Builds the node for bit selection of words.

   Description        [Works with words. Performs local semantic and syntactic
   simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with words. Performs local semantic and syntactic
   simplification

  \se None
*/
Expr_ptr ExprMgr_simplify_word_bit_select(const ExprMgr_ptr self, const SymbTable_ptr st,
                                                 const Expr_ptr w,
                                                 const Expr_ptr r);

/*!
  \methodof ExprMgr
  \brief Builds the node for word concatenation.

   Description        [Works with words.
   Performs local syntactic simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_word_concatenate(const ExprMgr_ptr self, const Expr_ptr a,
                                         const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the node for casting word1 to boolean.

   Description        [Works with words with width 1.
   Performs local syntactic simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with words with width 1.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_word1_to_bool(const ExprMgr_ptr self, Expr_ptr w);

/*!
  \methodof ExprMgr
  \brief Builds the node for casting boolean to word1.

   Description        [Works with booleans.
   Performs local syntactic simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with booleans.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_bool_to_word1(const ExprMgr_ptr self, Expr_ptr a);

/*!
  \methodof ExprMgr
  \brief Builds the node for casting signed words to unsigned
   words.

   Description        [Works with words.
   Performs local syntactic simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_signed_word_to_unsigned(const ExprMgr_ptr self, Expr_ptr w);

/*!
  \methodof ExprMgr
  \brief Builds the node for casting unsigned words to signed words.

   Description        [Works with words.
   Performs local syntactic simplification]

   SideEffects        [None]

   SeeAlso            []

*****************************************************************************[EXTRACT_DOC_NOTE: * /]


  Works with words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_unsigned_word_to_signed(const ExprMgr_ptr self, Expr_ptr w);

/*!
  \methodof ExprMgr
  \brief Builds the node for extending a word.

  Works with words.
   Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_word_extend(const ExprMgr_ptr self, Expr_ptr w,
                                    Expr_ptr i,
                                    const SymbTable_ptr st);

/*!
  \methodof ExprMgr
  \brief Retrieves the time out of an ATTIME node



  \se None
*/
int ExprMgr_attime_get_time(const ExprMgr_ptr self, Expr_ptr e);

/*!
  \methodof ExprMgr
  \brief Retrieves the untimed node out of an ATTIME node



  \se None
*/
Expr_ptr ExprMgr_attime_get_untimed(const ExprMgr_ptr self, Expr_ptr e);

/*!
  \methodof ExprMgr
  \brief Makes a union node

  [AT] this function may be extremely inefficient
   If expression is recursively constructed of many UNION, see issue 4483.


  \se None
*/
Expr_ptr ExprMgr_union(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Makes a setin node, with possible syntactic
   simplification.



  \se None
*/
Expr_ptr ExprMgr_setin(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b, const SymbTable_ptr st);

/*!
  \methodof ExprMgr
  \brief Makes a TWODOTS node, representing an integer range



  \se None
*/
Expr_ptr ExprMgr_range(const ExprMgr_ptr self, const Expr_ptr a, const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds an Uninterpreted function

  Builds an uninterpreted function named "name" with
   "params" as parameters. "params" must be a cons
   list of expressions (Created with find_node)

  \se None
*/
Expr_ptr ExprMgr_function(const ExprMgr_ptr self, const Expr_ptr name,
                                 const Expr_ptr params);

/*!
  \methodof ExprMgr
  \brief Obtain the base time of an expression

  Current time is recursively calculated as follows:

   1. EXPR_UNTIMED_CURRENT for Nil and leaves;
   2. UNTIMED_FROZEN if all vars are frozen;
   3. Time specified for an ATTIME node, assuming
   that the inner expression is untimed.

   Nesting of ATTIME nodes is _not_ allowed;
   4. Minimum time for left and right children
   assuming

   EXPR_UNTIMED_CURRENT <
   EXPR_UNTIMED_NEXT <
   t, for any t >= 0.

  \se None
*/
int ExprMgr_get_time(const ExprMgr_ptr self, SymbTable_ptr st, Expr_ptr expr);

/*!
  \methodof ExprMgr
  \brief Obtain the time interval of an expression

  Returns an array of two elements s.t.
                       - interval[0] = <min>,
                       - interval[1] = <max>.

                       Time interval is recursively calculated as follows:

                       1. EXPR_UNTIMED_CURRENT for Nil and leaves;
                       2. EXPR_UNTIMED_DONTCARE if all vars are frozen;
                       3. Time specified for an ATTIME node, assuming
                       that the inner expression is untimed.

                       Nesting of ATTIME nodes is _not_ allowed;
                       4. The value <min> is the minimum time between left and
                       right children, the value <max> is the maximum.

                       For untimed expressions we assume:
                       EXPR_UNTIMED_CURRENT <
                       EXPR_UNTIMED_NEXT

  \se The returned value is allocated and must be freed by the
                       user.

  \sa Expr_get_time
*/
int* ExprMgr_get_time_interval(const ExprMgr_ptr self,
                                      const SymbTable_ptr st,
                                      Expr_ptr expr);

/*!
  \methodof ExprMgr
  \brief Returns the untimed version of an expression



  \se Expr_get_time
*/
Expr_ptr ExprMgr_untimed(const ExprMgr_ptr self, SymbTable_ptr st, Expr_ptr expr);

/*!
  \methodof ExprMgr
  \brief Returns the untimed version of an expression without
   searching for the current time

  Returns the untimed version of an expression using the
   current time provided as an argument.

  \se Expr_get_time
*/
Expr_ptr ExprMgr_untimed_explicit_time(const ExprMgr_ptr self, SymbTable_ptr st,
                                              Expr_ptr expr,
                                              int time);

/*!
  \methodof ExprMgr
  \brief Builds the node for WSIZEOF

  Works with words. Performs local syntactic simplification.

  \se None

  \sa Expr_resolve
*/
Expr_ptr ExprMgr_wsizeof(const ExprMgr_ptr self, Expr_ptr l, Expr_ptr r);

/*!
  \methodof ExprMgr
  \brief Builds the node for CAST_TOINT

  Works with scalars. Performs local syntactic simplification.

  \sa Expr_resolve
*/
Expr_ptr ExprMgr_cast_toint(const ExprMgr_ptr self, Expr_ptr l, Expr_ptr r);

/*!
  \methodof ExprMgr
  \brief Builds the node for FLOOR

  Works with integers and reals. Performs local syntactic
   simplification.

  \sa Expr_resolve
*/
Expr_ptr ExprMgr_simplify_floor(const ExprMgr_ptr self, const SymbTable_ptr symb_table,
                                       Expr_ptr body);


/*!
  \methodof ExprMgr
  \brief Builds the node for FLOOR

  Works with integers and reals.

  \sa Expr_resolve
*/
Expr_ptr ExprMgr_floor(const ExprMgr_ptr self, Expr_ptr l);


/*!
  \methodof ExprMgr
  \brief Sums one to a costant number

  Sums one to a costant number

  \se None
*/
Expr_ptr ExprMgr_plus_one(const ExprMgr_ptr self,
                                 const Expr_ptr a);

/*!
  \methodof ExprMgr
  \brief Substracts one to a costant number

  Substracts one to a costant number

  \se None
*/
Expr_ptr ExprMgr_minus_one(const ExprMgr_ptr self,
                                  const Expr_ptr a);

/*!
  \methodof ExprMgr
  \brief Sums a NUMBER to a constant number

  Calls ExprMgr_plus after having converted b to the proper
  type (the same of a).
*/
Expr_ptr ExprMgr_plus_number(const ExprMgr_ptr self,
                                    const Expr_ptr a,
                                    const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the logical/bitwise IFF of given operators

  Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_simplify_iff(const ExprMgr_ptr self,
                                     const SymbTable_ptr st,
                                     const Expr_ptr a,
                                     const Expr_ptr b);

/*!
  \methodof ExprMgr
  \brief Builds the node for resizing a word.

  Works with words. Performs local syntactic simplification

  \se None
*/
Expr_ptr ExprMgr_simplify_word_resize(const ExprMgr_ptr self, const SymbTable_ptr st,
                                             Expr_ptr w,
                                             Expr_ptr i);


/*!
  \methodof ExprMgr
  \brief Builds the node for CAST_TO_UNSIGNED_WORD

  Does not perform any simplification

*/
Expr_ptr ExprMgr_cast_to_unsigned_word(const ExprMgr_ptr self,
                                       Expr_ptr width, Expr_ptr arg);

/*!
  \methodof ExprMgr
  \brief Returns true if the time (obtained by Expr_get_time) is
   dont't care



  \se Expr_get_time
*/
boolean ExprMgr_time_is_dont_care(const ExprMgr_ptr self, int time);

/*!
  \methodof ExprMgr
  \brief Returns true if the time (obtained by Expr_get_time) is
   current



  \se Expr_get_time
*/
boolean ExprMgr_time_is_current(const ExprMgr_ptr self, int time);

/*!
  \methodof ExprMgr
  \brief Returns true if the time (obtained by Expr_get_time) is
   next



  \se Expr_get_time
*/
boolean ExprMgr_time_is_next(const ExprMgr_ptr self, int time);

/*!
  \methodof ExprMgr
  \brief Moves the next operator to the tree leafs (i.e. on identifiers)

  Moves the next operator to the tree leafs (i.e. on identifiers)
*/
Expr_ptr ExprMgr_move_next_to_leaves(const ExprMgr_ptr self, SymbTable_ptr st, Expr_ptr expr);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_WFF_EXPR_MGR_H__ */
