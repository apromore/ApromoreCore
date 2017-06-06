/* ---------------------------------------------------------------------------


  This file is part of the ``be'' package of NuSMV version 2.
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
  \brief The header file for the <tt>be</tt> package

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BE_BE_H__
#define __NUSMV_CORE_BE_BE_H__

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief Be_Manager is a generic manager required when you must operate
  on Boolean Expressions

  Any instance of Be_Manager can only be accessed via
  Be_Manager_ptr
*/

typedef struct Be_Manager_TAG* Be_Manager_ptr; /* generic be manger */


/*!
  \brief A Boolean Expression represented in Conjunctive Normal Form

  Special case -- A CONSTANT: If the formula is a constant,
  Be_Cnf_GetFormulaLiteral() will be INT_MAX,
  if formula is true then:  GetClausesList() will be empty list.
  if formula is false then:  GetClausesList()  will contain a single
  empty clause.
*/

typedef struct Be_Cnf_TAG* Be_Cnf_ptr; /* cnf representation */


/*!
  \brief The Boolean Expression type


*/


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void* be_ptr;


/*!
  \brief Specific to generic BE conversion gateway type

  This is the function type for the Be_Manager gateway that
  provides conversion functionality from specific boolean expression types
  to generic Boolean Expression type (for example from rbc to be_ptr).
*/


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef be_ptr (*Be_Spec2Be_fun)(Be_Manager_ptr self, void* spec_be);


/*!
  \brief Generic to specific BE conversion gateway type

  This is the function type for the Be_Manager gateway that
  provides conversion functionality from generic boolean expression types
  to specific Boolean Expression type (for example from be_ptr to rbc).
*/

typedef void*  (*Be_Be2Spec_fun)(Be_Manager_ptr self, be_ptr be);


/*!
  \brief BE equivalent of RBC CNF conversion algorithm.

  \sa Rbc_2CnfAlgorithm
*/
#include "nusmv/core/rbc/rbc.h"
typedef Rbc_2CnfAlgorithm Be_CnfAlgorithm;


#include <limits.h>
/* ================================================== */
/* Put here any specific boolean expression manager
   interface header: */
#include "nusmv/core/be/beRbcManager.h"
/* ================================================== */

#include "nusmv/core/utils/Slist.h"
#include "nusmv/core/cinit/NuSMVEnv.h"


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief Represents an invalid value possibly used when
  substituting.

  If during substitution this value is found, an
  error is raised. This is used for runtime checking of those
  expressions that are not expected to contain variables that
  cannot be substituted.
*/
#define BE_INVALID_SUBST_VALUE RBC_INVALID_SUBST_VALUE

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* ================================================== */
/* Package constructor/destructor: */

/*!
  \brief Initializes the module

  Call before any other function contained in this module

  \se Any module structure is allocated and initialized if required

  \sa Be_Quit
*/
void Be_Init(void);

/*!
  \brief De-initializes the module

  Call as soon as you finished to use this module services

  \se Any module structure is deleted if required

  \sa Be_Init
*/
void Be_Quit(void);
/* ================================================== */


/* ================================================== */
/* Be_Manager public interface: */

/*!
  \methodof Be_Manager
  \brief Private service of MSatEnc_term_to_expr

  Private service of MSatEnc_term_to_expr
*/
NuSMVEnv_ptr Be_Manager_GetEnvironment(const Be_Manager_ptr self);

/*!
  \methodof Be_Manager
  \brief Converts a specific-format boolean expression
  (for example in rbc format) into a generic BE

  Calls self->spec2be_converter in order to implement the
  polymorphism mechanism

  \se Calls self->be2spec_converter in order to implement the
  polymorphism mechanism

  \sa Be_Manager_Be2Spec
*/
be_ptr Be_Manager_Spec2Be(const Be_Manager_ptr self,
                                  void* spec_expr);

/*!
  \methodof Be_Manager
  \brief Converts a generic BE into a specific-format boolean expression
  (for example in rbc format)



  \sa Be_Manager_Spec2Be
*/
void* Be_Manager_Be2Spec(const Be_Manager_ptr self, be_ptr be);

/*!
  \methodof Be_Manager
  \brief Gets the specific manager under the be manager

  Gets the specific manager under the be manager
*/
void* Be_Manager_GetSpecManager(Be_Manager_ptr self);
/* ================================================== */


/* ==================================================  */
/* Be_Cnf class constructor, destructor and modifiers */

/*!
  \methodof Be_Cnf
  \brief Constructor for the Be_Cnf structure

  When the returned pointer is no longer used,
  call Be_Cnf_Delete

  \sa Be_Cnf_Delete
*/
Be_Cnf_ptr Be_Cnf_Create(const be_ptr be);

/*!
  \methodof Be_Cnf
  \brief Be_Cnf structure destructor



  \sa Be_Cnf_Create
*/
void Be_Cnf_Delete(Be_Cnf_ptr self);

/*!
  \methodof Be_Cnf
  \brief Removes any duplicate literal appearing in single clauses

  Removes any duplicate literal appearing in single clauses
*/
void Be_Cnf_RemoveDuplicateLiterals(Be_Cnf_ptr self);
/* ==================================================  */

/* ================================================== */
/* Be_Cnf class access members: (for special case,a constant,see Be_Cnf_ptr)*/

/*!
  \methodof Be_Cnf
  \brief Returns the original BE problem this CNF was created from


*/
be_ptr Be_Cnf_GetOriginalProblem(const Be_Cnf_ptr self);

/*!
  \methodof Be_Cnf
  \brief Returns the literal assigned to the whole formula.
  It may be negative. If the formula is a constant unspecified value is returned


*/
int Be_Cnf_GetFormulaLiteral(const Be_Cnf_ptr self);

/*!
  \methodof Be_Cnf
  \brief Returns the independent variables list in the CNF
  representation


*/
Slist_ptr Be_Cnf_GetVarsList(const Be_Cnf_ptr self);

/*!
  \methodof Be_Cnf
  \brief Returns a list of lists which contains the CNF-ed formula

  Each list in the list is a set of integers which
  represents a single clause. Any integer value depends on the variable
  name and the time which the variasble is considered in, whereas the
  integer sign is the variable polarity in the CNF-ed representation.
*/
Slist_ptr Be_Cnf_GetClausesList(const Be_Cnf_ptr self);

/*!
  \methodof Be_Cnf
  \brief Returns the maximum variable index in the list of clauses


*/
int Be_Cnf_GetMaxVarIndex(const Be_Cnf_ptr self);

/*!
  \methodof Be_Cnf
  \brief Returns the number of independent variables in the given
  Be_Cnf structure


*/
size_t Be_Cnf_GetVarsNumber(const Be_Cnf_ptr self);

/*!
  \methodof Be_Cnf
  \brief Returns the number of clauses in the given Be_Cnf structure


*/
size_t Be_Cnf_GetClausesNumber(const Be_Cnf_ptr self);

/*!
  \methodof Be_Cnf
  \brief Sets the literal assigned to the whole formula


*/
void Be_Cnf_SetFormulaLiteral(const Be_Cnf_ptr self,
                                         const int formula_literal);

/*!
  \methodof Be_Cnf
  \brief Sets the maximum variable index value


*/
void Be_Cnf_SetMaxVarIndex(const Be_Cnf_ptr self,
                                      const int max_idx);
/* ================================================== */


/* ================================================== */
/* BE logical operations interface: */

/*!
  \brief Returns true if the given be is the true value,
  otherwise returns false


*/
boolean Be_IsTrue(Be_Manager_ptr manager, be_ptr arg);

/*!
  \brief Returns true if the given be is the false value,
  otherwise returns false


*/
boolean Be_IsFalse(Be_Manager_ptr manager, be_ptr arg);

/*!
  \brief Returns true if the given be is a constant value,
  such as either False or True


*/
boolean Be_IsConstant(Be_Manager_ptr manager, be_ptr arg);

/*!
  \brief Builds a 'true' constant value


*/
be_ptr  Be_Truth(Be_Manager_ptr manager);

/*!
  \brief Builds a 'false' constant value


*/
be_ptr  Be_Falsity(Be_Manager_ptr manager);

/*!
  \brief Negates its argument


*/
be_ptr  Be_Not(Be_Manager_ptr manager, be_ptr arg);

/*!
  \brief Builds a new be which is the conjunction between
  its two arguments


*/
be_ptr
Be_And(Be_Manager_ptr manager, be_ptr arg1, be_ptr arg2);

/*!
  \brief Builds a new be which is the disjunction of
  its two arguments


*/
be_ptr
Be_Or(Be_Manager_ptr manager, be_ptr arg1, be_ptr arg2);

/*!
  \brief Builds a new be which is the exclusive-disjunction
  of its two arguments


*/
be_ptr
Be_Xor(Be_Manager_ptr manager, be_ptr arg1, be_ptr arg2);

/*!
  \brief Builds a new be which is the implication between
  its two arguments


*/
be_ptr
Be_Implies(Be_Manager_ptr manager, be_ptr arg1, be_ptr arg2);

/*!
  \brief Builds a new be which is the logical equivalence
  between its two arguments


*/
be_ptr
Be_Iff(Be_Manager_ptr manager, be_ptr arg1, be_ptr arg2);

/*!
  \brief Builds an if-then-else operation be



  \se ...
*/
be_ptr
Be_Ite(Be_Manager_ptr manager, be_ptr arg_if,
       be_ptr arg_then, be_ptr arg_else);

/*!
  \brief Creates a fresh copy G(X') of the be F(X) by shifting
  each variable index of a given amount

  Shifting operation replaces each occurence of the
               variable x_i in `f' with the variable x_(i + shift).  A
               simple lazy mechanism is implemented to optimize that
               cases which given expression is a constant in.

               The two indices arrays log2phy and phy2log map
               respectively the logical level to the physical level,
               and the physical level to the logical levels. They
               allow the be encoder to freely organize the variables
               into a logical and a physical level. This feature has
               been introduced with NuSMV-2.4 that ships dynamic
               encodings.

               !!!! WARNING !!!!
                 Since version 2.4 memoizing has been moved to BeEnc,
                 as there is no way of calculating a good hashing key
                 as the time would be requested, but timing
                 information are not available at this stage.

*/
be_ptr
Be_LogicalShiftVar(Be_Manager_ptr manager, be_ptr f,
                   int shift,
                   const int* log2phy,
                   const int* phy2log);

/*!
  \brief Replaces all variables in f with other variables, taking
               them at logical level

  Replaces every occurence of the variable x_i in in `f'
               with the variable x_j provided that subst[i] = j.

               Notice that in this context, 'i' and 'j' are LOGICAL
               indices, not physical, i.e. the substitution array is
               provided in terms of logical indices, and is related
               only to the logical level.

               For a substitution at physical level, see Be_VarSubst.

               There is no need for `subst' to contain all the
               variables, but it should map at least the variables in
               `f' in order for the substitution to work properly.

               The two indices arrays log2phy and phy2log map
               respectively the logical level to the physical level,
               and the physical level to the logical levels. They
               allow the be encoder to freely organize the variables
               into a logical and a physical level. This feature has
               been introduced with NuSMV-2.3 that ships dynamic
               encodings.
*/
be_ptr
Be_LogicalVarSubst(Be_Manager_ptr manager, be_ptr f,
                   int* subst,
                   const int* log2phy,
                   const int* phy2log);


/* ================================================== */


/* ================================================== */
/* Utilities interface: */

/*!
  \brief Converts the given be into the corresponding CNF-ed be

  Since it creates a new Be_Cnf structure, the caller
  is responsible for deleting it when it is no longer used
  (via Be_Cnf_Delete).

  'alg' can be one value in
  (RBC_TSEITIN_CONVERSION, RBC_SHERIDAN_CONVERSION)
  Option "rbc_rbc2cnf_algorithm" (RBC_CNF_ALGORITHM) holds the user
  preferred value.

  'polarity' is used to determine if the clauses generated should
   represent the RBC positively, negatively, or both (1, -1 or 0
   respectively). For an RBC that is known to be true, the clauses
   that represent it being false are not needed (they would be removed
   anyway by propogating the unit literal which states that the RBC is
   true). Similarly for when the RBC is known to be false. This
   parameter is only used with the compact cnf conversion algorithm,
   and is ignored if the simple algorithm is used.

  \sa Be_Cnf_Delete
*/
Be_Cnf_ptr
Be_ConvertToCnf(Be_Manager_ptr manager, be_ptr f, int polarity,
                Be_CnfAlgorithm alg);

/*!
  \brief Converts a CNF literal into a BE literal

  The function returns 0 if there is no BE index
  associated with the given CNF index.  A given CNF literal should be
  created by given BE manager (through Be_ConvertToCnf).

  \sa Be_ConvertToCnf
*/
int Be_CnfLiteral2BeLiteral(const Be_Manager_ptr self,
                                   int cnfLiteral);

/*!
  \brief Converts a BE literal into a CNF literal (sign is taken into
  account)



  \sa Be_ConvertToCnf
*/
int Be_BeLiteral2CnfLiteral(const Be_Manager_ptr self,
                                   int beLiteral);

/*!
  \brief Converts a BE literal into a CNF literal



  \sa Be_ConvertToCnf
*/
int Be_BeLiteral2BeIndex(const Be_Manager_ptr self,
                                int beLiteral);

/*!
  \brief Converts a BE index into a BE literal (always positive)



  \sa Be_ConvertToCnf
*/
int Be_BeIndex2BeLiteral(const Be_Manager_ptr self,
                                int beIndex);

/*!
  \brief Returns a CNF literal (always positive) associated with a
  given BE index

  If no CNF index is associated with a given BE index, 0
  is returned. BE indexes are associated with CNF indexes through
  function Be_ConvertToCnf.

  \sa Be_ConvertToCnf
*/
int Be_BeIndex2CnfLiteral(const Be_Manager_ptr self,
                                 int beIndex);

/*!
  \brief Converts the given CNF model into BE model

  Since it creates a new lsit , the caller
  is responsible for deleting it when it is no longer used
  (via lsDestroy)
*/
Slist_ptr Be_CnfModelToBeModel(Be_Manager_ptr manager,
                                      const Slist_ptr cnfModel);

/*!
  \brief Dumps the given be into a file with Davinci format


*/
void
Be_DumpDavinci(Be_Manager_ptr manager, be_ptr f, FILE* outFile);

/*!
  \brief Dumps the given be into a file with Davinci format


*/
void
Be_DumpGdl(Be_Manager_ptr manager, be_ptr f, FILE* outFile);

/*!
  \brief Dumps the given be into a file


*/
void
Be_DumpSexpr(Be_Manager_ptr manager, be_ptr f, FILE* outFile);

/* index<->be conversion layer: */

/*!
  \brief Converts the given variable index into the corresponding be

  If corresponding index had not been previously
  allocated, it will be allocated. If corresponding node does not
  exist in the dag, it will be inserted.
*/
be_ptr Be_Index2Var(Be_Manager_ptr manager, int varIndex);

/*!
  \brief Converts the given variable (as boolean expression) into
  the corresponding index


*/
int Be_Var2Index(Be_Manager_ptr manager, be_ptr var);

/* miscellaneous */

/*!
  \brief Returns true iff sign of literal is positive.



  \sa Be_CnfLiteral_Negate, Be_BeLiteral_IsSignPositive
*/
boolean Be_CnfLiteral_IsSignPositive(const Be_Manager_ptr self,
                                            int cnfLiteral);

/*!
  \brief Returns negated literal.



  \sa Be_CnfLiteral_IsSignPositive, Be_BeLiteral_Negate
*/
int Be_CnfLiteral_Negate(const Be_Manager_ptr self,
                                int cnfLiteral);

/*!
  \brief Returns true iff sign of literal is positive.



  \sa Be_BeLiteral_Negate, Be_CnfLiteral_IsSignPositive
*/
boolean Be_BeLiteral_IsSignPositive(const Be_Manager_ptr self,
                                           int beLiteral);

/*!
  \brief Returns negated literal.



  \sa Be_BeLiteral_IsSignPositive, Be_CnfLiteral_Negate
*/
int Be_BeLiteral_Negate(const Be_Manager_ptr self,
                               int beLiteral);

/*!
  \brief Performs the inlining of f, either including or not
  the conjuction set.

  If add_conj is true, the conjuction set is included, otherwise
        only the inlined formula is returned for a lazy SAT solving.

  \sa InlineResult
*/
be_ptr
Be_apply_inlining(Be_Manager_ptr self, be_ptr f, boolean add_conj);

/*!
  \methodof Be_Cnf
  \brief Print out some statistics

  Print out, in this order: the clause number, the var number, the
               highest variable index, the average clause size, the highest
               clause size

  \se "outFile" is written
*/
void
Be_Cnf_PrintStat(const Be_Cnf_ptr self, FILE* outFile, char* prefix);

/* ================================================== */

#endif /* __NUSMV_CORE_BE_BE_H__ */
