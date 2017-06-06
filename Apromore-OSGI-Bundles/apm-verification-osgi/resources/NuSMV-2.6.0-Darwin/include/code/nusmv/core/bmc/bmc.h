/* ---------------------------------------------------------------------------


  This file is part of the ``bmc'' package of NuSMV version 2.
  Copyright (C) 2000-2001 by FBK-irst and University of Trento.

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
  \brief The header file for the <tt>bmc</tt> package

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_BMC_H__
#define __NUSMV_CORE_BMC_BMC_H__


/* all BMC modules: */
#include "nusmv/core/bmc/bmcBmc.h"
#include "nusmv/core/bmc/bmcPkg.h"

#include "nusmv/core/bmc/bmcGen.h"
#include "nusmv/core/bmc/bmcDump.h"
#include "nusmv/core/bmc/bmcTableau.h"
#include "nusmv/core/bmc/bmcModel.h"
#include "nusmv/core/bmc/bmcConv.h"
#include "nusmv/core/bmc/bmcCheck.h"
#include "nusmv/core/bmc/bmcUtils.h"


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/* BMC Option names */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_OPT_INITIALIZED "__bmc_opt_initialized__"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_MODE          "bmc_mode"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_DIMACS_FILENAME "bmc_dimacs_filename"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INVAR_DIMACS_FILENAME "bmc_invar_dimacs_filename"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_PB_LENGTH      "bmc_length"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_PB_LOOP        "bmc_loopback"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INVAR_ALG        "bmc_invar_alg"

#if NUSMV_HAVE_INCREMENTAL_SAT

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INC_INVAR_ALG        "bmc_inc_invar_alg"
#endif

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_OPTIMIZED_TABLEAU "bmc_optimized_tableau"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_FORCE_PLTL_TABLEAU "bmc_force_pltl_tableau"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_SBMC_IL_OPT "bmc_sbmc_il_opt"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_SBMC_GF_FG_OPT "bmc_sbmc_gf_fg_opt"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_SBMC_CACHE_OPT "bmc_sbmc_cache_opt"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_HAS_TO_SOLVE true

/*!
  \brief The names for INVAR solving algorithms (incremental and
  non-incremental).

  
*/
#define BMC_INVAR_ALG_CLASSIC       "classic"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INVAR_ALG_EEN_SORENSSON "een-sorensson"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INVAR_ALG_FALSIFICATION "falsification"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INC_INVAR_ALG_DUAL      "dual"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INC_INVAR_ALG_ZIGZAG    "zigzag"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INC_INVAR_ALG_FALSIFICATION "falsification"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INC_INVAR_ALG_INTERP_SEQ "interp_seq"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INC_INVAR_ALG_INTERPOLANTS "interpolants"

/*!
  \brief The names for INVAR closure strategies.

  Currently this applies to DUAL algorithm only
*/
#define BMC_INVAR_BACKWARD "backward"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_INVAR_FORWARD "forward"

/*!
  \brief Name of the watchdog timer used in the bmc algorithm

  Name of the watchdog timer used in the bmc algorithm
*/
#define BMC_WATCHDOG_NAME "bmc_watchdog"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief BMC invariant checking algorithms

  optional

  \sa optional
*/
typedef enum {
  ALG_UNDEFINED,
  ALG_CLASSIC,
  ALG_EEN_SORENSSON,
  ALG_FALSIFICATION,
  ALG_DUAL,
  ALG_ZIGZAG,

  ALG_INTERP_SEQ,
  ALG_INTERPOLANTS,
} bmc_invar_algorithm;


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


/* BMC Options */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_bmc_mode(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_bmc_mode(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_bmc_mode(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char* get_bmc_dimacs_filename(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bmc_dimacs_filename(OptsHandler_ptr, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
char* get_bmc_invar_dimacs_filename(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bmc_invar_dimacs_filename(OptsHandler_ptr, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bmc_pb_length(OptsHandler_ptr opt, const int k);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int get_bmc_pb_length(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bmc_pb_loop(OptsHandler_ptr opt, const char* loop);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char* get_bmc_pb_loop(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bmc_invar_alg(OptsHandler_ptr opt, const char* loop);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char* get_bmc_invar_alg(OptsHandler_ptr);
#if NUSMV_HAVE_INCREMENTAL_SAT

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bmc_inc_invar_alg(OptsHandler_ptr opt, const char* loop);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char* get_bmc_inc_invar_alg(OptsHandler_ptr);
#endif

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bmc_optimized_tableau(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_bmc_optimized_tableau(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_bmc_optimized_tableau(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    set_bmc_force_pltl_tableau(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void    unset_bmc_force_pltl_tableau(OptsHandler_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_bmc_force_pltl_tableau(OptsHandler_ptr);


/* SBMC Options */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bmc_sbmc_gf_fg_opt(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_bmc_sbmc_gf_fg_opt(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_bmc_sbmc_gf_fg_opt(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bmc_sbmc_il_opt(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_bmc_sbmc_il_opt(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_bmc_sbmc_il_opt(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void set_bmc_sbmc_cache(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void unset_bmc_sbmc_cache(OptsHandler_ptr opt);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean opt_bmc_sbmc_cache(OptsHandler_ptr opt);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_H__ */
