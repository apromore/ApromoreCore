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
  \brief Interface for module Check

  Contains function definition for propositional wff checking

*/


#ifndef __NUSMV_CORE_BMC_BMC_CHECK_H__
#define __NUSMV_CORE_BMC_BMC_CHECK_H__


#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/* matching function for iteration in lists of wffs */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef int (*BMC_PF_MATCH)(const NuSMVEnv_ptr, node_ptr,  int, void*);

/* answer function in case of match: */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*BMC_PF_MATCH_ANSWER)(const NuSMVEnv_ptr, node_ptr, int, void*);


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Helper function to simplify calling to
  'bmc_check_wff_list' for searching of propositional wff only.
  Returns a new list of wffs which contains legal wffs only

  

  \sa bmc_check_wff_list
*/
node_ptr
Bmc_CheckFairnessListForPropositionalFormulae(const NuSMVEnv_ptr env,
                                              node_ptr wffList);

/*!
  \brief For each element belonging to a given list of wffs,
  calls the given matching function. If function matches, calls given
  answering function

  This is a generic searching function for a property
  across a list of wffs. <i>Please note that searching is specific for a list
  of wffs, but the searching semantic and behaviour are generic and
  customizable.</i><br>
  Searching may be stopped after the Nth match, or can be continued till all
  list elements have been checked (specify <B>-1</B> in this case).
  In any case searching cannot be carried out over the <I>MAX_MATCHES</I>
  value.<br><br>
  <TABLE BORDER>
  <CAPTION> <B>Arguments:</B> </CAPTION>
  <TR> <TH> Parameter name </TH>  <TH> Description </TH> </TR>
  <TR> <TD> wffList </TD>         <TD> A list of wffs to iterate in </TD> </TR>
  <TR> <TD> pCheck  </TD>         <TD> Pointer to matching function.
   The checking function type is <B>BMC_PF_MATCH</B>, and has three
   parameters: <BR>
   <B> wff </B> the formula to check for <BR>
   <B> index </B> index of wff into list <BR>
   <B> pOpt </B> generic pointer to custom structure (optional) </TD> </TR>

  <TR> <TD> pCheckOptArgument </TD> <TD> Argument passed to pCheck
  (specify <B>NULL</B> if you do not use it.) </TD> </TR>

  <TR> <TD> iMaxMatches </TD>       <TD> Maximum number of matching to be
  found before return. This must be less of <I>MAX_MATCHES</I>.<BR>
  Specify <B>-1</B> to iterate across the entire list. </TD> </TR>

  <TR> <TD> aiMatchedIndexes </TD>  <TD> Optional <B>int</B> array which
  will contain all match indexes. <BR>
  Specify <B>NULL</B> if you do not need this functionality.
  Array size must be less of <I>MAX_MATCHES</I>. </TD> </TR>

  <TR> <TD> pAnswer </TD>           <TD> Pointer to answer function
  of type <B>BMC_PF_MATCH_ANSWER</B>. This function is called everytime
  a match is found. <BR>
  Specify <B>NULL</B> if you do not need for this functionality.
  The answer function has the following prototype: <BR>
  <I>void answer(node_ptr wff, int index, void* pOpt)</I> <BR>
  where:<BR>

  <B> wff </B> the formula that matches the criteria <BR>
  <B> index </B> is the index of wff into the list
  <B> pOpt  </B> pointer to generic & customizable structure
  (see <I>pAnswerOptArgument</I> below)

  <B> pAnswerOptArgument </B> optional parameter for pAnswer function,
  in order to ensure more flexibility. Specify <B>NULL</B> if you do not need
  for this functionality.) </TD> </TR>
  </TABLE>

  \se Given aiMatchedIndexes array changes if at least one
  match has found out
*/
int
Bmc_WffListMatchProperty(const NuSMVEnv_ptr env,
                         node_ptr wffList, BMC_PF_MATCH pCheck,
                         void* pCheckOptArgument, int iMaxMatches,
                         unsigned int* aiMatchedIndexes,
                         BMC_PF_MATCH_ANSWER pAnswer,
                         void* pAnswerOptArgument);

/**AutomaticEnd***************************************************************/

#endif  /* __NUSMV_CORE_BMC_BMC_CHECK_H__ */

