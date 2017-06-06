/* ---------------------------------------------------------------------------


  This file is part of the ``bmc.sbmc'' package of NuSMV version 2.
  Copyright (C) 2004 by Timo Latvala <timo.latvala@tkk.fi>.

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

  For more information of NuSMV see <http://nusmv.fbk.eu>
  or email to <nusmv-users@fbk.eu>.
  Please report bugs to <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to <nusmv@fbk.eu>. 

-----------------------------------------------------------------------------*/

/*!
  \author Timo Latvala
  \brief Public interface for the stack of node_ptr.

  A stack of node_ptr

*/

#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_NODE_STACK_H__

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define __NUSMV_CORE_BMC_SBMC_SBMC_NODE_STACK_H__

#include "nusmv/core/node/node.h" /*For node_ptr*/


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct nodeStack
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct nodeStack *Bmc_Stack_ptr;

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

struct nodeStack {
  /**Number of slots allocated*/
  unsigned alloc;
  /**Number of slots occupied*/
  unsigned first_free;
  /**The table*/
  node_ptr *table; 
};

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
  \brief Create a new stack

  Create a new stack

  \se None
*/
Bmc_Stack_ptr Bmc_Stack_new_stack(const NuSMVEnv_ptr env);

/*!
  \brief Push a node unto the stack

  Push a node unto the stack

  \se None
*/
void Bmc_Stack_push(Bmc_Stack_ptr, node_ptr);

/*!
  \brief Pop an element from the stack

  Pop an element from the stack

  \se None
*/
node_ptr Bmc_Stack_pop(Bmc_Stack_ptr);

/*!
  \brief Delete the stack

  Delete the stack

  \se None
*/
void Bmc_Stack_delete(Bmc_Stack_ptr stack);

/*!
  \brief Return the number of occupied slots

  Return the number of occupied slots

  \se None
*/
unsigned Bmc_Stack_size(Bmc_Stack_ptr stack);

/*!
  \brief Return the top element of the stack

  Return the top element of the stack

  \se None
*/
node_ptr Bmc_Stack_top(Bmc_Stack_ptr stack);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_SBMC_SBMC_NODE_STACK_H__*/
