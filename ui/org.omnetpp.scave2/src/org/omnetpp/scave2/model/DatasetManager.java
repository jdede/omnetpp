package org.omnetpp.scave2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.omnetpp.scave.engine.IDList;
import org.omnetpp.scave.engine.Node;
import org.omnetpp.scave.engine.ResultFileManager;
import org.omnetpp.scave.engine.ResultItem;
import org.omnetpp.scave.engine.Run;
import org.omnetpp.scave.engine.ScalarResult;
import org.omnetpp.scave.engine.XYArray;
import org.omnetpp.scave.model.Add;
import org.omnetpp.scave.model.AddDiscardOp;
import org.omnetpp.scave.model.Apply;
import org.omnetpp.scave.model.Chart;
import org.omnetpp.scave.model.Compute;
import org.omnetpp.scave.model.Dataset;
import org.omnetpp.scave.model.DatasetItem;
import org.omnetpp.scave.model.DatasetType;
import org.omnetpp.scave.model.Deselect;
import org.omnetpp.scave.model.Discard;
import org.omnetpp.scave.model.Except;
import org.omnetpp.scave.model.Group;
import org.omnetpp.scave.model.ScaveModelFactory;
import org.omnetpp.scave.model.Select;
import org.omnetpp.scave.model.SelectDeselectOp;
import org.omnetpp.scave.model.SetOperation;
import org.omnetpp.scave.model.util.ScaveModelSwitch;
import org.omnetpp.scave2.charting.OutputVectorDataset;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

/**
 * This class calculates the content of a dataset
 * applying the operations described by the dataset. 
 */
public class DatasetManager {
	
	public static IDList getIDListFromDataset(ResultFileManager manager, Dataset dataset, DatasetItem lastProcessedItem) {
		ProcessDatasetSwitch processor = new ProcessDatasetSwitch(manager, lastProcessedItem);
		processor.doSwitch(dataset);
		return processor.getIDList();
	}
	
	static class ProcessDatasetSwitch extends ScaveModelSwitch {
		
		private ResultFileManager manager;
		private DatasetType type;
		private IDList idlist;
		private EObject target;
		private boolean finished;
		
		public ProcessDatasetSwitch(ResultFileManager manager, EObject target) {
			this.manager = manager;
			this.target = target;
			this.idlist = new IDList();
		}
		
		public IDList getIDList() {
			return idlist != null ? idlist : new IDList();
		}

		@Override
		protected Object doSwitch(int classifierID, EObject object) {
			Object result = this;
			if (!finished) {
				result = super.doSwitch(classifierID, object);
				if (object == target)
					finished = true;
			}
			return result;
		}

		public Object caseDataset(Dataset dataset) {
			type = dataset.getType();
			if (dataset.getBasedOn() != null)
				idlist = getIDListFromDataset(manager, dataset.getBasedOn(), null);
			
			for (Object item : dataset.getItems())
				doSwitch((EObject)item);
			
			return this;
		}

		public Object caseAdd(Add add) {
			idlist.merge(select(null, add));
			return this;
		}
		
		public Object caseDiscard(Discard discard) {
			idlist.substract(select(idlist, discard));
			return this;
		}

		public Object caseGroup(Group group) {
			if (EcoreUtil.isAncestor(group, target))
				for (Object item : group.getItems())
					doSwitch((EObject)item);
			return this;
		}

		public Object caseApply(Apply apply) {
			// TODO
			return this;
		}
		
		public Object caseCompute(Compute compute) {
			// TODO
			return this;
		}
		
		public Object caseChart(Chart chart) {
			if (chart == target)
				idlist = select(idlist, chart.getFilters());
			return this;
		}
		
		private IDList select(IDList source, List<SelectDeselectOp> filters) {
			// if no select, then interpret it as "select all"
			if (filters.size() == 0 || filters.get(0) instanceof Deselect) {
				Select selectAll = ScaveModelFactory.eINSTANCE.createSelect(); 
				filters = new ArrayList<SelectDeselectOp>(filters);
				filters.add(0, selectAll);
			}
			
			return DatasetManager.select(source, filters, manager, type);
		}
		
		private IDList select(IDList source, AddDiscardOp op) {
			return DatasetManager.select(source, op, manager, type);
		}
	}
	
	public static XYArray[] getDataFromDataset(ResultFileManager manager, Dataset dataset, DatasetItem lastProcessedItem) {
		Assert.isLegal(dataset.getType() == DatasetType.VECTOR_LITERAL, "Vector dataset expected.");
		
		DataflowNetworkBuilder builder = new DataflowNetworkBuilder(manager);
		builder.build(dataset, lastProcessedItem);
		builder.close();

		List<Node> outputs = builder.getOutputs();

		builder.getDataflowManager().dump();
		
		if (outputs.size() > 0) // XXX DataflowManager craches when there are no sinks
			builder.getDataflowManager().execute();

		XYArray[] result = new XYArray[outputs.size()]; 
		for (int i = 0; i < result.length; ++i)
			result[i] = outputs.get(i).getArray();
		return result;
	}
	
	public static CategoryDataset createScalarDataset(Chart chart, Dataset dataset, ResultFileManager manager) {
		IDList idlist = DatasetManager.getIDListFromDataset(manager, dataset, chart);
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		for (int i = 0; i < idlist.size(); ++i) {
			ScalarResult scalar = manager.getScalar(idlist.get(i));
			ds.addValue(scalar.getValue(),
					scalar.getFileRun().getRun().getRunName(),
					scalar.getModuleName()+"\n"+scalar.getName());
		}
		return ds;
	}
	
	public static OutputVectorDataset createVectorDataset(Chart chart, Dataset dataset, ResultFileManager manager) {
		XYArray[] dataValues = getDataFromDataset(manager, dataset, chart);
		IDList idlist = getIDListFromDataset(manager, dataset, chart);
		String[] dataNames = getResultItemIDs(idlist, manager);
		return new OutputVectorDataset(dataNames, dataValues);
	}
	
	public static String[] getResultItemNames(IDList idlist, ResultFileManager manager) {
		return getResultItemIDs(idlist, manager);
	}

	/**
	 * Returns the ids of data items in the <code>idlist</code>.
	 * The id is formed from the file name, run number, run id, module name,
	 * data name, experiment, measurement and replication.
	 * Constant fields will be omitted from the id.
	 */
	public static String[] getResultItemIDs(IDList idlist, ResultFileManager manager) {
		String[][] nameFragments = new String[(int)idlist.size()][];
		for (int i = 0; i < idlist.size(); ++i) {
			ResultItem item = manager.getItem(idlist.get(i));
			Run run = item.getFileRun().getRun();
			nameFragments[i] = new String[8];
			nameFragments[i][0] = item.getFileRun().getFile().getFilePath();
			nameFragments[i][1] = String.valueOf(run.getRunNumber());
			nameFragments[i][2] = String.valueOf(run.getRunName());
			nameFragments[i][3] = item.getModuleName();
			nameFragments[i][4] = item.getName();
			nameFragments[i][5] = run.getAttribute(RunAttribute.EXPERIMENT);
			nameFragments[i][6] = run.getAttribute(RunAttribute.REPLICATION);
			nameFragments[i][7] = run.getAttribute(RunAttribute.MEASUREMENT);
		}

		boolean[] same = new boolean[8];
		Arrays.fill(same, true);
		for (int i = 1; i < nameFragments.length; ++i) {
			for (int j = 0; j < 8; ++j)
				if (same[j] && !nameFragments[0][j].equals(nameFragments[i][j]))
					same[j] = false;
		}
		
		String[] result = new String[nameFragments.length];
		for (int i = 0; i < result.length; ++i) {
			StringBuffer id = new StringBuffer(30);
			for (int j = 0; j < 8; ++j)
				if (!same[j])
					id.append(nameFragments[i][j]).append(" ");
			if (id.length() == 0)
				id.append(i);
			else
				id.deleteCharAt(id.length() - 1);
			result[i] = id.toString();
		}
		return result;
	}
	
	public static IDList select(IDList source, List<SelectDeselectOp> filters, ResultFileManager manager, DatasetType type) {
		IDList result = new IDList();
		for (SelectDeselectOp filter : filters) {
			if (filter instanceof Select) {
				result.merge(select(source, (Select)filter, manager, type));
			}
			else if (filter instanceof Deselect) {
				result.substract(select(source, (Deselect)filter, manager, type));
			}
		}
		return result;
	}
	
	public static IDList select(IDList source, SetOperation op, ResultFileManager manager, DatasetType type) {
		IDList idlist = selectInternal(source, (SetOperation)op, manager, type);
		
		List<Except> excepts = null;
		if (op instanceof Select)
			excepts = ((Select)op).getExcepts();
		else if (op instanceof Deselect)
			excepts = ((Deselect)op).getExcepts();
		else if (op instanceof Add)
			excepts = ((Add)op).getExcepts();
		else if (op instanceof Discard)
			excepts = ((Discard)op).getExcepts();
		
		if (excepts != null)
			for (Except except : excepts)
				idlist.substract(selectInternal(idlist, except, manager, type));
		
		return idlist;
	}

	private static IDList selectInternal(IDList source, SetOperation op, ResultFileManager manager, DatasetType type) {
		Dataset sourceDataset = op.getSourceDataset();
		IDList sourceIDList = source != null ? source :
							  sourceDataset == null ? ScaveModelUtil.getAllIDs(manager, type) :
							  DatasetManager.getIDListFromDataset(manager, sourceDataset, null);
								
		return ScaveModelUtil.filterIDList(sourceIDList, new FilterParams(op), manager);
	}
}
