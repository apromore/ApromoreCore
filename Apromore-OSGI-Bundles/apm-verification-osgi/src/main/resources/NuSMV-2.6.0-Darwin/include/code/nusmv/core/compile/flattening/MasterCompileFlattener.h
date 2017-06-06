/* ---------------------------------------------------------------------------


  This file is part of the ``compile.flattening'' package of NuSMV version 2.
  Copyright (C) 2013 by FBK-irst.

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
  \author Sergio Mover
  \brief Public interface of class 'MasterCompileFlattener'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_COMPILE_FLATTENING_MASTER_COMPILE_FLATTENER_H__
#define __NUSMV_CORE_COMPILE_FLATTENING_MASTER_COMPILE_FLATTENER_H__


#include "nusmv/core/node/MasterNodeWalker.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/utils/defs.h"

/*!
  \struct MasterCompileFlattener
  \brief Definition of the public accessor for class MasterCompileFlattener

  
*/
typedef struct MasterCompileFlattener_TAG*  MasterCompileFlattener_ptr;


/*!
  \brief Enumeration used to select the handling of defines

  
*/

typedef enum MasterCompileFlattener_def_mode_type_TAG {
  Flattener_Get_Def_Mode,
  Flattener_Expand_Def_Mode
} MasterCompileFlattener_def_mode;

/*!
  \brief To cast and check instances of class MasterCompileFlattener

  These macros must be used respectively to cast and to check
  instances of class MasterCompileFlattener
*/
#define MASTER_COMPILE_FLATTENER(self) \
         ((MasterCompileFlattener_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MASTER_COMPILE_FLATTENER_CHECK_INSTANCE(self) \
         (nusmv_assert(MASTER_COMPILE_FLATTENER(self) != MASTER_COMPILE_FLATTENER(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof MasterCompileFlattener
  \brief The MasterCompileFlattener class constructor

  The MasterCompileFlattener class constructor

  \sa MasterCompileFlattener_destroy
*/
MasterCompileFlattener_ptr
MasterCompileFlattener_create(const NuSMVEnv_ptr env);

/*!
  \methodof MasterCompileFlattener
  \brief The MasterCompileFlattener class destructor

  The MasterCompileFlattener class destructor

  \sa MasterCompileFlattener_create
*/
void MasterCompileFlattener_destroy(MasterCompileFlattener_ptr self);

/*!
  \methodof MasterCompileFlattener
  \brief Builds the flattened version of an expression.

  Builds the flattened version of an
  expression. It does not expand defined symbols with the
  corresponding body.
*/
node_ptr
MasterCompileFlattener_flatten(MasterCompileFlattener_ptr self,
                               SymbTable_ptr symb_table,
                               node_ptr sexp,
                               node_ptr context);

/*!
  \methodof MasterCompileFlattener
  \brief Flattens an expression and expands defined symbols.

  Flattens an expression and expands defined symbols.

  \se New entries may be added to flatten_def_hash changes
*/
node_ptr
MasterCompileFlattener_flatten_expand_define(MasterCompileFlattener_ptr self,
                                             SymbTable_ptr symb_table,
                                             node_ptr sexp,
                                             node_ptr context);

/*!
  \methodof MasterCompileFlattener
  \brief Gets the flattened version of an atom.

  Gets the flattened version of an atom. If the
  atom is a define then it is expanded. If the definition mode
  is set to "expand", then the expanded flattened version is returned,
  otherwise, the atom is returned.

  \se The flatten_def_hash is modified in order to
  memoize previously computed definition expansion.
*/
node_ptr
MasterCompileFlattener_get_definition(MasterCompileFlattener_ptr self,
                                      SymbTable_ptr symb_table,
                                      node_ptr sexp,
                                      MasterCompileFlattener_def_mode mode);

/*!
  \methodof MasterCompileFlattener
  \brief Remove the information associated to name from
  the define hash

  
*/
void
MasterCompileFlattener_remove_define_info(MasterCompileFlattener_ptr self,
                                          SymbTable_ptr symb_table,
                                          node_ptr name);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_COMPILE_FLATTENING_MASTER_COMPILE_FLATTENER_H__ */
