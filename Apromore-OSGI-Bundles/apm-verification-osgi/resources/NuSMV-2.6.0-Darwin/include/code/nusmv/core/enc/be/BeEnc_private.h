
/* ---------------------------------------------------------------------------


  This file is part of the ``enc.be'' package of NuSMV version 2. 
  Copyright (C) 2004 by FBK-irst. 

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
  \brief Private and protected interface of class 'BeEnc'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_ENC_BE_BE_ENC_PRIVATE_H__
#define __NUSMV_CORE_ENC_BE_BE_ENC_PRIVATE_H__


#include "nusmv/core/enc/be/BeEnc.h" 
#include "nusmv/core/enc/base/BoolEncClient.h"
#include "nusmv/core/enc/base/BoolEncClient_private.h" 

#include "nusmv/core/be/be.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/utils/utils.h" 


/*!
  \brief BeEnc class definition derived from class BoolEncClient. 
  This is the variables encoder for BMC. This class implements the
  boolean encoding for BMC, by providing variables groups and
  time-related functions. If the Boolean Expression (BE) layer
  provides actual access to variables, such as creation, shifting,
  substitution, expressions, etc., the BeEnc class provides the
  right semantics for those variables.

  The idea is to provide:
  1. A structured layer for variables (encoding)
  2. A set of functionalities to manage the variables. 

  The encoding is internally organized to provide efficient operations
  on generic BEs, like variables shifting and substitutions, and
  conversions between symbolic variables to BE variables, BE indices,
  etc.

  The class BeEnc keeps and manages a set of encoded input, state and
  frozen variables that are committed as bunches within SymbLayers. 
  The encoder keeps the relation between:
  - The variables occurring within the committed layers and the untimed 
    variables in terms of BE variables. 
  - The untimed variables and the corresponding timed variables
    instantitated at determinated times. 

  The management of the untimed and timed variables is allowed by the
  use of two separated levels, called the Physical Level and the
  Logical Level.


  * Physical Level 

    This level holds the actual BE variables, that are instantitated
    to represent both untimed and timed variables. The order the
    variables are organized must be considered as random and not
    directly controllable from outside the class BeEnc, but it is the
    physical level that will be seen when the indices of BE variables
    are manipulated. Since there is not visible structure which BE
    variables within the physical level are organized with, the user
    cannot assume anything about the indices. For this reason, special
    iterators are provided to iterate over a set of variables (for
    example, the set of state untimed vars).

  * Logical Level
   
    This level provides the necessary structure to the physical level,
    and it is visible only from within the class BeEnc. Every
    operation that operates on BE variables will pass through the
    physical level at first, and then through the logical level. The
    class BeEnc provides the necessary support to pass from one level
    to the other, but it is necessary to remember that the logical
    level is NOT visible and accessible from outside the class.

  The logical level is splitted into two distinct parts: 
  1.1 Untimed variables block.
  1.2 Timed variables block.

  1.1 Untimed variables block 

      Keeps the set of untimed (logical) BE variables, such that
      any (untimed) expression in term of BE will contain
      references to variables located in this logical block. This
      block is splitted into 4 sub-blocks, that group respectively
      untimed current state variables, frozen variables, input
      variables, and next state variables.

       * : current state variable
       @ : frozen variable
       = : input variable
       # : next state variable

          --- -- -- ---  
          *** @@ == ### 
          --- -- -- ---  
          012 34 56 789     : BE variable logical index 
          143 52 87 690     : BE variable physical index

      The picture shows an example of a logical untimed variables
      block. The model contains 7 boolean variables (logical indices
      from 0..6), where logical indices 0..2 refer the state
      variables, indices 3 and 4 refer to frozen variables, and logical 
      indices 5 and 6 refer the input
      variables. The set of state variables is than replicated to
      represent the next state variables, referred by logical indices
      7..9 that constitutes the forth sub-block in the picture.

      Notice that logical indices 0..9 refer a boolean variable
      allocated by the BE layer, that does not distinguish between
      state, input, frozen or next variables.  
      
      Also notice that BE variables are physically allocated at
      indices that can be completely different from the corresponding
      indices within the logical level. The class BeEnc keeps the
      relation between the logical and the physical level, and
      viceversa.

          state     frozen  input      next      UNTIMED BLOCK
        |-------|   |---|   |---|   |-------|
        0   1   2   3   4   5   6   7   8   9    LOGICAL INDEX
       _______________________________________
      | 1 | 4 | 3 | 5 | 2 | 8 | 7 | 6 | 9 | 0 |  LOG->PHY LEVEL MAP
       ---------------------------------------
      | 9 | 0 | 4 | 2 | 1 | 3 | 7 | 6 | 5 | 8 |  PHY->LOG LEVEL MAP
       ---------------------------------------


  1.2 Timed variables block
      
      Following the untimed logical variables block, the timed
      logical variables block holds the set of frozen, state and
      input variables that are instantiated at a given time. A BE
      expression instantiated at time t, will contain BE logical
      variables that belong to this block.

      Timed vars block is logically splitted into separate frames,
      each of one corresponding to a given time t.  The structure
      of each frame depends on the specific time t and on the
      number of transitions the model has. When the problem
      length k is 0, only frame from time 0 is allocated, and this
      frame is constituted by only state and frozen variables. In
      this condition the encoding structure (with untimed and
      timed blocks) would be this:

                    Untimed block             Timed block
        |--------------------------------| |---------------|
          current frozen  input    next     state 0  frozen 0
         -- -- --  -- --  -- --  -- -- --   -- -- --  -- -- 
        |  |  |  ||  |  ||  |  ||  |  |  | |  |  |  ||  |  |
         -- -- --  -- --  -- --  -- -- --   -- -- --  -- -- 
         00 01 02  03 04  05 06  07 08 09   10 11 12  13 14  : logical indices
         01 04 03  05 02  08 07  06 09 00   13 11 17  05 02  : physical indices
         
      
      In the example BE logical indices 10..12 are allocated to
      keep current state variables and logical indices 13..14 to
      keep frozen variables at the initial state (time 0). Since
      there are no transitions, input variables are not allocated
      for this value of problem length k.
      
      Frozen variables keep their values constant at all times. Thus
      to encode a frozen variable at any time it is enough to have
      just one (untimed) index. As result, new PHYSICAL indexes for
      frozen variables are introduced only in the untimed block. To
      enable more efficient time shifting, LOGICAL timed frames
      include frozen variables, but the corresponding physical indexes
      are the same as in the untimed block (see indexes 13..14 and
      03..04, respectively, in the example). Thus just one BE variable
      is created for a given frozen variable at any time.

      When problem length k=1, the encoding becomes:

                     Untimed block                   Timed block
     |--------------------------------| |---------------------------------------|
       current frozen  input   next       state0  frozen0 input0  state1  frozen1
      -- -- --  -- --  -- --  -- -- --   -- -- --  -- --  -- --  -- -- --  -- --
     |  |  |  ||  |  ||  |  ||  |  |  | |  |  |  ||  |  ||  |  ||  |  |  ||  |  |
      -- -- --  -- --  -- --  -- -- --   -- -- --  -- --  -- --  -- -- --  -- --
      00 01 02  03 04  05 06  07 08 09   10 11 12  13 14  15 16  17 18 19  20 21


      Here 00..21 are logical indices, and the corresponding
      physical indices are not shown.
 
      When k>0 the timed block is a sequence of 
      state-frozen (input-state-frozen)+ timed sub-blocks.
      In the example above BE logical variables 15 and 16 have been
      added to encode input variables at time 0 (since there is a
      transition leading to time 1), and logical variables 17..19 and 20..21
      have been added to encode state and frozen variables, respectively,
      at time 1.
      
      When a generic, untimed BE expressions E is instantiated at time
      t (i.e. is shifted to time t), each current state variable
      occurring in E will be replaced by the corresponding BE logical
      variable in the timed sub-block t, as well any occurring input
      variable. Each next state variable will be replaced by the
      corresponding state variable located in the timed sub-block
      (t+1). Frozen variables during shifting always stay untimed.
      
      It is important to notice that the problem length k must be
      strictly taken into account when shifting operations are
      performed. In particular, as there are not transitions outgoing
      the last state at time t=k, neither inputs nor next states can
      be shifted at time t=k.
      
      From the logics point of view, when t=k inputs variables at
      time t do not exist at all (and the encoding directly maps
      this idea).  Because of this, the logic value of input
      variables at time t=k is false.
      
      See the class interface for further details on the provided
      features.

  \sa Base class BoolEncClient
*/

typedef struct BeEnc_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(BoolEncClient); 

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */

  /* the boolean expr manager, that is in charge of actually allocate
     be variables: */
  Be_Manager_ptr be_mgr;   

  /* the number of physical indices allocated from BE manager
     (which may be different from number of used physical index
     and number of logical indices) */
  int phy_idx_capacity; 
  
  /* the maximum physical index already in the use. */
  int max_used_phy_idx; 
   
  /* the number of logical indices which enough memory has been
     allocated for. The number of logical indices in use equals
     the total size of untimed and all timed frames and is always
     less than this fields. Number of logical and physical indices
     may be different because every frozen var requires one
     physical and many logical indices. */
  int log_idx_capacity; 

  /* the amount of memory should be requested as an excess to
     optimize memory handling */
  int grow_excess; 

  /* max time reached until now for the variables environment: */
  int max_allocated_time;          

  /* number of variables occurring in the untimed logical block */
  int state_vars_num;
  int frozen_vars_num;
  int input_vars_num;

  /* queue of physical indices that can be reused after a layer is
     removed: */
  NodeList_ptr avail_phy_idx_queue; 

  /* var name to corresponding be: */
  hash_ptr name2be;     

  /* var logical index to name: */
  node_ptr* index2name; 
  int index2name_size;
  
  /* logical to physical indices map and viceversa:
     the size of phy2log is phy_idx_capacity.
     the size of log2phy is log_idx_capacity.
   */
  int* log2phy;
  int* phy2log;

  int* subst_array; /* Used by substitution operations */
  int subst_array_size; 

  st_table* shift_hash; /* used to memoize shifting operations */

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} BeEnc;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivate and friend classes only         */
/* ---------------------------------------------------------------------- */
/*!
  \methodof BeEnc
  \todo
*/
void be_enc_init(BeEnc_ptr self, 
                 SymbTable_ptr symb_table, 
                 BoolEnc_ptr bool_enc);

/*!
  \methodof BeEnc
  \todo
*/
void be_enc_deinit(BeEnc_ptr self);

void be_enc_commit_layer(BaseEnc_ptr enc_base, const char* layer_name);
void be_enc_remove_layer(BaseEnc_ptr enc_base, const char* layer_name);


#endif /* __NUSMV_CORE_ENC_BE_BE_ENC_PRIVATE_H__ */
