/* ---------------------------------------------------------------------------


  This file is part of the ``node.printers'' package of NuSMV version 2.
  Copyright (C) 2006 by FBK-irst.

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
  \brief Public interface of class 'PrinterBase'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_NODE_PRINTERS_PRINTER_BASE_H__
#define __NUSMV_CORE_NODE_PRINTERS_PRINTER_BASE_H__


#include "nusmv/core/node/node.h"

#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct PrinterBase
  \brief Definition of the public accessor for class PrinterBase


*/
typedef struct PrinterBase_TAG*  PrinterBase_ptr;

/*!
  \brief To cast and check instances of class PrinterBase

  These macros must be used respectively to cast and to check
  instances of class PrinterBase
*/
#define PRINTER_BASE(self) \
         ((PrinterBase_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PRINTER_BASE_CHECK_INSTANCE(self) \
         (nusmv_assert(PRINTER_BASE(self) != PRINTER_BASE(NULL)))


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof PrinterBase
  \brief Prints the given node

  This is virtual method. BEfore calling, please ensure
  the given node can be handled by self, by calling
  PrinterBase_can_handle.

  Note: This method will be never called by the user

  \sa PrinterBase_can_handle
*/
VIRTUAL int
PrinterBase_print_node(PrinterBase_ptr self, node_ptr n, int priority);



/**AutomaticEnd***************************************************************/


#endif /* __NUSMV_CORE_NODE_PRINTERS_PRINTER_BASE_H__ */
