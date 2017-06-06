/* ---------------------------------------------------------------------------


  This file is part of the ``opt'' package of NuSMV version 2.
  Copyright (C) 1998-2001 by CMU and FBK-irst.

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
  \author Marco Roveri
  \brief The option header file.

  This file conatins a data structure to manage all the
  command line options of the NuSMV system.

*/


#ifndef __NUSMV_CORE_OPT_OPT_H__
#define __NUSMV_CORE_OPT_OPT_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#if NUSMV_HAVE_REGEX_H
# if NUSMV_HAVE_SYS_TYPES_H
/* posix requires that sys/types.h is included before regex */
#  include <sys/types.h>
# endif
# include <regex.h>
#endif

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/sat/sat.h" /* for SAT Solver */
#include "nusmv/core/trans/trans.h" /* for TransType */
#include "nusmv/core/enc/enc.h" /* for VarsOrderType and BddSohEnum*/
#include "nusmv/core/fsm/bdd/bdd.h" /* for BddOregJusticeEmptinessBddAlgorithmType */
#include "nusmv/core/be/be.h" /* For RBC2CNF algorithms */
#include "nusmv/core/opt/OptsHandler.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/* Opts names */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_PGM_NAME   NUSMV_PACKAGE_NAME

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_PGM_PATH   (char *)NULL

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_INPUT_FILE (char *)NULL

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_INPUT_ORDER_FILE (char *)NULL

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_OUTPUT_ORDER_FILE "temp.ord"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_TRANS_ORDER_FILE (char *)NULL

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_PP_CPP_PATH (char *)NULL

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_PP_M4_PATH (char *)NULL

/* outputs warning instead of errors in type checking */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BACKWARD_COMPATIBILITY false
/* allows warning messages to be printed during type checking */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_TYPE_CHECKING_WARNING_ON true

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_CONJ_PART_THRESHOLD 1000

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_IMAGE_CLUSTER_SIZE 1000

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_SHOWN_STATES 25
/* maximum number of states shown during an interactive simulation step*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MAX_SHOWN_STATES 65535

#if NUSMV_HAVE_SOLVER_MINISAT

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_SAT_SOLVER        "MiniSat"
#else
#if NUSMV_HAVE_SOLVER_ZCHAFF
#define DEFAULT_SAT_SOLVER        "zchaff"
#else
#define DEFAULT_SAT_SOLVER        (char*)NULL
#endif
#endif

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OPT_USER_POV_NULL_STRING  "" /* user pov of the null string */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_OREG_JUSTICE_EMPTINESS_BDD_ALGORITHM \
  BDD_OREG_JUSTICE_EMPTINESS_BDD_ALGORITHM_EL_BWD

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_SHOW_DEFINES_IN_TRACES true

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_SHOW_DEFINES_WITH_NEXT true

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_USE_COI_SIZE_SORTING true

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BDD_ENCODE_WORD_BITS true

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum {
  FORWARD,
  BACKWARD,
  FORWARD_BACKWARD,
  BDD_BMC
} Check_Strategy;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_INVAR_CHECK_STRATEGY FORWARD

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum {
  ZIGZAG_HEURISTIC,
  SMALLEST_BDD_HEURISTIC
} FB_Heuristic;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_FORWARD_BACKWARD_ANALYSIS_HEURISTIC ZIGZAG_HEURISTIC

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum {
  STEPS_HEURISTIC,
  SIZE_HEURISTIC
} Bdd2bmc_Heuristic;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BDD2BMC_HEURISTIC STEPS_HEURISTIC

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_DAGGIFIER_ENABLED true

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_DAGGIFIER_COUNTER_THS 3

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_DAGGIFIER_DEPTH_THS 2

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BDD2BMC_HEURISTIC_THRESHOLD 10

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define APPEND_CLUSTERS_VISIBLE 0

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROGRAM_NAME      "program_name"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROGRAM_PATH      "program_path"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define INPUT_FILE        "input_file"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SCRIPT_FILE       "script_file"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define INPUT_ORDER_FILE  "input_order_file"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OUTPUT_ORDER_FILE "output_order_file"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRANS_ORDER_FILE  "trans_order_file"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OUTPUT_FLATTEN_MODEL_FILE "output_flatten_model_file"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OUTPUT_BOOLEAN_MODEL_FILE "output_boolean_model_file"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OUTPUT_WORD_FORMAT "output_word_format"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BACKWARD_COMPATIBILITY "backward_compatibility"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TYPE_CHECKING_WARNING_ON "type_checking_warning_on"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define VERBOSE_LEVEL     "verbose_level"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RUN_CPP           "run_cpp"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PP_LIST           "pp_list"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SHOWN_STATES      "shown_states"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define IGNORE_SPEC       "ignore_spec"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define IGNORE_COMPUTE    "ignore_compute"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define IGNORE_LTLSPEC    "ignore_ltlspec"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define IGNORE_PSLSPEC    "ignore_pslspec"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OPT_CHECK_FSM   "check_fsm"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define IGNORE_INVAR      "ignore_invar"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FORWARD_SEARCH    "forward_search"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LTL_TABLEAU_FORWARD_SEARCH "ltl_tableau_forward_search"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PRINT_REACHABLE   "print_reachable"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENABLE_REORDER    "enable_reorder"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define REORDER_METHOD    "reorder_method"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DYNAMIC_REORDER   "dynamic_reorder"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ENABLE_SEXP2BDD_CACHING   "enable_sexp2bdd_caching"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PARTITION_METHOD  "partition_method"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CONJ_PART_THRESHOLD "conj_part_threshold"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define IMAGE_CLUSTER_SIZE "image_cluster_size"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define IGNORE_INIT_FILE  "ignore_init_file"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define AG_ONLY_SEARCH    "ag_only_search"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CONE_OF_INFLUENCE "cone_of_influence"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LIST_PROPERTIES "list_properties"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_PRINT_METHOD "prop_print_method"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_NO         "prop_no"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define A_SAT_SOLVER "sat_solver"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define IWLS95_PREORDER  "iwls95preorder"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define AFFINITY_CLUSTERING  "affinity"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define APPEND_CLUSTERS  "append_clusters"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define USE_REACHABLE_STATES  "use_reachable_states"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define USE_FAIR_STATES  "use_fair_states"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define COUNTER_EXAMPLES  "counter_examples"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACES_HIDING_PREFIX  "traces_hiding_prefix"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_TRACES_HIDING_PREFIX  "__"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_ENCODE_WORD_BITS "bdd_encode_word_bits"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PP_CPP_PATH "pp_cpp_path"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PP_M4_PATH "pp_m4_path"

#if NUSMV_HAVE_REGEX_H

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACES_REGEXP  "traces_regexp"
#endif

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_TRACE_PLUGIN  "default_trace_plugin"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ON_FAILURE_SCRIPT_QUITS "on_failure_script_quits"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define WRITE_ORDER_DUMPS_BITS "write_order_dumps_bits"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define USE_ANSI_C_DIV_OP "use_ansi_c_div_op"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define VARS_ORD_TYPE "vars_order_type"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_STATIC_ORDER_HEURISTICS   "bdd_static_order_heuristics"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBC_CNF_ALGORITHM "rbc_rbc2cnf_algorithm"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SYMB_INLINING "sexp_inlining"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBC_INLINING "rbc_inlining"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBC_INLINING_LAZY "rbc_inlining_lazy"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SHOW_DEFINES_IN_TRACES "traces_show_defines"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SHOW_DEFINES_WITH_NEXT "traces_show_defines_with_next"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define INVAR_CHECK_STRATEGY "check_invar_strategy"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CHECK_INVAR_FB_HEURISTIC "check_invar_forward_backward_heuristic"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CHECK_INVAR_BDDBMC_HEURISTIC "check_invar_bddbmc_heuristic"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CHECK_INVAR_BDDBMC_HEURISTIC_THRESHOLD "check_invar_bddbmc_threshold"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DAGGIFIER_ENABLED "daggifier_enabled"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DAGGIFIER_COUNTER_THRESHOLD "daggifier_counter_threshold"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DAGGIFIER_DEPTH_THRESHOLD "daggifier_depth_threshold"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DAGGIFIER_STATISTICS "daggifier_statistics"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OREG_JUSTICE_EMPTINESS_BDD_ALGORITHM    \
  "oreg_justice_emptiness_bdd_algorithm"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define USE_COI_SIZE_SORTING "use_coi_size_sorting"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BATCH "batch"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define QUIET_MODE "quiet_mode"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DISABLE_SYNTACTIC_CHECKS "disable_syntactic_checks"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define KEEP_SINGLE_VALUE_VARS "keep_single_value_vars"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_SIMULATION_STEPS "default_simulation_steps"

/* For modifying the behavior of ltl2smv to compact the fairness in
   one single justice constraint instead of possibly moer than one. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LTL2SMV_SINGLE_JUSTICE "ltl2smv_single_justice"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_LTL2SMV_SINGLE_JUSTICE false

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BOOLEAN_CONVERSION_USES_PREDICATE_NORMALIZATION "boolean_conversion_uses_predicate_normalization"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DEFAULT_BOOLEAN_CONVERSION_USES_PREDICATE_NORMALIZATION false

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct options
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct options_TAG*  options_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_use_reachable_states(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_use_reachable_states(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_use_reachable_states(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_use_fair_states(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_use_fair_states(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_use_fair_states(OptsHandler_ptr);


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_pgm_name(OptsHandler_ptr, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_pgm_name(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_pgm_name(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_script_file(OptsHandler_ptr, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_script_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_script_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_pgm_path(OptsHandler_ptr, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_pgm_path(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_pgm_path(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_input_file(OptsHandler_ptr, const char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_input_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_input_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_input_order_file(OptsHandler_ptr, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_input_order_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_input_order_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_output_order_file(OptsHandler_ptr, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_output_order_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_output_order_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean is_default_order_file(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_trans_order_file(OptsHandler_ptr, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_trans_order_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_trans_order_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_trans_order_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_output_flatten_model_file(OptsHandler_ptr, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_output_flatten_model_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_output_flatten_model_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_output_boolean_model_file(OptsHandler_ptr, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_output_boolean_model_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_output_boolean_model_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_output_word_format(OptsHandler_ptr, int i);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int     get_output_word_format(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_backward_comp(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_backward_comp(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_backward_comp(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_type_checking_warning_on(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_type_checking_warning_on(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_type_checking_warning_on(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_verbose_level(OptsHandler_ptr, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int     get_verbose_level(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_verbose_level_eq(OptsHandler_ptr, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_verbose_level_gt(OptsHandler_ptr, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_verbose_level_ge(OptsHandler_ptr, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_verbose_level_lt(OptsHandler_ptr, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_verbose_level_le(OptsHandler_ptr, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_pp_list(OptsHandler_ptr, char *, const NuSMVEnv_ptr env);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_pp_list(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_shown_states_level(OptsHandler_ptr, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int     opt_shown_states_level(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_ignore_spec(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_ignore_spec(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_ignore_spec(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_ignore_compute(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_ignore_compute(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_ignore_compute(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_ignore_ltlspec(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_ignore_ltlspec(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_ignore_ltlspec(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_ignore_pslspec(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_ignore_pslspec(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_ignore_pslspec(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_check_fsm(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_check_fsm(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_check_fsm(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_ignore_invar(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_ignore_invar(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_ignore_invar(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_forward_search(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_forward_search(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_forward_search(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_ltl_tableau_forward_search(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_ltl_tableau_forward_search(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_ltl_tableau_forward_search(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_print_reachable(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_print_reachable(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_print_reachable(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_reorder(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_reorder(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_reorder(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_reorder_method(OptsHandler_ptr, unsigned int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
unsigned int get_reorder_method(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_dynamic_reorder(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_dynamic_reorder(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_dynamic_reorder(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_enable_sexp2bdd_caching(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_enable_sexp2bdd_caching(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_enable_sexp2bdd_caching(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_batch(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_batch(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_batch(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_partition_method(OptsHandler_ptr, const TransType);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
TransType get_partition_method(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_partitioning_method(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_monolithic(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_conj_partitioning(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_iwls95cp_partitioning(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_monolithic(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_conj_partitioning(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_iwls95cp_partitioning(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_conj_part_threshold(OptsHandler_ptr, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_conj_part_threshold(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int     get_conj_part_threshold(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_image_cluster_size(OptsHandler_ptr, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void reset_image_cluster_size(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int get_image_cluster_size(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_ignore_init_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_ignore_init_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_ignore_init_file(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_ag_only(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_ag_only(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_ag_only(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_cone_of_influence(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_cone_of_influence(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_cone_of_influence(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_list_properties(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_list_properties(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_list_properties(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_pp_cpp_path(OptsHandler_ptr, const char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_pp_cpp_path(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_pp_cpp_path(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_pp_m4_path(OptsHandler_ptr, const char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    reset_pp_m4_path(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char *  get_pp_m4_path(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_prop_print_method(OptsHandler_ptr opt, const char* string);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void reset_prop_print_method(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int get_prop_print_method(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_prop_no(OptsHandler_ptr, int n);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int     get_prop_no(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void print_partition_method(FILE *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_sat_solver(OptsHandler_ptr, const char*);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char* get_sat_solver(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean set_default_trace_plugin(OptsHandler_ptr opt, int plugin);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int get_default_trace_plugin(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_iwls95_preorder(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_iwls95_preorder(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_iwls95_preorder(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_affinity(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_affinity(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_affinity(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_append_clusters(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_append_clusters(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_append_clusters(OptsHandler_ptr);

/* counter examples */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_counter_examples(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_counter_examples(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_counter_examples(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_traces_hiding_prefix(OptsHandler_ptr, const char*);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char* opt_traces_hiding_prefix(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bdd_encoding_word_bits(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_bdd_encoding_word_bits(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void reset_bdd_encoding_word_bits(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_bdd_encoding_word_bits(OptsHandler_ptr opt);

#if NUSMV_HAVE_REGEX_H

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char* opt_traces_regexp(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean set_traces_regexp(OptsHandler_ptr, const char*);
#endif

/* others */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_on_failure_script_quits(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_on_failure_script_quits(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_on_failure_script_quits(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_write_order_dumps_bits(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_write_order_dumps_bits(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_write_order_dumps_bits(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_use_ansi_c_div_op(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_use_ansi_c_div_op(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_vars_order_type(OptsHandler_ptr, VarsOrdType);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
VarsOrdType get_vars_order_type(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bdd_static_order_heuristics(OptsHandler_ptr, BddSohEnum value);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
BddSohEnum get_bdd_static_order_heuristics(OptsHandler_ptr);

/* inlining */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_symb_inlining(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_symb_inlining(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_symb_inlining(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_rbc_inlining(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_rbc_inlining(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_rbc_inlining(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_rbc_inlining_lazy(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_rbc_inlining_lazy(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_rbc_inlining_lazy(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_use_coi_size_sorting(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_use_coi_size_sorting(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_use_coi_size_sorting(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void opt_disable_syntactic_checks(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void opt_enable_syntactic_checks(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_syntactic_checks_disabled(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_keep_single_value_vars(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_keep_single_value_vars(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_keep_single_value_vars(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_show_defines_in_traces(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_show_defines_in_traces(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_show_defines_in_traces(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_show_defines_with_next(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_show_defines_with_next(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_show_defines_with_next(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
set_check_invar_strategy(OptsHandler_ptr opt, Check_Strategy strategy);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
Check_Strategy opt_check_invar_strategy(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char* opt_check_invar_strategy_as_string(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
set_check_invar_fb_heuristic(OptsHandler_ptr opt, FB_Heuristic strategy);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
FB_Heuristic opt_check_invar_fb_heuristic(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char* opt_check_invar_fb_heuristic_as_string(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
set_check_invar_bddbmc_heuristic(OptsHandler_ptr opt,
                                 Bdd2bmc_Heuristic strategy);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
Bdd2bmc_Heuristic
opt_check_invar_bddbmc_heuristic(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char*
opt_check_invar_bddbmc_heuristic_as_string(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
set_check_invar_bddbmc_heuristic_threshold(OptsHandler_ptr opt, int t);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int
opt_check_invar_bddbmc_heuristic_threshold(OptsHandler_ptr opt);

/* Daggifier on/off */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_is_daggifier_enabled(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void opt_enable_daggifier(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void opt_disable_daggifier(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int opt_get_daggifier_counter_threshold(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void opt_set_daggifier_counter_threshold(OptsHandler_ptr opt,
                                                int x);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int opt_get_daggifier_depth_threshold(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void opt_set_daggifier_depth_threshold(OptsHandler_ptr opt,
                                              int x);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_get_quiet_mode(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_quiet_mode(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_quiet_mode(OptsHandler_ptr opt);

/* Daggifier statistics */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_get_daggifier_statistics(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_daggifier_statistics(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_daggifier_statistics(OptsHandler_ptr opt);

/* different BDD-based algorithms to check language emptiness for
   omega-regular properties */
BddOregJusticeEmptinessBddAlgorithmType
  get_oreg_justice_emptiness_bdd_algorithm(OptsHandler_ptr opt);
void set_oreg_justice_emptiness_bdd_algorithm
(OptsHandler_ptr opt, BddOregJusticeEmptinessBddAlgorithmType alg);
void reset_oreg_justice_emptiness_bdd_algorithm
(OptsHandler_ptr opt);

/* RBC2CNF */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
set_rbc2cnf_algorithm(OptsHandler_ptr opt, Be_CnfAlgorithm alg);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
unset_rbc2cnf_algorithm(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
Be_CnfAlgorithm
get_rbc2cnf_algorithm(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_default_simulation_steps(OptsHandler_ptr, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void reset_default_simulation_steps(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int get_default_simulation_steps(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_ltl2smv_single_justice(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_ltl2smv_single_justice(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_ltl2smv_single_justice(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_boolconv_uses_prednorm(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_boolconv_uses_prednorm(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_boolconv_uses_prednorm(OptsHandler_ptr opt);

#endif /* __NUSMV_CORE_OPT_OPT_H__ */
