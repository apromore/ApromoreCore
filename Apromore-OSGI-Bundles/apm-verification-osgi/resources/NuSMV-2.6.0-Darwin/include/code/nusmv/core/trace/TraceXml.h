/* ---------------------------------------------------------------------------


  This file is part of the ``trace'' package of NuSMV version 2.
  Copyright (C) 2010 by FBK.

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
  \brief The Trace xml header

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_TRACE_XML_H__
#define __NUSMV_CORE_TRACE_TRACE_XML_H__

#include "nusmv/core/utils/utils.h"
/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief Numeric values for all possible tags that can occur in the xml
               representation

  
*/

typedef enum TraceXmlTag_TAG
{
  TRACE_XML_INVALID_TAG = -1,
  TRACE_XML_CNTX_TAG    =  0,
  TRACE_XML_NODE_TAG,
  TRACE_XML_STATE_TAG,
  TRACE_XML_COMB_TAG,
  TRACE_XML_INPUT_TAG,
  TRACE_XML_VALUE_TAG,
  TRACE_XML_LOOPS_TAG
} TraceXmlTag;

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_XML_CNTX_TAG_STRING   "counter-example"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_XML_NODE_TAG_STRING   "node"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_XML_STATE_TAG_STRING  "state"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_XML_COMB_TAG_STRING   "combinatorial"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_XML_INPUT_TAG_STRING  "input"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_XML_VALUE_TAG_STRING  "value"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_XML_LOOPS_TAG_STRING  "loops"


/**AutomaticStart*************************************************************/

/**AutomaticEnd***************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

TraceXmlTag TraceXmlTag_from_string(const char* tag); 
const char* TraceXmlTag_to_string(TraceXmlTag tag);

#endif /* __NUSMV_CORE_TRACE_TRACE_XML_H__ */
