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
  \brief Public interface of class 'PrinterNonAmbiguousDot'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_NODE_ANONYMIZERS_PRINTER_NON_AMBIGUOUS_DOT_H__
#define __NUSMV_CORE_NODE_ANONYMIZERS_PRINTER_NON_AMBIGUOUS_DOT_H__


#include "nusmv/core/node/printers/PrinterBase.h"
#include "nusmv/core/utils/defs.h"

/*!
  \struct PrinterNonAmbiguousDot
  \brief Definition of the public accessor for class PrinterNonAmbiguousDot

  
*/
typedef struct PrinterNonAmbiguousDot_TAG*  PrinterNonAmbiguousDot_ptr;

/*!
  \brief To cast and check instances of class PrinterNonAmbiguousDot

  These macros must be used respectively to cast and to check
  instances of class PrinterNonAmbiguousDot
*/
#define PRINTER_ANON_MAP_ENTRY(self) \
         ((PrinterNonAmbiguousDot_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PRINTER_ANON_MAP_ENTRY_CHECK_INSTANCE(self) \
         (nusmv_assert(PRINTER_ANON_MAP_ENTRY(self) != PRINTER_ANON_MAP_ENTRY(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof PrinterNonAmbiguousDot
  \brief The PrinterNonAmbiguousDot class constructor

  The PrinterNonAmbiguousDot class constructor

  \sa PrinterNonAmbiguousDot_destroy
*/
PrinterNonAmbiguousDot_ptr PrinterNonAmbiguousDot_create(NuSMVEnv_ptr env);

/*!
  \methodof PrinterNonAmbiguousDot
  \brief The PrinterNonAmbiguousDot class destructor

  The PrinterNonAmbiguousDot class destructor

  \sa PrinterNonAmbiguousDot_create
*/
void PrinterNonAmbiguousDot_destroy(PrinterNonAmbiguousDot_ptr self);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_NODE_ANONYMIZERS_PRINTER_NON_AMBIGUOUS_DOT_H__ */
