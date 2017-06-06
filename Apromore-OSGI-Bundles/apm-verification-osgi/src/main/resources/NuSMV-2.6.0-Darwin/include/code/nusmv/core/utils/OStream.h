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
  \brief Public interface of class 'OStream'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_OSTREAM_H__
#define __NUSMV_CORE_UTILS_OSTREAM_H__


#include <stdarg.h>
#include "nusmv/core/node/printers/MasterPrinter.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct OStream
  \brief Definition of the public accessor for class OStream

  
*/
typedef struct OStream_TAG*  OStream_ptr;

/*!
  \brief To cast and check instances of class OStream

  These macros must be used respectively to cast and to check
  instances of class OStream
*/
#define OSTREAM(self) \
         ((OStream_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OSTREAM_CHECK_INSTANCE(self) \
         (nusmv_assert(OSTREAM(self) != OSTREAM(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof OStream
  \brief The OStream class constructor

  The OStream class constructor

  \sa OStream_destroy
*/
OStream_ptr OStream_create(FILE* stream);

/*!
  \methodof OStream
  \brief The OStream class constructor

  The OStream class constructor. Opens a new FILE* instance
                      from the given filename. If append is false,
                      flag "w" is used, flag "a" is used otherwise

  \sa OStream_destroy
*/
OStream_ptr OStream_create_file(const char* fname, boolean append);

/*!
  \methodof OStream
  \brief The OStream class copier

  Internal FILE is referenced, the returned copy MUST be
  destroyed with destroy_safe

  \sa OStream_create
*/
OStream_ptr OStream_copy(OStream_ptr self);

/*!
  \methodof OStream
  \brief The OStream class destructor

  The OStream class destructor. If the internal FILE* stream
                      is not stdout or stderr, the stream is closed

  \sa OStream_create
*/
void OStream_destroy(OStream_ptr self);

/*!
  \methodof OStream
  \brief The OStream class destructor

  The OStream class destructor. The internal
                      stream is NOT closed

  \sa OStream_create
*/
void OStream_destroy_safe(OStream_ptr self);


/* Getters and Setters ********************************************************/

/*!
  \methodof OStream
  \brief Getter for the internal FILE* instance

  Getter for the internal FILE* instance

  \sa OStream_set_stream
*/
FILE* OStream_get_stream(const OStream_ptr self);

/*!
  \methodof OStream
  \brief Prints the given format string with the given parameters

  Prints the given format string with the given parameters

  \sa OStream_nprintf
*/
void OStream_printf(const OStream_ptr self,
                           const char* format, ...);

/*!
  \methodof OStream
  \brief Prints the given format string with the given parameters

  Prints the given format string with the given parameters.
                      Supports node printing (using '%N')
*/
void OStream_nprintf(const OStream_ptr self,
                            const MasterPrinter_ptr node_printer,
                            const char* format, ...);

/*!
  \methodof OStream
  \brief Prints the given format string with the given parameters

  Prints the given format string with the given parameters

  \sa OStream_nprintf
*/
void OStream_vprintf(const OStream_ptr self,
                            const char* format, va_list args);

/*!
  \methodof OStream
  \brief Prints the given format string with the given parameters

  Prints the given format string with the given parameters.
                      Supports node printing (using '%N')
*/
void OStream_nvprintf(const OStream_ptr self,
                             const MasterPrinter_ptr node_printer,
                             const char* format, va_list args);

/*!
  \methodof OStream
  \brief Flushes the ostream

  Flushes the ostream
*/
void OStream_flush(const OStream_ptr self);

/*!
  \methodof OStream
  \brief Increments the indentation of the ostream

  Increments the indentation of the ostream
*/
void OStream_inc_indent_size(OStream_ptr self);

/*!
  \methodof OStream
  \brief Decrements the indentation of the ostream

  Decrements the indentation of the ostream
*/
void OStream_dec_indent_size(OStream_ptr self);

/*!
  \methodof OStream
  \brief Returns the indentation of the ostream

  Returns the indentation of the ostream
*/
int OStream_get_indent_size(const OStream_ptr self);

/*!
  \methodof OStream
  \brief Resets the indentation of the ostream

  Resets the indentation of the ostream
*/
void OStream_reset_indent_size(OStream_ptr self);

/*!
  \methodof OStream
  \brief Sets the indentation of the ostream

  Sets the indentation of the ostream
*/
void OStream_set_indent_size(OStream_ptr self, int n);

/*!
  \methodof OStream
  \brief Enables/disables newline splitting for indentation

  Enables/disables newline splitting for indentation
*/
void OStream_set_split_newline(OStream_ptr self, boolean enabled);

/*!
  \methodof OStream
  \brief Sets the stream on which the OStream prints

  Sets the stream on which the OStream prints

  \se Flushes the current stream. Closes it if
                      it is not stdout or stderr
*/
void OStream_set_stream(OStream_ptr self, FILE* stream);

/*!
  \methodof OStream
  \brief Set the stream to NULL

  Useful for avoiding OStream_set_stream to close the
  stream. The old stream is returned.
*/
FILE* OStream_reset_stream(OStream_ptr self);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_OSTREAM_H__ */
