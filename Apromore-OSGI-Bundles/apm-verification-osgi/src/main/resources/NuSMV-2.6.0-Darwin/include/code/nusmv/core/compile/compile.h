/* ---------------------------------------------------------------------------


   This file is part of the ``compile'' package of NuSMV version 2.
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
  \author Marco Roveri, Emanuele Olivetti
  \brief Compilation of NuSMV input language into BDD.

  This package contains the compiler of NuSMV code into
   BDD. It works on a flattened/instantiated structure. Performs the
   checks on the parse tree and fills a layer (when requested) and
   the symbol table.

*/

#ifndef __NUSMV_CORE_COMPILE_COMPILE_H__
#define __NUSMV_CORE_COMPILE_COMPILE_H__

#include "nusmv/core/be/be.h" /* the generic boolean expressions interface */
#include "nusmv/core/cinit/NuSMVEnv.h"
#include "nusmv/core/compile/compileUtil.h"
#include "nusmv/core/compile/FlatHierarchy.h"
#include "nusmv/core/compile/PredicateExtractor.h"
#include "nusmv/core/compile/PredicateNormaliser.h"
#include "nusmv/core/compile/symb_table/SymbLayer.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/enc/bdd/BddEnc.h"
#include "nusmv/core/fsm/FsmBuilder.h"
#include "nusmv/core/fsm/sexp/BoolSexpFsm.h"
#include "nusmv/core/fsm/sexp/SexpFsm.h"
#include "nusmv/core/hrc/HrcNode.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/prop/Prop.h"
#include "nusmv/core/set/set.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/wff/ExprMgr.h"
#include "nusmv/core/node/anonymizers/NodeAnonymizerBase.h"

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_FLAG_FLATTENER_INITIALIZED "flattener_initialized"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_PROC_SELECTOR_VNAME        "proc_selector_vname"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct cmp_struct
  \brief Data structure used to store the current status of compilation.


*/
typedef struct cmp_struct* cmp_struct_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum {
  State_Variables_Instantiation_Mode,
  Frozen_Variables_Instantiation_Mode,
  Input_Variables_Instantiation_Mode
} Instantiation_Variables_Mode_Type;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum {
  State_Functions_Instantiation_Mode,
  Frozen_Functions_Instantiation_Mode
} Instantiation_Functions_Mode_Type;

/*!
  \brief Enumerates the different types of a specification

  Enumerates the different types of a specification
*/
typedef enum {ST_Notype, ST_Ctl, ST_Ltl, ST_Invar, ST_Compute} Spec_Type;

/*!
  \brief Enumerates the status of a specification

  Enumerates the status of a specification
*/
typedef enum {SS_Nostatus, SS_Unchecked, SS_True, SS_False, SS_Wrong, SS_Number} Spec_Status;

/*!
  \struct _Fsm_SexpRec
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct _Fsm_SexpRec    Fsm_SexpRec;
typedef struct _Fsm_SexpRec  * Fsm_SexpPtr;






/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief The name of the standard layer dedicated to model symbols

  Use this name when creating the layer of model symbols
*/
#define MODEL_LAYER_NAME  "model"

/*!
  \brief The name of the standard layer dedicated to determinization
   variables introduced by the booleanization of the model

  Use this name when creating the layer of determinization vars
*/
#define DETERM_LAYER_NAME "determ"

/*!
  \brief The name of the standard layer dedicated to inlining symbols
   introduced by the inlining process of the model

  Use this name when creating the layer of inlining symbols
*/
#define INLINING_LAYER_NAME "inlining"

/*!
  \brief The symbolic name of the input process selector variable.

  This is the internal symbolic name of process selector
   variable. The range of this variable is the set of names of the
   instantiated processes.
*/
#define PROCESS_SELECTOR_VAR_NAME "_process_selector_"

/*!
  \brief The "running" symbol.

  The "running" symbol used to refer the internal
   variable "running" of  processes.
*/
#define RUNNING_SYMBOL "running"

/*!
  \brief The name of the model layers class, set to be the default


*/
#define MODEL_LAYERS_CLASS "Model Class"

/*!
  \brief The name of the artefacts layers class, set to be the default


*/
#define ARTIFACTS_LAYERS_CLASS "Artifacts Class"

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Initializes the compile package.

  Initializes the compile package. The set of commands must
  be explicitly initialized later by calling Compile_InitCmd.
*/
void Compile_init(NuSMVEnv_ptr env);

/*!
  \brief Shut down the compile package

  Shut down the compile package
*/
void Compile_quit(NuSMVEnv_ptr env);

/*!
  \brief Inits the flattener module

  Inits all the internal structures, in order to correctly
   bootstrap the flattener

  \se This module will be initialized, all previously
   iniitalized data will be lost
*/
void CompileFlatten_init_flattener(NuSMVEnv_ptr env);

/*!
  \brief Quits the flattener module

  Resets all internal structures, in order to correctly
   shut down the flattener. Calls clear_* local functions, and resets all
   private variables.

  \se This module will be deinitialized, all previously
   iniitalized data will be lost
*/
void CompileFlatten_quit_flattener(NuSMVEnv_ptr env);

/*!
  \brief Add the tableau module to the list of known modules

  Add the tableau module (coming from parser) to the
   list of known modules. After this function has been invoked, the
   module will be recognized by the flattener
*/
void CompileFlatten_hash_module(const NuSMVEnv_ptr env,
                                       node_ptr parsed_module);

/*!
  \brief Traverse the module hierarchy, collect all required
   the informations and flatten the hierarchy.

  Traverses the module hierarchy and extracts the
   information needed to compile the automaton. The hierarchy of modules
   is flattened, the variables are added to the symbol table, all the
   necessary parts of the model are collected (i.e. the formulae to be
   verified, the initial expressions, etc). Most of the collected
   expressions are flattened.


   The returned value is a structure containing all the collected
   parts. See FlatHierarchy_create function for more info about, and
   constrains on content of the class FlatHierarchy.

   It is the invoker's responsibility to destroy the returned value.

   Parameter `create_process_variables` enables the creation of
   process variable (i.e. declaration of 'running's ). So, this
   parameter can be set up only for users 'main' modules. For auxiliary
   modules created during execution (for example, during LTL tablaue
   generation) this parameter should be set to false (as is done in ltl.c).

   Parameter calc_vars_constr controls the time association between
   constraints and variables is calculated. If true, the association is
   calculated before existing the function, otherwise it is possibly
   calculated later when needed, i.e. when
   FlatHierarchy_lookup_constrains is called. Postponing this calculation
   can be effective when vars constraints are not used in later phases.
   Any value of calc_vars_constr is safe, but having this parameter set
   to false possibly postpones calculations from the model construction
   phase to the model checking phase, when LTL MC is carried out, or when
   COI is involved.

   Parameter hrc_result contains the hrc node to be constructed from the
   model. If hrc_result is NULL then the structure is not populated.

  \se None
*/
FlatHierarchy_ptr
Compile_FlattenHierarchy(const NuSMVEnv_ptr env,
                         const SymbTable_ptr symb_table,
                         SymbLayer_ptr layer,
                         node_ptr, node_ptr, node_ptr,
                         boolean create_process_variable,
                         boolean calc_vars_constr,
                         boolean expand_bounded_arrays,
                         HrcNode_ptr hrc_result);

/*!
  \brief Semantic checks on assignments of the module.


  The function checks that there are no multiple assignments and
  circular definitions.<br> Then the functions tries to detect
  multiple assignments between different modules.
  Here is used an hash with the following semantics:
  This hash is used in two different phases of the
  checking.
  <ol>
  <li>The first deal with multiple definition. During this phase
      the data associated to symbols of the form <em>next(x)</em>,
      <em>init(x)</em> is the body of the assignment (eg, if you have the
      following assignment <em>next(x) := x & y;</em>, then the data
      associated to the symbol <em>next(x)</em> is <em>x & y</em>).</li>
  <li>The second deal with circular definition. In this phase the data
       associated to each symbol is extracted to check the body, and it is
       replaced with <tt>FAILURE_NODE</tt> or <tt>CLOSED_NODE</tt>.</li>
  </ol>
*/
void Compile_CheckAssigns(const SymbTable_ptr, node_ptr);

/*!
  \brief


*/
void Compile_check_case(const SymbTable_ptr, node_ptr expr);

/*!
  \brief Checks that given expression contains either no nested
  next, or no next operator at all.


*/
void Compile_check_next(const SymbTable_ptr st,
                               node_ptr expr, node_ptr context,
                               boolean is_one_next_allowed);

/*!
  \brief Checks that given expression contains no input
  variables in next.

  It outputs an error message (and rises an exception)
  iff the expression contains a next statement which itself has an
  input variable in it.
*/
void Compile_check_input_next(const SymbTable_ptr st,
                                     node_ptr expr, node_ptr context);

/*!
  \brief Concatenates contexts ctx1 and ctx2

  Since contexts are organized bottom-up
   ("a.b.c" becomes

   DOT
   /  \
   DOT   c
   / \
   a   b
   )

   ctx2 is appended to ctx1 by concatenating ctx1 to ctx2. For example
   if ctx1="c.d.e" and ctx2="a.b.c", node 'a' is searched in ctx2, and
   then substituted by

   / ...
   DOT
   /   \
   ->>  DOT   b
   /  \
   (ctx1)  a

   Important: nodes in ctx2 are traversed and possibly recreated with find_node

*/
node_ptr
CompileFlatten_concat_contexts(const NuSMVEnv_ptr env,
                               node_ptr ctx1, node_ptr ctx2);

/*!
  \brief Returns a range going from a to b

  Returns a range going from a to b. An empty range (Nil)
   is returned whether given 'a' is greater than 'b'
*/
node_ptr
CompileFlatten_expand_range(const NuSMVEnv_ptr env,
                            int a, int b);

/*!
  \brief Resolves the given symbol to be a number

  If given symbol is a number, the node is simply
   returned.  If it is a define, the body is
   returned if it is a number. If it is an actuial
   parameter, it is evaluated. Otherwise NULL is
   returned. Notice that returned nodes can be
   NUMBER, NUMBER_SIGNED_WORD or NUMBER_UNSIGNED_WORD.
   symb_table MUST be a valid SymbTable instance
*/
node_ptr
CompileFlatten_resolve_number(SymbTable_ptr symb_table,
                              node_ptr n, node_ptr context);

/*!
  \brief Takes an expression, and if it is a define or parameter
   resolves it to the actual expression.

  Sometimes a define may be equal to another
   define. This function will remove such chain of defines/parameters
   and return the actual expression or a fully resolved variable or
   constant identifier.
   This operation may be considered more like an optimization
   to avoid define chains, eg, during FSM output.

   NEXT is processed not as an expression but as a part of an identifier, i.e.
   its operand will be resolved as well.

   Note that array defines are not resolved to its definition.
*/
node_ptr
CompileFlatten_resolve_define_chains(const SymbTable_ptr symb_table,
                                     node_ptr expr, node_ptr context);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void insert_module_hash(const NuSMVEnv_ptr env, node_ptr x,
                               node_ptr y);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr lookup_module_hash(const NuSMVEnv_ptr env, node_ptr x);

/*!
  \brief Checks expressions for illegal occurrences of input vars

  Checks the TRANS, INIT, INVAR and ASSIGN statements to
  make sure that input variables are not used where they should not be. That
  is, anywhere in a TRANS, INIT or INVAR statement and within next expressions
  in the init and next sections of an ASSIGN statement.
*/
void compileCheckForInputVars(SymbTable_ptr, FlatHierarchy_ptr hierarchy);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_read_model(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_read_model(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_unset_read_model(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_hrc_built(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_hrc_built(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_flatten_hrc(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_flatten_hrc(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_encode_variables(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_encode_variables(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_process_selector(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_process_selector(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_build_frames(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_build_frames(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_build_model(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_build_model(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_build_flat_model(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_build_flat_model(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_build_bool_model(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_build_bool_model(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_fairness(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_fairness(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_coi(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_coi(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_bmc_init(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_bmc_init(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_unset_bmc_init(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int cmp_struct_get_bmc_setup(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_set_bmc_setup(cmp_struct_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void cmp_struct_unset_bmc_setup(cmp_struct_ptr cmp);

/*!
  \brief High level function which parses and type checks a
  simple wff from string

  Context can be given with "IN". If input string is NULL, TRUE is
  returned. Returns NULL if an error occurs.
*/
Expr_ptr
Compile_compile_simpwff_from_string(NuSMVEnv_ptr env,
                                    const SymbTable_ptr st,
                                    const char* str_formula);

/*!
  \brief High level function which parses and type checks a
  next wff from string

  Context can be given with "IN". If input string is NULL, TRUE is
  returned. Returns NULL if an error occurs.
*/
Expr_ptr
Compile_compile_nextwff_from_string(NuSMVEnv_ptr,
                                    const SymbTable_ptr st,
                                    const char* str_formula);

/*!
  \brief High level function which parses and type checks a
  specification from string

  Context can be given with "IN". If input string is NULL, TRUE is
  returned. Returns NULL if an error occurs.
*/
Expr_ptr
Compile_compile_spec_from_string(NuSMVEnv_ptr env,
                                 const SymbTable_ptr st,
                                 const char* str_formula,
                                 const Prop_Type prop_type);

/*!
  \brief Builds the flattened version of an expression.

  Builds the flattened version of an
   expression. It does not expand defined symbols with the
   corresponding body.

  \sa Flatten_GetDefinition, Compile_FlattenSexpExpandDefine
*/
node_ptr
Compile_FlattenSexp(const SymbTable_ptr symb_table, node_ptr, node_ptr);

/*!
  \brief Flattens an expression and expands defined symbols.

  Flattens an expression and expands defined symbols.

  \sa Flatten_GetDefinition, Compile_FlattenSexp
*/
node_ptr
Compile_FlattenSexpExpandDefine(const SymbTable_ptr symb_table,
                                node_ptr, node_ptr);

/*!
  \brief


*/
void
Compile_WriteFlattenModel(const NuSMVEnv_ptr env,
                          FILE* out,
                          const SymbTable_ptr st,
                          const array_t* layer_names,
                          const char* fsm_name,
                          FlatHierarchy_ptr hierarchy,
                          boolean force_flattening);

/*!
  \brief Dumps the flatten model on the given FILE

  Dumps the flatten model on the given FILE.
                       The dumped model is restricted to the set of variables
                       defined in the given FlatHierarchy
*/
void
Compile_WriteRestrictedFlattenModel(const NuSMVEnv_ptr env,
                                    FILE* out,
                                    const SymbTable_ptr st,
                                    const array_t* layer_names,
                                    const char* fsm_name,
                                    FlatHierarchy_ptr hierarchy,
                                    boolean force_flattening);

/*!
  \brief


*/
void
Compile_WriteObfuscatedFlattenModel(const NuSMVEnv_ptr env,
                                    FILE* out,
                                    const SymbTable_ptr st,
                                    const array_t* layer_names,
                                    const char* fsm_name,
                                    FlatHierarchy_ptr hierarchy,
                                    boolean print_map,
                                    boolean force_flattening,
                                    NodeAnonymizerBase_ptr anonymizer);

/*!
  \brief Prints the flatten version of FSM of an SMV model.

  Prints on the specified file the flatten
   FSM of an SMV model, i.e. a list of all variable, defines, and all
   constrains (INIT, TRANS, INVAR, ASSIGNS, JUSTICE, COMPASSION).
   Specifications are NOT printed.

   layer_names is an array of names of layers that is typically
   obtained from the symbol table. fsm_name is a name of the output
   structure, usually it is "MODULE main".
*/
void
Compile_WriteFlattenFsm(const NuSMVEnv_ptr env,
                        FILE* out,
                        const SymbTable_ptr symb_table,
                        const array_t* layer_names,
                        const char* fsm_name,
                        FlatHierarchy_ptr hierarchy,
                        boolean force_flattening);

/*!
  \brief


*/
void
Compile_WriteFlattenModel_udg(const NuSMVEnv_ptr env,
                              FILE* out,
                              const SymbTable_ptr st,
                              const array_t* layer_names,
                              const char* fsm_name,
                              FlatHierarchy_ptr hierarchy);

/*!
  \brief Prints the given flatten specifications.

  Prints into the specified file the flatten
   specifications.
*/
void
Compile_WriteFlattenSpecs(const NuSMVEnv_ptr env,
                          FILE* out,
                          const SymbTable_ptr st,
                          FlatHierarchy_ptr hierarchy,
                          boolean force_flattening);

/*!
  \brief Prints the given boolean model


*/
void
Compile_WriteBoolModel(const NuSMVEnv_ptr env,
                       FILE* out,
                       BddEnc_ptr enc,
                       NodeList_ptr layers,
                       const char* fsm_name,
                       BoolSexpFsm_ptr bool_sexp_fsm,
                       boolean force_flattening);

/*!
  \brief Prints the boolean FSM of an SMV model.

  Prints into the specified file the boolean FSM of an
   SMV model.
   bool_sexp_fsm should be a boolean Sexp FSM.
   layer_names is an array of layers whose variables will be printed,
   usually this parameter is a list of all layers committed to enc. The array
   should be ended by a NULL element.
*/
void
Compile_WriteBoolFsm(const NuSMVEnv_ptr env,
                     FILE* out,
                     const SymbTable_ptr symb_table,
                     NodeList_ptr layers,
                     const char* fsm_name,
                     BoolSexpFsm_ptr bool_sexp_fsm,
                     boolean force_flattening);

/*!
  \brief Prints the boolean specifications of an SMV model.

  Prints into the specified file the booleanized
   specifications of an SMV model.

   NOTE: a temporary layer will be created during the dumping for
   determinization variables that derived from the booleanization of
   the specifications. These variable declarations will be printed
   after the specs.

*/
void
Compile_WriteBoolSpecs(const NuSMVEnv_ptr env,
                       FILE* out,
                       BddEnc_ptr enc,
                       FlatHierarchy_ptr hierarchy);

/*!
  \brief Check if an expr is of a finite range type

  Check if an expr is of a finite range type.

                       REMARK: Words are considered finite only if
                       word_unbooleanizable is set to false

                       If cache is not null whenever we encounter a formula in
                       the cache we simply return the previously computed value,
                       otherwise an internal and temporary map is used.

                       NOTE: the internal representation of cache is private so
                             the user should provide only caches generated by
                             this function!

  \se none
*/
boolean Compile_is_expr_booleanizable(const SymbTable_ptr st,
                                             node_ptr expr,
                                             boolean word_booleanizable,
                                             hash_ptr cache);

/*!
  \brief Converts a scalar expression into a boolean expression.

  Takes an scalar expression intended to evaluate
  to boolean, maps through booleans down to the atomic scalar
  propositions, builds the corresponding boolean function, and returns
  the resulting boolean expression.

  The conversion of atomic scalar proposition is currently performed
  by generating the corresponding ADD, and then printing it in terms
  of binary variables.

  If one or more determinization variable must be created
  (i.e. non-determinism must be allowed) then det_layer is the
  SymbLayer instance to be filled with the newly created
  determinization variables. If non-determinism is not allowed, specify
  NULL as det_layer value. In this case you can use detexpr2bexpr as well.

  The input expression will be processed with Nil context (for
  flattened expr this does not matter).

  There is no need to clean the hash used for memoization, since it is done by
  the symbol table with a trigger.

  \se None

  \sa Compile_detexpr2bexpr, expr2bexpr_recur
*/
Expr_ptr Compile_expr2bexpr(BddEnc_ptr enc,
                                   SymbLayer_ptr det_layer,
                                   Expr_ptr expr);

/*!
  \brief Converts a scalar expression into a boolean expression.

  Takes an scalar expression intended to evaluate
  to boolean, maps through booleans down to the atomic scalar
  propositions, builds the corresponding boolean function, and returns
  the resulting boolean expression.

  The conversion of atomic scalar proposition is currently performed
  by generating the corresponding ADD, and then printing it in terms
  of binary variables.

  An error is returned if determinization variables are introduced in
  the booleanization process.

  The input expression will be processed with Nil context (for
  flattened expr this does not matter).

  \se None

  \sa Compile_expr2bexpr, expr2bexpr_recur,
  Compile_detexpr2bexpr_list
*/
Expr_ptr Compile_detexpr2bexpr(BddEnc_ptr enc, Expr_ptr expr);

/*!
  \brief Converts a scalar expression into a boolean expression.

  This function is exactly like Compile_detexpr2bexpr
  except that the input expressions is expected to be a list of expressions.
  The only purpose of this function wrt Compile_detexpr2bexpr is efficiency.
  For big model list of expressions may be huge and stack overflow may happen
  in Compile_detexpr2bexpr because the expressions are processed recursively
  whereas here top-level expressions are processed in loop.

  expr has to be a RIGHT-connected list of elements (i.e. car is head
  and cdr is tail). The connecting nodes have to be of type AND or
  CONS with the semantics of AND.  The returned expression is a list
  of the same order but with the booleanized expressions and AND used
  as connector.

  NOTE: some simplifications are done, e.g. if FALSE is met among
  elements then FALSE is returned.

  NOTE: when the function see on the right a node of a type other than
  AND and CONS then right child is considered as the last element in the
  list.

  NOTE: special case: if NEXT is met at the top then its sub-expression
  is processed as a list.

  TODO: if in future is will be necessary to process lists of
  different connector kind, e.g. OR, it will be necessary to provided
  the kind as parameter. Still AND and CONS have to dealt the same way
  because in traces it is unspecified if AND or CONS is used in
  var=value lists.

  \se None

  \sa Compile_detexpr2bexpr, Compile_expr2bexpr,
  expr2bexpr_recur
*/
Expr_ptr
Compile_detexpr2bexpr_list(BddEnc_ptr enc, Expr_ptr expr);

/*!
  \brief Computes dependencies of a given SMV expression

  The set of dependencies of a given formula are
   computed. A traversal of the formula is performed. Each time a
   variable is encountered, it is added to the so far computed
   set. When a formula depends on a next variable, then the
   corresponding current variable is added to the set. When an atom is
   found a call to <tt>formulaGetDefinitionDependencies</tt> is
   performed to compute the dependencies. Returned set must be
   disposed by the caller. This is the same as calling
   Formula_GetDependenciesByType with filter = VFT_CNIF and
   preserve_time = false

  \sa formulaGetDefinitionDependencies
*/
Set_t
Formula_GetDependencies(const SymbTable_ptr, node_ptr, node_ptr);

/*!
  \brief Computes the dependencies of an SMV expression by type

  The set of dependencies of a given formula are
   computed, as in Formula_GetDependencies, but the variable type filters the
   dependency collection.

   If flag preserve_time is true, then entries in the returned set
   will preserve the time they occur within the formula. For
   example, formula 'a & next(b) = 2 & attime(c, 2) < 4' returns
   {a,b,c} if preserve_time is false, and {a, next(b), attime(c, 2)}
   if preserve_time is true.

   Returned set must be disposed by the caller

  \sa formulaGetDependenciesByTypeAux
   formulaGetDefinitionDependencies
*/
Set_t
Formula_GetDependenciesByType(const SymbTable_ptr, node_ptr, node_ptr,
                              SymbFilterType, boolean);


/*!
  \brief Compute the dependencies of two set of formulae by type

  Given a formula and a list of fairness constraints, the set of
  symbols filtered w.r.t. the type occurring in them is
  computed. Returned Set must be disposed by the caller.

*/
Set_t
Formulae_GetDependenciesByType(const SymbTable_ptr, node_ptr, node_ptr, node_ptr,
                               SymbFilterType, boolean);

/*!
  \brief Calculates the set of constants occurring into
   the given formula

  Given a formula the set of constants occurring in
   them is computed and returned. Returned set must be disposed by the caller
*/
Set_t
Formula_GetConstants(const SymbTable_ptr symb_table,
                     node_ptr formula, node_ptr context);

/*!
  \brief Compute the dependencies of two set of formulae

  Given a formula and a list of fairness constraints, the
   set of variables occurring in them is computed. Returned Set must be
   disposed by the caller
*/
Set_t
Formulae_GetDependencies(const SymbTable_ptr, node_ptr, node_ptr,
                         node_ptr);

/*!
  \brief Computes the COI of a given expression

  Computes the COI of a given expression,
   up to step "steps" (or fixpoint if steps = -1).
   If not NULL, if the fixpoint has been reached
   (ie: there are no more dependencies), reached_fixpoint
   is set to true.
*/
Set_t ComputeCOIFixpoint(const SymbTable_ptr symb_table,
                                const FlatHierarchy_ptr hierarchy,
                                const Expr_ptr expression,
                                const int steps,
                                boolean* reached_fixpoint);

/*!
  \brief Computes the COI of a given set of variables, defined
   within the given hierarchy

  Computes the COI of a given set of variables, defined
   within the given hierarchy. Returned Set must be disposed by the caller
*/
Set_t ComputeCOI(const SymbTable_ptr,
                        const FlatHierarchy_ptr, Set_t);

/*!
  \brief Gets the flattened version of an atom.

  Gets the flattened version of an atom. If the
   atom is a define then it is expanded. If the definition mode
   is set to "expand", then the expanded flattened version is returned,
   otherwise, the atom is returned.

  \se The <tt>flatten_def_hash</tt> is modified in
   order to memoize previously computed definition expansion.
*/
node_ptr
Flatten_GetDefinition(const SymbTable_ptr symb_table, node_ptr atom,
                      const boolean expand_defines);

/*!
  \brief Resets the hashed information about the given symbol

  This method is used when removing symbols (for example,
   when removing a layer) as some information about that symbol may be
   chached internally to this module. For example this is the case of
   defines, whose flatten body are privately cached within this module.

   If the symbol is not cached or have no associated information, no
   action is taken.
*/
void Flatten_remove_symbol_info(const NuSMVEnv_ptr env, node_ptr name);

/*!
  \brief Takes a list of values and returns the same
   list being normalised

  Takes a list of values and returns the same
   list being normalised
*/
node_ptr
CompileFlatten_normalise_value_list(const NuSMVEnv_ptr env,
                                    node_ptr old_value_list);

/*!
  \brief convert a type from node_ptr-form constructed by parser
   into not-memory-shared SymbType_ptr.

  All normal simple and complex types can be processed.
   continuous type cannot be processed since the node CONTINUOUS in not known
   by the compiler.

   Note that PROCESS and MOD_TYPE are not types and cannot be processed here.
   Parameter:
   st -- is symbol table where constants met in type can be evaluated.
   layer -- is layer where constants will be declared (for enum types).
   type -- is the type to be converted.
   name -- is the name of variable a given type is processed for.
       It is used only in error messaged and also additional checks
       are done wrt special var _process_selector_.

   If type is constructed incorrectly then error is raise. I.e. NULL
   is never returned.

   NOTE: An invoker has to free the returned type.
*/
SymbType_ptr
Compile_InstantiateType(SymbTable_ptr st, SymbLayer_ptr layer,
                        node_ptr name, node_ptr type,
                        node_ptr context,
                        boolean expand_bounded_arrays);

/*!
  \brief Instantiates the given variable.

  It takes as input a variable name, its type and a
   context, and depending on the type of the variable some operation
   are performed in order to instantiate it in the given context:

   Depending on the kind of variable instantiation mode the variables
   are appended to <tt>input_variables</tt>, <tt>frozen_variables</tt> or
   <tt>state_variables</tt>, respectively.

   Note that if type is ARRAY then the "name" is declared
   with SymbLayer_declare_variable_array and then subvariables are
   created.

   Returns true iff a variable (input,state or frozen) or array was
   created.

   PRECONDITION: type has to be not memory-shared, and its ownership
   is passed to this function.


  \sa compile_instantiate_var
*/
boolean
Compile_DeclareVariable(SymbTable_ptr symb_table, SymbLayer_ptr layer,
                        node_ptr name, SymbType_ptr type,
                        node_ptr context,
                        Instantiation_Variables_Mode_Type mode);

/*!
  \brief Instantiates the given function.

  It takes as input a function name, its type and a
   context, and depending on the type of the function some operation
   are performed in order to instantiate it in the given context:

   Depending on the kind of function instantiation mode the functions
   are appended to <tt>state_functions</tt> or
   <tt>frozen_functions</tt>, respectively.

   Returns true iff a function (state or frozen) was
   created.

   PRECONDITION: type has to be not memory-shared, and its ownership
   is passed to this function.


  \sa compile_instantiate_fun
*/
boolean
Compile_DeclareFunction(SymbTable_ptr symb_table, SymbLayer_ptr layer,
                        node_ptr name, SymbType_ptr type,
                        node_ptr context,
                        Instantiation_Functions_Mode_Type mode);

/*!
  \brief

  Returns a node COLON(NUMBER count, NUMBER depth)
*/
node_ptr Compile_make_dag_info(const NuSMVEnv_ptr env,
                                      node_ptr expr, hash_ptr hash);

/*!
  \brief Top level function to create dags from expressions


*/
node_ptr Compile_convert_to_dag(const NuSMVEnv_ptr env,
                                       SymbTable_ptr symb_table,
                                       node_ptr expr,
                                       hash_ptr hash,
                                       hash_ptr defines);

/*!
  \brief Dumps to the given file the DEFINEs that had been created by
  the daggifier.

  \sa Compile_declare_dag_defines_in_layer
*/
void Compile_write_dag_defines(const NuSMVEnv_ptr env,
                               FILE* out, hash_ptr defines);

/*!
  \brief Declares to the given layer the DEFINEs that had been created
  by the daggifier.

  Notes: the daggifier does not declare the support DEFINEs that it
  creates, instead it add them to an hash table that is filled by
  Compile_convert_to_dag.

  The new names of the defines are requested to the symbol table,
  which takes into account already declared symbols. This means that
  if you call Compile_write_dag_defines twice or more, you may find
  repeated symbols for new defines' names. To avoid this, use this
  function, by passing alayer which belongs to the symbol table. This
  is a way of forcing those new symbols to become part of the symbol
  table, so supporting in fact multiple calls to
  Compile_convert_to_dag, and also the possibility to access those
  defines in expressions, and not only to dumpo the fsm to a file.

  Notice that the new symbols cannot be declared/defined by
  Compile_convert_to_dag, as at daggification time there are symbols
  that may be later discharged, and only at the end of the process
  (e.g. after multiple calls to Compile_convert_to_dag) it is known
  how many defines will be effectively used.

  \sa Compile_convert_to_dag, Compile_write_dag_defines

*/
void Compile_declare_dag_defines_in_layer(SymbLayer_ptr layer,
                                          hash_ptr defines);


/*!
  \brief Frees the content of given structures.

  Warning: the hashes are not freed, only the content
*/
void Compile_destroy_dag_info(const NuSMVEnv_ptr env,
                                     hash_ptr dag_info, hash_ptr defines);

/*!
  \brief Prints an array define node to out file.

  Prints a array define node to out file.
   This function is exported so the hrc package can use it.

   TODO[AT] remove this function.

*/
void Compile_print_array_define(const NuSMVEnv_ptr env, FILE* out, const node_ptr n);

/*!
  \brief Creates and fills an HrcNode (and all children) reading
   from a parse tree.

  mod_name is the name of the module which is the
   returned node (the local root node in the parse tree)

  \se None
*/
HrcNode_ptr Compile_hrc_from_parse_tree(const NuSMVEnv_ptr env,
                                               NodeMgr_ptr nomgr,
                                               node_ptr mod_name,
                                               node_ptr parse_tree);

/*!
  \brief Given a module definition body, fills the given HrcNode

  mod_defs is an hash mod_name ->
   module_definition. It can be NULL, but if only of the module
   body does not contain other module instances.

  \se None
*/
void Compile_fill_hrc_from_mod_body(const NuSMVEnv_ptr env,
                                           NodeMgr_ptr nomgr,
                                           node_ptr mod_body,
                                           HrcNode_ptr hrc_result,
                                           hash_ptr mod_defs);

/* directly or indirectly called by commmands */

/*!
  \brief  Traverses the parse tree coming from the smv parser and
                flattens the smv file. The FlatHierarchy is created and added
                to the environment

   Also the model layer is created and registered in the TraceMgr

  \se
*/
int
CompileFlatten_flatten_smv(NuSMVEnv_ptr env,
                           boolean calc_vars_constrains,
                           boolean expand_bounded_arrays);

/*!
  \brief


*/
void
Compile_show_vars(const NuSMVEnv_ptr env, const boolean total_only,
                 const boolean defs_only, const boolean vars_only,
                 const boolean statevars, const boolean frozenvars,
                 const boolean inputvars, const OStream_ptr ostream,
                 const boolean verbose);

/*!
  \brief Writes a flat and boolean model of a given SMV file

  Writes a flat and boolean model of a given SMV file
*/
int Compile_write_model_flat_bool(const NuSMVEnv_ptr env,
                                         const char* output_file,
                                         FILE* ofileid);

/*!
  \brief print predicates

  ,i.e. normalized expressions are not used later on. it would be
   better to keep the results somewhere and reuse it in other modules of nusmv,
   where normalization is required NOTE: here the flatten hierarchy is
   normalized to collect predicates.  Another solution is to normalize the Sexp
   FSM.  There is no strong opinion toward any solution. Normalization of
   flatten hierarchy was chose just because it does not require initialization
   of encoding or FSM creation which may be useful in some cases.
*/
void Compile_print_predicates(const NuSMVEnv_ptr env);

/*!
  \brief creates the  master scalar fsm if needed


*/
int Compile_create_flat_model(NuSMVEnv_ptr env);

/*!
  \brief Creates the  master boolean fsm if needed.
   A new layer called DETERM_LAYER_NAME
   will be added if the bool fsm is created.

  The newly created layer will be committed to both the
   boolean and bdd encodings. Warning: it is assumed here that the flat model
   has been already created
*/
int Compile_create_boolean_model(NuSMVEnv_ptr env);

/* For write_coi_model */

/*!
  \brief Removes expression in the form "a := b" from the given
   expression

  Removes expression in the form "a := b" from the given
   expression. The new expression is returned
*/
Expr_ptr
Compile_remove_assignments(const NuSMVEnv_ptr env, Expr_ptr expr);

/*!
  \brief Dumps the model applied to COI for the given property

  Dumps the model applied to COI for the given property
*/
void Compile_write_coi_prop_fsm(const NuSMVEnv_ptr env,
                                       FlatHierarchy_ptr fh,
                                       Set_t cone, Set_t props,
                                       OStream_ptr output_file);

/*!
  \brief Dumps the COI for the given property

  Dumps the COI for the given property
*/
void Compile_write_coi_prop(const NuSMVEnv_ptr env,
                                   Set_t cone, Set_t props,
                                   OStream_ptr output_file);

/*!
  \brief


*/
void Compile_print_summary(const NuSMVEnv_ptr env,
                                  OStream_ptr file, SymbTable_ptr st,
                                  NodeList_ptr list, const char * str,
                                  boolean limit_output);

/*!
  \brief Computes the total bit number of symbols in the given
   list


*/
int Compile_get_bits(const SymbTable_ptr st, const NodeList_ptr lst);

/*!
  \brief Dumps on output_file the global coi FSM

  Dumps on output_file the FSM built using the union of all
   properties cone of influence. Properties can be filtered
   by type using prop_type: if prop_type == Prop_NoType,
   all properties are used
*/
void Compile_write_global_coi_fsm(NuSMVEnv_ptr env,
                                         FlatHierarchy_ptr hierarchy,
                                         Prop_Type prop_type,
                                         OStream_ptr output_file);

/*!
  \brief Dumps properties shared COI FSMs or sets

  Dumps properties shared COI informations.
   If only_dump_coi is true, only the set of
   variables in the cone of each property is
   dumped. Otherwise, an FSM is created and
   dumped. Properties with the same COI will appear
   in the same FSM. Properties can be filtered by
   type using prop_type: if prop_type ==
   Prop_NoType, all properties are used
*/
int Compile_write_properties_coi(NuSMVEnv_ptr env,
                                        FlatHierarchy_ptr hierarchy,
                                        Prop_Type prop_type,
                                        boolean only_dump_coi,
                                        const char* file_name);

/*!
  \brief Prints the given type to the given stream

  Prints the given type to the given stream.

   The type must be created with the
   Compile_get_var_type function. If the type
   is scalar, then values are printed until
   "threshold" number of characters are reached. If
   some values are missing because of the
   threshold, then "other # values" is added in
   output

   TODO[MR]: Possibly to extend with other types

*/
void Compile_print_type(const NuSMVEnv_ptr env,
                               OStream_ptr file, node_ptr ntype,
                               int threshold);

/*!
  \brief Creates an internal representaion of the symbol type

  Creates an internal representaion of the symbol type.
   The representation of the type returned is
   intended to be used only with the
   Compile_print_type procedure. If 2 types are
   the same, the same node is returned
   It is not usable with type continuous.

   TODO[MR]: Possibly to extend with other types

*/
node_ptr Compile_get_var_type(const NuSMVEnv_ptr env,
                                     SymbType_ptr type);

/*!
  \brief Prints the flatten version of FSM of an SMV model.

  Prints on the specified file the flatten
   FSM of an SMV model, i.e. a list of all variable, defines, and all
   constrains (INIT, TRANS, INVAR, ASSIGNS, JUSTICE, COMPASSION).
   Specifications are NOT printed.

   layer_names is an array of names of layers that is typically
   obtained from the symbol table. fsm_name is a name of the output
   structure, usually it is "MODULE main".
*/
void Compile_WriteFlattenFsm_udg(const NuSMVEnv_ptr env,
                                        FILE* out,
                                        const SymbTable_ptr st,
                                        const array_t* layer_names,
                                        const char* fsm_name,
                                        FlatHierarchy_ptr hierarchy);

/*!
  \brief Prints the given flatten specifications.

  Prints into the specified file the flatten
   specifications.
*/
void Compile_WriteFlattenSpecs_udg(const NuSMVEnv_ptr env,
                                          FILE* out,
                                          const SymbTable_ptr st,
                                          FlatHierarchy_ptr hierarchy);

/*!
  \brief Prints the given boolean model


*/
void Compile_WriteBoolModel_udg(const NuSMVEnv_ptr env,
                                       FILE* out,
                                       BddEnc_ptr enc,
                                       NodeList_ptr layers,
                                       const char* fsm_name,
                                       BoolSexpFsm_ptr bool_sexp_fsm);

/*!
  \brief Prints the boolean FSM of an SMV model.

  Prints into the specified file the boolean FSM of an
   SMV model.
   bool_sexp_fsm should be a boolean Sexp FSM.
   layer_names is an array of layers whose variables will be printed,
   usually this parameter is a list of all layers committed to enc. The array
   should be ended by a NULL element.
*/
void Compile_WriteBoolFsm_udg(const NuSMVEnv_ptr env,
                                     FILE* out, const SymbTable_ptr st,
                                     NodeList_ptr layers, const char* fsm_name,
                                     BoolSexpFsm_ptr bool_sexp_fsm);

/*!
  \brief Prints the boolean specifications of an SMV model.

  Prints into the specified file the booleanized
   specifications of an SMV model.

   NOTE: a temporary layer will be created during the dumping for
   determinization variables that derived from the booleanization of
   the specifications. These variable declarations will be printed
   after the specs.

*/
void Compile_WriteBoolSpecs_udg(const NuSMVEnv_ptr env, FILE* out,
                                       BddEnc_ptr enc,
                                       FlatHierarchy_ptr hierarchy);

/*!
  \brief

  Returns a node COLON(NUMBER count, NUMBER depth)
*/
node_ptr Compile_make_dag_info_udg(const NuSMVEnv_ptr env,
                                          node_ptr expr, hash_ptr hash);

/*!
  \brief Top level function to create dags from expressions


*/
node_ptr Compile_convert_to_dag_udg(const NuSMVEnv_ptr env,
                                           SymbTable_ptr symb_table,
                                           node_ptr expr,
                                           hash_ptr dag_hash,
                                           hash_ptr defines);

/*!
  \brief Prints a array define node to out file.

  Prints a array define node to out file.
   This function is exported so the hrc package can use it.
*/
void Compile_print_array_define_udg(const NuSMVEnv_ptr env,
                                           FILE* out,
                                           const node_ptr n);

/* Moved here because it is called from Hycomp */

/*!
  \brief This function processes a hierarchy after
   collecting all its subparts.

  This processing means:
   1. process_selector variable and running defines are declared (only if
   create_process_variables is on)
   2. All the required lists of expressions are reversed.
   All the constrains (not specifications) are flattened.
   3. An association between vars and constrains are created (for ASSIGN,
   INIT, INVAR, TRANS).
   4. Type checking of the variable and define declarations and of all the
   expressions.
   5. Also a correct use of input variables and lack of circular dependences
   are checked.

   The parameters:
   layer is a layer with module variables.
   hierachy is a hierarchy to be process.
   name is a name of the module instance, i.e. a context of all expressions.
   create_process_variables enables creation of process variables.

*/
void Compile_ProcessHierarchy(const NuSMVEnv_ptr env,
                                     SymbTable_ptr symb_table,
                                     SymbLayer_ptr layer,
                                     FlatHierarchy_ptr hierachy,
                                     node_ptr name,
                                     boolean create_process_variables,
                                     boolean calc_vars_constr);

/*!
  \brief Prints usage statistic.

  Prints on <code>outstream</code> usage
  statistics, i.e. the amount of memory used, the amount of time
  spent, the number of BDD nodes allocated and the size of the model
  in BDD.

  \sa compilePrintBddModelStatistic
*/
int Compile_print_usage(NuSMVEnv_ptr env, OStream_ptr file);

/*!
  \brief Prints some stats about the bddfsm

  if "printPreds" it prints also the predicates of the fsm
*/
int Compile_print_fsm_stats(NuSMVEnv_ptr env,
                                   BddFsm_ptr fsm,
                                   FILE* outstream,
                                   boolean printPreds);

/* not declared b/c these are supposed to be used by the Python
   wrapper */
int Compile_encode_variables(NuSMVEnv_ptr env,
                             char* input_order_file_name,
                             boolean bdd_enc_enum_only);
int Compile_build_model(NuSMVEnv_ptr env,
                        TransType partition_method);

/*!
  \brief Checks if the flattening has been carried out

  Returns 0 if constructed, 1 otherwise. If given file is
  not NULL, an error message is also printed out to it (typically, you
  will use errstream)
*/
int Compile_check_if_flattening_was_built(const NuSMVEnv_ptr env,
                                                 FILE* err);

/*!
  \brief Checks if the variables enconding has been constructed

  Returns 0 if constructed, 1 otherwise. If given file is
  not NULL, an error message is also printed out to it (typically, you
  will use errstream)
*/
int Compile_check_if_encoding_was_built(const NuSMVEnv_ptr env,
                                               FILE* err);

/*!
  \brief Checks if the encoding really committed the
  model layer to the bdd encoder.

  Returns 0 if the layer was committed, 1 otherwise.
  err can be NULL if the function does not have to produce an output
*/
int Compile_check_if_model_layer_is_in_bddenc(const NuSMVEnv_ptr env,
                                                     FILE* err);

/*!
  \brief Checks if flat model has been constructed

  Returns 0 if constructed, 1 otherwise. If given file is
  not NULL, an error message is also printed out to it (typically, you
  will use errstream). If forced is true, than the model is
  requested to be built even when COI is enabled.
*/
int Compile_check_if_flat_model_was_built(const NuSMVEnv_ptr env,
                                                 FILE* err,
                                                 boolean forced);

/*!
  \brief Checks if boolean model has been constructed

  Returns 0 if constructed, 1 otherwise. If given file is
  not NULL, an error message is also printed out to it (typically, you
  will use errstream). If forced is true, thatn the model is
  requested to be built even when COI is enabled.
*/
int Compile_check_if_bool_model_was_built(const NuSMVEnv_ptr env,
                                                 FILE* err,
                                                 boolean forced);

/*!
  \brief Checks if bdd model has been constructed

  Returns 0 if constructed, 1 otherwise. If given file is
  not NULL, an error message is also printed out to it (typically, you
  will use errstream). Use this function from commands that require
  the model to be constructed for being executed.
*/
int Compile_check_if_model_was_built(const NuSMVEnv_ptr env,
                                            FILE* err,
                                            boolean forced);

#endif /* __NUSMV_CORE_COMPILE_COMPILE_H__ */
