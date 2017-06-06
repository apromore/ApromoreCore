/* ---------------------------------------------------------------------------


  This file is part of the ``trans.bdd'' package of NuSMV version 2.
  Copyright (C) 2003 by FBK-irst.

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
  \brief  The header file of ClusterList class.

   

*/


#ifndef __NUSMV_CORE_TRANS_BDD_CLUSTER_LIST_H__
#define __NUSMV_CORE_TRANS_BDD_CLUSTER_LIST_H__

#include "nusmv/core/trans/bdd/Cluster.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/node/node.h"

/*!
  \struct ClusterList
  \brief ClusterList Class.

   This class forms a list of clusters.
*/
typedef struct ClusterList_TAG* ClusterList_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef node_ptr ClusterListIterator_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLUSTER_LIST(x)  \
        ((ClusterList_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLUSTER_LIST_CHECK_INSTANCE(x)  \
        (nusmv_assert(CLUSTER_LIST(x) != CLUSTER_LIST(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLUSTER_LIST_ITERATOR(x) \
        ((ClusterListIterator_ptr) x)

/* ---------------------------------------------------------------------- */

/*
   This define controls the way a cluster is put into the cluster list
   If this macro is not defined, clusters will be appended instead of
   being prepended.
*/
/*#define CLUSTER_LIST_PREPEND_CLUSTER*/
#undef CLUSTER_LIST_PREPEND_CLUSTER

/*
   This decides how the affinity among two BDDs is computed. If this
   symbol is defined, then affinity is the ratio between the number of
   shared variables and the number of the union of all variables
   (intersection/union) as as suggested by Moon, Hachtel, Somenzi in
   BBT paper. Otherwise a variation to the previous one is used: this
   is possibly more expensive than the previous one.
*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MHS_AFFINITY_DEFINITION

/* ---------------------------------------------------------------------- */

/*!
  \methodof ClusterList
  \brief  Class ClusterList Constructor. 

   The reference to DdManager passed here is internally
  stored but self does not become owner of it.
*/
ClusterList_ptr ClusterList_create(DDMgr_ptr dd);

/*!
  \methodof ClusterList
  \brief  ClusterList Class dectructor.

   Destroys the cluster list and all cluster instances
  inside it.
*/
void ClusterList_destroy(ClusterList_ptr self);

/*!
  \methodof ClusterList
  \brief Returns a copy of the "self".

  Duplicates self and each cluster inside it.
*/
ClusterList_ptr ClusterList_copy(const ClusterList_ptr self);

/*!
  \methodof ClusterList
  \brief  Returns an Iterator to iterate the self.

  
*/
ClusterListIterator_ptr
ClusterList_begin(const ClusterList_ptr self);

/*!
  \methodof ClusterList
  \brief  Returns an Iterator to iterate the self.

  
*/
DDMgr_ptr
ClusterList_get_dd_manager(const ClusterList_ptr self);

/*!
  \methodof ClusterList
  \brief  Returns the cluster kept at the position given by the
  iterator

  self keeps the ownership of the returned cluster
*/
Cluster_ptr
ClusterList_get_cluster(const ClusterList_ptr self,
                        const ClusterListIterator_ptr iter);

/*!
  \methodof ClusterList
  \brief  Sets the cluster of the "self" at the position given by
  iterator "iter" to cluster "cluster".

  
*/
void ClusterList_set_cluster(ClusterList_ptr self,
                                    const ClusterListIterator_ptr iter,
                                    Cluster_ptr cluster);

/*!
  \methodof ClusterList
  \brief  Returns the number of the clusters stored in "self".

  
*/
int ClusterList_length(const ClusterList_ptr self);

/*!
  \methodof ClusterList
  \brief Prepends given cluster to the list

  List becomes the owner of the given cluster
*/
void
ClusterList_prepend_cluster(ClusterList_ptr self, Cluster_ptr cluster);

/*!
  \methodof ClusterList
  \brief Appends given cluster to the list

  List becomes the owner of the given cluster, if the user
  is going to call standard destructor
*/
void
ClusterList_append_cluster(ClusterList_ptr self, Cluster_ptr cluster);

/*!
  \methodof ClusterListIterator
  \brief Use to iterate a list

  Advances the iterator by one.
*/
ClusterListIterator_ptr
ClusterListIterator_next(const ClusterListIterator_ptr self);

/*!
  \methodof ClusterListIterator
  \brief Use to check if iterator is at the end of list

  
*/
boolean
ClusterListIterator_is_end(const ClusterListIterator_ptr self);

/*!
  \methodof ClusterList
  \brief  Reverses the list of clusters. 

  
*/
void ClusterList_reverse(ClusterList_ptr self);

/*!
  \methodof ClusterList
  \brief Deletes every occurrence of the given cluster from the
  self.

  Returns the number of removed occurrences. Clusters found
  won't be destroyed, simply their references will be removed from the list
*/
int
ClusterList_remove_cluster(ClusterList_ptr self, Cluster_ptr cluster);

/*!
  \methodof ClusterList
  \brief  It returns a monolithic transition cluster corresponding
  to the cluster list of the "self".

  "self" remains unchanged. 
*/
ClusterList_ptr
ClusterList_apply_monolithic(const ClusterList_ptr self);

/*!
  \methodof ClusterList
  \brief It returns a threshold based cluster list corresponding
  to the cluster list of the "self".

  "self" remains unchanged.
*/
ClusterList_ptr
ClusterList_apply_threshold(const ClusterList_ptr self,
                            const ClusterOptions_ptr cl_options);

/*!
  \methodof ClusterList
  \brief Orders the clusters according to the IWLS95 algo. to
  perform image computation.

  This function builds the
  data structures to perform image computation. <br>
  This process consists of the following steps:<br>
  <ol>
  <li> Ordering of the clusters given as input accordingly with the
       heuristic described in IWLS95.</li>
  <li> Clustering of the result of previous step accordingly the
       threshold value stored in the option \"image_cluster_size\".</li>
  <li> Ordering of the result of previous step accordingly with the
       heuristic described in IWLS95.</li>
  </ol>
*/
ClusterList_ptr
ClusterList_apply_iwls95_partition(const ClusterList_ptr self,
                                   bdd_ptr state_vars_cube,
                                   bdd_ptr input_vars_cube,
                                   bdd_ptr next_state_vars_cube,
                                   const ClusterOptions_ptr cl_options);

/*!
  \methodof ClusterList
  \brief Performs the synchronous product between two cluster lists

  All clusters into other are simply appended to "self".
  The result goes into "self", no changes on other. The scheduling
  is done with the variables from both cluster lists.
  Precondition: both lists should have scheduling done.

  \se self will change
*/
void
ClusterList_apply_synchronous_product(ClusterList_ptr self,
                                      const ClusterList_ptr other);

/*!
  \methodof ClusterList
  \brief Returns the monolithic bdd corresponding to the "self".

  The returned bdd is referenced
*/
bdd_ptr
ClusterList_get_monolithic_bdd(const ClusterList_ptr self);

/*!
  \methodof ClusterList
  \brief Computes the cube of the set of support of all the clusters

  Given a list of clusters, it computes their set of support.
  Returned bdd is referenced.
*/
bdd_ptr
ClusterList_get_clusters_cube(const ClusterList_ptr self);

/*!
  \methodof ClusterList
  \brief It builds the quantification schedule of the variables
  inside the clusters of the "self".

  
*/
void
ClusterList_build_schedule(ClusterList_ptr self,
                           bdd_ptr state_vars_cube,
                           bdd_ptr input_vars_cube);

/*!
  \methodof ClusterList
  \brief  Computes the image of the given bdd "s" using the
  clusters of the "self" while quantifying state vars only.

  Returned bdd is referenced
*/
bdd_ptr
ClusterList_get_image_state(const ClusterList_ptr self, bdd_ptr s);

/*!
  \methodof ClusterList
  \brief Computes the image of the given bdd "s" using the
  clusters of the "self" while quantifying both state and input vars.

  Returned bdd is referenced
*/
bdd_ptr
ClusterList_get_image_state_input(const ClusterList_ptr self, bdd_ptr s);

/*!
  \methodof ClusterList
  \brief  Computes the k image of the given bdd "s" using the
  clusters of the "self" while quantifying state vars only.

  Returned bdd is referenced
*/
bdd_ptr
ClusterList_get_k_image_state(const ClusterList_ptr self, bdd_ptr s, int k);

/*!
  \methodof ClusterList
  \brief Computes the k image of the given bdd "s" using the
  clusters of the "self" while quantifying both state and input vars.

  Returned bdd is referenced
*/
bdd_ptr
ClusterList_get_k_image_state_input(const ClusterList_ptr self, bdd_ptr s, int k);

/*!
  \methodof ClusterList
  \brief Prints size of each cluster of the "self"

  
*/
void
ClusterList_print_short_info(const ClusterList_ptr self, FILE* file);

/*!
  \methodof ClusterList
  \brief Returns true if two clusters list are logically equivalent

  It compares BDDs not Clusters.
*/
boolean ClusterList_check_equality(const ClusterList_ptr self,
                                          const ClusterList_ptr other);

/*!
  \methodof ClusterList
  \brief Check the schedule for self. Call after you applied the
  schedule

  Let Ci and Ti be the ith cube and relation in the list.
  The schedule is correct iff<br>
  <ol>
  <li> For all Tj: j > i, S(Tj) and S(Ci) do not intersect, i.e., the
  variables which are quantified in Ci should not appear in the
  Tj for j>i.</li>
  </ol><br>

  where S(T) is the set of support of the BDD T.
  Returns true if the schedule is correct, false otherwise.
  This function is implemented for checking the correctness of the
  clustering algorithm only.<br>
  This function returns true if schedule is correct, false otherwise.
*/
boolean ClusterList_check_schedule(const ClusterList_ptr self);


#endif /* __NUSMV_CORE_TRANS_BDD_CLUSTER_LIST_H__ */
