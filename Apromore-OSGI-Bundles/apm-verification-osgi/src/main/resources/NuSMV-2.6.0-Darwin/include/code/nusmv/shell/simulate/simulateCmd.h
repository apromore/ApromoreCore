/* ---------------------------------------------------------------------------


  This file is part of the ``simulate'' package.
  %COPYRIGHT%
  

-----------------------------------------------------------------------------*/

/*!
  \author Michele Dorigatti
  \brief Module header file for simulate shell commands

  Module header file for simulate shell commands

*/


#ifndef __NUSMV_SHELL_SIMULATE_SIMULATE_CMD_H__
#define __NUSMV_SHELL_SIMULATE_SIMULATE_CMD_H__

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
  \brief Initializes the simulate shell commands

  Initializes the simulate shell commands
*/
void Simulate_Cmd_init(NuSMVEnv_ptr env);

/*!
  \brief Deinitialize the simulation shell commands

  Deinitialize the simulation shell commands
*/
void Simulate_Cmd_quit(NuSMVEnv_ptr env);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_SHELL_SIMULATE_SIMULATE_CMD_H__ */
