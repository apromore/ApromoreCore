/* $Id: util.h,v 1.1.2.1 2010-02-04 10:41:22 nusmv Exp $ */

#ifndef UTIL_H
#define UTIL_H

/* NuSMV: added begin */
#if HAVE_CONFIG_H
# include "nusmv-config.h"

#elif defined (_MSC_VER)
/* here the source code is compiled with MSVC */
# if !defined (NUSMV_SIZEOF_VOID_P)
#  if defined (_WIN64)
#    define NUSMV_SIZEOF_VOID_P 8
#  else
#    define NUSMV_SIZEOF_VOID_P 4
#  endif
# endif
# if !defined (NUSMV_SIZEOF_LONG)
#  if defined (_WIN64)
#    define NUSMV_SIZEOF_LONG 8
#  else
#    define NUSMV_SIZEOF_LONG 4
#  endif
# endif

# if !defined (NUSMV_SIZEOF_INT)
#  define NUSMV_SIZEOF_INT 4
# endif
#endif

#ifndef EXTERN
#   ifdef __cplusplus
#	define EXTERN extern "C"
#   else
#	define EXTERN extern
#   endif
#endif
#ifndef ARGS
#   if defined(__STDC__) || defined(__cplusplus) || defined(_MSC_VER)
#	define ARGS(protos)    protos          /* ANSI C */
#   else /* !(__STDC__ || __cplusplus) || defined(_MSC_VER)*/
#	define ARGS(protos)    ()              /* K&R C */
#   endif /* !(__STDC__ || __cplusplus || defined(_MSC_VER)) */
#endif
#ifndef NULLARGS
#   if defined(__STDC__) || defined(__cplusplus) || defined(_MSC_VER)
#       define NULLARGS    (void)
#   else
#       define NULLARGS    ()
#   endif
#endif
#ifndef const
#   if !defined(__STDC__) && !defined(__cplusplus)
#       define const
#   endif
#endif

#if !defined(NUSMV_SIZEOF_VOID_P) || !defined(NUSMV_SIZEOF_LONG) || !defined(NUSMV_SIZEOF_INT)
#error Constants NUSMV_SIZEOF_VOID_P, NUSMV_SIZEOF_LONG and NUSMV_SIZEOF_INT must be defined
#endif
/* NuSMV: added end */

#ifdef __cplusplus
extern "C" {
#endif

#if defined(__GNUC__)
#   define UTIL_INLINE __inline__
#   if __GNUC__ > 2 || __GNUC_MINOR__ >= 7
#       define UTIL_UNUSED __attribute__ ((unused))
#   else
#       define UTIL_UNUSED
#   endif
#else
#   define UTIL_INLINE
#   define UTIL_UNUSED
#endif

/* NuSMV: add begin */
#if NUSMV_SIZEOF_VOID_P == 8 && NUSMV_SIZEOF_INT == 4
  #if NUSMV_SIZEOF_LONG == 8
typedef long util_ptrint;
typedef unsigned long util_ptruint;
  #else
typedef long long util_ptrint;
typedef unsigned long long util_ptruint;
  #endif
#else
typedef int util_ptrint;
typedef unsigned int util_ptruint;
#endif
  /* WAS: #if NUSMV_SIZEOF_VOID_P == 8 && NUSMV_SIZEOF_INT == 4
          typedef long util_ptrint;
          typedef unsigned long util_ptruint;
          #else
          typedef int util_ptrint;
          typedef unsigned int util_ptruint;
          #endif */
/* NuSMV: add end */

/* #define USE_MM */		/* choose libmm.a as the memory allocator */

/* these are too entrenched to get away with changing the name */
#define strsav		util_strsav

#if NUSMV_HAVE_UNISTD_H
#include <unistd.h>
#endif

extern char *optarg;
extern int optind, opterr;

#define NIL(type)		((type *) 0)

#if defined(USE_MM) || defined(MNEMOSYNE)
/*
 *  assumes the memory manager is either libmm.a or libmnem.a
 *	libmm.a:
 *	- allows malloc(0) or realloc(obj, 0)
 *	- catches out of memory (and calls MMout_of_memory())
 *	- catch free(0) and realloc(0, size) in the macros
 *	libmnem.a:
 *	- reports memory leaks
 *	- is used in conjunction with the mnemalyse postprocessor
 */
#ifdef MNEMOSYNE
#include "mnemosyne.h"
#define ALLOC(type, num)	\
    ((num) ? ((type *) malloc(sizeof(type) * (num))) : \
	    ((type *) malloc(sizeof(long))))
#else
#define ALLOC(type, num)	\
    ((type *) malloc(sizeof(type) * (num)))
#endif
#define REALLOC(type, obj, num)	\
    (obj) ? ((type *) realloc((char *) obj, sizeof(type) * (num))) : \
	    ((type *) malloc(sizeof(type) * (num)))
#define FREE(obj)		\
    ((obj) ? (free((char *) (obj)), (obj) = 0) : 0)
#else
/*
 *  enforce strict semantics on the memory allocator
 *	- when in doubt, delete the '#define USE_MM' above
 */
#define ALLOC(type, num)	\
    ((type *) MMalloc(sizeof(type) * (size_t) (num)))
#define REALLOC(type, obj, num)	\
    ((type *) MMrealloc((char *) (obj), sizeof(type) * (size_t) (num)))
#define FREE(obj)		\
    ((obj) ? (free((char *) (obj)), (obj) = 0) : 0)
#endif


/* Ultrix (and SABER) have 'fixed' certain functions which used to be int */
#if defined(ultrix) || defined(SABER) || defined(aiws) || defined(hpux) || defined(apollo) || defined(__osf__) || defined(__SVR4) || defined(__GNUC__)
#define VOID_OR_INT void
#define VOID_OR_CHAR void
#else
#define VOID_OR_INT int
#define VOID_OR_CHAR char
#endif


/* No machines seem to have much of a problem with these */
#include <stdio.h>
#include <ctype.h>


/* Some machines fail to define some functions in stdio.h */
#if !defined(__STDC__) && !defined(__cplusplus) && !defined(_MSC_VER)
extern FILE *popen(), *tmpfile();
extern int pclose();
#endif

/* snprintf is not available under MSVC */
#if defined(_MSC_VER)
#define snprintf \
	sprintf_s
#endif

/* most machines don't give us a header file for these */
#if (defined(__STDC__) || defined(__cplusplus) || defined(ultrix) || defined(_MSC_VER)) && !defined(MNEMOSYNE) || defined(__SVR4)
# include <stdlib.h>
#else
# ifndef _IBMR2
    extern VOID_OR_INT abort(), exit();
# endif
# if !defined(MNEMOSYNE) && !defined(_IBMR2)
    extern VOID_OR_INT free (void *);
    extern VOID_OR_CHAR *malloc(), *realloc();
# endif
  extern char *getenv();
  extern int system();
  extern double atof();
#endif


/* some call it strings.h, some call it string.h; others, also have memory.h */
#if defined(__STDC__) || defined(__cplusplus) || defined(_IBMR2) || defined(ultrix) || defined(_MSC_VER)
#include <string.h>
#else
/* ANSI C string.h -- 1/11/88 Draft Standard */
extern char *strcpy(), *strncpy(), *strcat(), *strncat(), *strerror();
extern char *strpbrk(), *strtok(), *strchr(), *strrchr(), *strstr();
extern int strcoll(), strxfrm(), strncmp(), strlen(), strspn(), strcspn();
extern char *memmove(), *memccpy(), *memchr(), *memcpy(), *memset();
extern int memcmp(), strcmp();
#endif


#if defined(__STDC__) || defined(_MSC_VER)
#include <assert.h>
#else
#if !defined(NDEBUG)
#undef assert
#define assert(ex) {\
    if (! (ex)) {\
	(void) fprintf(stderr,\
	    "Assertion failed: file %s, line %d\n\"%s\"\n",\
	    __FILE__, __LINE__, "ex");\
	(void) fflush(stdout);\
	abort();\
    }\
}
#elif !defined(assert)
#define assert(ex) ;
#endif
#endif


#define fail(why) {\
    (void) fprintf(stderr, "Fatal error: file %s, line %d\n%s\n",\
	__FILE__, __LINE__, why);\
    (void) fflush(stdout);\
    abort();\
}


#ifdef lint
#undef putc			/* correct lint '_flsbuf' bug */
#undef ALLOC			/* allow for lint -h flag */
#undef REALLOC
#define ALLOC(type, num)	(((type *) 0) + (num))
#define REALLOC(type, obj, num)	((obj) + (num))
#endif


/* These arguably do NOT belong in util.h */
/* NuSMV: added begin */
#ifndef ABS
/* NuSMV: added end */
#define ABS(a)			((a) < 0 ? -(a) : (a))
/* NuSMV: added begin */
#endif
#ifndef MAX
/* NuSMV: added end */
#define MAX(a,b)		((a) > (b) ? (a) : (b))
/* NuSMV: added begin */
#endif
#ifndef MIN
/* NuSMV: added end */
#define MIN(a,b)		((a) < (b) ? (a) : (b))
/* NuSMV: added begin */
#endif
/* NuSMV: added end */


/* NuSMV: added begin */
/**Macro**********************************************************************
  Synopsis     [This is a portable prefix to print size_t valus with printf]
  Description  [Use this prefix when printinf size_t values with printf.
  Warning! This macro is not prefixed with '%']
  SideEffects  []
  SeeAlso      []
******************************************************************************/
#ifndef PRIuPTR
# if !NUSMV_HAVE_INTTYPES_H
#  if (defined __MINGW32__) || (defined __CYGWIN__)
#   ifdef _WIN64
#    define PRIuPTR "I64u"
#   else
#    define PRIuPTR "u"
#   endif
#  else
#   if __WORDSIZE == 64
#    define PRIuPTR "lu"
#   else
#    define PRIuPTR "u"
#   endif
#  endif
# else /* HAVE_INTTYPES_H */
#  include <inttypes.h>
# endif
#endif
/* NuSMV: added end */


#ifndef USE_MM
extern char *MMalloc (size_t);
extern void MMout_of_memory (size_t);
extern void (*MMoutOfMemory) (size_t);
extern char *MMrealloc (char *, size_t);
#endif

extern long util_cpu_time (void);
extern int util_getopt (int, char **, char *);
extern void util_getopt_reset (void);
extern char *util_path_search (char *);
extern char *util_file_search (char *, char *, char *);
extern int util_pipefork (char **, FILE **, FILE **, int *);
extern void util_print_cpu_stats (FILE *);
extern char *util_print_time (unsigned long);
extern int util_save_image (char *, char *);
extern char *util_strsav (const char *);
extern char *util_tilde_expand (char *);
extern void util_restart (char *, char *, int);


/* util_getopt() global variables (ack !) */
extern int util_optind;
extern char *util_optarg;

extern long getSoftDataLimit (void);

#ifdef __cplusplus
}
#endif

#endif /* UTIL_H */
