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
  \brief Private interface accessed by class SymbTable

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_LAYER_PRIVATE_H__
#define __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_LAYER_PRIVATE_H__

#include "nusmv/core/compile/symb_table/SymbLayer.h"
#include "nusmv/core/compile/symb_table/SymbCache.h"


#include "nusmv/core/utils/utils.h"


/* ---------------------------------------------------------------------- */
/*     Private methods                                                    */
/* ---------------------------------------------------------------------- */

/*!
  \methodof SymbLayer
  \brief Class SymbLayer constructor

  name is copied, the caller keeps ownership of cache.
*/
SymbLayer_ptr
SymbLayer_create(const char* name, const LayerInsertPolicy policy,
                 SymbCache_ptr cache);

/*!
  \methodof SymbLayer
  \brief Class SymbLayer destructor

  Use this destructor if the SymbCache will not be
   destroyed after this call (ie. You are removing
   a layer from the Symbol Table)
*/
void SymbLayer_destroy(SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Class SymbLayer destructor

  Use this destructor if the SymbCache will be
   destroyed after this call (ie. You are
   destroying the Symbol Table)
*/
void SymbLayer_destroy_raw(SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Sets the layer name.

  This method is protected (not usable by users, only
   used by the symbol table when renaming a layer
*/
void
SymbLayer_set_name(SymbLayer_ptr self, const char* new_name);

/*!
  \methodof SymbLayer
  \brief Called every time an instance is committed within an
   encoding.

  This method is part of a private registration protocol
   between encodings and layers, and must be considered as a private
   method.  Every time a layer is registered (committed) within an
   enconding, the layer is notified with a call to this method from the
   encoding instance which the layer is committed to. This mechanism
   helps to detect errors when a layer in use by an encoding is removed
   and destroyed from within a symbol table. The destructor will always
   check that self is not in use by any encoding when it is invoked.

  \sa removed_from_enc
*/
void
SymbLayer_committed_to_enc(SymbLayer_ptr self);

/*!
  \methodof SymbLayer
  \brief Called every time an instance is removed from an
   encoding.

  This method is part of a private registration protocol
   between encodings and layers, and must be considered as a private
   method.  Every time a layer is removed (uncommitted) from an
   enconding, the layer is notified with a call to this method from the
   encoding instance which the layer is being removed from. This mechanism
   helps to detect errors when a layer in use by an encoding is removed
   and destroyed from within a symbol table. The destructor will always
   check that self is not in use by any encoding when it is invoked.

  \sa commit_to_enc
*/
void
SymbLayer_removed_from_enc(SymbLayer_ptr self);


#endif /* __NUSMV_CORE_COMPILE_SYMB_TABLE_SYMB_LAYER_PRIVATE_H__ */
