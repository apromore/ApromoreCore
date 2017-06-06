/* ---------------------------------------------------------------------------


  This file is part of the ``compile'' package of NuSMV version 2.
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
  \author Andrei Tchaltsev
  \brief Public interface for a predicate-extractor class

  

  The purpose of PredicateExtractor class is to extract predicates and
  clusters from expressions. This class is similar to
  PredicateNormaliser but does not normalize the whole input expressions,
  just create predicates and cluster.

  Thus if normalization of the input expression is not required it is
  more efficient to use this class.

  A few definitions:

  predicate -- is a boolean expression which have only scalar (not
    boolean) subexpressions. See PredicateNormaliser.h for more info about
    predicate normalization.

  cluster -- is a set of variables met in one predicates. If a
    variable is met in 2 different predicates then their clusters are
    united in one.  Implicitly, clusters divide predicates in groups,
    i.e. every group is a set of predicates that caused this cluster.

  Note that from the definitions both clusters and predicates can be
  only over scalar (not boolean) variables.

  This class allows computation of only predicate or both predicates
  and cluster.

  Initially, I ([AT]) tried to implement an option to compute clusters
  only without predicates but this did not work. The problem is that
  1) it is necessary to memoize the results, 2) clusters may disappear
  during computation (i.e. be merged with others). Because of 1) it is
  necessary to hash expr->clusters-in-it. Because of 2) it is
  necessary to hash cluster->expr-where-is-came-from and then any
  merge of clusters may require to update huge number of elements in
  the both above hashes.
  Right now a hash expr->predicate-subparts-in-it is created. This
  allows to get clusters through getting dependencies. Any other
  solution I through of was of about the same efficiency. Thus I
  decided to use the most straightforward one.



  This is a stand-alone class. This class needs only a type checker --
  to get the type of input expression and type check the generated
  (returned) expressions.  

*/



#ifndef __NUSMV_CORE_COMPILE_PREDICATE_EXTRACTOR_H__
#define __NUSMV_CORE_COMPILE_PREDICATE_EXTRACTOR_H__

#include "nusmv/core/compile/symb_table/SymbTable.h"
#include "nusmv/core/compile/FlatHierarchy.h" /* for FlatHierarchy_ptr */
#include "nusmv/core/set/set.h"
#include "nusmv/core/utils/utils.h"


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct PredicateExtractor
  \brief Preicate Extractor class

  
*/
typedef struct PredicateExtractor_TAG* PredicateExtractor_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PREDICATE_EXTRACTOR(x) \
         ((PredicateExtractor_ptr) x)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PREDICATE_EXTRACTOR_CHECK_INSTANCE(x) \
         ( nusmv_assert(PREDICATE_EXTRACTOR(x) != PREDICATE_EXTRACTOR(NULL)) )


/* ---------------------------------------------------------------------- */
/* Public interface                                                       */
/* ---------------------------------------------------------------------- */

/*!
  \methodof PredicateExtractor
  \brief The constructor creates a predicate-extractor

  See PredicateExtractor.h for more info on
   predicates and clusters.  The parameter 'checker' is a type checker
   used during predicate extraction and subsequent type checking of
   generated expressions.

   Parameter use_approx can be used to make the extractor
   give up when dealing with too-large expressions. This is
   currently used by the heuristics which extract the variable
   ordering out of the fsm.

   NOTE that the type checker remembers the type of checked
   expressions (free or reuse nodes with care).
*/
PredicateExtractor_ptr
PredicateExtractor_create(SymbTable_ptr st, boolean use_approx);

/*!
  \methodof PredicateExtractor
  \brief Class PredicateExtractor destructor

  
*/
void  PredicateExtractor_destroy(PredicateExtractor_ptr self);

/*!
  \methodof PredicateExtractor
  \brief The function computes and collects
   the predicates of a given expression

  
   See PredicateExtractor.h for more info on predicates and clusters.

   Note: that normalization of the input expression is not done.  Only
   predicates are computed (the lesser things are done the lesser
   time/memory is spent). See class PredicateNormaliser if
   predicate-normalized expressions are required.

   To additionally get/compute clusters
   PredicateExtractor_get_all_clusters can be used.

   Input expressions may/may not be expanded/normalized/flattened,
   whereas the collected predicates are flattened, expanded and
   created with find_node, in particular all identifiers fully
   resolved.

   WARNING: memoization is done. Providing the same expression a second
   times does not produce any additional predicate.

   Collected clusters/predicates are stored internally and can be
   obtained with PredicateExtractor_get_all_preds and
   PredicateExtractor_get_all_clusters.  
*/
void
PredicateExtractor_compute_preds(PredicateExtractor_ptr self,
                                 node_ptr expr);

/*!
  \methodof PredicateExtractor
  \brief This function applies PredicateExtractor_compute_preds
   to every element of an hierarchy

  Note that symbol table in self has to correspond to
   the hierarchy, i.e. contains all the required symbols
*/
void
PredicateExtractor_compute_preds_from_hierarchy(PredicateExtractor_ptr self,
                                                FlatHierarchy_ptr fh);

/*!
  \methodof PredicateExtractor
  \brief Returns the set of predicates computed so far

  Predicates are fully expanded and resolved expressions
   created with find_node, i.e. no freeing or modifications are allowed.
   Returned Set_t belongs to self.
*/
Set_t
PredicateExtractor_get_all_preds(const PredicateExtractor_ptr self);

/*!
  \methodof PredicateExtractor
  \brief Returns the set of clusters for all so far collected
   predicates

  This function computes and returns clusters for all so far
   computed predicates.

   Returned result is Set_t of Set_t of fully resolved variables.
   Everything returned belongs to self.

   Note that this function perform computation and may take some time
   (though intermediate results are remembered between calls).

   It is possible to get a group of predicates responsible for a given
   cluster with PredicateExtractor_get_preds_of_a_cluster.

   NOTE: subsequent call of PredicateExtractor_compute_preds makes any
   data returned by this function invalid.
*/
Set_t
PredicateExtractor_get_all_clusters(const PredicateExtractor_ptr self);

/*!
  \methodof PredicateExtractor
  \brief Given a fully resolved var name the function
   returns a cluster the variable belongs to

  If clusters were not computed before this function
   triggers the cluster computation.

   Returned result is Set_t of fully resolved variables.
   Everything returned belongs to self.

   If a var was not met in any of predicates then NULL is
   returned. (This is always so for boolean vars since boolean vars
   cannot be in predicates).

   NOTE: subsequent call of PredicateExtractor_compute_preds makes any
   data returned by this function invalid.
*/
Set_t
PredicateExtractor_get_var_cluster(const PredicateExtractor_ptr self,
                                  node_ptr var);

/*!
  \methodof PredicateExtractor
  \brief Returns a set of predicates responsible for a given cluster

  Given a cluster (Set_t of vars) returned by
   PredicateExtractor_get_all_clusters this function
   returns a set of predicates which caused the given cluster.

   Returned result is not-empty Set_t of fully expanded/resolved expressions
   and belongs to self.

   NOTE: subsequent call of PredicateExtractor_compute_preds makes any
   data returned by this function or
   PredicateExtractor_get_all_clusters invalid.
*/
Set_t PredicateExtractor_get_preds_of_a_cluster(const PredicateExtractor_ptr self,
Set_t cluster);

/*!
  \methodof PredicateExtractor
  \brief The function prints out the predicates collected so far
   and clusters computed.

  Options printPredicates and printClusters
   control what should be printed.
   At least one of them has to be set up.

   If only predicates are printed, then they are printed in the order
   they were obtained.

   Otherwise, clusters are printed and if additionally printPredicates
   is up then the after every cluster its predicates are printed.

   Note that if clusters were not computed so far but asked to be
   printed, they will be computed.
   
*/
void
PredicateExtractor_print(const PredicateExtractor_ptr self,
                         FILE* stream,
                         boolean printPredicates,
                         boolean printClusters);

#endif /* __NUSMV_CORE_COMPILE_PREDICATE_EXTRACTOR_H__ */
