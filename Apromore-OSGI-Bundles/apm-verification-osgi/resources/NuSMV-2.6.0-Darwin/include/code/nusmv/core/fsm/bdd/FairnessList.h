/* ---------------------------------------------------------------------------


  This file is part of the ``fsm.bdd'' package of NuSMV version 2. 
  Copyright (C) 2003 by FBK-irst. 

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
  \brief Declares the interface for the classes that contains fairness
  conditions

  This interface exports three objects: 
  - a generic FairnessList base class
  - a class for justice list (list of BDDs), derived from FairnessList
  - a class for compassion list (couple of BDDs), derived from
    FairnessList

*/



#ifndef __NUSMV_CORE_FSM_BDD_FAIRNESS_LIST_H__
#define __NUSMV_CORE_FSM_BDD_FAIRNESS_LIST_H__

#include "nusmv/core/fsm/bdd/bdd.h"
#include "nusmv/core/utils/object.h" /* for object inheritance support */
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/dd/dd.h"

/* ---------------------------------------------------------------------- */
/* Base type, derives from Object                                         */

/*!
  \struct FairnessList
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct FairnessList_TAG* FairnessList_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FAIRNESS_LIST(x) \
       ((FairnessList_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define FAIRNESS_LIST_CHECK_INSTANCE(self) \
       (nusmv_assert( FAIRNESS_LIST(self) != FAIRNESS_LIST(NULL) ))
/* ---------------------------------------------------------------------- */



/* ---------------------------------------------------------------------- */
/* Iterator for the list                                                  */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef node_ptr FairnessListIterator_ptr; 
/* ---------------------------------------------------------------------- */



/* ---------------------------------------------------------------------- */
/* Derives from FairnessList */

/*!
  \struct JusticeList
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct JusticeList_TAG* JusticeList_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define JUSTICE_LIST(x) \
       ((JusticeList_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define JUSTICE_LIST_CHECK_INSTANCE(self) \
       (nusmv_assert( JUSTICE_LIST(self) != JUSTICE_LIST(NULL) ))
/* ---------------------------------------------------------------------- */


/* ---------------------------------------------------------------------- */
/* Derives from FairnessList */

/*!
  \struct CompassionList
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct CompassionList_TAG* CompassionList_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define COMPASSION_LIST(x) \
       ((CompassionList_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define COMPASSION_LIST_CHECK_INSTANCE(self) \
       (nusmv_assert( COMPASSION_LIST(self) != COMPASSION_LIST(NULL) ))
/* ---------------------------------------------------------------------- */



/* ---------------------------------------------------------------------- */
/*  Public methods:                                                       */
/* ---------------------------------------------------------------------- */

/*!
  \methodof FairnessList
  \brief Base class constructor

  
*/
FairnessList_ptr FairnessList_create(DDMgr_ptr dd_manager);

/*!
  \methodof FairnessList
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean FairnessList_is_empty(const FairnessList_ptr self);

/*!
  \methodof FairnessList
  \brief Use to start iteration

  
*/
FairnessListIterator_ptr 
FairnessList_begin(const FairnessList_ptr self);

/*!
  \methodof FairnessListIterator
  \brief Use to check end of iteration

  
*/
boolean 
FairnessListIterator_is_end(const FairnessListIterator_ptr self);

/*!
  \methodof FairnessListIterator
  \brief use to iterate on an list iterator

  
*/
FairnessListIterator_ptr 
FairnessListIterator_next(const FairnessListIterator_ptr self);


/* Justice */

/*!
  \methodof JusticeList
  \brief Constructor for justice fairness constraints list

  Call FairnessList_destroy to destruct self
*/
JusticeList_ptr JusticeList_create(DDMgr_ptr dd_manager);

/*!
  \methodof JusticeList
  \brief Getter for BddStates pointed by given iterator

  Returned bdd is referenced
*/
BddStates 
JusticeList_get_p(const JusticeList_ptr self, 
                  const FairnessListIterator_ptr iter);

/*!
  \methodof JusticeList
  \brief Appends the given bdd to the list

  Given bdd is referenced, so the caller should free it
  when it is no longer needed
*/
void JusticeList_append_p(JusticeList_ptr self, BddStates p);

/*!
  \methodof JusticeList
  \brief Creates the union of the two given fairness lists. Result
  goes into self

  

  \se self changes
*/
void 
JusticeList_apply_synchronous_product(JusticeList_ptr self, 
                                      const JusticeList_ptr other);

/* Compassion */

/*!
  \methodof CompassionList
  \brief Constructor for compassion fairness constraints list

  Call FairnessList_destroy to destruct self
*/
CompassionList_ptr CompassionList_create(DDMgr_ptr dd_manager);

/*!
  \methodof CompassionList
  \brief Getter of left-side bdd pointed by given iterator

  Returned bdd is referenced
*/
BddStates 
CompassionList_get_p(const CompassionList_ptr self, 
                     const FairnessListIterator_ptr iter);

/*!
  \methodof CompassionList
  \brief Getter of right-side bdd pointed by given iterator

  Returned bdd is referenced
*/
BddStates 
CompassionList_get_q(const CompassionList_ptr self, 
                     const FairnessListIterator_ptr iter);

/*!
  \methodof CompassionList
  \brief Appends the given BDDs to the list

  Given bdds are referenced, so the caller should free it
  when it is no longer needed
*/
void CompassionList_append_p_q(CompassionList_ptr self, 
                                      BddStates p, BddStates q);

/*!
  \methodof CompassionList
  \brief Creates the union of the two given fairness lists. Result
  goes into self

  

  \se self changes
*/
void 
CompassionList_apply_synchronous_product(CompassionList_ptr self, 
                                         const CompassionList_ptr other);

#endif /* __NUSMV_CORE_FSM_BDD_FAIRNESS_LIST_H__ */
