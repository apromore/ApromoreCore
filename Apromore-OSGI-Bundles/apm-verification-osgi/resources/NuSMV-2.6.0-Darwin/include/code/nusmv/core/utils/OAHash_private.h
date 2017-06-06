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
  \brief Private interface of class 'OAHash'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_UTILS_OAHASH_PRIVATE_H__
#define __NUSMV_CORE_UTILS_OAHASH_PRIVATE_H__


#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/OAHash.h"


/**AutomaticStart*************************************************************/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OA_HASH_MINSIZE 8

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PERTURB_SHIFT 5

typedef struct OAEntry_TAG {
  size_t hash; /* This is for performances, if key_eq_fun is fast
                  (e.g. pointers equality) can be removed, sparing 64
                  bits for each entry*/
  void* key;
  void* value;
} OAEntry;

/*!
  \brief OAHash class definition

  
*/

typedef struct OAHash_TAG
{
  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */

  size_t fill;
  size_t used;
  size_t mask;

  OAEntry* table;
  OAEntry small_table[OA_HASH_MINSIZE];

  /* The argument passed to key_eq_fun, key_hash_fun and
     free_entry_fun */
  void* custom_arg;

  /* In the OAHash, this value is equal to custom_arg. However, having
     it adds the possibility to class extending the OAHash to have a
     custom value. */
  void* free_fun_arg;

  /* Function to check keys equality. Parameters are key1, key2 and
     the custom argument */
  OA_HASH_EQ_FUN key_eq_fun;

  /* The function for calculating the hash of the given
     key. Parameters are key and the custom argument */
  OA_HASH_HASH_FUN key_hash_fun;

  /* The function used for deleting entries. Parameters are key, value
     and the custom argument */
  OA_HASH_FREE_FUN free_entry_fun;

} OAHash;



/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof OAHash
  \todo
*/
void oa_hash_init(OAHash_ptr self,
                  OA_HASH_EQ_FUN key_eq_fun,
                  OA_HASH_HASH_FUN key_hash_fun,
                  OA_HASH_FREE_FUN free_entry_fun,
                  void* custom_arg);

/*!
  \methodof OAHash
  \todo
*/
void oa_hash_deinit(OAHash_ptr self);

/**AutomaticEnd***************************************************************/



#endif /* __OA_HASH_H__ */
