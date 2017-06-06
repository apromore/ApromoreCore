/* ---------------------------------------------------------------------------


  This file is part of the ``enc.base'' package of NuSMV version 2.
  Copyright (C) 2004 by FBK-irst.

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
  \brief Public interface of class 'BaseEnc'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_ENC_BASE_BASE_ENC_H__
#define __NUSMV_CORE_ENC_BASE_BASE_ENC_H__

#include "nusmv/core/compile/symb_table/SymbTable.h"

#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct BaseEnc
  \brief Definition of the public accessor for class BaseEnc

  
*/
typedef struct BaseEnc_TAG*  BaseEnc_ptr;

/*!
  \brief To cast and check instances of class BaseEnc

  These macros must be used respectively to cast and to check
  instances of class BaseEnc
*/
#define BASE_ENC(self) \
         ((BaseEnc_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BASE_ENC_CHECK_INSTANCE(self) \
         (nusmv_assert(BASE_ENC(self) != BASE_ENC(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BaseEnc
  \brief Returns the SymbTable that self uses

  Returns the SymbTable that self uses. Returned instance
  belongs to self, do not destroy it.

  \sa BaseEnc_get_type_checker
*/
SymbTable_ptr BaseEnc_get_symb_table(const BaseEnc_ptr self);

/*!
  \methodof BaseEnc
  \brief Returns the type checker instance owned by the
                      SymbTable that self uses

  Returns the type checker instance owned by the
                      SymbTable that self uses. Returned instance
                      belongs to self, do not destroy it.

  \sa BaseEnc_get_symb_table
*/
TypeChecker_ptr
BaseEnc_get_type_checker(const BaseEnc_ptr self);

/*!
  \methodof BaseEnc
  \brief Returns true whether the given layer name is the
  name of a layer that is currently committed to self.

  
*/
boolean
BaseEnc_layer_occurs(const BaseEnc_ptr self, const char* layer_name);

/*!
  \methodof BaseEnc
  \brief Returns the list of the committed layers

  Returned list is a list of SymbLayer instances. The
  returned list is ordered wrt to layers insert policy. The list and
  its content still belongs to self, do not destroy or change it
*/
NodeList_ptr
BaseEnc_get_committed_layers(const BaseEnc_ptr self);

/*!
  \methodof BaseEnc
  \brief Returns the list of names of the committed layers

  Returned array belongs to self. Do not store it
  permanently, change or delete it. If you commit or remove a
  layer into self, any previoulsy returned array will become
  invalid.
*/
const array_t*
BaseEnc_get_committed_layer_names(BaseEnc_ptr self);

/*!
  \methodof BaseEnc
  \brief Commits all layers which have not been committed yet

  Use this to commit e.g. all layers already committed
  to another encoder. Returns the number of committed layers
*/
int
BaseEnc_commit_layers(BaseEnc_ptr self,
                      const array_t* layer_names);

/*!
  \methodof BaseEnc
  \brief Removes all layers which have been actually committed

  Use this to remove a bunch of layers. Only the layers
  which have been previously committed will be actually removed, all
  the other will be ignored. Returns the number of committed layers

  \sa BaseEnc_commit_layers
*/
int
BaseEnc_remove_layers(BaseEnc_ptr self,
                      const array_t* layer_names);

/*!
  \methodof BaseEnc
  \brief The BaseEnc class destructor

  The BaseEnc class destructor. Since this class is pure
  there is no constructor.
*/
VIRTUAL void BaseEnc_destroy(BaseEnc_ptr self);

/*!
  \methodof BaseEnc
  \brief Call this method to enter a new layer. All variables
  occurring in the layer, will be encoded as a result.

  This method is virtual. The result of the encoding
  depends on the actual instance (its actual class) it is invoked on.
*/
VIRTUAL void BaseEnc_commit_layer(BaseEnc_ptr self,
                                         const char* layer_name);

/*!
  \methodof BaseEnc
  \brief Call this method to remove an already committed layer.
  All variables occurring in the layer will be removed as a result. It will no
  longer allowed to use those variables within expressions encoded by self

  This method is virtual. The result of the removal
  depends on the actual instance (its actual class) it is invoked on.
*/
VIRTUAL void BaseEnc_remove_layer(BaseEnc_ptr self,
                                         const char* layer_name);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_ENC_BASE_BASE_ENC_H__ */
