
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
  \author Alessandro Mariotti
  \brief Public interface of class 'PrinterSexpCore'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_NODE_PRINTERS_PRINTER_SEXP_CORE_H__
#define __NUSMV_CORE_NODE_PRINTERS_PRINTER_SEXP_CORE_H__


#include "nusmv/core/node/printers/PrinterBase.h" /* fix this */ 
#include "nusmv/core/utils/utils.h"

/*!
  \struct PrinterSexpCore
  \brief Definition of the public accessor for class PrinterSexpCore

  
*/
typedef struct PrinterSexpCore_TAG*  PrinterSexpCore_ptr;

/*!
  \brief To cast and check instances of class PrinterSexpCore

  These macros must be used respectively to cast and to check
  instances of class PrinterSexpCore
*/
#define PRINTER_SEXP_CORE(self) \
         ((PrinterSexpCore_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PRINTER_SEXP_CORE_CHECK_INSTANCE(self) \
         (nusmv_assert(PRINTER_SEXP_CORE(self) != PRINTER_SEXP_CORE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof PrinterSexpCore
  \brief The PrinterSexpCore class constructor

  The PrinterSexpCore class constructor

  \sa PrinterSexpCore_destroy
*/
PrinterSexpCore_ptr PrinterSexpCore_create(const NuSMVEnv_ptr env,
                                                  const char* name);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_NODE_PRINTERS_PRINTER_SEXP_CORE_H__ */
