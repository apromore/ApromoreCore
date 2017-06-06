/* ---------------------------------------------------------------------------


  This file is part of the ``fsm'' package of NuSMV version 2.
  Copyright (C) 2008 by FBK-irst.

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
  \brief Public interfaces for package fsm

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_FSM_FSM_H__
#define __NUSMV_CORE_FSM_FSM_H__


#include "nusmv/core/utils/utils.h"

/*!
  \brief The possible types of an Fsm


*/

typedef enum FsmType_TAG {
  FSM_TYPE_SEXP = 1,
  FSM_TYPE_BOOL_SEXP = FSM_TYPE_SEXP << 1,
  FSM_TYPE_BDD = FSM_TYPE_SEXP << 2,
  FSM_TYPE_BE = FSM_TYPE_SEXP << 3,
} FsmType;

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Package initialization


*/
void Fsm_init(void);

/*!
  \brief Package deinitialization


*/
void Fsm_quit(void);

#endif /* __FSM_INT_H__ */
