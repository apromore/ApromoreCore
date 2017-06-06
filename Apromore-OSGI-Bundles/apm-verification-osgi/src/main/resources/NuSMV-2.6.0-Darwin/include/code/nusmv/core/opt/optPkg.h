/* ---------------------------------------------------------------------------


  This file is part of the ``opt'' package.
  %COPYRIGHT%
  

-----------------------------------------------------------------------------*/

/*!
  \author Michele Dorigatti
  \brief module header file for package handling

  module header file for package handling

*/


#ifndef __NUSMV_CORE_OPT_OPT_PKG_H__
#define __NUSMV_CORE_OPT_OPT_PKG_H__

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
  \brief Initialize the NuSMV options.

  The NuSMV options are initialized. A pointer to a
                       structure containing the NuSMV options is
                       allocated, its fields are initialized within
                       the given Environment

                      Environment requisites:
                      - No instances registered with key ENV_OPTS_HANDLER
*/
void Opt_Pkg_init(NuSMVEnv_ptr const env);

/*!
  \brief De-initialize the options in the given environment

  The NuSMV options are deinitialized within the
                       given environment

                      Environment requisites:
                      - A OptsHandler instance registered with key
                        ENV_OPTS_HANDLER
*/
void Opt_Pkg_deinit(NuSMVEnv_ptr const env);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_OPT_OPT_PKG_H__ */
