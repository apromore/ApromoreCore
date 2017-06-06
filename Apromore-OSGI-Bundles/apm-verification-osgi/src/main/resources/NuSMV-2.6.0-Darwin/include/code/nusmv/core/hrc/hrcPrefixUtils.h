/* ---------------------------------------------------------------------------


  This file is part of the ``hrc'' package of NuSMV version 2.
  Copyright (C) 2009 by FBK.

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
  \author Sergio Mover
  \brief Header of hrcPrefixUtils.c.

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_HRC_HRC_PREFIX_UTILS_H__
#define __NUSMV_CORE_HRC_HRC_PREFIX_UTILS_H__

#include "nusmv/core/set/set.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/hrc/hrc.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/


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
  \brief Given a set of symbol returns a new set that
  contains only symbols that have a given prefix.

  Given a set of symbol returns a new set that
  contains only symbols that have a given prefix.

  The returned set must be destroyed by the caller.
*/
Set_t hrc_prefix_utils_get_prefix_symbols(Set_t symbol_set,
                                                 node_ptr prefix);

/*!
  \brief Returns true if subprefix is contained in prefix.


*/
boolean
hrc_prefix_utils_is_subprefix(node_ptr subprefix, node_ptr prefix);

/*!
  \brief Build the expression prefixed by context.

  Build the expression prefixed by context.

  If expression is of DOT or CONTEXT type we cannot build the tree
  DOT(context, expression). We need to recursively visit expression
  to build a correct DOT tree.
*/
node_ptr
hrc_prefix_utils_add_context(NodeMgr_ptr nodemgr,
                             node_ptr context, node_ptr expression);

/*!
  \brief Get the first subcontext of the given symbol.

  Get the first subcontext of the given symbol.

  Search the second CONTEXT or DOT node in symbol and returns it. If it
  is not found then Nil is returned.

  DOT and CONTEXT nodes are always searched in the car node.
*/
node_ptr hrc_prefix_utils_get_first_subcontext(node_ptr symbol);

/*!
  \brief Removes context from identifier.

  Removes context from identifier.
  If context is not
*/
node_ptr hrc_prefix_utils_remove_context(NodeMgr_ptr nodemgr,
                                                node_ptr identifier,
                                                node_ptr context);

/*!
  \brief Creates a new name for the module instance.

  Creates a new name for the module instance.

  The generated module name is <module_name>_<module_instance_flattened_name>
*/
node_ptr
hrc_prefix_utils_assign_module_name(HrcNode_ptr instance,
                                    node_ptr instance_name);

node_ptr hrc_prefix_utils_concat_context(const NuSMVEnv_ptr env,
                                         node_ptr ctx1,
                                         node_ptr ctx2);

node_ptr hrc_prefix_utils_contextualize_expr(const NuSMVEnv_ptr env,
                                               node_ptr expr,
                                               node_ptr context);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_HRC_HRC_PREFIX_UTILS_H__ */
