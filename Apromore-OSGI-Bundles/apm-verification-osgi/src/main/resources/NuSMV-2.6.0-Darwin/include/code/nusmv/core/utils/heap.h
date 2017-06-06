#ifndef __NUSMV_CORE_UTILS_HEAP_H__
#define __NUSMV_CORE_UTILS_HEAP_H__

#if HAVE_CONFIG_H
#  include "nusmv-config.h"
#endif

#include "nusmv/core/utils/defs.h"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define HEAP_MAXLENGTH_INIT 31

/*!
  \struct heap_
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct heap_ * heap;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
heap heap_create(void);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void heap_destroy(heap h);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void heap_add(heap h, float val, void * el);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
int heap_isempty(heap h);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void* heap_getmax(heap h);

#endif
