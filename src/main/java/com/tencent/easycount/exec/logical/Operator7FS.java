package com.tencent.easycount.exec.logical;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.plan.logical.OpDesc7FS;
import com.tencent.easycount.util.status.StatusPrintable;

public class Operator7FS extends Operator<OpDesc7FS> {
	// private static Logger log = LoggerFactory.getLogger(Operator7FS.class);

	private Finalized finalizer;
	private boolean containsKey = false;
	private boolean containsAttrs = false;
	private transient ObjectInspector keyInspector = null;
	private transient ObjectInspector attrsInspector = null;

	// @SuppressWarnings("rawtypes")
	// private transient ExprNodeEvaluator keyEvaluator = null;
	// private transient ObjectInspector keyInspector = null;
	//
	// @SuppressWarnings("rawtypes")
	// private transient ExprNodeEvaluator attrsEvaluator = null;
	// private transient ObjectInspector attrsInspector = null;

	@Override
	public void printInternal(int printId) {
	}

	public Operator7FS(OpDesc7FS opDesc, ECConfiguration hconf,
			TaskContext taskContext) {
		super(opDesc, hconf, taskContext);
	}

	@Override
	protected void initializeOp(ECConfiguration hconf, TaskContext taskContext) {
		// try {
		// ExprNodeDesc keyExprDesc = getOpDesc().getKeyExpr();
		// if (keyExprDesc != null) {
		// keyEvaluator = ExprNodeEvaluatorFactoryNew.get(keyExprDesc);
		// keyInspector = (PrimitiveObjectInspector) keyEvaluator
		// .initialize(inputObjInspectors[0]);
		// }
		// ExprNodeDesc attrsExprDesc = getOpDesc().getAttrsExpr();
		// if (attrsExprDesc != null) {
		// attrsEvaluator = ExprNodeEvaluatorFactoryNew.get(attrsExprDesc);
		// attrsInspector = attrsEvaluator
		// .initialize(inputObjInspectors[0]);
		// }
		// } catch (HiveException e) {
		// log.error(TDBankUtils.getExceptionStack(e));
		// }
		this.containsKey = getOpDesc().containsKeyExpr();
		this.containsAttrs = getOpDesc().containsAttrsExpr();
		int extendCols = 0;
		if (containsKey) {
			extendCols++;
		}
		if (containsAttrs) {
			extendCols++;
		}
		if (extendCols > 0) {
			StructObjectInspector inputOI = (StructObjectInspector) inputObjInspectors[0];
			List<? extends StructField> fields = inputOI
					.getAllStructFieldRefs();
			ArrayList<String> names = new ArrayList<String>();
			ArrayList<ObjectInspector> ois = new ArrayList<ObjectInspector>();
			for (int i = 0; i < fields.size() - extendCols; i++) {
				StructField field = fields.get(i);
				names.add(field.getFieldName());
				ois.add(field.getFieldObjectInspector());
			}
			this.outputObjInspector = ObjectInspectorFactory
					.getStandardStructObjectInspector(names, ois);
			if (containsKey) {
				this.keyInspector = fields.get(names.size())
						.getFieldObjectInspector();
				if (containsAttrs) {
					this.attrsInspector = fields.get(names.size() + 1)
							.getFieldObjectInspector();

				}
			} else if (containsAttrs) {
				this.attrsInspector = fields.get(names.size())
						.getFieldObjectInspector();
			}
		}
		initializeChildren(hconf, taskContext);
	}

	@Override
	public void processOp(Object row, int tag) {
		this.finalizer.finalize(row, outputObjInspector, keyInspector,
				attrsInspector, getOpDesc().getOpTagIdx());
	}

	public Finalized getFinalizer() {
		return finalizer;
	}

	public void setFinalizer(Finalized finalizer) {
		this.finalizer = finalizer;
	}

	@Override
	protected void closeOp(boolean abort) {
		try {
			this.finalizer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static interface Finalized extends Closeable, StatusPrintable {
		public boolean finalize(Object row, ObjectInspector objectInspector,
				ObjectInspector keyInspector, ObjectInspector attrsInspector,
				int opTagIdx);
	}

}
