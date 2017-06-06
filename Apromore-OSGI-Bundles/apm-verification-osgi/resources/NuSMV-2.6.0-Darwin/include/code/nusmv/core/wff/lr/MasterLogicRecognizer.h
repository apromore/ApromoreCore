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
  \brief Public interface of class 'MasterLogicRecognizer'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_WFF_LR_MASTER_LOGIC_RECOGNIZER_H__
#define __NUSMV_CORE_WFF_LR_MASTER_LOGIC_RECOGNIZER_H__


#include "nusmv/core/node/MasterNodeWalker.h"
#include "nusmv/core/utils/defs.h"

/*!
  \brief 

  We exploit the equivalance of the NULL pointer and 0
*/

typedef enum LogicType_TAG {
  EXP_FIRST = -1,
  EXP_NONE = 0,
  EXP_SIMPLE,
  EXP_NEXT,
  EXP_LTL,
  EXP_CTL,
  EXP_ERROR,
  EXP_LAST
} LogicType;

/*!
  \brief 

  
*/
#define LOGIC_RECOGNIZED_CHECK_INSTANCE(self) \
  nusmv_assert(self > EXP_FIRST && self < EXP_LAST);

/*!
  \brief 

  
*/
#define LOGIC_RECOGNIZED_ASSERT_VALID(self) \
  nusmv_assert(self > EXP_FIRST && self < EXP_ERROR);

/*!
  \brief 

  
*/
#define LOGIC_RECOGNIZED_ASSERT_HAS_VALUE(self) \
  nusmv_assert(self > EXP_NONE && self < EXP_ERROR);

/*!
  \struct MasterLogicRecognizer
  \brief Definition of the public accessor for class MasterLogicRecognizer

  
*/
typedef struct MasterLogicRecognizer_TAG*  MasterLogicRecognizer_ptr;

/*!
  \brief To cast and check instances of class MasterLogicRecognizer

  These macros must be used respectively to cast and to check
  instances of class MasterLogicRecognizer
*/
#define MASTER_LOGIC_RECOGNIZER(self) \
         ((MasterLogicRecognizer_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define MASTER_LOGIC_RECOGNIZER_CHECK_INSTANCE(self) \
         (nusmv_assert(MASTER_LOGIC_RECOGNIZER(self) != MASTER_LOGIC_RECOGNIZER(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof MasterLogicRecognizer
  \brief The MasterLogicRecognizer class constructor

  The MasterLogicRecognizer class constructor

  \sa MasterLogicRecognizer_destroy
*/
MasterLogicRecognizer_ptr MasterLogicRecognizer_create(NuSMVEnv_ptr env);

/*!
  \methodof MasterLogicRecognizer
  \brief Create a MLR and register to it all the default recognizers

  
*/
MasterLogicRecognizer_ptr
MasterLogicRecognizer_create_with_default_recognizers(NuSMVEnv_ptr env);

/*!
  \methodof MasterLogicRecognizer
  \brief The MasterLogicRecognizer class destructor

  The MasterLogicRecognizer class destructor

  \sa MasterLogicRecognizer_create
*/
void MasterLogicRecognizer_destroy(MasterLogicRecognizer_ptr self);

/*!
  \methodof MasterLogicRecognizer
  \todo
*/
LogicType MasterLogicRecognizer_recognize(MasterLogicRecognizer_ptr self,
                                                node_ptr expression,
                                                node_ptr context);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_WFF_LR_MASTER_LOGIC_RECOGNIZER_H__ */
