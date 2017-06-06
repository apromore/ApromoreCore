/* ---------------------------------------------------------------------------


  This file is part of the ``cmd'' package of NuSMV version 2.
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
  \author Adapted to NuSMV by Marco Roveri
  \brief Implements command line interface, and miscellaneous commands.

  \todo: Missing description

*/


#ifndef __NUSMV_SHELL_CMD_CMD_H__
#define __NUSMV_SHELL_CMD_CMD_H__

/*---------------------------------------------------------------------------*/
/* Nested includes                                                           */
/*---------------------------------------------------------------------------*/
#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_START_TIME          "cmdStartTime"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef int (*PFI)(NuSMVEnv_ptr env, int argc, char **argv);


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

#if NUSMV_HAVE_LIBREADLINE

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *readline(char *PROMPT);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void add_history(char *line);
#endif
#if NUSMV_HAVE_SETVBUF

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int setvbuf(FILE*, char*, int mode, size_t size);
#endif
#ifdef PURIFY

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void purify_all_inuse();
#endif

/*!
  \brief Adds a command to the command table.

  Adds a command to the command table.  If name already defines
  an existing command, its definition is replaced.  FuncFp is a function
  pointer to code of the form: <p>

                int <br>
                CommandTest(argc, argv)<br>
                  int argc;<br>
                  char **argv;<br>
                {<br>
                    return 0;<br>
                }<p>

  argv\[0\] will generally
  be the command name, and argv\[1\] ... argv\[argc-1\] are the arguments for the
  command.  util_getopt() can be used to parse the arguments, but
  util_getopt_reset() must be used before calling util_getopt().  The command
  function should return 0 for normal operation, 1 for any error.  The changes
  flag is used to automatically save the hmgr before executing the command (in
  order to support undo).
  The flag reentrant is true if the command execution can be interrupted without
  leaving the internal status inconsistent.

*/
void Cmd_CommandAdd(NuSMVEnv_ptr env, char* name, PFI funcFp, int changes,
                           boolean reentrant);

/*!
  \brief Removes given command from the command table.

  Returns true if command was found and removed,
  false if not found
*/
boolean Cmd_CommandRemove(NuSMVEnv_ptr env, const char* name);

/*!
  \brief Executes a command line.

  Executes a command line.  This is the top-level of the command
  interpreter, and supports multiple commands (separated by ;), alias
  substitution, etc.  For many simple operations, Cmd_CommandExecute() is the
  easiest way to accomplish a given task. For example, to set a variable, use
  the code: Cmd_CommandExecute("set color blue").
*/
int Cmd_CommandExecute(NuSMVEnv_ptr env, char* command);

/*!
  \brief Secure layer for Cmd_CommandExecute

  This version is securly callable from scripting languages.
  Do not call Cmd_CommandExecute directly from a scripting language, otherwise
  the script execution could be aborted without any warning.
*/
int Cmd_SecureCommandExecute(NuSMVEnv_ptr env, char* command);

/*!
  \brief Opens the file with the given mode.

  Opens the file with the given mode (see fopen()).  Tilde
  expansion (~user/ or ~/) is performed on the fileName, and "-" is allowed as
  a synonym for stdin (or stdout, depending on the mode).  If the file cannot
  be opened, a message is reported using perror(); the silent flag, if true,
  suppresses this error action.  In either case, A NULL file pointer is
  returned if any error occurs.  The fileName (after tilde expansion) is
  returned in the pointer realFileName, if realFileName is non-empty.  This
  is a pointer which should be free'd when you are done with it.
*/
FILE* Cmd_FileOpen(const NuSMVEnv_ptr env,
                          char* fileName, char* mode,
                          char** realFileName_p, int silent);

/*!
  \brief Initializes the command package.

  \todo Missing description

  \se Commands are added to the command table.

  \sa Cmd_End
*/
void Cmd_Init(NuSMVEnv_ptr env);

/*!
  \brief Ends the command package.

  Ends the command package. Tables are freed.

  \sa Cmd_Init
*/
void Cmd_End(NuSMVEnv_ptr env);

/*!
  \brief Opens a pipe with a pager

  Returns NULL if an error occurs

  \se required

  \sa optional
*/
FILE* CmdOpenPipe(const NuSMVEnv_ptr env, int useMore);

/*!
  \brief Closes a previously opened pipe

  optional

  \se required

  \sa optional
*/
void CmdClosePipe(FILE* file);

/*!
  \brief Open a file whose name is given

  optional

  \se required

  \sa optional
*/
FILE* CmdOpenFile(const NuSMVEnv_ptr env, const char* filename);

/*!
  \brief Closes a previously opened file

  optional

  \se required

  \sa optional
*/
void CmdCloseFile(FILE* file);

/*!
  \brief Tries to set the stream as a pipe or as a given file

  Tries to set the stream as a pipe or as a given file

  \se outstream is changed
*/
Outcome Cmd_Misc_open_pipe_or_file(NuSMVEnv_ptr const env,
                                          const char* dbgFileName,
                                          FILE** outstream);

/*!
  \brief Open a file or a pipe and sets them into the StreamMgr
  outstream

  return 0 on success, 1 on
  failure. Cmd_Misc_restore_global_out_stream must be ALWAYS called before
  command termination

  \sa Cmd_Misc_restore_global_out_stream
*/
int Cmd_Misc_set_global_out_stream(NuSMVEnv_ptr env,
                                          char* filename,
                                          boolean useMore,
                                          FILE** prev_outstream);

/*!
  \brief Closes the outstream in the stream mgr, previously opened
  by Cmd_Misc_set_global_out_stream, and remove it from the StreamMgr. The sets
  prev_outstream into the stream mgr



  \sa Cmd_Misc_set_global_out_stream
*/
void Cmd_Misc_restore_global_out_stream(NuSMVEnv_ptr env,
                                               char* filename,
                                               boolean use_a_pipe,
                                               FILE* prev_outstream);



/*!
  \brief Sources the .nusmvrc file.

  Sources the .nusmvrc file.  Always sources the .nusmvrc from
  library.  Then source the .nusmvrc from the home directory.  If there is none
  in the home directory, then execute the one in the current directory if one
  is present.  Returns 1 if scripts were successfully executed, else return 0.
*/
int Cmd_Misc_NusmvrcSource(NuSMVEnv_ptr env);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_SHELL_CMD_CMD_H__ */
