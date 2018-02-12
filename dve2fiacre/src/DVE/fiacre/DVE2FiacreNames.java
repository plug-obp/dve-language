package DVE.fiacre;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import DVE.model.System;
import DVE.model.util.ModelSwitch;

public class DVE2FiacreNames {
	ModelSwitch<Boolean> modelSwitch = new ModelSwitch<Boolean>() {
		public Boolean caseNamedDeclaration(DVE.model.NamedDeclaration object) {
			object.setName("_" + object.getName());
			return true;
		}
	};
	public void fixNames(System sys) {
		modelSwitch.doSwitch(sys);
		TreeIterator<EObject> tree = sys.eAllContents();
		while (tree.hasNext()) {
			modelSwitch.doSwitch(tree.next());
		}
	}
}
