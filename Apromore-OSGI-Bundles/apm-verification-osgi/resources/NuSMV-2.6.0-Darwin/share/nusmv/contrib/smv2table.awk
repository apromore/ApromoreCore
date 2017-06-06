# smv2table.awk
# Copyright 2002, 2009 Adolfo Villafiorita
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

# This awk script parses a NuSMV trace and produces a tab separated
# matrix-like representation of the counter-example.
#
# Usage:
#  awk -f <this_file> [-v VAR_IN_COLUMNS=1] [-v GREP="string"] trace
# 
# if VARS_IN_COLUMNS=1 then the variables are outputed in columns, that
# is, each column represents the evolution of a variable (default: by rows).
#
# if GREP is different from the empty string, then only the
# variables matching any substring of GREP are outputed.
#
BEGIN {
  current_state = 0
  current_var   = 0 
}

# accessory information: ignore it
/Trace .*/ {}

# state identification line: increment current state number
# remark. A better approach consists in assigning to current_state
# the second number matched by the pattern (that, in the
# counterexample, identifies the current state). This would allow,
# e.g., to correctly assign the state number to traces in which states
# are not numbered sequentially
/-> State: [0-9]+\.[0-9]+ <-/ {
  current_state ++;
  input_active = 0;
}

/-> Input: [0-9]+\.[0-9]+ <-/ {
  input_active = 1; 
  # unused at the moment... could be used to distinguish input variables
}


# lines of the type "   var = value" identify assignments
/[\t ]+.*=.*/ {
  gsub("[\t ]+", "", $0); # strip spaces
  split($0, token, "=");  # split string 

  # From here on: token[1] = var, token[2] = val;

  if (match(token[1], GREP)) {
    state[current_state, token[1]] = token[2];
  
    # if we are processing the first state, we also build an array of
    # all the variable names.
    if (current_state == 1) {
      current_var ++;
      variable[current_var] = token[1];
    }
  }
}

END {
  if (VARS_IN_COLUMNS == 0) {
    # Header: State 1 2 3 ...
    printf "State"
    for (i = 1; i <= current_state; i++) {
      printf "\t%d", i
    }
    printf "\n";

    # Rows: 
    #   Var_1 val1.1 val1.2 val1.3 ...
    #   Var_2 val1.2 val1.2 val1.3 ...
    for (j = 1; j <= current_var; j++) {
        printf "%s", variable[j];
        for (i = 1; i < current_state; i++) {
            printf "\t%s", state[i, variable[j]];
        }
        printf "\n";
    }
  }
  else {
    # Header: Var_1 Var_2 Var_3
    printf "State";
    for (j = 1; j <= current_var; j++) {
      printf "\t%s", variable[j];
    }
    printf "\n";

    # values of all the variables
    for (i = 1; i <= current_state; i++) {
      printf "%d", i;
      for (j = 1; j <= current_var; j++) {
        printf "\t%s", state[i, variable[j]];
      }
      printf "\n";
    }
  }
}
