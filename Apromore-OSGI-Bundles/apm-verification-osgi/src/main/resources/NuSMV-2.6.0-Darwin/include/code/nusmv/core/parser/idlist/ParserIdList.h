/* ---------------------------------------------------------------------------


  This file is part of the ``parser.idlist'' package of NuSMV version 2. 
  Copyright (C) 2006 by FBK-irst. 

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
  \brief  The header file of ParserIdList class.

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_PARSER_IDLIST_PARSER_ID_LIST_H__
#define __NUSMV_CORE_PARSER_IDLIST_PARSER_ID_LIST_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/NodeList.h"
#include "nusmv/core/cinit/NuSMVEnv.h"

/*!
  \struct ParserIdList
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct ParserIdList_TAG* ParserIdList_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PARSER_ID_LIST(x) \
        ((ParserIdList_ptr) (x))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PARSER_ID_LIST_CHECK_INSTANCE(x) \
        (nusmv_assert(PARSER_ID_LIST(x) != PARSER_ID_LIST(NULL)))


/* ---------------------------------------------------------------------- */

/*!
  \methodof ParserIdList
  \brief 

  
*/
ParserIdList_ptr ParserIdList_create(const NuSMVEnv_ptr env);

/*!
  \methodof ParserIdList
  \brief 

  
*/
void ParserIdList_destroy(ParserIdList_ptr self);

/*!
  \methodof ParserIdList
  \brief 

  
*/
void ParserIdList_parse_from_file(ParserIdList_ptr self, FILE* f);

/*!
  \methodof ParserIdList
  \brief 

  
*/
void 
ParserIdList_parse_from_string(ParserIdList_ptr self, const char* str);

/*!
  \methodof ParserIdList
  \brief Returns the list of variables read by the parser

  Returned list is owned by self, and should not be
  changed or destroyed
*/
NodeList_ptr ParserIdList_get_id_list(const ParserIdList_ptr self);

/*!
  \methodof ParserIdList
  \brief 

  
*/
void ParserIdList_reset(ParserIdList_ptr self);


#endif /* __NUSMV_CORE_PARSER_IDLIST_PARSER_ID_LIST_H__ */
