/* ---------------------------------------------------------------------------


  This file is part of the ``node.anonymizers'' package of NuSMV version 2.
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
  \author Roberto Cavada
  \brief Public interface of class 'NodeAnonymizerBase'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_BASE_H__
#define __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_BASE_H__


#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/defs.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/utils/BiMap.h"

/*!
  \struct NodeAnonymizerBase
  \brief Definition of the public accessor for class NodeAnonymizerBase

  
*/
typedef struct NodeAnonymizerBase_TAG*  NodeAnonymizerBase_ptr;

/*!
  \brief To cast and check instances of class NodeAnonymizerBase

  These macros must be used respectively to cast and to check
  instances of class NodeAnonymizerBase
*/
#define NODE_ANONYMIZER_BASE(self) \
         ((NodeAnonymizerBase_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_ANONYMIZER_BASE_CHECK_INSTANCE(self) \
         (nusmv_assert(NODE_ANONYMIZER_BASE(self) != NODE_ANONYMIZER_BASE(NULL)))

/*!
  \brief The encoding of the printed map:
  NODE_ANONYMIZER_DELIMITER_STR is the csv delimiter
  NODE_ANONYMIZER_DOT_STR       is the dot
  NODE_ANONYMIZER_SEPARATOR_STR is the separator between the children of the dot

  
*/
#define NODE_ANONYMIZER_DELIMITER_STR ":"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_ANONYMIZER_DOT_STR "."

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_ANONYMIZER_SEPARATOR_STR ","

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_ANONYMIZER_DELIMITER_CHAR ':'

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_ANONYMIZER_DOT_CHAR '.'

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define NODE_ANONYMIZER_SEPARATOR_CHAR ','


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof NodeAnonymizerBase
  \brief The NodeAnonymizerBase class destructor

  The NodeAnonymizerBase class destructor

  \sa NodeAnonymizerBase_create
*/
void NodeAnonymizerBase_destroy(NodeAnonymizerBase_ptr self);


/* Miscellaneous **************************************************************/

/*!
  \methodof NodeAnonymizerBase
  \brief Anonymize id and insert it in the mapping

  if prefix is not the empty string, it is used instead of
  the default prefix to create the anonymous id

  @param id     must not be in the map and must be an identifier
  @param prefix must not be NULL
*/
int NodeAnonymizerBase_map(NodeAnonymizerBase_ptr self,
                                  node_ptr id,
                                  const char* prefix);

/*!
  \methodof NodeAnonymizerBase
  \brief Insert a mapping

  
  @param original  must be an id and not in the map
  @param anonymous must be an id and not in the map back
*/
int NodeAnonymizerBase_force_map(NodeAnonymizerBase_ptr self,
                                        node_ptr original,
                                        node_ptr anonymous);

/*!
  \methodof NodeAnonymizerBase
  \brief Translates an expression

  expr is not normalized
*/
node_ptr NodeAnonymizerBase_map_expr(NodeAnonymizerBase_ptr self,
                                            node_ptr expr);

/*!
  \methodof NodeAnonymizerBase
  \brief Translates back an expression previously anonymized

  All the identifiers in expr must belong to the codomain of
  the map
  @return the anonymized expression or NULL if an error occurred
*/
node_ptr NodeAnonymizerBase_map_back(NodeAnonymizerBase_ptr self,
                                            node_ptr expr);

/*!
  \methodof NodeAnonymizerBase
  \brief Print the map on the given string

  
*/
int NodeAnonymizerBase_print_map(NodeAnonymizerBase_ptr self,
                                        FILE* stream);

/*!
  \methodof NodeAnonymizerBase
  \brief Reads a map from stream, and merge it into self

  If an error occurs, self is not changed.
  every line of stream must be in the following format

  id       := [A-Za-z_][A-Za-z0-9_\$#-]*
  dot_expr := (id | . | ,)+
  
  dot_expr:dot_expr

  
*/
int NodeAnonymizerBase_read_map_from_stream(NodeAnonymizerBase_ptr self,
                                                   FILE* stream);

/*!
  \brief Reads a map from a bimap, and merge it into self

  map is assumed to be syntactically correct
  error is returned if an original id is associated with a different anonymous
  id or if an anonymous id is already used in self
*/
int NodeAnonymizer_read_map_from_bimap(NodeAnonymizerBase_ptr self,
                                              BiMap_ptr map);

/*!
  \methodof NodeAnonymizerBase
  \brief Returns the size of the mapping

  
*/
size_t NodeAnonymizerBase_get_map_size(NodeAnonymizerBase_ptr self);


/* Queries *******************************************************************/

/*!
  \methodof NodeAnonymizerBase
  \brief True if id is already in the map as an original identifier
  

  id must be an id
*/
boolean NodeAnonymizerBase_is_id_original(NodeAnonymizerBase_ptr self,
                                                 node_ptr id);

/*!
  \methodof NodeAnonymizerBase
  \brief True if id is already in the map as an anonymous
  identifier

  id must be an id
*/
boolean NodeAnonymizerBase_is_id_anonymous(NodeAnonymizerBase_ptr self,
                                                  node_ptr id);

/*!
  \methodof NodeAnonymizerBase
  \brief True if the map is empty

  
*/
boolean NodeAnonymizerBase_is_map_empty(NodeAnonymizerBase_ptr self);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_BASE_H__ */
