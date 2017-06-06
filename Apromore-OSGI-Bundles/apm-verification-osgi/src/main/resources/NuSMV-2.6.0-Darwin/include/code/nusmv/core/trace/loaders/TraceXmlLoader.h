/* ---------------------------------------------------------------------------


  This file is part of the ``trace.plugins'' package of NuSMV version 2.
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
  \brief The header file for the TraceXmlLoader class

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_LOADERS_TRACE_XML_LOADER_H__
#define __NUSMV_CORE_TRACE_LOADERS_TRACE_XML_LOADER_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include "nusmv/core/trace/loaders/TraceLoader.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct TraceXmlLoader
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct TraceXmlLoader_TAG* TraceXmlLoader_ptr;

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_XML_LOADER(x) \
         ((TraceXmlLoader_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_XML_LOADER_CHECK_INSTANCE(x) \
         (nusmv_assert(TRACE_XML_LOADER(x) != TRACE_XML_LOADER(NULL)))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof TraceXmlLoader
  \brief Constructor

  
*/
TraceXmlLoader_ptr
TraceXmlLoader_create(const char* xml_filename,
                      boolean halt_on_undefined_symbols,
                      boolean halt_on_wrong_section);

/**AutomaticEnd***************************************************************/

#endif /* __TRACE_XML__H */

