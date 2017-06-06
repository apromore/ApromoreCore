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
  \brief Internal declarations for command package.

  \todo: Missing description

*/


#ifndef __NUSMV_SHELL_CMD_CMD_INT_H__
#define __NUSMV_SHELL_CMD_CMD_INT_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include "nusmv/shell/cmd/cmd.h"

#include "nusmv/core/cinit/cinit.h"
#include "nusmv/core/opt/opt.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/array.h"
#include "nusmv/core/utils/avl.h"


#if NUSMV_STDC_HEADERS
#  include <string.h>
#  include <stdlib.h>
#else
 void free();
# if NUSMV_HAVE_STRING_H
#  include <string.h>
# else
# ifndef strncpy
char* strncpy(char*, const char*, size_t);
# endif
# endif
#endif

/*
 * This is for Solaris -- it needs to be convinced that we're actually
 * using BSD-style calls in sys/ioctl.h, otherwise it doesn't find
 * "ECHO" "CRMOD" and "TIOCSTI" when compiling cmdFile.c
 */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BSD_COMP

#if defined(IOCTL_WITH_TERMIOS) && IOCTL_WITH_TERMIOS
#  include <sys/ioctl.h>
#  include <sys/termios.h>
#else
#  if NUSMV_HAVE_SYS_IOCTL_H
#    include <sys/ioctl.h>
#  else
#    if NUSMV_HAVE_SYS_TERMIOS_H
#      include <sys/termios.h>
#    endif
#  endif
#endif

/* Linux and its wacky header files... */
#if defined NUSMV_HAVE_BSD_SGTTY_H && NUSMV_HAVE_BSD_SGTTY_H
#  include <bsd/sgtty.h>
#endif

#if NUSMV_HAVE_SYS_SIGNAL_H
#  include <sys/signal.h>
#endif
#if NUSMV_HAVE_SIGNAL_H
#  include <signal.h>
#endif

/*
 * No unix system seems to be able to agree on how to access directories,
 * which cmdFile.c needs to do.  This solution, suggested by the autoconf
 * distribution, seems to handle most of the nonsense.
 */

#if NUSMV_HAVE_DIRENT_H
# if NUSMV_HAVE_SYS_TYPES_H
#  include <sys/types.h>
# endif
#  include <dirent.h>
#  define NAMLEN(dirent) strlen((dirent)->d_name)
#else
#  define dirent direct
#  define NAMLEN(dirent) (dirent)->d_namlen
#  if NUSMV_HAVE_SYS_NDIR_H
#    include <sys/ndir.h>
#  endif
#  if NUSMV_HAVE_SYS_DIR_H
#    include <sys/dir.h>
#  endif
#  if NUSMV_HAVE_NDIR_H
#    include <ndir.h>
#  endif
#endif

/* Environment internal structures */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_CMD_ALIAS_TABLE     "__cmd_alias_table__"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_CMD_COMMAND_TABLE   "__cmd_cmd_table__"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_CMD_COMMAND_HISTORY "__cmd_cmd_history__"

/*---------------------------------------------------------------------------*/
/* Stucture declarations                                                     */
/*---------------------------------------------------------------------------*/
typedef struct CmdAliasDescrStruct {
  char *name;
  int argc;
  char **argv;
} CmdAliasDescr_t;

typedef struct CommandDescrStruct {
  char *name;
  PFI command_fp;
  int changes_hmgr;
  boolean reentrant;
} CommandDescr_t;

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief True iff a command named 'name' is defined.


*/
boolean Cmd_CommandDefined(NuSMVEnv_ptr env, const char* name);

/*!
  \brief Returns the command stored under 'name' in the command table.

  Returned value does not belong to caller.
*/
CommandDescr_t *Cmd_CommandGet(NuSMVEnv_ptr env, const char* name);

/*!
  \brief required

  optional

  \se required

  \sa optional
*/
void CmdCommandFree(char * value);

/*!
  \brief Copies value.


*/
CommandDescr_t * CmdCommandCopy(CommandDescr_t * value);

/*!
  \brief Duplicates the function of fgets, but also provides file
  completion in the same style as csh

   Input is read from `stream' and returned in `buf'.  Up to
  `size' bytes will be placed into `buf'.  If `stream' is not stdin, is
  equivalent to calling fgets(buf, size, stream).

  `prompt' is the prompt you want to appear at the beginning of the line.  The
  caller does not have to print the prompt string before calling this routine.
  The prompt has to be reprinted if the user hits ^D.

  The file completion routines are derived from the source code for csh, which
  is copyrighted by the Regents of the University of California.
*/
char * CmdFgetsFilec(NuSMVEnv_ptr env, char * buf, unsigned int size,
                            FILE * stream, char * prompt);

/*!
  \brief Simple history substitution routine.

  Simple history substitution routine. Not, repeat NOT, the
  complete csh history substitution mechanism.

  In the following ^ is the SUBST character and ! is the HIST character.
  Deals with:
        !!                      last command
        !stuff                  last command that began with "stuff"
        !*                      all but 0'th argument of last command
        !$                      last argument of last command
        !:n                     n'th argument of last command
        !n                      repeat the n'th command
        !-n                     repeat n'th previous command
        ^old^new                replace "old" w/ "new" in previous command


  Trailing spaces are significant. Removes all initial spaces.

  Returns `line' if no changes were made.  Returns pointer to a static buffer
  if any changes were made.  Sets `changed' to 1 if a history substitution
  took place, o/w set to 0.  Returns NULL if error occurred.
*/
char * CmdHistorySubstitution(NuSMVEnv_ptr env, char * line, int * changed);

/*!
  \brief required

  optional

  \se required

  \sa optional
*/
void CmdFreeArgv(int argc, char ** argv);

/*!
  \brief required

  optional

  \se required

  \sa optional
*/
void CmdAliasFree(char * value);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_SHELL_CMD_CMD_INT_H__ */
