package org.cte.DVE.fiacre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import obp.fiacre.model.ArgumentVariable;
import obp.fiacre.model.BinOp;
import obp.fiacre.model.CaseStmt;
import obp.fiacre.model.ComponentDecl;
import obp.fiacre.model.ConstantDecl;
import obp.fiacre.model.Exp;
import obp.fiacre.model.Field;
import obp.fiacre.model.Instance;
import obp.fiacre.model.LocalPortDecl;
import obp.fiacre.model.LocalVariable;
import obp.fiacre.model.NullStmt;
import obp.fiacre.model.Par;
import obp.fiacre.model.ParamPortDecl;
import obp.fiacre.model.Pattern;
import obp.fiacre.model.PortDecl;
import obp.fiacre.model.ProcessDecl;
import obp.fiacre.model.Profile;
import obp.fiacre.model.Program;
import obp.fiacre.model.RefArg;
import obp.fiacre.model.Seq;
import obp.fiacre.model.Statement;
import obp.fiacre.model.To;
import obp.fiacre.model.Type;
import obp.fiacre.model.Variable;
import obp.fiacre.util.FiacreBuilder;

import org.cte.DVE.evaluation.StaticEvaluator;

import DVE.model.Assignment;
import DVE.model.ChannelDeclaration;
import DVE.model.ConstantDeclaration;
import DVE.model.Element;
import DVE.model.Expression;
import DVE.model.NamedDeclaration;
import DVE.model.NumberLiteral;
import DVE.model.Process;
import DVE.model.State;
import DVE.model.StateReference;
import DVE.model.System;
import DVE.model.Transition;
import DVE.model.TypedChannelDeclaration;
import DVE.model.VariableDeclaration;
import DVE.model.util.ModelSwitch;

public class DVE2Fiacre {
	ModelSwitch<obp.fiacre.model.Element> modelSwitch = new ModelSwitch<obp.fiacre.model.Element>() {
		Map<Element, obp.fiacre.model.Element> visited = new HashMap<Element, obp.fiacre.model.Element>();
		Stack<obp.fiacre.model.Element> fcrContext = new Stack<obp.fiacre.model.Element>();
		obp.fiacre.util.FiacreBuilder fcrBuilder = new FiacreBuilder();
		obp.fiacre.model.TypeDecl byteType;
		DVEGlobalUsageExtractor globalsUsageExtractor;
		
		Stack<Boolean> rhsContextStack = new Stack<Boolean>();
		
		@Override
		public obp.fiacre.model.Element caseSystem(DVE.model.System object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return o;
			
			doSwitch(object.getProperties());
			
			new DVE2FiacreNames().fixNames(object);
			
			globalsUsageExtractor = new DVEGlobalUsageExtractor();
			globalsUsageExtractor.extract(object);
			
			Program fcrP = new Program();
			fcrContext.push(fcrP);
			
			obp.fiacre.model.Interval intv = fcrBuilder.interval(fcrBuilder.literal(0), fcrBuilder.literal(255));
			byteType = new obp.fiacre.model.TypeDecl();
			byteType.setIs(intv);
			byteType.setName("byte");
			
			fcrP.addDeclaration(byteType);
			
			ComponentDecl compDecl = new ComponentDecl();
			compDecl.setName("sys");
			fcrP.setRoot(compDecl);
			Par par = new Par();
			compDecl.setBody(par);
			
			List<Process> processes = new ArrayList<Process>();
			Map<String, LocalPortDecl> channels2ports = new HashMap<String, LocalPortDecl>();
			Map<String, LocalVariable> variable2localvar = new HashMap<String, LocalVariable>();
			for (NamedDeclaration nD : object.getDeclarations()) {
				if (nD instanceof ConstantDeclaration) {
					obp.fiacre.model.Element decl = doSwitch(nD);
					fcrP.addDeclaration((ConstantDecl)decl);
				}  
				else if (nD instanceof VariableDeclaration) {
					obp.fiacre.model.Element decl = doSwitch(nD);
					compDecl.addVar((LocalVariable) decl);
					variable2localvar.put(nD.getName(), (LocalVariable)decl);
				}
				else if (nD instanceof Process) {
					processes.add((Process) nD);
				}
				else if (nD instanceof ChannelDeclaration) {
					if (nD instanceof TypedChannelDeclaration) {
						TypedChannelDeclaration chan = (TypedChannelDeclaration) nD;
						int bufferSize = 0;
						if (chan.getBufferSize() != null) {
							bufferSize = ((NumberLiteral)StaticEvaluator.evaluate(chan.getBufferSize())).getValue().intValue();
						}
						if (bufferSize > 0) {
							obp.fiacre.model.Element decl = doSwitch(nD);
							compDecl.addVar((LocalVariable) decl);
							variable2localvar.put(nD.getName(), (LocalVariable)decl);
							continue;
						}
					}
					LocalPortDecl port = (LocalPortDecl) doSwitch(nD);
					compDecl.addLocalPort(port);
					channels2ports.put(nD.getName(), port);
				}
			}
			
			for (Process nD : processes) {				
				ProcessDecl decl = (ProcessDecl) doSwitch(nD);
				fcrP.addDeclaration(decl);
				obp.fiacre.model.InterfacedComp instI = new obp.fiacre.model.InterfacedComp();
				Instance fcrInst = new Instance();
				fcrInst.setType(decl);
				
				for (ParamPortDecl lpd : decl.getPortList()) {
					LocalPortDecl port = channels2ports.get(lpd.getName());
					fcrInst.addPort(port);
				}
				
				for (ArgumentVariable av : decl.getArgList()) {
					LocalVariable lv = variable2localvar.get(av.getName());
					RefArg rA = new RefArg();
					rA.setRef(lv);
					fcrInst.addArg(rA);
				}
				
				instI.setComposition(fcrInst);
				par.addArg(instI);
			}
			
			fcrP.addDeclaration(compDecl);
			return fcrContext.pop();
		}
		
		@Override
		public obp.fiacre.model.Type caseByteType(DVE.model.ByteType object) {
			Type type = fcrBuilder.type(byteType);
			visited.put(object, type);
			return type;
		}
		
		@Override
		public obp.fiacre.model.Element caseIntegerType(DVE.model.IntegerType object) {
			Type type = fcrBuilder.intType();
			visited.put(object, type);
			return type;
		}
		
		@Override
		public obp.fiacre.model.Element caseArrayType(DVE.model.ArrayType object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return o;
			
			Type elementType = (Type) doSwitch(object.getElementType());
			rhsContextStack.push(true);
			Exp sizeE = (Exp) doSwitch(object.getSize());
			rhsContextStack.pop();
			Type type = fcrBuilder.arrayType(sizeE, elementType);
			visited.put(object, type);
			return type;
		}
		
		@Override
		public ConstantDecl caseConstantDeclaration(DVE.model.ConstantDeclaration object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return (ConstantDecl) o;
			
			Type fcrType = (Type) doSwitch(object.getType());
			Exp initial = null;
			if (object.getInitial() != null) { 
				rhsContextStack.push(true);
				initial = (Exp) doSwitch(object.getInitial());
				rhsContextStack.pop();
			}
			
			ConstantDecl cons = fcrBuilder.constantDecl(object.getName(), fcrType, initial);
			visited.put(object, cons);
			
			return cons;
		}
		
		@Override
		public LocalVariable caseVariableDeclaration(DVE.model.VariableDeclaration object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return (LocalVariable) o;
			
			Type fcrType = (Type) doSwitch(object.getType());
			Exp initial = null;
			if (object.getInitial() != null) {
				rhsContextStack.push(true);
				initial = (Exp) doSwitch(object.getInitial());
				rhsContextStack.pop();
			}
			
			LocalVariable var = fcrBuilder.localVariable((object.getName()), fcrType, initial);
			visited.put(object, var);
			return var;
		}
		
		@Override
		public obp.fiacre.model.Element caseChannelDeclaration(ChannelDeclaration object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return o;
			
			LocalPortDecl channel = fcrBuilder.localPortDecl((object.getName()), new Profile());
			
			visited.put(object, channel);
			return channel;
		}
		
		Type synthesizeRecord(List<DVE.model.Type> types) {
			List<Field> fields = new ArrayList<Field>();
			int idx = 0;
			for (DVE.model.Type ty : types) {
				fields.add(fcrBuilder.field("f"+idx, (Type)doSwitch(ty)));
			}
			return fcrBuilder.record(fields);
		}
		
		public obp.fiacre.model.Element caseTypedChannelDeclaration(TypedChannelDeclaration object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return o;
			
			int bufferSize = 0;
			if (object.getBufferSize() != null) {
				bufferSize = ((NumberLiteral)StaticEvaluator.evaluate(object.getBufferSize())).getValue().intValue();
			}
			
			if (bufferSize>0) {
				Type elementType = object.getTypes().size() > 1 ? synthesizeRecord(object.getTypes()) : (Type)doSwitch(object.getTypes().get(0));
				Type fcrType = fcrBuilder.queueType(fcrBuilder.literal(bufferSize), elementType);
				
				LocalVariable var = fcrBuilder.localVariable((object.getName()), fcrType);
				visited.put(object, var);
				return var;
			}
			
			Profile prf = new Profile();
			for (DVE.model.Type type : object.getTypes()) {
				obp.fiacre.model.Type fcrType = (obp.fiacre.model.Type) doSwitch(type);
				prf.addType(fcrType);
			}
			LocalPortDecl channel = fcrBuilder.localPortDecl(object.getName(), prf);
			
			visited.put(object, channel);
			return channel;
		}
		
		@Override
		public obp.fiacre.model.Literal caseNumberLiteral(DVE.model.NumberLiteral object) {
			return fcrBuilder.literal(object.getValue().intValue());
		}
		
		@Override
		public obp.fiacre.model.Literal caseTrueLiteral(DVE.model.TrueLiteral object) {
			return fcrBuilder.literal(true);
		}
		
		@Override
		public obp.fiacre.model.Literal caseFalseLiteral(DVE.model.FalseLiteral object) {
			return fcrBuilder.literal(false);
		}
		
		@Override
		public obp.fiacre.model.Element caseArrayLiteral(DVE.model.ArrayLiteral object) {
			List<Exp> exps = new ArrayList<Exp>(object.getValues().size());
			for (Expression e : object.getValues()) {
				exps.add((Exp) doSwitch(e));
			}
			return fcrBuilder.literalArray(exps);
		}
		
		@Override
		public obp.fiacre.model.Element caseVariableReference(DVE.model.VariableReference object) {
			if (object.getRef() instanceof ConstantDeclaration) {
				ConstantDecl cd = (ConstantDecl) visited.get(object.getRef());
				return fcrBuilder.constantRef(cd);
			}
			Variable var = (Variable) visited.get(object.getRef());
			return fcrBuilder.varRef(var);
		}
		
		@Override
		public obp.fiacre.model.Element caseChannelReference(DVE.model.ChannelReference object) {
			return visited.get(object.getRef());
		}
		
		@Override
		public obp.fiacre.model.Element caseUnaryExpression(DVE.model.UnaryExpression object) {
			if (object.getOperand() instanceof StateReference) {
				reportError("Fiacre support state expressions");
				return fcrBuilder.literal(1);
			}
			switch (object.getOperator()) {
			case BNOT:
				reportError("Fiacre does not have the binary not operator");
				return doSwitch(object.getOperand());
			case MINUS:
				return fcrBuilder.minus((Exp) doSwitch(object.getOperand()));
			case NOT:
				return fcrBuilder.not((Exp) doSwitch(object.getOperand()));
			default:
				break;
			}
			return null;
		}
		
		@Override
		public obp.fiacre.model.Element caseBinaryExpression(DVE.model.BinaryExpression object) {
			if (object.getOperands().get(0) instanceof StateReference 
					|| object.getOperands().get(1) instanceof StateReference) {
				reportError("Fiacre support state expressions");
				return fcrBuilder.literal(1);
			}
			BinOp op = null;
			switch (object.getOperator()) {
			case AND:
				op = BinOp.BAND;
				break;
			case BAND:
				reportError("Fiacre does not have the binary and operator");
				op = BinOp.BAND;
				break;
			case BOR:
				reportError("Fiacre does not have the binary or operator");
				op = BinOp.BOR;
				break;
			case BXOR:
				reportError("Fiacre does not have the binary xor operator");
				op = BinOp.BOR;
				break;
			case DIV:
				op = BinOp.BDIV;
				break;
			case EQ:
				op = BinOp.BEQ;
				break;
			case GEQ:
				op = BinOp.BGE;
				break;
			case GT:
				op = BinOp.BGT;
				break;
			case IMPLY:
				reportError("Fiacre does not have the 'imply' operator");
				op = BinOp.BOR;
				break;
			case LEQ:
				op = BinOp.BLE;
				break;
			case LT:
				op = BinOp.BLT;
				break;
			case MINUS:
				op = BinOp.BMINUS;
				break;
			case MOD:
				op = BinOp.BMOD;
				break;
			case MULT:
				op = BinOp.BMUL;
				break;
			case NEQ:
				op = BinOp.BNE;
				break;
			case OR:
				op = BinOp.BOR;
				break;
			case PLUS:
				op = BinOp.BADD;
				break;
			case SHL:
				reportError("Fiacre does not have the << operator");
				op = BinOp.BADD;
				break;
			case SHR:
				reportError("Fiacre does not have the >> operator");
				op = BinOp.BADD;
				break;
			}
			
			Exp lhs = (Exp) doSwitch(object.getOperands().get(0));
			Exp rhs = (Exp) doSwitch(object.getOperands().get(1));
			return fcrBuilder.expression(op, lhs, rhs);
		}
		
		public obp.fiacre.model.Element caseIndexedExpression(DVE.model.IndexedExpression object) {
			obp.fiacre.model.Element base =  doSwitch(object.getBase());
			rhsContextStack.push(true);
			Exp index = (Exp) doSwitch(object.getIndex());
			rhsContextStack.pop();
			
			if (rhsContextStack.peek()) {
				return fcrBuilder.indexedExp((Exp)base, index);
			}
			return fcrBuilder.arrayPattern((Pattern)base, index);
		}
		
		List<ParamPortDecl> buildFiacrePorts(Set<ChannelDeclaration> channels, boolean input, boolean output) {
			List<ParamPortDecl> fcrPorts = new ArrayList<ParamPortDecl>();
			
			for (ChannelDeclaration cD : channels) {
				ParamPortDecl port;
				if (cD instanceof TypedChannelDeclaration) {
					PortDecl pd = (PortDecl)doSwitch(cD);
					port = fcrBuilder.portDecl(cD.getName(), pd.getChannel(), input, output);
				}
				else {
					port = fcrBuilder.portDecl(cD.getName(), (Type)null, input, output);
				}
				visited.put(cD, port);
				fcrPorts.add(port);
			}
			return fcrPorts;
		}
		
		void generateFcrPortsInProcess(DVE.model.Process object, ProcessDecl proc) {
			Set<ChannelDeclaration> inputs = globalsUsageExtractor.inputChannelMap.get(object);
			Set<ChannelDeclaration> outputs = globalsUsageExtractor.outputChannelMap.get(object);
			Set<ChannelDeclaration> io = new HashSet<ChannelDeclaration>(inputs);
			io.retainAll(outputs);
			Set<ChannelDeclaration> inOnly = new HashSet<ChannelDeclaration>(inputs);
			inOnly.removeAll(io);
			Set<ChannelDeclaration> outOnly = new HashSet<ChannelDeclaration>(outputs);
			outOnly.removeAll(io);
			
			proc.addAllPort(buildFiacrePorts(io, true, true));
			proc.addAllPort(buildFiacrePorts(inOnly, true, false));
			proc.addAllPort(buildFiacrePorts(outOnly, false, true));
		}
		
		List<ArgumentVariable> buildFiacreQueues(Set<ChannelDeclaration> vars, boolean input, boolean output) {
			List<ArgumentVariable> fcrParams = new ArrayList<ArgumentVariable>();
			
			for (ChannelDeclaration vD : vars) {
				Variable queue = (Variable) doSwitch(vD);
				ArgumentVariable param = fcrBuilder.argumentVariable(vD.getName(), queue.getType(), input, output, true);
				visited.put(vD, param);
				fcrParams.add(param);
			}
			return fcrParams;
		}
		
		void generateFcrQueuesInProcess(DVE.model.Process object, ProcessDecl proc) {
			Set<ChannelDeclaration> inputs = globalsUsageExtractor.inputQueueMap.get(object);
			Set<ChannelDeclaration> outputs = globalsUsageExtractor.outputQueueMap.get(object);
			Set<ChannelDeclaration> io = new HashSet<ChannelDeclaration>(inputs);
			io.retainAll(outputs);
			Set<ChannelDeclaration> inOnly = new HashSet<ChannelDeclaration>(inputs);
			inOnly.removeAll(io);
			Set<ChannelDeclaration> outOnly = new HashSet<ChannelDeclaration>(outputs);
			outOnly.removeAll(io);
			
			proc.addAllArg(buildFiacreQueues(io, true, true));
			proc.addAllArg(buildFiacreQueues(inOnly, true, false));
			proc.addAllArg(buildFiacreQueues(outOnly, false, true));
		}
		
		List<ArgumentVariable> buildFiacreParams(Set<VariableDeclaration> vars, boolean input, boolean output) {
			List<ArgumentVariable> fcrParams = new ArrayList<ArgumentVariable>();
			
			for (VariableDeclaration vD : vars) {
				Type type = (Type) doSwitch(vD.getType());
				ArgumentVariable param = fcrBuilder.argumentVariable(vD.getName(), type, input, output, true);
				visited.put(vD, param);
				fcrParams.add(param);
			}
			return fcrParams;
		}
		
		void generateFcrParamsInProcess(DVE.model.Process object, ProcessDecl proc) {
			Set<VariableDeclaration> write 	= globalsUsageExtractor.modifiedVariableMap.get(object);
			Set<VariableDeclaration> read 	= globalsUsageExtractor.usedVariableMap.get(object);
			
			Set<VariableDeclaration> rw = new HashSet<VariableDeclaration>(read);
			rw.retainAll(write);
			Set<VariableDeclaration> rOnly = new HashSet<VariableDeclaration>(read);
			rOnly.removeAll(rw);
			Set<VariableDeclaration> wOnly = new HashSet<VariableDeclaration>(write);
			wOnly.removeAll(rw);
			
			proc.addAllArg(buildFiacreParams(rw, true, true));
			proc.addAllArg(buildFiacreParams(rOnly, true, false));
			proc.addAllArg(buildFiacreParams(wOnly, false, true));
		}
		
		@Override
		public ProcessDecl caseProcess(DVE.model.Process object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return (ProcessDecl) o;
			
			ProcessDecl proc = new ProcessDecl();
			proc.setName(object.getName());
			
			generateFcrPortsInProcess(object, proc);
			generateFcrQueuesInProcess(object, proc);
			generateFcrParamsInProcess(object, proc);
			
			for (NamedDeclaration vD : object.getDeclarations()) {
				LocalVariable lV = (LocalVariable) doSwitch(vD);
				proc.addVar(lV);
			}
			
			for (State state : object.getStates()) {
				proc.addState((obp.fiacre.model.State) doSwitch(state));
			}
			
			To initAct = new To();
			initAct.setDest((obp.fiacre.model.State) doSwitch(object.getInitial()));
			proc.setInitAction(initAct);
			
			for (Transition tran : object.getTransitions()) {
				obp.fiacre.model.Transition fcrTran = (obp.fiacre.model.Transition) doSwitch(tran);
				proc.addTransition(fcrTran);
			}
			
			if (!object.getCommited().isEmpty()) {
				java.lang.System.err.println("Fiacre does not support commited states");
			}
			if (!object.getAccepting().isEmpty()) {
				java.lang.System.err.println("Fiacre does not support accepting states");
			}
			
			visited.put(object, proc);
			return proc;
		}
		
		@Override
		public obp.fiacre.model.Transition caseTransition(Transition object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return (obp.fiacre.model.Transition) o;
			
			obp.fiacre.model.Transition fcrTran = new obp.fiacre.model.Transition();
			
			obp.fiacre.model.State fromState = (obp.fiacre.model.State) doSwitch(object.getFrom());
			fcrTran.setFrom(fromState);
			
			Seq seq = new Seq();
			fcrTran.setAction(seq);
			
			if (object.getGuard() != null) {
				//guard code
				rhsContextStack.push(true);
				Exp condition = (Exp)doSwitch(object.getGuard());
				rhsContextStack.pop();
				
				CaseStmt caseS = fcrBuilder.caseStmt(condition, fcrBuilder.rule(fcrBuilder.literal(true), new NullStmt()));
				seq.addStatement(caseS);
			}
			if (object.getSync() != null) {
				Statement syncS = (Statement) doSwitch(object.getSync());
				seq.addStatement(syncS);
			}
			
			if (object.getEffect() != null) {
				for (Assignment assign : object.getEffect()) {
					Statement eff = (Statement) doSwitch(assign);
					seq.addStatement(eff);
				}
			}
			
			obp.fiacre.model.State toState = (obp.fiacre.model.State) doSwitch(object.getTo());
			To to = new To();
			to.setDest(toState);
			seq.addStatement(to);
			
			visited.put(object, fcrTran);
			return fcrTran;
		}
		
		@Override
		public obp.fiacre.model.Element caseAssignment(Assignment object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return o;
			

			rhsContextStack.push(false);
			Pattern lhs = (Pattern)doSwitch(object.getLhs());	
			rhsContextStack.pop();
			rhsContextStack.push(true);
			Exp rhs = (Exp)doSwitch(object.getRhs());
			rhsContextStack.pop();
			
			//this does not work
			Statement assign = fcrBuilder.assign(lhs, rhs);
			
			visited.put(object, assign);
			return assign;
		}
		
		@Override
		public obp.fiacre.model.Element caseInputSynchronization(DVE.model.InputSynchronization object) {
			obp.fiacre.model.Element channel = doSwitch(object.getChannel());
			if (channel instanceof PortDecl) {
				PortDecl port = (PortDecl)channel;
				if (object.getValue() != null) {
					rhsContextStack.push(false);
					Pattern pattern = (Pattern) doSwitch(object.getValue());
					rhsContextStack.pop();
					return fcrBuilder.oneReception(port, pattern);
				}
				
				return fcrBuilder.oneReception(port);
			}
			Variable var = (Variable)channel;
			Pattern pattern = null;
			if (object.getValue() != null) {
				rhsContextStack.push(false);
				pattern = (Pattern) doSwitch(object.getValue());
				rhsContextStack.pop();
			}
			
			return fcrBuilder.caseStmt(
					fcrBuilder.notEmpty(fcrBuilder.varRef(var)), 
					fcrBuilder.rule(fcrBuilder.literal(true), 
							fcrBuilder.seq(
									fcrBuilder.assign(pattern, fcrBuilder.first(fcrBuilder.varRef(var))),
									fcrBuilder.assign(fcrBuilder.varRef(var), fcrBuilder.dequeue(fcrBuilder.varRef(var))) )));
		}
		
		@Override
		public obp.fiacre.model.Element caseOutputSynchronization(DVE.model.OutputSynchronization object) {
			obp.fiacre.model.Element channel = doSwitch(object.getChannel());
			if (channel instanceof PortDecl) {
				PortDecl port = (PortDecl)channel;
				if (object.getValue() != null) {
					rhsContextStack.push(true);
					Exp exp = (Exp) doSwitch(object.getValue());
					rhsContextStack.pop();
					return fcrBuilder.emission(port, exp);
				}
				
				return fcrBuilder.emission(port);
			}
			Variable var = (Variable)channel;
			Exp expr = null;
			if (object.getValue() != null) {
				rhsContextStack.push(true);
				expr = (Exp) doSwitch(object.getValue());
				rhsContextStack.pop();
			}
			
			return fcrBuilder.caseStmt(
					fcrBuilder.notFull(fcrBuilder.varRef(var)), 
					fcrBuilder.rule(fcrBuilder.literal(true), 
							fcrBuilder.seq(
									fcrBuilder.assign(fcrBuilder.varRef(var), fcrBuilder.enqueue(fcrBuilder.varRef(var), expr)) )));
		}
		
		@Override
		public obp.fiacre.model.Element caseState(State object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return o;
			
			obp.fiacre.model.State state = new obp.fiacre.model.State();
			state.setName((object.getName()));
			
			visited.put(object, state);
			return state;
		}
		
		@Override
		public obp.fiacre.model.Element caseStateReference(DVE.model.StateReference object) {
			obp.fiacre.model.Element o = visited.get(object);
			if (o != null) return o;
			
			obp.fiacre.model.State state = (obp.fiacre.model.State) doSwitch(object.getRef());
			visited.put(object, state);
			return state;
		}
		
		@Override
		public obp.fiacre.model.Element caseSystemProperties(DVE.model.SystemProperties object) {
			doSwitch(object.getSystemType());
			
			if (object.getProperty() != null) {
				java.lang.System.err.println("Property process '" + object.getProperty().getRefName() + "' cannot be converted");
			}
			return null;
		}
		@Override
		public obp.fiacre.model.Element caseSynchronous(DVE.model.Synchronous object) {
			java.lang.System.err.println("Cannot handle DVE synchronous composition (does not match Fiacre composition operator)");
			return null;
		}
		
	};
	
	Set<String> errorMessages = new LinkedHashSet<String>();
	void reportError(String error) {
		if (!errorMessages.contains(error)) {
			errorMessages.add(error);
		}
	}

	public Program transform(System sys) {
		
		DVEFixBooleanExpressions booleanFixer = new DVEFixBooleanExpressions();
		booleanFixer.apply(sys);
		
		Program program = (Program) modelSwitch.doSwitch(sys);
		for (String errorM : errorMessages) {
			java.lang.System.err.println(errorM);
		}
		if (!errorMessages.isEmpty()) return null;
		return program;
	}
}
