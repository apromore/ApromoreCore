/* ---------------------------------------------------------------------------


  This file is part of the ``core.node.anonymizers'' package of NuSMV version 2.
  Copyright (C) 2014 by FBK-irst.

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
  \author Michele Dorigatti
  \brief Private and protected interface of class 'NodeAnonymizerST'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_ST_PRIVATE_H__
#define __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_ST_PRIVATE_H__


#include "nusmv/core/node/anonymizers/NodeAnonymizerST.h" 
#include "nusmv/core/node/anonymizers/NodeAnonymizerDot.h" 
#include "nusmv/core/node/anonymizers/NodeAnonymizerDot_private.h" 
#include "nusmv/core/utils/defs.h"

/*!
  \brief 

  
*/
#define NAST_COUNTERS_CARDINALITY 11




/*!
  \brief NodeAnonymizerST class definition derived from
               class NodeAnonymizerDot

  
  counters Used for building unique ids
           WORD = 0, INTEGER = 1, ENUMERATIVE = 2, BOOLEAN = 3,
           CONSTANT = 4, DEFINE = 5, ARRAY=6, ARRAY_DEFINES=7, WORDARRAY=8,
           FUNCTION = 9, INTARRAY=10
          

  \sa Base class NodeAnonymizerDot
*/

typedef struct NodeAnonymizerST_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(NodeAnonymizerDot);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  SymbTable_ptr symb_table;
  int counters[NAST_COUNTERS_CARDINALITY];

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} NodeAnonymizerST;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof NodeAnonymizerST
  \brief The NodeAnonymizerST class private initializer

  The NodeAnonymizerST class private initializer

  \sa NodeAnonymizerST_create
*/
void node_anonymizer_st_init(NodeAnonymizerST_ptr self,
                                    NuSMVEnv_ptr env,
                                    const char* default_prefix,
                                    size_t memoization_threshold,
                                    SymbTable_ptr symb_table);

/*!
  \methodof NodeAnonymizerST
  \brief The NodeAnonymizerST class private deinitializer

  The NodeAnonymizerST class private deinitializer

  \sa NodeAnonymizerST_destroy
*/
void node_anonymizer_st_deinit(NodeAnonymizerST_ptr self);

/*!
  \methodof NodeAnonymizerST
  \brief \todo Missing synopsis

  \todo Missing description
*/
const char* node_anonymizer_st_build_anonymous(NodeAnonymizerBase_ptr self,
                                                      node_ptr id,
                                                      const char* prefix);

/*!
  \methodof NodeAnonymizerST
  \brief translate an id to a new, anonymous one. If id was already
                      mapped, the returned id is just taken from
                      there. Otherwise, it is computed, added to the map and
                      finally returned to the caller

  
  @param id     an identifier
  @param prefix a prefix to be used instead of the default one
*/
node_ptr node_anonymizer_st_translate(NodeAnonymizerBase_ptr self,
                                             node_ptr id,
                                             const char* prefix);

#endif /* __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_ST_PRIVATE_H__ */
