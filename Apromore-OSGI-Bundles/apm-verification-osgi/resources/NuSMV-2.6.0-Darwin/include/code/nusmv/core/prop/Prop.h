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
  \brief Public interface of class 'Prop'

  This file is responsible of manipulate all the
  informations associated to a given property, i.e. the kind of
  property, the property itself, its cone of influence, if the
  property is not satisfied the associated copunter-example, the
  associated FSM in different formats (flatten sexp, flatten boolean
  sexp, bdd, and BE).

*/



#ifndef __NUSMV_CORE_PROP_PROP_H__
#define __NUSMV_CORE_PROP_PROP_H__

#if HAVE_CONFIG_H
#include "nusmv-config.h"
#endif

#include "nusmv/core/fsm/FsmBuilder.h"
#include "nusmv/core/wff/ExprMgr.h"
#include "nusmv/core/set/set.h"
#include "nusmv/core/fsm/sexp/SexpFsm.h"
#include "nusmv/core/fsm/sexp/BoolSexpFsm.h"
#include "nusmv/core/fsm/bdd/BddFsm.h"
#include "nusmv/core/fsm/be/BeFsm.h"

#include "nusmv/core/utils/object.h"
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/OStream.h"


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/
/*!
  \brief The status of a property

  The status of a property, i.e. If it is checked,
  unchecked, satisifed or unsatisfied.

  \sa optional
*/

enum _Prop_Status {Prop_NoStatus, Prop_Unchecked, Prop_True, Prop_False,
                   Prop_Number, Prop_Error};

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_NOSTATUS_STRING "NoStatus"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_UNCHECKED_STRING "Unchecked"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_TRUE_STRING "True"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_FALSE_STRING "False"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_NUMBER_STRING "Number"


/*!
  \brief Enumerates the different types of a specification

  Enumerates the different types of a specification
*/

 /* warning [MD] Bad practice: downcase constants */
enum _Prop_Type {
  Prop_Prop_Type_First = 100, /* Do not touch this */
  /* ---------------------------------------------------------------------- */
  Prop_NoType,
  Prop_Ctl,
  Prop_Ltl,
  Prop_Psl,
  Prop_Invar,
  Prop_Compute,
  Prop_CompId, /* For properties names comparison */
  /* ---------------------------------------------------------------------- */
  Prop_Prop_Type_Last /* Do not touch this */
};


/*!
  \brief Format used when printing


*/

enum _PropDb_PrintFmt {
  PROPDB_PRINT_FMT_TABULAR,
  PROPDB_PRINT_FMT_DEFAULT = PROPDB_PRINT_FMT_TABULAR,
  PROPDB_PRINT_FMT_XML,
};

enum _Prop_PrintFmt {
  PROP_PRINT_FMT_FORMULA,
  PROP_PRINT_FMT_FORMULA_TRUNC,
  PROP_PRINT_FMT_INDEX,
  PROP_PRINT_FMT_NAME,
  PROP_PRINT_FMT_DEFAULT = PROP_PRINT_FMT_FORMULA
};

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_NOTYPE_STRING "NoType"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_CTL_STRING "CTL"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_LTL_STRING "LTL"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_PSL_STRING "PSL"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_INVAR_STRING "Invar"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_COMPUTE_STRING "Quantitative"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/
typedef enum _Prop_Status Prop_Status;
typedef enum _Prop_Type Prop_Type;
typedef enum _PropDb_PrintFmt PropDb_PrintFmt;
typedef enum _Prop_PrintFmt Prop_PrintFmt;

/*!
  \struct Prop
  \brief Definition of the public accessor for class Prop


*/
typedef struct Prop_TAG*  Prop_ptr;

/*!
  \brief To cast and check instances of class Prop

  These macros must be used respectively to cast and to check
  instances of class Prop
*/
#define PROP(self) \
         ((Prop_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PROP_CHECK_INSTANCE(self) \
         (nusmv_assert(PROP(self) != PROP(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/
/* Constructors, Destructors, Copiers and Cleaners ****************************/

/*!
  \methodof Prop
  \brief The Prop class constructor

  Allocate a property. If no more room is available
                      then a call to <tt>numsv_exit</tt> is
                      performed. All the fields of the prop
                      structure are initialized to either NULL or
                      the corresponding default type
                      (e.g. Prop_NoType for property type).

  \sa Prop_destroy
*/
Prop_ptr Prop_create(const NuSMVEnv_ptr env);

/*!
  \methodof Prop
  \brief Creates a property, but does not insert it within the
                      database, so the property can be used on the
                      fly.

  Creates a property structure filling only the
                      property and property type fields. The
                      property index within the db is not set.
*/
Prop_ptr Prop_create_partial(const NuSMVEnv_ptr env,
                             Expr_ptr expr, Prop_Type type);

/*!
  \brief The Prop class copier

  Note for developers: we do not take an env in input
  because the fms copy functions do not take it. In order to have copy to
  another environment we need to extend also those copiers

  \sa Prop_destroy
*/
Prop_ptr Prop_copy(Prop_ptr input);

/*!
  \methodof Prop
  \brief A constructor from a string

  Returns NULL on failure
*/
Prop_ptr Prop_create_from_string(NuSMVEnv_ptr env, char* str, Prop_Type type);

/*!
  \methodof Prop
  \brief The Prop class destructor

  Free a property. Notice that before freeing the
                      property all the elements of the property
                      that needs to be freed will be automatically
                      freed.

  \sa Prop_create
*/
void Prop_destroy(Prop_ptr self);


/* Getters and Setters ********************************************************/

/*!
  \methodof Prop
  \brief Returns the property as it has been parsed and created

  Returns the property stored in the prop. If the
  property is PSL, the result should be converted to core symbols
  before model checking (see Prop_get_expr_core or
  PslNode_convert_psl_to_core).

  \sa Prop_get_expr_core
*/
Expr_ptr Prop_get_expr(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the property, but it is converted before in
                      terms of core symbols.

  Returns the property in a form that it can be
                      handled by the system (model checking,
                      dependency finder, etc.).  This may imply a
                      conversion and a different structure of the
                      resulting formula. For example in PSL FORALLs
                      are expanded, SERE are removed, global
                      operators G and AG are simplified, etc.

                      Use this function at system-level, and
                      Prop_get_expr to get the original formula instead

  \sa Prop_get_expr
*/
Expr_ptr Prop_get_expr_core(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Derived from Prop_get_expr_core, but for PSL only
                      removes forall replicators rather than
                      converting the whole expression into LTL.

  \sa Prop_get_expr
*/
Expr_ptr Prop_get_expr_core_for_coi(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Derived from Prop_get_expr_core, it flattens the expression
  to properly remove the "CONTEXT" token if any as top level operator.

  \sa Prop_get_expr_core, Prop_get_expr

*/
Expr_ptr Prop_get_flattened_expr(const Prop_ptr self, SymbTable_ptr st);

/*!
  \methodof Prop
  \brief Returns the cone of a property

  If the cone of influence of a property has been
                      computed, this function returns it.
*/
Set_t    Prop_get_cone(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Sets the cone of a property

  Stores the cone of influence of the property
*/
void Prop_set_cone(Prop_ptr self, Set_t cone);

/*!
  \methodof Prop
  \brief Returns the property type

  Returns the property kind of the stroed
  property, i.e. CTL, LTL, ...
*/
Prop_Type Prop_get_type(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the a string associated to a property type

  Returns the string corresponding to a property type
                      for printing it. Returned string must NOT be
                      deleted
*/
const char* Prop_get_type_as_string(Prop_ptr self);

/*!
  \methodof Prop
  \brief Gets the name of a property

  Get the property name
*/
node_ptr Prop_get_name(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Gets the name of a property as a string

  Get the property name as a string, must be freed
*/
char* Prop_get_name_as_string(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Sets the name of a property

  Sets the name of a property
*/
void Prop_set_name(Prop_ptr self, const node_ptr name);

/*!
  \methodof Prop
  \brief Retrieves the Symbol Table from the property.

  Retrieves the symbol table from the property. If there is an FSM
  associated to the property, then it takes the symbol table
  associated to the FSM, otherwise it returns NULL, and it will be the
  responsibility of the caller to handle this case.  */
SymbTable_ptr Prop_get_symb_table(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the status of the property

  Returns the status of the property
*/
Prop_Status Prop_get_status(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the a string associated to a property status

  Returns the string corresponding to a property
                      status for printing it. The caller must NOT
                      free the returned string, dince it is a
                      constant.
*/
const char* Prop_get_status_as_string(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the number of the property

  For COMPUTE properties returns the number resulting
                      from the evaluation of the property.
*/
int Prop_get_number(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the number value as a string (only for compute
  types)

  Returns a number, 'Inifinite' or 'Unchecked'. The
                      returned string is dynamically created, and
                      caller must free it.
*/
char* Prop_get_number_as_string(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the trace number associated to a property

  For unsatisfied properties, the trace number of the
                      asscociated counterexample is returned. 0 is
                      returned if no trace is available
*/
int Prop_get_trace(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Sets the status of the property

  Sets the status of the property
*/
void Prop_set_status(Prop_ptr self, Prop_Status s);

/*!
  \methodof Prop
  \brief Sets the number of the property

  Sets the number resulting from the
                      evaluation of the property.
*/
void Prop_set_number(Prop_ptr self, int n);

/*!
  \methodof Prop
  \brief Sets the number of the property to INFINITE

  Sets the to INFINITE the number resulting from the
                      evaluation of the property.
*/
void Prop_set_number_infinite(Prop_ptr self);

/*!
  \methodof Prop
  \brief Sets the number of the property to UNDEFINED

  Sets the to UNDEFINED the number resulting from the
                      evaluation of the property.
*/
void Prop_set_number_undefined(Prop_ptr self);

/*!
  \methodof Prop
  \brief Sets the trace number

  Sets the trace number for an unsatisfied property.
*/
void Prop_set_trace(Prop_ptr self, int t);

/*!
  \methodof Prop
  \brief Returns the index of a property

  Returns the unique identifier of a property
*/
int Prop_get_index(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Sets the index of a property

  Sets the unique identifier of a property
*/
void Prop_set_index(Prop_ptr self, const int index);

/*!
  \methodof Prop
  \todo
*/
char* Prop_get_name_as_string(const Prop_ptr self);

/*!
  \brief Computes ground scalar sexp fsm for property \"self\"



  \se Ground sexp fsm is computed (taking COI into account if
  needed) and registered into self.
*/
SexpFsm_ptr
Prop_compute_ground_sexp_fsm(const NuSMVEnv_ptr env, const Prop_ptr self);

/*!
  \brief Computes ground bdd fsm for property \"self\"



  \se Ground bdd fsm is computed (taking COI into account if
  needed) and registered into self.
*/
BddFsm_ptr
Prop_compute_ground_bdd_fsm(const NuSMVEnv_ptr env, const Prop_ptr self);

/*!
  \brief Computes ground be fsm for property \"self\"

  Ground be fsm is computed (taking COI into account if
  needed) and registered into self.
*/
BeFsm_ptr
Prop_compute_ground_be_fsm(const NuSMVEnv_ptr env, const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the scalar FSM of a property

  Returns the scalar FSM associated to the
                      property. Self keeps the ownership of the
                      given fsm
*/
SexpFsm_ptr Prop_get_scalar_sexp_fsm(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the boolean FSM of a property

  Returns the boolean FSM associated to the
                      property. Self keeps the ownership of the
                      given fsm
*/
BoolSexpFsm_ptr Prop_get_bool_sexp_fsm(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the BDD FSM of a property

  Returns the BDD FSM associated to the property. Self
                      keeps the ownership of the given fsm
*/
BddFsm_ptr  Prop_get_bdd_fsm(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the BE FSM  of a property

  Returns the boolean BE FSM associated to the
                      property. Self keeps the ownership of the
                      given fsm
*/
BeFsm_ptr Prop_get_be_fsm(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Sets the scalar FSM of a property

  The given fsm will be duplicated, so the caller keeps
                      the ownership of fsm
*/
void Prop_set_scalar_sexp_fsm(Prop_ptr self, SexpFsm_ptr fsm);

/*!
  \methodof Prop
  \brief Sets the boolean FSM of a property

  The given fsm will be duplicated, so the caller
                      keeps the ownership of fsm
*/
void Prop_set_bool_sexp_fsm(Prop_ptr self, BoolSexpFsm_ptr fsm);

/*!
  \methodof Prop
  \brief Sets the boolean FSM in BDD of a property

  The given fsm will be duplicated, so the caller
                      keeps the ownership of fsm
*/
void Prop_set_bdd_fsm(Prop_ptr self, BddFsm_ptr fsm);

/*!
  \methodof Prop
  \brief Sets the boolean BE FSM of a property

  The given fsm will be duplicated, so the caller keeps
                      the ownership of fsm
*/
void Prop_set_be_fsm(Prop_ptr self, BeFsm_ptr fsm);

/*!
  \methodof Prop
  \brief Returns the property text, with no explicit context

  The returned string must be deleted by the caller.
*/
char* Prop_get_text(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns the context name of a property

  If the property has no explicit context, 'Main' will
                      be returned. The returned string must be
                      deleted by the caller.
*/
char* Prop_get_context_text(const Prop_ptr self);


/* COI ************************************************************************/

/*!
  \methodof Prop
  \brief Computes the COI for the given property

  Computes the COI for the given property.
                      The caller should free the returned set
*/
Set_t Prop_compute_cone(const Prop_ptr self,
                               FlatHierarchy_ptr hierarchy,
                               SymbTable_ptr symb_table);

/*!
  \brief Applies cone of influence to the given property

  The COI is applied only on the scalar FSM

  \se Internal Scalar FSM is computed
*/
void
Prop_apply_coi_for_scalar(const NuSMVEnv_ptr env, Prop_ptr self);

/*!
  \brief Applies cone of influence to the given property

  The COI is applied only for BDD-based model
                      checking.  To apply for BMC, use
                      Prop_apply_coi_for_bmc. If psl2core is false,
                      then the PSL property is only expanded to
                      remove forall, otherwise it is converted into LTL.

  \se Internal FSMs are computed
*/
void
Prop_apply_coi_for_bdd(const NuSMVEnv_ptr env, Prop_ptr self);

/*!
  \brief Applies cone of influence to the given property

  The COI is applied only for BMC-based model
                      checking.  To apply for BDD, use
                      Prop_apply_coi_for_bdd. This method creates a
                      new layer for those determinization vars that
                      derives from the booleanization of the fsm
                      deriving from the property cone. That layer
                      will be committed to the BoolEnc and BeEnc
                      encodings only, not to the BddEnc. The newly
                      created layer will be assigned to a name that
                      depends on the property number within the
                      database DbProp. If psl2core is false, then
                      the PSL property is only expanded to remove
                      forall, otherwise it is converted into LTL.

  \se Internal FSMs are computed
*/
void
Prop_apply_coi_for_bmc(const NuSMVEnv_ptr env, Prop_ptr self);

/*!
  \methodof Prop
  \brief Cleans up part of the stuff generated by
                      Prop_apply_coi_for_bmc

  Removes the layer created by Prop_apply_coi_for_bmc
                      from be_enc, bdd_enc, and bool_enc and
                      destroys layer. Fsms are assumed to be
                      destroyed upon destroying the property.

  \se Prop_apply_coi_for_bmc
*/
void Prop_destroy_coi_for_bmc(Prop_ptr self);


/* Printers *******************************************************************/

/*!
  \methodof Prop
  \brief Prints a property

  Prints a property.  PSL properties are specially
  handled.
*/
void Prop_print(Prop_ptr self, OStream_ptr file, Prop_PrintFmt fmt);

/*!
  \methodof Prop
  \brief Prints a property with info or its position and status
                      within the database

  Prints a property on the specified FILE stream. Some
                      of the information stored in the property
                      structure are printed out (e.g. property,
                      property kind, property status, ...).

                      The property is printed in the given format. Use
                      PROPDB_PRINT_FMT_DEFAULT for a default format.
*/
void Prop_print_db(Prop_ptr self, OStream_ptr file, PropDb_PrintFmt);


/* Miscellaneous **************************************************************/
SexpFsm_ptr
Prop_compute_ground_sexp_fsm(const NuSMVEnv_ptr env, const Prop_ptr self);
BddFsm_ptr
Prop_compute_ground_bdd_fsm(const NuSMVEnv_ptr env, const Prop_ptr self);
BeFsm_ptr
Prop_compute_ground_be_fsm(const NuSMVEnv_ptr env, const Prop_ptr self);

/*!
  \brief Sets the FSMs in the property from the environment

  Sets the FSMs in the property from the environment
*/
void Prop_set_environment_fsms(const NuSMVEnv_ptr env, Prop_ptr prop);

/*!
  \brief Utils function that builds a set of properties out of a
  cons list of formulae

  The set must be freed by the caller
*/
Set_t Prop_set_from_formula_list(NuSMVEnv_ptr env, node_ptr list, Prop_Type type);

/*!
  \methodof Prop
  \brief Convert, if possible, a property to an equivalent invarspec

  Convert, if possible, a property to an equivalent invarspec
*/
Prop_ptr Prop_convert_to_invar(Prop_ptr self);


/* Queries ********************************************************************/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
boolean Prop_needs_rewriting(SymbTable_ptr st,
                                    const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns true if the property is PSL property and it
  is LTL compatible


*/
boolean Prop_is_psl_ltl(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Returns true if the property is PSL property and it
  is CTL compatible


*/
boolean Prop_is_psl_obe(const Prop_ptr self);

/*!
  \methodof Prop
  \brief Verifies a given property

  Depending the property, different model checking
                      algorithms are called. The status of the
                      property is updated accordingly to the result
                      of the verification process.
*/
void Prop_verify(Prop_ptr self);

/*!
  \methodof Prop
  \brief Check if a property in the database is of a given type

  Checks if a property in the database is of a given
                      type.  If the type is correct, value 0 is
                      returned, otherwise an error message is
                      emitted and value 1 is returned.
*/
int Prop_check_type(const Prop_ptr self, Prop_Type type);


/* PropType sub-interface *****************************************************/

/*!
  \brief Returns the a string associated to a property type

  Returns the string corresponding to a property type
                      for printing it. Returned string must NOT be
                      deleted
*/
const char* PropType_to_string(const Prop_Type type);

/*!
  \brief Returns the parsing type given the property type

  Returns the parsing type given the property type.
  The returned string must NOT be freed.
*/
const char* PropType_to_parsing_string(const Prop_Type type);

/*!
  \brief


*/
short int PropType_to_node_type(const Prop_Type type);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_PROP_PROP_H__ */
