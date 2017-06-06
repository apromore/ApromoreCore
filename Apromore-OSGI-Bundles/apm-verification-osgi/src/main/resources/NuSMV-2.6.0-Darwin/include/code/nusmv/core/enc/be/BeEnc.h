
/* ---------------------------------------------------------------------------


  This file is part of the ``enc.be'' package of NuSMV version 2. 
  Copyright (C) 2004 by FBK-irst. 

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
  \author Roberto Cavada
  \brief Public interface of class 'BeEnc'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_ENC_BE_BE_ENC_H__
#define __NUSMV_CORE_ENC_BE_BE_ENC_H__


#include "nusmv/core/enc/base/BoolEncClient.h" 
#include "nusmv/core/enc/bool/BoolEnc.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/be/be.h"
#include "nusmv/core/node/node.h"

#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h"

/*!
  \struct BeEnc
  \brief Definition of the public accessor for class BeEnc

  
*/
typedef struct BeEnc_TAG*  BeEnc_ptr;

/*!
  \brief To cast and check instances of class BeEnc

  These macros must be used respectively to cast and to check
  instances of class BeEnc
*/
#define BE_ENC(self) \
         ((BeEnc_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BE_ENC_CHECK_INSTANCE(self) \
         (nusmv_assert(BE_ENC(self) != BE_ENC(NULL)))

/*!
  \brief A constant representing the time a current
  untimed variable belong to.

  The value of the constant is guaranteed to be out of the range
  of legal time numbers, i.e. out of \[0, BeEnc_get_max_time()\].
  
  This constant, for example, is returned by BeEnc_index_to_time
  given a frozen variable (which can be only untimed).
*/
#define BE_CURRENT_UNTIMED -1


/*!
  \brief The category which a be variable belongs to.

  Used to classify a be variable within 3 main categories: 
  - current state variables
  - frozen variables
  - input variables
  - next state variables 

  These values can be combined when for example the iteration is
  performed.  In this way it is possible to iterate through the set of
  current and next state vars, skipping the inputs.
  
*/

typedef enum BeVarType_TAG {
  BE_VAR_TYPE_CURR   = 0x1 << 0, 
  BE_VAR_TYPE_FROZEN = 0x1 << 1, 
  BE_VAR_TYPE_INPUT  = 0x1 << 2, 
  BE_VAR_TYPE_NEXT   = 0x1 << 3,

  /* a shorthand for all legal types */
  BE_VAR_TYPE_ALL    = BE_VAR_TYPE_CURR | BE_VAR_TYPE_FROZEN | 
                       BE_VAR_TYPE_INPUT | BE_VAR_TYPE_NEXT,
  /* this value is used internally to represent erroneous situations */
  BE_VAR_TYPE_ERROR  = 0x1 << 4,
} BeVarType;

/**AutomaticStart*************************************************************/


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BeEnc
  \brief The BeEnc class constructor

  The BeEnc class constructor

  \sa BeEnc_destroy
*/
BeEnc_ptr BeEnc_create(SymbTable_ptr symb_table, 
                              BoolEnc_ptr bool_enc);

/*!
  \brief The BeEnc class destructor

  The BeEnc class destructor

  \sa BeEnc_create
*/
VIRTUAL 
/*!
  \methodof BeEnc
  \todo
*/
void BeEnc_destroy(BeEnc_ptr self);

/* Getters: */

/*!
  \methodof BeEnc
  \brief <b>Returns</b> the
<tt>Boolean Expressions manager</tt> contained into the variable manager,
to be used by any operation on BEs

  Warning: do not delete the returned instance of
Be_Manager class, it belongs to self

  \se None
*/
Be_Manager_ptr BeEnc_get_be_manager(const BeEnc_ptr self);

/*!
  \methodof BeEnc
  \brief Returns the <tt>number of state variables currently
handled by the encoder</tt>

  

  \se None
*/
int BeEnc_get_state_vars_num(const BeEnc_ptr self);

/*!
  \methodof BeEnc
  \brief Returns the <tt>number of frozen variables currently
handled by the encoder</tt>

  

  \se None
*/
int BeEnc_get_frozen_vars_num(const BeEnc_ptr self);

/*!
  \methodof BeEnc
  \brief Returns the <tt>number of input variables currently
handled by the encoder</tt>

  

  \se None
*/
int BeEnc_get_input_vars_num(const BeEnc_ptr self);

/*!
  \methodof BeEnc
  \brief Returns the <tt>number of input and state variables
currently handled by the encoder</tt>

  

  \se None
*/
int BeEnc_get_vars_num(const BeEnc_ptr self);

/*!
  \methodof BeEnc
  \brief <b>Returns</b> the <tt>maximum allocated time </tt>

  

  \se None
*/
int BeEnc_get_max_time(const BeEnc_ptr self);


/* Conversion of name to something */

/*!
  \methodof BeEnc
  \brief Given the variable name, returns the corresponding BE
untimed variable

  

  \se None
*/
be_ptr 
BeEnc_name_to_untimed(const BeEnc_ptr self, const node_ptr var_name);

/*!
  \methodof BeEnc
  \brief Given the name of a be var, returns the untimed index that
variable is allocated at.

  

  \se None
*/
int 
BeEnc_name_to_index(const BeEnc_ptr self, const node_ptr name);

/*!
  \methodof BeEnc
  \brief Given the name of an untimed variable, returns the
corresponding BE variable at the given time

  This method expands the maximum allocated time if necessary.

WARNING: If the given variable name corresponds to an untimed next
state var, the returned index will be instantitated at time+1

NOTE: Frozen variables are returned untimed as they are never allocated as
timed variables.


  \se None
*/
be_ptr 
BeEnc_name_to_timed(const BeEnc_ptr self, 
                    const node_ptr name, const int time);


/* Conversion of index to something */

/*!
  \methodof BeEnc
  \brief Given the index of a be var, returns the symbolic variable
name.

  Current implementation requires that the variable belongs
to the untimed block

  \se None
*/
node_ptr 
BeEnc_index_to_name(const BeEnc_ptr self, const int index);

/*!
  \methodof BeEnc
  \brief Given a variable index, returns the corresponding be variable

  Current implementation requires that the variable index
belongs to the untimed block

  \se None
*/
be_ptr 
BeEnc_index_to_var(const BeEnc_ptr self, const int index);

/*!
  \methodof BeEnc
  \brief Given an untimed variable index, returns the corresponding
BE variable at the given time

  This method expands the maximum allocated time if necessary.

WARNING: If the given index corresponds to an untimed next state
var, the returned timed var will be instantitated at time+1

NOTE: Frozen variables are returned untimed as timed frozen
variables are never instantiated.


  \se None
*/
be_ptr 
BeEnc_index_to_timed(const BeEnc_ptr self, const int index, 
                     const int time);

/*!
  \methodof BeEnc
  \brief Given a state or input variable index this returns the time
the variable is allocated

  The given state or input variable index must refer a timed
variable.

If the given index refer to a frozen variable then BE_CURRENT_UNTIMED
constant is returned (frozen variables are only allocated in the
untimed block).

  \se None
*/
int BeEnc_index_to_time(const BeEnc_ptr self, const int index);

/*!
  \methodof BeEnc
  \brief Given a timed variable index, returns the corresponding
untimed BE variable index

  Returned index will refer either to an untimed current
state variable, an untimed frozen variable or an untimed input variable.

  \se None
*/
int 
BeEnc_index_to_untimed_index(const BeEnc_ptr self, const int index);

/* Conversion of be variable to something */

/*!
  \methodof BeEnc
  \brief Given a be variable, returns the correponding
variable name

  Current implementation requires the given be variable
to be untimed 

  \se None
*/
node_ptr 
BeEnc_var_to_name(const BeEnc_ptr self, be_ptr be_var);

/*!
  \methodof BeEnc
  \brief Given a be variable, returns the corresponding be index

  

  \se None
*/
int 
BeEnc_var_to_index(const BeEnc_ptr self, const be_ptr var);

/*!
  \methodof BeEnc
  \brief Given an untimed variable, returns the corresponding
BE variable at the given time

  This method expands the maximum allocated time if necessary.

WARNING: If the given variable is an untimed next state, the
returned index will be instantitated at time+1

NOTE: Frozen variables are returned untimed as they are always untimed.


  \se None
*/
be_ptr 
BeEnc_var_to_timed(const BeEnc_ptr self, const be_ptr var, 
                   const int time);

/*!
  \methodof BeEnc
  \brief Given a timed or untimed variable, returns the corresponding
BE variable in the current state untimed block (current state, frozen
and input vars).

  

  \se None
*/
be_ptr 
BeEnc_var_to_untimed(const BeEnc_ptr self, const be_ptr var);

/*!
  \methodof BeEnc
  \brief Converts an untimed current state variable to the corresponding
untimed variable in the next state untimed block.

  Given variable must be a current state untimed variable

  \se None
*/
be_ptr 
BeEnc_var_curr_to_next(const BeEnc_ptr self, const be_ptr curr);

/*!
  \methodof BeEnc
  \brief Converts an untimed next state variable to the corresponding
untimed variable in the current state untimed block.

  Given variable must be a next state untimed variable

  \se None
*/
be_ptr 
BeEnc_var_next_to_curr(const BeEnc_ptr self, const be_ptr next); 


/* Shifting of expressions */

/*!
  \methodof BeEnc
  \brief <b>Shifts</b> given <i>current</i> <b>expression at
next time</b>

  Warning: argument 'exp' must contain only untimed
current state variables and untimed frozen variables,
otherwise results will be unpredictible
*/
be_ptr 
BeEnc_shift_curr_to_next(BeEnc_ptr self, const be_ptr exp);

/*!
  \methodof BeEnc
  \brief <b>Shifts</b> given <i>untimed</i> <b>expression at
the given time</b>

  All next state variables will be shifted to time+1.
Maximum time is extended if necessary.
WARNING:
argument 'exp' must contain only untimed variables, otherwise
results will be unpredictible
*/
be_ptr 
BeEnc_untimed_expr_to_timed(BeEnc_ptr self, const be_ptr exp, 
                            const int time);

/*!
  \methodof BeEnc
  \brief Given an untimed expression, shifts current, frozen, input and
next variables to given times

  Shifts untimed current state vars to time ctime, frozen
untimed vars to ftime, input untimed vars to itime, and untimed next
state vars to ntime.
*/
be_ptr 
BeEnc_untimed_expr_to_times(BeEnc_ptr self, const be_ptr exp, 
                            const int ctime, 
                            const int ftime, 
                            const int itime, 
                            const int ntime);

/*!
  \methodof BeEnc
  \brief <b>Makes an AND interval</b> of given expression using
<b>range \[<tt>from</tt>, <tt>to</tt>\]</b>

  Maximum time is extended if necessary.
*/
be_ptr 
BeEnc_untimed_to_timed_and_interval(BeEnc_ptr self, 
                                    const be_ptr exp, 
                                    const int from, const int to);

/*!
  \methodof BeEnc
  \brief <b>Makes an OR interval</b> of given expression using
<b>range \[<tt>from</tt>, <tt>to</tt>\]</b>

  Maximum time is extended if necessary.
*/
be_ptr 
BeEnc_untimed_to_timed_or_interval(BeEnc_ptr self, 
                                   const be_ptr exp, 
                                   const int from, const int to);


/* Tests on type of be variables' indices */

/*!
  \methodof BeEnc
  \brief Checks whether given index corresponds to a state variable

  Valid state variables are in current and next state
blocks, and in timed state areas.
*/
boolean 
BeEnc_is_index_state_var(const BeEnc_ptr self, const int index);

/*!
  \methodof BeEnc
  \brief Checks whether given index corresponds to a frozen variable

  
*/
boolean 
BeEnc_is_index_frozen_var(const BeEnc_ptr self, const int index);

/*!
  \methodof BeEnc
  \brief Checks whether given index corresponds to an input variable

  Input variables are in the input untimed block,
or in timed input areas.
*/
boolean 
BeEnc_is_index_input_var(const BeEnc_ptr self, const int index);

/*!
  \methodof BeEnc
  \brief Checks whether given index corresponds to an untimed variable

  Note: frozen variables are always untimed.
*/
boolean 
BeEnc_is_index_untimed(const BeEnc_ptr self, const int index);

/*!
  \methodof BeEnc
  \brief Checks whether given index corresponds to an untimed
current state variable

  
*/
boolean 
BeEnc_is_index_untimed_curr(const BeEnc_ptr self, const int index);

/*!
  \methodof BeEnc
  \brief Checks whether given index corresponds to an untimed
frozen variable

  Frozen variables are always untimed. So this function is
exactly the same as BeEnc_is_index_frozen_var

  \sa BeEnc_is_index_frozen_var
*/
boolean 
BeEnc_is_index_untimed_frozen(const BeEnc_ptr self, const int index);

/*!
  \methodof BeEnc
  \brief Checks whether given index corresponds to an untimed input
variable

  
*/
boolean 
BeEnc_is_index_untimed_input(const BeEnc_ptr self, const int index);

/*!
  \methodof BeEnc
  \brief Checks whether given index corresponds to an untimed
current state variable, or an untimed frozen, or an untimed input variable

  
*/
boolean 
BeEnc_is_index_untimed_curr_frozen_input(const BeEnc_ptr self, 
                                         const int index);

/*!
  \methodof BeEnc
  \brief Checks whether given index corresponds to an untimed
next state variable

  
*/
boolean 
BeEnc_is_index_untimed_next(const BeEnc_ptr self, const int index);


/* Iteration over set of untimed variables */

/*!
  \methodof BeEnc
  \brief Call this to begin an iteration between a given
category of variables

  Use this method to begin an iteration between a given
category of variables, for example the set of input variables, or
the set of current and next state variables. The type is a bitwise
OR combination of types. When the first index is obtained, following
indices can be obtained by calling get_next_var_index, until
is_var_index_valid returns false, that means that the
iteration is over and must be given up.

  \se None

  \sa BeEnc_get_next_var_index, BeEnc_get_var_index_with_offset,
BeEnc_is_var_index_valid
*/
int 
BeEnc_get_first_untimed_var_index(const BeEnc_ptr self, BeVarType type);

/*!
  \methodof BeEnc
  \brief Use to sequentially iterate over a selected category of
variables.

  Use this method to obtain the index of the variable
that follows the variable whose index is provided. If the iteration
is over, an invalid index will be returned. Use the method
is_var_index_valid to check the validity of the returned index.

  \se None

  \sa BeEnc_get_first_untimed_var_index,
BeEnc_is_var_index_valid, BeEnc_get_var_index_with_offset
*/
int 
BeEnc_get_next_var_index(const BeEnc_ptr self, 
                         int var_index, BeVarType type);

/*!
  \methodof BeEnc
  \brief Use to randomly iterate over a selected category
of variables within the untimed block.

  Use this method to obtain the index of the variable
that follows the variable whose index is provided, after offset
positions. If the iteration is over, an invalid index will be
returned. Use the method is_var_index_valid to check the validity of
the returned index.

  \se None

  \sa BeEnc_get_first_untimed_var_index,
BeEnc_is_var_index_valid, BeEnc_get_next_var_index
*/
int 
BeEnc_get_var_index_with_offset(const BeEnc_ptr self, 
                                int from_index, int offset, 
                                BeVarType type);

/*!
  \methodof BeEnc
  \brief Use to check whether an iteration over a set of variables
is over.

  This method returns true whether the index returned by
methods get_first_untimed_var_index, get_next_var_index and
get_var_index_with_offset is valid and can be used later in the
iteration. If false is returned, then the iteration is over.

  \se None

  \sa BeEnc_get_first_untimed_var_index,
BeEnc_get_next_var_index, BeEnc_get_var_index_with_offset
*/
boolean 
BeEnc_is_var_index_valid(const BeEnc_ptr self, int var_index);




/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_ENC_BE_BE_ENC_H__ */
