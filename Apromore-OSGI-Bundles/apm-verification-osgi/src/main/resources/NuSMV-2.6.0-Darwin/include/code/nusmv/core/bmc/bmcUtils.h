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
  \author Roberto Cavada, Marco Benedetti
  \brief The public interface to the bmc utilities

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_BMC_UTILS_H__
#define __NUSMV_CORE_BMC_BMC_UTILS_H__

#include "nusmv/core/trace/Trace.h"
#include "nusmv/core/trace/TraceMgr.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/ucmd.h"


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define UNKNOWN_OP      -1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CONSTANT_EXPR    0

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LITERAL          1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_CONNECTIVE  2

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TIME_OPERATOR    3

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

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
#define isConstantExpr(op) ((op)==TRUEEXP)  || ((op)==FALSEEXP)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define isVariable(op)     (((op)==DOT) || ((op) == BIT))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define isPastOp(op)       ((op)==OP_PREC)  || ((op)==OP_NOTPRECNOT) ||    \
                           ((op)==OP_ONCE)  || ((op)==OP_HISTORICAL) ||    \
                           ((op)==SINCE)    || ((op)==TRIGGERED)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define isBinaryOp(op)     ((op)==AND)      || ((op)==OR)            ||    \
                           ((op)==IFF)      || ((op)==UNTIL)         ||    \
                           ((op)==SINCE)    || ((op)==RELEASES)      ||    \
                           ((op)==TRIGGERED)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define getOpClass(op) \
  ((op)==TRUEEXP)       || ((op)==FALSEEXP)      ? CONSTANT_EXPR           \
  :                                                                        \
  ((op)==DOT) || ((op) == BIT) || ((op)==NOT)    ? LITERAL                 \
  :                                                                        \
  ((op)==AND)           || ((op)==OR)        ||                            \
  ((op)==IFF)                                    ? PROP_CONNECTIVE         \
  :                                                                        \
  ((op)==OP_PREC)       || ((op)==OP_NEXT)   ||                            \
  ((op)==OP_NOTPRECNOT) ||                                                 \
  ((op)==OP_ONCE)       || ((op)==OP_FUTURE) ||                            \
  ((op)==OP_HISTORICAL) || ((op)==OP_GLOBAL) ||                            \
  ((op)==SINCE)         || ((op)==UNTIL)     ||                            \
  ((op)==TRIGGERED)     || ((op)==RELEASES)      ? TIME_OPERATOR           \
  :                                                                        \
                                                   UNKNOWN_OP

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Given a problem, and a solver containing a model for that
               problem, generates and prints a counter-example

  A trace is generated and printed using the currently
               selected plugin. Generated trace is returned, in order
               to make possible for the caller to do some other
               operation, like association with the checked
               property. Returned trace object *cannot* be destroyed
               by the caller.

  \sa Bmc_Utils_generate_cntexample Bmc_Utils_fill_cntexample
*/
Trace_ptr
Bmc_Utils_generate_and_print_cntexample(BeEnc_ptr be_enc,
                                        TraceMgr_ptr tm,
                                        SatSolver_ptr solver,
                                        be_ptr be_prob,
                                        const int k,
                                        const char* trace_name,
                                        NodeList_ptr symbols);

/*!
  \brief Given a problem, and a solver containing a model for that
              problem, generates a counter-example

  Generated trace is returned, in order to make possible
               for the caller to do some other operation, like
               association with the checked property. Returned trace
               is non-volatile

  \sa Bmc_Utils_generate_and_print_cntexample
*/
Trace_ptr
Bmc_Utils_generate_cntexample(BeEnc_ptr be_enc,
                              SatSolver_ptr solver,
                              be_ptr be_prob,
                              const int k,
                              const char* trace_name,
                              NodeList_ptr symbols);

/*!
  \brief Given a solver containing a model for a
              problem, fills the given counter-example correspondingly

  The filled trace is returned. The given trace must be empty

  \sa Bmc_fill_trace_from_cnf_model Bmc_Utils_generate_cntexample
*/
Trace_ptr
Bmc_Utils_fill_cntexample(BeEnc_ptr be_enc,
                          SatSolver_ptr solver,
                          const int k, Trace_ptr trace);

/*!
  \brief Returns true if l has the internally encoded "no loop"
               value

  This is supplied in order to hide the internal value of
               loopback which corresponds to the "no loop" semantic.
*/
boolean Bmc_Utils_IsNoLoopback(const int l);

/*!
  \brief Returns true if the given string represents the no
               loopback value

  This is supplied in order to hide the internal value of
               loopback which corresponds to the "no loop" semantic.
*/
boolean Bmc_Utils_IsNoLoopbackString(const char* str);

/*!
  \brief Returns true if the given loop value represents a single
               (relative or absolute) loopback

  Both cases "no loop" and "all loops" make this function
               returning false, since these values are not single
               loops.
*/
boolean Bmc_Utils_IsSingleLoopback(const int l);

/*!
  \brief Returns true if the given loop value represents the "all
               possible loopbacks" semantic

  This is supplied in order to hide the internal value of
               loopback which corresponds to the "all loops"
               semantic.
*/
boolean Bmc_Utils_IsAllLoopbacks(const int l);

/*!
  \brief Returns true if the given string represents the "all
               possible loops" value.

  This is supplied in order to hide the internal value of
               loopback which corresponds to the "all loops"
               semantic.
*/
boolean Bmc_Utils_IsAllLoopbacksString(const char* str);

/*!
  \brief Returns the integer value which represents the "no loop"
               semantic


*/
int Bmc_Utils_GetNoLoopback(void);

/*!
  \brief Returns the integer value which represents the "all loops"
               semantic


*/
int Bmc_Utils_GetAllLoopbacks(void);

/*!
  \brief Returns a constant string which represents the "all loops"
               semantic.


*/
const char* Bmc_Utils_GetAllLoopbacksString(void);

/*!
  \brief Converts a relative loop value (wich can also be an
               absolute loop value) to an absolute loop value

  For example the -4 value when k is 10 is the value 6,
               but the value 4 (absolute loop value) is still 4
*/
int Bmc_Utils_RelLoop2AbsLoop(const int loop, const int k);

/*!
  \brief Checks the (k,l) couple. l must be absolute.

  Returns OUTCOME_SUCCESS if k and l are compatible, otherwise
               return OUTCOME_GENERIC_ERROR
*/
Outcome Bmc_Utils_Check_k_l(const int k, const int l);

/*!
  \brief Given time<=k and a \[l, k\] interval, returns next time,
               or BMC_NO_LOOP if time is equal to k and there is no
               loop


*/
int Bmc_Utils_GetSuccTime(const int time, const int k, const int l);

/*!
  \brief Given a string representing a loopback possible value,
               returns the corresponding integer.  The (optional)
               parameter result will be assigned to OUTCOME_SUCCESS if the
               conversion has been successfully performed, otherwise
               to OUTCOME_GENERIC_ERROR is the conversion failed. If result is
               NULL, OUTCOME_SUCCESS is the aspected value, and an assertion
               is implicitly performed to check the conversion
               outcome.

  Use this function to correctly convert a string
               containing a loopback user-side value to the internal
               representation of the same loopback value

  \se result will change if supplied
*/
int Bmc_Utils_ConvertLoopFromString(const char* strValue, Outcome* result);

/*!
  \brief Given an integer containing the inner representation of
               the loopback value, returns as parameter the
               corresponding user-side value as string

  Inverse semantic of
               Bmc_Utils_ConvertLoopFromString. bufsize is the maximum
               buffer size

  \se String buffer passed as argument will change

  \sa Bmc_Utils_ConvertLoopFromString
*/
void Bmc_Utils_ConvertLoopFromInteger(const int iLoopback, char* szLoopback, const int _bufsize);

/*!
  \brief Search into a given string any symbol which belongs to a
               determined set of symbols, and expand each found
               symbol, finally returning the resulting string

  This function is used in order to perform the macro
               expansion of filenames. table_ptr is the pointer to a
               previously prepared table which fixes any
               corrispondence from symbol to strings to be
               substituited from.  table_len is the number of rows in
               the table (i.e. the number of symbols to search for.)

  \se filename_expanded string data will change
*/
void
Bmc_Utils_ExpandMacrosInFilename(const char* filename_to_be_expanded,
      const SubstString* table_ptr,
      const size_t table_len,
      char* filename_expanded, size_t buf_len);

/*!
  \brief Applies inlining taking into account of current user
               settings


*/
be_ptr
Bmc_Utils_apply_inlining(Be_Manager_ptr be_mgr, be_ptr f);

/*!
  \brief Applies inlining forcing inclusion of the conjunct
               set. Useful in the incremental SAT applications to
               guarantee soundness


*/
be_ptr
Bmc_Utils_apply_inlining4inc(Be_Manager_ptr be_mgr, be_ptr f);

/*!
  \brief Reads a simple expression and builds the corresponding BE
               formula.

  Reads a simple expression and builds the corresponding
               BE formula. Exceptions are raised if the expression
               cannot be parsed or has type errors.

  \se None

  \sa Bmc_Utils_next_costraint_from_string
*/
be_ptr
Bmc_Utils_simple_costraint_from_string(BeEnc_ptr be_enc,
                                       BddEnc_ptr bdd_enc,
                                       const char* str,
                                       Expr_ptr* node_expr);

/*!
  \brief Reads a next expression and builds the corresponding BE
               formula.

  Reads a next expression and builds the corresponding BE
               formula. Exceptions are raised if the expression cannot
               be parsed or has type errors. If node_expr is not NULL,
               it will be set to the parsed expression.

  \se None

  \sa Bmc_Utils_simple_costraint_from_string
*/
be_ptr
Bmc_Utils_next_costraint_from_string(BeEnc_ptr be_enc,
                                     BddEnc_ptr bdd_enc,
                                     const char* str,
                                     Expr_ptr* node_expr);

/*!
  \brief Converts Be into CNF, and adds it into a group of a
                 incremental solver, sets polarity to 1, and then destroys
                 the CNF.

  Outputs into outstream the total time of conversion,
                 adding, setting polarity and destroying BE.
*/
void
Bmc_Utils_add_be_into_inc_solver_positively(SatIncSolver_ptr solver,
                                            SatSolverGroup group,
                                            be_ptr prob,
                                            BeEnc_ptr be_enc,
                                            Be_CnfAlgorithm cnf_alg);

/*!
  \brief Converts Be into CNF, and adds it into a group of a
                 non-incremental solver, sets polarity to 1, and
                 then destroys the CNF.

  Outputs into outstream the total time of conversion,
                 adding, setting polarity and destroying BE.
*/
void
Bmc_Utils_add_be_into_non_inc_solver_positively(SatSolver_ptr solver,
                                                be_ptr prob,
                                                BeEnc_ptr be_enc,
                                                Be_CnfAlgorithm cnf_alg);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_UTILS_H__ */
