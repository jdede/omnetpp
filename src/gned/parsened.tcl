#==========================================================================
#  PARSENED.TCL -
#            part of the GNED, the Tcl/Tk graphical topology editor of
#                            OMNeT++
#
#   By Andras Varga
#
#==========================================================================

#----------------------------------------------------------------#
#  Copyright (C) 1992,99 Andras Varga
#  Technical University of Budapest, Dept. of Telecommunications,
#  Stoczek u.2, H-1111 Budapest, Hungary.
#
#  This file is distributed WITHOUT ANY WARRANTY. See the file
#  `license' for details on this and other legal matters.
#----------------------------------------------------------------#


#------------------------------------------------
# Data structure is defined in datadict.tcl
#------------------------------------------------


# NedParser_createNedElement --
#
# This procedure is called from parsened.cc, NEDParser::create()
#
proc NedParser_createNedElement {nedarrayname type parentkey} {
   global ned_attr ned_attlist
   upvar #0 $nedarrayname nedarray

   # choose key
   set key $nedarray(nextkey)
   incr nedarray(nextkey)

   # add ned() fields
   foreach field $ned_attlist(common) {
      set nedarray($key,$field) $ned_attr(common,$field)
   }
   foreach field $ned_attlist($type) {
      set nedarray($key,$field) $ned_attr($type,$field)
   }
   set nedarray($key,type) $type

   # set parent
   set nedarray($key,parentkey) $parentkey
   lappend nedarray($parentkey,childrenkeys) $key

   return $key
}

# NedParser_findChild --
#
# Find a child element within the given parent and with a given attribute value.
# (attr is usually "name".)
# This procedure is called from parsened.cc, NEDParser::create()
#
proc NedParser_findChild {nedarrayname parentkey attr value} {
   upvar #0 $nedarrayname nedarray

   set key ""
   foreach key1 $nedarray($parentkey,childrenkeys) {
       if {[info exist nedarray($key1,$attr)] && $nedarray($key1,$attr)==$value} {
          if {$key==""} {
             set key $key1
          } else {
             return "not unique"
          }
       }
   }
   return $key
}


# split_dispstr --
#
# Split up display string into an array.
#    dispstr: display string
#    array:   dest array name
#    returns: original order of tags (as a list)
#
# Example:
#   if "p=50,99;i=cloud" is parsed into array 'a', the result is:
#      $a(p) = {50 99}
#      $a(i) = {cloud}
#
proc split_dispstr {dispstr array} {
   upvar $array arr

   set tags {}
   foreach tag [split $dispstr {;}] {
      set tag [split $tag {=}]
      set key [lindex $tag 0]
      set val [split [lindex $tag 1] {,}]

      lappend tags $key
      if {$key != ""} {
         set arr($key) $val
      }
   }
   return $tags
}


# assemble_dispstr --
#
# Assemble and return a display string from a form produced by split_dispstr
#    array:   src array name
#    order:   preferred order of tags (as a list)
#    returns: display string
#
proc assemble_dispstr {array order} {
   upvar $array arr

   set dispstr ""
   # loop through all tags in their preferred order
   foreach tag [lsort -command {dispstr_ordertags $order} [array names array]] {
       set vals $array($tag)
       # discard empty elements at end of list
       while {[lindex $vals end]==""} {
           set vals [lreplace $vals end end]
       }
       append dispstr "$tag="
       append dispstr [join $vals ","]
   }
   return $dispstr
}

# private proc for assemble_dispstr
proc dispstr_ordertags {order t1 t2} {
   return [lsearch -exact $order $t1] - [lsearch -exact $order $t2]
}


# parse_module_dispstr --
#
# update a 'module' ned element with values from its display string
#
proc parse_module_dispstr {key dispstr) {
   global ned

   split_dispstr $dispstr tags

   # GNED currently only handles only few values from a dispstr...
   if [info exist tags(p)] {
      set ned($key,x-pos) [lindex $tags(p) 0]
      set ned($key,y-pos) [lindex $tags(p) 1]
   }
   if [info exist tags(b)] {
      set ned($key,x-size) [lindex $tags(b) 0]
      set ned($key,y-size) [lindex $tags(b) 1]
   }
   if [info exist tags(o)] {
      set ned($key,fill-color) [lindex $tags(o) 0]
      set ned($key,outline-color) [lindex $tags(o) 1]
      set ned($key,linethickness) [lindex $tags(o) 2]
   }
}


# parse_submod_dispstr --
#
# update a 'submod' ned element with values from its display string
#
proc parse_submod_dispstr {key dispstr) {
   global ned

   split_dispstr $dispstr tags

   # GNED currently only handles only few values from a dispstr...
   if [info exist tags(p)] {
      set ned($key,x-pos) [lindex $tags(p) 0]
      set ned($key,y-pos) [lindex $tags(p) 1]
   }
   if [info exist tags(b)] {
      set ned($key,x-size) [lindex $tags(b) 0]
      set ned($key,y-size) [lindex $tags(b) 1]
   }
   if [info exist tags(i)] {
      set ned($key,icon) [lindex $tags(i) 0]
   }
   if [info exist tags(o)] {
      set ned($key,fill-color) [lindex $tags(o) 0]
      set ned($key,outline-color) [lindex $tags(o) 1]
      set ned($key,linethickness) [lindex $tags(o) 2]
   }
}

# parse_conn_dispstr --
#
# update a 'conn' ned element with values from its display string
#
proc parse_conn_dispstr {key dispstr) {
   global ned

   split_dispstr $dispstr tags

   # GNED currently only handles only few values from a dispstr...
   if [info exist tags(m)] {
      set ned($key,drawmode) [lindex $tags(m) 0]
      set ned($key,an_src_x) [lindex $tags(p) 1]
      set ned($key,an_src_y) [lindex $tags(p) 2]
      set ned($key,an_dest_x) [lindex $tags(p) 3]
      set ned($key,an_dest_y) [lindex $tags(p) 4]
   }

   if [info exist tags(o)] {
      set ned($key,fill-color) [lindex $tags(o) 0]
      set ned($key,linethickness) [lindex $tags(o) 1]
   }
}


