This is version 2 of NuSMV, the New Symbolic Model Verifier.

----------------------------------------------------------------------

NuSMV is a re-implementation and extension of SMV, the first model
checker based on BDDs. It has been designed to be an open architecture
for model checking, which can be reliably used for the verification of
industrial designs, as a core for custom verification tools, and as a
test-bed for formal verification techniques.

NuSMV version 2 extends NuSMV with new model checking algorithms and
techniques. It combines classical BDD-based symbolic techniques with
SAT-based techniques. It also presents other new features: for
instance, it allows for a more powerful manipulation of multiple
models; it can generate flat models for the whole language; it allows
for cone of influence reduction.

The BDD-based model checking component exploits the CUDD library
developed by Fabio Somenzi at Colorado University. The SAT-based
model checking component includes an RBC-based Bounded Model
Checker, connected to a SAT solver to be compiled separately
(instructions and building support are batteries included in
NuSMV, details are underneath).

The currently available SAT solvers are:
+ The MiniSat SAT library developed by Niklas Een or Niklas Sorensson.
+ The ZCHAFF SAT library developed by the Princeton University

NuSMV version 2 is distributed with an OpenSource license, namely the
GNU Lesser General Public License version 2 (LGPL-2). The aim is to
provide a publicly available state-of-the-art symbolic model
checker. With the OpenSource development model, a whole community
participates in the development of a software systems, with a
distributed team and independent peer review. This may result in a
rapid system evolution, and in increased software quality and
reliability: for instance, the OpenSource model has boosted the
take-up of notable software systems, such as Linux and Apache. With
the NuSMV OpenSource project, we would like to reach the same goals
within the model checking community, opening the development of NuSMV.

You can find further details on NuSMV 2 and on the NuSMV project in
paper:

  A. Cimatti, E. Clarke, E. Giunchiglia, F. Giunchiglia,
  M. Pistore, M. Roveri, R. Sebastiani, and A. Tacchella.
  "NuSMV 2: An OpenSource Tool for Symbolic Model Checking".
  In Proc. CAV'02, LNCS. Springer Verlag, 2002.


Please contact <nusmv-users@fbk.eu> for further information on
NuSMV. Please contact <nusmv@fbk.eu> for getting in touch with
the NuSMV development staff.


===========
0. CONTENTS
===========
 1. Copyright
 2. Useful links
 3. Building NuSMV
 4. Platforms
 5. Installing NuSMV
 6. Binary distribution
 7. Files in the NuSMV distribution


============
1. COPYRIGHT
============

NuSMV version 2 (NuSMV 2 in short) is licensed under the GNU Lesser
General Public License (LGPL in short). File LGPL-2.1 contains a copy
of the License.

The aim of the NuSMV OpenSource project is to allow the whole model
checking community to participate to the development of NuSMV. To this
purpose, we have chosen a license that:
1) is "copyleft", that is, it requires that anyone who improves the
   system has to make the improvements freely available;
2) permits to use the system in research and commercial applications,
   without restrictions.

In brief, the LGPL license allows anyone to freely download, copy,
use, modify, and redistribute NuSMV 2, proviso that any modification
and/or extension to the library is made publicly available under the
terms of LGPL.

The license also allows the usage of the NuSMV 2 as part of a larger
software system *without* being obliged to distributing the whole
software under LGPL. Also in this case, the modification to NuSMV 2
(*not* to the larger software) should be made available under LGPL.

The precise terms and conditions for copying, distribution and
modification can be found in file LGPL-2.1. You can contact
<nusmv@fbk.eu> if you have any doubt or comment on the
license.

Different partners have participated the initial release of
NuSMV 2. Every source file in the NuSMV 2 distribution contains a
header that acknowledges the developers and the copyright holders for
the file. In particular:

 * CMU and ITC-IRST contributed the source code on NuSMV version 1.
 * ITC-IRST has also developed several extensions for NuSMV 2.
 * ITC-IRST and the University of Trento have developed
   the SAT-based Bounded Model Checking package on NuSMV 2.
 * the University of Genova has contributed SIM, a state-of-the-art
   SAT solver used until version 2.5.0, and the RBC package use in
   the Bounded Model Checking algorithms.
 * Fondazione Bruno Kessler (FBK) is currenlty the main
   developer and maintainer of NuSMV 2.

The NuSMV team has also received several contributions for different
part of the system. In particular:

 * Ariel Fuxman <afuxman@cs.toronto.edu> has extended the LTL to SMV
   tableau translator to the past fragment of LTL
 * Rik Eshuis <eshuis@cs.utwente.nl> has contributed a strong fairness
   model checking algorithm for LTL specifications
 * Dan Sheridan <dan.sheridan@contact.org.uk> has contributed several
   extensions and enhancements to the Bounded Model Checking algorithms.


*******************************************************************************
*                             Cudd License                                    *
*******************************************************************************

*******************************************************************************
Copyright (c) 1995-2004, Regents of the University of Colorado

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

Neither the name of the University of Colorado nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************


*******************************************************************************
*             ZChaff64 2007.03.12 License (linked optionally)                 *
*******************************************************************************

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



*******************************************************************************
*                    MiniSat release 2.2 License                              *
*******************************************************************************

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



================
2. USEFUL LINKS
================

The NuSMV home page:

        http://nusmv.fbk.eu/

The NuSMV mailing list page:

        http://nusmv.fbk.eu/mail.html

The most recent versions of NuSMV:

        http://nusmv.fbk.eu/download.html

The home page of nuXmv, the extension over NuSMV to allow for
synchronous infinite-state systems:

        http://nuxmv.fbk.eu

The CUDD home page:

        http://vlsi.colorado.edu/~fabio/CUDD/

The MiniSat page:

        http://MiniSat.se/

The ZCHAFF home page:

        http://www.princeton.edu/~chaff/zChaff.html

The OpenSource site:

        http://www.opensource.org/

The GNU General Public License home page:

        http://www.gnu.org/copyleft/



==================================
3. BUILDING NUSMV FROM SOURCE CODE
==================================

To compile NuSMV 2 follow the instructions below.

NOTE: NuSMV is also distributed already compiled for the most common
operating systems and architectures. See the "BINARY DISTRIBUTION"
section for instructions on how to install an already compiled
package.

NOTE: platform specific instructions for building NuSMV are contained
in the "PLATFORMS" section.


3.1 Requirements
----------------

To build NuSMV, CUDD, MiniSat and/or ZChaff as them are distributed,
you will need:

  * An ANSI C compiler (gcc will do, as will several versions of cc)
  * An ANSI C++ compiler (g++ will do)
  * GNU Flex version 2.5 or greater
  * GNU Bison version 1.22 or greater
  * cmake utility version 2.8 or greater
  * GNU make utility version 3.74 or greater
  * GNU patch utility
  * GNU tar and gzip utilities
  * Library libxml2
  * Approximately 45 MB of free disk space for building the system
    (76MB with optional documentation)
  * Approximately 30 MB of free disk space for the installation

A few tools and libraries are required to have full features provided,
but they are not strictly required to build NuSMV.
In particular:

   * A fully working 'latex' environment for generation of documentation
   * Program 'doxygen' for generation of the programmer's documentation
   * Library 'readline' for a shell usability improvement.


3.2 Building steps
------------------

1. Download the most recent versions of NuSMV's source code from the
   address above into a convenient directory, e.g., /tmp.

2. Move to the directory where you would like to build NuSMV and unpack the
   distributions:

        % cd /home/nusmv                                # for example
        % gzip -dc /tmp/NuSMV-2.6.0.tar.gz | tar xf -

   This will unpack in a new directory "<TOPDIR>"

3. MiniSat and (on request) ZCHAFF solvers are downloaded and compiled
   automatically when compiling NuSMV. However if download is not
   possible during compilation, it is possible to download them
   separately before compiling NuSMV. See For instructions on how to
   download, compile and link MiniSat and ZChaff with NuSMV, read
   files README.MiniSat' and 'README.zChaff', respectively.

   IMPORTANT: at least one SAT solver must be provided.

4. Move into the [...]/<TOPDIR>/NuSMV directory and:
   4.1 Create a directory for building, and enter into it
       % pwd
       [...]/<TOPDIR>/NuSMV
       % mkdir build
       % cd
       % pwd
       [...]/<TOPDIR>/NuSMV/build

   4.2 Configure by invoking cmake
       % cmake ..
       -- The C compiler identification is GNU 4.4.7
       -- The CXX compiler identification is GNU 4.4.7
       [...]
       -- Build files have been written to: [...]/<TOPDIR>/NuSMV/build

   OPTIONAL: MiniSat is enabled by default, whilst for licensing
   issues zChaff is disabled by default.  If you want to use a
   different setting, set ENABLE_MINISAT and/or ENABLE_ZCHAFF cmake
   variables. Setting cmake variables can be done by editing
   configuration file [...]/<TOPDIR>/NuSMV/build.CMakeCache.txt or
   directly when invoking cmake. For example:
       % cmake .. -DENABLE_ZCHAFF=ON

   Note: Not all checks done when configuring will return
   successfully. This is normal and should not affect compilation if
   configuration ends without errors.

   Note: cmake offers a set of variables which can be used to change
   wrong guess or defaults you may not want. See
   [...]/<TOPDIR>/NuSMV/build.CMakeCache.txt for the list of variables
   and their values.

   Note: For further information see the "INSTALLING NUSMV" section
   below.

   Note: by default infinite-width words are disabled, i.e. the
   maximum width of words is limited to 64. To enable infinite width,
   set variable ENABLE_BIGNUMBERS when calling cmake:
       % cmake .. -DENABLE_BIGNUMBERS=ON
   Use of infinite-width words libgmp is required to be installed.

   Note: there are several cmake variables allowing for tailoring. See
   NuSMV/CMakeList.txt file for further information. Variables can be
   set either when invoking cmake, or by editing CMakeCache.txt file
   which is created by calling cmake the first time. Refer to the
   instructions provided with cmake for further information about how
   variables can be set.

   4.3 If configuration was successful, compile NuSMV
       % pwd
       [...]/<TOPDIR>/NuSMV/build
       % make

   This is expected to build an executable "NuSMV" in the
   [...]/<TOPDIR>/NuSMV/build/bin directory.

   NuSMV tries finding file "master.nusmvrc" in the directory given by
   the environment variable NUSMV_LIBRARY_PATH. Set this to the
   "share" directory in the source tree. For example for bash shell:

        % export NUSMV_LIBRARY_PATH=[...]/<TOPDIR>/NuSMV/share/nusmv

5. OPTIONAL: Verify that NuSMV works by running it on some of the
   examples included in the distribution:

        % make check


============
4. PLATFORMS
============

NuSMV has been tested in a range of architecture/operating system
combinations. It has been tested on PC Intel, Apple Mac and Sun
architectures, with different versions and distributions of Linux,
Solaris, Windows and Mac OS X.

NuSMV-2.4.2 and later versions can be compiled natively at 64 bits.

File README_PLATFORMS.txt provides detailed information about each
supported platform.


===================
5. INSTALLING NUSMV
===================

Administrators and people who want to discard the source after building
NuSMV will want to install NuSMV in a central area:

* To install the NuSMV executable, library, headers, and help files, type,
  while in the nusmv directory,

        % make install

  By default, this will put binaries, libraries, headers, and help files
  in /usr/local/bin, /usr/local/lib, /usr/local/include, and /usr/local/share
  respectively.  To choose a different location, provide a default prefix
  when you invoke configure, e.g., to install in /opt/NuSMV/bin, etc.,
  set variable CMAKE_INSTALL_PREFIX. E.g.:
        % pwd
        [...]/<TOPDIR>/NuSMV/build
        % cmake .. -DCMAKE_INSTALL_PREFIX=/opt/nusmv

  when configuring NuSMV.

  "make clean" removes all the files generated during "make".  This is
  useful when you want to rebuild NuSMV with a different prefix, when you
  want to rebuild with different compiler options, etc.

  The path "${CMAKE_INSTALL_PREFIX}/share/nusmv" is hard-coded into NUSMV.
  In this directory, NuSMV expects to find "master.nusmvrc".
  This may be overridden by setting the environment variable
  "NUSMV_LIBRARY_PATH" to the name of an alternate directory, e.g. for
  bash shell:

        % export NUSMV_LIBRARY_PATH=/opt/nusmv/share/nusmv


======================
6. BINARY DISTRIBUTION
======================

NuSMV is also distributed already compiled. To install the binary
distribution of NuSMV follows the instructions below:

1. Download the most recent versions of NuSMV from the addresses
   listed above into a convenient directory, (e.g., /tmp).

2. Move to the root directory where you want to install NuSMV. Let's
   say "/opt" and untar the distribution:

        % cd /opt
        % gzip -dc /tmp/NuSMV-X.Y.Z-Linux-i686.tar.gz | tar xf -

   This command will create a directory "/opt/nusmv-X.Y.Z"
   containing the NuSMV files (X.Y.Z is the release number).

3. Optionally, add the directory /opt/nusmv-X.Y.Z/bin to your command
   search PATH environment variable. E.g. for bash shell:

        % export PATH=${PATH}:/opt/nusmv-X.Y.Z/bin

4. Optionally, set the NUSMV_LIBRARY_PATH environment
   variable. E.g. for bash shell:

        % export NUSMV_LIBRARY_PATH=/opt/nusmv-X.Y.Z/share/nusmv

5. Now you are ready to run and enjoy NuSMV.
       % NuSMV -int   # to run the interactive mode


==================================
7. FILES IN THE NUSMV DISTRIBUTION
==================================

cudd-2.4.1.1/       The modified CUDD source tree.
                    Further information on CUDD can be found in the
                    README file in this directory.

MiniSat/            Support for downloading and compiling MiniSAT. Does
                    not contains the source code of MiniSat, which is
                    not distributed with NuSMV.

nusmv/              The NuSMV source tree.

  AUTHORS           The NuSMV's team members list and other contributors.

  CMakeFile.txt     The top-level input file of the cmake building system.

  INSTALL           Contains generic installation instructions.

  NEWS              Contains the changelog for the different releases.

  LGPL-2.1          The GNU Lesser General Public License.

  README.txt        This file.

  README_PLATFORMS.txt
                    Platform-related information

  README_zChaff.txt
                    Contains information about how to embed zChaff into NuSMV.

  README_MiniSat.txt
                    Contains information about how to embed MiniSat into NuSMV.

  cmake/*           Services used by cmake building system.

  code/*            Source code packages of the NuSMV system.

  contrib/          A collection of useful programs and scripts.
                    See contrib/README for further information.

  doc/FAQ           A collection of Frequently Asked Questions

  doc/*/*           Various NuSMV manuals and tutorial.

  examples/         Various collected examples.

  share/nusmv/master.nusmvrc
                    A NuSMV script designed to be ran automatically at
                    start-up: contains aliases for commonly used
                    commands and some default parameter settings.

zchaff/             Support for downloading and compiling ZChaff. Does
                    not contains the source code of ZChaff, which is
                    not distributed with NuSMV.

===
EOF
===
