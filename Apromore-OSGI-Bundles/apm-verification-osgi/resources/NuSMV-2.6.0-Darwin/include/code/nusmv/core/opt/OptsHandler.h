/* ---------------------------------------------------------------------------


   This file is part of the ``opt'' package of NuSMV version 2.
   Copyright (C) 2009 by FBK.

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
  \author Marco Roveri, Alessandro Mariotti
  \brief Generic handler of options

  Generic handler of options. An option is uniquely
   identified in an option handler by:
   <ul>
   <li> a name (it is the string of its name).
   <li> a default value (it is the string of its name).
   <li> a value (it is the string of its name).
   </ul>
   When registering an option the user must specify two functions. The
   first is responsible of checking that the value passed while setting
   a value is a valid value for the given option. The second function
   is a function that transforms the stroed value in a value suitable
   to be used in the calling program. <br>

   For boolean options are provided special methods to register the
   option and for setting and getting a value associated to it.<br>

   For enumerative options are provided special methods to register the
   option and for setting and getting a value associated to it. An
   enumertive option is registered by providing an array of structures
   of type Opts_EnumRec. Similarly to the below declaration:<br>
   <pre>typedef enum {foo1, ...., fooN} fooenumtype;
   Opts_EnumRec foo[] = {"foo1", foo1,
   "foo2", foo2,
   ....
   "fooN", fooN};
   ....
   handler = OptsHandler_create();
   OptsHandler_register_enum_option(handler, "foooption", "foo1", foo, N);

   if (OptsHandler_get_enum_option_value(handler, "foooption") == foo2) {
   ...
   }

   ...

   switch(OptsHandler_get_enum_option_value(handler, "foooption")) {
   case foo1:
   ...
   case fooN:
   ...
   default:
   ...
   }
   </pre>
   

*/


#ifndef __NUSMV_CORE_OPT_OPTS_HANDLER_H__
#define __NUSMV_CORE_OPT_OPTS_HANDLER_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum {
  GENERIC_OPTION,
  USER_OPTION,
  INTEGER_OPTION,
  ENUM_OPTION,
  BOOL_OPTION
} Option_Type;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef enum {
  ACTION_SET,
  ACTION_RESET,
  ACTION_GET
} Trigger_Action;

/*!
  \struct _OptsHandler_Rec
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct _OptsHandler_Rec   OptsHandler_Rec;
typedef struct _OptsHandler_Rec * OptsHandler_ptr;

/*!
  \struct _Opts_EnumRec
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct _Opts_EnumRec Opts_EnumRec;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef boolean (*Opts_CheckFnType)(OptsHandler_ptr, const char *, void*);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void * (*Opts_ReturnFnType)(OptsHandler_ptr, const char *, void*);
typedef boolean (*Opts_TriggerFnType)(OptsHandler_ptr, const char *,
                                      const char*, Trigger_Action, void*);

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/
struct _Opts_EnumRec {
  char * v;
  int  e;
};


/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OPTS_TRUE_VALUE "1"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OPTS_FALSE_VALUE "0"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OPTS_VALUE_ERROR (void *)-9999

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OPTS_IS_PUBLIC true

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OPTS_DEFAULT_VALUE_TRUE true

/*!
  \brief To cast and check instances of class ModelSimplifier

  These macros must be used respectively to cast and to check
   instances of class ModelSimplifier
*/
#define OPTS_HANDLER(self)                      \
  ((OptsHandler_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OPTS_HANDLER_CHECK_INSTANCE(self)                       \
  (nusmv_assert(OPTS_HANDLER(self) != OPTS_HANDLER(NULL)))

/*!
  \brief Operates on each entry of the option handler

  Operates on each entry of the option handler. name and
   value must be declared to be char **.

  \sa Opts_GenInit Opts_Gen Opts_GenFree
*/
#define OPTS_FOREACH_OPTION(h, name, value)                             \
  for (Opts_Gen_init(h); Opts_Gen_next(h, name, value) || (Opts_Gen_deinit(h), 0); )


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof OptsHandler
  \brief Creates an empty option handler

  Creates an empty option handler. 

  \se None

  \sa OptsHandler_destroy
*/
OptsHandler_ptr OptsHandler_create(void);

/*!
  \brief Frees an option handler.

  Frees an option handler.

  \se None

  \sa OptsHandler_create
*/
void OptsHandler_destroy(OptsHandler_ptr h);

/*!
  \methodof OptsHandler
  \brief Checks if an option has already been registered.

  Checks if an option has already been
  registered. Returns true if it has been already registered false otherwise.

  \se None
*/
boolean OptsHandler_is_option_registered(OptsHandler_ptr self,
                                                const char * name);

/*!
  \methodof OptsHandler
  \brief Checks if an option has already been registered.

  Checks if an option has already been
  registered. Returns false if it has been already registered true otherwise.

  \se None
*/
boolean OptsHandler_is_option_not_registered(OptsHandler_ptr self,
                                                    const char * name);

/*!
  \methodof OptsHandler
  \brief Registers an option in an option handler.

  Registers an option in an option handler. Returns
  true if the registration of the option succeeds, false otherwise.

  \se None
*/
boolean
OptsHandler_register_option(OptsHandler_ptr self,
                            const char * name,
                            const char * def,
                            Opts_CheckFnType check,
                            Opts_ReturnFnType get,
                            boolean is_public,
                            Option_Type type,
                            void* arg);

/*!
  \methodof OptsHandler
  \brief Registers a generic option in the option handler.

  Registers an option in an option handler. Returns
  true if the registration of the option succeeds, false otherwise.

  \se None
*/
boolean
OptsHandler_register_generic_option(OptsHandler_ptr self,
                                    const char * name,
                                    const char * def,
                                    boolean is_public);

/*!
  \methodof OptsHandler
  \brief Registers a user-defined option in the option handler.

  Registers a user-defined option in the option handler.

  \se None
*/
boolean
OptsHandler_register_user_option(OptsHandler_ptr self,
                                 const char * name,
                                 const char * def);

/*!
  \brief Registers a boolean option in an option handler.

  Registers a boolean option in an option
  handler. The user is not required to provide any function to check
  and return a value. Returns true if the registration of the option succeeds,
  false otherwise.

  \se None
*/
boolean
OptsHandler_register_bool_option( OptsHandler_ptr self,
                                  const char * name,
                                  boolean value,
                                  boolean is_public);

/*!
  \methodof OptsHandler
  \brief Registers an enumerative option in an option handler.

  Registers an enumerative option in an option
  handler. The possible values are stored in an array of strings given
  in input. The user is not required to provide any function to check
  and return a value. Returns true if the registration of the option succeeds,
  false otherwise.

  \se None
*/
boolean
OptsHandler_register_enum_option(OptsHandler_ptr self,
                                 const char * name,
                                 const char * def,
                                 Opts_EnumRec pv[], int npv,
                                 boolean is_public);

/*!
  \methodof OptsHandler
  \brief Registers an integer option in an option handler.

  Registers an integer option in an option
  handler. The user is not required to provide any function to check
  and return a value. Returns true if the registration of the option succeeds,
  false otherwise.

  \se None
*/
boolean
OptsHandler_register_int_option(OptsHandler_ptr self,
                                const char * name,
                                int value,
                                boolean is_public);

/*!
  \methodof OptsHandler
  \brief Checks if the given is public or not.

  Checks if the given is public or not.
*/
boolean
OptsHandler_is_option_public(OptsHandler_ptr self,
                             const char* name);

/*!
  \methodof OptsHandler
  \brief Checks if the given option is enumerative

  Checks if the given option is enumerative
*/
boolean
OptsHandler_is_enum_option(OptsHandler_ptr self,
                           const char* name);

/*!
  \methodof OptsHandler
  \brief Checks if the given option is generic

  Checks if the given option is generic
*/
boolean
OptsHandler_is_generic_option(OptsHandler_ptr self,
                              const char* name);

/*!
  \methodof OptsHandler
  \brief Checks if the given option is user-defined

  Checks if the given option is user-defined
*/
boolean
OptsHandler_is_user_option(OptsHandler_ptr self,
                           const char* name);

/*!
  \methodof OptsHandler
  \brief Checks if the given option is boolean

  Checks if the given option is boolean
*/
boolean
OptsHandler_is_bool_option(OptsHandler_ptr self,
                           const char* name);

/*!
  \methodof OptsHandler
  \brief Checks if the given option is integer

  Checks if the given option is integer
*/
boolean
OptsHandler_is_int_option(OptsHandler_ptr self,
                          const char* name);

/*!
  \methodof OptsHandler
  \brief Unregisters an option in an option handler.

  Unregisters an option in an option handler. Returns
  true if the unregistration of the option succeeds, false otherwise.

  \se None
*/
boolean
OptsHandler_unregister_option(OptsHandler_ptr self,
                              const char * name);

/*!
  \methodof OptsHandler
  \brief Assigns the given value to a registered option.

  Assigns the given value to an option registered
  in an option handler. Returns true if the setting of the value
  succeeds, false if the option name is not registered in the option
  handler or if the value to assigns does not is of the type allowed
  for the option.
  Ownership of value is taken.
  "value" is converted to the proper representation according to the register
  option type. So, for setting a number in string format, just use this
  function, do NOT cast the integer to a string.

  \se None
*/
boolean
OptsHandler_set_option_value(OptsHandler_ptr self,
                             const char * name,
                             const char * value);

/*!
  \methodof OptsHandler
  \brief Get the string representation of option's possible values

  Get the string representation of option's possible values
  num_values stores the number of values, cannot be null
*/
void
OptsHandler_get_enum_option_values(OptsHandler_ptr self,
                                   const char * name,
                                   char *** values,
                                   int * num_values);

/*!
  \methodof OptsHandler
  \brief Get the node representation of the list of all possible
  values

  Useful for printing.
  num_values stores the number of values, cannot be null
  Returned nodelist must be freed
*/
node_ptr OptsHandler_get_enum_option_values_as_node(OptsHandler_ptr self,
                                                           NuSMVEnv_ptr env,
                                                           const char* name,
                                                           int* num_values);

/*!
  \methodof OptsHandler
  \brief Assigns the given value to a registered option.

  Assigns the given value to an option registered
  in an option handler. Returns true if the setting of the value
  succeeds, false if the option name is not registered in the option
  handler or if the value to assigns does not is of the type allowed
  for the option.

  \se None
*/
boolean
OptsHandler_set_enum_option_value(OptsHandler_ptr self,
                                  const char * name,
                                  const char * value);

/*!
  \methodof OptsHandler
  \brief Returns the value of an enum option.

  Returns the value of an enum option
  value. OPTS_VALUE_ERROR is returned if the option is not a
  registered option.

  \se None
*/
int
OptsHandler_get_enum_option_value(OptsHandler_ptr self,
                                  const char * name);

/*!
  \methodof OptsHandler
  \brief Returns the default value of an enum option.

  Returns the default value of an enum option
  value. OPTS_VALUE_ERROR is returned if the option is not a
  registered option.

  \se None
*/
int
OptsHandler_get_enum_option_default_value(OptsHandler_ptr self,
                                          const char * name);

/*!
  \methodof OptsHandler
  \brief Assigns the given value to a registered option.

  Assigns the given value to an option registered
  in an option handler. Returns true if the setting of the value
  succeeds, false if the option name is not registered in the option
  handler or if the value to assigns does not is of the type allowed
  for the option.

  \se None
*/
boolean
OptsHandler_set_bool_option_value(OptsHandler_ptr self,
                                  const char * name,
                                  boolean value);

/*!
  \methodof OptsHandler
  \brief Assigns the given value to a registered option.

  Assigns the given value to an option registered
  in an option handler. Returns true if the setting of the value
  succeeds, false if the option name is not registered in the option
  handler or if the value to assigns does not is of the type allowed
  for the option.
  If the number is in string format, just use OptsHandler_set_option_value

  \se None
*/
boolean
OptsHandler_set_int_option_value(OptsHandler_ptr self,
                                 const char * name,
                                 int value);

/*!
  \methodof OptsHandler
  \brief Assigns the default value to a registered option.

  Assigns the default value to an option registered
  in an option handler. Returns true if the setting of the value
  succeeds, false if the option name is not registered in the option
  handler.

  \se None
*/
boolean
OptsHandler_reset_option_value(OptsHandler_ptr self,
                               const char * name);

/*!
  \methodof OptsHandler
  \brief Returns the value of a registered option.

  Returns the value of an option registered
  in an option. OPTS_VALUE_ERROR is returned if the option is not a
  registered option.

  \se None
*/
void *
OptsHandler_get_option_value(OptsHandler_ptr self,
                             const char * name);

/*!
  \methodof OptsHandler
  \brief Returns the default value of a registered option.

  Returns the default value of an option registered
                      in an option. OPTS_VALUE_ERROR is returned if
                      the option is not a registered option.

  \se None
*/
void *
OptsHandler_get_option_default_value(OptsHandler_ptr self,
                                     const char * name);

/*!
  \methodof OptsHandler
  \brief Returns the value of a string option

  Returns the value of a string option.
                      Depending on the return function, the string may be freed.
                      The internal getter function duplicates the string.
                      Caller should free the string
*/
char *
OptsHandler_get_string_option_value(OptsHandler_ptr self,
                                    const char * name);

/*!
  \methodof OptsHandler
  \brief Returns the default value of a string option

  Returns the default value of a string option.
                      Depending on the return function, the string may be freed.
                      The internal getter function duplicates the string.
                      Caller should free the string
*/
char* OptsHandler_get_string_option_default_value(OptsHandler_ptr self,
                                                         const char* name);

/*!
  \methodof OptsHandler
  \brief Returns the string representation of the default value

  Returns the string representation of the default value.
                      The returned string must be freed, if not NULL.
*/
char*
OptsHandler_get_string_representation_option_default_value(OptsHandler_ptr self,
                                                           const char * name);

/*!
  \methodof OptsHandler
  \brief Returns the string representation of the value

  Returns the string representation of the value.
                      The returned string must be freed, if not NULL.
*/
char*
OptsHandler_get_string_representation_option_value(OptsHandler_ptr self,
                                                   const char * name);

/*!
  \methodof OptsHandler
  \brief Returns the value of a boolean option.

  Returns the value of a boolean option
  value. OPTS_VALUE_ERROR is returned if the option is not a
  registered option.

  \se None
*/
boolean
OptsHandler_get_bool_option_value(OptsHandler_ptr self,
                                  const char * name);

/*!
  \methodof OptsHandler
  \brief Returns the default value of a boolean option.

  Returns the default value of a boolean option
                      value. OPTS_VALUE_ERROR is returned if the
                      option is not a registered option.

  \se None
*/
boolean
OptsHandler_get_bool_option_default_value(OptsHandler_ptr self,
                                          const char * name);

/*!
  \methodof OptsHandler
  \brief Returns the value of an int option.

  Returns the value of an enum option
  value. OPTS_VALUE_ERROR is returned if the option is not a
  registered option.

  \se None
*/
int
OptsHandler_get_int_option_default_value(OptsHandler_ptr self,
                                         const char * name);

/*!
  \methodof OptsHandler
  \brief Returns the value of an int option.

  Returns the value of an int option
  value. OPTS_VALUE_ERROR is returned if the option is not a
  registered option.

  \se None
*/
int
OptsHandler_get_int_option_value(OptsHandler_ptr self,
                                 const char * name);

/*!
  \methodof OptsHandler
  \brief 

  
*/
boolean
OptsHandler_add_option_trigger(OptsHandler_ptr self, const char* name,
                               Opts_TriggerFnType trigger, void* arg);

/*!
  \methodof OptsHandler
  \brief Removes the given trigger from the given option

  Removes the given trigger from the given option
*/
boolean
OptsHandler_remove_option_trigger(OptsHandler_ptr self, const char* name,
                                  Opts_TriggerFnType trigger);

/*!
  \methodof OptsHandler
  \brief Initializes a generator for an option handler.

  Initializes a generator handler which when used
  with Opts_Gen_next() will progressively return each (name, value)
  record in the option handler.

  \se None

  \sa Opts_Gen_next Opts_Gen_deinit
*/
void Opts_Gen_init(OptsHandler_ptr self);

/*!
  \methodof OptsHandler
  \brief Gets the next pair (name, value) for an option handler.

  Given a generator created by Opts_GenInit(),
     this routine returns the next (name, value) pair in the
     generation sequence. When there are no more items in the
     generation sequence,  the routine returns 0.

  \se None

  \sa Opts_Gen_init Opts_Gen_deinit
*/
int Opts_Gen_next(OptsHandler_ptr self, char ** name, char ** value);

/*!
  \methodof OptsHandler
  \brief Frees an option generator for an option handler.

  After generating all items in a generation
  sequence, this routine must be called to reclaim the resources
  associated with the created generator.

  \se None

  \sa Opts_Gen_next Opts_Gen_init
*/
void Opts_Gen_deinit(OptsHandler_ptr self);

/*!
  \methodof OptsHandler
  \brief Prints all the options on a file

  Prints all the options stored in the option
  handler on a given file.

  \sa Opts_GenFree Opts_Gen Opts_GenInit Opts_PrintAllOptions
*/
void OptsHandler_print_all_options(OptsHandler_ptr self, FILE * fd,
                                          boolean print_private);

/*!
  \methodof OptsHandler
  \brief \todo Missing synopsis

  \todo Missing description
*/
void OptsHandler_generate_test(OptsHandler_ptr self, FILE* of,
                                      boolean gen_unset);

/*!
  \brief Copy src_opts to dst_opts

  Copy src_opts to dst_opts.

  Note that:
    - dst_opts can have a different environment from src_opts
    - triggers and args are not copied
*/
void OptsHandler_copy(OptsHandler_ptr src_opts,
                             OptsHandler_ptr dst_opts);

/**AutomaticEnd***************************************************************/

#endif /* _ */
