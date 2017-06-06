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
  \author Sergio Mover
  \brief The file contains the macros used to wrap the call to a
  watchdog library.

  The watchdog library implements functions that set/remove timeouts
  that will stop the NuSMV execution when they expire.
*/

#ifndef __NUSMV_CORE_UTILS_WATCHDOG_UTIL_H__
#define __NUSMV_CORE_UTILS_WATCHDOG_UTIL_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include "nusmv/core/cinit/NuSMVEnv.h"
#include "nusmv/core/utils/error.h"

#if NUSMV_HAVE_WATCHDOG_LIBRARY
#include "watchdog.h"
#endif

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/
static NuSMVEnv_ptr watchdog_env;


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief Initialize the watchdog "name" in the watchdog library.

  The macro intializes the watchdog library.
  The macro takes as input a NuSMVEnv_ptr pointer, the name of the
  watchdog (const char*), the period of the watchdog (the timeout?),
  the action performed when the period expires (?), and the options
  used to set the timeout (?)

  If the library is not linked the macro will do nothing.
*/
#if NUSMV_HAVE_WATCHDOG_LIBRARY
#define WATCHDOG_INIT(env, name, period, action, options) \
  watchdog_mod(name, period, action, options, env)
#else
#define WATCHDOG_INIT(env, name, period, action, options)
#endif

/*!
  \brief Starts the watchdog named "name"

  The macro starts the decrement the watchdog named "name".
  The macro takes as input the current environment (NuSMVEnv_ptr) and
  the name of the watchdog (const char*).
  The watchdog has to be already declared with the function
  WATCHDOG_INIT.

  If the library is not linked the macro will do nothing.
*/
#if NUSMV_HAVE_WATCHDOG_LIBRARY
#define WATCHDOG_START(env, name) \
  watchdog_start(name)
#else
#define WATCHDOG_START(env, name)
#endif

/*!
  \brief Pause the watchdog named "name"

  The macro pause the decrement the watchdog named "name".
  The macro takes as input the current environment (NuSMVEnv_ptr) and
  the name of the watchdog (const char*).
  The watchdog has to be already declared with the function
  WATCHDOG_INIT.

  If the library is not linked the macro will do nothing.
*/
#if NUSMV_HAVE_WATCHDOG_LIBRARY
#define WATCHDOG_PAUSE(env, name) \
  watchdog_pause(name)
#else
#define WATCHDOG_PAUSE(env, name)
#endif

/*!
  \brief Returns the status of the watchdog

  Returns the macro of the watchdog.
  env is a NuSMVENV_ptr, name is the name of the watchdog (char*) and
  status is a pointer to a boolean variable (bool* status).

  The macro sets status to true if the watchdog is enabled,
  and to false otherwise.
  The macro returns -1 if the watchdog with the given name does not
  exists while returns a positive number (unsigned long long int) that
  contains the current value of the watchdog's timer.

  The function to retrieve the status does not exist in the
  watchdog library.

  If the library is not linked the macro returns -1.
*/
#if NUSMV_HAVE_WATCHDOG_LIBRARY
#define WATCHDOG_GET_STATUS(env, name, status)  \
  -1
#else
#define WATCHDOG_GET_STATUS(env, name, status) -1
#endif

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Callback function when the watchdog expires

  Callback function when the watchdog expires.
*/
void watchdog_action(void* env);




/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_UTILS_WATCHDOG_UTIL_H__ */
