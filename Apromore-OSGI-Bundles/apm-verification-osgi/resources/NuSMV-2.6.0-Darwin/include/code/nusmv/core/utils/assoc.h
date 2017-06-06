/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 1998-2001 by CMU and FBK-irst.

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
  \author Marco Roveri
  \brief Simple assscoiative list

  Provides the user with a data structure that
  implemnts an associative list. If there is already an entry with
  the same ky in the table, than the value associated is replaced with
  the new one.

*/


#ifndef __NUSMV_CORE_UTILS_ASSOC_H__
#define __NUSMV_CORE_UTILS_ASSOC_H__

#if HAVE_CONFIG_H
#  include "nusmv-config.h"
#endif

#include "cudd/util.h" /* for ARGS and EXTERN */
#include "nusmv/core/node/node.h" /* for node_ptr */
#include "cudd/st.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ASSOC_DELETE ST_DELETE

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ASSOC_CONTINUE ST_CONTINUE

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ASSOC_STOP ST_STOP

/*!
  \brief Iterate over all k-v pairs in the assoc.

  Iterate over all k-v pairs in the assoc.

                      IMPORTANT NOTE: If the loop is interrupted
                      (e.g. by a "break" call, the iterator must be
                      freed manually

  \se Expected types:
                      st_table* table
                      st_generator* iter
                      char** key
                      char** value
*/


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define ASSOC_FOREACH(table, iter, key, value)  \
  st_foreach_item(table, iter, key, value)

/*!
  \brief Generates a new iterator for the given hash

  Generates a new iterator for the given hash
*/


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define assoc_iter_init(table)                  \
  st_init_gen(table)

/*!
  \brief Iterate over all k-v pairs in the assoc.

  Returns the next k-v pair in the iterator.
                      If there are no more items, returns 0
*/


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define assoc_iter_next(iter, key, value)       \
  st_gen(iter, key, value)

/*!
  \brief Generates a new iterator for the given hash

  Generates a new iterator for the given hash
*/


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define assoc_iter_free(iter)                   \
  st_free_gen(iter)

/*!
  \brief Retrieve the number of elements in the hash

  Retrieve the number of elements in the hash
*/


/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define assoc_get_size(table)                   \
  st_count(table)

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct st_table
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct st_table* hash_ptr;
typedef enum st_retval assoc_retval;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef st_generator* assoc_iter;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef ST_PFSR PF_STCPCPCP;

typedef struct AssocAndDestroy_TAG
{
  hash_ptr assoc;
  PF_STCPCPCP destroy_func;
} AssocAndDestroy;

/*!
  \struct AssocAndDestroy
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct AssocAndDestroy_TAG* AssocAndDestroy_ptr;

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \brief


*/
hash_ptr new_assoc(void);

/*!
  \brief


*/
hash_ptr new_assoc_with_size(int initial_size);

/*!
  \brief


*/
hash_ptr new_assoc_with_params(ST_PFICPCP compare_fun,
                                      ST_PFICPI hash_fun);

/*!
  \brief


*/
hash_ptr new_assoc_string_key(void);

/*!
  \brief

   Frees any internal storage associated with the hash table.
  It's user responsibility to free any storage associated with the pointers in
  the table.
*/
void free_assoc(hash_ptr hash);

/*!
  \brief


*/
hash_ptr copy_assoc(hash_ptr hash);
hash_ptr assoc_deep_copy(hash_ptr hash, ST_PFSR copy_fun);

/*!
  \brief


*/
void clear_assoc(hash_ptr hash);

/*!
  \brief

  this is actually a very general function
*/
void clear_assoc_and_free_entries(hash_ptr, ST_PFSR);

/*!
  \brief

  this is actually a very general function
*/
void
clear_assoc_and_free_entries_arg(hash_ptr hash, ST_PFSR fn, char* arg);


/* Getters and Setters ********************************************************/

/*!
  \brief


*/
node_ptr find_assoc(hash_ptr, node_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void insert_assoc(hash_ptr, node_ptr, node_ptr);

/*!
  \brief

  Removes a key from the table. Returns the data associated with
   the given key, and if the key has not been in the table then Nil is
   returned.
*/
node_ptr remove_assoc(hash_ptr hash, node_ptr key);

/*!
  \brief

  Returns the list of inserted keys. If parameter ignore_nils is
               true, the those keys whose associated values are Nil (typically,
               removed associations) will not be added to the returned
               list. Entries in the returned list are presented in an arbitrary
               order.  NOTE: the invoker has to free the list (see free_list)
               after using it.

   WARNING: Calling this function is not free: The whole hash is traversed and
            the list of nodes is created (and has to be freed). Use
            ASSOC_FOREACH or assoc_foreach if possible
*/
node_ptr assoc_get_keys(hash_ptr hash,
                        NodeMgr_ptr nodemgr,
                        boolean ignore_nils);


/* Miscellaneous **************************************************************/

/*!
  \brief Iterates over the elements of the hash.

  For each (key, value) record in `hash', assoc_foreach
   call func with the arguments
   <pre>
   (*func)(key, value, arg)
   </pre>
   If func returns ASSOC_CONTINUE, st_foreach continues processing
   entries.  If func returns ASSOC_STOP, st_foreach stops processing and
   returns immediately. If func returns ASSOC_DELETE, then the entry is
   deleted from the symbol table and st_foreach continues.  In the case
   of ASSOC_DELETE, it is func's responsibility to free the key and value,
   if necessary.<p>

  \se None
*/
void assoc_foreach(hash_ptr hash, ST_PFSR fn, char *arg);

#endif /* __NUSMV_CORE_UTILS_ASSOC_H__ */
