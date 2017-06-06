/* ---------------------------------------------------------------------------


  This file is part of the ``wff.lr'' package of NuSMV version 2.
  Copyright (C) 2013 by FBK-irst.

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
  \author Michele Dorigatti
  \brief Public interface of class 'LogicRecognizerCore'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_WFF_LR_LOGIC_RECOGNIZER_CORE_H__
#define __NUSMV_CORE_WFF_LR_LOGIC_RECOGNIZER_CORE_H__


#include "nusmv/core/wff/lr/LogicRecognizerBase.h"
#include "nusmv/core/utils/defs.h"

/*!
  \struct LogicRecognizerCore
  \brief Definition of the public accessor for class LogicRecognizerCore

  
*/
typedef struct LogicRecognizerCore_TAG*  LogicRecognizerCore_ptr;

/*!
  \brief To cast and check instances of class LogicRecognizerCore

  These macros must be used respectively to cast and to check
  instances of class LogicRecognizerCore
*/
#define LOGIC_RECOGNIZER_CORE(self) \
         ((LogicRecognizerCore_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define LOGIC_RECOGNIZER_CORE_CHECK_INSTANCE(self) \
         (nusmv_assert(LOGIC_RECOGNIZER_CORE(self) != LOGIC_RECOGNIZER_CORE(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof LogicRecognizerCore
  \brief The LogicRecognizerCore class constructor

  The LogicRecognizerCore class constructor

  \sa LogicRecognizerCore_destroy
*/
LogicRecognizerCore_ptr LogicRecognizerCore_create(NuSMVEnv_ptr env);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_WFF_LR_LOGIC_RECOGNIZER_CORE_H__ */
