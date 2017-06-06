/* ---------------------------------------------------------------------------


  This file is part of the ``bmc'' package of NuSMV version 2.
  Copyright (C) 2000-2001 by FBK-irst and University of Trento.

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
  \brief The header file for the <tt>cmd</tt> module, the user
  commands handling layer.

  \todo: Missing description

*/


#ifndef __NUSMV_SHELL_BMC_BMC_CMD_H__
#define __NUSMV_SHELL_BMC_BMC_CMD_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif

#include "nusmv/core/prop/Prop.h" /* for Prop_type type */
#include "nusmv/core/utils/utils.h"
#include "nusmv/core/sat/sat.h"
#include "nusmv/core/utils/bmc_profiler.h"
#include "nusmv/core/utils/watchdog_util.h"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define BMC_USAGE 2

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define IS_INC_SAT true

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define IS_BMC_DUMP true

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Adds all bmc-related commands to the interactive shell

  

  \sa CInit_Init
*/
void Bmc_AddCmd(NuSMVEnv_ptr env);

/*!
  \brief Remove all bmc-related commands to the interactive shell

  Remove env sbmc command
*/
void Bmc_Cmd_quit(NuSMVEnv_ptr env);

/*!
  \brief Check k and l and assign to l the right value

  Check k and l and assign to l the right value.

  \se l is changed
*/
Outcome Bmc_Cmd_compute_rel_loop(NuSMVEnv_ptr const env,
                                        int* const rel_loop,
                                        const char* str_loop,
                                        const int k);

/*!
  \command{bmc_setup} Builds the model in a Boolean Epression format.

  \command_args{[-h] | [-f]}

  You must call this command before use any other
  bmc-related command. Only one call per session is required.<BR>
  Command options:<p>
  <dl>
    <dt> <tt>-f </tt>
    <dd> Forces the BMC model to be built.
  </dl>

*/
int Bmc_CommandBmcSetup(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{bmc_simulate} Generates a trace of the model from 0 (zero) to k

  \command_args{[-h] [-p | -v] [-r]
  [[-c "constraints"] | [-t "constraints"] ] [-k steps]
  }

  bmc_simulate does not require a specification
  to build the problem, because only the model is used to build it.
  The problem length is represented by the <i>-k</i> command parameter,
  or by its default value stored in the environment variable
  <i>bmc_length</i>.<BR>
  Command Options:<p>
  <dl>
    <dt> <tt>-p</tt>
       <dd> Prints current generated trace (only those variables whose value
       changed from the previous state).
    <dt> <tt>-v</tt>
       <dd> Verbosely prints current generated trace (changed and unchanged
       state variables).
    <dt> <tt>-r</tt>
       <dd> Picks a state from a set of possible future states in a random way.
    <dt> <tt>-c "constraints"</tt>
       <dd> Performs a simulation in which computation is restricted
       to states satisfying those <tt>constraints</tt>. The desired
       sequence of states could not exist if such constraints were too
       strong or it may happen that at some point of the simulation a
       future state satisfying those constraints doesn't exist: in
       that case a trace with a number of states less than
       <tt>steps</tt> trace is obtained. The expression cannot contain
       next operators, and is automatically shifted by one state in
       order to constraint only the next steps
    <dt> <tt>-t "constraints"</tt>
       <dd> Performs a simulation in which computation is restricted
       to states satisfying those <tt>constraints</tt>. The desired
       sequence of states could not exist if such constraints were too
       strong or it may happen that at some point of the simulation a
       future state satisfying those constraints doesn't exist: in
       that case a trace with a number of states less than
       <tt>steps</tt> trace is obtained.  The expression can contain
       next operators, and is NOT automatically shifted by one state
       as done with option -c
    <dt> <tt>-k steps</tt>
       <dd> Maximum length of the path according to the constraints.
       The length of a trace could contain less than <tt>steps</tt> states:
       this is the case in which simulation stops in an intermediate
       step because it may not exist any future state satisfying those
       constraints.
    </dl>

*/
int Bmc_CommandBmcSimulate(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{bmc_inc_simulate} Incrementally generates a trace of the model
  performing a given number of steps.

  \command_args{[-h] [-p | -v] [-r]
  [[-c "constraints"] | [-t "constraints"] ] [-k steps]
  }

  bmc_inc_simulate performs incremental simulation
  of the model. If no length is specified with <i>-k</i> command
  parameter, then the number of steps of simulation to perform is
  taken from the value stored in the environment variable
  <i>bmc_length</i>.<BR>
  Command Options:<p>
  <dl>
    <dt> <tt>-p</tt>
       <dd> Prints current generated trace (only those variables whose value
       changed from the previous state).
    <dt> <tt>-v</tt>
       <dd> Verbosely prints current generated trace (changed and unchanged
       state variables).
    <dt> <tt>-r</tt>
       <dd> Picks a state from a set of possible future states in a random way.
    <dt> <tt>-i</tt>
       <dd> Enters simulation's interactive mode.
    <dt> <tt>-a</tt>
       <dd> Displays all the state variables (changed and unchanged)
            in the interactive session
    <dt> <tt>-c "constraints"</tt>
       <dd> Performs a simulation in which computation is restricted
       to states satisfying those <tt>constraints</tt>. The desired
       sequence of states could not exist if such constraints were too
       strong or it may happen that at some point of the simulation a
       future state satisfying those constraints doesn't exist: in
       that case a trace with a number of states less than
       <tt>steps</tt> trace is obtained. The expression cannot contain
       next operators, and is automatically shifted by one state in
       order to constraint only the next steps
    <dt> <tt>-t "constraints"</tt>
       <dd> Performs a simulation in which computation is restricted
       to states satisfying those <tt>constraints</tt>. The desired
       sequence of states could not exist if such constraints were too
       strong or it may happen that at some point of the simulation a
       future state satisfying those constraints doesn't exist: in
       that case a trace with a number of states less than
       <tt>steps</tt> trace is obtained.  The expression can contain
       next operators, and is NOT automatically shifted by one state
       as done with option -c
    <dt> <tt>-k steps</tt>
       <dd> Maximum length of the path according to the constraints.
       The length of a trace could contain less than <tt>steps</tt> states:
       this is the case in which simulation stops in an intermediate
       step because it may not exist any future state satisfying those
       constraints.
    </dl>

*/
int
Bmc_CommandBmcIncSimulate(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{bmc_pick_state} Picks a state from the set of initial states

  \command_args{[-h] [-v] \}

  

  Chooses an element from the set of initial states, and makes it the
  <tt>current state</tt> (replacing the old one). The chosen state is
  stored as the first state of a new trace ready to be lengthened by
  <tt>steps</tt> states by the <tt>bmc_simulate</tt> or
  <tt>bmc_inc_simulate</tt> commands. A constraint can be provided to
  restrict the set of candidate states. <p>

  Command Options:<p>
  <dl>
    <dt> <tt>-v</tt>
       <dd> Verbosely prints out chosen state (all state variables, otherwise
       it prints out only the label <tt>t.1</tt> of the state chosen, where
       <tt>t</tt> is the number of the new trace, that is the number of
       traces so far generated plus one).
    <dt> <tt>-r</tt>
       <dd> Randomly picks a state from the set of initial states.
    <dt> <tt>-i</tt>
       <dd> Enters simulation's interactive mode.
    <dt> <tt>-a</tt>
       <dd> Displays all the state variables (changed and unchanged)
            in the interactive session
    <dt> <tt>-c "constraint"</tt>
       <dd> Uses <tt>constraint</tt> to restrict the set of initial states
       in which the state has to be picked.
    <dt> <tt>-s trace.state</tt>
       <dd> Picks state from trace.state label. A new simulation trace will
       be created by copying prefix of the source trace up to specified state.
  </dl> 

*/
int Bmc_CommandBmcPickState(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{bmc_simulate_check_feasible_constraints} Performs a feasibility check on the list of given
  constraints. Constraints that are found to be feasible can be safely
  assumed not to cause deadlocks if used in the following step of
  incremental simulation.

  \command_args{[-h | -q] [-c "formula"]* }

  This command generates feasibility problems for
  each constraint. Every constraint is checked against current state
  and FSM's transition relation, in order to exclude the possibility
  of deadlocks. Constraints found to be feasible can be safely assumed
  not to cause deadlocks if used in the following step of incremental
  simulation.<BR>
  <p>
    Command options:<p>
    <dl>
    <dt> <tt>-q</tt>
       <dd> Enables quiet mode. For each analyzed constraint "0" is
       printed if the constraint is found to be unfeasible, "1" is
       printed otherwise. <BR>
    <dt> <tt>-c "formula"</tt>
       <dd> Provide a constraint as a <tt>formula</tt> specified on
            the command-line. This option can be specified multiple
            times, in order to analyze a list of constraints.<BR>
  </dl>

*/
int
Bmc_CommandBmcSimulateCheckFeasibleConstraints(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{gen_ltlspec_bmc} Dumps into one or more dimacs files the given LTL
  specification, or all LTL specifications if no formula is given.
  Generation and dumping parameters are the maximum bound and the loopback
  values

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P "name"]
  [-k max_length] [-l loopback] [-o filename]}

    This command generates one or more problems, and
  dumps each problem into a dimacs file. Each problem is related to a specific
  problem bound, which increases from zero (0) to the given maximum problem
  bound. In this short description "<i>length</i>" is the bound of the
  problem that system is going to dump out. <BR>
  In this context the maximum problem bound is represented by the
  <i>max_length</i> parameter, or by its default value stored in the
  environment variable <i>bmc_length</i>.<BR>
  Each dumped problem also depends on the loopback you can explicitly
  specify by the <i>-l</i> option, or by its default value stored in the
  environment variable <i>bmc_loopback</i>. <BR>
  The property to be checked may be specified using the <i>-n idx</i>,
  the <i>-p "formula"</i> or the <i>-P "name"</i> options. <BR>
  You may specify dimacs file name by using the option <i>-o "filename"</i>,
  otherwise the default value stored in the environment variable
  <i>bmc_dimacs_filename</i> will be considered.<BR>
  <p>
  Command options:<p>
  <dl>
    <dt> <tt>-n <i>index</i></tt>
       <dd> <i>index</i> is the numeric index of a valid LTL specification
       formula actually located in the properties database. <BR>
       The validity of <i>index</i> value is checked out by the system.
    <dt> <tt>-p "formula [IN context]"</tt>
       <dd> Checks the <tt>formula</tt> specified on the command-line. <BR>
            <tt>context</tt> is the module instance name which the variables
            in <tt>formula</tt> must be evaluated in.
    <dt> <tt>-P name</tt>
       <dd> Checks the LTLSPEC property with name <tt>name</tt> in the property
            database.
    <dt> <tt>-k <i>max_length</i></tt>
       <dd> <i>max_length</i> is the maximum problem bound used when
       increasing problem bound starting from zero. Only natural number are
       valid values for this option. If no value is given the environment
       variable <i>bmc_length</i> value is considered instead.
    <dt> <tt>-l <i>loopback</i></tt>
       <dd> <i>loopback</i> value may be: <BR>
       - a natural number in (0, <i>max_length-1</i>). Positive sign ('+') can
       be also used as prefix of the number. Any invalid combination of bound
       and loopback will be skipped during the generation and
       dumping process.<BR>
       - a negative number in (-1, -<i>bmc_length</i>). In this case
       <i>loopback</i> is considered a value relative to <i>max_length</i>.
       Any invalid combination of bound and loopback will be skipped during
       the generation process.<BR>
       - the symbol 'X', which means "no loopback" <BR>
       - the symbol '*', which means "all possible loopback from zero to
       <i>length-1</i>"
    <dt> <tt>-o <i>filename</i></tt>
       <dd> <i>filename</i> is the name of dumped dimacs files, without
       extension. <BR>
       If this options is not specified, variable <i>bmc_dimacs_filename</i>
       will be considered. The file name string may contain special symbols
       which will be macro-expanded to form the real file name.
       Possible symbols are: <BR>
       - @F: model name with path part <BR>
       - @f: model name without path part <BR>
       - @k: current problem bound <BR>
       - @l: current loopback value <BR>
       - @n: index of the currently processed formula in the properties
       database <BR>
       - @@: the '@' character
  </dl>

  \sa Bmc_CommandGenLtlSpecBmcOnePb, Bmc_GenSolveLtl
*/
int Bmc_CommandGenLtlSpecBmc(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{gen_ltlspec_bmc_onepb} Dumps into one dimacs file the problem generated for
  the given LTL specification, or for all LTL specifications if no formula
  is explicitly given.
  Generation and dumping parameters are the problem bound and the loopback
  values

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P "name"] [-k length]
  [-l loopback] [-o filename]}

   As the <i>gen_ltlspec_bmc</i> command, but it generates
  and dumps only one problem given its bound and loopback. <BR>
  <p>
  Command options:<p>
  <dl>
    <dt> <tt>-n <i>index</i></tt>
       <dd> <i>index</i> is the numeric index of a valid LTL specification
       formula actually located in the properties database. <BR>
       The validity of <i>index</i> value is checked out by the system.
    <dt> <tt>-p "formula [IN context]"</tt>
       <dd> Checks the <tt>formula</tt> specified on the command-line. <BR>
            <tt>context</tt> is the module instance name which the variables
            in <tt>formula</tt> must be evaluated in.
    <dt> <tt>-P name</tt>
       <dd> Checks the LTLSPEC property with name <tt>name</tt> in the property
            database.
    <dt> <tt>-k <i>length</i></tt>
       <dd> <i>length</i> is the single problem bound used to generate and
       dump it. Only natural number are valid values for this option.
       If no value is given the environment variable <i>bmc_length</i>
       is considered instead.
    <dt> <tt>-l <i>loopback</i></tt>
       <dd> <i>loopback</i> value may be: <BR>
       - a natural number in (0, <i>length-1</i>). Positive sign ('+') can
       be also used as prefix of the number. Any invalid combination of length
       and loopback will be skipped during the generation and dumping
       process.<BR>
       - a negative number in (-1, -<i>length</i>).
       Any invalid combination of length and loopback will be skipped during
       the generation process.<BR>
       - the symbol 'X', which means "no loopback" <BR>
       - the symbol '*', which means "all possible loopback from zero to
       <i>length-1</i>"
    <dt> <tt>-o <i>filename</i></tt>
       <dd> <i>filename</i> is the name of the dumped dimacs file, without
       extension. <BR>
       If this
       options is not specified, variable <i>bmc_dimacs_filename</i> will be
       considered. The file name string may contain special symbols which
       will be macro-expanded to form the real file name.
       Possible symbols are: <BR>
       - @F: model name with path part <BR>
       - @f: model name without path part <BR>
       - @k: current problem bound <BR>
       - @l: current loopback value <BR>
       - @n: index of the currently processed formula in the properties
       database <BR>
       - @@: the '@' character
  </dl>

  \sa Bmc_CommandGenLtlSpecBmc, Bmc_GenSolveLtl
*/
int
Bmc_CommandGenLtlSpecBmcOnePb(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{check_ltlspec_bmc} Checks the given LTL specification, or all LTL
  specifications if no formula is given. Checking parameters are the maximum
  length and the loopback values

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P "name"]
  [-k max_length] [-l loopback] [-o filename]}

  
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
  The property to be checked may be specified using the <i>-n idx</i>,
  the <i>-p "formula"</i> or the <i>-P "name"</i> options. <BR>
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
    <dt> <tt>-o <i>filename</i></tt>
       <dd> <i>filename</i> is the name of the dumped dimacs file, without
       extension. <BR>
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

  \sa Bmc_CommandCheckLtlSpecBmcOnePb, Bmc_GenSolveLtl
*/
int
Bmc_CommandCheckLtlSpecBmc(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{check_ltlspec_bmc_onepb} Checks the given LTL specification, or all LTL
  specifications if no formula is given. Checking parameters are the single
  problem bound and the loopback values

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P "name"]
  [-k length] [-l loopback] [-o filename]}

  As command check_ltlspec_bmc but it produces only one
  single problem with fixed bound and loopback values, with no iteration
  of the problem bound from zero to max_length. <BR>
  <p>
  Command options:<p>
  <dl>
    <dt> <tt>-n <i>index</i></tt>
       <dd> <i>index</i> is the numeric index of a valid LTL specification
       formula actually located in the properties database. <BR>
       The validity of <i>index</i> value is checked out by the system.
    <dt> <tt>-p "formula [IN context]"</tt>
       <dd> Checks the <tt>formula</tt> specified on the command-line. <BR>
            <tt>context</tt> is the module instance name which the variables
            in <tt>formula</tt> must be evaluated in.
    <dt> <tt>-P name</tt>
       <dd> Checks the LTLSPEC property with name <tt>name</tt> in the property
            database.
    <dt> <tt>-k <i>length</i></tt>
       <dd> <i>length</i> is the problem bound used when generating the
       single problem. Only natural number are valid values for this option.
       If no value is given the environment variable <i>bmc_length</i> is
       considered instead.
    <dt> <tt>-l <i>loopback</i></tt>
       <dd> <i>loopback</i> value may be: <BR>
       - a natural number in (0, <i>max_length-1</i>). Positive sign ('+') can
       be also used as prefix of the number. Any invalid combination of length
       and loopback will be skipped during the generation/solving process.<BR>
       - a negative number in (-1, -<i>bmc_length</i>). In this case
       <i>loopback</i> is considered a value relative to <i>length</i>.
       Any invalid combination of length and loopback will be skipped
       during the generation/solving process.<BR>
       - the symbol 'X', which means "no loopback" <BR>
       - the symbol '*', which means "all possible loopback from zero to
       <i>length-1</i>"
    <dt> <tt>-o <i>filename</i></tt>
       <dd> <i>filename</i> is the name of the dumped dimacs file, without
       extension.<BR>
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

  \sa Bmc_CommandCheckLtlSpecBmc, Bmc_GenSolveLtl
*/
int
Bmc_CommandCheckLtlSpecBmcOnePb(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{gen_invar_bmc} Generates the given invariant, or all
  invariants if no formula is given

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P "name"]
  [-o filename]}

  <p>
  Command options:<p>
  <dl>
    <dt> <tt>-n <i>index</i></tt>
       <dd> <i>index</i> is the numeric index of a valid INVAR specification
       formula actually located in the properties database. <BR>
       The validity of <i>index</i> value is checked out by the system.
    <dt> <tt>-p "formula" [IN context]</tt>
       <dd> Checks the <tt>formula</tt> specified on the command-line. <BR>
            <tt>context</tt> is the module instance name which the variables
            in <tt>formula</tt> must be evaluated in.
    <dt> <tt>-P "name"</tt>
       <dd> Checks the invariant property stored in the properties
       database with name "name"
    <dt> <tt>-o <i>filename</i></tt>
       <dd> <i>filename</i> is the name of the dumped dimacs file,
       without extension. <BR>
       If you
       do not use this option the dimacs file name is taken from the
       environment variable <i>bmc_invar_dimacs_filename</i>. <BR>
       File name may contain special symbols which will be macro-expanded
       to form the real dimacs file name. Possible symbols are: <BR>
       - @F: model name with path part <BR>
       - @f: model name without path part <BR>
       - @n: index of the currently processed formula in the properties
       database <BR>
       - @@: the '@' character
  </dl>

  \sa Bmc_GenSolveInvar
*/
int Bmc_CommandGenInvarBmc(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{check_invar_bmc} Generates and solve the given invariant, or all
  invariants if no formula is given

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P "name"]
  [-k max_length] [-a algorithm] [-o filename] }

  <p>
  Command options:<p>
  <dl>
    <dt> <tt>-n <i>index</i></tt>
       <dd> <i>index</i> is the numeric index of a valid INVAR specification
       formula actually located in the properties database. <BR>
       The validity of <i>index</i> value is checked out by the system.
    <dt> <tt>-p "formula [IN context]"</tt>
       <dd> Checks the <tt>formula</tt> specified on the command-line. <BR>
            <tt>context</tt> is the module instance name which the variables
            in <tt>formula</tt> must be evaluated in.
    <dt> <tt>-P name</tt>
       <dd> Checks the INVARSPEC property with name <tt>name</tt> in the property
            database.
    <dt> <tt>-k <i>max_length</i></tt>
       <dd> (Use only when selected algorithm is een-sorensson).
            Use to specify the maximal deepth to be reached by the een-sorensson
            invariant checking algorithm. If not specified, the value assigned
            to the system variable <i>bmc_length</i> is taken.
    <dt> <tt>-a <i>algorithm</i></tt>
       <dd> Uses the specified algorithm to solve the invariant. If used, this
            option will override system variable <i>bmc_invar_alg</i>.
            At the moment, possible values are: "classic", "een-sorensson".
    <dt> <tt>-e</i></tt>
       <dd> Uses an additional step clause for algorithm "een-sorensson".</tt>
       <dd> <i>filename</i> is the name of the dumped dimacs file, without
       extension. <BR>
       It may contain special symbols which will be macro-expanded to form
       the real file name. Possible symbols are: <BR>
       - @F: model name with path part <BR>
       - @f: model name without path part <BR>
       - @n: index of the currently processed formula in the properties
       database <BR>
       - @@: the '@' character
  </dl>

  \sa Bmc_GenSolveInvar
*/
int Bmc_CommandCheckInvarBmc(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \brief Bmc commands options handling for commands (optionally)
  acceping options -k -l -o -a -n -p -P -e

   Output variables called res_* are pointers to
  variables that will be changed if the user specified a value for the
  corresponding option. For example if the user specified "-k 2", then
  *res_k will be assigned to 2. The caller can selectively choose which
  options can be specified by the user, by passing either a valid pointer
  as output parameter, or NULL to disable the corresponding option.
  For example by passing NULL as actual parameter of res_l, option -l will
  be not accepted.

  If both specified, k and l will be checked for mutual consistency.
  Loop will contain a relative value, like the one the user specified.

  prop_type is the expected property type, if specified.

  All integers values will not be changed if the corresponding options
  had not be specified by the user, so the caller might assign them to
  default values before calling this function.

  All strings will be allocated by the function if the corresponding
  options had been used by the user. In this case it is responsability
  of the caller to free them. Strings will be assigned to NULL if the
  user had not specified any corresponding option.

  Returns OUTCOME_GENERIC_ERROR if an error has occurred;
  Returns OUTCOME_SUCCESS_REQUIRED_HELP if -h options had been specified;
  Returns OUTCOME_SUCCESS in all other cases.
  

  \se Result parameters might change
*/
Outcome
Bmc_cmd_options_handling(NuSMVEnv_ptr env, int argc, char** argv,
                         Prop_Type prop_type,
                         Prop_ptr* res_prop,
                         int* res_k,
                         int* res_l,
                         char** res_a,
                         char** res_s,
                         char** res_o,
                         boolean* res_e,
                         int *res_step_k);


#if NUSMV_HAVE_INCREMENTAL_SAT

/*!
  \command{check_ltlspec_bmc_inc} Checks the given LTL specification, or all LTL
  specifications if no formula is given, using incremental algorithms.
  Checking parameters are the maximum length and the loopback values

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P "name"]
  [-k max_length] [-l loopback] }

  
  This command generates one or more problems, and calls (incremental)
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
  The property to be checked may be specified using the <i>-n idx</i>,
  the <i>-p "formula"</i> or the <i>-P "name"</i> options. <BR>
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
  </dl>

  \sa Bmc_CommandCheckLtlSpecBmcOnePb, Bmc_CommandCheckLtlSpecBmc
*/
int
Bmc_CommandCheckLtlSpecBmcInc(NuSMVEnv_ptr env, int argc, char** argv);

/*!
  \command{check_invar_bmc_inc} Generates and solve the given invariant, or all
  invariants if no formula is given

  \command_args{[-h | -n idx | -p "formula" [IN context] | -P "name"]
  [-k max_length] [-a algorithm] [-s strategy] }

  <p>
  Command options:<p>
  <dl>
    <dt> <tt>-n <i>index</i></tt>
       <dd> <i>index</i> is the numeric index of a valid INVAR specification
       formula actually located in the properties database. <BR>
       The validity of <i>index</i> value is checked out by the system.
    <dt> <tt>-p "formula [IN context]"</tt>
       <dd> Checks the <tt>formula</tt> specified on the command-line. <BR>
            <tt>context</tt> is the module instance name which the variables
            in <tt>formula</tt> must be evaluated in.
    <dt> <tt>-P name</tt>
       <dd> Checks the INVARSPEC property with name <tt>name</tt> in the property
            database.
    <dt> <tt>-k <i>max_length</i></tt>
       <dd> Use to specify the maximal depth to be reached by the incremental
            invariant checking algorithm. If not specified, the value assigned
            to the system variable <i>bmc_length</i> is taken.
    <dt> <tt>-a <i>algorithm</i></tt>
       <dd> Use to specify incremental invariant checking algorithm. Currently
            this can be one of the following values: dual, zigzag,
            falsification.
    <dt> <tt>-s <i>strategy</i></tt>
       <dd> Use to specify closure strategy (this currenly applies to dual
       algorithm only). This can be one of the following values: backward,
       forward.
  </dl>

  \sa Bmc_CommandCheckInvarBmc
*/
int
Bmc_CommandCheckInvarBmcInc(NuSMVEnv_ptr env, int argc, char** argv);
#endif


#if NUSMV_HAVE_BMC_PROFILER_LIBRARY
int Bmc_CommandProfile(NuSMVEnv_ptr env, int argc, char ** argv);
#endif

#if NUSMV_HAVE_WATCHDOG_LIBRARY
int Bmc_CommandWatchdog(NuSMVEnv_ptr env, int argc, char ** argv);
#endif

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_SHELL_BMC_BMC_CMD_H__ */
