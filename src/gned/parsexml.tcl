#==========================================================================
#  PARSEXML.TCL -
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


#
# This file is *very* experimental at the moment...
#

catch {package require xml}

# loadNED --
#
#
proc loadXML {xmlfile} {
    global ned ned_attr
    global tmp_ned tmp_errors

    busy "Loading $xmlfile..."

    # parse NED file and fill the tmp_ned() array
    catch {unset tmp_ned}
    catch {unset tmp_errors}

    set tmp_ned(nextkey) $ned(nextkey)

    # temporarily add a root element
    set rootkey $tmp_ned(nextkey)
    incr tmp_ned(nextkey)
    set tmp_ned($rootkey,childrenkeys) {}

    # parsing...
    if [catch {set num_errs [doParseXML $xmlfile $rootkey]} errmsg] {
        tk_messageBox -icon error -title "Error" -type ok -message "Error loading $xmlfile:\n$errmsg"
        catch {unset tmp_ned}
        catch {unset tmp_errors}
        busy
        return
    }

    # handle parse errors
    if {$num_errs!="0"} {
        # simplified handling: display only the first error
        set errmsg "$tmp_errors(0,type): $tmp_errors(0,text) in line $tmp_errors(0,line)"

        tk_messageBox -icon error -title "Error(s)" -type ok \
            -message "Error(s) loading $xmlfile:\n$errmsg"

        catch {unset tmp_ned}
        catch {unset tmp_errors}
        busy
        return
    }

    # get key of nedfile, and delete tempoary root element
    set filekey [lindex $tmp_ned($rootkey,childrenkeys) 0]
    unset tmp_ned($rootkey,childrenkeys)

    # debug code:
    # set showkeys [lsort [array names tmp_ned "*"]]
    # foreach i $showkeys {
    #     puts "DBG: tmp_ned($i)=\"$tmp_ned($i)\""
    # }

    # collect modules from code for further display
    set modulekeys {}
    foreach key $tmp_ned($filekey,childrenkeys) {
        if {$tmp_ned($key,type)=="module"} {
            lappend modulekeys $key
        }
    }

    # add tmp_ned() contents to ned()
    foreach i [array names tmp_ned] {
        set ned($i) $tmp_ned($i)
    }
    set ned(nextkey) $tmp_ned(nextkey)
    unset tmp_ned

    # insert under "root" (item 0)
    insertItem $filekey 0

    # debug code
    #puts "dbg: checkArray says:"
    #checkArray

    # update manager
    updateTreeManager

    # open modules on canvases
    foreach key $modulekeys {
        openModuleOnCanvas $key
    }

    # remove hourglass cursor
    busy
}


proc doParseXML {xmlfile rootkey} {
    global tmp_ned tmp_errors tmp_idmap

    puts "TBD: should use tmp_errors()..."
    puts "TBD: display strings..."

    # handling of file errors left to the caller
    set fin [open $xmlfile r]
    set xml [read $fin]
    close $fin

    set p [xml::parser x -elementstartcommand sax_elementstart \
                         -elementendcommand sax_elementend \
                         -errorcommand sax_error \
                         -warningcommand sax_warning ]

    global stack
    set stack $rootkey

    catch {unset tmp_idmap}
    $p parse $xml
    catch {unset tmp_idmap}

    # TBD: return number of errors
    return 0
}


proc sax_elementstart {tag attlist} {
    global ned_desc ned_attr ned_attlist
    global tmp_ned tmp_errors tmp_idmap
    global stack

    # puts "DBG: elementstart $tag ($attlist)"

    # verify type
    if ![info exist ned_desc($tag,parents)] {
        error "invalid tag name '$tag'"
    }
    set type $tag

    # create NED element
    set parentkey [lindex $stack end]
    set key [NedParser_createNedElement tmp_ned $type $parentkey]

    # verify & load attributes
    foreach {att val} $attlist {
        regsub -all "%0d%0a" $val "\n" val
        regsub -all "%22" $val "\"" val
        if {$att=="id"} {
            # fill XML-id to tmp_ned-key map
            set tmp_idmap($val) $key
        } elseif {$att=="display"} {
            # spec handling
        } elseif {$att=="src-ownerkey" || $att=="dest-ownerkey"} {
            # these attributes refer to previous submod elements
            if [info exist tmp_idmap($val)] {
                set tmp_ned($key,$att) $tmp_idmap($val)
            } else {
                error "invalid idref '$val' in entity '$tag'"
            }
        } elseif [info exist ned_attr($type,$att)] {
            # other attributes
            set tmp_ned($key,$att) $val
        } else {
            error "invalid attr name '$att' in entity '$tag'"
        }
    }
    lappend stack $key
    return 0
}

proc sax_elementend {tag} {
    global stack
    # puts "DBG: elementend $tag"
    # remove last element from stack
    set stack [lreplace $stack end end]
    return 0
}

proc sax_error args {
    error "parse error: $args"
}

proc sax_warning args {
    error "parse warning: $args"
}



