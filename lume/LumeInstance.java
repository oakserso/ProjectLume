package lumeProject.lume;

import java.util.HashMap;
import java.util.Map;

class LumeInstance {
    private LumeClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    LumeInstance(LumeClass klass) {
        this.klass = klass;
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        LumeFunction method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);
    
        throw new RuntimeError(name, 
            "Undefined property '" + name.lexeme + "'.");
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}