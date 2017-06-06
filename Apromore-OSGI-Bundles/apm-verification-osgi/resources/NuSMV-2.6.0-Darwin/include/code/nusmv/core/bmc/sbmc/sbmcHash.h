/* ---------------------------------------------------------------------------


  This file is part of the ``bmc.sbmc'' package of NuSMV version 2.
  Copyright (C) 2004 by Timo Latvala <timo.latvala@tkk.fi>.

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

  For more information of NuSMV see <http://nusmv.fbk.eu>
  or email to <nusmv-users@fbk.eu>.
  Please report bugs to <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to <nusmv@fbk.eu>. 

-----------------------------------------------------------------------------*/

/*!
  \author Timo Latvala
  \brief Public interface for the hash for pairs (node_ptr, unsigned).

  An hash table for pairs (node_ptr, unsigned).

*/


#ifndef __NUSMV_CORE_BMC_SBMC_SBMC_HASH_H__

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define __NUSMV_CORE_BMC_SBMC_SBMC_HASH_H__

#include "nusmv/core/node/node.h" /*For node_ptr*/


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_HASH_NOTFOUND -1

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct htable
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct htable *hashPtr;

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

struct table_pair {
  node_ptr key;
  int data;
};

struct htable {
  /**Number of slots allocated*/
  unsigned alloc;
  /**Number of slots occupied*/
  unsigned occupied;
  /**The table*/
  struct table_pair *table; 
};

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
  \brief Create a new hash_table

  Create a new hash_table

  \se None
*/
hashPtr Bmc_Hash_new_htable(const NuSMVEnv_ptr env);

/*!
  \brief Find a node in the table

  Find a node in the table. Return BMC_HASH_NOTFOUND if the
  node is not present 

  \se None
*/
int Bmc_Hash_find(hashPtr, node_ptr);

/*!
  \brief Insert an element in the table

  Insert an element in the table

  \se None
*/
void Bmc_Hash_insert(hashPtr, node_ptr, int);

/*!
  \brief Delete the table

  Delete the table

  \se None
*/
void Bmc_Hash_delete_table(hashPtr hash);

/*!
  \brief Return the number of occupied slots

  Return the number of occupied slots

  \se None
*/
unsigned Bmc_Hash_size(hashPtr hash);

#endif /*__NUSMV_CORE_BMC_SBMC_SBMC_HASH_H__*/
