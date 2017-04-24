package ru.ifmo.se.kirmanak;

import javafx.collections.ModifiableObservableListBase;

import java.util.Vector;

class Collection extends ModifiableObservableListBase<Humans> {
    private final Vector<Humans> delegate = new Vector<>();

    @Override
    public Humans get(int index) {
        return delegate.get(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    protected void doAdd(int index, Humans element) {
        delegate.add(index, element);
    }

    @Override
    protected Humans doSet(int index, Humans element) {
        return delegate.set(index, element);
    }

    @Override
    protected Humans doRemove(int index) {
        return delegate.remove(index);
    }
}
