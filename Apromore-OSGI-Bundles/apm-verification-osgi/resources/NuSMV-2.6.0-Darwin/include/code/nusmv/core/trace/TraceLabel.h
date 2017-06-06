/* ---------------------------------------------------------------------------


  This file is part of the ``trace'' package of NuSMV version 2.
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
  \brief The header file for the TraceLabel class.

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_TRACE_LABEL_H__
#define __NUSMV_CORE_TRACE_TRACE_LABEL_H__

#include "nusmv/core/node/node.h"
#include "nusmv/core/fsm/bdd/BddFsm.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef node_ptr TraceLabel;

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
#define TRACE_LABEL(x) \
          ((node_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_LABEL_CHECK_INSTANCE(x) \
          (nusmv_assert(TRACE_LABEL(x) != TRACE_LABEL(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_LABEL_INVALID Nil

/**AutomaticStart*************************************************************/
/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/* TraceLabel Constructors */

/*!
  \brief TraceLabel Constructor

  returns a label for the specified trace and state index.

  \sa TraceLabel_create_from_string
*/
TraceLabel TraceLabel_create(NodeMgr_ptr nodemgr,
                                    int trace_id, int state_id);

/*!
  \brief TraceLabel Constructor

  creates the label from the specified string. In case of any
  error, it returns TRACE_LABEL_INVALID as result.

  The string 'str' should follow this format: ^\s*(\d+)\s*\.\s*(-?\d+)$ in which
  the first group matches the trace number and the second matches the state
  number.

  TODO[AMi] This function can be merged to similar code in traceCmd.c
  

  \sa TraceLabel_create
*/
TraceLabel TraceLabel_create_from_string(NodeMgr_ptr nodemgr,
                                                const char* label_str);

/* TraceLabel Getters */

/*!
  \brief Returns the state index associated with the TraceLabel.

  

  \sa TraceLabel_get_trace
*/
int TraceLabel_get_state(TraceLabel self);

/*!
  \brief Returns the trace index associated with the TraceLabel.

  

  \sa TraceLabel_get_state
*/
int TraceLabel_get_trace(TraceLabel self);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_TRACE_TRACE_LABEL_H__  */
