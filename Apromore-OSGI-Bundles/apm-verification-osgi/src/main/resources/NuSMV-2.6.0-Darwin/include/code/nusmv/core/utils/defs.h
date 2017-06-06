/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 2007 by FBK-irst.

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
  \brief Some low-level definitions

  Some low-level definitions

*/


#ifndef __NUSMV_CORE_UTILS_DEFS_H__
#define __NUSMV_CORE_UTILS_DEFS_H__

#if HAVE_CONFIG_H
#  include "nusmv-config.h"
#endif

#if NUSMV_HAVE_STDLIB_H
#  include <stdlib.h>
#endif

#include <assert.h>
#include "cudd/util.h"

#  ifndef NUSMV_GCC_WARN_UNUSED_RESULT
#    define NUSMV_GCC_WARN_UNUSED_RESULT
#  endif


#ifdef EXTERN
# ifndef HAVE_EXTERN_ARGS_MACROS
    /* EXTERN is supposed to be no longer used if not explicitly required */
#   undef EXTERN
# endif
#endif


#ifdef ARGS
# ifndef HAVE_EXTERN_ARGS_MACROS
    /* ARGS is supposed to be no longer used if not explicitly required */
#   undef ARGS
# endif
#endif


/* These are potential duplicates. */
#if HAVE_EXTERN_ARGS_MACROS
# ifndef EXTERN
#   ifdef __cplusplus
#	define EXTERN extern "C"
#   else
#	define EXTERN extern
#   endif
# endif
# ifndef ARGS
#   if defined(__STDC__) || defined(__cplusplus) || defined(_MSC_VER)
#	define ARGS(protos)	protos		/* ANSI C */
#   else /* !(__STDC__ || __cplusplus || defined(_MSC_VER)) */
#	define ARGS(protos)	()		/* K&R C */
#   endif /* !(__STDC__ || __cplusplus || defined(_MSC_VER)) */
# endif
#endif

#ifndef NORETURN
#   if defined __GNUC__
#       define NORETURN __attribute__ ((__noreturn__))
#   else
#       define NORETURN
#   endif
#endif

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define nusmv_assert(expr) \
    assert(expr)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NIL_PTR(ptr_type)       \
    ((ptr_type) NULL)


/* use whenever you assign an integer to or from a pointer */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef util_ptrint  nusmv_ptrint;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef util_ptruint nusmv_ptruint;



/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

#ifndef max

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define max(_a_, _b_) ((_a_ < _b_) ? _b_ : _a_)
#endif

#ifndef min

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define min(_a_, _b_) ((_a_ < _b_) ? _a_ : _b_)
#endif

/*!
  \brief Casts the given pointer (address) to an int


*/
#define PTR_TO_INT(x) \
         ((int) (nusmv_ptrint) (x))

/*!
  \brief Casts the given int to the given pointer type


*/
#define PTR_FROM_INT(ptr, x) \
         ((ptr) (nusmv_ptrint) (x))

/*!
  \brief Casts the given int to void *


*/
#define VOIDPTR_FROM_INT(x) \
         ((void *) (nusmv_ptrint) (x))


/* These are used to "stringize" x after its macro-evaluation: */
/* WARNING: these macro are not safe. If the macro x contains sequences
   corresponding to an element of the build triplet (architecture-vendor-os)
   they will be expanded to 1, because they are internal macro of
   cpp. Examples os such string are:
   i386, linux, unix.
   See issue 2838. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MACRO_STRINGIZE_2nd_LEVEL(x) \
   #x

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MACRO_STRINGIZE(x) \
    MACRO_STRINGIZE_2nd_LEVEL(x)

/*!
  \brief This is a portable prefix to print size_t valus with printf

  Use this prefix when printinf size_t values with printf.
  Warning! This macro is not prefixed with '%'
*/

#if !NUSMV_HAVE_INTTYPES_H
# if (defined __MINGW32__) || (defined __CYGWIN__)
#  ifdef _WIN64
#   define PRIuPTR "I64u"
#   define PRIdPTR "I64d"
#  else
#   define PRIuPTR "u"
#   define PRIdPTR "d"
#  endif
# else
#  if __WORDSIZE == 64
#   define PRIuPTR "lu"
#   define PRIdPTR "ld"
#  else
#   define PRIuPTR "u"
#   define PRIdPTR "d"
#  endif
# endif
#else
# include <inttypes.h>
# endif /* NUSMV_HAVE_INTTYPES_H */

/*!
  \brief Fallback definition of PRI...MAX in case of missing
  <inttypes.h>. To be used as format specifier when printing unsigned long long
  int

  \todo Missing description

  \sa Documentation of <inttypes.h>
*/

#if !NUSMV_HAVE_INTTYPES_H
# if (defined __MINGW32__) || (defined __CYGWIN__)
#  ifdef _WIN64
#   define PRIuMAX "I64u"
#   define PRIdMAX "I64d"
#   define PRIoMAX "I64o"
#   define PRIXMAX "I64X"
#  else
#   define PRIuMAX "lu"
#   define PRIdMAX "ld"
#   define PRIoMAX "lo"
#   define PRIXMAX "lX"
#  endif
# else
#  if __WORDSIZE == 64
#   define PRIuMAX "llu"
#   define PRIdMAX "lld"
#   define PRIoMAX "llo"
#   define PRIXMAX "llX"
#  else
#   define PRIuMAX "lu"
#   define PRIdMAX "ld"
#   define PRIoMAX "lo"
#   define PRIXMAX "lX"
#  endif
# endif
#else
# include <inttypes.h>
# endif /* NUSMV_HAVE_INTTYPES_H */


#if NUSMV_HAVE_SRANDOM
#  if NUSMV_HAVE_GETPID
#    define utils_random_set_seed() \
       srandom((unsigned int)getpid())
#  else
#include <time.h>
#    define utils_random_set_seed() \
       srandom((unsigned int)time(NULL))
#  endif
#else
#  if NUSMV_HAVE_GETPID
#    define utils_random_set_seed() \
       srand((unsigned int)getpid())
#  else
#include <time.h>
#    define utils_random_set_seed() \
       srand((unsigned int)time(NULL))
#  endif
#endif

#if NUSMV_HAVE_RANDOM
#  define utils_random() \
    random()
#else
#  define utils_random() \
    rand()
#endif


#if NUSMV_HAVE_STDBOOL_H
#include <stdbool.h>

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef bool boolean;
#else
#ifdef __cplusplus
typedef bool boolean;
#else
typedef enum {false=0, true=1} boolean;
#endif
#endif

/* MD If OUTCOME_SUCCESS would have the value 0 it would be consistent with
   the usual boolean representation */
typedef enum Outcome_TAG
{
  OUTCOME_GENERIC_ERROR,
  OUTCOME_PARSER_ERROR,
  OUTCOME_SYNTAX_ERROR,
  OUTCOME_FILE_ERROR,
  OUTCOME_SUCCESS_REQUIRED_HELP,
  OUTCOME_SUCCESS
} Outcome;

/* to avoid warnings */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define UNUSED_PARAM(x) (void)(x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define UNUSED_VAR(x) (void)(x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENUM_CHECK(value, first, last) \
  nusmv_assert(first < value && value < last)

/* useful placeholders ********************************************************/
/*!
  \brief for switch cases without break
*/
#define FALLTHROUGH

/* for functions calling ErrorMgr_nusmv_exit or calling another function
   calling it and not catching it */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define THROWS_EXCEPTION

/* for underling the use of comma operator */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define COMMA_OPERATOR ,

 /* type for comparison function */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef int (*PFIVPVP)(const void*, const void*);

/* Generic void function */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void* (*PFVPVPVP)(void*, void*);

#endif /* __NUSMV_CORE_UTILS_DEFS_H__ */
