/* ---------------------------------------------------------------------------


  This file is part of the ``trace.plugins'' package of NuSMV version 2.
  Copyright (C) 2003 by FBK-irst.

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
  \author Ashutosh Trivedi
  \brief The header file for the TraceExplainer class.

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_PLUGINS_TRACE_EXPLAINER_H__
#define __NUSMV_CORE_TRACE_PLUGINS_TRACE_EXPLAINER_H__

#include "nusmv/core/trace/plugins/TracePlugin.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct TraceExplainer
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct TraceExplainer_TAG* TraceExplainer_ptr;

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
#define TRACE_EXPLAINER(x) \
         ((TraceExplainer_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_EXPLAINER_CHECK_INSTANCE(x) \
         (nusmv_assert(TRACE_EXPLAINER(x) != TRACE_EXPLAINER(NULL)))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof TraceExplainer
  \brief Creates an Explainer Plugin and initializes it.

  Explainer plugin constructor. As arguments it takes the boolean
  variable /"changes_only/". If <tt>changes_only</tt> is 1, than only state
  variables which assume a different value from the previous printed one are
  printed out.
*/
TraceExplainer_ptr
TraceExplainer_create(boolean changes_only);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_TRACE_PLUGINS_TRACE_EXPLAINER_H__ */

