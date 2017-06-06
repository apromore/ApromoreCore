/* ---------------------------------------------------------------------------


  This file is part of the ``required'' package.
  %COPYRIGHT%
  

-----------------------------------------------------------------------------*/

/*!
  \author Michele Dorigatti
  \brief \todo: Missing synopsis

  \todo: Missing description

*/


#ifndef __NUSMV_SHELL_CMD_CMD_CMD_H__
#define __NUSMV_SHELL_CMD_CMD_CMD_H__

#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/


//used to import command help list from cmdHelp.c
typedef struct command_item_TAG {
      const char* command_name;
      const char* command_description;
} command_item;

extern command_item command_help[];
extern int command_number;


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_CMD_HELP           "CMD_HELP"

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief 

  
*/
char* cmd_help_get(NuSMVEnv_ptr env, char* command_name);

/*!
  \brief 

  
*/
void cmd_help_add(NuSMVEnv_ptr env, const char* command_name, const char* command_description);

/*!
  \brief 

  
*/
void cmd_help_remove(NuSMVEnv_ptr env, char* command_name);

/*!
  \brief 

  
*/
void Cmd_init_cmd(NuSMVEnv_ptr env);

/*!
  \brief 

  
*/
void Cmd_quit_cmd(NuSMVEnv_ptr env);

/*!
  \command{} Stub command useful to disable a command

  \command_args{}

  Prints a message and returns error
*/
int Cmd_command_not_available(NuSMVEnv_ptr env, int argc, char** argv);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_SHELL_CMD_CMD_CMD_H__ */
