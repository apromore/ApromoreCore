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
  \author Ashutosh Trivedi, Marco Pensallorto
  \brief The header file for the trace package.

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_PKG_TRACE_H__
#define __NUSMV_CORE_TRACE_PKG_TRACE_H__

#include "nusmv/core/trace/Trace.h"
#include "nusmv/core/trace/TraceMgr.h"
#include "nusmv/core/trace/exec/traceExec.h"
#include "nusmv/core/cinit/NuSMVEnv.h"
/* #include "trace/exec/TraceExecInfo.h" */



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

/* package */

/*!
  \brief Initializes the Trace Package.

  TraceMgr get initialized. 

  \sa TracePkg_quit
*/
void TracePkg_init(NuSMVEnv_ptr env);

/*!
  \brief Quits the Trace package.

  

  \sa TracePkg_init
*/
void TracePkg_quit(NuSMVEnv_ptr env);

/* self-test */
#if defined TRACE_DEBUG

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int TracePkg_test_package(FILE* out, FILE* err);
#endif

/* Filtering services for external packags */

/*!
  \brief Returns the filtered list of symbols

  Returned list is the result of filtering the input
  list, using standard filtering strategies that apply to symbols in
  traces.

  The returned list must be freed by the caller

  \sa TraceMgr_is_visible_symbol
*/
NodeList_ptr
TracePkg_get_filtered_symbols(TraceMgr_ptr gtm,
                              const NodeList_ptr symbols);

/* Trace Manager */

/*!
  \brief Returns the trace plugin currently selected as default

  Returns the trace plugin currently selected as default
*/
int TracePkg_get_default_trace_plugin(TraceMgr_ptr gtm);

/*!
  \brief Called when the user selects a trace plugin to be used as
               default

  Returns true upon success, false otherwise
*/
boolean TracePkg_set_default_trace_plugin(TraceMgr_ptr gtm,
                                                 int dp);

/* Trace execution */

/*!
  \brief Complete trace re-execution

  Complete trace re-execution.  In order to be run, the
  trace must be complete w.r.t. master fsm language. Returns 0 if a
  trace is executed successfully, and 1 otherwise.

  \se None
*/
int
Trace_execute_trace(const NuSMVEnv_ptr env,
                    const Trace_ptr trace,
                    const CompleteTraceExecutor_ptr exec_info);

/*!
  \brief Partial trace re-execution and fill-in

  Partial trace re-execution and fill-in.

                      Tries to complete the given trace using the
                      given incomplete trace executor.  If successful,
                      a complete trace is registered into the Trace
                      Manager.

                      0 is returned if trace could be succesfully completed.
                      1 is returned otherwise

  \se None
*/
int
Trace_execute_partial_trace(const NuSMVEnv_ptr env,
                            const Trace_ptr trace,
                            const PartialTraceExecutor_ptr exec_info,
                            const NodeList_ptr language);

/* Custom value fetch functions */

/*!
  \brief Extracts assignments in (trace, step) to a set of symbols

  Builds a bdd representing the assignments from a given
               step in trace. The symbols to be assigned are picked
               according to \"iter_type\". Refer to documentation of
               the TraceIteratorType for possible sets.

               Remarks: returned bdd is referenced
*/
bdd_ptr
TraceUtils_fetch_as_bdd(Trace_ptr trace, TraceIter step,
                        TraceIteratorType iter_type,
                        BddEnc_ptr bdd_enc);

/*!
  \brief Extracts assignments in (trace, step) to a set of symbols

  Builds a be representing the assignments from a given
               step in trace. The symbols to be assigned are picked
               according to \"iter_type\". Refer to documentation of
               the TraceIteratorType for possible sets.
*/
be_ptr
TraceUtils_fetch_as_be(Trace_ptr trace, TraceIter step,
                       TraceIteratorType iter_type,
                       BeEnc_ptr be_enc, BddEnc_ptr bdd_enc);

/*!
  \brief Extracts assignments in (trace, step) to a set of symbols

  Builds a sexp representing the assignments from a given
               step in trace. The symbols to be assigned are picked
               according to \"iter_type\". Refer to documentation of
               the TraceIteratorType for possible sets.

               Remarks: returned expression is find-node'd

  \sa TraceUtils_fetch_as_big_and
*/
Expr_ptr
TraceUtils_fetch_as_sexp(Trace_ptr trace, TraceIter step,
                         TraceIteratorType iter_type);

/*!
  \brief Extracts assignments in (trace, step) to a set of symbols

  Do the same thing as TraceUtils_fetch_as_sexp, but do not
               simplify or reorder the pointers of expressions created.

  \sa TraceUtils_fetch_as_sexp
*/
Expr_ptr
TraceUtils_fetch_as_big_and(Trace_ptr trace, TraceIter step,
                            TraceIteratorType iter_type);

/*!
  \brief Force the evaluation of the defines of the trace

  Useful for use the trace even if the encoder is destroyed
*/
void Trace_Eval_evaluate_defines(Trace_ptr trace);

/*!
  \brief Reads the trace from the specified file into the memory

  In case of error, NULL is returned. The trace will be
  stored in the tracemgr, that has the ownership
*/
Trace_ptr TracePkg_read_trace(NuSMVEnv_ptr env,
                                     SexpFsm_ptr sexp_fsm,
                                     const char* filename,
                                     boolean halt_if_undef,
                                     boolean halt_if_wrong_section);

/*!
  \brief Executes traces stored in the Trace Manager

  if trace_no equals to zero, all the traces will be
  executed
*/
int TracePkg_execute_traces(NuSMVEnv_ptr env,
                                   TraceMgr_ptr trace_mgr,
                                   FILE* output_stream,
                                   char* engine,
                                   int verbosity,
                                   int trace_no);

/*!
  \brief Executes traces stored in the Trace Manager

  if trace_no equals to zero, all the traces will be
  executed
*/
int TracePkg_execute_partial_traces(NuSMVEnv_ptr env,
                                           TraceMgr_ptr trace_mgr,
                                           FILE* output_stream,
                                           char* engine,
                                           int verbosity,
                                           int trace_no);
           
/**AutomaticEnd***************************************************************/

#endif /* __TRACE__H  */
