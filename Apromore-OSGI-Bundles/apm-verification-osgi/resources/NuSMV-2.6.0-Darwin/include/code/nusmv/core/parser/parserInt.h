/* ---------------------------------------------------------------------------


  This file is part of the ``parser'' package of NuSMV version 2. 
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
  \brief Internal header of the parser package.

  Internal header of the parser package.

*/


#ifndef __NUSMV_CORE_PARSER_PARSER_INT_H__
#define __NUSMV_CORE_PARSER_PARSER_INT_H__

#include <stdlib.h>
#include <stdio.h>

#include "nusmv/core/parser/parser.h"
#include "cudd/util.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"
#include "nusmv/core/dd/dd.h"
#include "nusmv/core/set/set.h"
#include "nusmv/core/rbc/rbc.h"
#include "nusmv/core/compile/compile.h"
#include "nusmv/core/opt/opt.h"

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \struct yy_buffer_state
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct yy_buffer_state* YY_BUFFER_STATE;


/*---------------------------------------------------------------------------*/
/* Constants declarations                                                    */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/
extern node_ptr parsed_tree;

enum PARSE_MODE { PARSE_MODULES, PARSE_COMMAND, PARSE_LTL_EXPR };
extern enum PARSE_MODE parse_mode_flag;

extern int nusmv_yylineno;
extern FILE *nusmv_yyin;

extern int psl_yylineno;
extern FILE *psl_yyin;

extern cmp_struct_ptr cmps;


extern node_ptr psl_parsed_tree;
extern node_ptr psl_property_name;

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int nusmv_yylex(void);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int nusmv_yyparse(void);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void nusmv_yyrestart(FILE *input_file);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void nusmv_yy_switch_to_buffer(YY_BUFFER_STATE new_buffer);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
YY_BUFFER_STATE nusmv_yy_scan_buffer(char *base, size_t size);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
YY_BUFFER_STATE nusmv_yy_create_buffer(FILE *file, int size);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void nusmv_yy_delete_buffer(YY_BUFFER_STATE b);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
YY_BUFFER_STATE nusmv_yy_scan_string(const char *yy_str);

/*!
  \brief Add a new syntax error to the list

  This is called by the parser when needed
*/
void parser_add_syntax_error(const NuSMVEnv_ptr env,
                                    const char* fname, int lineno, 
                                    const char* token,
                                    const char* err_msg);

/*!
  \brief Frees the list of structures containing the syntax
  errors built by the parser. 

  
*/
void parser_free_parsed_syntax_errors(const NuSMVEnv_ptr env);

/* psl */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void Parser_switch_to_psl(void);

#endif /* __NUSMV_CORE_PARSER_PARSER_INT_H__ */
