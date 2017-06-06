/* ---------------------------------------------------------------------------


  This file is part of the ``rbc'' package of NuSMV version 2.
  Copyright (C) 2000-2001 by University of Genova.

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
  \author Armando Tacchella, Marco Roveri
  \brief Formula handling with Reduced Boolean Circuits (RBCs).

  External functions and data structures of the rbc package.

*/

#ifndef __NUSMV_CORE_RBC_RBC_H__
#define __NUSMV_CORE_RBC_RBC_H__


#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

/* Standard includes. */
#if NUSMV_HAVE_MALLOC_H
# if NUSMV_HAVE_SYS_TYPES_H
#  include <sys/types.h>
# endif
# include <malloc.h>
#elif defined(NUSMV_HAVE_SYS_MALLOC_H) && NUSMV_HAVE_SYS_MALLOC_H
# if NUSMV_HAVE_SYS_TYPES_H
#  include <sys/types.h>
# endif
# include <sys/malloc.h>
#elif NUSMV_HAVE_STDLIB_H
# include <stdlib.h>
#endif

#include <stdio.h>

/* Submodule includes. */
#include "nusmv/core/dag/dag.h"
#include "nusmv/core/utils/Slist.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBC_TSEITIN_CONVERSION_NAME  "tseitin"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBC_SHERIDAN_CONVERSION_NAME "sheridan"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBC_INVALID_CONVERSION_NAME  "invalid"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief RBC CNF conversion algorithm.

  RBC CNF conversion algorithm.
*/

typedef enum _Rbc_2CnfAlgorithm {
  RBC_INVALID_CONVERSION = 0,
  RBC_TSEITIN_CONVERSION,
  RBC_SHERIDAN_CONVERSION
} Rbc_2CnfAlgorithm;

/*!
  \brief RBC boolean values.

  RBC boolean values.
*/

typedef enum Rbc_Bool {
  RBC_FALSE = DAG_ANNOTATION_BIT,
  RBC_TRUE = 0
} Rbc_Bool_c;

/*!
  \struct RbcManager
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct RbcManager Rbc_Manager_t;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef Dag_Vertex_t      Rbc_t;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef Dag_DfsFunctions_t RbcDfsFunctions_t;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*Rbc_ProcPtr_t)(void);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef int (*Rbc_IntPtr_t)(void);

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                     */
/*---------------------------------------------------------------------------*/
struct RbcDfsFunctions {
  Rbc_IntPtr_t   Set;
  Rbc_ProcPtr_t  FirstVisit;
  Rbc_ProcPtr_t  BackVisit;
  Rbc_ProcPtr_t  LastVisit;
};

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
#define RBC_INVALID_SUBST_VALUE \
   INT_MAX


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Package initialization


*/
void Rbc_pkg_init(void);

/*!
  \brief Package deinitialization


*/
void Rbc_pkg_quit(void);

/*!
  \brief Translates the rbc into the corresponding (equisatisfiable)
               set of clauses.

  This calls the user's choice of translation procedure

  \se `clauses' and `vars' are filled up. `clauses' is the empty list
  if `f' was true, and contains a single empty clause if `f' was
  false. 'polarity' is used to determine if the clauses generated
  should represent the RBC positively, negatively, or both (1, -1 or 0
  respectively). For an RBC that is known to be true, the clauses that
  represent it being false are not needed (they would be removed
  anyway by propogating the unit literal which states that the RBC is
  true). Similarly for when the RBC is known to be false. This
  parameter is only used with the compact cnf conversion algorithm,
  and is ignored if the simple algorithm is used.
*/
int Rbc_Convert2Cnf(Rbc_Manager_t* rbcManager, Rbc_t* f,
                    int polarity, Rbc_2CnfAlgorithm alg,
                    /* outputs: */
                    Slist_ptr clauses, Slist_ptr vars,
                    int* literalAssignedToWholeFormula);

/*!
  \brief Returns the RBC index corresponding to a particular CNF var

  Returns -1, if there is no original RBC variable
  corresponding to CNF variable, this may be the case if CNF variable
  corresponds to an internal node (not leaf) of RBC tree. Input CNF
  variable should be a correct variable generated by RBC manager.
*/
int Rbc_CnfVar2RbcIndex(Rbc_Manager_t* rbcManager, int cnfVar);

/*!
  \brief Returns the associated CNF variable of a given RBC index

  Returns 0, if there is no original RBC variable
  corresponding to CNF variable. This may be the case if particular RBC
  node (of the given variable) has never been converted into CNF
*/
int Rbc_RbcIndex2CnfVar(Rbc_Manager_t* rbcManager, int rbcIndex);

/*!
  \brief Logical constant 1 (truth).

  Returns the rbc that stands for logical truth.

  \se none
*/
Rbc_t* Rbc_GetOne(Rbc_Manager_t* rbcManager);

/*!
  \brief Logical constant 0 (falsity).

  Returns the rbc that stands for logical falsity.

  \se none
*/
Rbc_t* Rbc_GetZero(Rbc_Manager_t* rbcManager);

/*!
  \brief Returns true if the given rbc is a constant value,
                      such as either False or True


*/
boolean Rbc_IsConstant(Rbc_Manager_t* manager, Rbc_t* f);

/*!
  \brief Returns a variable.

  Returns a pointer to an rbc node containing the requested
               variable. Works in three steps:
               <ul>
               <li> the requested variable index exceeds the current capacity:
                    allocated more room up to the requested index;
               <li> the variable node does not exists: inserts it in the dag
                    and makes it permanent;
               <li> returns the variable node.
               </ul>

  \se none
*/
Rbc_t* Rbc_GetIthVar(Rbc_Manager_t* rbcManager, int varIndex);

/*!
  \brief Returns the complement of an rbc.

  Returns the complement of an rbc.

  \se none
*/
Rbc_t* Rbc_MakeNot(Rbc_Manager_t* rbcManager, Rbc_t* left);

/*!
  \brief Makes the conjunction of two rbcs.

  Makes the conjunction of two rbcs.
               Works in three steps:
               <ul>
               <li> performs boolean simplification: if successfull, returns
                    the result of the simplification;
               <li> orders left and right son pointers;
               <li> looks up the formula in the dag and returns it.
               </ul>

               If RBC_ENABLE_LOCAL_MINIMIZATION_WITHOUT_BLOWUP is defined,
               applies all the rules proposed in "R. Brummayer and
               A. Biere. Local Two-Level And-Inverter Graph Minimization
               without Blowup". In Proc. MEMICS 2006.  The expressions o1, o2,
               o3, o4 refers to the four level of optimization proposed in the
               paper.  The rules are implemented as macros in order to avoid
               repetitions

  \se none
*/
Rbc_t* Rbc_MakeAnd(Rbc_Manager_t* rbcManager, Rbc_t* left, Rbc_t* right, Rbc_Bool_c sign);

/*!
  \brief Makes the disjunction of two rbcs.

  Makes the disjunction of two rbcs: casts the connective to
               the negation of a conjunction using De Morgan's law.

  \se none
*/
Rbc_t* Rbc_MakeOr(Rbc_Manager_t* rbcManager, Rbc_t* left, Rbc_t* right, Rbc_Bool_c sign);

/*!
  \brief Makes the coimplication of two rbcs.

  Makes the coimplication of two rbcs.
               Works in four steps:
               <ul>
               <li> performs boolean simplification: if successfull, returns
                    the result of the simplification;
               <li> orders left and right son pointers;
               <li> re-encodes the negation
               <li> looks up the formula in the dag and returns it.

               <li> If the coimplication mode is disable, expands the connective
                    in three AND nodes.
               </ul>

  \se none
*/
Rbc_t* Rbc_MakeIff(Rbc_Manager_t* rbcManager, Rbc_t* left, Rbc_t* right, Rbc_Bool_c sign);

/*!
  \brief Makes the exclusive disjunction of two rbcs.

  Makes the exclusive disjunction of two rbcs: casts the
               connective as the negation of a coimplication.

  \se none
*/
Rbc_t* Rbc_MakeXor(Rbc_Manager_t* rbcManager, Rbc_t* left, Rbc_t* right, Rbc_Bool_c sign);

/*!
  \brief Makes the if-then-else of three rbcs.

  Makes the if-then-else of three rbcs: expands the connective
              into the corresponding product-of-sums.

              If the if-then-else mode is disable, expands the connective in
              three AND nodes

  \se none
*/
Rbc_t* Rbc_MakeIte(Rbc_Manager_t* rbcManager, Rbc_t* c, Rbc_t* t, Rbc_t* e,
Rbc_Bool_c sign);

/*!
  \brief Gets the left operand.

  Gets the left operand.

  \se none
*/
Rbc_t* Rbc_GetLeftOpnd(Rbc_t* f);

/*!
  \brief Gets the right operand.

  Gets the right operand.

  \se none
*/
Rbc_t* Rbc_GetRightOpnd(Rbc_t* f);

/*!
  \brief Gets the variable index.

  Returns the variable index,
               -1 if the rbc is not a variable.

  \se none
*/
int Rbc_GetVarIndex(Rbc_t* f);

/*!
  \brief Makes a node permanent.

  Marks the vertex in the internal dag. This saves the rbc
               from being wiped out during garbage collection.

  \se none
*/
void Rbc_Mark(Rbc_Manager_t* rbc, Rbc_t* f);

/*!
  \brief Makes a node volatile.

  Unmarks the vertex in the internal dag. This exposes the rbc
               to garbage collection.

  \se none
*/
void Rbc_Unmark(Rbc_Manager_t* rbc, Rbc_t* f);

/*!
  \brief Creates a new RBC manager.

  Creates a new RBC manager:
               <ul>
               <li> <i>varCapacity</i> how big is the variable index
                    (this number must be strictly greater than 0)
               </ul>
               Returns the allocated manager if varCapacity is greater than 0,
               and NIL(Rbc_Manager_t) otherwise.

  \se none

  \sa Rbc_ManagerFree
*/
Rbc_Manager_t* Rbc_ManagerAlloc(const NuSMVEnv_ptr env, int varCapacity);

/*!
  \brief Reserves more space for new variables.

  If the requested space is bigger than the current one
               makes room for more variables in the varTable.

  \se none
*/
void Rbc_ManagerReserve(Rbc_Manager_t* rbcManager, int newVarCapacity);

/*!
  \brief Resets RBC manager



  \se none
*/
void Rbc_ManagerReset(Rbc_Manager_t* rbcManager);

/*!
  \brief Returns the current variable capacity of the rbc.

  This number is the maximum number of variables (starting from 0)
               that can be requested without causing any memory allocation.

  \se none
*/
int Rbc_ManagerCapacity(Rbc_Manager_t* rbcManager);

/*!
  \brief Deallocates an RBC manager.

  Frees the variable index and the internal dag manager.

  \se none
*/
void Rbc_ManagerFree(Rbc_Manager_t* rbcManager);

/*!
  \brief Garbage collection in the RBC manager.

  Relies on the internal DAG garbage collector.

  \se None
*/
void Rbc_ManagerGC(Rbc_Manager_t* rbcManager);

/*!
  \brief Returns the environment instance


*/
NuSMVEnv_ptr Rbc_ManagerGetEnvironment(Rbc_Manager_t* rbcManager);

/*!
  \brief Print out an rbc using DaVinci graph format.

  Print out an rbc using DaVinci graph format.

  \se None
*/
void
Rbc_OutputDaVinci(Rbc_Manager_t* rbcManager, Rbc_t* f, FILE* outFile);

/*!
  \brief Print out an rbc using LISP S-expressions.

  Print out an rbc using LISP S-exrpressions.

  \se None
*/
void
Rbc_OutputSexpr(Rbc_Manager_t* rbcManager, Rbc_t* f, FILE* outFile);

/*!
  \brief Print out an rbc using Gdl graph format.

  Print out an rbc using Gdl graph format.

  \se None
*/
void
Rbc_OutputGdl(Rbc_Manager_t* rbcManager, Rbc_t* f, FILE* outFile);

/*!
  \brief Creates a fresh copy G(Y) of the rbc F(X) such
               that G(Y) = F(X)[Y/X] where X and Y are vectors of
               variables.

  Given `rbcManager', the rbc `f', and the array of
               integers `subst', replaces every occurence of the
               variable x_i in in `f' with the variable x_j
               provided that subst[i] = j. There is no need for
               `subst' to contain all the variables, but it should
               map at least the variables in `f' in order for the
               substitution to work properly.

               Here the substitution is performed completely at
               physical level (i.e. at the level of pure rbc
               indices). For a substitution at logical level, see
               Rbc_LogicalSubst.

  !!!!!! WARNING   WARNING   WARNING   WARNING   WARNING   WARNING !!!!!
  !!                                                                  !!
  !!  This function cannot be used with the new encoding BeEnc. As    !!
  !!  substitution involves the traversal of the logical layer within !!
  !!  the BeEnc, simple shifting is no longer usable, and will        !!
  !!  produce unpredictable results if used on variables handled by   !!
  !!  a BeEnc instance.                                               !!
  !!                                                                  !!
  !!  Use Rbc_LogicalSubst instead.                                   !!
  !!                                                                  !!
  !!!!!! WARNING   WARNING   WARNING   WARNING   WARNING   WARNING !!!!!



  \se none

  \sa Rbc_LogicalSubst
*/
Rbc_t*
Rbc_Subst(Rbc_Manager_t* rbcManager, Rbc_t* f, int* subst);

/*!
  \brief Creates a fresh copy G(Y) of the rbc F(X) such
               that G(Y) = F(X)[Y/X] where X and Y are vectors of
         variables.

  Given `rbcManager', the rbc `f', and the array of integers
               `subst', replaces every occurence of the variable
         x_i in in `f' with the variable x_j provided that
         subst[i] = j.

         Notice that in this context, 'i' and 'j' are LOGICAL
         indices, not physical, i.e. the substitution array is
         provided in terms of logical indices, and is related
         only to the logical level.

         For a substitution at physical level, see Rbc_Subst.

         There is no need for `subst' to contain all the
         variables, but it should map at least the variables in
         `f' in order for the substitution to work properly.

         The two indices arrays log2phy and phy2log map
         respectively the logical level to the physical level,
         and the physical level to the logical levels. They
         allow the be encoder to freely organize the variables
         into a logical and a physical level. This feature has
         been introduced with NuSMV-2.4 that ships dynamic
         encodings.

  \se none

  \sa Rbc_Subst
*/
Rbc_t* Rbc_LogicalSubst(Rbc_Manager_t* rbcManager, Rbc_t* f,
                               int* subst, const int* log2phy,
                               const int* phy2log);

/*!
  \brief Creates a fresh copy G(X') of the rbc F(X) by shifting
               each variable index of a certain amount.

  Given `rbcManager', the rbc `f', and the integer `shift',
               replaces every occurence of the variable x_i in in `f' with
         the variable x_(i + shift).

  !!!!!! WARNING   WARNING   WARNING   WARNING   WARNING   WARNING !!!!!
  !!                                                                  !!
  !!  This function cannot be used with the new encoding BeEnc,       !!
  !!  with NuSMV-2.4. As shifting involves the traversal of the       !!
  !!  logical layer within the                                        !!
  !!  BeEnc, simple shifting is no longer usable, and will produce    !!
  !!  unpredictable results if used on variables handled by a BeEnc   !!
  !!  instance.                                                       !!
  !!                                                                  !!
  !!  Use Rbc_LogicalShiftVar instead.                                !!
  !!                                                                  !!
  !!!!!! WARNING   WARNING   WARNING   WARNING   WARNING   WARNING !!!!!



  \se none
*/
Rbc_t* Rbc_Shift(Rbc_Manager_t* rbcManager, Rbc_t* f, int shift);

/*!
  \brief Creates a fresh copy G(X') of the rbc F(X) by shifting
               each variable index of a certain amount.

  Given `rbcManager', the rbc `f', and the integer `shift',
               replaces every occurence of the variable x_i in in `f' with
         the variable x_(i + shift).

         Notice that in this context, 'i' is a LOGICAL
         index, not physical, i.e. the substitution array is
         provided in terms of logical indices, and is related
         only to the logical level.

         For a substitution at physical level, see Rbc_SubstRbc.

         The two indices arrays log2phy and phy2log map
         respectively the logical level to the physical level,
         and the physical level to the logical levels. They
         allow the be encoder to freely organize the variables
         into a logical and a physical level. This feature has
         been introduced with NuSMV-2.4 that ships dynamic
         encodings.

  \se none
*/
Rbc_t*
Rbc_LogicalShift(Rbc_Manager_t* rbcManager, Rbc_t* f,
                 int shift, const int* log2phy, const int* phy2log);

/*!
  \brief Creates a fresh copy G(S) of the rbc F(X) such
               that G(S) = F(X)[S/X] where X is a vector of variables and
         S is a corresponding vector of formulas.

  Given `rbcManager', the rbc `f', and the array of rbcs
               `substRbc', replaces every occurence of the variable
         x_i in in `f' with the rbc r_i provided that
         substRbc[i] = r_i. There is no need for `substRbc' to contain
         all the  variables, but it should map at least the variables
         in `f' in order for the substitution to work properly.

         Here the substitution is performed completely at
         physical level (i.e. at the level of pure rbc
         indices). For a substitution at logical level, see
         Rbc_LogicalSubstRbc.

  \se none
*/
Rbc_t*
Rbc_SubstRbc(Rbc_Manager_t* rbcManager, Rbc_t* f, Rbc_t** substRbc);

/*!
  \brief Creates a fresh copy G(S) of the rbc F(X) such
               that G(S) = F(X)[S/X] where X is a vector of variables and
         S is a corresponding vector of formulas.

  Given `rbcManager', the rbc `f', and the array of rbcs
               `substRbc', replaces every occurence of the variable
         x_i in in `f' with the rbc r_i provided that
         substRbc[i] = r_i.

         Notice that in this context, 'i' is a LOGICAL index,
         not physical.

         There is no need for `substRbc' to contain
         all the  variables, but it should map at least the variables
         in `f' in order for the substitution to work properly.

         The two indices arrays log2phy and phy2log map
         respectively the logical level to the physical level,
         and the physical level to the logical levels. They
         allow the be encoder to freely organize the variables
         into a logical and a physical level. This feature has
         been introduced with NuSMV-2.4 that ships dynamic
         encodings.

  \se none
*/
Rbc_t*
Rbc_LogicalSubstRbc(Rbc_Manager_t* rbcManager, Rbc_t* f,
                    Rbc_t** substRbc, int* phy2log);

/*!
  \brief Prints various statistics.

  Prints various statistics.

  \se None
*/
void
Rbc_PrintStats(Rbc_Manager_t* rbcManager, int clustSz, FILE* outFile);

/*!
  \brief



  \se None
*/
Slist_ptr
RbcUtils_get_dependencies(Rbc_Manager_t* rbcManager, Rbc_t* f,
                          boolean reset_dag);

/*!
  \brief Calculates the inlining of the given formula

  Returned InlineResult instance is cached and must be _NOT_
        destroyed by the caller

  \se None

  \sa InlineResult
*/
struct InlineResult_TAG*
RbcInline_apply_inlining(Rbc_Manager_t* rbcm, Rbc_t* f);

/*!
  \brief Conversion from string to CNF conversion algorithm enumerative


*/
Rbc_2CnfAlgorithm
Rbc_CnfConversionAlgorithmFromStr(const char* str);

/*!
  \brief Conversion from CNF conversion algorithm enumerative to string


*/
const char *
Rbc_CnfConversionAlgorithm2Str(Rbc_2CnfAlgorithm algo);

/*!
  \brief String of valid conversion algorithms


*/
const char *
Rbc_CnfGetValidRbc2CnfAlgorithms(void);

/*!
  \brief Check if a rbc type is RBCTOP

  Check if a rbc type is RBCTOP
*/
boolean Rbc_is_top(Rbc_t* rbc);

/*!
  \brief Check if a rbc type is RBCVAR

  Check if a rbc type is RBCVAR
*/
boolean Rbc_is_var(Rbc_t* rbc);

/*!
  \brief Check if a rbc type is RBCAND

  Check if a rbc type is RBCAND
*/
boolean Rbc_is_and(Rbc_t* rbc);

/*!
  \brief Check if a rbc type is RBCIFF

  Check if a rbc type is RBCIFF
*/
boolean Rbc_is_iff(Rbc_t* rbc);

/*!
  \brief Check if a rbc type is RBCITE

  Check if a rbc type is RBCITE
*/
boolean Rbc_is_ite(Rbc_t* rbc);

/*!
  \brief Get the DAG cleaning function

  Get the DAG cleaning function
*/
RbcDfsFunctions_t*
Rbc_ManagerGetDfsCleanFun(Rbc_Manager_t* rbcManager);

/*!
  \brief Calls the internal DFS

  This is an external function that call the internal DFS

  \sa Dag_Dfs()
*/
void Rbc_Dfs_exported(Rbc_t* dfsRoot,
                             RbcDfsFunctions_t* dfsFun,
                             void* dfsData,
                             Rbc_Manager_t* manager);

/*!
  \brief Calls the internal DFS clean

  This is an external function that call the internal DFS clean

  \sa Dag_Dfs()
*/
void Rbc_Dfs_clean_exported(Rbc_t* dfsRoot,
                                   Rbc_Manager_t* manager);

#endif /* __NUSMV_CORE_RBC_RBC_H__ */
