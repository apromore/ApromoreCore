/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 1998-2001 by CMU and FBK-irst.

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
  \author Originated from glu library of VIS
  \brief The header of the generic array manipulator.

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_UTILS_ARRAY_H__

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define __NUSMV_CORE_UTILS_ARRAY_H__

#if HAVE_CONFIG_H
#  include "nusmv-config.h"
#endif

/* Return value when memory allocation fails */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ARRAY_OUT_OF_MEM -10000


typedef struct array_t {
  char *space;
  int num;   /* number of array elements.            */
  int n_size;        /* size of 'data' array (in objects)    */
  int obj_size;      /* size of each array object.           */
  int index;         /* combined index and locking flag.     */
  unsigned int e_index;
  int e_insert;
} array_t;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
array_t *array_do_alloc(int, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
array_t *array_dup(array_t *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
array_t *array_join(array_t *, array_t *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void array_free(array_t *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int array_append(array_t *, array_t *);

/*!
  \brief Sorts the array content according to the given
comparison function

  IMPORTANT!  compare has argument int (void* pa, void* pb)
pa and pb must be dereferenced to access the corresponding values into
the array
*/
void array_sort(array_t *, int (*)(const void*, const void*));
/* void array_uniq(array_t *, int (*)(), void (*)()); */

/*!
  \brief


*/
void array_uniq(array_t* array,
                int (*compare)(char**, char**),
                void (*free_func)(char*));

/*!
  \brief


*/
int array_abort(const array_t *, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int array_resize(array_t *, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *array_do_data(array_t *);

/* allocates an array of 'number' elements of the type 'type' */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define array_alloc(type, number)               \
    array_do_alloc(sizeof(type), number)

/*!
  \brief

  Documentation needed!
*/
#define array_insert(type, a, i, datum)                                 \
  (  -(a)->index != sizeof(type) ? array_abort((a),4) : 0,              \
     (a)->index = (i),                                                  \
     (a)->index < 0 ? array_abort((a),0) : 0,                           \
     (a)->index >= (a)->n_size ?                                        \
     ((array_t*)a)->e_insert = array_resize(a, (a)->index + 1) : 0, \
     (a)->e_insert != ARRAY_OUT_OF_MEM ?                            \
     *((type *) ((a)->space + (a)->index * (a)->obj_size)) = datum : datum, \
     (a)->e_insert != ARRAY_OUT_OF_MEM ?                            \
     ((a)->index >= (a)->num ? (a)->num = (a)->index + 1 : 0) : 0,      \
     (a)->e_insert != ARRAY_OUT_OF_MEM ?                            \
     ((a)->index = -(int)sizeof(type)) : ARRAY_OUT_OF_MEM )

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define array_insert_last(type, array, datum)   \
    array_insert(type, array, (array)->num, datum)

/* added an assert to catch an eventual conversion of a->num (int) to unsigned
   int */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define array_fetch(type, a, i)                                         \
  (((array_t*)a)->e_index = (i),                                    \
    nusmv_assert((a)->num >= 0),                                    \
   (((a)->e_index) >= (a)->num) ? array_abort((a),1) : 0,            \
   *((type *) ((a)->space + (a)->e_index * (a)->obj_size)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define array_fetch_p(type, a, i)                                       \
  (((array_t*)a)->e_index = (i),                                              \
   ((a)->e_index >= (a)->num) ? array_abort((a),1) : 0,             \
   ((type *) ((a)->space + (a)->e_index * (a)->obj_size)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define array_fetch_last(type, array)           \
    array_fetch(type, array, ((array)->num)-1)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define array_fetch_last_p(type, array)         \
    array_fetch_p(type, array, ((array)->num)-1)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define array_n(array)                          \
    (array)->num

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define array_data(type, array)                 \
    (type *) array_do_data(array)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define arrayForEachItem(                                      \
  type,  /* type of object stored in array */                  \
  array, /* array to iterate */                                \
  i,     /* int, local variable for iterator */                \
  data   /* object of type */                                  \
)                                                              \
  for((i) = 0;                                                 \
      (((i) < array_n((array)))                                \
       && (((data) = array_fetch(type, (array), (i))), 1));    \
      (i)++)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define arrayForEachItemP(                                     \
  type,  /* type of object stored in array */                  \
  array, /* array to iterate */                                \
  i,     /* int, local variable for iterator */                \
  pdata  /* pointer to object of type */                       \
)                                                              \
  for((i) = 0;                                                 \
      (((i) < array_n((array)))                                \
       && (((pdata) = array_fetch_p(type, (array), (i))), 1));  \
      (i)++)

#endif
