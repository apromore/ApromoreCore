This file contains information about how MiniSat can be downloaded
manually to allow for compilation and linking within NuSMV. This in
normal conditions is not needed, as this step is normally made
automatically.

******************************************************************************
*                          (0) NuSMV and MiniSat                             *
******************************************************************************

Since version 2.2.2, NuSMV is able to use the propositional solver
"MiniSat" (in addition to "Z-Chaff") to deal with the SAT instances
generated during a Bounded-Model-Checking session. Further information
about MiniSat can be found in the paper entitled "An Extensible
SAT-solver", by Niklas Een and Niklas Sorensson.

The currently linked version of MiniSat is 2.2, and more precisely a
patched version of SHA-1 commit
37dc6c67e2af26379d88ce349eb9c4c6160e8543 which at current time is
pointed by master branch of git repository of MiniSat
(https://github.com/niklasso/minisat)

Version MiniSat-2.2 can be freely used, when respecting the license:

----------------------------------------------------------------------
MiniSat -- Copyright (c) 2003-2006, Niklas Een, Niklas Sorensson
           Copyright (c) 2007-2010  Niklas Sorensson

Permission is hereby granted, free of charge, to any person obtaining a
copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
----------------------------------------------------------------------


******************************************************************************
*         (1) Enabling/Disabling MiniSat when compiling NuSMV                *
******************************************************************************

MiniSat is enabled by default, i.e. when compiling NuSMV, MiniSat will
be downloaded, compiled and linked within it.

Setting cmake variable ENABLE_MINISAT=OFF (ON by default) can be used
to avoid downloading, compiling and linking MiniSat within NuSMV.

WARNING: at least one SAT solver is required when compiling NuSMV.


******************************************************************************
*         (2) Downloading MiniSat for compiling with NuSMV                   *
******************************************************************************

NOTE: This step is not needed in normal conditions.

      In NuSMV <= 2.5.4, MiniSat had to be downloaded and built by the
      user, which is no longer the case with NuSMV >= 2.6.0, as
      MiniSat is downloaded and built automatically when building
      NuSMV (if MiniSat is enabled at configuration time). However, if
      the automatic download is not possible, MiniSat can be download
      before compiling NuSMV, by following the instructions here
      reported.

Prerequisites: A C++ compiler (like g++) is required. As MiniSat is patched
to be linked to nusmv, the program 'patch' is also required.

In the following, we assume that the archive is saved in the <TOPDIR> root
directory. So, you should have:

    [...]/<TOPDIR>/NuSMV/
    [...]/<TOPDIR>/cudd-2.4.1.1/
    [...]/<TOPDIR>/MiniSat/
    [...]/<TOPDIR>/zchaff/

1. Download MiniSat zip archive from
   https://github.com/niklasso/minisat/archive/37dc6c67e2af26379d88ce349eb9c4c6160e8543.zip

2. Save the downloaded zip archive in [...]/<TOPDIR>/MiniSat/

3. The saved archive will be found and used later when compiling NuSMV.

===
EOF
===
