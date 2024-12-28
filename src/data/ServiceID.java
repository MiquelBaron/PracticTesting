package data;

import java.util.Objects;

public class ServiceID {
    private String id;

    public ServiceID(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceID serviceID = (ServiceID) o;
        return id.equals(serviceID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
