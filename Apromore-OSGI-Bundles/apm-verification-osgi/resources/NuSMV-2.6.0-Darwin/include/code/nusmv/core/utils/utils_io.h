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
  \brief Header for the utils_io.c file.

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_UTILS_UTILS_IO_H__
#define __NUSMV_CORE_UTILS_UTILS_IO_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/prop/Prop.h"
#include "nusmv/core/node/printers/MasterPrinter.h"
#include "nusmv/core/utils/error.h"
#include <stdarg.h>

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

enum var_type {
  VAR_NONE = 0,
  VAR_INT = 1,
  VAR_CHAR = VAR_INT << 1,
  VAR_STRING = VAR_INT << 2,
  VAR_POINTER = VAR_INT << 3,
  VAR_DOUBLE = VAR_INT << 4
};

enum var_modifier {
  MOD_NONE = VAR_INT << 6,
  MOD_SIZE_T = VAR_INT << 7,
  MOD_SHORT = VAR_INT << 8,
  MOD_SHORT_SHORT = VAR_INT << 9,
  MOD_LONG = VAR_INT << 10,
  MOD_LONG_LONG = VAR_INT << 11,
  MOD_LONG_DOUBLE = VAR_INT << 12
};

/*!
  \brief This functions takes a string that begins with a '%' and
               returns the type of the expected value associated with
               the given printf format.

  This functions takes a string that begins with a '%' and
               returns the type of the expected value associated with
               the given printf format. Does not support '%%' and
               '%N', which should be preprocessed by the
               caller. Returns the number of characters read, which is
               uqual to the format length (e.g. '%.2f' will return 4)
*/
int UtilsIO_get_param_len(const char* fmt,
                                 enum var_type* type,
                                 enum var_modifier* mod);

/*!
  \brief NuSMV custom fprintf function.

  NuSMV custom fprintf function. This function
                      works identically as the stdio snprintf
                      function, but handles node_ptr arguments in the
                      "fmt" parameter, using the '%N' format. The
                      given MasterPrinter will be used to convert the
                      node to a printable string.

  \sa fprintf
*/
int UtilsIO_node_fprintf(const MasterPrinter_ptr printer,
                                FILE* output, const char* fmt, ...);

/*!
  \brief NuSMV custom vfprintf function.

  NuSMV custom vfprintf function. This function
                      works identically as the stdio snprintf
                      function, but handles node_ptr arguments in the
                      "fmt" parameter, using the '%N' format. The
                      given MasterPrinter will be used to convert the
                      node to a printable string.

  \sa vfprintf
*/
int UtilsIO_node_vfprintf(const MasterPrinter_ptr printer,
                                 FILE* output, const char* fmt,
                                 va_list args);

/*!
  \brief NuSMV custom vsnprintf function.

  NuSMV custom vsnprintf function. This function
                      works identically as the stdio snprintf
                      function, but handles node_ptr arguments in the
                      "fmt" parameter, using the '%N' format. The
                      given MasterPrinter will be used to convert the
                      node to a printable string.

  \sa vsnprintf
*/
int UtilsIO_node_vsnprintf(const MasterPrinter_ptr printer,
                                  char* output, size_t size,
                                  const char* fmt, va_list args);

/*!
  \brief NuSMV custom snprintf function.

  NuSMV custom snprintf function. This function
                      works identically as the stdio snprintf
                      function, but handles node_ptr arguments in the
                      "fmt" parameter, using the '%N' format. The
                      given MasterPrinter will be used to convert the
                      node to a printable string.

  \sa snprintf
*/
int UtilsIO_node_snprintf(const MasterPrinter_ptr printer,
                                 char* output, size_t size,
                                 const char* fmt, ...);

#endif /* __NUSMV_CORE_UTILS_UTILS_IO_H__ */
