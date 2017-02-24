/**
 * Отношения с Малышом
 */
enum Relative {
    father {
        public String toString() {
            return "папа";
        }
    },
    mother {
        public String toString() {
            return "мама";
        }
    },
    uncle {
        public String toString() {
            return "дядя";
        }
    },
    sibling {
        public String toString() {
            return "сиблинг";
        }
    },
    housewife {
        public String toString() {
            return "домработница";
        }
    },;

    public boolean equals(Relative relative) {
        return this.hashCode() == relative.hashCode() && this.toString().equals(relative.toString());
    }
}
