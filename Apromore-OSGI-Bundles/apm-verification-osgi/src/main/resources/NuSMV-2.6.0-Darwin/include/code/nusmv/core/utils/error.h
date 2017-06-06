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
  \author Marco Roveri
  \brief header for the error.c file

  header for the error.c file.

*/


#ifndef __NUSMV_CORE_UTILS_ERROR_H__
#define __NUSMV_CORE_UTILS_ERROR_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include <stdio.h>
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/portability.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/NodeList.h"

/* warning [MD] Why Prop.h is included here? */
#include "nusmv/core/prop/Prop.h"


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/* Define the alternative for nusmv_assert(0) */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define error_unreachable_code()                                        \
  do {                                                                  \
    fprintf(stderr, "%s:%d:%s: reached invalid code\n",                 \
            __FILE__, __LINE__, __func__);                              \
    exit(4);                                                            \
  } while (0)

/* Define the alternative for nusmv_assert(0 && "message") */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define error_unreachable_code_msg(...)                                 \
  do {                                                                  \
    printf(__VA_ARGS__);                                                \
    fprintf(stderr, "%s:%d:%s: reached invalid code\n",                 \
            __FILE__, __LINE__, __func__);                              \
    exit(4);                                                            \
  } while (0)

/*!
  \brief Checks if the return value of a snprintf call is
                compatible with the size of the buffer passed as first
                argument to the snprintf function call

  Checks if the return value of a snprintf call is
                compatible with the size of the buffer passed as first
                argument to the snprintf function call. An internal
                error is thrown if a buffer overflow is found.

                An example of use:

                  char buf[40];
                  int chars = snprintf(buf, 40, "hello world");
                  SNPRINTF_CHECK(chars, 40);

                WARNING: do no use this macro if you are using snprintf to
                actually truncate a string. The macro will always abort,
                because the return value of snprintf is always the length of
                the original string. See man snprintf.
                  

  \sa snprintf
*/
#define SNPRINTF_CHECK(chars, buffsize)                         \
  do {                                                          \
    if (chars < 0) {                                            \
      fprintf(stderr, "%s:%d:%s: Error in buffer writing",      \
              __FILE__, __LINE__, __func__);                    \
      exit(5);                                                  \
    }                                                           \
    else if ((unsigned int)chars >= buffsize) {                 \
      fprintf(stderr, "%s:%d:%s: String buffer overflow",       \
              __FILE__, __LINE__, __func__);                    \
      exit(5);                                                  \
    }                                                           \
    else {};                                                    \
  } while (0)


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Initializes the errro manager within the
                       given environment

  Initializes the errro manager within the
                       given environment. Conflicts if another
                       instance is registered with key
                       ENV_ERROR_MANAGER
*/
void Error_init(NuSMVEnv_ptr env);

/*!
  \brief Deinitializes the errro manager within the
                       given environment

  Deinitializes the errro manager within the
                       given environment
*/
void Error_quit(NuSMVEnv_ptr env);

/*!
  \brief Initializes the memory routines.

  This function deals with the memory routines
   taken form the CUDD. It initializes the pointer to function
   <tt>MMoutOfMemory</tt> which is used by the memory allocation
   functions when the <tt>USE_MM</tt> macro is not defined (the
   default). This pointer specifies the function to call when the
   allocation routines fails to allocate memory.
*/
void init_memory(void);


#endif /* __NUSMV_CORE_UTILS_ERROR_H__ */
