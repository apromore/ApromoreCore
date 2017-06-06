/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
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
  \brief Public interface of class 'ErrorMgr'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_ERROR_MGR_H__
#define __NUSMV_CORE_UTILS_ERROR_MGR_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/cinit/NuSMVEnv.h"
#include "nusmv/core/prop/Prop.h"
#include "nusmv/core/utils/StreamMgr.h"
#include "nusmv/core/opt/opt.h"
#include "nusmv/core/node/NodeMgr.h"
#include "nusmv/core/node/printers/MasterPrinter.h"
#include "nusmv/core/utils/UStringMgr.h"

/*!
  \struct ErrorMgr
  \brief Definition of the public accessor for class ErrorMgr

  
*/
typedef struct ErrorMgr_TAG*  ErrorMgr_ptr;


typedef enum FailureKind_TAG {
  FAILURE_DIV_BY_ZERO,
  FAILURE_CASE_NOT_EXHAUSTIVE,
  FAILURE_ARRAY_OUT_OF_BOUNDS,
  FAILURE_UNSPECIFIED
} FailureKind;

/*!
  \brief To cast and check instances of class ErrorMgr

  These macros must be used respectively to cast and to check
  instances of class ErrorMgr
*/
#define ERROR_MGR(self) \
         ((ErrorMgr_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ERROR_MGR_CHECK_INSTANCE(self) \
         (nusmv_assert(ERROR_MGR(self) != ERROR_MGR(NULL)))

/*@-skipposixheaders@*/
/*@-skipisoheaders@*/
#include <setjmp.h>
/*@=skipposixheaders@*/
/*@=skipisoheaders@*/
/* New versions of cygwin do not need special treatments */
/* #ifdef __CYGWIN__  */
/* #define JMPBUF jmp_buf */
/* #define SETJMP(buf,val) setjmp(buf) */
/* #define LONGJMP(buf,val) longjmp(buf, val) */
/* #else */
#if defined(__MINGW32__) || defined(_MSC_VER)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define JMPBUF jmp_buf

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SETJMP(buf,val) setjmp(buf)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LONGJMP(buf,val) longjmp(buf, val)
#else
#define JMPBUF sigjmp_buf
#define SETJMP(buf,val) sigsetjmp(buf, val)
#define LONGJMP(buf,val) siglongjmp(buf, val)
#endif
/* #endif */

/* warning take care not to do something like this:

  CATCH(errmgr) {
     cmd1....
     return 1;
  } FAIL(errmgr) {
     cmd2...
    return 0;
  }

  The right way to use it is:

  {
    type result;

    CATCH(errmgr) {
     cmd1....
     result = value;
    } FAIL(errmgr) {
     cmd2...
     result = 1;
    }
    return(result);
  }
  I.e. return inside CATCH/FAIL may cause damage of the stack
*/

/* To exploit the CATCH/FAIL mechanism, you can use any ErrorMgr exit function */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ErrorMgr_set_long_jmp(err)              \
  SETJMP(*(ErrorMgr_new_long_jmp(err)), 1)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CATCH(err) if (ErrorMgr_set_long_jmp(err) == 0) {

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FAIL(err)  ErrorMgr_cancel_long_jmp(err); } else



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof ErrorMgr
  \brief The ErrorMgr class constructor

  The ErrorMgr class constructor

                      Environment requisites:
                      - No instances registered with key ENV_ERROR_MANAGER
                      - OptsHandler instance registered as ENV_OPTS_HANDLER
                      - StreamMgr instance registered as ENV_STREAM_MANAGER
                      - UStringMgr instance registered as ENV_STRING_MGR

  \sa ErrorMgr_destroy
*/
ErrorMgr_ptr ErrorMgr_create(const NuSMVEnv_ptr env);

/*!
  \methodof ErrorMgr
  \brief The ErrorMgr class destructor

  The ErrorMgr class destructor

  \sa ErrorMgr_create
*/
void ErrorMgr_destroy(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief The ErrorMgr StreamMgr getter

  The ErrorMgr StreamMgr getter
*/
StreamMgr_ptr
ErrorMgr_get_stream_manager(const ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief The ErrorMgr  UStringMgr getter

  The ErrorMgr  UStringMgr getter
*/
UStringMgr_ptr
ErrorMgr_get_string_manager(const ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief The ErrorMgr OptsHandler getter

  The ErrorMgr OptsHandler getter
*/
OptsHandler_ptr
ErrorMgr_get_options_handler(const ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief \todo Missing synopsis

  \todo Missing description
*/
node_ptr ErrorMgr_get_the_node(const ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief \todo Missing synopsis

  \todo Missing description
*/
void ErrorMgr_set_the_node(ErrorMgr_ptr self, node_ptr node);

/*!
  \methodof ErrorMgr
  \brief Save stack context for non-local goto

  Saves the stack environment in the local
                      array jmp_buf_arr for later use by
                      ErrorMgr_long_jmp.

  \sa ErrorMgr_long_jmp
*/
JMPBUF*
ErrorMgr_new_long_jmp(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief Restore the environment saved in <code>jmp_buf</code>.

  Restores the environment saved by the last call of

                      SETJMP* ErroMgr_new_long_jmp(). After
                      ErrorMgr_long_jmp() is completed, program
                      execution continues as if the corresponding call
                      of <code>SETJMP()</code> had just returned a
                      value different from <code>0</code> (zero).
*/
void
ErrorMgr_long_jmp(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief Pop one of the environments saved in jmp_buf

  Removes the last envirnoment saved in
                      jmp_buf by a SETJMP(*(ErrorMgr_new_long_jmp())
                      call.
*/
void
ErrorMgr_cancel_long_jmp(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief Reset environment saved in jmp_buf.

  Resets the environment saved by the calls to
                      SETJMP(*(ErroMgr_new_long_jmp())). After this
                      call, all the longjump points previously stored
                      are cancelled.
*/
void
ErrorMgr_reset_long_jmp(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief General routine to start error reporting.

  This is a general routine to be called by error
                      reporting routines as first call. The file name
                      and corresponding line number of the token that
                      has generated the error (which is retrieved by
                      <code>ErrorMgr_get_the_node(errmgr)</code> are printed out.

  \sa ErrorMgr_finish_parsing_err
*/
void
ErrorMgr_start_parsing_err(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief General routine to terminate error reporting.

  This is the general routine to be called as last
                      routine in specific error reporting routines.
                      If error happens during flattening, the system
                      is also reset.  Finally, a call to
                      ErrorMgr_nusmv_exit(errmgr) is performed.
*/
void
ErrorMgr_finish_parsing_err (ErrorMgr_ptr self) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief General exit routine.

  If non local goto are anebaled, instead of
                      exiting from the program, then the non local
                      goto is executed.
                      int n: the exit code

  \sa ErrorMgr_set_jmp ErrorMgr_long_jmp
*/
void
ErrorMgr_nusmv_exit (ErrorMgr_ptr self, int n) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief General error reporting routine.

  Produces a message on the
                      error stream of the stream manager. The arguments
                      are similar to those of the printf, but only if
                      fmt is not NULL or the empty string
*/
void
ErrorMgr_rpterr (ErrorMgr_ptr self, const char* fmt, ...) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief General error reporting routine

  Produces a message on the error streams of the
                      stream manager. The arguments are similar to
                      those of the printf, except argument "node"
                      which is output at the end of the message with
                      function print_node
*/
void
ErrorMgr_rpterr_node (ErrorMgr_ptr self, node_ptr node,
                           const char* fmt, ...) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief Prints an error message, and returns

  No exception is raised, the message is simply printed 
  out to the error stream
*/
void ErrorMgr_error_msg(const ErrorMgr_ptr self,
                               const char* format, ...);

/*!
  \methodof ErrorMgr
  \brief Prints out a warning.

  Produces a warning message on the error stream of the
                      stream manager. The arguments are similar to
                      those of the printf.
*/
void ErrorMgr_warning_msg(ErrorMgr_ptr self,
                                 const char * fmt, ...);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
boolean
ErrorMgr_io_atom_is_empty(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_io_atom_push(ErrorMgr_ptr self, node_ptr s);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_io_atom_pop(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
node_ptr
ErrorMgr_io_atom_head(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_print_io_atom_stack(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief Prints out an internal error.

  Produces a message on the  error stream of the
                      stream manager.  The message is considered an
                      internal error. The arguments are similar to
                      those of the printf.
*/
void
ErrorMgr_internal_error (ErrorMgr_ptr self,
                              const char * fmt, ...) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_multiple_substitution (ErrorMgr_ptr self,
                                           node_ptr nodep) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  n must be a FAILURE node
*/
void
ErrorMgr_report_failure_node (ErrorMgr_ptr self, node_ptr n) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_failure_node(ErrorMgr_ptr self, node_ptr n);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void ErrorMgr_warning_case_not_exhaustive(ErrorMgr_ptr self,
                                                 node_ptr failure);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_possible_div_by_zero(ErrorMgr_ptr self,
                                      node_ptr failure);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_possible_array_out_of_bounds(ErrorMgr_ptr self,
                                              node_ptr failure);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_array_out_of_bounds (ErrorMgr_ptr self,
                                         int index, int low, int high) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_lhs_of_index_is_not_array (ErrorMgr_ptr self) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_div_by_zero (ErrorMgr_ptr self, node_ptr expr) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_div_by_nonconst (ErrorMgr_ptr self, node_ptr expr) NUSMV_FUNCATTR_NORETURN;


/*!
  \methodof ErrorMgr
  \brief 

  
*/
void ErrorMgr_error_mod_by_nonword (ErrorMgr_ptr self, node_ptr expr);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_type_error (ErrorMgr_ptr self, node_ptr n) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_range_error (ErrorMgr_ptr self, node_ptr n, node_ptr var) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_range_warning(ErrorMgr_ptr self, node_ptr n, node_ptr var);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_multiple_assignment (ErrorMgr_ptr self, node_ptr t1) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_empty_range (ErrorMgr_ptr self, node_ptr name, int dim1, int dim2) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_not_word_wsizeof (ErrorMgr_ptr self, node_ptr expr) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_not_constant_extend_width (ErrorMgr_ptr self,
                                               const node_ptr expr) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_not_constant_resize_width (ErrorMgr_ptr self,
                                               const node_ptr expr) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_not_constant_wtoint (ErrorMgr_ptr self, const node_ptr expr) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_not_constant_width_of_word_type (ErrorMgr_ptr self,
                                                     node_ptr name) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_not_constant_width_of_word_array_type (ErrorMgr_ptr self,
                                                           node_ptr name) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_not_constant_width_of_array_type (ErrorMgr_ptr self,
                                                      node_ptr name) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_wrong_word_operand (ErrorMgr_ptr self,
                                        const char* msg, node_ptr expr) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_assign_both (ErrorMgr_ptr self, node_ptr v, node_ptr v1,
                                 int lineno, int lineno2) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_unknown_var_in_order_file (ErrorMgr_ptr self, node_ptr n) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_variable_not_declared(ErrorMgr_ptr self, node_ptr vname);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_missing_variable(ErrorMgr_ptr self, node_ptr vname);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_missing_variables(ErrorMgr_ptr self,
                                   NodeList_ptr vars_list);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_non_ag_only_spec(ErrorMgr_ptr self, Prop_ptr prop);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_ag_only_without_reachables(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_fsm_init_empty(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_fsm_invar_empty(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_fsm_fairness_empty(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_fsm_init_and_fairness_empty(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_var_appear_twice_in_order_file (ErrorMgr_ptr self,
                                                    node_ptr n) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_var_appear_twice_in_order_file(ErrorMgr_ptr self,
                                                node_ptr n);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_id_appears_twice_in_idlist_file(ErrorMgr_ptr self,
                                                 node_ptr n);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_var_not_in_order_file (ErrorMgr_ptr self, node_ptr n) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_not_proper_number (ErrorMgr_ptr self,
                                       const char* op, node_ptr n) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_not_proper_numbers (ErrorMgr_ptr self, const char* op,
                                        node_ptr n1, node_ptr n2) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_ambiguous (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_undefined (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_shadowing (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_redefining (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_redefining_operational_symbol (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_redefining_input_var (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_reassigning (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_assign_input_var (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_assign_frozen_var (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_assign_expected_var (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_circular (ErrorMgr_ptr self, node_ptr s) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_too_many_vars (ErrorMgr_ptr self) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_out_of_memory (ErrorMgr_ptr self, size_t size) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_invalid_subrange (ErrorMgr_ptr self, node_ptr range) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_invalid_bool_cast (ErrorMgr_ptr self, node_ptr expr) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_out_of_bounds_word_toint_cast (ErrorMgr_ptr self,
                                                   node_ptr expr) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_invalid_toint_cast (ErrorMgr_ptr self, node_ptr expr) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_invalid_count_operator (ErrorMgr_ptr self, node_ptr expr) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_invalid_enum_value (ErrorMgr_ptr self, node_ptr value) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  TODO[AMA] This should not stay here, see issue 4485
*/
void
ErrorMgr_error_game_definition_contains_input_vars (ErrorMgr_ptr self,
                                                         node_ptr var_name) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_property_contains_input_vars (ErrorMgr_ptr self,
                                                  Prop_ptr prop) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_assign_exp_contains_input_vars (ErrorMgr_ptr self,
                                                    node_ptr exp) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_next_exp_contains_input_vars (ErrorMgr_ptr self,
                                                  node_ptr exp) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_invar_exp_contains_input_vars (ErrorMgr_ptr self,
                                                   node_ptr exp) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_init_exp_contains_input_vars (ErrorMgr_ptr self, node_ptr exp) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  TODO[AMA] This should not stay here, see issue 4485
*/
void
ErrorMgr_error_second_player_var (ErrorMgr_ptr self, node_ptr exp) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  TODO[AMA] This should not stay here, see issue 4485
*/
void
ErrorMgr_error_second_player_next_var (ErrorMgr_ptr self, node_ptr exp) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_unknown_preprocessor (ErrorMgr_ptr self,
                                          const char* prep_name) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_set_preprocessor(ErrorMgr_ptr self,
                                const char* name,
                                boolean is_warning);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_type_system_violation (ErrorMgr_ptr self) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_psl_not_supported_feature (ErrorMgr_ptr self) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_psl_not_supported_feature_next_number (ErrorMgr_ptr self) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_not_supported_feature (ErrorMgr_ptr self, const char* msg) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_expected_number (ErrorMgr_ptr self) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_psl_not_supported_feature(ErrorMgr_ptr self,
                                           node_ptr psl_spec, int index);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_psl_repeated_replicator_id (ErrorMgr_ptr self) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_invalid_number(ErrorMgr_ptr self, const char* szNumber);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_bmc_invalid_k_l(ErrorMgr_ptr self,const int k, const int l);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_property_already_specified(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_invalid_numeric_value (ErrorMgr_ptr self,
                                           int value, const char* reason) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_file_not_found (ErrorMgr_ptr self, const char* filename) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_error_file_clobbering (ErrorMgr_ptr self, const char* filename) NUSMV_FUNCATTR_NORETURN;

/*!
  \methodof ErrorMgr
  \brief 

  
*/
void
ErrorMgr_warning_processes_deprecated(ErrorMgr_ptr self);

/*!
  \methodof ErrorMgr
  \brief Builder for FAILURE nodes

  
*/
node_ptr
ErrorMgr_failure_make(ErrorMgr_ptr self, const char* msg,
                      FailureKind kind, int lineno);

/*!
  \methodof ErrorMgr
  \brief Returns the message string associated to the
   failure node

  
*/
const char*
ErrorMgr_failure_get_msg(ErrorMgr_ptr self, node_ptr failure);

/*!
  \methodof ErrorMgr
  \brief Returns the failure kind associated to the
                      failure node

  
*/
FailureKind
ErrorMgr_failure_get_kind(ErrorMgr_ptr self, node_ptr failure);

/*!
  \methodof ErrorMgr
  \brief 

  
*/
int
ErrorMgr_failure_get_lineno(ErrorMgr_ptr self, node_ptr failure);

/*!
  \methodof ErrorMgr
  \brief Enable "tag"
  See TagInfo struct for information about tags
*/
void ErrorMgr_enable_tag(ErrorMgr_ptr self, const char* tag);

/*!
  \methodof ErrorMgr
  \brief Disable "tag"
  See TagInfo struct for information about tags
*/
void ErrorMgr_disable_tag(ErrorMgr_ptr self, const char* tag);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_ERROR_MGR_H__ */
