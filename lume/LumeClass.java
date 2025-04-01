package lumeProject.lume;

import java.util.List;
import java.util.Map;

class LumeClass implements LumeCallable {
    final String name;
    final LumeClass superclass;
    private final Map<String, LumeFunction> methods;

    LumeClass(String name, LumeClass superclass, 
    Map<String, LumeFunction> methods) {
        this.superclass = superclass;
        this.name = name;
        this.methods = methods;
    }

    LumeFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }
    
        return null;
    }

    @Override
    public Object call(Interpreter interpreter,
                       List<Object> arguments) {
        LumeInstance instance = new LumeInstance(this);
        LumeFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

    @Override
    public int arity() {
        LumeFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }

    @Override
    public String toString() {
        return name;
    }
}