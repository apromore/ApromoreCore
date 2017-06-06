/* ---------------------------------------------------------------------------


  This file is part of the ``compile.type_checking'' package of NuSMV 
  version 2. 
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
  \brief Public interface of class 'TypeChecker'

   This class contains the functions performing type checking. 
  The class uses type_checking_violation_handler to deal with 
  type system violations (which may result in an error or warning)
  in the input files.
  After the type checking is performed, use function
  TypeChecker_get_expression_type to get the type of a particular
  expression.
  After the type checking is performed it is possible to obtain the
  type of an expression. Only memory-sharing types (SymbTablePkg_..._type)
  are used, so you can compare pointers instead of the type's contents.
  

*/



#ifndef __NUSMV_CORE_COMPILE_TYPE_CHECKING_TYPE_CHECKER_H__
#define __NUSMV_CORE_COMPILE_TYPE_CHECKING_TYPE_CHECKER_H__

#include "nusmv/core/node/MasterNodeWalker.h"

#include "nusmv/core/compile/symb_table/SymbType.h" 
#include "nusmv/core/compile/symb_table/SymbLayer.h" 
#include "nusmv/core/utils/utils.h" 



/*---------------------------------------------------------------------------*/
/* Type definitions                                                          */
/*---------------------------------------------------------------------------*/

/*!
  \struct TypeChecker
  \brief Definition of the public type for class TypeChecker

  
*/
typedef struct TypeChecker_TAG*  TypeChecker_ptr;

/*!
  \brief To cast and check instances of class TypeChecker

  These macros must be used respectively to cast and to check
  instances of class TypeChecker
*/
#define TYPE_CHECKER(self) \
         ((TypeChecker_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TYPE_CHECKER_CHECK_INSTANCE(self) \
         (nusmv_assert(TYPE_CHECKER(self) != TYPE_CHECKER(NULL)))



/* forward declaration, to avoid  circular dependency */
struct Prop_TAG;
struct SymbTable_TAG;


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof TypeChecker
  \brief TypeChecker class constructor

  TypeChecker class constructor.
   The 'symbolTable' is a symbol table to look for the type of found
   identifiers

  \sa TypeChecker_destroy
*/
TypeChecker_ptr 
TypeChecker_create(struct SymbTable_TAG* symbolTable);

/*!
  \methodof TypeChecker
  \brief TypeChecker class constructor, with registration of
   a set of default of checkers.

  TypeChecker class constructor, with registration of
   a set of default of checkers.
   The 'symbolTable' is a symbol table to look for the type of found
   identifiers

  \sa TypeChecker_destroy
*/
TypeChecker_ptr TypeChecker_create_with_default_checkers(struct SymbTable_TAG* symbolTable);

/*!
  \methodof TypeChecker
  \brief The TypeChecker class destructor

  The TypeChecker class destructor

  \sa TypeChecker_create
*/
void TypeChecker_destroy(TypeChecker_ptr self);

/*!
  \methodof TypeChecker
  \brief Returns the symbol table this type checker is
   associated to.

  During its lifetime every type checker can deal only
   with one symbol table instance (because type checker caches the
   checked expressions and their types). The symbol table is given to
   the type checker during construction, and this function returns
   this symbol table.

  \sa TypeChecker_create
*/
struct SymbTable_TAG*
TypeChecker_get_symb_table(TypeChecker_ptr self);

/*!
  \methodof TypeChecker
  \brief Calls TypeChecker_check_layer over all the layers of the symbol table
  of self 

  \sa TypeChecker_check_layer
*/
boolean 
TypeChecker_check_symb_table(TypeChecker_ptr self);

/*!
  \methodof TypeChecker
  \brief Checks that the types of variable decalarations
   in the layer are correctly formed. Also defines are checked to have
   some well-formed type.

  Constrain: the input layer should belong to the symbol
   table the type checker is associated with.

   The function iterates over all variables in the layer, and checks their type
   with function TypeChecker_is_type_wellformed.

   The function also type checks the expressions provided in
   defines (also the generated "running" defines) have some type.
   NB for developers: This is done to allow the type checker
   to remember the type of these defines (and associated constants and variable
   _process_selector_). Without this list the evaluation phase will not
   know the type of these defines (if there were not explicitly used in the
   input text), since they can be implicitly used in ASSIGN(TRANS) contrains.

   Returns true if all the types are correctly formed and the defines
   are correct, false otherwise. 

  \sa TypeChecker_is_type_wellformed
*/
boolean 
TypeChecker_check_layer(TypeChecker_ptr self,
                        SymbLayer_ptr layer);

/*!
  \methodof TypeChecker
  \brief Checks all the module contrains are correctly typed

  
   The module contrains are declarations INIT, INVAR, TRANS, ASSIGN,
   JUSTICE, COMPASSION.

   The first parameter 'checker' is a type checker to perfrom checking.
   All the remaining parameters are the sets of expressions
   constituting the bodies of the corresponding high-level
   declarations. These expressions are created during compilation and
   then passed to this function unmodified, flattened or
   flattened+expanded.  So this function is relatively specialised to
   deal with concrete data-structures created during compilation. For
   example, the expressions in the given sets are expected to be
   separated by CONS and AND.

   NOTE: if an expression has been flattened, then
   info about line numbers mat not be accurate.

   The type checker remebers all the checked expressions and their
   types, thus TypeChecker_get_expression_type uses memoizing to
   return the type of already checked expressions.

   The parameter 'assign' is actually the 'procs' returned
   by Compile_FlattenHierarchy, which contains all the assignments.

   If some of expressions violates the type system, the type checker's
   violation handler is invoked. See checkers into the checkers
   sub-package for more info.

   Returns false if the module contrains violate the type system, and
   otherwise true is returned.
   
*/
boolean
TypeChecker_check_constrains(TypeChecker_ptr self,
                             node_ptr init, node_ptr trans, 
                             node_ptr invar, node_ptr assign, 
                             node_ptr justice, node_ptr compassion);

/*!
  \methodof TypeChecker
  \brief Checks that the expression constituting the
   property is correctly typed

  
   If some of expressions violates the type system, the type checker's
   violation handler is invoked. See checkers into the checkers
   sub-package for more info.

   The type checker remebers all the checked expressions and their
   types, thus TypeChecker_get_expression_type uses memoizing to return
   the type of already checked expressions.

   If the property violates the type system, the false value is return,
   and true value otherwise.
*/
boolean 
TypeChecker_check_property(TypeChecker_ptr self, 
                           struct Prop_TAG* property);

/*!
  \methodof TypeChecker
  \brief The method type checks an expression and returns true
   if an expression is wellformed with respect to the type system and
   false otherwise.

  
   The main purpose of this function is to be invoked on temporarily
   created expressions before they are evaluated to ADD or BDD.
   This function may not be useful for expressions read from files (such as
   bodies of INVAR, SPEC, etc) since they go through flattening in the
   compilation package and type-checked there.

   NOTE: an expression may be unmodified (after parsing and compilation),
   flattened or flattened+expanded.
   The expressions and their types are remembered by the type checker and
   the expressions types can be obtained with TypeChecker_get_expression_type.

   NOTE: memoizing is enabled before checking the expression.

   See checkers into the checkers sub-package for more info.

  \sa type_checker_check_expression
*/
boolean 
TypeChecker_is_expression_wellformed(TypeChecker_ptr self,
                                     node_ptr expression, 
                                     node_ptr context);

/*!
  \methodof TypeChecker
  \brief Performs type checking of a specification

  
   A specification is a usual (i.e. able to have a type)
   expression wrapped into a node with a specification tag such as INIT,
   INVAR, SPEC, COMPASSION, etc.
   There are two special case:
   ASSIGN can contains a list of EQDEF statements, and
   COMPUTE can contains MIN and MAX statement.

   The returned value is true if no violations of the type system are detected,
   and false otherwise.

   NOTE: the expression may be unmodified (after compilation),
   flattened or flattened+expanded.
   The expressions and their types are remembered by the type checker and
   the expressions types can be obtained with TypeChecker_get_expression_type.

   NOTE: memizing is enbaled before checking the specification

   See checkers into the checkers sub-package for more info.

  \sa type_checker_check_expression
*/
boolean 
TypeChecker_is_specification_wellformed(TypeChecker_ptr self, 
                                        node_ptr expression);

/*!
  \methodof TypeChecker
  \brief Checks that a type is well formed.

  
   This function is used to check the well-formedness of a type
   from a symbol table. This type should have properly created body,
   in particular, bodies should have correct line info.

   The constrains on a type are:
   1. word type: the width should be a NUMBER and have positive value.
   The width should not be greater than implemenetation limit
   WordNumber_max_width() bit (since we do not use
   arbitrary-precision arithmetic and ADD structores will be
   too big otherwise)
   2. enum type: there should be no duplicate values.

   The third parameter is the variable name that type is to be checked.
   The variable name is used just to output proper message in the case
   of a type violation.

   In the case of a type violation the violation handler obtain an
   expression CONS with variable name as left child and the body of the type
   as the right child.
   

  \sa type_checker_check_expression
*/
boolean 
TypeChecker_is_type_wellformed(TypeChecker_ptr self,
                               SymbType_ptr type, node_ptr varName);

/*!
  \methodof TypeChecker
  \brief Returns the type of an expression.

  If the expression has been already type-checked
   by the same type-checker, then the type of an expression is returned.
   Otherwise, the expression is type checked and its type is returned.

   The parameter 'context' indicates the context where expression
   has been checked. It should be exactly the same as during type checking.
   For outside user this parameter is usually Nil.
   NOTE: The returned type may be error-type indicating that
   the expression violates the type system.
   NOTE: all returned types are the memory-sharing types
   (see SymbTablePkg_..._type). So you can compare pointers instead of
   the type's contents.

   

  \se TypeChecker_is_expression_wellformed,
   TypeChecker_is_specification_wellformed
*/
SymbType_ptr 
TypeChecker_get_expression_type(TypeChecker_ptr self,
                                node_ptr expression,
                                node_ptr context);

/*!
  \methodof TypeChecker
  \brief Returns true iff a given expression has been type checked

  If this function returns true then
   TypeChecker_get_expression_type will return the cached type without performing
   the actual type checking.

   The parameter 'context' indicates the context where expression
   has been checked. It should be exactly the same as during type checking.
   For outside user this parameter is usually Nil.
   

  \sa TypeChecker_get_expression_type
*/
boolean
TypeChecker_is_expression_type_checked(TypeChecker_ptr self,
                                       node_ptr expression,
                                       node_ptr context);
  
/**AutomaticEnd***************************************************************/


#endif /* __NUSMV_CORE_COMPILE_TYPE_CHECKING_TYPE_CHECKER_H__ */
