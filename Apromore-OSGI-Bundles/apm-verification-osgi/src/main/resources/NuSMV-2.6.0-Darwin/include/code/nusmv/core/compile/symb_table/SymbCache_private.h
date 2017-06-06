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
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

  For more information on NuSMV see <http://nusmv.fbk.eu>
  or email to <nusmv-users@fbk.eu>.
  Please report bugs to <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to <nusmv@fbk.eu>. 

-----------------------------------------------------------------------------*/

/*!
  \author Roberto Cavada
  \brief The SymbCache class private interface

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_CACHE_PRIVATE_H__
#define __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_CACHE_PRIVATE_H__


#include "nusmv/core/compile/symb_table/SymbCache.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/compile/symb_table/NFunction.h"
#include "nusmv/core/node/node.h"

/*---------------------------------------------------------------------------*/
/* Type definitions                                                          */
/*---------------------------------------------------------------------------*/

/* ---------------------------------------------------------------------- */
/*     Private methods                                                    */
/* ---------------------------------------------------------------------- */

/*!
  \methodof SymbCache
  \brief Class constructor

  Callable only by the SymbTable instance that owns self.
  The caller keeps the ownership of given SymbTable instance
*/
SymbCache_ptr SymbCache_create(SymbTable_ptr symb_table,
                                      NuSMVEnv_ptr env);

/*!
  \methodof SymbCache
  \brief Class destructor

  Callable only by the SymbTable instance that owns self.
*/
void SymbCache_destroy(SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Declares a new input variable.

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
*/
void
SymbCache_new_input_var(SymbCache_ptr self,
                        node_ptr var, SymbType_ptr type);

/*!
  \methodof SymbCache
  \brief Declares a new state variable.

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
*/
void
SymbCache_new_state_var(SymbCache_ptr self,
                        node_ptr var, SymbType_ptr type);

/*!
  \methodof SymbCache
  \brief Declares a new frozen variable.

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.

  \sa SymbCache_redeclare_state_as_frozen_var
*/
void
SymbCache_new_frozen_var(SymbCache_ptr self,
                          node_ptr var, SymbType_ptr type);

/*!
  \methodof SymbCache
  \brief Redeclare a state variable as a frozen variable

  A variable is frozen if it is known that its value cannot
  be changed during transitions.
  The given 'name' has to be already declared state variable and not yet
  redeclared as frozen.

  \sa SymbCache_new_frozen_var
*/
void
SymbCache_redeclare_state_as_frozen_var(SymbCache_ptr self,
                                        node_ptr var);

/*!
  \methodof SymbCache
  \brief Removes a variable from the cache of symbols, and from
  the flattener module

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
*/
void
SymbCache_remove_var(SymbCache_ptr self, node_ptr var);

/*!
  \methodof SymbCache
  \brief Declares a new DEFINE.

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
*/
void
SymbCache_new_define(SymbCache_ptr self,
                     node_ptr name,
                     node_ptr context, node_ptr definition);

/*!
  \methodof SymbCache
  \brief Declares a new NFunction.

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
*/
void
SymbCache_new_function(SymbCache_ptr self, node_ptr name,
                       node_ptr context, SymbType_ptr type);

/*!
  \methodof SymbCache
  \brief Declares a new module parameter.

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
*/
void
SymbCache_new_parameter(SymbCache_ptr self,
                        node_ptr formal,
                        node_ptr context, node_ptr actual);

/*!
  \methodof SymbCache
  \brief Declares a new define array.

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
  Internally we use ARRAY_DEF node to recognize a define array.
*/
void
SymbCache_new_array_define(SymbCache_ptr self, node_ptr name,
                            node_ptr ctx, node_ptr definition);

/*!
  \methodof SymbCache
  \brief Declares a new ARRAY var.

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
*/
void
SymbCache_new_variable_array(SymbCache_ptr self, node_ptr name,
                             SymbType_ptr type);

/*!
  \methodof SymbCache
  \brief Removes a DEFINE from the cache of symbols, and from
  the flattener define hash

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
*/
void
SymbCache_remove_define(SymbCache_ptr self, node_ptr define);

/*!
  \methodof SymbCache
  \brief Removes an NFunction from the cache of symbols

  This (private) method can be used only by
                      SymbLayer, otherwise the resulting status
                      will be corrupted.
*/
void
SymbCache_remove_function(SymbCache_ptr self, node_ptr name);

/*!
  \methodof SymbCache
  \brief Declares a new constant.

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted. Multiple-time
  declared constant are accepted, and a reference count is kept to deal with
  them
*/
void
SymbCache_new_constant(SymbCache_ptr self, node_ptr name);

/*!
  \methodof SymbCache
  \brief Removes a constant from the cache of symbols, and from
  the flattener module

  Removal is performed taking into account of reference
  counting, as constants can be shared among several layers. This
  (private) method can be used only by SymbLayer, otherwise the
  resulting status will be corrupted.
*/
void
SymbCache_remove_constant(SymbCache_ptr self, node_ptr constant);

/*!
  \methodof SymbCache
  \brief Removes all the symbols in the array

  Removes all the symbols in the array in
                      linear time
*/
void
SymbCache_remove_symbols(SymbCache_ptr self,
                         const node_ptr* symbols,
                         const unsigned int size);

/*!
  \methodof SymbCache
  \brief Get the symbol type

  Get the symbol type. The symbol must be declared
                      in the cache
*/
SymbTableType
SymbCache_get_symbol_type(const SymbCache_ptr self,
                          const node_ptr symbol);

/*!
  \methodof SymbCache
  \brief Generates an interator over the Symbol Cache symbols

  Generates an interator over the Symbol Cache symbols.
                      The iterator will ignore all symbols that do not
                      satisfy the mask
*/
void
SymbCache_gen_iter(const SymbCache_ptr self,
                   SymbTableIter* iter,
                   const unsigned int mask);

/*!
  \methodof SymbCache
  \brief Moves the iterator over the next symbol

  Moves the iterator over the next symbol,
                      regarding to the mask given when built using
                      SymbCache_gen_iter
*/
void
SymbCache_next_iter(const SymbCache_ptr self,
                    SymbTableIter* iter);

/*!
  \methodof SymbCache
  \brief Checks if the iterator is at it's end

  Checks if the iterator is at it's end
*/
boolean SymbCache_is_iter_end(const SymbCache_ptr self,
                                     const SymbTableIter* iter);

/*!
  \methodof SymbCache
  \brief Get the symbol pointed by the iterator

  Get the symbol pointed by the iterator
*/
node_ptr SymbCache_iter_get_symbol(const SymbCache_ptr self,
                                          const SymbTableIter* iter);

/*!
  \methodof SymbCache
  \brief Sets the filter for an interator over the
                      Symbol Cache symbols

  Sets the filter for an interator over the
                      Symbol Cache symbols. The iterator will be moved
                      in order to point to a symbol that satisfies
                      both the mask and the filter
*/
void SymbCache_iter_set_filter(const SymbCache_ptr self,
                                      SymbTableIter* iter,
                                      SymbTableIterFilterFun filter,
                                      void* arg);

/*!
  \methodof SymbCache
  \brief Returns the number of declared contants

  
*/
int SymbCache_get_constants_num(const SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Returns the number of declared state variables.

  
*/
int SymbCache_get_state_vars_num(const SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Returns the number of declared frozen variables.

  
*/
int SymbCache_get_frozen_vars_num(const SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Returns the number of declared input variables

  
*/
int SymbCache_get_input_vars_num(const SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Returns the number of DEFINEs.

  
*/
int SymbCache_get_defines_num(const SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Returns the number of NFunctions.

  
*/
int SymbCache_get_functions_num(const SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Returns the number of parameters.

  
*/
int SymbCache_get_parameters_num(const SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Returns the number of define arrays.

  
*/
int SymbCache_get_array_defines_num(const SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Returns the number of Symbol Types.

  
*/
int SymbCache_get_variable_arrays_num(const SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Returns the number of symbols.

  
*/
int SymbCache_get_symbols_num(const SymbCache_ptr self);


/*!
  \methodof SymbCache
  \brief Adds a trigger to the symbol cache

  Adds a trigger to the symbol cache.
                      "arg" is the argument that will be passed to
                      function "trigger" when invoked.

                      must_free_arg controls if the given argument must be
                      automatically freed when cleaning up.

                      If the trigger is already registered (same
                      function and same action), it is not added again

                      The "action" parameter determines when "trigger"
                      is triggered. The possibilities are:

                      ST_TRIGGER_SYMBOL_ADD: Triggered when a symbol
                        is added. When the trigger is called, all
                        informations about the symbol are already
                        available (e.g. SymbType).

                      ST_TRIGGER_SYMBOL_REMOVE: Triggered when a
                        symbol is removed. All informations about the
                        symbol are still available when the trigger is
                        invoked.

                      ST_TRIGGER_SYMBOL_REDECLARE: Triggered when a
                        symbol that had been removed and later
                        redeclared with the same name. All
                        informations about the new symbol are
                        available, while informations about the old
                        symbol are not

  \sa SymbCache_remove_trigger
*/
void
SymbCache_add_trigger(const SymbCache_ptr self,
                      SymbTableTriggerFun trigger,
                      SymbTableTriggerAction action,
                      void* arg1, boolean must_free_arg);

/*!
  \methodof SymbCache
  \brief Removes a trigger from the symbol cache

  Removes a trigger from the symbol cache

  \sa SymbCache_add_trigger
*/
void
SymbCache_remove_trigger(const SymbCache_ptr self,
                         SymbTableTriggerFun trigger,
                         SymbTableTriggerAction action);


/*!
  \methodof SymbCache
  \brief Removes a parameter from the cache of symbols

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
*/
void SymbCache_remove_parameter(SymbCache_ptr self, node_ptr formal);

/*!
  \methodof SymbCache
  \brief Removes a variable array from the cache of symbols

  This (private) method can be used only by SymbLayer,
  otherwise the resulting status will be corrupted.
*/
void SymbCache_remove_variable_array(SymbCache_ptr self, node_ptr symbol);


#endif /* __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_CACHE_PRIVATE_H__ */
