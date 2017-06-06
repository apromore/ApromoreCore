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
  \brief The private interface of class TracePlugin

  Private definition to be used by derived classes

*/

#ifndef __NUSMV_CORE_TRACE_PLUGINS_TRACE_PLUGIN_PRIVATE_H__
#define __NUSMV_CORE_TRACE_PLUGINS_TRACE_PLUGIN_PRIVATE_H__

#include "nusmv/core/trace/plugins/TracePlugin.h"
#include "nusmv/core/trace/TraceOpt.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/object_private.h"

#include "nusmv/core/node/anonymizers/NodeAnonymizerBase.h"
#include "nusmv/core/node/anonymizers/NodeAnonymizerST.h"

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief TracePlugin Class

  This class defines a prototype for a generic
               TracePlugin. This class is virtual and must be
               specialized.
*/

typedef struct TracePlugin_TAG
{
  INHERITS_FROM(Object);

  char* desc; /* Short description of the plugin */

  /* current trace */
  Trace_ptr trace;

  /* options from the caller */
  TraceOpt_ptr opt;

  /* used for filtering */
  hash_ptr visibility_map;

  /* used for obfuscation */
  NodeAnonymizerBase_ptr anonymizer;

  /* ---------------------------------------------------------------------- */
  /*     Virtual Methods                                                    */
  /* ---------------------------------------------------------------------- */

  /* action */
  VIRTUAL int (*action)(const TracePlugin_ptr self);

  /* protected virtual methods for printing */
  VIRTUAL void (*print_symbol)(const TracePlugin_ptr self, node_ptr symbol);

  VIRTUAL void (*print_list)(const TracePlugin_ptr self, node_ptr list);

  VIRTUAL void (*print_assignment)(const TracePlugin_ptr self,
                                   node_ptr symbol, node_ptr val);
} TracePlugin;

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/


/* protected methods */

/*!
  \methodof TracePlugin
  \brief 

  
*/
VIRTUAL void
TracePlugin_print_symbol(const TracePlugin_ptr self, node_ptr symb);

/*!
  \methodof TracePlugin
  \brief 

  
*/
VIRTUAL void
TracePlugin_print_list(const TracePlugin_ptr self, node_ptr list);

/*!
  \methodof TracePlugin
  \brief 

  
*/
VIRTUAL void
TracePlugin_print_assignment(const TracePlugin_ptr self,
                             node_ptr symb, node_ptr val);

/*!
  \methodof TracePlugin
  \brief Check that node is printable

  
*/
boolean trace_plugin_is_visible_symbol(TracePlugin_ptr self,
                                              node_ptr symb);

/*!
  \methodof TracePlugin
  \brief 

  
*/
void trace_plugin_print_symbol(const TracePlugin_ptr self,
                                      node_ptr symbol);

/*!
  \methodof TracePlugin
  \brief 

  
*/
void trace_plugin_print_list(const TracePlugin_ptr self,
                                    node_ptr list);

/*!
  \methodof TracePlugin
  \brief 

  
*/
void trace_plugin_print_assignment(const TracePlugin_ptr self,
                                          node_ptr symb, node_ptr val);

/*!
  \methodof TracePlugin
  \brief This function initializes the plugin class.

  
*/
void trace_plugin_init(TracePlugin_ptr self, char* desc);

/*!
  \methodof TracePlugin
  \brief This function de-initializes the plugin class.

  
*/
void trace_plugin_deinit(TracePlugin_ptr self);

/*!
  \methodof TracePlugin
  \brief Action associated with the Class action.

  It is a pure virtual function and TracePlugin is an abstract
  base class. Every derived class must ovewrwrite this function. It returns 1
  if operation is successful, 0 otherwise.
  MD: This is not true at least for trace_explainer
*/
int trace_plugin_action(const TracePlugin_ptr self);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_TRACE_PLUGINS_TRACE_PLUGIN_PRIVATE_H__ */
