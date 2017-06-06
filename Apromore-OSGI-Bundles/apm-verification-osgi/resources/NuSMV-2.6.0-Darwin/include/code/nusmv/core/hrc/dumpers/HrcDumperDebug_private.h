/* ---------------------------------------------------------------------------


  This file is part of the ``hrc.dumper'' package of NuSMV version 2. 
  Copyright (C) 2011 by FBK-irst. 

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
  \brief Private and protected interface of class 'HrcDumperDebug'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_DEBUG_PRIVATE_H__
#define __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_DEBUG_PRIVATE_H__


#include "nusmv/core/hrc/dumpers/HrcDumperDebug.h" 
#include "nusmv/core/hrc/dumpers/HrcDumper.h" 
#include "nusmv/core/hrc/dumpers/HrcDumper_private.h"
#include "nusmv/core/utils/utils.h" 


/*!
  \brief HrcDumperDebug class definition derived from
               class HrcDumper

  

  \sa Base class HrcDumper
*/

typedef struct HrcDumperDebug_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(HrcDumper); 

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} HrcDumperDebug;



/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only        */
/* ---------------------------------------------------------------------- */

/*!
  \methodof HrcDumperDebug
  \brief The HrcDumperDebug class private initializer

  The HrcDumperDebug class private initializer

  \sa HrcDumperDebug_create
*/
void hrc_dumper_debug_init(HrcDumperDebug_ptr self,
                                  const NuSMVEnv_ptr env,
                                  FILE* fout);

/*!
  \methodof HrcDumperDebug
  \brief The HrcDumperDebug class private deinitializer

  The HrcDumperDebug class private deinitializer

  \sa HrcDumper_destroy
*/
void hrc_dumper_debug_deinit(HrcDumperDebug_ptr self);

/*!
  \methodof HrcDumper
  \brief 

  
*/
void hrc_dumper_debug_dump_snippet(HrcDumper_ptr self,
                                          HrcDumperSnippet snippet,
                                          const HrcDumperInfo* info);



#endif /* __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_DEBUG_PRIVATE_H__ */
