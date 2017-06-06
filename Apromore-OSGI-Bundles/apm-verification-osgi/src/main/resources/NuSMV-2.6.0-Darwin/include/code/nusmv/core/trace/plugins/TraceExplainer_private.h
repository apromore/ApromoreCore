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
  \brief The private header file for the TraceExplainer class.

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_PLUGINS_TRACE_EXPLAINER_PRIVATE_H__
#define __NUSMV_CORE_TRACE_PLUGINS_TRACE_EXPLAINER_PRIVATE_H__

#include "nusmv/core/trace/plugins/TracePlugin_private.h"
#include "nusmv/core/trace/plugins/TraceExplainer.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief TraceExplainer Class

   This class contains information to explain a trace:<br>
        <dl>
            <dt><code>changes_only</code>
                <dd> If this field is true, then explainer will print only
                those variables whose value changes.
  </dl>
  <br>
  This Class inherits from TracePlugin class.
  
*/

typedef struct TraceExplainer_TAG
{
  INHERITS_FROM(TracePlugin);

  boolean changes_only;
} TraceExplainer;

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/*!
  \methodof TraceExplainer
  \todo
*/
void trace_explainer_init(TraceExplainer_ptr self,
                          boolean changes_only);

/*!
  \methodof TraceExplainer
  \todo
*/
void trace_explainer_deinit(TraceExplainer_ptr self);

int trace_explainer_action(const TracePlugin_ptr plugin);

/**AutomaticEnd***************************************************************/

#endif /* __TRACE_EXPLAIN_PRIVATE__H */

