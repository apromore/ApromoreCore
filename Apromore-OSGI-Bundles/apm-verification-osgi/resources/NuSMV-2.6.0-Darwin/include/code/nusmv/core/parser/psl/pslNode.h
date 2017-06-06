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
  \brief PslNode interface

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_PARSER_PSL_PSL_NODE_H__
#define __NUSMV_CORE_PARSER_PSL_PSL_NODE_H__

#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/error.h"
#include "nusmv/core/utils/ErrorMgr.h"

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
typedef node_ptr PslNode_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef short int PslOp;

typedef enum PslOpConvType_TAG {
  TOK2PSL,
  TOK2SMV,
  PSL2SMV,
  PSL2PSL,
  PSL2TOK
} PslOpConvType;


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief This value represents a null PslNode

  
*/
#define PSL_NULL ((PslNode_ptr) NULL)

/*!
  \brief Casts the given node to an int

  
*/
#define PSLNODE_TO_INT(x) \
    ((int) (nusmv_ptrint) x)

/*!
  \brief Casts the given int to a PslNode_ptr

  
*/
#define PSLNODE_FROM_INT(x) \
    ((PslNode_ptr) (nusmv_ptrint) x)


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Reduces the given PSL formula to an equivalent formula that
                    uses only core symbols. Resulting formula is
                    either LTL of CTL, and can be used for model
                    checking.

  This is the high fcuntion used at system level to convert
                    PSL expression to equivalent expressions that can
                    be managed at system level.  Warning: the
                    resulting expression may have a different
                    structure, do not refer to its structure to report
                    errors to the user, use it internally intstead.

  \se None
*/
node_ptr PslNode_convert_psl_to_core(const NuSMVEnv_ptr env, PslNode_ptr expr);

PslNode_ptr psl_new_node(NodeMgr_ptr nodemgr, PslOp op,
                                PslNode_ptr left, PslNode_ptr right);

/*!
  \brief Returns the given expression's left branch

  

  \se None
*/
PslNode_ptr psl_node_get_left(PslNode_ptr n);

/*!
  \brief Returns the given expression's right branch

  

  \se None
*/
PslNode_ptr psl_node_get_right(PslNode_ptr n);

/*!
  \brief Returns the given expression's top level operator

  

  \se None
*/
PslOp psl_node_get_op(PslNode_ptr n);
void psl_node_set_left(PslNode_ptr n, PslNode_ptr l);
void psl_node_set_right(PslNode_ptr n, PslNode_ptr r);

/*!
  \brief Casts a PslNode_ptr to a node_ptr

  The returned structure will still contain operators
in the SMV parser's domain

  \se None
*/
PslNode_ptr PslNode_convert_from_node_ptr(node_ptr expr);

/*!
  \brief Casts a node_ptr to a PslNode_ptr

  The returned structure will still contain operators
in the PSL parser's domain

  \se None
*/
node_ptr PslNode_convert_to_node_ptr(PslNode_ptr expr);

/*!
  \brief Creates a new TRUE node

  

  \se None
*/
PslNode_ptr psl_node_make_true(NodeMgr_ptr nodemgr);

/*!
  \brief Creates a new FALSE node

  

  \se None
*/
PslNode_ptr psl_node_make_false(NodeMgr_ptr nodemgr);

/*!
  \brief Checks if a node is a TRUE node

  

  \se None
*/
boolean psl_node_is_true(PslNode_ptr e);

/*!
  \brief Checks if a node is a FALSE node

  

  \se None
*/
boolean psl_node_is_false(PslNode_ptr e);

PslNode_ptr psl_node_prune(NodeMgr_ptr nodemgr,
                                  PslNode_ptr tree, PslNode_ptr branch);

boolean psl_node_is_sere(PslNode_ptr expr);
PslNode_ptr psl_node_sere_star_get_count(const PslNode_ptr e);
boolean psl_node_is_handled_star(const NuSMVEnv_ptr env,
                                        PslNode_ptr expr, boolean toplevel);
boolean psl_node_sere_is_propositional(PslNode_ptr e);
boolean psl_node_sere_is_repeated(PslNode_ptr e);
boolean psl_node_sere_is_star(PslNode_ptr e);

/*!
  \brief Getter for a star sere

  

  \se None
*/
PslNode_ptr psl_node_sere_star_get_starred(PslNode_ptr e);

boolean psl_node_sere_is_stareq(PslNode_ptr e);
boolean psl_node_sere_is_starminusgt(PslNode_ptr e);
boolean psl_node_sere_is_standalone_star(PslNode_ptr e);
boolean psl_node_sere_is_plus(PslNode_ptr e);
boolean psl_node_sere_is_standalone_plus(PslNode_ptr e);
boolean psl_node_sere_is_star_count(PslNode_ptr e);

/*!
  \brief Returns true if the given expr is a star sere with
count zero

  

  \se None
*/
boolean psl_node_sere_is_star_count_zero(PslNode_ptr e);
boolean psl_node_sere_is_concat_holes_free(PslNode_ptr e);
boolean psl_node_sere_is_concat_fusion(PslNode_ptr e);
boolean psl_node_sere_is_concat_fusion_holes_free(PslNode_ptr e);
boolean psl_node_sere_is_2ampersand(PslNode_ptr e);

boolean psl_node_is_suffix_implication(PslNode_ptr expr);
boolean psl_node_is_suffix_implication_weak(PslNode_ptr expr);
boolean psl_node_is_suffix_implication_strong(PslNode_ptr expr);

boolean psl_node_is_propstar(PslNode_ptr e);

boolean psl_node_is_ite(PslNode_ptr _ite);
PslNode_ptr psl_node_get_ite_cond(PslNode_ptr _ite);
PslNode_ptr psl_node_get_ite_then(PslNode_ptr _ite);
PslNode_ptr psl_node_get_ite_else(PslNode_ptr _ite);

boolean psl_node_is_case(PslNode_ptr _case);
PslNode_ptr psl_node_get_case_cond(PslNode_ptr _case);
PslNode_ptr psl_node_get_case_then(PslNode_ptr _case);
PslNode_ptr psl_node_get_case_next(PslNode_ptr _case);

/*!
  \brief Returns true if the given expression is a SERE in the form {a}

  

  \se None

  \sa psl_node_is_sere
*/
boolean psl_node_is_serebrackets(PslNode_ptr e);

/*!
  \brief Returns true if the given expression is a concat.

  Returns true if the top level operator is a concat.

  \se None

  \sa psl_node_sere_is_concat_fusion,
                    psl_node_sere_is_fusion
*/
boolean psl_node_sere_is_concat(PslNode_ptr e);

/*!
  \brief Returns true if the given expression is a fusion.

  Returns true if the top level operator is a fusion.

  \se None

  \sa psl_node_sere_is_concat_fusion,
                    psl_node_sere_is_concat
*/
boolean psl_node_sere_is_fusion(PslNode_ptr e);

/*!
  \brief Returns the left operand of a concat.

  

  \se None
*/
PslNode_ptr psl_node_sere_concat_get_left(PslNode_ptr e);

/*!
  \brief Returns the right operand of a concat.

  

  \se None
*/
PslNode_ptr psl_node_sere_concat_get_right(PslNode_ptr e);

/*!
  \brief Returns the leftmost element of a concat sere

  
*/
PslNode_ptr psl_node_sere_concat_get_leftmost(PslNode_ptr e);

/*!
  \brief Returns the rightmost element of a concat sere

  
*/
PslNode_ptr psl_node_sere_concat_get_rightmost(PslNode_ptr e);

/*!
  \brief Cuts the leftmost element of a concat sere

  
*/
PslNode_ptr psl_node_sere_concat_cut_leftmost(NodeMgr_ptr nodemgr,
                                                     PslNode_ptr e);

/*!
  \brief Returns the left operand of a fusion.

  

  \se None
*/
PslNode_ptr psl_node_sere_fusion_get_left(PslNode_ptr e);

/*!
  \brief Returns the right operand of a fusion.

  

  \se None
*/
PslNode_ptr psl_node_sere_fusion_get_right(PslNode_ptr e);

/*!
  \brief Returns true if the given expression is an or.

  Duplicate of psl_node_sere_is_disj.

  \se None

  \sa psl_node_sere_is_disj
*/
boolean psl_node_sere_is_or(PslNode_ptr e);

/*!
  \brief Returns the expression in a propositional sere.

  

  \se None
*/
PslNode_ptr psl_node_sere_propositional_get_expr(PslNode_ptr e);

/*!
  \brief Returns the left operand of a compound sere.

  

  \se None
*/
PslNode_ptr psl_node_sere_compound_get_left(PslNode_ptr e);

/*!
  \brief Returns the right operand of a compound sere.

  

  \se None
*/
PslNode_ptr psl_node_sere_compound_get_right(PslNode_ptr e);

/*!
  \brief Maker for a propositional sere

  

  \se None
*/
PslNode_ptr psl_node_make_sere_propositional(NodeMgr_ptr nodemgr,
                                                    PslNode_ptr expr);

/*!
  \brief Maker for a concatenation sere

  

  \se None
*/
PslNode_ptr psl_node_make_sere_concat(NodeMgr_ptr nodemgr,
                                             PslNode_ptr seq1, PslNode_ptr seq2);

/*!
  \brief Maker for a && sere

  

  \se None
*/
PslNode_ptr psl_node_make_sere_2ampersand(NodeMgr_ptr nodemgr,
                                                 PslNode_ptr seq1, PslNode_ptr seq2);

/*!
  \brief Maker for a star sere

  

  \se None
*/
PslNode_ptr psl_node_make_sere_star(NodeMgr_ptr nodemgr,
                                           PslNode_ptr seq);

PslNode_ptr psl_node_make_sere_compound(NodeMgr_ptr nodemgr,
                                               PslNode_ptr seq1, PslOp op,
                                               PslNode_ptr seq2);
boolean psl_node_is_sere_compound_binary(PslNode_ptr e);

PslNode_ptr psl_node_make_cons(NodeMgr_ptr nodemgr,
                                      PslNode_ptr elem, PslNode_ptr next);
PslNode_ptr psl_node_make_cons_new(NodeMgr_ptr nodemgr,
                                          PslNode_ptr elem, PslNode_ptr next);

boolean psl_node_is_boolean_type(PslNode_ptr expr);

boolean psl_node_is_leaf(PslNode_ptr expr);

/*!
  \brief Returns true if the given node is the PSL syntactic value
'inf'

  

  \se None
*/
boolean psl_node_is_infinite(PslNode_ptr expr);
boolean psl_node_is_id(PslNode_ptr expr);
boolean psl_node_is_id_equal(PslNode_ptr _id1, PslNode_ptr _id2);

/*!
  \brief Returns true if the given expression is an integer number

  

  \se None
*/
boolean psl_node_is_number(PslNode_ptr e);

/*!
  \brief Returns true if the given expression is a word number

  

  \se None
*/
boolean psl_node_is_word_number(PslNode_ptr e);

PslNode_ptr psl_node_make_number(NodeMgr_ptr nodemgr,
                                        int value);

/*!
  \brief Returns the integer value associated with the given number
node. 

  

  \se None
*/
int psl_node_number_get_value(PslNode_ptr e);
boolean psl_node_is_num_equal(PslNode_ptr _id1, PslNode_ptr _id2);

/*!
  \brief Maker for a FAILURE node

  

  \se None
*/
PslNode_ptr
psl_node_make_failure(NodeMgr_ptr nodemgr, const char* msg, FailureKind kind);

/*!
  \brief Maker for a CASE node

  

  \se None
*/
PslNode_ptr
psl_node_make_case(NodeMgr_ptr nodemgr,
                   PslNode_ptr _cond,
                   PslNode_ptr _then, PslNode_ptr _next);

/*!
  \brief Returns true if the given node is a range

  

  \se None
*/
boolean psl_node_is_range(PslNode_ptr expr);

/*!
  \brief Returns the low bound of the given range

  

  \se None
*/
PslNode_ptr psl_node_range_get_low(PslNode_ptr expr);

/*!
  \brief Returns the high bound of the given range

  

  \se None
*/
PslNode_ptr psl_node_range_get_high(PslNode_ptr expr);

boolean psl_node_is_cons(PslNode_ptr e);

/*!
  \brief Returns the currently pointed element of a list

  

  \se None
*/
PslNode_ptr psl_node_cons_get_element(PslNode_ptr e);

/*!
  \brief Returns the next element of a list

  

  \se None
*/
PslNode_ptr psl_node_cons_get_next(PslNode_ptr e);
PslNode_ptr psl_node_cons_reverse(PslNode_ptr e);

PslNode_ptr psl_node_suffix_implication_get_premise(PslNode_ptr e);
PslNode_ptr psl_node_suffix_implication_get_consequence(PslNode_ptr e);
PslNode_ptr psl_node_sere_repeated_get_expr(PslNode_ptr e);

/*!
  \brief Returns the count associated to the repeated sere

  

  \se None
*/
PslNode_ptr psl_node_sere_repeated_get_count(PslNode_ptr e);

/*!
  \brief Returns the count associated to the repeated sere

  

  \se None
*/
PslOp psl_node_sere_repeated_get_op(PslNode_ptr e);

boolean psl_node_is_repl_prop(PslNode_ptr _prop);
PslNode_ptr psl_node_repl_prop_get_property(PslNode_ptr _prop);
PslNode_ptr psl_node_repl_prop_get_replicator(PslNode_ptr _prop);

boolean psl_node_is_replicator(PslNode_ptr _repl);

/*!
  \brief Given a replicator, returns the its values set.

  

  \se None
*/
PslNode_ptr psl_node_get_replicator_value_set(PslNode_ptr _repl);

/*!
  \brief Given a replicator, returns the operator joining each
replicated expression

  

  \se None
*/
PslOp psl_node_get_replicator_join_op(PslNode_ptr _repl);

/*!
  \brief Given a replicator, returns its values set as a list
of the enumerated values

  

  \se required

  \sa optional
*/
PslNode_ptr
psl_node_get_replicator_normalized_value_set(const NuSMVEnv_ptr env,
                                             PslNode_ptr rep);

/*!
  \brief Given a replicator, returns its range

  

  \se None
*/
PslNode_ptr psl_node_get_replicator_range(PslNode_ptr _repl);

/*!
  \brief Given a replicator, returns the its ID

  

  \se None
*/
PslNode_ptr psl_node_get_replicator_id(PslNode_ptr _repl);

/*!
  \brief Contestualizes a context node into the 'main' context 

  This function is used to build the internal structure of
   the context (e.g. module instance name) from the parse tree. The
   function is needed since with the grammar it is not possible/simple
   to build directly the desired structure.

  \se None
*/
PslNode_ptr psl_node_context_to_main_context(NodeMgr_ptr nodemgr,
                                                    PslNode_ptr context);

/*!
  \brief Creates a psl node that represents a contestualized
node

  

  \se None
*/
PslNode_ptr PslNode_new_context(NodeMgr_ptr nodemgr,
                                       PslNode_ptr ctx, PslNode_ptr node);

PslNode_ptr psl_node_make_extended_next(NodeMgr_ptr nodemgr,
                                               PslOp op, PslNode_ptr expr,
                                               PslNode_ptr when,
                                               PslNode_ptr condition);
boolean psl_node_is_extended_next(PslNode_ptr e);

/*!
  \brief Returns the FL expression of a next expression node

  

  \se None
*/
PslNode_ptr psl_node_extended_next_get_expr(PslNode_ptr next);

/*!
  \brief Returns the when component of a next expression node

  

  \se None
*/
PslNode_ptr psl_node_extended_next_get_when(PslNode_ptr next);

/*!
  \brief Returns the boolean condition of a next expression node

  

  \se None
*/
PslNode_ptr
psl_node_extended_next_get_condition(PslNode_ptr next);



/* Predicates */

/*!
  \brief Returns true iff given expression can be translated
into LTL.

  

  \se None

  \sa optional
*/
boolean PslNode_is_handled_psl(const NuSMVEnv_ptr env, PslNode_ptr e);

/*!
  \brief Checks for a formula being a propositional formula

  Checks for a formula being a propositional formula

  \se None

  \sa PslNode_is_trans_propositional
*/
boolean PslNode_is_propositional(const PslNode_ptr expr);

/*!
  \brief Checks for a formula being a propositional formula

  Checks for a formula being a propositional formula,
                    next operator here leaves the formula propositional

  \se None

  \sa PslNode_is_propositional
*/
boolean PslNode_is_trans_propositional(const PslNode_ptr expr);

/*!
  \brief Checks if a propositional formula contains a next

  Checks for a formula being a propositional formula

  \se None

  \sa optional
*/
boolean PslNode_propositional_contains_next(const PslNode_ptr expr);

/*!
  \brief Checks for a formula being an CTL formula

  Checks for a formula being an CTL formula

  \se None

  \sa optional
*/
boolean PslNode_is_obe(const PslNode_ptr expr);

/*!
  \brief Checks for a formula being an LTL formula

  Checks for a formula being an LTL formula

  \se None

  \sa optional
*/
boolean PslNode_is_ltl(const PslNode_ptr expr);

/* convert */
PslNode_ptr PslNode_convert_id(const NuSMVEnv_ptr env, PslNode_ptr id, PslOpConvType type);
PslNode_ptr PslNode_pslobe2ctl(const NuSMVEnv_ptr env, PslNode_ptr expr, PslOpConvType type);
PslNode_ptr PslNode_pslltl2ltl(const NuSMVEnv_ptr env, PslNode_ptr expr, PslOpConvType type);
PslNode_ptr PslNode_remove_sere(const NuSMVEnv_ptr env, PslNode_ptr e);
PslNode_ptr PslNode_remove_forall_replicators(const NuSMVEnv_ptr env, PslNode_ptr e);


#endif /* __NUSMV_CORE_PARSER_PSL_PSL_NODE_H__ */
