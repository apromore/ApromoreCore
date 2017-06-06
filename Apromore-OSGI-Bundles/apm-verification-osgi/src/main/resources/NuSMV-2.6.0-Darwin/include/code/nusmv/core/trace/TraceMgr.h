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
  \brief The header file for the <tt>TraceMgr</tt> class.

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_TRACE_MGR_H__
#define __NUSMV_CORE_TRACE_TRACE_MGR_H__

#include "nusmv/core/dd/dd.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/array.h"
#include "nusmv/core/utils/error.h"
#include "nusmv/core/trace/plugins/TracePlugin.h"
#include "nusmv/core/trace/exec/PartialTraceExecutor.h"
#include "nusmv/core/trace/exec/CompleteTraceExecutor.h"
#include "nusmv/core/trace/eval/BaseEvaluator.h"
#include "nusmv/core/trace/TraceLabel.h"
#include "nusmv/core/trace/TraceOpt.h"
#include "nusmv/core/node/node.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_MGR_DEFAULT_PLUGIN -1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_MGR_LAST_TRACE -1

/*!
  \brief Environment handles for trace plugins

  Environment handles to retrieve the right index of a trace
  plugin, thus avoiding to use magic numbers.

  WARNING:
  The indexes retrieved from the environment with these handles have to be
  lowered of one. This is necessary because a plugin can have a 0 index, that
  clashes with the pointer NULL value

  \se required

  \sa optional
*/
#define ENV_TRACE_EXPLAINER_CHANGES_ONLY "env_trace_explainer_changes_only"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_TRACE_EXPLAINER "env_trace_explainer"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_TRACE_TABLE_COLUMN "env_trace_table_column"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_TRACE_TABLE_ROW "env_trace_table_row"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_TRACE_XML_DUMPER "env_trace_xml_dumper"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_TRACE_COMPACT "env_trace_compact"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_TRACE_EMBEDDED_XML_DUMPER "env_trace_embedded_xml_dumper"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_TRACE_EMPTY_INDEX "etraceemptyindex"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

/*!
  \struct TraceMgr
  \brief TraceMgr Class

   This class contains informations about TraceMgr:<br>
        <dl>
        <dt><code>trace_list</code>
            <dd>  List of Traces registered with TraceMgr.
        <dt><code>plugin_list</code>
            <dd>  List of plugins registered with TraceMgr.
        <dt><code>layer_names</code>
            <dd>  List of symb layers registered with TraceMgr.
        <dt><code>complete_trace_executors</code>
            <dd>  Dictionary str->object of complete trace executors
            registered with TraceMgr.
        <dt><code>partial_trace_executors</code>
            <dd>  Dictionary str->object of partial trace executors
            registered with TraceMgr.
        <dt><code>evaluator</code>
            <dd>  Currently registered evaluator.
        <dt><code>default_opt</code>
            <dd>  Internal TraceOpt object.
        <dt><code>current_trace_number</code>
            <dd>  Index of the current trace.
        <dt><code>default_plugin</code>
            <dd>  default plugin to print traces.
        <dt><code>internal_plugins_num</code>
            <dd> The number of plugins registered within NuSMV. All
                 the possibly existing other external plugins will be
                 assigned to indices greater or equal to this value.
        </dl>
        <br>

*/
typedef struct TraceMgr_TAG* TraceMgr_ptr;

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
#define TRACE_MGR(x) \
        ((TraceMgr_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_MGR_CHECK_INSTANCE(x) \
        (nusmv_assert(TRACE_MGR(x) != TRACE_MGR(NULL)))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototype                                                        */
/*---------------------------------------------------------------------------*/

/* TraceMgr Constructor/Destructor */

/*!
  \methodof TraceMgr
  \brief Initializes the TraceMgr.


*/
TraceMgr_ptr TraceMgr_create(NuSMVEnv_ptr env);

/*!
  \methodof TraceMgr
  \brief Destroys the TraceMgr with all the registered traces and
  plugins


*/
void TraceMgr_destroy(TraceMgr_ptr self);

/* TraceMgr Getters */

/*!
  \methodof TraceMgr
  \brief Returns the size of the TraceMgr.

  This function returns the number of traces registered with
  traceManager
*/
int TraceMgr_get_size(const TraceMgr_ptr self);

/*!
  \methodof TraceMgr
  \brief Returns the trace stored at given index


*/
Trace_ptr
TraceMgr_get_trace_at_index(const TraceMgr_ptr self,
                            int index);

/* TraceMgr register functions */

/*!
  \methodof TraceMgr
  \brief Registers a trace with TraceMgr.

  It registers a trace with the TraceMgr and returns
  the corresponding index. The given trace can not be previously
  registered with any Trace Manager.

  OWNERSHIP: "trace" is now owned by the TraceMgr
*/
int TraceMgr_register_trace(TraceMgr_ptr self,
                                   Trace_ptr trace);

/* Evaluators management */

/*!
  \methodof TraceMgr
  \brief Retrieves currently registered evaluator


*/
BaseEvaluator_ptr
TraceMgr_get_evaluator(TraceMgr_ptr self);

/*!
  \methodof TraceMgr
  \brief Registers an evaluator

  Registers an evaluator. If some evaluator was already
  registered it is destroyed
*/
void
TraceMgr_register_evaluator(TraceMgr_ptr self,
                            BaseEvaluator_ptr eval);

/*!
  \methodof TraceMgr
  \brief Unregisters current evaluator

  Unregisters currently registered evaluator. If some
  evaluator was already registered it is destroyed. If no evaluator
  was registered, no action is performed
*/
void TraceMgr_unregister_evaluator(TraceMgr_ptr self);

/* Complete trace executors management functions */

/*!
  \methodof TraceMgr
  \brief Registers a complete trace executor plugin with TraceMgr.

  It registers a complete trace executor with the
  TraceMgr and returns the corresponding index

  \se A previously registered executor (if any existing) is destroyed

  \sa TraceMgr_register_partial_trace_executor
*/
void
TraceMgr_register_complete_trace_executor(TraceMgr_ptr self,
       const char* executor_name, const char* executor_desc,
       const CompleteTraceExecutor_ptr executor);

/*!
  \methodof TraceMgr
  \brief Retrieves a registered complete trace executor instance
  with given name

  Returns a valid complete trace executor instance if any
  suitable such object has been previously registered with given name,
  using TraceMgr_register_complete_trace_executor. If no such
  object is found, NULL is returned

  \se none
*/
CompleteTraceExecutor_ptr
TraceMgr_get_complete_trace_executor(const TraceMgr_ptr self,
                                         const char* name);

/*!
  \methodof TraceMgr
  \brief Returns an array of registered complete trace executor
  IDs. IDs are alphabetically sorted using lexicographical ordering

  Returned array must be destroyed by the caller

  \sa TraceMgr_register_complete_trace_executor
*/
array_t*
TraceMgr_get_complete_trace_executor_ids(const TraceMgr_ptr self);

/*!
  \methodof TraceMgr
  \brief Retrieves description for a registered complete trace executor

  Retrieves description for a registered complete trace
  executor. The executor must have been previously registered within
  the trace manager using TraceMgr_register_complete_trace_executor.

  Trying to retrieve description for a non-registered executor results
  in an assertion failure.

  \se none
*/
const char*
TraceMgr_get_complete_trace_executor_desc(const TraceMgr_ptr self,
                                          const char* name);

/*!
  \brief Returns default registered complete trace executor

  Returns default registered complete trace executor, if
  any. If no executor has yet been registered NULL is returned.

  TODO[MP] default policy must be revised now it's lexicographic order


  \se none

  \sa TraceMgr_register_complete_trace_executor
*/
CompleteTraceExecutor_ptr
TraceMgr_get_default_complete_trace_executor(
      const TraceMgr_ptr global_trace_manager);

/* Partial trace executors management functions */

/*!
  \methodof TraceMgr
  \brief Registers a partial trace executor plugin with TraceMgr.

  It registers a partial trace executor with the
  TraceMgr and returns the corresponding index.
*/
void
TraceMgr_register_partial_trace_executor(TraceMgr_ptr self,
       const char* executor_name, const char* executor_desc,
       const PartialTraceExecutor_ptr executor);

/*!
  \methodof TraceMgr
  \brief Returns an array of registered partial trace executor
  IDs. IDs are alphabetically sorted using lexicographical ordering

  Returned array must be destroyed by the caller

  \sa TraceMgr_register_complete_trace_executor
*/
array_t*
TraceMgr_get_partial_trace_executor_ids(const TraceMgr_ptr self);

/*!
  \methodof TraceMgr
  \brief Retrieves partial trace registered with given name

  Retrieves description for a registered partial trace
  executor. The executor must have been previously registered within
  the trace manager using TraceMgr_register_complete_trace_executor.

  Trying to retrieve description for a non-registered executor results
  in an assertion failure.

  \se none
*/
PartialTraceExecutor_ptr
TraceMgr_get_partial_trace_executor(const TraceMgr_ptr self,
                                    const char* name);

/*!
  \methodof TraceMgr
  \brief Retrieves description for partial trace executor

  Retrieves description for partial trace executor
  registered with given name, or NULL if no such executor exists

  \se none
*/
const char*
TraceMgr_get_partial_trace_executor_desc(const TraceMgr_ptr self,
                                         const char* name);

/*!
  \brief Returns default registered partial trace executor

  Returns default registered partial trace executor, if
  any. If no executor has yet been registered NULL is returned.

  TODO[MP] default policy must be revised now it's lexicographic order

  \se none

  \sa TraceMgr_register_partial_trace_executor
*/
PartialTraceExecutor_ptr
TraceMgr_get_default_partial_trace_executor(const TraceMgr_ptr global_trace_manager);

/* Registration of layers */

/*!
  \methodof TraceMgr
  \brief Registers a new layer name to be used later by the
  explainers when printing symbols. Only the symbols into registered
  layers will be shown.

  Use this method to control which symbols will be shown
  when a trace is shown. Only symbols occurring inside registered
  layers will be presented by plugins. Warning: before renaming or
  deleting a previoulsy registered layer, the layer should be
  unregistered. If not unregistered, the behaviour is unpredictable.

  \sa unregister_layer
*/
void
TraceMgr_register_layer(TraceMgr_ptr self,
                        const char* layer_name);

/*!
  \methodof TraceMgr
  \brief Unregisters a previoulsy registered layer

  The given layer must be registered before calling this method,
  otherwise an internal error occurs

  \sa register_layer
*/
void
TraceMgr_unregister_layer(TraceMgr_ptr self,
                          const char* layer_name);

/*!
  \methodof TraceMgr
  \brief Returns true if the given layer names was previously
  registered



  \sa unregister_layer
*/
boolean
TraceMgr_is_layer_registered(const TraceMgr_ptr self,
                             const char* layer_name);

/*!
  \methodof TraceMgr
  \brief Returns an array of names (strings) of the registered layers

  Returned array belongs to self, do not change or delete it
*/
array_t*
TraceMgr_get_registered_layers(const TraceMgr_ptr self);


/* Other Functions */

/*!
  \methodof TraceMgr
  \brief Sets trace_id as ths current trace of the TraceMgr.


*/
void
TraceMgr_set_current_trace_number(TraceMgr_ptr self,
                                      int trace_id);

/*!
  \methodof TraceMgr
  \brief Returns the trace_id of the current trace of the TraceMgr.


*/
int
TraceMgr_get_current_trace_number(TraceMgr_ptr self);

/*!
  \methodof TraceMgr
  \brief Checks whether a symbol is visible

  Returns true iff the symbol is visible according to the
  following criteria:

  1. symbol name does not contain the prefix defined in system
  variable traces_hiding_prefix.

  2. if system variable traces_regexp is not empty, (1)
  holds and symbol name matches the regexp described in
  traces_regexp

  \se none
*/
boolean
TraceMgr_is_visible_symbol(TraceMgr_ptr self, node_ptr symbol);

/*!
  \brief Shows the traces generated in a NuSMV session

  Shows the traces generated in a NuSMV session
*/
int TraceMgr_show_traces(TraceMgr_ptr const self,
                                const int plugin_index,
                                const boolean is_all,
                                const int trace,
                                TraceOpt_ptr const trace_opt,
                                const int traceno,
                                int from_state,
                                int to_state);

/*!
  \brief Executes complete traces on the model FSM

  Execute the traces between first trace and last trace
*/
int
TraceMgr_execute_traces(TraceMgr_ptr const self,
                        CompleteTraceExecutor_ptr const executor,
                        const int first_trace,
                        const int last_trace);

/*!
  \brief Executes partial traces on the model FSM

  Execute the traces between first trace and last trace
*/
int
TraceMgr_execute_partial_traces(TraceMgr_ptr const self,
                                PartialTraceExecutor_ptr const executor,
                                const int first_trace,
                                const int last_trace);

/* Functions related to Labels */

/*!
  \methodof TraceMgr
  \brief Checks if the label is valid label in a registered trace.

  This function can be safely used to determine whether a
  label denotes a valid <trace, state> pair. This is guaranteed to
  raise no errors (exceptions, assertions) and should be used before
  any other label-related function to avoid any subsequent failure.
*/
boolean
TraceMgr_is_label_valid(TraceMgr_ptr self, TraceLabel label);

/*!
  \methodof TraceMgr
  \brief Returns a trace iterator pointing to the particular trace step
               indicated by the given label.


*/
TraceIter
TraceMgr_get_iterator_from_label(TraceMgr_ptr self,
                                     TraceLabel label);

/*!
  \methodof TraceMgr
  \brief Returns the absolute state index pointed by the label.


*/
int
TraceMgr_get_abs_index_from_label(TraceMgr_ptr self,
                                      TraceLabel label);

/* Interaction with trace plugins *********************************************/

/*!
  \methodof TraceMgr
  \brief Returns the total number of plugins registered with
  TraceMgr.

  This function returns the total number of plugins
  registered with traceManager
*/
int TraceMgr_get_plugin_size(const TraceMgr_ptr self);

/*!
  \methodof TraceMgr
  \brief Returns the number of internal plugins registered with
  TraceMgr.

  This function returns the number of internal plugins
  registered with traceManager
*/
int
TraceMgr_get_internal_plugin_size(const TraceMgr_ptr self);

/*!
  \methodof TraceMgr
  \brief Returns the plugin stored at given index


*/
TracePlugin_ptr
TraceMgr_get_plugin_at_index(const TraceMgr_ptr self,
                                 int index);

/*!
  \methodof TraceMgr
  \brief Registers default plugins.

  Statically registers available plugins
*/
void TraceMgr_init_plugins(TraceMgr_ptr self);

/*!
  \methodof TraceMgr
  \brief Registers a plugin with TraceMgr.

  It registers a plugin with the TraceMgr and returns the
  corresponding index.
*/
int TraceMgr_register_plugin(TraceMgr_ptr self,
                                        TracePlugin_ptr plugin);

/*!
  \methodof TraceMgr
  \brief Executes the given trace plugin on given trace

  \"opt\" is either a valid TraceOpt instance or NULL. Defaults
               are provided by the trace manager in the latter case.

               plugin_index is either a non-negative integer, to which
               must correspond a registered plugin, or a negative
               integer. Default plugin is used in the latter case.

               trace_index is either a non_negative integerm to which
               must correspond a valid registered trace, or a negative
               integer. Last registered trace is used in the latter case.

               The return value is the one provided by TracePlugin_action, so
               -1 is error raised by the ErrorMgr, 0 should be normal error, 1
               is success. But this is not respected.

  \se none

  \sa TRACE_MGR_DEFAULT_PLUGIN, TRACE_MGR_LAST_TRACE
*/
int TraceMgr_execute_plugin(const TraceMgr_ptr self,
                                       const TraceOpt_ptr opt,
                                       int plugin_index,
                                       int trace_index);

/*!
  \methodof TraceMgr
  \brief Sets plugin_id as ths default_plugin of the TraceMgr.

   Default plugin is the plugin to be used to print a trace by
  default.
*/
void
TraceMgr_set_default_plugin(TraceMgr_ptr self,
                                int plugin_id);

/*!
  \methodof TraceMgr
  \brief Returns the index of the default plugin of the TraceMgr.


*/
int
TraceMgr_get_default_plugin(TraceMgr_ptr self);

/*!
  \brief Lists out all the available plugins inside the system.

  Lists out all the available plugins inside the system.
*/
int TraceMgr_show_plugins(TraceMgr_ptr const self,
                                 const boolean is_show_all,
                                 const int dp);

/*!
  \methodof TraceMgr
  \brief Returns true if the plugin whose index is provided is
  internal to NuSMV. It returns false if the given plugin has been
  externally registered.


*/
boolean
TraceMgr_is_plugin_internal(const TraceMgr_ptr self, int index);

/******************************************************************************/


/**AutomaticEnd***************************************************************/


#endif /* __NUSMV_CORE_TRACE_TRACE_MGR_H__ */
