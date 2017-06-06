/* ---------------------------------------------------------------------------


  This file is part of the ``hrc.dumpers'' package of NuSMV version 2.
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
  \author Roberto Cavada
  \brief Private and protected interface of class 'HrcDumper'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_PRIVATE_H__
#define __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_PRIVATE_H__


#include "nusmv/core/hrc/dumpers/HrcDumper.h"

#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/EnvObject_private.h"
#include "nusmv/core/utils/utils.h"


/*!
  \brief HrcDumper class definition derived from
               class Object

  

  \sa Base class Object
*/

typedef struct HrcDumper_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(EnvObject);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */

  MasterPrinter_ptr printer;
  FILE* fout;
  boolean use_indentation;
  int indent;
  size_t indent_size;
  boolean indent_pending; /* used to control indentation */
  unsigned int columns;
  boolean use_mod_suffix;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  void (*dump_snippet)(HrcDumper_ptr self,
                       HrcDumperSnippet snippet,
                       const HrcDumperInfo* info);

  void (*dump_comment)(HrcDumper_ptr self,
                       const char* msg);

  void (*dump_header)(HrcDumper_ptr self,
                      const char* msg);

  void (*dump_node)(HrcDumper_ptr self,
                    node_ptr node);
} HrcDumper;


/* ---------------------------------------------------------------------- */
/* Macros                                                                 */
/* ---------------------------------------------------------------------- */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define HRC_DEFAULT_COLUMNS 79

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define HRC_MODULE_SUFFIX "_hrc"


#define _HRC_DUMP_STR(x)          \
  {                               \
    hrc_dumper_dump_indent(self); \
    fprintf(self->fout, x);       \
  }

#define _HRC_DUMP_STR_NL(x)                                           \
  {                                                                   \
    hrc_dumper_dump_indent(self);                                     \
    fprintf(self->fout, x);                                           \
    hrc_dumper_nl(self);                                              \
  }

#define _HRC_DUMP_NL()                                                \
  {                                                                   \
    hrc_dumper_nl(self);                                              \
  }

#define _HRC_DUMP_NODE(x)                              \
  {                                                    \
    hrc_dumper_dump_indent(self);                      \
    self->dump_node(self, x);                          \
  }

#define _HRC_DUMP_COMMENT(x)      \
  {                               \
    self->dump_comment(self, x);  \
  }

#define _HRC_DUMP_HEADER(x)       \
  {                               \
    self->dump_header(self, x);   \
  }



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof HrcDumper
  \brief The HrcDumper class private initializer

  The HrcDumper class private initializer

  \sa HrcDumper_create
*/
void hrc_dumper_init(HrcDumper_ptr self,
                            const NuSMVEnv_ptr env,
                            FILE* fout);

/*!
  \methodof HrcDumper
  \brief The HrcDumper class private deinitializer

  The HrcDumper class private deinitializer

  \sa HrcDumper_destroy
*/
void hrc_dumper_deinit(HrcDumper_ptr self);

/*!
  \methodof HrcDumper
  \brief 

  
*/
void hrc_dumper_dump_snippet(HrcDumper_ptr self,
                                    HrcDumperSnippet snippet,
                                    const HrcDumperInfo* info);

/*!
  \methodof HrcDumper
  \brief Dumps a comment

  
*/
void hrc_dumper_dump_comment(HrcDumper_ptr self,
                                    const char* msg);

/*!
  \methodof HrcDumper
  \brief 

  
*/
void hrc_dumper_dump_header(HrcDumper_ptr self, const char* msg);

/*!
  \methodof HrcDumper
  \brief Dumps a node

  Virtual unimplemented method
*/
void hrc_dumper_dump_node(HrcDumper_ptr self, node_ptr node);

/*!
  \methodof HrcDumper
  \brief 

  
*/
void hrc_dumper_dump_indent(HrcDumper_ptr self);

/*!
  \methodof HrcDumper
  \brief Implements indentation of a newline

  
*/
void hrc_dumper_nl(HrcDumper_ptr self);

/*!
  \methodof HrcDumper
  \brief Prints the type of a variable.

  Prints the type of a variable. The printers used
  in compileWrite.c in compile package cannot be used in hrc, unless
  symbol table is used.

  The printer manages the following types: BOOLEAN, INTEGER, REAL,
  UNSIGNED_WORD, SIGNED_WORD, SCALAR, WORD_ARRAY and  ARRAY_TYPE.
*/
void hrc_dumper_dump_var_type(HrcDumper_ptr self, node_ptr node);

#endif /* __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_PRIVATE_H__ */
