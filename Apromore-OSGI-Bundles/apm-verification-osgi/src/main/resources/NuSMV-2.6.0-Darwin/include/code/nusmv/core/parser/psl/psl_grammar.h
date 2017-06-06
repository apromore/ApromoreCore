/* A Bison parser, made by GNU Bison 2.3.  */

/* Skeleton interface for Bison's Yacc-like parsers in C

   Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003, 2004, 2005, 2006
   Free Software Foundation, Inc.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA 02110-1301, USA.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     TKEOF = 258,
     TKSTRING = 259,
     TKERROR = 260,
     TKSTRUCT = 261,
     TKNUMBER = 262,
     TKREALNUMBER = 263,
     TKWORDNUMBER = 264,
     TKBASENUMBER = 265,
     TKTRUE = 266,
     TKFALSE = 267,
     TKUNSIGNEDWORDNUMBER = 268,
     TKSIGNEDWORDNUMBER = 269,
     TKINCONTEXT = 270,
     TKEQDEF = 271,
     TKNAME = 272,
     TKSERE = 273,
     TKSERECONCAT = 274,
     TKSEREFUSION = 275,
     TKSERECOMPOUND = 276,
     TKSEREREPEATED = 277,
     TKCONS = 278,
     TKCONCATENATION = 279,
     TKREPLPROP = 280,
     TKARRAY = 281,
     TKCONTEXT = 282,
     TKATOM = 283,
     TKFAILURE = 284,
     TKITE = 285,
     TKVUNIT = 286,
     TKVMODE = 287,
     TKVPROP = 288,
     TKSTRONG = 289,
     TKDEFPARAM = 290,
     TKINHERIT = 291,
     TKFAIRNESS = 292,
     TKCONST = 293,
     TKBEGIN = 294,
     TKEND = 295,
     TKPARAMETER = 296,
     TKTASK = 297,
     TKENDTASK = 298,
     TKFORK = 299,
     TKJOIN = 300,
     TKSUPPLY0 = 301,
     TKSUPPLY1 = 302,
     TKSTRONG0 = 303,
     TKPULL0 = 304,
     TKWEAK0 = 305,
     TKHIGHZ0 = 306,
     TKSTRONG1 = 307,
     TKPULL1 = 308,
     TKWEAK1 = 309,
     TKHIGHZ1 = 310,
     TKINPUT = 311,
     TKOUTPUT = 312,
     TKINOUT = 313,
     TKDEFAULT_CLOCK = 314,
     TKDEFAULT_COLON = 315,
     TKDEASSIGN = 316,
     TKDISABLE = 317,
     TKENDSPECIFY = 318,
     TKFOR = 319,
     TKINITIAL = 320,
     TKSPECIFY = 321,
     TKWAIT = 322,
     TKFOREVER = 323,
     TKREPEAT = 324,
     TKWHILE = 325,
     TKENDMODULE = 326,
     TKENDFUNCTION = 327,
     TKWIRE = 328,
     TKTRI = 329,
     TKTRI1 = 330,
     TKWAND = 331,
     TKTRIAND = 332,
     TKTRI0 = 333,
     TKWOR = 334,
     TKTRIOR = 335,
     TKTRIREG = 336,
     TKREG = 337,
     TKINTEGER = 338,
     TKINF = 339,
     TKDOT = 340,
     TKENDPOINT = 341,
     TKASSIGN = 342,
     TKFORCE = 343,
     TKRELEASE = 344,
     TKPROPERTY = 345,
     TKSEQUENCE = 346,
     TKMODULE = 347,
     TKFUNCTION = 348,
     TKRESTRICT = 349,
     TKRESTRICT_GUARANTEE = 350,
     TKFORALL = 351,
     TKFORANY = 352,
     TKASSERT = 353,
     TKASSUME = 354,
     TKASSUME_GUARANTEE = 355,
     TKCOVER = 356,
     TKBOOLEAN = 357,
     TKCASE = 358,
     TKCASEX = 359,
     TKCASEZ = 360,
     TKELSE = 361,
     TKENDCASE = 362,
     TKIF = 363,
     TKNONDET = 364,
     TKNONDET_VECTOR = 365,
     TKNONDET_RANGE = 366,
     TKWNONDET = 367,
     TKBASE = 368,
     TKDOTDOT = 369,
     TKPIPEMINUSGT = 370,
     TKPIPEEQGT = 371,
     TKIDENTIFIER = 372,
     TKHIERARCHICALID = 373,
     TKLP = 374,
     TKRP = 375,
     TKLC = 376,
     TKRC = 377,
     TKLB = 378,
     TKRB = 379,
     TKCOMMA = 380,
     TKDIEZ = 381,
     TKTRANS = 382,
     TKHINT = 383,
     TKTEST_PINS = 384,
     TKALWAYS = 385,
     TKNEVER = 386,
     TKEVENTUALLYBANG = 387,
     TKWITHINBANG = 388,
     TKWITHIN = 389,
     TKWITHINBANG_ = 390,
     TKWITHIN_ = 391,
     TKWHILENOTBANG = 392,
     TKWHILENOT = 393,
     TKWHILENOTBANG_ = 394,
     TKWHILENOT_ = 395,
     TKNEXT_EVENT_ABANG = 396,
     TKNEXT_EVENT_A = 397,
     TKNEXT_EVENT_EBANG = 398,
     TKNEXT_EVENT_E = 399,
     TKNEXT_EVENTBANG = 400,
     TKNEXT_EVENT = 401,
     TKNEXT_ABANG = 402,
     TKNEXT_EBANG = 403,
     TKNEXT_A = 404,
     TKNEXT_E = 405,
     TKNEXTBANG = 406,
     TKNEXT = 407,
     TKNEXTfunc = 408,
     TKBEFOREBANG = 409,
     TKBEFORE = 410,
     TKBEFOREBANG_ = 411,
     TKBEFORE_ = 412,
     TKUNTILBANG = 413,
     TKUNTIL = 414,
     TKUNTILBANG_ = 415,
     TKUNTIL_ = 416,
     TKABORT = 417,
     TKROSE = 418,
     TKFELL = 419,
     TKPREV = 420,
     TKG = 421,
     TKXBANG = 422,
     TKX = 423,
     TKF = 424,
     TKU = 425,
     TKW = 426,
     TKEG = 427,
     TKEX = 428,
     TKEF = 429,
     TKAG = 430,
     TKAX = 431,
     TKAF = 432,
     TKA = 433,
     TKE = 434,
     TKIN = 435,
     TKUNION = 436,
     TKQUESTIONMARK = 437,
     TKCOLON = 438,
     TKSEMI = 439,
     TKPIPEPIPE = 440,
     TKAMPERSANDAMPERSAND = 441,
     TKMINUSGT = 442,
     TKLTMINUSGT = 443,
     TKPIPE = 444,
     TKTILDEPIPE = 445,
     TKOR = 446,
     TKPOSEDGE = 447,
     TKNEGEDGE = 448,
     TKCARET = 449,
     TKXOR = 450,
     TKXNOR = 451,
     TKCARETTILDE = 452,
     TKTILDECARET = 453,
     TKAMPERSAND = 454,
     TKTILDEAMPERSAND = 455,
     TKEQEQ = 456,
     TKBANGEQ = 457,
     TKEQEQEQ = 458,
     TKBANGEQEQ = 459,
     TKEQ = 460,
     TKGT = 461,
     TKGE = 462,
     TKLT = 463,
     TKLE = 464,
     TKLTLT = 465,
     TKGTGT = 466,
     TKWSELECT = 467,
     TKGTGTGT = 468,
     TKLTLTLT = 469,
     TKPLUS = 470,
     TKMINUS = 471,
     TKSPLAT = 472,
     TKSLASH = 473,
     TKPERCENT = 474,
     TKSPLATSPLAT = 475,
     TKBANG = 476,
     TKTILDE = 477,
     TKLBSPLAT = 478,
     TKLBEQ = 479,
     TKLBMINUSGT = 480,
     TKLBPLUSRB = 481,
     TKWCONCATENATION = 482,
     TKBOOL = 483,
     TKWRESIZE = 484,
     TKWSIZEOF = 485,
     TKWTOINT = 486,
     TKUWCONST = 487,
     TKBITSELECTION = 488,
     TKUMINUS = 489,
     TKSWCONST = 490,
     TKWORD1 = 491,
     TKSIGNED = 492,
     TKUNSIGNED = 493,
     TKEXTEND = 494,
     TKSTRUDLE = 495,
     TKSEREFORGR = 496,
     TKPSLSPEC = 497
   };
#endif
/* Tokens.  */
#define TKEOF 258
#define TKSTRING 259
#define TKERROR 260
#define TKSTRUCT 261
#define TKNUMBER 262
#define TKREALNUMBER 263
#define TKWORDNUMBER 264
#define TKBASENUMBER 265
#define TKTRUE 266
#define TKFALSE 267
#define TKUNSIGNEDWORDNUMBER 268
#define TKSIGNEDWORDNUMBER 269
#define TKINCONTEXT 270
#define TKEQDEF 271
#define TKNAME 272
#define TKSERE 273
#define TKSERECONCAT 274
#define TKSEREFUSION 275
#define TKSERECOMPOUND 276
#define TKSEREREPEATED 277
#define TKCONS 278
#define TKCONCATENATION 279
#define TKREPLPROP 280
#define TKARRAY 281
#define TKCONTEXT 282
#define TKATOM 283
#define TKFAILURE 284
#define TKITE 285
#define TKVUNIT 286
#define TKVMODE 287
#define TKVPROP 288
#define TKSTRONG 289
#define TKDEFPARAM 290
#define TKINHERIT 291
#define TKFAIRNESS 292
#define TKCONST 293
#define TKBEGIN 294
#define TKEND 295
#define TKPARAMETER 296
#define TKTASK 297
#define TKENDTASK 298
#define TKFORK 299
#define TKJOIN 300
#define TKSUPPLY0 301
#define TKSUPPLY1 302
#define TKSTRONG0 303
#define TKPULL0 304
#define TKWEAK0 305
#define TKHIGHZ0 306
#define TKSTRONG1 307
#define TKPULL1 308
#define TKWEAK1 309
#define TKHIGHZ1 310
#define TKINPUT 311
#define TKOUTPUT 312
#define TKINOUT 313
#define TKDEFAULT_CLOCK 314
#define TKDEFAULT_COLON 315
#define TKDEASSIGN 316
#define TKDISABLE 317
#define TKENDSPECIFY 318
#define TKFOR 319
#define TKINITIAL 320
#define TKSPECIFY 321
#define TKWAIT 322
#define TKFOREVER 323
#define TKREPEAT 324
#define TKWHILE 325
#define TKENDMODULE 326
#define TKENDFUNCTION 327
#define TKWIRE 328
#define TKTRI 329
#define TKTRI1 330
#define TKWAND 331
#define TKTRIAND 332
#define TKTRI0 333
#define TKWOR 334
#define TKTRIOR 335
#define TKTRIREG 336
#define TKREG 337
#define TKINTEGER 338
#define TKINF 339
#define TKDOT 340
#define TKENDPOINT 341
#define TKASSIGN 342
#define TKFORCE 343
#define TKRELEASE 344
#define TKPROPERTY 345
#define TKSEQUENCE 346
#define TKMODULE 347
#define TKFUNCTION 348
#define TKRESTRICT 349
#define TKRESTRICT_GUARANTEE 350
#define TKFORALL 351
#define TKFORANY 352
#define TKASSERT 353
#define TKASSUME 354
#define TKASSUME_GUARANTEE 355
#define TKCOVER 356
#define TKBOOLEAN 357
#define TKCASE 358
#define TKCASEX 359
#define TKCASEZ 360
#define TKELSE 361
#define TKENDCASE 362
#define TKIF 363
#define TKNONDET 364
#define TKNONDET_VECTOR 365
#define TKNONDET_RANGE 366
#define TKWNONDET 367
#define TKBASE 368
#define TKDOTDOT 369
#define TKPIPEMINUSGT 370
#define TKPIPEEQGT 371
#define TKIDENTIFIER 372
#define TKHIERARCHICALID 373
#define TKLP 374
#define TKRP 375
#define TKLC 376
#define TKRC 377
#define TKLB 378
#define TKRB 379
#define TKCOMMA 380
#define TKDIEZ 381
#define TKTRANS 382
#define TKHINT 383
#define TKTEST_PINS 384
#define TKALWAYS 385
#define TKNEVER 386
#define TKEVENTUALLYBANG 387
#define TKWITHINBANG 388
#define TKWITHIN 389
#define TKWITHINBANG_ 390
#define TKWITHIN_ 391
#define TKWHILENOTBANG 392
#define TKWHILENOT 393
#define TKWHILENOTBANG_ 394
#define TKWHILENOT_ 395
#define TKNEXT_EVENT_ABANG 396
#define TKNEXT_EVENT_A 397
#define TKNEXT_EVENT_EBANG 398
#define TKNEXT_EVENT_E 399
#define TKNEXT_EVENTBANG 400
#define TKNEXT_EVENT 401
#define TKNEXT_ABANG 402
#define TKNEXT_EBANG 403
#define TKNEXT_A 404
#define TKNEXT_E 405
#define TKNEXTBANG 406
#define TKNEXT 407
#define TKNEXTfunc 408
#define TKBEFOREBANG 409
#define TKBEFORE 410
#define TKBEFOREBANG_ 411
#define TKBEFORE_ 412
#define TKUNTILBANG 413
#define TKUNTIL 414
#define TKUNTILBANG_ 415
#define TKUNTIL_ 416
#define TKABORT 417
#define TKROSE 418
#define TKFELL 419
#define TKPREV 420
#define TKG 421
#define TKXBANG 422
#define TKX 423
#define TKF 424
#define TKU 425
#define TKW 426
#define TKEG 427
#define TKEX 428
#define TKEF 429
#define TKAG 430
#define TKAX 431
#define TKAF 432
#define TKA 433
#define TKE 434
#define TKIN 435
#define TKUNION 436
#define TKQUESTIONMARK 437
#define TKCOLON 438
#define TKSEMI 439
#define TKPIPEPIPE 440
#define TKAMPERSANDAMPERSAND 441
#define TKMINUSGT 442
#define TKLTMINUSGT 443
#define TKPIPE 444
#define TKTILDEPIPE 445
#define TKOR 446
#define TKPOSEDGE 447
#define TKNEGEDGE 448
#define TKCARET 449
#define TKXOR 450
#define TKXNOR 451
#define TKCARETTILDE 452
#define TKTILDECARET 453
#define TKAMPERSAND 454
#define TKTILDEAMPERSAND 455
#define TKEQEQ 456
#define TKBANGEQ 457
#define TKEQEQEQ 458
#define TKBANGEQEQ 459
#define TKEQ 460
#define TKGT 461
#define TKGE 462
#define TKLT 463
#define TKLE 464
#define TKLTLT 465
#define TKGTGT 466
#define TKWSELECT 467
#define TKGTGTGT 468
#define TKLTLTLT 469
#define TKPLUS 470
#define TKMINUS 471
#define TKSPLAT 472
#define TKSLASH 473
#define TKPERCENT 474
#define TKSPLATSPLAT 475
#define TKBANG 476
#define TKTILDE 477
#define TKLBSPLAT 478
#define TKLBEQ 479
#define TKLBMINUSGT 480
#define TKLBPLUSRB 481
#define TKWCONCATENATION 482
#define TKBOOL 483
#define TKWRESIZE 484
#define TKWSIZEOF 485
#define TKWTOINT 486
#define TKUWCONST 487
#define TKBITSELECTION 488
#define TKUMINUS 489
#define TKSWCONST 490
#define TKWORD1 491
#define TKSIGNED 492
#define TKUNSIGNED 493
#define TKEXTEND 494
#define TKSTRUDLE 495
#define TKSEREFORGR 496
#define TKPSLSPEC 497




#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef union YYSTYPE
#line 64 "psl_grammar.y"
{
  node_ptr node;
  int lineno;

  /* these are news */
  int ival;
  char* wval;
  char* fval;
  char* baseval;
  char* idname;
  PslExpr psl_expr;
  PslOp operator;
}
/* Line 1529 of yacc.c.  */
#line 547 "/Users/cavada/Projects/ESTools/builders/cmake/nusmv/build/NuSMV-bin/code/nusmv/core/parser/psl/psl_grammar.h"
	YYSTYPE;
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif

extern YYSTYPE psl_yylval;

