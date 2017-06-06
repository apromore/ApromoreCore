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
  \brief Public interface of class 'PropDb'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_PROP_PROP_DB_H__
#define __NUSMV_CORE_PROP_PROP_DB_H__

#include "nusmv/core/prop/Prop.h"

#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/list.h"
#include "nusmv/core/utils/ErrorMgr.h"


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \struct PropDb
  \brief Definition of the public accessor for class PropDb


*/
typedef struct PropDb_TAG*  PropDb_ptr;

/*!
  \brief To cast and check instances of class PropDb

  These macros must be used respectively to cast and to check
  instances of class PropDb
*/
#define PROP_DB(self) \
         ((PropDb_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_DB_CHECK_INSTANCE(self) \
         (nusmv_assert(PROP_DB(self) != PROP_DB(NULL)))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_DB_FOREACH(self, i)                \
  for (i = 0; i < PropDb_get_size(self); ++i)


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof PropDb
  \brief The PropDb class constructor

  The PropDb class constructor

  \sa PropDb_destroy
*/
PropDb_ptr PropDb_create(NuSMVEnv_ptr env);

/*!
  \methodof PropDb
  \brief The PropDb class destructor

  The PropDb class destructor

  \sa PropDb_create
*/
void PropDb_destroy(PropDb_ptr self);

/*!
  \methodof PropDb
  \brief Disposes the DB of properties

  Disposes the DB of properties
*/
void PropDb_clean(PropDb_ptr self);


/* Getters and Setters ********************************************************/
/* List of Properties --------------------------------------------------------*/

/*!
  \methodof PropDb
  \brief Return the list of properties of a given type
  Returned list to be disposed by the caller.
*/
lsList PropDb_get_props_of_type(const PropDb_ptr self,
                                const Prop_Type type);

/*!
  \methodof PropDb
  \brief Return the list of properties of a given type,
                      ordered by COI size

  Returned list must be disposed by the caller.
*/
lsList PropDb_get_ordered_props_of_type(const PropDb_ptr self,
                                        const FlatHierarchy_ptr hierarchy,
                                        const Prop_Type type);

/*!
  \methodof PropDb
  \brief Get the list of properties ordered by COI size

  Get the list of properties ordered by COI size.

                      List elements are couples made using cons: the
                      car part points to the property, while the cdr
                      part points to the COI. The list and it's
                      elements (cons nodes and COI sets) should be
                      freed by the caller

                      Note: here "cons" could be substituted by Pair
*/
NodeList_ptr
PropDb_get_ordered_properties(const PropDb_ptr self,
                              const FlatHierarchy_ptr hierarchy);

/*!
  \methodof PropDb
  \brief Get the list of properties, grouped by COI

  Get the list of properties, grouped by COI.
                      A list of couples is returned. The left part of
                      the couple is the COI (represented as a
                      Set_t). The right part of the couple is a Set
                      containing all properties with that COI.  The
                      returned list is ordered by COI size.  The list,
                      all couples and all sets should be freed by the
                      caller

  \sa PropDb_get_ordered_properties
*/
NodeList_ptr
PropDb_get_coi_grouped_properties(const PropDb_ptr self,
                                  const FlatHierarchy_ptr hierarchy);

/*!
  \methodof PropDb
  \brief Wrapper for PropDb_get_ordered_props_of_type,
  PropDb_get_props_of_type

  Call the correct PropDb_get...props_of_type depending on
  opt_use_coi_size_sorting.
  the list must be freed by the user.
*/
lsList PropDb_prepare_prop_list(const PropDb_ptr self,
                                const Prop_Type type);

/* ---------------------------------------------------------------------------*/
/* Property ------------------------------------------------------------------*/

/*!
  \methodof PropDb
  \brief Returns the last entered property in the DB

  Returns the last entered property in the DB of
                      properties.
*/
Prop_ptr PropDb_get_last(const PropDb_ptr self);

/*!
  \methodof PropDb
  \brief Returns the property indexed by index

  Returns the property whose unique identifier is
  provided in input. Returns NULL if not found.
*/
Prop_ptr PropDb_get_prop_at_index(const PropDb_ptr self,
                                  int num);

/*!
  \methodof PropDb
  \brief Produces a set of properties from a delimited string
  containing properties indices

  Indices are names separated by ':' or ','
  A range can be specified with 'A-B' where B >= A.
  This can be handy in commands.

  In case of error in input, an exception is raised.

  Returned set must be disposed by the caller
*/
Set_t PropDb_get_props_at_indices(const PropDb_ptr self,
                                  const ErrorMgr_ptr errmgr,
                                  const char* indices);

/* ---------------------------------------------------------------------------*/
/* Index ---------------------------------------------------------------------*/

/*!
  \methodof PropDb
  \brief Returns the property with the given name

  Returns the property with the given name, rapresented
                      as flattened nodes hierarchy, -1 if not found.
*/
int PropDb_get_prop_name_index(const PropDb_ptr self,
                               const node_ptr name);

/*!
  \methodof PropDb
  \brief Get a valid property index from a string

  Gets the index of a property form a string.
  If the string does not contain a valid index, an error message is emitted
  and -1 is returned.
*/
int PropDb_get_prop_index_from_string(const PropDb_ptr self,
                                      const char* idx);

/*!
  \methodof PropDb
  \brief Returns the index of the property associated to a trace.

  Returns the index of the property associated to a trace.
  -1 if no property is associated to the given trace.
*/
int PropDb_get_prop_index_from_trace_index(const PropDb_ptr self,
                                           const int trace_idx);
/* ---------------------------------------------------------------------------*/

/*!
  \methodof PropDb
  \brief Returns the size of the DB

  Returns the size (i.e. the number of entries)
  stored in the DB of properties.
*/
int PropDb_get_size(const PropDb_ptr self);

/*!
  \methodof PropDb
  \brief Given a string representing a property name,
                      returns the property index, if it exists

  Parses the given name, builds it's node_ptr
                      interpretation and looks into the PropDb if the
                      property exists.

  \sa PropDb_get_prop_name_index
*/
int PropDb_prop_parse_name(const PropDb_ptr self,
                           const char* str);

/*!
  \methodof PropDb
  \brief Sets the current print format

  When printing, the given format will be used.
  Returns the previously set format.

  \sa PropDb_get_print_fmt
*/
PropDb_PrintFmt PropDb_set_print_fmt(const PropDb_ptr self,
                                     PropDb_PrintFmt new_fmt);


/* Printing********************************************************************/

/*!
  \methodof PropDb
  \brief Prints the header of the property list

  This method has to be called before
  PropDb_print_list_footer.

  \se PropDb_print_list_footer
*/
void PropDb_print_list_header(const PropDb_ptr self,
                              OStream_ptr file);

/*!
  \methodof PropDb
  \brief Prints the footer of the property list

  This method has to be called after
  PropDb_print_list_header.

  \se PropDb_print_list_header
*/
void PropDb_print_list_footer(const PropDb_ptr self,
                              OStream_ptr file);

/*!
  \methodof PropDb
  \todo
*/
int PropDb_print_prop_at_index(const PropDb_ptr self,
                               OStream_ptr file, const int index);

/*!
  \methodof PropDb
  \brief Prints all the properties stored in the DB

  Prints on the given file stream all the property
  stored in the DB of properties.
*/
void PropDb_print_all(const PropDb_ptr self,
                      OStream_ptr file);

/*!
  \methodof PropDb
  \brief Prints all the properties stored in the DB

  Prints on the given file stream all the property
  stored in the DB of properties whose type match the requested one.
*/
void PropDb_print_all_type(const PropDb_ptr self,
                           OStream_ptr file, Prop_Type type);

/*!
  \methodof PropDb
  \brief Prints all the properties stored in the DB

  Prints on the given file stream all the property
  stored in the DB of properties whose status match the requested one.
*/
void PropDb_print_all_status(const PropDb_ptr self,
                             OStream_ptr file, Prop_Status status);

/*!
  \methodof PropDb
  \brief Prints all the properties stored in the DB

  Prints on the given file stream all the property
  stored in the DB of properties whose type and status match the
  requested ones. Prop_NoStatus and Prop_NoType serve as wildcards.
*/
void PropDb_print_all_status_type(const PropDb_ptr self,
                                  OStream_ptr file, Prop_Status status,
                                  Prop_Type type);


/* Verify**********************************************************************/

/*!
  \methodof PropDb
  \brief Checks properties

  Check the property type and the property index and calls
  the proper ProbDb_verify_* function.
*/
int
PropDb_check_property(const PropDb_ptr self,
                      const Prop_Type pt,
                      const char* formula,
                      const int prop_no);

/*!
  \methodof PropDb
  \brief Verifies all the properties in the DB

  All the properties stored in the database not
  yet verified will be verified. The properties are verified following
  the order CTL/COMPUTE/LTL/INVAR.
*/
void PropDb_verify_all(const PropDb_ptr self);

/*!
  \methodof PropDb
  \brief Verifies all properties of a given type

  The DB of properties is searched for a property
  of the given type. All the found properties are then verified
  calling the appropriate model checking algorithm. Properties already
  checked will be ignored.
*/
void PropDb_verify_all_type(const PropDb_ptr self, Prop_Type);

/*!
  \methodof PropDb
  \brief Verifies all the properties in the DB

  All the properties stored in the database not
  yet verified will be verified. The properties are verified following
  the COI size order (from smaller to bigger)
*/
void PropDb_ordered_verify_all(const PropDb_ptr self,
                                      const FlatHierarchy_ptr hierarchy);

/*!
  \methodof PropDb
  \brief Verifies all properties of a given type, ordered by COI
                      size

  The DB of properties is searched for a property
  of the given type. All the found properties are then verified
  calling the appropriate model checking algorithm. Properties already
  checked will be ignored. Properties found with the given type are checked
  in order, based on the COI size. If type is Prop_NoType, all properties
  are checked
*/
void
PropDb_ordered_verify_all_type(const PropDb_ptr self,
                               const FlatHierarchy_ptr hierarchy,
                               const Prop_Type type);

/*!
  \methodof PropDb
  \brief Verifies a given property

  The DB of properties is searched for a property
  whose unique identifier match the identifier provided and then if
  such a property exists it will be verified calling the appropriate
  model checking algorithm. If the property was checked before, then
  the property is not checked again.
*/
void PropDb_verify_prop_at_index(const PropDb_ptr self,
                                 const int index);

/*!
  \brief Wrapper for PropDb_verify_all_type,
  PropDb_ordered_verify_all_type

  Call the correct PropDb_..._verify_all_type depending on
  opt_use_coi_size_sorting.
*/
void PropDb_verify_all_type_wrapper(PropDb_ptr const self,
                                    const Prop_Type type);

/* Miscellaneous **************************************************************/

/*!
  \methodof PropDb
  \brief Add a property to the database from a string and a type

  Parses and creates a property of a given type from
  a string. If the formula is correct, it is added to the
  property database and its index is returned.
  Otherwise, -1 is returned.
  Valid types are Prop_Ctl, Prop_Ltl, Prop_Psl, Prop_Invar and Prop_Compute.
  If expr_name is not NULL, it is set as the name of the property.
*/
int PropDb_prop_parse_and_add(const PropDb_ptr self,
                              SymbTable_ptr symb_table,
                              const char* str,
                              const Prop_Type type,
                              const node_ptr expr_name);

/*!
  \methodof PropDb
  \brief Returns the currently set print format

  When printing, the currenlty set format is used.

  \sa PropDb_set_print_fmt
*/
PropDb_PrintFmt PropDb_get_print_fmt(const PropDb_ptr self);

/*!
  \methodof PropDb
  \brief Fills the DB of properties

  Given for each kind of property a list of
  respective formulae, this function is responsible to fill the DB with
  them. Returns 1 if an error occurred, 0 otherwise
*/
int
PropDb_fill(PropDb_ptr self, SymbTable_ptr symb_table,
            node_ptr, node_ptr, node_ptr,
            node_ptr, node_ptr);

/*!
  \methodof PropDb
  \brief Inserts a property in the DB of properties

  Insert a property in the DB of properties.
  If not previously set, sets the property index.
  Returns true if out of memory
*/
boolean PropDb_add(PropDb_ptr self, Prop_ptr);

/*!
  \methodof PropDb
  \brief Inserts a property in the DB of properties

  Given a formula and its type, a property is
  created and stored in the DB of properties. It returns either -1 in
  case of failure, or the index of the inserted property.

*/
int
PropDb_prop_create_and_add(PropDb_ptr self, SymbTable_ptr symb_table,
                           node_ptr, Prop_Type);

/*!
  \methodof PropDb
  \brief Shows the currently stored properties

  Shows the currently stored properties.
  If type is Prop_NoType, all properties will be printed.
  If status is Prop_NoStatus, all status will be considered.
*/
int
PropDb_show_property(const PropDb_ptr self,
                     const boolean print_props_num,
                     const PropDb_PrintFmt fmt,
                     const Prop_Type type,
                     const Prop_Status status,
                     const int prop_no,
                     FILE* outstream);

/*!
  \methodof PropDb
  \brief Checks if "prop" is registered within self


*/
boolean
PropDb_is_prop_registered(PropDb_ptr self,
                          Prop_ptr prop);


/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_PROP_PROP_DB_H__ */
