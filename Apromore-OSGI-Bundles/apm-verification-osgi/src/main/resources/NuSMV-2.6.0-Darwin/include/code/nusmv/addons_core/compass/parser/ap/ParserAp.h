/* ---------------------------------------------------------------------------


  This file is part of the ``parser.ap'' package of NuSMV version 2. 
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

-----------------------------------------------------------------------------*/

/*!
  \author Marco Roveri
  \brief  The header file of ParserAp class.

  \todo: Missing description

*/



#ifndef __NUSMV_ADDONS_CORE_COMPASS_PARSER_AP_PARSER_AP_H__
#define __NUSMV_ADDONS_CORE_COMPASS_PARSER_AP_PARSER_AP_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/utils/NodeList.h"

/*!
  \struct ParserAp
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct ParserAp_TAG* ParserAp_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PARSER_AP(x) \
        ((ParserAp_ptr) (x))

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define PARSER_AP_CHECK_INSTANCE(x) \
        (nusmv_assert(PARSER_AP(x) != PARSER_AP(NULL)))

/*!
  \methodof ParserAp
  \brief 

  
*/
ParserAp_ptr ParserAp_create(const NuSMVEnv_ptr env);

/*!
  \methodof ParserAp
  \brief 

  
*/
void ParserAp_destroy(ParserAp_ptr self);

/*!
  \methodof ParserAp
  \brief 

  
*/
void ParserAp_parse_from_file(ParserAp_ptr self, FILE* f);

/*!
  \methodof ParserAp
  \brief 

  
*/
void 
ParserAp_parse_from_string(ParserAp_ptr self, const char* str);

/*!
  \methodof ParserAp
  \brief Returns the list of ap read by the parser

  Returned list is owned by self, and should not be
  changed or destroyed
*/
NodeList_ptr ParserAp_get_ap_list(const ParserAp_ptr self);

/*!
  \methodof ParserAp
  \brief 

  
*/
void ParserAp_reset(ParserAp_ptr self);


#endif /* __NUSMV_ADDONS_CORE_COMPASS_PARSER_AP_PARSER_AP_H__ */
