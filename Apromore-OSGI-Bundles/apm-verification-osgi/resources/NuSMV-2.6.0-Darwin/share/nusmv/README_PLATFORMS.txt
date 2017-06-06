This file contains specific information for building NuSMV under the
most used platforms. NuSMV have been successfully built and tested
under these platforms/operating systems:

1) Pc/Linux 32 and 64 bit architectures
2) Apple Mac/Mac OS X
3) Pc/Microsoft Windows 32 and 64 bit architectures
4) Ultrasparc 5/Solaris

If you experience any problem while trying to build or execute NuSMV,
please get in touch with us using the e-mail address
<nusmv-users@fbk.eu>.

In the following, we assume that the archive is saved in the <TOPDIR> root
directory.

======================================================================
1) Linux distributions
   -------------------

   GNU/Linux is the NuSMV's Team development platform, so compiling
   NuSMV under a Linux distribution is the most easy way, and should
   be the preferred platform by users who are interested in developing
   extensions of NuSMV. Default tools chain "cmake && make && make
   install" should build the system. File README contains more
   detailed information about standard build steps.


======================================================================
2) Mac OS X
   --------

   NuSMV has been tested on Mac OS X 10.3 (Panther) and 10.10 (Yosemite).

   It has to be noticed that, the runtime statistics provided by NuSMV
   are not available under Mac OS X.

 3.1) Binary Distribution
      -------------------

      The distributed binary executable is fully self-contained, so
      there are no external libraries needed for its execution.
      However, the executable may require some system libraries that
      depends on the version of the OS.

 3.2) Source Distribution
      -------------------

  3.2.1) Install Mac Ports
         -----------------
         This is needed to install dependencies.
         See https://guide.macports.org/

  3.2.2) Install libraries and tools within Mac Ports
         ---------------------------
         Install: python-2.7.x, flex, bison, cmake >= 2.8, patch
         Install: glibc, readline, ncurses, libxml2, libgmp

  3.2.3) Build NuSMV
         -----------
         Follow instruction given for Linux distributions.

 3.3) Generating Documentation
      ------------------------

      To generate part of the documentation (the user manual and the
      tutorial), LaTeX is needed. Before installing and configuring a
      version of it, you might consider that the NuSMV binary
      distribution already provides full documentation and help files.

      LaTeX: The currently governing Latex installation for Mac OSX is
             MacTex. See https://tug.org/mactex

      To generate the development help out of source code, doxygen is
      required (http://www.doxygen.org/)


======================================================================
3) Microsoft Windows
   -----------------

 3.1) Binary distribution
      -------------------

      Binary distribution has been tested for MS Windows versions XP,
      Vista, 7 and 8.

      Untar binary package of NuSMV into c:\nusmv, by using Winzip for
      example.

      After the unrolling, c:\nusmv must contain directories:
      c:\nusmv
          |- bin
          |- lib
          |- share
          |- include

      Append to the environment variable PATH the direcory "c:\nusmv\bin".

 3.2) Source distribution
      -------------------
      NuSMV has been tested with two solutions on Microsoft Windows
      operating systems: MinGW (http://www.mingw.org/) and Cygwin
      (http://www.cygwin.com) environments. In order to build
      documentation and help files, latex and doxygen are
      required. Section 3.3 gives a few references about them.

 3.2.1) MinGW environment
        -----------------
        NOTE: there exist 32 and 64 bit MinGW environments. Here only
              64 bit is described, although NuSMV can be built with
              both 32 and 64 environments.

        MinGW is a POSIX emulation environment, that provides tools
        and system libraries that allow NuSMV to be ported natively
        under Windows operating systems. 'Natively' means that once
        built, NuSMV will not require the MinGW environment to be run.

        MinGW can be used to compile native 32 and 64 bit executable
        for Windows, and can be used under several platforms, thus
        also allowing for cross-compilation when needed.

        To install and configure the MinGW environment under Windows,
        the following steps must be carried out. Each of these steps
        may be performed as normal users, i.e. without any system
        administrator privileges.

     1. Access MSYS2 page at
        http://mingw-w64.org/doku.php/download/msys2. Download MSYS2
        and follow the installation instructions that can be found at
        that pages.

        We suggest to install the MSYS environment into the default
        folder, i.e. "C:\msys64" at writing time (%MSYS_FOLDER% from
        here on). It is strongly recommended that you do not use an
        installation path containing blanks. The build of NuSMV
        might not be successful in this case.

        Update the system, following the msys2 installation
        instructions.

     2. Using the packaging system provided by msys2 (pacman), install:

        mingw64:
        $> pacman -S mingw-w64-x86_64-gcc mingw-w64-x86_64-toolchain

     3. Open a new mingw-w64 win64 shell from the programs start menu.
        3.1 check that gcc can be found:
        $> gcc -v

        3.2 Install the following packages:

        Tools: cmake (>=2.8), make, python2 (2.7.x), patch, bison

        $> pacman -S bison
        .. to install bison


 3.2.2) Cygwin environment
        ------------------
        Cygwin is a UNIX emulation environment, that is, it makes the
        standard UNIX API available also on Windows.  Further
        information and installation instructions for Cygwin can be
        found at

        http://www.cygwin.com/

        Install: python-2.7.x, flex, bison, cmake >= 2.8, patch
        Install: glibc, readline, ncurses, libxml2, libgmp

        Once the Cygwin platform has been installed with all
        development needed tools and libraries, NuSMV can be compiled
        following the instructions given in the "BUILDING NUSMV"
        section. Notice that resultant executables and libraries will
        require the Cygwin Runtime Environment installed to work
        properly.


 3.2.3) Other (optional) packages
        -------------------------
        To generate documentation and help files, a few additional
        packages and programs are needed. Before installing and
        configuring these additional programs, you might consider that
        binary distributions already provide full documentation and help
        files.

      latex: There are several implementations for Windows, one can be
             found at:
             http://www.miktex.org

      doxygen: This is used when generating the programmer's manual.
               See www.doxygen.org


 3.2.4) Building of NuSMV
        -----------------
        As for Linux, both MinGW and Cygwin environments allow to follow
        the standard build steps:

        1) Untar the NuSMV source distribution archive, for example by
           using Winzip.

        2) Proceed like described in file README, from the 3rd step on.


======================================================================
4) Sun Solaris
   -----------
   The compilation of NuSMV under Solaris should be as straightforward
   as under Linux. It might be the case that under certain system
   configurations the native Sun compiler would be required. To allow
   for the use of a different compiler in the building chain it is
   just sufficient to assign a value to the cmake variables when
   configuring:

   $> pwd
      [...]/<TOPDIR>/NuSMV/build
   $> cmake .. -DCMAKE_C_COMPILER=/usr/bin/cc -DCMAKE_CXX_COMPILER=/usr/bin/c++

   Following call to 'make' will then be using the specified compiler
   'cc' as default C compiler, and 'c++' as a default C++ compiler.
