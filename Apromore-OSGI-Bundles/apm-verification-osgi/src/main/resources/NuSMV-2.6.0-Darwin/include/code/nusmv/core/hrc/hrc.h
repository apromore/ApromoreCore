/* ---------------------------------------------------------------------------


  This file is part of the ``hrc'' package of NuSMV version 2. 
  Copyright (C) 2009 by FBK-irst. 

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
  \brief The package to manage the NuSMV module hierachy.

  The package to manage the NuSMV module hierachy.

*/


#ifndef __NUSMV_CORE_HRC_HRC_H__
#define __NUSMV_CORE_HRC_HRC_H__

#include "nusmv/core/hrc/HrcNode.h"
#include "nusmv/core/hrc/dumpers/HrcDumper.h"

#include "nusmv/core/set/set.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/
/*!
  \brief The possible type of printing of an hrc

  
*/

typedef enum HrcDumpFormat_TAG {
  HRC_DUMP_FORMAT_INVALID = 0,
  HRC_DUMP_FORMAT_DEBUG = 1,
  HRC_DUMP_FORMAT_SMV,
  HRC_DUMP_FORMAT_XML
} HrcDumpFormat;

HrcDumpFormat Hrc_dump_format_str_to_enum(char* format);
char* Hrc_dump_format_enum_to_str(HrcDumpFormat format);
char* Hrc_dump_format_get_available(void);

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Initializes the hrc package.

  Initializes the hrc package. The initialization
  consists of the allocation of the mainHrcNode global variable and
  the initialization of the hrc package commands.

  \sa TracePkg_quit
*/
void Hrc_init(NuSMVEnv_ptr env);

/*!
  \brief Quits the hrc package.

  Quits the hrc package, freeing the global variable
  mainHrcNode and removing the hrc commands.

  \sa TracePkg_init
*/
void Hrc_quit(NuSMVEnv_ptr env);

/*!
  \brief DEPRECATED Prints the SMV module for the hrcNode.

  Deprecated function. Use Hrc_DumpModel or Hrc_dump_model instead.

  Prints the SMV module for the hrcNode. If the
  flag append_suffix is true then the suffix HRC_WRITE_MODULE_SUFFIX
  is appended when a module type is printed. So
  HRC_WRITE_MODULE_SUFFIX is appended to the module name in module
  declarations and to the module name in a module instantiation. The
  feature is needed for testing to avoid name clash among modules
  names when the original model and the model generated from hrc are
  merged.
*/
int Hrc_WriteModel(HrcNode_ptr self,
                          FILE * ofile,
                          boolean append_suffix);

/*!
  \brief Prints the SMV module for the hrcNode.

  Prints the SMV module for the hrcNode. If the
  flag append_suffix is true then the suffix HRC_WRITE_MODULE_SUFFIX
  is appended when a module type is printed. So
  HRC_WRITE_MODULE_SUFFIX is appended to the module name in module
  declarations and to the module name in a module instantiation. The
  feature is needed for testing to avoid name clash among modules
  names when the original model and the model generated from hrc are
  merged.
*/
void Hrc_DumpModel(HrcNode_ptr hrcNode, HrcDumper_ptr dumper);

/*!
  \brief Writes the SMV model contained in the root node
  of the hrc structure.

  Based on the format, constructs the right dumper. Then
  dump the hrc model.
  ownership of ofileid is taken (by the dumpers)
*/
int
Hrc_dump_model(const NuSMVEnv_ptr env,
               HrcDumpFormat format,
               FILE* ofileid,
               const boolean append_suffix,
               const boolean use_indent);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_HRC_HRC_H__ */
