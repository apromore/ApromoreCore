/* ---------------------------------------------------------------------------


  This file is part of the ``node.printers'' package of NuSMV version 2. 
  Copyright (C) 2009 by FBK.

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
  \author Marco Pensallorto
  \brief Public interface of class 'PrinterIWffCore'

  An indenting subclass of PrinterWffCore

*/


#ifndef __NUSMV_CORE_NODE_PRINTERS_PRINTER_IWFF_CORE_H__
#define __NUSMV_CORE_NODE_PRINTERS_PRINTER_IWFF_CORE_H__

#include "nusmv/core/node/printers/PrinterBase.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct PrinterIWffCore
  \brief Definition of the public accessor for class PrinterIWffCore

  
*/
typedef struct PrinterIWffCore_TAG*  PrinterIWffCore_ptr;

/*!
  \brief To cast and check instances of class PrinterWffCore

  These macros must be used respectively to cast and to check
  instances of class PrinterIWffCore
*/
#define PRINTER_IWFF_CORE(self) \
         ((PrinterIWffCore_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PRINTER_IWFF_CORE_CHECK_INSTANCE(self) \
         (nusmv_assert(PRINTER_IWFF_CORE(self) != PRINTER_IWFF_CORE(NULL)))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof PrinterIWffCore
  \brief The PrinterIWffCore class constructor

  The PrinterIWffCore class constructor

  \sa PrinterIWffCore_destroy
*/
PrinterIWffCore_ptr PrinterIWffCore_create(const NuSMVEnv_ptr env,
                                                  const char* name);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_NODE_PRINTERS_PRINTER_IWFF_CORE_H__ */
