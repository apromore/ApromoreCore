/* ---------------------------------------------------------------------------


  This file is part of the ``hrc'' package of NuSMV version 2.
  Copyright (C) 2009 by FBK-irst.

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
  \author Marco Roveri
  \brief Public interface of class 'HrcNode'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_HRC_HRC_NODE_H__
#define __NUSMV_CORE_HRC_HRC_NODE_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include <stdlib.h>
#include <stdio.h>
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/Slist.h"
#include "nusmv/core/utils/Olist.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"

/*!
  \struct HrcNode
  \brief Definition of the public accessor for class HrcNode

  
*/
typedef struct HrcNode_TAG*  HrcNode_ptr;

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief To cast and check instances of class HrcNode

  These macros must be used respectively to cast and to check
  instances of class HrcNode
*/
#define HRC_NODE(self) \
         ((HrcNode_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define HRC_NODE_CHECK_INSTANCE(self) \
         (nusmv_assert(HRC_NODE(self) != HRC_NODE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof HrcNode
  \brief The HrcNode class constructor

  The HrcNode class constructor

  \sa HrcNode_destroy
*/
HrcNode_ptr HrcNode_create(const NuSMVEnv_ptr env);

/*!
  \methodof HrcNode
  \brief Creates a new node that is a copy of self without
  considering children.

  Creates a new node that is a copy of self without
  considering children.

  The copy does not even link a node with its parent.

  \sa HrcNode_copyRename
*/
HrcNode_ptr HrcNode_copy(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Creates a new node that is a copy of self without
  considering children. The name of the module of the new node is
  set as new_module_name.

  Creates a new node that is a copy of self without
  considering childre. The name of the module of the new node is
  set as new_module_name.

  The copy does not even link a node with its parent.

  \sa HrcNode_copy
*/
HrcNode_ptr HrcNode_copy_rename(const HrcNode_ptr self,
                                      node_ptr new_module_name);

/*!
  \methodof HrcNode
  \brief Creates a copy of self and recursively of all
  its children.

  Creates a copy of self and recursively of all
  its children. 
*/
HrcNode_ptr HrcNode_recursive_copy(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Resets all fields of the given node

  Resets all fields of the given node.
                      This is needed for safely recycle a node instance.
                      For example, if a parsing error occurs.
                      Children are destroyed.
                      
*/
void HrcNode_cleanup(HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief The HrcNode class destructor

  The HrcNode class destructor

  \se The node is freed

  \sa HrcNode_create
*/
void HrcNode_destroy(HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief The HrcNode class destructor. It recurses on the childs.

  The HrcNode class destructor. It recurses on the childs.

  \se The whole hierarchy tree is freed

  \sa HrcNode_create, HrcNode_destroy
*/
void HrcNode_destroy_recur(HrcNode_ptr self);


/* Getters and Setters ********************************************************/

/*!
  \methodof HrcNode
  \brief Sets the symbol table inside the node.

  Sets the symbol table inside the node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_set_symbol_table(HrcNode_ptr self, SymbTable_ptr st);

/*!
  \methodof HrcNode
  \brief Gets the symbol table.

  Gets the symbol table.

  \se None

  \sa optional
*/
SymbTable_ptr HrcNode_get_symbol_table(HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Sets the MOD_TYPE lineno of the node.

  Sets the MOD_TYPE lineno of the node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_set_lineno(HrcNode_ptr self, int lineno);

/*!
  \methodof HrcNode
  \brief Gets the MOD_TYPE lineno of the node.

  Gets the MOD_TYPE lineno of the node.

  \se None

  \sa optional
*/
int HrcNode_get_lineno(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Sets the MOD_TYPE name of the node.

  Sets the MOD_TYPE name of the node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_set_name(HrcNode_ptr self, node_ptr name);
/* Get the normalized name of the current node */

/*!
  \methodof HrcNode
  \brief Gets the MOD_TYPE name of the node.

  Gets the MOD_TYPE name of the node. WARNING: the returned
  name is 'normalized' and can be used as hash value.

  \se None

  \sa optional
*/
node_ptr HrcNode_get_name(const HrcNode_ptr self);
/* Get the name of the current node, NOT normalized and as passed to SetName */

/*!
  \methodof HrcNode
  \brief Gets the MOD_TYPE name of the node.

  Gets the MOD_TYPE name of the node. WARNING: the
  returned name is the name passed to SetName, and it is not
  'normalized' like in GetName. This can be used to obtain the node
  as produced by the parser.

  \se None

  \sa optional
*/
node_ptr HrcNode_get_crude_name(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Sets the instance name of the node.

  Sets the instance name of the node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_set_instance_name(HrcNode_ptr self, node_ptr name);

/*!
  \methodof HrcNode
  \brief Gets the instance name of the node.

  Gets the instance name of the node.

  \se None

  \sa optional
*/
node_ptr HrcNode_get_instance_name(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Gets the flattened instance name of the node.

  Gets the flattened instance name of the node.

  The hierarchy is visited upward from self until main node is
  found. The flattened and normalized instance node is built and
  returned.

  The flattened instance name is the name obtained considering all the
  anchestors instance of the current node.

  The result of this operation could also be memoized to improve
  performances (this would avoid to recompute the same path twice).
*/
node_ptr HrcNode_get_flattened_instance_name(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Sets the parent node of the node.

  Sets the parent node of the node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_set_parent(const HrcNode_ptr self, HrcNode_ptr father);
/* NULL if it is the root */

/*!
  \methodof HrcNode
  \brief Get the parent node of the node.

  Get the parent node of the node. HRC_NODE(NULL)
  is returned if no father available.

  \se None

  \sa optional
*/
HrcNode_ptr HrcNode_get_parent(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Get the parent node of the node.

  Get the parent node of the node. HRC_NODE(NULL)
  is returned if no father available.

  \se None

  \sa optional
*/
HrcNode_ptr HrcNode_get_root(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Replaces the formal parameters of the current node.

  Relaces the formal parameters of the current node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_formal_parameters(HrcNode_ptr self, Olist_ptr par);

/*!
  \methodof HrcNode
  \brief Gets the formal parameters of the current node.

  Gets the formal parameters of the current
  node. The result is a list of pairs (name . type), where type
  specifies the type of the parameter if know, otherwise it is Nil.

  \se None

  \sa optional
*/
Oiter HrcNode_get_formal_parameters_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a formal parameter to the the current node.

  Adds a formal parameter to the current node. It
  should be a pair (name . type), where type specifies the type of the
  parameter if known, otherwise it is Nil.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_formal_parameter(HrcNode_ptr self, node_ptr par);

/*!
  \methodof HrcNode
  \brief Returns the formal param (pair name, type)

  Returns the parameter par_name of type
  par_type. The search is performed only inside self node, thus the
  function does not recur over hierarchy.
  A cons between the found name and its type is returned upon
  success. Returned cons must be freed by the caller.
  Nil is returned if parameter is not found.

  par_name is the name of the formal parameter.

  Note that given parameter may have type set to Nil.
  
*/
node_ptr HrcNode_find_formal_parameter(const HrcNode_ptr self,
                                              node_ptr par_name);

/*!
  \methodof HrcNode
  \brief Returns the number of formal parameters

  \todo Missing description
*/
int HrcNode_get_formal_parameters_length(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Replaces the actual parameters of the current node.

  Replaces the actual parameters of the current node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_actual_parameters(HrcNode_ptr self, Olist_ptr par);

/*!
  \methodof HrcNode
  \brief Gets the actual parameters of the current node.

  Gets the actual parameters of the current
  node. The result is a list of pairs (name . expr), where expr specifies the
  expression the current current formal parameter node has been
  instatiated to.

  \se None

  \sa optional
*/
Oiter HrcNode_get_actual_parameters_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an actual parameter to the current node.

  Adds an actual parameter to the current node. It
  should be a pair (name . expr), where expr specifies the expression the
  parameter has been instantiated to.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_actual_parameter(HrcNode_ptr self, node_ptr par);

/*!
  \methodof HrcNode
  \brief Returns the number of actual parameters

  \todo Missing description
*/
int HrcNode_get_actual_parameters_length(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Replaces the local state variables of the current node.

  Replaces the local state variables of the current node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_state_variables(HrcNode_ptr self, Olist_ptr vars);

/*!
  \methodof HrcNode
  \brief Gets the local state variables of the current node.

  Gets the local state variables of the current
  node. The result is a list of pairs (name . type) where type is the
  type of the corresponding variable.

  \se None

  \sa optional
*/
Oiter HrcNode_get_state_variables_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a state variable to the current node.

  Adds a state variable to the current node. The
  var should be a pairs (name . type) where type is the type of the
  corresponding variable.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_state_variable(HrcNode_ptr self, node_ptr var);

/*!
  \methodof HrcNode
  \brief Replacess the local input variables of the current node.

  Replaces the local input variables of the current  node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_input_variables(HrcNode_ptr self, Olist_ptr vars);

/*!
  \methodof HrcNode
  \brief Gets the local input variables of the current node.

  Gets the local input variables of the current
  node. The result is a list of pairs (name . type) where type is the
  type of the corresponding variable.

  \se None

  \sa optional
*/
Oiter HrcNode_get_input_variables_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a input variable to the current node.

  Adds a input variable to the current node. The
  var should be a pairs (name . type) where type is the type of the
  corresponding variable.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_input_variable(HrcNode_ptr self, node_ptr var);

/*!
  \methodof HrcNode
  \brief Replaces the local frozen variables of the
  current node.

  Replaces the local frozen variables of the
  current node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_frozen_variables(HrcNode_ptr self, Olist_ptr vars);

/*!
  \methodof HrcNode
  \brief Gets the local frozen variables of the current node.

  Gets the local frozen variables of the current
  node. The result is a list of pairs (name . type) where type is the
  type of the corresponding variable.

  \se None

  \sa optional
*/
Oiter HrcNode_get_frozen_variables_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a frozen variable to the current node.

  Adds a frozen variable to the current node. The
  var should be a pairs (name . type) where type is the type of the
  corresponding variable.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_frozen_variable(HrcNode_ptr self, node_ptr var);

/*!
  \methodof HrcNode
  \brief Replaces the local frozen functions of the
  current node.

  Replaces the local frozen functions of the
  current node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_frozen_functions(HrcNode_ptr self, Olist_ptr functions);

/*!
  \methodof HrcNode
  \brief Gets the frozen functions of the current node.

  Gets the frozen functions of the current
  node. The result is a list of pairs (name . type), where type
  specifies the type of the function if know, otherwise it is Nil.

  \se None

  \sa optional
*/
Oiter HrcNode_get_frozen_functions_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a frozen function to the current node.

  Adds a frozen function to the current node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_frozen_function(HrcNode_ptr self, node_ptr fun);

/*!
  \methodof HrcNode
  \brief Replaces the local DEFINES of the current node.

  Replaces the local DEFINES for the current node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_defines(HrcNode_ptr self, Olist_ptr defs);

/*!
  \methodof HrcNode
  \brief Gets the local DEFINES of the current node.

  Gets the local DEFINES of the current node. The
  result is a list of pairs (name . expr) where expr is the body of
  the DEFINEd symbol.

  \se None

  \sa optional
*/
Oiter HrcNode_get_defines_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a DEFINE to the current node.

  Adds a define declaration to the current node. The
  define should be a pairs (name . expr) where expr is the body of the
  current DEFINE symbol.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_define(HrcNode_ptr self, node_ptr def);

/*!
  \methodof HrcNode
  \brief Returns the define (def_name, def_body)

  Returns the define def_name with body.
  The search is performed only inside self node, thus the
  function does not recur over hierarchy.

  A cons between the found name and define body is returned upon
  success. Returned cons must be freed by the caller.
  Nil is returned if define is not found.

  def_name is the name of the define.
  
*/
node_ptr HrcNode_find_define(const HrcNode_ptr self,
                                    node_ptr def_name);

/*!
  \methodof HrcNode
  \brief Replaces the local ARRAY DEFINES of the current
  node.

  Replaces the local ARRAY DEFINES for the current
  node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_array_defines(HrcNode_ptr self, Olist_ptr mdefs);

/*!
  \methodof HrcNode
  \brief Gets the local ARRAY DEFINES of the current node.

  Gets the local ARRAY DEFINES of the current node. The
  result is a list of pairs (name . expr) where expr is the body of
  the ARRAY DEFINEd symbol.

  \se None

  \sa optional
*/
Oiter HrcNode_get_array_defines_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an ARRAY DEFINE to the current node.

  Adds a ARRAY DEFINE declaration to the current
  node. The array define should be a pairs (name . expr) where expr is the
  body of the  current DEFINE symbol.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_array_define(HrcNode_ptr self, node_ptr mdef);

/* INIT */

/*!
  \methodof HrcNode
  \brief Replaces the INIT expressions for the current
  node.

  Replaces the INIT expressions for the current
  node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_init_exprs(HrcNode_ptr self, Olist_ptr exprs);

/*!
  \methodof HrcNode
  \brief Sets the INIT expressions for the current node.

  Sets the INIT expressions for the current
  node. It is a possibly empty list of implicitly conjoined expressions.

  \se None

  \sa optional
*/
Oiter HrcNode_get_init_exprs_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an INIT expression to the current node.

  Adds an INIT expression to the current node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_init_expr(HrcNode_ptr self, node_ptr expr);

/* init(x) := expr */

/*!
  \methodof HrcNode
  \brief Replaces the init(*) := expressions for the
  current node.

  Replaces the init(*) := expressions for the
  current node. 

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_init_assign_exprs(HrcNode_ptr self, Olist_ptr assigns);

/*!
  \methodof HrcNode
  \brief Gets the init(*) := expressions for the current node.

  Gets the init(*) := expressions for the current
  node. It is a list of implicitly conjoined assignments.

  \se Structure is updated

  \sa optional
*/
Oiter HrcNode_get_init_assign_exprs_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an init(*) := assignment to the current node.

  Adds an init(*) := assignment to the current
  node. An assignment is an ASSIGN node that has as left child init(*)
  and as right child the assignment.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_init_assign_expr(HrcNode_ptr self, node_ptr assign);

/* INVAR */

/*!
  \methodof HrcNode
  \brief Replaces the INVAR expressions for the current
  node.

  Replaces the INVAR expressions for the current
  node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_invar_exprs(HrcNode_ptr self, Olist_ptr exprs);

/*!
  \methodof HrcNode
  \brief Sets the INVAR expressions for the current node.

  Sets the INVAR expressions for the current
  node. It is a possibly empty list of implicitly conjoined expressions.

  \se None

  \sa optional
*/
Oiter HrcNode_get_invar_exprs_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an INVAR expression to the current node.

  Adds an INVAR expression to the current node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_invar_expr(HrcNode_ptr self, node_ptr expr);

/* x := expr */

/*!
  \methodof HrcNode
  \brief Replaces the (*) := expressions for the current node.

  Replaces the (*) := expressions for the current
  node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_invar_assign_exprs(HrcNode_ptr self, Olist_ptr assigns);

/*!
  \methodof HrcNode
  \brief Gets the (*) := expressions for the current node.

  Gets the (*) := expressions for the current
  node. It is a list of implicitly conjoined assignments.

  \se Structure is updated

  \sa optional
*/
Oiter HrcNode_get_invar_assign_exprs_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an (*) := assignment to the current node.

  Adds an (*) := assignment to the current
  node. An assignment is an ASSIGN node that has as left child (*)
  and as right child the assignment.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_invar_assign_expr(HrcNode_ptr self, node_ptr assign);


/* TRANS */

/*!
  \methodof HrcNode
  \brief Replaces the TRANS expressions for the current node.

  Replaces the TRANS expressions for the current
  node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_trans_exprs(HrcNode_ptr self, Olist_ptr exprs);

/*!
  \methodof HrcNode
  \brief Gets the TRANS expressions for the current node.

  Gets the TRANS expressions for the current
  node. It is a possibly empty list of implicitly conjoined expressions.

  \se None

  \sa optional
*/
Oiter HrcNode_get_trans_exprs_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an TRANS expression to the current node.

  Adds an TRANS expression to the current node.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_trans_expr(HrcNode_ptr self, node_ptr expr);

/* next(x) := epxr */

/*!
  \methodof HrcNode
  \brief Replaces the next(*) := expressions for the current node.

  Replaces the next(*) := expressions for the current
  node. It is a list of implicitly conjoined assignments.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_next_assign_exprs(HrcNode_ptr self, Olist_ptr assigns);

/*!
  \methodof HrcNode
  \brief Gets the next(*) := expressions for the current node.

  Gets the next(*) := expressions for the current
  node. It is a list of implicitly conjoined assignments.

  \se Structure is updated

  \sa optional
*/
Oiter HrcNode_get_next_assign_exprs_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an next(*) := assignment to the current node.

  Adds an next(*) := assignment to the current
  node. An assignment is an ASSIGN node that has as left child next(*)
  and as right child the assignment.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_next_assign_expr(HrcNode_ptr self, node_ptr assign);

/* JUSTICE */

/*!
  \methodof HrcNode
  \brief Replaces the list of JUSTICE constraints.

  Replaces the list of JUSTICE constraints.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_justice_exprs(HrcNode_ptr self, Olist_ptr justices);

/*!
  \methodof HrcNode
  \brief Gets the list of JUSTICE constraints.

  Gets the list of JUSTICE constraints.

  \se None

  \sa optional
*/
Oiter HrcNode_get_justice_exprs_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a JUSTICE constraint.

  Adds a JUSTICE constraint.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_justice_expr(HrcNode_ptr self, node_ptr justice);

/* COMPASSION */

/*!
  \methodof HrcNode
  \brief Replaces the list of COMPASSION constraints.

  Replaces the list of COMPASSION constraints.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_compassion_exprs(HrcNode_ptr self, Olist_ptr compassions);

/*!
  \methodof HrcNode
  \brief Gets the list of COMPASSION constraints.

  Gets the list of COMPASSION constraints.

  \se None

  \sa optional
*/
Oiter HrcNode_get_compassion_exprs_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a COMPASSION constraint.

  Adds a COMPASSION constraint.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_compassion_expr(HrcNode_ptr self, node_ptr compassion);

/* CONSTANTS */

/*!
  \methodof HrcNode
  \brief Replaces the list of CONSTANTS declarations.

  Replaces the list of CONSTANTS declarations.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_constants(HrcNode_ptr self, Olist_ptr constants);

/*!
  \methodof HrcNode
  \brief Gets the list of CONSTANTS declarations.

  Gets the list of CONSTANTS declarations.

  \se None

  \sa optional
*/
Oiter HrcNode_get_constants_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a CONSTANTS declaration to the list of
  constants.

  Adds a CONSTANTS declaration to the list of
  constants. All constants are kept in a unique list.

  constant is a list of constants.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_constants(HrcNode_ptr self, node_ptr constant);

/* CTLSPEC */

/*!
  \methodof HrcNode
  \brief Replaces the list of CTL properties.

  Replaces the list of CTL properties.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_ctl_properties(HrcNode_ptr self, Olist_ptr ctls);

/*!
  \methodof HrcNode
  \brief Gets the list of CTL properties.

  Gets the list of CTL properties.

  \se None

  \sa optional
*/
Oiter HrcNode_get_ctl_properties_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a CTL property.

  Adds a CTL property.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_ctl_property_expr(HrcNode_ptr self, node_ptr ctl);

/* LTLSPEC */

/*!
  \methodof HrcNode
  \brief Replaces the list of LTL properties.

  Replaces the list of LTL properties.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_ltl_properties(HrcNode_ptr self, Olist_ptr ltls);

/*!
  \methodof HrcNode
  \brief Gets the list of LTL properties.

  Gets the list of LTL properties.

  \se None

  \sa optional
*/
Oiter HrcNode_get_ltl_properties_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an LTL property.

  Adds an LTL property.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_ltl_property_expr(HrcNode_ptr self, node_ptr ltl);

/* PSLSPEC */

/*!
  \methodof HrcNode
  \brief Replaces the list of PSL properties.

  Replaces the list of PSL properties.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_psl_properties(HrcNode_ptr self, Olist_ptr psls);

/*!
  \methodof HrcNode
  \brief Gets the list of PSL properties.

  Gets the list of PSL properties.

  \se None

  \sa optional
*/
Oiter HrcNode_get_psl_properties_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an PSL property.

  Adds an PSL property.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_psl_property_expr(HrcNode_ptr self, node_ptr psl);

/* INVARSPEC */

/*!
  \methodof HrcNode
  \brief Replaces the list of INVARIANT properties.

  Replaces the list of INVARIANT properties.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_invar_properties(HrcNode_ptr self, Olist_ptr invars);

/*!
  \methodof HrcNode
  \brief Gets the list of INVARIANT properties.

  Gets the list of INVARIANT properties.

  \se None

  \sa optional
*/
Oiter HrcNode_get_invar_properties_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds an INVARIANT property.

  Adds an INVARIANT property.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_invar_property_expr(HrcNode_ptr self, node_ptr invar);

/* COMPUTESPEC */

/*!
  \methodof HrcNode
  \brief Replaces the list of COMPUTE properties.

  Replaces the list of COMPUTE properties.

  \se Structure is updated

  \sa optional
*/
void HrcNode_replace_compute_properties(HrcNode_ptr self, Olist_ptr computes);

/*!
  \methodof HrcNode
  \brief Gets the list of COMPUTE properties.

  Gets the list of COMPUTE properties.

  \se None

  \sa optional
*/
Oiter HrcNode_get_compute_properties_iter(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Adds a COMPUTE property.

  Adds a COMPUTE property.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_compute_property_expr(HrcNode_ptr self, node_ptr compute);

/*!
  \methodof HrcNode
  \brief Getter for the undef field

  Getter for the undef field

  \sa HrcNode_set_undef
*/
void HrcNode_set_undef(const HrcNode_ptr self, void* undef);

/*!
  \methodof HrcNode
  \brief Getter for the undef field

  Getter for the undef field

  \sa HrcNode_set_undef
*/
void* HrcNode_get_undef(const HrcNode_ptr self);

/* We assume the father of the child has been set by someone else */

/*!
  \methodof HrcNode
  \brief Sets the list of local childs for the current node.

  Sets the list of local childs for the current
  node. Assumption is that the child nodes have the current node as parent.

  \se Structure is updated

  \sa optional
*/
void HrcNode_set_child_hrc_nodes(HrcNode_ptr self, Slist_ptr list);

/*!
  \methodof HrcNode
  \brief Adds a child node to the current node.

  Add a child node to the current node. The parent
  of the child should have been set by someone else and it is expected
  to be the current one.

  \se Structure is updated

  \sa optional
*/
void HrcNode_add_child_hrc_node(HrcNode_ptr self, HrcNode_ptr node);

/*!
  \methodof HrcNode
  \brief \todo Missing synopsis

  \todo Missing description
*/
Slist_ptr HrcNode_get_child_hrc_nodes(const HrcNode_ptr self);


/* Queries  *******************************************************************/

/*!
  \methodof HrcNode
  \brief Checks if an assignment can be declared within the node

  Checks if an assignment can be declared within the node.
                      If an INIT/NEXT assign is already declared for a symbol,
                      then only a NEXT/INIT assign can be declared. If an INVAR
                      assignment is already declared, then no other assignments
                      can be declared
*/
boolean HrcNode_can_declare_assign(HrcNode_ptr self, node_ptr symbol,
                                          int assign_type);

/*!
  \methodof HrcNode
  \brief Checks wether current node is the root of the hierarchy.

  Checks wether current node is the root of the
  hierarchy. Returns true if it is the root, false otherwise.

  \se None

  \sa optional
*/
boolean HrcNode_is_root(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Checks wether current node is a leaf node in the hierarchy.

  Checks wether current node is a leaf node in the
  hierarchy. Returns true if it is a leaf node, false otherwise.

  \se None

  \sa optional
*/
boolean HrcNode_is_leaf(const HrcNode_ptr self);


/* Miscellaneous **************************************************************/
/* NULL if it does not exixts */

/*!
  \methodof HrcNode
  \brief Returns the pointer to a node instance of mod_type.

  Returns the pointer to the first instance of a
  module of type mod_type encountered in a depth first traversal of
  the hierarchy tree. Returns HRC_NODE(NULL) if no instance exists.

  \se None

  \sa optional
*/
HrcNode_ptr
HrcNode_find_hrc_node_by_mod_type(const HrcNode_ptr self,
                                  node_ptr mod_type);

/* The caller has to free returned list */

/*!
  \methodof HrcNode
  \brief Returns all the node instances of mod_type, from given
  root.

  Returns a list of pointers to all the instances of a
  module of type mod_type encountered in a depth first traversal of
  the hierarchy tree. Returned list must be freed by the caller.

  \se None

  \sa optional
*/
Olist_ptr
HrcNode_find_hrc_nodes_by_mod_type(const HrcNode_ptr self,
                                   const node_ptr mod_type);

/* NULL if it does not exits */

/*!
  \methodof HrcNode
  \brief Returns the pointer to the a module instance of a
  of given name.

  Returns the pointer to the a module instance of a
  of given name. Returns HRC_NODE(NULL) if no instance exists.

  \se None

  \sa optional
*/
HrcNode_ptr
HrcNode_find_hrc_node_by_instance_name(const HrcNode_ptr self,
                                       node_ptr name);

/* Find var_name of a given kind (state, frozen, input) in self node */

/*!
  \methodof HrcNode
  \brief Returns the variable var_name of type var_type.

  Returns the variable var_name of type
  var_type. The search is performed only inside self node, thus the
  function does not recur over hierarchy.
  A cons between the found variable and its type is returned upon
  success. Returned cons must be freed by the caller.
  Nil is returned if variable is not found.

  var_name is the name of the variable while type is the type of
  variable to search (VAR, FROZENVAR, IVAR).

  [MD] bad interface, the input is a symbol, the output is a pair symbol, type
  (returned as a CONS)
*/
node_ptr
HrcNode_find_var(HrcNode_ptr self, node_ptr var_name, int type);

/*!
  \methodof HrcNode
  \brief Returns the variable var_name searching in all types

  Like HrcNode_find_var, but it searches in all variable
  types VAR, FROZENVAR, INVAR
*/
node_ptr
HrcNode_find_var_all(HrcNode_ptr self, node_ptr var_name);

/*!
  \methodof HrcNode
  \brief Link two nodes

  Link two nodes
*/
void HrcNode_link_nodes(HrcNode_ptr self, HrcNode_ptr child);

/*!
  \methodof HrcNode
  \brief Unlink two nodes

  Unlink two nodes and "deinstantiate" child, since its
  instantiation makes sense only as a child of self
*/
void HrcNode_unlink_nodes(HrcNode_ptr self, HrcNode_ptr child);

/*!
  \methodof HrcNode
  \brief Remove a state variable from the current node

  The symbol table is not modified.
                      Linear in the number of state variables
*/
void HrcNode_remove_state_variable(HrcNode_ptr self,
                                          node_ptr var);

/*!
  \methodof HrcNode
  \brief Remove a frozen variable from the current node

  The symbol table is not modified.
                      Linear in the number of frozen variables
*/
void HrcNode_remove_frozen_variable(HrcNode_ptr self,
                                          node_ptr var);

/*!
  \methodof HrcNode
  \brief Remove a input variable from the current node

  The symbol table is not modified.
                      Linear in the number of input variables
*/
void HrcNode_remove_input_variable(HrcNode_ptr self,
                                          node_ptr var);

/*!
  \methodof HrcNode
  \brief Returns the number of all declared variables

  
*/
int HrcNode_get_vars_num(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Returns the number of all declared state variables

  
*/
int HrcNode_get_state_vars_num(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Returns the number of all declared frozen variables

  
*/
int HrcNode_get_frozen_vars_num(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Returns the number of all declared input variables

  
*/
int HrcNode_get_input_vars_num(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Returns the number of all declared defines

  
*/
int HrcNode_get_defines_num(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Returns the number of all declared array define

  
*/
int HrcNode_get_array_defines_num(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Returns the number of all parameters

  
*/
int HrcNode_get_parameters_num(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Returns the number of all declared constants

  Warning: this functions returns a number that can be different from the one
  returned by SymbTable_get_constants_num (constants within types could be not
  counted here)
*/
int HrcNode_get_constants_num(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Returns the number of all NFunctions

*/
int HrcNode_get_functions_num(const HrcNode_ptr self);

/*!
  \methodof HrcNode
  \brief Returns the number of all symbols

  
*/
int HrcNode_get_symbols_num(const HrcNode_ptr self);




/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_HRC_HRC_NODE_H__ */
