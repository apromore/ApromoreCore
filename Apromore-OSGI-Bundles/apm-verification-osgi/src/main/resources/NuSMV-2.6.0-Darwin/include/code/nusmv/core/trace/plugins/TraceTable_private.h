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
  \brief The private header file for the TraceTable class.

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_PLUGINS_TRACE_TABLE_PRIVATE_H__
#define __NUSMV_CORE_TRACE_PLUGINS_TRACE_TABLE_PRIVATE_H__

#include "nusmv/core/trace/plugins/TracePlugin_private.h"
#include "nusmv/core/trace/plugins/TraceTable.h"

#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/enc/bdd/BddEnc.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/
/*!
  \brief TraceTable Class

   This class contains information to explain a trace:<br>
        <dl>
            <dt><code>state_vars_list </code>
                <dd> list of state variables.
            <dt><code>input_vars_list</code>
                <dd>  list of input variables.
            <dt><code> style </code>
                <dd> Printing style of the table. Available choices are:<br>
                      a) TRACE_TABLE_TYPE_ROW  <br>
                      b)TRACE_TABLE_TYPE_COLUMN <br>
  </dl>
  <br>
  This Class inherits from TracePlugin class.
  
*/

typedef struct TraceTable_TAG
{
  INHERITS_FROM(TracePlugin);

  TraceTableStyle style;
} TraceTable;

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
  \methodof TraceTable
  \todo
*/
void trace_table_init(TraceTable_ptr self, TraceTableStyle style);

/*!
  \methodof TraceTable
  \todo
*/
void trace_table_deinit(TraceTable_ptr self);

int trace_table_action(const TracePlugin_ptr plugin);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_TRACE_PLUGINS_TRACE_TABLE_PRIVATE_H__ */

