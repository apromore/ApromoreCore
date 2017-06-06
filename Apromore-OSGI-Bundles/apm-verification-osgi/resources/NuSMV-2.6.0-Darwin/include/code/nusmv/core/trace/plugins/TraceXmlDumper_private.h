/* ---------------------------------------------------------------------------


  This file is part of the ``trace.plugins'' package of NuSMV version 2.
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
  \brief The private header file for the TraceXmldumper class.

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_PLUGINS_TRACE_XML_DUMPER_PRIVATE_H__
#define __NUSMV_CORE_TRACE_PLUGINS_TRACE_XML_DUMPER_PRIVATE_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include "nusmv/core/trace/pkg_traceInt.h"
#include "nusmv/core/trace/plugins/TracePlugin_private.h"

#include "nusmv/core/trace/plugins/TraceXmlDumper.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief This is a plugin that dumps the XML representation of a trace

  The member "is_embedded" controls if the xml printed will be a
  subtree of a containg element or a full xml document
*/

typedef struct TraceXmlDumper_TAG
{
  INHERITS_FROM(TracePlugin);

  boolean is_embedded;

} TraceXmlDumper;

/*!
  \struct XmlNodes
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct XmlNodes_TAG* XmlNodes_ptr;

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/*!
  \methodof TraceXmlDumper
  \todo
*/
void trace_xml_dumper_init(TraceXmlDumper_ptr self,
                           boolean is_embedded);

/*!
  \methodof TraceXmlDumper
  \todo
*/
void trace_xml_dumper_deinit(TraceXmlDumper_ptr self);

int trace_xml_dumper_action(TracePlugin_ptr plugin);

/*!
  \methodof TraceXmlDumper
  \todo
*/
void trace_xml_dumper_print_symbol(TracePlugin_ptr self, node_ptr symb);
/*!
  \methodof TraceXmlDumper
  \todo
*/
void trace_xml_dumper_print_assignment(TracePlugin_ptr self,
                                       node_ptr symb, node_ptr val);


/**AutomaticEnd***************************************************************/

#endif /* __TRACE_XML_DUMPER_PRIVATE__H */

