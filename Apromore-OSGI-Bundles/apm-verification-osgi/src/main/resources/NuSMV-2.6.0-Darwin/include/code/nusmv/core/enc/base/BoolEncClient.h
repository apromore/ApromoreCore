
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
  \brief Public interface of pure base class 'BoolEncClient'

  This is a base class for all those encoders that need to 
  access an instance of BoolEnc for their operations.

*/



#ifndef __NUSMV_CORE_ENC_BASE_BOOL_ENC_CLIENT_H__
#define __NUSMV_CORE_ENC_BASE_BOOL_ENC_CLIENT_H__


#include "nusmv/core/enc/bool/BoolEnc.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct BoolEncClient
  \brief Definition of the public accessor for class BoolEncClient

  
*/
typedef struct BoolEncClient_TAG*  BoolEncClient_ptr;

/*!
  \brief To cast and check instances of class BoolEncClient

  These macros must be used respectively to cast and to check
  instances of class BoolEncClient
*/
#define BOOL_ENC_CLIENT(self) \
         ((BoolEncClient_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BOOL_ENC_CLIENT_CHECK_INSTANCE(self) \
         (nusmv_assert(BOOL_ENC_CLIENT(self) != BOOL_ENC_CLIENT(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BoolEncClient
  \brief Returns the contained (used) BoolEnc instance

  REturned instance belongs to self, do not 
  destroy it.
*/
BoolEnc_ptr 
BoolEncClient_get_bool_enc(const BoolEncClient_ptr self);

/*!
  \brief The BoolEncClient class destructor

  The BoolEncClient class destructor. Since this class is
  pure, only a virtual destructor is provided, and there is no
  constructor
*/
VIRTUAL 
/*!
  \methodof BoolEncClient
  \todo
*/
void BoolEncClient_destroy(BoolEncClient_ptr self);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_ENC_BASE_BOOL_ENC_CLIENT_H__ */
