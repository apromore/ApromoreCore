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
  \author Originated from glu library of VIS
  \brief AVL Trees

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_UTILS_AVL_H__

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define __NUSMV_CORE_UTILS_AVL_H__

#if HAVE_CONFIG_H
#  include "nusmv-config.h"
#endif

#include "cudd/util.h"

/*!
  \struct avl_node_struct
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct avl_node_struct avl_node;
struct avl_node_struct {
    avl_node *left, *right;
    char *key;
    char *value;
    int height;
};

/*!
  \struct avl_tree_struct
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct avl_tree_struct avl_tree;
struct avl_tree_struct {
  avl_node *root;
  int (*compar)(char*, char*);
  int num_entries;
  int modified;
};

/*!
  \struct avl_generator_struct
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct avl_generator_struct avl_generator;
struct avl_generator_struct {
    avl_tree *tree;
    avl_node **nodelist;
    int count;
};

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define AVL_FORWARD 	0

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define AVL_BACKWARD 	1

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
avl_tree *avl_init_table(int (*compare)(char*, char*));

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int avl_delete(avl_tree *, char **, char **);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int avl_insert(avl_tree *, char *, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int avl_lookup(avl_tree *, char *, char **);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int avl_first(avl_tree *, char **, char **);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int avl_last(avl_tree *, char **, char **);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int avl_find_or_add(avl_tree *, char *, char ***);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int avl_count(avl_tree *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int avl_numcmp(char *, char *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int avl_gen(avl_generator *, char **, char **);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void avl_foreach(avl_tree *, void (*)(char*, char*), int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void avl_free_table(avl_tree *, void (*)(char*), void (*)(char*));

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void avl_free_gen(avl_generator *);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
avl_generator *avl_init_gen(avl_tree *, int);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define avl_is_member(tree, key)	avl_lookup(tree, key, (char **) 0)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define avl_foreach_item(table, gen, dir, key_p, value_p) 	\
    for(gen = avl_init_gen(table, dir); 			\
	    avl_gen(gen, key_p, value_p) || (avl_free_gen(gen),0);)

#endif
