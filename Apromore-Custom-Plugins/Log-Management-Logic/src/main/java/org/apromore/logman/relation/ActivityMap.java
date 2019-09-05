package org.apromore.logman.relation;

import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.multimap.set.AbstractMutableSetMultimap;

public class ActivityMap extends AbstractMutableSetMultimap<Activity,Activity> {

    @Override
    public MutableSetMultimap<Activity, Activity> newEmpty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableSetMultimap<Activity, Activity> flip() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableSetMultimap<Activity, Activity> selectKeysValues(
            Predicate2<? super Activity, ? super Activity> predicate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableSetMultimap<Activity, Activity> rejectKeysValues(
            Predicate2<? super Activity, ? super Activity> predicate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableSetMultimap<Activity, Activity> selectKeysMultiValues(
            Predicate2<? super Activity, ? super Iterable<Activity>> predicate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableSetMultimap<Activity, Activity> rejectKeysMultiValues(
            Predicate2<? super Activity, ? super Iterable<Activity>> predicate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected MutableMap<Activity, MutableSet<Activity>> createMap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected MutableMap<Activity, MutableSet<Activity>> createMapWithKeyCount(int keyCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected MutableSet<Activity> createCollection() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
