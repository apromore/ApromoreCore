/* ---------------------------------------------------------------------------


  This file is part of the ``enc.bool'' package of NuSMV version 2.
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
  \brief Public interface of class 'BoolEnc'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_ENC_BOOL_BOOL_ENC_H__
#define __NUSMV_CORE_ENC_BOOL_BOOL_ENC_H__

#include "nusmv/core/enc/bool/BitValues.h"
#include "nusmv/core/enc/base/BaseEnc.h"

#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/set/set.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/object.h"

/*!
  \struct BoolEnc
  \brief Definition of the public accessor for class BoolEnc

  
*/
typedef struct BoolEnc_TAG*  BoolEnc_ptr;

/*!
  \brief To cast and check instances of class BoolEnc

  These macros must be used respectively to cast and to check
  instances of class BoolEnc
*/
#define BOOL_ENC(self) \
         ((BoolEnc_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BOOL_ENC_CHECK_INSTANCE(self) \
         (nusmv_assert(BOOL_ENC(self) != BOOL_ENC(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BoolEnc
  \brief The BoolEnc class constructor

  The BoolEnc class constructor

  \sa BoolEnc_destroy
*/
BoolEnc_ptr BoolEnc_create(SymbTable_ptr symb_table);

/*!
  \methodof BoolEnc
  \brief The BoolEnc class destructor

  The BoolEnc class destructor

  \sa BoolEnc_create
*/
VIRTUAL void BoolEnc_destroy(BoolEnc_ptr self);

/*!
  \methodof BoolEnc
  \brief Returns true if the given symbol is the name of
  a bit variable that is part of a scalar var

  

  \sa BoolEnc_get_scalar_var_of_bit
*/
boolean
BoolEnc_is_var_bit(const BoolEnc_ptr self, node_ptr name);

/*!
  \methodof BoolEnc
  \brief Returns true if the given symbol is the name of
  a scalar (non-boolean) variable

  
*/
boolean
BoolEnc_is_var_scalar(const BoolEnc_ptr self, node_ptr name);

/*!
  \methodof BoolEnc
  \brief Returns the name of the scalar variable whose
  the given bit belongs

  Returns the name of the scalar variable whose
  the given bit belongs. The given var MUST be a bit

  \sa BoolEnc_is_var_bit, BoolEnc_get_index_from_bit
*/
node_ptr
BoolEnc_get_scalar_var_from_bit(const BoolEnc_ptr self, node_ptr name);

/*!
  \methodof BoolEnc
  \brief Given a scalar variable name, construct the name for
                      the nth-indexed bit.

  Constructs and returns the name of the nth-indexed bit
                      of the given scalar variable

  \sa BoolEnc_is_var_bit, BoolEnc_get_index_from_bit
*/
node_ptr
BoolEnc_make_var_bit(const BoolEnc_ptr self, node_ptr name, int index);

/*!
  \methodof BoolEnc
  \brief Returns the index of given bit.

  The given var MUST be a bit

  \sa BoolEnc_is_var_bit, BoolEnc_get_scalar_var_from_bit
*/
int
BoolEnc_get_index_from_bit(const BoolEnc_ptr self, node_ptr name);

/*!
  \methodof BoolEnc
  \brief Returns the list of boolean vars used in the encoding of
  given scalar var

  Returned list must be destroyed by the caller
*/
NodeList_ptr
BoolEnc_get_var_bits(const BoolEnc_ptr self, node_ptr name);

/*!
  \methodof BoolEnc
  \brief Given a variable, returns its boolean encoding

  Given variable must have been encoded by self
*/
node_ptr
BoolEnc_get_var_encoding(const BoolEnc_ptr self, node_ptr name);

/*!
  \methodof BoolEnc
  \brief Given a set of constants values (for example, the domain
                    of a scalar variable), calculates its boolean
                    encoding by introducing boolean symbols that
                    are returned along with the resulting
                    encoding.

  This method can be used to retrieve the boolean
                    encoding of a given set of symbols.

                    For example, it may be used to calculate the
                    boolean encoding representing the domain of a
                    scalar variable which has not been added to any
                    layer. It returns the boolean encoding
                    (typically a ITE node) and the set of boolean
                    symbols (bits) that have been introduced in the
                    encoding. Important: the introduced boolean
                    symbols are not variables, as they are not
                    declared into the symbol table. It is up to the
                    caller later to declare them if needed.

                    The introduced symbol names are guaranteed to
                    be not among the currently declared symbols.

                    To retrieve the boolean encoding of an existing
                    (and committed) variable, use method
                    get_var_encoding instead.

  \se Passed set is filled with symbol bits occurring in the
                     encoding. No memoization or change is performed.

  \sa BoolEnc_get_var_encoding
*/
node_ptr
BoolEnc_get_values_bool_encoding(const BoolEnc_ptr self,
                                 node_ptr values,
                                 Set_t* bits);

/*!
  \methodof BoolEnc
  \brief Given the name of a scalar layer, a name of the
  corresponding boolean layer is returned.

  Returned string should NOT be modified or freed.
*/
const char*
BoolEnc_scalar_layer_to_bool_layer(const BoolEnc_ptr self,
                                   const char* layer_name);

/*!
  \brief Determines if a layer name corresponds to a bool layer

  Given the name of a layer, returns true if it is the
  name of a boolean layer.

  \se None

  \sa BoolEnc_scalar_layer_to_bool_layer
*/
boolean
BoolEnc_is_bool_layer(const char* layer_name);

/*!
  \methodof BoolEnc
  \brief Given a BitValues instance already set with an
  assigments for its bits, returns the corresponding value for the
  scalar or word variable whose bits are contained into the
  BitValues instance

  Returns an ATOM, a NUMBER, an NUMBER_UNSIGNED_WORD,
  etc. depending on the kind of variable. 
*/
node_ptr
BoolEnc_get_value_from_var_bits(const BoolEnc_ptr self,
                                const BitValues_ptr bit_values);

/*!
  \methodof BoolEnc
  \brief Given a variable, it returns the mask of its encoding

  Returns an expression representing the mask that
  removes repetitions of leaves in a variable encoding by assigning
  value false to don't care boolean variables.Forr Boolean variables
  it returns the expression TRUE. Similarly for Word variables (since
  for words there are non redundant assignments).

  As an example of what this function does, let us consider a variable
  x having range 0..4. It can be encoded with 3 bits are needed to
  encode it: x0, x1, x2. The encodeding performed by NuSMV is

     ITE(x0, ITE(x1, 1, 3), ITE(x1, 2, ITE(x2, 4,  0))).

  Thus x=2 corresponds to assignment !x0&x1 where x2 is a dont'care.
  Similarly for x=1 and x=3 (for x=0 and x=4) there is a unique
  complete assignment to the x0, x1, x2 variables that represent the
  respective encoding). This function fixes a value for x2 in the
  assignments representing x=2, x=1 and x=3 respectively (it force x2
  to be false). Thus it builds the formula in this case:

     ITE(x0, ITE(x2, 0, 1), ITE(x1, 1, ITE(x2, 0,  1)))

  that removes the redundant assignments where needed.

  Result is memoized. See issue 0925 for further details.
*/
node_ptr
BoolEnc_get_var_mask(const BoolEnc_ptr self, node_ptr name);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_ENC_BOOL_BOOL_ENC_H__ */
