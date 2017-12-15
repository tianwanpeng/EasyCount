package com.tencent.easycount.exec.logical;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils.ReturnObjectInspectorResolver;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.plan.logical.OpDesc4UNION;
import com.tencent.easycount.util.status.TDBankUtils;

public class Operator4UNION extends Operator<OpDesc4UNION> {
	private static Logger log = LoggerFactory.getLogger(Operator4UNION.class);

	StructObjectInspector[] parentObjInspectors;
	List<? extends StructField>[] parentFields;

	ReturnObjectInspectorResolver[] columnTypeResolvers;
	boolean[] needsTransform;

	@Override
	public void printInternal(int printId) {
	}

	public Operator4UNION(OpDesc4UNION opDesc, ECConfiguration hconf,
			TaskContext taskContext) {
		super(opDesc, hconf, taskContext);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initializeOp(ECConfiguration hconf, TaskContext taskContext) {

		int parents = parentOperators.size();
		parentObjInspectors = new StructObjectInspector[parents];
		parentFields = new List[parents];
		for (int p = 0; p < parents; p++) {
			parentObjInspectors[p] = (StructObjectInspector) inputObjInspectors[p];
			parentFields[p] = parentObjInspectors[p].getAllStructFieldRefs();
		}

		// Get columnNames from the first parent
		int columns = parentFields[0].size();
		ArrayList<String> columnNames = new ArrayList<String>(columns);
		for (int c = 0; c < columns; c++) {
			columnNames.add(parentFields[0].get(c).getFieldName());
		}

		// Get outputFieldOIs
		columnTypeResolvers = new ReturnObjectInspectorResolver[columns];
		for (int c = 0; c < columns; c++) {
			columnTypeResolvers[c] = new ReturnObjectInspectorResolver(true);
		}

		for (int p = 0; p < parents; p++) {
			assert (parentFields[p].size() == columns);
			for (int c = 0; c < columns; c++) {
				try {
					columnTypeResolvers[c].update(parentFields[p].get(c)
							.getFieldObjectInspector());
				} catch (UDFArgumentTypeException e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}
		}

		ArrayList<ObjectInspector> outputFieldOIs = new ArrayList<ObjectInspector>(
				columns);
		for (int c = 0; c < columns; c++) {
			outputFieldOIs.add(columnTypeResolvers[c].get());
		}

		// create output row ObjectInspector
		outputObjInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(columnNames, outputFieldOIs);

		// whether we need to do transformation for each parent
		needsTransform = new boolean[parents];
		for (int p = 0; p < parents; p++) {
			// Testing using != is good enough, because we use
			// ObjectInspectorFactory
			// to
			// create ObjectInspectors.
			needsTransform[p] = (inputObjInspectors[p] != outputObjInspector);
			if (needsTransform[p]) {
				// LOG.info("Union Operator needs to transform row from parent["
				// + p + "] from " + inputObjInspectors[p] + " to "
				// + outputObjInspector);
			}
		}
		initializeChildren(hconf, taskContext);
	}

	@Override
	public void processOp(Object row, int tag) {

		StructObjectInspector soi = parentObjInspectors[tag];
		List<? extends StructField> fields = parentFields[tag];
		if (needsTransform[tag]) {
			ArrayList<Object> outputRow = new ArrayList<Object>(fields.size());
			for (int c = 0; c < fields.size(); c++) {
				outputRow.add(columnTypeResolvers[c].convertIfNecessary(
						soi.getStructFieldData(row, fields.get(c)),
						fields.get(c).getFieldObjectInspector()));
			}
			forward(outputRow);
		} else {
			forward(row);
		}
	}
}
