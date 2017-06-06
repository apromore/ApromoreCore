/* ---------------------------------------------------------------------------


  This file is part of the ``compile'' package.
  %COPYRIGHT%


-----------------------------------------------------------------------------*/

/*!
  \author Michele Dorigatti
  \brief Utility functions for compile

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_COMPILE_COMPILE_UTIL_H__
#define __NUSMV_CORE_COMPILE_COMPILE_UTIL_H__

#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/cinit/NuSMVEnv.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/node/NodeMgr.h"
#include "nusmv/core/node/node.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Given an expression, it creates a symbol which name
   is the concatenation of prefix, the expression itself as a string and suffix

  The new symbol is find_noded. This function does not
   declare the symbol in a Symbol Table and does not check if it has been
   already declared.
   Passing empty strings as both prefix and suffix gives a symbol whose name is
   the expression itself.
   A possible improvement could be adding suitable defaults for prefix and
   suffix.

###############################################################################
   WARNING: This function is a draft, and has not be tested for every case.

   In particular, newline characters (it should happen with CASE) in the string
   returned by sprint_node are not correctly handled.

###############################################################################


  \sa SymbTable_get_fresh_symbol_name, for an alternative way
   to achieve a similar result
*/
node_ptr Compile_Util_symbol_from_expr(NuSMVEnv_ptr const env,
                                              node_ptr const var,
                                              const char* prefix,
                                              const char* suffix);

/*!
  \brief Builds an internal representation for a given string.

  Builds an internal representation for a given
   string. If the conversion has been performed in the past, then the
   hashed value is returned back, else a new one is created, hashed and
   returned. We hash this in order to allow the following:
   <pre>
   VAR
   x : {a1, a2, a3};
   y : {a3, a4, a5};

   ASSIGN
   next(x) := case
   x = y    : a2;
   !(x = y) : a1;
   1        : a3;
   esac;
   </pre>
   i.e. to allow the equality test between x and y. This can be
   performed because we internally have a unique representation of the
   atom <tt>a3</tt>.

   ownership of char* is NOT taken

  \sa find_atom
*/
node_ptr sym_intern(const NuSMVEnv_ptr env, const char*);

/*!
  \bref Like sym_intern, but taking a string_ptr instead of C string

  \sa sym_intern
*/
node_ptr sym_intern_from_ustring(const NuSMVEnv_ptr env,
                                 const string_ptr _string);

/*!
  \brief Compares two names and returns true iff the names are
   equal.

  This can be applied only on names containing DOT and
   ATOM, and work well with both find-noded and new-noded nodes,
   even independently on the NodeMgr the names were built with.
*/
boolean
sym_names_are_equal(const NuSMVEnv_ptr env,
                    node_ptr name1, node_ptr name2);

/*!
  \brief Simplifies the given property by exploiting
   the distributivity of G, AG and H over AND, and distributivity of F, AF and O
   over OR

  Transformation rules are:
   1) <OP> <OP> a           :-> <OP> a
   2) (<OP> a) * (<OP> b)   :-> <OP> (a * b);
   3) (<OP> (a * <OP> b))   :-> <OP> (a * b);
   4) (<OP> (<OP> a * b))   :-> <OP> (a * b);
   5) (<OP> (<OP> a * <OP> b)) :-> <OP> (a * b);

   Where <OP> can be either:
   G|AG|H for * := &
   F|AF|O for * := |

   Given property can be both flattened or unflattened.

*/
node_ptr Compile_pop_distrib_ops(const NuSMVEnv_ptr env,
                                        node_ptr prop);


/*!
  \brief Remove bounded LTL temporal operators if any.

  Transformation rules are:
   1) F [l, u] p            :-> X^l( p | X ( p | ... | X p))
   2) G [l, u] p            :-> X^l( p & X ( p & ... & X p))
   3) O [l, u] p            :-> Y^l( p | Y ( p | ... | Y p))
   4) H [l, u] p            :-> Y^l( p & Y ( p & ... & Y p))

   Given property can be both flattened or unflattened.

*/
node_ptr Compile_remove_ltl_bop(const NuSMVEnv_ptr env, node_ptr prop);

/*!
  \brief This function creates a new list of variables that will
   contain the same symbols into 'vars', but ordered wrt to
   'vars_order' content

  This function can be used to construct an ordered list
   of symbols. The set of symbols is provided by the input list 'vars',
   whereas the ordering is provided by the 'vars_order' list, that can
   be an intersecting set over 'vars'. The resulting list will
   contain those symbols that occur in vars_order (respecting their
   order), plus all the symbols in vars that do not occur in vars_order,
   pushed at the end of the list. All duplicates (if any) will not occur
   into the resulting list. The returned set must be destroyed by the
   caller.
*/
Set_t
Compile_make_sorted_vars_list_from_order(const SymbTable_ptr st,
const NodeList_ptr vars, const NodeList_ptr vars_order);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_COMPILE_COMPILE_UTIL_H__ */
