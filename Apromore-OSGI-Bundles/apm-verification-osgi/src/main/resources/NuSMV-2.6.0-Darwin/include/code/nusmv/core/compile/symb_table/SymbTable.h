/* ---------------------------------------------------------------------------

   This file is part of the ``compile.symb_table'' package of NuSMV
   version 2.  Copyright (C) 2004 by FBK-irst.

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
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.

   For more information on NuSMV see <http://nusmv.fbk.eu>
   or email to <nusmv-users@fbk.eu>.
   Please report bugs to <nusmv-users@fbk.eu>.

   To contact the NuSMV development board, email to <nusmv@fbk.eu>.

   --------------------------------------------------------------------------*/

/*!
  \author Roberto Cavada
  \brief The system wide symbol table interface

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TABLE_CLASS_H__
#define __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TABLE_CLASS_H__

#if HAVE_CONFIG_H
#  include "nusmv-config.h"
#endif

#include "nusmv/core/compile/symb_table/SymbLayer.h"
#include "nusmv/core/compile/symb_table/SymbCache.h"
#include "nusmv/core/cinit/NuSMVEnv.h"
#include "nusmv/core/compile/type_checking/TypeChecker.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/array.h"
#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/compile/symb_table/NFunction.h"
#include "nusmv/core/set/set.h"
#include "nusmv/core/compile/symb_table/ResolveSymbol.h"
#include "nusmv/core/utils/UStringMgr.h"
#include "nusmv/core/utils/Pair.h"
#include "nusmv/core/node/anonymizers/NodeAnonymizerBase.h"

/*!
  \struct SymbTable
  \brief SymbTable class accessors


*/
typedef struct SymbTable_TAG* SymbTable_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_TABLE(x)                           \
  ((SymbTable_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_TABLE_CHECK_INSTANCE(x)                    \
  (nusmv_assert(SYMB_TABLE(x) != SYMB_TABLE(NULL)))


/*!
  \brief Controls the filter type in some search dependencies routines

  Controls the filter type in some search dependencies routines.
  The available filters are:
  <dl>
  <dt> <tt>VFT_CURRENT</tt>
  <dd> filters out the current state variables</dd>
  </dt>
  <dt> <tt>VFT_NEXT</tt>
  <dd> filters out the next state variables</dd>
  </dt>
  <dt> <tt>VFT_INPUT</tt>
  <dd> filters out the input variables</dd>
  </dt>
  <dt> <tt>VFT_FROZEN</tt>
  <dd> filters out the frozen variables</dd>
  </dt>
  <dt> <tt>VFT_DEFINE</tt>
  <dd> filters out the DEFINE</dd>
  </dt>
  <dt> <tt>VFT_FUNCTION</tt>
  <dd> filters out the FUNCTION</dd>
  </dt>
  <dt> <tt>VFT_STATE</tt>
  <dd> filters out the current and next state variables</dd>
  </dt>
  <dt> <tt>VFT_CURR_INPUT</tt>
  <dd> filters out the current state and input variables</dd>
  </dt>
  <dt> <tt>VFT_CURR_FROZEN</tt>
  <dd> filters out the current state and frozen variables</dd>
  </dt>
  <dt> <tt>VFT_CNIF</tt>
  <dd>filters out all the variables. Constants NOT included</dd>
  </dt>
  <dt> <tt>VFT_CNIFD</tt>
  <dd>filters out all the variables and DEFINE.
  Constants NOT included</dd>
  </dt>
  <dt> <tt>VFT_ALL</tt>
  <dd>filters out all the variables, function, DEFINE and constants</dd>
  </dt>
  </dl>
  Combined modes can be obtained by bit-or: for example
  VFT_NEXT | VFT_INPUT is going to search for the variables which
  are next or input variables
*/

typedef enum SymbFilterType_TAG {
  VFT_CURRENT     = 1,
  VFT_NEXT        = VFT_CURRENT << 1,
  VFT_INPUT       = VFT_CURRENT << 2,
  VFT_FROZEN      = VFT_CURRENT << 3,
  VFT_DEFINE      = VFT_CURRENT << 4,
  VFT_FUNCTION    = VFT_CURRENT << 5,
  VFT_CONSTANTS   = VFT_CURRENT << 6,  /* only symbols */

  /* handy combinations */
  VFT_STATE       = (VFT_CURRENT | VFT_NEXT),
  VFT_CURR_INPUT  = (VFT_CURRENT | VFT_INPUT),
  VFT_CURR_FROZEN = (VFT_CURRENT | VFT_FROZEN),
  VFT_CNIF        = (VFT_CURRENT | VFT_NEXT   |
                     VFT_INPUT   | VFT_FROZEN), /* It was VFT_ALL */
  VFT_CNIFD       = (VFT_CNIF | VFT_DEFINE),    /* It was VFT_ALL_DEFINE */
  VFT_ALL         = (VFT_CNIFD | VFT_FUNCTION | VFT_CONSTANTS),

} SymbFilterType;


/*!
  \brief Describes the kind of symbol

  \todo Missing description
*/

typedef enum SymbCategory_TAG {
  SYMBOL_INVALID = 0, /* This is required by current implementation */
  SYMBOL_CONSTANT,
  SYMBOL_FROZEN_VAR,
  SYMBOL_STATE_VAR,
  SYMBOL_INPUT_VAR,
  SYMBOL_STATE_DEFINE,
  SYMBOL_INPUT_DEFINE,
  SYMBOL_STATE_INPUT_DEFINE,
  SYMBOL_NEXT_DEFINE,
  SYMBOL_STATE_NEXT_DEFINE,
  SYMBOL_INPUT_NEXT_DEFINE,
  SYMBOL_STATE_INPUT_NEXT_DEFINE,
  SYMBOL_DEFINE,
  SYMBOL_FUNCTION,
  SYMBOL_PARAMETER,
  SYMBOL_ARRAY_DEFINE,
  SYMBOL_VARIABLE_ARRAY,
} SymbCategory;


typedef enum SymbTableType_TAG {
  STT_NONE           = 0,
  STT_CONSTANT       = 1,

  STT_STATE_VAR      = STT_CONSTANT << 1, /* 2 */
  STT_INPUT_VAR      = STT_CONSTANT << 2, /* 4 */
  STT_FROZEN_VAR     = STT_CONSTANT << 3, /* 8 */
  STT_VAR            = (STT_STATE_VAR | STT_INPUT_VAR |
                        STT_FROZEN_VAR), /* 14 */

  STT_DEFINE         = STT_CONSTANT << 4, /* 16 */
  STT_ARRAY_DEFINE   = STT_CONSTANT << 5, /* 32 */

  STT_PARAMETER      = STT_CONSTANT << 6, /* 64 */

  STT_FUNCTION       = STT_CONSTANT << 7, /* 128 */

  STT_VARIABLE_ARRAY = STT_CONSTANT << 8, /* 256 */

  STT_ALL            = (STT_CONSTANT | STT_VAR | STT_DEFINE |
                        STT_ARRAY_DEFINE | STT_PARAMETER | STT_FUNCTION |
                        STT_VARIABLE_ARRAY), /* 511 */
} SymbTableType;

typedef enum SymbTableTriggerAction_TAG {
  ST_TRIGGER_SYMBOL_ADD,
  ST_TRIGGER_SYMBOL_REMOVE,
  ST_TRIGGER_SYMBOL_REDECLARE
} SymbTableTriggerAction;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*SymbTableForeachFun)(const SymbTable_ptr,
                                    const node_ptr sym,
                                    void* arg);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef boolean (*SymbTableIterFilterFun)(const SymbTable_ptr table,
                                          const node_ptr sym, void* arg);


/*!
  \brief Trigger called by the symbol table when a symbol change status


*/

typedef void (*SymbTableTriggerFun)(const SymbTable_ptr table,
                                    const node_ptr sym,
                                    SymbTableTriggerAction action,
                                    void* arg);

typedef struct SymbTableIter_TAG {
  unsigned int index;
  unsigned int mask;
  SymbTableIterFilterFun filter;
  SymbTable_ptr st;
  void* arg;
} SymbTableIter;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_TABLE_FOREACH(self, iter, mask)    \
  for (SymbTable_gen_iter(self, &iter, mask);   \
       !SymbTable_iter_is_end(self, &iter);     \
       SymbTable_iter_next(self, &iter))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_TABLE_FOREACH_FILTER(self, iter, mask, filter, arg)        \
  for (SymbTable_gen_iter(self, &iter, mask),                           \
           SymbTable_iter_set_filter(self, &iter, filter, arg);         \
       !SymbTable_iter_is_end(self, &iter);                             \
       SymbTable_iter_next(self, &iter))

/* ---------------------------------------------------------------------- */
/*     Public methods                                                     */
/* ---------------------------------------------------------------------- */

/*!
  \methodof SymbTable
  \brief Class constructor


  Environment requisites:
  - OptsHandler instance registered as ENV_OPTS_HANDLER
  - StreamMgr instance registered as ENV_STREAM_MANAGER
  - NodeMgr instance registered as ENV_NODE_MGR
  - NodePrinter instance registered as ENV_WFF_PRINTER
  - UStringMgr instance registered as ENV_STRING_MGR
*/
SymbTable_ptr SymbTable_create(NuSMVEnv_ptr env);

/*!
  \methodof SymbTable
  \brief Class destructor


*/
void SymbTable_destroy(SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the internally stored type checker

  Returned instance belongs to self
*/
TypeChecker_ptr
SymbTable_get_type_checker(const SymbTable_ptr self);

/* -------------------------------------- */
/*            ITERATORS                   */
/* -------------------------------------- */

/*!
  \methodof SymbTable
  \brief Initializes the given iterator with the given mask.

  Initializes the given iterator with the given mask.
  It is fundamental to call this procedure before
  using the iterator.

  SYMB_TABLE_FOREACH and SYMB_TABLE_FOREACH_FILTER
  automaticaly call this function, so the caller
  does not have to worry about that.
*/
void SymbTable_gen_iter(const SymbTable_ptr self,
                        SymbTableIter* iter,
                        unsigned int mask);

/*!
  \methodof SymbTable
  \brief Moves the iterator to the next valid symbol

  Moves the iterator to the next valid symbol
*/
void SymbTable_iter_next(const SymbTable_ptr self,
                         SymbTableIter* iter);

/*!
  \methodof SymbTable
  \brief Checks if the iterator is at it's end

  Checks if the iterator is at it's end
*/
boolean SymbTable_iter_is_end(const SymbTable_ptr self,
                              const SymbTableIter* iter);

/*!
  \methodof SymbTable
  \brief Gets the symbol pointed by the given iterator

  Gets the symbol pointed by the given iterator.
  The given iterator must not be at it's end
*/
node_ptr SymbTable_iter_get_symbol(const SymbTable_ptr self,
                                   const SymbTableIter* iter);

/*!
  \methodof SymbTable
  \brief Sets the filter to be used by the iterator

  Sets the filter to be used by the iterator.
  The iterator internally moves itself to the next
  valid symbol that satisfies both the filter and
  the mask
*/
void SymbTable_iter_set_filter(const SymbTable_ptr self,
                               SymbTableIter* iter,
                               SymbTableIterFilterFun fun,
                               void* arg);

/*!
  \methodof SymbTable
  \brief Executes the given function over each symbol
  that satisfies the given symbol mask

  Executes the given function over each symbol
  that satisfies the given symbol mask
*/
void SymbTable_foreach(const SymbTable_ptr self, unsigned int mask,
                       SymbTableForeachFun fun, void* arg);

/*!
  \methodof SymbTable
  \brief Creates a set starting from the iterator

  Creates a set starting from the iterator. The set
  must be freed. The iterator is NOT changed (it
  is passed as value..)
*/
Set_t SymbTable_iter_to_set(const SymbTable_ptr self,
                            SymbTableIter iter);

/*!
  \methodof SymbTable
  \brief Creates a set starting from the iterator

  Creates a list starting from the iterator. The list
  must be freed. The iterator is NOT changed (it
  is passed as value..)
*/
NodeList_ptr SymbTable_iter_to_list(const SymbTable_ptr self,
                                    SymbTableIter iter);

/*!
  \methodof SymbTable
  \brief Counts the elements of the iterator

  Counts the elements of the iterator. The iterator
  is NOT changed (it is passed as value..)
*/
unsigned int SymbTable_iter_count(const SymbTable_ptr self,
                                  SymbTableIter iter);

/*!
  \methodof SymbTable
  \brief Adds a trigger to the symbol table

  Adds a trigger to the symbol table.
  "arg" and "arg2" are the arguments that will be passed to
  function "trigger" when invoked.

  If the trigger is already registered (same
  function and same action), it is not added again

  The "action" parameter determines when "trigger"
  is triggered. The possibilities are:

  ST_TRIGGER_SYMBOL_ADD: Triggered when a symbol
  is added. When the trigger is called, all
  informations about the symbol are already
  available (e.g. SymbType).

  ST_TRIGGER_SYMBOL_REMOVE: Triggered when a
  symbol is removed. This may happen when
  removing a layer from the symbol table, or
  when removing a variable from a layer. All
  informations about the symbol are still
  available when the trigger is invoked

  ST_TRIGGER_SYMBOL_REDECLARE: Triggered when a
  symbol that had been removed from the symbol
  table, or a from a layer, is redeclared with
  the same name. All informations about the new
  symbol are available, while informations about
  the old symbol are not.

  Param must_free_arg controls if given argument must be freed upon trigger
  destruction.

  \sa SymbTable_remove_trigger
*/
void
SymbTable_add_trigger(const SymbTable_ptr self,
                      SymbTableTriggerFun trigger,
                      SymbTableTriggerAction action,
                      void* arg1, boolean must_free_arg);

/*!
  \methodof SymbTable
  \brief Removes a trigger from the Symbol Table

  Removes a trigger from the Symbol Table

  \sa SymbTable_add_trigger
*/
void
SymbTable_remove_trigger(const SymbTable_ptr self,
                         SymbTableTriggerFun trigger,
                         SymbTableTriggerAction action);

/* -------------------------------------- */
/*            Built-in filters            */
/* -------------------------------------- */

/*!
  \methodof SymbTable
  \brief Default iterator filter: Input symbols

  Default iterator filter: Input symbols.
  Only defines that predicate over input variables
  or input variables themselfs satisfy this
  filter.
*/
boolean
SymbTable_iter_filter_i_symbols(const SymbTable_ptr self,
                                const node_ptr sym,
                                void* arg);

/*!
  \methodof SymbTable
  \brief Default iterator filter: State, Frozen and Input symbols

  Default iterator filter: State, Frozen and Input symbols.
  Only defines that predicate over state or frozen
  AND input variables or variables themselfs
  satisfy this filter.
*/
boolean
SymbTable_iter_filter_sf_i_symbols(const SymbTable_ptr self,
                                   const node_ptr sym,
                                   void* arg);

/*!
  \methodof SymbTable
  \brief Default iterator filter: State, Frozen symbols

  Default iterator filter: State, Frozen symbols.
  Only defines that predicate over state or frozen
  variables or state / frozen variables themselfs
  satisfy this filter.
*/
boolean
SymbTable_iter_filter_sf_symbols(const SymbTable_ptr self,
                                 const node_ptr sym,
                                 void* arg);

/*!
  \methodof SymbTable
  \brief Default iterator filter: skip var array elements

  Variables declared from an array are skipped
*/
boolean SymbTable_iter_filter_out_var_array_elems(const SymbTable_ptr self,
                                                  const node_ptr sym,
                                                  void* arg);


/* -------------------------------------- */
/*            Layers handling             */
/* -------------------------------------- */

/*!
  \methodof SymbTable
  \brief Creates and adds a new layer

  The created layer is returned. Do not destroy the
  layer, since it belongs to self. if layer name is NULL, then a
  temporary name will be searched and a new layer will be created. To
  retrieve the layer name, query the returned SymbLayer instance.
  layer_name must not exist within self

  \sa remove_layer
*/
SymbLayer_ptr
SymbTable_create_layer(SymbTable_ptr self, const char* layer_name,
                       const LayerInsertPolicy ins_policy);

/*!
  \methodof SymbTable
  \brief Removes and destroys a layer

  The layer must be not in use by any encoding, so remove
  it from all encodings before calling this method. The removed layer
  will be no longer available after the invocation of this method.

  If given layer belongs to a set of layer classes, the layer will
  be removed from the classes as well (meaning that there is no
  need to remove the layer from the classes it belongs to)

  If you are going to destroy the symb table, you could avoid this call

  \sa create_layer
*/
void
SymbTable_remove_layer(SymbTable_ptr self, SymbLayer_ptr layer);

/*!
  \methodof SymbTable
  \brief \todo Missing synopsis

  \todo Missing description
*/
SymbLayer_ptr
SymbTable_get_layer(const SymbTable_ptr self,
                    const char* layer_name);

/*!
  \methodof SymbTable
  \brief True if layer_name belongs to self

  True if layer_name belongs to self
*/
boolean
SymbTable_has_layer(const SymbTable_ptr self,
                    const char* layer_name);

/*!
  \methodof SymbTable
  \brief Renames an existing layer

  Use to rename an existing layer. Useful for example to
  substitute an existing layer with another.
*/
void
SymbTable_rename_layer(const SymbTable_ptr self,
                       const char* layer_name, const char* new_name);

/*!
  \methodof SymbTable
  \brief Returns the list of owned layers.

  The returned list belongs to self. Do not free or
  change it.
*/
NodeList_ptr SymbTable_get_layers(const SymbTable_ptr self);


/* -------------------------------------- */
/*                Symbols                 */
/* -------------------------------------- */

/* Lists of symbols: */

/*!
  \methodof SymbTable
  \brief Returns the list of state and frozen symbols
  that belong to the given layers

  Everytime this method is called, it will create and
  calculate a new list. layers is an array of strings.
  WARNING: The returned instance must be destroyed by the caller.
  Note: state symbols include frozen variables.
*/
NodeList_ptr
SymbTable_get_layers_sf_symbols(SymbTable_ptr self,
                                const array_t* layer_names);

/*!
  \methodof SymbTable
  \brief Returns the list of state and frozen variables
  that belong to the given layers

  Everytime this method is called, it will create and
  calculate a new list. layers is an array of strings.
  WARNING: The returned instance must be destroyed by the caller
*/
NodeList_ptr
SymbTable_get_layers_sf_vars(SymbTable_ptr self,
                             const array_t* layer_names);

/*!
  \methodof SymbTable
  \brief Returns the list of input symbols that belong to the
  given layers

  Everytime this method is called, it will create and
  calculate a new list. layers is an array of strings.
  WARNING: The returned instance must be destroyed by the caller
*/
NodeList_ptr
SymbTable_get_layers_i_symbols(SymbTable_ptr self,
                               const array_t* layer_names);

/*!
  \methodof SymbTable
  \brief Returns the list of input variables that belong to the
  given layers

  Everytime this method is called, it will create and
  calculate a new list. layers is an array of strings.
  WARNING: The returned instance must be destroyed by the caller
*/
NodeList_ptr
SymbTable_get_layers_i_vars(SymbTable_ptr self,
                            const array_t* layer_names);

/*!
  \methodof SymbTable
  \brief Returns the list of state and input symbols that
  belong to the given layers, meaning those DEFINES whose body
  contain both state (or frozen) and input variables. This methods
  does _NOT_ return the state symbols plus the input symbols.

  Everytime this method is called, it will create and
  calculate a new list. layers is an array of strings.
  WARNING: The returned instance must be destroyed by the caller
*/
NodeList_ptr
SymbTable_get_layers_sf_i_symbols(SymbTable_ptr self,
                                  const array_t* layer_names);

/*!
  \methodof SymbTable
  \brief Returns the list of variables that belong to the given layers

  Everytime this method is called, it will create and
  calculate a new list. layers is an array of strings.
  WARNING: The returned instance must be destroyed by the caller
*/
NodeList_ptr
SymbTable_get_layers_sf_i_vars(SymbTable_ptr self,
                               const array_t* layer_names);

/* Number of symbols: */

/*!
  \methodof SymbTable
  \brief Returns the number of all declared variables


*/
int SymbTable_get_vars_num(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the number of all declared state variables


*/
int SymbTable_get_state_vars_num(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the number of all declared frozen variables


*/
int SymbTable_get_frozen_vars_num(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the number of all declared input variables


*/
int SymbTable_get_input_vars_num(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the number of all declared defines


*/
int SymbTable_get_defines_num(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the number of all declared array define


*/
int SymbTable_get_array_defines_num(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the number of all parameters


*/
int SymbTable_get_parameters_num(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the number of all declared constants


*/
int SymbTable_get_constants_num(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the number of all NFunctions


*/
int SymbTable_get_functions_num(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the number of all symbols


*/
int SymbTable_get_symbols_num(const SymbTable_ptr self);


/* Classes of layers: */

/*!
  \methodof SymbTable
  \brief Declares a new class of layers

  This method creates a new class of layers. The
  class must be not existing. The method can be used to create a
  class of layers that might be empty. It is not required to
  create a class before calling methods that use that class, like
  e.g.  SymbTable_layer_add_to_class that wll create the class
  when not existing. class_name can be NULL to create the default
  class (whose name must have been previously specified with
  SymbTable_set_default_layers_class_name)

  \sa SymbTable_layer_add_to_class
*/
void
SymbTable_create_layer_class(SymbTable_ptr self,
                             const char* class_name);

/*!
  \methodof SymbTable
  \brief Checks if a class of layers exists

  This method checks if class 'class_name' has been
  previously created in the SymbTable.Returns true if the class exists,
  false otherwise.

  \se None
*/
boolean
SymbTable_layer_class_exists(SymbTable_ptr self,
                             const char* class_name);

/*!
  \methodof SymbTable
  \brief Adds a given layer (that must exist into self already)
  to a class of layers. Classes are used to group layers into
  possibly overlapping sets. For example the class of layers
  containing the set of symbols that belongs to the SMV model.  If
  class_name is NULL, the default class name will be taken (must
  be set before)

  A new class will be created if given class does not
  exist yet. The given layer must be existing.

  \sa SymbTable_layer_remove_from_class
*/
void
SymbTable_layer_add_to_class(SymbTable_ptr self,
                             const char* layer_name,
                             const char* class_name);

/*!
  \methodof SymbTable
  \brief Removes a given layer (that must exist into self already)
  from a given class of layers. If class_name is NULL, the default class
  is taken (must be set before)

  Given class must be existing, or if NULL default
  class must be existing. If the layer is not found, nothing happens.

  \sa SymbTable_layer_add_to_class
*/
void
SymbTable_layer_remove_from_class(SymbTable_ptr self,
                                  const char* layer_name,
                                  const char* class_name);

/*!
  \methodof SymbTable
  \brief Returns an array of layer names that belong to the
  given class name. If class_name is NULL, default class name will
  be taken (must be set before).

  Specified class must be existing, or if NULL is
  specified a default class must have been defined. Returned
  array belongs to self and has NOT to be destroyed or changed by
  the caller.
*/
array_t*
SymbTable_get_class_layer_names(SymbTable_ptr self,
                                const char* class_name);

/*!
  \methodof SymbTable
  \brief Returns true if given layer name belongs to the given class

  If class_name is NULL, the default class will be checked
*/
boolean
SymbTable_is_layer_in_class(SymbTable_ptr self,
                            const char* layer_name,
                            const char* class_name);

/*!
  \methodof SymbTable
  \brief


*/
void
SymbTable_set_default_layers_class_name(SymbTable_ptr self,
                                        const char* class_name);

/*!
  \methodof SymbTable
  \brief Returns the default layers class name that has been
  previously set. The default layers class name is the class of
  layers that is taken when the system needs a default set of
  layers to work with. Typically the default class is the class of
  model layers, that is used for example when dumping the
  hierarchy by command write_bool_model.

  Returned string belongs to self, and must be NOT
  destroyed or changed. Returned string is NULL if not previously set.

  \sa SymbTable_set_default_layers_class_name
*/
const char*
SymbTable_get_default_layers_class_name(const SymbTable_ptr self);


/* Symbols related info: */

/*!
  \methodof SymbTable
  \brief Returns the type of a given variable

  The type belongs to the layer, do not destroy it.
  "name" is assumed to be a var
*/
SymbType_ptr
SymbTable_get_var_type(const SymbTable_ptr self, const node_ptr name);

/* Symbols related info: */

/*!
  \methodof SymbTable
  \brief Returns the type of a given function

  "name" is assumed to be a function
*/
SymbType_ptr
SymbTable_get_function_type(const SymbTable_ptr self, const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the body of the given DEFINE


*/
node_ptr
SymbTable_get_define_body(const SymbTable_ptr self,
                          const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the NFunction with the given name


*/
NFunction_ptr
SymbTable_get_function(const SymbTable_ptr self,
                       const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the actual param of the given formal parameter


*/
node_ptr
SymbTable_get_actual_parameter(const SymbTable_ptr self,
                               const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the body of the given array define name


*/
node_ptr
SymbTable_get_array_define_body(const SymbTable_ptr self,
                                const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the type of the given var array


*/
SymbType_ptr
SymbTable_get_variable_array_type(const SymbTable_ptr self,
                                  const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the flattenized body of the given
  define


*/
node_ptr
SymbTable_get_define_flatten_body(const SymbTable_ptr self,
                                  const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the flattenized actual parameter of the given
  formal parameter


*/
node_ptr
SymbTable_get_flatten_actual_parameter(const SymbTable_ptr self,
                                       const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the flattened body of the given array define name


*/
node_ptr
SymbTable_get_array_define_flatten_body(const SymbTable_ptr self,
                                        const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the context of the given DEFINE name


*/
node_ptr
SymbTable_get_define_context(const SymbTable_ptr self,
                             const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the context of the NFunction with the given name


*/
node_ptr
SymbTable_get_function_context(const SymbTable_ptr self,
                               const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the context of the actual parameter associated
  with the given formal one


*/
node_ptr
SymbTable_get_actual_parameter_context(const SymbTable_ptr self,
                                       const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the context of the given array define name


*/
node_ptr
SymbTable_get_array_define_context(const SymbTable_ptr self,
                                   const node_ptr name);

/*!
  \methodof SymbTable
  \brief This function returns the category of
  an identifier

  If a symbol is not properly recognized, SYMBOL_INVALID is returned.

  An identifier is var or define. It is also allowed to have arrays
  with constant index, i.e. if V is identifier than V[5] is also
  identifier.

  \se None
*/
SymbCategory
SymbTable_get_symbol_category(const SymbTable_ptr self,
                              const node_ptr name);

/* Queries: */

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is a state variable.


*/
boolean
SymbTable_is_symbol_state_var(const SymbTable_ptr self,
                              const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the type of a given symbol

  The type belongs to self, do not destroy it.
  Symbol must be declared, but if has no type (e.g. is a define), NULL is
  returned
*/
SymbType_ptr
SymbTable_get_symbol_type(const SymbTable_ptr self,
                          const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is a frozen variable.


*/
boolean
SymbTable_is_symbol_frozen_var(const SymbTable_ptr self,
                               const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is a frozen or a state variable.


*/
boolean
SymbTable_is_symbol_state_frozen_var(const SymbTable_ptr self,
                                     const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is an input variable.


*/
boolean
SymbTable_is_symbol_input_var(const SymbTable_ptr self,
                              const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is either a state, frozen or
  an input variable.


*/
boolean
SymbTable_is_symbol_var(const SymbTable_ptr self, const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is a variable of enum type
  with the values 0 and 1 (boolean)


*/
boolean
SymbTable_is_symbol_bool_var(const SymbTable_ptr self,
                             const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is declared


*/
boolean
SymbTable_is_symbol_declared(const SymbTable_ptr self,
                             const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is a declared
  DEFINE


*/
boolean
SymbTable_is_symbol_define(const SymbTable_ptr self,
                           const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is a declared
  NFunction


*/
boolean
SymbTable_is_symbol_function(const SymbTable_ptr self,
                             const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is a declared
  parameter


*/
boolean
SymbTable_is_symbol_parameter(const SymbTable_ptr self,
                              const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is a declared
  array define


*/
boolean
SymbTable_is_symbol_array_define(const SymbTable_ptr self,
                                 const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is a declared
  variable array


*/
boolean
SymbTable_is_symbol_variable_array(const SymbTable_ptr self,
                                   const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbol is a declared
  constant

  Notice that this method will check only symbols defined
  within self. For example if an integer constant was not declared
  within self, this method will return false for it. For generic
  expressions, consider using function node_is_leaf which performs a
  purely-syntactly check.
*/
boolean
SymbTable_is_symbol_constant(const SymbTable_ptr self,
                             const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true iff this name is sub-element of
  a variable array.
*/
boolean
SymbTable_is_symbol_array_var_element(const SymbTable_ptr symb_table,
                                      const node_ptr name);

/*!
  \methodof SymbTable
  \brief Return the variable array corresponding to element

  element must be a valid array element (NULL is never returned). An array
  element is a variable corresponding to an array index.

  \sa SymbTable_is_symbol_array_var_element
*/
node_ptr SymbTable_get_var_array_from_element(const SymbTable_ptr self,
                                              node_ptr element);

/*!
  \methodof SymbTable
  \brief Return the variable corresponding to lower bound of array

  array must be a valid variable array (NULL is never returned)
*/
node_ptr SymbTable_get_array_lower_bound_variable(const SymbTable_ptr self,
                                                  node_ptr array);
/*!
  \methodof SymbTable
  \brief Return the variable corresponding to upper bound of array

  array must be a valid variable array (NULL is never returned)
*/
node_ptr SymbTable_get_array_upper_bound_variable(const SymbTable_ptr self,
                                                  node_ptr array);

/*!
  \methodof SymbTable
  \brief True if the variables contained in array are input vars
*/
boolean SymbTable_is_symbol_input_var_array(const SymbTable_ptr self,
                                            node_ptr array);

/*!
  \methodof SymbTable
  \brief True if the variables contained in array are frozen vars
*/
boolean SymbTable_is_symbol_frozen_var_array(const SymbTable_ptr self,
                                             node_ptr array);

/*!
  \methodof SymbTable
  \brief True if the variables contained in array are state vars
*/
boolean SymbTable_is_symbol_state_var_array(const SymbTable_ptr self,
                                            node_ptr array);


/*!
  \methodof SymbTable
  \brief Returns true if the given variable has a finite domain


*/
boolean
SymbTable_is_var_finite(const SymbTable_ptr self, const node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns true if var_list contains at least one input
  variable, false otherwise

  The given list of variables is traversed until an input
  variable is found
*/
boolean
SymbTable_list_contains_input_var(const SymbTable_ptr self,
                                  const NodeList_ptr var_list);

/*!
  \methodof SymbTable
  \brief Returns true if var_list contains at least one state
  or frozen variable, false otherwise

  The given list of variables is traversed until
  a state or frozen variable is found
*/
boolean
SymbTable_list_contains_state_frozen_var(const SymbTable_ptr self,
                                         const NodeList_ptr var_list);

/*!
  \methodof SymbTable
  \brief Returns true if the given symbols list contains
  one or more undeclared variable names, false otherwise

  Iterates through the elements in var_list
  checking each one to see if it is one undeclared variable.
*/
boolean
SymbTable_list_contains_undef_var(const SymbTable_ptr self,
                                  const NodeList_ptr var_list);

/*!
  \methodof SymbTable
  \brief Returns a set of type tags of all variables, variable arrays and
  functions contained in the symbol table

  Returns a set of SymbTypeTag, to be destroyed by the caller.
*/
Set_t SymbTable_get_type_tags(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Checks whether the Symbol Table contains infinite
  precision variables

  Checks whether the Symbol Table contains infinite
  precision variables
*/
boolean
SymbTable_contains_infinite_precision_variables(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Checks whether the Symbol Table contains enum variables

  Checks whether the Symbol Table contains enum variables
*/
boolean
SymbTable_contains_enum_variables(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Checks whether the Symbol Table contains word variables

  Checks whether the Symbol Table contains word variables
*/
boolean
SymbTable_contains_word_variables(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Checks whether the Symbol Table contains array variables

  Checks whether the Symbol Table contains array variables
*/
boolean
SymbTable_contains_array_variables(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Checks whether the Symbol Table contains functions

  Checks whether the Symbol Table contains functions
*/
boolean
SymbTable_contains_functions(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Returns the layer a variable is defined in.

  Returns the layer a variable is defined in, NULL
  if there is no layer containing it.
*/
SymbLayer_ptr
SymbTable_variable_get_layer(SymbTable_ptr  self, node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the layer a DEFINE is defined in.

  Returns the layer a DEFINE is defined in, NULL
  if there is no layer containing it.
*/
SymbLayer_ptr
SymbTable_define_get_layer(SymbTable_ptr  self, node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the layer a symbol is defined in.

  Returns the layer a symbol is defined in, NULL
  if there is no layer containing it.
*/
SymbLayer_ptr
SymbTable_symbol_get_layer(SymbTable_ptr  self, node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns the layer a NFunction is defined in.

  Returns the layer a NFunction is defined in, NULL
  if there is no layer containing it.
*/
SymbLayer_ptr
SymbTable_function_get_layer(SymbTable_ptr  self, node_ptr name);

/*!
  \methodof SymbTable
  \brief Returns a valid name for a new determinization variable

  Returns a valid name for a new determinization
  variable.  Searches in the symbol table for a
  variable name which is not declared yet, and
  returns it. Warning: This method does not
  declare a new variable, it simply finds a
  valid name for a new determinization
  variable. If the returned variable name is
  not used later to declare a new variable,
  succeed calls to this method may not return a
  valid name.

  \sa symb_table_deinit
*/
node_ptr
SymbTable_get_determinization_var_name(const SymbTable_ptr self);

/*!
  \methodof SymbTable
  \brief Given a prefix, returns a fresh symbol name.  This
  function NEVER returns the same symbol twice and
  NEVER returns a declared name

  If prefix is NULL then a valid fresh symbol is choosed.
*/
node_ptr
SymbTable_get_fresh_symbol_name(const SymbTable_ptr self,
                                const char * prefix);


/*!
  \methodof SymbTable
  \brief Given a string name, constructs a node and returns it,
  using the internal node and string managers.

  NOTE: This method is used by SymbTable_get_fresh_symbol_name to
  construct a node out of a string.
*/
node_ptr SymbTable_get_symbol_from_str(const SymbTable_ptr self,
                                       const char* symbol_str);

/*!
  \methodof SymbTable
  \brief Returns the name of the class in which the given layer is
  declared or NULL if there is no such a class.
*/
const char*
SymbTable_get_class_of_layer(const SymbTable_ptr self,
                             const char* layer_name);

/*!
  \methodof SymbTable
  \todo
*/
ResolveSymbol_ptr
SymbTable_resolve_symbol(SymbTable_ptr self,
                         node_ptr expr, node_ptr context);

/*!
  \methodof SymbTable
  \brief Create a new SymbolTable which contains the same info as
  the given one except the specified symbols in
  blacklist, in the same environment

  Returned ST is allocated and has to be released by caller.
  The copy is performed iterating over each layer in the
  Symbol Table. The new ST contains a copy of each layer of
  the given Symbol Table

  \sa SymbTable_create SymbTable_destroy
*/
SymbTable_ptr SymbTable_copy(SymbTable_ptr self,
                             Set_t blacklist);

/*!
  \methodof SymbTable
  \brief Retrieves a special hash_ptr instance handled by the
  SymbTable.

  Retrieves a special hash_ptr instance handled by the SymbTable.
  If the given hash already exists in the SymbTable, then that instance is
  returned. Otherwise, a new one is created and added in the SymbTable. The
  returned hash_ptr will be freed by the SymbTable. Entries in such
  hash will be freed using <destroy_func>. Avoid multiple calls to this
  function if possible (e.g. in triggers, pass the hash_ptr as trigger
  argument).
  For a particular hash, this function has to be called always with the same
  arguments (they are ignored after the first call). This can be checked
  defining MEMOIZED_HASH_DEBUG.
  It is possible to request a customize hash table passing <compare_func> and
  <hash_func>.

  It is even possible to pass functions to be triggered when the symbol table
  is modified. Tipically, an hash clearing function is passed as
  <remove_action>. For remove_action, for clearing the hash it may be useful
  to use function SymbTable_clear_handled_remove_action_hash.
*/
hash_ptr SymbTable_get_handled_hash_ptr(SymbTable_ptr self,
                                        const char* key,
                                        ST_PFICPCP compare_func,
                                        ST_PFICPI hash_func,
                                        ST_PFSR destroy_func,
                                        SymbTableTriggerFun add_action,
                                        SymbTableTriggerFun remove_action,
                                        SymbTableTriggerFun redeclare_action
                                        );

/*!
  \brief This function can be used with SymbTable_get_handled_hash_ptr
  for the parameter remove_action.

  arg is an AssocAndDestroy_ptr. The type of this function is
  SymbTableTriggerFun

  \sa SymbTable_get_handled_hash_ptr
*/
void
SymbTable_clear_handled_remove_action_hash(const SymbTable_ptr st,
                                           const node_ptr sym,
                                           SymbTableTriggerAction action,
                                           void* arg);

/*!
  \methodof SymbTable
  \brief Return the string version of the SymbCategory of symbol
  Returned string must NOT be freed
*/
char* SymbTable_sprint_category(SymbTable_ptr self,
                                node_ptr symbol);

/*!
  \methodof SymbTable
  \brief \todo Anonymize a symb table

  \todo input symbol table is copied
*/
SymbTable_ptr SymbTable_anonymize(const SymbTable_ptr self,
                                  Set_t blacklist,
                                  NodeAnonymizerBase_ptr anonymizer);


#endif /* __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_TABLE_CLASS_H__ */
