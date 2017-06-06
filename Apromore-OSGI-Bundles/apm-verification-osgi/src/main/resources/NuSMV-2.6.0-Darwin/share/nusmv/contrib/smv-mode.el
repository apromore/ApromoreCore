;;;; Major mode for writing SMV programs.
;;;; Sergey Berezin, Jan. 1998. Send bugs and suggestions to 
;;;; sergey.berezin@cs.cmu.edu
;;;;
;;;; This file can be used and modified freely.

;;;; Modified by Marco Roveri roveri@fbk.eu to capture NuSMV
;;;; language features (i.e. to fontify and indent NuSMV
;;;; constructs). Notice that the commands to interact with SMV
;;;; executable works only if the CMU SMV is used they are not working
;;;; with NuSMV.

;;;; This library provides an emacs interface to SMV. The main features are
;;;; fontification (xemacs or gnu-emacs >= 19.30), line indentation (TAB)
;;;; and inferior execution. In addition, you can conveniently generate and
;;;; edit the variable ordering and command line parameters of SMV, that
;;;; can be saved together with the *.smv file and autoloaded when you open
;;;; this file again. For more information, type C-h m in any SMV buffer,
;;;; or read on.

;;;; key             binding
;;;; ---             -------

;;;; TAB		smv-indent-line
;;;; C-c		Prefix Command
;;;; C-x		Prefix Command

;;;; C-c C-c		smv-interrupt
;;;; C-c C-o		smv-edit-order-file
;;;; C-c C-f		smv-run
;;;; C-c C-e		smv-edit-options

;;;; C-x C-s		smv-save-buffer

;;;; C-c C-f runs SMV on buffer, C-c C-c interrupts already
;;;; running SMV process, C-c C-o generates if necessary
;;;; and displays the variable ordering file.  C-c C-e
;;;; displays SMV command line options in a separate buffer and allows the
;;;; user to edit them. Running M-x smv-save-and-load-options (usually
;;;; bound to C-c C-c) in that buffer saves and updates new options, closes
;;;; the option edit buffer and brings you back to current SMV buffer.

;;;; The command line options are saved in *.opt file and are autoloaded
;;;; when you open the SMV file. If you change some of the options, you
;;;; will be asked about saving them when you save the main file (C-x C-s).

;;;; Running SMV (C-c C-f) creates a separate buffer where inferior SMV
;;;; process will leave its output. Currently, each run of SMV clears the
;;;; compilation buffer. If you need to save multiple runs, save them one
;;;; at a time. I'll probably implement the option of not erasing this
;;;; buffer some time later.

;;;; To use this library, place the following lines into your ~/.emacs file:
;;;;
;;;; ;;; SMV mode
;;;; (autoload 'smv-mode "smv-mode" "SMV specifications editing mode." t)
;;;; (setq auto-mode-alist 
;;;;       (append  (list '("\\.smv$" . smv-mode) '("\\.ord$" . smv-ord-mode))
;;;; 	       auto-mode-alist))
;;;; (setq completion-ignored-extensions
;;;;       (cons ".ord" (cons ".opt" completion-ignored-extensions)))
;;;;
;;;; Of course, the file smv-mode.el must be in one of the directories in your
;;;; `load-path'. C-h v load-path to see the list, or `cons' your own path:
;;;; (setq load-path (cons "/the/full/path/to-your/dir" load-path))
;;;;
;;;; To turn the font-lock on by default, put in .emacs
;;;; (global-font-lock-mode t) ;; if you use gnu-emacs, or
;;;; (setq-default font-lock-auto-fontify t) ;; if you use xemacs.
;;;;
;;;; In GNU emacs faces `font-lock-preprocessor-face' and 
;;;; `font-lock-variable-name-face' may not be predefined.
;;;; In this case they are defined automatically when smv-mode.el
;;;; is loaded the first time. You can also define them yourself in .emacs:
;;;;
;;;; ;;; Make faces that are not in gnu-emacs font-lock by default
;;;; (defvar font-lock-preprocessor-face 'font-lock-preprocessor-face)
;;;; (defvar font-lock-variable-name-face 'font-lock-variable-name-face)
;;;; (make-face 'font-lock-preprocessor-face)
;;;; (make-face 'font-lock-variable-name-face)

(require 'font-lock)
(require 'compile)

(defvar smv-font-lock-mode-on t
  "If not nil, turn the fontification on.")

;;;; Syntax definitions

(defvar smv-mode-syntax-table nil  "Syntax table used while in SMV mode.")

(if smv-mode-syntax-table ()
    (let ((st (syntax-table)))
      (unwind-protect
	   (progn
	     (setq smv-mode-syntax-table (make-syntax-table))
	     (set-syntax-table smv-mode-syntax-table)
;	     (modify-syntax-entry ?_ "_")
;	     (modify-syntax-entry ?- "_")
	     (modify-syntax-entry ?_ "w")
;	     (modify-syntax-entry ?+ "w") ;; `+' is not a part of a word
	     (modify-syntax-entry ?- "w")
	    ; (modify-syntax-entry ?- "< 12") ;; Doesn't work???
	     (modify-syntax-entry ?\? "w")
	     (modify-syntax-entry ?: "." )
	     (modify-syntax-entry ?# "<")
	     (modify-syntax-entry ?\f ">")
	     (modify-syntax-entry ?\n ">"))
	(set-syntax-table st))))

;;;; Fontification stuff

;;; Add smv-mode to the list of fontified modes
(if (string-match "XEmacs" emacs-version) ()
  (setq font-lock-defaults-alist 
	(cons '(smv-mode smv-font-lock-keywords
			 nil nil nil nil)
	      font-lock-defaults-alist)))

(defun smv-keyword-match (keyword)
;  "Convert a string into a regexp matching any capitalization of that string."
  "Convert a string into a regexp matching that string as a separate word."
  (let ((regexp "")
	(index 0)
	(len (length keyword)))
;    (while (< index len)
;      (let ((c (aref keyword index)))
;	(setq regexp
;	      (concat regexp (format "[%c%c]" (downcase c) (upcase c))))
;	(setq index (+ index 1))))
    (format "\\b%s\\b" keyword)))

(defvar smv-font-lock-separator-face 'smv-font-lock-separator-face)
(defvar font-lock-preprocessor-face 'font-lock-preprocessor-face)
(defvar font-lock-variable-name-face 'font-lock-variable-name-face)

(if (facep 'font-lock-preprocessor-face) ()
    (progn
      (make-face 'font-lock-preprocessor-face)
      (set-face-foreground 'font-lock-preprocessor-face "green4")))

(if (facep 'font-lock-variable-name-face) ()
    (progn
      (make-face 'font-lock-variable-name-face)
      (set-face-foreground 'font-lock-variable-name-face "deeppink")))

(if (facep 'smv-font-lock-separator-face) ()
    (progn
      (make-face 'smv-font-lock-separator-face)
      (set-face-foreground 'smv-font-lock-separator-face "indianred")))

(defvar smv-mode-hook nil
  "Functions to run when loading an SMV file.")

(defconst smv-keywords
  '("MODULE" "VAR" "IVAR" "TRANS" "ASSIGN" "INVAR" "DEFINE" "SPEC"
    "LTLSPEC" "INVARSPEC" "JUSTICE" "COMPASSION" "process" "array"
    "FAIRNESS" "case" "esac" "AG" "EG" "AF" "EF" "AX" "EX" "U" "of" "mod"
    "A" "F" "G" "X" "xor" "xnor" "O" "H" "Y" "Z" "S" "V" "T" "TRUE" "FALSE"
    "0" "1" "for" "WORDS" "WORDDEFINE" "boolean" "next" "init" "INIT" "ISA"
    "powerof2" "wordconst" "toword" "self" "in" "notin" "COMPUTE" "MAX" "MIN"
    "PRINT" "hide" "expose")
  "The list of SMV keywords.")

(defconst smv-declaration-keywords
  '("MODULE" "VAR" "IVAR" "TRANS" "ASSIGN" "INVAR" "DEFINE" "SPEC" "LTLSPEC"
    "INVARSPEC" "FAIRNESS" "JUSTICE" "COMPASSION" "WORDS" "WORDDEFINE" "INIT"
    "ISA")
  "The list of keywords that open a declaration. Used for indentation.")

(defconst smv-declaration-keywords-regexp
  (mapconcat 'smv-keyword-match smv-declaration-keywords "\\|"))

(defconst smv-openning-keywords
  '("case" "for" "next" "init")
  "The list of keywords that open a subexpression. Used for indentation.")

(defconst smv-openning-keywords-regexp
  (mapconcat 'smv-keyword-match smv-openning-keywords "\\|"))

(defconst smv-closing-keywords
  '("esac")
  "The list of keywords that close a subexpression. Used for indentation.")

(defconst smv-closing-keywords-regexp
  (mapconcat 'smv-keyword-match smv-closing-keywords "\\|"))

(defconst smv-assignment-regexp
  (concat "\\(\\(\\(" (smv-keyword-match "init") "\\|"
		  (smv-keyword-match "next") 
		  "\\)(\\s-*\\([][_?.A-Za-z0-9-]+\\)\\s-*)\\)"
		  "\\|\\(\\s-*[][_?.A-Za-z0-9-]+\\)\\)\\s-*:=")
  "Regexp matching the beginning of an assignment. Used for indentation
purposes.")

(defconst smv-infix-operators
  '("<->" "<-" "->" ":=" "<=w\\>" ">=w\\>" "<w\\>" ">w\\>" "=w\\>"
    "+w\\>" "-w\\>" "*w\\>" "<=" ">=" "!=" "=" "\\[" "\\]"
    "\\b-\\b" "\\bin\\b" "\\bxor\\b" "\\bxnor\\b""\\bnotin\\b" "\\bmod\\b" "+" "|" "&" "<" ">")
  "The list of regexps that match SMV infix operators. The distinction
is made primarily for indentation purposes.")

(defconst smv-infix-operators-regexp
  (mapconcat 'identity smv-infix-operators "\\|"))

(defconst smv-other-operators
  '("!")
  "Non-infix SMV operators that are not listed in `smv-infix-operators'.")

(defconst smv-other-operators-regexp
  (mapconcat 'identity smv-other-operators "\\|"))

(defconst smv-operators (append smv-infix-operators smv-other-operators)
  "The list of regexps that match SMV operators. It is set to the
concatenation of `smv-infix-operators' and `smv-other-operators'.")

(defconst smv-separator-regexp "[,.;():]"
  "A regexp that matches any separator in SMV mode.")

(defun smv-minimal-decoration ()
  (interactive)
  (setq font-lock-keywords smv-font-lock-keywords-1))

(defun smv-maximal-decoration ()
  (interactive)
  (setq font-lock-keywords smv-font-lock-keywords-2))

(defconst smv-font-lock-keywords-1
  (purecopy
   (list
    (list (concat (smv-keyword-match "MODULE") " *\\([-_?A-Za-z0-9]+\\)")
	  1 'font-lock-preprocessor-face)
    (list (concat "\\(" (smv-keyword-match "init") "\\|"
		  (smv-keyword-match "next")
		  "\\)(\\s-*\\([][_?.A-Za-z0-9-]+\\)\\s-*)\\s-*:=")
	  2 'font-lock-variable-name-face)
    ;;; For DEFINE and invar assignments
    (list "\\([][_?.A-Za-z0-9-]+\\)\\s-*:="
	  1 'font-lock-variable-name-face)
    (list "\\<\\([Aa]\\|[Ee]\\)\\[" 1 'font-lock-keyword-face)
    (list (concat "\\("
		  (mapconcat 'identity smv-operators "\\|")
		  "\\)")
	  1 'font-lock-function-name-face 'prepend)
    (mapconcat 'smv-keyword-match smv-keywords "\\|")
;; Fix the `--' comments
    (list "\\(--.*$\\)" 1 'font-lock-comment-face t) ))
  "Additional expressions to highlight in SMV mode.")

(defconst smv-font-lock-keywords-2
  (purecopy 
   (append smv-font-lock-keywords-1
	   (list
	    (list "\\([{}]\\)" 1 'font-lock-type-face)
	    (list (concat "\\(" smv-separator-regexp "\\)")
		  1 'smv-font-lock-separator-face 'prepend))))
  "Additional expressions to highlight in SMV mode.")
  
(defconst smv-font-lock-keywords
  (if font-lock-maximum-decoration
      smv-font-lock-keywords-2
      smv-font-lock-keywords-1))

;;;; Running SMV 

(defvar smv-command "smv" 
  "The command name to run SMV. The defaul is usually \"smv\"")

(defvar smv-cache-size 32749
 "SMV cache size passed to SMV process under -c option.
The value can be a string or a number. Should better be a prime number.")

(defvar smv-mini-cache-size 32749
 "SMV mini-cache size passed to SMV process under -m option.
The value can be a string or a number. Should better be a prime number.")

(defvar smv-key-table-size 32749
 "SMV key table size passed to SMV process under -k option.
The value can be a string or a number. Should better be a prime number.")

(defvar smv-forward-search t
"If not nil, SMV process will be run with -f option.")

(defvar smv-report-option t
"If not nil, SMV process will be run with -r option.")

(defvar smv-order-file nil
  "The file name with variable ordering SMV process will use under option -i.
If this variable is nil, don't use this option.")

(defvar smv-verbose-level nil
"The verbose mode of SMV. Valid values are \"0\", \"1\", \"2\", etc.
However, any value higher than \"2\" is equivalent to \"2\".
This value is passed to the SMV process as -v opton.
If nil, don't use verbose mode.")

(defvar smv-heuristic-factor nil 
"Must be string representing real in [0.0..1.0] or nil. The variable
ordering is determined by a heuristic procedure which is based on the
syntactic structure of the program, and a floating point
heuristic-factor between 0.0 and 1.0 [This option is currently
broken. Set it to nil.].")

(defvar smv-command-line-args nil
  "Miscellaneous SMV command line args.
Must be a single string or nil.")

(defvar smv-compile-buffer nil
  "The buffer associated with inferior SMV process.
This variable is updated automatically each time SMV process takes off.")

(defvar smv-options-changed nil)

(defun smv-args (file &optional args)
  "Compiles the string of SMV command line args from various variables."
  (mapconcat 'identity
	     (append
	      (if smv-cache-size (list "-c" (format "%s" smv-cache-size)) nil)
	      (if smv-mini-cache-size 
		  (list "-m" (format "%s" smv-mini-cache-size))
		nil)
	      (if smv-key-table-size 
		  (list "-k" (format "%s" smv-key-table-size))
		nil)
	      (if smv-forward-search '("-f") nil)
	      (if smv-report-option '("-r") nil)
	      (if smv-order-file (list "-i" smv-order-file) nil)
	      (if smv-verbose-level (list "-v" smv-verbose-level) nil)
	      (if smv-heuristic-factor (list "-h" smv-heuristic-factor) nil)
	      (if smv-command-line-args 
		  (if (string-match "%s" smv-command-line-args)
		      (list (format smv-command-line-args file))
		    (list smv-command-line-args file))
		(list file))
	      (if args (list args) nil))
	     " "))

(defun smv-run ()
  "Runs SMV on the current buffer."
  (interactive)
  (let ((buffer (current-buffer)))
    (if (buffer-file-name)
	(progn
	  (if (buffer-modified-p) (smv-save-buffer))
	  (setq smv-compile-buffer 
		(compile-internal
		 (concat smv-command " " (smv-args (buffer-file-name)))
				  "No more errors"
				  "SMV"))
	  (set-buffer smv-compile-buffer) ;;; Doesn't work???
	  (end-of-buffer)
	  (set-buffer buffer)
	  )
    (error "Buffer does not seem to be associated with any file"))) )

(defun smv-generate-order-file ()
  "Run SMV with -o option to generate a variable order file."
  (interactive)
  (if (buffer-file-name)
      (let* ((buffer (current-buffer))
	     (buffer-file (file-name-nondirectory (buffer-file-name)))
	     (match (string-match "\\.smv$" buffer-file))
	     (order-file-name (if match
				 (concat (substring buffer-file 0 match)
					 ".ord")
				(concat buffer-file ".ord")))
	     (do-it (if (file-exists-p order-file-name)
			(y-or-n-p (format "The file %s already exists. Overwrite it?" order-file-name))
		      t)))
	(message nil)
	(if do-it
	    (progn
	      (save-some-buffers)
	      (setq smv-compile-buffer (get-buffer-create "*smv*"))
	      (set-buffer smv-compile-buffer)
	      (goto-char (point-max))
	      (let ((exit-code 
                  (apply 'call-process smv-command nil smv-compile-buffer nil 
			    "-o" order-file-name
			    (append 
			     (if smv-heuristic-factor 
				 (list "-h" (format "%s" smv-heuristic-factor))
			       nil)
			     (list buffer-file)))))
		(set-buffer buffer)
		(if (string= smv-order-file order-file-name) ()
		  (progn
		    (setq smv-order-file order-file-name)
		    (setq smv-options-changed t)))
		(if (and (= 0 exit-code) (file-exists-p order-file-name))
		    (find-file-other-window order-file-name)
		  (switch-to-buffer-other-window smv-compile-buffer))))))
    (error "Buffer does not seem to be associated with any file")) )

(defun smv-edit-order-file ()
  "Load and edit order file for the current SMV program. If the file
is not mentioned in the command line parameters, set it. If there is
no such file, generate it and load (see `smv-generate-order-file')."
  (interactive)
  (setq smv-current-buffer (current-buffer))
  (if (or smv-order-file (buffer-file-name))
      (let* ((buffer-file (file-name-nondirectory (buffer-file-name)))
	     (order-file-name (or smv-order-file
				  (let ((match (string-match "\\.smv$"
						       buffer-file)))
				    (if match
					(concat (substring buffer-file
							   0 match)
						".ord")
				      (concat buffer-file ".ord"))))))
	(setq smv-order-file order-file-name)
	(if (file-exists-p order-file-name)
	    (find-file-other-window order-file-name)
	  (progn
	    (message "Generating %s..." order-file-name)
	    (smv-generate-order-file)
	    (message "Generating %s...done" order-file-name))))
    (error "Buffer does not seem to be associated with any SMV file")) )

(defun smv-save-options ()
  "Saves current options in *.opt file."
  (interactive)
  (let* ((buffer (current-buffer))
	 (opt-file-name 
	  (let ((match (string-match "\\.smv$"
				     (buffer-file-name))))
	    (if match
		(concat (substring (buffer-file-name)
				   0 match)
			".opt")
	      (concat (buffer-file-name) ".opt"))))
	 (opt-buffer-name 
	  (let ((match (string-match "\\.smv$"
				     (buffer-name))))
	    (if match
		(concat (substring (buffer-name)
				   0 match)
			".opt")
	      (concat (buffer-name) ".opt"))))
	 (opt-buffer-exists (get-buffer opt-buffer-name))
	 (opt-buffer (get-buffer-create opt-buffer-name))
	 (save-options-from-buffer 
	  (and opt-buffer-exists 
	       (buffer-modified-p opt-buffer)
	       (y-or-n-p (format "buffer %s is modified. Save options from that buffer?" 
				 (buffer-name opt-buffer)))))
	 (options (format ";;;; This file is generated automatically.\n(setq smv-cache-size %S)\n(setq smv-mini-cache-size %S)\n(setq smv-key-table-size %S)\n(setq smv-forward-search %S)\n(setq smv-report-option %S)\n(setq smv-order-file %S)\n(setq smv-verbose-level %S)\n(setq smv-heuristic-factor %S)\n(setq smv-command-line-args %S)\n"
             smv-cache-size
             smv-mini-cache-size
             smv-key-table-size
             smv-forward-search
             smv-report-option
             smv-order-file
             smv-verbose-level
             smv-heuristic-factor
             smv-command-line-args )))
    (set-buffer opt-buffer)
    (if save-options-from-buffer (smv-save-and-load-options)
      (progn
	(erase-buffer)
	(insert options)
	(write-file opt-file-name)
	(kill-buffer opt-buffer)))
    (set-buffer buffer)
    (setq smv-options-changed nil)
    (message "Options are saved.")))

(defun smv-save-and-load-options ()
  "Saves the current buffer and updates SMV options in the associated
buffer.  This buffer is either the value of `smv-current-buffer', or
it tries to make a few reasonable guesses. If no SMV buffer is found,
only saves the current buffer.

Normally is called from the *.opt file while editing options for SMV
specification." 
  (interactive)
  (let* ((buffer (current-buffer))
	 (buffer-file (buffer-file-name))
	 (smv-buffer-name
	  (let* ((match (string-match "\\.[^.]*$" (buffer-name))))
	    (if match
		(concat (substring (buffer-name) 0 match) ".smv")
	      (concat (buffer-name) ".smv"))))
	 (smv-buffer (get-buffer smv-buffer-name))
	 (smv-buffer
	  (cond (smv-buffer smv-buffer)
		((and (boundp 'smv-current-buffer)
		      (buffer-live-p smv-current-buffer))
		 smv-current-buffer)
		(t nil))))
    (save-buffer)
    (if smv-buffer
	(let ((smv-window (get-buffer-window smv-buffer))
	      (window (get-buffer-window buffer)))
	  (set-buffer smv-buffer)
	  (load buffer-file)
	  (setq smv-current-buffer smv-buffer)
	  (if smv-window 
	      (select-window smv-window)
	    (switch-to-buffer smv-buffer))
	  (if (get-buffer-window buffer)
	      (delete-window (get-buffer-window buffer)))
	  (setq smv-options-changed nil)))) )

(defun smv-save-and-return ()
  "Saves the current buffer and returns back to the associated SMV
buffer.  The SMV buffer is either the value of `smv-current-buffer', or
it tries to make a few reasonable guesses. If no SMV buffer is found,
only saves the current buffer.

Normally is called from the *.ord buffer while editing variable ordering
for SMV specification. Bound to \\[smv-save-and-return]"
  (interactive)
  (let* ((buffer (current-buffer))
	 (buffer-file (buffer-file-name))
	 (smv-buffer-name
	  (let* ((match (string-match "\\.[^.]*$" (buffer-name))))
	    (if match
		(concat (substring (buffer-name) 0 match) ".smv")
	      (concat (buffer-name) ".smv"))))
	 (smv-buffer (get-buffer smv-buffer-name))
	 (smv-buffer
	  (cond (smv-buffer smv-buffer)
		((and (boundp 'smv-current-buffer)
		      (buffer-live-p smv-current-buffer))
		 smv-current-buffer)
		(t nil))))
    (save-buffer)
    (if smv-buffer
	(let ((smv-window (get-buffer-window smv-buffer)))
	  (setq smv-current-buffer smv-buffer)
	  (if smv-window 
	      (select-window smv-window)
	    (switch-to-buffer smv-buffer))
	  (if (get-buffer-window buffer) 
	      (delete-window (get-buffer-window buffer)))))) )

(defun smv-edit-options ()
  "Loads current options in a new buffer and lets the user edit it.
Run \\[eval-buffer] when done."
  (interactive)
  (let* ((buffer (current-buffer))
	 (opt-file-name 
	  (let ((match (string-match "\\.smv$"
				     (buffer-file-name))))
	    (if match
		(concat (substring (buffer-file-name)
				   0 match)
			".opt")
	      (concat (buffer-file-name) ".opt"))))
	 (opt-buffer-name 
	  (let ((match (string-match "\\.smv$"
				     (buffer-name))))
	    (if match
		(concat (substring (buffer-name)
				   0 match)
			".opt")
	      (concat (buffer-name) ".opt"))))
	 (opt-buffer (get-buffer-create opt-buffer-name))
	 (options (format ";;;; This file is generated automatically.\n;;;; Do C-c C-c when done to update and save the options.\n;;;; Good values for caches are: 16381, 32749, 65521, 262063, 522713, 1046429\n;;;; 2090867, 4186067, 8363639, 16777207\n(setq smv-cache-size %S)\n(setq smv-mini-cache-size %S)\n(setq smv-key-table-size %S)\n(setq smv-forward-search %S)\n(setq smv-report-option %S)\n(setq smv-order-file %S)\n(setq smv-verbose-level %S)\n(setq smv-heuristic-factor %S)\n(setq smv-command-line-args %S)\n"
             smv-cache-size
             smv-mini-cache-size
             smv-key-table-size
             smv-forward-search
             smv-report-option
             smv-order-file
             smv-verbose-level
             smv-heuristic-factor
             smv-command-line-args )))
    (setq smv-options-changed t)
    (switch-to-buffer-other-window opt-buffer)
    (set-visited-file-name opt-buffer-name)
    (erase-buffer)
    (insert options)
    (make-local-variable 'smv-currect-buffer)
    (setq smv-current-buffer buffer)
    (smv-options-edit-mode)))

(defun smv-interrupt ()
  "Kills current SMV process."
  (interactive)
  (quit-process (get-buffer-process smv-compile-buffer) t))

(defun smv-send-signal (sig)
  "Sends signal SIG to the SMV process. SIG must be an integer."
  (if (get-buffer-process smv-compile-buffer)
      (if (file-exists-p ".smv-pid")
	(save-excursion
	  (let ((buf (get-buffer-create ".smv-pid")))
	    (set-buffer buf)
	    (erase-buffer)
	    (insert-file-contents ".smv-pid")
	    (let ((pid (read buf)))
	      (if (integerp pid)
		  (signal-process pid sig)
		(error "The file .smv-pid is screwed up: %s" pid)))
	    (kill-buffer buf)))
	(error "Your SMV version does not support signal handling"))
    (error "SMV is not running")))

(defun smv-send-usr1 () 
  "Sends SIGUSR1 to the current SMV process. I have a version of SMV
that uses it to toggle dynamic variable ordering."
  (interactive)
  (smv-send-signal 10))

(defun smv-send-usr2 () 
  "Sends SIGUSR2 to the current SMV process. I have a version of SMV
that uses it to force garbage collection."
  (interactive)
  (smv-send-signal 12))

(defun smv-set-cache-size (arg)
  "Sets SMV cache size to use in command line option -c."
  (interactive (list (read-from-minibuffer "Set cache size to: "
			       smv-cache-size)))
  (if (stringp arg)
      (progn
	(setq smv-cache-size arg)
	(setq smv-options-changed t))
    (error "Not a string. The value is not set.")))
  
(defun smv-set-key-table-size (arg)
  "Sets SMV key table size to use in command line option -c."
  (interactive (list (read-from-minibuffer "Set key table size to: "
			       smv-key-table-size)))
  (if (stringp arg)
      (progn 
	(setq smv-key-table-size arg)
	(setq smv-options-changed t))
    (error "Not a string. The value is not set.")))
  
(defun smv-forward-search (&optional arg)
  "Toggles the use of -f option (forward search). 
With positive arg set to on."
  (interactive "P")
  (setq smv-options-changed t)
  (if arg
      (setq smv-forward-search t)
    (setq smv-forward-search (not smv-forward-search))) )
  
(defun smv-report-option (&optional arg)
  "Toggles the use of -r option (report statistics). 
With positive arg set to on."
  (interactive "P")
  (setq smv-options-changed t)
  (if arg
      (setq smv-report-option t)
    (setq smv-report-option (not smv-report-option))) )
  
(defun smv-set-order-file (arg)
  "Sets SMV variable ordering file to use in command line option -i.
If empty line is given, don't use any ordering file."
  (interactive (list (read-from-minibuffer "Set order file to: "
			       smv-order-file)))
  (if (stringp arg)
      (progn
	(if (string= arg "") (setq smv-order-file nil)
	  (setq smv-order-file arg))
	(setq smv-options-changed t))
    (error "Not a string. The value is not set.")))
  
(defun smv-set-verbose-level (arg)
  "Sets SMV verbose level to use in command line option -v.
If empty line is given, don't use any ordering file."
  (interactive (list (read-from-minibuffer "Set verbose level to: "
			       smv-verbose-level)))
  (if (stringp arg)
      (progn
	(if (string= arg "") (setq smv-verbose-level nil)
	  (setq smv-verbose-level arg))
	(setq smv-options-changed t))
    (error "Not a string. The value is not set.")))

(defun smv-set-heuristic-factor (arg)
  "Sets SMV heuristic factor to use in command line option -h.
If empty line is given, don't use this option."
  (interactive (list (read-from-minibuffer "Set heuristic factor to: "
			       smv-heuristic-factor)))
  (if (stringp arg)
      (progn
	(if (string= arg "") (setq smv-heuristic-factor nil)
	  (setq smv-heuristic-factor arg))
	(setq smv-options-changed t))
    (error "Not a string. The value is not set.")))

(defun smv-set-command-line-args (arg)
  "Sets SMV command line options. Don't set -f, -r, -v, -c, -k, -o, or -i
options here, use corresponding special variables for that.
If empty line is given, don't use any ordering file."
  (interactive (list (read-from-minibuffer "Other arguments: "
			       smv-command-line-args)))
  (if (stringp arg)
      (progn 
	(if (string= arg "") (setq smv-command-line-args nil)
	  (setq smv-command-line-args arg))
	(setq smv-options-changed t))
    (error "Not a string. The value is not set.")))

;;;; Saving file
(defun smv-save-buffer ()
  "Saves SMV file together with options. Prompts the user whether to
override the *.opt file if the options have changed."
  (interactive)
  (let ((opt-file-name 
	 (let ((match (string-match "\\.smv$"
				    (buffer-file-name))))
	   (if match
	       (concat (substring (buffer-file-name)
				  0 match)
		       ".opt")
	     (concat (buffer-file-name) ".opt")))))
    (cond ((and (file-exists-p opt-file-name)
		smv-options-changed)
	     (if (y-or-n-p "Options have changed. Save them?")
		 (progn
		   (smv-save-options)
		   (setq smv-options-changed nil))))
	  (smv-options-changed 
	     (smv-save-options)
	     (setq smv-options-changed nil))))
    (save-buffer))

;;;; Indentation

(defun smv-previous-line ()
  "Moves the point to the fisrt non-comment non-blank string before
the current one and positions the cursor on the first non-blank character."
  (interactive)
  (forward-line -1)
  (beginning-of-line)
  (skip-chars-forward " \t")
  (while (and (not (bobp)) (looking-at "$\\|--\\|#"))
    (forward-line -1)
    (beginning-of-line)
    (skip-chars-forward " \t")))

(defun smv-previous-indentation () 
  "Returns a pair (INDENT . TYPE). INDENT is the indentation of the
previous string, if there is one, and TYPE is 'openning, 'declaration
or 'plain, depending on whether previous string starts with an
openning, declarative keyword or neither. \"Previous string\" means
the last string before the current that is not an empty string or a
comment."
  (if (bobp) '(0 . 'plain)
    (save-excursion
      (smv-previous-line)
      (let ((type (cond ((or (looking-at smv-openning-keywords-regexp)
			     (looking-at smv-assignment-regexp)) 'openning)
			((looking-at smv-declaration-keywords-regexp)
			 'declaration)
			(t 'plain)))
	    (indent (current-indentation)))
	(cons indent type)))))

(defun smv-compute-indentation ()
  "Computes the indentation for the current string based on the
previous string. Current algorithm is too simple and needs
improvement."
  (save-excursion
   (beginning-of-line)
   (skip-chars-forward " \t")
   (cond ((looking-at smv-declaration-keywords-regexp) 0)
	 ((looking-at smv-assignment-regexp) 2)
	 (t (let* ((indent-data (smv-previous-indentation))
		   (indent (car indent-data))
		   (type (cdr indent-data)))
	      (setq indent
		    (cond ((looking-at smv-closing-keywords-regexp) 
			   (if (< indent 2) 0 (- indent 2)))
			  ((or (eq type 'openning) (eq type 'declaration))
			   (+ indent 2))
			  (t indent)))
	      indent)))))

(defun smv-indent-line ()
  "Indent the current line relative to the previous meaningful line."
  (interactive)
  (let* ((initial (point))
	 (final (let ((case-fold-search nil))(smv-compute-indentation)))
	 (offset0 (save-excursion
		    (beginning-of-line)
		    (skip-chars-forward " \t")
		    (- initial (point))))
	 (offset (if (< offset0 0) 0 offset0)))
    (indent-line-to final)
    (goto-char (+ (point) offset))))

;;;; Now define the keymap

(defconst smv-mode-map nil  "SMV keymap")

(if smv-mode-map ()
  (progn
    (setq smv-mode-map (make-sparse-keymap))
    (define-key smv-mode-map [delete] 'backward-delete-char-untabify)
    (define-key smv-mode-map [backspace] 'backward-delete-char-untabify)
    (define-key smv-mode-map "\C-x\C-s"  'smv-save-buffer)
    (define-key smv-mode-map "\C-c\C-e"  'smv-edit-options)
    (define-key smv-mode-map "\C-c\C-f"  'smv-run)
    (define-key smv-mode-map "\C-c\C-o"  'smv-edit-order-file)
    (define-key smv-mode-map "\C-c\C-c"  'smv-interrupt)
    (define-key smv-mode-map "\C-c\C-r"  'smv-send-usr1)
    (define-key smv-mode-map "\C-c\C-s"  'smv-send-usr2)
    (define-key smv-mode-map "\C-c;"  'comment-region)
    (define-key smv-mode-map "\t"  'smv-indent-line)))

(defun smv-mode ()
  "Major mode for SMV specification files. 

\\{smv-mode-map}

\\[smv-run] runs SMV on buffer, \\[smv-interrupt] interrupts already
running SMV process, \\[smv-edit-order-file] generates if necessary
and displays the variable ordering file.  \\[smv-edit-options]
displays SMV command line options in a separate buffer and allows the
user to edit them. Running \\[smv-save-and-load-options] (usually
bound to C-c C-c) in that buffer saves and updates new options, closes
the option edit buffer and brings you back to current SMV buffer.

\\[smv-send-usr1] and \\[smv-send-usr2] are used to send UNIX signals
to SMV process to toggle dynamic variable ordering and force garbage
collection respectively. Available only for a new (experimental) SMV
version.

The command line options are saved in *.opt file and are autoloaded
when you open the SMV file. If you change some of the options, you
will be asked about saving them when you save the main file (\\[smv-save-buffer]).

Running SMV (\\[smv-run]) creates a separate buffer were inferior SMV
process will leave its output. Currently, each run of SMV clears the
compilation buffer. If you need to save multiple runs, save them one
at a time. I'll probably implement the option of not erasing this
buffer some time later.

Please send bugs and suggestions to berez+@cs.cmu.edu."
  (interactive)
  (use-local-map smv-mode-map)
;;; Disable asking for the compile command
  (make-local-variable 'compilation-read-command)
  (setq compilation-read-command nil)
;;; Make all the variables with SMV options local to the current buffer
  (make-local-variable 'smv-command)
  (make-local-variable 'smv-cache-size)
  (make-local-variable 'smv-mini-cache-size)
  (make-local-variable 'smv-key-table-size)
  (make-local-variable 'smv-forward-search)
  (make-local-variable 'smv-report-option)
  (make-local-variable 'smv-order-file)
  (make-local-variable 'smv-verbose-level)
  (make-local-variable 'smv-command-line-args)
  (make-local-variable 'smv-options-changed)
  (setq smv-options-changed nil)
;;; Change the regexp search to be case sensitive
;;  (setq case-fold-search nil)
;;; Set syntax table
  (set-syntax-table smv-mode-syntax-table)
  (make-local-variable 'comment-start)
;; fix up comment handling
  (setq comment-start "--")
  (make-local-variable 'comment-end)
  (setq comment-end "")
  (make-local-variable 'comment-start-skip)
  (setq comment-start-skip "#+ *\\|--+ *")
  (setq require-final-newline t)
;;; Define the major mode
  (setq major-mode 'smv-mode)
  (setq mode-name "SMV")
;;; Load command line options for SMV process
  (let ((opt-file-name 
	 (let ((match (string-match "\\.smv$"
				    (buffer-file-name))))
	   (if match
	       (concat (substring (buffer-file-name)
				  0 match)
		       ".opt")
	     (concat (buffer-file-name) ".opt")))))
    (if (file-exists-p opt-file-name)
	(load opt-file-name)))
;;; Do fontification, if necessary
  (setq font-lock-keywords 
	(if font-lock-maximum-decoration
	    smv-font-lock-keywords-2
	  smv-font-lock-keywords-1))
  (if (and (not (string-match "XEmacs" emacs-version))
	   smv-font-lock-mode-on font-lock-global-modes window-system)
      (progn
	(font-lock-mode 1)
;;;	(if (string-match "XEmacs" emacs-version) () 
;;;	  (font-lock-fontify-buffer))
	))
  (setq mode-line-process nil) ; put 'smv-status when hooked up to inferior SMV
  (run-hooks 'smv-mode-hook))

(defun smv-ord-mode ()
  "Major mode for SMV variable ordering files. 
\\[smv-save-and-return] will save the buffer and get you back to
the accosiated SMV file."
  (interactive)
;;; It's basically the same as SMV mode, except that we don't
;;; do any compilation here.
  (make-local-variable 'smv-current-buffer)
  (use-local-map (make-sparse-keymap))
  (local-set-key "\C-c\C-c" 'smv-save-and-return)
;;; Set syntax table
  (set-syntax-table smv-mode-syntax-table)
  (make-local-variable 'comment-start)
;; fix up comment handling
  (setq comment-start "--")
  (make-local-variable 'comment-end)
  (setq comment-end "")
  (make-local-variable 'comment-start-skip)
  (setq comment-start-skip "#+ *\\|--+ *")
  (setq require-final-newline t)
;;; Define the major mode
  (setq major-mode 'smv-ord-mode)
  (setq mode-name "SMV Order")
;;; Do fontification, if necessary
  (setq font-lock-keywords 
	(if font-lock-maximum-decoration
	    smv-font-lock-keywords-2
	  smv-font-lock-keywords-1))
  (if (and (not (string-match "XEmacs" emacs-version))
	   smv-font-lock-mode-on font-lock-global-modes window-system)
      (font-lock-mode t))
  (setq mode-line-process nil))

(defun smv-options-edit-mode ()
  "Major mode for editing SMV options. Actually, this is Emacs Lisp
mode with a few changes. In particular, \\[smv-save-and-load-options] will save the file, 
find the associated SMV file and updates its options accordingly.  See
`\\[describe-bindings]' for key bindings.  "
  (interactive)
  (emacs-lisp-mode)
  (make-local-variable 'smv-current-buffer)
;;; Make all the variables with SMV options local to the current buffer
;;; to avoid accidental override of the global values
  (make-local-variable 'smv-command)
  (make-local-variable 'smv-cache-size)
  (make-local-variable 'smv-mini-cache-size)
  (make-local-variable 'smv-key-table-size)
  (make-local-variable 'smv-forward-search)
  (make-local-variable 'smv-report-option)
  (make-local-variable 'smv-order-file)
  (make-local-variable 'smv-verbose-level)
  (make-local-variable 'smv-command-line-args)
  (make-local-variable 'smv-options-changed)
  (setq major-mode 'smv-options-edit-mode)
  (setq mode-name "SMV Options")
;  (setq font-lock-keywords '(t ("^(\\(def\\(\\(const\\(\\|ant\\)\\|ine-key\\(\\|-after\\)\\|var\\)\\|\\(class\\|struct\\|type\\)\\|\\([^ 	
;()]+\\)\\)\\)\\>[ 	'(]*\\(\\sw+\\)?" (1 font-lock-keyword-face) (8 (cond ((match-beginning 3) font-lock-variable-name-face) ((match-beginning 6) font-lock-type-face) (t font-lock-function-name-face)) nil t))))
  (if (and (not (string-match "XEmacs" emacs-version))
	   smv-font-lock-mode-on  font-lock-global-modes window-system)
      (font-lock-mode t))
  (use-local-map (copy-keymap (current-local-map)))
  (local-set-key "\C-c\C-c" 'smv-save-and-load-options))

(provide 'smv-mode)
