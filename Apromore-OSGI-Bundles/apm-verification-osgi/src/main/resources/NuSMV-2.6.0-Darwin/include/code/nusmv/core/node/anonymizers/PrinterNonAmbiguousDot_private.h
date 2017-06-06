/* ---------------------------------------------------------------------------


  This file is part of the ``core.node.anonymizer'' package of NuSMV version 2.
  Copyright (C) 2014 by FBK-irst.

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
  \author Michele Dorigatti
  \brief Private and protected interface of class 'PrinterNonAmbiguousDot'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_NODE_ANONYMIZERS_PRINTER_NON_AMBIGUOUS_DOT_PRIVATE_H__
#define __NUSMV_CORE_NODE_ANONYMIZERS_PRINTER_NON_AMBIGUOUS_DOT_PRIVATE_H__


#include "nusmv/core/node/anonymizers/PrinterNonAmbiguousDot.h"
#include "nusmv/core/node/printers/PrinterBase.h"
#include "nusmv/core/node/printers/PrinterBase_private.h"
#include "nusmv/core/utils/defs.h"


/*!
  \brief PrinterNonAmbiguousDot class definition derived from
               class PrinterBase

  

  \sa Base class PrinterBase
*/

typedef struct PrinterNonAmbiguousDot_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(PrinterBase);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} PrinterNonAmbiguousDot;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof PrinterNonAmbiguousDot
  \brief The PrinterNonAmbiguousDot class private initializer

  The PrinterNonAmbiguousDot class private initializer

  \sa PrinterNonAmbiguousDot_create
*/
void printer_anon_map_entry_init(PrinterNonAmbiguousDot_ptr self,
                                        const NuSMVEnv_ptr env,
                                        const char* name,
                                        int low,
                                        size_t num);

/*!
  \methodof PrinterNonAmbiguousDot
  \brief The PrinterNonAmbiguousDot class private deinitializer

  The PrinterNonAmbiguousDot class private deinitializer

  \sa PrinterNonAmbiguousDot_destroy
*/
void printer_anon_map_entry_deinit(PrinterNonAmbiguousDot_ptr self);

/*!
  \methodof PrinterNonAmbiguousDot
  \brief Virtual menthod that prints the given node

  
*/
int printer_anon_map_entry_print_node(PrinterBase_ptr self,
                                             node_ptr n,
                                             int priority);


#endif /* __NUSMV_CORE_NODE_ANONYMIZERS_PRINTER_NON_AMBIGUOUS_DOT_PRIVATE_H__ */
