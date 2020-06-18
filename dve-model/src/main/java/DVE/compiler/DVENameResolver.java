package DVE.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import DVE.compiler.builder.DVEBuilder;

import DVE.model.Assignment;
import DVE.model.ChannelDeclaration;
import DVE.model.Element;
import DVE.model.Expression;
import DVE.model.NamedDeclaration;
import DVE.model.Process;
import DVE.model.Reference;
import DVE.model.State;
import DVE.model.StateReference;
import DVE.model.System;
import DVE.model.Transition;
import DVE.model.VariableDeclaration;
import DVE.model.util.ModelSwitch;

public class DVENameResolver {
	DVEBuilder builder = new DVEBuilder();
	Map<String, NamedDeclaration> globalNames = new HashMap<String, NamedDeclaration>();
	Map<Process, Map<String, NamedDeclaration>> processNames = new HashMap<Process, Map<String,NamedDeclaration>>();
	Stack<NamedDeclaration> context = new Stack<NamedDeclaration>();
	
	Set<Reference> unresolvedSet = new HashSet<Reference>();
	boolean failed = false;
	
	ModelSwitch<Boolean> modelSwitch = new ModelSwitch<Boolean>() {
		
		public Boolean caseSystem(DVE.model.System object) {
			context.push(object);
			for (NamedDeclaration nD : object.getDeclarations()) {
				doSwitch(nD);
			}
			context.pop();
			
			for (Reference ref : unresolvedSet) {
				if (!doSwitch(ref)) {
					java.lang.System.err.println("Cannot resolve " + ref.getRefName());
					failed = true;
				}
			}
			
			return true;
		}
		
		Process processContext() {
			if (context.size() < 1) return null;
			if (context.peek() instanceof Process) return (Process)context.peek();
			return null;
		}
		
		public Boolean caseVariableDeclaration(DVE.model.VariableDeclaration object) {
			Process ctx = processContext();
			if (ctx == null) {
				globalNames.put(object.getName(), object);
				if (object.getInitial() != null) doSwitch(object.getInitial());
				return true;
			}
			processNames.get(ctx).put(object.getName(), object);
			if (object.getInitial() != null) doSwitch(object.getInitial());
			return true;
		}
		
		public Boolean caseConstantDeclaration(DVE.model.ConstantDeclaration object) {
			Process ctx = processContext();
			if (ctx == null) {
				globalNames.put(object.getName(), object);
				if (object.getInitial() != null) doSwitch(object.getInitial());
				return true;
			}
			processNames.get(ctx).put(object.getName(), object);
			if (object.getInitial() != null) doSwitch(object.getInitial());
			return true;
		}
		
		public Boolean caseChannelDeclaration(DVE.model.ChannelDeclaration object) {
			globalNames.put(object.getName(), object);
			return true;
		}
		
		public Boolean caseProcess(DVE.model.Process object) {
			globalNames.put(object.getName(), object);
			processNames.put(object, new HashMap<String, NamedDeclaration>());
			context.push(object);
			for (State state : object.getStates()) {
				doSwitch(state);
			}
			
			for (NamedDeclaration nDecl : object.getDeclarations()) {
				doSwitch(nDecl);
			}
			
			if (object.getInitial() != null) {
				doSwitch(object.getInitial());
			}
			else {
				java.lang.System.err.println("Process " + object.getName() + " does not have an initial state\n");
			}
			
			
			for (StateReference sR : object.getCommited()) {
				doSwitch(sR);
			}
			for (StateReference sR : object.getAccepting()) {
				doSwitch(sR);
			}
			for (Transition t : object.getTransitions()) {
				doSwitch(t);
			}
			context.pop();
			return true;
		}
		
		public Boolean caseTransition(Transition object) {
			if (object.getFrom() != null) doSwitch(object.getFrom());
			doSwitch(object.getTo());
			if (object.getGuard() != null) doSwitch(object.getGuard());
			if (object.getSync() != null) doSwitch(object.getSync());
			for (Assignment a : object.getEffect()) {
				doSwitch(a);
			}
			return true;
		}
		
		public Boolean caseSynchronization(DVE.model.Synchronization object) {
			doSwitch(object.getChannel());
			return true;
		}
		
		public Boolean caseInputSynchronization(DVE.model.InputSynchronization object) {
			doSwitch(object.getChannel());
			if (object.getValue() != null) doSwitch(object.getValue());
			return true;
		}
		public Boolean caseOutputSynchronization(DVE.model.OutputSynchronization object) {
			doSwitch(object.getChannel());
			if (object.getValue() != null) doSwitch(object.getValue());
			return true;
		}
		
		public Boolean caseAssignment(Assignment object) {
			doSwitch(object.getLhs());
			doSwitch(object.getRhs());
			return true;
		}
		
		public Boolean caseArrayLiteral(DVE.model.ArrayLiteral object) {
			for (Expression exp : object.getValues()) {
				doSwitch(exp);
			}
			return true;
		}
		
		public Boolean caseBinaryExpression(DVE.model.BinaryExpression object) {
			for (Expression exp : object.getOperands()) {
				doSwitch(exp);
			}
			return true;
		}
		
		public Boolean caseUnaryExpression(DVE.model.UnaryExpression object) {
			doSwitch(object.getOperand());
			return true;
		}
		
		public Boolean caseIndexedExpression(DVE.model.IndexedExpression object) {
			doSwitch(object.getBase());
			doSwitch(object.getIndex());
			return true;
		}
		
		public Boolean caseSystemProperties(DVE.model.SystemProperties object) {
			doSwitch(object.getProperty());
			return true;
		}
		
		public Boolean caseState(State object) {
			Process ctx = processContext();
			processNames.get(ctx).put(object.getName(), object);
			return true;
		}
		
		boolean checkIfError(Element obj, String name) {
			if (obj == null) {
				java.lang.System.err.println("undeclared identifier '" + name + "'");
				failed = true;
				return true;
			}
			return false;
		}
		
		public Boolean caseStateReference(StateReference object) {
			Map<String, NamedDeclaration> env = processNames.get(processContext());
			State obj = (State) env.get(object.getRefName());
			if (checkIfError(obj, object.getRefName())) return false;
			object.setRef(obj);
			return true;
		}
		
		public Boolean caseProcessReference(DVE.model.ProcessReference object) {
			Process p = (Process)globalNames.get(object.getRefName());
			if (checkIfError(p, object.getRefName())) return false;
			object.setRef(p);
			return true;
		}
		
		public Boolean caseVariableReference(DVE.model.VariableReference object) {
			Process ctx = processContext();
			if (ctx != null) {
				//local variable
				VariableDeclaration vDecl = (VariableDeclaration) processNames.get(ctx).get(object.getRefName());
				if (vDecl != null) {
					object.setRef(vDecl);
					return true;
				}
			}
			//either there is no local variable with this name -> global reference
			//or we are in a global context
			VariableDeclaration vDecl = (VariableDeclaration) globalNames.get(object.getRefName());
			if (checkIfError(vDecl, object.getRefName())) return false;
			object.setRef(vDecl);
			return true;
		}
		
		public Boolean caseChannelReference(DVE.model.ChannelReference object) {
			ChannelDeclaration cD = (ChannelDeclaration) globalNames.get(object.getRefName());
			if (checkIfError(cD, object.getRefName())) return false;
			object.setRef(cD);
			return true;
		}
		
		public Boolean caseProcessStateReference(DVE.model.ProcessStateReference object) {
			Process prefix = (Process) globalNames.get(object.getPrefix().getRefName());
			
			if (prefix == null) {
				unresolvedSet.add(object);
				return false;
			}
			object.getPrefix().setRef(prefix);
			
			State state = (State) processNames.get(prefix).get(object.getRefName());
			if (checkIfError(state, object.getRefName())) return false;
			object.setRef(state);
			return true;
		}
		
		public Boolean caseProcessVariableReference(DVE.model.ProcessVariableReference object) {
			Process prefix = (Process) globalNames.get(object.getPrefix().getRefName());
			
			if (prefix == null) {
				unresolvedSet.add(object);
				return false;
			}
			object.getPrefix().setRef(prefix);
			
			VariableDeclaration var = (VariableDeclaration) processNames.get(prefix).get(object.getRefName());
			if (checkIfError(var, object.getRefName())) return false;
			object.setRef(var);
			return true;
		}
	};
	public void apply(System sys) throws Exception {
		modelSwitch.doSwitch(sys);
		if (failed) throw new Exception();
	}
}
