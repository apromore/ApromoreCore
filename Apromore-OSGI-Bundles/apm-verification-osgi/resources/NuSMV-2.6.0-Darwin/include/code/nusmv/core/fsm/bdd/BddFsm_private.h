/* ---------------------------------------------------------------------------

  This file is part of the ``fsm.bdd'' package of NuSMV version 2.
  Copyright (C) 2015 by FBK-irst.

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
  \author Roberto Cavada
  \brief Declares the private interface of the class BddFsm

*/



#ifndef __NUSMV_CORE_FSM_BDD_BDD_FSM_PRIVATE_H__
#define __NUSMV_CORE_FSM_BDD_BDD_FSM_PRIVATE_H__

#include "nusmv/core/fsm/bdd/BddFsm.h"
#include "nusmv/core/fsm/bdd/bddInt.h"

/*!
  \struct BddFsm
*/
typedef struct BddFsm_TAG
{
  /* private members */
  DDMgr_ptr dd;
  SymbTable_ptr symb_table;
  BddEnc_ptr  enc;

  BddStates      init;

  BddInvarStates invar_states;
  BddInvarInputs invar_inputs;

  BddTrans_ptr trans;

  JusticeList_ptr    justice;
  CompassionList_ptr compassion;

  BddFsmCache_ptr cache;
} BddFsm;


#endif /* __NUSMV_CORE_FSM_BDD_BDD_FSM_PRIVATE_H__ */
