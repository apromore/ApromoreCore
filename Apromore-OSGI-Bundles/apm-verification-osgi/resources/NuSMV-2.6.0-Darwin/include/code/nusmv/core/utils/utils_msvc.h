/* ---------------------------------------------------------------------------

  This file is part of the ``utils'' package of NuSMV version 2.
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
  \author Roberto Cavada

  \brief Code that need including windows.h, specific and compiled
  only with MSVC.

  windows.h exports symbols that clash with some of the one exported
  by symbols.h and defs.h. In this file none of those files are
  imported, only windows.h to separate clearly the two namespaces.
*/

#ifndef __NUSMV_CORE_UTILS_MSVC_H__
#define __NUSMV_CORE_UTILS_MSVC_H__

int Utils_msvc_files_are_the_same(const char* fname1, const char* fname2);

#endif  /* __NUSMV_CORE_UTILS_MSVC_H__ */
