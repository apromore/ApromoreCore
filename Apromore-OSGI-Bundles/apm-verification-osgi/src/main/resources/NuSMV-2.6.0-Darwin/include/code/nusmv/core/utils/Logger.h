/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 2011 by FBK-irst.

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
  \author Alessandro Mariotti
  \brief Public interface of class 'Logger'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_LOGGER_H__
#define __NUSMV_CORE_UTILS_LOGGER_H__

#include "nusmv/core/cinit/NuSMVEnv.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/printers/MasterPrinter.h"
#include "nusmv/core/utils/OStream.h"
#include "nusmv/core/opt/OptsHandler.h"

/*!
  \struct Logger
  \brief Definition of the public accessor for class Logger

  
*/
typedef struct Logger_TAG*  Logger_ptr;

/*!
  \brief To cast and check instances of class Logger

  These macros must be used respectively to cast and to check
  instances of class Logger
*/
#define LOGGER(self) \
         ((Logger_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LOGGER_CHECK_INSTANCE(self) \
         (nusmv_assert(LOGGER(self) != LOGGER(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LOGGER_ERROR_VL 0

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LOGGER_WARN_VL 1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LOGGER_INFO_VL 2

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LOGGER_DEBUG_VL 3

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LOGGER_TRACE_VL 100


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void Logger_init(NuSMVEnv_ptr env);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void Logger_quit(NuSMVEnv_ptr env);

/*!
  \methodof Logger
  \brief The Logger class constructor

  The Logger class constructor

  \sa Logger_destroy
*/
Logger_ptr Logger_create(FILE* stream);

/*!
  \methodof Logger
  \brief The Logger class destructor

  The Logger class destructor

  \sa Logger_create
*/
void Logger_destroy(Logger_ptr self);

/*!
  \methodof Logger
  \brief Logs the given format string with the given parameters

  Logs the given format string with the given parameters

  \sa Logger_nlog
*/
void Logger_log(const Logger_ptr self,
                       const char* format, ...);

/*!
  \methodof Logger
  \brief Logs the given format string with the given parameters

  Logs the given format string with the given parameters,
  keeping in account the verbose level

  \sa Logger_nlog
*/
void Logger_vlog(const Logger_ptr self,
                        OptsHandler_ptr opts,
                        const int verbose_level,
                        const char* format, ...);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Logger_vlog_error(self, opts, format, ...) \
  Logger_vlog(self, opts, LOGGER_ERROR_VL, format, __VA_ARGS__)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Logger_vlog_warn(self, opts, format, ...) \
  Logger_vlog(self, opts, LOGGER_WARN_VL, format, __VA_ARGS__)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Logger_vlog_info(self, opts, format, ...) \
  Logger_vlog(self, opts, LOGGER_INFO_VL, format, __VA_ARGS__)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Logger_vlog_debug(self, opts, format, ...) \
  Logger_vlog(self, opts, LOGGER_DEBUG_VL, format, __VA_ARGS__)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Logger_vlog_trace(self, opts, format, ...) \
  Logger_vlog(self, opts, LOGGER_TRACE_VL, format, __VA_ARGS__)

/*!
  \methodof Logger
  \brief Logs the given format string with the given parameters

  Logs the given format string with the given parameters.
                      Supports node printing (using '%N')

  \sa Logger_log, UtilsIO_node_vfprintf
*/
void Logger_nlog(const Logger_ptr self,
                        const MasterPrinter_ptr node_printer,
                        const char* format, ...);

/*!
  \methodof Logger
  \brief Logs the given format string with the given parameters

  Logs the given format string with the given parameters.
                      Supports node printing (using '%N'), keeping in account
                      the verbose level

  \sa Logger_log, UtilsIO_node_vfprintf
*/
void Logger_vnlog(const Logger_ptr self,
                        const MasterPrinter_ptr node_printer,
                         OptsHandler_ptr opts,
                         const int verbose_level,
                         const char* format, ...);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Logger_vnlog_error(self, wffprint, opts, format, ...) \
  Logger_vlog(self, opts, LOGGER_ERROR_VL, "%s: ", __func__);               \
  Logger_vnlog(self, wffprint, opts, LOGGER_ERROR_VL, format, __VA_ARGS__)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Logger_vnlog_warn(self, wffprint, opts, format, ...) \
  Logger_vlog(self, opts, LOGGER_WARN_VL, "%s: ", __func__);               \
  Logger_vnlog(self, wffprint, opts, LOGGER_WARN_VL, format, __VA_ARGS__)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Logger_vnlog_info(self, wffprint, opts, format, ...) \
  Logger_vlog(self, opts, LOGGER_INFO_VL, "%s: ", __func__);               \
  Logger_vnlog(self, wffprint, opts, LOGGER_INFO_VL, format, __VA_ARGS__)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Logger_vnlog_debug(self, wffprint, opts, format, ...) \
  Logger_vlog(self, opts, LOGGER_DEBUG_VL, "%s: ", __func__);               \
  Logger_vnlog(self, wffprint, opts, LOGGER_DEBUG_VL, format, __VA_ARGS__)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Logger_vnlog_trace(self, wffprint, opts, format, ...) \
  Logger_vlog(self, opts, LOGGER_TRACE_VL, "%s: ", __func__);               \
  Logger_vnlog(self, wffprint, opts, LOGGER_TRACE_VL, format, __VA_ARGS__)

/*!
  \methodof Logger
  \brief Returns the stream used by the given logger instance

  Returns the stream used by Logger_nlog and Logger_log

  \sa Logger_log, Logger_nlog
*/
FILE* Logger_get_stream(const Logger_ptr self);

/*!
  \methodof Logger
  \brief Returns the internal OStream instance.

  Returns the internal OStream instance,
                      which should not be changed by the caller
*/
OStream_ptr Logger_get_ostream(const Logger_ptr self);

/*!
  \methodof Logger
  \brief Increments the indentation of the logger

  Increments the indentation of the logger
*/
void Logger_inc_indent_size(Logger_ptr self);

/*!
  \methodof Logger
  \brief Decrements the indentation of the logger

  Decrements the indentation of the logger
*/
void Logger_dec_indent_size(Logger_ptr self);

/*!
  \methodof Logger
  \brief Returns the indentation of the logger

  Returns the indentation of the logger
*/
int Logger_get_indent_size(const Logger_ptr self);

/*!
  \methodof Logger
  \brief Resets the indentation of the logger

  Resets the indentation of the logger
*/
void Logger_reset_indent_size(Logger_ptr self);

/*!
  \methodof Logger
  \brief Sets the indentation of the logger

  Sets the indentation of the logger
*/
void Logger_set_indent_size(Logger_ptr self, int n);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_LOGGER_H__ */
