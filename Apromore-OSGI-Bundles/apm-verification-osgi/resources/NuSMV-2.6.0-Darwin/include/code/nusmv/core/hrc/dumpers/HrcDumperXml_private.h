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
  \brief Private and protected interface of class 'HrcDumperXml'

  This file can be included only by derived and friend classes

*/



#ifndef __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_XML_PRIVATE_H__
#define __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_XML_PRIVATE_H__


#include "nusmv/core/hrc/dumpers/HrcDumperXml.h"
#include "nusmv/core/hrc/dumpers/HrcDumper.h" /* fix this */
#include "nusmv/core/hrc/dumpers/HrcDumper_private.h" /* fix this */
#include "nusmv/core/utils/utils.h"


/*!
  \brief HrcDumperXml class definition derived from
               class HrcDumper

  

  \sa Base class HrcDumper
*/

typedef struct HrcDumperXml_TAG
{
  /* this MUST stay on the top */
  INHERITS_FROM(HrcDumper);

  /* -------------------------------------------------- */
  /*                  Private members                   */
  /* -------------------------------------------------- */


  /* -------------------------------------------------- */
  /*                  Virtual methods                   */
  /* -------------------------------------------------- */

} HrcDumperXml;



/* ---------------------------------------------------------------------- */
/* Constants                                                              */
/* ---------------------------------------------------------------------- */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define SMV_XSD_NS  "http://es.fbk.eu/xsd"


/* ---------------------------------------------------------------------- */
/* Macros                                                                 */
/* ---------------------------------------------------------------------- */
#undef _HRC_DUMP_STR_NL
#define _HRC_DUMP_STR_NL(x)                                           \
  {                                                                   \
    hrc_dumper_dump_indent(self);                                     \
    fprintf(self->fout, x);                                           \
    if (self->use_indentation) {                                      \
      hrc_dumper_nl(self);                                            \
    }                                                                 \
  }

#undef _HRC_DUMP_NL
#define _HRC_DUMP_NL()                                                \
  {                                                                   \
    if (self->use_indentation) {                                      \
      hrc_dumper_nl(self);                                            \
    }                                                                 \
  }

#define _HRC_DUMP_XML_TAG_BEGIN(t) \
  {                                \
  _HRC_DUMP_STR("<");              \
  _HRC_DUMP_STR(t);                \
  _HRC_DUMP_STR(">");              \
  }

#define _HRC_DUMP_XML_TAG_END(t)   \
  {                                \
    _HRC_DUMP_STR("</");           \
    _HRC_DUMP_STR(t);              \
    _HRC_DUMP_STR_NL(">");         \
  }

#define _HRC_DUMP_XML_TAG_BEGIN_END(t, s)       \
  {                                             \
    _HRC_DUMP_XML_TAG_BEGIN(t);                 \
    if ((char*) NULL != (char*) s) {            \
      _HRC_DUMP_STR(s);                         \
    }                                           \
    _HRC_DUMP_XML_TAG_END(t);                   \
  }

#define _HRC_DUMP_XML_NODE(n)                               \
  hrc_dumper_xml_dump_escaped_node(HRC_DUMPER_XML(self), n)

#define _HRC_DUMP_XML_NODE_BEGIN_END(t, n) \
  {                                        \
    _HRC_DUMP_STR("<");                    \
    _HRC_DUMP_STR(t);                      \
    _HRC_DUMP_STR(">");                    \
    _HRC_DUMP_XML_NODE(n);                 \
    _HRC_DUMP_STR("</");                   \
    _HRC_DUMP_STR(t);                      \
    _HRC_DUMP_STR_NL(">");                 \
  }


/* ---------------------------------------------------------------------- */
/* Private methods to be used by derivated and friend classes only         */
/* ---------------------------------------------------------------------- */

/*!
  \methodof HrcDumperXml
  \brief The HrcDumperXml class private initializer

  The HrcDumperXml class private initializer

  \sa HrcDumperXml_create
*/
void hrc_dumper_xml_init(HrcDumperXml_ptr self,
                                const NuSMVEnv_ptr env,
                                FILE* fout);

/*!
  \methodof HrcDumperXml
  \brief The HrcDumperXml class private deinitializer

  The HrcDumperXml class private deinitializer

  \sa HrcDumper_destroy
*/
void hrc_dumper_xml_deinit(HrcDumperXml_ptr self);

/*!
  \methodof HrcDumper
  \brief 

  
*/
void hrc_dumper_xml_dump_snippet(HrcDumper_ptr self,
                                        HrcDumperSnippet snippet,
                                        const HrcDumperInfo* info);

/*!
  \methodof HrcDumper
  \brief Dumps a comment

  
*/
void hrc_dumper_xml_dump_comment(HrcDumper_ptr self,
                                        const char* msg);


#endif /* __NUSMV_CORE_HRC_DUMPERS_HRC_DUMPER_XML_PRIVATE_H__ */
