/* ---------------------------------------------------------------------------


  This file is part of the ``prop'' package.
  %COPYRIGHT%
  

-----------------------------------------------------------------------------*/

/*!
  \author Michele Dorigatti
  \brief Module header file for prop shell commands

  Module header file for prop shell commands

*/


#ifndef __NUSMV_SHELL_PROP_PROP_CMD_H__
#define __NUSMV_SHELL_PROP_PROP_CMD_H__

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
  \brief Initiliaze the prop package for commands

  Initialize the prop package for commands.  This must be
  called independently from the package initialization function
*/
void PropPkg_init_cmd(NuSMVEnv_ptr env);

/*!
  \brief Quit the prop package for commands

  This must be called independently from
  the package initialization function
*/
void PropPkg_quit_cmd(NuSMVEnv_ptr env);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_SHELL_PROP_PROP_CMD_H__ */
