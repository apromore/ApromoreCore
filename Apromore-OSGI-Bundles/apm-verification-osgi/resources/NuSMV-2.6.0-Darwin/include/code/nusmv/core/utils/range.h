/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
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
  \brief External header of the utils package

  External header of the utils package.

*/


#ifndef __NUSMV_CORE_UTILS_RANGE_H__
#define __NUSMV_CORE_UTILS_RANGE_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"

/*!
  \brief Called before using Utils_range_check callback function

  

  \se Utils_range_check
*/
void Utils_set_data_for_range_check(const NuSMVEnv_ptr env,
                                           node_ptr var,
                                           node_ptr range);

/*!
  \brief Called before using Utils_range_check callback function

  

  \se Utils_range_check
*/
void Utils_set_mode_for_range_check(const NuSMVEnv_ptr env,
                                           boolean is_fatal);

/*!
  \brief Checks if the values of <code>n</code> is in the
  range allowed for the variable.

  Checks if the values of <code>n</code> is in the
  range allowed for the variable. The allowed values are stored in the
  global variable <code>the_range</code>, which should be set before
  invocation of this function.
  An error occure if:
   1. the value is not in the range (all FAILURE node are, of course, irgnored)
  

  \se Utils_set_data_for_range_check
*/
void Utils_range_check(const NuSMVEnv_ptr env, node_ptr n);

/*!
  \brief Checks if the values of <code>n</code> does not
  contains FAILURE node. If they do then report and terminate.

  

  \se Utils_set_data_for_range_check
*/
void Utils_failure_node_check(const NuSMVEnv_ptr env, node_ptr n);

/*!
  \brief Checks if the first argument is contained in the second.

  Returns true if the first argument is contained in the
  set represented by the second, false otherwise. If the first
  argument is not a CONS, then it is considered to be a singleton.

  \se None

  \sa in_list
*/
boolean Utils_is_in_range(node_ptr s, node_ptr d);

/*!
  \brief Checks that in given subrange n..m, n<=m

  Returns True if in given subrange n..m n <= m.
  Given node_ptr must be of TWODOTS type

  \sa Utils_check_subrange_not_negative
*/
boolean Utils_check_subrange(node_ptr subrange);

/*!
  \brief Checks that in given subrange n..m, n<=m, and that n,m
  are not negative

  Check for correct positive (or zero) range

  \sa Utils_check_subrange
*/
boolean Utils_check_subrange_not_negative(node_ptr subrange);


#endif /* __NUSMV_CORE_UTILS_RANGE_H__ */
