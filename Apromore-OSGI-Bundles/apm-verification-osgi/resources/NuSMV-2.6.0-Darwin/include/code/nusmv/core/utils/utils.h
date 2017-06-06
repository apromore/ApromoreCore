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
  \brief External header of the utils package

  External header of the utils package.

*/


#ifndef __NUSMV_CORE_UTILS_UTILS_H__
#define __NUSMV_CORE_UTILS_UTILS_H__

#if HAVE_CONFIG_H
#include "nusmv-config.h"
#else

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NUSMV_FUNCATTR_NORETURN
#endif

#include "nusmv/core/utils/defs.h"
#include "nusmv/core/cinit/NuSMVEnv.h"
#include "nusmv/core/utils/list.h" /* MD: it should be better to remove this dependency */

/* --------------------------------------------------------------------- */
/*      Exported functions                                               */
/* --------------------------------------------------------------------- */

/*!
  \brief Initializes the utils package



  \se None
*/
void Utils_pkg_init(const NuSMVEnv_ptr env);

/*!
  \brief De-initializes the utils package



  \se None
*/
void Utils_pkg_quit(const NuSMVEnv_ptr env);

/* String Manipulation ********************************************************/

/*!
  \brief Convert an int to a string

  The string must be freed after use
*/
char* Utils_int_to_str(const int an_int);

/*!
  \brief Returns the size than an ant would need if represented as
  a string


*/
size_t Utils_int_size_as_string(const int an_int);

/* FileSystem *****************************************************************/

/*!
  \brief Returns pathname without path prefix



  \se None
*/
const char* Utils_StripPath(const char* pathfname);

/*!
  \brief Returns filename without path and extension

  Example: given "~/.../test.smv", "test" will be returned.
  filename must be a string whose length is large enought to contain the "pure"
  filename

  \se the string pointed by 'filename' changes
*/
void
Utils_StripPathNoExtension(const char* fpathname, char* filename);

/*!
  \brief Returns directory part of fpathname without filename and
                      extension

  dirname must be a string whose length is large enough to
                      contain the directory part

  \se The string pointed to by 'dirname' changes

  \sa Utils_StripPathNoExtension, Utils_StripPath
*/
void Utils_StripPathNoFilenameNoExtension(const char* fpathname,
                                          char* dirname);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char* Utils_get_temp_filename_in_dir(const char* dir,
                                     const char* templ);

/*!
  \brief Checks a list of directories for a given file.

  The list of directories (delimited by the charaters given)
  are checked for the existence of the file.

  @return:
  0 if not existing
  1 if exists
  2 if unknown


  \sa Utils_file_exists_in_directory
*/
int Utils_file_exists_in_paths(const char* filename,
                               const char* paths,
                               const char* delimiters);

/*!
  \brief Checks for the existence of a file within a directory.


  @return:
  0 if not existing
  1 if exists
  2 if unknown


  \sa Utils_file_exists_in_paths
*/
int Utils_file_exists_in_directory(const char* filename,
                                   char* directory);

/*!
  \brief Checks if a file exists in file system

  filename is absolute or relative to the current
  working dir
*/
boolean Utils_file_exists(const char* filename);

/*!
  \brief check if given file names are referring the same file node
*/
boolean Utils_files_are_the_same(const char* fname1, const char* fname3);

/*!
  \brief Checks if a file can be written in file system

  filename is absolute or relative to the current
  working dir
*/
boolean Utils_file_can_be_written(const char* filename);

/*!
  \brief Checks if a file exists in file system and it has
  executable permission granted

  filename is absolute or relative to the current
  working dir
*/
boolean Utils_exe_file_exists(const char* filename);


/* Miscellaneous **************************************************************/

/*!
  \brief An abstraction over BSD strcasecmp

  Compares the two strings s1 and s2,
  ignoring the case of the characters.
*/
int Utils_strcasecmp(const char* s1, const char* s2);

/*!
  \brief Computes the log2 of the given unsigned argument
                      rounding the result to the closest upper
                      integer. 0 gives 1 as result.

  This function can be used to calculate the number of
  bits needed to represent a value.
*/
int Utils_log2_round(unsigned long long int a);

/*!
  \brief Compare function for c library qsort

  Compares pointers

  \se required

  \sa optional
*/
int Utils_ptr_compar(const void* a, const void* b);

/*!
  \brief Escapes all characters in given string, and dumps them
  into the xml file


*/
void Utils_str_escape_xml_file(const char* str, FILE* file);

/*!
  \brief Destroys a list of list

  This function can be used to destroy lists of list. The
  contained set of lists is removed from memory as the top level list.
  More than two levels are not handled at the moment.

  \se Lists are deallocated

  \sa lsDestroy
*/
void Utils_FreeListOfLists(lsList list_of_lists);


/* High-level support for timers used in benchmarking ************************/

/*!
  \brief Starts a timer whose name is given

  If the timer does not exist, it will be created and
  started. If already started an error occurs.
*/
void Utils_start_timer(const NuSMVEnv_ptr env, const char* name);

/*!
  \brief Stops a timer whose name is given

  The timer must be already existing and running.
*/
void Utils_stop_timer(const NuSMVEnv_ptr env, const char* name);

/*!
  \brief Resets a timer whose name is given

  The timer must be already existing.
*/
void Utils_reset_timer(const NuSMVEnv_ptr env, const char* name);

/*!
  \brief prints info about a timer whose name is given

  The timer must be already existing. msg can be NULL
*/
void Utils_print_timer(const NuSMVEnv_ptr env,
                       const char* name, const char* msg);


/* Draft of an interface to cudd util subpackage ******************************/

/*!
  \brief Substitutes tilde, and '/' with '\\' under windows

  Returned string must be destroyed. Returned string is
  equal (but not it) to the input string if home could not be find
*/
char* Utils_util_tilde_expand(char* fname);


#endif /* __NUSMV_CORE_UTILS_UTILS_H__ */
