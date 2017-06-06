/* ---------------------------------------------------------------------------


  This file is part of the ``enc.bool'' package of NuSMV version 2. 
  Copyright (C) 2008 by FBK-irst. 

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
  \brief Public interface of class 'BitValues'

  BitValues is a structured array of values of
  bits.  bits are boolean variable that are used to encode a scalar
  variable.  A BitValues is used when extracting scalar value
  of a variable from the assigments to its bits. A bit value can be
  BIT_VAL_TRUE, BIT_VAL_FALSE or BIT_VAL_DONTCARE

*/


#ifndef __NUSMV_CORE_ENC_BOOL_BIT_VALUES_H__
#define __NUSMV_CORE_ENC_BOOL_BIT_VALUES_H__

#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/utils.h" 
#include "nusmv/core/utils/NodeList.h"

/*!
  \struct BitValues
  \brief Definition of the public accessor for class BitValues

  
*/
typedef struct BitValues_TAG*  BitValues_ptr;


/*!
  \brief BitValue is the set of possible values a bit can take

  
*/

typedef enum BitValue_TAG { 
  BIT_VALUE_FALSE,
  BIT_VALUE_TRUE,
  BIT_VALUE_DONTCARE,
} BitValue;

/*!
  \brief To cast and check instances of class BitValues

  These macros must be used respectively to cast and to check
  instances of class BitValues
*/
#define BIT_VALUES(self) \
         ((BitValues_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BIT_VALUES_CHECK_INSTANCE(self) \
         (nusmv_assert(BIT_VALUES(self) != BIT_VALUES(NULL)))



/**AutomaticStart*************************************************************/

struct BoolEnc_TAG; /* a forward declaration */

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BitValues
  \brief The BitValues class constructor

  The BitValues class constructor

  \sa BitValues_destroy
*/
BitValues_ptr 
BitValues_create(struct BoolEnc_TAG* enc, node_ptr var);

/*!
  \methodof BitValues
  \brief The BitValues class destructor

  The BitValues class destructor

  \sa BitValues_create
*/
void BitValues_destroy(BitValues_ptr self);

/*!
  \methodof BitValues
  \brief Returns the scalar variable self is a value for

  
*/
node_ptr BitValues_get_scalar_var(const BitValues_ptr self);

/*!
  \methodof BitValues
  \brief Returns the number of bits inside self

  
*/
size_t BitValues_get_size(const BitValues_ptr self);

/*!
  \methodof BitValues
  \brief Returns the list of names of internal bits

  Returned list belongs to self, do not destroy or
  change it. 
*/
NodeList_ptr BitValues_get_bits(const BitValues_ptr self);

/*!
  \methodof BitValues
  \brief Resets the values of bits to BIT_VALUE_DONTCARE

  
*/
void BitValues_reset(BitValues_ptr self);

/*!
  \methodof BitValues
  \brief Gets the value of ith bit

  
*/
BitValue BitValues_get(const BitValues_ptr self, size_t index);

/*!
  \methodof BitValues
  \brief Given a TRUE or FALSE expression, returns the
  corresponding BitValue

  
*/
BitValue 
BitValues_get_value_from_expr(const BitValues_ptr self, node_ptr expr);

/*!
  \methodof BitValues
  \brief Sets ith bit value to the given value

  
*/
void BitValues_set(BitValues_ptr self, 
                          size_t index, BitValue val);

/*!
  \methodof BitValues
  \brief Sets ith bit value to the given value that is given as
  node_ptr

  expr can be either TRUE or FALSE
*/
void BitValues_set_from_expr(BitValues_ptr self, 
                                    size_t index, node_ptr expr);

/*!
  \methodof BitValues
  \brief Given a list of assignments (IFF or EQUAL) to bits,
  sets values

  The list can be partial, unspecified values are set to
  BIT_VALUE_DONCARE
*/
void BitValues_set_from_values_list(BitValues_ptr self, 
                                           struct BoolEnc_TAG* enc,
                                           node_ptr vals);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_ENC_BOOL_BIT_VALUES_H__ */
