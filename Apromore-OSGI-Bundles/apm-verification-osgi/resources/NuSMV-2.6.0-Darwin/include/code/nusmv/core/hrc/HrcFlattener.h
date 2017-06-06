
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
  \author Alessandro Mariotti
  \brief Public interface of class 'HrcFlattener'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_HRC_HRC_FLATTENER_H__
#define __NUSMV_CORE_HRC_HRC_FLATTENER_H__


#include "nusmv/core/utils/utils.h"
#include "nusmv/core/hrc/HrcNode.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/compile/FlatHierarchy.h"
#include "nusmv/core/fsm/sexp/SexpFsm.h"
#include "nusmv/core/hrc/hrc.h"

/*!
  \struct HrcFlattener
  \brief Definition of the public accessor for class HrcFlattener

  
*/
typedef struct HrcFlattener_TAG*  HrcFlattener_ptr;

/*!
  \brief To cast and check instances of class HrcFlattener

  These macros must be used respectively to cast and to check
  instances of class HrcFlattener
*/
#define HRC_FLATTENER(self) \
         ((HrcFlattener_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define HRC_FLATTENER_CHECK_INSTANCE(self) \
         (nusmv_assert(HRC_FLATTENER(self) != HRC_FLATTENER(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief HrcFlattener main routine

  HrcFlattener top-level function. Given an Hrc hierarchy
                      and a symbol table (and eventually a symbol
                      layer), creates and returns a flat
                      hierarchy. Parameter layer can be NULL. Is so, a
                      new layer belonging to the given symbol table is
                      created. If not NULL, the layer must belong to
                      the given symbol table

  \sa HrcFlattener_create
*/
FlatHierarchy_ptr
HrcToFlatHierarchy(const NuSMVEnv_ptr env,
                   HrcNode_ptr node,
                   SymbTable_ptr symb_table,
                   SymbLayer_ptr layer);

/*!
  \brief HrcFlattener main routine

  HrcFlattener top-level function. Given an Hrc hierarchy
                      and a symbol table (and eventually a symbol
                      layer), creates and returns a sexpfsm. Parameter
                      layer can be NULL. Is so, a new layer belonging
                      to the given symbol table is created. If not
                      NULL, the layer must belong to the given symbol
                      table

  \se Adds new symbols to the given symbol table

  \sa HrcFlattener_create
*/
SexpFsm_ptr
HrcToSexpFsm(const NuSMVEnv_ptr env,
             HrcNode_ptr node,
             SymbTable_ptr symb_table,
             SymbLayer_ptr layer);

/*!
  \methodof HrcFlattener
  \brief The HrcFlattener class constructor

  The HrcFlattener class constructor. Parameter
                      layer can be NULL. Is so, a new layer belonging
                      to the given symbol table is created. If not
                      NULL, the layer must belong to the given symbol
                      table. The given hrc node must be the top-level
                      node.  Hrc Localize methods should be used first
                      if trying to flatten an instance which is not
                      the main one

  \sa HrcFlattener_destroy
*/
HrcFlattener_ptr HrcFlattener_create(const NuSMVEnv_ptr env,
                                            HrcNode_ptr node,
                                            SymbTable_ptr symb_table,
                                            SymbLayer_ptr layer);

/*!
  \methodof HrcFlattener
  \brief Does the actual flattening.

  This method does the actual flattening
                      job. Takes the input hrc node and processes it
                      in 2 steps: in the first step a first version of
                      the hierarchy is build, where expressions are
                      just contextualized but not flattened. After
                      this Compile_ProcessHierarchy is called and the
                      actual flat hierachy is built. The symbol table
                      is also filled
*/
void HrcFlattener_flatten_hierarchy(HrcFlattener_ptr self);

/*!
  \methodof HrcFlattener
  \brief Fills the symbol table without building
                      any flat hierarchy

  Fills the symbol table without building
                      any flat hierarchy
*/
void HrcFlattener_populate_symbol_table(HrcFlattener_ptr self);

/*!
  \methodof HrcFlattener
  \brief Get the built flat hierarchy

  Get the internally built flat hierarchy. The hierarchy
                      is populated only if
                      HrcFlattener_flatten_hierarchy was previously
                      called

  \sa HrcFlattener_flatten_hierarchy
*/
FlatHierarchy_ptr
HrcFlattener_get_flat_hierarchy(HrcFlattener_ptr self);

/*!
  \methodof HrcFlattener
  \brief Get the symbol table

  Gets the internally populated symbol table. The
                      st is populated only if
                      HrcFlattener_flatten_hierarchy was previously
                      called

  \sa HrcFlattener_flatten_hierarchy
*/
SymbTable_ptr
HrcFlattener_get_symbol_table(HrcFlattener_ptr self);

/*!
  \methodof HrcFlattener
  \brief Get the symbol layer

  Gets the internally populated symbol layer. The
                      layer is populated only if
                      HrcFlattener_flatten_hierarchy was previously
                      called

  \sa HrcFlattener_flatten_hierarchy
*/
SymbLayer_ptr
HrcFlattener_get_symbol_layer(HrcFlattener_ptr self);

/*!
  \methodof HrcFlattener
  \brief Dumps the flatten model on file "out"

  Dumps the flatten model on file "out"
*/
void
HrcFlattener_write_flatten_model(HrcFlattener_ptr self,
                                 FILE* out);

/*!
  \methodof HrcFlattener
  \brief The HrcFlattener class destructor

  The HrcFlattener class destructor

  \sa HrcFlattener_create
*/
void HrcFlattener_destroy(HrcFlattener_ptr self);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_HRC_HRC_FLATTENER_H__ */
