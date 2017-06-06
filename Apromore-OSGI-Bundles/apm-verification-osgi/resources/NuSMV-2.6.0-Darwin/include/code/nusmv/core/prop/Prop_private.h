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
  \brief Private and protected interface of class 'Prop'

  This file can be included only by derived and friend classes

*/


#ifndef __NUSMV_CORE_PROP_PROP_PRIVATE_H__
#define __NUSMV_CORE_PROP_PROP_PRIVATE_H__


#if HAVE_CONFIG_H
#include "nusmv-config.h"
#endif

#include "nusmv/core/prop/Prop.h"

#include "nusmv/core/utils/EnvObject.h"
#include "nusmv/core/utils/EnvObject_private.h"
#include "nusmv/core/utils/utils.h"


/*!
  \brief Prop class definition derived from
               class Object

  This structure contains informations about a given
  property:<br>
  <dl>
  <dt><code>index</code>
     <dd>is the progressive number identifying the specification.</dd>
  <dt><code>prop</code>
      <dd>is the specification formula (s-expression).
  <dt><code>type</code>
      <dd>is the type of the specification (CTL, LTL, INVAR, COMPUTE).
  <dt><code>cone</code>
      <dd>is the cone of influence of the formula.
  <dt><code>status</code>
      <dd>is the actual checking status of the specification.
  <dt><code>number</code>
      <dd>Result of a COMPUTE property.
  <dt><code>trace</code>
      <dd>is the index of the counterexample produced when the
          formula is found to be false, otherwise is zero.
  <dt><code>scalar_fsm</code>
      <dd>The FSM associated to the property in scalar format.
  <dt><code>bool_fsm</code>
      <dd>The FSM associated to the property in boolean format.
  <dt><code>bdd_fsm</code>
      <dd>The FSM associated to the property in BDD format.
  <dt><code>be_fsm</code>
      <dd>The FSM associated to the property in BE format.
  </dl>

  \sa Base class Object
*/


/* Those are the types of the virtual methods. They can be used for
   type casts in subclasses. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef Expr_ptr (*Prop_get_expr_method)(const Prop_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef const char* (*Prop_get_type_as_string_method)(const Prop_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*Prop_print_method)(const Prop_ptr, OStream_ptr);
typedef void (*Prop_print_db_method)(const Prop_ptr, OStream_ptr);
typedef void (*Prop_verify_method)(Prop_ptr);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef Prop_ptr (*Prop_convert_to_invar_method)(Prop_ptr);

typedef void (*Prop_set_environment_fsms_method)(const NuSMVEnv_ptr env, Prop_ptr);

/* The class itself. */
typedef struct Prop_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(EnvObject);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */
  unsigned int index;  /* Progressive number */
  Expr_ptr     prop;   /* property formula (s-expression) */

  /* AM -> Support for property name */
  node_ptr name;

  Set_t        cone;   /* The cone of influence */
  Prop_Type    type;   /* type of specification */
  Prop_Status  status; /* verification status */
  int          number; /* The result of a quantitative spec */
  int          trace;  /* the counterexample number (if any) */

  FsmBuilder_ptr fsm_mgr;  /* Used to produce FSMs from cone */

  SexpFsm_ptr     scalar_fsm; /* the scalar FSM */
  BoolSexpFsm_ptr bool_fsm;   /* The scalar FSM converted in Boolean */
  BddFsm_ptr      bdd_fsm;    /* The BDD FSM */
  BeFsm_ptr       be_fsm;     /* The BE FSM */

  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */
  /* Expr_ptr (*)(const Prop_ptr) */
  Prop_get_expr_method get_expr;
  /* const char* (*)(const Prop_ptr) */
  Prop_get_type_as_string_method get_type_as_string;
  /* void (*)(const Prop_ptr, FILE*) */
  Prop_print_method print;
  /* void (*)(const Prop_ptr, FILE*) */
  Prop_print_method print_truncated;
  /* void (*)(const Prop_ptr, FILE*) */
  Prop_print_db_method print_db_tabular;
  /* void (*)(const Prop_ptr, FILE*) */
  Prop_print_db_method print_db_xml;
  /* void (*)(Prop_ptr) */
  Prop_verify_method verify;
  Prop_convert_to_invar_method convert_to_invar;

  /* void (*)(const NuSMVEnv_ptr env, Prop_ptr) */
  Prop_set_environment_fsms_method set_environment_fsms;

} Prop;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only        */
/* ---------------------------------------------------------------------- */

/*!
  \methodof Prop
  \brief The Prop class private initializer

  The Prop class private initializer

  \sa Prop_create
*/
void prop_init(Prop_ptr self, const NuSMVEnv_ptr env);

/*!
  \methodof Prop
  \brief The Prop class private deinitializer

  The Prop class private deinitializer

  \sa Prop_destroy
*/
void prop_deinit(Prop_ptr self);

/*!
  \methodof Prop
  \todo
*/
Expr_ptr prop_get_expr(const Prop_ptr self);
/*!
  \methodof Prop
  \todo
*/
const char* prop_get_type_as_string(const Prop_ptr self);

/*!
  \methodof Prop
  \todo
*/
void prop_print(const Prop_ptr self, OStream_ptr file);
/*!
  \methodof Prop
  \todo
*/
void prop_print_truncated(const Prop_ptr self, OStream_ptr file);

/*!
  \methodof Prop
  \todo
*/
void prop_print_db_tabular(const Prop_ptr self, OStream_ptr file);
/*!
  \methodof Prop
  \todo
*/
void prop_print_db_xml(const Prop_ptr self, OStream_ptr file);

/*!
  \methodof Prop
  \todo
*/
void prop_verify(Prop_ptr self);

/*!
  \methodof Prop
  \todo
*/
void prop_set_scalar_sexp_fsm(Prop_ptr self, SexpFsm_ptr fsm,
                              const boolean duplicate);
/*!
  \methodof Prop
  \todo
*/
void prop_set_bool_sexp_fsm(Prop_ptr self, BoolSexpFsm_ptr fsm,
                            const boolean duplicate);
/*!
  \methodof Prop
  \todo
*/
void prop_set_bdd_fsm(Prop_ptr self, BddFsm_ptr fsm,
                      const boolean duplicate);
/*!
  \methodof Prop
  \todo
*/
void prop_set_be_fsm(Prop_ptr self, BeFsm_ptr fsm,
                     const boolean duplicate);

/*!
  \brief Sets the Prop fsms from the given environment

  Sets the Prop fsms from the given environment
*/
void prop_set_environment_fsms(const NuSMVEnv_ptr env, Prop_ptr prop);

/*!
  \methodof Prop
  \brief Convert self to an invar, if possible

  Convert self to an invar, if possible
*/
Prop_ptr prop_convert_to_invar(Prop_ptr self);


#endif /* __NUSMV_CORE_PROP_PROP_PRIVATE_H__ */
