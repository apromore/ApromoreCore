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
  \brief The public interface of class SymbCache

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_CACHE_H__
#define __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_CACHE_H__

#include "nusmv/core/compile/symb_table/SymbType.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/compile/symb_table/NFunction.h"

/*!
  \struct SymbCache
  \brief The SymbCache type 

  An instance of class SymbCache is hold by each instance
  of SymbTable. This means that the life cycle of a SymbCache is never
  managed by the user. Furthermore, only tests on symbols are allowed
  to be performed by the user. All other features (e.g. creation of
  new symbols) are performed by SymbLayers and by SymbTable, by using a
  private interface
*/
typedef struct SymbCache_TAG*  SymbCache_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_CACHE(x) \
          ((SymbCache_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_CACHE_CHECK_INSTANCE(x) \
          ( nusmv_assert(SYMB_CACHE(x) != SYMB_CACHE(NULL)) )


/* ---------------------------------------------------------------------- */
/* Class SymbCache's public methods                                       */
/* ---------------------------------------------------------------------- */

/*!
  \methodof SymbCache
  \brief SymbCache environment instance getter

  SymbCache environment instance getter
*/
NuSMVEnv_ptr SymbCache_get_environment(const SymbCache_ptr self);

/*!
  \methodof SymbCache
  \brief Returns the type of a given variable

  "name" must be a variable
*/
SymbType_ptr
SymbCache_get_var_type(const SymbCache_ptr self, const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the body of the given DEFINE name

  
*/
node_ptr
SymbCache_get_define_body(const SymbCache_ptr self,
                          const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the actual param of the given formal parameter

  
*/
node_ptr
SymbCache_get_actual_parameter(const SymbCache_ptr self,
                               const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the body of the given define array name

  
*/
node_ptr
SymbCache_get_array_define_body(const SymbCache_ptr self,
                                 const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the type of array variable, i.e. of variable_array

  
*/
SymbType_ptr
SymbCache_get_variable_array_type(const SymbCache_ptr self,
                               const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the type of a function
*/
SymbType_ptr
SymbCache_get_function_type(const SymbCache_ptr self,
                            const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the flattenized body of the given DEFINE name

  
*/
node_ptr
SymbCache_get_define_flatten_body(const SymbCache_ptr self,
                                  const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the flattenized actual parameter of the given
  formal parameter

  
*/
node_ptr
SymbCache_get_flatten_actual_parameter(const SymbCache_ptr self,
                                       const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the context of the given DEFINE name

  
*/
node_ptr
SymbCache_get_define_context(const SymbCache_ptr self,
                             const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the context of the given NFunction

  
*/
node_ptr
SymbCache_get_function_context(const SymbCache_ptr self,
                                 const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the context of the actual parameter associated
  with the given formal parameter 

  
*/
node_ptr
SymbCache_get_actual_parameter_context(const SymbCache_ptr self,
                                       const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns the context of the given define array name

  
*/
node_ptr
SymbCache_get_array_define_context(const SymbCache_ptr self,
                                    const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the given symbol is a state variable.

  
*/
boolean
SymbCache_is_symbol_state_var(const SymbCache_ptr self,
                              const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the variable is frozen

  A variable is frozen if it is known that the var cannot
  change its value during transitions.
*/
boolean
SymbCache_is_symbol_frozen_var(const SymbCache_ptr self,
                                const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the variable is a frozen or a state
  variable

  
*/
boolean
SymbCache_is_symbol_state_frozen_var(const SymbCache_ptr self,
                                const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the given symbol is an input
  variable.

  
*/
boolean
SymbCache_is_symbol_input_var(const SymbCache_ptr self,
                              const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the given symbol is either a state, a frozen or
  an input variable.

  
*/
boolean
SymbCache_is_symbol_var(const SymbCache_ptr self, const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the given symbol is declared

  
*/
boolean
SymbCache_is_symbol_declared(const SymbCache_ptr self,
                             const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the given symbol is a declared
  DEFINE

  
*/
boolean
SymbCache_is_symbol_define(const SymbCache_ptr self,
                           const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the given symbol is a declared
                      NFunction

  
*/
boolean
SymbCache_is_symbol_function(const SymbCache_ptr self,
                               const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the given symbol is a declared formal
  parameter

  
*/
boolean
SymbCache_is_symbol_parameter(const SymbCache_ptr self,
                           const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the given symbol is a declared
  define array

  
*/
boolean
SymbCache_is_symbol_array_define(const SymbCache_ptr self,
                                  const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if the given symbol is a declared
  variable array

  
*/
boolean
SymbCache_is_symbol_variable_array(const SymbCache_ptr self,
                                   const node_ptr name);


/*!
  \methodof SymbCache
  \brief Returns true if the given symbol is a declared
  constant

  
*/
boolean
SymbCache_is_symbol_constant(const SymbCache_ptr self,
                             const node_ptr name);

/*!
  \methodof SymbCache
  \brief Returns true if var_list contains at least one input
  variable

  The given list of variables is traversed until an input
  variable is found
*/
boolean
SymbCache_list_contains_input_var(const SymbCache_ptr self,
                                  const NodeList_ptr var_list);

/*!
  \methodof SymbCache
  \brief Returns true if var_list contains at least one state
  or frozen variable

  The given list of variables is traversed until
  a state or frozen variable is found
*/
boolean
SymbCache_list_contains_state_frozen_var(const SymbCache_ptr self,
                                  const NodeList_ptr var_list);

/*!
  \methodof SymbCache
  \brief Returns true if the given symbols list contains
  one or more undeclared variable names

  Iterates through the elements in var_list
  checking each one to see if it is one undeclared variable.
*/
boolean
SymbCache_list_contains_undef_var(const SymbCache_ptr self,
                                  const NodeList_ptr var_list);



#endif /* __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_CACHE_H__ */
