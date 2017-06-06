/* ---------------------------------------------------------------------------


  This file is part of the ``enc.bdd'' package of NuSMV version 2. 
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
  \brief The Bdd encoding package public interface

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_ENC_BDD_BDD_H__
#define __NUSMV_CORE_ENC_BDD_BDD_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/dd/dd.h"

/*!
  \brief The BddVarSet type 

  A VarSet is a set of variables represented as a list of BDDs
*/
typedef bdd_ptr BddVarSet_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_VAR_SET(x) \
        ((BddVarSet_ptr) x)


/* ---------------------------------------------------------------------- */
/* Public methods                                                         */
/* ---------------------------------------------------------------------- */


#endif /* __NUSMV_CORE_ENC_BDD_BDD_H__ */
