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
  \brief The header file of ClusterOptions class.

   

*/


#ifndef __NUSMV_CORE_TRANS_BDD_CLUSTER_OPTIONS_H__
#define __NUSMV_CORE_TRANS_BDD_CLUSTER_OPTIONS_H__


#include "nusmv/core/opt/OptsHandler.h"

/*!
  \struct ClusterOptions
  \brief  ClusterOptions Class.

  This class contains the options to perform ordering
  of clusters in the IWLS95 partitioning method. <br>
  <code>cluster_size</code> is the threshold value used to create
  clusters. <code>w1</code>, <code>w2</code> etc are the weights used
  in the heuristic algorithms to order the clusters for early
  quantification.
*/
typedef struct ClusterOptions_TAG* ClusterOptions_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLUSTER_OPTIONS(x) \
             ((ClusterOptions_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define CLUSTER_OPTIONS_CHECK_INSTANCE(x) \
             nusmv_assert(CLUSTER_OPTIONS(x) != CLUSTER_OPTIONS(NULL))

/*!
  \methodof ClusterOptions
  \brief  "ClusterOptions" class constructor.

    Creates a ClusterOptions instance. 
*/
ClusterOptions_ptr 
ClusterOptions_create(OptsHandler_ptr options);

/*!
  \methodof ClusterOptions
  \brief  ClusterOption class destructor.

  
*/
void ClusterOptions_destroy(ClusterOptions_ptr self);

/*!
  \methodof ClusterOptions
  \brief  Returns the threshold field. 

  
*/
int ClusterOptions_get_threshold(const ClusterOptions_ptr self);

/*!
  \methodof ClusterOptions
  \brief  Checks whether Affinity is enabled. 

  
*/
boolean ClusterOptions_is_affinity(const ClusterOptions_ptr self);

/*!
  \methodof ClusterOptions
  \brief  Returns true if clusters must be appended, false if 
  clusters must be prepended 

  
*/
boolean 
ClusterOptions_clusters_appended(const ClusterOptions_ptr self);

/*!
  \methodof ClusterOptions
  \brief  Checks whether preordering is enabled. 

  
*/
boolean 
ClusterOptions_is_iwls95_preorder(const ClusterOptions_ptr self);

/*!
  \methodof ClusterOptions
  \brief  Returns the cluster_size field. 

  
*/
int 
ClusterOptions_get_cluster_size(const ClusterOptions_ptr self);

/*!
  \methodof ClusterOptions
  \brief  Retrieves the parameter w1. 

   According to the IWLS95 paper parameter w1 represents the
  weight attached to the R^1_c( =v_c/w_c) factor.
*/
int 
ClusterOptions_get_w1(const ClusterOptions_ptr self);

/*!
  \methodof ClusterOptions
  \brief Retrieves the parameter w2. 

  According to the IWLS95 paper parameter w2 represents the
    weight attached to the R^2_c( =w_c/x_c) factor.
*/
int 
ClusterOptions_get_w2(const ClusterOptions_ptr self);

/*!
  \methodof ClusterOptions
  \brief Retrieves the parameter w3. 

  According to the IWLS95 paper parameter w3 represents the
    weight attached to the R^3_c( =y_c/z_c) factor.
*/
int 
ClusterOptions_get_w3(const ClusterOptions_ptr self);

/*!
  \methodof ClusterOptions
  \brief Retrieves the parameter w4. 

  According to the IWLS95 paper parameter w4 represents the
    weight attached to the R^4_c( =min_c/max_c) factor.
*/
int 
ClusterOptions_get_w4(const ClusterOptions_ptr self);

/*!
  \methodof ClusterOptions
  \brief  Prints all the cluster options inside the specified file.

  
*/
void ClusterOptions_print(const ClusterOptions_ptr self, FILE* file);

#endif /* __NUSMV_CORE_TRANS_BDD_CLUSTER_OPTIONS_H__ */
