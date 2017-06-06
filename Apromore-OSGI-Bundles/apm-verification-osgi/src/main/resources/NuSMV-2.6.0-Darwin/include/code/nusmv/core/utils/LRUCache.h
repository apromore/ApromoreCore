/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 2011 by FBK-irst.

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
  \author Alessandro Mariotti
  \brief Public interface of class 'LRUCache'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_LRUCACHE_H__
#define __NUSMV_CORE_UTILS_LRUCACHE_H__


#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/OAHash.h"

/*!
  \struct LRUCache
  \brief Definition of the public accessor for class LRUCache


*/
typedef struct LRUCache_TAG*  LRUCache_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef OAHashIter LRUCacheIter;

/* Function pointers for LRU */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef OA_HASH_EQ_FUN LRU_CACHE_EQ_FUN;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef OA_HASH_HASH_FUN LRU_CACHE_HASH_FUN;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef OA_HASH_FREE_FUN LRU_CACHE_FREE_FUN;

/*!
  \brief To cast and check instances of class LRUCache

  These macros must be used respectively to cast and to check
  instances of class LRUCache
*/
#define LRU_CACHE(self) \
         ((LRUCache_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LRU_CACHE_CHECK_INSTANCE(self) \
         (nusmv_assert(LRU_CACHE(self) != LRU_CACHE(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LRU_CACHE_FOREACH(self, iter)             \
  for (iter = LRUCache_get_first_iter(self);      \
       !LRUCache_iter_is_end(self, iter);         \
       iter = LRUCache_iter_next(self, iter))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LRU_CACHE_FOREACH_ENTRY(self, iter, key, value)                 \
  for (iter = LRUCache_get_first_iter(self);                            \
       !LRUCache_iter_is_end(self, iter) &&                             \
         (LRUCache_iter_values(self, iter, (void**)key, (void**)value), true); \
       iter = LRUCache_iter_next(self, iter))


/* Export some functionalities from the OAHash, that don't need any
   overriding */

/* boolean LRUCache_has_key (LRUCache_ptr self, const void* key) */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LRUCache_has_key(self, key)             \
  OAHash_has_key(OA_HASH(self), key)

/* LRUCacheIter LRUCache_get_first_iter (LRUCache_ptr self) */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LRUCache_get_first_iter(self)           \
  OAHash_get_first_iter(OA_HASH(self))

/* LRUCacheIter LRUCache_iter_next (LRUCache_ptr self,
                                          LRUCacheIter iter) */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LRUCache_iter_next(self, iter)                  \
  OAHash_iter_next(OA_HASH(self), (OAHashIter)iter)

/* boolean LRUCache_iter_is_end (const LRUCache_ptr self,
   const LRUCacheIter iter) */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LRUCache_iter_is_end(self, iter)                \
  OAHash_iter_is_end(OA_HASH(self), (OAHashIter)iter)

/* size_t LRUCache_get_size (const LRUCache_ptr self) */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LRUCache_get_size(self)                 \
  OAHash_get_size(OA_HASH(self))

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof LRUCache
  \brief The LRUCache class constructor

  The LRUCache class constructor.

  OAHash class keys equality and hashing functions can be used, e.g.
  OAHash_pointer_eq_fun and OAHash_pointer_hash_fun, respectively.

  @param free_entry_fun can be NULL
  @param custom_arg can be NULL

  \sa LRUCache_destroy, OAHash_create
*/
LRUCache_ptr LRUCache_create(size_t threshold,
                             LRU_CACHE_EQ_FUN key_eq_fun,
                             LRU_CACHE_HASH_FUN key_hash_fun,
                             LRU_CACHE_FREE_FUN free_entry_fun,
                             void* custom_arg);

/*!
  \methodof LRUCache
  \brief The LRUCache class destructor

  The LRUCache class destructor

  \sa LRUCache_create
*/
void LRUCache_destroy(LRUCache_ptr self);

/*!
  \methodof LRUCache
  \brief Looks up for an element in the LRUCache

  Looks up for an element in the LRUCache

  \sa LRUCache_insert
*/
void* LRUCache_lookup(LRUCache_ptr self, const void* key);

/*!
  \methodof LRUCache
  \brief Inserts an element in the LRUCache

  Inserts an element in the LRUCache

  \sa LRUCache_lookup
*/
boolean LRUCache_insert(LRUCache_ptr self,
                        const void* key, const void* value);

/*!
  \methodof LRUCache
  \brief Removes the entry added with key "key" from the LRUCache

  Removes the entry added with key "key" from the LRUCache.
                      Returns true if the key has been actually
                      removed (i.e. the key was in the hash
*/
boolean LRUCache_remove(LRUCache_ptr self, const void* key);

/*!
  \methodof LRUCache
  \brief Clears all entries in the cache

  Clears all entries in the cache
*/
void LRUCache_clear(LRUCache_ptr self);

/*!
  \methodof LRUCache
  \brief Retrieves the key and value entries associated
                      with the given iterator

  Retrieves the key and value entries associated
                      with the given iterator. Both key and value can
                      be NULL
*/
void LRUCache_iter_values(const LRUCache_ptr self,
                          const LRUCacheIter iter,
                          void** k, void** v);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_LRUCACHE_H__ */
