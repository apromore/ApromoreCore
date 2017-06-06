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
  \brief Interface with the parser

  This file describe the interface with the parser. The
  result of the parsing is stored in a global variable called
  <code>parsed_tree</code>.

*/


#ifndef __NUSMV_CORE_PARSER_PARSER_H__
#define __NUSMV_CORE_PARSER_PARSER_H__

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/node/node.h"


/*---------------------------------------------------------------------------*/
/* Macros definitions                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define OPT_PARSER_IS_LAX  "parser_is_lax"

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Initializes the parser

  Initializes the parser
*/
void Parser_Init(NuSMVEnv_ptr env);

/*!
  \brief Deinitializes the parser

  Deinitializes the parser
*/
void Parser_Quit(NuSMVEnv_ptr env);

/*!
  \brief Reads a NuSMV file into NuSMV

  Reads a NuSMV file into NuSMV.
*/
int Parser_read_model(NuSMVEnv_ptr env,
                             char* ifile);

/*!
  \brief Parse a comand from a given string.

  Create a string for a command, and then call
  <tt>nsumv_yyparse</tt> to read from the created string.
  If a parsing error occurs than return 1, else return 0.
  The result of parsing is stored in <tt>pc</tt> to be used from the caller.
*/
int Parser_ReadCmdFromString(NuSMVEnv_ptr env, int argc, const char** argv, 
                                    const char* head, const char* tail, 
                                    node_ptr* pc);

/*!
  \brief Parse a simple expression from string

   The resulting parse tree is returned through res. If a
  parsing error occurs then return 1, else return 0. 
*/
int Parser_ReadSimpExprFromString(NuSMVEnv_ptr env, const char* str_expr, 
                                         node_ptr* res);

/*!
  \brief Parse a command expression from file

   The resulting parse tree is returned through res. If a
  parsing error occurs then return 1, else return 0. 
*/
int Parser_ReadCmdFromFile(NuSMVEnv_ptr env, const char *filename, 
                                  node_ptr* res);

/*!
  \brief Parse SMV code from a given file.

  Parse SMV code from a given file. If
  no file is provided, parse from stdin. If a parsing error occurs then
  return 1, else return 0. The result of parsing is stored in
  the global variable <tt>parsed_tree</tt> to be used from the caller.
*/
int Parser_ReadSMVFromFile(NuSMVEnv_ptr env, const char* filename);

/*!
  \brief Parse LTL expression from a given file.

  Parse SMV code from a given file. If
  no file is provided, parse from stdin. If a parsing error occurs then
  return 1, else return 0. The result of parsing is stored in
  the global variable <tt>parsed_tree</tt> to be used from the caller.
*/
int Parser_ReadLtlExprFromFile(NuSMVEnv_ptr env, const char* filename);

/*!
  \brief Parses a PSL expression from the given string.

  The PSL parser is directly called. The resulting
  parse tree is returned through res. 1 is returned if an error occurred.
*/
int Parser_read_psl_from_string(const NuSMVEnv_ptr env,
                                       int argc, const char** argv,
                                       node_ptr* res);

/*!
  \brief Parses a PSL expression from the given file.

  The PSL parser is directly called. The resulting
  parse tree is returned through res. 1 is returned if an error occurred.
*/
int Parser_read_psl_from_file(const NuSMVEnv_ptr env,
                                     const char* filename, node_ptr* res);

/*!
  \brief Parse a next expression from string

   The resulting parse tree is returned through res. If a
  parsing error occurs then return 1, else return 0. 
*/
int
Parser_ReadNextExprFromString(NuSMVEnv_ptr env, const char* str_expr, node_ptr* res);

/*!
  \brief Parse a type declaration from string

   The resulting parse tree is returned through res. If a
  parsing error occurs then return 1, else return 0. 

  E.g. "2..5" will return a ITYPE node containing TWODOTS node. It
  works with all itypes of the language, i.e. all var types but
  process and module instances.
*/
int Parser_ReadTypeFromString(NuSMVEnv_ptr env,
                                     const char* str_type,
                                     node_ptr* res);

/*!
  \brief Parse an identifier expression from string

   The resulting parse tree is returned through res. If a
  parsing error occurs then return 1, else return 0. 
*/
int
Parser_ReadIdentifierExprFromString(NuSMVEnv_ptr env, const char* str_expr, node_ptr* res);

/*!
  \brief Parse a next expression from file

   The resulting parse tree is returned through res. If a
  parsing error occurs then return 1, else return 0. 
*/
int
Parser_ReadNextExprFromFile(NuSMVEnv_ptr env, const char *filename, node_ptr* res);

/*!
  \brief Returns a list of SYNTAX_ERROR nodes

  Each node of the list can be passed to
  Parser_get_syntax_error to get information out of it. The
  returned lists must be NOT modified or freed by the caller.

  \sa Parser_get_syntax_error
*/
node_ptr Parser_get_syntax_errors_list(const NuSMVEnv_ptr env);

/*!
  \brief Returns information out of nodes contained in list
  returned by Parser_get_syntax_errors_list.

  Each node contains information which will be set in
  output params filename, lineno and message. Those information
  must be NOT modified or freed by the caller. If not interested in
  an information, pass NULL with the respective parameter.

  \sa Parser_get_syntax_errors_list
*/
void Parser_get_syntax_error(node_ptr node, 
                                    const char** out_filename, 
                                    int* out_lineno, 
                                    const char** out_token,
                                    const char** out_message);

/*!
  \brief Prints information contained in one node ot the list
  returned by Parser_get_syntax_errors_list.

  The syntax error information contained in the given
  node is printed to the given output file.

  \sa Parser_get_syntax_errors_list
*/
void Parser_print_syntax_error(node_ptr error, FILE* fout);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void Parser_OpenInput(const NuSMVEnv_ptr env, const char *filename);

/*!
  \brief Close the input file

  Closes the input file and corresponding buffer used
  by the parser to read tokens.
  NB: This function should be invoked only after successive invocation
  of parser_open_input_pp.

  \sa Parser_OpenInput
*/
void Parser_CloseInput(void);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void Parser_switch_to_smv(void);

/*!
  \brief Skips multiline comment during parsing

  Skips multiline comment during parsing
*/
int Parser_skip_multiline_comment(int (*read_function)(void));

/*!
  \brief Skips one line comment during parsing

  Skips one line comment during parsing
*/
int Parser_skip_one_line_comment(int (*read_function)(void));

#endif /* __NUSMV_CORE_PARSER_PARSER_H__ */
