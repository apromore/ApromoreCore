/* ---------------------------------------------------------------------------


  %COPYRIGHT%

-----------------------------------------------------------------------------*/

/*!
  \author Mirco Giacobbe
  \brief Public interface of class 'BiMap'

  This class implements a bijective map, 
               namely a one-to-one correspondance

*/



#ifndef __NUSMV_CORE_UTILS_BI_MAP_H__
#define __NUSMV_CORE_UTILS_BI_MAP_H__

#if HAVE_CONFIG_H
#  include "nusmv-config.h"
#endif

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BI_MAP_FAST_ITERATOR

#include "nusmv/core/utils/defs.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/utils/assoc.h"
#include "nusmv/core/node/node.h"

/*!
  \struct BiMap
  \brief Definition of the public accessor for class BiMap

  
*/
typedef struct BiMap_TAG*  BiMap_ptr;

#ifndef BI_MAP_FAST_ITERATOR
typedef struct BiMapIter_TAG{
  st_generator gen;
  char* key_p;
  char* value_p;
  int end;
} BiMapIter;
#else
typedef struct BiMapIter_TAG{
  ListIter_ptr d_iter;
  ListIter_ptr c_iter;
} BiMapIter;
#endif

/*!
  \brief To cast and check instances of class BiMap

  These macros must be used respectively to cast and to check
  instances of class BiMap
*/
#define BI_MAP(self) \
         ((BiMap_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BI_MAP_CHECK_INSTANCE(self) \
         (nusmv_assert(BI_MAP(self) != BI_MAP(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BI_MAP_FOREACH(self, iter) \
  for (BiMap_gen_iter(self, &iter); \
       !BiMap_iter_is_end(self, &iter); \
       BiMap_iter_next(self, &iter)) \


/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof BiMap
  \brief The BiMap class constructor

  The BiMap class constructor

  \sa BiMap_destroy
*/
BiMap_ptr BiMap_create(void);

/*!
  \methodof BiMap
  \brief The BiMap class destructor

  The BiMap class destructor

  \sa BiMap_create
*/
void BiMap_destroy(BiMap_ptr self);

/*!
  \methodof BiMap
  \brief Put a new mapping

  It inserts a new domain_element - codomain_element mapping.
               It does not allow overwriting of previous elements 
               in the respective domains.
*/
void BiMap_put(BiMap_ptr self, node_ptr domain_element, node_ptr codomain_element);

/*!
  \methodof BiMap
  \brief Return the codomain value for the given domain element

  It assumes the mapping exists
*/
node_ptr BiMap_get(BiMap_ptr self, node_ptr domain_element);

/*!
  \methodof BiMap
  \brief Return the domain value that maps to the given codomain element

  It assumes the mapping exists
*/
node_ptr BiMap_inverse_get(BiMap_ptr self, node_ptr codomain_element);

/*!
  \methodof BiMap
  \brief Returns true if the given domain element is mapped to any value,
               false otherwise.

  
*/
boolean BiMap_domain_contains(BiMap_ptr self, node_ptr domain_element);

/*!
  \methodof BiMap
  \brief Returns true if the given codomain element is mapped from any value,
               false otherwise.

  
*/
boolean BiMap_codomain_contains(BiMap_ptr self, node_ptr codomain_element);

/*!
  \methodof BiMap
  \brief Returns the number of element to element mappings.

  
*/
unsigned BiMap_size(BiMap_ptr self);

/*!
  \methodof BiMap
  \brief BiMap.is_empty

  It returns true iff the map does not contain any element
*/
boolean BiMap_is_empty(BiMap_ptr self);

/*!
  \methodof BiMap
  \brief 

  
*/
void BiMap_clear(BiMap_ptr self);

/* iterator */

/*!
  \methodof BiMap
  \brief Generate a new iter

  
*/
void BiMap_gen_iter(BiMap_ptr self, BiMapIter* iter);

/*!
  \methodof BiMap
  \brief Returns true if the iteration is ended

  
*/
boolean BiMap_iter_is_end(BiMap_ptr self, BiMapIter* iter);

/*!
  \methodof BiMap
  \brief Sets the iterator to the next element

  
*/
void BiMap_iter_next(BiMap_ptr self, BiMapIter* iter);

/*!
  \methodof BiMap
  \brief Returns the number of element to element mappings.

  
*/
node_ptr BiMap_iter_get_domain_element(BiMap_ptr self, 
                                          BiMapIter* iter);

/*!
  \methodof BiMap
  \brief Returns the number of element to element mappings.

  
*/
node_ptr BiMap_iter_get_codomain_element(BiMap_ptr self, 
                                            BiMapIter* iter);

/* big getters */

/*!
  \methodof BiMap
  \brief Returns the domain as a list

  The order is preserved according to the codomain_as_list.
               The returned list owns to the BiMap and should not be modified.
               Modifications can compromise the consistency of the map.

  \sa codomain
*/
NodeList_ptr BiMap_domain(BiMap_ptr self);

/*!
  \methodof BiMap
  \brief Returns the codomain as a list

  The order is preserved according to the domain_as_list.
               The returned list owns to the BiMap and should not be modified.
               Modifications can compromise the consistency of the map.

  \sa domain
*/
NodeList_ptr BiMap_codomain(BiMap_ptr self);

/*!
  \methodof BiMap
  \brief Returns the map as hash

  The returned hash owns to the BiMap and should not be modified.
               Modifications can compromise the consistency of the map.

  \sa inverse_map
*/
hash_ptr BiMap_map(BiMap_ptr self);

/*!
  \methodof BiMap
  \brief Returns the inverse map as hash

  The returned hash owns to the BiMap and should not be modified.
               Modifications can compromise the consistency of the map.

  \sa map
*/
hash_ptr BiMap_inverse_map(BiMap_ptr self);

#endif /* __NUSMV_CORE_UTILS_BI_MAP_H__ */
