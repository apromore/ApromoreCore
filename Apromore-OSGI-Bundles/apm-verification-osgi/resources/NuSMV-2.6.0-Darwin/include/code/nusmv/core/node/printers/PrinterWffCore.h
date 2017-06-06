/* ---------------------------------------------------------------------------


  This file is part of the ``node.printers'' package of NuSMV version 2.
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
  \brief Public interface of class 'PrinterWffCore'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_NODE_PRINTERS_PRINTER_WFF_CORE_H__
#define __NUSMV_CORE_NODE_PRINTERS_PRINTER_WFF_CORE_H__


#include "nusmv/core/node/printers/PrinterBase.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct PrinterWffCore
  \brief Definition of the public accessor for class PrinterWffCore


*/
typedef struct PrinterWffCore_TAG*  PrinterWffCore_ptr;

/*!
  \brief To cast and check instances of class PrinterWffCore

  These macros must be used respectively to cast and to check
  instances of class PrinterWffCore
*/
#define PRINTER_WFF_CORE(self) \
         ((PrinterWffCore_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PRINTER_WFF_CORE_CHECK_INSTANCE(self) \
         (nusmv_assert(PRINTER_WFF_CORE(self) != PRINTER_WFF_CORE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof PrinterWffCore
  \brief The PrinterWffCore class constructor

  The PrinterWffCore class constructor

  \sa PrinterWffCore_destroy
*/
PrinterWffCore_ptr PrinterWffCore_create(const NuSMVEnv_ptr env,
                                         const char* name);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_NODE_PRINTERS_PRINTER_WFF_CORE_H__ */
