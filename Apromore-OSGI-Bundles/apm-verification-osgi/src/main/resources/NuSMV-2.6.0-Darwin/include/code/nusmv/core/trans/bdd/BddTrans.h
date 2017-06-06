/* ---------------------------------------------------------------------------


  This file is part of the ``trans.bdd'' package of NuSMV version 2.
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
  \brief The header file of the class BddTrans

   The package <tt> trans.bdd </tt> implements
  classes to store and maipulate transition relation in bdd form

*/


#ifndef __NUSMV_CORE_TRANS_BDD_BDD_TRANS_H__
#define __NUSMV_CORE_TRANS_BDD_BDD_TRANS_H__

#include "nusmv/core/trans/generic/GenericTrans.h"

#include "nusmv/core/trans/bdd/ClusterList.h"
#include "nusmv/core/trans/bdd/ClusterOptions.h"

#include "nusmv/core/dd/dd.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct BddTrans
  \brief The structure used to represent the transition relation.

  
*/
typedef struct BddTrans_TAG* BddTrans_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_TRANS(x)  \
        ((BddTrans_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_TRANS_CHECK_INSTANCE(x)  \
        (nusmv_assert(BDD_TRANS(x) != BDD_TRANS(NULL)))


/*!
  \brief This is enumeration of possible kinds of image computations

  Image computation can be done forward or backward. In
  both cases it is possible to leave only state or state-input
  variables. For example, TRANS_IMAGE_FORWARD_STATE is the kind
  referring to forward image which returns state variables only,
  i.e. with input variables abstracted away.

  Use macros TRANS_IMAGE_IS_FORWARD and TRANS_IMAGE_IS_STATE_ONLY
  to detect the class of kinds.
*/

typedef enum TransImageKind_TAG {
  TRANS_IMAGE_FORWARD_STATE = 0,
  TRANS_IMAGE_FORWARD_STATE_INPUT = 1,
  TRANS_IMAGE_BACKWARD_STATE = 2,
  TRANS_IMAGE_BACKWARD_STATE_INPUT = 3
} TransImageKind;



/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief Takes TransImageKind and returns 1 iff the kind
  is one of forward ones.

  Thus 0 is returned iff the kind is backward one.
*/
#define TRANS_IMAGE_IS_FORWARD(kind) (!((kind) >> 1))

/*!
  \brief Takes TransImageKind and returns 1 iff the kind
  is one of image returning states only without inputs.

  Thus 0 is returned iff the image is to return
  both state and input vars
*/
#define TRANS_IMAGE_IS_STATE_ONLY(kind) (!((kind) & 1))

/* ---------------------------------------------------------------------- */
/* Enable this to auto-check trans after creation                         */
#if 0
# define TRANS_DEBUG_THRESHOLD
#endif
/* ---------------------------------------------------------------------- */

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BddTrans
  \brief Builds the transition relation from a provided
  cluster list.

  None of given arguments will become owned by self.
  You should destroy cl_options by yourself.
  This is a specialized version of constructor to build BddTrans
  based on ClusterList

  \sa BddTrans_generic_create
*/
BddTrans_ptr
BddTrans_create(DDMgr_ptr dd_manager,
                const ClusterList_ptr clusters_bdd,
                bdd_ptr state_vars_cube,
                bdd_ptr input_vars_cube,
                bdd_ptr next_state_vars_cube,
                const TransType trans_type,
                const ClusterOptions_ptr cl_options);

/*!
  \brief Builds the transition relation

  This is a generic version of BddTrans constructor.
  It takes a generic data structure 'transition' and functions to manipulate
  with it. Ownership of 'transition' is passed to self and
  will be destroyed during BddTrans destruction by 'destroy' function.

  All the parameters are used just to set up struct BddTrans_TAG.
  See it for the description of, and constraints on, the parameters.
  
*/
BddTrans_ptr
BddTrans_generic_create(
                        const NuSMVEnv_ptr env,
                        const TransType trans_type,
                        void* transition,
                        void* (*copy)(void* tranistion),
                        void  (*destroy)(void* tranistion),
                        bdd_ptr (*compute_image)(void* tranistion,
                                                 bdd_ptr bdd, TransImageKind kind),
                        bdd_ptr (*compute_k_image)(void* tranistion,
                                                   bdd_ptr bdd, int k,
                                                   TransImageKind kind),
                        bdd_ptr (*get_monolithic_bdd)(void* tranistion),
                        void (*synchronous_product)(void* tranistion1,
                                                    void* const transition2),
                        void (*print_short_info)(void* tranistion, FILE* file),
                        ClusterList_ptr (*trans_get_clusterlist)(void* transition));

/*!
  \methodof BddTrans
  \brief Performs the synchronous product between two trans

  The result goes into self and contained forward and backward
  cluster lists would be rescheduled. Other will remain unchanged. 

  \se self will change
*/
void
BddTrans_apply_synchronous_product(BddTrans_ptr self,
                                   const BddTrans_ptr other);

/*!
  \methodof BddTrans
  \brief Returns a monolithic BDD representing the whole
  transition relation.

  Warning: computation of such BDD may be very
  time- and memory-consuming.

  Invoker has to free the returned BDD.

  \se self will change
*/
bdd_ptr
BddTrans_get_monolithic_bdd(const BddTrans_ptr self);

/*!
  \methodof BddTrans
  \brief Returns a clusterized BDD representing the whole
  transition relation.

  Invoker has to free the returned clusterlist
*/
ClusterList_ptr
BddTrans_get_clusterlist(const BddTrans_ptr self);

/*!
  \methodof BddTrans
  \brief Computes the forward image by existentially quantifying
  over state variables only.

  Returned bdd is referenced

  \se self keeps the ownership of the returned instance.
*/
bdd_ptr
BddTrans_get_forward_image_state(const BddTrans_ptr self, bdd_ptr s);

/*!
  \methodof BddTrans
  \brief Computes the forward image by existentially quantifying
  over both state and input variables.

  Returned bdd is referenced
*/
bdd_ptr
BddTrans_get_forward_image_state_input(const BddTrans_ptr self,
                                       bdd_ptr s);

/*!
  \methodof BddTrans
  \brief Computes the backward image by existentially quantifying
  over state variables only.

  Returned bdd is referenced
*/
bdd_ptr
BddTrans_get_backward_image_state(const BddTrans_ptr self, bdd_ptr s);

/*!
  \methodof BddTrans
  \brief Computes the backward image by existentially quantifying
  over both state and input variables.

  Returned bdd is referenced
*/
bdd_ptr
BddTrans_get_backward_image_state_input(const BddTrans_ptr self,
                                        bdd_ptr s);

/*!
  \methodof BddTrans
  \brief Computes the k forward image by existentially quantifying
  over state variables only.

  Returned bdd is referenced

  \se self keeps the ownership of the returned instance.
*/
bdd_ptr
BddTrans_get_k_forward_image_state(const BddTrans_ptr self,
                                   bdd_ptr s, int k);

/*!
  \methodof BddTrans
  \brief Computes the k forward image by existentially quantifying
  over both state and input variables.

  Returned bdd is referenced
*/
bdd_ptr
BddTrans_get_k_forward_image_state_input(const BddTrans_ptr self,
                                         bdd_ptr s, int k);

/*!
  \methodof BddTrans
  \brief Computes the k backward image by existentially quantifying
  over state variables only.

  Returned bdd is referenced
*/
bdd_ptr
BddTrans_get_k_backward_image_state(const BddTrans_ptr self,
                                    bdd_ptr s, int k);

/*!
  \methodof BddTrans
  \brief Computes the k backward image by existentially
  quantifying over both state and input variables.

  Returned bdd is referenced
*/
bdd_ptr
BddTrans_get_k_backward_image_state_input(const BddTrans_ptr self,
                                            bdd_ptr s, int k);

/*!
  \methodof BddTrans
  \brief Prints short info associated to a Trans

  Prints info about the size of each cluster in
  forward/backward transition relations
*/
void BddTrans_print_short_info(const BddTrans_ptr self,
                                      FILE* file);



#endif /* __NUSMV_CORE_TRANS_BDD_BDD_TRANS_H__ */
