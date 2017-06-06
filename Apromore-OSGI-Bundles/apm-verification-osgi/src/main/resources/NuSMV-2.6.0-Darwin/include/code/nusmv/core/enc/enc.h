/* ---------------------------------------------------------------------------


  This file is part of the ``enc'' package of NuSMV version 2.
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
  \brief Public API for the enc package. Basically methods for
  accessing global encodings are provided here

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_ENC_ENC_H__
#define __NUSMV_CORE_ENC_ENC_H__

#include "nusmv/core/utils/utils.h"

#include "nusmv/core/enc/bool/BoolEnc.h"
#include "nusmv/core/enc/bdd/BddEnc.h"
#include "nusmv/core/enc/be/BeEnc.h"
#include "nusmv/core/utils/StreamMgr.h"


/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                    */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Structure definitions                                                     */
/*---------------------------------------------------------------------------*/
/* possible Variable Ordering Types */
typedef enum  {
  VARS_ORD_INPUTS_BEFORE,
  VARS_ORD_INPUTS_AFTER,
  VARS_ORD_TOPOLOGICAL,
  VARS_ORD_INPUTS_BEFORE_BI, /* default */
  VARS_ORD_INPUTS_AFTER_BI,
  VARS_ORD_TOPOLOGICAL_BI,
  VARS_ORD_UNKNOWN

} VarsOrdType;

/* possible BDD Static Ordering Heuristics */
typedef enum {
  BDD_STATIC_ORDER_HEURISTICS_NONE,
  BDD_STATIC_ORDER_HEURISTICS_BASIC,
  BDD_STATIC_ORDER_HEURISTICS_ERROR, /* means an error has happened*/
} BddSohEnum;

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Functions declarations                                                    */
/*---------------------------------------------------------------------------*/

/*!
  \brief Initializes the encoding package

  This function initializes only data-structures
  global to all encoding.
  To initialize particular incoding, you have to invoke corresponding
  init-functions, such as Enc_init_bool_encoding, etc.

  \sa Enc_init_bool_encoding, Enc_init_bdd_encoding,
  Enc_reinit_bdd_encoding, Enc_quit_encodings
*/
void Enc_init_encodings(NuSMVEnv_ptr env);

/*!
  \brief Initializes the boolean encoding for this session

  Call it to initialize for the current session the
                      encoding, before flattening. In the current
                      implementation, you must call this *before* the
                      flattening phase. After the flattening, you must
                      initialize the bdd encoding as well. Don't
                      forget to call Enc_quit_encodings when the
                      session ends. 

  \sa Enc_init_bdd_encoding, Enc_quit_encodings
*/
void Enc_init_bool_encoding(NuSMVEnv_ptr env);

/*!
  \brief Initializes the bdd enc for the given environment

  
*/
void Enc_init_bdd_encoding(NuSMVEnv_ptr env,
                                  const char* input_order_file_name);

/*!
  \brief Initializes the be enc for this session

  
*/
void Enc_init_be_encoding(NuSMVEnv_ptr env);

/*!
  \brief Call to destroy all encodings, when session ends

  Call to destroy encodings, when session ends.
  Enc_init_encodings had to be called before calling this function.
*/
void Enc_quit_encodings(NuSMVEnv_ptr env);

/*!
  \brief Returns the string corresponding to give parameter

  Returned string does not have to be freed
*/
const char* Enc_vars_ord_to_string(VarsOrdType);

/*!
  \brief Converts a string to the corresponding var order type.

  VARS_ORD_UNKNOWN is returned when the string does not
                      match the given string. If the streams argument
                      is not NULL, and VARS_ORD_STR_LEXICOGRAPHIC is
                      given as str argument, a warning will be printed
*/
VarsOrdType Enc_string_to_vars_ord(const char*, StreamMgr_ptr);

/*!
  \brief Returns a string of all possible values for
  vars_ord_type

  Returned string does not have to be freed
*/
const char* Enc_get_valid_vars_ord_types(void);

/*!
  \brief Returns the string corresponding to give parameter

  Returned string does not have to be freed
*/
const char* Enc_bdd_static_order_heuristics_to_string(BddSohEnum);

/*!
  \brief Converts a string to the corresponding BDD Static Order Heuristics.

  BDD_STATIC_ORDER_HEURISTICS_ERROR is returned when the
  string does not match the given string
*/
BddSohEnum Enc_string_to_bdd_static_order_heuristics(const char*);

/*!
  \brief Returns a string of all possible values for
  bdd_static_order_heuristics

  Returned string does not have to be freed
*/
const char* Enc_get_valid_bdd_static_order_heuristics(void);

/*!
  \brief Top level function for resetting the evaluation self of
  the bdd encoder

  
*/
int Enc_clean_evaluation_cache(NuSMVEnv_ptr env,
                                      BddEnc_ptr enc);

#endif /* __NUSMV_CORE_ENC_ENC_H__ */
