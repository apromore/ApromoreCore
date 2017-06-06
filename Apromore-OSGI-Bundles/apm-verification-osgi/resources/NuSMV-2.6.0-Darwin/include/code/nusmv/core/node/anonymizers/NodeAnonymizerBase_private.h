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
  \brief Private and protected interface of class 'NodeAnonymizerBase'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_BASE_PRIVATE_H__
#define __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_BASE_PRIVATE_H__


#include "nusmv/core/node/anonymizers/NodeAnonymizerBase.h"
#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/EnvObject_private.h"
#include "nusmv/core/utils/defs.h"
#include "nusmv/core/utils/BiMap.h"
#include "nusmv/core/utils/LRUCache.h"
#include "nusmv/core/node/node.h"


#ifdef NODE_ANONYMIZER_BASE_DEBUG
#  include "nusmv/core/utils/Logger.h"
#  include "nusmv/core/node/printers/MasterPrinter.h"
#endif

#ifdef NODE_ANONYMIZER_BASE_DEBUG
FILE* nab_debug_stream;
Logger_ptr nab_debug_logger;
#endif

#ifdef NODE_ANONYMIZER_BASE_DEBUG
#  define NAB_DEBUG_PRINT(format, ...)                                  \
  {                                                                     \
    NuSMVEnv_ptr const env = EnvObject_get_environment(ENV_OBJECT(self)); \
    const MasterPrinter_ptr sexpprint =                                 \
      MASTER_PRINTER(NuSMVEnv_get_value(env, ENV_SEXP_PRINTER));        \
    Logger_ptr logger = nab_debug_logger;                               \
                                                                        \
    if (NULL == logger) {                                               \
      nab_debug_stream = fopen("nab_debug.txt", "w");                   \
      nab_debug_logger = Logger_create(nab_debug_stream);               \
      logger = nab_debug_logger;                                        \
    }                                                                   \
    Logger_log(logger, "%s", "\n");                                     \
    Logger_nlog(logger, sexpprint, format, __VA_ARGS__);                \
    Logger_log(logger, "%s", "\n");                                     \
   }
#else
#  define NAB_DEBUG_PRINT(format, message, ...) /* empty */
#endif

/*!
  \brief NodeAnonymizerBase class definition derived from
               class EnvObject


  Private members:
  map                 the bidirectional map of identifiers
  orig2anon           cache for expression translation
  memoization_threshold threshold for the caches. It represents the max total
  space used for memoization, so it is divided equally over the two caches.
  counter             used for assignind unique identifiers
  default_prefix      the prefix that will used for all anonymized
                      identifiers

  Virtual methods:
  translate           translate an id to a new, anonymous one. If id was already
                      mapped, the returned id is just taken from
                      there. Otherwise, it is computed, added to the map and
                      finally returned to the caller.

  is_leaf             true if node is a leaf
  is_id               true if node is an identifier (something to be translated)


  \sa Base class EnvObject
*/

typedef struct NodeAnonymizerBase_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(EnvObject);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  BiMap_ptr map;
  LRUCache_ptr orig2anon;
  LRUCache_ptr anon2orig;
  size_t memoization_threshold;
  unsigned long long counter;
  const char* default_prefix;

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  node_ptr (*translate)(NodeAnonymizerBase_ptr self,
                        node_ptr id,
                        const char* prefix);
  const char* (*build_anonymous)(NodeAnonymizerBase_ptr self,
                                 node_ptr id,
                                 const char* prefix);
  boolean (*is_leaf)(NodeAnonymizerBase_ptr self, node_ptr node);
  boolean (*is_id)(NodeAnonymizerBase_ptr self, node_ptr node);

} NodeAnonymizerBase;

/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */
/* Miscellaneous **************************************************************/

/*!
  \methodof NodeAnonymizerBase
  \brief The NodeAnonymizerBase class private initializer

  The NodeAnonymizerBase class private initializer
  @param default_prefix if NULL, it will default to "x"

  \sa NodeAnonymizerBase_create
*/
void node_anonymizer_base_init(NodeAnonymizerBase_ptr self,
                                      NuSMVEnv_ptr env,
                                      const char* default_prefix,
                                      size_t memoization_threshold);

/*!
  \methodof NodeAnonymizerBase
  \brief The NodeAnonymizerBase class private deinitializer

  The NodeAnonymizerBase class private deinitializer

  \sa NodeAnonymizerBase_destroy
*/
void node_anonymizer_base_deinit(NodeAnonymizerBase_ptr self);

/*!
  \methodof NodeAnonymizerBase
  \brief translate an id to a new, anonymous one. If id was already
                      mapped, the returned id is just taken from
                      there. Otherwise, it is computed, added to the map and
                      finally returned to the caller


  @param id     an identifier
  @param prefix a prefix to be used instead of the default one
*/
node_ptr node_anonymizer_base_translate(NodeAnonymizerBase_ptr self,
                                               node_ptr id,
                                               const char* prefix);

/*!
  \methodof NodeAnonymizerBase
  \brief Choose the prefix to be used in the construction of the
  anonymous identifier

  prefix must not be NULL
*/
const char* node_anonymizer_base_choose_prefix(NodeAnonymizerBase_ptr self,
                                                      const char* prefix);

/*!
  \methodof NodeAnonymizerBase
  \brief Translates an expression


*/
node_ptr node_anonymizer_base_map_expr(NodeAnonymizerBase_ptr self,
                                              node_ptr expr);

/*!
  \methodof NodeAnonymizerBase
  \brief Translates back an expression previously anonymized


*/
node_ptr node_anonymizer_base_map_back(NodeAnonymizerBase_ptr self,
                                              node_ptr expr);

/*!
  \methodof NodeAnonymizerBase
  \brief Reads a map from a bimap, and merge it into self

  map is assumed to be syntactically correct
  error is returned if an original id is associated with a different anonymous
  id or if an anonymous id is already used in self
*/
int node_anonymizer_read_map_from_bimap(NodeAnonymizerBase_ptr self,
                                               BiMap_ptr map);

/*!
  \methodof NodeAnonymizerBase
  \brief Build the string to be used for the anonymous id


  @return the returned string must be freed

  \se self->counter is incremented
*/
const char* node_anonymizer_base_build_anonymous(NodeAnonymizerBase_ptr self,
                                                        node_ptr id,
                                                        const char* prefix);

/*!
  \methodof NodeAnonymizerBase
  \brief Searches id in the map


  @node_ptr the anonoymous id corresponding to id if found, otherwise null
*/
node_ptr node_anonymizer_base_search_mapping(NodeAnonymizerBase_ptr self,
                                             node_ptr id);

/*!
  \methodof NodeAnonymizerBase
  \brief Insert a mapping in the map

  id must not be already in the map
*/
void node_anonymizer_base_insert_mapping(NodeAnonymizerBase_ptr self,
                                                node_ptr id,
                                                node_ptr anonymous);

/* Expression caches **********************************************************/

/*!
  \methodof NodeAnonymizerBase
  \brief Lookups for expr in the cache

  leaves, ids and NULL are not allowed as keys
*/
node_ptr node_anonymizer_base_search_expr_cache(NodeAnonymizerBase_ptr self,
                                                       node_ptr expr);

/*!
  \methodof NodeAnonymizerBase
  \brief Inserts an entry in the cache

  leaves, ids and NULL are not allowed as keys or values
*/
void node_anonymizer_base_insert_expr_cache(NodeAnonymizerBase_ptr self,
                                                   node_ptr expr,
                                                   node_ptr anonymous_expr);

/*!
  \methodof NodeAnonymizerBase
  \brief Lookups for expr in the cache

  leaves, ids and NULL are not allowed as keys
*/
node_ptr
node_anonymizer_base_search_anon2orig(NodeAnonymizerBase_ptr self,
                                            node_ptr expr);

/*!
  \methodof NodeAnonymizerBase
  \brief Inserts an entry in the cache

  leaves, ids and NULL are not allowed as keys or values
*/
void node_anonymizer_base_insert_anon2orig(NodeAnonymizerBase_ptr self,
                                                  node_ptr anonymous_expr,
                                                  node_ptr expr);


/* Queries *******************************************************************/

/*!
  \methodof NodeAnonymizerBase
  \brief True if node is leaf (traversal should stop)


*/
boolean node_anonymizer_base_is_leaf(NodeAnonymizerBase_ptr self,
                                            node_ptr id);

/*!
  \methodof NodeAnonymizerBase
  \brief True if id is an id


*/
boolean node_anonymizer_base_is_id(NodeAnonymizerBase_ptr self,
                                          node_ptr id);

/*!
  \methodof NodeAnonymizerBase
  \brief True if id is already in the map

  id must be an id
*/
boolean node_anonymizer_base_is_id_original(NodeAnonymizerBase_ptr self,
                                                   node_ptr id);

/*!
  \methodof NodeAnonymizerBase
  \brief True if id is already in the map

  id must be an id
*/
boolean node_anonymizer_base_is_id_anonymous(NodeAnonymizerBase_ptr self,
                                                    node_ptr id);



#endif /* __NUSMV_CORE_NODE_ANONYMIZERS_NODE_ANONYMIZER_BASE_PRIVATE_H__ */
