/* ---------------------------------------------------------------------------


  This file is part of the ``cinit'' package of NuSMV version 2.
  Copyright (C) 2011 by FBK-irst.

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
  \author Alessandro Mariotti
  \brief Public interface of class 'NuSMVEnv'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_CINIT_NU_SMVENV_H__
#define __NUSMV_CORE_CINIT_NU_SMVENV_H__

#include "nusmv/core/utils/defs.h"
#include "cudd/st.h"

/*!
  \struct NuSMVEnv
  \brief Definition of the public accessor for class NuSMVEnv

  
*/
typedef struct NuSMVEnv_TAG*  NuSMVEnv_ptr;

/*!
  \brief To cast and check instances of class NuSMVEnv

  These macros must be used respectively to cast and to check
  instances of class NuSMVEnv
*/
#define NUSMV_ENV(self) \
         ((NuSMVEnv_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NUSMV_ENV_CHECK_INSTANCE(self) \
         (nusmv_assert(NUSMV_ENV(self) != NUSMV_ENV(NULL)))

/* Common used keys. Those are "fast" keys, since they start with a
   "+" and have a ascii character that follows it. All printable ASCII
   characters can be used, the rest of the string will be ignored, but
   it is left for debugging purposes */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_LOGGER          "+!_logger"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_STREAM_MANAGER  "+\"_stream_mgr"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_STRING_MGR      "+#_ustring_manager"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_WORD_NUMBER_MGR "+$_word_number_mgr"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_NODE_MGR        "+%_node_manager"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_ERROR_MANAGER   "+&_error_manager"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_EXPR_MANAGER    "+'_expr_manager"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_WFF_PRINTER     "+(_wff_printer"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_IWFF_PRINTER    "+)_iwff_printer"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_SEXP_PRINTER    "+*_sexp_printer"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_NODE_NORMALIZER "++_normalizer"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_OPTS_HANDLER    "+,_opts_handler"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_SYMB_TABLE      "+-_symb_table"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_FLAT_HIERARCHY  "+._flat_hierarchy"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_PROP_DB         "+/_prop_db"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_HRC_HIERARCHY   "+0_hrc_hierarchy"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_FSM_BUILDER     "+1_fsm_builder"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_DD_MGR          "+2_dd_manager"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_DD_VARS_HANDLER "+3_dd_vars_hand"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_TRACE_MGR       "+4_trace_mgr"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_SEXP_FSM        "+5_sexp_fsm"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_BOOL_FSM        "+6_bool_fsm"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_BDD_FSM         "+7_bdd_fsm"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_BE_FSM          "+8_be_fsm"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_BOOL_ENCODER    "+9_bool_enc"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_BDD_ENCODER     "+:_bdd_enc"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_BE_ENCODER      "+;_be_enc"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_MSAT_ENCODER    "+<_msat_enc"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_FLATTENER       "+=_flattener"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_DEPENDENCY      "+>_dependency"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENV_MASTER_LOGIC_RECOGNIZER "+?_master_logic_recognizer"
/* next ascii is "@" */

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof NuSMVEnv
  \brief The NuSMVEnv class constructor

  The NuSMVEnv class constructor

  \sa NuSMVEnv_destroy
*/
NuSMVEnv_ptr NuSMVEnv_create(void);

/*!
  \methodof NuSMVEnv
  \brief The NuSMVEnv class destructor

  The NuSMVEnv class destructor

  \sa NuSMVEnv_create
*/
void NuSMVEnv_destroy(NuSMVEnv_ptr self);


/* Getters and Setters ********************************************************/

/*!
  \methodof NuSMVEnv
  \brief Getter for the custom instances

  Getter for the custom instances.
                      The key is expected to exist and have a non NULL
                      value associated, otherwise an assertion is thrown.

                      If the key starts with characted '+', the key
                      will be considered a "fast-key". This means that
                      the second character of the string is used as an
                      index to an array. Using fast-keys is quite
                      faster than using normal keys, however,
                      fast-keys can be at most 222 (255 ASCII
                      characters minus the firsts 33). Have a look to
                      NuSMVEnv.h for checking out which ASCII
                      characters have been already used for
                      fast-keys.

  \sa NuSMVEnv_set_value
*/
void* NuSMVEnv_get_value(const NuSMVEnv_ptr self, const char* key);

/*!
  \methodof NuSMVEnv
  \brief Checks whether the given key exists in the environment

  Checks whether the given key exists in the environment

  \sa NuSMVEnv_create
*/
boolean NuSMVEnv_has_value(const NuSMVEnv_ptr self, const char* key);

/*!
  \methodof NuSMVEnv
  \brief Setter for custom instances

  Setter for custom instances. Should
                      be used only by initialization functions. The
                      string key must be unique: adding an existing
                      key will result in an assertion. The value must
                      be not NULL

                      If the key starts with characted '+', the key
                      will be considered a "fast-key". This means that
                      the second character of the string is used as an
                      index to an array. Using fast-keys is quite
                      faster than using normal keys, however,
                      fast-keys can be at most 222 (255 ASCII
                      characters minus the firsts 33). Have a look to
                      NuSMVEnv.h for checking out which ASCII
                      characters have been already used for
                      fast-keys.
*/
void NuSMVEnv_set_value(NuSMVEnv_ptr self, const char* key, void* value);

/*!
  \methodof NuSMVEnv
  \brief Setter for custom instances

  Setter for custom instances. Should
                      be used only by initialization functions. The
                      string key must be unique: adding an existing
                      key will replace the old value. The instance
                      argument must not be NULL

                      If the key starts with characted '+', the key
                      will be considered a "fast-key". This means that
                      the second character of the string is used as an
                      index to an array. Using fast-keys is quite
                      faster than using normal keys, however,
                      fast-keys can be at most 222 (255 ASCII
                      characters minus the firsts 33). Have a look to
                      NuSMVEnv.h for checking out which ASCII
                      characters have been already used for
                      fast-keys.
*/
void* NuSMVEnv_set_or_replace_value(NuSMVEnv_ptr self,
                                           const char* key,
                                           void* value);

/*!
  \methodof NuSMVEnv
  \brief Remover for custom instances

  Remover for custom instances. Should
                      be used only by deinitialization functions. The
                      key is expected to exist in the given
                      environment, an assertion is thrown otherwise
*/
void* NuSMVEnv_remove_value(NuSMVEnv_ptr self, const char* key);

/*!
  \methodof NuSMVEnv
  \brief Getter for flag values.

  Getter for flag values. Returns false
                      if the flag does not exist, and the value of the
                      flag otherwise
*/
boolean NuSMVEnv_get_flag(NuSMVEnv_ptr self, const char* flag);

/*!
  \methodof NuSMVEnv
  \brief Setter for flag values

  Setter for flag values
*/
void NuSMVEnv_set_flag(NuSMVEnv_ptr self,
                              const char* flag, boolean value);

/*!
  \methodof NuSMVEnv
  \brief Checks whether the given flag exists in the environment

  Checks whether the given flag exists in the environment

  \sa NuSMVEnv_create
*/
boolean NuSMVEnv_has_flag(NuSMVEnv_ptr self, const char* flag);

/*!
  \methodof NuSMVEnv
  \brief Remover for flags

  Remover for flags. Returns the value of the flag.
                      If the flag does not exist, returns false
*/
boolean NuSMVEnv_remove_flag(NuSMVEnv_ptr self, const char* flag);


/* Self-handled structures */

/*!
  \methodof NuSMVEnv
  \brief Retrieves a special hash_ptr instance handled by the env

  Retrieves a special hash_ptr instance handled by the env.
                      If the given hash already exists in the
                      NuSMVEnv, then that instance is
                      returned. Otherwise, a new one is created and
                      added in the environment. If the given key
                      already exists in the environment before the
                      first call of this function with the given key,
                      the behavior of this function is not
                      predictable. The returned hash_ptr will be freed
                      by the environment, however, entries in such
                      hash must be freed by the caller. Do not remove
                      the given key from the environment using
                      NuSMVEnv_remove_value
*/
st_table* NuSMVEnv_get_handled_hash_ptr(NuSMVEnv_ptr self,
                                               const char* key);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_CINIT_NU_SMVENV_H__ */
