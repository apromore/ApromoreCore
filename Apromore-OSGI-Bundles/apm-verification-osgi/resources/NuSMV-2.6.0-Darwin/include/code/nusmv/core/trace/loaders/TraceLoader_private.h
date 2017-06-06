/* ---------------------------------------------------------------------------


  This file is part of the ``trace.loaders'' package of NuSMV version 2.
  Copyright (C) 2010 by FBK-irst.

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
  \brief The private interface of class TraceLoader

  Private definition to be used by derived classes

*/

#ifndef __NUSMV_CORE_TRACE_LOADERS_TRACE_LOADER_PRIVATE_H__
#define __NUSMV_CORE_TRACE_LOADERS_TRACE_LOADER_PRIVATE_H__

#include "nusmv/core/trace/loaders/TraceLoader.h"
#include "nusmv/core/compile/compile.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/object_private.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief TraceLoader Class

  This class defines a prototype for a generic
               TraceLoader. This class is virtual and must be
               specialized.
*/

typedef struct TraceLoader_TAG
{
  INHERITS_FROM(Object);

  char* desc; /* Short description of the loader */

  /* ---------------------------------------------------------------------- */
  /*     Virtual Methods                                                    */
  /* ---------------------------------------------------------------------- */

  /* action */
  VIRTUAL Trace_ptr (*load)(TraceLoader_ptr self, const SymbTable_ptr st,
                            const NodeList_ptr symbols);

} TraceLoader;

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof TraceLoader
  \brief This function initializes the loader class.

  
*/
void trace_loader_init(TraceLoader_ptr self, char* desc);

/*!
  \methodof TraceLoader
  \brief This function de-initializes the loader class.

  
*/
void trace_loader_deinit(TraceLoader_ptr self);

/*!
  \methodof TraceLoader
  \brief Action associated with the Class action.

  It is a pure virtual function and TraceLoader is an abstract
  base class. Every derived class must ovewrwrite this function. It returns 1
  if operation is successful, 0 otherwise.
*/
Trace_ptr trace_loader_load(TraceLoader_ptr self,
                                   const SymbTable_ptr st,
                                   const NodeList_ptr symbols);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_TRACE_LOADERS_TRACE_LOADER_PRIVATE_H__ */
