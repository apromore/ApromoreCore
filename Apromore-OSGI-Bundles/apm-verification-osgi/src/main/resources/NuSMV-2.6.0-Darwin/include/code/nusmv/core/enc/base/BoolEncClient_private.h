
/* ---------------------------------------------------------------------------


  This file is part of the ``enc.base'' package of NuSMV version 2. 
  Copyright (C) 2004 by FBK-irst. 

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
  \brief Private and protected interface of class 'BoolEncClient'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_ENC_BASE_BOOL_ENC_CLIENT_PRIVATE_H__
#define __NUSMV_CORE_ENC_BASE_BOOL_ENC_CLIENT_PRIVATE_H__


#include "nusmv/core/enc/base/BoolEncClient.h" 

#include "nusmv/core/enc/base/BaseEnc.h" 
#include "nusmv/core/enc/base/BaseEnc_private.h" 
#include "nusmv/core/enc/bool/BoolEnc_private.h"

#include "nusmv/core/compile/symb_table/SymbTable.h"

#include "nusmv/core/utils/utils.h" 


/*!
  \brief BoolEncClient class definition derived from
               class BaseEnc

  

  \sa Base class BaseEnc
*/

typedef struct BoolEncClient_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(BaseEnc); 

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  BoolEnc_ptr bool_enc;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} BoolEncClient;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */
/*!
  \methodof BoolEncClient
  \todo
*/
void bool_enc_client_init(BoolEncClient_ptr self, 
                          SymbTable_ptr symb_table, 
                          BoolEnc_ptr bool_enc);

/*!
  \methodof BoolEncClient
  \todo
*/
void bool_enc_client_deinit(BoolEncClient_ptr self);

void bool_enc_client_commit_layer(BaseEnc_ptr base_enc, 
                                  const char* layer_name);

void bool_enc_client_remove_layer(BaseEnc_ptr base_enc, 
                                  const char* layer_name);

#endif /* __NUSMV_CORE_ENC_BASE_BOOL_ENC_CLIENT_PRIVATE_H__ */
