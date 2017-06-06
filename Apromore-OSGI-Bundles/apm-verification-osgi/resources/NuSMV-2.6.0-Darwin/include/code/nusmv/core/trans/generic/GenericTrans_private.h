/* ---------------------------------------------------------------------------


  This file is part of the ``trans.generic'' package of NuSMV version 2.
  Copyright (C) 2003 by FBK-irst.

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
  \brief The private interface of class GenericTrans

  Private definition to be used by derived classes

*/


#ifndef __NUSMV_CORE_TRANS_GENERIC_GENERIC_TRANS_PRIVATE_H__
#define __NUSMV_CORE_TRANS_GENERIC_GENERIC_TRANS_PRIVATE_H__


#include "nusmv/core/trans/generic/GenericTrans.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/EnvObject_private.h"


/*!
  \brief Transition Relation Class "GenericTrans"

   This class defines a prototype for a generic
  transition relation.
  This class is virtual, and must be specialized.
  
*/

typedef struct GenericTrans_TAG
{
  INHERITS_FROM(EnvObject);

  /* ---------------------------------------------------------------------- */
  /*     Private members                                                    */
  /* ---------------------------------------------------------------------- */
  TransType _type;

  /* ---------------------------------------------------------------------- */
  /*     Virtual Methods                                                    */
  /* ---------------------------------------------------------------------- */

} GenericTrans;


/*!
  \methodof GenericTrans
  \todo
*/
void generic_trans_init(GenericTrans_ptr self,
                        const NuSMVEnv_ptr env,
                        const TransType trans_type);

/*!
  \methodof GenericTrans
  \todo
*/
void generic_trans_deinit(GenericTrans_ptr self);

/*!
  \methodof GenericTrans
  \todo
*/
void generic_trans_copy_aux(const GenericTrans_ptr self,
                            GenericTrans_ptr copy);


#endif /* __NUSMV_CORE_TRANS_GENERIC_GENERIC_TRANS_PRIVATE_H__ */
