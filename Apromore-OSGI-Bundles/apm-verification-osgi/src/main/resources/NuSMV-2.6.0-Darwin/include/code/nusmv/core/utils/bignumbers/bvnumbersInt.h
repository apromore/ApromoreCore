/* ---------------------------------------------------------------------------


  This file is part of the ``utils/bignumbers'' package of NuSMV version 2.
  Copyright (C) 2012 by FBK-irst.

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
  \author \todo: Missing author
  \brief The header file containing the word specific functions on
  infinite length numbers

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_UTILS_BIGNUMBERS_BVNUMBERS_INT_H__

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define __NUSMV_CORE_UTILS_BIGNUMBERS_BVNUMBERS_INT_H__

#include "nusmv/core/utils/bignumbers/numbersInt.h"
#include <limits.h>

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Stucture declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

boolean BVQNumber_fits(const QNumber* n, size_t num_bits);

boolean BVQNumber_is_pow2(const QNumber* n, size_t* out_n);

QNumber BVQNumber_twos_complement(const QNumber* n, size_t num_bits);

QNumber BVQNumber_pow2(size_t n);

/* bit-manipulating functions */
boolean BVQNumber_test_bit(const QNumber* n, size_t index);

void BVQNumber_set_bit(QNumber* n, size_t index, int value);

QNumber BVQNumber_bit_and(const QNumber* n1, const QNumber* n2);

QNumber BVQNumber_bit_or(const QNumber* n1, const QNumber* n2);

QNumber BVQNumber_bit_xor(const QNumber* n1, const QNumber* n2);

QNumber BVQNumber_bit_complement(const QNumber* n);

QNumber BVQNumber_bit_left_shift(const QNumber* n, size_t i);

QNumber BVQNumber_bit_right_shift(const QNumber* n, size_t i);

size_t BVQNumber_scan_bit_1(const QNumber* n, size_t start_index);

boolean BVQNumber_to_long(const QNumber* n, long* out);

void BVQNumber_check_bv(const QNumber* n);

long BVQNumber_bit_and_l(long a, long b);

long BVQNumber_bit_or_l(long a, long b);

long BVQNumber_bit_xor_l(long a, long b);

size_t get_max_size_t_value(void);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_UTILS_BIGNUMBERS_BVNUMBERS_INT_H__ */
