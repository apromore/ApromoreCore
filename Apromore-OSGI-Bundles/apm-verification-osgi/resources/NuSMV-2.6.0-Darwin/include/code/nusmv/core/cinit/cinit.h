/* ---------------------------------------------------------------------------

  This file is part of the ``cinit'' package of NuSMV version 2.
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
  \brief "Main" package of NuSMV ("cinit" = core init).

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_CINIT_CINIT_H__
#define __NUSMV_CORE_CINIT_CINIT_H__

/*---------------------------------------------------------------------------*/
/* Nested includes                                                           */
/*---------------------------------------------------------------------------*/
#include "cudd/util.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/opt/OptsHandler.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

#ifndef NUSMV_LIBRARY_NAME

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NUSMV_LIBRARY_NAME "NuSMV"
#endif

#ifndef NUSMV_LIBRARY_VERSION

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NUSMV_LIBRARY_VERSION "2.5.0"
#endif

#ifndef NUSMV_LIBRARY_BUILD_DATE

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NUSMV_LIBRARY_BUILD_DATE "<compile date not supplied>"
#endif

#ifndef NUSMV_LIBRARY_EMAIL

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NUSMV_LIBRARY_EMAIL "nusmv-users@list.fbk.eu"
#endif

#ifndef NUSMV_LIBRARY_WEBSITE

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NUSMV_LIBRARY_WEBSITE "http://nusmv.fbk.eu"
#endif

#ifndef NUSMV_LIBRARY_BUGREPORT

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NUSMV_LIBRARY_BUGREPORT "Please report bugs to <nusmv-users@fbk.eu>"
#endif


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/
extern FILE *nusmv_historyFile;

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*FP_V_E)(NuSMVEnv_ptr);

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Returns the current NuSMV version.

  Returns a static string giving the NuSMV version and compilation
  timestamp.  The user should not free this string.

  \sa CInit_NuSMVObtainLibrary
*/
char * CInit_NuSMVReadVersion(void);

/*!
  \brief Returns the NuSMV library path.

  Returns a string giving the directory which contains
               the standard NuSMV library.  Used to find things like
               the default .nusmvrc, the on-line help files, etc. It
               is the responsibility of the user to free the returned
               string.

  \sa CInit_NuSMVReadVersion
*/
char * CInit_NuSMVObtainLibrary(void);

/*!
  \brief Prints the banner of NuSMV.


*/
void CInit_BannerPrint(FILE * file);

/*!
  \brief Prints the COMPLETE banner of the NuSMV library.

  To be used by addons linking against the NuSMV library.
               You can use this as banner print function if you don't
               need a special banner print function and you are
               linking against NuSMV
*/
void CInit_BannerPrintLibrary(FILE * file);

/*!
  \brief Prints the banner of the NuSMV library.

  To be used by tools linking against the NuSMV library
               and using custom banner function
*/
void CInit_BannerPrint_nusmv_library(FILE * file);

/*!
  \brief Prints the banner of cudd.


*/
void CInit_BannerPrint_cudd(FILE * file);

/*!
  \brief Prints the banner of minisat.


*/
void CInit_BannerPrint_minisat(FILE * file);

/*!
  \brief Prints the banner of zchaff.


*/
void CInit_BannerPrint_zchaff(FILE * file);

/*!
  \brief Gets the command line call for the specified pre-processor
  name. Returns NULL if given name is not available, or a string that must be
  NOT freed

  \todo Missing description
*/
char* get_preprocessor_call(const NuSMVEnv_ptr env, const char* name);

/*!
  \brief Gets the actual program name of the specified pre-processor.
  Returns NULL if given name is not available, or a string that must be
  freed

  \todo Missing description
*/
char* get_preprocessor_filename(const NuSMVEnv_ptr env, const char* name);

/*!
  \brief Gets the names of the avaliable pre-processors. Returned
  string must be freed

  \todo Missing description
*/
char* get_preprocessor_names(const NuSMVEnv_ptr env);

/*!
  \brief Returns the number of available proprocessors

  \todo Missing description
*/
int get_preprocessors_num(const NuSMVEnv_ptr env);

/*!
  \brief The Sm tool_name field getter

  The Sm tool_name field getter

  \sa NuSMVCore_set_tool_name
*/
char* NuSMVCore_get_tool_name(void);

/*!
  \brief The Sm tool rc file name field getter

  The Sm tool rc file name field getter

  \sa NuSMVCore_set_tool_name
*/
char* NuSMVCore_get_tool_rc_file_name(void);

/*!
  \brief The Sm tool_name field setter

  The Sm tool_name field setter

  \sa NuSMVCore_get_tool_name
*/
void NuSMVCore_set_tool_name(char* tool_name);

/*!
  \brief The Sm tool_version field getter

  The Sm tool_version field getter

  \sa NuSMVCore_set_tool_version
*/
char* NuSMVCore_get_tool_version(void);

/*!
  \brief The Sm tool_version field setter

  The Sm tool_version field setter

  \sa NuSMVCore_get_tool_version
*/
void NuSMVCore_set_tool_version(char* tool_version);

/*!
  \brief The Sm build_date field getter

  The Sm build_date field getter

  \sa NuSMVCore_set_build_date
*/
char* NuSMVCore_get_build_date(void);

/*!
  \brief The Sm build_date field setter

  The Sm build_date field setter

  \sa NuSMVCore_get_build_date
*/
void NuSMVCore_set_build_date(char* build_date);

/*!
  \brief The Sm prompt_string field getter

  The Sm prompt_string field getter

  \sa NuSMVCore_set_prompt_string
*/
char* NuSMVCore_get_prompt_string(void);

/*!
  \brief The Sm prompt_string field setter

  The Sm prompt_string field setter

  \sa NuSMVCore_get_prompt_string
*/
void NuSMVCore_set_prompt_string(char* prompt_string);

/*!
  \brief The Sm email field getter

  The Sm email field getter

  \sa NuSMVCore_set_email
*/
char* NuSMVCore_get_email(void);

/*!
  \brief The Sm email field setter

  The Sm email field setter

  \sa NuSMVCore_get_email
*/
void NuSMVCore_set_email(char* email);

/*!
  \brief The Sm website field getter

  The Sm website field getter

  \sa NuSMVCore_set_website
*/
char* NuSMVCore_get_website(void);

/*!
  \brief The Sm website field setter

  The Sm website field setter

  \sa NuSMVCore_get_website
*/
void NuSMVCore_set_website(char* website);

/*!
  \brief The Sm bug_report_message field getter

  The Sm bug_report_message field getter

  \sa NuSMVCore_set_bug_report_message
*/
char* NuSMVCore_get_bug_report_message(void);

/*!
  \brief The Sm bug_report_message field setter

  The Sm bug_report_message field setter

  \sa NuSMVCore_get_bug_report_message
*/
void NuSMVCore_set_bug_report_message(char* bug_report_message);

/*!
  \brief The Sm linked_addons field getter

  The Sm linked_addons field getter

  \sa NuSMVCore_set_linked_addons
*/
char* NuSMVCore_get_linked_addons(void);

/*!
  \brief The Sm linked_addons field setter

  The Sm linked_addons field setter

  \sa NuSMVCore_get_linked_addons
*/
void NuSMVCore_set_linked_addons(char* linked_addons);

/*!
  \brief The Sm library_name field getter

  The Sm library_name field getter

  \sa NuSMVCore_get_tool_name
*/
char* NuSMVCore_get_library_name(void);

/*!
  \brief The Sm library_name field getter

  The Sm library_name field getter

  \sa NuSMVCore_get_tool_name
*/
void  NuSMVCore_set_library_name(const char *);

/*!
  \brief The Sm library_version field getter

  The Sm library_version field getter

  \sa NuSMVCore_get_tool_version
*/
char* NuSMVCore_get_library_version(void);

/*!
  \brief The Sm library_build_date field getter

  The Sm library_build_date field getter

  \sa NuSMVCore_get_build_date
*/
char* NuSMVCore_get_library_build_date(void);

/*!
  \brief The Sm library_build_date field getter

  The Sm library_build_date field getter

  \sa NuSMVCore_get_build_date
*/
void  NuSMVCore_set_library_build_date(const char *);

/*!
  \brief The Sm library_email field getter

  The Sm library_email field getter

  \sa NuSMVCore_get_email
*/
char* NuSMVCore_get_library_email(void);

/*!
  \brief The Sm library_email field getter

  The Sm library_email field getter

  \sa NuSMVCore_get_email
*/
void  NuSMVCore_set_library_email(const char *);

/*!
  \brief The Sm library_website field getter

  The Sm library_website field getter

  \sa NuSMVCore_get_website
*/
char* NuSMVCore_get_library_website(void);

/*!
  \brief The Sm library_website field setter

  The Sm library_website field setter

  \sa NuSMVCore_get_website
*/
void  NuSMVCore_set_library_website(const char *);

/*!
  \brief The Sm library_bug_report_message field getter

  The Sm library_bug_report_message field getter

  \sa NuSMVCore_get_bug_report_message
*/
char* NuSMVCore_get_library_bug_report_message(void);

/*!
  \brief The Sm banner_print_fun field setter

  The Sm banner_print_fun field setter

  \sa NuSMVCore_get_banner_print_fun
*/
void NuSMVCore_set_banner_print_fun(void (*banner_print_fun)(FILE*));

/*!
  \brief The Sm batch fun field setter

  The Sm batch fun field setter

  \sa NuSMVCore_get_batch_fun
*/
void NuSMVCore_set_batch_fun(void (*batch_fun)(NuSMVEnv_ptr));

/* Macros to document the function calls */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CINIT_NO_PARAMETER NULL

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CINIT_IS_DEPRECATED true

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CINIT_IS_PUBLIC true

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CINIT_NO_DEPENDENCY NULL

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CINIT_NO_CONFLICT NULL

/*!
  \brief Adds a command line option to the system.

  Adds a command line option to the system. The command
   line option MUST have an environment option
   associated. When the command line option is
   specified, the environment option is
   automatically set to the correct value (In case
   of boolean options, the current value is
   negated, in any other case, the cmd option
   requires an argument which is set to the
   associated option

   Function arguments:
   1) name -> The name of the cmd line option (e.g. -int)
   2) usage -> The usage string that will be
   printed in the help (i.e. -help)
   3) parameter -> NULL if none, a string value if any.
   e.g: "k" for bmc_length
   4) env_var  -> The associated environment variable name
   5) public -> Tells whether the cmd line option
   is public or not. If not, the usage
   is not printed when invoking the
   tool with -h.
   6) deprecated -> Tells whether the cmd line
   options is deprecated or not
   7) dependency -> The possibly option name on which this
   one dependens on. NULL if none.
   e.g. -bmc_length depends on -bmc
   8) conflict -> The list of option names that
   conflict with this one.
   e.g. -mono conflicts with
   "-thresh -iwls95"


  \sa NuSMVCore_add_command_line_option
*/
void NuSMVCore_add_env_command_line_option(char* name,
                                           char* usage,
                                           char* parameter,
                                           char* env_var,
                                           boolean is_deprecated,
                                           boolean is_public,
                                           char* dependency,
                                           char* conflict);

/*!
  \brief Adds a command line option to the system.

  Adds a command line option to the system.

   When the command line option is specified, the
   check_and_apply function is called, which should
   first check that the (possible) parameter is
   valid, and then perform an action on it.

   Function arguments:
   1) name -> The name of the cmd line option (e.g. -int)
   2) usage -> The usage string that will be
   printed in the help (i.e. -help)
   3) parameter -> NULL if none, a string value if any.
   e.g: "k" for bmc_length
   4) check_and_apply -> The function that checks
   the (possible) parameter
   value and performs an
   action
   5) public -> Tells whether the cmd line option
   is public or not. If not, the usage
   is not printed when invoking the
   tool with -h.
   6) deprecated -> Tells whether the cmd line
   options is deprecated or not
   7) dependency -> The possibly option name on which this
   one dependens on. NULL if none.
   e.g. -bmc_length depends on -bmc
   8) conflict -> The list of option names that
   conflict with this one.
   e.g. -mono conflicts with
   "-thresh -iwls95"


  \sa NuSMVCore_add_env_command_line_option
*/
void
NuSMVCore_add_command_line_option(char* name,
                                  char* usage,
                                  char* parameter,
                                  boolean (*check_and_apply)(OptsHandler_ptr, char*, NuSMVEnv_ptr),
                                  boolean is_deprecated,
                                  boolean is_public,
                                  char* dependency,
                                  char* conflict);

/*!
  \brief Initializes the NuSMVCore data. This  function has to be called
   _BEFORE_ doing anything else with the library

  Initializes the NuSMVCore data. The following operations are
   performed:

   1) Initialize the internal class
   2) Sets all fields to default for NuSMV


  \sa Package_init_cmd_options Package_init
*/
void NuSMVCore_init_data(void);

/*!
  \brief Initializes the system

  Initializes the system. First calls the
                       NuSMVCore initialization function, and then
                       calls each initialization function that is in
                       the given list. The order of the list is
                       followed.
                       The list must be declared as follows:

                       FP_V_E funs[][2] = {{Init_1, Quit_1},
                                           {Init_2, Quit_2},
                                            ...
                                           {Init_n, Quit_n}
                                           }

  \sa NuSMVCore_set_reset_first_fun NuSMVCore_set_reset_last_fun
*/
void NuSMVCore_init(NuSMVEnv_ptr env, FP_V_E fns[][2], int);

/*!
  \brief Executes the main program.

  Executes the main program.

  \sa NuSMVCore_init NuSMVCore_quit
*/
boolean NuSMVCore_main(NuSMVEnv_ptr env, int argc,
                              char ** argv, int* status);

/*!
  \brief Initializes all NuSMV library command line options

  Initializes all NuSMV library command line options.
   All command line options are registered within the
   library.  If standard command line options are needed,
   this function has to be called before NuSMVCore_main and
   after NuSMVCore_init

  \sa Package_init Package_main
*/
void NuSMVCore_init_cmd_options(NuSMVEnv_ptr env);

/*!
  \brief Shuts down and restarts the system

  Shuts down and restarts the system. 4 steps are done:
   1) Call the reset_first function (if any).
   2) Call the NuSMV package reset_first function
   3) Call the NuSMV package reset_last function
   4) Call the reset_last function (if any)


  \sa NuSMVCore_set_reset_first_fun NuSMVCore_set_reset_last_fun
*/
void NuSMVCore_reset(NuSMVEnv_ptr env);

/*!
  \brief Shuts down the system

  Shuts down the system. First all quit functions
                       in the list given to NuSMVCore_init are
                       called. Then all complex structures that have a
                       dependency among some internal packages are
                       deinitialized. After that, the Core is shut
                       down and finally all simple internal structures
                       are freed

  \sa NuSMVCore_set_reset_first_fun
   NuSMVCore_set_reset_last_fun, NuSMVCore_quit_extended
*/
void NuSMVCore_quit(NuSMVEnv_ptr env);

/*!
  \brief Shuts down the system

  Shuts down the system. First all quit functions
                       in the list given to NuSMVCore_init are
                       called. Then all complex structures that have a
                       dependency among some internal packages are
                       deinitialized. After that, the Core is shut
                       down and finally, if keep_core_data is false,
                       all simple internal structures are freed

                       Lot of code duplication with NuSMVCore_quit.
                       The parameter keep_core_data is useful to avoid
                       issues related to the non-reentrancy of data
                       structure in cinitData.c (core_data) See bug
                       4119.

  \sa NuSMVCore_set_reset_first_fun NuSMVCore_set_reset_last_fun
*/
void NuSMVCore_quit_extended(NuSMVEnv_ptr env,
                                    const boolean keep_core_data);

/*!
  \brief Initializes information about the pre-processors avaliable.

  \todo Missing description
*/
void init_preprocessors(const NuSMVEnv_ptr env);

/*!
  \brief Removes information regarding the avaliable pre-processors.

  \todo Missing description
*/
void quit_preprocessors(const NuSMVEnv_ptr env);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_CINIT_CINIT_H__ */
