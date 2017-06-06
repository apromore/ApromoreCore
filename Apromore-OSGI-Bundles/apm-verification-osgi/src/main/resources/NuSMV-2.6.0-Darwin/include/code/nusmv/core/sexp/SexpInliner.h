/* ---------------------------------------------------------------------------


  This file is part of the ``sexp'' package of NuSMV version 2.
  Copyright (C) 2008 by FBK-irst.

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
  \brief The SexpInliner API

  Class SexpInliner declaration

*/


#ifndef __NUSMV_CORE_SEXP_SEXP_INLINER_H__
#define __NUSMV_CORE_SEXP_SEXP_INLINER_H__

#include "nusmv/core/wff/ExprMgr.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/set/set.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct SexpInliner
  \brief The SexpInliner type 

  The SexpInliner type 
*/
typedef struct SexpInliner_TAG* SexpInliner_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SEXP_INLINER(x) \
         ((SexpInliner_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SEXP_INLINER_CHECK_INSTANCE(x) \
         (nusmv_assert(SEXP_INLINER(x) != SEXP_INLINER(NULL)))

/*!
  \struct InlineRes
  \brief Inliner result type 

  Inliner result type 
*/
typedef struct InlineRes_TAG* InlineRes_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define INLINE_RES(x) \
         ((InlineRes_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define INLINE_RES_CHECK_INSTANCE(x) \
         (nusmv_assert(INLINE_RES(x) != INLINE_RES(NULL)))


/*---------------------------------------------------------------------------*/
/* Public Function Interface                                                 */
/*---------------------------------------------------------------------------*/


/* ===================  SexpInliner  =================== */

/*!
  \methodof SexpInliner
  \brief Sexp Inliner constructor

  fixpoint_limit is a integer bound controlling the
                      maximum number of iteration to be carried out
                      when inlining an expression. Use 0 (zero) for
                      no limit.
*/
SexpInliner_ptr SexpInliner_create(SymbTable_ptr st,
                                          const size_t fixpoint_limit);

/*!
  \methodof SexpInliner
  \brief Copy costructor

  
*/
SexpInliner_ptr SexpInliner_copy(const SexpInliner_ptr self);

/*!
  \methodof SexpInliner
  \brief Destructor

  
*/
void SexpInliner_destroy(SexpInliner_ptr self);

/*!
  \methodof SexpInliner
  \brief Returns the symbol table that is connected to the
                      BoolEnc instance connected to self

  
*/
SymbTable_ptr
SexpInliner_get_symb_table(const SexpInliner_ptr self);

/*!
  \methodof SexpInliner
  \brief Forces to learn that var (can be timed) and expr are
                      equivalent. The expression is assumed to be
                      already flattened, and defines expanded.

  The equivalence is learnt even if given name is
                      blacklisted. The equivalence substitutes any
                      previously forced equivalence about the same
                      variable. Returns true if the equivalence was
                      accepted, or false otherwise.
*/
boolean
SexpInliner_force_equivalence(SexpInliner_ptr self,
                              node_ptr var, Expr_ptr expr);

/*!
  \methodof SexpInliner
  \brief Forces to learn all equivalences in given set

  There is an implicit assumption about the format of
                      each element in the set. It must be either a
                      EQUAL, a EQDEF or a IFF node where left
                      operand is a variable. This method may be
                      useful to force equivalences previously
                      returned by a InlineRes instance.

                      Returns true if any equivalence was accepted,
                      false if all were rejected
*/
boolean
SexpInliner_force_equivalences(SexpInliner_ptr self, Set_t equivs);

/*!
  \methodof SexpInliner
  \brief Forces to learn that var and expr are
                      invariantly equivalent

  var must be a flat variable name (not nexted not
                      timed). The expression is assumed to be
                      already flattened.
                      The invariant is learnt even if given name is
                      blacklisted. The invariant substitutes any
                      previously forced invariant about the same
                      variable.

                      Returns true if the invariant was successfully
                      forced, or false otherwise.
*/
boolean
SexpInliner_force_invariant(SexpInliner_ptr self,
                            node_ptr var, Expr_ptr expr);

/*!
  \methodof SexpInliner
  \brief Forces to learn all invariants in given set

  There is an implicit assumption about the format of
                      each element in the set. It must be either a
                      EQUAL, a EQDEF, or a IFF node where left
                      operand is a variable. This method may be
                      useful to force invariants previously
                      returned by a InlineRes instance.

                      Returns true if any invariant was accepted,
                      false if all were rejected
*/
boolean
SexpInliner_force_invariants(SexpInliner_ptr self, Set_t invars);

/*!
  \methodof SexpInliner
  \brief Adds to the blacklist the given name

  Any name occurring in the blacklist will be not
                      substituted. Use to avoid inlining a set of variables.
*/
void
SexpInliner_blacklist_name(SexpInliner_ptr self, node_ptr var);

/*!
  \methodof SexpInliner
  \brief Clears the internal cache of forced equivalences

  
*/
void
SexpInliner_clear_equivalences(SexpInliner_ptr self);

/*!
  \methodof SexpInliner
  \brief Clears the internal cache of forced invariants

  
*/
void
SexpInliner_clear_invariants(SexpInliner_ptr self);

/*!
  \methodof SexpInliner
  \brief Clears the internal set of blacklisted names.

  
*/
void
SexpInliner_clear_blacklist(SexpInliner_ptr self);

/*!
  \methodof SexpInliner
  \brief Performs inlining of given expression

  Applies inlining to the given expression, with fixpoint.

                 Returned InlineRes object contains the
                 result. Returned instance must be destroyed by the
                 caller. If given variable changed is not NULL, it
                 will be set to true if any inlining has been
                 applied, or will be set to false if no inlining
                 has been applied.

                 Before carrying out the actual inlining, this
                 method learn automatically equivalences out of the
                 given formula.

                 WARNING: The expression is assumed to be already
                 flattened, and normalized (all nodes created with
                 find_node)
*/
InlineRes_ptr
SexpInliner_inline(SexpInliner_ptr self, Expr_ptr expr,
                   boolean* changed);

/*!
  \methodof SexpInliner
  \brief Performs inlining of given expression

  Applies inlining to the given expression, with
  fixpoint, and returns the result expression.


  If given variable changed is not NULL, it will be set to true if
  any inlining has been applied, or will be set to false if no
  inlining has been applied.

  Before carrying out the actual inlining, this method learn
  automatically equivalences out of the given formula.
  [MD] Is this comment wrong? Simply cut and paste from the method
  [MD] SexpInliner_inline?

  WARNING: The expression is assumed to be already flattened, and
  normalized (all nodes created with find_node)

  \se SexpInliner_inline
*/
Expr_ptr
SexpInliner_inline_no_learning(SexpInliner_ptr self, Expr_ptr expr,
                               boolean* changed);

/*!
  \methodof SexpInliner
  \brief Get the internal var2expr hash

  Get the internal var2expr hash. Do not perform any
  side-effects on this hash
*/
hash_ptr
SexpInliner_get_var2expr_hash(SexpInliner_ptr self);

/*!
  \methodof SexpInliner
  \brief Get the internal var2invar hash

  Get the internal var2invar hash. Do not perform any
  side-effects on this hash
*/
hash_ptr
SexpInliner_get_var2invar_hash(SexpInliner_ptr self);

/* ===================  InlineRes  =================== */

/*!
  \methodof InlineRes
  \brief Class destroyer

  
*/
void InlineRes_destroy(InlineRes_ptr self);

/*!
  \methodof InlineRes
  \brief Returns the original expression which has been inlined

  
*/
Expr_ptr
InlineRes_get_original_expr(const InlineRes_ptr self);

/*!
  \methodof InlineRes
  \brief Composes the whole result, making the conjuction of the
                 inlined expression, equivalences and invariants

  
*/
Expr_ptr
InlineRes_get_result(const InlineRes_ptr self);

/*!
  \methodof InlineRes
  \brief Composes the whole result, making the conjuction of the
                 inlined expression, equivalences and invariants

  The equivalences and the invariants are sorted before being
  conjuncted to the inlined expression, to return a unique expression.
*/
Expr_ptr
InlineRes_get_result_unique(const InlineRes_ptr self);

/*!
  \methodof InlineRes
  \brief Returns the inlined expression, without equivalences and
                 invariants

  
*/
Expr_ptr
InlineRes_get_inlined_expr(const InlineRes_ptr self);

/*!
  \methodof InlineRes
  \brief Returns the extracted and forced equivalences as an
                 expression

  
*/
Expr_ptr
InlineRes_get_equivalences_expr(const InlineRes_ptr self);

/*!
  \methodof InlineRes
  \brief Returns the extracted and forced equivalences as a set

  Returned set belongs to self, do not free it
*/
Set_t
InlineRes_get_equivalences(const InlineRes_ptr self);

/*!
  \methodof InlineRes
  \brief Returns the conjuction of all forced invariants

  
*/
Expr_ptr
InlineRes_get_invariant_expr(const InlineRes_ptr self);

/*!
  \methodof InlineRes
  \brief Returns the extracted and forced invariants as a set

  Returned set belongs to self, do not free it
*/
Set_t
InlineRes_get_invariants(const InlineRes_ptr self);

#endif /* __SEXP_INLINER_H__ */
