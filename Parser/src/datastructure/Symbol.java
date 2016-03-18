package datastructure;

import java.util.Objects;

/**
 *
 * @author yucunli
 */
public abstract class Symbol {
    
    private final String value;
    
    public Symbol(String value) {
        this.value = value;
    }

    public abstract Boolean isTerminal();
    
    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Symbol other = (Symbol) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
    
    public String getValue() {
        return value;
    }
}
