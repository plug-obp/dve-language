package DVE.extractions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import DVE.evaluation.StaticEvaluator;
import org.eclipse.emf.ecore.util.EcoreUtil;

import DVE.model.util.ModelSwitch;
import DVE.model.Assignment;
import DVE.model.ChannelDeclaration;
import DVE.model.ConstantDeclaration;
import DVE.model.Expression;
import DVE.model.NamedDeclaration;
import DVE.model.NumberLiteral;
import DVE.model.Process;
import DVE.model.System;
import DVE.model.Transition;
import DVE.model.TypedChannelDeclaration;
import DVE.model.VariableDeclaration;

public class DVEGlobalUsageExtractor {
	Map<Process, Set<VariableDeclaration>> usedVariableMap = new HashMap<Process, Set<VariableDeclaration>>();
	Map<Process, Set<VariableDeclaration>> modifiedVariableMap = new HashMap<Process, Set<VariableDeclaration>>();
	Map<Process, Set<ChannelDeclaration>> inputChannelMap = new HashMap<Process, Set<ChannelDeclaration>>();
	Map<Process, Set<ChannelDeclaration>> outputChannelMap = new HashMap<Process, Set<ChannelDeclaration>>();
	Map<Process, Set<ChannelDeclaration>> inputQueueMap = new HashMap<Process, Set<ChannelDeclaration>>();
	Map<Process, Set<ChannelDeclaration>> outputQueueMap = new HashMap<Process, Set<ChannelDeclaration>>();
	
	Stack<Boolean> isRValueStack = new Stack<Boolean>();
	
	Process currentProcess = null;
	
	ModelSwitch<Boolean> modelSwitch = new ModelSwitch<Boolean>() {
		public Boolean caseSystem(DVE.model.System object) {
			for (Process p : object.getProcesses()) {
				doSwitch(p);
			}
			return true;
		}
		
		public Boolean caseProcess(Process object) {
			currentProcess = object;
			usedVariableMap.put(currentProcess, new HashSet<VariableDeclaration>());
			modifiedVariableMap.put(currentProcess, new HashSet<VariableDeclaration>());
			inputChannelMap.put(currentProcess, new HashSet<ChannelDeclaration>());
			outputChannelMap.put(currentProcess, new HashSet<ChannelDeclaration>());
			inputQueueMap.put(currentProcess, new HashSet<ChannelDeclaration>());
			outputQueueMap.put(currentProcess, new HashSet<ChannelDeclaration>());
			for (NamedDeclaration nD : object.getDeclarations()) {
				doSwitch(nD);
			}
			
			for (Transition t : object.getTransitions()) {
				doSwitch(t);
			}
			return true;
		}
		
		public Boolean caseVariableDeclaration(VariableDeclaration object) {
			if (object.getInitial() != null) {
				isRValueStack.push(true);
				doSwitch(object.getInitial());
				isRValueStack.pop();
			}
			return true;
		}
		
		public Boolean caseTransition(Transition object) {
			if (object.getGuard() != null) {
				isRValueStack.push(true);
				doSwitch(object.getGuard());
				isRValueStack.pop();
			}
			if (object.getSync() != null) doSwitch(object.getSync());
			for (Assignment assign : object.getEffect()) {
				doSwitch(assign);
			}
			return true;
		}
		public Boolean caseInputSynchronization(DVE.model.InputSynchronization object) {
			ChannelDeclaration chanD = object.getChannel().getRef();
			if (chanD instanceof TypedChannelDeclaration) {
				TypedChannelDeclaration chan = (TypedChannelDeclaration) chanD;
				int bufferSize = 0;
				if (chan.getBufferSize() != null) {
					bufferSize = ((NumberLiteral)StaticEvaluator.evaluate(chan.getBufferSize())).getValue().intValue();
				}
				if (bufferSize > 0) {
					inputQueueMap.get(currentProcess).add(chanD);
				}
				else {
					inputChannelMap.get(currentProcess).add(chanD);
				}
			}
			else {
				inputChannelMap.get(currentProcess).add(chanD);
			}
			//here the value should be an l-value that is modified
			if (object.getValue() != null) {
				isRValueStack.push(false);
				doSwitch(object.getValue());
				isRValueStack.pop();
			}
			return true;
		}
		public Boolean caseOutputSynchronization(DVE.model.OutputSynchronization object) {
			ChannelDeclaration chanD = object.getChannel().getRef();
			if (chanD instanceof TypedChannelDeclaration) {
				TypedChannelDeclaration chan = (TypedChannelDeclaration) chanD;
				int bufferSize = 0;
				if (chan.getBufferSize() != null) {
					bufferSize = ((NumberLiteral)StaticEvaluator.evaluate(chan.getBufferSize())).getValue().intValue();
				}
				if (bufferSize > 0) {
					outputQueueMap.get(currentProcess).add(chanD);
				}
				else {
					outputChannelMap.get(currentProcess).add(chanD);
				}
			}
			else {
				outputChannelMap.get(currentProcess).add(chanD);
			}
			if (object.getValue() != null) {
				isRValueStack.push(true);
				doSwitch(object.getValue());
				isRValueStack.pop();
			}
			return true;
		}
		public Boolean caseVariableReference(DVE.model.VariableReference object) {
			//check if it a ref to a constant
			if (object.getRef() instanceof ConstantDeclaration) return true;
			//check if it is a local reference 
			for (NamedDeclaration vD : currentProcess.getDeclarations()) {
				if (EcoreUtil.equals(object.getRef(), vD)) return true;
			}
			if (isRValueStack.peek()) {
				usedVariableMap.get(currentProcess).add(object.getRef());
			} else {
				modifiedVariableMap.get(currentProcess).add(object.getRef());
			}
			return true;
		}
		
		public Boolean caseBinaryExpression(DVE.model.BinaryExpression object) {
			doSwitch(object.getOperands().get(0));
			doSwitch(object.getOperands().get(1));
			return true;
		}
		public Boolean caseUnaryExpression(DVE.model.UnaryExpression object) {
			doSwitch(object.getOperand());
			return true;
		}
		public Boolean caseArrayLiteral(DVE.model.ArrayLiteral object) {
			for (Expression e : object.getValues()) {
				doSwitch(e);
			}
			return true;
		}
		public Boolean caseAssignment(Assignment object) {
			//lhs needs to be an l-value, and it is modified
			isRValueStack.push(false);
			doSwitch(object.getLhs());
			isRValueStack.pop();
			isRValueStack.push(true);
			doSwitch(object.getRhs());
			isRValueStack.pop();
			return true;
		}
		public Boolean caseIndexedExpression(DVE.model.IndexedExpression object) {
			doSwitch(object.getBase());
			isRValueStack.push(true);
			doSwitch(object.getIndex());
			isRValueStack.pop();
			return true;
		}
	};
	
	public void extract(System sys) {
		modelSwitch.doSwitch(sys);
		return;
	}
}
