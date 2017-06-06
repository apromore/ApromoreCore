/* ---------------------------------------------------------------------------


  This file is part of the ``parser.prob'' package of NuSMV version 2.
  Copyright (C) 2008 by FBK-irst.

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
  \brief  The header file of ParserProb class.

  \todo: Missing description

*/



#ifndef __NUSMV_ADDONS_CORE_COMPASS_PARSER_PROB_PARSER_PROB_H__
#define __NUSMV_ADDONS_CORE_COMPASS_PARSER_PROB_PARSER_PROB_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*!
  \struct ParserProb
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct ParserProb_TAG* ParserProb_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PARSER_PROB(x) \
        ((ParserProb_ptr) (x))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PARSER_PROB_CHECK_INSTANCE(x) \
        (nusmv_assert(PARSER_PROB(x) != PARSER_PROB(NULL)))

/*!
  \methodof ParserProb
  \brief 

  
*/
ParserProb_ptr ParserProb_create(const NuSMVEnv_ptr env);

/*!
  \methodof ParserProb
  \brief 

  
*/
void ParserProb_destroy(ParserProb_ptr self);

/*!
  \methodof ParserProb
  \brief 

  
*/
void ParserProb_parse_from_file(ParserProb_ptr self, FILE* f);

/*!
  \methodof ParserProb
  \brief 

  
*/
void
ParserProb_parse_from_string(ParserProb_ptr self, const char* str);

/*!
  \methodof ParserProb
  \brief Returns the list of prob read by the parser

  Returned list is owned by self, and should not be
  changed or destroyed
*/
NodeList_ptr ParserProb_get_prob_list(const ParserProb_ptr self);

/*!
  \methodof ParserProb
  \brief 

  
*/
void ParserProb_reset(ParserProb_ptr self);


#endif /* __NUSMV_ADDONS_CORE_COMPASS_PARSER_PROB_PARSER_PROB_H__ */
