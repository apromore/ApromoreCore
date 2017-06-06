/* ---------------------------------------------------------------------------

This file is part of the ``rbc.clg'' package
  of NuSMV version 2. Copyright (C) 2007 by FBK-irst.

  NuSMV version 2 is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public License
  as published by the Free Software Foundation; either version 2 of
  the License, or (at your option) any later version.

  NuSMV version 2 is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.

  For more information on NuSMV see <http://nusmv.fbk.eu>
  or email to <nusmv-users@fbk.eu>.
  Please report bugs to <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to <nusmv@fbk.eu>.

-----------------------------------------------------------------------------*/

/*!
  \author Dan Sheridan & Marco Roveri
  \brief Clause graphs

  Compact data structure for representing sets of clauses with
               sharing of common structure. The data structure is a graph of
               conjunctions and disjunctions which are converted using the
               standard (exponential-size) CNF conversion to obtain the required
               clauses.

*/


#ifndef __NUSMV_CORE_RBC_CLG_CLG_H__
#define __NUSMV_CORE_RBC_CLG_CLG_H__

#include "nusmv/core/utils/utils.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLG_DIMACS 20 /* Create clauses suitable for a DIMACS file */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLG_ZCHAFF 21 /* Create clauses suitable for feeding to ZChaff directly */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLG_NUSMV  22 /* Create clauses suitable for feeding to NuMSV */

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct Clg_Vertex
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct Clg_Vertex* clause_graph;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void(*Clg_Commit)(void*, int*, int);

/*!
  \struct ClgManager
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct ClgManager_TAG* ClgManager_ptr;

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
  \methodof ClgManager
  \brief Creates an instance of the clause manager

  Creates an instance of the clause manager
*/
ClgManager_ptr ClgManager_create(void);

/*!
  \brief Free all CLGs

  
*/
void ClgManager_destroy(ClgManager_ptr clgManager);

/*!
  \brief Create a CLG representing a single literal

  
*/
clause_graph Clg_Lit(ClgManager_ptr clgmgr, int literal);

/*!
  \brief Create a CLG representing a conjunction of two CLGs

  
*/
clause_graph Clg_Conj(ClgManager_ptr clgmgr,
                             clause_graph left, clause_graph right);

/*!
  \brief Create a CLG representing a disjunction of two CLGs

  
*/
clause_graph Clg_Disj(ClgManager_ptr clgmgr,
                             clause_graph left, clause_graph right);

/*!
  \brief Extract the real clauses from the CLG

  Calls commit with each extracted clause as an argument.
                      type indicates the style of clause (eg, ZChaff
                      all-positive integer format); *data is passed to commit
                      as an extra argument.

                      Clauses have duplicated literals suppressed and
                      clauses with both positive and negative
                      occurrences of the same literal are skipped.
*/
void Clg_Extract(const NuSMVEnv_ptr env, clause_graph head,
                        int type, Clg_Commit commit, void *data);

/*!
  \brief Return the number of clauses stored in the CLG

  
*/
int Clg_Size(clause_graph graph);

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_RBC_CLG_CLG_H__ */

