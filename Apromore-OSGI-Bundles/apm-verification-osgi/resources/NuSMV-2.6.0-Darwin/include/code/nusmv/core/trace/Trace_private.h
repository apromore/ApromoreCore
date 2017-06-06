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
  \brief The private header file for the Trace class

  optional

*/


#ifndef __NUSMV_CORE_TRACE_TRACE_PRIVATE_H__
#define __NUSMV_CORE_TRACE_TRACE_PRIVATE_H__

#include "nusmv/core/trace/Trace.h"
#include "nusmv/core/utils/array.h"
#include "nusmv/core/utils/error.h"
#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/utils/EnvObject_private.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/
typedef enum TraceSection_TAG {
  /* reserved */
  TRACE_SECTION_INVALID = 0,

  TRACE_SECTION_FROZEN_VAR, TRACE_SECTION_STATE_VAR,
  TRACE_SECTION_INPUT_VAR, TRACE_SECTION_STATE_DEFINE,
  TRACE_SECTION_INPUT_DEFINE, TRACE_SECTION_STATE_INPUT_DEFINE,
  TRACE_SECTION_NEXT_DEFINE, TRACE_SECTION_STATE_NEXT_DEFINE,
  TRACE_SECTION_INPUT_NEXT_DEFINE, TRACE_SECTION_STATE_INPUT_NEXT_DEFINE,

  /* reserved */
  TRACE_SECTION_END,
} TraceSection;

/*  frames */

/*!
  \struct TraceFrozenFrame
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct TraceFrozenFrame_TAG* TraceFrozenFrame_ptr;

/*!
  \struct TraceVarFrame
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct TraceVarFrame_TAG* TraceVarFrame_ptr;

/*!
  \struct TraceDefineFrame
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct TraceDefineFrame_TAG* TraceDefineFrame_ptr;

/*!
  \brief Trace Class

   This class contains informations about a Trace:<br>
  <dl>
        <dt><code>id</code>
            <dd>  Unique ID of the registered traces. -1 for unregistered
            traces.
        <dt><code>desc</code>
            <dd>  Description of the trace.
        <dt><code>length</code>
            <dd>  Diameter of the trace. (i.e. the number of transitions)
        <dt><code>type</code>
            <dd>  Type of the trace.
        <dt><code>first_step</code>
            <dd>  Pointer to the first step of the doubly linked list of
            TraceSteps.
        <dt><code>last_node</code>
        <dd> Pointer to the last node of the doubly linked list of
            TraceSteps.
        <dt><code>defines_evaluated</code>
            <dd>  Internal index used to perform lazy evaluation of defines.
        <dt><code>symb2index</code>
            <dd>  Symbol to index hash table for fast look-up.
    </dl>
        <br>
  
*/

typedef struct Trace_TAG
{
  INHERITS_FROM(EnvObject);

  /* metadata */
  TraceType type;
  const char* desc;
  int id;

  unsigned length;
  boolean frozen;
  boolean is_volatile;

  boolean allow_bits;

  SymbTable_ptr st;

  NodeList_ptr symbols;
  NodeList_ptr s_vars;
  NodeList_ptr sf_vars;
  NodeList_ptr i_vars;

  /* first and last frame */
  TraceVarFrame_ptr first_frame;
  TraceVarFrame_ptr last_frame;

  /* Keep frozenvars separated */
  TraceFrozenFrame_ptr frozen_frame;

  /* buckets (first and last unused) */
  unsigned n_buckets[TRACE_SECTION_END];
  node_ptr* buckets[TRACE_SECTION_END];

  /*  lookup aux structures */
  hash_ptr symb2section;
  hash_ptr symb2address;
  hash_ptr symb2layername;

} Trace;

/* frames */
typedef struct TraceVarFrame_TAG
{
  /* metadata */
  node_ptr* state_values;
  node_ptr* input_values;

  /* for frozen traces only */
  boolean loopback;

  /* Defines frames */
  TraceDefineFrame_ptr fwd_define_frame;
  TraceDefineFrame_ptr bwd_define_frame;

  /* doubly linked list */
  TraceVarFrame_ptr next_frame;
  TraceVarFrame_ptr prev_frame;
} TraceVarFrame;

typedef struct TraceFrozenFrame_TAG
{
  node_ptr* frozen_values;
  /* unsigned n_frozen_values; */
} TraceFrozenFrame;

typedef struct TraceDefineFrame_TAG
{
  node_ptr* s_values;
  node_ptr* i_values;
  node_ptr* si_values;
  node_ptr* n_values;
  node_ptr* sn_values;
  node_ptr* in_values;
  node_ptr* sin_values;
} TraceDefineFrame;


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/
#undef CHECK
#if defined TRACE_DEBUG

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CHECK(cond) nusmv_assert(cond)
#else
#define CHECK(cond)
#endif

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_VAR_FRAME(x) \
  ((TraceVarFrame_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_DEFINE_FRAME(x) \
  ((TraceDefineFrame_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_FROZEN_FRAME(x) \
  ((TraceFrozenFrame_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_VAR_FRAME_CHECK_INSTANCE(x) \
  (nusmv_assert(TRACE_VAR_FRAME(x) != TRACE_VAR_FRAME(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_DEFINE_FRAME_CHECK_INSTANCE(x) \
  (nusmv_assert(TRACE_DEFINE_FRAME(x) != TRACE_DEFINE_FRAME(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_FROZEN_FRAME_CHECK_INSTANCE(x) \
  (nusmv_assert(TRACE_FROZEN_FRAME(x) != TRACE_FROZEN_FRAME(NULL)))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
Trace_ptr
trace_create(SymbTable_ptr st, const char* desc,
             const TraceType type, NodeList_ptr symbols,
             boolean is_volatile,
           boolean allow_bits);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
Trace_ptr
trace_copy(Trace_ptr self, TraceIter until_here,
           boolean is_volatile);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_is_volatile(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_equals(const Trace_ptr self, const Trace_ptr other);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
Trace_ptr
trace_concat(Trace_ptr self, Trace_ptr* other);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
trace_destroy(Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_symbol_fwd_lookup(Trace_ptr self, node_ptr symb,
                        TraceSection* section, unsigned* index);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr
trace_symbol_bwd_lookup(Trace_ptr self, TraceSection section,
                        unsigned offset);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
TraceIter
trace_append_step(Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_step_is_loopback(const Trace_ptr self, const TraceIter step);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_step_test_loopback(Trace_ptr self, const TraceIter step);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
trace_step_force_loopback(const Trace_ptr self, TraceIter step);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_is_frozen(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_is_thawed(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
trace_freeze(Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
trace_thaw(Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_symbol_in_language(const Trace_ptr self, const node_ptr symb);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
SymbCategory
trace_symbol_get_category(Trace_ptr self, node_ptr symb);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char*
trace_get_layer_from_symb(const Trace_ptr self, const node_ptr symb);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_symbol_is_assigned(const Trace_ptr self,
                         const TraceIter step, node_ptr symb);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_step_put_value(Trace_ptr self, const TraceIter step,
                     const node_ptr symb, const node_ptr value);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr
trace_step_get_value(const Trace_ptr self, const TraceIter step,
                     const node_ptr symb);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_is_complete_vars(const Trace_ptr self, const NodeList_ptr vars,
                       FILE* report_stream);

/* horizontal iterators management */

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
TraceIter
trace_first_iter(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
TraceIter
trace_ith_iter(const Trace_ptr self, unsigned i);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
TraceIter
trace_last_iter(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
unsigned
trace_iter_i(const Trace_ptr self, TraceIter iter);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
TraceIter
trace_iter_get_next(const TraceIter iter);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
TraceIter
trace_iter_get_prev(const TraceIter iter);

/* vertical iterators management */

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
TraceStepIter
trace_step_iter(const Trace_ptr self, const TraceIter step,
                TraceIteratorType iter_type);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_step_iter_fetch(TraceStepIter* step_iter,
                      node_ptr* symb, node_ptr* value);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
TraceSymbolsIter
trace_symbols_iter(const Trace_ptr self, TraceIteratorType iter_type);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_symbols_iter_fetch(TraceSymbolsIter* symbols_iter,
                         node_ptr* symb);

/* trace metadata */

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
unsigned
trace_get_length(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_is_empty(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
NodeList_ptr
trace_get_symbols(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
NodeList_ptr
trace_get_s_vars(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
NodeList_ptr
trace_get_sf_vars(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
NodeList_ptr
trace_get_i_vars(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
SymbTable_ptr
trace_get_symb_table(Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
trace_register(Trace_ptr self, int id);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
trace_unregister(Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean
trace_is_registered(Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
int
trace_get_id(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
TraceType
trace_get_type(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
trace_set_type(Trace_ptr self, TraceType trace_type);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char*
trace_get_desc(const Trace_ptr self);

/*!
  \methodof Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
trace_set_desc(Trace_ptr self, const char* desc);

/*!
  \methodof Trace
  \brief Evaluates defines for a trace

  Evaluates define for a trace, based on assignments to
               state, frozen and input variables.

               If a previous value exists for a define, The mismatch
               is reported to the caller by appending a failure node
               describing the error to the "failures" list. If
               "failures" is NULL failures are silently discarded.  If
               no previous value exists for a given define, assigns
               the define to the calculated value according to vars
               assignments. The "failures" list must be either NULL
               or a valid, empty list.

               0 is returned if no mismatching were detected, 1
               otherwise 

  \se The trace is filled with defines, failures list is
               populated as necessary.
*/
void
trace_step_evaluate_defines(Trace_ptr self, const TraceIter step);

/*!
  \methodof Trace
  \brief 

  
*/
boolean
trace_step_check_defines(Trace_ptr self, const TraceIter step,
                         NodeList_ptr failures);

/* private conversion functions */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
SymbCategory
trace_section_to_category(const TraceSection section);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
TraceSection
trace_category_to_section(const SymbCategory category);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char*
trace_symb_category_to_string(const SymbCategory category);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_TRACE_TRACE_PRIVATE_H__ */
