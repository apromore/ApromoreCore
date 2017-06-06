; (qgen L W)
; Generates a .bmc queue program with queues with L slots and wide W
; 

(defun qgen (l w) 
 (format "%s%s%s%s" 
  (vardecl l w)
  (init l w)
  (trans l w)
  (spec l w)) nil) 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; dirty hack
(defun log2 (n)
 (if (<= n 1) 0
 (if (<= n 2) 1
 (if (<= n 4) 2
 (if (<= n 8) 3
 (if (<= n 16) 4
 (if (<= n 32) 5
 (if (<= n 64) 6
 (if (<= n 128) 7
 (if (<= n 256) 8
 (if (<= n 512) 9
 (if (<= n 1024) 10
 (if (<= n 2048) 11
  (+ 1 (log2 (/ n 2))))))))))))))))


(defun vardecl (l w)
  (format "\nVAR\n\n" nil)
  (vardecl11 "vin" l)
  (vardecl1 "a" 0 l w)
  (vardecl11 "atop" (log2 l))
  (vardecl11 "avout" w)
  (vardecl1 "c" 0 l w)
  (vardecl11 "chead" (log2 l))
  (vardecl11 "ctail" (log2 l))
  (vardecl11 "cvout" w)
)

; declares all the vars prefix_i[w] with 0 <= i < w
;
(defun vardecl1 (prefix i l w)
 (if (>= i l) (format "\n" nil)
  (concat
   (vardecl11 prefix i w)
   (vardecl1 prefix (+ 1 i) l w))))

(defun vardecl11 (prefix l w)
  (format " %s%s[%s]\n" prefix l w nil))
 

(defun afull (l w)
  (format "DEFINE\n afull = (aempty | !(atop=~s))" (- l 1) nil))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defun init (l w)
 (format "INIT\n\n(\n" nil)
 (format "aempty & atop=0 & avout=0 & cempty & chead = 0 & ctail=0 & cvout=0\n" nil)
 (setallvals "a" 0 l 0) 
 (setallvals "c" 0 l 0) 
 (format " & \n" nil)
 (addinvars l w)
 (format ")\n" nil))

(defun setallvals (prefix i l v)
 (if (>= i l) (format "" nil)
  (concat
   (format "& %s%s = %s " prefix i v nil)
   (setallvals prefix (+ i 1) l v))))


(defun addinvars ( l w )
 (format "(!(R&W))
&
-- either we write or we read
((R | W))
&
-- precondition for writing
-- atop=7 means queue full
( W -> !afull )
&
-- precondition for reading
( R -> !aempty )" nil))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defun trans (l w)
 (concat 
  (format "\n\nTRANS\n (\n" nil)
  (addinvars l w)
  (addwrite l w)
  (format "\n & \n" nil)
  (addread l w)
  (format "\n)\n" nil)))


(defun addwrite(l w)
 (concat
  (format "  ( W -> (\n" nil)
  (addwritearray w l)
  (format " & \n" nil)
  (addwriteclist w l)
  (format " ) " nil)))


(defun addwritearray (w l)
  (format "
   -- update of the array version
   (
    ((atop=0) -> (a0'=vin&a1'=a1 &a2'=a2 &a3'=a3 &a4'=a4 &a5'=a5 &a6'=a6 &a7'=a7 ))
    &
    ((atop=1) -> (a0'=a0 &a1'=vin&a2'=a2 &a3'=a3 &a4'=a4 &a5'=a5 &a6'=a6 &a7'=a7 ))
    &
    ((atop=2) -> (a0'=a0 &a1'=a1 &a2'=vin&a3'=a3 &a4'=a4 &a5'=a5 &a6'=a6 &a7'=a7 ))
    &
    ((atop=3) -> (a0'=a0 &a1'=a1 &a2'=a2 &a3'=vin&a4'=a4 &a5'=a5 &a6'=a6 &a7'=a7 ))
    &
    ((atop=4) -> (a0'=a0 &a1'=a1 &a2'=a2 &a3'=a3 &a4'=vin&a5'=a5 &a6'=a6 &a7'=a7 ))
    &
    ((atop=5) -> (a0'=a0 &a1'=a1 &a2'=a2 &a3'=a3 &a4'=a4 &a5'=vin&a6'=a6 &a7'=a7 ))
    &
    ((atop=6) -> (a0'=a0 &a1'=a1 &a2'=a2 &a3'=a3 &a4'=a4 &a5'=a5 &a6'=vin&a7'=a7 ))
    &
    ((atop=7) -> (a0'=a0 &a1'=a1 &a2'=a2 &a3'=a3 &a4'=a4 &a5'=a5 &a6'=a6 &a7'=vin))
   &
   !aempty'
   &
   (atop'=inc(atop))
   &
   avout'=avout
   &
  )

"))



(defun addwriteclist (l w)

 (format "
   -- update of the linked list queue
   (
    ((ctail=0) -> (c0'=vin&c1'=c1 &c2'=c2 &c3'=c3 &c4'=c4 &c5'=c5 &c6'=c6 &c7'=c7 ))
    &
    ((ctail=1) -> (c0'=c0 &c1'=vin&c2'=c2 &c3'=c3 &c4'=c4 &c5'=c5 &c6'=c6 &c7'=c7 ))
    &
    ((ctail=2) -> (c0'=c0 &c1'=c1 &c2'=vin&c3'=c3 &c4'=c4 &c5'=c5 &c6'=c6 &c7'=c7 ))
    &
    ((ctail=3) -> (c0'=c0 &c1'=c1 &c2'=c2 &c3'=vin&c4'=c4 &c5'=c5 &c6'=c6 &c7'=c7 ))
    &
    ((ctail=4) -> (c0'=c0 &c1'=c1 &c2'=c2 &c3'=c3 &c4'=vin&c5'=c5 &c6'=c6 &c7'=c7 ))
    &
    ((ctail=5) -> (c0'=c0 &c1'=c1 &c2'=c2 &c3'=c3 &c4'=c4 &c5'=vin&c6'=c6 &c7'=c7 ))
    &
    ((ctail=6) -> (c0'=c0 &c1'=c1 &c2'=c2 &c3'=c3 &c4'=c4 &c5'=c5 &c6'=vin&c7'=c7 ))
    &
    ((ctail=7) -> (c0'=c0 &c1'=c1 &c2'=c2 &c3'=c3 &c4'=c4 &c5'=c5 &c6'=c6 &c7'=vin))
   &
   (ctail'=inc(ctail))
   &
   chead'=chead
   &
   !cempty'
   &
   cvout'=cvout
   )" nil))


(defun addread (l w)
 (concat
  (format "\n  ( R -> (\n" nil)
  (addreadarray l w)
  (format "\n & \n" nil)
  (addwritearray l w)
  (format "\n))\n")))


(defun addreadarray (l w)
 (format "
   -- read from the array version
   (
    ( avout'=a0 & a0'=a1 & a1'=a2 & a2'=a3 & a3'=a4 & a4'=a5 & a5'=a6 & a6'=a7 & a7'=0 )
    &
    (
     ( (atop=1) -> atop'=0     &aempty')
     &
     (!(atop=1) -> atop'=dec(atop)&aempty'=aempty)
    )
   )" nil))


(defun addreadclist (l w)
 (format "
   -- read from the linked list queue
   (
    ((chead=0) -> (c0'=0 &c1'=c1&c2'=c2&c3'=c3&c4'=c4&c5'=c5&c6'=c6&c7'=c7&cvout'=c0))
    &									            
    ((chead=1) -> (c0'=c0&c1'=0 &c2'=c2&c3'=c3&c4'=c4&c5'=c5&c6'=c6&c7'=c7&cvout'=c1))
    &									            
    ((chead=2) -> (c0'=c0&c1'=c1&c2'=0 &c3'=c3&c4'=c4&c5'=c5&c6'=c6&c7'=c7&cvout'=c2))
    &									            
    ((chead=3) -> (c0'=c0&c1'=c1&c2'=c2&c3'=0 &c4'=c4&c5'=c5&c6'=c6&c7'=c7&cvout'=c3))
    &										    
    ((chead=4) -> (c0'=c0&c1'=c1&c2'=c2&c3'=c3&c4'=0 &c5'=c5&c6'=c6&c7'=c7&cvout'=c4))
    &									            
    ((chead=5) -> (c0'=c0&c1'=c1&c2'=c2&c3'=c3&c4'=c4&c5'=0 &c6'=c6&c7'=c7&cvout'=c5))
    &									            
    ((chead=6) -> (c0'=c0&c1'=c1&c2'=c2&c3'=c3&c4'=c4&c5'=c5&c6'=0 &c7'=c7&cvout'=c6))
    &									            
    ((chead=7) -> (c0'=c0&c1'=c1&c2'=c2&c3'=c3&c4'=c4&c5'=c5&c6'=c6&c7'=0 &cvout'=c7))
   &
   ((chead'=inc(chead))&ctail'=ctail)
   &
   (
    ( (ctail=inc(chead)&!cempty ) -> ( cempty'))
    &
    (!(ctail=inc(chead)&!cempty ) -> ( cempty'=cempty ))
   )
  )" nil))


(defun addspec ()
  (format "
SPEC

 AG ( avout=cvout )
" nil))

