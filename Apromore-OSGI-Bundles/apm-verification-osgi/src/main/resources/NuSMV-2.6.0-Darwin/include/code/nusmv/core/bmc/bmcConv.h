/* ---------------------------------------------------------------------------


  This file is part of the ``bmc'' package of NuSMV version 2.
  Copyright (C) 2000-2001 by FBK-irst and University of Trento.

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
  \brief The conversion be<->bdd module interface

  This layer contains all functionalities to perform
  conversion from Boolean Expressions (be) to BDD-based boolean expressions,
  and vice-versa

*/


#ifndef __NUSMV_CORE_BMC_BMC_CONV_H__
#define __NUSMV_CORE_BMC_BMC_CONV_H__

#include "nusmv/core/be/be.h"
#include "nusmv/core/enc/be/BeEnc.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Given a be, constructs the corresponding boolean
  expression

  Descends the structure of the BE with dag-level
  primitives. Uses the be encoding to perform all time-related operations.
  There is no need to clean the hash used for memoizing since it is done by the
  symbol table with a trigger.
*/
node_ptr Bmc_Conv_Be2Bexp(BeEnc_ptr be_enc, be_ptr be);

/*!
  \brief <b>Converts</b> given <b>boolean expression</b> into
  correspondent <b>reduced boolean circuit</b>

  Uses the be encoding to perform all
  time-related operations.

  \se be hash may change
*/
be_ptr Bmc_Conv_Bexp2Be(BeEnc_ptr be_enc, node_ptr bexp);

/*!
  \brief <b>Converts</b> given <b>boolean expressions list </b>
  into correspondent <b>reduced boolean circuits list</b>

  

  \se be hash may change
*/
node_ptr
Bmc_Conv_BexpList2BeList(BeEnc_ptr be_enc, node_ptr bexp_list);

/*!
  \brief This function converts a BE model (i.e. a list of BE
  literals) to symbolic expressions.

  

  be_model is the model which will be transformed, i.e llList of
  BE literal.

  k is the number of steps (i.e. times+1) in the model.

  The returned results will be provided in:
  *frozen will point to expression over frozen variables,
  *states will point to an array of size k+1 to expressions over state vars.
  *inputs will point to an array of size k+1 to expressions over input vars.

  In arrays every index corresponds to the corresponding time,
  beginning from 0 for initial state.

  Every expressions is a list with AND used as connection and Nil at
  the end, i.e. it can be used as a list and as an expression.
  Every element of the list can have form:
  1) "var" or "!var" (if parameter convert_to_scalars is false)
  2) "var=const" (if parameter convert_to_scalar is true).

  By default BE literals are converted to bits of symbolic
  variables. With parameter convert_to_scalars set up the bits are
  converted to actual symbolic variables and scalar/word/etc
  values. Note however that if BE model does not provide a value for
  particular BE index then the corresponding bit may not be presented
  in the result expressions or may be given some random value
  (sometimes with convert_to_scalars set up). Note that in both cases
  the returned assignments may be incomplete.

  It is the responsibility of the invoker to free all arrays and the
  lists of expressions (i.e. run free_list on *frozen and every
  element of arrays returned).  EQUAL nodes (when convert_to_scalars
  is set up) are created with find_nodes, i.e. no freeing is need.

  No caching or other side-effect are applied

  TODO[AT] a parameter may be added to make the returned assignments complete
*/
void Bmc_Conv_get_BeModel2SymbModel(const BeEnc_ptr be_enc,
                                    const Slist_ptr be_model,
                                    int k,
                                    boolean convert_to_scalars,
                                    node_ptr* frozen,
                                    array_t** states,
                                    array_t** inputs);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_CONV_H__ */

