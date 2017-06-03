package com.tencent.easycount.plan;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.ExprNodeColumnEvaluator;
import org.apache.hadoop.hive.ql.exec.ExprNodeConstantEvaluator;
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluatorHead;
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluatorRef;
import org.apache.hadoop.hive.ql.exec.ExprNodeFieldEvaluator;
import org.apache.hadoop.hive.ql.exec.ExprNodeGenericFuncEvaluator;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeConstantDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeFieldDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.util.status.TDBankUtils;

public class ExprNodeEvaluatorFactoryNew {
	private static Logger log = LoggerFactory
			.getLogger(ExprNodeEvaluatorFactoryNew.class);

	private ExprNodeEvaluatorFactoryNew() {
	}

	@SuppressWarnings("rawtypes")
	public static ExprNodeEvaluator get(final ExprNodeDesc desc) {
		// Constant node
		if (desc instanceof ExprNodeConstantDesc) {
			return new ExprNodeConstantEvaluator((ExprNodeConstantDesc) desc);
		}
		// Column-reference node, e.g. a column in the input row
		if (desc instanceof ExprNodeColumnDesc) {
			return new ExprNodeColumnEvaluator((ExprNodeColumnDesc) desc);
		}
		// Generic Function node, e.g. CASE, an operator or a UDF node
		if (desc instanceof ExprNodeGenericFuncDesc) {

			// ExprNodeGenericFuncDescWrapper fdesc = new
			// ExprNodeGenericFuncDescWrapper(
			// desc.getTypeInfo(), ((ExprNodeGenericFuncDesc) desc)
			// .getGenericUDF().getClass(), desc.getChildren());
			// return new ExprNodeGenericFuncEvaluatorNew(fdesc);
			try {
				return new ExprNodeGenericFuncEvaluator(
						(ExprNodeGenericFuncDesc) desc);
			} catch (final Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
		// Generic Function node, e.g. CASE, an operator or a UDF node
		if (desc instanceof ExprNodeNewGenericFuncDesc) {
			try {
				return new ExprNodeEVGenericFuncEvaluator(
						(ExprNodeNewGenericFuncDesc) desc);
			} catch (final Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
				return null;
			}
		}
		// Field node, e.g. get a.myfield1 from a
		if (desc instanceof ExprNodeFieldDesc) {
			try {
				return new ExprNodeFieldEvaluator((ExprNodeFieldDesc) desc);
			} catch (final HiveException e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
		// Null node, a constant node with value NULL and no type information
		// TODO
		// if (desc instanceof ExprNodeNullDesc) {
		// return new ExprNodeNullEvaluator((ExprNodeNullDesc) desc);
		// }

		if (desc instanceof ExprNodeNewColumnRefDesc) {
			try {
				return new ExprNodeColumnEvaluator(
						((ExprNodeNewColumnRefDesc) desc).getColumnDesc());
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		if (desc instanceof ExprNodeNewForeachDesc) {
			try {
				return new ExprNodeEVForeachEvaluator(
						(ExprNodeNewForeachDesc) desc);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		if (desc instanceof ExprNodeNewExecuteDesc) {
			try {
				return new ExprNodeEVExecuteEvaluator(
						(ExprNodeNewExecuteDesc) desc);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		throw new RuntimeException(
				"Cannot find ExprNodeEvaluator for the exprNodeDesc = " + desc);
	}

	/**
	 * Should be called before eval is initialized
	 */
	@SuppressWarnings("rawtypes")
	public static ExprNodeEvaluator toCachedEval(final ExprNodeEvaluator eval) {
		if (eval instanceof ExprNodeGenericFuncEvaluator) {
			final EvaluatorContext context = new EvaluatorContext();
			iterate(eval, context);
			if (context.hasReference) {
				return new ExprNodeEvaluatorHead(eval);
			}
		}
		// has nothing to be cached
		return eval;
	}

	@SuppressWarnings("rawtypes")
	private static ExprNodeEvaluator iterate(final ExprNodeEvaluator eval,
			final EvaluatorContext context) {
		if (!(eval instanceof ExprNodeConstantEvaluator)
				&& eval.isDeterministic()) {
			final ExprNodeEvaluator replace = context.getEvaluated(eval);
			if (replace != null) {
				return replace;
			}
		}
		final ExprNodeEvaluator[] children = eval.getChildren();
		if ((children != null) && (children.length > 0)) {
			for (int i = 0; i < children.length; i++) {
				final ExprNodeEvaluator replace = iterate(children[i], context);
				if (replace != null) {
					children[i] = replace;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static class EvaluatorContext {

		private final Map<String, ExprNodeEvaluator> cached = new HashMap<String, ExprNodeEvaluator>();

		private boolean hasReference;

		public ExprNodeEvaluator getEvaluated(final ExprNodeEvaluator eval) {
			final String key = eval.getExpr().getExprString();
			final ExprNodeEvaluator prev = this.cached.get(key);
			if (prev == null) {
				this.cached.put(key, eval);
				return null;
			}
			this.hasReference = true;
			return new ExprNodeEvaluatorRef(prev);
		}
	}

}
