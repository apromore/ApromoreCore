/* ---------------------------------------------------------------------------

 This file is part of the ``bmc.sbmc'' package of NuSMV version 2.
  Copyright (C) 2006 Tommi Junttila.

  NuSMV version 2 is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public License
  as published by the Free Software Foundation; either version 2 of
  the License, or (at your option) any later version.

  NuSMV version 2 is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
  USA.

  For more information of NuSMV see <http://nusmv.fbk.eu> or
  email to <nusmv-users@fbk.eu>.  Please report bugs to
  <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to  <nusmv@fbk.eu>. 

-----------------------------------------------------------------------------*/

/*!
  \author Tommi Junttila, Marco Roveri
  \brief High level functionalities for Incrememntal SBMC

  User-commands directly use function defined in this module. 
  This is the highest level in the Incrememntal SBMC API architecture.

*/


#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_BMC_INC_H__
#define __NUSMV_CORE_BMC_SBMC_SBMC_BMC_INC_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/prop/Prop.h"

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
  \brief High level function that performs incremental sbmc

  optional

  \se required

  \sa optional
*/
int Sbmc_zigzag_incr(NuSMVEnv_ptr env,
                            Prop_ptr ltlprop,
                            const int max_k,
                            const int opt_do_virtual_unrolling,
                            const int opt_do_completeness_check);

/*!
  \brief High level function that performs incremental
  sbmc under assumptions. Currently this routine requires MiniSAT being
  used as SAT solver.

  optional

  \se required

  \sa optional
*/
int Sbmc_zigzag_incr_assume(NuSMVEnv_ptr env,
                                   Prop_ptr ltlprop,
                                   const int max_k,
                                   const int opt_do_virtual_unrolling,
                                   const int opt_do_completeness_check,
                                   Slist_ptr assumptions,
                                   Slist_ptr* conflict);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_SBMC_SBMC_BMC_INC_H__ */
