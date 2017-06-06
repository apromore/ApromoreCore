/* ---------------------------------------------------------------------------


  This file is part of the ``required'' package.
  %COPYRIGHT%
  

-----------------------------------------------------------------------------*/

/*!
  \author Michele Dorigatti
  \brief Module header for bmcTest

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_BMC_TEST_H__
#define __NUSMV_CORE_BMC_BMC_TEST_H__

#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/
/* user can generate a random wff based on a specified operator: */
typedef enum GenWffOperator_TAG {
  GWO_None, GWO_Globally, GWO_Future, GWO_Until, GWO_Releases,
  GWO_Historically, GWO_Once, GWO_Since, GWO_Triggered
} GenWffOperator;


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
  \brief The first time Bmc_Test_test_tableau is called in the current
  session this function creates a smv file with a model and generates a random
  ltl spec to test tableau. The following times it is called it appends a new
  formula to the file.

  BMC_ALL_LOOPS is not supported
*/
int Bmc_Test_test_tableau(NuSMVEnv_ptr env,
                                 node_ptr wff,
                                 GenWffOperator wff_operator,
                                 int max_depth,
                                 int max_conns,
                                 boolean usePastOperators,
                                 boolean crossComparison,
                                 int k,
                                 int l);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_TEST_H__ */
