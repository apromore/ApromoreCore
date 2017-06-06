/* ---------------------------------------------------------------------------


  This file is part of the ``utils/bigwordnumbers'' package of NuSMV version 2.
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
  \brief QNumber class description

  This file contains the class description of the files
  of the QNumber class which is the current implementation for NuSMV
  infinite numbers.
  

*/


#ifndef __NUSMV_CORE_UTILS_BIGNUMBERS_NUMBERS_INT_H__

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define __NUSMV_CORE_UTILS_BIGNUMBERS_NUMBERS_INT_H__

#include <gmp.h>
#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include <math.h>
#include <string.h>

#include "nusmv/core/utils/defs.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define QNumber_NUM_BITS_NORMAL (sizeof(long) * 8)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define manual_set_buf_size 100

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief A GMP big number

  A GMP big number
*/

typedef struct  {
  mpz_t num;
  mpz_t den;
} Gmp;


/*!
  \brief A big number can either be a GMP big number or still be
                    a long

  A big number can either be a GMP big number or still be
                    a long
*/

typedef struct {
  /*
   * Implementation - numerator and denominator (normalized).  we keep both
   * in unions: if the values are small, we use plain integers, otherwise we
   * switch to GMP
   * */
  union {
    long num;
    Gmp *gmp;
  } data;
  long den;
} QNumber;

/*---------------------------------------------------------------------------*/
/* Stucture declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

long QNumber_gcd_nocache(long a, long b);

Gmp* Gmp_alloc(void);

void Gmp_free(Gmp* p);

void QNumber_clean_gmp(QNumber* p);

QNumber QNumber_from_long(long n);

QNumber QNumber_from_two_longs(long n, long d);

QNumber QNumber_from_mpq(mpq_t* n);

QNumber QNumber_from_two_mpzs(mpz_t *n, mpz_t *d);

QNumber QNumber_from_other(const QNumber *other);

QNumber QNumber_from_nothing(void);

/*!
  \methodof QNumber
  \todo
*/
QNumber* QNumber_copy_to_heap(QNumber* self);

boolean QNumber_big(const QNumber *self);

QNumber QNumber_assign(mpz_t n, mpz_t d, int b);

boolean QNumber_fix_int_min(QNumber *self);

boolean QNumber_is_int_big(const QNumber *self);

boolean QNumber_is_int_normal(const QNumber *self);

void QNumber_make_big(QNumber *self);

QNumber QNumber_operator_unaray_minus(const QNumber *r);

QNumber
QNumber_make_number_from_unsigned_long_long(unsigned long long n);

/*!
  \methodof QNumber
  \todo
*/
void QNumber_operator_self_plus_big(QNumber* self, const QNumber *r);

/*!
  \methodof QNumber
  \todo
*/
void QNumber_operator_self_plus(QNumber* self, const QNumber *r);

/*!
  \methodof QNumber
  \todo
*/
void QNumber_operator_self_minus_big(QNumber* self, const QNumber *r);

/*!
  \methodof QNumber
  \todo
*/
void QNumber_operator_self_minus(QNumber* self, const QNumber *r);

/*!
  \methodof QNumber
  \todo
*/
void QNumber_operator_self_mul_big(QNumber* self, const QNumber *r);

/*!
  \methodof QNumber
  \todo
*/
void QNumber_operator_self_mul(QNumber* self, const QNumber *r);

/*!
  \methodof QNumber
  \todo
*/
void QNumber_operator_self_div(QNumber* self, const QNumber * r);

boolean QNumber_operator_less_than_both_small(const QNumber *n,
                                              const QNumber *r);

boolean QNumber_operator_less_than_r_small(const QNumber *n, const QNumber *r);

boolean QNumber_operator_less_than_n_small(const QNumber *n, const QNumber *r);

boolean QNumber_operator_less_than_both_big(const QNumber *n,
                                            const QNumber *r);

boolean QNumber_operator_less_than(const QNumber *n, const QNumber *r);

boolean QNumber_operator_equals(const QNumber *n, const QNumber *r);

QNumber QNumber_inv(const QNumber *self);

void QNumber_self_neg(QNumber *self);

QNumber QNumber_neg(const QNumber *self);

QNumber QNumber_operator_plus(const QNumber *a, const QNumber *b);

QNumber QNumber_operator_minus(const QNumber *a, const QNumber *b);

QNumber QNumber_operator_mul(const QNumber *a, const QNumber *b);

QNumber QNumber_operator_div(const QNumber *a, const QNumber *b);

boolean QNumber_operator_more_than(const QNumber *a, const QNumber *b);

boolean QNumber_operator_equals(const QNumber *a, const QNumber *b);

boolean QNumber_operator_less_than_or_equals(const QNumber *a,
                                             const QNumber *b);

boolean QNumber_operator_more_than_or_equals(const QNumber *a,
                                             const QNumber *b);

boolean QNumber_operator_not_equal(const QNumber *a, const QNumber *b);

QNumber QNumber_abs(const QNumber *n);

int QNumber_sgn(const QNumber *n);

int QNumber_cmp(const QNumber *a, const QNumber *b);

QNumber QNumber_gcd(const QNumber *a, const QNumber *b);

long   QNumber_gcd_long(long a, long b);

void QNumber_self_addmul(QNumber *self,
                         const QNumber *a,
                         const QNumber *b);

boolean QNumber_self_to_int(const QNumber *self,
                            int* out_value);

QNumber QNumber_floor(const QNumber *self);

QNumber QNumber_get_num(const QNumber *self);

QNumber QNumber_get_den(const QNumber *self);

boolean QNumber_divides(const QNumber *self, const QNumber *other);

boolean QNumber_is_integer(const QNumber * self);

void QNumber_divmod(const QNumber *self,
                    const QNumber *other,
                    QNumber *q,
                    QNumber *r);

void QNumber_self_decompose(const QNumber *self);

boolean QNumber_decompose(const QNumber *self, QNumber *z, QNumber *q);

void QNumber_normalize(QNumber *self);

boolean QNumber_add_overflow(long* res, long lhs, long rhs);

boolean QNumber_sub_overflow(long* res, long lhs, long rhs);

boolean QNumber_mul_overflow(long *res, long lhs, long rhs);

boolean QNumber_div_overflow(long* res, long lhs, long rhs);

int QNumber_integer_from_string(char* str,
                                char* error,
                                int base,
                                QNumber* target);

char* QNumber_print_integer(const QNumber* n, int base);

void init_mpz_pool(mpz_t* mpz_pool, size_t nr_to_init);

void clean_mpz_pool(mpz_t* mpz_pool, size_t nr_to_clean);

#endif  /* __NUSMV_CORE_UTILS_BIGNUMBERS_NUMBERS_INT_H__ */

