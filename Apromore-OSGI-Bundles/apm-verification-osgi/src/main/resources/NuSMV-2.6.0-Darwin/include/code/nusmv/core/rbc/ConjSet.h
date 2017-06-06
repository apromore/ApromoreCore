/* ---------------------------------------------------------------------------


  This file is part of the ``rbc'' package of NuSMV version 2. 
  Copyright (C) 2007 by FBK-irst. 

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
  \brief Public interface of class 'ConjSet'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_RBC_CONJ_SET_H__
#define __NUSMV_CORE_RBC_CONJ_SET_H__

#include "nusmv/core/rbc/rbc.h"

#include "nusmv/core/utils/utils.h"

/*!
  \struct ConjSet
  \brief Definition of the public accessor for class ConjSet

  A ConjSet holds the associations between variables 
	and the corresponding expression each variable can be substituted with. 
	A ConjSet is internally used by RBC inlining. In particular it is used 
	by class InlineResult
*/
typedef struct ConjSet_TAG*  ConjSet_ptr;

/*!
  \brief To cast and check instances of class ConjSet

  These macros must be used respectively to cast and to check
  instances of class ConjSet
*/
#define CONJ_SET(self) \
         ((ConjSet_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CONJ_SET_CHECK_INSTANCE(self) \
         (nusmv_assert(CONJ_SET(self) != CONJ_SET(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof ConjSet
  \brief The ConjSet class constructor

  The ConjSet class constructor

  \sa ConjSet_destroy
*/
ConjSet_ptr ConjSet_create(Rbc_Manager_t* rbcm);

/*!
  \methodof ConjSet
  \brief The ConjSet class destructor

  The ConjSet class destructor

  \sa ConjSet_create
*/
void ConjSet_destroy(ConjSet_ptr self);

/*!
  \methodof ConjSet
  \brief Copy constructor

  
*/
ConjSet_ptr ConjSet_copy(const ConjSet_ptr self);

/*!
  \methodof ConjSet
  \brief Adds a new variable assignment to set

  Will be kept onlt if 'better' then the possibly
  previous assigment
*/
void ConjSet_add_var_assign(ConjSet_ptr self, 
                     Rbc_t* var, Rbc_t* expr);

/*!
  \methodof ConjSet
  \brief Inherits as much as possible (provided that what it
  inherits is not worse than what it has collected so far) from the given
  ConjSet

  
*/
void ConjSet_inherit_from(ConjSet_ptr self, 
                       const ConjSet_ptr other);

/*!
  \methodof ConjSet
  \brief Makes the ConjSet flattened.

  Flattens the ConjSet, making minimal the graph of
  dependencies. A flatten ConjSet can then be used to substitute
  an expression
*/
void ConjSet_flattenize(ConjSet_ptr self);

/*!
  \methodof ConjSet
  \brief Substitutes all variables occurring into f that belong
        to self with the corresponding expression.

  If self was previously flattened, the resulting RBC
        will be flattened as well, but only about those parts that has
        the same language of self
*/
Rbc_t* ConjSet_substitute(ConjSet_ptr self, Rbc_t* f);

/*!
  \methodof ConjSet
  \brief Returns the conjuction of self with the given formula

  Returns a formula like:
            for all v,exp belonging to self,
          (/\ (v <-> exp)) /\ f
        
*/
Rbc_t* ConjSet_conjoin(ConjSet_ptr self, Rbc_t* f);

/*!
  \methodof ConjSet
  \brief Prints debugging information about self

  
*/
void ConjSet_print(const ConjSet_ptr self, FILE* file);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_RBC_CONJ_SET_H__ */
