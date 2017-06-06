/* ---------------------------------------------------------------------------


  This file is part of the ``rbc'' package of NuSMV version 2.
  Copyright (C) 2000-2001 by University of Genova.
  Copyright (C) 2011 by FBK.

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
  \author Armando Tacchella and Tommi Junttila
  \brief Formula handling with Reduced Boolean Circuits (RBCs).

  Internal functions and data structures of the rbc package.

*/


#ifndef __NUSMV_CORE_RBC_RBC_INT_H__
#define __NUSMV_CORE_RBC_RBC_INT_H__

#include "nusmv/core/rbc/rbc.h"
#include "nusmv/core/rbc/InlineResult.h"

#include "nusmv/core/opt/opt.h"
#include "nusmv/core/utils/LRUCache.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/assoc.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/
/* both of them used for conditional compilation */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBC_ENABLE_ITE_CONNECTIVE 1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBC_ENABLE_IFF_CONNECTIVE 1

/* Rbc operators (symbols) */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBCTOP   (int) 0

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBCVAR   (int) 1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBCAND   (int) 2

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBCIFF   (int) 3

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBCITE   (int) 4

/* special value for a rbc node.
   The constant can be any illegal pointer value with proper alignment
   (see the description in definition of DAG_ANNOTATION_BIT for more info
   about alignment).
*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBCDUMMY ((Rbc_t*) 4)

/* Rbc statistics. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBCVAR_NO   (int)  0  /* How many variables. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RBCMAX_STAT (int)  1

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/
/*---------------------------------------------------------------------------*/
/* Stucture declarations                                                     */
/*---------------------------------------------------------------------------*/
/*!
  \brief RBC manager.

  Handles rbcs:
                 <ul>
                 <li> dagManager, to handle the associated pool of vertices;
                 <li> varTable, to index variable vertices;
                 <li> varCapacity, the maximum number of variables;
                 <li> one and zero, the logical constants true and false;
                 <li> rbcNode2cnfVar: RBC node -> CNF var
                      (used only in CNF convertion);
                 <li> cnfVar2rbcNode: CNF var -> RBC node;
                      (used only to obtain original variables from
                       CNF formula solution);
                 <li> maxUnchangedRbcVariable is the maximal RBC var
                      that will have the same index in CNF
                      (used for ease the readability of CNF formulas)
                      It is set during the first invocation of Rbc_Convert2Cnf;
                 <li> maxCnfVariable is maximal variable used in CNF formula,
                      used to generate new unique CNF variables.
                 <li> stats, for bookkeeping.
                 </ul>
*/

struct RbcManager {
  NuSMVEnv_ptr environment;
  Dag_Manager_t* dagManager;
  Rbc_t** varTable;
  int varCapacity;
  Rbc_t* one;
  Rbc_t* zero;

  LRUCache_ptr inlining_cache;

  /* splitted cache mapping in two sets (model, cnf) */
  hash_ptr rbcNode2cnfVar_model;
  hash_ptr rbcNode2cnfVar_cnf;

  hash_ptr cnfVar2rbcNode_model;
  hash_ptr cnfVar2rbcNode_cnf;

  int maxUnchangedRbcVariable;
  int maxCnfVariable;

  int stats[RBCMAX_STAT];
};


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/
/*!
  \brief Control the way compact CNF conversion is performed



  \se none
*/

//#define CNF_CONV_SP 0

/*!
  \brief Get the leftmost child.

  Get the leftmost child.
*/
#define RBC_GET_LEFTMOST_CHILD(rbc) (rbc->outList[0])

/*!
  \brief Get the right children.

  Get the right children.
*/
#define RBC_GET_SECOND_CHILD(rbc) (rbc->outList[1])

/*!
  \brief Rbc interface to underlying package

  Rbc interface to underlying package
*/
#define RbcGetRef(p) Dag_VertexGetRef(p)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RbcSet(p) Dag_VertexSet(p)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RbcClear(p) Dag_VertexClear(p)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RbcIsSet(p) Dag_VertexIsSet(p)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define RbcId(r,s) DagId(r,s)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define Rbc_get_type(rbc) rbc->symbol

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

int Rbc_Convert2CnfSimple(Rbc_Manager_t* rbcManager, Rbc_t* f,
                          Slist_ptr clauses, Slist_ptr vars,
                          int* literalAssignedToWholeFormula);

int Rbc_Convert2CnfCompact(Rbc_Manager_t* rbcManager, Rbc_t* f,
                           int polarity,
                           Slist_ptr clauses, Slist_ptr vars,
                           int* literalAssignedToWholeFormula);

int Rbc_get_node_cnf(Rbc_Manager_t* rbcm, Rbc_t* f, int* maxvar);

/* inlining cache control */
void rbc_inlining_cache_init(Rbc_Manager_t *);
void rbc_inlining_cache_quit(Rbc_Manager_t *);

/*!
  \brief Inline caching private service to retrieve a value.

  Returned instance is NOT referenced, do not destroy it as
  it belongs to the cache.
*/
InlineResult_ptr
rbc_inlining_cache_lookup_result(Rbc_Manager_t* rbcm, Rbc_t* f);

void Rbc_Dfs(Rbc_t* dfsRoot,
             RbcDfsFunctions_t* dfsFun,
             void* dfsData,
             Rbc_Manager_t* manager);

void Rbc_Dfs_clean(Rbc_t* dfsRoot,
                   Rbc_Manager_t* manager);

void Rbc_Dfs_do_only_last_visit(Rbc_t* dfsRoot,
                                RbcDfsFunctions_t* dfsFun,
                                void* dfsData,
                                Rbc_Manager_t* manager);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_RBC_RBC_INT_H__ */
