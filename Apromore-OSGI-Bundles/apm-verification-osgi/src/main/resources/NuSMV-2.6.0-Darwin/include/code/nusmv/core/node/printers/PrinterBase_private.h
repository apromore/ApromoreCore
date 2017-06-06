
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
  \brief Private and protected interface of class 'PrinterBase'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_NODE_PRINTERS_PRINTER_BASE_PRIVATE_H__
#define __NUSMV_CORE_NODE_PRINTERS_PRINTER_BASE_PRIVATE_H__


#include "nusmv/core/node/printers/PrinterBase.h"
#include "nusmv/core/node/NodeWalker_private.h"

#include "nusmv/core/utils/utils.h"


/*!
  \brief PrinterBase class definition derived from
               class NodeWalker



  \sa Base class Object
*/

typedef struct PrinterBase_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(NodeWalker);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  int (*print_node)(PrinterBase_ptr self, node_ptr n, int priority);

} PrinterBase;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only        */
/* ---------------------------------------------------------------------- */

/*!
  \methodof PrinterBase
  \brief Creates and initializes a printer.
  To be usable, the printer will have to be registered to a MasterPrinter.

  To each printer is associated a partition of
  consecutive indices over the symbols set. The lowest index of the
  partition is given through the parameter low, while num is the
  partition size. Name is used to easily identify printer instances.

  This constructor is private, as this class is virtual

  \sa PrinterBase_destroy
*/
PrinterBase_ptr
PrinterBase_create(const NuSMVEnv_ptr env, const char* name, int low,
                   size_t num);

/*!
  \methodof PrinterBase
  \brief The PrinterBase class private initializer

  The PrinterBase class private initializer

  \sa PrinterBase_create
*/
void
printer_base_init(PrinterBase_ptr self, const NuSMVEnv_ptr env,
                  const char* name, int low, size_t num,
                  boolean can_handle_null);

/*!
  \methodof PrinterBase
  \brief The PrinterBase class private deinitializer

  The PrinterBase class private deinitializer

  \sa PrinterBase_destroy
*/
void printer_base_deinit(PrinterBase_ptr self);

/*!
  \methodof PrinterBase
  \brief This method must be called by the virtual method
  print_node to recursively print sub nodes


*/
int
printer_base_throw_print_node(PrinterBase_ptr self,
                              node_ptr n, int priority);

/*!
  \methodof PrinterBase
  \brief Use this method to print a string to the stream currently
  set


*/
int
printer_base_print_string(PrinterBase_ptr self, const char* str);


#endif /* __NUSMV_CORE_NODE_PRINTERS_PRINTER_BASE_PRIVATE_H__ */
