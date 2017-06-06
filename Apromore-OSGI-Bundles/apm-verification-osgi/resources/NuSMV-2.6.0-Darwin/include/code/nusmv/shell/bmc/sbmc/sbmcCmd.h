/* ---------------------------------------------------------------------------


  This file is part of the ``bmc.sbmc'' package of NuSMV version 2.
  Copyright (C) 2004 Timo Latvala <timo.latvala@tkk.fi>
  Copyright (C) 2006 Tommi Junttila

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

  For more information of NuSMV see <http://nusmv.fbk.eu>
  or email to <nusmv-users@fbk.eu>.
  Please report bugs to <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to <nusmv@fbk.eu>. 

-----------------------------------------------------------------------------*/

/*!
  \author Timo Latvala, Marco Roveri
  \brief The header file for the <tt>cmd</tt> module, the user
  commands handling layer.

  \todo: Missing description

*/


#ifndef __NUSMV_SHELL_BMC_SBMC_SBMC_CMD_H__
#define __NUSMV_SHELL_BMC_SBMC_SBMC_CMD_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/prop/Prop.h"

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Adds all bmc-related commands to the interactive shell

  

  \sa CInit_Init
*/
void SBmc_AddCmd(NuSMVEnv_ptr env);

/*!
  \brief Adds all bmc-related commands to the interactive shell

  

  \sa CInit_Init
*/
void Sbmc_Cmd_quit(NuSMVEnv_ptr env);

/*!
  \command{check_ltlspec_sbmc} Finds error up to depth k

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P
  "name"] [-k max_length] [-l loopback] [-1] [-o filename]}

  
  This command generates one or more problems, and calls
  SAT solver for each one. Each problem is related to a specific problem
  bound, which increases from zero (0) to the given maximum problem
  length. Here "<i>length</i>" is the bound of the problem that system
  is going to generate and/or solve. <BR>
  In this context the maximum problem bound is represented by the
  <i>-k</i> command parameter, or by its default value stored in the
  environment variable <i>bmc_length</i>.<BR>
  The single generated problem also depends on the "<i>loopback</i>"
  parameter you can explicitly specify by the <i>-l</i> option, or by its
  default value stored in the environment variable <i>bmc_loopback</i>. <BR>
  The property to be checked may be specified using the <i>-n idx</i> or
  the <i>-p "formula"</i> options. <BR>
  If you need to generate a dimacs dump file of all generated problems, you
  must use the option <i>-o "filename"</i>. <BR>
  <p>
  Command options:<p>
  <dl>
    <dt> <tt>-n <i>index</i></tt>
       <dd> <i>index</i> is the numeric index of a valid LTL specification
       formula actually located in the properties database. <BR>
    <dt> <tt>-p "formula" [IN context]</tt>
       <dd> Checks the <tt>formula</tt> specified on the command-line. <BR>
            <tt>context</tt> is the module instance name which the variables
            in <tt>formula</tt> must be evaluated in.
    <dt> <tt>-P name</tt>
       <dd> Checks the LTLSPEC property with name <tt>name</tt> in the property
            database.
    <dt> <tt>-k <i>max_length</i></tt>
       <dd> <i>max_length</i> is the maximum problem bound must be reached.
       Only natural number are valid values for this option. If no value
       is given the environment variable <i>bmc_length</i> is considered
       instead.
    <dt> <tt>-l <i>loopback</i></tt>
       <dd> <i>loopback</i> value may be: <BR>
       - a natural number in (0, <i>max_length-1</i>). Positive sign ('+') can
       be also used as prefix of the number. Any invalid combination of length
       and loopback will be skipped during the generation/solving process.<BR>
       - a negative number in (-1, -<i>bmc_length</i>). In this case
       <i>loopback</i> is considered a value relative to <i>max_length</i>.
       Any invalid combination of length and loopback will be skipped
       during the generation/solving process.<BR>
       - the symbol 'X', which means "no loopback" <BR>
       - the symbol '*', which means "all possible loopback from zero to
       <i>length-1</i>"
    <dt> <tt>-1</tt>
       <dd> Generates and solves a single problem with length <tt>k</tt>
    <dt> <tt>-o <i>filename</i></tt>
       <dd> <i>filename</i> is the name of the dumped dimacs file.
       It may contain special symbols which will be macro-expanded to form
       the real file name. Possible symbols are: <BR>
       - @F: model name with path part <BR>
       - @f: model name without path part <BR>
       - @k: current problem bound <BR>
       - @l: current loopback value <BR>
       - @n: index of the currently processed formula in the properties
       database <BR>
       - @@: the '@' character
  </dl>

  For further information about this implementation see:
  T. Latvala, A. Biere, K. Heljanko, and T. Junttila. Simple is
  Better: Efficient Bounded Model Checking for Past LTL. In: R. Cousot
  (ed.), Verification, Model Checking, and Abstract Interpretation,
  6th International Conference VMCAI 2005, Paris, France, Volume 3385
  of LNCS, pp. 380-395, Springer, 2005.  Copyright (C)
  Springer-Verlag.

*/
int
Sbmc_CommandCheckLtlSpecSBmc(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{gen_ltlspec_sbmc} Dumps into one or more dimacs files the given LTL
  specification, or all LTL specifications if no formula is given.
  Generation and dumping parameters are the maximum bound and the loopback
  values. Uses Kepa's and Timo's method for doing bmc.

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P "name"]
  [-k max_length] [-l loopback] [-1] [-o filename]}

   This command generates one or more problems, and
  dumps each problem into a dimacs file. Each problem is related to a specific
  problem bound, which increases from zero (0) to the given maximum problem
  bound. In this short description "<i>length</i>" is the bound of the
  problem that system is going to dump out. Uses Kepa's and Timo's method for doing bmc. <BR>
  In this context the maximum problem bound is represented by the
  <i>max_length</i> parameter, or by its default value stored in the
  environment variable <i>bmc_length</i>.<BR>
  Each dumped problem also depends on the loopback you can explicitly
  specify by the <i>-l</i> option, or by its default value stored in the
  environment variable <i>bmc_loopback</i>. <BR>
  The property to be checked may be specified using the <i>-n idx</i> or
  the <i>-p "formula"</i> options. <BR>
  You may specify dimacs file name by using the option <i>-o "filename"</i>,
  otherwise the default value stored in the environment variable
  <i>bmc_dimacs_filename</i> will be considered.<BR>
  <p>
  Command options:<p>
  <dl>
    <dt> <tt>-n <i>index</i></tt>
       <dd> <i>index</i> is the numeric index of a valid LTL specification
       formula actually located in the properties database. <BR>
    <dt> <tt>-p "formula" [IN context]</tt>
       <dd> Checks the <tt>formula</tt> specified on the command-line. <BR>
            <tt>context</tt> is the module instance name which the variables
            in <tt>formula</tt> must be evaluated in.
    <dt> <tt>-P name</tt>
       <dd> Checks the LTLSPEC property with name <tt>name</tt> in the property
            database.
    <dt> <tt>-k <i>max_length</i></tt>
       <dd> <i>max_length</i> is the maximum problem bound must be reached.
       Only natural number are valid values for this option. If no value
       is given the environment variable <i>bmc_length</i> is considered
       instead.
    <dt> <tt>-l <i>loopback</i></tt>
       <dd> <i>loopback</i> value may be: <BR>
       - a natural number in (0, <i>max_length-1</i>). Positive sign ('+') can
       be also used as prefix of the number. Any invalid combination of length
       and loopback will be skipped during the generation/solving process.<BR>
       - a negative number in (-1, -<i>bmc_length</i>). In this case
       <i>loopback</i> is considered a value relative to <i>max_length</i>.
       Any invalid combination of length and loopback will be skipped
       during the generation/solving process.<BR>
       - the symbol 'X', which means "no loopback" <BR>
       - the symbol '*', which means "all possible loopback from zero to
       <i>length-1</i>"
    <dt> <tt>-1</tt>
       <dd> Generates a single problem with length <tt>k</tt>
    <dt> <tt>-o <i>filename</i></tt>
       <dd> <i>filename</i> is the name of the dumped dimacs file.
       It may contain special symbols which will be macro-expanded to form
       the real file name. Possible symbols are: <BR>
       - @F: model name with path part <BR>
       - @f: model name without path part <BR>
       - @k: current problem bound <BR>
       - @l: current loopback value <BR>
       - @n: index of the currently processed formula in the properties
       database <BR>
       - @@: the '@' character
  </dl>

  For further information about this implementation see:
  T. Latvala, A. Biere, K. Heljanko, and T. Junttila. Simple is
  Better: Efficient Bounded Model Checking for Past LTL. In: R. Cousot
  (ed.), Verification, Model Checking, and Abstract Interpretation,
  6th International Conference VMCAI 2005, Paris, France, Volume 3385
  of LNCS, pp. 380-395, Springer, 2005.  Copyright (C)
  Springer-Verlag.

*/
int
Sbmc_CommandGenLtlSpecSBmc(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{check_ltlspec_sbmc_inc} Incremental SBMC LTL model checking

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P
  "name"] [-k max_length] [-c] [-N]}

  
  This command generates one or more problems, and calls
  SAT solver for each one. Each problem is related to a specific problem
  bound, which increases from zero (0) to the given maximum problem
  length. Here "<i>length</i>" is the bound of the problem that system
  is going to generate and/or solve. <BR>
  In this context the maximum problem bound is represented by the
  <i>-k</i> command parameter, or by its default value stored in the
  environment variable <i>bmc_length</i>.<BR>
  The property to be checked may be specified using the <i>-n idx</i>,
  <i>-p "formula"</i>, or <i>-P "property_name"</i> options. <BR>
  Completeness check, although slower, can be used to determine whether
  the property holds.<BR>
  <p>
  Command options:<p>
  <dl>
    <dt> <tt>-n <i>index</i></tt>
       <dd> <i>index</i> is the numeric index of a valid LTL specification
       formula actually located in the properties database. <BR>
    <dt> <tt>-p "formula" [IN context]</tt>
       <dd> Checks the <tt>formula</tt> specified on the command-line. <BR>
            <tt>context</tt> is the module instance name which the variables
            in <tt>formula</tt> must be evaluated in.
    <dt> <tt>-P name</tt>
       <dd> Checks the LTLSPEC property with name <tt>name</tt> in the property
            database.
    <dt> <tt>-k <i>max_length</i></tt>
       <dd> <i>max_length</i> is the maximum problem bound must be reached.
       Only natural number are valid values for this option. If no value
       is given the environment variable <i>bmc_length</i> is considered
       instead.
    <dt> <tt>-c</tt>
       <dd> Performs completeness check at every step. This can be
       effectively used to determine whether a property holds.</tt>
    <dt><tt>-N</tt>
       <dd> Does not perform virtual unrolling.
  </dl>

  For further information about this implementation see:
  T. Latvala, A. Biere, K. Heljanko, and T. Junttila. Simple is
  Better: Efficient Bounded Model Checking for Past LTL. In: R. Cousot
  (ed.), Verification, Model Checking, and Abstract Interpretation,
  6th International Conference VMCAI 2005, Paris, France, Volume 3385
  of LNCS, pp. 380-395, Springer, 2005.  Copyright (C)
  Springer-Verlag.

*/
int
Sbmc_CommandLTLCheckZigzagInc(NuSMVEnv_ptr env, int argc, char** argv);

/**AutomaticEnd***************************************************************/

#endif /* _BMC_CMD_H */
