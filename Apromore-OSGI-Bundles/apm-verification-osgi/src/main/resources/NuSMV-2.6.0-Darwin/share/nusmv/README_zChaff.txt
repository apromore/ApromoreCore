zChaff is a powerful SAT Solver [1] that can be optionally used with
NuSMV to perform BMC.

This file contains information about how zChaff can be downloaded
manually to allow for compilation and linking within NuSMV. This in
normal conditions is not needed, as this step is normally made
automatically.


******************************************************************************
*                           (0) zChaff License                               *
******************************************************************************
Before using zChaff with NuSMV, you have to know that:

    *****************************************************************
    *** zChaff is for non-commercial purposes only.               ***
    *** NO COMMERCIAL USE OF ZCHAFF IS ALLOWED WITHOUT WRITTEN    ***
    *** PERMISSION FROM PRINCETON UNIVERSITY.                     ***
    *** Please contact Sharad Malik (malik@ee.princeton.edu)      ***
    *** for details.                                              ***
    *****************************************************************

Copyright 2000-2004, Princeton University.  All rights reserved.
By using this software the USER indicates that he or she has read,
understood and will comply with the following:

--- Princeton University hereby grants USER nonexclusive permission
to use, copy and/or modify this software for internal, noncommercial,
research purposes only. Any distribution, including commercial sale
or license, of this software, copies of the software, its associated
documentation and/or modifications of either is strictly prohibited
without the prior consent of Princeton University.  Title to copyright
to this software and its associated documentation shall at all times
remain with Princeton University.  Appropriate copyright notice shall
be placed on all software copies, and a complete copy of this notice
shall be included in all copies of the associated documentation.
No right is  granted to use in advertising, publicity or otherwise
any trademark,  service mark, or the name of Princeton University.

--- This software and any associated documentation is provided "as is"

PRINCETON UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES, EXPRESS
OR IMPLIED, INCLUDING THOSE OF MERCHANTABILITY OR FITNESS FOR A
PARTICULAR PURPOSE, OR THAT  USE OF THE SOFTWARE, MODIFICATIONS, OR
ASSOCIATED DOCUMENTATION WILL NOT INFRINGE ANY PATENTS, COPYRIGHTS,
TRADEMARKS OR OTHER INTELLECTUAL PROPERTY RIGHTS OF A THIRD PARTY.

Princeton University shall not be liable under any circumstances for
any direct, indirect, special, incidental, or consequential damages
with respect to any claim by USER or any third party on account of
or arising from the use, or inability to use, this software or its
associated documentation, even if Princeton University has been advised
of the possibility of those damages.


******************************************************************************
*         (1) Enabling/Disabling zChaff when compiling NuSMV                 *
******************************************************************************

Due to its licensing restrictions, zChaff is not enabled by default,
i.e. when compiling NuSMV, zChaff will be not downloaded, compiled and
linked within it.

Setting cmake variable ENABLE_ZCHAFF=ON (OFF by default) can be used
to enable downloading, compiling and linking zChaff within NuSMV.

WARNING: at least one SAT solver is required when compiling NuSMV.


******************************************************************************
*         (2) Downloading zChaff for compiling with NuSMV                    *
******************************************************************************

NOTE: This step is not needed in normal conditions.

      In NuSMV <= 2.5.4, zChaff had to be downloaded and built by the
      user, which is no longer the case with NuSMV >= 2.6.0, as
      zChaff is downloaded and built automatically when building
      NuSMV (if zChaff is enabled at configuration time). However, if
      the automatic download is not possible, zChaff can be download
      before compiling NuSMV, by following the instructions here
      reported.

Prerequisites: A C++ compiler (like g++) is required. As zChaff is patched
to be linked to nusmv, the program 'patch' is also required.

In the following, we assume that the archive is saved in the <TOPDIR> root
directory. So, you should have:

    [...]/<TOPDIR>/NuSMV/
    [...]/<TOPDIR>/cudd-2.4.1.1/
    [...]/<TOPDIR>/MiniSat/
    [...]/<TOPDIR>/zchaff/

1. Download zChaff zip archive from
   http://www.princeton.edu/~chaff/zchaff/zchaff.64bit.2007.3.12.zip

2. Save the downloaded zip archive in [...]/<TOPDIR>/zChaff/

3. The saved archive will be found and used later when compiling NuSMV.


******************************************************************************
*                     (3) Instruct NuSMV to use zChaff                       *
******************************************************************************
To exploit the zChaff solver, you have to enter an interactive
NuSMV session:

    NuSMV -int yourModel.smv

issue the command:

    set sat_solver "zchaff"

and then proceed with your BMC session. zChaff will be used to solve
the generated instances.

Since version 2.3.1, command line option "-sat_solver" is provided to
set the SAT solver in batch mode as well.


[1] See <http://www.princeton.edu/~chaff/zchaff.html>

===
EOF
===
