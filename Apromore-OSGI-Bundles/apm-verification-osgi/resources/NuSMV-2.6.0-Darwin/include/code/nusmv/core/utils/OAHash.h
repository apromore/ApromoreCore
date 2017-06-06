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
  \brief Public interface of class 'OAHash'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_OAHASH_H__
#define __NUSMV_CORE_UTILS_OAHASH_H__


#include "nusmv/core/utils/utils.h"

/*!
  \struct OAHash
  \brief Definition of the public accessor for class OAHash


*/
typedef struct OAHash_TAG*  OAHash_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef size_t OAHashIter;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef boolean (*OA_HASH_EQ_FUN)(const void*, const void*, void*);

typedef size_t (*OA_HASH_HASH_FUN)(const void*, void*);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*OA_HASH_FREE_FUN)(void* key, void* value, void* arg);

/*!
  \brief To cast and check instances of class OAHash

  These macros must be used respectively to cast and to check
   instances of class OAHash
*/
#define OA_HASH(self)                           \
  ((OAHash_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OA_HASH_CHECK_INSTANCE(self)                    \
  (nusmv_assert(OA_HASH(self) != OA_HASH(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OA_HASH_FOREACH(self, iter)             \
  for (iter = OAHash_get_first_iter(self);      \
       !OAHash_iter_is_end(self, iter);         \
       iter = OAHash_iter_next(self, iter))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OA_HASH_FOREACH_ENTRY(self, iter, key, value)                   \
  for (iter = OAHash_get_first_iter(self);                              \
       !OAHash_iter_is_end(self, iter) &&                               \
         (OAHash_iter_values(self, iter, (void**)key, (void**)value), true); \
       iter = OAHash_iter_next(self, iter))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof OAHash
  \brief The OAHash class constructor

  The OAHash class constructor

  For key_eq_fun and key_hash_fun there are a few utilities like
  OAHash_pointer_eq_fun and OAHash_pointer_hash_fun, respectively.

  @param free_entry_fun can be NULL
  @param custom_arg can be NULL

  \sa OAHash_destroy
*/
OAHash_ptr OAHash_create(OA_HASH_EQ_FUN key_eq_fun,
                         OA_HASH_HASH_FUN key_hash_fun,
                         OA_HASH_FREE_FUN free_entry_fun,
                         void* custom_arg);

/*!
  \methodof OAHash
  \brief The OAHash class constructor

  The OAHash class constructor

  \sa OAHash_destroy
*/
OAHash_ptr OAHash_copy(const OAHash_ptr self);

/*!
  \methodof OAHash
  \brief The OAHash class destructor

  The OAHash class destructor

  \sa OAHash_create
*/
void OAHash_destroy(OAHash_ptr self);

/*!
  \methodof OAHash
  \brief Inserts the given pair key -> value in the OAHash

  Inserts the given pair key -> value in the OAHash.

  If the key already exists, it is replaced and the old value is
  returned. Returns true if the key has been replaced
*/
boolean OAHash_insert(OAHash_ptr self,
                      const void* key,
                      const void* value);

/*!
  \methodof OAHash
  \brief Looks up for the given key in the OAHash

  Looks up for the given key in the OAHash.

  NULL is returned if not found (NOTE: NULL values are admitted by the
  OAHash. If one inserts an entry such as OAHash_insert(k, NULL) in
  the hash, OAHash_lookup(k) will return NULL. Use OAHash_has_key for
  a correct interpretation of the return value
*/
void* OAHash_lookup(OAHash_ptr self,
                    const void* key);

/*!
  \methodof OAHash
  \brief Checks if the given key is in the OAHash

  Checks if the given key is in the OAHash
*/
boolean OAHash_has_key(OAHash_ptr self,
                       const void* key);

/*!
  \methodof OAHash
  \brief Removes the entry added with key "key" from the OAHash

  Removes the entry added with key "key" from the OAHash.

  Returns true if the key has been actually removed (i.e. the key was
  in the hash
*/
boolean OAHash_remove(OAHash_ptr self,
                      const void* key);

/*!
  \methodof OAHash
  \brief Clears all entries in the hash

  Clears all entries in the hash
*/
void OAHash_clear(OAHash_ptr self);

/*!
  \methodof OAHash
  \brief Returns the number of elements in the OAHash

  Returns the number of elements in the OAHash
*/
size_t OAHash_get_size(OAHash_ptr self);

/*!
  \methodof OAHash
  \brief Returns the first iterator for the given OAHash

  Returns the first iterator for the given OAHash
*/
OAHashIter OAHash_get_first_iter(const OAHash_ptr self);

/*!
  \methodof OAHash
  \brief Returns the next iterator for the given OAHash

  Returns the next iterator for the given OAHash
*/
OAHashIter OAHash_iter_next(const OAHash_ptr self, const OAHashIter iter);

/*!
  \methodof OAHash
  \brief Checks if the given iterator is not valid

  Checks if the given iterator is not valid (i.e. it points over the
  end)
*/
boolean OAHash_iter_is_end(const OAHash_ptr self, const OAHashIter iter);

/*!
  \methodof OAHash

  \brief Retrieves the key and value entries associated with the given
  iterator

  Retrieves the key and value entries associated with the given
  iterator. Both key and value can be NULL
*/
void OAHash_iter_values(const OAHash_ptr self, const OAHashIter iter,
                        void** key, void** value);



/* Functions for key_eq_fun and key_hash_fun */

/* Pointers based */

/*!
  \brief The OAHash pointer equality function

  The OAHash pointer equality function

  \sa OAHash_create
*/
boolean OAHash_pointer_eq_fun(const void* k1, const void* k2, void* arg);

/*!
  \brief The OAHash pointer hash function

  The OAHash pointer hash function

  \sa OAHash_create
*/
size_t OAHash_pointer_hash_fun(const void* key, void* arg);

/*!
  \brief The OAHash string equality function

  The OAHash string equality function

  \sa OAHash_create
*/
boolean OAHash_string_eq_fun(const void* k1, const void* k2, void* arg);

/*!
  \brief The OAHash string hash function

  The OAHash string hash function

  \sa OAHash_create
*/
size_t OAHash_string_hash_fun(const void* key, void* arg);



/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_UTILS_OAHASH_H__ */
