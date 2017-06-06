/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2. 
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
  \author Alessandro Mariotti
  \brief Public interface of class 'NFunction'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_COMPILE_SYMB_TABLE_NFUNCTION_H__
#define __NUSMV_CORE_COMPILE_SYMB_TABLE_NFUNCTION_H__


#include "nusmv/core/utils/utils.h" 
#include "nusmv/core/compile/symb_table/SymbType.h"
#include "nusmv/core/utils/NodeList.h"

/*!
  \struct NFunction
  \brief Definition of the public accessor for class NFunction

  
*/
#ifdef DEFINED_NFunction_ptr
#else
typedef struct NFunction_TAG*  NFunction_ptr;
#define DEFINED_NFunction_ptr 1
#endif

/*!
  \brief To cast and check instances of class NFunction

  These macros must be used respectively to cast and to check
f  instances of class NFunction
*/
#define N_FUNCTION(self)                        \
  ((NFunction_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define N_FUNCTION_CHECK_INSTANCE(self)                 \
  (nusmv_assert(N_FUNCTION(self) != N_FUNCTION(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof NFunction
  \brief The NFunction class constructor

  The NFunction class constructor

  \sa NFunction_destroy
*/
NFunction_ptr NFunction_create_uninterpreted(int num_args,
                                                    SymbType_ptr* args,
                                                    SymbType_ptr ret);

/*!
  \methodof NFunction
  \brief The NFunction class constructor

  The NFunction class constructor

  \sa NFunction_destroy
*/
NFunction_ptr NFunction_create_interpreted(int num_args,
                                                  SymbType_ptr* args,
                                                  SymbType_ptr ret,
                                                  void* body);

/*!
  \methodof NFunction
  \brief The NFunction class copy

  The NFunction class copy

  \sa NFunction_create, NFunction_destroy
*/
NFunction_ptr NFunction_copy(NFunction_ptr self);

/*!
  \methodof NFunction
  \brief The NFunction class destructor

  The NFunction class destructor

  \sa NFunction_create
*/
void NFunction_destroy(NFunction_ptr self);

/*!
  \methodof NFunction
  \brief The NFunction args_number field getter

  The NFunction args_number field getter

  \sa NFunction_set_args_number
*/
int NFunction_get_args_number(NFunction_ptr self);

/*!
  \methodof NFunction
  \brief The NFunction args field getter

  The NFunction args field getter

  \sa NFunction_set_args
*/
NodeList_ptr NFunction_get_args(NFunction_ptr self);

/*!
  \methodof NFunction
  \brief The NFunction return_type field getter

  The NFunction return_type field getter

  \sa NFunction_set_return_type
*/
SymbType_ptr NFunction_get_return_type(NFunction_ptr self);

/*!
  \methodof NFunction
  \brief The NFunction main_type field getter

  The NFunction main_type field getter
*/
SymbType_ptr NFunction_get_main_type(NFunction_ptr self);

/*!
  \methodof NFunction
  \brief The NFunction is_uninterpreted field getter

  The NFunction is_uninterpreted field getter
*/
boolean NFunction_is_uninterpreted(NFunction_ptr self);

/*!
  \methodof NFunction
  \brief The NFunction body field getter

  The NFunction body field getter
*/
void* NFunction_get_body(NFunction_ptr self);

/*!
\brief Comparison service
*/
boolean NFunction_equals(NFunction_ptr self,
                         NFunction_ptr other);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_COMPILE_SYMB_TABLE_NFUNCTION_H__ */
