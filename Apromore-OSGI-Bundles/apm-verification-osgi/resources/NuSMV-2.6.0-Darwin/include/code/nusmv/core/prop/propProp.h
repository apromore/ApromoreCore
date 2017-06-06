/* ---------------------------------------------------------------------------


  This file is part of the ``prop'' package.
  %COPYRIGHT%
  

-----------------------------------------------------------------------------*/

/*!
  \author Michele Dorigatti
  \brief Public header for package prop

  It includes all the other public
  headers of the package.
  NOTE: It is called propProp.h instead of prop.h for avoiding clashing with
  Prop.h on case insensitive file systems

*/


#ifndef __NUSMV_CORE_PROP_PROP_PROP_H__
#define __NUSMV_CORE_PROP_PROP_PROP_H__

#include "nusmv/core/utils/defs.h"
#include "nusmv/core/utils/list.h"

#include "nusmv/core/prop/PropDb.h"
#include "nusmv/core/prop/Prop.h"
#include "nusmv/core/prop/propPkg.h"
#include "nusmv/core/prop/Prop_Rewriter.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/


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
  \brief Convert a list of properties to invarspec, if possible,
  adding them to the database

  Returns a list of the indexes of the properties added to
  the database
*/
lsList Prop_convert_props_to_invar(PropDb_ptr prop_db, lsList props);

/*!
  \brief Produces a set of properties from a delimited string
  containing properties indices

  Indices are names separated by ':,; '
  A range can be specified with 'A-B' where B >= A.
  Empty set is returned in case of error.
*/
Set_t Prop_propset_from_indices(NuSMVEnv_ptr env, const char* indices);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_PROP_PROP_PROP_H__ */
