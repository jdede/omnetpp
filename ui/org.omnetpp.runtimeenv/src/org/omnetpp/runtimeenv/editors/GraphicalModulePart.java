package org.omnetpp.runtimeenv.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.omnetpp.experimental.simkernel.swig.cDisplayString;
import org.omnetpp.experimental.simkernel.swig.cModule;
import org.omnetpp.experimental.simkernel.swig.cModule_SubmoduleIterator;
import org.omnetpp.experimental.simkernel.swig.cSimulation;
import org.omnetpp.figures.CompoundModuleFigure;
import org.omnetpp.figures.layout.SubmoduleConstraint;
import org.omnetpp.runtimeenv.Activator;
import org.omnetpp.runtimeenv.ISimulationListener;
import org.omnetpp.runtimeenv.figures.SubmoduleFigureEx;

/**
 * 
 * @author Andras
 */
public class GraphicalModulePart {
    //may add cSimulation and SimulationManager as well
    protected CompoundModuleFigure moduleFigure;
    protected int moduleID;
    protected Map<Integer,SubmoduleFigureEx> submodules = new HashMap<Integer,SubmoduleFigureEx>();
    protected Map<Integer,String> lastSubmoduleDisplayStrings = new HashMap<Integer,String>();
    protected ISimulationListener simulationListener;
    protected MouseListener mouseListener;
    
    /**
     * Constructor.
     * @param parentFigure typically the root figure on the canvas
     * @param moduleID id of the compound module to be displayed
     */
    public GraphicalModulePart(IFigure parentFigure, int moduleID) {
        this.moduleID = moduleID;
        cModule module = cSimulation.getActiveSimulation().getModule(moduleID);

        moduleFigure = new CompoundModuleFigure();
        parentFigure.add(moduleFigure);
        parentFigure.setConstraint(moduleFigure, new Rectangle(0,0,500,500));  //XXX

        moduleFigure.setDisplayString(module.getDisplayString());
        
//            //TODO just testing
//            ConnectionFigure connectionFigure = new ConnectionFigure();
//            connectionFigure.setArrowHeadEnabled(true);
//            connectionFigure.setSourceAnchor(new GateAnchor(submoduleFigure));
//            connectionFigure.setTargetAnchor(new CompoundModuleGateAnchor(moduleFigure));
//            moduleFigure.getConnectionLayer().add(connectionFigure);

        update();

        moduleFigure.addMouseListener(mouseListener = new MouseListener() {
            @Override
            public void mouseDoubleClicked(MouseEvent me) {
                handleMouseDoubleClick(me);
            }
            @Override
            public void mousePressed(MouseEvent me) {
                handleMousePressed(me);
            }
            @Override
            public void mouseReleased(MouseEvent me) {
                handleMouseReleased(me);
            }
        });
        
        Activator.getSimulationManager().addChangeListener(simulationListener = new ISimulationListener() {
            @Override
            public void changed() {
                update();
            }
        });
    }

    public void dispose() {
        Activator.getSimulationManager().removeChangeListener(simulationListener);
    }
    
    protected void update() {
        refreshChildren();
        refreshVisuals();
        //XXX System.out.println(cSimulation.getActiveSimulation().getSystemModule().getSubmodule("server").getDisplayString().str());
    }

    protected void refreshChildren() {
        //TODO only call this function if there were any moduleCreated/moduleDeleted notifications from the simkernel
        cSimulation sim = cSimulation.getActiveSimulation();
        ArrayList<Integer> toBeRemoved = null;
        ArrayList<Integer> toBeAdded = null;
        
        // find submodule figures whose module has been deleted
        for (int id : submodules.keySet()) {
            if (sim.getModule(id) == null) { //FIXME create & use moduleExists(id) for efficiency
                if (toBeRemoved == null)
                    toBeRemoved = new ArrayList<Integer>();
                toBeRemoved.add(id);
            }
        }

        // find submodules that not yet have a figure
        for (cModule_SubmoduleIterator it = new cModule_SubmoduleIterator(sim.getModule(moduleID)); !it.end(); it.next()) {
            int id = it.get().getId();  //FIXME performance: add getModuleId() to the iterator directly
            if (!submodules.containsKey(id)) {
                if (toBeAdded == null)
                    toBeAdded = new ArrayList<Integer>();
                toBeAdded.add(id);
            }
        }

        // do the removals and additions
        if (toBeRemoved != null) {
            for (int id : toBeRemoved) {
                moduleFigure.getSubmoduleLayer().remove(submodules.get(id));
                submodules.remove(id);
                lastSubmoduleDisplayStrings.remove(id);
            }
        }
        if (toBeAdded != null) {
            for (int id : toBeAdded) {
                // create figure
                SubmoduleFigureEx submoduleFigure = new SubmoduleFigureEx();
                submoduleFigure.setModuleID(id);
                submoduleFigure.setPinDecoration(false);
                submoduleFigure.setName(sim.getModule(id).getFullName());
                moduleFigure.getSubmoduleLayer().add(submoduleFigure);
                submodules.put(id, submoduleFigure);
                lastSubmoduleDisplayStrings.put(id, null);
            }
        }
    }

    protected void refreshVisuals() {
        cSimulation sim = cSimulation.getActiveSimulation();
        for (int id : submodules.keySet()) {
            cDisplayString displayString = sim.getModule(id).getDisplayString();
            String displayStringText = displayString.toString();
            if (!displayStringText.equals(lastSubmoduleDisplayStrings.get(id))) {
                SubmoduleFigureEx submoduleFigure = submodules.get(id);
                submoduleFigure.setDisplayString(displayString);

                // layouting magic
                SubmoduleConstraint constraint = new SubmoduleConstraint();
                constraint.setLocation(submoduleFigure.getPreferredLocation());
                constraint.setSize(submoduleFigure.getPreferredSize());
                Assert.isTrue(constraint.height != -1 && constraint.width != -1);
                moduleFigure.getSubmoduleLayer().setConstraint(submoduleFigure, constraint);
                
                lastSubmoduleDisplayStrings.put(id, displayStringText);
            }
        }
    }

    protected void handleMousePressed(MouseEvent me) {
        System.out.println(findSubmoduleAt(me.x,me.y));
    }

    protected void handleMouseDoubleClick(MouseEvent me) {
        // TODO Auto-generated method stub
    }
    
    protected void handleMouseReleased(MouseEvent me) {
        // TODO Auto-generated method stub
    }
    
    public SubmoduleFigureEx findSubmoduleAt(int x, int y) {
        IFigure target = moduleFigure.findFigureAt(x, y);
        while (target != null && !(target instanceof SubmoduleFigureEx))
            target = target.getParent();
        return (SubmoduleFigureEx)target;
    }
}
