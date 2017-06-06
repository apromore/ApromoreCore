/* ---------------------------------------------------------------------------


  This file is part of the ``fsm.bdd'' package of NuSMV version 2.
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
  \author Roberto Cavada
  \brief Declares the public interface for the package fsm.bdd

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_FSM_BDD_BDD_H__
#define __NUSMV_CORE_FSM_BDD_BDD_H__

#include "nusmv/core/dd/dd.h"


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_ELFWD_OPT_FORWARD_SEARCH 1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_ELFWD_OPT_LTL_TABLEAU_FORWARD_SEARCH 2

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_ELFWD_OPT_USE_REACHABLE_STATES 4

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_ELFWD_OPT_COUNTER_EXAMPLES 8

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_ELFWD_OPT_ALL (BDD_ELFWD_OPT_FORWARD_SEARCH | \
                           BDD_ELFWD_OPT_LTL_TABLEAU_FORWARD_SEARCH | \
                           BDD_ELFWD_OPT_USE_REACHABLE_STATES | \
                           BDD_ELFWD_OPT_COUNTER_EXAMPLES)

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef bdd_ptr BddStates;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_STATES(x) \
          ((BddStates) x)

typedef bdd_ptr BddInputs;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_INPUTS(x) \
          ((BddInputs) x)

typedef bdd_ptr BddStatesInputs;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_STATES_INPUTS(x) \
          ((BddStatesInputs) x)

typedef bdd_ptr BddStatesInputsNexts;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_STATES_INPUTS_NEXTS(x) \
          ((BddStatesInputsNexts) x)

typedef bdd_ptr BddInvarStates;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_INVAR_STATES(x) \
          ((BddInvarStates) x)

typedef bdd_ptr BddInvarInputs;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_INVAR_INPUTS(x) \
          ((BddInvarInputs) x)

/*!
  \struct BddELFwdSavedOptions
  \brief Holds the values of those options that might have been
                      overridden to allow execution of forward Emerson-Lei.

  

  \se n/a

  \sa Bdd_elfwd_check_set_and_save_options,
                      Bdd_elfwd_restore_options
*/
typedef struct BddELFwdSavedOptions_TAG* BddELFwdSavedOptions_ptr;

/*!
  \brief Enumeration of algorithms for determining language
                emptiness of a Buchi fair transition system with BDDs

  Currently only has backward and forward variants of
                Emerson-Lei.

                The ..._MIN/MAX_VALID values can be used to
                determine the least and greatest valid elements so as to
                eliminate some need for change when other algorithms are
                added. These values might need to be adapted below when new
                algorithms are added.
*/


enum BddOregJusticeEmptinessBddAlgorithmType_TAG {
  BDD_OREG_JUSTICE_EMPTINESS_BDD_ALGORITHM_INVALID = -1,
  BDD_OREG_JUSTICE_EMPTINESS_BDD_ALGORITHM_EL_BWD  =  0,
  BDD_OREG_JUSTICE_EMPTINESS_BDD_ALGORITHM_EL_FWD,

  BDD_OREG_JUSTICE_EMPTINESS_BDD_ALGORITHM_MIN_VALID =
    BDD_OREG_JUSTICE_EMPTINESS_BDD_ALGORITHM_EL_BWD,
  BDD_OREG_JUSTICE_EMPTINESS_BDD_ALGORITHM_MAX_VALID =
    BDD_OREG_JUSTICE_EMPTINESS_BDD_ALGORITHM_EL_FWD
};
typedef enum BddOregJusticeEmptinessBddAlgorithmType_TAG
  BddOregJusticeEmptinessBddAlgorithmType;

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

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
  \brief const char* to BddOregJusticeEmptinessBddAlgorithmType

  Converts the given type from string "name" to a
                      BddOregJusticeEmptinessBddAlgorithmType object.

  \se None.

  \sa BddOregJusticeEmptinessBddAlgorithmType_to_string
*/
BddOregJusticeEmptinessBddAlgorithmType
Bdd_BddOregJusticeEmptinessBddAlgorithmType_from_string(const char* name);

/*!
  \brief BddOregJusticeEmptinessBddAlgorithmType to const char*

  It takes BddOregJusticeEmptinessBddAlgorithmType of
                      self and returns a string specifying the type of it.
                      Returned string is statically allocated and must not be
                      freed.

  \se None.

  \sa Bdd_BddOregJusticeEmptinessBddAlgorithmType_from_string
*/
const char* Bdd_BddOregJusticeEmptinessBddAlgorithmType_to_string(const BddOregJusticeEmptinessBddAlgorithmType self);

/*!
  \brief Prints the BDD-based algorithms to check language
                      emptiness for omega-regular properties the system
                      currently supplies

  

  \se None.

  \sa BddOregJusticeEmptinessBddAlgorithmType,
                      Bdd_BddOregJusticeEmptinessBddAlgorithmType_to_string
*/
void
  Bdd_print_available_BddOregJusticeEmptinessBddAlgorithms(FILE *file);

/*!
  \brief Checks options for forward Emerson-Lei algorithm

  Depending on the value of which_options, it checks that
                      forward search, ltl_tableau_forward_search, and
                      use_reachable_states are enabled and counter_examples is
                      disabled. Returns true if the checks are successful,
                      false otherwise. If on_fail_print is true, it prints an
                      error message on failure.

  \se None.
*/
boolean Bdd_elfwd_check_options(NuSMVEnv_ptr env,
                                       unsigned int which_options,
                                       boolean on_fail_print);

/*!
  \brief Checks, sets and saves previous values of options for
                      forward Emerson-Lei

  Which values are actually checked, set, and saved is
                      determined by the value of which_options. If set
                      in which_options, forward search,
                      ltl_tableau_forward_search, and
                      use_reachable_states are enabled and
                      counter_examples is disabled. Previous values
                      are stored and returned.

                      Creates the returned
                      BddELFwdSavedOptions_ptr. It does *not* belong
                      to caller - it will be destroyed by the
                      corresponding call to
                      Bdd_elfwd_restore_options.

  \se Modifies options.

  \sa Bdd_elfwd_restore_options
*/
BddELFwdSavedOptions_ptr
Bdd_elfwd_check_set_and_save_options(NuSMVEnv_ptr env,
                                     unsigned int which_options);

/*!
  \brief Restores previous values of options for forward
                      Emerson-Lei

  Which values are actually restored from saved_options is
                      determined by the value of which_options.

  \se Modifies options.

  \sa Bdd_elfwd_check_set_and_save_options
*/
void
Bdd_elfwd_restore_options(NuSMVEnv_ptr env,
                          unsigned int which_options,
                          BddELFwdSavedOptions_ptr saved_options);

/**AutomaticEnd***************************************************************/


#endif /* __NUSMV_CORE_FSM_BDD_BDD_H__ */
