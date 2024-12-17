package data;

final public class UserAccount {

    private final String id;

    public UserAccount(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("UserAccount ID cannot be null or empty");
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount userAccount = (UserAccount) o;
        return id.equals(userAccount.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "UserAccount{" + "id='" + id + '\'' + '}';
    }
}
