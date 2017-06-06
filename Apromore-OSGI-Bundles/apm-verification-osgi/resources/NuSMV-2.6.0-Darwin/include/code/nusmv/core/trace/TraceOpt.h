/* ---------------------------------------------------------------------------


  This file is part of the ``trace'' package of NuSMV version 2.
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
  \author Alessandro Mariotti
  \brief Public interface of class 'TraceOpt'

  \todo: Missing description

*/

#ifndef __NUSMV_CORE_TRACE_TRACE_OPT_H__
#define __NUSMV_CORE_TRACE_TRACE_OPT_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/opt/opt.h"
#include "nusmv/core/utils/StreamMgr.h"
#include "nusmv/core/utils/OStream.h"

/*!
  \struct TraceOpt
  \brief Definition of the public accessor for class TraceOpt

  
*/
typedef struct TraceOpt_TAG*  TraceOpt_ptr;

/*!
  \brief To cast and check instances of class TraceOpt

  These macros must be used respectively to cast and to check
  instances of class TraceOpt
*/
#define TRACE_OPT(self) \
         ((TraceOpt_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRACE_OPT_CHECK_INSTANCE(self) \
         (nusmv_assert(TRACE_OPT(self) != TRACE_OPT(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof TraceOpt
  \brief The TraceOpt class constructor

  The TraceOpt class constructor

  \sa TraceOpt_destroy
*/
TraceOpt_ptr TraceOpt_create(StreamMgr_ptr streams);

/*!
  \methodof TraceOpt
  \brief The TraceOpt class constructor

  The TraceOpt class constructor

  \sa TraceOpt_destroy
*/
TraceOpt_ptr TraceOpt_create_from_env(const NuSMVEnv_ptr env);

/*!
  \methodof TraceOpt
  \brief Updates trace options struct with current values in env

  Updates trace options struct with current values in env

  \sa TraceOpt_destroy
*/
void TraceOpt_update_from_env(TraceOpt_ptr self,
                                     const NuSMVEnv_ptr env);

/*!
  \methodof TraceOpt
  \brief The TraceOpt class destructor

  The TraceOpt class destructor

  \sa TraceOpt_create
*/
void TraceOpt_destroy(TraceOpt_ptr self);

/*!
  \methodof TraceOpt
  \brief The TraceOpt obfuscate field getter

  The TraceOpt obfuscate field getter

  \sa TraceOpt_set_obfuscate
*/
boolean TraceOpt_obfuscate(TraceOpt_ptr self);

/*!
  \methodof TraceOpt
  \brief The TraceOpt obfuscate field setter

  The TraceOpt obfuscate field setter

  \sa TraceOpt_obfuscate
*/
void TraceOpt_set_obfuscate(TraceOpt_ptr self, boolean obfuscate);

/*!
  \methodof TraceOpt
  \brief The TraceOpt show_defines field getter

  The TraceOpt show_defines field getter

  \sa TraceOpt_set_show_defines
*/
boolean TraceOpt_show_defines(TraceOpt_ptr self);

/*!
  \methodof TraceOpt
  \brief The TraceOpt show_defines field setter

  The TraceOpt show_defines field setter

  \sa TraceOpt_show_defines
*/
void TraceOpt_set_show_defines(TraceOpt_ptr self,
                                      boolean show_defines);

/*!
  \methodof TraceOpt
  \brief The TraceOpt show_defines_with_next field getter

  The TraceOpt show_defines_with_next field getter

  \sa TraceOpt_set_show_defines_with_next
*/
boolean TraceOpt_show_defines_with_next(TraceOpt_ptr self);

/*!
  \methodof TraceOpt
  \brief The TraceOpt show_defines_with_next field setter

  The TraceOpt show_defines_with_next field setter

  \sa TraceOpt_show_defines_with_next
*/
void TraceOpt_set_show_defines_with_next(TraceOpt_ptr self,
                                                boolean show_next);

/*!
  \methodof TraceOpt
  \brief The TraceOpt hiding_prefix field getter

  The TraceOpt hiding_prefix field getter

  \sa TraceOpt_set_hiding_prefix
*/
const char* TraceOpt_hiding_prefix(TraceOpt_ptr self);

/*!
  \methodof TraceOpt
  \brief The TraceOpt hiding_prefix field setter

  The TraceOpt hiding_prefix field setter

  \sa TraceOpt_hiding_prefix
*/
void TraceOpt_set_hiding_prefix(TraceOpt_ptr self,
                                       const char* hiding_prefix);

#if NUSMV_HAVE_REGEX_H

/*!
  \methodof TraceOpt
  \brief The TraceOpt regexp field getter

  The TraceOpt regexp field getter

  \sa TraceOpt_regexp
*/
regex_t* TraceOpt_regexp(TraceOpt_ptr self);
#endif

/*!
  \methodof TraceOpt
  \brief The TraceOpt from_here field getter

  The TraceOpt from_here field getter

  \sa TraceOpt_set_from_here
*/
unsigned TraceOpt_from_here(TraceOpt_ptr self);

/*!
  \methodof TraceOpt
  \brief The TraceOpt from_here field setter

  The TraceOpt from_here field setter

  \sa TraceOpt_from_here
*/
void TraceOpt_set_from_here(TraceOpt_ptr self, unsigned index);

/*!
  \methodof TraceOpt
  \brief The TraceOpt to_here field getter

  The TraceOpt to_here field getter

  \sa TraceOpt_set_to_here
*/
unsigned TraceOpt_to_here(TraceOpt_ptr self);

/*!
  \methodof TraceOpt
  \brief The TraceOpt to_here field setter

  The TraceOpt to_here field setter

  \sa TraceOpt_to_here
*/
void TraceOpt_set_to_here(TraceOpt_ptr self, unsigned index);

/*!
  \methodof TraceOpt
  \brief The TraceOpt output_stream field getter

  The TraceOpt output_stream field getter

  \sa TraceOpt_set_output_stream
*/
OStream_ptr TraceOpt_output_stream(TraceOpt_ptr self);

/*!
  \methodof TraceOpt
  \brief The TraceOpt output_stream field setter

  The TraceOpt output_stream field setter

  \sa TraceOpt_output_stream
*/
void TraceOpt_set_output_stream(TraceOpt_ptr self, FILE* out);

/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_TRACE_TRACE_OPT_H__ */
