//=========================================================================
//
//  ERRMSG.CC - part of
//                          OMNeT++
//           Discrete System Simulation in C++
//
//   Contents:
//    emsg[]:  error message table
//
//  Author: Andras Varga
//
//=========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 1992,99 Andras Varga
  Technical University of Budapest, Dept. of Telecommunications,
  Stoczek u.2, H-1111 Budapest, Hungary.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/


//
// emsg[]:
//   error messages table
//
//   Blank slots are message codes no longer in use.
//

char *emsg[] = {
  "No error -- everything's fine",                       // eOK
  "(%s)%s: Cannot cast from type `%c' to `%c'",          // eBADCAST
  "(%s)%s: Indirection would create circular reference", // eCIRCREF
  "(%s)%s: Bad number of arguments, %d expected",        // eNUMARGS
  "",                                                    // eBADTYPE
  "",                                                    // eBANNEDTYPE
  "Simulation stopped by segment on host `%s'",          // eSTOPSIMRCVD
  "No such module or module finished already",           // eNOMOD
  "Module has no gate %d",                               // eNOGATE
  "send(): Gate `%s' is an input gate",                  // eINGATE
  "No module parameter called `%s'",                     // eNOPARAM
  "Not enough memory on heap",                           // eNOMEM
  "Incausality during simulation",                       // eINCAUSAL
  "Message sent to already terminated module `%s'",      // eMODFIN
  "Transfer to nonexistent, finished or compound module",// eBADTRANSF
  "(%s)%s: setValue(): Type `%c' does not suit arg types", // eBADINIT
  "Something unexpected happened (internal error)",      // eUNEXP
  "Cannot use receive..() with handleMessage()",         // eNORECV
  "",                                                    // eSTKLOW
  "No more events -- simulation ended",                  // eENDEDOK
  "Module initialization error",                         // eMODINI
  "",                                                    // eNULLPTR
  "(%s)%s: Object #%d not found",                        // eNULLREF
  "Cannot write output vector file",                     // eOUTVECT
  "Cannot write parameter change file",                  // ePARCHF
  "wait(): negative delay",                              // eNEGTIME
  "",                                                    // eMATH
  "Floating point exception",                            // eFPERR
  "receive()/receiveNew(): negative timeout",            // eNEGTOUT
  "Message cannot be delivered",                         // eNODEL
  "Simulation cancelled",                                // eCANCEL
  "Network setup failed",                                // eSETUP
  "(%s)%s: Badly formed Reverse Polish expression",      // eBADEXP
  "All finish() functions called, simulation ended",     // eFINISH
  "Simulation stopped with endSimulation()",             // eENDSIM
  "CPU time limit reached -- simulation stopped",        // eREALTIME
  "Simulation time limit reached -- simulation stopped", // eSIMTIME
  "This object cannot DUP itself",                       // eCANTDUP
  "FSM: infinite loop of transient states (now in state %s)", // eINFLOOP
  "FSM: state changed during state entry code (now in state %s)", // eSTATECHG
  "",                                                    // eADDPAR
  "Badly connected gate",                                // eBADGATE
  "Gate not connected to anything",                      // eNOTCONN
  "",                                                    // eBADKEY
  "Simple module definition not found",                  // eNOMODDEF
  "Channel definition not found",                        // eNOCHANDEF
  "Network not found",                                   // eNOSUCHNET
  "PVM: function call error in %s",                      // ePVM
  "Bad command line argument",                           // eBADARGS
  "Cannot schedule a message to the past",               // eBACKSCHED
  "receiveOn(): gate %d is output gate",                 // eOUTGATE
  "Channel error out of range [0..1]",                   // NL eCHERROR
  "Delay time less than zero",                           // NL eCHDELAY
  "Array index out of boundaries",                       // NL eARRAY
  "",  /* this msg is never printed */                   // eCUSTOM
  "Not enough memory on heap for module %s",             // eNOMEM2
  "User error: %s",                                      // eUSER
  ""
};
