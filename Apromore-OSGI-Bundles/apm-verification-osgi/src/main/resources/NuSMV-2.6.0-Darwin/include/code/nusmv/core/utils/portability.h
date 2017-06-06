/* ---------------------------------------------------------------------------

  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 2005 FBK-irst.

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

  For more information of NuSMV see <http://nusmv.fbk.eu>
  or email to <nusmv-users@fbk.eu>.
  Please report bugs to <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to <nusmv@fbk.eu>.

-----------------------------------------------------------------------------*/

/*!
  \author Roberto Cavada
  \brief External header of the portability package

  This module contains functions provided for portability
  reasons.

*/


#ifndef __NUSMV_CORE_UTILS_PORTABILITY_H__
#define __NUSMV_CORE_UTILS_PORTABILITY_H__

#include <limits.h> /* for ULLONG_MAX and LLONG_MAX */

#if HAVE_CONFIG_H
#include "nusmv-config.h"
#endif

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

#if !NUSMV_HAVE_MALLOC
# undef malloc
# undef realloc
# if NUSMV_HAVE_MALLOC_H
#  include <malloc.h>
# elif NUSMV_HAVE_STDLIB_H
#  include <stdlib.h>
# endif

# ifndef malloc
void* malloc(size_t);
# endif /* ifndef malloc */

# ifndef realloc
void* realloc(void*, size_t);
# endif /* ifndef realloc */
#endif /* if not NUSMV_HAVE_MALLOC */

#if NUSMV_HAVE_ERRNO_H
#include <errno.h>
#else
/* extern definition for the errno variable */
#ifndef errno
extern int errno;
#endif

#ifndef ERANGE
/* Result too large */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ERANGE 34
#endif

#ifndef EINVAL
/* Result invalid */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define EINVAL 22
#endif

#endif

/* some specific macros for Visual Studio */
#if defined(_MSC_VER)
#define strncasecmp _strnicmp
#define strcasecmp _stricmp
#endif

#if defined(_MSC_VER)
#define __func__  __FUNCTION__
#endif

/* strtoull not available within MSVC */
#if ! NUSMV_HAVE_STRTOULL && defined(_MSC_VER)
# define strtoull                               \
  _strtoui64
#endif

/* for compilers which are not compliant with C99 but have "long long"
   ULLONG_MAX/LLONG_MAX/LLONG_MIN may be not defined. Note that "long
   long" has to be supported as it is used, e.g., in WordNumber class.
*/
#ifndef ULLONG_MAX
/* this solution should be safe as unsigned cast is done
"by repeatedly adding or subtracting one more than the maximum value
that can be represented in the new type until the value is in the
range of the new type". I.e. the max value of unsigned long long.
Another possibility is (~0ULL) */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ULLONG_MAX ((unsigned long long) -1)
#endif

#ifndef LLONG_MAX

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LLONG_MAX ((long long)(ULLONG_MAX >> 1))
#endif

#ifndef LLONG_MIN
/* probably this is not a portable definition. Expert opinion required. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LLONG_MIN (- LLONG_MAX - 1)
#endif

/*---------------------------------------------------------------------------*/
/* Structure definitions                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Functions declarations                                                    */
/*---------------------------------------------------------------------------*/



#endif /* ifndef __NUSMV_CORE_UTILS_PORTABILITY_H__ */
