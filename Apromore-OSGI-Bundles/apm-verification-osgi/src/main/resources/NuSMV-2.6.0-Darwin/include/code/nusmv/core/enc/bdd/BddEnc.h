/* ---------------------------------------------------------------------------

  This file is part of the ``enc.bdd'' package of NuSMV version 2.
  Copyright (C) 2004 by FBK-irst.

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
  \brief Public interface of class 'BddEnc'

  Encoder for bdds, derived from class BoolEncClient

*/



#ifndef __NUSMV_CORE_ENC_BDD_BDD_ENC_H__
#define __NUSMV_CORE_ENC_BDD_BDD_ENC_H__

#include "nusmv/core/enc/bdd/bdd.h"
#include "nusmv/core/enc/base/BoolEncClient.h"
#include "nusmv/core/enc/bool/BoolEnc.h"
#include "nusmv/core/enc/utils/OrdGroups.h"

#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/wff/ExprMgr.h"
#include "nusmv/core/fsm/bdd/bdd.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/dd/VarsHandler.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/utils/OStream.h"

#include "nusmv/core/enc/utils/AddArray.h"

/*!
  \struct BddEnc
  \brief Definition of the public accessor for class BddEnc


*/
typedef struct BddEnc_TAG*  BddEnc_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*VPFNNF)(FILE*, node_ptr, node_ptr);

typedef void (*VPFBEFNNV)(BddEnc_ptr, OStream_ptr, node_ptr, node_ptr, void*);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum { DUMP_FORMAT_INVALID,
               DUMP_FORMAT_DOT,
               DUMP_FORMAT_DAVINCI,
} t_format;

/*!
  \brief To cast and check instances of class BddEnc

  These macros must be used respectively to cast and to check
  instances of class BddEnc
*/
#define BDD_ENC(self) \
         ((BddEnc_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_ENC_CHECK_INSTANCE(self) \
         (nusmv_assert(BDD_ENC(self) != BDD_ENC(NULL)))

/*!
  \brief Used when dumping ordering file

  Used when dumping ordering file
*/
typedef enum {
  DUMP_DEFAULT,
  DUMP_BITS,
  DUMP_SCALARS_ONLY
} VarOrderingType;



/*!
  \brief Set to 1 if you want to enable the LAZY commit of layers within the BDD Enc

   Set to 1 if you want to enable the LAZY commit of layers within the BDD Enc
*/
#define __BDDENC_LAZY_COMMIT_LAYER__ 1

/**AutomaticStart************************************************************/

/*--------------------------------------------------------------------------*/
/* Function prototypes                                                      */
/*--------------------------------------------------------------------------*/

/*!
  \methodof BddEnc
  \brief The BddEnc class constructor

  The BddEnc class constructor. ord_groups can be NULL if
   ordering is not used. self become the owner of the given ord_groups
   instance

  \sa BddEnc_destroy
*/
BddEnc_ptr
BddEnc_create(SymbTable_ptr symb_table,
              BoolEnc_ptr bool_enc, VarsHandler_ptr dd_vars_hndr,
              OrdGroups_ptr ord_groups);

/*!
  \methodof BddEnc
  \brief The BddEnc class destructor

  The BddEnc class destructor

  \sa BddEnc_create
*/
VIRTUAL
void BddEnc_destroy(BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Gets the DD vars handler this encoding refers to.

  Gets the DD vars handler this encoding refers to.
*/
VarsHandler_ptr
BddEnc_get_dd_vars_handler(const BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Gets the DD manager this encoding refers to.

  Gets the DD manager this encoding refers to.
*/
DDMgr_ptr BddEnc_get_dd_manager(const BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Returns the internally used order that was specified
   when creating the instance. Order is used when committing a layer and
   when forcing a reordering

  Can be NULL. The returned instance belongs to
   self. Do not change it if you do not know well what you are
   doing.
*/
OrdGroups_ptr BddEnc_get_ord_groups(const BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Returns the ADD representing the expression expr, in
   the given context

  Returned add is referenced. A NULL value of the
   provided expression (expr) corresponds to a true ADD returned as result.

   NOTE: Mostly expressions must be type checked before being
   evaluated. For example, use TypeChecker_is_expression_wellformed to
   type check generated expression. FSM should be checked with
   TypeCheckingPkg_check_constrains, and a property should be checked
   with TypeCheckingPkg_check_property
*/
add_ptr
BddEnc_expr_to_add(BddEnc_ptr self, const Expr_ptr expr,
                   const node_ptr context);

/*!
  \methodof BddEnc
  \brief Returns the ADD array representing the
   expression expr, in the given context

  Each element of the returned add array is
   referenced. A NULL value of the provided expression (expr)
   corresponds to a true ADD array returned as result.

   The returned array will belong to the invoker.

   NOTE: Mostly expressions must be type checked before being
   evaluated. For example, use TypeChecker_is_expression_wellformed to
   type check generated expression. FSM should be checked with
   TypeCheckingPkg_check_constrains, and a property should be checked
   with TypeCheckingPkg_check_property
*/
AddArray_ptr
BddEnc_expr_to_addarray(BddEnc_ptr self, const Expr_ptr expr,
                        const node_ptr context);

/*!
  \methodof BddEnc
  \brief Returns the BDD representing the expression expr, in
   the given contex

  Returned bdd is referenced.

   NOTE: Mostly expressions must be type checked before being
   evaluated. For example, use TypeChecker_is_expression_wellformed to
   type check generated expression. FSM should be checked with
   TypeCheckingPkg_check_constrains, and a property should be checked
   with TypeCheckingPkg_check_property
*/
bdd_ptr
BddEnc_expr_to_bdd(BddEnc_ptr self, const Expr_ptr expr,
                   const node_ptr context);

/*!
  \methodof BddEnc
  \brief Converts a ADD into the corresponding (boolean)
   expression.

  Takes an ADD with leaves FALSE, TRUE, or {FALSE,TRUE}.

   The case of {FALSE,TRUE} leaves is determinized if a valid layer is
   passed, otherwise it is preserved.

   Important: if a valid determinization layer is given, the layer
   cannot be alreay commited to the encoder, and will have to be
   possibly committed later if the returned expression is intended to
   be used by this encoder in terms of ADD or BDD.

   Recurs down on the structure of the ADD, and maps each non terminal
   node into an if-then-else expression, maps FALSE and TRUE terminal nodes
   into true and false expressions, and maps {FALSE,TRUE} into a newly
   introduced variable to determinize the expression.

  \se A new boolean variable can be declared within det_layer

  \sa bdd_enc_add2expr_recur
*/
node_ptr
BddEnc_add_to_expr(BddEnc_ptr self, const add_ptr add,
                   SymbLayer_ptr det_layer);

/*!
  \methodof BddEnc
  \brief Converts a ADD into the corresponding (possibly scalar)
   expression.

  Takes an ADD and converts it to the corresponding
   scalar expression.

   Non deterministic leaves will be determinized only if a valid layer is passed.
   Important: if a valid determinization layer is given, the layer cannot
   be alreay commited to the encoder, and will have to be possibly committed
   later if the returned expression is intended to be used by this encoder in
   terms of ADD or BDD.

   Recurs down on the structure of the ADD, and maps each non terminal
   node into an if-then-else expression

  \se A new scalar variable may be declared within det_layer

  \sa bdd_enc_add2expr_recur
*/
node_ptr
BddEnc_add_to_scalar_expr(BddEnc_ptr self, const add_ptr add,
                          SymbLayer_ptr det_layer);

/*!
  \methodof BddEnc
  \brief Converts a BDD into the corresponding (boolean)
   expression.



  \sa bdd_enc_add2expr_recur
*/
node_ptr
BddEnc_bdd_to_expr(BddEnc_ptr self, const bdd_ptr bdd);

/*!
  \methodof BddEnc
  \brief Gets the support of the set of state variables

  Returned bdd is referenced, the caller must free it after
   it is no longer used. Result is cached if not previously converted from
   internal ADD representation.  Returns NULL if an error occurred.
*/
BddVarSet_ptr
BddEnc_get_state_vars_cube(const BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Gets the support of the set of next-state variables

  Returned bdd is referenced, the caller must free it after
   it is no longer used.  Result is cached if not previously converted from
   internal ADD representation. Returns NULL if an error occurred.
*/
BddVarSet_ptr
BddEnc_get_next_state_vars_cube(const BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Gets the support of the set of frozen variables

  Returned bdd is referenced, the caller must free it after
   it is no longer used.  Result is cached if not previously converted from
   internal ADD representation. Returns NULL if an error occurred.
*/
BddVarSet_ptr
BddEnc_get_frozen_vars_cube(const BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Gets the support of the set of state and frozen variables

  The result is a conjunct of BddEnc_get_state_vars_cube and
   BddEnc_get_frozen_vars_cube.

   Returned bdd is referenced, the caller must free it after
   it is no longer used.  Result is cached if not previously converted from
   internal ADD representation. Returns NULL if an error occurred.
*/
BddVarSet_ptr
BddEnc_get_state_frozen_vars_cube(const BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Gets the support of the set of state, next state and
                       frozen variables

  The result is a conjunction of
   BddEnc_get_state_frozen_vars_cube and BddEnc_get_next_state_vars_cube.

   Returned bdd is referenced, the caller must free it after
   it is no longer used. Returns NULL if an error occurred.
*/
BddVarSet_ptr
BddEnc_get_state_next_state_frozen_vars_cube(const BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Gets the support of the set of input variables

  Returned bdd is referenced, the caller must free it after
   it is no longer used.  Result is cached if not previously converted from
   internal ADD representation.  Returns NULL if an error occurred.
*/
BddVarSet_ptr
BddEnc_get_input_vars_cube(const BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Given a layer the function produces a cube of all
   layer's variables

  vt can be a combination of VFT_CURRENT, VFT_NEXT, VFT_FROZEN,
   VFT_INPUT (see SymbFilterType for combination shortcuts). Returned
   bdd is referenced, the caller must free it after it is no longer
   used.
*/
BddVarSet_ptr
BddEnc_get_layer_vars_cube(const BddEnc_ptr self,
                           SymbLayer_ptr layer,
                           SymbFilterType vt);

/*!
  \methodof BddEnc
  \brief Returns true if the variable is in the cube and false
   otherwise

   Parameter name is a fully-resolved name of variable.
   The cube of this variable is subtracted from the cube given in
   'cube' parameter, and the result is compared with the original
   'cube'. If they are different then at least a part (one bit, for
   example) of the variable is in the input cube. Therefore true is
   returned.
*/
boolean
BddEnc_is_var_in_cube(const BddEnc_ptr self,
                      node_ptr name, add_ptr cube);

/*!
  \methodof BddEnc
  \brief Exchange next state variables for state variables, in
   terms of ADD

  Given an ADD whose variables are STATE variables,
   returns an isomorphic ADD where NEXT-STATE
   variables have been substituted for the
   corrisponding STATE variables
*/
add_ptr
BddEnc_state_var_to_next_state_var_add(const BddEnc_ptr self,
                                       add_ptr add);

/*!
  \methodof BddEnc
  \brief Exchange state variables for next state variables in terms
   of ADD

  Given an ADD whose variables are NEXT-STATE variables,
   returns an isomorphic ADD where STATE variables
   have been substituted for the corrisponding
   STATE variables
*/
add_ptr
BddEnc_next_state_var_to_state_var_add(const BddEnc_ptr self,
                                       add_ptr add);

/*!
  \methodof BddEnc
  \brief Exchange next state variables for state variables

  Given a BDD whose variables are STATE variables,
   returns an isomorphic BDD where NEXT-STATE
   variables have been substituted for the
   corrisponding STATE variables
*/
bdd_ptr
BddEnc_state_var_to_next_state_var(const BddEnc_ptr self, bdd_ptr bdd);

/*!
  \methodof BddEnc
  \brief Exchange state variables for next state variables

  Given a BDD whose variables are NEXT-STATE variables,
   returns an isomorphic BDD where STATE variables
   have been substituted for the corrisponding
   STATE variables
*/
bdd_ptr
BddEnc_next_state_var_to_state_var(const BddEnc_ptr self, bdd_ptr bdd);

/*!
  \methodof BddEnc
  \brief Call before a group of BddEnc_print_bdd calls

  This sets some fileds used by BddEnc_print_bdd.  Also
   clears the table used when printing only changed states.  After
   having called BddEnc_print_bdd, call BddEnc_print_bdd_end.  If
   <tt>changes_only</tt> is true, than only state and frozen variables which
   assume a different value from the previous printed one are printed
   out.
*/
void
BddEnc_print_bdd_begin(BddEnc_ptr self, NodeList_ptr symbols,
                       boolean changes_only);

/*!
  \methodof BddEnc
  \brief Must be called after each call to
   BddEnc_print_bdd_begin

  Must be called after each call to
   BddEnc_print_bdd_begin, in order to clean up some internal structure
*/
void
BddEnc_print_bdd_end(BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Prints the given bdd. In particular prints only the
   symbols occuring in the symbols list passed to print_bdd_begin. Individual
   assignments may be printed using a user-defined function, passed as a
   parameter

  Before calling this method, you must call
   print_bdd_begin. Then you can call this method once or more, but
   eventually you will have to call print_bdd_end to commit.
   Returns the number of symbols actually printed
*/
int
BddEnc_print_bdd(BddEnc_ptr self,
                 bdd_ptr bdd,
                 VPFBEFNNV p_fun,
                 OStream_ptr file,
                 void* arg);

/*!
  \methodof BddEnc
  \brief Prints a set of states. Individual assignments may be
   printed using a user-defined function, passed as a parameter

  Note: states are represented by state and frozen variables
*/
void
BddEnc_print_set_of_states(BddEnc_ptr self,
                           bdd_ptr states,
                           boolean changes_only,
                           boolean print_defines,
                           VPFBEFNNV p_fun,
                           OStream_ptr file,
                           void* arg);

/*!
  \methodof BddEnc
  \brief Prints a set of input pairs. Individual assignments may
   be printed using a user-defined function, passed as a parameter


*/
void
BddEnc_print_set_of_inputs(BddEnc_ptr self,
                           bdd_ptr inputs,
                           boolean changes_only,
                           VPFBEFNNV p_fun,
                           OStream_ptr file,
                           void* arg);

/*!
  \methodof BddEnc
  \brief Prints a set of state-input pairs. Individual
   assignments may be printed using a user-defined function, passed as a
   parameter

  Note: states are represented by state and frozen variables
*/
void
BddEnc_print_set_of_state_input_pairs(BddEnc_ptr self,
                                      bdd_ptr state_input_pairs,
                                      boolean changes_only,
                                      VPFBEFNNV p_fun,
                                      OStream_ptr file,
                                      void* arg);

/*!
  \methodof BddEnc
  \brief Prints a set of models for given trans



  \se none
*/
void
BddEnc_print_set_of_trans_models(BddEnc_ptr self,
                                 bdd_ptr state_input_pairs,
                                 /* boolean changes_only, */
                                 OStream_ptr file);

/*!
  \methodof BddEnc
  \brief This function is similar to
   BddEnc_print_set_of... functions except that instead of
   printing values of variables, this funtion creates a list of pairs
   var-itsValue.



   This functions takes a BDD and a list of symbols (variables or
   defines, both can be wrapped in NEXT), and returns a list of
   (symb, symb_value) which makes BDD not false (input BDD) should not
   be false constant).  Returned list is a list of AND nodes with Nil
   at the end. Every element is a EQUAL node with symbol on the left
   and its value on the right.

   Order of symbols in the returned list is the same of provided
   symbols list. If parameter 'onlyRequiredSymbs' is true then
   symbols whose values are not constrained by provided BDD will be
   skipped. Otherwise, some legal arbitrary values for such symbols
   will be created and returned list will contain all the symbols
   from 'symbols'.

   If parameter resultAssignment is not null pointer, then it
   returns the produced assignments in the form of BDD, i.e. a
   conjunct of all generated equations "symbol = itsValue".

   The input BDD may or may not be a complete assignment. The invoker
   should free the returned list (with free_list) and returned BDD (if any).
   Note, that EQUAL nodes should not be freed/modified as created with find_node.

*/
node_ptr
BddEnc_assign_symbols(BddEnc_ptr self, bdd_ptr bdd,
                      NodeList_ptr symbols,
                      boolean onlyRequiredSymbs,
                      bdd_ptr* resultBdd);

/*!
  \methodof BddEnc
  \brief Prints out the symbolic names of boolean
   variables stored in a cube.

  Given a cube of boolean BDD variables, this
   function prints out the symbolic names of the corresponding
   variables. The symbolic name of the variables to be printed out are
   listed in <tt>list_of_sym</tt>.

  \se None
*/
void
BddEnc_print_vars_in_cube(BddEnc_ptr self, bdd_ptr cube,
                          node_ptr list_of_sym,
                          OStream_ptr file);

/*!
  \methodof BddEnc
  \brief Returns the symbolic names of boolean
   variables stored in a cube.

  Given a cube of boolean BDD variables, this function returns the
   list of symbolic names of the corresponding variables. If NEXT
   variables are also found in the cube, and include_next is true,
   NEXT variables will be also checked, even if not occurring
   explicitly in the input list.

   Returned list must be disposed by the caller.
*/
NodeList_ptr BddEnc_get_vars_in_cube(const BddEnc_ptr self,
                                     bdd_ptr cube,
                                     node_ptr list_of_sym,
                                     boolean include_next);

/*!
  \methodof BddEnc
  \todo
*/
NodeList_ptr BddEnc_get_var_ordering(const BddEnc_ptr self,
                                     const VarOrderingType ord_type);

/*!
  \methodof BddEnc
  \brief Writes on a file the variable order.

  This function writes the variable order currently in
   use in the system in the specified output file. The file generated
   as output can be used as input order file for next computations. If
   the specified output file is an empty string ("" or NULL, see
   util_is_string_null) output is redirected to stdout.  The output
   content depends on the value of dump_type, and can be either pure
   scalar (for backward compatibility) or single bits

   Ownership of "output_order_file_name" is taken, unless it is the value
   returned by get_output_order_file

  \sa Compile_ReadOrder
*/
int
BddEnc_write_var_ordering(const BddEnc_ptr self,
                          const char* oo_filename,
                          const VarOrderingType dump_type);

/*!
  \methodof BddEnc
  \brief Returns the number of reorderings that have been carried
   out since either the self construction or the last call to method
   reset_reordering_count

  Returns the number of reorderings performed by CUDD
   since the instance creation, or since the last call to method
   reset_reordering_count. Explicit and auto-triggered reorderings are
   counted. Notice that forced ordering due to layers commitment may
   increment the orderings count.

  \sa BddEnc_reset_reordering_count
*/
int BddEnc_get_reordering_count(const BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Resets the reordering count. The value returned by any
   following call to method get_reordering_count will be relative to
   the moment this method had been called

  Resets the reordering count. The value returned by any
   following call to method get_reordering_count will be relative to
   the moment this method had been called.

  \sa BddEnc_get_reordering_count
*/
void BddEnc_reset_reordering_count(BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Return the number of states of a given ADD.

  Return the number of minterms (i.e. states)
   represented by an ADD.
   Note: states are represented by state and frozen variables
*/
double
BddEnc_count_states_of_add(const BddEnc_ptr self, add_ptr add);

/*!
  \methodof BddEnc
  \brief Return the number of states of a given BDD.

  Return the number of states represented by a BDD.
   Note: states are represented by state and frozen variables.
*/
double
BddEnc_count_states_of_bdd(const BddEnc_ptr self, bdd_ptr bdd);

/*!
  \methodof BddEnc
  \brief Return the number of inputs of a given BDD.

  Return the number of inputs represented by a BDD.
*/
double
BddEnc_count_inputs_of_bdd(const BddEnc_ptr self, bdd_ptr bdd);

/*!
  \methodof BddEnc
  \brief Return the number of states inputs of a given BDD.

  Return the number of states inputs represented by a BDD.
   Note: states are represented by state and frozen variables
*/
double
BddEnc_count_states_inputs_of_bdd(const BddEnc_ptr self, bdd_ptr bdd);

/*!
  \methodof BddEnc
  \brief Return the number of minterms of a given ADD.

  Return the number of minterms
   represented by a ADD.
*/
double
BddEnc_get_minterms_of_add(const BddEnc_ptr self, add_ptr add);

/*!
  \methodof BddEnc
  \brief Return the number of minterms of a given BDD.

  Return the number of minterms
   represented by a BDD.
*/
double
BddEnc_get_minterms_of_bdd(const BddEnc_ptr self, bdd_ptr bdd);

/*!
  \methodof BddEnc
  \brief Extracts a minterm from a given BDD.

  Extracts a minterm from a given BDD. Returned
   bdd is referenced.
   Note: states are represented by state and frozen variables.

  \sa bdd_pick_one_minterm
*/
bdd_ptr
BddEnc_pick_one_state(const BddEnc_ptr self, bdd_ptr states);

/*!
  \methodof BddEnc
  \brief Extracts a minterm from a given BDD.

  Extracts a minterm from a given BDD. Returned
   bdd is referenced

  \sa bdd_pick_one_minterm
*/
bdd_ptr
BddEnc_pick_one_input(const BddEnc_ptr self, bdd_ptr inputs);

/*!
  \methodof BddEnc
  \brief Extracts a minterm over input/state variables from a
                       given BDD.

  Extracts a minterm from a given BDD. Returned
   bdd is referenced.
   Note: input-states are represented by input, state and frozen variables.

  \sa bdd_pick_one_minterm, BddEnc_pick_one_state,
                       BddEnc_pick_one_input
*/
bdd_ptr
BddEnc_pick_one_input_state(const BddEnc_ptr self,
                            bdd_ptr inputs_states);

/*!
  \methodof BddEnc
  \brief Returns the array of All Possible Minterms

  Takes a minterm and returns an array of all its terms,
   according to internally kept vars. Notice that
   the array of the result has to be previously allocated, and its size
   must be greater or equal the number of the minterms.
   The returned array contains referenced BDDs so it is necessary to
   dereference them after their use. Returns true if an error occurred.

   Note: states are represented by state and frozen variables.


  \se result_array will change

  \sa bdd_pick_all_terms
*/
boolean
BddEnc_pick_all_terms_states_inputs(const BddEnc_ptr self,
                                    bdd_ptr bdd,
                                    bdd_ptr* result_array,
                                    const int array_len);

/*!
  \methodof BddEnc
  \brief Returns the array of All Possible Minterms

  Takes a minterm and returns an array of all its terms,
   according to internally kept vars. Notice that
   the array of the result has to be previously allocated, and its size
   must be greater or equal the number of the minterms.
   The returned array contains referenced BDD so it is necessary to
   dereference them after their use. Returns true if an error occurred.

   Note: states are represented by state and frozen variables.

  \se result_array will change

  \sa bdd_pick_all_terms
*/
boolean
BddEnc_pick_all_terms_states(const BddEnc_ptr self, bdd_ptr bdd,
                             bdd_ptr* result_array,
                             const int array_len);

/*!
  \methodof BddEnc
  \brief Returns the array of All Possible Minterms

  Takes a minterm and returns an array of all its terms,
   according to internally kept vars. Notice that
   the array of the result has to be previously allocated, and its size
   must be greater or equal the number of the minterms.
   The returned array contains referenced BDD so it is necessary to
   dereference them after their use. Returns true if an error occurred

  \se result_array will change

  \sa bdd_pick_all_terms
*/
boolean
BddEnc_pick_all_terms_inputs(const BddEnc_ptr self, bdd_ptr bdd,
                             bdd_ptr* result_array,
                             const int array_len);

/*!
  \methodof BddEnc
  \brief Extracts a random minterm from a given BDD.

  Extracts a random minterm from a given BDD.
   Returned bdd is referenced.

   Note: states are represented by state and frozen variables.

  \sa bdd_pick_one_minterm_rand
*/
bdd_ptr
BddEnc_pick_one_state_rand(const BddEnc_ptr self, bdd_ptr states);

/*!
  \methodof BddEnc
  \brief Extracts a random minterm from a given BDD.

  Extracts a random minterm from a given BDD.
   Returned bdd is referenced

  \sa bdd_pick_one_minterm_rand
*/
bdd_ptr
BddEnc_pick_one_input_rand(const BddEnc_ptr self, bdd_ptr inputs);

/*!
  \methodof BddEnc
  \brief Extracts a random minterm from a given BDD.

  Extracts a random minterm from a given BDD.
   Returned bdd is referenced.

   Note: input-states are represented by input, state and frozen variables.

  \sa bdd_pick_one_minterm_rand,
                       BddEnc_pick_one_input_rand, BddEnc_pick_one_state_rand
*/
bdd_ptr
BddEnc_pick_one_input_state_rand(const BddEnc_ptr self,
                                 bdd_ptr inputs_states);

/*!
  \methodof BddEnc
  \brief Given a variable index, this method return the
   symbolic name of the correpsonding variable



  \se required

  \sa BddEnc_get_var_index_from_name
*/
node_ptr
BddEnc_get_var_name_from_index(const BddEnc_ptr self, int index);

/*!
  \methodof BddEnc
  \brief Given a variable index, this method return true iff
   the given variable belongs to the encoder



  \se required

  \sa BddEnc_get_var_name_from_index
*/
boolean
BddEnc_has_var_at_index(const BddEnc_ptr self, int index);

/*!
  \methodof BddEnc
  \brief Returns the DD index of the given variable

  The input variable should be boolean

  \se required

  \sa BddEnc_get_var_name_from_index
*/
int
BddEnc_get_var_index_from_name(const BddEnc_ptr self, node_ptr name);

/*!
  \methodof BddEnc
  \brief Returns the ADD leaf corresponding to the given atom

  Returns the ADD leaf corresponding to the given atom,
   if defined, NULL otherwise. The returned ADD - if any - is referenced.
   If the inner flag enforce_constant is set,

   Suppose to have a declaration of this kind:<br>
   <pre>
   VAR
   condition : {idle, stopped}
   </pre>
   then in the constant hash for the atom <tt>idle</tt> there is the
   corresponding leaf ADD, i.e. the ADD whose value is the symbol
   <tt>idle</tt>.
*/
add_ptr
BddEnc_constant_to_add(const BddEnc_ptr self, node_ptr constant);

/*!
  \methodof BddEnc
  \brief Complements an ADD according to a flag.

  Given the ADD <code>a</code>, this function returns
   the negation of ADD <code>a</code> or <code>a</code> itself according the
   value of <code>flag</code>. If <code>flag = -1</code> then returns <code>not
   a</code>, else returns <code>a</code>. It is important that the ADD is a
   FALSE/TRUE ADD (i.e. it has only FALSE or TRUE as leaf).

  \sa bdd_enc_eval
*/
add_ptr
BddEnc_eval_sign_add(BddEnc_ptr self, add_ptr a, int flag);

/*!
  \methodof BddEnc
  \brief Complements a BDD according to a flag.

  Given the BDD <code>a</code>, this function returns
   the negation of BDD <code>a</code> or <code>a</code> itself
   according the value of <code>flag</code>. If <code>flag =
   -1</code> then returns <code>not a</code>, else returns
   <code>a</code>. It is important that the BDD has only FALSE or
   TRUE as leaves.
*/
bdd_ptr
BddEnc_eval_sign_bdd(BddEnc_ptr self, bdd_ptr a, int flag);

/*!
  \methodof BddEnc
  \brief Evaluates a number in a context.

  Evaluate the <em>NUMBER</em> represented by <code>e</code>
   in context <code>context</code>. <em>NUMBERS</em> can be encoded in
   different ways in different processes.

  \sa bdd_enc_eval
*/
int
BddEnc_eval_num(BddEnc_ptr self, node_ptr e, node_ptr context);

/*!
  \methodof BddEnc
  \brief Evaluates a constant expression.

  Evaluate a constant expression. If the
   expression does not evaluate to a constant, then an internal error
   is generated. Returned add is referenced.

  \sa eval eval_num
*/
add_ptr
BddEnc_eval_constant(BddEnc_ptr self, Expr_ptr expr, node_ptr context);

/*!
  \methodof BddEnc
  \brief Given a variable, define or process constant
   the corresponding ADD array is returned.

  Given an identifier (as an expanded identifier
   <code>name</code>), this function returns the ADD array of its
   definition, or NULL if not defined. If the variable is
   of a Word type then the returned array may contain several elements (ADDs).
   For all other kinds of expressions only one element can be in the
   array.  Errors occurs if the identifier is a define which is
   circularly declared. The returned array will belong to the invoker.

  \sa BddEnc_expr_to_add
*/
AddArray_ptr
BddEnc_get_symbol_add(BddEnc_ptr self, node_ptr name);

/*!
  \methodof BddEnc
  \brief Returns the mask (as an ADD) in terms of frozen and
   state variables

  Returned add is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
add_ptr
BddEnc_get_state_frozen_vars_mask_add(BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Returns the mask (as an ADD) in terms of input variables

  Returned add is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
add_ptr
BddEnc_get_input_vars_mask_add(BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Returns the mask (as ADD) in terms of state, frozen and
   input variables

  Returned add is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
add_ptr
BddEnc_get_state_frozen_input_vars_mask_add(BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Returns the mask (as BDD) in terms of frozen and state
   variables

  Returned bdd is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
bdd_ptr
BddEnc_get_state_frozen_vars_mask_bdd(BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Returns the mask (as BDD) in terms of input variables

  Returned bdd is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
bdd_ptr
BddEnc_get_input_vars_mask_bdd(BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Returns the mask (as BDD) in terms of frozen, state and input
   variables

  Returned bdd is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
bdd_ptr
BddEnc_get_state_frozen_input_vars_mask_bdd(BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Applies a mask to the given add which must contain only
   frozen and state variables

  Returned add is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
add_ptr
BddEnc_apply_state_frozen_vars_mask_add(BddEnc_ptr self, add_ptr states);

/*!
  \methodof BddEnc
  \brief Applies a mask to the given add which must contain only
   input variables

  Returned add is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
add_ptr
BddEnc_apply_input_vars_mask_add(BddEnc_ptr self, add_ptr inputs);

/*!
  \methodof BddEnc
  \brief Applies a mask to the given add which must contain
   frozen, state and input variables

  Returned add is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
add_ptr
BddEnc_apply_state_frozen_input_vars_mask_add(BddEnc_ptr self,
                                              add_ptr states_inputs);

/*!
  \methodof BddEnc
  \brief Applies a mask to the given BDD which must contain only
   frozen and state variables

  Returned bdd is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
BddStates
BddEnc_apply_state_frozen_vars_mask_bdd(BddEnc_ptr self,
                                        BddStates states);

/*!
  \methodof BddEnc
  \brief Applies a mask to the given BDD which must contain only
   input variables

  Returned bdd is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
BddInputs
BddEnc_apply_input_vars_mask_bdd(BddEnc_ptr self, BddInputs inputs);

/*!
  \methodof BddEnc
  \brief Applies a mask to the given BDD which must contain
   frozen, state and input variables

  Returned bdd is referenced. Calculated mask will be
   cached for future use. The mask will be applicable only to variable that
   occur within the layers committed to self
*/
BddStatesInputs
BddEnc_apply_state_frozen_input_vars_mask_bdd(BddEnc_ptr self,
                                           BddStatesInputs states_inputs);

/*!
  \methodof BddEnc
  \brief Given a variable, it returns the mask of its encoding

  Returns the mask that removes repetitions of leaves in
   a variable encoding. Returned ADD is
   referenced. Automatic reordering, if enabled, is
   temporary disabled during this computation.
*/
add_ptr
BddEnc_get_var_mask(BddEnc_ptr self, node_ptr var_name);

/*!
  \methodof BddEnc
  \brief Finds a set of prime implicants for a formula
   represented as a BDD.

  Finds the set of prime implicants of a BDD b. Each
   element of the resulting array is a prime implicant of the BDD
   b. The prime implicant is represented as a list of pairs

   (: <vname> <value>)

   The meaning is that the variable <vname> is equal to <value>,
   i.e. <vname> = <value>. A further post-process of the result can
   write it in a better way, expecially for non boolean variables,
   where several prime implicants can be combined by writing complex
   predicates instead of simple equalities.

   The list of layers can be declared for instance as:

   char ** layers = {MODEL_LAYER_NAME, "BA_ABSTRACTION", NULL};

   Where MODEL_LAYER_NAME is the name of the model layer,
   "BA_ABSTRACTION" is the name of a new user created layer.
   Very inportant is the NULL at the end to terminate the list.

   This function needs more comment

  \se None
*/
array_t*
BddEnc_ComputePrimeImplicants(BddEnc_ptr self,
                              const array_t* layer_names,
                              bdd_ptr formula);

/*!
  \methodof BddEnc
  \brief Forces a variable ordering in the BDD encoding.

  It takes an OrdGroups structure representing the
   possibly partial ordering and the routine complete it with the
   possible missing variables. It is assumed all the variables in the
   given ordering group have been previously allocated within the BDD
   package.
*/
void
BddEnc_force_order(BddEnc_ptr self, OrdGroups_ptr new_po_grps);

/*!
  \methodof BddEnc
  \brief Forces a variable ordering in the BDD encoding
   reading it from a file.

  It reads an order file and then forces it
   within the BDD package. The order file may be partial. Thanks to
   <tt>BddEnc_force_order</tt> the ordering is  completed with the
   possible missing variables.

  \sa BddEnc_force_order
*/
void
BddEnc_force_order_from_file(BddEnc_ptr self, FILE* orderfile);

/*!
  \methodof BddEnc
  \brief Prints a BDD as a Well Formed Formula using optional
  sharing

  The bdd representing the formula to be printed is first
  converted to a wff.

  If sharing is required optimizations are performed on the printout.

  If indentation is required, the start_at_column integer offset is
  used to determine the starting indenting offset to print the
  expression.

  \se prints the expression on the given stream.

  \sa BddEnc_bdd_to_wff
*/
void
BddEnc_print_bdd_wff(BddEnc_ptr self, bdd_ptr bdd, NodeList_ptr vars,
                     boolean do_sharing, boolean do_indent,
                     int start_at_column, OStream_ptr out);

/*!
  \methodof BddEnc
  \brief Prints statistical information of a formula.

  Prints statistical information about a given formula.
                 It is computed taking care of the encoding and of the
                 indifferent variables in the encoding.
*/
void
BddEnc_print_formula_info(BddEnc_ptr self, Expr_ptr formula,
                          boolean print_models, boolean print_formula,
                          OStream_ptr out);

/*!
  \methodof BddEnc
  \brief Converts a bdd into a Well Formed Formula representing
  it.

  A new expression is built, that represents the formula
  given as the input bdd.

  The list of variables is used to compute the scalar essentials. Note
  that only the following kinds of variables are allowed in this list.

  1. Pure booleans (i.e. not part of an encoding)
  2. Finite scalars (both ranged and words).

  State, frozen and input variables are all allowed, no NEXT. (It will
  be part of this function's responsibility to add state variables' NEXTs
  as needed.

  \se none

  \sa Bddenc_print_wff_bdd
*/
node_ptr
BddEnc_bdd_to_wff(BddEnc_ptr self, bdd_ptr bdd, NodeList_ptr vars);

/*!
  \methodof BddEnc
  \brief Clean the internal cache which contains the results
   of evaluation of expressions to ADD or BDD form.


   NB: NuSMV option "enable_sexp2bdd_caching" allows to disable the cache
   completely
*/
void
BddEnc_clean_evaluation_cache(BddEnc_ptr self);

/*!
  \methodof BddEnc
  \brief Given a set of variables the function produces a cube of
                them filtering out some of them

  vt is a filter and can be a combination of
   VFT_CURRENT, VFT_NEXT, VFT_FROZEN, VFT_INPUT (see SymbFilterType
   for combination shortcuts).
   vars has to contain variables only without NEXT or anything else.
   E.g. if vars contains state var V and vt includes VFT_CURRENT and
   VFT_NEXT then the result will contains both current and next bits
   of V.

   Returned bdd is referenced, the caller must free it after it is no
   longer used.
*/
BddVarSet_ptr BddEnc_get_vars_cube(const BddEnc_ptr self,
                                          Set_t vars,
                                          SymbFilterType vt);

/*!
  \methodof BddEnc
  \brief Given a set of variables the function produces a cube of
                all of them

  this function is similar to BddEnc_get_vars_cube
   with 2 differences:
   1. all variables in 'vars' are put into the result cube
   2. in order to compute next-state variable bits of variable V
     'vars' has to contain NEXT(V).

   Returned bdd is referenced, the caller must free it after it is no
   longer used.
*/
BddVarSet_ptr
BddEnc_get_unfiltered_vars_cube(const BddEnc_ptr self, Set_t vars);

/*!
  \methodof BddEnc
  \brief Dumps the given AddArray in DOT format

  Labels is an array of strings to be used as roots
  labels. The size of the array must be equal to the size of the
  AddArray.
  Returns 0 in case of success, 1 otherwise
*/
int BddEnc_dump_addarray_dot(BddEnc_ptr self,
                                    AddArray_ptr addarray,
                                    const char** labels,
                                    FILE* outfile);

/*!
  \methodof BddEnc
  \brief Dumps the given AddArray in DAVINCI format

  Labels is an array of strings to be used as roots
  labels. The size of the array must be equal to the size of the
  AddArray.
  Returns 0 in case of success, 1 otherwise
*/
int BddEnc_dump_addarray_davinci(BddEnc_ptr self,
                                        AddArray_ptr addarray,
                                        const char** labels,
                                        FILE* outfile);

/* Functions directly called by commands */

/*!
  \brief Prints a formula in canonical format.

  Prints a formula in canonical format.
*/
void
BddEnc_print_formula(const NuSMVEnv_ptr env, node_ptr constr,
                     const boolean verbose, const boolean formula);

/*!
  \methodof BddEnc
  \brief Dumps an expression in the specified output format.

  type check the expression, then convers to bdd and dumps
*/
int
BddEnc_dump_expr(const BddEnc_ptr self, const node_ptr parsed_expr,
                 const char* str_constr, const t_format format,
                 FILE* outfile);

/**AutomaticEnd**************************************************************/

#endif /* __NUSMV_CORE_ENC_BDD_BDD_ENC_H__ */
