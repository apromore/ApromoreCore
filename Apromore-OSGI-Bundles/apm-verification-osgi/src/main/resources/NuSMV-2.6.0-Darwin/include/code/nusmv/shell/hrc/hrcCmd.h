/* ---------------------------------------------------------------------------


  This file is part of the ``hrc'' package of NuSMV version 2. 
  Copyright (C) 2009 by FBK.

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
  \brief The header file for the shell interface of the hrc packace

  \todo: Missing description

*/


#ifndef __NUSMV_SHELL_HRC_HRC_CMD_H__
#define __NUSMV_SHELL_HRC_HRC_CMD_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include "nusmv/core/utils/utils.h"


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


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \command{hrc_write_model} Writes the hrc structure from root node to a
  given SMV file

  \command_args{[-h] | [-o "filename"] [-d]}

  Writes the currently loaded SMV model stored in
  hrc structure in the specified file. If no file is specified the

  standard output is used. <p>

  Command Options:
  <dl>
    <dt> <tt>-o "filename"</tt>
    <dd> Attempts to write the SMV model in "filename".

    <dt> <tt>-d</tt>
    <dd> Renames modules appending "_hrc" the the original module name.
  </dl>
 
*/
int CommandHrcWriteModel(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{hrc_dump_model} Writes the hrc structure from root node to a
  given SMV file

  \command_args{[-h] | [-o "filename"] [-d]}
  Writes the currently loaded SMV model stored in
  hrc structure in the specified file. If no file is specified the
  standard output is used. <p>

  Command Options:
  <dl>
    <dt> <tt>-f "format"</tt>
    <dd> Dumps in the given format (debug, smv or xml).

    <dt> <tt>-o "filename"</tt>
    <dd> Dumps output to "filename"

    <dt> <tt>-d </tt>
    <dd> Renames every module name appending the suffix "_hrc"

    <dt> <tt>-i </tt>
    <dd> Disable indentation.

  </dl>
 
*/
int CommandHrcDumpModel(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \brief Initializes the commands of the hrc package.

  
*/
void Hrc_init_cmd(NuSMVEnv_ptr env);

/*!
  \brief Removes the commands provided by the hrc package.

  
*/
void Hrc_quit_cmd(NuSMVEnv_ptr env);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_SHELL_HRC_HRC_CMD_H__ */
