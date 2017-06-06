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
  \brief Public interface of class 'StreamMgr'

  The stream manager class holds the error, output and
               input stream and provides some functionalities to print
               or read from those streams

*/



#ifndef __NUSMV_CORE_UTILS_STREAM_MGR_H__
#define __NUSMV_CORE_UTILS_STREAM_MGR_H__


#include "nusmv/core/utils/utils.h"
#include "nusmv/core/cinit/NuSMVEnv.h"
#include "nusmv/core/node/printers/MasterPrinter.h"
#include "nusmv/core/utils/OStream.h"

/*!
  \struct StreamMgr
  \brief Definition of the public accessor for class StreamMgr

  
*/
typedef struct StreamMgr_TAG*  StreamMgr_ptr;

/*!
  \brief To cast and check instances of class StreamMgr

  These macros must be used respectively to cast and to check
  instances of class StreamMgr
*/
#define STREAM_MGR(self) \
         ((StreamMgr_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define STREAM_MGR_CHECK_INSTANCE(self) \
         (nusmv_assert(STREAM_MGR(self) != STREAM_MGR(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Initializes the stream manager structure within the
                      given environment

  Initializes the stream manager structure within the
                      given environment

                      Environment requisites:
                      - No instances registered with key ENV_STREAM_MANAGER
*/
void Stream_init(NuSMVEnv_ptr env);

/*!
  \brief 

  
*/
void Stream_quit(NuSMVEnv_ptr env);

/*!
  \methodof StreamMgr
  \brief The StreamMgr class constructor

  The StreamMgr class constructor

  \sa StreamMgr_destroy
*/
StreamMgr_ptr StreamMgr_create(void);

/*!
  \methodof StreamMgr
  \brief The StreamMgr class destructor

  The StreamMgr class destructor

  \sa StreamMgr_create
*/
void StreamMgr_destroy(StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Setter for the error stream

  Setter for the error stream
*/
void StreamMgr_set_error_stream(StreamMgr_ptr self, FILE* err);

/*!
  \methodof StreamMgr
  \brief Setter for the output stream

  Setter for the output stream
*/
void StreamMgr_set_output_stream(StreamMgr_ptr self, FILE* out);

/*!
  \methodof StreamMgr
  \brief Setter for the input stream

  Setter for the input stream
*/
void StreamMgr_set_input_stream(StreamMgr_ptr self, FILE* in);

/*!
  \methodof StreamMgr
  \brief Set the output stream to NULL

  Useful for avoiding OStream_set_stream to close the
  stream. The old stream is returned
*/
FILE* StreamMgr_reset_output_stream(StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Set the output stream to NULL

  Useful for avoiding OStream_set_stream to close the
  stream. The old stream is returned
*/
FILE* StreamMgr_reset_error_stream(StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Getter for the error stream

  Getter for the error stream
*/
FILE* StreamMgr_get_error_stream(const StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Getter for the output stream

  Getter for the output stream
*/
FILE* StreamMgr_get_output_stream(const StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Getter for the error internal OStream instance

  Getter for the error internal OStream instance.
                      Should not be changed from the caller
*/
OStream_ptr
StreamMgr_get_error_ostream(const StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Getter for the output internal OStream instance

  Getter for the output internal OStream instance.
                      Should not be changed from the caller
*/
OStream_ptr
StreamMgr_get_output_ostream(const StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Getter for the input stream

  Getter for the input stream
*/
FILE* StreamMgr_get_input_stream(const StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Prints on the output stream

  Prints on the output stream. If the stream is NULL,
                      does not print anything
*/
void StreamMgr_print_output(const StreamMgr_ptr self,
                                   const char* format, ...);

/*!
  \methodof StreamMgr
  \brief Prints on the output stream

  Prints on the output stream. If the stream is NULL,
                      does not print anything. This printing routine
                      supports node_ptr's too. Use format '%N' for nodes
*/
void StreamMgr_nprint_output(const StreamMgr_ptr self,
                                    const MasterPrinter_ptr printer,
                                    const char* format, ...);

/*!
  \methodof StreamMgr
  \brief Prints on the output stream

  Prints on the output stream. If the stream is NULL,
                      does not print anything
*/
void StreamMgr_print_error(const StreamMgr_ptr self,
                                  const char* format, ...);

/*!
  \methodof StreamMgr
  \brief Prints on the error stream

  Prints on the error stream. If the stream is NULL, does
                      not print anything. This printing routine
                      supports node_ptr's too. Use format '%N' for nodes
*/
void StreamMgr_nprint_error(const StreamMgr_ptr self,
                                   const MasterPrinter_ptr printer,
                                   const char* format, ...);

/*!
  \methodof StreamMgr
  \brief Prints on the output stream

  Prints on the output stream. If the stream is NULL,
                      does not print anything
*/
void StreamMgr_flush_streams(const StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Increments the indentation of the stream manager

  Increments the indentation of the stream manager
*/
void StreamMgr_inc_indent_size(StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Decrements the indentation of the stream manager

  Decrements the indentation of the stream manager
*/
void StreamMgr_dec_indent_size(StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Returns the indentation of the stream manager

  Returns the indentation of the stream manager
*/
int StreamMgr_get_indent_size(const StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Resets the indentation of the stream manager

  Resets the indentation of the stream manager
*/
void StreamMgr_reset_indent_size(StreamMgr_ptr self);

/*!
  \methodof StreamMgr
  \brief Sets the indentation of the stream manager

  Sets the indentation of the stream manager
*/
void StreamMgr_set_indent_size(StreamMgr_ptr self, int n);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_STREAM_MGR_H__ */
