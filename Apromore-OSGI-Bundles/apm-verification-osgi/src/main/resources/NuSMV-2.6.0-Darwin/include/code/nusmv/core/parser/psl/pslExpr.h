/* ---------------------------------------------------------------------------


   This file is part of the ``parser.psl'' package of NuSMV version 2.
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
  \author Roberto Cavada
  \brief PSL parser interface

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_PARSER_PSL_PSL_EXPR_H__
#define __NUSMV_CORE_PARSER_PSL_PSL_EXPR_H__

#include "nusmv/core/parser/psl/pslNode.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/error.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

typedef enum SyntaxClass_TAG
  {
    SC_NUM_EXPR,       /* numerical or id */
    SC_BOOL_EXPR,      /* boolean or id */
    SC_WORD_EXPR,
    SC_IDENTIFIER,     /* only id */
    SC_NUM_BOOL_WORD_EXPR,  /* boolean or numerical or word or id */
    SC_NUM_BOOL_EXPR,  /* boolean or numerical or id */

    SC_BOOL_WORD_EXPR, /* Boolean or word or id */

    SC_NUM_WORD_EXPR, /* numerical or word or id operation */

    SC_PROPERTY,
    SC_FL_PROPERTY,
    SC_OBE_PROPERTY,

    SC_SEQUENCE,
    SC_REPLICATOR,
    SC_NONE,
    SC_RANGE,
    SC_LIST,
    SC_NUM_RANGE       /* number, id or range */
  } SyntaxClass ;


typedef struct PslExpr_TAG
{
  SyntaxClass klass;
  PslNode_ptr psl_node;
} PslExpr;




/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/* Shortcuts for unary operators
   Legend ------------------------------
   B : boolean (or identifier)
   N : numeric (or identifier)
   W : Word    (or identifier)
   NBW: B or N or W
   NW: N or W
   BW: B or W
   T: the same type of the operand
   F: fl property
   O: obe property
   ------------------------------------ */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_W2W_OP(env, res, right, op)                            \
  psl_expr_make_unary_op(env, &res, &right, op, SC_WORD_EXPR, SC_WORD_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_B2W_OP(env, res, right, op)                            \
  psl_expr_make_unary_op(env, &res, &right, op, SC_BOOL_EXPR, SC_WORD_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_W2B_OP(env, res, right, op)                            \
  psl_expr_make_unary_op(env, &res, &right, op, SC_WORD_EXPR, SC_BOOL_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_W2N_OP(env, res, right, op)                            \
  psl_expr_make_unary_op(env, &res, &right, op, SC_WORD_EXPR, SC_BOOL_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_NW2NW_OP(env, res, right, op)                          \
  psl_expr_make_unary_op(env, &res, &right, op, SC_NUM_WORD_EXPR, SC_NUM_WORD_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_BW2BW_OP(env, res, right, op)                          \
  psl_expr_make_unary_op(env, &res, &right, op, SC_BOOL_WORD_EXPR, SC_BOOL_WORD_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_N2N_OP(env, res, right, op)                            \
  psl_expr_make_unary_op(env, &res, &right, op, SC_NUM_EXPR, SC_NUM_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_N2B_OP(env, res, right, op)                            \
  psl_expr_make_unary_op(env, &res, &right, op, SC_NUM_EXPR, SC_BOOL_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_B2B_OP(env, res, right, op)                            \
  psl_expr_make_unary_op(env, &res, &right, op, SC_BOOL_EXPR, SC_BOOL_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_NBW2N_OP(env, res, right, op)                          \
  psl_expr_make_unary_op(env, &res, &right, op, SC_NUM_BOOL_WORD_EXPR, SC_NUM_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_NBW2B_OP(env, res, right, op)                          \
  psl_expr_make_unary_op(env, &res, &right, op, SC_NUM_BOOL_WORD_EXPR, SC_BOOL_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_F2F_OP(env, res, right, op)                            \
  psl_expr_make_unary_op(env, &res, &right, op, SC_FL_PROPERTY, SC_FL_PROPERTY)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_B2F_OP(env, res, right, op)                            \
  psl_expr_make_unary_op(env, &res, &right, op, SC_BOOL_EXPR, SC_FL_PROPERTY)

/* this preserves the right's klass */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_T2T_OP(env, res, right, op)                            \
  psl_expr_make_unary_op(env, &res, &right, op, right.klass, right.klass)

/* Shortcuts for binary operators: */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_W_N2W_OP(env, res, left, op, right)                    \
  psl_expr_make_binary_mixed_op(env, &res, &left, op, &right,                \
                                SC_WORD_EXPR, SC_NUM_EXPR, SC_WORD_EXPR)
/* Shortcuts for binary operators: */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_N_N2W_OP(env, res, left, op, right)    \
  psl_expr_make_binary_op(env, &res, &left, op, &right,      \
                          SC_NUM_EXPR, SC_WORD_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_N_N2N_OP(env, res, left, op, right)                    \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_NUM_EXPR, SC_NUM_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_N_N2B_OP(env, res, left, op, right)                    \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_NUM_EXPR, SC_BOOL_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_NB_NB2B_OP(env, res, left, op, right)                  \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_NUM_BOOL_EXPR, SC_BOOL_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_BW_BW2BW_OP(env, res, left, op, right)         \
  psl_expr_make_binary_op(env, &res, &left, op, &right,              \
                          SC_BOOL_WORD_EXPR, SC_BOOL_WORD_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_B_B2B_OP(env, res, left, op, right)                    \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_BOOL_EXPR, SC_BOOL_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_W_W2W_OP(env, res, left, op, right)                    \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_WORD_EXPR, SC_WORD_EXPR)

#define PSL_EXPR_MAKE_W_N2W_OP(env, res, left, op, right)                    \
  psl_expr_make_binary_mixed_op(env, &res, &left, op, &right,                \
                                SC_WORD_EXPR, SC_NUM_EXPR, SC_WORD_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_W_NW2W_OP(env, res, left, op, right)                   \
  psl_expr_make_binary_mixed_op(env, &res, &left, op, &right,                \
                                SC_WORD_EXPR, SC_NUM_WORD_EXPR, SC_WORD_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_N_W2W_OP(env, res, left, op, right)                    \
  psl_expr_make_binary_mixed_op(env, &res, &left, op, &right,                \
                                SC_NUM_EXPR, SC_WORD_EXPR, SC_WORD_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_NB_NB2N_OP(env, res, left, op, right)                  \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_NUM_BOOL_EXPR, SC_NUM_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_NW_NW2NW_OP(env, res, left, op, right)                 \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_NUM_WORD_EXPR, SC_NUM_WORD_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_NW_NW2B_OP(env, res, left, op, right)                  \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_NUM_WORD_EXPR, SC_BOOL_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_NBW_NBW2N_OP(env, res, left, op, right)                \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_NUM_BOOL_WORD_EXPR, SC_NUM_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_NBW_NBW2B_OP(env, res, left, op, right)                \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_NUM_BOOL_WORD_EXPR, SC_BOOL_EXPR)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_F_F2F_OP(env, res, left, op, right)                    \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_FL_PROPERTY, SC_FL_PROPERTY)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_B_B2F_OP(env, res, left, op, right)                    \
  psl_expr_make_binary_op(env, &res, &left, op, &right, SC_BOOL_EXPR, SC_FL_PROPERTY)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_T_T2T_OP(env, res, left, op, right)                    \
  psl_expr_make_binary_op(env, &res, &left, op, &right, left.klass, left.klass)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_EXT_NEXT_OP_BOOL(env, res, operator, fl_property, bool_expr) \
  psl_expr_make_extended_next_op(env, operator, &fl_property, NULL, &bool_expr, &res);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_EXT_NEXT_OP_WHEN(env, res, operator, fl_property, when) \
  psl_expr_make_extended_next_op(env, operator, &fl_property, &when, NULL, &res);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PSL_EXPR_MAKE_EXT_NEXT_OP_WHEN_BOOL(env, res, operator, fl_property, \
                                            when, bool_expr)            \
  psl_expr_make_extended_next_op(env, operator, &fl_property, &when, &bool_expr, &res);


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void psl_expr_make_unary_op(const NuSMVEnv_ptr env,
                                   PslExpr* res,
                                   const PslExpr* right,
                                   const PslOp op_id,
                                   const SyntaxClass right_req_klass,
                                   const SyntaxClass res_klass);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void psl_expr_make_binary_op(const NuSMVEnv_ptr env,
                                    PslExpr* res,
                                    const PslExpr* left,
                                    const PslOp op_id,
                                    const PslExpr* right,
                                    const SyntaxClass ops_req_klass,
                                    const SyntaxClass res_klass);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void psl_expr_make_binary_mixed_op(const NuSMVEnv_ptr env,
                                          PslExpr* res,
                                          const PslExpr* left,
                                          const PslOp op_id,
                                          const PslExpr* right,
                                          const SyntaxClass left_req_klass,
                                          const SyntaxClass right_req_klass,
                                          const SyntaxClass res_klass);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void psl_expr_make_extended_next_op(const NuSMVEnv_ptr env,
                                           PslOp op_id,
                                           const PslExpr* fl_property,
                                           const PslExpr* when,
                                           const PslExpr* bool_expr,
                                           PslExpr* res);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_replicator(const NuSMVEnv_ptr env,
                                        PslOp op_id,
                                        PslExpr id, PslExpr range,
                                        PslExpr value_set);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_replicated_property(const NuSMVEnv_ptr env,
                                  PslExpr replicator, PslExpr expr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_atom(const NuSMVEnv_ptr env, const char* str);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_id(const NuSMVEnv_ptr env,
                                PslExpr left, PslExpr right);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_id_array(const NuSMVEnv_ptr env,
                                      PslExpr id, PslExpr num);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_context(const NuSMVEnv_ptr env,
                                     PslExpr ctx, PslExpr node);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_empty(void);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_true(const NuSMVEnv_ptr env);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_false(const NuSMVEnv_ptr env);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_inf(const NuSMVEnv_ptr env);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_boolean_type(const NuSMVEnv_ptr env);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_boolean_value(const NuSMVEnv_ptr env,
                                           int val);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_failure(const NuSMVEnv_ptr env, const char* msg, FailureKind kind);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_number(const NuSMVEnv_ptr env,
                                    int val);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_base_number(const NuSMVEnv_ptr env,
                                         char* base_num);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_real_number(const NuSMVEnv_ptr env, char* fval);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_word_number(const NuSMVEnv_ptr env, char* wval);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_range(const NuSMVEnv_ptr env,
                                   PslExpr low, PslExpr high);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_case(const NuSMVEnv_ptr env,
                   PslExpr cond, PslExpr _then, PslExpr _list);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_ite(const NuSMVEnv_ptr env,
                  PslExpr cond, PslExpr _then, PslExpr _else);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_suffix_implication_weak(const NuSMVEnv_ptr env,
                                      PslExpr seq, PslOp op,
                                      PslExpr expr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_suffix_implication_strong(const NuSMVEnv_ptr env,
                                        PslExpr seq, PslOp op,
                                        PslExpr expr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_within(const NuSMVEnv_ptr env,
                     PslOp op, PslExpr begin, PslExpr end,
                     PslExpr seq);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_whilenot(const NuSMVEnv_ptr env,
                       PslOp op, PslExpr expr, PslExpr seq);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_abort(const NuSMVEnv_ptr env,
                                   PslExpr fl_prop, PslExpr cond);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_sere(const NuSMVEnv_ptr env,
                                  PslExpr expr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_sere_concat(const NuSMVEnv_ptr env,
                                         PslExpr seq1, PslExpr seq2);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_sere_fusion(const NuSMVEnv_ptr env,
                                         PslExpr seq1, PslExpr seq2);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_sere_compound_binary_op(const NuSMVEnv_ptr env,
                                      PslExpr seq1, PslOp op,
                                      PslExpr seq2);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_repeated_sere(const NuSMVEnv_ptr env,
                            PslOp op, PslExpr sere, PslExpr count);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_cons(const NuSMVEnv_ptr env,
                                  PslExpr a, PslExpr b);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_cons_new(const NuSMVEnv_ptr env,
                                      PslExpr a, PslExpr b);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_concatenation(const NuSMVEnv_ptr env,
                                           PslExpr expr_list);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_multiple_concatenation(const NuSMVEnv_ptr env,
                                     PslExpr expr, PslExpr expr_list);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr psl_expr_make_obe_unary(const NuSMVEnv_ptr env,
                                       PslOp op, PslExpr expr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_obe_binary(const NuSMVEnv_ptr env,
                         PslExpr left, PslOp op, PslExpr right);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_bit_selection(const NuSMVEnv_ptr env,
                            PslExpr word_expr, PslExpr left, PslExpr right);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
PslExpr
psl_expr_make_word_concatenation(const NuSMVEnv_ptr env,
                                 PslExpr left, PslExpr right);


#endif /* __NUSMV_CORE_PARSER_PSL_PSL_EXPR_H__ */
