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

  You should have received a copy of the GNU Lesser General Publi
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

  For more information on NuSMV see <http://nusmv.fbk.eu>
  or email to <nusmv-users@fbk.eu>.
  Please report bugs to <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to <nusmv@fbk.eu>.

-----------------------------------------------------------------------------*/

/*!
  \author Marco Pensallorto
  \brief The header file for the Trace class

  optional

*/

#ifndef __NUSMV_CORE_TRACE_TRACE_H__
#define __NUSMV_CORE_TRACE_TRACE_H__

#include "nusmv/core/set/set.h"
#include "nusmv/core/wff/ExprMgr.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/utils/OStream.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct Trace
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct Trace_TAG* Trace_ptr;
typedef struct TraceVarFrame_TAG* TraceIter;

/*!
  \brief Macro to iterate over trace (horizontal iterator)

  Use this macro to iterate from the first step to the
                last.

  \se none

  \sa none
*/
#define TRACE_FOREACH(trace, iter)                                  \
  for ((iter)=Trace_first_iter(trace); TRACE_END_ITER != (iter);    \
       (iter)=TraceIter_get_next(iter))

/*!
  \brief Macro to iterate over trace step (vertical iterator)

  Use this macro to iterate over assignments for a given
                step.

  \se symbol and value are assigned for each iteration

  \sa TRACE_SYMBOLS_FOREACH
*/
#define TRACE_STEP_FOREACH(trace, step, type, iter, symbol, value)   \
  iter = Trace_step_iter((trace), (step), (type));                   \
  while (Trace_step_iter_fetch((&iter), (&symbol), (&value)))

/*!
  \brief Macro to iterate over symbols (vertical iterator)

  Use this macro to iterate over symbols of a trace.

  \se symbol is assigned for each iteration

  \sa TRACE_STEP_FOREACH
*/
#define TRACE_SYMBOLS_FOREACH(trace, type, iter, symbol)            \
  iter = Trace_symbols_iter((trace), (type));                       \
  while (Trace_symbols_iter_fetch((&iter), (&symbol)))


/*!
  \brief Trace type enumeration


*/

typedef enum TraceType_TAG {
  TRACE_TYPE_UNSPECIFIED = -1, /* reserved */

  TRACE_TYPE_CNTEXAMPLE = 0,
  TRACE_TYPE_SIMULATION,
  TRACE_TYPE_EXECUTION,

  TRACE_TYPE_END,
} TraceType;


/*!
  \brief Trace vertical iterator kind enum

  Specific kind of iterators can be required using
               the appropriate value from this enumeration

  \sa TraceStepIter, Trace_step_iter
*/

typedef enum TraceIteratorType_TAG {

  TRACE_ITER_NONE=0,

  /* vars */
  TRACE_ITER_F_VARS=0x2,
  TRACE_ITER_S_VARS=0x4,
  TRACE_ITER_I_VARS=0x8,

  /* var groups */
  TRACE_ITER_SF_VARS=0x6,
  TRACE_ITER_ALL_VARS=0xe,

  /* defines */
  TRACE_ITER_S_DEFINES=0x10,
  TRACE_ITER_I_DEFINES=0x20,

  TRACE_ITER_SI_DEFINES=0x40,
  TRACE_ITER_N_DEFINES=0x80,
  TRACE_ITER_SN_DEFINES=0x100,
  TRACE_ITER_IN_DEFINES=0x200,
  TRACE_ITER_SIN_DEFINES=0x400,

  /* vars+defines groups */
  TRACE_ITER_SF_SYMBOLS = 0x16,
  TRACE_ITER_S_SYMBOLS = 0x14,
  TRACE_ITER_I_SYMBOLS = 0x28,

  /* transitional groups: the following iterator types are used to
     describe defines across a transition: COMBINATORIAL holds all the
     defines which depend on (S, I), (N), (S, N), (I, N), (S, I, N).
     In addition to all the defines aforementioned, TRANSITIONAL
     contains INPUT defines as well. */
  TRACE_ITER_COMBINATORIAL=0x7c0,
  TRACE_ITER_TRANSITIONAL=0x07e0,

} TraceIteratorType;


/*!
  \brief Trace vertical iterator type

  optional

  \sa optional
*/

typedef struct TraceStepIter_TAG
{
  Trace_ptr trace;
  TraceIter step;
  TraceIteratorType type;

  unsigned section;
  unsigned cursor;
} TraceStepIter;


/*!
  \brief Trace vertical iterator type

  optional

  \sa optional
*/

typedef struct TraceSymbolsIter_TAG
{
  Trace_ptr trace;
  TraceIteratorType type;

  unsigned section;
  unsigned cursor;
} TraceSymbolsIter;

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE(x) \
  ((Trace_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_CHECK_INSTANCE(x) \
  (nusmv_assert(TRACE(x) != TRACE(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_ITER(x) \
  ((TraceIter) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_ITER_CHECK_INSTANCE(x) \
  (nusmv_assert(TRACE_ITER(x) != TRACE_ITER(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_STEP_ITER(x) \
  ((TraceStepIter) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_STEP_ITER_CHECK_INSTANCE(x) \
  (nusmv_assert(TRACE_STEP_ITER(x) != TRACE_STEP_ITER(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_SYMBOLS_ITER(x) \
  ((TraceStepIter) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_SYMBOLS_ITER_CHECK_INSTANCE(x) \
  (nusmv_assert(TRACE_SYMBOLS_ITER(x) != TRACE_SYMBOLS_ITER(NULL)))

/*!
  \brief Iterator ends


*/
#define TRACE_END_ITER TRACE_ITER(NULL)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_STEP_END_ITER TRACE_STEP_ITER(NULL)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_SYMBOLS_END_ITER TRACE_SYMBOLS_ITER(NULL)

/* reserved for Trace Manager */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_UNREGISTERED -1

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof Trace
  \brief Trace class constructor

  Allocates and initializes a trace.  In NuSMV, a trace
               is an engine-independent description of a computation
               path for some FSM.  The newly created trace is
               associated with a language, that is a set of symbols
               (variables and defines) that can occur in the trace.
               Notice that bits (from boolean encoding) are not
               allowed in the language.  To allow bits, use
               create_allow_bits.

	       In addition, a description and a type can be given to a
               trace.

               If the trace is not volatile, all input parameters are
               internally duplicated in an independent copy.  The
               caller is responsible for freeing them. Same for
               volatile traces, made exception for the symbol table,
               since only a reference will be retained. In this case,
               the caller is responsible for freeing them, and take
               care of the symbol table, which must NOT be freed until
               the created trace instance is destroyed.

               Remarks:

               * First step is already allocated for the returned
                 trace.  Use Trace_first_iter to obtain a valid
                 iterator pointing to the initial step. Use
                 Trace_add_step to append further steps.

  \sa Trace_first_iter, Trace_append_step, Trace_destroy
*/
Trace_ptr
Trace_create(SymbTable_ptr st, const char* desc,
             const TraceType type, NodeList_ptr symbols,
             boolean is_volatile);

/*!
  \methodof Trace
  \brief Trace class constructor

  Allocates and initializes a trace.  In NuSMV, a trace
               is an engine-independent description of a computation
               path for some FSM.  The newly created trace is
               associated with a language, that is a set of symbols
               (variables and defines) that can occur in the trace.
               Bits (from boolean encoding) are allowed in the
               language, along with their corresponding scalar
               variables if needed.

	       In addition, a description and a type can be given to a
               trace.

               If the trace is not volatile, all input parameters are
               internally duplicated in an independent copy.  The
               caller is responsible for freeing them. Same for
               volatile traces, made exception for the symbol table,
               since only a reference will be retained. In this case,
               the caller is responsible for freeing them, and take
               care of the symbol table, which must NOT be freed until
               the created trace instance is destroyed.

               Remarks:

               * First step is already allocated for the returned
                 trace.  Use Trace_first_iter to obtain a valid
                 iterator pointing to the initial step. Use
                 Trace_add_step to append further steps.

  \sa Trace_first_iter, Trace_append_step, Trace_destroy
*/
Trace_ptr
Trace_create_allow_bits(SymbTable_ptr st, const char* desc,
                  const TraceType type, NodeList_ptr symbols,
                  boolean is_volatile);

/*!
  \methodof Trace
  \brief Trace class copy constructor

  Returns an independent copy of \"self\" trace. If a
               non-NULL \"until_here\" iterator is passed copying
               process halts when \"until_here\" iterator has been
               reached.  To obtain a full copy, pass TRACE_END_ITER as
               the \"until_here\" paramter. Loopback information is
               propagated to the copy only if a full copy is required.

               If the trace is not volatile, all trace structures are
               internally duplicated in an independent copy. Same for
               volatile traces, made exception for the symbol table,
               since only a reference of the original trace's symbol
               table will be retained. In this case, the caller must
               take care of the original trace symbol table, which
               must NOT be freed until the created trace instance is
               destroyed. In detail:

               - If the original trace is volatile, then it's copy is
                 valid unless the symbol table given at creation time
                 is destroyed. In all other cases, the returned trace
                 is valid.

               - If the original trace is not volatile, then the
                 returned copy is valid as long as the original trace
                 is valid.

               Notice that a volatile copy "C" of a volatile copy "B"
               of a non-volatile trace "A" needs "A" to exist, and so
               on.


               Remarks:

               * The full copy of a frozen trace is a frozen
               trace. Partial copies are always thawed.

  \sa Trace_thaw, Trace_freeze, Trace_destroy
*/
Trace_ptr
Trace_copy(const Trace_ptr self, const TraceIter until_here,
           boolean is_volatile);

/*!
  \methodof Trace
  \brief Trace concatenation

  *Destructively* concatenates \"other\" to
               \"self\". That is, \"self\" is appended all available
               data about variables and defines from
               \"*other\". Frozen vars and state vars of the
               conjunction state for both \"self\" and \"other\"
               traces are synctactically checked for
               consistency. Their values are merged in the resulting
               trace.

               Warning: an internal error is raised if an
               inconsistency is detected.

               Returned valued is \"self\".

  \se \"self\" is extended, \"*other\" is destroyed and its
               pointer is set to NULL.
*/
Trace_ptr
Trace_concat(Trace_ptr self, Trace_ptr* other);

/*!
  \methodof Trace
  \brief Trace class destructor

  Frees all the resources used by \"self\" trace instance

  \sa Trace_create, Trace_copy
*/
void
Trace_destroy(Trace_ptr self);


/* Queries ********************************************************************/

/*!
  \methodof Trace
  \brief Equality predicate between traces

  Two traces are equals iff:

              1. They're the same object or NULL.

              or

              2. They have exactly the same language, length,
                 assignments for all variables in all times and
                 the same loopbacks.

                 (Defines are not taken into account for equality.)

              They need not be both frozen of thawed, neither being
              both registered or unregistered. (Of course two traces
              *cannot* have the same ID).

  \se required

  \sa optional
*/
boolean
Trace_equals(const Trace_ptr self, const Trace_ptr other);

/*!
  \methodof Trace
  \brief Checks whether the trace is empty or not

  A trace is empty if the length is 0 and there are no
               assignments in the initial states

  \sa TraceType
*/
boolean
Trace_is_empty(const Trace_ptr self);


/* metadata */

/*!
  \methodof Trace
  \brief Gets the description of given trace.


*/
const char*
Trace_get_desc(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Sets the description of given trace.

  The string in \"desc\" is duplicated inside the
               trace. The caller can dispose the actual parameter.

               Remarks: NIL(char) is accepted as a non-descriptive
               description.
*/
void
Trace_set_desc(const Trace_ptr self, const char* desc);

/*!
  \methodof Trace
  \brief Determine whether the \"self\" trace is volatile

  A volatile trace does not own a symbol table instance,
               so it is valid as long as the symbol table does not
               change and is available. A non-volatile trace instead
               owns a copy of the symbol table given at construction
               time and is completely independand among system changes
               over time
*/
boolean
Trace_is_volatile(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Gets the id of given trace.

  Returns the ID of given trace. A valid id is a
               non-negative number.
*/
int
Trace_get_id(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Checks whether trace is registered with the trace manager.


*/
boolean
Trace_is_registered(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Sets the id of given trace.

  Sets the ID of the given trace. A valid ID is a
               non-negative number.
*/
void
Trace_register(const Trace_ptr self, int id);

/*!
  \methodof Trace
  \brief Unregisters a trace


*/
void
Trace_unregister(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Gets the type of the trace.

  For a list of see definition of TraceType enum

  \sa TraceType
*/
TraceType
Trace_get_type(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Sets the type of the trace.

  For a list of see definition of TraceType enum

  \sa TraceType
*/
void
Trace_set_type(Trace_ptr self, TraceType trace_type);

/*!
  \methodof Trace
  \brief Gets the length of the trace.

  Length for a trace is defined as the number of the
               transitions in it. Thus, a trace consisting only of an
               initial state is a 0-length trace. A trace with two
               states is a 1-length trace and so forth.

  \sa TraceType
*/
unsigned
Trace_get_length(const Trace_ptr self);


/* freeze/thaw */

/*!
  \methodof Trace
  \brief Determine whether the \"self\" trace is frozen

  A frozen trace holds explicit information about
              loopbacks and can not be appended a step, or added a
              variable value.

              Warning: after freezing no automatic looback calculation
              will be performed: it is up to the owner of the trace to
              manually add loopback information using
              Trace_step_force_loopback.

  \se required

  \sa optional
*/
boolean
Trace_is_frozen(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Determine whether the \"self\" trace is thawed

  A thawed trace holds no explicit information about
              loopbacks and can be appended a step and

              Warning: after thawing the trace will not persistently
              retain any loopback information. In particular it is
              *illegal* to force a loopback on a thawed trace.

  \se required

  \sa optional
*/
boolean
Trace_is_thawed(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Freezes a trace

  A frozen trace holds explicit information about
              loopbacks. Its length and assignments are immutable,
              that is it cannot be appended more steps, nor can it
              accept more values that those already stored in it.

              Still it is possible to register/unregister the trace
              and to change its type or description.

  \se required

  \sa optional
*/
void
Trace_freeze(Trace_ptr self);

/*!
  \methodof Trace
  \brief Thaws a trace

  A thawed traces holds no explicit information about
              loopbacks and can be appended a step and added values in
              the customary trace building process.

              Warning: after thawing the trace will not persistently
              retain any loopback information. In particular it is
              *illegal* to force a loopback on a thawed trace.

  \se required

  \sa optional
*/
void
Trace_thaw(Trace_ptr self);


/* step management */

/*!
  \methodof Trace
  \brief Extends a trace by adding a new step to it

  A step is a container for incoming input and next
               state(i.e. it has the form <i, S>)

               The returned step can be used as parameter to all
               Trace_step_xxx functions

  \sa Trace_create, Trace_step_put_value, Trace_step_get_value,
               Trace_step_get_iter, Trace_step_get_next_value
*/
TraceIter
Trace_append_step(Trace_ptr self);

/*!
  \methodof Trace
  \brief Tests whether state is \"step\" is a loopback state w.r.t the
               last state in \"self\".

  This function behaves accordingly to two different modes a trace
               can be: frozen or thawed(default).

               If the trace is frozen, permanent loopback information
               is used to determine if \"step\" has a loopback state.
               No further loopback computation is made.

               If the trace is thawed, dynamic loopback calculation
               takes place, using a variant of Rabin-Karp pattern
               matching algorithm

  \sa Trace_create, Trace_step_put_value, Trace_step_get_value,
               Trace_step_get_iter, Trace_step_get_next_value
*/
boolean
Trace_step_is_loopback(const Trace_ptr self, TraceIter step);

/*!
  \methodof Trace
  \brief Forces a loopback on a frozen trace

  Use this function to store explicit loopback information
               in a frozen trace. The trace will retain loopback data
               until being thawed again.

  \se required

  \sa optional
*/
void
Trace_step_force_loopback(const Trace_ptr self, TraceIter step);

/* data accessors */

/*!
  \methodof Trace
  \brief Stores an assignment into a trace step

  A step is a container for incoming input and next
               state(i.e. it has the form <i, S>)

               \"step\" must be a valid step for the trace.  If symb
               belongs to the language associated to the trace at
               creation time, the normalized value of \"value\" is
               stored into the step. Assignment is checked for type
               correctness.

               Returns true iff the value was succesfully assigned to symb
               in given step of self.

               Remarks:

               * Assignments to symbols not in trace language are
               silently ignored.

  \sa Trace_append_step, Trace_step_get_value
*/
boolean
Trace_step_put_value(Trace_ptr self, TraceIter step,
                      node_ptr symb, node_ptr value);

/*!
  \methodof Trace
  \brief Retrieves an assignment from a trace step

  A step is a container for incoming input and next
               state(i.e. it has the form <i, S>)

               \"step\" must be a valid step for the trace.  \"symb\"
               must belong to the language associated to the trace at
               creation time. The value stored into the step is
               returned or Nil if no such value exists.

               Remarks: An internal error is raised if \"symb\" is not
               in trace language.

  \sa Trace_create, Trace_step_put_value, Trace_step_get_value,
               Trace_step_get_iter, Trace_step_get_next_value
*/
node_ptr
Trace_step_get_value(const Trace_ptr self, TraceIter step,
                     node_ptr symb);

/* horizontal iterators, used to traverse a trace */

/*!
  \methodof Trace
  \brief Returns a trace iterator pointing to the first step of the trace

  A step is a container for incoming input and next
               state(i.e. it has the form <i, S>)

               The returned step can be used as parameter to all
               Trace_step_xxx functions.

               Remarks:

                 * the first step holds *no* input information.

  \sa Trace_last_iter
*/
TraceIter
Trace_first_iter(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Returns a trace iterator pointing to the i-th step of the trace

  Returns a trace iterator pointing to the i-th step of
               the trace.  Counting starts at 1. Thus, here is the
               sequence of first k steps for a trace.

               S1 i2 S2 i3 S3 ... ik Sk

               Remarks:

                 * the first step holds *no* input information.

  \sa Trace_first_iter, Trace_last_iter
*/
TraceIter
Trace_ith_iter(const Trace_ptr self, unsigned i);

/*!
  \methodof Trace
  \brief Returns a trace iterator pointing to the last step of the trace

  A step is a container for incoming input and next
               state(i.e. it has the form <i, S>)

               The returned step can be used as parameter to all
               Trace_step_xxx functions

  \sa Trace_first_iter
*/
TraceIter
Trace_last_iter(const Trace_ptr self);

/*!
  \brief Returns a trace iterator pointing to the next step of the
               trace

  Returns a trace iterator pointing to the next step of
               the trace. TRACE_END_ITER is returned if no such
               iterator exists

  \sa TraceIter_get_prev
*/
TraceIter
TraceIter_get_next(const TraceIter iter);

/*!
  \brief Returns a trace iterator pointing to the previous
               step of the trace

  Returns a trace iterator pointing to the previous step
               of the trace. TRACE_END_ITER is returned if no such
               iterator exists

  \sa TraceIter_get_next
*/
TraceIter
TraceIter_get_prev(const TraceIter iter);

/*!
  \brief Iterator-at-end-of-trace predicate


*/
boolean
TraceIter_is_end(const TraceIter iter);

/* vertical iterators (hint: use macros instead) */

/*!
  \methodof Trace
  \brief Step iterator factory constructor

  A step is a container for incoming input and next
               state(i.e. it has the form <i, S>)

               \"step\" must be a valid step for the trace. An
               iterator over the assignments in \"step\" is returned.
               This iterator can be used with Trace_step_iter_fetch.

               Hint: do not use this function. Use TRACE_STEP_FOREACH
               macro instead (it is way easier and more readable).

  \sa TRACE_STEP_FOREACH
*/
TraceStepIter
Trace_step_iter(const Trace_ptr self, const TraceIter step,
                const TraceIteratorType iter_type);

/*!
  \brief Step iterator next function

  A step iterator is a stateful iterator which yields
               an single assignment at each call of this function.

               \"step_iter\" must be a valid step iterator for the
               trace. If a valid assignment was found, True is
               returned.  Otherwise False is returned. This indicates
               end of iteration.

               Hint: do not use this function. Use TRACE_SYMBOLS_FOREACH
               macro instead (it is way easier and more readable).

  \sa Trace_step_get_iter
*/
boolean
Trace_step_iter_fetch(TraceStepIter* step_iter,
                      node_ptr* symb, node_ptr* value);

/*!
  \methodof Trace
  \brief Symbols iterator factory constructor

  An iterator over the symbols in \"self\" is returned.
               This iterator can be used with Trace_symbols_iter_fetch.

               Hint: do not use this function. Use TRACE_SYMBOLS_FOREACH
               macro instead (it is way easier and more readable).

  \sa TRACE_SYMBOLS_FOREACH
*/
TraceSymbolsIter
Trace_symbols_iter(const Trace_ptr self,
                   const TraceIteratorType iter_type);

/*!
  \brief Symbols iterator next function

  A symbols iterator is a stateful iterator which yields
               a symbols in the trace language at each call of this function.

               \"symbols_iter\" must be a valid symbols iterator for
               the trace. If a symbols is found, True is returned.
               Otherwise False is returned. This indicates end of
               iteration.

               Hint: do not use this function. Use TRACE_SYMBOLS_FOREACH
               macro instead (it is way easier and more readable).

  \sa Trace_symbols_get_iter
*/
boolean
Trace_symbols_iter_fetch(TraceSymbolsIter* symbols_iter,
                         node_ptr *symb);

/* language queries */

/*!
  \methodof Trace
  \brief Exposes Trace internal symbol table

  Returns the trace symbol table. The symbol table is
               owned by the trace and should *not* be modified in any
               way.

  \se required

  \sa optional
*/
SymbTable_ptr
Trace_get_symb_table(Trace_ptr self);

/*!
  \methodof Trace
  \brief Exposes the list of symbols in trace language

  Returned list belongs to \"self\". Do not change or dispose it.

  \se required

  \sa optional
*/
NodeList_ptr
Trace_get_symbols(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Exposes the list of state vars in trace language

  Returned list belongs to \"self\". Do not change or dispose it.

  \se required

  \sa optional
*/
NodeList_ptr
Trace_get_s_vars(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Exposes the list of state-frozen vars in trace language

  Returned list belongs to \"self\". Do not change or dispose it.

  \se required

  \sa optional
*/
NodeList_ptr
Trace_get_sf_vars(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Exposes the list of input vars in trace language

  Returned list belongs to \"self\". Do not change or dispose it.

  \se required

  \sa optional
*/
NodeList_ptr
Trace_get_i_vars(const Trace_ptr self);

/*!
  \methodof Trace
  \brief Tests whether a symbol is \"self\"'s language

  Returns true iff symb is part of the language defined
               for \"self\" defined at creation time.

  \se required

  \sa optional
*/
boolean
Trace_symbol_in_language(const Trace_ptr self, node_ptr symb);

/*!
  \methodof Trace
  \brief Tests whether all given symbols are in \"self\"'s language

  Returns true iff all symbols are part of the language defined
               for \"self\" defined at creation time.

  \se required

  \sa optional
*/
boolean
Trace_covers_language(const Trace_ptr self, NodeList_ptr symbols);

/*!
  \methodof Trace
  \brief Checks if given symbol is assigned at given step

  Returns true iff given symbols is assigned at given step.
               Remarks: An internal error is raised if \"symb\" is not
               in trace language.

  \sa Trace_step_get_value
*/
boolean
Trace_symbol_is_assigned(Trace_ptr self, TraceIter step, node_ptr symb);

/*!
  \methodof Trace
  \brief Checks if a Trace is complete on the given set of vars

  Checks if a Trace is complete on the given set of vars

  \se None
*/
boolean
Trace_is_complete(Trace_ptr self, NodeList_ptr vars, boolean report);

const char* TraceType_to_string(const NuSMVEnv_ptr env,
                                const TraceType self);

/*!
  \methodof Trace
  \brief Checks if a Trace has at least one loopback

  Return true iff a Trace has at least one loopback

  \se None
*/
boolean Trace_has_loopback(const Trace_ptr self);


/*!
  \methodof Trace
  \brief Checks if loopback_idx is a valid loopback on Trace

  Return true iff loopback_idx is a valid loopback on Trace

  \se None
*/
boolean Trace_validate_loopback(const Trace_ptr self, int loopback_idx);

/*!
  \methodof Trace
  \brief Returns the first loopback found on self from step

  Search for the first loopback on trace from step and return its id.
  In case there is no loopbacks returns -1.
  Remark: step can be NULL to indicate to start from the first
  step.


  \se None
*/
int Trace_get_first_loopback_from(const Trace_ptr self, TraceIter* step);

/*!
  \methodof Trace
  \brief Prints available loopbacks in self

  Prints available loopbacks in self

  \se None
*/
void Trace_print_loopbacks(const Trace_ptr self, OStream_ptr stream);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_TRACE_TRACE_H___ */
