/* ---------------------------------------------------------------------------


  This file is part of the ``hrc.dumpers'' package of NuSMV version 2. 
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
  \brief Public interface of class 'HrcDumperXml'

  \todo: Missing description

*/



#ifndef __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_XML_H__
#define __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_XML_H__


#include "nusmv/core/hrc/dumpers/HrcDumper.h" /* fix this */ 
#include "nusmv/core/utils/utils.h"

/*!
  \struct HrcDumperXml
  \brief Definition of the public accessor for class HrcDumperXml

  
*/
typedef struct HrcDumperXml_TAG*  HrcDumperXml_ptr;

/*!
  \brief To cast and check instances of class HrcDumperXml

  These macros must be used respectively to cast and to check
  instances of class HrcDumperXml
*/
#define HRC_DUMPER_XML(self) \
         ((HrcDumperXml_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define HRC_DUMPER_XML_CHECK_INSTANCE(self) \
         (nusmv_assert(HRC_DUMPER_XML(self) != HRC_DUMPER_XML(NULL)))



/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \methodof HrcDumperXml
  \brief The HrcDumperXml class constructor

  The HrcDumperXml class constructor

  \sa HrcDumper_destroy
*/
HrcDumperXml_ptr HrcDumperXml_create(const NuSMVEnv_ptr env,
                                            FILE* fout);


/**AutomaticEnd***************************************************************/



#endif /* __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_XML_H__ */
