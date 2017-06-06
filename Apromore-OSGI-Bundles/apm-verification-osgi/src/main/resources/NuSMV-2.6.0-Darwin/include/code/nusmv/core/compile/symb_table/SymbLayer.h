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
  \brief The wide system symbols layer interface

  This is the public interface of the class SymbLayer

*/


#ifndef __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_LAYER_H__
#define __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_LAYER_H__

#include "nusmv/core/compile/symb_table/SymbType.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/compile/symb_table/NFunction.h"
#include "nusmv/core/set/set.h"

/*!
  \struct SymbLayer
  \brief Public type accessor for class SymbLayer

  See the description of class SymbLayer for further
  information.
*/
typedef struct SymbLayer_TAG* SymbLayer_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_LAYER(x)  \
        ((SymbLayer_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_LAYER_CHECK_INSTANCE(x)  \
        (nusmv_assert(SYMB_LAYER(x) != SYMB_LAYER(NULL)))


/*!
  \brief To be used as a policy when a layer is pushed in the
  layers stack of a SymbTable

  WHen a layer is pushed within a symbol table, it will
  be inserted according to an insertion order that will change the order the
  symbols occuring within the layer are encoded in the encodings.
  The default behaviour is to push the layer on the top of the stack.

  Forced positions (SYMB_LAYER_POS_FORCE_*) make the layer to stay always
  at that position. Only one layer can be added to a symbol table with
  the same forced postion at a given time, i.e. two or more layers are not
  allowed to have the same forced position into the same symbol table.
  
*/

typedef enum LayerInsertPolicy_TAG {
  SYMB_LAYER_POS_DEFAULT,   /* default is equal to SYMB_LAYER_POS_TOP */
  SYMB_LAYER_POS_FORCE_TOP, /* layer is forced to be always at the top */
  SYMB_LAYER_POS_TOP,       /* Inserted before other top */
  SYMB_LAYER_POS_BOTTOM,    /* inserted after other bottom */
  SYMB_LAYER_POS_FORCE_BOTTOM /* layer is forced to be always at the bottom */
} LayerInsertPolicy;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef boolean (*SymbLayerIterFilterFun)(const SymbLayer_ptr layer,
                                          const node_ptr sym,
                                          void* arg);

typedef struct SymbLayerIter_TAG {
  unsigned int index;
  unsigned int mask;
  SymbLayerIterFilterFun filter;
  void* arg;
} SymbLayerIter;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_LAYER_FOREACH(self, iter, mask)             \
  for (SymbLayer_gen_iter(self, &iter, mask);            \
       !SymbLayer_iter_is_end(self, &iter);              \
       SymbLayer_iter_next(self, &iter))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_LAYER_FOREACH_FILTER(self, iter, mask, filter, arg)        \
  for (SymbLayer_gen_iter(self, &iter, mask),                           \
         SymbLayer_iter_set_filter(self, &iter, filter, arg);           \
       !SymbLayer_iter_is_end(self, &iter);                             \
       SymbLayer_iter_next(self, &iter))


/* ---------------------------------------------------------------------- */
/*     Public methods                                                     */
/* ---------------------------------------------------------------------- */


/* Iterators */

/*!
  \methodof SymbLayer
  \brief Generates an interator over the Symbol Cache symbols

  Generates an interator over the Symbol Cache symbols.
   The iterator will ignore all symbols that do not
   satisfy the mask
*/
void SymbLayer_gen_iter(const SymbLayer_ptr self,
                               SymbLayerIter* iter,
                               unsigned int mask);

/*!
  \methodof SymbLayer
  \brief Moves the iterator over the next symbol

  Moves the iterator over the next symbol,
   regarding to the mask given when built using
   SymbCache_gen_iter
*/
void SymbLayer_iter_next(const SymbLayer_ptr self,
                                SymbLayerIter* iter);

/*!
  \methodof SymbLayer
  \brief Checks if the iterator is at it's end

  Checks if the iterator is at it's end
*/
boolean SymbLayer_iter_is_end(const SymbLayer_ptr self,
                                     const SymbLayerIter* iter);

/*!
  \methodof SymbLayer
  \brief Get the symbol pointed by the iterator

  Get the symbol pointed by the iterator
*/
node_ptr SymbLayer_iter_get_symbol(const SymbLayer_ptr self,
                                          const SymbLayerIter* iter);

/*!
  \brief Sets the filter for an interator over the
   Symbol Layer symbols

  Sets the filter for an interator over the
   Symbol Layer symbols. The iterator will be moved
   in order to point to a symbol that satisfies
   both the mask and the filter
*/
void
SymbLayer_iter_set_filter(const SymbLayer_ptr layer,
                          SymbLayerIter* iter,
                          SymbLayerIterFilterFun fun,
                          void* arg);

/*!
  \methodof SymbLayer
  \brief Boolean Variables filter

  SymbLayer built-in filter: Returns true iff the symbol
   is a boolean variable
*/
boolean
SymbLayer_iter_filter_bool_vars(const SymbLayer_ptr self,
                                const node_ptr sym,
                                void* arg);

/*!
  \methodof SymbLayer
  \brief Generates a set starting from the given iterator.

  Generates a set starting from the given iterator.
   The iter will not be consumed (since passed as
   copy)
*/
Set_t
SymbLayer_iter_to_set(const SymbLayer_ptr self, SymbLayerIter iter);

/*!
  \methodof SymbLayer
  \brief Generates a list starting from the given iterator.

  Generates a list starting from the given iterator.
   The iter will not be consumed (since passed as
   copy)
*/
NodeList_ptr
SymbLayer_iter_to_list(const SymbLayer_ptr self, SymbLayerIter iter);

/*!
  \methodof SymbLayer
  \brief Counts the elements of the iterator.

  Counts the elements of the iterator.
   The iter will not be consumed (since passed as
   copy)
*/
unsigned int
SymbLayer_iter_count(const SymbLayer_ptr self, SymbLayerIter iter);

/*!
  \methodof SymbLayer
  \brief Returns the name self had been registered with.

  Returned string must not be freed, it belongs to self
*/
const char*
SymbLayer_get_name(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Call this method to know if a new constant can be
   declared within this layer.

  Since more than one layer can declare the same constants,
   this method might return true even if another layer already contain the
   given constant. If the constant had already been declared within self,
   false is returned. 
*/
boolean
SymbLayer_can_declare_constant(const SymbLayer_ptr self,
                               const node_ptr name);

/*!
  \methodof SymbLayer
  \brief Insert a new constant

  A new constant is created

  \sa SymbLayer_can_declare_constant
*/
void
SymbLayer_declare_constant(SymbLayer_ptr self, node_ptr name);

/*!
  \methodof SymbLayer
  \brief Call this method to know if a new variable can be
   declared within this layer.

  Returns true if the given symbol does not exist
   within the symbol table which self belongs to. Returns
   false if the symbol was already declared. 
*/
boolean
SymbLayer_can_declare_var(const SymbLayer_ptr self,
                          const node_ptr name);

/*!
  \methodof SymbLayer
  \brief Insert a new input variable

  A new input variable is created of a given type.
   The variable type can be created with SymbType_create or returned by
   funtions SymbTablePkg_..._type.
   The layer is responsible for destroying the variable's type.
*/
void
SymbLayer_declare_input_var(SymbLayer_ptr self, node_ptr var,
                            SymbType_ptr type);

/*!
  \methodof SymbLayer
  \brief Insert a new state variable

  A new state variable is created of a given type.
   The variable type can be created with SymbType_create or returned by
   funtions SymbTablePkg_..._type.
   The layer is responsible for destroying the variable's type.
*/
void
SymbLayer_declare_state_var(SymbLayer_ptr self, node_ptr var,
                            SymbType_ptr type);

/*!
  \methodof SymbLayer
  \brief Insert a new frozen variable

  A new frozen variable is created of a given type.
   The variable type can be created with SymbType_create or returned by
   funtions SymbTablePkg_..._type.
   The layer is responsible for destroying the variable's type.
*/
void
SymbLayer_declare_frozen_var(SymbLayer_ptr self, node_ptr var,
                            SymbType_ptr type);

/*!
  \methodof SymbLayer
  \brief Redeclare a state variable as a frozen variable

  A variable is frozen if it is known then the var's value
   cannot change in transitions.
   'var' must be a state variable already defined and not redeclared as frozen.
*/
void
SymbLayer_redeclare_state_as_frozen_var(SymbLayer_ptr self, node_ptr var);

/*!
  \methodof SymbLayer
  \brief Call this method to know if a new DEFINE can be
   declared within this layer.

  Returns true if the given symbol does not exist within
   the symbol table which self belongs to. Returns false if the symbol
   was already declared. 
*/
boolean
SymbLayer_can_declare_define(const SymbLayer_ptr self,
                             const node_ptr name);

/*!
  \methodof SymbLayer
  \brief Call this method to know if a new NFunction can be
   declared within this layer.

  Returns true if the given symbol does not exist
   within the symbol table which self belongs to. Returns
   false if the symbol was already declared. 
*/
boolean
SymbLayer_can_declare_function(const SymbLayer_ptr self,
                                 const node_ptr name);

/*!
  \methodof SymbLayer
  \brief Call this method to know if a new parameter can be
   declared within this layer.

  Returns true if the given symbol does not exist within
   the symbol table which self belongs to. Returns false if the symbol
   was already declared. 
*/
boolean
SymbLayer_can_declare_parameter(const SymbLayer_ptr self,
                                const node_ptr name);

/*!
  \methodof SymbLayer
  \brief Call this method to know if a new define array can be
   declared within this layer.

  Returns true if the given symbol does not exist within
   the symbol table which self belongs to. Returns false if the symbol
   was already declared. 
*/
boolean
SymbLayer_can_declare_array_define(const SymbLayer_ptr self,
                             const node_ptr name);

/*!
  \methodof SymbLayer
  \brief Call this method to know if a new variable_array can be
   declared within this layer.

  Returns true if the given symbol does not exist within
   the symbol table which self belongs to. Returns false if the symbol
   was already declared. 
*/
boolean
SymbLayer_can_declare_variable_array(const SymbLayer_ptr self,
                                  const node_ptr name);

/*!
  \methodof SymbLayer
  \brief Insert a new DEFINE

  A new DEFINE of a given value is created. name must be
   contestualized, context is provided as a separated information
*/
void
SymbLayer_declare_define(SymbLayer_ptr self, node_ptr name,
                         node_ptr ctx, node_ptr definition);

/*!
  \methodof SymbLayer
  \brief Insert a new NFunction

  A new NFunction is declared within the layer.
   Name must be contestualized, context is provided
   as a separated information

  \sa SymbLayer_can_declare_function
*/
void
SymbLayer_declare_function(SymbLayer_ptr self, node_ptr name,
                           node_ptr ctx, SymbType_ptr type);

/*!
  \methodof SymbLayer
  \brief Insert a new formal parameters

  A new parameter of a given value is created. name must be
   contestualized, context is provided as a separated information
*/
void
SymbLayer_declare_parameter(SymbLayer_ptr self, node_ptr formal,
                            node_ptr ctx, node_ptr actual);

/*!
  \methodof SymbLayer
  \brief Insert a new array define array

  A new define array of a given value is created. name must be
   contestualized, context is provided as a separated information
*/
void
SymbLayer_declare_array_define(SymbLayer_ptr self, node_ptr name,
                                node_ptr ctx, node_ptr definition);

/*!
  \methodof SymbLayer
  \brief Insert a new symbol-type association, i.e. array var 

  The specified name will be associated to the give array type
   in the symbols collection
*/
void
SymbLayer_declare_variable_array(SymbLayer_ptr self, node_ptr var,
                              SymbType_ptr type);

/*!
  \methodof SymbLayer
  \brief Removes a symbol previously delcared

  Symbol must be a var, a variable array, a define or a function

  This method can be called only if self is not
   currently commited to any encoding. It is not allowed to remove
   symbols from layers that are committed to any encoder. This is
   required as caches and other mechanisms may fail to work
   correctly otherwise.
*/
void SymbLayer_remove_symbol(SymbLayer_ptr self, node_ptr name);

/*!
  \methodof SymbLayer
  \brief Removes a variable previously delcared

  This method can be called only if self is not
   currently commited to any encoding. It is not allowed to remove
   symbols from layers that are committed to any encoder. This is
   required as caches and other mechanisms may fail to work
   correctly otherwise.
*/
void SymbLayer_remove_var(SymbLayer_ptr self, node_ptr name);

/*!
  \methodof SymbLayer
  \brief Removes a previously declared DEFINE

  This method can be called only if self is not
   currently commited to any encoding. It is not allowed to remove
   symbols from layers that are committed to any encoder. This is
   required as caches and other mechanisms may fail to work
   correctly otherwise.
*/
void SymbLayer_remove_define(SymbLayer_ptr self, node_ptr name);

/*!
  \methodof SymbLayer
  \brief Removes a previously declared NFunction

  This method can be called only if self is not
   currently commited to any encoding. It is not allowed to remove
   symbols from layers that are committed to any encoder. This is
   required as caches and other mechanisms may fail to work
   correctly otherwise.
*/
void SymbLayer_remove_function(SymbLayer_ptr self, node_ptr name);

/*!
  \methodof SymbLayer
  \brief Removes a previously declared array

  This method can be called only if self is not
   currently commited to any encoding. It is not allowed to remove
   symbols from layers that are committed to any encoder. This is
   required as caches and other mechanisms may fail to work
   correctly otherwise.
*/
void SymbLayer_remove_variable_array(SymbLayer_ptr self, node_ptr name);


/*!
  \methodof SymbLayer
  \brief Returns the number of declared symbols

  
*/
int SymbLayer_get_symbols_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of declared contants

  
*/
int SymbLayer_get_constants_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of declared state variables.

  
*/
int SymbLayer_get_state_vars_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of declared boolean state variables

  
*/
int SymbLayer_get_bool_state_vars_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of declared frozen variables.

  
*/
int SymbLayer_get_frozen_vars_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of declared boolean frozen variables

  
*/
int SymbLayer_get_bool_frozen_vars_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of declared input variables

  
*/
int SymbLayer_get_input_vars_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of declared variables

  
*/
int SymbLayer_get_vars_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of declared boolean input variables

  
*/
int SymbLayer_get_bool_input_vars_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of DEFINEs.

  
*/
int SymbLayer_get_defines_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of NFunctions.

  
*/
int SymbLayer_get_functions_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of parameters.

  
*/
int SymbLayer_get_parameters_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of define arrays.

  
*/
int SymbLayer_get_array_defines_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Returns the number of Symbol Types.

  
*/
int SymbLayer_get_variable_arrays_num(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Compares the insertion policies of self and other, and
   returns true if self must be inserted *before* other

  Compares the insertion policies of self and other, and
   returns true if self must be inserted *before* other.
*/
boolean
SymbLayer_must_insert_before(const SymbLayer_ptr self,
                             const SymbLayer_ptr other);

/*!
  \methodof SymbLayer
  \brief Returns true if the variable is defined in the layer.

  Returns true if the variable is defined in the layer.
*/
boolean
SymbLayer_is_variable_in_layer(SymbLayer_ptr self,
                               node_ptr name);

/*!
  \methodof SymbLayer
  \brief Returns true if the symbol is defined in the layer.

  Returns true if the symbol is defined in the layer.
*/
boolean
SymbLayer_is_symbol_in_layer(SymbLayer_ptr self, node_ptr name);

/*!
  \methodof SymbLayer
  \brief Returns the policy that must be adopted to stack this
   layer into a layers stack, within a SymbTable instance

  This method is thought to be used exclusively by class
   SymbTable
*/
LayerInsertPolicy
SymbLayer_get_insert_policy(const SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief SymbLayer environment instance getter

  SymbLayer environment instance getter
*/
NuSMVEnv_ptr SymbLayer_get_environment(const SymbLayer_ptr self);

#endif /* __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_LAYER_H__ */
