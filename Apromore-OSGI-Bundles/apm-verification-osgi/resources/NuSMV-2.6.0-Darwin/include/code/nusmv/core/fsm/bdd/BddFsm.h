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
  \author Roberto Cavada, Marco Benedetti
  \brief Declares the interface of the class BddFsm

  A BddFsm is a Finite State Machine (FSM) whose building blocks
               (the set of initial state, the transition relation, the set of
               constraints on inputs and so on) are represented by means of
               BDD data structures, and whose capabilities are based on
               operations upon and between BDDs as well.

*/



#ifndef __NUSMV_CORE_FSM_BDD_BDD_FSM_H__
#define __NUSMV_CORE_FSM_BDD_BDD_FSM_H__

#include "nusmv/core/fsm/bdd/bdd.h"
#include "nusmv/core/fsm/bdd/FairnessList.h"

#include "nusmv/core/dd/dd.h"
#include "nusmv/core/trans/bdd/BddTrans.h"
#include "nusmv/core/enc/bdd/BddEnc.h" /* Encoding */
#include "nusmv/core/fsm/sexp/sexp.h" /* VarSet_ptr */

/*!
  \struct BddFsm
  \brief


*/
typedef struct BddFsm_TAG*  BddFsm_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_FSM(x) \
         ((BddFsm_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BDD_FSM_CHECK_INSTANCE(x) \
         (nusmv_assert( BDD_FSM(x) != BDD_FSM(NULL) ))


enum Bdd_Fsm_dir_TAG {BDD_FSM_DIR_BWD, BDD_FSM_DIR_FWD};
typedef enum Bdd_Fsm_dir_TAG BddFsm_dir;

/*!
  \brief Format used by printers of transitions
*/
enum BddFsmTransPrinterFormat {
  BDD_FSM_TRANS_PRINTER_SILENT,  /* use to avoid printing */
  BDD_FSM_TRANS_PRINTER_SMV,
  BDD_FSM_TRANS_PRINTER_CSV,
  BDD_FSM_TRANS_PRINTER_DOT,
  BDD_FSM_TRANS_PRINTER_INVALID = -1,
};


/* ---------------------------------------------------------------------- */
/* public interface                                                       */
/* ---------------------------------------------------------------------- */

/*!
  \methodof BddFsm
  \brief Constructor for BddFsm

  All given bdd are referenced.
   self becomes the owner of given trans, justice and compassion objects,
   whereas the encoding is owned by the caller
*/
BddFsm_ptr
BddFsm_create(BddEnc_ptr encoding, BddStates init,
              BddInvarStates invar_states, BddInvarInputs invar_inputs,
              BddTrans_ptr trans,
              JusticeList_ptr justice, CompassionList_ptr compassion);

/*!
  \methodof BddFsm
  \brief Destructor of class BddFsm


*/
void BddFsm_destroy(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Copy constructor for BddFsm


*/
BddFsm_ptr BddFsm_copy(const BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Copies cached information of 'other' into self

  Copies cached information (reachable states, levels,
   fair states, etc.) possibly previoulsy calculated by 'other' into
   self.  Call this method when self is qualitatively identical to
   'other', but for some reason the trans is organized
   differently. Call to reuse still valid information calculated by
   'other' into self. If keep_family is true, the cache will be reused
   and not copied, meaning that self will belong to the same family as
   'other'. In this case a change in 'other' will have effects also on
   self (and viceversa). Notice that previoulsy calculated information
   into 'self' will be lost after the copy.
*/
void BddFsm_copy_cache(BddFsm_ptr self, const BddFsm_ptr other,
                              boolean keep_family);

/*!
  \methodof BddFsm
  \brief Getter for justice list

  self keeps the ownership of the returned object
*/
JusticeList_ptr BddFsm_get_justice(const BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Getter for compassion list

  self keeps the ownership of the returned object
*/
CompassionList_ptr BddFsm_get_compassion(const BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Getter for init

  Returned bdd is referenced
*/
BddStates BddFsm_get_init(const BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns the be encoding associated with the given fsm
   instance


*/
BddEnc_ptr BddFsm_get_bdd_encoding(const BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Getter for state constraints

  Returned bdd is referenced
*/
BddInvarStates
BddFsm_get_state_constraints(const BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Getter for input constraints

  Returned bdd is referenced
*/
BddInvarInputs
BddFsm_get_input_constraints(const BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Getter for the trans

  Returned Trans instance is not copied, do not destroy
   it, since self keeps the ownership.
*/
BddTrans_ptr BddFsm_get_trans(const BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns the set of fair states of a fsm.

  A state is fair iff it can reach a cycle that visits all
   fairness constraints.

   Note: a state is represented by state and frozen variables.

  \se Internal cache could change
*/
BddStates BddFsm_get_fair_states(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns the set of fair state-input pairs of the machine.

  A state-input pair is fair iff it can reach a cycle that
   visits all fairness constraints.

   Note: a state is represented by state and frozen variables.

  \se Internal cache could change
*/
BddStatesInputs BddFsm_get_fair_states_inputs(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns the set of reverse fair states of a fsm.

  A state is reverse fair iff it can be reached from a cycle
   that visits all fairness constraints.

   Note: a state is represented by state and frozen variables.

  \se Internal cache could change
*/
BddStates BddFsm_get_revfair_states(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns the set of reverse fair state-input pairs of the
   machine.

  A state-input pair is reverse fair iff it can be reached from
   a cycle that visits all fairness constraints.

   Note: a state is represented by state and frozen variables.

  \se Internal cache could change
*/
BddStatesInputs BddFsm_get_revfair_states_inputs(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns a bdd that represents the monolithic
   transition relation

  This method returns a monolithic representation of
   the transition relation, which is computed on the
   basis of the internal partitioned representation by
   composing all the element of the partition.

   Returned bdd is referenced.

  \se Internal cache could change
*/
bdd_ptr BddFsm_get_monolithic_trans_bdd(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns true if the set of reachable states has already been
   computed


   Note: a state is represented by state and frozen variables.
*/
boolean BddFsm_reachable_states_computed(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Gets the set of reachable states of this machine

  Returned bdd is referenced.

   This method returns the set R of reachable states,
   i.e.  those states that can be actually reached
   starting from one of the initial state.

   R is the set of states such that "i TRC s" holds for
   some state i in the set of initial states, where TRC
   is the transitive closure of the conjunction of the
   transition relation of the machine with the set of
   invar states, the set of constraints on inputs and the
   set of state/input constraints.

   R is computed by this method in a forward manner by
   exploiting the "BddFsm_get_forward_image" method
   during a fixpoint calculation. In particular, R is
   computed by reaching the fixpoint on the functional
   that maps S onto the forward image
   BddFsm_get_forward_image(S) of S, where the
   computation is started from the set of initial states.
   Notice that the set of invar states, the set of
   constraints on inputs and the set of state/input
   constrains are implicitly taken into account by
   BddFsm_get_forward_image(S).

   Note: a state is represented by state and frozen variables.

  \se Internal cache could change
*/
BddStates BddFsm_get_reachable_states(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Copies reachable states of 'other' into 'self'

  This method can be called when reachable states among
   FSMs can be reused, for example when other's reachable states are an
   over-extimation of self's. Parameter force_calculation forces the
   calculation of the reachable states of 'other' if needed (i.e. not
   previoulsy calculated).

   The two FSMs are allowed to belong to the same family. If parameter
   keep_family is true, than the original FSM's family will not change,
   and all the family's members (all the FSMs that have a common
   relative) will have their reachable states changed
   accordingly. Otherwise, self will be detached by its own original
   family (originating a new one), and all relatives will be not
   changed.

  \se Internal cache could change of both self and other
*/
void
BddFsm_copy_reachable_states(BddFsm_ptr self, BddFsm_ptr other,
                             boolean keep_family,
                             boolean force_calculation);

/*!
  \methodof BddFsm
  \brief Returns the set of reachable states at a given distance

  Computes the set of reachable states if not previously,
   cached. Returned bdd is referenced.

   If distance is greater than the diameter, an assertion
   is fired.

   This method returns the set R of states of this
   machine which can be reached in exactly "distance"
   steps by applying the "BddFsm_get_forward_image"
   method ("distance" times) starting from one of
   the initial states (and cannot be reached with less
   than "distance" steps).

   In the case that the distance is less than 0, the
   empty-set is returned.

   These states are computed as intermediate steps of the
   fixpoint characterization given in the
   "BddFsm_get_reachable_states" method.

   Note: a state is represented by state and frozen variables.

  \se Internal cache could change
*/
BddStates
BddFsm_get_reachable_states_at_distance(BddFsm_ptr self,
                                        int distance);

/*!
  \methodof BddFsm
  \brief Returns the distance of a given set of states from initial
   states

  Computes the set of reachable states if not previously cached.
   Returns -1 if given states set is not reachable.

   This method returns an integer which represents the
   distance of the farthest state in "states". The
   distance of one single state "s" is the number of
   applications of the "BddFsm_get_forward_image"
   method (starting from the initial set of states)
   which is necessary and sufficient to end up with a set
   of states containing "s". The distance of a *set* of
   states "set" is the maximal distance of states in
   "set", i.e. the number of applications of the
   "BddFsm_get_forward_image" method (starting from the
   initial set of states) which is necessary and
   sufficient to reach at least once (not necessarily
   during the last application, but somewhere along the
   way) each state in "set".

   So, the distance of a set of states is a max-min
   function.
   Could update the cache.

  \se Internal cache could change
*/
int
BddFsm_get_distance_of_states(BddFsm_ptr self,
                              BddStates states);

/*!
  \methodof BddFsm
  \brief Returns the minimum distance of a given set of states
   from initial states

  Computes the set of reachable states if not previously cached.
   Returns -1 if given states set is not reachable.

   This method returns an integer which represents the
   distance of the nearest state in "states". The
   distance of one single state "s" is the number of
   applications of the "BddFsm_get_forward_image"
   method (starting from the initial set of states)
   which is necessary and sufficient to end up with a set
   of states containing "s".
   Could update the cache.

  \se Internal cache could change
*/
int
BddFsm_get_minimum_distance_of_states(BddFsm_ptr self,
                                      BddStates states);

/*!
  \methodof BddFsm
  \brief Returns the diameter of the machine from the inital state

  This method returns an integer which represents the
   diameter of the machine with respect to the set of
   initial states, i.e.  the distance of the fatherst
   state in the machine (starting from the initial
   states), i.e. the maximal value among the lengths of
   shortest paths to each reachable state.  The initial
   diameter is computed as the number of iteration the
   fixpoint procedure described above (see
   "BddFsm_get_reachable_states") does before reaching
   the fixpoint.  It can also be seen as the maximal
   value the "BddFsm_get_distance_of_states" can return
   (which is returned when the argument "states" is set
   to "all the states").

   Could update the cache.

  \se Internal cache could change
*/
int BddFsm_get_diameter(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns the set of states without subsequents

  This method returns the set of states with no
   successor.  A state "ds" has no successor when all the
   following conditions hold:

   1) ds is a state satisfying stateConstr.
   2) no transition from ds exists which is consistent
   with input and state/input constraint and leads to
   a state satisfying stateConstr.

   Could update the cache.
   Note: a state is represented by state and frozen variables.

  \se Internal cache could change
*/
BddStates
BddFsm_get_not_successor_states(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns the set of deadlock states

  This method returns the set of deadlock states.  A
   state ds is said to be a deadlock state when all the
   following conditions hold:

   1) ds is a state satisfying stateConstr;
   2) no transition from ds exists which is consistent
   with input and state/input constraint and leads to
   a state satisfying stateConstr;
   3) s is rechable.

   Could update the cache. May trigger the computation of
   reachable states and states without successors.
   Returned bdd is referenced.

   Note: a state is represented by state and frozen variables.

  \se Cache can change
*/
BddStates BddFsm_get_deadlock_states(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns true if this machine is total

  This method checks wether this machine is total, in
   the sense that each INVAR state has at least one INVAR
   successor state given the constraints on the inputs
   and the state/input.

   This is done by checking that the BddFsm_ImageBwd
   image of the set of all the states is the set of all
   the INVAR states.  This way, the INVAR constraints
   together with the set of constraints on both input and
   state/input are implicitly taken into account by
   BddFsm_get_forward_image.

   The answer "false" is produced when states exist that
   admit no INVAR successor, given the sets of input and
   state/input constraints. However, all these "dead"
   states may be non-reachable, so the machine can still
   be "deadlock free".  See the "BddFsm_is_deadlock_free"
   method.

   Could update the cache. May trigger the computation of
   states without successors.

  \se Cache can change
*/
boolean BddFsm_is_total(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns true if this machine is deadlock free

  This method checks wether this machine is deadlock
   free, i.e.  wether it is impossible to reach an INVAR
   state with no admittable INVAR successor moving from
   the initial condition.

   This happens when the machine is total. If it is not,
   each INVAR state from which no transition to another
   INVAR state can be made according to the input and
   state/input constraints is non-reachable.

   This method checks deadlock freeness by checking
   that the intersection between the set of reachable
   states and the set of INVAR states with no admittable
   INVAR successor is empty.

   Could update the cache. May trigger the computation of
   deadlock states.

  \se Cache can change
*/
boolean BddFsm_is_deadlock_free(BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Returns the forward image of a set of states

  This method computes the forward image of a set of
   states S, i.e. the set of INVAR states which are
   reachable from one of the INVAR states in S by means
   of one single machine transition among those
   consistent with both the input constraints and the
   state/input constraints.

   The forward image of S(X,F) is computed as follows.
   X - state variables, I - input variables, F - frozen variables.

   a. S1(X,F,I)   := S(X,F) and Invar(X,F) and InputConst(I)
   b. S2(X',F)    := { <x',f> | <x,f,i,x'> in Tr(X,F,I,X') for
   some <x,i> in S1(X,F,I) }
   c. S3(X,F)     := S2(X',F)[x/x']
   d. FwdImg(X,F) := S3(X,F) and Invar(X,F)

   Note: a state is represented by state and frozen variables,
   but frozen variable are never abstracted away.

   Returned bdd is referenced.
*/
BddStates
BddFsm_get_forward_image(const BddFsm_ptr self, BddStates states);

/*!
  \methodof BddFsm
  \brief Returns the constrained forward image of a set of states

  This method computes the forward image of a set of
   states S, given a set C of contraints on STATE, FROZEN
   and INPUT vars which are meant to represent a
   restriction on allowed transitions and inputs.

   The constrained image is the set of INVAR states which
   are reachable from one of the INVAR states in S by
   means of one single machine transition among those
   consistent with both the constraints defined within
   the machine and the additional constraint C(X,F,I).

   The forward image of S(X,F) is computed as follows.
   X - state variables, I - input variables, F - frozen variables.

   a. S1(X,F,I) := S(X,F) and Invar(X,F) and InputConst(I) and C(X,F,I)
   b. S2(X',F)    := { <x',f> | <x,f,i,x'> in Tr(X,F,I,X') for
   some <x,f,i> in S1(X,F,I) }
   c. S3(X,F)     := S2(X',F)[x/x']
   d. FwdImg(X,F) := S3(X,F) and Invar(X,F)

   To apply no contraints, parameter constraints must be the
   true bdd.

   Note: a state is represented by state and frozen variables,
   but frozen variable are never abstracted away.

   Returned bdd is referenced
*/
BddStates
BddFsm_get_constrained_forward_image(const BddFsm_ptr self,
                                     BddStates states,
                                     BddStatesInputsNexts constraints);

/*!
  \methodof BddFsm
  \brief Returns the constrained forward image of a set of states

  This method computes the forward image of a set of
   states S, given a set C of contraints on STATE, FROZEN
   and INPUT and NEXT vars which are meant to represent a
   restriction on allowed transitions and inputs.

   The constrained image is the set of INVAR states which
   are reachable from one of the INVAR states in S by
   means of one single machine transition among those
   consistent with both the constraints defined within
   the machine and the additional constraint C(X,F,I).

   The forward image of S(X,F) is computed as follows.
   X - state variables, I - input variables, F - frozen variables.

   a. S1(X,F,I) := S(X,F) and Invar(X,F) and InputConst(I) and C(X,F,I)
   b. S2(X',F)    := { <x',f> | <x,f,i,x'> in Tr(X,F,I,X') for
   some <x,f,i> in S1(X,F,I) }
   c. S3(X,F)     := S2(X',F)[x/x']
   d. FwdImg(X,F) := S3(X,F) and Invar(X,F)

   To apply no contraints, parameter constraints must be the
   true bdd.

   Note: a state is represented by state and frozen variables,
   but frozen variable are never abstracted away.

   Returned bdd is referenced
*/
BddStates BddFsm_get_sins_constrained_forward_image(
    const BddFsm_ptr self,
    BddStates states,
    BddStatesInputsNexts constraints);

/*!
  \methodof BddFsm
  \brief Returns the forward image of a set of state-input pairs

  This method computes the forward image of a set of
   state-input pairs SI. This is the set of state-input
   pairs that fulfills INVAR and INPUT constraints and
   can be reached via a legal transition from at least
   one member of si that itself must fulfill INVAR and
   INPUT.

   The forward image of SI(X,F,I) is computed as follows.
   X - state variables, F - frozen variables, I - input
   variables.

   a. S1(X,F,I)     := SI(X,F,I) and Invar(X,F) and Input(I)
   b. S2(X',F)      := { <x',f> | <x,f,i,x'> in Tr(X,F,I,X')
   for some <x,i> in S1(X,F,I) }
   c. S3(X,F)       := S2(X',F)[x/x']
   d. FwdImg(X,F,I) := S3(X,F) and Invar(X,F) and Input(X,F,I)

   Note: a state is represented by state and frozen variables,
   but frozen variable are never abstracted away.

   Returned bdd is referenced.

  \sa BddFsm_get_constrained_forward_image_states_inputs,
   BddFsm_get_forward_image
*/
BddStatesInputs
BddFsm_get_forward_image_states_inputs(const BddFsm_ptr self,
                                       BddStatesInputs si);

/*!
  \methodof BddFsm
  \brief Returns the constrained forward image of a set of
   state-input pairs

  This method computes the forward image of a set of
   state-input pairs SI constrained by constraints (from
   now on C). This is the set of state-input pairs that
   fulfills INVAR and INPUT constraints and can be
   reached via a legal transition from at least one
   member of SI that itself must fulfill INVAR, INPUT,
   and C.

   The forward image of SI(X,F,I) is computed as follows.
   X - state variables, F - frozen variables, I - input
   variables.

   a. S1(X,F,I)     := SI(X,F,I) and Invar(X,F) and Input(I)
   and C(X,F,I)
   b. S2(X',F)      := { <x',f> | <x,f,i,x'> in Tr(X,F,I,X')
   for some <x,i> in S1(X,F,I) }
   c. S3(X,F)       := S2(X',F)[x/x']
   d. FwdImg(X,F,I) := S3(X,F) and Invar(X,F) and Input(I)

   To apply no contraints, parameter constraints must be
   the true bdd.

   Note: a state is represented by state and frozen variables,
   but frozen variable are never abstracted away.

   Returned bdd is referenced.

  \sa BddFsm_get_forward_image_states_inputs,
   BddFsm_get_constrained_forward_image
*/
BddStatesInputs
BddFsm_get_constrained_forward_image_states_inputs(const BddFsm_ptr self,
BddStatesInputs si,
BddStatesInputsNexts constraints);

/*!
  \methodof BddFsm
  \brief Returns the backward image of a set of states

  This method computes the backward image of a set S of
   states, i.e. the set of INVAR states from which some
   of the INVAR states in S is reachable by means of one
   single machine transition among those consistent with
   both the input constraints and the state/input
   constraints.

   The backward image of S(X,F) is computed as follows.
   X - state variables, I - input variables, F - frozen variables.

   a. S1(X,F)     := S(X,F) and Invar(X,F)
   b. S2(X',F)    := S1(X,F)[x'/x]
   c. S3(X,F,I)   := Invar(X,F) and InputConst(I)
   c. BwdImg(X,F) := { <x,f> | <x,f,i,x'> in Tr(X,F,I,X') for
   some <x,f,i> in S3(X,F,I) and some <x',f> in S2(X',F) }

   Note: a state is represented by state and frozen variables,
   but frozen variable are never abstracted away.

   Returned bdd is referenced.
*/
BddStates
BddFsm_get_backward_image(const BddFsm_ptr self, BddStates states);

/*!
  \methodof BddFsm
  \brief Returns the constrained backward image of a set of states

  This method computes the backward image of a set of
   states S, given a set C(X,F,I) of contraints on STATE, FROZEN
   and INPUT vars which are meant to represent a
   restriction on allowed transitions and inputs.

   The constrained image is the set of INVAR states from
   which some of the INVAR states in S is reachable by
   means of one single machine transition among those
   consistent with both the machine constraints and the
   given additional constraint C(X,F,I).

   The backward image of S(X,F,I) is computed as follows.
   X - state variables, I - input variables, F - frozen variables.

   a. S1(X,F)     := S(X,F) and Invar(X,F)
   b. S2(X',F)    := S1(X,F)[x'/x]
   c. S3(X,F,I)   := Invar(X,F) and InputConst(I)
   and IC(I) and C(X,F,I)
   c. BwdImg(X,F) := { <x,f> | <x,f,i,x'> in Tr(X,F,I,X') for
   some <x,f,i> in S3(X,F,I) and some <x',f> in S2(X',F) }

   To apply no contraints, parameter constraints must be
   the true bdd.

   Note: a state is represented by state and frozen variables,
   but frozen variable are never abstracted away.

   Returned bdd is referenced.
*/
BddStates
BddFsm_get_constrained_backward_image(const BddFsm_ptr self,
BddStates states,
BddStatesInputsNexts constraints);

/*!
  \methodof BddFsm
  \brief Returns the weak backward image of a set of states

  This method computes the set of <state,frozen,input> tuples
   that leads into the set of states given as input.
   i.e. the set of <s,f,i> such that <s,f,i> is
   consistent with both the input constraints and the
   state/input constraints, s is INVAR, and a transition
   from s to s' labelled by i exists for some INVAR s' in
   S.

   The weak backward image of S(X,F) is computed as follows.
   X - state variables, I - input variables, F - frozen variables.

   a. S1(X,F)   := S(X,F) and Invar(X,F)
   b. S2(X',F   := S1(X,F)[x'/x]
   c. S3(X,F,I) := Invar(X,F) and InputConst(I)
   c. WeakBwdImg(X,F,I) := {<x,f,i> | <x,f,i,x'> in Tr(X,F,I,X')
   for some <x,f,i> in S3(X,I) and some <x,f>' in S2(X',F) }

   Note: a state is represented by state and frozen variables,
   but frozen variable are never abstracted away.

   Returned bdd is referenced.
*/
BddStatesInputs
BddFsm_get_weak_backward_image(const BddFsm_ptr self,
                               BddStates states);

/*!
  \methodof BddFsm
  \brief Returns the k-backward image of a set of states

  This method computes the set of <state,frozen,input> tuples
   that lead into at least k distinct states of the set
   of states given as input. The returned couples
   and the states in the set given in input are restricted

   The k-backward image of S(X,F) is computed as follows.
   X - state variables, I - input variables, F - frozen variables.

   a. S1(X,F)   := S(X,F) and Invar(X,F)
   b. S2(X',F)    := S1(X,F)[X'/X]
   c. S3(X,F,I,k) := {<x,f,i> | exists x'[1..k] : S2(x'[m],f) and
   x'[m] != x'[n] if m != n and
   <x,f,i,x'[m]> in Tr }
   d. KBwdImg(X,F,I,k) := S3(X,F,I,k) and Invar(X,F) and
   InputConst(I)

   Note: a state is represented by state and frozen variables,
   but frozen variable are never abstracted away.

   The returned bdd is referenced.
*/
BddStatesInputs
BddFsm_get_k_backward_image(const BddFsm_ptr self,
                            BddStates states,
                            int k);

/*!
  \methodof BddFsm
  \brief Returns the strong backward image of a set of states

  This method computes the set of <state,frozem,input>
   transitions that have at least one successor and are
   such that all the successors lay inside the INVAR
   subset of the set of states given as input.

   The strong backward image of S(X, F, I) is computed as
   follows.
   X - state variables, I - input variables, F - frozen variables.

   a. S1(X,F,I) := WeakBwdImg(not S(X,F))
   b. S2(X,F,I) := (not S1(X,F,I)) and StateConstr(X,F) and
   InputConst(I)
   c. Tr(X,F,I) := {<x,d,i> | <x,d,i,x'> in Tr(X,F,I,X') for some x'}
   d. StrongBwdImg(X,F,I) := S2(X,F,I) and Tr(X,F,I)

   Note: a state is represented by state and frozen variables,
   but frozen variable are never abstracted away.

   Returned bdd is referenced.
*/
BddStatesInputs
BddFsm_get_strong_backward_image(const BddFsm_ptr self,
                                 BddStates states);

/*!
  \methodof BddFsm
  \brief Prints some information about this BddFsm.

  Prints some information about this BddFsm.

  \se None
*/
void BddFsm_print_info(const BddFsm_ptr self, OStream_ptr file);

/*!
  \methodof BddFsm
  \brief Prints statistical information about reachable states.

  Prints statistical information about reachable states, i.e. the real
  number of reachable states. It is computed taking care of the
  encoding and of the indifferent variables in the encoding.
*/
void
BddFsm_print_reachable_states_info(const BddFsm_ptr self,
                                   const boolean print_states,
                                   const boolean print_defines,
                                   const boolean print_formula,
                                   OStream_ptr file);

/*!
  \methodof BddFsm
  \brief Prints statistical information about fair states.

  Prints the number of fair states, taking care of the encoding and of
  the indifferent variables in the encoding. In verbose mode also
  prints transitions.
*/
void
BddFsm_print_fair_states_info(const BddFsm_ptr self,
                              const boolean verbose,
                              OStream_ptr file);

/*!
  \methodof BddFsm
  \brief Prints statistical information about fair states and
   transitions.

   Prints the number of fair states, taking care of
   the encoding and of the indifferent variables in the encoding.
*/
void BddFsm_print_fair_transitions_info(
    const BddFsm_ptr self,
    const enum BddFsmTransPrinterFormat format,
    OStream_ptr file);

/*!
  \methodof BddFsm
  \brief Prints statistical information about fair state/input pairs.

  Prints the number of fair states, taking care of
  the encoding and of the indifferent variables in the encoding.
*/
void BddFsm_print_fair_state_input_pairs_info(const BddFsm_ptr self,
                                              const boolean print_transitions,
                                              OStream_ptr file);

/*!
  \methodof BddFsm
  \brief Check that the transition relation is total

  Check that the transition relation is total. If not the
   case than a deadlock state is printed out. May trigger the
   computation of reachable states and states without successors.
*/
void BddFsm_check_machine(const BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Performs the synchronous product of two fsm

  Original description for BddFsm_apply_synchronous_product:

                The result goes into self, no changes on other.  Both
                the two FSMs must be based on the same dd manager.
                The cache will change, since a new separated family
                will be created for the internal cache, and it will
                not be shared anymore with previous family.  From the
                old cache will be reused as much as possible.

                Modified part:

                Takes cubes of state, input, and next state variables
                as arguments (rather than obtaining the cubes of all
                these variables from the bdd encoding). This is
                supposed to avoid problems when only subsets of
                variables need to be considered (as is the case for
                games).



  \se self will change

  \sa BddFsm_apply_synchronous_product,
                BddFsmCache_reset_not_reusable_fields_after_product
*/
void
BddFsm_apply_synchronous_product_custom_varsets(BddFsm_ptr self,
                                                const BddFsm_ptr other,
                                                bdd_ptr state_vars_cube,
                                                bdd_ptr input_vars_cube,
                                                bdd_ptr next_vars_cube);

/*!
  \methodof BddFsm
  \brief Variant of
                BddFsm_apply_synchronous_product_custom_varsets that
                simply takes all variables in the encoding into
                account.

  The result goes into self, no changes on other. Both
                the two FSMs must be based on the same dd manager.
                The cache will change, since a new separated family
                will be created for the internal cache, and it will
                not be shared anymore with previous family.  From the
                old cache will be reused as much as possible

  \se self will change

  \sa BddFsm_apply_synchronous_product_custom_varsets,
                BddFsmCache_reset_not_reusable_fields_after_product
*/
void
BddFsm_apply_synchronous_product(BddFsm_ptr self,
                                 const BddFsm_ptr other);

/*!
  \methodof BddFsm
  \brief Checks if a set of states is fair.


*/
boolean BddFsm_is_fair_states(const BddFsm_ptr self,
                                     BddStates states);

/*!
  \methodof BddFsm
  \brief Given two sets of states, returns the set of inputs
   labeling any transition from a state in the first set to a state in
   the second set.

  Note: a state is represented by state and frozen variables.
*/
BddInputs
BddFsm_states_to_states_get_inputs(const BddFsm_ptr self,
                                   BddStates cur_states,
                                   BddStates next_states);

/*!
  \methodof BddFsm
  \brief Returns a state-input pair for which at least one
   legal successor (if dir  = BDD_FSM_DIR_BWD) or
   predecessor (otherwise) exists


*/
BddStatesInputs
BddFsm_get_states_inputs_constraints(const BddFsm_ptr self,
                                     BddFsm_dir dir);

/*!
  \methodof BddFsm
  \brief Returns the states occurring in a set of states-inputs pairs.

  Quantifies away the input variables.
   Note: a state is represented by state and frozen variables.
*/
BddStates BddFsm_states_inputs_to_states(const BddFsm_ptr self,
                                         BddStatesInputs si);

/*!
  \methodof BddFsm
  \brief Returns the inputs occurring in a set of states-inputs pairs.

  Quantifies away the state variables (including frozen ones).
   A state is represented by state and frozen variables thus
   both state and frozen variables are abstracted away.
*/
BddStates BddFsm_states_inputs_to_inputs(const BddFsm_ptr self,
                                                BddStatesInputs si);

/*!
  \methodof BddFsm
  \brief Returns the cached reachable states
*/
boolean BddFsm_get_cached_reachable_states(const BddFsm_ptr self,
                                                  BddStates** layers,
                                                  int* size);

/*!
  \methodof BddFsm
  \brief Updates the cached reachable states
*/
void BddFsm_update_cached_reachable_states(const BddFsm_ptr self,
                                                  node_ptr layers,
                                                  int size,
                                                  boolean completed);

/*!
  \methodof BddFsm
  \brief Sets the whole set of reachable states for this FSM, with
   no onion ring informations

  Sets the whole set of reachable states for this FSM, with
   no onion ring informations
*/
void BddFsm_set_reachable_states(const BddFsm_ptr self,
                                        BddStates reachable);

/*!
  \methodof BddFsm
  \brief Checks if the set of reachable states exists in the FSM

  Checks if the set of reachable states exists in the FSM
*/
boolean BddFsm_has_cached_reachable_states(const BddFsm_ptr self);

/*!
  \methodof BddFsm
  \brief Makes k steps of expansion of the set of reachable states
   of this machine but limit the computation to terminate in the
   number of seconds specified (even if this limit can be exceeded for
   the termination of the last cycle)

   If k<0 the set is expanded until fixpoint, if max_seconds<0 no
   time limit is considered

  \se Changes the internal cache
*/
boolean
BddFsm_expand_cached_reachable_states(BddFsm_ptr self,
                                      int k,
                                      int max_seconds);

/* Features directly called by commands */

/*!
  \methodof BddFsm
  \brief Computes the set of reachable states

  Computes the set of reachable states
*/
boolean
BddFsm_compute_reachable(BddFsm_ptr self, int k, int t, int* diameter);


/*!
  \methodof BddFsm
  \brief Computes the number of transitions exiting given set of
  states/inputs
*/
double
BddFsm_count_transitions(const BddFsm_ptr self,
                         BddStatesInputs bdd);

/*!
  \methodof BddFsm
  \brief
*/
int
BddFsm_dump_fsm(BddFsm_ptr self,
                const NuSMVEnv_ptr env, node_ptr node_expr,
                char* str_constr,
                boolean init,
                boolean invar, boolean trans, boolean fair,
                boolean reachable,
                FILE* outfile);

/*!
  \methodof BddFsm
  \brief Prints the reachable states.

  print_reachable_states
*/
int
BddFsm_print_reachable_states(BddFsm_ptr self,
                              NuSMVEnv_ptr env, OStream_ptr stream,
                              boolean verbose, boolean print_defines,
                              boolean formula);

/*!
  \methodof BddFsm
  \brief Prints the fair states.

  Prints the fair states.
*/
int BddFsm_print_fair_states(BddFsm_ptr self,
                             const NuSMVEnv_ptr env,
                             const OStream_ptr outstream,
                             const boolean verbose);

/*!
  \methodof BddFsm
  \brief Print the fair transitions

*/
int BddFsm_print_fair_transitions(BddFsm_ptr self,
                                  const NuSMVEnv_ptr env,
                                  const enum BddFsmTransPrinterFormat format,
                                  const OStream_ptr outstream);

/*!
  \methodof BddFsm
  \brief Prints the fair state/input pairs

  This is like BddFsm_print_fair_transitions, but without the
  destination state.

*/
int BddFsm_print_fair_state_input_pairs(BddFsm_ptr self,
                                        const NuSMVEnv_ptr env,
                                        const OStream_ptr outstream,
                                        const boolean verbose);
/*!
  \brief Converts from string the format for trans printer.

  BDD_FSM_TRANS_PRINTER_INVALID is returned if the string does not
  correspond to any valid format.
*/
enum BddFsmTransPrinterFormat
BddFsm_trans_printer_format_from_string(const char* format_str);

/*!
  \brief Returns the string representation of the given format.
  NULL is returned if the format does not exist.
  The returned string must be NOT freed by the caller.
*/
const char*
BddFsm_trans_printer_format_to_string(enum BddFsmTransPrinterFormat format);

/*!
  \brief Returns the list of available formats for trans printer

  The number of entries is written by the function in num.
  The returned list must be freeded by the caller.
*/
enum BddFsmTransPrinterFormat*
BddFsm_trans_printer_get_avail_formats(size_t* num);


#endif /* __NUSMV_CORE_FSM_BDD_BDD_FSM_H__ */
