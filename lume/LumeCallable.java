package lumeProject.lume;

import java.util.List;

interface LumeCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
