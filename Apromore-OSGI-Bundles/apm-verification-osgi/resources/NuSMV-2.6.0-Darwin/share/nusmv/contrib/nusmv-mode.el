;;; nusmv-mode.el --- major-mode for editing and running NuSMV sources

;; Copyright (C) 2003-2008 Roger Villemaire

;; Author: Roger Villemaire <villemaire.roger@uqam.ca>
;;	Pier-Luc Simard
;;	Nicolas Leclerc
;; Created: October 2003
;; Updated: August 2008
;; Version: 1.1
;; Keywords: languages NuSMV

;; This program is free software; you can redistribute it and/or
;; modify it under the terms of the GNU General Public License as
;; published by the Free Software Foundation; either version 2 of
;; the License, or (at your option) any later version.

;; This program is distributed in the hope that it will be
;; useful, but WITHOUT ANY WARRANTY; without even the implied
;; warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
;; PURPOSE.  See the GNU General Public License for more details.

;; You should have received a copy of the GNU General Public
;; License along with this program; if not, write to the Free
;; Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
;; MA 02111-1307 USA

;;; Commentary:
;;; This library provides an Emacs interface to NuSMV
;;; <http://nusmv.fbk.eu/>.  It will work only with Emacs >= 21.
;;; The main features are fontification, line indentation (TAB)
;;; and inferior execution.  The content of the the option file (by default
;;; the same name as the source but with extension .opt) will be automatically
;;; taken as NuSMV options in calling NuSMV.  This feature can be turn off.
;;; As most of the features this can be customize.
;;; See the NuSMV menu -> Customize
;;;
;;; We also offer a nusmv-output-mode.  In this mode you can complete
;;; NuSMV commands with the TAB key.  If there is already a word on the command
;;; line TAB will complete to a file name.
;;;
;;; There is a nusmv-error-mode which use compilation-minor-mode. So a click
;;; with the middle button will lead you to the error in the source file.
;;;
;;; There is also a nusmv-m4-mode which offer only a new menu entry
;;; to expand the source file with m4. This can also be done in nusmv-mode
;;; with the command M-x nusmv-exp-with-m4
;;; This is convenient if one use a version of NuSMV which support m4
;;; (-m4 options).
;;;
;;; In the options file you just type in the options you would give
;;; on the command line. You can also add line-ends and comments beginning
;;; with # and they will be skipped.

;;; For more information, type C-h m in any NuSMV buffer.

;;; To use this library, place the following lines into your ~/.emacs file:
;;;
;;;; First if this file directory is not in your `load-path'
;;;; (C-h v load-path to see its value). Then add it with
;;(setq load-path (cons "THIS-FILE-PATH" load-path))

;;;; Second make sure this NuSMV mode is loaded for any file with extension .smv
;;(autoload 'nusmv-mode "nusmv-mode" "Major mode for NuSMV specification files." t)
;;(setq auto-mode-alist
;;      (append  (list '("\\.smv$" . nusmv-mode))
;;	       auto-mode-alist))

;;;; load nusmv-m4-mode for any file with extension m4.smv
;;(autoload 'nusmv-m4-mode "nusmv-mode" " `nusmv-mode' with m4 support." t)
;;(setq auto-mode-alist
;;      (append  (list '("\\.m4.smv$" . nusmv-m4-mode))
;;	       auto-mode-alist))
;;;
;;; To load this file in any buffer type M-x load-file THIS-FILE
;;; To turn this mode on in any buffer type M-x nusmv-mode

;;; History:
;;;    Sergey Berezin  wrote in 1998 a major mode for writing SMV programs.
;;     His mode was later modified by Marco Roveri to capture NuSMV
;;;    language features (i.e. to fontify and indent NuSMV constructs).
;;;
;;;    This file is a major rewrite and extension of
;;;    this original SMV mode. This mode is for NuSMV and emacs 21, we
;;;    don't have any specific features for SMV nor for xemacs.
;;;    We kept smv-mode indentation method, for those who want to stay
;;;    with it but give our own more involved method.
;;;
;;; Limitations:
;;;    Compilation minor mode keeps a link only to one error for each line.
;;;    So if we have a second error on a line a click on the second
;;;    error message will give "No error to go to". This can happen
;;;    if there is a NuSMV and m4 error on the same line.
;;;
;;;    NuSMV usage message will go to the output buffer and not to the
;;;    error buffer.
;;;    We don't catch options errors for m4 like
;;;/usr/bin/m4: invalid option -- x
;;;Try `/usr/bin/m4 --help' for more information.
;;;    They are send to the output buffer
;;;
;;;    If indentation of complete file is too slow, we should define
;;;    indent-region-function
;;;    to be a fastest but equivalent method.
;;;
;;;    The option buffer is edited in Fundamental mode. Maybe it would be
;;;    more convenient to have something like customize to choose options
;;;    from menus and buttons. This would help to find existing options and
;;;    their meaning.
;;;
;;;    Some NuSMV error messages span two lines like
;;;file bidon.smv: line 8: recursively defined: x
;;;in definition of init(x) at line 8
;;;    The first chunk will be sent to the error buffer but the second
;;;    one will go to the output buffer. I don't see any simple solution.
;;;    Something could be done in NuSMV. Either send only errors to standard
;;;    errors (so the output of the help command would go to standard output)
;;;    or make error messages formed of a single line or append something
;;;    special at the beginning of the second and further errors lines.
;;;    At first we developped the mode to send standard output to the
;;;    output buffer and standard error to a temporary file and then to
;;;    the error buffer. One cannot send both standard error and output
;;;    to two different buffers in elisp. When NuSMV is interactive you
;;;    must copy the errors from the file into the buffer periodically.
;;;    This is not as nice as the actual implementation.
;;;
;;;    Ideally in nusmv-output-mode it should be better instead of completing
;;;    the first word to a command and additional words to file names, to
;;;    complete additional words according to the command. This would be
;;;    much more work since commands can be followed by options, numbers and
;;;    file name depending of the command.
;;;
;;;    With Emacs for Windows(TM) the output mode usually don't show NuSMV
;;;    questions before reading the answer (examples: pick_state and simulate).
;;;    This problem already arise in shell mode and seems to be due to the fact
;;;    that under Windows(TM) a pipe instead of a PTTY is used to communicate
;;;    with the process.
;;;    See
;;;    http://www.gnu.org/software/emacs/windows/faq7.html#subproc-buffer
;;;
;;;    Indentation is not perfect. Here are some examples.
;;;
;;;    In logical formulas we indent
;;;  & abc in {a,b}
;;;    & a = 0
;;;
;;;    In a module
;;;    VAR a : boolean;
;;;    b : boolean;
;;;

;;; Version 1.1 changes' list 2008/08/23:
;;; Update the mode to NuSMV 2.4.3.
;;; The first NuSMV prompt is now preceded by some control sequence, show
;;; it as such in nusmv-output-mode.
;;; Update the list of NuSMV commands for TAB completion.

;;; Code:
;;;


(require 'font-lock) ; needed for coloration
(require 'compile) ; needed to define nusmv-error-mode

;;;;
;;;; Syntax definitions
;;;;

(defvar nusmv-mode-syntax-table nil
  "Syntax table used while in NuSMV mode.")

;; Initialize the syntax table
(setq nusmv-mode-syntax-table (make-syntax-table))
(modify-syntax-entry ?\_ "_" nusmv-mode-syntax-table)
(modify-syntax-entry ?\\ "_" nusmv-mode-syntax-table)
(modify-syntax-entry ?$ "_" nusmv-mode-syntax-table)
(modify-syntax-entry ?# "_" nusmv-mode-syntax-table)
(modify-syntax-entry ?\- "_ 12" nusmv-mode-syntax-table)
(modify-syntax-entry ?\# "<" nusmv-mode-syntax-table) ; m4 comments
(modify-syntax-entry ?\n ">" nusmv-mode-syntax-table)
(modify-syntax-entry ?\f ">" nusmv-mode-syntax-table)
(modify-syntax-entry ?\: "."  nusmv-mode-syntax-table)


(defun nusmv-keyword-match (keyword)
  "Convert KEYWORD into a regexp matching that string as a separate word."
  (format "\\b%s\\b" (regexp-quote keyword)))

;;;;
;;;; Syntax definitions
;;;;


;;;;
;;;; Custom Groups
;;;;

;;customization group for NuSMV
(defgroup nusmv-custom-group nil
   "Major mode for editing NuSMV files."
   :tag "NuSMV"
   :version "21.2.1"
   :group 'languages)

(defgroup nusmv-custom-group-colors nil
   "NuSMV coloration."
   :tag "NuSMV coloration"
   :version "21.2.1"
   :group 'nusmv-custom-group)

(defgroup nusmv-custom-group-indent nil
  "NuSMV Indentation."
  :tag "NuSMV indentation"
  :version "21.2.1"
  :group 'nusmv-custom-group)

(defgroup nusmv-custom-group-running nil
  "Running NuSMV."
  :tag   "Running NuSMV"
  :version "21.2.1"
  :group 'nusmv-custom-group)

(defgroup nusmv-custom-group-hooks nil
  "NuSMV hooks."
  :tag   "NuSMV hooks"
  :version "21.2.1"
  :group 'nusmv-custom-group)

;;;;
;;;; end Custom Groups
;;;;


;;;;
;;;; Variables
;;;;

;;;; Fontification variables and faces

(defvar nusmv-font-lock-keyword-face 'nusmv-font-lock-keyword-face)
(defvar nusmv-font-lock-separator-face 'nusmv-font-lock-separator-face)
(defvar nusmv-font-lock-module-name-face 'nusmv-font-lock-module-name-face)
(defvar nusmv-font-lock-variable-name-face 'nusmv-font-lock-variable-name-face)
(defvar nusmv-font-lock-directive-face 'nusmv-font-lock-directive-face)
(defvar nusmv-font-lock-type-face 'nusmv-font-lock-type-face)
(defvar nusmv-font-lock-constant-face 'nusmv-font-lock-constant-face)

(defface nusmv-font-lock-keyword-face
   '((t (:foreground "purple")))
   "font for displaying nusmv keywords."
   :group 'nusmv-custom-group-colors)

(defface nusmv-font-lock-separator-face
   '((t (:foreground "indianred")))
   "font for displaying separators."
   :group 'nusmv-custom-group-colors)

;; Take font-lock-function-name-face as default color
(defface nusmv-font-lock-module-name-face
   (list (list t (list :foreground
		       (face-attribute 'font-lock-function-name-face
				       :foreground))))
   "font for displaying NuSMV modules names."
   :group 'nusmv-custom-group-colors)


(defface nusmv-font-lock-directive-face
   '((t (:foreground "deeppink")))
   "font for displaying m4 and cpp directives.
The highlighted directives are include,define,dnl for m4 and
include,define for cpp."
   :group 'nusmv-custom-group-colors)


;; Take font-lock-variable-name-face as default color
(defface nusmv-font-lock-variable-name-face
   (list (list t (list :foreground
		       (face-attribute 'font-lock-variable-name-face
				       :foreground))))
  "font for displaying variables names in define directives."
   :group 'nusmv-custom-group-colors)


;; Take font-lock-type-face  as default color
(defface nusmv-font-lock-type-face
   (list (list t (list :foreground
		       (face-attribute 'font-lock-type-face
				       :foreground))))
  "font for displaying NuSMV types names."
   :group 'nusmv-custom-group-colors)

;; Take font-lock-constant-face  as default color
(defface nusmv-font-lock-constant-face
   (list (list t (list :foreground
		       (face-attribute 'font-lock-constant-face
				       :foreground))))
  "font for displaying NuSMV constants names."
   :group 'nusmv-custom-group-colors)

;;;; end Fontification variables and faces


;;;; Indentation variables
(defconst nusmv-comment-start "--"
  "Beginning of a comment.")

;; Matches NuSMV (--)  m4 (#, dnl) comments and end-of-line.
(defconst nusmv-comment-regexp "$\\|--\\|#\\|\\bdnl\\b"
  "Matches beginning of NuSMV, m4 comments, cpp directives and line ends.")


(defun nusmv-natural-p (widget value)
  "Test that VALUE is a natural number."
  (wholenump value)
  )

;; Unfortunately if the given integer is negative we get a default
;; error message saying that it is not an integer.
(defcustom nusmv-indent 2
  "*Size of NuSMV indentation used by TAB key.  Must be non-negative."
  :type '(integer :match nusmv-natural-p)
  :version "21.2.1"
  :group 'nusmv-custom-group-indent)

;; ;; -flags
;; ;; \C-j
;; (defcustom nusmv-indent-after-return t
;; ;-flag
;;   "*Non-nil means automatically indent after a line end (\\C-j)."
;;   :type 'boolean
;;   :version "21.2.1"
;;   :group 'nusmv-custom-group-indent)

;; Taken from ada-mode
(defcustom nusmv-indent-align-comments-flag t
  "*Non-nil means align comments on previous line comment, if any.
If nil, indentation is calculated as usual.

For instance:
    A := 1;   --  A multi-line comment
              --  aligned if nusmv-indent-align-comments-flag is t"
  :type 'boolean
  :version "21.2.1"
  :group 'nusmv-custom-group-indent)

(defcustom nusmv-tab-policy 'nusmv
  "*Control the behavior of the TAB key."
  :type '(radio (const :doc "\t New indentation method"
		       nusmv)
		(const :doc "\t Always adds nusmv-indent blanks at line beginning."
		       indent-rigidly)
		(const :doc "\t Old smv-mode indentation method."
		       smv))
  :version "21.2.1"
  :group 'nusmv-custom-group-indent)

;; Unfortunately if the given integer is negative we get a default
;; error message saying that it is not an integer.
(defcustom nusmv-block-indent 0
  "*Size of NuSMV indentation used by TAB key for blocks keywords.
This include VAR ASSIGN DEFINE FAIRNESS SPEC ...
Must be non-negative."
  :type '(integer :match nusmv-natural-p)
  :version "21.2.1"
  :group 'nusmv-custom-group-indent)

;;;; end Indentation variables


;;;; Variables for running NuSMV

(defcustom nusmv-command "NuSMV"
  "*The command name to run NuSMV."
  :type 'string
  :version "21.2.1"
  :group 'nusmv-custom-group-running)

(defvar nusmv-command-history nil
  "History of commands entered by user.")

(defvar nusmv-m4-command-history nil
  "History of m4 commands entered by user.")


;; Option for the error window output
(defcustom nusmv-output-replaces-source-flag t
  "*Non-nil means the output window will replace the source window.
Otherwise the output window is pop-up.  When there is a single window this
will give a second one if user option 'pop-up-windows' is non-nil ('on')"
	:type 'boolean
	:version "21.2.1"
	:group 'nusmv-custom-group-running)


(defcustom nusmv-output-behavior 'erase-no-confirm
   "*Controls if new output is appended to old or replace old output."
  :type '(radio (const :doc "\t Append to output"
		       append)
		(const :doc "\t Erase old output and don't ask for confirmation."
		       erase-no-confirm)
		(const :doc "\t Erase old output and ask for confirmation."
		       erase-confirm))
  :version "21.2.1"
   :group 'nusmv-custom-group-running)


(defcustom nusmv-options-from-file-flag t
  "*Non-nil means NuSMV options will be taken from the option file.
This file have the same name has the source code but with extension .smv
replace by `nusmv-options-extension'."
	:type 'boolean
	:version "21.2.1"
	:group 'nusmv-custom-group-running)

;; remplacer smv partout
(defconst nusmv-source-extension "smv" "NuSMV source file extension.")

(defcustom nusmv-options-extension "opt"
  "*Options file extension.
This will replace  `nusmv-source-extension' to build output file name.
Don't but a '.' at the beginning."
  :type 'string
	:version "21.2.1"
	:group 'nusmv-custom-group-running)

(defcustom nusmv-output-extension "out"
  "*Output file extension.
This will replace `nusmv-source-extension' to build output file name.
Don't but a '.' at the beginning."
  :type 'string
	:version "21.2.1"
	:group 'nusmv-custom-group-running)

(defcustom nusmv-order-extension "ord"
  "*Order file extension.
This will replace `nusmv-source-extension' to build order file name.
Don't but a '.' at the beginning."
  :type 'string
	:version "21.2.1"
	:group 'nusmv-custom-group-running)

(defcustom nusmv-shell-file-name shell-file-name
  "*Shell used to run NuSMV process.  Must understands the -c option.
bash and csh are ok.  If not in your path but an absolute name."
  :type 'string
  :version "21.2.1"
  :group 'nusmv-custom-group-running)

(defcustom nusmv-m4-file-name "m4"
  "*Name of m4 preprocessor.  If not in your path but an absolute name."
  :type 'string
  :version "21.2.1"
  :group 'nusmv-custom-group-running)

(defcustom nusmv-error-buffer-name "*nusmv-errors*"
  "*The name of the buffer containing NuSMV errors messages."
  :type 'string
  :version "21.2.1"
  :group 'nusmv-custom-group-running)

;; the mode used for output buffers
;; The value must be a function. If you need to require some feature
;; before calling the function you must define your own wraper in this
;; file.
(defcustom nusmv-output-mode-fun 'nusmv-output-mode
  "*The name of the mode used by NuSMV output files."
  :type 'function
  :version "21.2.1"
  :group 'nusmv-custom-group-running)

;; the mode used for the error buffer
;; The value must be a function. If you need to require some feature
;; before calling the function you must define your own wraper in this
;; file.
(defcustom nusmv-error-mode-fun 'nusmv-error-mode
  "*The name of the mode used by NuSMV error buffer."
  :type 'function
  :version "21.2.1"
  :group 'nusmv-custom-group-running)


;; NuSMV execution is asynchronous. We can have many NuSMV processes running
;; at the same time. We must keep track of incomplete lines received
;; from the various NuSMV processes. We send complete lines to either
;; the output or error buffer after a pattern match.
(defvar nusmv-process-string-alist  nil
  "Associate a process with the incomplete line received from it.")

;;;; end Variables for running NuSMV


(defcustom nusmv-mode-hook nil
  "*List of functions to call when NuSMV mode is invoked.
This hook is automatically executed after the `nusmv-mode' is
fully loaded."
  :type 'hook
  :version "21.2.1"
  :group 'nusmv-custom-group-hooks)


(defcustom nusmv-output-mode-hook nil
  "*List of functions to call when NuSMV output mode is invoked.
This hook is automatically executed after the nusmv-output-mode is
fully loaded."
  :type 'hook
  :version "21.2.1"
  :group 'nusmv-custom-group-hooks)

(defcustom nusmv-error-mode-hook nil
  "*List of functions to call when NuSMV error mode is invoked.
This hook is automatically executed after the nusmv-error-mode is
fully loaded."
  :type 'hook
  :version "21.2.1"
  :group 'nusmv-custom-group-hooks)


;;;;
;;;; end Variables
;;;;



;;;;
;;;; Regular expressions
;;;;

;;;; Regular expressions for fontification

;; no SMV keywords. Only nusmv keywords.
(defconst nusmv-keywords
  '("MODULE" "VAR" "IVAR" "TRANS" "ASSIGN" "INVAR" "DEFINE" "SPEC"
    "LTLSPEC" "INVARSPEC" "FAIRNESS" "JUSTICE" "COMPASSION"
    "process" "array" "of" "case" "esac"
    "next" "init" "INIT" "ISA"
    "in" "union" "COMPUTE" "MAX" "MIN")
  "NuSMV keywords.")

;; This could be useful some day in a regular expression.
;;(defconst nusmv-atom "[A-Za-z_][A-Za-z0-9_$#-]*"
;;  "NuSMV atom.")

(defconst nusmv-number "[0-9][0-9]*"
  "A number, also inside an identifier.")

;; type names that act like keywords
;; set construct {}, boolean
(defconst nusmv-type-names-regexp
  "\\bboolean\\b\\|{.+}"
  "NuSMV predefined types.")

(defconst nusmv-range-regexp
  ;; number..number where the first number
  ;; is not inside an identifier
  (concat "[^A-Za-z0-9_$#]\\("
	  nusmv-number
	  "\\.\\."
	  nusmv-number
	  "\\)")
  "A range of numbers.")


;; constant values
;; self, TRUE, FALSE
(defconst nusmv-constants-regexp
   (mapconcat 'nusmv-keyword-match
	      (list "self" "TRUE" "FALSE" )
	      "\\|")
  "NuSMV constants.")

;; number constants
(defconst nusmv-number-constants-regexp
   (mapconcat 'identity
	      (list ;; a number but not inside an identifier
		    (concat "[^A-Za-z0-9_$#]\\("
			    nusmv-number
			    "\\)")
		    ;; can be at line beginning
		    (concat "^\\("
			    nusmv-number
			    "\\)")
)
	      "\\|")
  "NuSMV number constants.")


;; the logical operators
(defconst nusmv-symbolic-logic-operators
  '("!" "&" "|" "xor" "->" "<->")
  "NuSMV symbolic logical operators.")

(defconst nusmv-textual-logic-operators
  '("EG" "EX" "EF" "AG" "AX" "AF" "E" "A" "U"
 "X" "G" "F" "V" "Y" "Z" "H" "O" "S" "T"
 "EBF" "ABF" "EBG" "ABG" "BU")
  "NuSMV textual logical operators.")

;; built-in functions
(defconst nusmv-built-in-functions
  '("="
    ;; "!=" not necessary since ! and = are already colored

    ;; - is a binary operator if it is preceeded by a space
    ;; otherwise it is part of an atom.
    "<" ">" "<=" ">=" "+" "[[:space:]]-"
    "\\*" "/" "\\bmod\\b"
    ":=")
  "NuSMV built-in functions.")

;;;; end Regular expressions for fontification


;;;; Regular expressions for indentation

;; No SMV only NuSMV declaration-keywords
(defconst nusmv-declaration-keywords
  '("MODULE" "VAR" "IVAR" "ASSIGN" "TRANS" "INIT" "INVAR" "DEFINE"
    "ISA" "FAIRNESS" "JUSTICE" "COMPASSION" "SPEC" "INVARSPEC" "LTLSPEC"
    "COMPUTE")
  "The list of keywords that open a declaration.")

(defconst nusmv-declaration-keywords-regexp
  (mapconcat 'nusmv-keyword-match nusmv-declaration-keywords "\\|"))

(defconst nusmv-openning-keywords
  '("case" "for" "next" "init")
  "The list of keywords that open a subexpression.")

(defconst nusmv-openning-keywords-regexp
  (mapconcat 'nusmv-keyword-match nusmv-openning-keywords "\\|"))

(defconst nusmv-closing-keywords
  '("esac")
  "The list of keywords that close a subexpression.")

(defconst nusmv-closing-keywords-regexp
  (mapconcat 'nusmv-keyword-match nusmv-closing-keywords "\\|"))

;; The token := can appear only in an ASSIGN or DEFINE so we will
;; align it accordingly.
(defconst nusmv-assignment-regexp "[ \t]*[^ \t]+[ \t]*:="
  "Regexp matching the beginning of an assignment.")

(defconst nusmv-assignment-bol-regexp ":="
  "Regexp matching the beginning of an assignment.")

;;;; end Regular expressions for indentation


;;;; Regular expressions for running NuSMV
;; error messages
;; These expressions must contain a first parenthesis group
;; around the file name and a second around the line number.
;; This will be used in nusmv-error-mode.

;; The format of NuSMV errors
;; file *name-of-file*: line *line-number*: at token...error message...
;; file <command-line>: line 4: at token "AG": parse error
(defvar nusmv-error-regexp-1
  "^[ \t]*file \\([a-zA-Z<][-a-zA-Z._0-9>]+\\): line \\([0-9]+\\):"
  "Format of NuSMV error message.")

;; m4 error message
;; bidon.m4:1: /usr/bin/m4: Warning: Excess arguments to built-in `define' ignored
(defvar nusmv-error-regexp-2
  "^[ \t]*\\([a-zA-Z][-a-zA-Z._0-9]+\\):\\([0-9]+\\):"
  "Format of m4 error message.")

;; In order to execute NuSMV we use the command
;; nusmv-shell-file-name -c "NuSMV"
;; So if NuSMV is not in the path then the following message occur.
;; /usr/bin/bash: line 1: NuSMV: command not found
;; We want to send this message to the error buffer.
(defvar nusmv-error-regexp-3
  "^[ \t]*\\([a-zA-Z][-a-zA-Z._0-9]+\\): line \\([0-9]+\\):"
  "Format of NuSMV error message.")

(defvar nusmv-error-regexp
  (concat nusmv-error-regexp-1 "\\|"
	  nusmv-error-regexp-2 "\\|"
	  nusmv-error-regexp-3)
  "Format of NuSMV and m4 error message.")

;; 2008/08/23 "^NuSMV > " doesn't catch the prompt anymore.
;; In NuSMV 2.4.3, the first prompt is preceded by the string ESC[?1034h
;; The following code will accept any string ending by "NuSMV > ".
(defvar nusmv-prompt-regexp ".*NuSMV > "
  "Format of NuSMV interactive prompt.")

;; In interactive mode the following incomplete lines (i.e. no line-end)
;; must be send immediately to output
;; - the nusmv prompt
;; - questions of NuSMV like
;; - - Choose a state from the above (0-2):
;; - - There's only one future state. Press Return to Proceed.
(defvar nusmv-question-regexp-1 "[ \t]*Choose[^:]*:[ \t]*"
  "Format of a NuSMV interactive question.")

(defvar nusmv-question-regexp-2 ".*Press Return to Proceed.*"
  "Format of a NuSMV interactive question.")

(defvar nusmv-complete-output-regexp
  (mapconcat 'identity
	     (list nusmv-prompt-regexp
		   nusmv-question-regexp-1
		   nusmv-question-regexp-2)
	     "\\|")
  "Match strings which must be send to output even if not a complete line.")
;;;; end Regular expressions for running NuSMV


;;;;
;;;; end Regular expressions
;;;;




;;;;
;;;; Fontification
;;;;

;;;; We define here nusmv-font-lock-keywords-*. They specify
;;;; what is colored and in which color.
;;;; The value of your font-lock-maximum-decoration variable set
;;;; which nusmv-font-lock-keywords-* is used.

;;;; font-lock-maximum-decoration Documentation:
;;;; *Maximum decoration level for fontification.
;;;; If nil, use the default decoration (typically the minimum available).
;;;; If t, use the maximum decoration available.
;;;; If a number, use that level of decoration (or if not available the
;;;; maximum).
;;;; If a list, each element should be a cons pair of the form
;;;; (MAJOR-MODE . LEVEL),
;;;; where MAJOR-MODE is a symbol or t (meaning the default).  For example:
;;;;  ((c-mode . t) (c++-mode .  2) (t .  1))
;;;; means use the maximum decoration available for buffers in C mode, level 2
;;;; decoration for buffers in C++ mode, and level 1 decoration otherwise.
;;;; You can customize this variable.

;; From the elisp manual
;;   * Level 1: highlight function declarations, file directives (such as
;;      include or import directives), strings and comments.  The idea is
;;      speed, so only the most important and top-level components are
;;      fontified.

;; So for NuSMV we
;; highlight MODULE declaration,
;; file directives: m4 (include,define,dnl), cpp (#include,#define)
;; m4 quotes [: and :] (according to NuSMV.m4)

(defconst nusmv-font-lock-keywords-1
   (list
    (list (concat (nusmv-keyword-match "MODULE") " *\\([-_?A-Za-z0-9]+\\)")
	  1 'nusmv-font-lock-module-name-face)
    (list "\\b\\(include\\)("
	  '(1 'nusmv-font-lock-directive-face))
    (list "\\b\\(define\\)(\\([^  ,]+\\)"
	  '(1 'nusmv-font-lock-directive-face)
	  '(2 nusmv-font-lock-variable-name-face))
    (list "\\(\\bdnl\\b.*$\\)"
	  '(1 'nusmv-font-lock-directive-face t)) ; always overwrite
    (list "^\\(#\\)\\(include\\)\\b *\\(.+$\\)"
	  '(1 'nusmv-font-lock-directive-face t)
	  '(2 'nusmv-font-lock-directive-face t)
	  '(3 'font-lock-string-face t))
    (list "^\\(#\\)\\(define\\)\\b *\\([^ ]+\\)\\(.*$\\)"
	  '(1 'nusmv-font-lock-directive-face t)
	  '(2 'nusmv-font-lock-directive-face t)
	  '(3 'nusmv-font-lock-variable-name-face t)
	  '(4 'font-lock-default-face t))
    (list "\\[:\\|:\\]" 0 'nusmv-font-lock-directive-face t)
    )
;;;)
  "Highlight MODULE, file directives and m4 quotes in NuSMV mode.")


;; From the elisp manual
;;    * Level 2: in addition to level 1, highlight all language keywords,
;;      including type names that act like keywords, as well as named
;;      constant values.  The idea is that all keywords (either syntactic
;;      or semantic) should be fontified appropriately.
;; In NuSMV highlight
;; keywords
;; set construct {}, boolean, number..number
;; TRUE, FALSE, numbers
(defconst nusmv-font-lock-keywords-2
  (append nusmv-font-lock-keywords-1
	  (list
	   ;; the keywords
	   (list    (mapconcat 'nusmv-keyword-match nusmv-keywords "\\|")
		    0 'nusmv-font-lock-keyword-face)
	   ;; prefefined types
	   (list nusmv-type-names-regexp
		 0 'nusmv-font-lock-type-face)
	   ;; numbers ranges
	   (list nusmv-range-regexp
		 1 'nusmv-font-lock-type-face)
	   ;; constants
	   (list nusmv-constants-regexp
		 0 'nusmv-font-lock-constant-face)
	   ;; number constants
 	   (list nusmv-number-constants-regexp
		 ;; if subexpression don't exists continue
 		 (list 1 'nusmv-font-lock-constant-face nil t)
 		 (list 2 'nusmv-font-lock-constant-face nil t)
		 )
	   ))
"Highlight keywords, set, boolean, ranges and constants in NuSMV.")

;; From the elisp manual
;;    * Level 3: in addition to level 2, highlight the symbols being
;;      defined in function and variable declarations, and all builtin
;;      function names, wherever they appear.
;; In NuSMV logical and builtin function names.
(defconst nusmv-font-lock-keywords-3
  (append nusmv-font-lock-keywords-2
 	  (list
 	   ;; logical operators
 	   (list    (mapconcat 'identity nusmv-symbolic-logic-operators "\\|")
 		    0 'nusmv-font-lock-keyword-face)
	   (list    (mapconcat 'nusmv-keyword-match
			       nusmv-textual-logic-operators "\\|")
 		    0 'nusmv-font-lock-keyword-face)
 	   ;; built-in
 	   (list   (mapconcat 'identity nusmv-built-in-functions "\\|")
		    0 'nusmv-font-lock-keyword-face)
 	   )
 	  )
 "Highlight logical operators and built-in in NuSMV.")

;;;;
;;;; end Fontification
;;;;


;;;;
;;;; Indentation
;;;;

;;;; FIRST
;;;; First indentation method: tab-hard.

(defun nusmv-tab-hard ()
  "Indent current line to nusmv-indent column."
  (interactive "*")
  (save-excursion
    (beginning-of-line)
       (indent-to nusmv-indent)
))


;;;; SECOND
;;;; Second indentation method: original indent of smv-mode by
;;;; Sergey Berezin modified by Marco Roveri.

(defun nusmv-previous-line ()
  "Move the point to the last non-comment non-blank line.
Positions the cursor on the first non-blank character."

  (forward-line -1)
  (back-to-indentation)

  (while (and (not (eq (nusmv-line-number) 1))
	      (looking-at nusmv-comment-regexp))
    (forward-line -1)
    (back-to-indentation)
    )
  )

(defun nusmv-previous-indentation ()
  "Return a pair (INDENT . TYPE).
INDENT is the indentation of the previous line, if there is one, and TYPE
is 'openning, 'declaration or 'plain, depending on whether previous line
start with an openning, declarative keyword or neither.  \"Previous line\"
means the last line before the current that is not an empty line or a comment."
  (if (bobp) '(0 . 'plain)
    (save-excursion
      (nusmv-previous-line)
      (if (eq (nusmv-line-number) 1) '(0 . 'plain)
      (let ((type (cond ((or (looking-at nusmv-openning-keywords-regexp)
			     (looking-at nusmv-assignment-regexp)) 'openning)
			((looking-at nusmv-declaration-keywords-regexp)
			 'declaration)
			(t 'plain)))
	    (indent (current-indentation)))
	(cons indent type))))))

(defun nusmv-compute-indentation-old ()
  "Computes the indentation for the current line based on the previous line.
Current algorithm is too simple and needs improvement."
  (save-excursion
    (back-to-indentation)
    (cond ((looking-at nusmv-declaration-keywords-regexp) 0)
	  ((looking-at nusmv-assignment-regexp) nusmv-indent)
	  (t (let* ((indent-data (nusmv-previous-indentation))
		    (indent (car indent-data))
		    (type (cdr indent-data)))
	       (setq indent
		     (cond ((looking-at nusmv-closing-keywords-regexp)
			    (if (< indent nusmv-indent) 0
			      (- indent nusmv-indent)))
			   ((or (eq type 'openning) (eq type 'declaration))
			    (+ indent nusmv-indent))
			   (t indent)))
	       indent)))))

(defun nusmv-indent-current-old ()
  "Indent the current line relative to the previous meaningful line."

  (indent-line-to (nusmv-compute-indentation-old))
)


;;;; THIRD
;;;; Third indentation method:
;;;; We first look at the first non-blank character on the line and in some
;;;; cases decide indentation on that basis. Otherwise we go back up one line
;;;; at a time, scanning lines from right to left, skipping comments and
;;;; looking for some specific tokens.

(defun nusmv-last-non-blank-non-comment-character ()
  "Move point just passed the last non-blank non-comment character of the
current line.  Return point."

  (beginning-of-line)
   (let ((found-point ; look for a comment on the current line.
 	(re-search-forward nusmv-comment-regexp (line-end-position) t)))
     (if (eq found-point nil) ; not found
 	(end-of-line)
       ;; found, so go back before the comment.
       (backward-char (length (match-string 0))))

    ;; go back to last non-space
    (skip-chars-backward " \t" (line-beginning-position))
    (point)))


(defun nusmv-move-to-matching-encloser (OPEN CLOSE &optional limit)
  "Go back to the position of the matching OPEN.
OPEN and CLOSE are strings considered to by matching enclosers \(like open
and close parenthesis).
We suppose that point is at CLOSE and we go back to the position of the
matching OPEN.  Return the column.  Stop at position LIMIT if non-nil.
Returns nil if search failed."

  ;; if no limit stop at the buffer beginning
  (let ((searchlimit nil))
    (if (eq limit nil)
  	(setq searchlimit (point-min))
      (setq searchlimit limit))

    ;; Going backwards, add one for every CLOSE met and substract one for every
    ;; OPEN. At zero you have the matching OPEN.
    (if (eq (point) searchlimit)
	nil ; since point is at close.

      (let ((count 1))
	(while (and (not (bobp)) (> (point) searchlimit) (not (eq count 0)))
	  ;; if at line beginning go to previous line but skip comment at
	  ;; the end
	  (if (bolp)
	      (progn (backward-char)
		     (nusmv-last-non-blank-non-comment-character))
	    (backward-char))

	  (if (looking-at (regexp-quote OPEN)) (setq count (- count 1)))
	  (if (looking-at (regexp-quote CLOSE)) (setq count (+ count 1))))


 	(if (eq count 0) (current-column)
	  nil
	  )
	)
      )
    )
  )

(defun nusmv-get-open (CLOSE)
"Return the open encloser corresponding to CLOSE."
(cond ((string= ")" CLOSE) "(")
      ((string= "}" CLOSE) "{")
      ((string= "]" CLOSE) "[")
      ((string= ":]" CLOSE ) "[:")
      ((string= "esac" CLOSE ) "case")
      (t nil)))

(defun nusmv-closing-encloser ()
"Return the closing encloser at point, nil if there is none."
   (cond ((looking-at (regexp-quote ")")) ")")
	 ((looking-at (regexp-quote "]")) "]")
	 ((looking-at (regexp-quote "}")) "}")
	 ((looking-at (regexp-quote ":]")) ":]")
	 ((looking-at (regexp-quote "esac")) "esac")
	 (t nil)))

(defun nusmv-matching-encloser-column ()
"Return the column of the open encloser of the closing encloser at point."
(save-excursion
    (nusmv-move-to-matching-encloser
	       (nusmv-get-open (nusmv-closing-encloser))
	       (nusmv-closing-encloser)
	       )))

(defun nusmv-open-encloser-length ()
"Return the length of the closing encloser at point, nil if it doesnt existe."
   (cond ((looking-at (regexp-quote "(")) 1)
	 ((looking-at (regexp-quote "[")) 1)
	 ((looking-at (regexp-quote "{")) 1)
	 ((looking-at (regexp-quote "[:")) 2)
	 ((looking-at (regexp-quote "case")) 4)
	 (t nil)))

(defun nusmv-previous-comment-indentation ()
  "Return the column of the comment on the last line.
If there is none return nil."
  (save-excursion
    (forward-line -1)
    (beginning-of-line)

    (let ((beg (line-beginning-position))
	  (end (line-end-position))
	  (comment-pos
	   (search-forward nusmv-comment-start (line-end-position) t))
	  )
      (if (eq comment-pos nil)
	  nil
	(- (current-column) (string-width nusmv-comment-start))))))

(defun nusmv-comment-p ()
  "Verify if the line begin with `nusmv-comment-start'."
  (save-excursion
    (back-to-indentation)
    (if (looking-at (regexp-quote nusmv-comment-start)) t nil)))


(defun nusmv-line-number (&optional POINT)
  "Return the line number of POINT if given and of point otherwise.
The first line is numbered 1."
  (save-excursion
    (let* ((pos (if POINT POINT (point)))
    ;; (count-lines (point-min) pos) returns the line number of pos
    ;; EXCEPT if pos is in column 0. In that case it return one less.
	   (val (count-lines (point-min) pos)))
      (goto-char pos)
      (if (eq (current-column) 0) (+ val 1) val))))


(defun nusmv-next-is-module-p ()
  "Return t if the next line start by keyword MODULE, else return nil."
  (save-excursion
    (end-of-line)
    (if (eobp) nil
      (progn (forward-line)
	     (back-to-indentation)
	     (if (looking-at "MODULE ") t nil)))))


(defun nusmv-column-passed-open ()
  "Return the column of the first non-blank character following ( or {.
Look backwards on current line for ( or {, return the column of first
character passed this character.  If we find ) or } on current line we
skip to the corresponding open and continue the search.  Don't move point.
If none found return current indent of current line."
  (save-excursion
    ;; Take care of the fact that we want an open encloser which inclose
    ;; the current position, i.e. skip blocks of the form (...)

    (let ((indent nil))
      (while (and (not indent) ; no value found
		  (not (bolp)))

	(if (re-search-backward "[({)}]" (line-beginning-position) t)
	    ;; at an open encloser
	    (if (looking-at "[({]")
		(progn (forward-char)
		       (skip-chars-forward " \t")
		       (setq indent (current-column)))
	      ;; otherwise at a closing encloser
	      ;; skip passed the open-encloser
	      (nusmv-move-to-matching-encloser
	       (nusmv-get-open (nusmv-closing-encloser))
	       (nusmv-closing-encloser) (line-beginning-position)))
	  ;; no enclosers on this line
	  (setq indent (current-indentation))))

      (if (not indent)
	  (current-indentation)
	indent))))


(defun nusmv-look-back-for-indent ()
"Go back looking for specific tokens in order to indent the current line.
Returns the suggested indentation column for the current line."
  (save-excursion
    (if (eq (nusmv-line-number) 1)
	0
       (let ((previous-indent nil)
	     (previous-line-number nil)
	     (indent nil))
	 ;; We scan lines right to left going back to the beginning of the
	 ;; buffer

	 ;; Go to previous line
	 (nusmv-previous-line)
	 (nusmv-last-non-blank-non-comment-character)
	 (setq previous-indent (current-indentation))
	 (setq previous-line-number (nusmv-line-number))

	 ;; From right to left look for some specifics tokens
	 ;; moving to the beginning of the buffer
	 (while (and (not (bobp)) (not indent))
	   (cond
	    ((looking-at "MODULE ") (setq indent nusmv-indent))
	    ((looking-at nusmv-declaration-keywords-regexp)
	     (setq indent (+ nusmv-indent nusmv-block-indent)))

	    ((nusmv-closing-encloser)
	     ;; skip passed the open-encloser
	     (nusmv-move-to-matching-encloser
	      (nusmv-get-open (nusmv-closing-encloser))
	      (nusmv-closing-encloser))
	     (if (bobp)
		 (setq indent previous-indent)
	       ; go back one character
	       (progn (if (bolp)
			  (progn (backward-char)
				 (nusmv-last-non-blank-non-comment-character))
			(backward-char)))))


	    ((looking-at ",\\|;")
	     ;; if on previous line
	     (if (eq previous-line-number (nusmv-line-number))
		 ;; indent to preceeding open on the same line if
		 ;; one otherwise to this line
		 (setq indent (nusmv-column-passed-open))
	       ;; if not on previous line
	       (setq indent previous-indent)))


	    ((looking-at "case")
	     ;; if on previous line
	     (if (eq previous-line-number (nusmv-line-number))
		 ;; indent to s of case
		 (setq indent (+ (current-column) 2))
	       ;; if not on previous line
	       (setq indent previous-indent)))


	    ;; looking at an open-encloser other than case
	    ((nusmv-open-encloser-length)
	     ;; if on previous line
	     (if (eq previous-line-number (nusmv-line-number))
		 ;; indent to first character passed open-encloser
		 (progn
		   (forward-char
		    (nusmv-open-encloser-length)) ; skip open encloser
		   (skip-chars-forward " \t")
		   (setq indent (current-column)))
	       ;; if not on previous line
	       (setq indent previous-indent)))

	    ((looking-at
	   ":=\\|:\\|\\barray\\b\\|\\bof\\b\\|\\.\\.\\|\\bin\\b\\|\\bunion\\b")
	     ;; if on previous line
	     (if (eq previous-line-number (nusmv-line-number))
		 ;; indent one more
		 (setq indent (+ previous-indent nusmv-indent))
	       ;; if not on previous line
	       (setq indent previous-indent)))

	    ((bolp) ; if at line beginning
	     (nusmv-previous-line)
	     (nusmv-last-non-blank-non-comment-character))

	    (t (backward-char))
	    )
	   )

	 ;; if still no indent found
	 (if (not indent)
	     previous-indent
	   indent)))))


(defun nusmv-compute-indentation ()
  "Computes the indentation for the current line.  Return a column."
(save-excursion
  (back-to-indentation)
   (cond ((looking-at "MODULE ") 0)
	 ((looking-at nusmv-declaration-keywords-regexp) nusmv-block-indent)
	 ;; looking at a closing encloser
	 ((nusmv-closing-encloser)
	  (let ((val (nusmv-matching-encloser-column)))
	    ;; if open not found
	    (if val val 0)))

	 ;; Align on previous comment
	 ((and (nusmv-comment-p) nusmv-indent-align-comments-flag
	       (nusmv-previous-comment-indentation))
	  (nusmv-previous-comment-indentation))
	 ;; Comment just before MODULE is left aligned
	 ;; Unfortunately this works only for a single line of comment.
	 ((and (nusmv-comment-p) (nusmv-next-is-module-p)) 0)


	 ;; assignments
	 ((looking-at nusmv-assignment-regexp)
	  (+ nusmv-indent  nusmv-block-indent))

	 ((looking-at nusmv-assignment-bol-regexp)
	  ;; Go to previous line
	  (nusmv-previous-line)
	  (nusmv-last-non-blank-non-comment-character)
	  (let ((previous-indent (current-indentation)))
	    (+ nusmv-indent previous-indent)))

	 (t (nusmv-look-back-for-indent)))))


(defun nusmv-indent-current ()
  "Indent the current line according to nusmv-compute-indentation."
  (indent-line-to (nusmv-compute-indentation)))

(defun nusmv-tab ()
  "Do indenting or tabbing according to `nusmv-tab-policy'."
  (cond ((eq nusmv-tab-policy 'indent-rigidly) (nusmv-tab-hard))
        ((eq nusmv-tab-policy 'nusmv) (nusmv-indent-current))
        ((eq nusmv-tab-policy 'smv) (nusmv-indent-current-old))))

(defun nusmv-indent-line ()
  "NuSMV mode version of the `indent-line-function'."
  (interactive "*")

  (let ((starting-point (point-marker)))
    (beginning-of-line)
    (nusmv-tab)
	(progn
	  (if (< (point) starting-point)
	      (goto-char starting-point))
	  (set-marker starting-point nil))))


(defun nusmv-indent-file ()
  "Indent all the lines in the file."
  (interactive "*")

  (indent-region (point-min) (point-max) nil)
  )

;;;;
;;;; end Indentation
;;;;


;;;;
;;;; Commenting
;;;;


;; The following function is adapted from the ada-mode (ada-mode.el)
(defun nusmv-uncomment-region (beg end &optional arg)
  "Delete `comment-start' at the beginning of a line in the region."
  (interactive "*r\nP")
  (comment-region beg end (- (or arg 2))))

;;;;
;;;; end Commenting
;;;;



;;;;
;;;; Running NuSMV
;;;;

;; We run NuSMV in the following way.
;; Ask to save any unsaved files.
;; Display a prompt containing the NuSMV command (and options from
;;  opt file if nusmv-options-from-file-flag is true).
;; Run the command. An output filter sends the output to the output buffer
;;  and errors to the error buffer.
;; If there are errors show the error buffer.
;; If there are no errors, show the output buffer. If
;; nusmv-output-replaces-source-flag is true
;; output is shown in the window used by the source code otherwise it is
;; pop-up.

;;
;; This can give strange results if the current buffer is not the one
;; of the selected window. So you should not call nusmv-run after a set-buffer.


;; All the process is file name based. For instance in order to find the
;; options file and to give a name to the output buffer a smv source buffer
;; must be visiting a file. So we make sure this is the case.
;; WARNING: If the current buffer has no file-name then the prompt for asking
;; one says : 'Save current buffer in file:' so don't call nusmv-run
;; if the current buffer is not the selected one.

(defun nusmv-buffer-file-name ()
  "Return the current buffer visiting file name.
If none ask the user to save the current buffer in a file."
  (if (buffer-file-name)
      (buffer-file-name)
    (progn
      (let ((file-name
	(read-file-name "Save current buffer in file: ")))
	(write-file file-name)
	(buffer-file-name)))))

(defun nusmv-get-options-file-name ()
"Return the name of the current buffer options file."
 (let ((match (string-match
	       (concat "\\." nusmv-source-extension "$")
	       (nusmv-buffer-file-name))))
   (if match
       (concat (substring (nusmv-buffer-file-name)
				   0 match)
	       "." nusmv-options-extension)
     (concat (nusmv-buffer-file-name) "." nusmv-options-extension))))



(defun nusmv-get-output-file-name  ()
"Return the name of the current buffer output file."
 (let ((match (string-match
	       (concat "\\." nusmv-source-extension "$")
	       (nusmv-buffer-file-name))))
   (if match
       (concat (substring (nusmv-buffer-file-name)
				   0 match)
	       "." nusmv-output-extension)
     (concat (nusmv-buffer-file-name) "." nusmv-output-extension))))


(defun nusmv-get-order-file-name ()
"Return the name of the current buffer order file."

 (let ((match (string-match
	       (concat "\\." nusmv-source-extension "$")
	       (nusmv-buffer-file-name))))
   (if match
       (concat (substring (nusmv-buffer-file-name)
				   0 match)
	       "." nusmv-order-extension)
     (concat (nusmv-buffer-file-name) "." nusmv-order-extension))))


(defun nusmv-get-options-buffer ()
  "Return the current-buffer's options buffer if it exists and nil otherwise."
  (get-file-buffer (nusmv-get-options-file-name)))

(defun nusmv-get-output-buffer ()
  "Return the current-buffer's output buffer if it exists and nil otherwise."
  (get-file-buffer (nusmv-get-output-file-name)))

(defun nusmv-get-order-buffer ()
  "Return the current-buffer's order buffer if it exists and nil otherwise."
  (get-file-buffer (nusmv-get-order-file-name)))


(defun nusmv-get-options-buffer-create ()
 "Return the current-buffer's options buffer if it exists, otherwise create it."
  (let ((buf (nusmv-get-options-buffer)))
    (if buf buf
      (find-file-noselect (nusmv-get-options-file-name)))))


;; We define an nusmv-output-mode but let the user choose it or fundamental
;; mode to display output.


(defvar nusmv-output-mode-map '())

;; RV 2008/08/23 New commands' list as for NuSMV 2.4.3
(defvar nusmv-commands
(list
"add_property"
"alias"
"bmc_setup"
"bmc_simulate"
"build_boolean_model"
"build_flat_model"
"build_model"
"check_ctlspec"
"check_fsm"
"check_invar"
"check_invar_bmc"
"check_invar_bmc_inc"
"check_ltlspec"
"check_ltlspec_bmc"
"check_ltlspec_bmc_inc"
"check_ltlspec_bmc_onepb"
"check_ltlspec_sbmc"
"check_ltlspec_sbmc_inc"
"check_property"
"check_pslspec"
"check_pslspec_bmc"
"check_pslspec_sbmc"
"check_pslspec_bmc"
"check_pslspec_bmc_inc"
"compute"
"compute_reachable"
"dynamic_var_ordering"
"echo"
"encode_variables"
"flatten_hierarchy"
"gen_invar_bmc"
"gen_ltlspec_bmc"
"gen_ltlspec_bmc_onepb"
"gen_ltlspec_sbmc"
"get_internal_status"
"go"
"go_bmc"
"goto_state"
"help"
"history"
"pick_state"
"print_bdd_stats"
"print_clusterinfo"
"print_current_state"
"print_fair_states"
"print_fair_transitions"
"print_fsm_stats"
"print_iwls95options"
"print_reachable_states"
"print_usage"
"process_model"
"quit"
"read_model"
"read_trace"
"reset"
"set"
"set_bdd_parameters"
"show_plugins"
"show_property"
"show_traces"
"show_vars"
"simulate"
"source"
"time"
"unalias"
"unset"
"usage"
"which"
"write_boolean_model"
"write_flat_model"
"write_order"
)
"List of interactive commands.
\\<nusmv-output-mode-map \\[comint-dynamic-complete] in nusmv-output mode will complete these commands.")


(defun nusmv-dynamic-complete ()
  "Complete to a NuSMV command if just after prompt otherwise to a file name."
    (let ((end (point))
	  (beg)
	  (first-word-beg))
      (save-excursion
	;; go back to the beginning of the word
	(skip-chars-backward "^ \t" (line-beginning-position))
	(setq beg (point))

	;; go back to first word
	(beginning-of-line)
	(skip-chars-forward " \t" (line-end-position))
	(setq first-word-beg (point)))

      ;; if just pass prompt
      (if (eq first-word-beg beg)
	  ;; complete as a command
	    (comint-dynamic-simple-complete
	     (buffer-substring beg end) nusmv-commands)
	;; else as a filename
	(comint-dynamic-complete-filename))))


(defun nusmv-output-mode ()
  "This is comint mode customize to NuSMV.
\\<nusmv-output-mode-map>

\\{nusmv-output-mode-map}

\\[comint-dynamic-complete] will complete a NuSMV command or a file name if it is following a command.
\\[comint-previous-input] will allow to get back a previous command."

  (interactive)
  (require 'comint)
  (kill-all-local-variables)

  (cond ((not nusmv-output-mode-map)
	 (setq nusmv-output-mode-map (copy-keymap comint-mode-map))
	 (define-key nusmv-output-mode-map "\t" 'comint-dynamic-complete)
	 (define-key nusmv-output-mode-map "\M-?"
	   'comint-dynamic-list-filename-completions)))

  (comint-mode)

  (setq comint-prompt-regexp nusmv-prompt-regexp)
  (setq major-mode 'nusmv-output-mode)
  (setq mode-name "NuSMV output")
  (use-local-map nusmv-output-mode-map)

  (setq comint-dynamic-complete-functions '(nusmv-dynamic-complete))
  (run-hooks 'nusmv-output-mode-hook))



;; We define an nusmv-error-mode but let the user choose it or fundamental
;; mode to display errors.

(defvar nusmv-error-mode-map
  (let ((map (cons 'keymap compilation-minor-mode-map)))
    (define-key map " " 'scroll-up)
    (define-key map "\^?" 'scroll-down)
    ;; Set up the menu-bar
    (define-key map [menu-bar compilation-menu]
      (cons "Compile" (make-sparse-keymap "Compile")))
    (define-key map [menu-bar compilation-menu compilation-mode-separator2]
      '("----" . nil))
    (define-key map [menu-bar compilation-menu compilation-mode-first-error]
      '("First Error" . first-error))
    (define-key map [menu-bar compilation-menu compilation-mode-previous-error]
      '("Previous Error" . previous-error))
    (define-key map [menu-bar compilation-menu compilation-mode-next-error]
      '("Next Error" . next-error))
    (define-key map [menu-bar compilation-menu compilation-separator2]
      '("----" . nil))
    (define-key map [menu-bar compilation-menu compilation-mode-grep]
      '("Search Files (grep)" . grep))
    map)
  "Keymap for nusmv-error-mode.
`compilation-minor-mode-map' is a cdr of this.")


(defun nusmv-error-mode ()
  "This mode use compilation minor mode to show NuSMV errors.
A click with the middle button on an error message will bring you to
the error source.
\\<nusmv-error-mode-map>

\\{nusmv-error-mode-map}"

  (require 'compile)
  (kill-all-local-variables)

  (use-local-map nusmv-error-mode-map)
  (setq major-mode 'nusmv-error-mode)
  (setq mode-name "NuSMV errors")

  (make-local-variable 'compilation-error-regexp-alist)
  ;; Add the regular expression format of the NuSMV error messages
  ;; The regular expression given must contain two parenthesis pairs.
  ;; One around the file name and one around the line number
  ;; There could be a third one around the character position, but
  ;; is not used since NuSMV don't give it.
  (setq compilation-error-regexp-alist
	(cons (list nusmv-error-regexp-1 1 2)
	      compilation-error-regexp-alist))

  ;; highlight
  (set (make-local-variable 'font-lock-defaults)
       '(compilation-mode-font-lock-keywords t))

  (compilation-minor-mode)
  (run-hooks 'nusmv-error-mode-hook))


(defun nusmv-get-output-buffer-create ()
 "Return the current-buffer's output buffer if it exists, otherwise create it.
Put this buffer in nusmv-output-mode-fun"
  (let ((buf (nusmv-get-output-buffer)))
    (save-excursion
      (set-buffer (if buf buf
		    (find-file-noselect (nusmv-get-output-file-name) t)))

      ;; But in the right mode if necessary
      (if (not (eq major-mode nusmv-output-mode-fun))
	  (funcall nusmv-output-mode-fun))

      (current-buffer))))

(defun nusmv-get-order-buffer-create ()
 "Return the current-buffer's order buffer if it exists, otherwise create it."
  (let ((buf (nusmv-get-order-buffer)))
    (if buf buf
      (find-file-noselect (nusmv-get-order-file-name)))))


(defun nusmv-get-error-buffer-create ()
 "Return the error buffer if it exists, otherwise create it.
Put this buffer in nusmv-error-mode-fun"
  (let ((buf (get-buffer nusmv-error-buffer-name)))
      (save-excursion
	(set-buffer (if buf buf
		      (get-buffer-create nusmv-error-buffer-name)))
     ;; But in the right mode if necessary
      (if (not (eq major-mode nusmv-error-mode-fun))
	  (funcall nusmv-error-mode-fun))

      (current-buffer))))


(defun nusmv-read-options (&optional buffer)
  "Read options from `current-buffer' skipping comments and end-of-lines.
If BUFFER is given read from it.  Comments are delimited by '#' and
end-of-lines.  Keep everything that is between shell quotes (' and \")"

  (save-excursion
    (let ((buf (if buffer buffer (current-buffer)))
	  (beg nil)
	  (end nil)
	  (result ""))
      (set-buffer buf)

      (goto-char (point-min))

      (while (not (eobp))
	;; find the next non-comment non-empty line
	(while (and (or (char-equal (following-char) ?#)
			(char-equal (following-char) ?\n))
		    (not (eobp)))
	  (forward-line))

	(setq beg (point))
	;; find the end-of-line or comment beginning
	;; skip all shell quotes, i.e. ' and "
	;; This means that any end-of-line between shell quotes
	;; will be kept.
	(setq end nil)

	(while (and (not (eobp)) (not end)) ; no end found
	  ;; Go to first # ' " or end-of-line
	  (skip-chars-forward "^#'\"\n")
	  (cond
	   ((char-equal (following-char) ?#)
	    (setq end (point))
	    )
	   ((char-equal (following-char) ?')
	    (forward-char)
	    (skip-chars-forward "^'")
	    (if (eobp)
		(error "No closing %c in buffer %s" ?' (buffer-name))
	      (forward-char))
	    )
	   ((char-equal (following-char) ?\")
	    (forward-char)
	    (skip-chars-forward "^\"")
	    (if (eobp)
		(error "No closing %c in buffer %s" ?\" (buffer-name))
	      (forward-char))
	    )
	   ((char-equal (following-char) ?\n)
	    (setq end (point))
	    )
	   ))

	;; if at buffer end give the string without checking
	;; if we were expecting an ending quote
	(if (not end) (setq end (point-max)))

	;; append to result
	(setq result (concat result
			     (buffer-substring-no-properties beg end)))
	)
      result)))



(defun nusmv-options-file-content ()
  "Return as a string the content of current-buffer's options file.
Return a empty string if there is no such file.
Here we really mean the file.  We look at the options buffer only if it is not
modified."

  (let* ((opt-file-name (nusmv-get-options-file-name))
	 (opt-buffer (nusmv-get-options-buffer)))

    ;; If opt-buffer is visiting option file and
    ;; buffer is up to date.
    (if (and opt-buffer (not (buffer-modified-p opt-buffer)))
	(nusmv-read-options opt-buffer)
      ;; otherwise if it exists open the options file
      ;; to read directly in it.
      (if (file-exists-p opt-file-name)
	  ;; open in a temporaly buffer to read the options file
	  (with-temp-buffer
	    (insert-file-contents opt-file-name)
	    (nusmv-read-options))
	;; otherwise there are no options
	""))))


(defun nusmv-get-command-string ()
  "Get the command string for running NuSMV from the user.
We propose a string build from nusmv-command and options from options file
\(if `nusmv-options-from-file-flag' is true).  There is also a command history."

  (let ((command (concat nusmv-command
			 (if nusmv-options-from-file-flag
			     (concat " " (nusmv-options-file-content) " ") " ")
			 (file-relative-name (nusmv-buffer-file-name)))))
    (read-string "NuSMV command: "
		 command
		 nusmv-command-history)))



(defun nusmv-read-m4-options (&optional buffer)
  "Read m4 options from `current-buffer' skipping comments and end-of-lines.
If BUFFER is given read from it.  Comments are delimited by '#' and
end-of-lines.  Keep everything that is between shell quotes (' and \")"

  (save-excursion
    (let ((buf (if buffer buffer (current-buffer)))
	  (beg nil)
	  (end nil))
      (set-buffer buf)

      (goto-char (point-min))

      ;; find -m4options
      (while (and (not (eobp))
		  (not beg))
	;; find the next non-comment non-empty line
	(cond
	 ((looking-at (regexp-quote "-m4options"))
	  (skip-chars-forward "^ \t\n")
	  (skip-chars-forward " \t\n")
	  (setq beg (point))
	  )
	 ((char-equal (following-char) ?#)
	  (skip-chars-forward "^\n")
	  (forward-char)
	  )
	 ((char-equal (following-char) ?')
	  (forward-char)
	  (skip-chars-forward "^'")
	  (if (eobp)
	      (error "No closing %c in buffer %s" ?' (buffer-name))
	    (forward-char))
	  )
	 ((char-equal (following-char) ?\")
	  (forward-char)
	  (skip-chars-forward "^\"")
	  (if (eobp)
	      (error "No closing %c in buffer %s" ?\" (buffer-name))
	    (forward-char))
	  )
	 (t (forward-char))
	 ))

      (if beg
	  (progn
	    (cond
	     ((char-equal (following-char) ?')
	      (forward-char)
	      (setq beg (point))
	      (skip-chars-forward "^'")
	      (if (eobp)
		  (error "No closing %c in buffer %s" ?' (buffer-name))
		(setq end (point)))
	      )
	     ((char-equal (following-char) ?\")
	      (forward-char)
	      (setq beg (point))
	      (skip-chars-forward "^\"")
	      (if (eobp)
		  (error "No closing %c in buffer %s" ?\" (buffer-name))
		(setq end (point)))
	      )
	     (t (skip-chars-forward "^ \t\n")
		(setq end (point))
		)
	     )
	    (buffer-substring-no-properties beg end)
	    )
	"")
      )))


(defun nusmv-m4-options ()
  "Return as a string the m4 options of the current-buffer's options file.
Return a empty string if there is no such file.
Here we really mean the file.  We look at the options buffer only if it is not
modified."

  (let* ((opt-file-name (nusmv-get-options-file-name))
	 (opt-buffer (nusmv-get-options-buffer)))

    ;; If opt-buffer is visiting option file and
    ;; buffer is up to date.
    (if (and opt-buffer (not (buffer-modified-p opt-buffer)))
	(nusmv-read-m4-options opt-buffer)
      ;; otherwise if it exists open the options file
      ;; to read directly in it.
      (if (file-exists-p opt-file-name)
	  ;; open in a temporaly buffer to read the options file
	  (with-temp-buffer
	    (insert-file-contents opt-file-name)
	    (nusmv-read-m4-options))
	;; otherwise there are no options
	""))))


(defun nusmv-get-m4-command-string ()
  "Get the command string for running m4 from the user.
We propose a string build from `nusmv-m4-file-name' and nusmv-buffer-file-name.
There is also a command history."

  (let ((command (concat nusmv-m4-file-name
			 " "
			 (nusmv-m4-options)
			 " "
			 (file-relative-name (nusmv-buffer-file-name)))))
    (read-string "m4 command: "
		 command
		 nusmv-m4-command-history)))


(defun nusmv-start-process (command output-buffer error-buffer)
  "Start a process associate with output-buffer and running COMMAND.
OUTPUT-BUFFER-process will be the name of the process.
If OUTPUT-BUFFER has already a running process ask to kill it.
Return the process created."

  ;; Do as in compile internal
  (save-excursion
    (set-buffer output-buffer)

    ;; In order to use compilation-mode with the error
    ;; buffer you need to have at least two first lines
    ;; containing no error message. This makes sure
    ;; compilation-parse-errors works properly.
    (with-current-buffer
	(nusmv-get-error-buffer-create)
      ;; Always erase.
      (erase-buffer)
      (insert "cd " default-directory "\n"
	      command "\n\n"))
    ;; We erase the error buffer before killing a running process since this
    ;; will put an error message and show the error buffer. Maybe we should
    ;; turn off error messages here.

    (let ((output-proc (get-buffer-process (current-buffer))))
      (if output-proc
	  ;; already a process
	  (if (or (not (eq (process-status output-proc) 'run))
		  (yes-or-no-p
		   (format "A NuSMV process is running in buffer %s; kill it? "
			   (buffer-name))))
	      (condition-case ()
		  (progn
		    (interrupt-process output-proc)
		    (sit-for 1)
		    (delete-process output-proc)
		    (sit-for 0) ; let a chance to the buffer to get
				; the process output and errors
		    )
		(error nil))
	    (error "Cannot have two processes in `%s' at once"
		   (buffer-name))
	    )))


    ;; erase output-buffer if necessary
    (if (or  (eq nusmv-output-behavior 'erase-no-confirm)
	     (eq nusmv-output-behavior 'erase-confirm))
	(save-excursion (set-buffer output-buffer)
			(erase-buffer)))


    (start-process (concat (buffer-name) "-process")
		   (current-buffer)
		   nusmv-shell-file-name
		   "-c" command)))



(defun nusmv-insert-error (string &optional silently)
  "Insert STRING in the error buffer.
If SILENTLY is true don't show the error buffer."

  (let ((buffer (nusmv-get-error-buffer-create)))
    (if (buffer-name buffer)
	(progn
	  (if (not silently)
	      (display-buffer buffer))

	  (with-current-buffer buffer
	      ;; Insert the text
	      (goto-char (point-max))
	      (insert string)))

      ;; if no more buffer
      (error "%s" "Impossible to create an error-buffer" )))
  )


(defun nusmv-output-refilter (process string)
  "If the output mode need some further filtering put it here."

  (if (eq nusmv-output-mode-fun 'nusmv-output-mode)
      (comint-output-filter process string)
    (with-current-buffer (process-buffer process)
      (let ((moving (= (point) (process-mark process))))
	(save-excursion
	  ;;Insert the text, advancing the process marker.
	  (goto-char (process-mark process))
	  (insert string)
	  (set-marker (process-mark process) (point)))
	(if moving (goto-char (process-mark process)))))))


(defun nusmv-insert-output (process string)
  "Insert STRING in PROCESS' buffer.
If this buffer doesn't exists anymore, output an error message and kill
the process."

  (let ((buffer (process-buffer process)))
    (if (buffer-name buffer)
	(progn
	  (pop-to-buffer buffer)

	  (nusmv-output-refilter process string))
      ;; if no more buffer
      (progn
	(nusmv-insert-error
	 (format "No more buffer to send output of %s: Terminating process.\n"
		 (process-name process)
		 ))

	;; remove from nusmv-process-string-alist
	;; info elisp says
;;  - Function: assq-delete-all key alist
;;      This function deletes from ALIST all the elements whose CAR is
;;      `eq' to KEY.  It returns ALIST, modified in this way.  Note that
;;      it modifies the original list structure of ALIST.
	;; but this doesn't seems to always work.

	(setq nusmv-process-string-alist
	      (assq-delete-all process nusmv-process-string-alist))
	(delete-process process))))) ; kill-process ?


(defun nusmv-substring (process string beg end)
  "If BEG and END are integers just return the substring.
If BEG is not an integer append before STRING the string associate with
PROCESS in `nusmv-process-string-alist'. In this last case remove the entry
for PROCESS in this alist."

  (if (not (integerp beg)) ; get the entry in nusmv-process-string-alist
      (progn
	(let ((value (cdr (assq process nusmv-process-string-alist))))

	  (setq nusmv-process-string-alist
		(assq-delete-all process nusmv-process-string-alist))
	  (concat value (substring string 0 end))))

    ;; beg is an integer. The entry in nusmv-process-string-alist has already
    ;; been used
    (progn
      (substring string beg end))))


(defun nusmv-error (string)
  "Return true if STRING is an error message."
  (if (string-match nusmv-error-regexp string) t nil)
  )


;; An error message start with a line for which `nusmv-error' returns
;; t and MUST end with an empty line. Up to the empty line everything
;; is sent to the error-buffer.
(defvar nusmv-error-flag nil
  "If non-nil then all output will be sent to the error buffer.
Otherwise the output is pattern-matched using `nusmv-error'.")

(defun nusmv-output-filter (process string)
  "Send complete lines to either error or output buffer.
Patterh match them agains `nusmv-error-regexp' to see where to send them."

   (let ((beg nil) ; nil means to take the beginning of the message
		   ; from nusmv-process-string-alist
	 (end 0))

     ;; look for the next end-of-line
     (while (setq end (string-match "\n" string end))
       ;; get the complete line with its line-end
       (setq message (nusmv-substring process
				      string
				      beg
				      (+ 1 end)))

       (cond
	(nusmv-error-flag (if (string= message "\n")
			      (setq nusmv-error-flag nil))
			  (nusmv-insert-error message)
			  )
	((nusmv-error message) (setq nusmv-error-flag t)
	                       (nusmv-insert-error message))
	(t (nusmv-insert-output process message)))
       ;; look for next line
       (setq beg (+ end 1))
       (setq end (+ end 1))
       )


     ;; keep the incomplete line
     (if beg ; no entry in nusmv-process-string-alist
	 ;; if not empty keep
	 (if (not (string= (substring string beg) ""))
	     (setq nusmv-process-string-alist
		   (cons
		    (cons process (substring string beg))
		    nusmv-process-string-alist)))
       ;; beg is nil so there is an entry in  nusmv-process-string-alist
       ;; concat to this entry
       (if (not (string= string ""))
	   (let ((value (cdr (assq process nusmv-process-string-alist))))
	     ;; remove the entry
	     (setq nusmv-process-string-alist
		   (assq-delete-all process nusmv-process-string-alist))
	     ;; add the concatenation
	     (setq nusmv-process-string-alist
		   (cons
		    (cons process (concat value string))
		    nusmv-process-string-alist))))))

   ;; let go some non-complete lines like the prompt and questions
   ;; Useful in interactive mode.
   (let* ((value (cdr (assq process nusmv-process-string-alist)))
          (found (and value (string-match nusmv-complete-output-regexp
					  value))))
     (if found
	 (progn
	   ;; show it
	   (nusmv-insert-output process
				(substring value 0 (match-end 0)))
	   ;; remove the entry
	   (setq nusmv-process-string-alist
		 (assq-delete-all process nusmv-process-string-alist))
	   ;; add the remainder if non empty
	   (let ((remainder (substring value (match-end 0))))
	     (if (not (string= remainder ""))
		 (setq nusmv-process-string-alist
		       (cons
			(cons process remainder)
			nusmv-process-string-alist))))))))




;; Si le processus est nil ca ne marche pas.
(defun nusmv-output-sentinel (output-process description-string)
  "This function is called when there is a status change for OUTPUT-PROCESS."

  ;; Exit
  (cond ((eq (process-status output-process) 'exit)

 	 ;; send the incomplete line to output
	 (let ((inc-line (cdr (assq
			       output-process nusmv-process-string-alist))))
	   (if inc-line ;; something
	       (nusmv-insert-output output-process
 			      (concat inc-line
				      "\n"))))

	 (nusmv-insert-output output-process "\nNuSMV process finished\n")
	 (nusmv-insert-error (format
			      "\nNuSMV execution finished at %s\n"
			      (current-time-string)) t)

 	 ;; remove from nusmv-process-string-alist
	 (setq nusmv-process-string-alist
	       (assq-delete-all output-process nusmv-process-string-alist))
 	 (delete-process output-process) ;; ou (kill-process output-process)
 	 )

 	((eq (process-status output-process) 'signal)

 	 ;; send the incomplete line to output
	 (let ((inc-line (cdr (assq
			       output-process nusmv-process-string-alist))))
	   (if inc-line ;; something
	       (nusmv-insert-output output-process
 			      (concat inc-line
				      "\n"))))

	 ;; give an error message
	 (nusmv-insert-output output-process "\nNuSMV process finished\n")
 	 (nusmv-insert-error (format "%s has received signal %d: %s\n"
 				     (process-name output-process)
 				     (process-exit-status output-process)
 				     description-string))

	 (nusmv-insert-error
	  (format
	   "NuSMV exited abnormally with code %d at %s\n"
	   (process-exit-status output-process)
	   (current-time-string)))


 	 ;; remove from nusmv-process-string-alist
	 (setq nusmv-process-string-alist
	       (assq-delete-all output-process nusmv-process-string-alist))
 	 (delete-process output-process) ;; ou (kill-process output-process)
	 )))


(defun nusmv-proceed-with-output-p ()
  "Return true if it is fine to write into the output buffer."

  (or (not (nusmv-get-output-buffer))
      (eq nusmv-output-behavior 'append)
      (eq nusmv-output-behavior 'erase-no-confirm)
      (not (buffer-modified-p (nusmv-get-output-buffer)))
      (yes-or-no-p
       (format "Content of buffer %s will be erased. Proceed anyway? "
	       (buffer-name (nusmv-get-output-buffer))))))


(defun nusmv-not-output-buffer-p ()
  "Return t if the current buffer is visiting a file of name not ending with
`nusmv-output-extension'."

  (if (buffer-file-name)
      (let ((match (string-match
		    (concat "\\." nusmv-output-extension "$")
		    (buffer-file-name))))
	(if match nil t))
    nil))


(defun nusmv-run ()
  "Run NuSMV on the current buffer."
  (interactive)

  (let ((error-file-name nil)
	(command-string))

    ;; Ask to save any unsaved buffers except output and
    ;; error buffers.
    (save-some-buffers nil 'nusmv-not-output-buffer-p)

    ;; Display the prompt
    (setq command-string (nusmv-get-command-string))

    ;; confirm erasing output buffer if necessary
    (if (nusmv-proceed-with-output-p)

	(progn (setq output-buffer
		     (nusmv-get-output-buffer-create))

;; 	       ;; erase output-buffer if necessary
;; 	       (if (or  (eq nusmv-output-behavior 'erase-no-confirm)
;; 			(eq nusmv-output-behavior 'erase-confirm))
;; 		   (save-excursion (set-buffer output-buffer)
;; 				   (erase-buffer)))


;; 	       ;; In order to use compilation-mode with the error
;; 	       ;; buffer you need to have at least two first lines
;; 	       ;; containing no error message. This makes sure
;; 	       ;; compilation-parse-errors works properly.
;; 	       (with-current-buffer
;; 		   (nusmv-get-error-buffer-create)
;; 		 ;; Always erase.
;; 		 (erase-buffer)
;; 		 (insert "cd " default-directory "\n"
;; 			 command-string "\n\n"))


	       ;; execute command
	       (let ((output-process
		      (nusmv-start-process command-string
					   output-buffer
					   (nusmv-get-error-buffer-create)
					   )))

		 ;; Now put up a sentinel to take care of process exit
		 ;; and signals
		 (set-process-sentinel output-process
				       'nusmv-output-sentinel)

		 ;; Put up a filter that sends output to output buffer and
		 ;; errors to error-buffer
		 (set-process-filter output-process 'nusmv-output-filter))

	       ;; While waiting for output display source if
	       ;; needed and the output buffer.
	       ;; If we find some errors the filter will
	       ;; display the error buffer.
	       (if nusmv-output-replaces-source-flag
		   (switch-to-buffer output-buffer)
		 (pop-to-buffer output-buffer))))) ; ou display-to-buffer
  )



(defun nusmv-edit-options ()
  "Let the user edit options."
  (interactive)

  (let ((opt-file-name (nusmv-get-options-file-name))
	(opt-buffer (nusmv-get-options-buffer)))

    ;; Go to buffer if it exists
    (if opt-buffer
	(switch-to-buffer opt-buffer)
      ;; load file. If none, create a new one
      (progn
	(save-excursion
	  (setq opt-buffer (nusmv-get-options-buffer-create))
	  (set-buffer opt-buffer)
	  (switch-to-buffer opt-buffer))))))




(defun nusmv-edit-bdd-order ()
  "Let the user edit order file."
  (interactive)

  (let ((ord-file-name (nusmv-get-order-file-name))
	(ord-buffer (nusmv-get-order-buffer)))

    ;; Go to buffer if it exists
    (if ord-buffer
	(switch-to-buffer ord-buffer)
      ;; load file. If none, create a new one
      (progn
	  (save-excursion
	    (setq ord-buffer (nusmv-get-order-buffer-create))
	    (set-buffer ord-buffer)
	    (switch-to-buffer ord-buffer))))))



(defun nusmv-generate-order-file ()
  "Run NUSMV with -o option to generate a variable order file."
  ;; Do as for nusmv-run but put output in order file
  ;; execute in a synchronous way (so we wait for the process to finish)

  (interactive)

  (let ((error-file-name nil)
	(command-string))

    ;; Ask to save any unsaved buffers except output and
    ;; error buffers.
    (save-some-buffers nil 'nusmv-not-output-buffer-p)

    ;; Display the prompt
    (setq command-string (nusmv-get-command-string))

    ;; Insert the -o order-file-name just after the NuSMV command
    (let ((match (string-match " " command-string)))
      (setq command-string
	    (concat (substring command-string 0 match)
		    " -o "
		    (file-relative-name (nusmv-get-order-file-name))
		    (substring command-string match))))


    ;; Confirm that the order will go to order file
    (if (yes-or-no-p
	 (format (concat "Generated order will"
			 (if (file-exists-p (nusmv-get-order-file-name))
			     " replace the content of "
			   " be put in ")
			 "file %s. Proceed anyway? ")
		 (file-relative-name (nusmv-get-order-file-name))))

	(progn
	  ;; In order to use compilation-mode with the error
	  ;; buffer you need to have at least two first lines
	  ;; containing no error message. This makes sure
	  ;; compilation-parse-errors works properly.
	  (with-current-buffer
	      (nusmv-get-error-buffer-create)

	    ;; Always erase.
	    (erase-buffer)

	    (insert "cd " default-directory "\n"
		    command-string "\n\n"))


	  ;; execute command sending output to error buffer
	  ;; and asking NuSMV to put order in order-file
	  (setq return-status
		(call-process nusmv-shell-file-name ; program
			      nil                   ; infile
			      (nusmv-get-error-buffer-create) ;destination
			      nil ; display
			      "-c" ;args
			      command-string))


	  (if (not (integerp return-status))
	      (nusmv-insert-error
	       (format "%s process has received signal : %s\n"
		       mode-name
		       return-status))
	    (if (/= return-status 0)
	      (nusmv-insert-error
	       (format "%s process has exited with status %d\n"
		       mode-name
		       return-status))
	      ;; no errors. Load order into order buffer if there is one
	      (if (nusmv-get-order-buffer)
		(save-excursion
		  (set-buffer (nusmv-get-order-buffer))
		  (if (yes-or-no-p
		       (format "Buffer %s already exists. Replace it with the new generated order? " (buffer-name (nusmv-get-order-buffer))))
		      (revert-buffer t ;ignore-auto
				 t ; noconfirm
				 ))))))))))


(defun nusmv-ask-output-buffer ()
  "Get a buffer name from the user.
Propose a buffer visiting file.smv for source file name of file.m4.smv
and file.smv.smv for a source name file.smv.  Here .smv means
.`nusmv-source-extension'.  Put buffer in `nusmv-mode'.  Return a buffer."

  (let* ((file-name (file-relative-name (nusmv-buffer-file-name)))
	 (match (string-match
		 (concat ".m4." nusmv-source-extension "$")
		 file-name))
	 (output-file))
    (if match
	(setq output-file (concat (substring file-name 0 match)
				  (concat "."
					  nusmv-source-extension)))
      (setq output-file (concat file-name "." nusmv-source-extension)))

    (read-string "Put m4 output in buffer: "
		 output-file)

    (with-current-buffer
	(find-file output-file)
      (nusmv-mode)
      (current-buffer))))


(defun nusmv-exp-with-m4 ()
  "Expand the current buffer using m4 preprocessor."
  ;; Do as for  nusmv-generate-order-file
  ;; Send output to a buffer (we propose name.smv for source name.m4.smv,
  ;; otherwise name.smv.smv)
  ;; Here .smv means .nusmv-source-extension.

  (interactive)

  (let ((error-file-name nil)
	(command-string)
	(output-buffer))

    ;; Ask to save any unsaved buffers except output and
    ;; error buffers.
    (save-some-buffers nil 'nusmv-not-output-buffer-p)

    ;; Display the prompt
    (setq command-string (nusmv-get-m4-command-string))

    ;; Ask into which buffer to put the m4 output
    (setq output-buffer (nusmv-ask-output-buffer))

    ;; If the buffer is modified ask to confirm erasing
    (if (or (not (buffer-modified-p output-buffer))
	    (yes-or-no-p
	     (format "Buffer %s is modified and will be erased. Proceed anyway? " (buffer-name output-buffer))))

	(progn
	  ;; Standard error AND standard output are send into
	  ;; the output-buffer. Ok, I could do as for nusmv-run
	  ;; and used the filter, but is it worth it?

	  ;; execute command sending output to output-buffer
	  (setq return-status
		(call-process nusmv-shell-file-name ; program
			      nil                   ; infile
			      output-buffer ;destination
			      nil ; display
			      "-c" ;args
			      command-string))


	  (if (not (integerp return-status))
	      (nusmv-insert-error
	       (format "%s process has received signal : %s\n"
		       mode-name
		       return-status))
	    (if (/= return-status 0)
		(nusmv-insert-error
		 (format "%s process has exited with status %d\nSee \
buffer %s for any error messages.\n"
			 mode-name
			 return-status
			 output-buffer))
	      ;; no error show buffer
	      (if nusmv-output-replaces-source-flag
		  (switch-to-buffer output-buffer)
		(pop-to-buffer output-buffer))))) ; ou display-to-buffer

      )))



(defun nusmv-interrupt ()
  "Kill current NuSMV process.
This is either this buffer's process or this buffer output buffer's process."
  (interactive)

  (let ((output-proc (get-buffer-process (current-buffer))))
    (if (not output-proc)
	(let ((output-buffer (nusmv-get-output-buffer)))
	  (if output-buffer
	      (setq output-proc
		    (get-buffer-process output-buffer)))))
    (if output-proc
	(condition-case ()
	    (progn
	      (interrupt-process output-proc)
	      (sit-for 1)
	      (delete-process output-proc))
	  (error nil))
      (error "%s" "This buffer has no associate process running"))))


;;;;
;;;; end Running NuSMV
;;;;


(defun nusmv-customize ()
  "Customize NuSMV."
  (interactive)

  (require 'cus-edit)
  (customize-group 'nusmv-custom-group))

;;;; define the keymap
(defconst nusmv-mode-map nil  "NuSMV keymap.")

(if nusmv-mode-map ()
  (progn
    (setq nusmv-mode-map (make-sparse-keymap))
    (define-key nusmv-mode-map "\C-j"  'newline-and-indent)
    (define-key nusmv-mode-map [delete] 'backward-delete-char-untabify)
    (define-key nusmv-mode-map [backspace] 'backward-delete-char-untabify)
    (define-key nusmv-mode-map "\C-c\C-e"  'nusmv-edit-options)
    (define-key nusmv-mode-map "\C-c\C-f"  'nusmv-run)
    (define-key nusmv-mode-map "\C-c\C-o"  'nusmv-edit-order-file)
    (define-key nusmv-mode-map "\C-c\C-c"  'nusmv-interrupt)
    (define-key nusmv-mode-map "\C-c\C-b"  'nusmv-edit-bdd-order)
    (define-key nusmv-mode-map "\C-c\C-g"  'nusmv-generate-order-file)
    (define-key nusmv-mode-map "\C-c\C-n"  'indent-region)
    (define-key nusmv-mode-map "\C-c\C-a"  'nusmv-indent-file)
    (define-key nusmv-mode-map "\C-c;"  'comment-region)
    (define-key nusmv-mode-map "\C-c:"  'nusmv-uncomment-region)

    (modify-frame-parameters (selected-frame)
                              '((menu-bar-lines . 2)))

     ;; Make a menu keymap (with a prompt string)
     ;; and make it the menu bar item's definition.
     (define-key nusmv-mode-map [menu-bar nusmv]
       (cons "NuSMV" (make-sparse-keymap "NuSMV")))

     ;; Define specific subcommands in this menu.
     (define-key nusmv-mode-map [menu-bar nusmv customize]
       '("Customize" . nusmv-customize))

     (define-key nusmv-mode-map [menu-bar nusmv separator1]
       '("--"))

     (define-key nusmv-mode-map [menu-bar nusmv nusmv_uncomment_region]
       '("Uncomment Region" . nusmv-uncomment-region))

     (define-key nusmv-mode-map [menu-bar nusmv comment_region]
       '("Comment Region" . comment-region))

      (define-key nusmv-mode-map [menu-bar nusmv separator2]
       '("--"))

     (define-key nusmv-mode-map [menu-bar nusmv indent_line_in_file]
       '("Indent Lines in File" . nusmv-indent-file))

     (define-key nusmv-mode-map [menu-bar nusmv indent_line_in_selection]
       '("Indent Lines in Selection" . indent-region))

      (define-key nusmv-mode-map [menu-bar nusmv separator3]
       '("--"))

     (define-key nusmv-mode-map [menu-bar nusmv edit_bdd_order]
       '("Edit BDD Order" . nusmv-edit-bdd-order))

     (define-key nusmv-mode-map [menu-bar nusmv nusmv-generate-order-file]
       '("Generate Order File" . nusmv-generate-order-file))

     (define-key nusmv-mode-map [menu-bar nusmv edit_options]
       '("Edit options" . nusmv-edit-options))

     (define-key nusmv-mode-map [menu-bar nusmv separator4]
       '("--"))

     (define-key nusmv-mode-map [menu-bar nusmv interrupt]
       '("Interrupt NuSMV Process" . nusmv-interrupt))

     (define-key nusmv-mode-map [menu-bar nusmv run_nusmv]
       '("Run NuSMV" . nusmv-run))))


;;;; define the nusmv-m4 keymap
(defconst nusmv-m4-mode-map nil  "NuSMV m4 keymap.")

; voir si toutes les entrees du menu ont des cles et si elles sont correctes.
(if nusmv-m4-mode-map ()
  (progn
	 (setq nusmv-m4-mode-map (copy-keymap nusmv-mode-map))
	 (define-key nusmv-m4-mode-map [menu-bar nusmv separator6]
	   '("--"))
	 (define-key nusmv-m4-mode-map "\C-c\C-p"  'nusmv-exp-with-m4)
	 (define-key nusmv-m4-mode-map [menu-bar nusmv nusmv_exp_with_m4]
	   '("Expand with M4" . nusmv-exp-with-m4))))


(defun nusmv-mode ()
  "Major mode for NuSMV specification files.
\\{nusmv-mode-map}
\\{nusmv-mode-map}

\\[nusmv-run] runs NuSMV on buffer, \\[nusmv-interrupt] interrupts already
running NuSMV process, \\[nusmv-edit-options] displays NuSMV command line
options in a separate buffer and allows the user to edit them.

The command line options are saved in *.`nusmv-options-extension' file and are
proposed when running NuSMV. This behavior can be turn off by customizing
`nusmv-options-from-file-flag'.

Running NuSMV (\\[nusmv-run]) creates two separate buffers. One containing
errors and one containing the NuSMV output. The error buffer is by default
in `nusmv-error-mode' which allow to middle click on errors message to get
to the source of the error. The output buffer is by default in
`nusmv-output-mode' which is similar to `shell-mode'.

This same file offer also a `nusmv-m4-mode' with an additional menu entry
to see the resulta of preprocessing the file with m4. This feature is
also available in `nusmv-mode' with \\[nusmv-exp-with-m4].

In order to load `nusmv-mode' for every file with extension .smv and
`nusmv-m4-mode' for files with extension .m4.smv but the following in
your ~/.emacs file and write the proper directory name.

;;; First if this file directory is not in your `load-path'
;;; add it with
\(setq load-path (cons \"PATH_OF_THIS_FILE\" load-path))

;;; Second make sure this NuSMV mode is loaded for any file with extension .smv
\(autoload 'nusmv-mode \"nusmv-mode\" \"Major mode for NuSMV specification files.\" t)
\(setq auto-mode-alist
      (append  (list '(\"\\.smv$\" . nusmv-mode))
	       auto-mode-alist))

;;; and nusmv-m4-mode for any file with extension m4.smv
\(autoload 'nusmv-m4-mode \"nusmv-mode\" \"`nusmv-mode' with m4 support.\" t)
\(setq auto-mode-alist
      (append  (list '(\"\\.m4.smv$\" . nusmv-m4-mode))
	       auto-mode-alist))

Please send bugs and suggestions to <villemaire.roger@uqam.ca>."
  (interactive)

  (kill-all-local-variables)

  (use-local-map nusmv-mode-map)


  ;; Fontification
  (make-local-variable 'font-lock-defaults)
  (setq font-lock-defaults
	'( (list nusmv-font-lock-keywords-1
		 nusmv-font-lock-keywords-2
		 nusmv-font-lock-keywords-3)
	   nil nil nil nil))


  ;; Indentation

  ;; All search and look-at are done case sensitive.
  (setq case-fold-search nil)

  ;; indent according to nusmv-indent-line
  (setq indent-line-function 'nusmv-indent-line)

  ;; end Indentation



  ;; Commenting

  ;; RV is this still useful ?
  ;; used by autofill and indent-new-comment-line
  (set (make-local-variable 'comment-start-skip) "---*[ \t]*")

  ;; comment-start is used by comment-region
  (make-local-variable 'comment-start)
  (setq comment-start nusmv-comment-start)

  ;; comment end must be set because it may hold a wrong value if
  ;; this buffer had been in another mode before. RE
  (set (make-local-variable 'comment-end) "")

  (set (make-local-variable 'comment-column) 40)

  ;; end Commenting



  ;; Running NuSMV

  ;; Set syntax table
  (set-syntax-table nusmv-mode-syntax-table)

  ;; Define the major mode
  (setq major-mode 'nusmv-mode)
  (setq mode-name "NuSMV")

  (run-hooks 'nusmv-mode-hook))


(defun nusmv-m4-mode ()
  "Same as `nusmv-mode' but with an additional entry in the menu for m4 expandsion."
  (interactive)
  (nusmv-mode)
  (use-local-map nusmv-m4-mode-map)
  ;; Define the major mode
  (setq major-mode 'nusmv-m4-mode)
  (setq mode-name "NuSMV-M4")
)


(provide 'nusmv-mode)

;;; nusmv-mode.el ends here
