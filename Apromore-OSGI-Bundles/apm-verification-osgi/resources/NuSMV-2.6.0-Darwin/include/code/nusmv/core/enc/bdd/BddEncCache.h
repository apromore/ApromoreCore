/* ---------------------------------------------------------------------------


  This file is part of the ``enc.bdd'' package of NuSMV version 2.
  Copyright (C) 2003 by FBK-irst.

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
  \brief The Bdd encoding cache interface

  This interface and relative class is intended to be
  used exclusively by the BddEnc class

*/


#ifndef __NUSMV_CORE_ENC_BDD_BDD_ENC_CACHE_H__
#define __NUSMV_CORE_ENC_BDD_BDD_ENC_CACHE_H__

#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/dd/dd.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/enc/utils/AddArray.h"

/*!
  \struct BddEncCache
  \brief The BddEncCache type 

  The BddEncCache type 
*/
typedef struct BddEncCache_TAG*  BddEncCache_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_ENC_CACHE(x) \
          ((BddEncCache_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_ENC_CACHE_CHECK_INSTANCE(x) \
          ( nusmv_assert(BDD_ENC_CACHE(x) != BDD_ENC_CACHE(NULL)) )


/* ---------------------------------------------------------------------- */
/* Types                                                                  */
/* ---------------------------------------------------------------------- */

/* ---------------------------------------------------------------------- */
/* Public methods                                                         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof BddEncCache
  \brief Class constructor

  
*/
BddEncCache_ptr
BddEncCache_create(SymbTable_ptr symb_table, DDMgr_ptr dd);

/*!
  \methodof BddEncCache
  \brief Class destructor

  
*/
void BddEncCache_destroy(BddEncCache_ptr self);

/*!
  \methodof BddEncCache
  \brief Call to associate given constant to the relative add

  This methods adds the given constant only if it
  does not exist already, otherwise it only increments a reference counter,
  to be used when the constant is removed later.
*/
void
BddEncCache_new_constant(BddEncCache_ptr self, node_ptr constant,
                         add_ptr constant_add);

/*!
  \methodof BddEncCache
  \brief Removes the given constant from the internal hash

  
*/
void
BddEncCache_remove_constant(BddEncCache_ptr self, node_ptr constant);

/*!
  \methodof BddEncCache
  \brief Returns true whether the given constant has been encoded

  
*/
boolean
BddEncCache_is_constant_encoded(const BddEncCache_ptr self,
                                node_ptr constant);

/*!
  \methodof BddEncCache
  \brief Returns the ADD corresponding to the given constant, or
  NULL if not defined

  Returned ADD is referenced, NULL is returned if the given
  constant is not currently encoded
*/
add_ptr
BddEncCache_lookup_constant(const BddEncCache_ptr self,
                            node_ptr constant);

/*!
  \methodof BddEncCache
  \brief Call this to insert the encoding for a given boolean
  variable

  
*/
void
BddEncCache_new_boolean_var(BddEncCache_ptr self, node_ptr var_name,
                            add_ptr var_add);

/*!
  \methodof BddEncCache
  \brief Removes the given variable from the internal hash

  
*/
void
BddEncCache_remove_boolean_var(BddEncCache_ptr self, node_ptr var_name);

/*!
  \methodof BddEncCache
  \brief Returns true whether the given boolean variable has
  been encoded

  
*/
boolean
BddEncCache_is_boolean_var_encoded(const BddEncCache_ptr self,
                                   node_ptr var_name);

/*!
  \methodof BddEncCache
  \brief Retrieves the add associated with the given boolean
  variable, if previously encoded. 

  Returned add is referenced. NULL is returned if the
  variable is not encoded.
*/
add_ptr
BddEncCache_lookup_boolean_var(const BddEncCache_ptr self, node_ptr var_name);

/*!
  \methodof BddEncCache
  \brief This method is used to remember the result of evaluation,
  i.e. to keep the association between the expression in node_ptr form
  and its ADD representation.

  The provided array of ADD will belong to "self"
  and will be freed during destruction of the class or setting a new
  value for the same node_ptr.

  NOTE: if NuSMV option "enable_sexp2bdd_caching" is unset to 0 then no
  result is kept and the provided add_array is immediately freed
*/
void BddEncCache_set_evaluation(BddEncCache_ptr self,
                                       node_ptr expr,
                                       AddArray_ptr add_array);

/*!
  \methodof BddEncCache
  \brief This method is used to remove the result of evaluation
  of an expression

  If a given node_ptr is associated already with
  some AddArray then the array is freed. Otherwise nothing happens
*/
void BddEncCache_remove_evaluation(BddEncCache_ptr self,
                                          node_ptr expr);

/*!
  \methodof BddEncCache
  \brief Retrieve the evaluation of a given symbol,
  as an array of ADD

   If given symbol has not been evaluated, NULL is returned.
  If the evaluation is in the process, BDD_ENC_EVALUATING is returned.
  Otherwise an array of ADD is returned.

  The returned array must be destroyed by the invoker!

  NB: For all expressions except of the Word type the returned
  array can contain only one element.
  NB: If NuSMV option enable_sexp2bdd_caching is unset to 0 then the hash
  may be empty.
*/
AddArray_ptr BddEncCache_get_evaluation(BddEncCache_ptr self,
                                               node_ptr expr);

/*!
  \methodof BddEncCache
  \brief Cleans those hashed entries that are about a symbol that
  is being removed

  This is called by the BddEnc class when a layer is
  begin removed and the cache has to be cleaned up
*/
void BddEncCache_clean_evaluation_about(BddEncCache_ptr self,
                                               NodeList_ptr symbs);

/*!
  \methodof BddEncCache
  \brief Cleans up the cache from all the evaluated expressions

  Note that hashed encoding of boolean variables and constants
  (added by BddEncCache_new_boolean_var and BddEncCache_new_constant, resp.)
  remains intact.
*/
void BddEncCache_clean_evaluation(BddEncCache_ptr self);

#endif /* __NUSMV_CORE_ENC_BDD_BDD_ENC_CACHE_H__ */
