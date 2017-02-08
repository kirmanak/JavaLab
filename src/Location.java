class Location {
    private final String name;
    private int hash = 0;
    public Location(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getName();
    }

    public boolean equals(Location location) {
        return this.hashCode() == location.hashCode() ? this.getName().equals(location.getName()) : false;
    }

    public int hashCode () {
        if (this.hash == 0) {
            this.hash = this.getName().length();
        }
        return this.hash;
    }
}
