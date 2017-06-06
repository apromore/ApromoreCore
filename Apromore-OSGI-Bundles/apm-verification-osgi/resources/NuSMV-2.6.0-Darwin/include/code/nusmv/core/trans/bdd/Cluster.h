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
  \brief The header file of trans cluster class.

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_TRANS_BDD_CLUSTER_H__
#define __NUSMV_CORE_TRANS_BDD_CLUSTER_H__

#include "nusmv/core/trans/bdd/ClusterOptions.h"
#include "nusmv/core/utils/object.h" 
#include "nusmv/core/dd/dd.h"

/*!
  \struct Cluster
  \brief Cluster Class

   This class contains informations about a cluster:<br>
          <dl> 
            <dt><code>curr_cluster</code>
                <dd> The clusterized transition relation.  
            <dt><code>ex_state_input</code>
                <dd>  List of variables (state and input vars) that can be 
                existentially quantified when curr_cluster is multiplied in
                the product.
            <dt><code>ex_state</code>
                <dd> List of variables (only state vars) that can be 
                existentially quantified when curr_cluster is multiplied in the
                product. 
        </dl>
        <br>
        Note that frozen variables are not taken into account because
        they are never abstracted away (or used some other way) in
        pre- or post-image computation.
        <br>
        In addition, this class inherits from the Object class and contains
        a virtual copy constructor. 
*/
typedef struct Cluster_TAG*  Cluster_ptr;

/*!
  \struct ClusterIwls95
  \brief Iwls'95 Cluster Class 

   This class inherits from the "Cluster" class and also contains
  a field "benifit" to be used while ordering clusters.
*/
typedef struct ClusterIwls95_TAG*  ClusterIwls95_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLUSTER(x)    \
          ((Cluster_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLUSTER_CHECK_INSTANCE(x)  \
          (nusmv_assert(CLUSTER(x) != CLUSTER(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLUSTER_IWLS95(x)    \
          ((ClusterIwls95_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLUSTER_IWLS95_CHECK_INSTANCE(x)  \
          (nusmv_assert(CLUSTER_IWLS95(x) != CLUSTER_IWLS95(NULL)))



/* ---------------------------------------------------------------------- */
/*    Public interface                                                    */
/* ---------------------------------------------------------------------- */

/*!
  \methodof Cluster
  \brief The "Cluster" class constructor.

  Allocates and initializes a cluster.

  \sa Object_destroy
*/
Cluster_ptr Cluster_create(DDMgr_ptr dd);

/*!
  \methodof Cluster
  \brief Checks if two clusters are equal

  Notice that the check is performed only using the
  \"curr_cluster\" field of the Cluster class.
*/
boolean 
Cluster_is_equal(const Cluster_ptr self, const Cluster_ptr other);

/*!
  \methodof Cluster
  \brief Retrives the clusterized transition relation of the self
  .

  Returned bdd will be referenced
*/
bdd_ptr Cluster_get_trans(const Cluster_ptr self);

/*!
  \methodof Cluster
  \brief Sets the transition relation inside the cluster

  The given bdd will be referenced. Previously stored bdd 
  will be released
*/
void 
Cluster_set_trans(Cluster_ptr self, DDMgr_ptr dd, bdd_ptr current);

/*!
  \methodof Cluster
  \brief Returns a pointer to the list of variables (both state 
  and input vars) to be quantified.

  Returns a pointer to the list of variables to be
  quantified respect to the transition relation inside the cluster. Returned
  bdd is referenced.
*/
bdd_ptr 
Cluster_get_quantification_state_input(const Cluster_ptr self);

/*!
  \methodof Cluster
  \brief Sets the list of variables (both state and input vars) to
  be quantified inside the cluster.

  Given value will be referenced
*/
void 
Cluster_set_quantification_state_input(Cluster_ptr self, 
                                       DDMgr_ptr dd, bdd_ptr new_val);

/*!
  \methodof Cluster
  \brief Returns a pointer to the list of variables (state vars
  only) to be quantified

  Returned value is referenced
*/
bdd_ptr 
Cluster_get_quantification_state(const Cluster_ptr self);

/*!
  \methodof Cluster
  \brief Sets the list of variables (state vars only) to be
  quantified inside the cluster

  Given value will be referenced
*/
void 
Cluster_set_quantification_state(Cluster_ptr self, 
                                 DDMgr_ptr dd, bdd_ptr new_val);


/* ClusterIwls95 inherits from Cluster: */

/*!
  \methodof ClusterIwls95
  \brief  "ClusterIwls95" Class constructor.

  Allocates and initializes a cluster for IWLS95 alg.
  Please note that returned object can be casted to a cluster class instance.
  Use Cluster_destroy to destroy returned instance. The parameters passed to
  the constructor correspond to cluster options and 7 different factors (v_c,
  w_c, x_c, y_c, z_c, min_c and max_c) as explained in IWLS95 paper.

  \sa Cluster_destroy Cluster_create
*/
ClusterIwls95_ptr 
ClusterIwls95_create(DDMgr_ptr dd, 
                     const ClusterOptions_ptr trans_options, 
                     const double v_c, 
                     const double w_c, 
                     const double x_c, 
                     const double y_c, 
                     const double z_c, 
                     const double min_c, 
                     const double max_c);

/*!
  \methodof ClusterIwls95
  \brief Returns the value of the "benifit" variable. 

  
*/
double 
ClusterIwls95_get_benefit(const ClusterIwls95_ptr self);


#endif /* __NUSMV_CORE_TRANS_BDD_CLUSTER_H__ */
