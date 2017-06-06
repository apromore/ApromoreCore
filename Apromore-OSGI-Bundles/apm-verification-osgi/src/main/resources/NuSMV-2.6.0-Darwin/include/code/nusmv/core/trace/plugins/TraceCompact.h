/* ---------------------------------------------------------------------------


  This file is part of the ``trace.plugins'' package of NuSMV version 2. 
  Copyright (C) 2011 by FBK-irst.

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
  \brief The header file for the TraceCompact class.

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_PLUGINS_TRACE_COMPACT_H__
#define __NUSMV_CORE_TRACE_PLUGINS_TRACE_COMPACT_H__

#include "nusmv/core/trace/plugins/TracePlugin.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct TraceCompact
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct TraceCompact_TAG* TraceCompact_ptr;

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
#define TRACE_COMPACT(x) \
	 ((TraceCompact_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_COMPACT_CHECK_INSTANCE(x) \
	 (nusmv_assert(TRACE_COMPACT(x) != TRACE_COMPACT(NULL)))

/**AutomaticStart*************************************************************/ 
/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof TraceCompact
  \brief Creates an Compact Plugin and initializes it.

  Compact plugin constructor. 
*/
TraceCompact_ptr TraceCompact_create(void);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_TRACE_PLUGINS_TRACE_COMPACT_H__ */

