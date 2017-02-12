/**
 * Родственные отношения по отношению к Малышу
 */
enum Relative {
    father{
        public String toString() {
            return "папа";
        }
    },
    mother{
        public String toString() {
            return "мама";
        }
    },
    uncle{
        public String toString() {
            return "дядя";
        }
    },
    siblings{
        public String toString() {
            return "ребёнок";
        }
    };

    public boolean equals(Relative relative) {
        return this.hashCode() == relative.hashCode() && this.toString().equals(relative.toString());
    }
}
