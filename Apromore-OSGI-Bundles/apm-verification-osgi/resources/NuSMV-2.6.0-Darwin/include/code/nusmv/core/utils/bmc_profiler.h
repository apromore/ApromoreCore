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
  \brief Defines the macros used to wrap a profiler library for BMC problems.

  Defines a set of macros that wrap the calls to a profiler library
  for BMC problems.
  The profiler library allows to write on a file the statistics (time)
  took to solve an instance of the BMC problem.
*/

#ifndef __NUSMV_CORE_UTILS_BMC_PROFILER_H__
#define __NUSMV_CORE_UTILS_BMC_PROFILER_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include "nusmv/core/cinit/NuSMVEnv.h"
#include "nusmv/core/utils/error.h"

#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
#include "profiling.h"
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

#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
/*!
  \struct Structure used to store the internal state of the profiler

  The structure is used to know if the profiler is enabled and the name
  to the output file.
*/
typedef struct BmcProfilerStruct_TAG
{
  boolean enabled;
  char* out_file_name;
} BmcProfilerStruct;

typedef BmcProfilerStruct* BmcProfilerStruct_ptr;
#endif

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
/*!
  \brief Key used to retrieve the profiler of this environment
*/
#define ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__ "env_bmc_profiler"
#endif

/*!
  \brief Initialize the data structure used to keep the status of the
  profiler library (on the NuSMV side)

  If the library is not linked the macro does nothing.
*/
#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
#define BMC_PROFILER_INIT_ENV(env)                                      \
  nusmv_assert(! NuSMVEnv_has_value(env, ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__));            \
  {                                                                     \
     BmcProfilerStruct_ptr prof;                                        \
     prof = ALLOC(BmcProfilerStruct, 1);                                \
     nusmv_assert((BmcProfilerStruct_ptr) NULL != prof);                \
     prof->enabled = false;                                             \
     prof->out_file_name = (char*) NULL;                                \
     NuSMVEnv_set_value(env, ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__, prof);                   \
  }
#else
#define BMC_PROFILER_INIT_ENV(env)
#endif

/*!
  \brief Remove the data structure kept in the environment

  If the library is not linked the macro does nothing.
*/
#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
#define BMC_PROFILER_DEINIT_ENV(env)                                    \
  nusmv_assert(NuSMVEnv_has_value(env, ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__));              \
  {                                                                     \
    if (BMC_PROFILER_IS_ENABLED(env)) {BMC_PROFILER_DISABLE(env);}       \
    BmcProfilerStruct_ptr prof = (BmcProfilerStruct_ptr)                \
      NuSMVEnv_remove_value(env, ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__);                     \
    if ( (char*) NULL != prof->out_file_name) {FREE(prof->out_file_name);} \
  }
#else
#define BMC_PROFILER_DEINIT_ENV(env)
#endif

/*!
  \brief Check if the profiler is enabled

  The macro returns true iff the profiler is enabled.
  If the library is not linked the macro returns false.
*/
#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
#define BMC_PROFILER_IS_ENABLED(env)                                    \
  ((BmcProfilerStruct_ptr) NuSMVEnv_get_value(env, ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__))->enabled
#else
#define BMC_PROFILER_IS_ENABLED(env) false
#endif

/*!
  \brief Returns the string of the profiler file.

  Returns the string of the profiler file.
  If the library is not linked the macro returns false.
*/
#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
#define BMC_PROFILER_GET_OUT_FILE(env)                                  \
  ((BmcProfilerStruct_ptr) NuSMVEnv_get_value(env, ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__))->out_file_name
#else
#define BMC_PROFILER_GET_OUT_FILE(env) (char*) NULL
#endif


/*!
  \brief Initialize the BMC profiler library

  The macro intializes the BMC profiler library.
  If the library is not linked the macro will do nothing.
*/
#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
#define BMC_PROFILER_INITIALIZE(env)                                \
  if (BMC_PROFILER_IS_ENABLED(env)) {                               \
    profiler_initialize();                                          \
  }
#else
#define BMC_PROFILER_INITIALIZE(env)
#endif

/*!
  \brief Enable the profiling of the different calls to the sat solver
  during a BMC execution.

  The macro takes as input the environment where the BMC is executed
  and a name (const char*) of the file used to output the statistics.
  The file is rewritten if exists and it has to be closed calling
  BMC_PROFILER_DISABLE.

  If the library is not linked the macro will do nothing.
*/
#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
#define BMC_PROFILER_ENABLE(env, filename)                              \
  nusmv_assert(NuSMVEnv_has_value(env, ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__));              \
  {                                                                     \
    BmcProfilerStruct_ptr prof;                                         \
    prof = (BmcProfilerStruct_ptr) NuSMVEnv_get_value(env, ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__); \
    nusmv_assert(! prof->enabled);                                      \
    if ( (char*) NULL != prof->out_file_name) {                         \
      FREE(prof->out_file_name);                                        \
    }                                                                   \
    prof->enabled = true;                                               \
    prof->out_file_name = util_strsav(filename);                        \
    /* actual call to the library */                                    \
    profiler_enable(prof->out_file_name);                               \
  }
#else
#define BMC_PROFILER_ENABLE(env, file)
#endif

/*!
  \brief Disable the profiling of the BMC algorithms.

  The macro takes as input the environment where the BMC is executed.
  The macro has to be called only after calling BMC_PROFILER_ENABLE.
  Calling this macro will cause to close the current handle to the
  output file used for printing the profiling information.

  If the library is not linked the macro will do nothing.
*/
#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
#define BMC_PROFILER_DISABLE(env)                                       \
  nusmv_assert(NuSMVEnv_has_value(env, ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__));              \
  {                                                                     \
    BmcProfilerStruct_ptr prof;                                         \
    prof = (BmcProfilerStruct_ptr) NuSMVEnv_get_value(env, ENV__NUSMV_CORE_UTILS_BMC_PROFILER_H__); \
    nusmv_assert(prof->enabled);                                        \
    if ( (char*) NULL != prof->out_file_name) {                         \
      FREE(prof->out_file_name);                                        \
      prof->out_file_name = (char*) NULL;                               \
    }                                                                   \
    prof->enabled = false;                                              \
    /* actual call to the library */                                    \
    profiler_disable();                                                 \
  }
#else
#define BMC_PROFILER_DISABLE(env)
#endif

/*!
  \brief Log the beginning of a BMC sat check at depth bmc_step.

  Log the beginning of a BMC sat check at depth bmc_step.
  This will print in the profiling file a message that contains the
  information about the start of the bmc_step BMC sat check and the
  current timestamp.

  The macro takes as input the environment where the BMC is executed
  and the current step (a non-negative integer value).
  The macro has to be called only after calling BMC_PROFILER_ENABLE.

  If the library is not linked the macro will do nothing.
*/
#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
#define BMC_PROFILER_SAMPLING_SOLVE_START(env, bmc_step)                \
  if (BMC_PROFILER_IS_ENABLED(env)) {                                   \
    profiler_output_message("\n-- start of problem solving for bound", bmc_step); \
  }
#else
#define BMC_PROFILER_SAMPLING_SOLVE_START(env, bmc_step)
#endif

/*!
  \brief Log the end of a BMC sat check at depth bmc_step.

  Log the end of a BMC sat check at depth bmc_step.
  This will print in the profiling file a message that contains the
  information about the end of the bmc_step BMC sat check and the
  current timestamp.

  The macro takes as input the environment where the BMC is executed
  and the current step (a non-negative integer value).
  The macro has to be called only after calling BMC_PROFILER_ENABLE.

  If the library is not linked the macro will do nothing.
*/
#if NUSMV_HAVE__NUSMV_CORE_UTILS_BMC_PROFILER_H___LIBRARY
#define BMC_PROFILER_SAMPLING_SOLVE_END(env, bmc_step)                  \
  if (BMC_PROFILER_IS_ENABLED(env)) {                                   \
    profiler_output_message("\n-- end of problem solving for bound", bmc_step); \
    profiler_sampling(bmc_step);                                        \
  }
#else
#define BMC_PROFILER_SAMPLING_SOLVE_END(env, bmc_step)
#endif


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_UTILS_BMC_PROFILER_H__ */
