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
  \brief The public interface of the GenericTrans class

  Declares the public interface to manipulate generic trans
  objects, to be used as base class from more specific derived transition
  relation objects

*/


#ifndef __NUSMV_CORE_TRANS_GENERIC_GENERIC_TRANS_H__
#define __NUSMV_CORE_TRANS_GENERIC_GENERIC_TRANS_H__

#include "nusmv/core/trans/trans.h" /* for TransType */
#include "nusmv/core/cinit/NuSMVEnv.h"

/*!
  \struct GenericTrans
  \brief The structure used to represent the transition relation.

  
*/
typedef struct GenericTrans_TAG* GenericTrans_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define GENERIC_TRANS(x)  \
        ((GenericTrans_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define GENERIC_TRANS_CHECK_INSTANCE(x)  \
        (nusmv_assert(GENERIC_TRANS(x) != GENERIC_TRANS(NULL)))


/* ---------------------------------------------------------------------- */
/*     Public methods                                                     */
/* ---------------------------------------------------------------------- */

/*!
  \methodof GenericTrans
  \brief Builds the transition relation

  None of given arguments will become owned by self.
  You should destroy cl_options by yourself.

  \sa Object_destroy
*/
GenericTrans_ptr
GenericTrans_create(const NuSMVEnv_ptr env, const TransType trans_type);

/*!
  \methodof GenericTrans
  \brief Retrives the type of trans structure.

  Returns the type of the transition relation structure passed as
  the arguments. 
*/
TransType GenericTrans_get_type(const GenericTrans_ptr self);


#endif /* __NUSMV_CORE_TRANS_GENERIC_GENERIC_TRANS_H__ */
