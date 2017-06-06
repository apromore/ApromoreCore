/* ---------------------------------------------------------------------------


  This file is part of the ``compile'' package of NuSMV version 2.
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
  \author Andrei Tchaltsev
  \brief The class is used to store results of flattening a hierarchy.

  This class is virtually a set of fields to store
  various structures obtained after flattening parsed tree
  (i.e. module "main"). For example, there are list of INVARSPEC, a
  list of INIT expressions, a list of COMPASSION expressions, etc.

  Also this structure has a hash table to associate
  1. a variable on the left handside of an assignment to its right handside.
  2. a variable name to all constrains (INIT, TRANS, INVAR) which constain
     the given variable.

  See FlatHierarchy_create for more info on this class.

*/


#ifndef __NUSMV_CORE_COMPILE_FLAT_HIERARCHY_H__
#define __NUSMV_CORE_COMPILE_FLAT_HIERARCHY_H__

#include "nusmv/core/node/node.h"
#include "nusmv/core/set/set.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/utils/assoc.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct FlatHierarchy
  \brief The FlatHierarchy type

  The struct store info of flattened modules
*/
typedef struct FlatHierarchy* FlatHierarchy_ptr;

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FLAT_HIERARCHY(x) ((FlatHierarchy_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FLAT_HIERARCHY_CHECK_INSTANCE(x) \
         ( nusmv_assert(FLAT_HIERARCHY(x) != FLAT_HIERARCHY(NULL)) )


/* ---------------------------------------------------------------------- */
/* Public interface                                                       */
/* ---------------------------------------------------------------------- */
/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof FlatHierarchy
  \brief The constructor

  The class is used to store information
obtained after flattening module hierarchy.
These class stores:
the list of TRANS, INIT, INVAR, ASSIGN, SPEC, COMPUTE, LTLSPEC,
PSLSPEC, INVARSPEC, JUSTICE, COMPASSION,
a full list of variables declared in the hierarchy,
a hash table associating variables to their assignments and constrains.

NOTE: this structure is filled in by compileFlatten.c routines. There are
a few assumptions about the content stored in this class:
1. All expressions are stored in the same order as in the input
file (in module body or module instantiation order).
2. Assigns are stored as a list of pairs
{process instance name, assignments in it}.
3. Variable list contains only vars declared in this hierarchy.
4. The association var->assignments should be for assignments of
this hierarchy only.
Note that var may potentially be from another hierarchy. For
example, with Games of the GAME package an assignment in the body of
one hierarchy (one player) may have on the left hand side a variable from
another hierarchy (another player).
See FlatHierarchy_lookup_assign, FlatHierarchy_insert_assign
5. The association var->constrains (init, trans, invar) should be
for constrains of this hierarchy only. Similar to
var->assignment association (see above) a variable may
potentially be from another hierarchy.
See FlatHierarchy_lookup_constrains, FlatHierarchy_add_constrains

*/
FlatHierarchy_ptr FlatHierarchy_create(SymbTable_ptr st);

/*!
  \methodof FlatHierarchy
  \brief Class FlatHierarcy destructorUtility constructor

  Use this constructor to set the main hierarchy members

  \se FlatHierarchy_create
*/
FlatHierarchy_ptr
FlatHierarchy_create_from_members(SymbTable_ptr st,
                                  node_ptr init,
                                  node_ptr invar,
                                  node_ptr trans,
                                  node_ptr input,
                                  node_ptr justice,
                                  node_ptr compassion);

/*!
  \methodof FlatHierarchy
  \brief Class FlatHierarcy destructor

  The destoructor does not destroy the nodes
given to it with access functions.

  \se FlatHierarchy_create
*/
void  FlatHierarchy_destroy(FlatHierarchy_ptr self);

/*!
  \methodof FlatHierarchy
  \brief Returns a newly created instance that is a copy of self

*/
FlatHierarchy_ptr
FlatHierarchy_copy(const FlatHierarchy_ptr self);

/*!
  \methodof FlatHierarchy
  \brief Merges the contents of other into self (leaves other
intact)

  \se flat_hierarchy_mergeinto,
SexpFsm_apply_synchronous_product
*/
void FlatHierarchy_mergeinto(FlatHierarchy_ptr self,
                                    const FlatHierarchy_ptr other);



/* Getters and Setters ********************************************************/

/*!
  \methodof FlatHierarchy
  \brief Returns the Flat Hierarchy SymbolTable's environment

  Returns the Flat Hierarchy SymbolTable's environment
*/
NuSMVEnv_ptr
FlatHierarchy_get_environment(const FlatHierarchy_ptr self);

/*!
  \methodof FlatHierarchy
  \brief Returns the associated symbol table

  
*/
SymbTable_ptr
FlatHierarchy_get_symb_table(const FlatHierarchy_ptr self);

/*!
  \methodof FlatHierarchy
  \brief Returns the associated symbol table

  
*/
void
FlatHierarchy_set_symb_table(const FlatHierarchy_ptr self,
                             SymbTable_ptr symb_table);

/* Access function to the class's fields : constrains and specifications ******/

/*!
  \brief A set of functions accessing the fields of the class

  
*/
node_ptr FlatHierarchy_get_init(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_set_init(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_add_init(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_invar(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_set_invar(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_add_invar(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_trans(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_set_trans(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_add_trans(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_input(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_set_input(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_assign(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
FlatHierarchy_set_assign(FlatHierarchy_ptr cmp, node_ptr n);


/* properties *****************************************************************/

/*!
  \brief 

  It is a cons list of constraints
*/
node_ptr FlatHierarchy_get_justice(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
FlatHierarchy_set_justice(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_compassion(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_set_compassion(FlatHierarchy_ptr cmp,
                                         node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean FlatHierarchy_add_property_name(FlatHierarchy_ptr cmp,
                                            node_ptr name);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_spec(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_set_spec(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_ltlspec(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  Input is a cons list, with elements LTLSPEC nodes
*/
void
FlatHierarchy_set_ltlspec(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_invarspec(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_set_invarspec(FlatHierarchy_ptr cmp,
                                        node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_pslspec(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
FlatHierarchy_set_pslspec(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_compute(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
FlatHierarchy_set_compute(FlatHierarchy_ptr cmp, node_ptr n);


/* -- access functions to the variable sets -- ********************************/

/*!
  \brief Returns the set of variables declared in the given hierarchy

  Do not destroy or change returned set

  \sa FlatHierarchy_add_var
*/
Set_t FlatHierarchy_get_vars(FlatHierarchy_ptr cmp);

/*!
  \brief Add a variable name to the list of variables
declared in the given hierarchy

  

  \sa FlatHierarchy_get_vars
*/
void FlatHierarchy_add_var(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \methodof FlatHierarchy
  \brief Remove a variable name to the list of variables
                    declared in the given hierarchy

  

  \sa FlatHierarchy_get_vars
*/
void FlatHierarchy_remove_var(FlatHierarchy_ptr self, node_ptr n);

/*!
  \methodof FlatHierarchy
  \brief Returns an ordered list of variables

  Starting from hierarchy assignments, creates a DAG
                    and returns the topological sort of it. The set of nodes
                    contained in this dag is the union of the dependencies
                    of all assings expressions of the hierarchy.
                    If not NULL, outbound_edges will contain a map between vars
                    and their respective outgoing edges to other variables
*/
NodeList_ptr
FlatHierarchy_get_ordered_vars(const FlatHierarchy_ptr self,
                               hash_ptr* outbound_edges);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_preds(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_add_pred(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_set_pred(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_mirrors(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_add_mirror(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_set_mirror(FlatHierarchy_ptr cmp, node_ptr n);


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr FlatHierarchy_get_property_patterns(FlatHierarchy_ptr cmp);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_add_property_pattern(FlatHierarchy_ptr cmp, node_ptr n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_set_property_patterns(FlatHierarchy_ptr cmp, node_ptr n);


/* Access function to the hash.
   Given a var name these functions can return right handside of
   init-assignment, invar-assignmnent and next-assign which have the given
   variable on the left handside.
   The functions also can return the list of INIT, INVAR and TRANS expressions
   which contain the given variable.
*/

/*!
  \methodof FlatHierarchy
  \brief Returns the right handside of an assignment which has
"name" as the left handside.

  The name can be a usual variable name, init(variable
name) or next(variable name). The name should be fully resolved (and
created with find_node).

NB: All returned assignments are supposed to be declared in the
given hierarchy.

  \sa FlatHierarchy_insert_assign
*/
node_ptr FlatHierarchy_lookup_assign(FlatHierarchy_ptr self,
                                            node_ptr name);

/*!
  \methodof FlatHierarchy
  \brief Insert the right handside of an assignment which
has "name" as the left handside

  The name can be a usual variable name, init(var-name) or
next(var-name).
The variable name should be fully resolved (and created  with find_node).

NB: All given assignments should have been declared in the given hierarchy.

  \sa FlatHierarchy_lookup_assign
*/
void FlatHierarchy_insert_assign(FlatHierarchy_ptr self,
                                        node_ptr name,
                                        node_ptr assign);

/*!
  \methodof FlatHierarchy
  \brief Returns  a list of constrains which contain
a variable of the given name.

  
If the parameter "name" is a usual variable name then
the INVAR expressions are returned.
If the parameter "name" has a form init(var-name) then
the INIT expressions are returned.
If the parameter "name" has a form next(var-name) then
the TRANS expressions are returned.

The name should be fully resolved (and created with find_node).

NB: All returned expressions are supposed to be declared in the
given hierarchy.

  \sa FlatHierarchy_add_constrains
*/
node_ptr FlatHierarchy_lookup_constrains(FlatHierarchy_ptr self,
                                                node_ptr name);

/*!
  \methodof FlatHierarchy
  \brief Adds the given expressions to the list
of constrains associated to the given variable

  
The parameter "name" can be a usual variable name then
an expression is expected to be INVAR body.
The parameter "name" can have a form init(var-name) then
an expression is expected to be INIT body.
The parameter "name" can have a form next(var-name) then
an expression is expected to be TRANS body.

In any case the variable name should be fully resolved (and created
with find_node).

NB: All given expressions should have been declared in the given hierarchy.

  \sa FlatHierarchy_lookup_constrains
*/
void FlatHierarchy_add_constrains(FlatHierarchy_ptr self,
                                         node_ptr name,
                                         node_ptr expr);

/*!
  \methodof FlatHierarchy
  \brief Retrieves the list of constrains associated to constants

  Retrieves the list of constrains associated to constants
                    for the given hierarchy section.
                    Type must be one of "INIT, INVAR or TRANS"

  \sa FlatHierarchy_add_constant_constrains
*/
node_ptr
FlatHierarchy_lookup_constant_constrains(FlatHierarchy_ptr self,
                                         int type);

/*!
  \methodof FlatHierarchy
  \brief Adds the given expressions to the list
                    of constrains associated to constants

  Adds the given expressions to the list
                    of constrains associated to constants.
                    Type must be one of "INIT, INVAR or TRANS"

  \sa FlatHierarchy_lookup_constant_constrains
*/
void FlatHierarchy_add_constant_constrains(FlatHierarchy_ptr self,
                                                  node_ptr expr,
                                                  int type);

/*!
  \methodof FlatHierarchy
  \brief Creates association between variables and all the expressions
the variables occur in.

  For every variable var-name in the given expressions the
function creates association between:
1. var-name and and the INVAR expression list the variable occur.
2. init(var-name) and INIT expression list
3. next(var-name) and TRANS expression list.
The result is remembered by flatHierarchy.

The function compileFlattenProcess works similarly but with assignments.

  \sa compileFlattenProcess
*/
void
FlatHierarchy_calculate_vars_constrains(FlatHierarchy_ptr self);

/*!
  \methodof FlatHierarchy
  \brief This function returns the hash table storing the
association between a variable name and expressions the variable is used in.

  self retains ownership of returned value.

Note: you should know what you are doing when performing modifications
on the table.

*/
hash_ptr
FlatHierarchy_get_var_expr_associations(FlatHierarchy_ptr self);

/*!
  \methodof FlatHierarchy
  \brief This function sets the hash table storing the
association between a variable name and expressions the variable is used in
to h.

  self obtains ownership of h.

Note: you should know what you are doing when using this function. You are
fully responsible that the contents of h make sense - no checks are performed
whatsoever.

*/
void
FlatHierarchy_set_var_expr_associations(FlatHierarchy_ptr self,
                                        hash_ptr h);

/*!
  \methodof FlatHierarchy
  \brief This function cleans the association between a variable name
and expressions the variable is used in.

  
Practically, this function cleans association created by
FlatHierarchy_insert_assign and FlatHierarchy_add_constrains such that
functions FlatHierarchy_lookup_assign and FlatHierarchy_lookup_constrains
will return Nil for any var name.

Note: you should know what you are doing when invoke this function since
it makes COI and various checks of FSM incorrect.

*/
void
FlatHierarchy_clear_var_expr_associations(FlatHierarchy_ptr self);

/*!
  \methodof FlatHierarchy
  \brief This function returns the hash table storing the
                    association between the hierarchy section and
                    constants expressions

  self retains ownership of returned value.
                    Note: you should know what you are doing when
                    performing modifications on the table.  
*/
hash_ptr
FlatHierarchy_get_constants_associations(FlatHierarchy_ptr self);

/*!
  \methodof FlatHierarchy
  \brief This function sets the hash table storing the
                    association between the hierarchy section and
                    constants expressions

  self retains ownership of h.
                    Note: you should know what you are doing when
                    performing modifications on the table.  
*/
void
FlatHierarchy_set_constants_associations(FlatHierarchy_ptr self,
                                         hash_ptr h);

/*!
  \methodof FlatHierarchy
  \brief Clears the association between hierarchy sections
                    and constant expressions

  Clears the association between hierarchy sections
                    and constant expressions
*/
void
FlatHierarchy_clear_constants_associations(FlatHierarchy_ptr self);


/* Miscellaneous **************************************************************/

/*!
  \methodof FlatHierarchy
  \brief Performs a self check of the instance content wrt the
                    set of language self was declared to contain

  
*/
void FlatHierarchy_self_check(const FlatHierarchy_ptr self);

/*!
  \methodof FlatHierarchy
  \brief \todo Missing synopsis

  \todo Missing description
*/
void FlatHierarchy_type_check(FlatHierarchy_ptr self);


#endif /* __NUSMV_CORE_COMPILE_FLAT_HIERARCHY_H__ */
