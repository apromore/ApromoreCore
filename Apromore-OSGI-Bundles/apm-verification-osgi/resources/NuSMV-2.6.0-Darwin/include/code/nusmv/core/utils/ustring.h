/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2.
  Copyright (C) 1998-2001 by CMU and FBK-irst.

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
  \author Marco Roveri
  \brief Routines to handle with strings.

  Routines to handle with strings, in order to maintain
  an unique instance of each string.

*/

#ifndef __NUSMV_CORE_UTILS_USTRING_H__
#define __NUSMV_CORE_UTILS_USTRING_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/UStringMgr.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define str_get_text(_s_) _s_->text

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Creates an  UStringMgr instance within the given
                      environment.

  Creates an  UStringMgr instance within the given
                      environment.

                      Environment requisites:
                      - No instances registered with key ENV_STRING_MGR
*/
void init_string(NuSMVEnv_ptr env);

/*!
  \brief Destroys the  UStringMgr instance within the given
                      environment.

  Destroys the  UStringMgr instance within the given
                      environment.

                      Environment requisites:
                      - An instance of  UStringMgr registered with key
                        ENV_STRING_MGR
*/
void quit_string(NuSMVEnv_ptr env);

#endif /* __NUSMV_CORE_UTILS_USTRING_H__ */
