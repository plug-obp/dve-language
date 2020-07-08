package sdve.transformations;


import DVE.model.*;
import SDVE.model.AbstractTransition;
import SDVE.model.FlatTransition;
import SDVE.model.System;
import SDVE.model.Transition;
import SDVE.model.util.ModelSwitch;
import org.eclipse.emf.ecore.EObject;
import sdve.extractions.DefaultValueExtractor;

public class Normalizer {
    public static System normalize(System system) {
        new Normalizer().modelSwitch.doSwitch(system);
        return system;
    }
    //if no guard then add true literal
    //if no default value then add the default value of the type
    ModelSwitch<Boolean> modelSwitch = new ModelSwitch<Boolean>() {
        ModelFactory dveFactory = ModelFactory.eINSTANCE;
        @Override
        public Boolean caseSystem(System object) {
            for (NamedDeclaration decl : object.getDeclarations()) {
                doSwitch(decl);
            }
            for (AbstractTransition transition : object.getTransitions()) {
                doSwitch(transition);
            }
            return true;
        }

        @Override
        public Boolean caseTransition(Transition object) {
            if (object.getGuard() == null) {
                object.setGuard(dveFactory.createTrueLiteral());
            }
            return true;
        }

        @Override
        public Boolean caseFlatTransition(FlatTransition object) {
            if (object.getGuard() == null) {
                object.setGuard(dveFactory.createTrueLiteral());
            }
            return true;
        }

        DVE.model.util.ModelSwitch<Boolean> dveNormalizer = new DVE.model.util.ModelSwitch<Boolean>() {
            @Override
            public Boolean caseConstantDeclaration(ConstantDeclaration object) {
                if (object.getInitial() == null) {
                    object.setInitial(DefaultValueExtractor.defaultValue(object.getType()));
                }
                return true;
            }

            @Override
            public Boolean caseVariableDeclaration(VariableDeclaration object) {
                if (object.getInitial() == null) {
                    object.setInitial(DefaultValueExtractor.defaultValue(object.getType()));
                }
                return true;
            }

            @Override
            public Boolean caseElement(Element object) {
                return false;
            }

            @Override
            public Boolean defaultCase(EObject object) {
                return modelSwitch.doSwitch(object);
            }
        };

        @Override
        public Boolean defaultCase(EObject object) {
            return dveNormalizer.doSwitch(object);
        }
    };
}
