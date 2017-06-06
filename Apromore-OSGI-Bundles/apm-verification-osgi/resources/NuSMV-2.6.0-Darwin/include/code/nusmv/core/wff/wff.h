/* ---------------------------------------------------------------------------


   This file is part of the ``wff'' package of NuSMV version 2.
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
  \brief Public interface for Well-Formed-Formula manipulation

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_WFF_WFF_H__
#define __NUSMV_CORE_WFF_WFF_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/wff/wffRewrite.h"
#include "nusmv/core/wff/ExprMgr.h"

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* Package initialization / deinitialization */

/*!
  \brief Initializes the wff package

  

  \sa wff_pkg_quit
*/
void wff_pkg_init(const NuSMVEnv_ptr env);

/*!
  \brief Deinitializes the wff package

  

  \sa wff_pkg_init
*/
void wff_pkg_quit(const NuSMVEnv_ptr env);


/* Package top-level exported functions */

/*!
  \brief Returns the modal depth of the given formula

  Returns 0 for propositional formulae, 1 or more for
  temporal formulae

  \se none
*/
int Wff_get_depth(const NuSMVEnv_ptr env, node_ptr ltl_wff);

/*!
  \brief Makes a <i>truth</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_truth(NodeMgr_ptr nodemgr);

/*!
  \brief Makes a <i>false</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_falsity(NodeMgr_ptr nodemgr);

/*!
  \brief Makes a <i>not</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_not(NodeMgr_ptr nodemgr,
                             node_ptr arg);

/*!
  \brief Makes an <i>and</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_and(NodeMgr_ptr nodemgr,
                             node_ptr arg1, node_ptr arg2);

/*!
  \brief Makes an <i>or</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_or(NodeMgr_ptr nodemgr,
                            node_ptr arg1, node_ptr arg2);

/*!
  \brief Makes an <i>implies</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_implies(NodeMgr_ptr nodemgr,
                                 node_ptr arg1, node_ptr arg2);

/*!
  \brief Makes an <i>iff</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_iff(NodeMgr_ptr nodemgr,
                             node_ptr arg1, node_ptr arg2);

/*!
  \brief Makes a <i>next</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_next(NodeMgr_ptr nodemgr,
                              node_ptr arg);

/*!
  \brief Applies <i>op_next</i> x times

  

  \se node hash may change
*/
node_ptr Wff_make_opnext_times(NodeMgr_ptr nodemgr,
                                      node_ptr arg, int x);

/*!
  \brief Makes an <i>op_next</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_opnext(NodeMgr_ptr nodemgr,
                                node_ptr arg);

/*!
  \brief Makes an <i>op_next</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_opprec(NodeMgr_ptr nodemgr,
                                node_ptr arg);

/*!
  \brief Makes an <i>op_next</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_opnotprecnot(NodeMgr_ptr nodemgr,
                                      node_ptr arg);

/*!
  \brief Makes a <i>globally</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_globally(NodeMgr_ptr nodemgr,
                                  node_ptr arg);

/*!
  \brief Makes a <i>historically</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_historically(NodeMgr_ptr nodemgr,
                                      node_ptr arg);

/*!
  \brief Makes an <i>eventually</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_eventually(NodeMgr_ptr nodemgr,
                                    node_ptr arg);

/*!
  \brief Makes an <i>once</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_once(NodeMgr_ptr nodemgr,
                              node_ptr arg);

/*!
  \brief Makes an <i>until</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_until(NodeMgr_ptr nodemgr,
                               node_ptr arg1, node_ptr arg2);

/*!
  \brief Makes an <i>since</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_since(NodeMgr_ptr nodemgr,
                               node_ptr arg1, node_ptr arg2);

/*!
  \brief Makes a <i>releases</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_releases(NodeMgr_ptr nodemgr,
                                  node_ptr arg1, node_ptr arg2);

/*!
  \brief Makes a <i>triggered</i> WFF

  

  \se node hash may change
*/
node_ptr Wff_make_triggered(NodeMgr_ptr nodemgr,
                                   node_ptr arg1, node_ptr arg2);


/* Queries *******************************************************************/

/*!
  \brief True if wff is a propositional formula

  Here, propositional means:
  * without temporal operators
  * boolean

  The allowance of next is controlled by a flag.
*/
boolean Wff_is_propositional(SymbTable_ptr symb_table,
                                    node_ptr wff,
                                    node_ptr context,
                                    boolean is_next_allowed);


#endif /* __NUSMV_CORE_WFF_WFF_H__ */
