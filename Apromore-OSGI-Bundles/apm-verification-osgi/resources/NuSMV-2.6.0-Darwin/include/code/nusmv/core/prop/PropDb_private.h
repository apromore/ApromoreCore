/* ---------------------------------------------------------------------------


  This file is part of the ``prop'' package of NuSMV version 2.
  Copyright (C) 2010 by FBK-irst.

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
  \brief Private and protected interface of class 'PropDb'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_PROP_PROP_DB_PRIVATE_H__
#define __NUSMV_CORE_PROP_PROP_DB_PRIVATE_H__

#if HAVE_CONFIG_H
#include "nusmv-config.h"
#endif

#include "nusmv/core/prop/PropDb.h"
#include "nusmv/core/prop/Prop.h"

#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/object_private.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/array.h"

#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/EnvObject_private.h"

/*!
  \brief PropDb class definition derived from
               class Object

  

  \sa Base class Object
*/


/* Those are the types of the virtual methods. They can be used for
   type casts in subclasses. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef int (*PropDb_prop_create_and_add_method)(PropDb_ptr, \
                                                 SymbTable_ptr, \
                                                 node_ptr, \
                                                 Prop_Type);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*PropDb_verify_all_method)(const PropDb_ptr);

/* The class itself. */
typedef struct PropDb_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(EnvObject);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  array_t* prop_database; /* contained properties */

  PropDb_PrintFmt print_fmt; /* print format */

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  /* int (*)(PropDb_ptr, SymbTable_ptr, node_ptr, Prop_Type) */
  PropDb_prop_create_and_add_method prop_create_and_add;
  /* void (*)(const PropDb_ptr) */
  PropDb_verify_all_method verify_all;
} PropDb;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof PropDb
  \brief The PropDb class private initializer

  The PropDb class private initializer

  \sa PropDb_create
*/
void prop_db_init(PropDb_ptr self, NuSMVEnv_ptr environment);

/*!
  \methodof PropDb
  \brief The PropDb class private deinitializer

  The PropDb class private deinitializer

  \sa PropDb_destroy
*/
void prop_db_deinit(PropDb_ptr self);

/*!
  \methodof PropDb
  \brief Inserts a property in the DB of properties

  Given a formula and its type, a property is
  created and stored in the DB of properties. It returns either -1 in
  case of failure, or the index of the inserted property.
  
*/
int prop_db_prop_create_and_add(PropDb_ptr self,
                                       SymbTable_ptr symb_table,
                                       node_ptr spec,
                                       Prop_Type type);

/*!
  \methodof PropDb
  \brief Verifies all the properties in the DB

  All the properties stored in the database not
  yet verified will be verified. The properties are verified following
  the order CTL/COMPUTE/LTL/INVAR.
*/
void prop_db_verify_all(const PropDb_ptr self);


#endif /* __NUSMV_CORE_PROP_PROP_DB_PRIVATE_H__ */
