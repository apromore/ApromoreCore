/* ---------------------------------------------------------------------------


  This file is part of the ``bmc'' package of NuSMV version 2.
  Copyright (C) 2004 Timo Latvala <timo.latvala@tkk.fi>

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
  \author Timo Latvala, Marco Roveri
  \brief Public interface for SBMC tableau-related functionalities

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_TABLEAU_LTLFORMULA_H__
#define __NUSMV_CORE_BMC_SBMC_SBMC_TABLEAU_LTLFORMULA_H__

#include "nusmv/core/fsm/be/BeFsm.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/be/be.h"
#include "nusmv/core/node/node.h"


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
  \brief Given a wff expressed in ltl builds the model-independent
  tableau at 'time' of a path formula bounded by \[k, l\]

  The function generates the necessary auxilliary
               predicates (loop, atmostonce) and calls on
               get_f_at_time to generate the tableau for the ltl
               formula.

  \sa AtMostOnce, Loop, get_f_at_time
*/
be_ptr
BmcInt_SBMCTableau_GetAtTime(const BeEnc_ptr be_enc, const node_ptr ltl_wff,
                             const int time, const int k, const int l);

/**AutomaticEnd***************************************************************/

#endif /* _BMC_TABLEAU__H */
