class Adults {
    private final String name;
    private String time;
    private Location location;
    private final String character;
    private final Relative relative;
    private int hash = 0;

    public Adults(String name, String character, Location location, String time, Relative relative) {
        this.name = name;
        this.location = location;
        this.relative = relative;
        this.character = character;
        this.time = time;
    }

    public String getName() {
        return this.name;
    }

    public Relative getRelative() {
        return this.relative;
    }

    public String getTime() {
        return this.time;
    }

    public String getCharacter() {
        return this.character;
    }

    public Location getLocation() {
        return this.location;
    }

    public String toString() {
        return this.getRelative().toString() + ' ' + this.getName();
    }

    public boolean equals(Adults adults) {
        if (this.hashCode() == adults.hashCode()) {
            return this.getName().equals(adults.getName()) && this.getRelative().equals(adults.getRelative())
                    && this.getCharacter().equals(adults.getCharacter()) && this.getTime().equals(adults.getTime())
                    && this.getLocation().equals(adults.getLocation());
        } else {
            return false;
        }
    }
    public int hashCode () {
        if (this.hash == 0) {
            this.hash = this.getName().length() + this.getRelative().toString().length();
        }
        return this.hash;
    }
}
