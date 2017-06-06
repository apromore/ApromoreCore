/* ---------------------------------------------------------------------------


  This file is part of the ``compile'' package of NuSMV version 2.
  Copyright (C) 2010 by FBK-irst.

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
  \author Marco Roveri Alessandro Mariotti
  \brief Public interface of class 'ResolveSymbol'

  Basic Routines for resolving a name

*/


#ifndef __NUSMV_CORE_COMPILE_SYMB_TABLE_RESOLVE_SYMBOL_H__
#define __NUSMV_CORE_COMPILE_SYMB_TABLE_RESOLVE_SYMBOL_H__


#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"

/*!
  \struct ResolveSymbol
  \brief Definition of the public accessor for class ResolveSymbol

  
*/
typedef struct ResolveSymbol_TAG*  ResolveSymbol_ptr;

/*!
  \brief To cast and check instances of class ResolveSymbol

  These macros must be used respectively to cast and to check
  instances of class ResolveSymbol
*/
#define RESOLVE_SYMBOL(self) \
         ((ResolveSymbol_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RESOLVE_SYMBOL_CHECK_INSTANCE(self) \
         (nusmv_assert(RESOLVE_SYMBOL(self) != RESOLVE_SYMBOL(NULL)))

/* Forward declaration of the SymbTable structure, in order to avoid
   circular dependency  (ST needs RS, RS needs ST) */
struct SymbTable_TAG;


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof ResolveSymbol
  \brief The ResolveSymbol class constructor

  The ResolveSymbol class constructor

  \sa ResolveSymbol_destroy
*/
ResolveSymbol_ptr ResolveSymbol_create(void);

/*!
  \methodof ResolveSymbol
  \brief The ResolveSymbol class destructor

  The ResolveSymbol class destructor

  \sa ResolveSymbol_create
*/
void ResolveSymbol_destroy(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Checks if the resolved symbol is undefined or not.

  Checks if the resolved symbol is undefined or not.
                      Returns true if the symbol is undefined. A
                      symbol is undefined if it is not declared within
                      the symbol table (i.e it is not a var, a define,
                      an array define, an array, a parameter or a
                      constant)
*/
boolean ResolveSymbol_is_undefined(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Checks if the resolved symbol is defined or not.

  Checks if the resolved symbol is defined or not.
                      Returns true if the symbol is defined. A
                      symbol is defined if it is declared within
                      the symbol table (i.e it is a var or a define,
                      an array define, an array, a parameter or a
                      constant)
*/
boolean ResolveSymbol_is_defined(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Checks if the symbol is ambiguos or not.

  Checks if the symbol is ambiguos or not. A symbol
                      is ambiguos if it is declared more than once
                      (e.g. as a variable and as a constant)
*/
boolean ResolveSymbol_is_ambiguous(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Checks if the symbol is a variable

  Checks if the symbol is a variable
*/
boolean ResolveSymbol_is_var(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Checks if the symbol is a define

  Checks if the symbol is a define
*/
boolean ResolveSymbol_is_define(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Checks if the symbol is a function

  Checks if the symbol is a function
*/
boolean ResolveSymbol_is_function(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Checks if the symbol is a constant

  Checks if the symbol is a constant
*/
boolean ResolveSymbol_is_constant(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Checks if the symbol is a parameter (formal)

  Checks if the symbol is a parameter (formal)
*/
boolean ResolveSymbol_is_parameter(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Checks if the symbol is an array

  Checks if the symbol is an array
*/
boolean ResolveSymbol_is_array(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Checks if the symbol is an array define

  Checks if the symbol is an array define
*/
boolean ResolveSymbol_is_array_def(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Check if there has been some error in the
                      resolution of the symbol

  Check if there has been some error in the
                      resolution of the symbol
*/
boolean ResolveSymbol_is_error(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Get the error message, if any error occurred.

  Get the error message, if any error occurred.
                      The returned message has to be freed by the caller
*/
char* ResolveSymbol_get_error_message(ResolveSymbol_ptr self,
                                             MasterPrinter_ptr printer);

/*!
  \methodof ResolveSymbol
  \brief Prints the error message, if any error occurred.

  Prints the error message on the given stream, 
                      if any error occurred.
*/
void ResolveSymbol_print_error_message(ResolveSymbol_ptr self,
                                              MasterPrinter_ptr printer,
                                              FILE* stream);

/*!
  \methodof ResolveSymbol
  \brief Throws an internal error if an error occurred.

  Throws an internal error if an error occurred.
                      The error MUST exist, so the function should be
                      used in couple with ResolveSymbol_is_error. The
                      printed message is taken using
                      ResolveSymbol_get_error_message, and rpterr is
                      used for throwing the error.

  \sa ResolveSymbol_get_error_message ResolveSymbol_is_error
*/
void ResolveSymbol_throw_error(ResolveSymbol_ptr self,
                                      const NuSMVEnv_ptr env);

/*!
  \methodof ResolveSymbol
  \brief Returns the resolved name of the symbol

  Returns the resolved name of the symbol
*/
node_ptr ResolveSymbol_get_resolved_name(ResolveSymbol_ptr self);

/*!
  \methodof ResolveSymbol
  \brief Resolves the given symbol in the given context, and 
                      returns it.

  Resolves the given symbol in the given context, and
                      returns it.

                      This function has to be called before any other
                      function, since it initializes the internal
                      structure. The internal structure is reset
                      before doing anything else.

                      It is possible to get the resolved name later by calling 
                      get_resolved_name.
                      

  \sa ResolveSymbol_get_resolved_name
*/
node_ptr ResolveSymbol_resolve(ResolveSymbol_ptr self,
                                      struct SymbTable_TAG* st,
                                      node_ptr name,
                                      node_ptr context);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_COMPILE_SYMB_TABLE_RESOLVE_SYMBOL_H__ */
