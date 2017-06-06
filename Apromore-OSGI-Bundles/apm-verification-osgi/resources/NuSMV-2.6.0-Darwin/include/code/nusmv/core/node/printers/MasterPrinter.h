/* ---------------------------------------------------------------------------


  This file is part of the ``node.printers'' package of NuSMV version 2.
  Copyright (C) 2006 by FBK-irst.

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
  \brief Public interface of class 'MasterPrinter'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_NODE_PRINTERS_MASTER_PRINTER_H__
#define __NUSMV_CORE_NODE_PRINTERS_MASTER_PRINTER_H__

#include "nusmv/core/node/NodeMgr.h"
#include "nusmv/core/node/MasterNodeWalker.h"

/*!
  \struct MasterPrinter
  \brief Definition of the public accessor for class MasterPrinter


*/
typedef struct MasterPrinter_TAG*  MasterPrinter_ptr;

/*!
  \brief To cast and check instances of class MasterPrinter

  These macros must be used respectively to cast and to check
  instances of class MasterPrinter
*/
#define MASTER_PRINTER(self) \
         ((MasterPrinter_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MASTER_PRINTER_CHECK_INSTANCE(self) \
         (nusmv_assert(MASTER_PRINTER(self) != MASTER_PRINTER(NULL)))


/*!
  \brief Definition of enumeration StreamType

  Values taken from this enum are used to set the stream
  type to be used by the MasterPrinter when producing a printing output
*/

typedef enum StreamType_TAG {
  STREAM_TYPE_DEFAULT,  /* the default stream type (STREAM_TYPE_STDOUT) */
  STREAM_TYPE_STDOUT,
  STREAM_TYPE_STDERR,
  STREAM_TYPE_STRING,
  STREAM_TYPE_FILE,     /* This requires a parameter */
  STREAM_TYPE_FUNCTION  /* This requires a parameter */
} StreamType;

/*!
  \brief Function pointer for STREAM_TYPE_FUNCTION type

  When STREAM_TYPE_FUNCTION is set as stream type, the
  argument must be a function pointer whose prototype is defined by
  StreamTypeFunction_ptr

  NOTE: The argument 'arg' is a generic argument useful for passing
  information in a reentrant way
*/
typedef int (*StreamTypeFunction_ptr)(const char* str, void* arg);


/*!
  \brief Definition of enumeration StreamType

  Values taken from this enum are used to set the stream
  type to be used by the MasterPrinter when producing a printing output
*/

union StreamTypeArg
{
  /* for STREAM_TYPE_FILE */
  FILE* file;

  /* for STREAM_TYPE_FUNCTION */
  struct {
    /* The function pointer */
    StreamTypeFunction_ptr func_ptr;
    /* The argument to pass to each function call */
    void* argument;
  } function;
};


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define STREAM_TYPE_ARG_UNUSED   \
  NULL


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* Fast and ready to use functions */

/*!
  \brief Pretty print a formula on a file

  Pretty print a formula on a file
*/
int print_node(MasterPrinter_ptr wffprinter, FILE *, node_ptr);

/*!
  \brief Pretty print a formula into a string

  Pretty print a formula into a string. The returned
   string must be freed after using it. Returns NULL in case of failure.
*/
char* sprint_node(MasterPrinter_ptr wffprinter, node_ptr);

/*!
  \brief Pretty print a formula on a file (indenting)

  Pretty print a formula on a file (indenting), starting
   at given offset.
*/
int print_node_indent_at(MasterPrinter_ptr iwffprinter,
                                FILE *stream, node_ptr n, int ofs);

/*!
  \brief Pretty print a formula into a string (indenting)

  Pretty print a formula into a string (indenting),
   starting at given offset. The returned string must be freed after
   using it. Returns NULL in case of failure.
*/
char* sprint_node_indent_at(MasterPrinter_ptr iwffprinter,
                                   node_ptr n, int ofs);

/*!
  \brief Pretty print a formula on a file (indenting)

  Pretty print a formula on a file (indenting), starting
   at column 0.
*/
int print_node_indent(MasterPrinter_ptr iwffprinter,
                             FILE *stream, node_ptr n);

/*!
  \brief Pretty print a formula into a string (indenting)

  Pretty print a formula into a string (indenting),
   starting at column 0. The returned string must be freed after using
   it. Returns NULL in case of failure.
*/
char* sprint_node_indent(MasterPrinter_ptr iwffprinter,
                                node_ptr n);


/* MasterPrinter class methods */

/*!
  \methodof MasterPrinter
  \brief The MasterPrinter class constructor

  The MasterPrinter class constructor

  \sa MasterPrinter_destroy
*/
MasterPrinter_ptr MasterPrinter_create(const NuSMVEnv_ptr env);

/*!
  \methodof MasterPrinter
  \brief Prints the given node on the stream currently set

  If the stream is a string stream, the result can be
  obtained be calling MasterPrinter_get_streamed_string. Returns
  0 if an error occurred for some reason.
*/
int
MasterPrinter_print_node(MasterPrinter_ptr self, node_ptr n);

/*!
  \methodof MasterPrinter
  \brief Prints the given string on the stream currently set

  If the stream is a string stream, the result can be
  obtained be calling MasterPrinter_get_streamed_string. Returns
  0 if an error occurred for some reason.
*/
int
MasterPrinter_print_string(MasterPrinter_ptr self, const char* str);

/*!
  \methodof MasterPrinter
  \brief Returns the string that has been streamed

  Returned string belongs to self, DO NOT free it.

  Warning: this method can be called only when the current
  stream type is STREAM_TYPE_STRING.

  \sa master_printer_reset_string_stream
*/
const char*
MasterPrinter_get_streamed_string(const MasterPrinter_ptr self);

/*!
  \methodof MasterPrinter
  \brief Reset the stream

  Set the indentation offset for this stream. Negative
  offsets are silently discarded.
*/
void
MasterPrinter_reset_stream(MasterPrinter_ptr self, int offs);

/*!
  \methodof MasterPrinter
  \brief Sets the stream type to be used to produce a printing
  result

  When the given type requires an argument (for example,
  STREAM_TYPE_FILE requires a file), the argument must be passed by
  using the 'arg' parameter. When not required (for example
  STREAM_TYPE_STRING), the caller can pass STREAM_TYPE_ARG_UNUSED
  as argument.

  When STREAM_TYPE_FILE is used, the argument must be the handler of
  an open writable file.

*/
void
MasterPrinter_set_stream_type(MasterPrinter_ptr self,
                              StreamType type,
                              const union StreamTypeArg* arg);

/*!
  \methodof MasterPrinter
  \brief Returns the currently set stream type

  Returns the currently set stream type
*/
StreamType
MasterPrinter_get_stream_type(const MasterPrinter_ptr self);

/*!
  \methodof MasterPrinter
  \brief Flushes the current stream, if possible or applicable

  The currently set stream is flushed out (i.e. no
  unstreamed data remains afterwards.
*/
int
MasterPrinter_flush_stream(MasterPrinter_ptr self);

/*!
  \methodof MasterPrinter
  \brief Closes the current stream, if possible or applicable

  The currently set stream is closed (file) or reset
  (string) and the stream type is set to be STREAM_TYPE_DEFAULT.
  IMPORTANT: If the current stream is nusmv_std{out,err} the stream is
  not closed.

  This function is provided to allow the called to forget the set
  stream after setting it into the master printer.
*/
void
MasterPrinter_close_stream(MasterPrinter_ptr self);

/*!
  \brief


*/
void debug_print_node(NuSMVEnv_ptr env, node_ptr node);

/*!
  \brief


*/
void debug_print_sexp(NuSMVEnv_ptr env, node_ptr node);

/**AutomaticEnd***************************************************************/


#endif /* __NUSMV_CORE_NODE_PRINTERS_MASTER_PRINTER_H__ */
