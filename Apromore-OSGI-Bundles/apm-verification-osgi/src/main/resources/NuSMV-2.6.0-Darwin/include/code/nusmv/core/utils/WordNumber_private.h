/* ---------------------------------------------------------------------------


  This file is part of the ``enc.utils'' package of NuSMV
  version 2.  Copyright (C) 2005 by FBK-irst.

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
  \author Andrei Tchaltsev
  \brief Private interface of the class WordNumber

  The private integeface contains the initialisation
  and deinitialisation of the class WordNumber, i.e. the memory manager
  of the class.

*/


#ifndef __NUSMV_CORE_UTILS_WORD_NUMBER_PRIVATE_H__
#define __NUSMV_CORE_UTILS_WORD_NUMBER_PRIVATE_H__

#if HAVE_CONFIG_H
#include "nusmv-config.h"
#endif

#if NUSMV_HAVE_BIGNUMBERS
#error "This file is not expected to be included when big numbers are used"
#endif

#include "nusmv/core/utils/WordNumber.h"
#include "nusmv/core/utils/ustring.h"
#include "nusmv/core/cinit/NuSMVEnv.h"


/*!
  \brief WordNumber struct.

  
*/

typedef struct WordNumber_TAG
{
  WordNumberValue value; /* Words are unsigned */
  int width;
  string_ptr parsedString;
} WordNumber;


/* ---------------------------------------------------------------------- */
/*     Private methods                                                    */
/* ---------------------------------------------------------------------- */


/*!
  \methodof WordNumber
  \brief 

  
*/

WordNumberValue
word_number_to_signed_c_value(const WordNumber_ptr self);


#endif /* __NUSMV_CORE_UTILS_WORD_NUMBER_PRIVATE_H__ */
