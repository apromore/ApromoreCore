/* ---------------------------------------------------------------------------


  This file is part of the ``parser.ord'' package of NuSMV version 2. 
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
  \brief  The header file of ParserOrd class.

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_PARSER_ORD_PARSER_ORD_H__
#define __NUSMV_CORE_PARSER_ORD_PARSER_ORD_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/NodeList.h"

/*!
  \struct ParserOrd
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct ParserOrd_TAG* ParserOrd_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PARSER_ORD(x) \
        ((ParserOrd_ptr) (x))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PARSER_ORD_CHECK_INSTANCE(x) \
        (nusmv_assert(PARSER_ORD(x) != PARSER_ORD(NULL)))

/*!
  \methodof ParserOrd
  \brief 

  
*/
ParserOrd_ptr ParserOrd_create(const NuSMVEnv_ptr env);

/*!
  \methodof ParserOrd
  \brief 

  
*/
void ParserOrd_destroy(ParserOrd_ptr self);

/*!
  \methodof ParserOrd
  \brief 

  
*/
void ParserOrd_parse_from_file(ParserOrd_ptr self, FILE* f);

/*!
  \methodof ParserOrd
  \brief 

  
*/
void 
ParserOrd_parse_from_string(ParserOrd_ptr self, const char* str);

/*!
  \methodof ParserOrd
  \brief Returns the list of variables read by the parser

  Returned list is owned by self, and should not be
  changed or destroyed
*/
NodeList_ptr ParserOrd_get_vars_list(const ParserOrd_ptr self);

/*!
  \methodof ParserOrd
  \brief 

  
*/
void ParserOrd_reset(ParserOrd_ptr self);


#endif /* __NUSMV_CORE_PARSER_ORD_PARSER_ORD_H__ */
